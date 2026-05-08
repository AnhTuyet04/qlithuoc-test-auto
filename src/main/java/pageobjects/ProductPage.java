package pageobjects;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import common.Constant;

public class ProductPage {

    private WebDriver driver;
    private WebDriverWait wait;

    private By menuProduct = By.xpath("//nav//a[contains(@href, '/products')]");

    private By btnAddProduct = By.className("btn-add");
    private By productTableRows = By.xpath("//table//tbody//tr");

    private By btnEditFirst = By.cssSelector("table tbody tr:first-child .icon-btn");

    private By txtProductName = By.name("tenSanPham");
    private By txtContent = By.name("hamLuong");
    private By txtUnit = By.name("donViTinh");
    private By txtPrice = By.name("giaBan");
    private By ddlGroup = By.name("maNhom");
    private By ddlStatus = By.name("trangThai");
    private By btnSubmit = By.className("btn-save");
    private By btnCancel = By.className("btn-cancel");

    private By txtProductCode = By.xpath("//input[contains(@name,'maSanPham') or contains(@name,'productCode')]");

    private By toastSuccess = By.xpath("//*[contains(text(),'thành công') or contains(text(),'Thành công')]");
    private By toastFail = By.xpath("//*[contains(text(),'thất bại') or contains(text(),'Thất bại') or contains(text(),'lỗi')]");
    private By toastMessage = By.cssSelector("[data-sonner-toast], .toast-message, .alert");
    private By validationMsg = By.cssSelector(".error-msg, .text-danger, .invalid-feedback, .error, .invalid");

    private By firstRowProductName = By.cssSelector(
            "table tbody tr:first-child td.td-name, table tbody tr:first-child td:nth-child(2)"
    );

    private By modalBox = By.className("modal-box");

    public ProductPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void openProductPage() {
        String base = Constant.URL.replaceAll("/$", "");
        driver.get(base + "/products");

        wait.until(ExpectedConditions.urlContains("/products"));

        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(btnAddProduct),
                ExpectedConditions.visibilityOfElementLocated(productTableRows),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Quản lý sản phẩm')]"))
        ));
    }

    public void navigateToProducts() {
        wait.until(ExpectedConditions.elementToBeClickable(menuProduct)).click();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(btnAddProduct),
                ExpectedConditions.visibilityOfElementLocated(productTableRows)
        ));
    }

    public void clickAddProduct() {
        closeToastIfExists();

        WebElement btn = wait.until(ExpectedConditions.visibilityOfElementLocated(btnAddProduct));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);

        wait.until(ExpectedConditions.visibilityOfElementLocated(txtProductName));
    }

    public void fillProductForm(String name, String content, String unit, String price, String status) {
        if (name != null) {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(txtProductName));
            el.clear();
            el.sendKeys(name);
        }

        if (content != null) {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(txtContent));
            el.clear();
            el.sendKeys(content);
        }

        if (unit != null) {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(txtUnit));
            el.clear();
            el.sendKeys(unit);
        }

        if (price != null) {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(txtPrice));
            el.clear();
            el.sendKeys(price);
        }

        if (status != null) {
            List<WebElement> selects = driver.findElements(ddlStatus);
            if (!selects.isEmpty()) {
                new Select(selects.get(0)).selectByVisibleText(status);
            }
        }
    }

    public void clickEditFirstProduct() {
        closeToastIfExists();

        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(btnEditFirst));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);

        wait.until(ExpectedConditions.visibilityOfElementLocated(txtProductName));
    }

    public void updateProductInfo(String name, String price) {
        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(txtProductName));
        nameInput.clear();
        if (name != null && !name.isEmpty()) {
            nameInput.sendKeys(name);
        }

        WebElement priceInput = wait.until(ExpectedConditions.visibilityOfElementLocated(txtPrice));
        priceInput.clear();
        if (price != null && !price.isEmpty()) {
            priceInput.sendKeys(price);
        }
    }

    public void changeStatus(String statusText) {
        WebElement selectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(ddlStatus));
        new Select(selectElement).selectByVisibleText(statusText);
    }

    public void clickSubmit() {
        closeToastIfExists();

        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(btnSubmit));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    public void clickCancel() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(btnCancel));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    public String getToastMessage() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            return shortWait.until(ExpectedConditions.visibilityOfElementLocated(toastMessage)).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isSuccessMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(toastSuccess)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isFailMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(toastFail)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isValidationErrorDisplayed() {
        try {
            WebElement nameField = driver.findElement(txtProductName);
            Object valid = ((JavascriptExecutor) driver)
                    .executeScript("return arguments[0].validity.valid;", nameField);
            if (Boolean.FALSE.equals(valid)) {
                return true;
            }
        } catch (Exception ignored) {
        }

        return !driver.findElements(validationMsg).isEmpty();
    }

    public boolean isInlineErrorDisplayed() {
        return !driver.findElements(validationMsg).isEmpty();
    }

    public boolean isModalStillOpen() {
        try {
            Thread.sleep(1000);
            List<WebElement> modals = driver.findElements(modalBox);
            return !modals.isEmpty() && modals.get(0).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isProductInTable(String productName) {
        By row = By.xpath("//table//tbody//tr[.//*[contains(text(),\"" + productName + "\")]]");
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(row)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getLastProductCode() {
        wait.until(driver -> {
            List<WebElement> rows = driver.findElements(productTableRows);
            if (rows.isEmpty()) return false;

            String text = rows.get(0).getText();
            return !text.contains("Đang tải");
        });

        List<WebElement> rows = driver.findElements(productTableRows);
        if (rows.isEmpty()) return "";

        return rows.get(rows.size() - 1)
                .findElement(By.xpath(".//td[1]"))
                .getText().trim();
    }

    public String getFirstProductNameInTable() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(firstRowProductName)).getText();
    }

    public boolean isProductCodeFieldReadOnly() {
        List<WebElement> codeFields = driver.findElements(txtProductCode);
        if (codeFields.isEmpty()) {
            return true;
        }

        WebElement field = codeFields.get(0);
        return field.getAttribute("disabled") != null
                || field.getAttribute("readonly") != null
                || !field.isEnabled();
    }

    public boolean isProductCodeEditable() {
        List<WebElement> codeInputs = driver.findElements(txtProductCode);
        if (codeInputs.isEmpty()) {
            return false;
        }

        WebElement field = codeInputs.get(0);
        return field.isEnabled() && field.getAttribute("readonly") == null;
    }

    public void clearProductName() {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(txtProductName));
        el.clear();
        ((JavascriptExecutor) driver).executeScript("arguments[0].value='';", el);
    }

    public String getDefaultStatus() {
        List<WebElement> selects = driver.findElements(ddlStatus);
        if (!selects.isEmpty()) {
            return new Select(selects.get(0)).getFirstSelectedOption().getText().trim();
        }

        List<WebElement> statusTexts = driver.findElements(
                By.xpath("//*[contains(@class,'status') or contains(@class,'trangthai')][1]")
        );
        return statusTexts.isEmpty() ? "" : statusTexts.get(0).getText().trim();
    }

    public boolean isOnProductListPage() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }

        return driver.getCurrentUrl().contains("/products")
                && !driver.findElements(btnAddProduct).isEmpty();
    }

    private void closeToastIfExists() {
        try {
            List<WebElement> toasts = driver.findElements(By.cssSelector("[data-sonner-toast]"));
            if (!toasts.isEmpty()) {
                ((JavascriptExecutor) driver).executeScript(
                        "document.querySelectorAll('[data-sonner-toast]').forEach(e => e.remove());"
                );
            }
        } catch (Exception ignored) {
        }
    }
}