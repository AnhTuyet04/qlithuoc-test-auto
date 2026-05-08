package testcases;

import java.io.File;
import java.time.Duration;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import common.Constant;
import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        Constant.WEBDRIVER = driver; // đồng bộ cho các page object dùng Constant.WEBDRIVER
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.get(Constant.URL);
    }

    public void login(String username, String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@type='text' or @name='username' or @name='tenDangNhap']"))).clear();
        driver.findElement(
                By.xpath("//input[@type='text' or @name='username' or @name='tenDangNhap']")).sendKeys(username);

        driver.findElement(
                By.xpath("//input[@type='password' or @name='password' or @name='matKhau']")).clear();
        driver.findElement(
                By.xpath("//input[@type='password' or @name='password' or @name='matKhau']")).sendKeys(password);

        driver.findElement(
                By.xpath("//button[contains(.,'Đăng nhập') or @type='submit']")).click();

        // Chờ chuyển trang sau đăng nhập
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/products"),
                ExpectedConditions.urlContains("/dashboard"),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("aside.sidebar, .dashboard"))
        ));
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        try {
            if (ITestResult.FAILURE == result.getStatus()) {
                TakesScreenshot ts = (TakesScreenshot) driver;
                File source = ts.getScreenshotAs(OutputType.FILE);

                File folder = new File("evidence");
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                FileUtils.copyFile(source, new File("evidence/" + result.getName() + ".png"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (driver != null) {
                try {
                    driver.quit();
                } catch (Exception e) {
                    System.out.println("Driver đã đóng");
                }
            }
        }
    }
}
