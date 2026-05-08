package pageobjects;

import common.Constant;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    private WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(15));

    private By txtUsername = By.xpath("//input[@type='text']");
    private By txtPassword = By.xpath("//input[@type='password']");
    private By btnLogin   = By.xpath("//button[contains(.,'Đăng nhập') or @type='submit']");
    private By errorMsg   = By.xpath("//*[contains(@class,'error') or contains(@class,'alert') or contains(@class,'message') or contains(@class,'toast')]");

    // ----------------------------------------------------------------
    // Đăng nhập và chờ chuyển trang thành công (dùng cho test Pass)
    // ----------------------------------------------------------------
    public void login(String username, String password) {
        fillForm(username, password);
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/products"),
                ExpectedConditions.urlContains("/dashboard"),
                ExpectedConditions.urlToBe(Constant.URL + "/")
        ));
    }

    // ----------------------------------------------------------------
    // Chỉ điền form và bấm nút — KHÔNG chờ redirect
    // Dùng cho test case kiểm tra thông báo lỗi
    // ----------------------------------------------------------------
    public void submitLogin(String username, String password) {
        fillForm(username, password);
    }

    // ----------------------------------------------------------------
    // Lấy text thông báo lỗi hiển thị trên trang
    // ----------------------------------------------------------------
    public String getErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMsg)).getText();
    }

    // ----------------------------------------------------------------
    // Kiểm tra icon hiện mật khẩu có tồn tại không
    // ----------------------------------------------------------------
    public boolean isShowPasswordIconPresent() {
        return !Constant.WEBDRIVER.findElements(
                By.xpath("//button[contains(@aria-label,'password') or contains(@class,'toggle') " +
                        "or .//*[name(🙁'svg'][contains(@class,'eye')] " +
                        "or .//*[name(🙁'i'][contains(@class,'eye')]]")
        ).isEmpty();
    }

    // ----------------------------------------------------------------
    // Kiểm tra field password đang ở type='text' (đang hiện) hay 'password' (đang ẩn)
    // ----------------------------------------------------------------
    public boolean isPasswordVisible() {
        WebElement pwd = Constant.WEBDRIVER.findElement(txtPassword);
        return "text".equals(pwd.getAttribute("type"));
    }

    // ----------------------------------------------------------------
    // Bấm icon toggle hiện/ẩn mật khẩu
    // ----------------------------------------------------------------
    public void clickShowPasswordIcon() {
        By toggleBtn = By.xpath(
                "//button[contains(@aria-label,'password') or contains(@class,'toggle')" +
                        " or .//*[name(🙁'svg'][contains(@class,'eye')] " +
                        " or .//*[name(🙁'i'][contains(@class,'eye')]]");
        wait.until(ExpectedConditions.elementToBeClickable(toggleBtn)).click();
    }

    // ----------------------------------------------------------------
    // Kiểm tra URL hiện tại có chứa chuỗi mong đợi không
    // ----------------------------------------------------------------
    public boolean isRedirectedTo(String urlPart) {
        try {
            wait.until(ExpectedConditions.urlContains(urlPart));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ----------------------------------------------------------------
    // Kiểm tra vẫn đang ở trang login (URL không đổi)
    // ----------------------------------------------------------------
    public boolean isStillOnLoginPage() {
        try {
            Thread.sleep(2000); // chờ ngắn để tránh false positive
        } catch (InterruptedException ignored) {}
        String url = Constant.WEBDRIVER.getCurrentUrl();
        return url.equals(Constant.URL + "/") || url.contains("login");
    }

    // ----------------------------------------------------------------
    // Private helper
    // ----------------------------------------------------------------
    private void fillForm(String username, String password) {
        WebElement user = wait.until(ExpectedConditions.visibilityOfElementLocated(txtUsername));
        user.clear();
        user.sendKeys(username);

        WebElement pass = wait.until(ExpectedConditions.visibilityOfElementLocated(txtPassword));
        pass.clear();
        pass.sendKeys(password);

        wait.until(ExpectedConditions.elementToBeClickable(btnLogin)).click();
    }
}