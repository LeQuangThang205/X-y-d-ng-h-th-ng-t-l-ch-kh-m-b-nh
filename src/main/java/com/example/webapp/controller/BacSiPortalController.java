package com.example.webapp.controller;

import com.example.webapp.entity.LichHen;
import com.example.webapp.entity.TrangThaiLichHen;
import com.example.webapp.service.DatLichKhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor")
public class BacSiPortalController {
    @Autowired
    private DatLichKhamService datLichKhamService;

    @GetMapping("/{doctorId}/appointments")
    public List<LichHen> doctorAppointments(@PathVariable Long doctorId) {
        return datLichKhamService.lichCuaBacSi(doctorId);
    }

    @PatchMapping("/appointments/{appointmentId}/status")
    public Map<String, Object> updateStatus(
            @PathVariable Long appointmentId,
            @RequestBody Map<String, String> req) {
        Map<String, Object> res = new HashMap<>();
        try {
            TrangThaiLichHen status = TrangThaiLichHen.valueOf(req.get("status"));
            LichHen lichHen = datLichKhamService.capNhatTrangThaiLichHen(appointmentId, status);
            res.put("success", true);
            res.put("appointment", lichHen);
        } catch (Exception ex) {
            res.put("success", false);
            res.put("message", ex.getMessage());
        }
        return res;
    }

    @PatchMapping("/appointments/{appointmentId}/clinical-note")
    public Map<String, Object> updateClinicalNote(
            @PathVariable Long appointmentId,
            @RequestBody Map<String, String> req) {
        Map<String, Object> res = new HashMap<>();
        try {
            LichHen lichHen = datLichKhamService.capNhatKetQuaKham(
                    appointmentId,
                    req.getOrDefault("chanDoan", ""),
                    req.getOrDefault("huongDieuTri", ""),
                    req.getOrDefault("donThuoc", ""),
                    req.getOrDefault("ghiChuTaiKham", ""));
            res.put("success", true);
            res.put("appointment", lichHen);
        } catch (Exception ex) {
            res.put("success", false);
            res.put("message", ex.getMessage());
        }
        return res;
    }
}
