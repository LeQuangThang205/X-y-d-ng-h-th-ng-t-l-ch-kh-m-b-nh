package com.example.webapp.controller;

import com.example.webapp.entity.LichHen;
import com.example.webapp.entity.LichLamViec;
import com.example.webapp.entity.TrangThaiLichHen;
import com.example.webapp.repository.BacSiRepository;
import com.example.webapp.service.DatLichKhamService;
import com.example.webapp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctor")
public class BacSiPortalController {
    @Autowired
    private DatLichKhamService datLichKhamService;

    @Autowired
    private BacSiRepository bacSiRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping("/{doctorId}/appointments")
    public List<LichHen> doctorAppointments(@PathVariable Long doctorId) {
        return datLichKhamService.lichCuaBacSi(doctorId);
    }

    @GetMapping("/{doctorId}/appointments/today")
    public List<LichHen> doctorTodayAppointments(@PathVariable Long doctorId) {
        return datLichKhamService.lichHomNayCuaBacSi(doctorId);
    }

    @GetMapping("/{doctorId}/appointments/week")
    public List<LichHen> doctorWeekAppointments(@PathVariable Long doctorId) {
        return datLichKhamService.lich7NgayCuaBacSi(doctorId);
    }

    @GetMapping("/{doctorId}/appointments/history")
    public List<LichHen> doctorHistoryAppointments(@PathVariable Long doctorId) {
        return datLichKhamService.lichSuKhamCuaBacSi(doctorId);
    }

    @GetMapping("/{doctorId}/report")
    public Map<String, Object> doctorReport(@PathVariable Long doctorId) {
        return datLichKhamService.baoCaoCaNhanBacSi(doctorId);
    }

    @GetMapping("/profile/by-account")
    public Map<String, Object> doctorProfileByAccount(@RequestParam String account) {
        Optional<com.example.webapp.entity.BacSi> doctorOpt = bacSiRepository.findFirstByEmailIgnoreCase(account);
        if (doctorOpt.isEmpty()) {
            throw new IllegalArgumentException("Khong tim thay ho so bac si");
        }

        com.example.webapp.entity.BacSi doctor = doctorOpt.get();
        Map<String, Object> res = new HashMap<>();
        res.put("doctorId", doctor.getId());
        res.put("fullName", doctor.getHoTen());
        res.put("email", doctor.getEmail());
        res.put("title", doctor.getChucDanh());
        res.put("phone", doctor.getSoDienThoai());
        res.put("portrait", doctor.getAnhChanDung());
        return res;
    }

    @GetMapping("/{doctorId}/profile")
    public Map<String, Object> doctorProfile(@PathVariable Long doctorId) {
        com.example.webapp.entity.BacSi doctor = bacSiRepository.findById(Objects.requireNonNull(doctorId))
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay bac si"));

        Map<String, Object> res = new HashMap<>();
        res.put("doctorId", doctor.getId());
        res.put("fullName", doctor.getHoTen());
        res.put("email", doctor.getEmail());
        res.put("title", doctor.getChucDanh());
        res.put("phone", doctor.getSoDienThoai());
        res.put("portrait", doctor.getAnhChanDung());
        return res;
    }

    @PatchMapping("/{doctorId}/profile")
    public Map<String, Object> updateDoctorProfile(@PathVariable Long doctorId, @RequestBody Map<String, String> req) {
        Map<String, Object> res = new HashMap<>();
        try {
            com.example.webapp.entity.BacSi doctor = bacSiRepository.findById(Objects.requireNonNull(doctorId))
                    .orElseThrow(() -> new IllegalArgumentException("Khong tim thay bac si"));

            String fullName = req.getOrDefault("fullName", "").trim();
            if (fullName.isEmpty()) {
                throw new IllegalArgumentException("Ho ten bac si khong duoc de trong");
            }

            doctor.setHoTen(fullName);
            doctor.setSoDienThoai(trimToNull(req.get("phone")));
            doctor.setChucDanh(trimToNull(req.get("title")));
            doctor.setAnhChanDung(trimToNull(req.get("portrait")));

            com.example.webapp.entity.BacSi saved = bacSiRepository.save(doctor);

            res.put("success", true);
            res.put("doctorId", saved.getId());
            res.put("fullName", saved.getHoTen());
            res.put("email", saved.getEmail());
            res.put("title", saved.getChucDanh());
            res.put("phone", saved.getSoDienThoai());
            res.put("portrait", saved.getAnhChanDung());
        } catch (Exception ex) {
            res.put("success", false);
            res.put("message", ex.getMessage());
        }
        return res;
    }

    @GetMapping("/{doctorId}/schedules")
    public List<LichLamViec> doctorSchedules(
            @PathVariable Long doctorId,
            @RequestParam(required = false) String date) {
        LocalDate target = (date == null || date.isBlank()) ? LocalDate.now() : LocalDate.parse(date);
        return datLichKhamService.lichLamViecTrongNgayCuaBacSi(doctorId, target);
    }

    @PatchMapping("/{doctorId}/appointments/{appointmentId}/status")
    public Map<String, Object> updateStatus(
            @PathVariable Long doctorId,
            @PathVariable Long appointmentId,
            @RequestBody Map<String, String> req) {
        Map<String, Object> res = new HashMap<>();
        try {
            TrangThaiLichHen status = TrangThaiLichHen.valueOf(req.get("status"));
            LichHen lichHen = datLichKhamService.capNhatTrangThaiLichHen(appointmentId, doctorId, status);

            String toEmail = lichHen.getBenhNhan() != null ? lichHen.getBenhNhan().getTaiKhoan() : null;
            String customerName = lichHen.getBenhNhan() != null ? lichHen.getBenhNhan().getFullname() : null;
            String doctorName = lichHen.getBacSi() != null ? lichHen.getBacSi().getHoTen() : null;
            String roomName = lichHen.getPhongKham() != null ? lichHen.getPhongKham().getTen() : null;

            boolean emailSent = true;
            String emailMessage = null;
            if (status == TrangThaiLichHen.DA_XAC_NHAN) {
                emailSent = emailService.sendAppointmentConfirmedEmail(
                        toEmail,
                        customerName,
                        doctorName,
                        roomName,
                        lichHen.getNgayKham(),
                        lichHen.getGioKham());
                if (!emailSent) {
                    emailMessage = "Lich hen da duoc xac nhan nhung gui email that bai";
                }
            } else if (status == TrangThaiLichHen.DA_KHAM) {
                emailSent = emailService.sendAppointmentCompletedEmail(
                        toEmail,
                        customerName,
                        doctorName,
                        lichHen.getNgayKham());
                if (!emailSent) {
                    emailMessage = "Da cap nhat da kham xong nhung gui email cam on that bai";
                }
            } else if (status == TrangThaiLichHen.BO_LO) {
                emailSent = emailService.sendAppointmentMissedEmail(
                        toEmail,
                        customerName,
                        doctorName,
                        lichHen.getNgayKham(),
                        lichHen.getGioKham());
                if (!emailSent) {
                    emailMessage = "Da cap nhat bo lo nhung gui email xin loi that bai";
                }
            }

            res.put("emailSent", emailSent);
            if (emailMessage != null) {
                res.put("emailMessage", emailMessage);
            }

            res.put("success", true);
            res.put("appointment", toAppointmentPayload(lichHen));
        } catch (Exception ex) {
            res.put("success", false);
            res.put("message", ex.getMessage());
        }
        return res;
    }

    @PatchMapping("/{doctorId}/appointments/{appointmentId}/clinical-note")
    public Map<String, Object> updateClinicalNote(
            @PathVariable Long doctorId,
            @PathVariable Long appointmentId,
            @RequestBody Map<String, String> req) {
        Map<String, Object> res = new HashMap<>();
        try {
            LichHen lichHen = datLichKhamService.capNhatKetQuaKham(
                    appointmentId,
                    doctorId,
                    req.getOrDefault("chanDoan", ""),
                    req.getOrDefault("huongDieuTri", ""),
                    req.getOrDefault("donThuoc", ""),
                    req.getOrDefault("ghiChuTaiKham", ""));
            res.put("success", true);
            res.put("appointment", toAppointmentPayload(lichHen));
        } catch (Exception ex) {
            res.put("success", false);
            res.put("message", ex.getMessage());
        }
        return res;
    }

    @PostMapping("/{doctorId}/appointments/{appointmentId}/follow-up")
    public Map<String, Object> createFollowUp(
            @PathVariable Long doctorId,
            @PathVariable Long appointmentId,
            @RequestBody Map<String, String> req) {
        Map<String, Object> res = new HashMap<>();
        try {
            LocalDate ngayTaiKham = LocalDate.parse(req.getOrDefault("ngayTaiKham", ""));
            LocalTime gioTaiKham = LocalTime.parse(req.getOrDefault("gioTaiKham", ""));
            LichHen lichTaiKham = datLichKhamService.taoLichTaiKham(
                    appointmentId,
                    doctorId,
                    ngayTaiKham,
                    gioTaiKham,
                    req.getOrDefault("ghiChuTaiKham", ""));
            res.put("success", true);
            res.put("appointment", toAppointmentPayload(lichTaiKham));
        } catch (Exception ex) {
            res.put("success", false);
            res.put("message", ex.getMessage());
        }
        return res;
    }

    @PostMapping("/{doctorId}/schedule/lock")
    public Map<String, Object> lockDoctorSlots(
            @PathVariable Long doctorId,
            @RequestBody Map<String, Object> req) {
        Map<String, Object> res = new HashMap<>();
        try {
            String dateText = String.valueOf(req.getOrDefault("date", LocalDate.now().toString()));
            LocalDate date = LocalDate.parse(dateText);

            @SuppressWarnings("unchecked")
            List<Number> raw = (List<Number>) req.get("slotIds");
            List<Long> slotIds = raw == null ? List.of() : raw.stream().map(Number::longValue).toList();

            Map<String, Object> result = datLichKhamService.khoaKhungGioTamThoi(doctorId, date, slotIds);
            res.put("success", true);
            res.put("result", result);
        } catch (Exception ex) {
            res.put("success", false);
            res.put("message", ex.getMessage());
        }
        return res;
    }

    @PatchMapping("/{doctorId}/shift/close")
    public Map<String, Object> closeDoctorShift(@PathVariable Long doctorId) {
        Map<String, Object> res = new HashMap<>();
        try {
            Map<String, Object> result = datLichKhamService.chotCaBacSi(doctorId);
            res.put("success", true);
            res.put("result", result);
        } catch (Exception ex) {
            res.put("success", false);
            res.put("message", ex.getMessage());
        }
        return res;
    }

    private String trimToNull(String value) {
        if (value == null)
            return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Map<String, Object> toAppointmentPayload(LichHen lichHen) {
        Map<String, Object> payload = new HashMap<>();
        if (lichHen == null) {
            return payload;
        }

        payload.put("id", lichHen.getId());
        payload.put("ngayKham", lichHen.getNgayKham());
        payload.put("gioKham", lichHen.getGioKham());
        payload.put("trangThai", lichHen.getTrangThai());
        payload.put("trieuChung", lichHen.getTrieuChung());
        payload.put("chanDoan", lichHen.getChanDoan());
        payload.put("huongDieuTri", lichHen.getHuongDieuTri());
        payload.put("donThuoc", lichHen.getDonThuoc());
        payload.put("ghiChuTaiKham", lichHen.getGhiChuTaiKham());

        if (lichHen.getBenhNhan() != null) {
            Map<String, Object> patient = new HashMap<>();
            patient.put("id", lichHen.getBenhNhan().getId());
            patient.put("fullname", lichHen.getBenhNhan().getFullname());
            payload.put("benhNhan", patient);
        }

        if (lichHen.getBacSi() != null) {
            Map<String, Object> doctor = new HashMap<>();
            doctor.put("id", lichHen.getBacSi().getId());
            doctor.put("hoTen", lichHen.getBacSi().getHoTen());
            payload.put("bacSi", doctor);
        }

        return payload;
    }
}
