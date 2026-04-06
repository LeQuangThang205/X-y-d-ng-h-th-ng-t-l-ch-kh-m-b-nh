package com.example.webapp.service;

import com.example.webapp.entity.BenhNhan;
import com.example.webapp.repository.BenhNhanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BenhNhanService {
    @Autowired
    private BenhNhanRepository patientRepository;

    public List<BenhNhan> getAllPatients() {
        return patientRepository.findAll();
    }

    public Optional<BenhNhan> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    public Optional<BenhNhan> getPatientByTaiKhoan(String taiKhoan) {
        if (taiKhoan == null || taiKhoan.isBlank()) {
            return Optional.empty();
        }
        return patientRepository.findFirstByTaiKhoan(taiKhoan.trim());
    }

    public BenhNhan savePatient(BenhNhan patient) {
        return patientRepository.save(patient);
    }

    public BenhNhan updatePatient(Long id, BenhNhan patch) {
        BenhNhan current = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay benh nhan"));

        current.setFullname(patch.getFullname());
        current.setAge(patch.getAge());
        current.setGender(patch.getGender());
        current.setCccd(patch.getCccd());
        current.setPhone(patch.getPhone());
        current.setAddress(patch.getAddress());

        if (patch.getTaiKhoan() != null && !patch.getTaiKhoan().isBlank()) {
            current.setTaiKhoan(patch.getTaiKhoan().trim());
        }

        return patientRepository.save(current);
    }

    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }
}
