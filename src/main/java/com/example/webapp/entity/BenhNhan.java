package com.example.webapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "patients")
public class BenhNhan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullname;

    private Integer age;

    private String gender;

    private String cccd;

    private String phone;

    private String address;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String anhChanDung;

    @Column(name = "tai_khoan", unique = true)
    private String taiKhoan;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tai_khoan", referencedColumnName = "username", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_patients_user_account"))
    private NguoiDung taiKhoanNguoiDung;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(String taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public String getAnhChanDung() {
        return anhChanDung;
    }

    public void setAnhChanDung(String anhChanDung) {
        this.anhChanDung = anhChanDung;
    }

    public NguoiDung getTaiKhoanNguoiDung() {
        return taiKhoanNguoiDung;
    }

    public void setTaiKhoanNguoiDung(NguoiDung taiKhoanNguoiDung) {
        this.taiKhoanNguoiDung = taiKhoanNguoiDung;
    }
}
