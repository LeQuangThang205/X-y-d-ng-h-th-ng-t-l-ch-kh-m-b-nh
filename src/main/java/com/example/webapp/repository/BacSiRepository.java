package com.example.webapp.repository;

import com.example.webapp.entity.BacSi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BacSiRepository extends JpaRepository<BacSi, Long> {
    List<BacSi> findByHoatDongTrue();

    List<BacSi> findByChuyenKhoaIdAndHoatDongTrue(Long chuyenKhoaId);
}
