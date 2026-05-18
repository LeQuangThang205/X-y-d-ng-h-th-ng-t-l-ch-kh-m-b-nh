package com.example.webapp.service;

import com.example.webapp.entity.BacSi;
import com.example.webapp.entity.NguoiDung;
import com.example.webapp.repository.BacSiRepository;
import com.example.webapp.repository.BenhNhanRepository;
import com.example.webapp.repository.LichHenRepository;
import com.example.webapp.repository.LichLamViecRepository;
import com.example.webapp.repository.ThongBaoNhacLichRepository;
import com.example.webapp.repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NguoiDungService {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private NguoiDungRepository userRepository;

    @Autowired
    private BenhNhanRepository benhNhanRepository;

    @Autowired
    private BacSiRepository bacSiRepository;

    @Autowired
    private LichHenRepository lichHenRepository;

    @Autowired
    private LichLamViecRepository lichLamViecRepository;

    @Autowired
    private ThongBaoNhacLichRepository thongBaoNhacLichRepository;

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

    public List<BacSi> layBacSiCoTaiKhoanDoctor() {
        return bacSiRepository.findAll().stream()
                .filter(doctor -> hasDoctorAccount(doctor.getEmail()))
                .collect(Collectors.toList());
    }

    public List<NguoiDung> findAllUsers() {
        return userRepository.findAll();
    }

    public NguoiDung findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay tai khoan id=" + userId));
    }

    @Transactional
    public NguoiDung updateRole(long userId, String role) {
        NguoiDung user = findUserById(userId);
        String currentRole = normalizeRole(user.getRole());
        String nextRole = normalizeRole(role);

        if ("doctor".equals(currentRole) && !"doctor".equals(nextRole)) {
            bacSiRepository.findFirstByEmailIgnoreCase(user.getUsername()).ifPresent(doctor -> {
                Long doctorId = doctor.getId();
                lichHenRepository.deleteByBacSiId(doctorId);
                lichLamViecRepository.deleteByBacSiId(doctorId);
                bacSiRepository.delete(doctor);
            });
        }

        user.setRole(nextRole);
        return userRepository.save(user);
    }

    public NguoiDung saveUser(NguoiDung user) {
        return userRepository.save(Objects.requireNonNull(user));
    }

    @Transactional
    public NguoiDung updatePassword(long userId, String newPassword) {
        validatePasswordPolicy(newPassword);
        NguoiDung user = findUserById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    @Transactional
    public NguoiDung changePassword(String username, String currentPassword, String newPassword) {
        String normalizedUsername = username == null ? null : username.trim();
        if (normalizedUsername == null || normalizedUsername.isBlank()) {
            throw new IllegalArgumentException("Username khong duoc de trong");
        }

        NguoiDung user = userRepository.findByUsername(normalizedUsername)
                .or(() -> userRepository.findByUsernameIgnoreCase(normalizedUsername))
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay tai khoan"));

        if (currentPassword == null || currentPassword.isEmpty()) {
            throw new IllegalArgumentException("Vui long nhap mat khau hien tai");
        }
        if (!checkPassword(user, currentPassword)) {
            throw new IllegalArgumentException("Mat khau hien tai khong chinh xac");
        }
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("Vui long nhap mat khau moi");
        }
        if (currentPassword.equals(newPassword)) {
            throw new IllegalArgumentException("Mat khau moi phai khac mat khau hien tai");
        }

        validatePasswordPolicy(newPassword);
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(long userId) {
        NguoiDung user = findUserById(userId);
        String username = user.getUsername();
        String role = normalizeRole(user.getRole());

        if ("customer".equals(role)) {
            benhNhanRepository.findFirstByTaiKhoan(username).ifPresent(patient -> {
                Long patientId = patient.getId();
                thongBaoNhacLichRepository.deleteByBenhNhanId(patientId);
                lichHenRepository.deleteByBenhNhanId(patientId);
                benhNhanRepository.delete(patient);
            });
        } else if ("doctor".equals(role)) {
            bacSiRepository.findFirstByEmailIgnoreCase(username).ifPresent(doctor -> {
                Long doctorId = doctor.getId();
                lichHenRepository.deleteByBacSiId(doctorId);
                lichLamViecRepository.deleteByBacSiId(doctorId);
                bacSiRepository.delete(doctor);
            });
        }

        userRepository.delete(user);
    }

    @Transactional
    public NguoiDung updateUsername(long userId, String newUsername) {
        NguoiDung user = findUserById(userId);
        String oldUsername = user.getUsername();
        String normalized = newUsername == null ? null : newUsername.trim();
        if (normalized == null || normalized.isBlank()) {
            throw new IllegalArgumentException("Username khong duoc de trong");
        }

        user.setUsername(normalized);
        NguoiDung saved = userRepository.save(user);

        String role = normalizeRole(saved.getRole());
        if ("customer".equals(role)) {
            benhNhanRepository.findFirstByTaiKhoan(oldUsername).ifPresent(patient -> {
                patient.setTaiKhoan(normalized);
                benhNhanRepository.save(patient);
            });
        } else if ("doctor".equals(role)) {
            bacSiRepository.findFirstByEmailIgnoreCase(oldUsername).ifPresent(doctor -> {
                doctor.setEmail(normalized);
                bacSiRepository.save(doctor);
            });
        }

        return saved;
    }

    @Transactional
    public int xoaHoSoBacSiKhongCoTaiKhoanDoctor() {
        int deleted = 0;
        List<BacSi> doctors = bacSiRepository.findAll();
        for (BacSi doctor : doctors) {
            if (hasDoctorAccount(doctor.getEmail())) {
                continue;
            }

            Long doctorId = doctor.getId();
            lichHenRepository.deleteByBacSiId(doctorId);
            lichLamViecRepository.deleteByBacSiId(doctorId);
            bacSiRepository.delete(doctor);
            deleted++;
        }
        return deleted;
    }

    public boolean checkPassword(NguoiDung user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    private void validatePasswordPolicy(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Mat khau khong duoc de trong");
        }

        String value = password;
        if (value.length() < 8) {
            throw new IllegalArgumentException("Mat khau moi phai co it nhat 8 ky tu");
        }
        if (!value.matches(".*[A-Za-z].*")) {
            throw new IllegalArgumentException("Mat khau moi phai chua it nhat 1 chu cai");
        }
        if (!value.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("Mat khau moi phai chua it nhat 1 chu so");
        }
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

    private boolean hasDoctorAccount(String accountLikeEmail) {
        if (accountLikeEmail == null || accountLikeEmail.isBlank()) {
            return false;
        }
        Optional<NguoiDung> accountOpt = userRepository.findByUsernameIgnoreCase(accountLikeEmail.trim());
        if (accountOpt.isEmpty()) {
            return false;
        }
        return "doctor".equals(normalizeRole(accountOpt.get().getRole()));
    }
}
