package com.example.webapp.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
public class LichHen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private BenhNhan benhNhan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    private BacSi bacSi;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "specialty_id", nullable = false)
    private ChuyenKhoa chuyenKhoa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clinic_room_id")
    private PhongKham phongKham;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "work_schedule_id")
    private LichLamViec lichLamViec;

    @Column(nullable = false)
    private LocalDate ngayKham;

    @Column(nullable = false)
    private LocalTime gioKham;

    @Column(length = 2000)
    private String trieuChung;

    @Column(length = 2000)
    private String chanDoan;

    @Column(length = 4000)
    private String huongDieuTri;

    @Column(length = 4000)
    private String donThuoc;

    @Column(length = 2000)
    private String ghiChuTaiKham;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiLichHen trangThai = TrangThaiLichHen.CHO_XAC_NHAN;

    @Column(nullable = false)
    private BigDecimal chiPhi = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime taoLuc;

    @PrePersist
    public void prePersist() {
        if (taoLuc == null) {
            taoLuc = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BenhNhan getBenhNhan() {
        return benhNhan;
    }

    public void setBenhNhan(BenhNhan benhNhan) {
        this.benhNhan = benhNhan;
    }

    public BacSi getBacSi() {
        return bacSi;
    }

    public void setBacSi(BacSi bacSi) {
        this.bacSi = bacSi;
    }

    public ChuyenKhoa getChuyenKhoa() {
        return chuyenKhoa;
    }

    public void setChuyenKhoa(ChuyenKhoa chuyenKhoa) {
        this.chuyenKhoa = chuyenKhoa;
    }

    public PhongKham getPhongKham() {
        return phongKham;
    }

    public void setPhongKham(PhongKham phongKham) {
        this.phongKham = phongKham;
    }

    public LichLamViec getLichLamViec() {
        return lichLamViec;
    }

    public void setLichLamViec(LichLamViec lichLamViec) {
        this.lichLamViec = lichLamViec;
    }

    public LocalDate getNgayKham() {
        return ngayKham;
    }

    public void setNgayKham(LocalDate ngayKham) {
        this.ngayKham = ngayKham;
    }

    public LocalTime getGioKham() {
        return gioKham;
    }

    public void setGioKham(LocalTime gioKham) {
        this.gioKham = gioKham;
    }

    public String getTrieuChung() {
        return trieuChung;
    }

    public void setTrieuChung(String trieuChung) {
        this.trieuChung = trieuChung;
    }

    public String getChanDoan() {
        return chanDoan;
    }

    public void setChanDoan(String chanDoan) {
        this.chanDoan = chanDoan;
    }

    public String getHuongDieuTri() {
        return huongDieuTri;
    }

    public void setHuongDieuTri(String huongDieuTri) {
        this.huongDieuTri = huongDieuTri;
    }

    public String getDonThuoc() {
        return donThuoc;
    }

    public void setDonThuoc(String donThuoc) {
        this.donThuoc = donThuoc;
    }

    public String getGhiChuTaiKham() {
        return ghiChuTaiKham;
    }

    public void setGhiChuTaiKham(String ghiChuTaiKham) {
        this.ghiChuTaiKham = ghiChuTaiKham;
    }

    public TrangThaiLichHen getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiLichHen trangThai) {
        this.trangThai = trangThai;
    }

    public BigDecimal getChiPhi() {
        return chiPhi;
    }

    public void setChiPhi(BigDecimal chiPhi) {
        this.chiPhi = chiPhi;
    }

    public LocalDateTime getTaoLuc() {
        return taoLuc;
    }

    public void setTaoLuc(LocalDateTime taoLuc) {
        this.taoLuc = taoLuc;
    }
}
