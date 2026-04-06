package com.example.webapp.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointment_reminders")
public class ThongBaoNhacLich {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "appointment_id", nullable = false)
    private LichHen lichHen;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private BenhNhan benhNhan;

    @Column(nullable = false, length = 1000)
    private String noiDung;

    @Column(nullable = false)
    private LocalDateTime thoiDiemNhac;

    @Column(nullable = false)
    private boolean daGui = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LichHen getLichHen() {
        return lichHen;
    }

    public void setLichHen(LichHen lichHen) {
        this.lichHen = lichHen;
    }

    public BenhNhan getBenhNhan() {
        return benhNhan;
    }

    public void setBenhNhan(BenhNhan benhNhan) {
        this.benhNhan = benhNhan;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public LocalDateTime getThoiDiemNhac() {
        return thoiDiemNhac;
    }

    public void setThoiDiemNhac(LocalDateTime thoiDiemNhac) {
        this.thoiDiemNhac = thoiDiemNhac;
    }

    public boolean isDaGui() {
        return daGui;
    }

    public void setDaGui(boolean daGui) {
        this.daGui = daGui;
    }
}
