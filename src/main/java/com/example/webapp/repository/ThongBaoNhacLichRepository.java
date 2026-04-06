package com.example.webapp.repository;

import com.example.webapp.entity.ThongBaoNhacLich;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThongBaoNhacLichRepository extends JpaRepository<ThongBaoNhacLich, Long> {
    List<ThongBaoNhacLich> findByBenhNhanIdOrderByThoiDiemNhacDesc(Long benhNhanId);
}
