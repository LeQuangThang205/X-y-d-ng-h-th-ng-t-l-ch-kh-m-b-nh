/**
 * Auth + helper API cho frontend static.
 * Luu session tam bang localStorage, chuan hoa role ve customer/doctor/admin.
 */
const STORAGE_KEY = "booking_currentUser";

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
        window.location.href = "/customer/trangchu.html";
        return null;
    }
    if (allowedRoles && allowedRoles.length) {
        var normalizedAllowed = allowedRoles.map(normalizeRole);
        if (!normalizedAllowed.includes(user.role)) {
            alert("Ban khong co quyen truy cap trang nay.");
            redirectByRole(user.role);
            return null;
        }
    }
    return user;
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

