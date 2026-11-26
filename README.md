# qlithuoc-test-auto-final

Bộ kiểm thử tự động (automated test suite) cho hệ thống **Quản lý bán thuốc** tại:
🌐 [https://qlibanthuoc-tester.vercel.app](https://qlibanthuoc-tester.vercel.app)

Dự án môn **MIS3021** – Nhóm 12 (49K14.1)

---

## Công nghệ sử dụng

| Thành phần       | Phiên bản |
|------------------|-----------|
| Java             | 25        |
| Selenium WebDriver | 4.21.0  |
| TestNG           | 7.10.2    |
| WebDriverManager | 5.8.0     |
| Apache Commons IO | 2.16.1   |
| Build tool       | Maven     |
| Trình duyệt      | Chrome (headful) |

---

## Cấu trúc thư mục

```
qlithuoc-test-auto-final/
│
├── pom.xml                          # Cấu hình Maven, khai báo dependency
│
├── src/
│   ├── main/java/
│   │   ├── common/
│   │   │   └── Constant.java        # URL, tài khoản dùng chung toàn bộ test
│   │   └── pageobjects/
│   │       ├── GeneralPage.java     # Các thao tác/tiện ích dùng chung
│   │       ├── LoginPage.java       # Trang đăng nhập
│   │       ├── ProductPage.java     # Trang quản lý sản phẩm
│   │       ├── InvoicePage.java     # Trang hóa đơn / bán hàng
│   │       ├── ImportPage.java      # Trang nhập hàng
│   │       └── InventoryPage.java   # Trang tồn kho
│   │
│   └── test/java/
│       ├── Base/
│       │   └── BaseTest.java        # Base class cho LoginTest (khởi tạo driver, helper methods)
│       └── testcases/
│           ├── BaseTest.java        # Base class cho các module còn lại (dùng Constant.WEBDRIVER)
│           ├── LoginTest.java       # Module: Đăng nhập (LGN)
│           ├── LogoutTest.java      # Module: Đăng xuất (LGO)
│           ├── AuthTest.java        # Module: Phân quyền (AUTH)
│           ├── ProductTest.java     # Module: Sản phẩm – Thêm & Cập nhật (PRD)
│           ├── InvoiceTest.java     # Module: Hóa đơn – Tạo, lọc, hủy (INVCE / BILL / CAN)
│           ├── ImportTest.java      # Module: Nhập hàng (IMP)
│           └── InventoryTest.java   # Module: Tồn kho (INV)
│
└── evidence/                        # Ảnh chụp màn hình kết quả test (126 file .png)
```

---

## Tài khoản kiểm thử

| Vai trò   | Username   | Password |
|-----------|------------|----------|
| Admin     | `admin`    | `123456` |
| Nhân viên | `nhanvien` | `123456` |

> Cấu hình tập trung trong `Constant.java`. Chỉ cần sửa ở đó khi thông tin thay đổi.

---

## Các module & test case

### 🔐 Đăng nhập – `LoginTest` (LGN_01 → LGN_13)

| ID      | Mô tả |
|---------|-------|
| LGN_01  | Đăng nhập thành công – tài khoản Admin |
| LGN_02  | Đăng nhập thành công – tài khoản Nhân viên |
| LGN_03  | Bỏ trống username |
| LGN_04  | Bỏ trống password |
| LGN_05  | Bỏ trống cả hai trường |
| LGN_06  | Sai username |
| LGN_07  | Sai password |
| LGN_08  | Sai liên tiếp 5 lần → tài khoản bị khóa tạm thời |
| LGN_09  | Đăng nhập khi tài khoản đã bị khóa |
| LGN_10  | Username có khoảng trắng đầu/cuối |
| LGN_11  | Password không đúng format (quá ngắn) |
| LGN_12  | Chức năng hiển thị/ẩn mật khẩu |
| LGN_13  | Session được tạo sau đăng nhập thành công |

---

### 🚪 Đăng xuất – `LogoutTest` (LGO_01 → LGO_05)

Kiểm tra luồng đăng xuất, hủy đăng xuất và trạng thái session sau khi thoát.

---

### 🛡️ Phân quyền – `AuthTest` (AUTH_01 → AUTH_08)

| ID       | Mô tả |
|----------|-------|
| AUTH_01  | Nhân viên không thấy nút "Thêm sản phẩm" |
| AUTH_02  | Nhân viên không thấy nút "Sửa" sản phẩm |
| AUTH_03  | Nhân viên không tạo được phiếu nhập hàng |
| AUTH_04  | Nhân viên không thấy nút "Hủy hóa đơn" |
| AUTH_05  | Admin truy cập được trang Tồn kho |
| AUTH_06  | Nhân viên truy cập được trang Tồn kho |
| AUTH_07  | Admin xem và lọc danh sách hóa đơn |
| AUTH_08  | Nhân viên tạo hóa đơn và thanh toán |

---

### 📦 Sản phẩm – `ProductTest` (PRD_ADD_01→10, PRD_UPD_01→09)

- **PRD_ADD** (10 TC): Thêm sản phẩm – hợp lệ, thiếu trường, trùng tên, v.v.
- **PRD_UPD** (9 TC): Cập nhật sản phẩm – sửa thành công, giá trị không hợp lệ, v.v.

---

### 🧾 Hóa đơn – `InvoiceTest`

| Nhóm    | Mô tả |
|---------|-------|
| INVCE   | Tạo hóa đơn (1 sản phẩm, nhiều sản phẩm, thiếu thông tin, …) |
| BILL_FIL | Lọc hóa đơn theo ngày, trạng thái, mã hóa đơn, nhiều điều kiện |
| CAN / BILL_CAN | Hủy hóa đơn – thành công, từ chối, không cho hủy lại, không cho hủy hoàn tất |

---

### 📥 Nhập hàng – `ImportTest` (IMP_*)

Tạo phiếu nhập hoàn tất, kiểm tra validation trường, kiểm tra ngày hết hạn, v.v.

---

### 🏪 Tồn kho – `InventoryTest` (INV_*)

Xem danh sách tồn kho, tìm kiếm theo tên sản phẩm, kiểm tra số lượng sau nhập/xuất.

---

## Chạy test

**Yêu cầu:** Java 17+, Maven, Chrome browser đã cài đặt.

```bash
# Chạy toàn bộ test suite
mvn test

# Chạy một class cụ thể
mvn test -Dtest=LoginTest

# Chạy một test case cụ thể
mvn test -Dtest=LoginTest#LGN_01_loginSuccessfullyWithAdminAccount
```

> WebDriverManager tự động tải ChromeDriver phù hợp — không cần cấu hình thêm.

---

## Bằng chứng kiểm thử (Evidence)

Thư mục `evidence/` chứa **126 ảnh chụp màn hình** kết quả chạy test, đặt tên theo quy tắc:

```
<MODULE_ID>_<MoTaNgan>.png
```

Ví dụ: `AUTH_05_AdminCanAccessInventory.png`, `BILL_CAN_01_HuyThanhCong.png`

---

## Báo cáo

File báo cáo đầy đủ: `Project_final report_MIS3021_49K14.1_Group12.docx`

---

## Nhóm thực hiện

**Nhóm 12 – MIS3021 – 49K14.1**

| Thành viên           | Vai trò      | Phụ trách module |
|----------------------|--------------|-----------------|
| Phan Lê Nghị         | Scrum Master | Phân quyền (AUTH) |
| Nguyễn Thị My Sa     | Team Member  | Cập nhật sản phẩm (PRD_UPD) |
| Phan Thị Thanh Nhàn  | Team Member  | Hóa đơn – Tạo, Thanh toán, Xem, Hủy, Lọc (INVCE / PAY / CAN / BILL_FIL) |
| Nguyễn Thị Ánh Tuyết | Team Member  | Đăng nhập (LGN), Đăng xuất (LGO) |
| Hồ Sỹ Hải Lâm        | Team Member  | Thêm sản phẩm (PRD_ADD), Nhập hàng (IMP), Tồn kho (INV) |
