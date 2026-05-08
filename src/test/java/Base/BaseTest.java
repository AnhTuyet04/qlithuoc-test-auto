package Base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;
import java.util.List;

public class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    protected final String BASE_URL = "https://qlibanthuoc-tester.vercel.app/";

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get(BASE_URL);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            try {
                Thread.sleep(10000); // Dừng lại 10 giây sau khi test chạy xong
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            driver.quit(); // Sau 10 giây thì đóng trình duyệt
        }
    }

    protected WebElement findFirstVisible(By... locators) {
        return wait.until(d -> {
            for (By locator : locators) {
                List<WebElement> elements = d.findElements(locator);

                for (WebElement element : elements) {
                    if (element.isDisplayed()) {
                        return element;
                    }
                }
            }

            return null;
        });
    }

    protected WebElement findFirstClickable(By... locators) {
        return wait.until(d -> {
            for (By locator : locators) {
                List<WebElement> elements = d.findElements(locator);

                for (WebElement element : elements) {
                    if (element.isDisplayed() && element.isEnabled()) {
                        return element;
                    }
                }
            }

            return null;
        });
    }

    protected void type(WebElement element, String value) {
        element.clear();
        element.sendKeys(value);
    }

    protected String getBodyText() {
        return driver.findElement(By.tagName("body")).getText();
    }
}