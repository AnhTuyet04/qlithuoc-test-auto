package pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class ImportPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By menuImport = By.xpath("//nav//a[contains(@href, '/imports')]");
    private By btnCreateImport = By.xpath("//button[contains(text(), 'Tạo phiếu nhập')]");

    private By searchBox = By.cssSelector("input[placeholder*='Tìm kiếm sản phẩm']");
    private By searchResultItems = By.cssSelector(".product-search-item, .dropdown-item, li.item, [role='option'], li, .search-result");
    private By itemCard = By.className("import-item-card");

    private By txtQuantity = By.xpath("//label[text()='Số lượng']/following-sibling::input");
    private By txtImportPrice = By.xpath("//label[text()='Giá nhập']/following-sibling::input");
    private By txtBatchNumber = By.xpath("//input[@placeholder='Nhập số lô']");
    private By txtExpiryDate = By.cssSelector("input[type='date']");

    private By btnComplete = By.xpath("//button[contains(text(), 'Hoàn tất') or contains(@class, 'btn-primary-full')]");
    private By btnSaveDraft = By.xpath("//button[contains(text(), 'Lưu nháp')]");

    private By toastMessage = By.cssSelector("[data-sonner-toast], .toast-message, .alert, .error-message");
    private By summaryTotal = By.xpath("//div[contains(text(), 'Tổng giá trị')]/following-sibling::div | //strong[contains(text(), 'đ')]");
    private By statusBadge = By.cssSelector("table tbody tr:first-child .status-badge, table tbody tr:first-child td:nth-last-child(2)");

    public ImportPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    private void clickJS(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        } catch (Exception e) {
            System.out.println("Lỗi click JS: " + e.getMessage());
        }
    }

    public void navigateToCreateImport() {
        try {
            Thread.sleep(1000);
            clickJS(menuImport);
            Thread.sleep(1500);
            clickJS(btnCreateImport);
            Thread.sleep(1500);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void selectProduct(String productName) {
        try {
            WebElement input = wait.until(ExpectedConditions.presenceOfElementLocated(searchBox));
            ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", input);
            input.clear();
            input.sendKeys(productName);

            Thread.sleep(2000);

            List<WebElement> results = driver.findElements(searchResultItems);
            if (!results.isEmpty()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", results.get(0));
            } else {
                input.sendKeys(Keys.ARROW_DOWN);
                Thread.sleep(500);
                input.sendKeys(Keys.ENTER);
            }

            wait.until(ExpectedConditions.presenceOfElementLocated(itemCard));
            Thread.sleep(1000);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void fillImportItemDetails(String batch, String expiry, String qty, String price) {
        try {
            // 1. CHỜ 1.5 GIÂY ĐỂ WEB LOAD XONG DỮ LIỆU AUTO-FILL (Như giá nhập mặc định 12017)
            Thread.sleep(1500);

            if (batch != null) {
                WebElement b = wait.until(ExpectedConditions.presenceOfElementLocated(txtBatchNumber));
                b.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
                if (!batch.isEmpty()) b.sendKeys(batch);
            }

            if (expiry != null) {
                WebElement e = wait.until(ExpectedConditions.presenceOfElementLocated(txtExpiryDate));
                if (!expiry.isEmpty()) {
                    String[] parts = expiry.split("/");
                    String isoDate = parts[2] + "-" + parts[1] + "-" + parts[0];

                    String reactJS = "var setter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
                            "setter.call(arguments[0], arguments[1]);" +
                            "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));";
                    ((JavascriptExecutor) driver).executeScript(reactJS, e, isoDate);
                } else {
                    e.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
                }
                e.sendKeys(Keys.ESCAPE);
                e.sendKeys(Keys.TAB);
            }

            if (qty != null) {
                WebElement q = wait.until(ExpectedConditions.presenceOfElementLocated(txtQuantity));
                // Kết hợp 2 cách xóa để đảm bảo sạch sẽ dữ liệu cũ
                ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", q);
                q.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
                if (!qty.isEmpty()) q.sendKeys(qty);
            }

            if (price != null) {
                WebElement p = wait.until(ExpectedConditions.presenceOfElementLocated(txtImportPrice));
                // Kết hợp 2 cách xóa để quét sạch cái giá 12017 đi
                ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", p);
                p.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
                if (!price.isEmpty()) {
                    p.sendKeys(price);
                }
            }

            // 2. Click ra khoảng trắng và đợi hệ thống tính toán lại Thành tiền mới nhất
            clickJS(By.tagName("body"));
            Thread.sleep(1500);

        } catch (Exception e) { e.printStackTrace(); }
    }

    public void clickComplete() {
        clickJS(btnComplete);
    }

    public void clickSaveDraft() {
        clickJS(btnSaveDraft);
    }

    public String getAllErrorMessages() {
        StringBuilder errors = new StringBuilder();
        try {
            for(WebElement el : driver.findElements(toastMessage)) errors.append(el.getText()).append(" | ");
            for(WebElement el : driver.findElements(By.xpath("//*[contains(@class, 'error') or contains(@class, 'text-danger')]"))) errors.append(el.getText()).append(" | ");
            for(WebElement el : driver.findElements(By.cssSelector("input:invalid"))) errors.append(el.getAttribute("validationMessage")).append(" | ");
        } catch (Exception e) {}
        return errors.toString().toLowerCase();
    }

    public long getTotalPrice() {
        try {
            WebElement totalEl = wait.until(ExpectedConditions.presenceOfElementLocated(summaryTotal));
            String cleaned = totalEl.getText().replaceAll("[^\\d]", "");
            return cleaned.isEmpty() ? 0L : Long.parseLong(cleaned);
        } catch (Exception e) { return 0L; }
    }

    public String getFirstRowStatus() {
        try {
            WebElement status = wait.until(ExpectedConditions.presenceOfElementLocated(statusBadge));
            return status.getText();
        } catch (Exception e) { return ""; }
    }
}