package com.example.webapp.controller;

import com.example.webapp.entity.*;
import com.example.webapp.repository.BacSiRepository;
import com.example.webapp.repository.BenhNhanRepository;
import com.example.webapp.repository.ChuyenKhoaRepository;
import com.example.webapp.repository.PhongKhamRepository;
import com.example.webapp.service.NguoiDungService;
import com.example.webapp.service.DatLichKhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class QuanTriPortalController {
    @Autowired
    private DatLichKhamService datLichKhamService;

    @Autowired
    private NguoiDungService nguoiDungService;

    @Autowired
    private BenhNhanRepository benhNhanRepository;

    @Autowired
    private BacSiRepository bacSiRepository;

    @Autowired
    private ChuyenKhoaRepository chuyenKhoaRepository;

    @Autowired
    private PhongKhamRepository phongKhamRepository;

    @GetMapping("/patients")
    public List<BenhNhan> patients() {
        return datLichKhamService.layBenhNhan();
    }

    @GetMapping("/doctors")
    public List<BacSi> doctors() {
        return nguoiDungService.layBacSiCoTaiKhoanDoctor();
    }

    @DeleteMapping("/doctors/orphaned")
    public Map<String, Object> cleanupOrphanDoctors() {
        Map<String, Object> res = new HashMap<>();
        try {
            int deleted = nguoiDungService.xoaHoSoBacSiKhongCoTaiKhoanDoctor();
            res.put("success", true);
            res.put("deleted", deleted);
            res.put("message", "Da xoa " + deleted + " ho so bac si khong co tai khoan doctor");
        } catch (Exception ex) {
            res.put("success", false);
            res.put("message", ex.getMessage());
        }
        return res;
    }

    @GetMapping("/specialties")
    public List<ChuyenKhoa> specialties() {
        return datLichKhamService.layDanhSachChuyenKhoa();
    }

    @GetMapping("/rooms")
    public List<PhongKham> rooms() {
        return datLichKhamService.layPhongKham();
    }

    @PostMapping("/specialties")
    public ChuyenKhoa createSpecialty(@RequestBody ChuyenKhoa specialty) {
        return datLichKhamService.luuChuyenKhoa(specialty);
    }

    @PutMapping("/specialties/{id}")
    public Map<String, Object> updateSpecialty(@PathVariable Long id, @RequestBody ChuyenKhoa specialty) {
        Map<String, Object> res = new HashMap<>();
        try {
            specialty.setId(id);
            ChuyenKhoa updated = datLichKhamService.luuChuyenKhoa(specialty);
            res.put("success", true);
            res.put("specialty", updated);
        } catch (Exception ex) {
            res.put("success", false);
            res.put("message", ex.getMessage());
        }
        return res;
    }

    @DeleteMapping("/specialties/{id}")
    public Map<String, Object> deleteSpecialty(@PathVariable Long id) {
        Map<String, Object> res = new HashMap<>();
        try {
            datLichKhamService.xoaChuyenKhoa(id);
            res.put("success", true);
            res.put("message", "Đã xóa chuyên khoa thành công");
        } catch (Exception ex) {
            res.put("success", false);
            res.put("message", ex.getMessage());
        }
        return res;
    }

    @PostMapping("/rooms")
    public PhongKham createRoom(@RequestBody PhongKham room) {
        return datLichKhamService.luuPhongKham(room);
    }

    @PostMapping("/doctors")
    public Map<String, Object> createDoctor() {
        Map<String, Object> res = new HashMap<>();
        res.put("success", false);
        res.put("message", "Chuc nang them bac si da duoc tat");
        return res;
    }

    @PostMapping("/work-schedules")
    public Map<String, Object> createWorkSchedule(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String shift,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(required = false) BigDecimal fee) {
        Map<String, Object> res = new HashMap<>();
        try {
            TimeRange range = resolveTimeRange(shift, start, end);
            LocalTime startTime = range.start();
            LocalTime endTime = range.end();
            LichLamViec lichLamViec = datLichKhamService.taoLichLamViec(doctorId, date, startTime, endTime, fee);
            res.put("success", true);
            res.put("workSchedule", lichLamViec);
        } catch (Exception ex) {
            res.put("success", false);
            res.put("message", ex.getMessage());
        }
        return res;
    }

    private TimeRange resolveTimeRange(String shift, String start, String end) {
        if (shift != null && !shift.isBlank()) {
            String value = shift.trim().toUpperCase();
            return switch (value) {
                case "MORNING", "CA_SANG" -> new TimeRange(LocalTime.of(8, 0), LocalTime.of(12, 0));
                case "AFTERNOON", "CA_CHIEU" -> new TimeRange(LocalTime.of(13, 0), LocalTime.of(17, 0));
                case "EVENING", "CA_TOI" -> new TimeRange(LocalTime.of(18, 0), LocalTime.of(21, 0));
                default ->
                    throw new IllegalArgumentException("Ca làm việc không hợp lệ. Chọn MORNING/AFTERNOON/EVENING");
            };
        }

        LocalTime startTime = parseTimeFlexible(start);
        LocalTime endTime = parseTimeFlexible(end);
        return new TimeRange(startTime, endTime);
    }

    @GetMapping("/reports/overview")
    public Map<String, Object> reportOverview() {
        return datLichKhamService.thongKeTongHop();
    }

    @GetMapping("/accounts")
    public List<Map<String, Object>> accounts() {
        return nguoiDungService.findAllUsers().stream().map(user -> {
            Map<String, Object> row = new HashMap<>();
            row.put("id", user.getId());
            row.put("username", user.getUsername());
            row.put("role", nguoiDungService.normalizeRole(user.getRole()));

            Optional<BenhNhan> patientOpt = benhNhanRepository.findFirstByTaiKhoan(user.getUsername());
            if (patientOpt.isPresent()) {
                row.put("displayName", patientOpt.get().getFullname());
                row.put("profileType", "customer");
            } else {
                Optional<BacSi> doctorOpt = bacSiRepository.findFirstByEmailIgnoreCase(user.getUsername());
                if (doctorOpt.isPresent()) {
                    row.put("displayName", doctorOpt.get().getHoTen());
                    row.put("profileType", "doctor");
                } else {
                    row.put("displayName", user.getUsername());
                    row.put("profileType", "account-only");
                }
            }

            return row;
        }).toList();
    }

    @PostMapping("/accounts")
    public Map<String, Object> createAccount(@RequestBody Map<String, String> req) {
        Map<String, Object> res = new HashMap<>();
        try {
            String username = req.getOrDefault("username", "").trim();
            String password = req.getOrDefault("password", "").trim();
            String role = nguoiDungService.normalizeRole(req.getOrDefault("role", "customer"));

            if (username.isEmpty()) {
                throw new IllegalArgumentException("Tài khoản không được để trống");
            }
            if (password.length() < 6) {
                throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự");
            }
            if (nguoiDungService.findByUsername(username).isPresent()) {
                throw new IllegalArgumentException("Tài khoản đã tồn tại");
            }

            NguoiDung created = nguoiDungService.registerUser(username, password, role);

            if ("customer".equals(role)) {
                String fullName = req.getOrDefault("fullName", "").trim();
                if (fullName.isEmpty()) {
                    throw new IllegalArgumentException("Họ tên khách hàng không được để trống");
                }

                BenhNhan benhNhan = new BenhNhan();
                benhNhan.setTaiKhoan(username);
                benhNhan.setFullname(fullName);
                benhNhan.setPhone(trimToNull(req.get("phone")));
                benhNhan.setAddress(trimToNull(req.get("address")));
                benhNhan.setGender(trimToNull(req.get("gender")));
                benhNhan.setCccd(trimToNull(req.get("cccd")));

                Integer age = parseIntegerNullable(req.get("age"));
                benhNhan.setAge(age);
                benhNhanRepository.save(benhNhan);
            }

            if ("doctor".equals(role)) {
                String fullName = req.getOrDefault("fullName", "").trim();
                if (fullName.isEmpty()) {
                    throw new IllegalArgumentException("Họ tên bác sĩ không được để trống");
                }

                BacSi bacSi = new BacSi();
                bacSi.setHoTen(fullName);
                bacSi.setEmail(username);
                bacSi.setSoDienThoai(trimToNull(req.get("phone")));
                bacSi.setChucDanh(trimToNull(req.get("title")));

                Long specialtyId = parseLongNullable(req.get("specialtyId"));
                if (specialtyId != null) {
                    ChuyenKhoa chuyenKhoa = chuyenKhoaRepository.findById(specialtyId)
                            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chuyên khoa"));
                    bacSi.setChuyenKhoa(chuyenKhoa);
                }

                Long roomId = parseLongNullable(req.get("roomId"));
                if (roomId != null) {
                    PhongKham phongKham = phongKhamRepository.findById(roomId)
                            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng khám"));
                    bacSi.setPhongKham(phongKham);
                }

                bacSiRepository.save(bacSi);
            }

            res.put("success", true);
            res.put("id", created.getId());
            res.put("username", created.getUsername());
            res.put("role", created.getRole());
            res.put("message", "Đã tạo tài khoản thành công");
        } catch (Exception ex) {
            res.put("success", false);
            res.put("message", ex.getMessage());
        }
        return res;
    }

    @PatchMapping("/accounts/{userId}/role")
    public Map<String, Object> updateAccountRole(@PathVariable long userId, @RequestBody Map<String, String> req) {
        Map<String, Object> res = new HashMap<>();
        try {
            String role = req.getOrDefault("role", "");
            NguoiDung updated = nguoiDungService.updateRole(userId, role);
            res.put("success", true);
            res.put("id", updated.getId());
            res.put("role", updated.getRole());
        } catch (Exception ex) {
            res.put("success", false);
            res.put("message", ex.getMessage());
        }
        return res;
    }

    @GetMapping("/accounts/{userId}/profile")
    public Map<String, Object> accountProfile(@PathVariable long userId) {
        Map<String, Object> res = new HashMap<>();
        NguoiDung user = nguoiDungService.findUserById(userId);

        res.put("id", user.getId());
        res.put("username", user.getUsername());
        res.put("role", nguoiDungService.normalizeRole(user.getRole()));

        Optional<BenhNhan> patientOpt = benhNhanRepository.findFirstByTaiKhoan(user.getUsername());
        if (patientOpt.isPresent()) {
            BenhNhan p = patientOpt.get();
            res.put("profileType", "customer");
            res.put("fullName", p.getFullname());
            res.put("phone", p.getPhone());
            res.put("address", p.getAddress());
            res.put("age", p.getAge());
            res.put("gender", p.getGender());
            res.put("cccd", p.getCccd());
            return res;
        }

        Optional<BacSi> doctorOpt = bacSiRepository.findFirstByEmailIgnoreCase(user.getUsername());
        if (doctorOpt.isPresent()) {
            BacSi d = doctorOpt.get();
            res.put("profileType", "doctor");
            res.put("fullName", d.getHoTen());
            res.put("phone", d.getSoDienThoai());
            res.put("email", d.getEmail());
            res.put("title", d.getChucDanh());
            res.put("specialty", d.getChuyenKhoa() != null ? d.getChuyenKhoa().getTen() : null);
            res.put("room", d.getPhongKham() != null ? d.getPhongKham().getTen() : null);
            return res;
        }

        res.put("profileType", "account-only");
        res.put("fullName", user.getUsername());
        return res;
    }

    @PatchMapping("/accounts/{userId}")
    public Map<String, Object> updateAccount(@PathVariable long userId, @RequestBody Map<String, String> req) {
        Map<String, Object> res = new HashMap<>();
        try {
            String newUsername = req.get("username");
            String newPassword = req.get("password");

            NguoiDung updated = nguoiDungService.findUserById(userId);
            if (updated == null) {
                throw new Exception("Tài khoản không tồn tại");
            }

            if (newUsername != null && !newUsername.trim().isEmpty()) {
                updated = nguoiDungService.updateUsername(userId, newUsername.trim());
            }
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                updated.setPassword(newPassword.trim());
            }

            updated = nguoiDungService.saveUser(updated);
            res.put("success", true);
            res.put("id", updated.getId());
            res.put("username", updated.getUsername());
            res.put("message", "Đã cập nhật tài khoản thành công");
        } catch (Exception ex) {
            res.put("success", false);
            res.put("message", ex.getMessage());
        }
        return res;
    }

    @DeleteMapping("/accounts/{userId}")
    public Map<String, Object> deleteAccount(@PathVariable long userId) {
        Map<String, Object> res = new HashMap<>();
        try {
            NguoiDung user = nguoiDungService.findUserById(userId);
            if (user == null) {
                throw new Exception("Tài khoản không tồn tại");
            }

            nguoiDungService.deleteUser(userId);
            res.put("success", true);
            res.put("message", "Đã xóa tài khoản thành công");
        } catch (Exception ex) {
            res.put("success", false);
            res.put("message", ex.getMessage());
        }
        return res;
    }

    private LocalTime parseTimeFlexible(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Thieu gio bat dau/ket thuc");
        }

        String input = value.trim();
        try {
            return LocalTime.parse(input, DateTimeFormatter.ofPattern("H:mm"));
        } catch (DateTimeParseException ignored) {
        }

        try {
            return LocalTime.parse(input, DateTimeFormatter.ofPattern("H:mm:ss"));
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Dinh dang gio khong hop le: " + value);
        }
    }

    private String trimToNull(String value) {
        if (value == null)
            return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Integer parseIntegerNullable(String value) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            return null;
        }
        try {
            return Integer.valueOf(trimmed);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Tuổi không hợp lệ");
        }
    }

    private Long parseLongNullable(String value) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            return null;
        }
        try {
            return Long.valueOf(trimmed);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Giá trị ID không hợp lệ");
        }
    }

    private record TimeRange(LocalTime start, LocalTime end) {
    }
}
