package com.example.webapp.repository;

import com.example.webapp.entity.BacSi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BacSiRepository extends JpaRepository<BacSi, Long> {
    List<BacSi> findByHoatDongTrue();

    List<BacSi> findByChuyenKhoaId(Long chuyenKhoaId);

    List<BacSi> findByChuyenKhoaIdAndHoatDongTrue(Long chuyenKhoaId);

    Optional<BacSi> findFirstByEmailIgnoreCase(String email);
}
