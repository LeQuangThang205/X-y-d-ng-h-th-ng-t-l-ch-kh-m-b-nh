# Website quan ly dat lich kham - Benh vien A Thai Nguyen

> Tai lieu giai thich cau truc giao dien (UI) va cach su dung nhanh. Toan bo noi dung bang tieng Viet khong dau de de dang dua vao cong cu AI ho tro.

## 1. Tong quan ung dung

- **Muc tieu**: Mo phong he thong dat lich kham benh online cho benh vien, gom 2 nhom nguoi dung chinh:
  - **Khach hang**: dat lich, quan ly thong tin ca nhan, xem lich da dat.
  - **Admin / Nhan vien le tan**: quan ly lich hen, khach hang, bac si, chuyen khoa, dich vu, bao cao thong ke.
- **Cong nghe**: Spring Boot (backend), giao dien HTML / CSS tinh tai thu muc `src/main/resources/static`.

## 2. Cau truc cac trang giao dien

### 2.1. Nhom khach hang (Frontend website)

- **Trang chu landing**: `trangchu.html`
  - Header co logo benh vien, menu: Trang chu, Chuyen khoa, Bac si, Dich vu, Dat lich, Lien he.
  - Hero section 2 cot: ben trai la tieu de lon "DAT LICH KHAM BENH ONLINE NHANH CHONG – TIEN LOI" va nut "Dat lich ngay", ben phai la hinh minh hoa bac si va benh nhan.
  - Cac section tiep theo:
    - Chuyen khoa noi bat (luoi card 4 cot).
    - Bac si tieu bieu (luoi card bac si).
    - Dich vu kham chua benh (grid 3x2).
    - Hotline & ho tro.
    - Footer 3 cot: thong tin benh vien, lien ket nhanh, thong tin lien he.

- **Trang dang nhap / dang ky**: `dangnhap.html`
  - Card trung tam, chia 2 tab: "Dang nhap" va "Dang ky".
  - Tab Dang nhap: email / so dien thoai, mat khau, checkbox "Ghi nho dang nhap", link "Quen mat khau", nut Dang nhap.
  - Tab Dang ky: ho ten, so dien thoai, email, mat khau, xac nhan mat khau, nut Dang ky.
  - Giao dien su dung mau xanh duong – trang, form bo goc va shadow nhe.

- **Trang khach hang sau khi dang nhap**:
  - `customer/trangkhach.html`: trang chinh cua khach hang, header co menu, khu vuc thong ke nhanh (so lich sap toi, so lich da kham) va card "Thao tac nhanh" (dat lich moi, xem lich cua toi).
  - `customer/hoso.html`: thong tin ca nhan, layout 2 cot (menu ben trai, noi dung ben phai). Co form chinh sua: ho ten, tuoi, gioi tinh, CCCD, so dien thoai, dia chi, email; ben duoi la bang lich su dat lich kham.
  - `customer/khoa.html`: danh sach chuyen khoa (grid card), moi card co icon, ten chuyen khoa, mo ta ngan va nut "Dat lich kham".
  - `customer/bacsi.html`: danh sach bac si, phia tren co thanh loc (dropdown chuyen khoa, thoi gian lam viec), ben duoi la card bac si (anh dai dien chu cai, ten, chuc danh, chuyen khoa).
  - `customer/datlich.html`: form dat lich chia 2 nhom:
    - Thong tin ca nhan: ho ten, tuoi, gioi tinh, CCCD, so dien thoai, dia chi.
    - Thong tin kham benh: chuyen khoa, bac si, dich vu, ngay, khung gio, trieu chung co ban.

### 2.2. Nhom admin / nhan vien le tan (Dashboard)

- **Dashboard chinh**: `admin/trangquantri.html`
  - Layout sidebar + noi dung chinh:
    - Sidebar: Dashboard, Quan ly khach hang, Quan ly dat lich, Quan ly bac si, Quan ly chuyen khoa, Quan ly dich vu, Thong ke & bao cao.
    - Noi dung: cac card thong ke nhanh (tong khach hang, lich kham hom nay, lich kham thang).

- **Quan ly dat lich**: `admin/lichhen.html`
  - Bang danh sach lich kham, cac cot: Khach hang, Bac si, Chuyen khoa, Thoi gian, Trang thai, Thao tac.
  - Nut hanh dong mo popup chi tiet lich kham (modal), trong modal co nut Xac nhan lich / Dong.

- **Quan ly bac si**: `admin/bacsi.html`
  - Su dung chung layout sidebar + main.
  - Noi dung chinh duoc thiet ke de de dang bo sung bang danh sach va form them / sua bac si.

- **Quan ly chuyen khoa**: `admin/khoa.html`
  - Tuong tu quan ly bac si, dung bang danh sach de luu chuyen khoa, nut Them moi, Chinh sua, Xoa (co the bo sung sau).

- **Thong ke & bao cao**: `admin/baocao.html`
  - Khu vuc card chua bieu do / chi so tong hop (co the tich hop chart JS sau).

## 3. Giai thich cac thanh phan CSS chinh

File style chinh: `src/main/resources/static/css/kieu.css`.

- **Bien mau va reset**:
  - Dinh nghia cac bien `--primary`, `--secondary`, `--bg`, `--card`, `--text`, `--text-muted`, `--border` de thong nhat giao dien toan he thong.
  - Reset box sizing, font va nen cho toan bo body.

- **Nhom nut (class `.btn`)**:
  - `.btn`: nut co border bo tron, font ro rang, hieu ung hover nhe.
  - `.btn-primary`: nut chinh mau xanh duong, gradient, shadow.
  - `.btn-outline`: nut vien mong, nen toi hon, dung cho hanh dong phu.
  - `.btn-lg`, `.btn-block`: bien the nut lon va nut full chieu ngang.

- **Landing page**:
  - `.landing-header`, `.landing-nav`, `.header-actions`, `.lang-switch`, `.lang-btn`: tao header sticky hien dai co menu va nut chuyen ngon ngu.
  - `.hero-section`, `.hero-inner`, `.hero-heading`, `.hero-desc`, `.hero-image-placeholder`: tao hero 2 cot, co hinh minh hoa bac si / benh nhan.
  - `.section`, `.dept-grid`, `.dept-card`, `.doctor-grid`, `.service-grid`, `.section-hotline`, `.landing-footer`, `.footer-grid`: quan ly cac block Chuyen khoa, Bac si, Dich vu, Hotline, Footer theo kieu card & grid.

- **Customer layout**:
  - `.customer-layout`, `.customer-header`, `.customer-content`: bo cuc cho cac trang sau khi khach hang dang nhap.
  - `.stats-row`, `.stat-card`: the hien thong ke nhanh (so lich, so da kham).
  - `.profile-layout`, `.profile-menu`: layout 2 cot cho trang ho so ca nhan.
  - `.booking-form-wrap`, `.form-row`, `.form-section`: to chuc form dat lich theo tung nhom ro rang.

- **Admin dashboard**:
  - `.app-layout`, `.sidebar`, `.sidebar-nav`, `.main-area`, `.topbar`, `.content`: bo cuc tong the kieu dashboard hien dai.
  - `.table-wrap`, `table`, `.badge`, `.badge-success`, `.badge-info`: dinh dang bang du lieu va nhan trang thai.
  - `.modal`, `.modal-overlay`, `.modal-content`: popup chi tiet lich hen cho nhan vien le tan.

## 4. Cach chay ung dung

1. Yeu cau:
   - Da cai dat Java 17 (hoac tuong duong) va Maven.
2. Cac buoc chay:
   - Mo du an trong IDE (VD: IntelliJ, VS Code + plugin Spring).
   - Chay class Spring Boot chinh (co annotation `@SpringBootApplication`).
   - Mac dinh ung dung chay tai `http://localhost:8080/`.
3. Cac duong dan chinh:
   - Trang chu landing: `http://localhost:8080/trangchu.html`
   - Dang nhap / dang ky: `http://localhost:8080/dangnhap.html`
   - Sau khi dang nhap voi vai tro customer: `http://localhost:8080/customer/trangkhach.html`
   - Dashboard admin: `http://localhost:8080/admin/trangquantri.html`

## 5. Goi y noi dung bao cao do an

Khi viet bao cao cho de tai "Xay dung website quan ly dat lich kham – Benh vien A Thai Nguyen", co the trinh bay cac muc chinh sau:

1. **Mo dau**:
   - Ly do chon de tai: nhu cau dat lich online, giam un tac, tang trai nghiem benh nhan.
2. **Phan tich yeu cau**:
   - Doi voi khach hang: dang ky tai khoan, dang nhap, dat lich, xem lich, xem lich su.
   - Doi voi admin / le tan: xem danh sach lich, xac nhan / huy lich, quan ly thong tin bac si, chuyen khoa, dich vu.
3. **Thiet ke giao dien**:
   - Mo ta cac man hinh chinh duoc liet ke o muc 2 (kem hinh chup man hinh).
   - Chu y trinh bay ro layout: header, sidebar, cac khu vuc card, bang, form.
4. **Thiet ke he thong**:
   - Mo ta ngan ve phan backend (controller, entity, repository neu co).
5. **Danh gia va huong phat trien**:
   - Uu diem: giao dien hien dai, ro rang, phan tach khach hang / admin.
   - Huong mo rong: tich hop co so du lieu that, he thong xac thuc, gui email / SMS nhac lich, bieu do thong ke thuc te.

Tai lieu nay co the dung lam co so de ban tiep tuc hoan thien chuc nang back end va mo rong giao dien theo nhu cau.

# Java Web Project

Dự án web Java sử dụng Spring Boot framework.

## Yêu cầu hệ thống

- Java JDK 17 hoặc cao hơn
- Maven 3.6+ (hoặc sử dụng Maven Wrapper)

## Cài đặt và chạy

### 1. Cài đặt dependencies

```bash
mvn clean install
```

### 2. Chạy ứng dụng

```bash
mvn spring-boot:run
```

Hoặc chạy trực tiếp class `WebApplication.java`

### 3. Truy cập ứng dụng

Sau khi ứng dụng khởi động, truy cập:

- API Home: http://localhost:8080/api/
- API Hello: http://localhost:8080/api/hello

## Cấu trúc dự án

```
java-web-project/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/webapp/
│   │   │       ├── WebApplication.java      # Main class
│   │   │       └── controller/
│   │   │           └── HomeController.java  # REST Controller
│   │   └── resources/
│   │       └── application.properties       # Cấu hình ứng dụng
│   └── test/                                 # Test files
├── pom.xml                                   # Maven configuration
└── README.md
```

## API Endpoints

- `GET /api/` - Trang chủ API
- `GET /api/hello` - Lời chào từ server

## Phát triển tiếp

Bạn có thể thêm:

- Database (JPA/Hibernate)
- Security (Spring Security)
- Thymeleaf cho frontend
- REST API endpoints mới
- Service layer
- Repository layer

## Tài liệu tham khảo

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- # [Spring Web MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)

# X-y-d-ng-h-th-ng-t-l-ch-kh-m-b-nh

> > > > > > > 7dfe002d19905a7cbe5b3372c8d213efd3d7b2b9
