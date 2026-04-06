package com.example.webapp.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "specialties")
public class ChuyenKhoa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ten;

    @Column(length = 1000)
    private String moTa;

    @Column(nullable = false)
    private BigDecimal phiKhamMacDinh = BigDecimal.ZERO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public BigDecimal getPhiKhamMacDinh() {
        return phiKhamMacDinh;
    }

    public void setPhiKhamMacDinh(BigDecimal phiKhamMacDinh) {
        this.phiKhamMacDinh = phiKhamMacDinh;
    }
}
