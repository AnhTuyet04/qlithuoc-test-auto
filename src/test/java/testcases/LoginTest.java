package testcases;

import Base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    // =====================================================
    // T sửa tài khoản test thật ở đây
    // =====================================================

    private final String ADMIN_USERNAME = "admin";
    private final String ADMIN_PASSWORD = "123456";

    private final String STAFF_USERNAME = "nhanvien";
    private final String STAFF_PASSWORD = "123456";

    private final String LOCKED_USERNAME = "taikhoanbikhoa";
    private final String LOCKED_PASSWORD = "123456";

    // =====================================================
    // Locator dùng chung cho trang đăng nhập
    // =====================================================

    private WebElement usernameInput() {
        return findFirstVisible(
                By.name("username"),
                By.name("email"),
                By.id("username"),
                By.id("email"),
                By.cssSelector("input[type='text']"),
                By.cssSelector("input[type='email']"),
                By.cssSelector("input[placeholder*='Tên']"),
                By.cssSelector("input[placeholder*='tài khoản']"),
                By.cssSelector("input[placeholder*='username']"),
                By.cssSelector("input[placeholder*='Email']")
        );
    }

    private WebElement passwordInput() {
        return findFirstVisible(
                By.name("password"),
                By.id("password"),
                By.cssSelector("input[type='password']"),
                By.cssSelector("input[placeholder*='Mật khẩu']"),
                By.cssSelector("input[placeholder*='password']")
        );
    }

    private WebElement loginButton() {
        return findFirstClickable(
                By.cssSelector("button[type='submit']"),
                By.xpath("//button[contains(.,'Đăng nhập')]"),
                By.xpath("//button[contains(.,'Login')]"),
                By.xpath("//button")
        );
    }

    private void login(String username, String password) {
        type(usernameInput(), username);
        type(passwordInput(), password);
        loginButton().click();
    }

    private boolean isLoginSuccess() {
        try {
            return wait.until(d -> {
                String text = d.findElement(By.tagName("body")).getText().toLowerCase();
                String url = d.getCurrentUrl().toLowerCase();

                return text.contains("đăng xuất")
                        || text.contains("dashboard")
                        || text.contains("trang chủ")
                        || text.contains("sản phẩm")
                        || text.contains("thuốc")
                        || text.contains("hóa đơn")
                        || text.contains("nhập hàng")
                        || url.contains("dashboard")
                        || url.contains("home")
                        || url.contains("admin");
            });
        } catch (Exception e) {
            return false;
        }
    }

    private boolean pageContainsAny(String... keywords) {
        try {
            return wait.until(d -> {
                String bodyText = d.findElement(By.tagName("body")).getText().toLowerCase();

                for (String keyword : keywords) {
                    if (bodyText.contains(keyword.toLowerCase())) {
                        return true;
                    }
                }

                return false;
            });
        } catch (Exception e) {
            return false;
        }
    }

    // =====================================================
    // LGN_01 - Đăng nhập thành công với tài khoản Admin hợp lệ
    // =====================================================

    @Test
    public void LGN_01_loginSuccessfullyWithAdminAccount() {
        System.out.println("Admin username đang dùng: " + ADMIN_USERNAME);
        System.out.println("Admin password length: " + ADMIN_PASSWORD);

        login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Assert.assertTrue(
                isLoginSuccess(),
                "LGN_01 FAIL: Admin đăng nhập không thành công."
        );
    }

    // =====================================================
    // LGN_02 - Đăng nhập thành công với tài khoản Nhân viên hợp lệ
    // =====================================================

    @Test
    public void LGN_02_loginSuccessfullyWithStaffAccount() {
        login(STAFF_USERNAME, STAFF_PASSWORD);

        Assert.assertTrue(
                isLoginSuccess(),
                "LGN_02 FAIL: Nhân viên đăng nhập không thành công."
        );
    }

    // =====================================================
    // LGN_03 - Bỏ trống username
    // =====================================================

    @Test
    public void LGN_03_loginWithoutUsername() {
        type(passwordInput(), ADMIN_PASSWORD);
        loginButton().click();

        boolean actualResult = pageContainsAny(
                "Vui lòng nhập đầy đủ thông tin"
        );

        Assert.assertEquals(
                actualResult,
                true,
                "LGN_03 FAIL: Hiển thị không đúng thông báo khi bỏ trống cả username."
        );
    }

    // =====================================================
    // LGN_04 - Bỏ trống password
    // =====================================================

    @Test
    public void LGN_04_loginWithoutPassword() {
        type(usernameInput(), ADMIN_USERNAME);
        loginButton().click();

        boolean actualResult = pageContainsAny(
                "Vui lòng nhập đầy đủ thông tin"
        );

        Assert.assertEquals(
                actualResult,
                true,
                "LGN_04 FAIL: Hiển thị không đúng thông báo khi bỏ trống password."
        );
    }

    // =====================================================
    // LGN_05 - Bỏ trống cả 2 trường
    // =====================================================

    @Test
    public void LGN_05_loginWithoutUsernameAndPassword() {
        loginButton().click();

        boolean actualResult = pageContainsAny(
                "Vui lòng nhập đầy đủ thông tin"
        );

        Assert.assertEquals(
                actualResult,
                true,
                "LGN_05 FAIL: Hiển thị không đúng thông báo khi bỏ trống cả username và password."
        );
    }

    // =====================================================
    // LGN_06 - Sai username
    // =====================================================

    @Test
    public void LGN_06_loginWithWrongUsername() {
        login("sai_username", ADMIN_PASSWORD);

        boolean actualResult = pageContainsAny(
                "Sai tên đăng nhập hoặc mật khẩu"
        );

        Assert.assertEquals(
                actualResult,
                true,
                "LGN_06 FAIL: Hiển thị không đúng thông báo khi nhập sai username."
        );
    }

    // =====================================================
    // LGN_07 - Sai password
    // =====================================================

    @Test
    public void LGN_07_loginWithWrongPassword() {
        login(ADMIN_USERNAME, "sai_password");

        boolean actualResult = pageContainsAny(
                "Sai tên đăng nhập hoặc mật khẩu"
        );

        Assert.assertEquals(
                actualResult,
                true,
                "LGN_07 FAIL: Hiển thị không đúng thông báo khi nhập sai password."
        );
    }

    // =====================================================
    // LGN_08 - Sai liên tiếp 5 lần
    // =====================================================

    @Test
    public void LGN_08_loginWrongPasswordFiveTimes() {
        for (int i = 1; i <= 5; i++) {
            type(usernameInput(), ADMIN_USERNAME);
            type(passwordInput(), "sai_password_" + i);
            loginButton().click();
        }

        Assert.assertTrue(
                pageContainsAny(
                        "tài khoản bị khóa tạm thời"
                ),
                "LGN_08 FAIL: Không khóa tài khoản sau khi đăng nhập sai 5 lần."
        );
    }

    // =====================================================
    // LGN_09 - Đăng nhập khi tài khoản đã bị khóa
    // =====================================================

    @Test
    public void LGN_09_loginWithLockedAccount() {
        login(LOCKED_USERNAME, LOCKED_PASSWORD);

        Assert.assertTrue(
                pageContainsAny(
                        "tài khoản đã bị khóa tạm thời",
                        "tài khoản bị khóa",
                        "vui lòng liên hệ quản trị",
                        "bị khóa",
                        "locked"
                ),
                "LGN_09 FAIL: Không hiển thị thông báo tài khoản bị khóa."
        );
    }

    // =====================================================
    // LGN_10 - Username có khoảng trắng đầu/cuối
    // =====================================================

    @Test
    public void LGN_10_loginWithUsernameHasLeadingAndTrailingSpaces() {
        login("   " + ADMIN_USERNAME + "   ", ADMIN_PASSWORD);

        boolean successAfterTrim = isLoginSuccess();

        boolean showInvalidFormatMessage = pageContainsAny(
                "sai định dạng",
                "không hợp lệ",
                "không đúng",
                "vui lòng",
                "invalid"
        );

        Assert.assertTrue(
                successAfterTrim || showInvalidFormatMessage,
                "LGN_10 FAIL: Hệ thống không xử lý username có khoảng trắng đầu/cuối đúng rule."
        );
    }

    // =====================================================
    // LGN_11 - Password không đúng format rule
    // =====================================================

    @Test
    public void LGN_11_loginWithInvalidPasswordFormat() {
        login(ADMIN_USERNAME, "12");

        Assert.assertTrue(
                pageContainsAny(
                        "password",
                        "mật khẩu",
                        "tối thiểu",
                        "ít nhất",
                        "8 ký tự",
                        "không hợp lệ",
                        "invalid",
                        "rule"
                ),
                "LGN_11 FAIL: Không báo lỗi khi password không đúng format rule."
        );
    }

    // =====================================================
    // LGN_12 - Chức năng hiển thị mật khẩu
    // =====================================================

    @Test
    public void LGN_12_showPasswordFunction() {
        WebElement password = passwordInput();
        type(password, ADMIN_PASSWORD);

        WebElement showPasswordButton = findFirstClickable(
                By.xpath("//input[@type='password']/following::button[1]"),
                By.xpath("//*[contains(@class,'eye')]"),
                By.xpath("//*[contains(@aria-label,'password')]"),
                By.xpath("//*[contains(@aria-label,'show')]")
        );

        showPasswordButton.click();

        String inputType = password.getAttribute("type");

        Assert.assertTrue(
                inputType.equalsIgnoreCase("text"),
                "LGN_12 FAIL: Sau khi bấm icon hiển thị mật khẩu, password chưa được hiển thị."
        );
    }

    // =====================================================
    // LGN_13 - Tạo session sau đăng nhập thành công
    // =====================================================


    @Test
    public void LGN_13_sessionCreatedAfterLoginSuccessfully() {
        login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Assert.assertTrue(
                isLoginSuccess(),
                "LGN_13 FAIL: Đăng nhập không thành công."
        );

        WebElement productMenu = findFirstClickable(
                By.xpath("//a[contains(.,'Sản phẩm')]"),
                By.xpath("//button[contains(.,'Sản phẩm')]"),
                By.xpath("//*[normalize-space()='Sản phẩm']"),
                By.xpath("//*[contains(text(),'Sản phẩm')]")
        );

        productMenu.click();

        boolean canAccessProductModule = wait.until(d -> {
            String bodyText = d.findElement(By.tagName("body")).getText().toLowerCase();
            return bodyText.contains("quản lý sản phẩm")
                    || bodyText.contains("thêm sản phẩm")
                    || bodyText.contains("danh sách sản phẩm")
                    || bodyText.contains("sản phẩm");
        });

        Assert.assertTrue(
                canAccessProductModule,
                "LGN_13 FAIL: Sau khi đăng nhập, không truy cập được module Sản phẩm."
        );
    }
}