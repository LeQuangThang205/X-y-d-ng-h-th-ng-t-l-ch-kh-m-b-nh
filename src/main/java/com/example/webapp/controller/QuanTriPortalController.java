package com.example.webapp.controller;

import com.example.webapp.entity.*;
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

@RestController
@RequestMapping("/api/admin")
public class QuanTriPortalController {
    @Autowired
    private DatLichKhamService datLichKhamService;

    @GetMapping("/patients")
    public List<BenhNhan> patients() {
        return datLichKhamService.layBenhNhan();
    }

    @GetMapping("/doctors")
    public List<BacSi> doctors() {
        return datLichKhamService.layBacSiQuanTri();
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

    @PostMapping("/rooms")
    public PhongKham createRoom(@RequestBody PhongKham room) {
        return datLichKhamService.luuPhongKham(room);
    }

    @PostMapping("/doctors")
    public BacSi createDoctor(@RequestBody Map<String, String> req) {
        BacSi doctor = new BacSi();
        doctor.setHoTen(req.get("hoTen"));
        doctor.setChucDanh(req.get("chucDanh"));
        doctor.setEmail(req.get("email"));
        doctor.setSoDienThoai(req.get("soDienThoai"));

        Long chuyenKhoaId = Long.valueOf(req.get("chuyenKhoaId"));
        Long phongKhamId = Long.valueOf(req.get("phongKhamId"));

        return datLichKhamService.luuBacSi(doctor, chuyenKhoaId, phongKhamId);
    }

    @PostMapping("/work-schedules")
    public Map<String, Object> createWorkSchedule(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false) BigDecimal fee) {
        Map<String, Object> res = new HashMap<>();
        try {
            LocalTime startTime = parseTimeFlexible(start);
            LocalTime endTime = parseTimeFlexible(end);
            LichLamViec lichLamViec = datLichKhamService.taoLichLamViec(doctorId, date, startTime, endTime, fee);
            res.put("success", true);
            res.put("workSchedule", lichLamViec);
        } catch (Exception ex) {
            res.put("success", false);
            res.put("message", ex.getMessage());
        }
        return res;
    }

    @GetMapping("/reports/overview")
    public Map<String, Object> reportOverview() {
        return datLichKhamService.thongKeTongHop();
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
}
