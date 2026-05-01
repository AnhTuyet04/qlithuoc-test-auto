package testcases;

import common.Constant;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;

public class BaseTest {

    @BeforeMethod
    public void beforeMethod() {
        WebDriverManager.chromedriver().setup();
        Constant.WEBDRIVER = new ChromeDriver();
        Constant.WEBDRIVER.manage().window().maximize();
        Constant.WEBDRIVER.get(Constant.URL);
    }

    @AfterMethod
    public void afterMethod(ITestResult result) {
        try {
            if (ITestResult.FAILURE == result.getStatus()) {
                TakesScreenshot ts = (TakesScreenshot) Constant.WEBDRIVER;
                File source = ts.getScreenshotAs(OutputType.FILE);

                File folder = new File("evidence");
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                FileUtils.copyFile(
                        source,
                        new File("evidence/" + result.getName() + ".png")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (Constant.WEBDRIVER != null) {
                try {
                    Constant.WEBDRIVER.quit();
                } catch (Exception e) {
                    System.out.println("Driver đã đóng");
                }
            }
        }
    }
}