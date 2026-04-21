package com.example.webapp.repository;

import com.example.webapp.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {
    Optional<NguoiDung> findByUsername(String username);

    Optional<NguoiDung> findByUsernameIgnoreCase(String username);
}
