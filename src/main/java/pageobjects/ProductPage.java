package pageobjects;

import common.Constant;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class ProductPage {

    private WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(15));

    // ── Danh sách sản phẩm ──────────────────────────────────────────
    private By btnAddProduct    = By.xpath("//button[contains(.,'Thêm sản phẩm') or contains(.,'Thêm mới')]");
    private By productTableRows = By.xpath("//table//tbody//tr");

    // ── Form thêm / sửa sản phẩm ────────────────────────────────────
    private By txtProductName   = By.xpath("//input[@name='name' or @placeholder[contains(.,'tên') or contains(.,'Tên')]]");
    private By txtContent       = By.xpath("//input[@name='content' or @name='hamluong' or @placeholder[contains(.,'hàm lượng') or contains(.,'Hàm lượng')]]");
    private By txtUnit          = By.xpath("//input[@name='unit' or @name='donvi' or @placeholder[contains(.,'đơn vị') or contains(.,'Đơn vị')]]");
    private By txtPrice         = By.xpath("//input[@name='price' or @name='giaban' or @type='number'][1]");
    private By ddlGroup         = By.xpath("//select[@name='group' or @name='nhomthuoc'] | //div[contains(@class,'select')][contains(.,'Nhóm')]");
    private By ddlStatus        = By.xpath("//select[@name='status' or @name='trangthai']");
    private By btnSubmit        = By.xpath("//button[@type='submit' or contains(.,'Thêm mới') or contains(.,'Cập nhật')]");
    private By btnCancel        = By.xpath("//button[contains(.,'Hủy')]");

    // ── Thông báo kết quả ────────────────────────────────────────────
    private By toastSuccess     = By.xpath("//*[contains(text(),'thành công')]");
    private By toastFail        = By.xpath("//*[contains(text(),'thất bại') or contains(text(),'Thất bại')]");
    private By toastAny         = By.xpath("//*[contains(@class,'toast') or contains(@class,'alert') or contains(@class,'message') or contains(@class,'notification')]");
    private By validationMsg    = By.xpath("//*[contains(@class,'error') or contains(@class,'invalid') or contains(@class,'valid')]");

    // ── Nút Sửa trong bảng ──────────────────────────────────────────
    private By btnEditFirst     = By.xpath("(//button[contains(@title,'Sửa') or contains(.,'Sửa')] | //a[contains(@title,'Sửa')])[1]");

    // ── Trường mã sản phẩm trong form sửa ───────────────────────────
    private By txtProductCode   = By.xpath("//input[@name='code' or @name='masp' or @placeholder[contains(.,'mã') or contains(.,'Mã')]]");

    // ================================================================
    // Navigation
    // ================================================================

    public void openProductPage() {
        Constant.WEBDRIVER.get(Constant.URL + "/products");
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(btnAddProduct),
                ExpectedConditions.visibilityOfElementLocated(productTableRows)
        ));
    }

    // ================================================================
    // Thêm sản phẩm
    // ================================================================

    public void clickAddProduct() {
        wait.until(ExpectedConditions.elementToBeClickable(btnAddProduct)).click();
        // Chờ form mở
        wait.until(ExpectedConditions.visibilityOfElementLocated(txtProductName));
    }

    /**
     * Điền form thêm/sửa sản phẩm.
     * Truyền null để bỏ trống trường đó.
     */
    public void fillProductForm(String name, String content, String unit, String price, String status) {
        if (name != null) {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(txtProductName));
            el.clear();
            el.sendKeys(name);
        }

        if (content != null) {
            List<WebElement> els = Constant.WEBDRIVER.findElements(txtContent);
            if (!els.isEmpty()) {
                els.get(0).clear();
                els.get(0).sendKeys(content);
            }
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
            List<WebElement> selects = Constant.WEBDRIVER.findElements(ddlStatus);
            if (!selects.isEmpty()) {
                new Select(selects.get(0)).selectByVisibleText(status);
            }
        }
    }

    public void clickSubmit() {
        wait.until(ExpectedConditions.elementToBeClickable(btnSubmit)).click();
    }

    public void clickCancel() {
        wait.until(ExpectedConditions.elementToBeClickable(btnCancel)).click();
    }

    // ================================================================
    // Kiểm tra kết quả
    // ================================================================

    /** Chờ và trả về text thông báo (toast / alert) */
    public String getToastMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(toastAny)).getText();
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

    /** Kiểm tra có thông báo validation (lỗi field) không */
    public boolean isValidationErrorDisplayed() {
        try {
            // Ưu tiên HTML5 validation
            WebElement nameField = Constant.WEBDRIVER.findElement(txtProductName);
            if (!nameField.getAttribute("validity.valid").equals("true")) return true;
        } catch (Exception ignored) {}

        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(validationMsg)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Kiểm tra sản phẩm vừa thêm có xuất hiện trong bảng không */
    public boolean isProductInTable(String productName) {
        By row = By.xpath("//table//tbody//tr[.//*[contains(text(),'" + productName + "')]]");
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(row)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Lấy mã sản phẩm của dòng cuối cùng trong bảng (để kiểm tra tự sinh mã) */
    public String getLastProductCode() {
        List<WebElement> rows = Constant.WEBDRIVER.findElements(productTableRows);
        if (rows.isEmpty()) return "";
        WebElement lastRow = rows.get(rows.size() - 1);
        // Cột đầu tiên thường là mã sản phẩm
        return lastRow.findElement(By.xpath(".//td[1]")).getText().trim();
    }

    /** Kiểm tra vẫn đang ở trang danh sách sản phẩm (sau khi bấm Hủy) */
    public boolean isOnProductListPage() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}
        return Constant.WEBDRIVER.getCurrentUrl().contains("/products")
                && Constant.WEBDRIVER.findElements(btnAddProduct).size() > 0;
    }

    // ================================================================
    // Cập nhật sản phẩm
    // ================================================================

    /** Bấm nút Sửa của dòng đầu tiên trong bảng */
    public void clickEditFirstProduct() {
        wait.until(ExpectedConditions.elementToBeClickable(btnEditFirst)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(txtProductName));
    }

    /** Kiểm tra trường mã sản phẩm có bị disabled / readonly không */
    public boolean isProductCodeFieldReadOnly() {
        List<WebElement> codeFields = Constant.WEBDRIVER.findElements(txtProductCode);
        if (codeFields.isEmpty()) return true; // không hiển thị trường mã → coi như không cho sửa
        WebElement field = codeFields.get(0);
        return field.getAttribute("disabled") != null
                || field.getAttribute("readonly") != null
                || !field.isEnabled();
    }

    /** Xóa nội dung trường tên sản phẩm */
    public void clearProductName() {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(txtProductName));
        el.clear();
        // Một số framework cần JS để trigger change event
        ((JavascriptExecutor) Constant.WEBDRIVER).executeScript("arguments[0].value='';", el);
    }

    /** Lấy giá trị mặc định của dropdown Trạng thái */
    public String getDefaultStatus() {
        List<WebElement> selects = Constant.WEBDRIVER.findElements(ddlStatus);
        if (!selects.isEmpty()) {
            return new Select(selects.get(0)).getFirstSelectedOption().getText().trim();
        }
        // Nếu không phải select tag, tìm text hiển thị
        List<WebElement> statusTexts = Constant.WEBDRIVER.findElements(
                By.xpath("//*[contains(@class,'status') or contains(@class,'trangthai')][1]"));
        return statusTexts.isEmpty() ? "" : statusTexts.get(0).getText().trim();
    }
}
