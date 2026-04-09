package com.example.webapp.service;

import com.example.webapp.entity.NguoiDung;
import com.example.webapp.repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NguoiDungService {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private NguoiDungRepository userRepository;

    public NguoiDung registerUser(String username, String password, String role) {
        NguoiDung user = new NguoiDung();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(normalizeRole(role));
        return userRepository.save(user);
    }

    public Optional<NguoiDung> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean checkPassword(NguoiDung user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "customer";
        }

        String lower = role.trim().toLowerCase();
        return switch (lower) {
            case "customer", "patient" -> "customer";
            case "doctor" -> "doctor";
            case "admin" -> "admin";
            default -> throw new IllegalArgumentException("Role phai la customer/doctor/admin");
        };
    }
}
