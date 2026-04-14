package com.example.webapp.service;

import com.example.webapp.entity.*;
import com.example.webapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DatLichKhamService {
    @Autowired
    private BenhNhanRepository benhNhanRepository;

    @Autowired
    private BacSiRepository bacSiRepository;

    @Autowired
    private ChuyenKhoaRepository chuyenKhoaRepository;

    @Autowired
    private PhongKhamRepository phongKhamRepository;

    @Autowired
    private LichLamViecRepository lichLamViecRepository;

    @Autowired
    private LichHenRepository lichHenRepository;

    @Autowired
    private ThongBaoNhacLichRepository thongBaoNhacLichRepository;

    public List<ChuyenKhoa> layDanhSachChuyenKhoa() {
        return chuyenKhoaRepository.findAll();
    }

    public List<BacSi> layDanhSachBacSi(Long chuyenKhoaId) {
        if (chuyenKhoaId == null) {
            return bacSiRepository.findByHoatDongTrue();
        }
        return bacSiRepository.findByChuyenKhoaIdAndHoatDongTrue(chuyenKhoaId);
    }

    public List<LichLamViec> layKhungGioTrong(Long bacSiId, LocalDate ngay) {
        List<LichLamViec> schedules = lichLamViecRepository.findByBacSiIdAndNgayOrderByGioBatDauAsc(bacSiId, ngay);

        // Tuong thich du lieu cu: slot co trangThai null se duoc xem la TRONG.
        for (LichLamViec item : schedules) {
            if (item.getTrangThai() == null) {
                item.setTrangThai(TrangThaiLichLamViec.TRONG);
                lichLamViecRepository.save(item);
            }
        }

        return schedules.stream()
                .filter(item -> item.getTrangThai() == TrangThaiLichLamViec.TRONG)
                .toList();
    }

    @Transactional
    public LichHen datLich(Long benhNhanId, Long lichLamViecId, String trieuChung) {
        BenhNhan benhNhan = benhNhanRepository.findById(benhNhanId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay benh nhan"));

        LichLamViec slot = lichLamViecRepository.findById(lichLamViecId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay khung gio"));

        if (slot.getTrangThai() != TrangThaiLichLamViec.TRONG) {
            throw new IllegalArgumentException("Khung gio nay da duoc dat hoac bi khoa");
        }

        LichHen lichHen = new LichHen();
        lichHen.setBenhNhan(benhNhan);
        lichHen.setBacSi(slot.getBacSi());
        lichHen.setChuyenKhoa(slot.getBacSi().getChuyenKhoa());
        lichHen.setPhongKham(slot.getBacSi().getPhongKham());
        lichHen.setLichLamViec(slot);
        lichHen.setNgayKham(slot.getNgay());
        lichHen.setGioKham(slot.getGioBatDau());
        lichHen.setTrieuChung(trieuChung);
        lichHen.setTrangThai(TrangThaiLichHen.CHO_XAC_NHAN);
        lichHen.setChiPhi(slot.getGiaKham());

        LichHen saved = lichHenRepository.save(lichHen);

        slot.setTrangThai(TrangThaiLichLamViec.DA_DAT);
        lichLamViecRepository.save(slot);

        taoThongBaoNhacLich(saved);
        return saved;
    }

    public List<LichHen> lichCuaBenhNhan(Long benhNhanId) {
        return lichHenRepository.findByBenhNhanIdOrderByNgayKhamAscGioKhamAsc(benhNhanId);
    }

    public List<ThongBaoNhacLich> thongBaoBenhNhan(Long benhNhanId) {
        return thongBaoNhacLichRepository.findByBenhNhanIdOrderByThoiDiemNhacDesc(benhNhanId);
    }

    public List<LichHen> lichCuaBacSi(Long bacSiId) {
        return lichHenRepository.findByBacSiIdOrderByNgayKhamAscGioKhamAsc(bacSiId);
    }

    @Transactional
    public LichHen capNhatTrangThaiLichHen(Long lichHenId, TrangThaiLichHen trangThaiMoi) {
        LichHen lichHen = lichHenRepository.findById(lichHenId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay lich hen"));

        lichHen.setTrangThai(trangThaiMoi);

        if (trangThaiMoi == TrangThaiLichHen.DA_HUY) {
            LichLamViec slot = lichHen.getLichLamViec();
            if (slot != null) {
                slot.setTrangThai(TrangThaiLichLamViec.TRONG);
                lichLamViecRepository.save(slot);
            }
        }

        return lichHenRepository.save(lichHen);
    }

    @Transactional
    public LichHen capNhatKetQuaKham(Long lichHenId, String chanDoan, String huongDieuTri,
            String donThuoc, String ghiChuTaiKham) {
        LichHen lichHen = lichHenRepository.findById(lichHenId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay lich hen"));

        lichHen.setChanDoan(chanDoan);
        lichHen.setHuongDieuTri(huongDieuTri);
        lichHen.setDonThuoc(donThuoc);
        lichHen.setGhiChuTaiKham(ghiChuTaiKham);

        if (lichHen.getTrangThai() != TrangThaiLichHen.DA_HUY) {
            lichHen.setTrangThai(TrangThaiLichHen.DA_KHAM);
        }

        return lichHenRepository.save(lichHen);
    }

    public ChuyenKhoa luuChuyenKhoa(ChuyenKhoa chuyenKhoa) {
        if (chuyenKhoa.getPhiKhamMacDinh() == null) {
            chuyenKhoa.setPhiKhamMacDinh(BigDecimal.ZERO);
        }
        return chuyenKhoaRepository.save(chuyenKhoa);
    }

    @Transactional
    public void xoaChuyenKhoa(Long id) {
        ChuyenKhoa chuyenKhoa = chuyenKhoaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chuyên khoa không tồn tại"));

        List<BacSi> bacSiList = bacSiRepository.findByChuyenKhoaId(id);
        if (!bacSiList.isEmpty()) {
            throw new IllegalArgumentException("Không thể xóa chuyên khoa đang được bác sĩ sử dụng");
        }

        chuyenKhoaRepository.deleteById(id);
    }

    public BacSi luuBacSi(BacSi bacSi, Long chuyenKhoaId, Long phongKhamId) {
        ChuyenKhoa chuyenKhoa = chuyenKhoaRepository.findById(chuyenKhoaId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay chuyen khoa"));
        PhongKham phongKham = phongKhamRepository.findById(phongKhamId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay phong kham"));

        bacSi.setChuyenKhoa(chuyenKhoa);
        bacSi.setPhongKham(phongKham);
        if (bacSi.getHoatDong() == null) {
            bacSi.setHoatDong(true);
        }
        return bacSiRepository.save(bacSi);
    }

    public PhongKham luuPhongKham(PhongKham phongKham) {
        return phongKhamRepository.save(phongKham);
    }

    public List<BenhNhan> layBenhNhan() {
        return benhNhanRepository.findAll();
    }

    public List<BacSi> layBacSiQuanTri() {
        return bacSiRepository.findAll();
    }

    public List<PhongKham> layPhongKham() {
        return phongKhamRepository.findAll();
    }

    @Transactional
    public LichLamViec taoLichLamViec(Long bacSiId, LocalDate ngay, LocalTime gioBatDau, LocalTime gioKetThuc,
            BigDecimal giaKham) {
        BacSi bacSi = bacSiRepository.findById(bacSiId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay bac si"));

        if (gioKetThuc.isBefore(gioBatDau) || gioKetThuc.equals(gioBatDau)) {
            throw new IllegalArgumentException("Gio ket thuc phai lon hon gio bat dau");
        }

        LichLamViec lichLamViec = new LichLamViec();
        lichLamViec.setBacSi(bacSi);
        lichLamViec.setNgay(ngay);
        lichLamViec.setGioBatDau(gioBatDau);
        lichLamViec.setGioKetThuc(gioKetThuc);
        lichLamViec.setTrangThai(TrangThaiLichLamViec.TRONG);

        BigDecimal gia = giaKham;
        if (gia == null || gia.compareTo(BigDecimal.ZERO) < 0) {
            gia = bacSi.getChuyenKhoa() != null ? bacSi.getChuyenKhoa().getPhiKhamMacDinh() : BigDecimal.ZERO;
        }
        lichLamViec.setGiaKham(gia);

        return lichLamViecRepository.save(lichLamViec);
    }

    public Map<String, Object> thongKeTongHop() {
        long tongLich = lichHenRepository.count();
        long tongHuy = lichHenRepository.countByTrangThai(TrangThaiLichHen.DA_HUY);
        BigDecimal doanhThu = lichHenRepository.tongDoanhThuDaKham();

        double tiLeHuy = tongLich == 0 ? 0 : (tongHuy * 100.0) / tongLich;

        Map<String, Object> report = new HashMap<>();
        report.put("tongLuotDat", tongLich);
        report.put("tongHuy", tongHuy);
        report.put("tiLeHuy", tiLeHuy);
        report.put("doanhThu", doanhThu);
        return report;
    }

    private void taoThongBaoNhacLich(LichHen lichHen) {
        LocalDateTime lichKham = LocalDateTime.of(lichHen.getNgayKham(), lichHen.getGioKham());
        LocalDateTime nhacLuc = lichKham.minusHours(24);
        if (nhacLuc.isBefore(LocalDateTime.now())) {
            nhacLuc = LocalDateTime.now().plusMinutes(1);
        }

        ThongBaoNhacLich thongBao = new ThongBaoNhacLich();
        thongBao.setLichHen(lichHen);
        thongBao.setBenhNhan(lichHen.getBenhNhan());
        thongBao.setThoiDiemNhac(nhacLuc);
        thongBao.setNoiDung("Nhac lich kham voi bac si " + lichHen.getBacSi().getHoTen() + " vao "
                + lichHen.getNgayKham() + " " + lichHen.getGioKham());
        thongBao.setDaGui(false);

        thongBaoNhacLichRepository.save(thongBao);
    }
}
