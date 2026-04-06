package com.example.webapp.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "work_schedules")
public class LichLamViec {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    private BacSi bacSi;

    @Column(nullable = false)
    private LocalDate ngay;

    @Column(nullable = false)
    private LocalTime gioBatDau;

    @Column(nullable = false)
    private LocalTime gioKetThuc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiLichLamViec trangThai = TrangThaiLichLamViec.TRONG;

    @Column(nullable = false)
    private BigDecimal giaKham = BigDecimal.ZERO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BacSi getBacSi() {
        return bacSi;
    }

    public void setBacSi(BacSi bacSi) {
        this.bacSi = bacSi;
    }

    public LocalDate getNgay() {
        return ngay;
    }

    public void setNgay(LocalDate ngay) {
        this.ngay = ngay;
    }

    public LocalTime getGioBatDau() {
        return gioBatDau;
    }

    public void setGioBatDau(LocalTime gioBatDau) {
        this.gioBatDau = gioBatDau;
    }

    public LocalTime getGioKetThuc() {
        return gioKetThuc;
    }

    public void setGioKetThuc(LocalTime gioKetThuc) {
        this.gioKetThuc = gioKetThuc;
    }

    public TrangThaiLichLamViec getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiLichLamViec trangThai) {
        this.trangThai = trangThai;
    }

    public BigDecimal getGiaKham() {
        return giaKham;
    }

    public void setGiaKham(BigDecimal giaKham) {
        this.giaKham = giaKham;
    }
}
