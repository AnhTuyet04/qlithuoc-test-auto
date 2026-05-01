package pageobjects;

import common.Constant;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class InventoryPage {

    private WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(15));

    // ── Tìm kiếm ────────────────────────────────────────────────────
    private By searchInput   = By.xpath("//input[contains(@placeholder,'Tìm') or contains(@placeholder,'tìm') or contains(@placeholder,'sản phẩm') or contains(@placeholder,'lô')]");
    private By ddlStatus     = By.xpath("//select[@name='status' or @name='trangthai'] | //select[contains(@class,'filter')]");

    // ── Bảng kết quả ────────────────────────────────────────────────
    private By tableRows     = By.xpath("//table//tbody//tr");
    private By emptyMsg      = By.xpath("//*[contains(text(),'Không có dữ liệu') or contains(text(),'không có dữ liệu') or contains(text(),'phù hợp')]");
    private By errorMsg      = By.xpath("//*[contains(text(),'Không tải được') or contains(text(),'lỗi') or contains(@class,'error')]");

    // ── Phân trang ───────────────────────────────────────────────────
    private By pagination    = By.xpath("//*[contains(@class,'pagination') or contains(@class,'paging')]");

    // ── Trạng thái / cảnh báo trong bảng ────────────────────────────
    // Dòng có số lượng = 0 (hết hàng)
    private By outOfStockRow = By.xpath("//table//tbody//tr[.//*[contains(text(),'Hết hàng') or contains(text(),'hết hàng') or contains(text(),'0')]]");
    // Badge / label cảnh báo cận hạn
    private By nearExpiryBadge = By.xpath("//*[contains(text(),'Cận hạn') or contains(text(),'cận hạn') or contains(@class,'near-expiry') or contains(@class,'warning')]");

    // ================================================================
    // Navigation
    // ================================================================

    public void openInventoryPage() {
        Constant.WEBDRIVER.get(Constant.URL + "/inventory");
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(tableRows),
                ExpectedConditions.visibilityOfElementLocated(emptyMsg)
        ));
    }

    // ================================================================
    // Tìm kiếm & Lọc
    // ================================================================

    public void searchByKeyword(String keyword) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput));
        input.clear();
        input.sendKeys(keyword);
        // Chờ kết quả cập nhật
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    }

    public void filterByStatus(String statusText) {
        List<WebElement> selects = Constant.WEBDRIVER.findElements(ddlStatus);
        if (!selects.isEmpty()) {
            new Select(selects.get(0)).selectByVisibleText(statusText);
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }
    }

    public void clearSearch() {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput));
        input.clear();
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    }

    // ================================================================
    // Kiểm tra kết quả
    // ================================================================

    /** Kiểm tra bảng có hiển thị dòng chứa keyword không */
    public boolean isResultDisplayed(String keyword) {
        By row = By.xpath("//table//tbody//tr[.//*[contains(text(),'" + keyword + "')]]");
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(row)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Kiểm tra có dữ liệu trong bảng không (ít nhất 1 dòng) */
    public boolean hasData() {
        try {
            List<WebElement> rows = Constant.WEBDRIVER.findElements(tableRows);
            return !rows.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /** Kiểm tra thông báo "Không có dữ liệu tồn kho phù hợp" */
    public boolean isEmptyMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(emptyMsg)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Kiểm tra thông báo lỗi tải dữ liệu */
    public boolean isErrorMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMsg)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Kiểm tra phân trang có hiển thị không */
    public boolean isPaginationDisplayed() {
        return !Constant.WEBDRIVER.findElements(pagination).isEmpty();
    }

    /** Đếm số dòng hiện tại trong bảng */
    public int getRowCount() {
        return Constant.WEBDRIVER.findElements(tableRows).size();
    }

    /**
     * Kiểm tra sản phẩm hết hàng (số lượng = 0) vẫn hiển thị trong bảng.
     * INV_08: Hệ thống phải hiển thị cả dòng hết hàng.
     */
    public boolean isOutOfStockRowDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(outOfStockRow)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Kiểm tra có badge / label cảnh báo cận hạn trong bảng không.
     * INV_09: Hệ thống phải hiển thị trạng thái cảnh báo cận hạn.
     */
    public boolean isNearExpiryWarningDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(nearExpiryBadge)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Lấy text của ô trạng thái trên dòng chứa keyword.
     * Dùng để verify trạng thái cụ thể (Còn hàng / Hết hàng / Cận hạn).
     */
    public String getStatusOfRow(String keyword) {
        By statusCell = By.xpath(
                "//table//tbody//tr[.//*[contains(text(),'" + keyword + "')]]//*[contains(@class,'status') or contains(@class,'badge')]"
        );
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(statusCell)).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }
}
