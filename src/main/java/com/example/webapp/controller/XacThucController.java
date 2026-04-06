package com.example.webapp.controller;

import com.example.webapp.entity.NguoiDung;
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

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String password = req.get("password");
        String role = req.getOrDefault("role", "patient");
        Map<String, Object> res = new HashMap<>();

        if (userService.findByUsername(username).isPresent()) {
            res.put("success", false);
            res.put("message", "Username already exists");
            return res;
        }

        try {
            NguoiDung user = userService.registerUser(username, password, role);
            res.put("success", true);
            res.put("user", user.getUsername());
            res.put("role", user.getRole());
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
}
