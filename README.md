# TT Care+ - Hệ thống đặt lịch khám bệnh trực tuyến

**TT Care+** là nền tảng web hiện đại dành cho bệnh viện, cho phép bệnh nhân đặt lịch khám trực tuyến một cách nhanh chóng và thuận tiện, đồng thời cung cấp bảng điều khiển quản lý toàn diện cho nhân viên bệnh viện.

## 📋 Mục lục

- [Tổng quan dự án](#tổng-quan-dự-án)
- [Tính năng chính](#tính-năng-chính)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Cấu trúc thư mục](#cấu-trúc-thư-mục)
- [Hướng dẫn cài đặt](#hướng-dẫn-cài-đặt)
- [Hướng dẫn sử dụng](#hướng-dẫn-sử-dụng)
- [Các thực thể dữ liệu](#các-thực-thể-dữ-liệu)
- [API chính](#api-chính)
- [Hướng dẫn phát triển](#hướng-dẫn-phát-triển)

---

## 🎯 Tổng quan dự án

**TT Care+** là một hệ thống quản lý lịch khám bệnh toàn diện với 3 nhóm người dùng chính:

### 👥 Nhóm người dùng

1. **Khách hàng (Bệnh nhân)**
   - Đặt lịch khám bệnh trực tuyến
   - Quản lý thông tin cá nhân
   - Xem lịch khám của mình
   - Xem danh sách bác sĩ và chuyên khoa
   - Nhận thông báo nhắc lịch

2. **Bác sĩ**
   - Xem lịch khám được phân công
   - Quản lý thời gian làm việc

3. **Quản trị viên / Nhân viên lễ tân**
   - Quản lý toàn bộ lịch khám hẹn
   - Quản lý thông tin bệnh nhân
   - Quản lý danh sách bác sĩ
   - Quản lý chuyên khoa và phòng khám
   - Xem báo cáo và thống kê

### 🎨 Thiết kế giao diện

- **Landing page**: Giới thiệu bệnh viện, quảng bá dịch vụ
- **Giao diện khách hàng**: Thân thiện, dễ sử dụng, responsive
- **Dashboard quản trị**: Chuyên nghiệp, hiện đại, đầy đủ chức năng

---

## ✨ Tính năng chính

### 🔐 Xác thực & Phân quyền

- Đăng nhập/Đăng ký cho khách hàng
- Phân biệt vai trò: Khách hàng, Bác sĩ, Quản trị viên
- Quản lý phiên đăng nhập an toàn (localStorage)
- Nhắc lịch tự động qua email/SMS

### 📅 Quản lý lịch khám

- Đặt lịch khám nhanh (3-4 bước)
- Chọn chuyên khoa, bác sĩ, khung giờ phù hợp
- Xem trạng thái lịch hẹn (Chờ xác nhận, Đã xác nhận, Đã khám, v.v.)
- Xác nhận / Hủy lịch (cho bệnh viện)

### 👨‍⚕️ Quản lý bác sĩ & Chuyên khoa

- Danh sách bác sĩ với ảnh đại diện (avatar với 2 chữ cái)
- Phân loại theo chuyên khoa (Nội tổng quát, Tai Mũi Họng, Da liễu, v.v.)
- Lịch làm việc linh hoạt

### 👤 Hồ sơ bệnh nhân

- Cập nhật thông tin cá nhân (Họ tên, Tuổi, Giới tính, CCCD, Địa chỉ)
- Lưu trữ lịch sử khám bệnh
- Hỗ trợ tái khám nhanh

### 📊 Thống kê & Báo cáo

- Thống kê số lượng lịch khám
- Báo cáo tỉ lệ khám thành công
- Phân tích theo chuyên khoa, bác sĩ

### 📱 Giao diện Responsive

- Tối ưu cho desktop, tablet, mobile
- Header thông minh (ẩn/hiển khi cuộn)
- Menu dropdown cho tài khoản người dùng

### 📰 Tin tức & Cẩm nang

- Trang "Tin tức & cẩm nang" với danh sách bài viết
- Hướng dẫn cho bệnh nhân (4 bước chuẩn bị khám)
- Dễ mở rộng để thêm bài viết mới

---

## 🛠️ Công nghệ sử dụng

### Backend

- **Java 17** - Ngôn ngữ lập trình
- **Spring Boot 3.2.0** - Framework web chính
- **Spring Data JPA** - ORM (Object-Relational Mapping)
- **Spring Security** - Bảo mật, mã hóa
- **MySQL 8.0.33** - Cơ sở dữ liệu
- **Maven** - Quản lý dependency

### Frontend

- **HTML5** - Cấu trúc trang
- **CSS3** (Responsive, Flexbox, Grid) - Styling
- **JavaScript (ES6+)** - Logic tương tác (không sử dụng framework nặng)
- **Vanilla JS** - Quản lý dropdown, modal, animation

### Đặc điểm Frontend

- Không dùng React/Vue/Angular (giữ lightweight)
- CSS Grid/Flexbox cho layout responsive
- LocalStorage cho quản lý session khách hàng

---

## 📁 Cấu trúc thư mục

```
TT Care+ (java-web-project)
│
├── src/main/
│   ├── java/com/example/webapp/
│   │   ├── UngDungWeb.java                 # Main class (Spring Boot)
│   │   │
│   │   ├── controller/                      # Điều khiển HTTP requests
│   │   │   ├── TrangChuController.java     # Trang chủ
│   │   │   ├── XacThucController.java      # Đăng nhập/Đăng ký
│   │   │   ├── BenhNhanPortalController.java # Portal bệnh nhân
│   │   │   ├── BenhNhanController.java     # API bệnh nhân
│   │   │   ├── BacSiPortalController.java  # Portal bác sĩ
│   │   │   ├── HomeController.java         # Dashboard trang chủ
│   │   │   └── QuanTriPortalController.java # Dashboard quản trị
│   │   │
│   │   ├── entity/                          # Model dữ liệu (JPA)
│   │   │   ├── NguoiDung.java              # Người dùng (User)
│   │   │   ├── BenhNhan.java               # Bệnh nhân
│   │   │   ├── BacSi.java                  # Bác sĩ
│   │   │   ├── ChuyenKhoa.java             # Chuyên khoa
│   │   │   ├── PhongKham.java              # Phòng khám
│   │   │   ├── LichHen.java                # Lịch hẹn khám
│   │   │   ├── LichLamViec.java            # Lịch làm việc bác sĩ
│   │   │   ├── ThongBaoNhacLich.java       # Thông báo nhắc lịch
│   │   │   ├── TrangThaiLichHen.java       # Trạng thái lịch hẹn (enum)
│   │   │   └── TrangThaiLichLamViec.java   # Trạng thái lịch làm (enum)
│   │   │
│   │   ├── repository/                      # Data Access Layer
│   │   │   ├── NguoiDungRepository.java
│   │   │   ├── BenhNhanRepository.java
│   │   │   ├── BacSiRepository.java
│   │   │   ├── ChuyenKhoaRepository.java
│   │   │   ├── PhongKhamRepository.java
│   │   │   ├── LichHenRepository.java
│   │   │   ├── LichLamViecRepository.java
│   │   │   ├── ThongBaoNhacLichRepository.java
│   │   │   └── ...
│   │   │
│   │   ├── service/                         # Business Logic Layer (Service)
│   │   │   ├── NguoiDungService.java       # Quản lý người dùng
│   │   │   ├── BenhNhanService.java        # Quản lý bệnh nhân
│   │   │   ├── DatLichKhamService.java     # Logic đặt lịch khám
│   │   │   ├── EmailService.java           # Gửi email thông báo
│   │   │   └── ...
│   │   │
│   │   └── config/                          # Cấu hình Spring
│   │       ├── SecurityConfig.java
│   │       ├── CorsConfig.java
│   │       └── ...
│   │
│   └── resources/
│       ├── application.properties           # Cấu hình ứng dụng
│       └── static/                          # Frontend tĩnh
│           ├── css/
│           │   └── kieu.css                # CSS chính (1500+ lines)
│           ├── js/
│           │   ├── xacthuc.js              # Logic xác thực & dropdown
│           │   └── ungdung.js              # Logic chung ứng dụng
│           ├── images/                     # Ảnh banner, logo
│           ├── admin/                      # Trang quản trị
│           │   ├── trangquantri.html       # Dashboard chính
│           │   ├── bach.html               # Quản lý bác sĩ
│           │   ├── khoa.html               # Quản lý chuyên khoa
│           │   ├── lichhen.html            # Quản lý lịch hẹn
│           │   ├── phongkham.html          # Quản lý phòng khám
│           │   ├── khachhang.html          # Quản lý khách hàng
│           │   ├── baocao.html             # Báo cáo thống kê
│           │   ├── taikhoan.html           # Quản lý tài khoản
│           │   ├── dangnhap.html           # Đăng nhập/Đăng ký
│           │   └── dangky-nhanvien.html    # Đăng ký nhân viên
│           └── customer/                   # Trang khách hàng (bệnh nhân)
│               ├── trangchu.html           # Trang chủ công khai
│               ├── tintuc-camnang.html     # Tin tức & cẩm nang
│               ├── khoa.html               # Danh sách chuyên khoa
│               ├── bacsi.html              # Danh sách bác sĩ
│               ├── datlich.html            # Form đặt lịch
│               ├── hoso.html               # Hồ sơ cá nhân
│               └── lichcuatoi.html         # Lịch khám của tôi
│
├── pom.xml                                  # Maven dependencies
├── README.md                                # Tài liệu này
└── target/                                  # Build output

```

---

## 🚀 Hướng dẫn cài đặt

### Yêu cầu hệ thống

- **Java JDK 17 trở lên** - [Tải tại đây](https://www.oracle.com/java/technologies/downloads/#java17)
- **Maven 3.8.1 trở lên** - [Tải tại đây](https://maven.apache.org/download.cgi)
- **MySQL 8.0 trở lên** - [Tải tại đây](https://www.mysql.com/downloads/)
- **IDE** - [IntelliJ IDEA](https://www.jetbrains.com/idea/) hoặc [VS Code](https://code.visualstudio.com/)

### Bước 1: Chuẩn bị cơ sở dữ liệu

```sql
-- Tạo schema
CREATE DATABASE tt_care_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE tt_care_db;

-- Spring Boot JPA sẽ tự tạo bảng từ entities
-- Hoặc chạy script init nếu có file schema.sql
```

### Bước 2: Clone/Tải dự án

```bash
# Clone từ GitHub (nếu có)
git clone https://github.com/your-repo/tt-care.git
cd tt-care

# Hoặc extract file ZIP
```

### Bước 3: Cấu hình application.properties

Chỉnh sửa file `src/main/resources/application.properties`:

```properties
# Server
server.port=8080
server.servlet.context-path=/

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/tt_care_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# Encoding
spring.http.encoding.charset=UTF-8
spring.http.encoding.enabled=true
spring.http.encoding.force=true

# Mail (tùy chọn - nếu cần gửi email)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Bước 4: Build dự án

```bash
# Sử dụng Maven
mvn clean install

# Hoặc IDE tự động build
```

### Bước 5: Chạy ứng dụng

**Cách 1: Từ IDE**

- Mở dự án trong IntelliJ IDEA / VS Code
- Tìm lớp `UngDungWeb.java` (có annotation `@SpringBootApplication`)
- Click nút Run (hoặc Shift + F10)

**Cách 2: Từ Terminal**

```bash
mvn spring-boot:run
```

**Cách 3: Chạy JAR đã build**

```bash
java -jar target/java-web-project-1.0.0.jar
```

### Bước 6: Truy cập ứng dụng

Mở trình duyệt và truy cập:

- **Trang chủ**: [http://localhost:8080/customer/trangchu.html](http://localhost:8080/customer/trangchu.html)
- **Đăng nhập**: [http://localhost:8080/admin/dangnhap.html](http://localhost:8080/admin/dangnhap.html)
- **Dashboard quản trị**: [http://localhost:8080/admin/trangquantri.html](http://localhost:8080/admin/trangquantri.html)

---

## 📖 Hướng dẫn sử dụng

### 👤 Cho Khách hàng (Bệnh nhân)

1. **Truy cập trang chủ**: [http://localhost:8080/customer/trangchu.html](http://localhost:8080/customer/trangchu.html)

2. **Đăng ký tài khoản**:
   - Click nút "Đăng ký" trên header
   - Điền email/SĐT, mật khẩu, xác nhận mật khẩu
   - Click "Đăng ký"

3. **Đặt lịch khám**:
   - Đăng nhập vào tài khoản
   - Click "Đặt lịch khám"
   - Chọn chuyên khoa → bác sĩ → dịch vụ → ngày → khung giờ
   - Điền triệu chứng (tùy chọn)
   - Click "Xác nhận đặt lịch"

4. **Quản lý hồ sơ cá nhân**:
   - Vào "Thông tin cá nhân"
   - Cập nhật: Họ tên, Tuổi, Giới tính, CCCD, SĐT, Địa chỉ
   - Click "Lưu thông tin"

5. **Xem lịch khám**:
   - Vào "Lịch khám của tôi"
   - Xem trạng thái: Chờ xác nhận, Đã xác nhận, Đã khám

6. **Xem tin tức & cẩm nang**:
   - Click "Tin tức & cẩm nang" trên menu
   - Đọc bài viết, hướng dẫn

### 👨‍💼 Cho Quản trị viên / Nhân viên lễ tân

1. **Đăng nhập**:
   - Truy cập [http://localhost:8080/admin/dangnhap.html](http://localhost:8080/admin/dangnhap.html)
   - Đăng nhập bằng tài khoản admin (bạn cần tạo trong DB)

2. **Dashboard chính**:
   - Thống kê: Tổng khách hàng, Lịch khám hôm nay, Lịch khám tháng
   - Navigation sidebar: Truy cập các module quản lý

3. **Quản lý lịch khám** (Lịch hẹn):
   - Xem danh sách lịch khám chưa xác nhận
   - Click vào lịch để xem chi tiết
   - Xác nhận hoặc hủy lịch

4. **Quản lý bác sĩ**:
   - Thêm bác sĩ mới, chỉnh sửa thông tin
   - Phân công chuyên khoa

5. **Quản lý chuyên khoa**:
   - Thêm/Sửa/Xóa chuyên khoa
   - Quản lý danh sách dịch vụ

6. **Báo cáo thống kê**:
   - Xem số lượng lịch khám theo tháng
   - Thống kê theo chuyên khoa, bác sĩ

---

## 🗂️ Các thực thể dữ liệu

### Bảng chính

| Thực thể             | Mô tả                | Các trường quan trọng                                         |
| -------------------- | -------------------- | ------------------------------------------------------------- |
| **NguoiDung**        | Người dùng hệ thống  | id, username, email, password, role (CUSTOMER/DOCTOR/ADMIN)   |
| **BenhNhan**         | Bệnh nhân            | id, fullname, age, gender, cccd, phone, address, account (FK) |
| **BacSi**            | Bác sĩ               | id, hoTen, chucDanh, chuyenKhoa (FK), phongKham (FK)          |
| **ChuyenKhoa**       | Chuyên khoa khám     | id, ten, moTa                                                 |
| **PhongKham**        | Phòng khám           | id, ten, dia_chi                                              |
| **LichHen**          | Lịch hẹn khám benh   | id, benhNhan (FK), bacSi (FK), ngayKham, gioKham, trangThai   |
| **LichLamViec**      | Lịch làm việc bác sĩ | id, bacSi (FK), ngayLamViec, gioBatDau, gioKetThuc            |
| **ThongBaoNhacLich** | Thông báo nhắc lịch  | id, lichHen (FK), noiDung, thoiDiemNhac                       |

### Enum (Trạng thái)

- **TrangThaiLichHen**: CHO_XAC_NHAN, DA_XAC_NHAN, DA_HUY, DANG_KHAM, DA_KHAM, BO_LO
- **TrangThaiLichLamViec**: DANG_LAM, NGHI, HUY

---

## 🔌 API chính

### Authentication

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123",
  "role": "customer"
}
```

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123"
}
```

### Bệnh nhân

```http
GET /api/patient/specialties          # Danh sách chuyên khoa
GET /api/patient/doctors               # Danh sách bác sĩ
GET /api/patient/doctors?specialtyId=1 # Bác sĩ theo chuyên khoa
GET /api/patient/doctors/{id}/available-slots?date=2024-01-15

POST /api/patient/appointments         # Đặt lịch khám
GET /api/patient/{patientId}/appointments # Xem lịch khám

POST /api/patients/upsert-by-account   # Cập nhật hồ sơ bệnh nhân
GET /api/patients/by-account           # Lấy hồ sơ bệnh nhân
```

### Quản trị

```http
GET /api/admin/appointments            # Danh sách lịch khám
PUT /api/admin/appointments/{id}       # Cập nhật lịch khám
DELETE /api/admin/appointments/{id}    # Hủy lịch khám

GET /api/admin/doctors                 # Danh sách bác sĩ
POST /api/admin/doctors                # Thêm bác sĩ
PUT /api/admin/doctors/{id}            # Cập nhật bác sĩ
DELETE /api/admin/doctors/{id}         # Xóa bác sĩ
```

---

## 💻 Hướng dẫn phát triển

### Thêm tính năng mới

#### Bước 1: Tạo Entity (Model dữ liệu)

```java
// src/main/java/com/example/webapp/entity/TinhNang.java
@Entity
@Table(name = "tinh_nang")
public class TinhNang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten")
    private String ten;

    // Getters/Setters
}
```

#### Bước 2: Tạo Repository

```java
// src/main/java/com/example/webapp/repository/TinhNangRepository.java
@Repository
public interface TinhNangRepository extends JpaRepository<TinhNang, Integer> {
    TinhNang findByTen(String ten);
}
```

#### Bước 3: Tạo Service

```java
// src/main/java/com/example/webapp/service/TinhNangService.java
@Service
public class TinhNangService {
    @Autowired
    private TinhNangRepository repo;

    public List<TinhNang> getAll() {
        return repo.findAll();
    }
}
```

#### Bước 4: Tạo Controller

```java
// src/main/java/com/example/webapp/controller/TinhNangController.java
@RestController
@RequestMapping("/api/tinh-nang")
public class TinhNangController {
    @Autowired
    private TinhNangService service;

    @GetMapping
    public ResponseEntity<List<TinhNang>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
}
```

#### Bước 5: Tạo Frontend (HTML/CSS/JS)

```html
<!-- src/main/resources/static/customer/tinh-nang.html -->
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>Tính năng</title>
    <link rel="stylesheet" href="/css/kieu.css" />
  </head>
  <body>
    <header class="customer-header">
      <!-- Header content -->
    </header>

    <main class="customer-content">
      <h1>Tính năng</h1>
      <div id="list"></div>
    </main>

    <script src="/js/xacthuc.js"></script>
    <script>
      fetch("/api/tinh-nang")
        .then((r) => r.json())
        .then((data) => {
          // Render data
        });
    </script>
  </body>
</html>
```

### Quy ước mã

- **Naming**: Sử dụng tiếng Việt cho biến, hàm (dễ hiểu trong team VN)
- **Backend**: Theo chuẩn Clean Code (MVC layer)
- **Frontend**: Vanilla JS (không framework nặng)
- **CSS**: Modular, sử dụng CSS variables

### Cơ sở dữ liệu

- Hibernate sẽ tự tạo bảng từ Entities
- Có thể viết `schema.sql` nếu cần khởi tạo dữ liệu
- Sử dụng migration tool là tùy chọn

---

## 🐛 Xử lý lỗi phổ biến

| Lỗi                    | Nguyên nhân                           | Giải pháp                       |
| ---------------------- | ------------------------------------- | ------------------------------- |
| `Connection refused`   | MySQL chưa chạy                       | Khởi động MySQL service         |
| `Access Denied`        | Sai username/password DB              | Kiểm tra application.properties |
| `404 Not Found`        | URL sai hoặc controller không tồn tại | Kiểm tra @RequestMapping        |
| `CORS error`           | Frontend khác domain                  | Bật CorsConfig trong Spring     |
| `NullPointerException` | Đối tượng null                        | Thêm null check hoặc @Nullable  |

---

## 📚 Tài liệu thêm

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [MDN Web Docs (HTML/CSS/JS)](https://developer.mozilla.org/)

---

## 👨‍💻 Đóng góp

Để đóng góp vào dự án:

1. Fork dự án
2. Tạo branch feature (`git checkout -b feature/AmazingFeature`)
3. Commit thay đổi (`git commit -m 'Add some AmazingFeature'`)
4. Push lên branch (`git push origin feature/AmazingFeature`)
5. Mở Pull Request

---

## 📄 License

Dự án này sử dụng license **MIT**. Xem file [LICENSE](LICENSE) để biết chi tiết.

---

## 📞 Liên hệ hỗ trợ

- **Email**: support@ttcare.vn
- **Hotline**: 1900 1234
- **Website**: https://www.ttcare.vn

---

**Cập nhật lần cuối**: 17 tháng 4, 2026
**Phiên bản**: 1.0.0

- Trang chu landing: `http://localhost:8080/trangchu.html`

- Dang nhap / dang ky: `http://localhost:8080/admin/dangnhap.html`
- Sau khi dang nhap voi vai tro customer: `http://localhost:8080/customer/trangchu.html`
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
