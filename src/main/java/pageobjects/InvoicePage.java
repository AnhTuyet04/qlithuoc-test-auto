package pageobjects;

import common.Constant;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class InvoicePage {

    private WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(20));

    // ================================================================
    // Locators — Tạo hóa đơn
    // ================================================================
    private By titleCreateInvoice = By.xpath(
            "//*[contains(text(),'Tạo hóa đơn') or contains(text(),'Tạo hóa đơn bán hàng')]");
    private By searchProduct  = By.xpath(
            "//input[contains(@placeholder,'Tìm') or contains(@placeholder,'tìm') or contains(@placeholder,'sản phẩm')]");
    private By quantityInput  = By.xpath("//input[@type='number'][last()]");
    private By btnSaveDraft   = By.xpath("//button[contains(.,'Lưu nháp') or contains(.,'lưu nháp')]");
    private By btnPay         = By.xpath("//button[contains(.,'Thanh toán')]");

    // ── Phương thức thanh toán ───────────────────────────────────────
    private By optCash        = By.xpath("//*[contains(text(),'Tiền mặt')]");
    private By optTransfer    = By.xpath("//*[contains(text(),'Chuyển khoản')]");

    // ── Tổng tiền trên form tạo ──────────────────────────────────────
    private By totalOnForm    = By.xpath(
            "//*[contains(@class,'total') or contains(text(),'Tổng')]/following-sibling::* " +
                    "| //*[contains(@class,'total-amount') or contains(@class,'grand-total')]");

    // ── Lô sản phẩm (FEFO) ──────────────────────────────────────────
    private By firstBatchOption = By.xpath(
            "(//select[contains(@name,'batch') or contains(@name,'lo')] " +
                    "| //div[contains(@class,'batch')]//option)[1]");

    // ================================================================
    // Locators — Danh sách hóa đơn
    // ================================================================
    private By invoiceTableRows = By.xpath("//table//tbody//tr");
    private By searchInvoice    = By.xpath(
            "//input[contains(@placeholder,'Tìm') or contains(@placeholder,'mã') or contains(@placeholder,'hóa đơn')]");
    private By ddlStatusFilter  = By.xpath(
            "//select[contains(@name,'status') or contains(@class,'filter')] " +
                    "| //select[.//option[contains(text(),'Hoàn tất') or contains(text(),'Nháp')]]");
    private By dateFrom         = By.xpath("//input[@type='date'][1]");
    private By dateTo           = By.xpath("//input[@type='date'][2]");
    private By btnFilter        = By.xpath("//button[contains(.,'Lọc') or contains(.,'lọc') or contains(.,'Tìm kiếm')]");
    private By emptyMsg         = By.xpath(
            "//*[contains(text(),'Không tìm thấy') or contains(text(),'không tìm thấy') " +
                    "or contains(text(),'Không có') or contains(text(),'phù hợp')]");

    // ================================================================
    // Locators — Chi tiết hóa đơn
    // ================================================================
    private By btnCancelInvoice = By.xpath(
            "//button[contains(.,'Hủy hóa đơn') or contains(.,'Hủy')]");
    private By btnConfirmCancel = By.xpath(
            "//button[contains(.,'Xác nhận') or contains(.,'Có') or contains(.,'OK')]");
    private By btnDenyCancel    = By.xpath(
            "//button[contains(.,'Không') or contains(.,'Hủy bỏ') or contains(.,'Cancel')]");
    private By invoiceStatus    = By.xpath(
            "//*[contains(@class,'status') or contains(@class,'badge') or contains(@class,'tag')]");

    // ================================================================
    // Locators — Thông báo chung
    // ================================================================
    private By toastAny     = By.xpath(
            "//*[contains(@class,'toast') or contains(@class,'alert') " +
                    "or contains(@class,'notification') or contains(@class,'Toastify')]");
    private By toastSuccess = By.xpath(
            "//*[contains(text(),'thành công') or contains(text(),'Thành công') " +
                    "or contains(text(),'Hoàn tất')]");
    private By toastFail    = By.xpath(
            "//*[contains(text(),'thất bại') or contains(text(),'Thất bại') " +
                    "or contains(text(),'không thể') or contains(text(),'Không thể')]");

    // ================================================================
    // Navigation
    // ================================================================

    public void openCreateInvoicePage() {
        Constant.WEBDRIVER.get(Constant.URL + "/invoices/new");
        wait.until(ExpectedConditions.visibilityOfElementLocated(titleCreateInvoice));
        wait.until(ExpectedConditions.visibilityOfElementLocated(searchProduct));
    }

    public void openInvoiceListPage() {
        Constant.WEBDRIVER.get(Constant.URL + "/invoices");
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(invoiceTableRows),
                ExpectedConditions.visibilityOfElementLocated(emptyMsg)
        ));
    }

    /** Mở chi tiết hóa đơn đầu tiên trong danh sách */
    public void openFirstInvoiceDetail() {
        By firstRow = By.xpath("//table//tbody//tr[1]");
        wait.until(ExpectedConditions.elementToBeClickable(firstRow)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(invoiceStatus));
    }

    /** Mở chi tiết hóa đơn theo mã */
    public void openInvoiceByCode(String code) {
        By row = By.xpath("//table//tbody//tr[.//*[contains(text(),'" + code + "')]]");
        wait.until(ExpectedConditions.elementToBeClickable(row)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(invoiceStatus));
    }

    // ================================================================
    // Tạo hóa đơn
    // ================================================================

    public void searchAndSelectProduct(String productName) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(searchProduct));
        input.clear();
        input.sendKeys(productName);

        By option = By.xpath(
                "//li[contains(text(),'" + productName + "')] " +
                        "| //div[contains(@class,'option')][contains(text(),'" + productName + "')] " +
                        "| //td[contains(text(),'" + productName + "')]");
        wait.until(ExpectedConditions.elementToBeClickable(option)).click();
    }

    public void enterQuantity(String quantity) {
        WebElement qty = wait.until(ExpectedConditions.visibilityOfElementLocated(quantityInput));
        qty.clear();
        qty.sendKeys(quantity);
    }

    public void clickSaveDraft() {
        wait.until(ExpectedConditions.elementToBeClickable(btnSaveDraft)).click();
    }

    /** Lấy text lô đầu tiên được hiển thị (kiểm tra FEFO) */
    public String getFirstBatchText() {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(firstBatchOption));
            return el.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /** Lấy text tổng tiền trên form tạo hóa đơn */
    public String getTotalAmountText() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(totalOnForm)).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /** Kiểm tra tổng tiền > 0 */
    public boolean isTotalAmountPositive() {
        String text = getTotalAmountText();
        String numeric = text.replaceAll("[^0-9]", "");
        if (numeric.isEmpty()) return false;
        return Long.parseLong(numeric) > 0;
    }

    // ================================================================
    // Thanh toán
    // ================================================================

    public void chooseCashPayment() {
        wait.until(ExpectedConditions.elementToBeClickable(optCash)).click();
    }

    public void chooseTransferPayment() {
        wait.until(ExpectedConditions.elementToBeClickable(optTransfer)).click();
    }

    public void clickPay() {
        wait.until(ExpectedConditions.elementToBeClickable(btnPay)).click();
    }

    /** Kiểm tra nút Thanh toán có hiển thị không */
    public boolean isPayButtonDisplayed() {
        return !Constant.WEBDRIVER.findElements(btnPay).isEmpty();
    }

    // ================================================================
    // Kiểm tra kết quả chung
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

    public boolean isPaymentSuccess() {
        return isSuccessMessageDisplayed();
    }

    // ================================================================
    // Danh sách hóa đơn — Xem & Lọc
    // ================================================================

    public boolean hasInvoiceData() {
        try {
            List<WebElement> rows = Constant.WEBDRIVER.findElements(invoiceTableRows);
            return !rows.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEmptyMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(emptyMsg)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isInvoiceInList(String keyword) {
        By row = By.xpath("//table//tbody//tr[.//*[contains(text(),'" + keyword + "')]]");
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(row)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Tìm kiếm hóa đơn theo mã */
    public void searchInvoiceByCode(String code) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(searchInvoice));
        input.clear();
        input.sendKeys(code);
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    }

    /** Lọc theo trạng thái */
    public void filterByStatus(String statusText) {
        List<WebElement> selects = Constant.WEBDRIVER.findElements(ddlStatusFilter);
        if (!selects.isEmpty()) {
            new Select(selects.get(0)).selectByVisibleText(statusText);
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }
    }

    /** Lọc theo khoảng thời gian (yyyy-MM-dd) */
    public void filterByDateRange(String fromDate, String toDate) {
        List<WebElement> fromFields = Constant.WEBDRIVER.findElements(dateFrom);
        List<WebElement> toFields   = Constant.WEBDRIVER.findElements(dateTo);
        if (!fromFields.isEmpty()) {
            fromFields.get(0).clear();
            fromFields.get(0).sendKeys(fromDate);
        }
        if (!toFields.isEmpty()) {
            toFields.get(0).clear();
            toFields.get(0).sendKeys(toDate);
        }
    }

    public void clickFilter() {
        List<WebElement> btns = Constant.WEBDRIVER.findElements(btnFilter);
        if (!btns.isEmpty()) {
            btns.get(0).click();
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }
    }

    /** Kiểm tra chức năng lọc theo thời gian có tồn tại không */
    public boolean isDateFilterAvailable() {
        return !Constant.WEBDRIVER.findElements(dateFrom).isEmpty()
                && !Constant.WEBDRIVER.findElements(dateTo).isEmpty();
    }

    // ================================================================
    // Chi tiết hóa đơn — Trạng thái
    // ================================================================

    /** Lấy text trạng thái hóa đơn hiện tại */
    public String getInvoiceStatus() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(invoiceStatus))
                    .getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /** Kiểm tra trạng thái hóa đơn có chứa text mong đợi không */
    public boolean isInvoiceStatusEquals(String expectedStatus) {
        return getInvoiceStatus().contains(expectedStatus);
    }

    /** Kiểm tra thông tin thanh toán (ngày giờ + phương thức) có hiển thị không */
    public boolean isPaymentInfoDisplayed() {
        By paymentInfo = By.xpath(
                "//*[contains(text(),'Tiền mặt') or contains(text(),'Chuyển khoản') " +
                        "or contains(text(),'CK') or contains(@class,'payment-method')]");
        By paymentTime = By.xpath(
                "//*[contains(@class,'payment-time') or contains(@class,'paid-at') " +
                        "or contains(text(),'Thời gian') or contains(text(),'thanh toán')]");
        try {
            boolean hasMethod = !Constant.WEBDRIVER.findElements(paymentInfo).isEmpty();
            boolean hasTime   = !Constant.WEBDRIVER.findElements(paymentTime).isEmpty();
            return hasMethod && hasTime;
        } catch (Exception e) {
            return false;
        }
    }

    // ================================================================
    // Hủy hóa đơn
    // ================================================================

    /** Kiểm tra nút Hủy hóa đơn có hiển thị không */
    public boolean isCancelButtonDisplayed() {
        return !Constant.WEBDRIVER.findElements(btnCancelInvoice).isEmpty();
    }

    public void clickCancelInvoice() {
        wait.until(ExpectedConditions.elementToBeClickable(btnCancelInvoice)).click();
        // Chờ dialog xác nhận
        wait.until(ExpectedConditions.visibilityOfElementLocated(btnConfirmCancel));
    }

    public void confirmCancel() {
        wait.until(ExpectedConditions.elementToBeClickable(btnConfirmCancel)).click();
    }

    public void denyCancel() {
        wait.until(ExpectedConditions.elementToBeClickable(btnDenyCancel)).click();
    }

    // ================================================================
    // Tìm hóa đơn theo trạng thái trong danh sách
    // ================================================================

    /** Mở hóa đơn đầu tiên có trạng thái chứa statusText */
    public void openFirstInvoiceWithStatus(String statusText) {
        By row = By.xpath(
                "//table//tbody//tr[.//*[contains(text(),'" + statusText + "')]][1]");
        wait.until(ExpectedConditions.elementToBeClickable(row)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(invoiceStatus));
    }

    /** Kiểm tra tất cả dòng trong bảng đều có trạng thái chứa statusText */
    public boolean allRowsHaveStatus(String statusText) {
        List<WebElement> rows = Constant.WEBDRIVER.findElements(invoiceTableRows);
        if (rows.isEmpty()) return false;
        for (WebElement row : rows) {
            if (!row.getText().contains(statusText)) return false;
        }
        return true;
    }
}
