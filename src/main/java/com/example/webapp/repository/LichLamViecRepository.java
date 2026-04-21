package com.example.webapp.repository;

import com.example.webapp.entity.LichLamViec;
import com.example.webapp.entity.TrangThaiLichLamViec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LichLamViecRepository extends JpaRepository<LichLamViec, Long> {
    List<LichLamViec> findByBacSiIdAndNgayAndTrangThai(Long bacSiId, LocalDate ngay, TrangThaiLichLamViec trangThai);

    List<LichLamViec> findByBacSiIdAndNgayOrderByGioBatDauAsc(Long bacSiId, LocalDate ngay);

    List<LichLamViec> findByBacSiIdAndNgayAndIdIn(Long bacSiId, LocalDate ngay, List<Long> ids);

    @Modifying
    @Query("delete from LichLamViec lw where lw.bacSi.id = ?1")
    int deleteByBacSiId(Long bacSiId);
}
