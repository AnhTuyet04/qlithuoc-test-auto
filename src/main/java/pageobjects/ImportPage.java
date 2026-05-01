package pageobjects;

import common.Constant;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ImportPage {

    private WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(15));

    // ── Danh sách phiếu nhập ────────────────────────────────────────
    private By btnCreateImport  = By.xpath("//button[contains(.,'Tạo phiếu nhập') or contains(.,'Tạo phiếu')]");

    // ── Form phiếu nhập ─────────────────────────────────────────────
    private By searchProduct    = By.xpath("//input[contains(@placeholder,'Tìm') or contains(@placeholder,'tìm') or contains(@placeholder,'sản phẩm')]");
    private By btnAddRow        = By.xpath("//button[contains(.,'Thêm') or contains(.,'thêm sản phẩm')]");

    // ── Các trường trong dòng sản phẩm (lấy theo index dòng cuối) ──
    private By allBatchInputs   = By.xpath("//input[@name[contains(.,'batch') or contains(.,'solo') or contains(.,'lo')] or @placeholder[contains(.,'lô') or contains(.,'Lô') or contains(.,'số lô')]]");
    private By allExpiryInputs  = By.xpath("//input[@type='date' or @name[contains(.,'expiry') or contains(.,'hansd') or contains(.,'hsd')]]");
    private By allQtyInputs     = By.xpath("//input[@name[contains(.,'qty') or contains(.,'quantity') or contains(.,'soluong')] or @placeholder[contains(.,'số lượng') or contains(.,'Số lượng')]]");
    private By allPriceInputs   = By.xpath("//input[@name[contains(.,'price') or contains(.,'gianhap')] or @placeholder[contains(.,'giá nhập') or contains(.,'Giá nhập')]]");

    // ── Nút hành động ───────────────────────────────────────────────
    private By btnComplete      = By.xpath("//button[contains(.,'Hoàn tất') or contains(.,'hoàn tất')]");
    private By btnSaveDraft     = By.xpath("//button[contains(.,'Lưu nháp') or contains(.,'lưu nháp')]");

    // ── Thông báo ───────────────────────────────────────────────────
    private By toastAny         = By.xpath("//*[contains(@class,'toast') or contains(@class,'alert') or contains(@class,'message') or contains(@class,'notification') or contains(@class,'Toastify')]");
    private By toastSuccess     = By.xpath("//*[contains(text(),'thành công') or contains(text(),'Thành công')]");
    private By toastFail        = By.xpath("//*[contains(text(),'thất bại') or contains(text(),'Thất bại') or contains(text(),'không thể') or contains(text(),'Không thể')]");
    private By validationMsg    = By.xpath("//*[contains(@class,'error') or contains(@class,'invalid') or contains(text(),'Vui lòng') or contains(text(),'phải lớn hơn')]");

    // ── Tổng tiền ───────────────────────────────────────────────────
    private By totalAmount      = By.xpath("//*[contains(@class,'total') or contains(text(),'Tổng tiền') or contains(text(),'tổng tiền')]/following-sibling::* | //*[@id='total'] | //*[contains(@class,'total-amount')]");

    // ================================================================
    // Navigation
    // ================================================================

    public void openImportPage() {
        Constant.WEBDRIVER.get(Constant.URL + "/imports");
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(btnCreateImport),
                ExpectedConditions.urlContains("/imports")
        ));
    }

    public void clickCreateImport() {
        wait.until(ExpectedConditions.elementToBeClickable(btnCreateImport)).click();
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(searchProduct),
                ExpectedConditions.visibilityOfElementLocated(btnAddRow)
        ));
    }

    // ================================================================
    // Thêm sản phẩm vào phiếu nhập
    // ================================================================

    /**
     * Tìm và chọn sản phẩm từ ô tìm kiếm.
     */
    public void searchAndSelectProduct(String productName) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(searchProduct));
        input.clear();
        input.sendKeys(productName);

        By option = By.xpath("//*[contains(text(),'" + productName + "')]");
        wait.until(ExpectedConditions.elementToBeClickable(option)).click();
    }

    /**
     * Điền thông tin dòng sản phẩm trong phiếu nhập.
     * Truyền null để bỏ trống trường đó (test case kiểm tra validation).
     *
     * @param batchNo   Số lô (null = bỏ trống)
     * @param expiryDate Hạn sử dụng dạng yyyy-MM-dd (null = bỏ trống, "expired" = ngày quá khứ)
     * @param quantity  Số lượng (null = bỏ trống)
     * @param price     Giá nhập (null = bỏ trống)
     */
    public void fillImportRow(String batchNo, String expiryDate, String quantity, String price) {
        // Lấy dòng cuối cùng (dòng vừa thêm)
        if (batchNo != null) {
            List<WebElement> batches = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(allBatchInputs));
            WebElement field = batches.get(batches.size() - 1);
            field.clear();
            field.sendKeys(batchNo);
        }

        if (expiryDate != null) {
            List<WebElement> expiries = Constant.WEBDRIVER.findElements(allExpiryInputs);
            if (!expiries.isEmpty()) {
                WebElement field = expiries.get(expiries.size() - 1);
                String dateValue;
                if ("expired".equals(expiryDate)) {
                    // Ngày đã hết hạn: hôm qua
                    dateValue = LocalDate.now().minusDays(1)
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } else if ("future".equals(expiryDate)) {
                    // Ngày hợp lệ: 1 năm sau
                    dateValue = LocalDate.now().plusYears(1)
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } else {
                    dateValue = expiryDate;
                }
                // Dùng JS để set value cho input type=date
                ((JavascriptExecutor) Constant.WEBDRIVER)
                        .executeScript("arguments[0].value='" + dateValue + "';", field);
            }
        }

        if (quantity != null) {
            List<WebElement> qtys = Constant.WEBDRIVER.findElements(allQtyInputs);
            if (!qtys.isEmpty()) {
                WebElement field = qtys.get(qtys.size() - 1);
                field.clear();
                field.sendKeys(quantity);
            }
        }

        if (price != null) {
            List<WebElement> prices = Constant.WEBDRIVER.findElements(allPriceInputs);
            if (!prices.isEmpty()) {
                WebElement field = prices.get(prices.size() - 1);
                field.clear();
                field.sendKeys(price);
            }
        }
    }

    // ================================================================
    // Hành động
    // ================================================================

    public void clickComplete() {
        wait.until(ExpectedConditions.elementToBeClickable(btnComplete)).click();
    }

    public void clickSaveDraft() {
        wait.until(ExpectedConditions.elementToBeClickable(btnSaveDraft)).click();
    }

    // ================================================================
    // Kiểm tra kết quả
    // ================================================================

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

    /**
     * Lấy text thông báo validation (lỗi field cụ thể).
     * Dùng cho IMP_04→IMP_09.
     */
    public String getValidationMessage() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(validationMsg)).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isValidationMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(validationMsg)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Lấy text tổng tiền hiển thị trên form.
     */
    public String getTotalAmountText() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(totalAmount)).getText();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Tính tổng tiền kỳ vọng = số lượng * giá nhập.
     */
    public long calculateExpectedTotal(int quantity, long price) {
        return (long) quantity * price;
    }

    /**
     * Kiểm tra phiếu nhập vừa tạo có xuất hiện trong danh sách không.
     */
    public boolean isImportInList() {
        By firstRow = By.xpath("//table//tbody//tr[1]");
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(firstRow)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Kiểm tra trạng thái phiếu nhập đầu tiên trong danh sách.
     * Dùng cho IMP_02 (Lưu nháp → trạng thái "Nháp").
     */
    public String getFirstImportStatus() {
        By statusCell = By.xpath("//table//tbody//tr[1]//*[contains(@class,'status') or contains(@class,'badge')]");
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(statusCell)).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }
}
