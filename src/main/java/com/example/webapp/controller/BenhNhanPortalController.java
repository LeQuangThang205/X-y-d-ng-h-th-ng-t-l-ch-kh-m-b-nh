package com.example.webapp.controller;

import com.example.webapp.entity.*;
import com.example.webapp.service.DatLichKhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patient")
public class BenhNhanPortalController {
    @Autowired
    private DatLichKhamService datLichKhamService;

    @GetMapping("/specialties")
    public List<ChuyenKhoa> specialties() {
        return datLichKhamService.layDanhSachChuyenKhoa();
    }

    @GetMapping("/doctors")
    public List<BacSi> doctors(@RequestParam(required = false) Long specialtyId) {
        return datLichKhamService.layDanhSachBacSi(specialtyId);
    }

    @GetMapping("/doctors/{doctorId}/available-slots")
    public List<LichLamViec> availableSlots(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return datLichKhamService.layKhungGioTrong(doctorId, date);
    }

    @PostMapping("/appointments")
    public Map<String, Object> bookAppointment(@RequestBody Map<String, String> req) {
        Map<String, Object> res = new HashMap<>();
        try {
            String patientIdRaw = req.get("patientId");
            String workScheduleIdRaw = req.get("workScheduleId");

            if (patientIdRaw == null || patientIdRaw.isBlank()) {
                throw new IllegalArgumentException(
                        "Thieu thong tin benh nhan. Vui long cap nhat ho so truoc khi dat lich.");
            }
            if (workScheduleIdRaw == null || workScheduleIdRaw.isBlank()) {
                throw new IllegalArgumentException("Vui long chon khung gio hop le.");
            }

            Long patientId = Long.valueOf(patientIdRaw.trim());
            Long workScheduleId = Long.valueOf(workScheduleIdRaw.trim());
            String symptom = req.getOrDefault("symptom", "");
            LichHen lichHen = datLichKhamService.datLich(patientId, workScheduleId, symptom);
            res.put("success", true);
            res.put("appointment", lichHen);
        } catch (Exception ex) {
            res.put("success", false);
            res.put("message", ex.getMessage());
        }
        return res;
    }

    @GetMapping("/{patientId}/appointments")
    public List<LichHen> appointments(@PathVariable Long patientId) {
        return datLichKhamService.lichCuaBenhNhan(patientId);
    }

    @GetMapping("/{patientId}/reminders")
    public List<ThongBaoNhacLich> reminders(@PathVariable Long patientId) {
        return datLichKhamService.thongBaoBenhNhan(patientId);
    }
}
