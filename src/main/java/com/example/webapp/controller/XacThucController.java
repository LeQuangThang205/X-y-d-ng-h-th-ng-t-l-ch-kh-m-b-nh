package com.example.webapp.controller;

import com.example.webapp.entity.BenhNhan;
import com.example.webapp.entity.NguoiDung;
import com.example.webapp.service.BenhNhanService;
import com.example.webapp.service.EmailService;
import com.example.webapp.service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class XacThucController {
    @Autowired
    private NguoiDungService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BenhNhanService patientService;

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String password = req.get("password");
        String role = req.getOrDefault("role", "customer");
        Map<String, Object> res = new HashMap<>();

        if (userService.findByUsername(username).isPresent()) {
            res.put("success", false);
            res.put("message", "Username already exists");
            return res;
        }

        try {
            NguoiDung user = userService.registerUser(username, password, role);
            String fullName = req.getOrDefault("fullName", username);

            if ("customer".equals(userService.normalizeRole(role))) {
                BenhNhan patient = new BenhNhan();
                patient.setTaiKhoan(username);
                patient.setFullname(fullName);
                patientService.savePatient(patient);
            }

            boolean emailSent = emailService.sendRegistrationSuccessEmail(username, fullName);
            res.put("success", true);
            res.put("user", user.getUsername());
            res.put("role", user.getRole());
            res.put("emailSent", emailSent);
            if (!emailSent) {
                res.put("message", "Đã tạo tài khoản thành công nhưng không gửi được email xác nhận");
            }
        } catch (Exception ex) {
            res.put("success", false);
            res.put("message", ex.getMessage());
        }

        return res;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String password = req.get("password");
        Map<String, Object> res = new HashMap<>();
        Optional<NguoiDung> userOpt = userService.findByUsername(username);
        if (userOpt.isPresent() && userService.checkPassword(userOpt.get(), password)) {
            res.put("success", true);
            res.put("user", userOpt.get().getUsername());
            res.put("role", userOpt.get().getRole());
        } else {
            res.put("success", false);
            res.put("message", "Invalid username or password");
        }
        return res;
    }

    @GetMapping("/me")
    public Map<String, Object> me(@RequestParam String username) {
        Map<String, Object> res = new HashMap<>();
        Optional<NguoiDung> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) {
            res.put("success", false);
            res.put("message", "Khong tim thay tai khoan");
            return res;
        }

        NguoiDung user = userOpt.get();
        res.put("success", true);
        res.put("user", user.getUsername());
        res.put("role", userService.normalizeRole(user.getRole()));
        return res;
    }
}
