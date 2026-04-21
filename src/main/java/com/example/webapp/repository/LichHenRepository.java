package com.example.webapp.repository;

import com.example.webapp.entity.LichHen;
import com.example.webapp.entity.TrangThaiLichHen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface LichHenRepository extends JpaRepository<LichHen, Long> {
    List<LichHen> findByBenhNhanIdOrderByNgayKhamAscGioKhamAsc(Long benhNhanId);

    List<LichHen> findByBacSiIdOrderByNgayKhamAscGioKhamAsc(Long bacSiId);

    List<LichHen> findByBacSiIdAndNgayKhamOrderByGioKhamAsc(Long bacSiId, LocalDate ngayKham);

    List<LichHen> findByBacSiIdAndNgayKhamBetweenOrderByNgayKhamAscGioKhamAsc(Long bacSiId, LocalDate fromDate,
            LocalDate toDate);

    List<LichHen> findByBacSiIdAndNgayKhamLessThanOrderByNgayKhamDescGioKhamDesc(Long bacSiId, LocalDate ngayKham);

    Optional<LichHen> findByIdAndBacSiId(Long id, Long bacSiId);

    long countByTrangThai(TrangThaiLichHen trangThai);

    long countByBacSiIdAndTrangThai(Long bacSiId, TrangThaiLichHen trangThai);

    long countByBacSiIdAndNgayKham(Long bacSiId, LocalDate ngayKham);

    long countByBacSiIdAndNgayKhamAndTrangThai(Long bacSiId, LocalDate ngayKham, TrangThaiLichHen trangThai);

    @Query("select coalesce(sum(lh.chiPhi), 0) from LichHen lh where lh.bacSi.id = :bacSiId and lh.trangThai = com.example.webapp.entity.TrangThaiLichHen.DA_KHAM")
    BigDecimal tongDoanhThuDaKhamTheoBacSi(Long bacSiId);

    @Query("select coalesce(sum(lh.chiPhi), 0) from LichHen lh where lh.trangThai = com.example.webapp.entity.TrangThaiLichHen.DA_KHAM")
    java.math.BigDecimal tongDoanhThuDaKham();

    @Modifying
    @Query("delete from LichHen lh where lh.benhNhan.id = ?1")
    int deleteByBenhNhanId(Long benhNhanId);

    @Modifying
    @Query("delete from LichHen lh where lh.bacSi.id = ?1")
    int deleteByBacSiId(Long bacSiId);
}
