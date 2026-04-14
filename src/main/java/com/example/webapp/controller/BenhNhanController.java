package com.example.webapp.controller;

import com.example.webapp.entity.BenhNhan;
import com.example.webapp.service.BenhNhanService;
import com.example.webapp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/patients")
public class BenhNhanController {
    @Autowired
    private BenhNhanService patientService;

    @Autowired
    private EmailService emailService;

    @GetMapping
    public List<BenhNhan> getAllPatients() {
        return patientService.getAllPatients();
    }

    @GetMapping("/{id}")
    public Optional<BenhNhan> getPatientById(@PathVariable Long id) {
        return patientService.getPatientById(id);
    }

    @GetMapping("/by-account")
    public Optional<BenhNhan> getPatientByAccount(@RequestParam String account) {
        return patientService.getPatientByTaiKhoan(account);
    }

    @PostMapping
    public BenhNhan createPatient(@RequestBody BenhNhan patient) {
        return patientService.savePatient(patient);
    }

    @PutMapping("/{id}")
    public BenhNhan updatePatient(@PathVariable Long id, @RequestBody BenhNhan patient) {
        return patientService.updatePatient(id, patient);
    }

    @PostMapping("/upsert-by-account")
    public BenhNhan upsertByAccount(
            @RequestParam String account,
            @RequestBody BenhNhan patient) {
        Optional<BenhNhan> current = patientService.getPatientByTaiKhoan(account);

        patient.setTaiKhoan(account);

        if (current.isPresent()) {
            // Profile already exists, just update it
            return patientService.updatePatient(current.get().getId(), patient);
        }

        // First time creating profile, save it and send welcome email
        var saved = patientService.savePatient(patient);

        // Send welcome email only when required profile fields are completed.
        if (isProfileCompleted(patient)) {
            boolean sent = emailService.sendRegistrationSuccessEmail(account, patient.getFullname());
            if (!sent) {
                System.err.println("Failed to send welcome email to: " + account);
            }
        }

        return saved;
    }

    private boolean isProfileCompleted(BenhNhan patient) {
        return patient != null
                && hasText(patient.getFullname())
                && hasText(patient.getGender())
                && hasText(patient.getCccd())
                && hasText(patient.getPhone())
                && hasText(patient.getAddress())
                && patient.getAge() != null;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    @DeleteMapping("/{id}")
    public void deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
    }
}
