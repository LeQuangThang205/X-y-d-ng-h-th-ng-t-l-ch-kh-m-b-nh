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

    public List<LichHen> lichHomNayCuaBacSi(Long bacSiId) {
        return lichHenRepository.findByBacSiIdAndNgayKhamOrderByGioKhamAsc(bacSiId, LocalDate.now());
    }

    public List<LichHen> lich7NgayCuaBacSi(Long bacSiId) {
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = fromDate.plusDays(6);
        return lichHenRepository.findByBacSiIdAndNgayKhamBetweenOrderByNgayKhamAscGioKhamAsc(bacSiId, fromDate, toDate);
    }

    public List<LichHen> lichSuKhamCuaBacSi(Long bacSiId) {
        return lichHenRepository.findByBacSiIdAndNgayKhamLessThanOrderByNgayKhamDescGioKhamDesc(bacSiId,
                LocalDate.now());
    }

    public List<LichLamViec> lichLamViecTrongNgayCuaBacSi(Long bacSiId, LocalDate ngay) {
        return lichLamViecRepository.findByBacSiIdAndNgayOrderByGioBatDauAsc(bacSiId, ngay);
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

    @Transactional
    public LichHen taoLichTaiKham(Long lichHenId, LocalDate ngayTaiKham, LocalTime gioTaiKham, String ghiChuTaiKham) {
        LichHen lichGoc = lichHenRepository.findById(lichHenId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay lich hen goc"));

        if (ngayTaiKham == null || gioTaiKham == null) {
            throw new IllegalArgumentException("Ngay tai kham va gio tai kham la bat buoc");
        }

        BacSi bacSi = lichGoc.getBacSi();
        BenhNhan benhNhan = lichGoc.getBenhNhan();

        List<LichLamViec> schedules = lichLamViecRepository.findByBacSiIdAndNgayOrderByGioBatDauAsc(
                bacSi.getId(),
                ngayTaiKham);

        LichLamViec slot = schedules.stream()
                .filter(item -> gioTaiKham.equals(item.getGioBatDau()))
                .findFirst()
                .orElse(null);

        if (slot == null) {
            slot = new LichLamViec();
            slot.setBacSi(bacSi);
            slot.setNgay(ngayTaiKham);
            slot.setGioBatDau(gioTaiKham);
            slot.setGioKetThuc(gioTaiKham.plusMinutes(30));
            slot.setTrangThai(TrangThaiLichLamViec.TRONG);
            BigDecimal giaKham = lichGoc.getChiPhi() != null ? lichGoc.getChiPhi() : BigDecimal.ZERO;
            slot.setGiaKham(giaKham);
            slot = lichLamViecRepository.save(slot);
        }

        if (slot.getTrangThai() != TrangThaiLichLamViec.TRONG) {
            throw new IllegalArgumentException("Khung gio tai kham da duoc dat hoac bi khoa");
        }

        LichHen taiKham = new LichHen();
        taiKham.setBenhNhan(benhNhan);
        taiKham.setBacSi(bacSi);
        taiKham.setChuyenKhoa(lichGoc.getChuyenKhoa());
        taiKham.setPhongKham(lichGoc.getPhongKham());
        taiKham.setLichLamViec(slot);
        taiKham.setNgayKham(ngayTaiKham);
        taiKham.setGioKham(gioTaiKham);
        taiKham.setTrangThai(TrangThaiLichHen.DA_XAC_NHAN);
        taiKham.setTrieuChung("Tai kham - " + (lichGoc.getTrieuChung() == null ? "" : lichGoc.getTrieuChung()));
        taiKham.setGhiChuTaiKham(ghiChuTaiKham);
        taiKham.setChiPhi(slot.getGiaKham() == null ? BigDecimal.ZERO : slot.getGiaKham());

        LichHen saved = lichHenRepository.save(taiKham);
        slot.setTrangThai(TrangThaiLichLamViec.DA_DAT);
        lichLamViecRepository.save(slot);

        return saved;
    }

    @Transactional
    public Map<String, Object> khoaKhungGioTamThoi(Long bacSiId, LocalDate ngay, List<Long> slotIds) {
        if (ngay == null) {
            ngay = LocalDate.now();
        }
        if (slotIds == null || slotIds.isEmpty()) {
            throw new IllegalArgumentException("Can chon it nhat 1 khung gio de khoa");
        }

        List<LichLamViec> slots = lichLamViecRepository.findByBacSiIdAndNgayAndIdIn(bacSiId, ngay, slotIds);
        long locked = 0;
        long skipped = 0;
        for (LichLamViec slot : slots) {
            if (slot.getTrangThai() == TrangThaiLichLamViec.TRONG) {
                slot.setTrangThai(TrangThaiLichLamViec.KHOA);
                locked++;
            } else {
                skipped++;
            }
        }
        lichLamViecRepository.saveAll(slots);

        Map<String, Object> result = new HashMap<>();
        result.put("ngay", ngay);
        result.put("tongChon", slotIds.size());
        result.put("daKhoa", locked);
        result.put("boQua", skipped);
        return result;
    }

    public Map<String, Object> baoCaoCaNhanBacSi(Long bacSiId) {
        LocalDate homNay = LocalDate.now();
        long tongLichHomNay = lichHenRepository.countByBacSiIdAndNgayKham(bacSiId, homNay);
        long daKhamHomNay = lichHenRepository.countByBacSiIdAndNgayKhamAndTrangThai(bacSiId, homNay,
                TrangThaiLichHen.DA_KHAM);
        long tongDaKham = lichHenRepository.countByBacSiIdAndTrangThai(bacSiId, TrangThaiLichHen.DA_KHAM);
        BigDecimal doanhThu = lichHenRepository.tongDoanhThuDaKhamTheoBacSi(bacSiId);

        Map<String, Object> report = new HashMap<>();
        report.put("ngay", homNay);
        report.put("tongLichHomNay", tongLichHomNay);
        report.put("daKhamHomNay", daKhamHomNay);
        report.put("tongBenhNhanDaKham", tongDaKham);
        report.put("doanhThu", doanhThu == null ? BigDecimal.ZERO : doanhThu);
        return report;
    }

    @Transactional
    public Map<String, Object> chotCaBacSi(Long bacSiId) {
        LocalDate homNay = LocalDate.now();
        List<LichHen> lichHomNay = lichHenRepository.findByBacSiIdAndNgayKhamOrderByGioKhamAsc(bacSiId, homNay);

        long boLo = 0;
        for (LichHen item : lichHomNay) {
            TrangThaiLichHen tt = item.getTrangThai();
            if (tt == TrangThaiLichHen.CHO_XAC_NHAN || tt == TrangThaiLichHen.DA_XAC_NHAN
                    || tt == TrangThaiLichHen.DANG_KHAM) {
                item.setTrangThai(TrangThaiLichHen.BO_LO);
                boLo++;
            }
        }
        lichHenRepository.saveAll(lichHomNay);

        Map<String, Object> result = new HashMap<>();
        result.put("ngay", homNay);
        result.put("tongLich", lichHomNay.size());
        result.put("chuyenBoLo", boLo);
        return result;
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
