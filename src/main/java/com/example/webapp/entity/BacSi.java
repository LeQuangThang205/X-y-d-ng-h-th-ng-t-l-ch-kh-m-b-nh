package com.example.webapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "doctors")
public class BacSi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String hoTen;

    private String chucDanh;

    @Column(unique = true)
    private String soDienThoai;

    @Column(unique = true)
    private String email;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email", referencedColumnName = "username", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_doctors_user_account"))
    private NguoiDung taiKhoanNguoiDung;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String anhChanDung;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "specialty_id")
    private ChuyenKhoa chuyenKhoa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clinic_room_id")
    private PhongKham phongKham;

    @Column(nullable = false)
    private Boolean hoatDong = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getChucDanh() {
        return chucDanh;
    }

    public void setChucDanh(String chucDanh) {
        this.chucDanh = chucDanh;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public NguoiDung getTaiKhoanNguoiDung() {
        return taiKhoanNguoiDung;
    }

    public void setTaiKhoanNguoiDung(NguoiDung taiKhoanNguoiDung) {
        this.taiKhoanNguoiDung = taiKhoanNguoiDung;
    }

    public String getAnhChanDung() {
        return anhChanDung;
    }

    public void setAnhChanDung(String anhChanDung) {
        this.anhChanDung = anhChanDung;
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

    public Boolean getHoatDong() {
        return hoatDong;
    }

    public void setHoatDong(Boolean hoatDong) {
        this.hoatDong = hoatDong;
    }
}
