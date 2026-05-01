package testcases;

import common.Constant;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import pageobjects.LoginPage;

import java.time.Duration;

/**
 * Module: Đăng nhập
 * Tester: Hồ Sỹ Hải Lâm
 * Pre-condition: Hệ thống hoạt động bình thường, có tài khoản Admin và Nhân viên,
 *                người dùng đang ở màn hình đăng nhập
 *
 * Tổng: 13 test case (LGN_01 → LGN_13)
 * Pass: 7 | Fail (known bugs): 6
 */
public class LoginTest extends BaseTest {

    // ----------------------------------------------------------------
    // LGN_01: Đăng nhập thành công với tài khoản Admin hợp lệ
    // Kết quả mong đợi: Đăng nhập thành công, chuyển vào trang chính
    // ----------------------------------------------------------------
    @Test
    public void LGN_01_LoginSuccessWithAdminAccount() {
        LoginPage loginPage = new LoginPage();
        loginPage.login(Constant.ADMIN_USER, Constant.ADMIN_PASS);

        Assert.assertTrue(
                Constant.WEBDRIVER.getCurrentUrl().contains("dashboard")
                || Constant.WEBDRIVER.getCurrentUrl().contains("products")
                || Constant.WEBDRIVER.getCurrentUrl().equals(Constant.URL + "/"),
                "LGN_01 FAIL: Admin đăng nhập không thành công"
        );
    }

    // ----------------------------------------------------------------
    // LGN_02: Đăng nhập thành công với tài khoản Nhân viên hợp lệ
    // Kết quả mong đợi: Đăng nhập thành công, chuyển vào trang chính
    // ----------------------------------------------------------------
    @Test
    public void LGN_02_LoginSuccessWithStaffAccount() {
        LoginPage loginPage = new LoginPage();
        loginPage.login(Constant.STAFF_USER, Constant.STAFF_PASS);

        Assert.assertTrue(
                Constant.WEBDRIVER.getCurrentUrl().contains("dashboard")
                || Constant.WEBDRIVER.getCurrentUrl().contains("products")
                || Constant.WEBDRIVER.getCurrentUrl().equals(Constant.URL + "/"),
                "LGN_02 FAIL: Nhân viên đăng nhập không thành công"
        );
    }

    // ----------------------------------------------------------------
    // LGN_03: Bỏ trống username
    // Kết quả mong đợi: Hiển thị "Vui lòng nhập đầy đủ thông tin"
    // Bug: Thực tế hiển thị "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu" → Fail
    // ----------------------------------------------------------------
    @Test
    public void LGN_03_LoginWithEmptyUsername() {
        LoginPage loginPage = new LoginPage();
        loginPage.submitLogin("", Constant.ADMIN_PASS);

        String msg = loginPage.getErrorMessage();
        Assert.assertTrue(
                msg.contains("Vui lòng nhập đầy đủ thông tin"),
                "LGN_03 FAIL: Thông báo lỗi không đúng. Thực tế: " + msg
        );
    }

    // ----------------------------------------------------------------
    // LGN_04: Bỏ trống password
    // Kết quả mong đợi: Hiển thị "Vui lòng nhập đầy đủ thông tin"
    // Bug: Thực tế hiển thị "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu" → Fail
    // ----------------------------------------------------------------
    @Test
    public void LGN_04_LoginWithEmptyPassword() {
        LoginPage loginPage = new LoginPage();
        loginPage.submitLogin(Constant.ADMIN_USER, "");

        String msg = loginPage.getErrorMessage();
        Assert.assertTrue(
                msg.contains("Vui lòng nhập đầy đủ thông tin"),
                "LGN_04 FAIL: Thông báo lỗi không đúng. Thực tế: " + msg
        );
    }

    // ----------------------------------------------------------------
    // LGN_05: Bỏ trống cả username và password
    // Kết quả mong đợi: Hiển thị "Vui lòng nhập đầy đủ thông tin"
    // Bug: Thực tế hiển thị "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu" → Fail
    // ----------------------------------------------------------------
    @Test
    public void LGN_05_LoginWithEmptyUsernameAndPassword() {
        LoginPage loginPage = new LoginPage();
        loginPage.submitLogin("", "");

        String msg = loginPage.getErrorMessage();
        Assert.assertTrue(
                msg.contains("Vui lòng nhập đầy đủ thông tin"),
                "LGN_05 FAIL: Thông báo lỗi không đúng. Thực tế: " + msg
        );
    }

    // ----------------------------------------------------------------
    // LGN_06: Sai username
    // Kết quả mong đợi: Hiển thị "Sai tên đăng nhập hoặc mật khẩu"
    // ----------------------------------------------------------------
    @Test
    public void LGN_06_LoginWithWrongUsername() {
        LoginPage loginPage = new LoginPage();
        loginPage.submitLogin("wrong_user_xyz", Constant.ADMIN_PASS);

        String msg = loginPage.getErrorMessage();
        Assert.assertTrue(
                msg.contains("Sai tên đăng nhập hoặc mật khẩu"),
                "LGN_06 FAIL: Thông báo lỗi không đúng. Thực tế: " + msg
        );
    }

    // ----------------------------------------------------------------
    // LGN_07: Sai password
    // Kết quả mong đợi: Hiển thị "Sai tên đăng nhập hoặc mật khẩu"
    // ----------------------------------------------------------------
    @Test
    public void LGN_07_LoginWithWrongPassword() {
        LoginPage loginPage = new LoginPage();
        loginPage.submitLogin(Constant.ADMIN_USER, "wrongpass999");

        String msg = loginPage.getErrorMessage();
        Assert.assertTrue(
                msg.contains("Sai tên đăng nhập hoặc mật khẩu"),
                "LGN_07 FAIL: Thông báo lỗi không đúng. Thực tế: " + msg
        );
    }

    // ----------------------------------------------------------------
    // LGN_08: Đăng nhập sai liên tiếp 5 lần
    // Kết quả mong đợi: Tài khoản bị khóa tạm thời hoặc hiển thị cảnh báo phù hợp
    // Bug: Không có thông báo khóa tài khoản → Fail
    // ----------------------------------------------------------------
    @Test
    public void LGN_08_LoginFailFiveTimesConsecutively() {
        LoginPage loginPage = new LoginPage();

        // Sai 5 lần liên tiếp
        for (int i = 1; i <= 5; i++) {
            Constant.WEBDRIVER.get(Constant.URL);
            loginPage = new LoginPage();
            loginPage.submitLogin(Constant.ADMIN_USER, "wrongpass_" + i);

            // Chờ thông báo lỗi xuất hiện trước lần tiếp theo
            try {
                new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(5))
                        .until(ExpectedConditions.visibilityOfElementLocated(
                                By.xpath("//*[contains(text(),'Sai') or contains(text(),'khóa')]")));
            } catch (Exception ignored) {}
        }

        String msg = loginPage.getErrorMessage();
        Assert.assertTrue(
                msg.contains("khóa") || msg.contains("tạm thời") || msg.contains("locked"),
                "LGN_08 FAIL: Không có thông báo khóa tài khoản sau 5 lần sai. Thực tế: " + msg
        );
    }

    // ----------------------------------------------------------------
    // LGN_09: Đăng nhập khi tài khoản đã bị khóa
    // Kết quả mong đợi: Hiển thị "Tài khoản đã bị khóa tạm thời, vui lòng liên hệ quản trị"
    // Bug: Không có chức năng khóa tài khoản → Fail
    // ----------------------------------------------------------------
    @Test
    public void LGN_09_LoginWithLockedAccount() {
        // Pre-condition: tài khoản này đã bị khóa (sau LGN_08 hoặc khóa thủ công)
        LoginPage loginPage = new LoginPage();
        loginPage.submitLogin(Constant.ADMIN_USER, Constant.ADMIN_PASS);

        String msg = loginPage.getErrorMessage();
        Assert.assertTrue(
                msg.contains("khóa") || msg.contains("tạm thời") || msg.contains("liên hệ quản trị"),
                "LGN_09 FAIL: Không hiển thị thông báo tài khoản bị khóa. Thực tế: " + msg
        );
    }

    // ----------------------------------------------------------------
    // LGN_10: Username có khoảng trắng đầu/cuối
    // Kết quả mong đợi: Hệ thống tự trim hoặc không chấp nhận
    // ----------------------------------------------------------------
    @Test
    public void LGN_10_LoginWithUsernameHavingLeadingTrailingSpaces() {
        LoginPage loginPage = new LoginPage();

        // Thêm khoảng trắng đầu và cuối username hợp lệ
        loginPage.login(" " + Constant.ADMIN_USER + " ", Constant.ADMIN_PASS);

        // Hệ thống tự trim → đăng nhập thành công, hoặc báo lỗi định dạng
        boolean loggedIn = Constant.WEBDRIVER.getCurrentUrl().contains("dashboard")
                || Constant.WEBDRIVER.getCurrentUrl().contains("products")
                || Constant.WEBDRIVER.getCurrentUrl().equals(Constant.URL + "/");

        Assert.assertTrue(loggedIn,
                "LGN_10 FAIL: Hệ thống không xử lý đúng username có khoảng trắng đầu/cuối"
        );
    }

    // ----------------------------------------------------------------
    // LGN_11: Password không đúng format rule (dưới 8 ký tự)
    // Kết quả mong đợi: Hệ thống không chấp nhận và hiển thị thông báo lỗi
    // ----------------------------------------------------------------
    @Test
    public void LGN_11_LoginWithPasswordBelowMinLength() {
        LoginPage loginPage = new LoginPage();
        loginPage.submitLogin(Constant.ADMIN_USER, "abc"); // dưới 8 ký tự

        String msg = loginPage.getErrorMessage();
        Assert.assertFalse(
                msg.isEmpty(),
                "LGN_11 FAIL: Không hiển thị thông báo lỗi khi password dưới 8 ký tự"
        );
    }

    // ----------------------------------------------------------------
    // LGN_12: Chức năng hiển thị mật khẩu (icon toggle)
    // Kết quả mong đợi: Password được hiển thị đúng khi bấm icon
    // Bug: Không có icon hiện mật khẩu → Fail
    // ----------------------------------------------------------------
    @Test
    public void LGN_12_ShowPasswordIconFunctionality() {
        LoginPage loginPage = new LoginPage();

        // Điền password trước
        new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='password']")))
                .sendKeys("testpassword");

        Assert.assertTrue(
                loginPage.isShowPasswordIconPresent(),
                "LGN_12 FAIL: Không có icon hiện mật khẩu trên trang đăng nhập"
        );

        loginPage.clickShowPasswordIcon();

        Assert.assertTrue(
                loginPage.isPasswordVisible(),
                "LGN_12 FAIL: Mật khẩu không được hiển thị sau khi bấm icon"
        );
    }

    // ----------------------------------------------------------------
    // LGN_13: Tạo session sau đăng nhập thành công
    // Kết quả mong đợi: Truy cập được các chức năng sau login
    // ----------------------------------------------------------------
    @Test
    public void LGN_13_SessionCreatedAfterLogin() {
        LoginPage loginPage = new LoginPage();
        loginPage.login(Constant.ADMIN_USER, Constant.ADMIN_PASS);

        // Thử truy cập các trang chức năng
        String[] protectedPages = {"/products", "/inventory", "/invoices"};

        for (String page : protectedPages) {
            Constant.WEBDRIVER.get(Constant.URL + page);

            boolean accessible = !Constant.WEBDRIVER.getCurrentUrl().contains("login")
                    && !Constant.WEBDRIVER.getCurrentUrl().equals(Constant.URL + "/");

            Assert.assertTrue(accessible,
                    "LGN_13 FAIL: Không truy cập được trang " + page + " sau khi đăng nhập"
            );
        }
    }
}
