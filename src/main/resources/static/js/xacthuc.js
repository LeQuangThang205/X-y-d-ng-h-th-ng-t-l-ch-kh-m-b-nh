/**
 * Auth + helper API cho frontend static.
 * Luu session tam bang localStorage, chuan hoa role ve customer/doctor/admin.
 */
const STORAGE_KEY = "booking_currentUser";
const PROFILE_REQUIRED_KEY = "booking_profileRequired";
const ACCESS_DENIED_MESSAGE = "Bạn không có quyền truy cập !";

function normalizeRole(role) {
    var v = (role || "").toLowerCase();
    if (v === "patient") return "customer";
    return v;
}

function apiJson(url, options) {
    var opts = options || {};
    return fetch(url, opts).then(function (res) {
        return res.json().then(function (data) {
            if (!res.ok) {
                var msg = data && data.message ? data.message : "Yeu cau that bai";
                throw new Error(msg);
            }
            return data;
        });
    });
}

function getCurrentUser() {
    var data = localStorage.getItem(STORAGE_KEY);
    if (!data) return null;
    try {
        var parsed = JSON.parse(data);
        parsed.role = normalizeRole(parsed.role);
        if (!parsed.name || !String(parsed.name).trim()) {
            parsed.name = parsed.fullname || parsed.username || parsed.email || "";
        }
        return parsed;
    } catch (e) {
        localStorage.removeItem(STORAGE_KEY);
        return null;
    }
}

function setCurrentUser(user) {
    var normalized = Object.assign({}, user, { role: normalizeRole(user.role) });
    if (!normalized.name || !String(normalized.name).trim()) {
        normalized.name = normalized.fullname || normalized.username || normalized.email || "";
    }
    localStorage.setItem(STORAGE_KEY, JSON.stringify(normalized));
}

function markProfileSetupRequired(account) {
    if (!account) return;
    var payload = {
        account: String(account).trim().toLowerCase(),
        required: true,
    };
    localStorage.setItem(PROFILE_REQUIRED_KEY, JSON.stringify(payload));
}

function clearProfileSetupRequirement(account) {
    var current = localStorage.getItem(PROFILE_REQUIRED_KEY);
    if (!current) return;

    if (!account) {
        localStorage.removeItem(PROFILE_REQUIRED_KEY);
        return;
    }

    try {
        var parsed = JSON.parse(current);
        var target = String(account).trim().toLowerCase();
        if (!parsed.account || parsed.account === target) {
            localStorage.removeItem(PROFILE_REQUIRED_KEY);
        }
    } catch (e) {
        localStorage.removeItem(PROFILE_REQUIRED_KEY);
    }
}

function isProfileSetupRequiredForCurrentUser() {
    var user = getCurrentUser();
    if (!user) return false;

    var raw = localStorage.getItem(PROFILE_REQUIRED_KEY);
    if (!raw) return false;
    try {
        var parsed = JSON.parse(raw);
        if (!parsed.required) return false;
        var currentAccount = String(user.username || user.email || "").trim().toLowerCase();
        if (!currentAccount) return false;
        return parsed.account === currentAccount;
    } catch (e) {
        localStorage.removeItem(PROFILE_REQUIRED_KEY);
        return false;
    }
}

function logout() {
    var onCustomerPage = window.location.pathname.indexOf("/customer/") === 0;
    if (onCustomerPage && !window.confirm("Bạn có chắc muốn đăng xuất ?")) {
        return;
    }
    localStorage.removeItem(STORAGE_KEY);
    window.location.href = "/customer/trangchu.html";
}

function requireAuth(allowedRoles) {
    var user = getCurrentUser();
    if (!user) {
        window.location.href = "/admin/dangnhap.html";
        return null;
    }
    if (allowedRoles && allowedRoles.length) {
        var normalizedAllowed = allowedRoles.map(normalizeRole);
        if (!normalizedAllowed.includes(user.role)) {
            alert(ACCESS_DENIED_MESSAGE);
            redirectByRole(user.role);
            return null;
        }
    }
    return user;
}

function isCustomerPublicPage(pathname) {
    var publicPages = {
        "/customer/trangchu.html": true,
        "/customer/khoa.html": true,
        "/customer/bacsi.html": true,
    };
    return !!publicPages[pathname];
}

function getRequiredRoleForPath(pathname) {
    if (!pathname) return null;

    if (pathname.indexOf("/admin/") === 0) {
        var adminPublic = {
            "/admin/dangnhap.html": true,
            "/admin/dangky.html": true,
            "/admin/dangky-nhanvien.html": true,
        };
        return adminPublic[pathname] ? null : "admin";
    }

    if (pathname.indexOf("/doctor/") === 0) {
        return "doctor";
    }

    if (pathname.indexOf("/customer/") === 0) {
        return isCustomerPublicPage(pathname) ? null : "customer";
    }

    return null;
}

function syncCurrentUserRole(user) {
    if (!user || !user.username) {
        return Promise.resolve(user);
    }
    return apiJson("/api/auth/me?username=" + encodeURIComponent(user.username))
        .then(function (res) {
            if (!res || !res.success) {
                return user;
            }
            var serverRole = normalizeRole(res.role);
            var localRole = normalizeRole(user.role);
            if (serverRole !== localRole) {
                user.role = serverRole;
                setCurrentUser(user);
            }
            return user;
        })
        .catch(function () {
            return user;
        });
}

function enforceRoleAccessForCurrentPath() {
    if (typeof window === "undefined") return Promise.resolve();
    var pathname = window.location.pathname || "";
    var requiredRole = getRequiredRoleForPath(pathname);
    if (!requiredRole) return Promise.resolve();

    var user = getCurrentUser();
    if (!user) {
        window.location.href = "/admin/dangnhap.html";
        return Promise.resolve();
    }

    return syncCurrentUserRole(user).then(function (syncedUser) {
        var role = normalizeRole((syncedUser && syncedUser.role) || "");
        if (role !== requiredRole) {
            alert(ACCESS_DENIED_MESSAGE);
            redirectByRole(role);
            return;
        }

        var pathname = window.location.pathname || "";
        if (role === "customer" && isProfileSetupRequiredForCurrentUser() && pathname !== "/customer/hoso.html") {
            window.location.href = "/customer/hoso.html?setup=1";
        }
    });
}

function redirectByRole(role) {
    var v = normalizeRole(role);
    var base = {
        admin: "/admin/trangquantri.html",
        doctor: "/doctor/lichkham.html",
        customer: "/customer/trangchu.html",
    };
    window.location.href = base[v] || "/admin/dangnhap.html";
}

function getRoleLabel(role) {
    var labels = {
        admin: "Quan tri vien",
        doctor: "Bac si",
        customer: "Khach hang",
    };
    return labels[normalizeRole(role)] || role;
}

function authRegister(username, password, role) {
    return apiJson("/api/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username: username, password: password, role: normalizeRole(role) }),
    });
}

function authLogin(username, password) {
    return apiJson("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username: username, password: password }),
    });
}

function setupAutoHideHeader() {
    var header = document.querySelector(".customer-header") || document.querySelector(".landing-header");
    if (!header) return;

    var lastY = window.scrollY || 0;
    var hidden = false;

    function setHidden(nextHidden) {
        if (hidden === nextHidden) return;
        hidden = nextHidden;
        if (hidden) {
            header.classList.add("header-hidden");
        } else {
            header.classList.remove("header-hidden");
        }
    }

    window.addEventListener("scroll", function () {
        var currentY = window.scrollY || 0;
        var delta = currentY - lastY;

        if (currentY < 80) {
            setHidden(false);
        } else if (delta > 6) {
            setHidden(true);
        } else if (delta < -4) {
            setHidden(false);
        }

        lastY = currentY;
    }, { passive: true });

    document.addEventListener("mousemove", function (e) {
        if (e.clientY <= 70) {
            setHidden(false);
        }
    });
}

function setupFlyInAnimations() {
    var selectors = [
        ".customer-layout .page-title",
        ".customer-layout .card",
        ".customer-layout .dept-card",
        ".customer-layout .doctor-card",
        ".customer-layout .booking-form-wrap",
        ".customer-layout .table-wrap",
        ".customer-layout .form-section",
        ".customer-layout .stat-card",
        ".customer-layout .filter-bar",
        ".landing-page .section",
        ".landing-page .dept-card",
        ".landing-page .doctor-card",
        ".landing-page .hero-inner",
        ".landing-page .hotline-inner",
        ".landing-page .footer-grid"
    ];

    var all = document.querySelectorAll(selectors.join(","));
    Array.prototype.forEach.call(all, function (el, index) {
        if (el.classList.contains("ui-fly-in")) return;
        el.style.setProperty("--fly-delay", (index * 0.08).toFixed(2) + "s");
        el.classList.add("ui-fly-in");
    });

    var target = document.querySelector(".customer-content") || document.body;
    if (!target || target._uiFlyObserverAttached) return;
    target._uiFlyObserverAttached = true;

    var observer = new MutationObserver(function () {
        var dynamic = document.querySelectorAll(".dept-card, .doctor-card, .card, .form-section, .table-wrap");
        var offset = 0;
        Array.prototype.forEach.call(dynamic, function (el) {
            if (el.classList.contains("ui-fly-in")) return;
            el.style.setProperty("--fly-delay", (offset * 0.06).toFixed(2) + "s");
            el.classList.add("ui-fly-in");
            offset += 1;
        });
    });

    observer.observe(target, { childList: true, subtree: true });
}

function initDynamicCustomerUi() {
    if (typeof window === "undefined") return;
    setupAutoHideHeader();
    setupFlyInAnimations();
}

function initAdminUiEffects() {
    if (typeof window === "undefined") return;
    var appLayout = document.querySelector(".app-layout");
    if (!appLayout) return;

    requestAnimationFrame(function () {
        appLayout.classList.add("page-ready");
    });

    var targets = appLayout.querySelectorAll(".page-title, .toolbar, .card, .stat-card, .report-card, .table-wrap");
    Array.prototype.forEach.call(targets, function (el, index) {
        if (el.classList.contains("ui-fly-in")) return;
        el.style.setProperty("--admin-delay", (index * 0.05).toFixed(2) + "s");
        el.classList.add("ui-fly-in");
    });

    var sidebarLinks = appLayout.querySelectorAll(".sidebar-nav a");
    Array.prototype.forEach.call(sidebarLinks, function (link) {
        link.addEventListener("click", function () {
            appLayout.classList.remove("page-ready");
        });
    });
}

enforceRoleAccessForCurrentPath();
initDynamicCustomerUi();
initAdminUiEffects();

