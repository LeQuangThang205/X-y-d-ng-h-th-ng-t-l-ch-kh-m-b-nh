package com.example.webapp.repository;

import com.example.webapp.entity.LichHen;
import com.example.webapp.entity.TrangThaiLichHen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichHenRepository extends JpaRepository<LichHen, Long> {
    List<LichHen> findByBenhNhanIdOrderByNgayKhamAscGioKhamAsc(Long benhNhanId);

    List<LichHen> findByBacSiIdOrderByNgayKhamAscGioKhamAsc(Long bacSiId);

    long countByTrangThai(TrangThaiLichHen trangThai);

    @Query("select coalesce(sum(lh.chiPhi), 0) from LichHen lh where lh.trangThai = com.example.webapp.entity.TrangThaiLichHen.DA_KHAM")
    java.math.BigDecimal tongDoanhThuDaKham();
}
