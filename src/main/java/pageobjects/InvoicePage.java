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

    // Khoi tao lazy de tranh capture WEBDRIVER=null tai thoi diem new InvoicePage()
    private WebDriverWait getWait() {
        return new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(20));
    }

    // ================================================================
    // Locators -- Tao hoa don
    // ================================================================
    private By titleCreateInvoice = By.xpath(
            "//*[contains(text(),'T\u1ea1o h\u00f3a \u0111\u01a1n') or contains(text(),'T\u1ea1o h\u00f3a \u0111\u01a1n b\u00e1n h\u00e0ng')]");
    private By searchProduct  = By.xpath(
            "//input[contains(@placeholder,'T\u00ecm') or contains(@placeholder,'t\u00ecm') or contains(@placeholder,'s\u1ea3n ph\u1ea9m')]");
    private By quantityInput  = By.xpath("//input[@type='number'][last()]");
    private By btnSaveDraft   = By.xpath("//button[contains(.,'L\u01b0u nh\u00e1p') or contains(.,'l\u01b0u nh\u00e1p')]");
    private By btnPay         = By.xpath("//button[contains(.,'Thanh to\u00e1n')]");

    // -- Phuong thuc thanh toan
    private By optCash        = By.xpath("//*[contains(text(),'Ti\u1ec1n m\u1eb7t')]");
    private By optTransfer    = By.xpath("//*[contains(text(),'Chuy\u1ec3n kho\u1ea3n')]");

    // -- Tong tien tren form tao
    private By totalOnForm    = By.xpath(
            "//*[contains(@class,'total') or contains(text(),'T\u1ed5ng')]/following-sibling::* " +
                    "| //*[contains(@class,'total-amount') or contains(@class,'grand-total')]");

    // -- Lo san pham (FEFO)
    private By firstBatchOption = By.xpath(
            "(//select[contains(@name,'batch') or contains(@name,'lo')] " +
                    "| //div[contains(@class,'batch')]//option)[1]");

    // ================================================================
    // Locators -- Danh sach hoa don
    // ================================================================
    private By invoiceTableRows = By.xpath("//table//tbody//tr");
    private By searchInvoice    = By.xpath(
            "//input[contains(@placeholder,'T\u00ecm') or contains(@placeholder,'m\u00e3') or contains(@placeholder,'h\u00f3a \u0111\u01a1n')]");
    private By ddlStatusFilter  = By.xpath(
            "//select[contains(@name,'status') or contains(@class,'filter')] " +
                    "| //select[.//option[contains(text(),'Ho\u00e0n t\u1ea5t') or contains(text(),'Nh\u00e1p')]]");
    private By dateFrom         = By.xpath("//input[@type='date'][1]");
    private By dateTo           = By.xpath("//input[@type='date'][2]");
    private By btnFilter        = By.xpath("//button[contains(.,'L\u1ecdc') or contains(.,'l\u1ecdc') or contains(.,'T\u00ecm ki\u1ebfm')]");
    private By emptyMsg         = By.xpath(
            "//*[contains(text(),'Kh\u00f4ng t\u00ecm th\u1ea5y') or contains(text(),'kh\u00f4ng t\u00ecm th\u1ea5y') " +
                    "or contains(text(),'Kh\u00f4ng c\u00f3') or contains(text(),'ph\u00f9 h\u1ee3p')]");

    // ================================================================
    // Locators -- Chi tiet hoa don
    // ================================================================
    private By btnCancelInvoice = By.xpath(
            "//button[contains(.,'H\u1ee7y h\u00f3a \u0111\u01a1n') or contains(.,'H\u1ee7y')]");
    private By btnConfirmCancel = By.xpath(
            "//button[contains(.,'X\u00e1c nh\u1eadn') or contains(.,'C\u00f3') or contains(.,'OK')]");
    private By btnDenyCancel    = By.xpath(
            "//button[contains(.,'Kh\u00f4ng') or contains(.,'H\u1ee7y b\u1ecf') or contains(.,'Cancel')]");
    private By invoiceStatus    = By.xpath(
            "//*[contains(@class,'status') or contains(@class,'badge') or contains(@class,'tag')]");

    // ================================================================
    // Locators -- Thong bao chung
    // ================================================================
    private By toastAny     = By.xpath(
            "//*[contains(@class,'toast') or contains(@class,'alert') " +
                    "or contains(@class,'notification') or contains(@class,'Toastify')]");
    private By toastSuccess = By.xpath(
            "//*[contains(text(),'th\u00e0nh c\u00f4ng') or contains(text(),'Th\u00e0nh c\u00f4ng') " +
                    "or contains(text(),'Ho\u00e0n t\u1ea5t')]");
    private By toastFail    = By.xpath(
            "//*[contains(text(),'th\u1ea5t b\u1ea1i') or contains(text(),'Th\u1ea5t b\u1ea1i') " +
                    "or contains(text(),'kh\u00f4ng th\u1ec3') or contains(text(),'Kh\u00f4ng th\u1ec3')]");

    // ================================================================
    // Navigation
    // ================================================================

    public void openCreateInvoicePage() {
        // Khong dung driver.get() vi Vercel SPA tra 404 khi truy cap route truc tiep
        // Phai navigate qua sidebar truoc, sau do click nut tao moi
        String currentUrl = Constant.WEBDRIVER.getCurrentUrl();

        // Neu chua o trang chinh thi ve trang chu truoc
        if (!currentUrl.contains(Constant.URL) || currentUrl.contains("login")) {
            Constant.WEBDRIVER.get(Constant.URL);
            getWait().until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//nav[contains(@class,'sidebar-nav')] | //aside")));
        }

        // Neu chua o trang /invoices thi click sidebar link
        if (!Constant.WEBDRIVER.getCurrentUrl().contains("/invoices")) {
            By invoiceLink = By.xpath("//nav//a[@href='/invoices'] | //aside//a[@href='/invoices']");
            getWait().until(ExpectedConditions.elementToBeClickable(invoiceLink)).click();
            getWait().until(ExpectedConditions.urlContains("/invoices"));
        }

        // Click nut "Tao hoa don" / "Tao moi"
        getWait().until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".btn-add")),
                ExpectedConditions.visibilityOfElementLocated(invoiceTableRows)
        ));
        getWait().until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-add"))).click();
        getWait().until(ExpectedConditions.visibilityOfElementLocated(searchProduct));
    }

    public void openInvoiceListPage() {
        // Khong dung driver.get() vi Vercel SPA tra 404 khi truy cap route truc tiep
        // Phai navigate qua sidebar link "Ban hang" -> /invoices
        String currentUrl = Constant.WEBDRIVER.getCurrentUrl();

        // Neu chua o trang chinh thi ve trang chu truoc
        if (!currentUrl.contains(Constant.URL) || currentUrl.contains("login")) {
            Constant.WEBDRIVER.get(Constant.URL);
            getWait().until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//nav[contains(@class,'sidebar-nav')] | //aside")));
        }

        // Click link "Ban hang" tren sidebar (href="/invoices")
        By invoiceLink = By.xpath("//nav//a[@href='/invoices'] | //aside//a[@href='/invoices']");
        getWait().until(ExpectedConditions.elementToBeClickable(invoiceLink)).click();

        // Cho URL chuyen sang /invoices va noi dung load xong
        getWait().until(ExpectedConditions.urlContains("/invoices"));
        getWait().until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(invoiceTableRows),
                ExpectedConditions.visibilityOfElementLocated(emptyMsg),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".btn-add"))
        ));
    }

    /** Mo chi tiet hoa don dau tien trong danh sach */
    public void openFirstInvoiceDetail() {
        By firstRow = By.xpath("//table//tbody//tr[1]");
        getWait().until(ExpectedConditions.elementToBeClickable(firstRow)).click();
        getWait().until(ExpectedConditions.visibilityOfElementLocated(invoiceStatus));
    }

    /** Mo chi tiet hoa don theo ma */
    public void openInvoiceByCode(String code) {
        By row = By.xpath("//table//tbody//tr[.//*[contains(text(),'" + code + "')]]");
        getWait().until(ExpectedConditions.elementToBeClickable(row)).click();
        getWait().until(ExpectedConditions.visibilityOfElementLocated(invoiceStatus));
    }

    // ================================================================
    // Tao hoa don
    // ================================================================

    public void searchAndSelectProduct(String productName) {
        WebElement input = getWait().until(ExpectedConditions.visibilityOfElementLocated(searchProduct));
        input.clear();
        input.sendKeys(productName);

        By option = By.xpath(
                "//li[contains(text(),'" + productName + "')] " +
                        "| //div[contains(@class,'option')][contains(text(),'" + productName + "')] " +
                        "| //td[contains(text(),'" + productName + "')]");
        getWait().until(ExpectedConditions.elementToBeClickable(option)).click();
    }

    public void enterQuantity(String quantity) {
        WebElement qty = getWait().until(ExpectedConditions.visibilityOfElementLocated(quantityInput));
        qty.clear();
        qty.sendKeys(quantity);
    }

    public void clickSaveDraft() {
        getWait().until(ExpectedConditions.elementToBeClickable(btnSaveDraft)).click();
    }

    /** Lay text lo dau tien duoc hien thi (kiem tra FEFO) */
    public String getFirstBatchText() {
        try {
            WebElement el = getWait().until(ExpectedConditions.visibilityOfElementLocated(firstBatchOption));
            return el.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /** Lay text tong tien tren form tao hoa don */
    public String getTotalAmountText() {
        try {
            return getWait().until(ExpectedConditions.visibilityOfElementLocated(totalOnForm)).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /** Kiem tra tong tien > 0 */
    public boolean isTotalAmountPositive() {
        String text = getTotalAmountText();
        String numeric = text.replaceAll("[^0-9]", "");
        if (numeric.isEmpty()) return false;
        return Long.parseLong(numeric) > 0;
    }

    // ================================================================
    // Thanh toan
    // ================================================================

    public void chooseCashPayment() {
        getWait().until(ExpectedConditions.elementToBeClickable(optCash)).click();
    }

    public void chooseTransferPayment() {
        getWait().until(ExpectedConditions.elementToBeClickable(optTransfer)).click();
    }

    public void clickPay() {
        getWait().until(ExpectedConditions.elementToBeClickable(btnPay)).click();
    }

    /** Kiem tra nut Thanh toan co hien thi khong */
    public boolean isPayButtonDisplayed() {
        return !Constant.WEBDRIVER.findElements(btnPay).isEmpty();
    }

    // ================================================================
    // Kiem tra ket qua chung
    // ================================================================

    public String getToastMessage() {
        return getWait().until(ExpectedConditions.visibilityOfElementLocated(toastAny)).getText();
    }

    public boolean isSuccessMessageDisplayed() {
        try {
            return getWait().until(ExpectedConditions.visibilityOfElementLocated(toastSuccess)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isFailMessageDisplayed() {
        try {
            return getWait().until(ExpectedConditions.visibilityOfElementLocated(toastFail)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPaymentSuccess() {
        return isSuccessMessageDisplayed();
    }

    // ================================================================
    // Danh sach hoa don -- Xem & Loc
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
            return getWait().until(ExpectedConditions.visibilityOfElementLocated(emptyMsg)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isInvoiceInList(String keyword) {
        By row = By.xpath("//table//tbody//tr[.//*[contains(text(),'" + keyword + "')]]");
        try {
            return getWait().until(ExpectedConditions.visibilityOfElementLocated(row)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Kiem tra phuong thuc thanh toan duoc luu co khop voi ky vong */
    public boolean isPaymentMethodSaved(String expectedMethod) {
        By methodEl = By.xpath(
                "//*[contains(text(),'" + expectedMethod + "') or contains(@class,'payment-method')]");
        return !Constant.WEBDRIVER.findElements(methodEl).isEmpty();
    }

    /** Kiem tra thoi gian thanh toan co hien thi khong */
    public boolean isPaymentTimeDisplayed() {
        By timeEl = By.xpath(
                "//*[contains(@class,'payment-time') or contains(@class,'paid-at') " +
                "or contains(text(),'Thoi gian') or contains(text(),'thanh toan') " +
                "or contains(text(),'/202') or contains(text(),':')]");
        return !Constant.WEBDRIVER.findElements(timeEl).isEmpty();
    }

    /** Kiem tra phuong thuc thanh toan co hien thi khong */
    public boolean isPaymentMethodDisplayed() {
        By methodEl = By.xpath(
                "//*[contains(text(),'Tien mat') or contains(text(),'Chuyen khoan') " +
                "or contains(text(),'CK') or contains(@class,'payment-method')]");
        return !Constant.WEBDRIVER.findElements(methodEl).isEmpty();
    }

    /** Kiem tra co thong tin san pham trong chi tiet hoa don */
    public boolean hasProductDetailDisplayed() {
        By productRow = By.xpath(
                "//table//tbody//tr | //div[contains(@class,'invoice-item') or contains(@class,'product-row')]");
        return !Constant.WEBDRIVER.findElements(productRow).isEmpty();
    }

    /** Kiem tra moi dong trong bang deu co badge trang thai */
    public boolean allRowsHaveStatusBadge() {
        List<WebElement> rows = Constant.WEBDRIVER.findElements(invoiceTableRows);
        if (rows.isEmpty()) return false;
        for (WebElement row : rows) {
            List<WebElement> badges = row.findElements(
                    By.xpath(".//*[contains(@class,'status') or contains(@class,'badge') or contains(@class,'tag')]"));
            if (badges.isEmpty()) return false;
        }
        return true;
    }

    /** Kiem tra co hien thi phan trang khong */
    public boolean isPaginationDisplayed() {
        By pagination = By.xpath(
                "//*[contains(@class,'pagination') or contains(@class,'paging')] " +
                "| //button[contains(.,'Trang') or contains(.,'Next') or contains(.,'>')] " +
                "| //a[contains(@aria-label,'page') or contains(@aria-label,'next')]");
        return !Constant.WEBDRIVER.findElements(pagination).isEmpty();
    }

    /** Dem so dong hoa don hien tai trong bang */
    public int getInvoiceRowCount() {
        return Constant.WEBDRIVER.findElements(invoiceTableRows).size();
    }

    /** Chuyen sang trang tiep theo */
    public void goToNextPage() {
        By nextBtn = By.xpath(
                "//button[contains(.,'Next') or contains(.,'>')] " +
                "| //a[contains(@aria-label,'next') or contains(@aria-label,'Next')] " +
                "| //*[contains(@class,'next')]");
        getWait().until(ExpectedConditions.elementToBeClickable(nextBtn)).click();
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    }

    /** Kiem tra lich su huy co hien thi trong chi tiet hoa don */
    public boolean isCancelHistoryDisplayed() {
        By historyEl = By.xpath(
                "//*[contains(text(),'huy') or contains(text(),'Huy') or contains(text(),'cancel') " +
                "or contains(@class,'history') or contains(@class,'log') or contains(@class,'timeline')]");
        return !Constant.WEBDRIVER.findElements(historyEl).isEmpty();
    }
    public void searchProductOnly(String productName) {
        WebElement input = getWait().until(ExpectedConditions.visibilityOfElementLocated(searchProduct));
        input.clear();
        input.sendKeys(productName);
        // Cho debounce search (~1s)
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
    }

    /** Kiem tra co ket qua trong dropdown tim kiem khong */
    public boolean hasSearchResults() {
        By dropdownItem = By.xpath(
                "//li[contains(@class,'option') or contains(@class,'item') or contains(@class,'result')] " +
                "| //div[contains(@class,'product-search-item')] " +
                "| //div[contains(@class,'option')]");
        return !Constant.WEBDRIVER.findElements(dropdownItem).isEmpty();
    }

    /** Chon item dau tien trong dropdown tim kiem */
    public void selectFirstSearchResult() {
        By firstItem = By.xpath(
                "(//li[contains(@class,'option') or contains(@class,'item')] " +
                "| //div[contains(@class,'product-search-item')] " +
                "| //div[contains(@class,'option')])[1]");
        getWait().until(ExpectedConditions.elementToBeClickable(firstItem)).click();
    }

    /** Kiem tra co san pham nao trong hoa don chua (co card/dong san pham) */
    public boolean hasProductInInvoice() {
        By productCard = By.xpath(
                "//div[contains(@class,'import-item-card') or contains(@class,'invoice-item') or contains(@class,'product-row')] " +
                "| //table[contains(@class,'invoice')]//tbody//tr");
        return !Constant.WEBDRIVER.findElements(productCard).isEmpty();
    }

    /** Kiem tra o tim kiem co dang active (enabled va khong readonly) */
    public boolean isSearchInputActive() {
        try {
            WebElement input = Constant.WEBDRIVER.findElement(searchProduct);
            return input.isEnabled() && input.getAttribute("readonly") == null;
        } catch (Exception e) {
            return false;
        }
    }

    /** Doc gia tri max cua input so luong (ton kho thuc te) */
    public int getMaxQuantityFromInput() {
        By qtyInput = By.xpath(
                "//div[contains(@class,'import-item-card') or contains(@class,'invoice-item')][1]" +
                "//input[@type='number' or contains(@class,'qty') or contains(@name,'quantity')]");
        try {
            WebElement el = getWait().until(ExpectedConditions.visibilityOfElementLocated(qtyInput));
            String maxAttr = el.getAttribute("max");
            if (maxAttr != null && !maxAttr.isEmpty()) return Integer.parseInt(maxAttr);
        } catch (Exception ignored) {}
        return 0;
    }

    /** Kiem tra nut Thanh toan co bi disabled khong */
    public boolean isPayButtonDisabled() {
        List<WebElement> btns = Constant.WEBDRIVER.findElements(btnPay);
        if (btns.isEmpty()) return true;
        WebElement btn = btns.get(0);
        return !btn.isEnabled()
                || "true".equals(btn.getAttribute("disabled"))
                || (btn.getAttribute("class") != null && btn.getAttribute("class").contains("disabled"));
    }

    /** Kiem tra input don gia co bi disabled khong (bao ve gia he thong) */
    public boolean isPriceInputDisabled() {
        By priceInput = By.xpath(
                "//div[contains(@class,'import-item-card') or contains(@class,'invoice-item')][1]" +
                "//input[@type='number'][2] " +
                "| //input[contains(@name,'price') or contains(@name,'gia') or contains(@class,'price')]");
        try {
            List<WebElement> els = Constant.WEBDRIVER.findElements(priceInput);
            if (els.isEmpty()) return true; // khong hien thi field gia -> coi nhu khong cho sua
            WebElement el = els.get(0);
            return !el.isEnabled() || "true".equals(el.getAttribute("disabled"));
        } catch (Exception e) {
            return true;
        }
    }

    /** Tim kiem hoa don theo ma */
    public void searchInvoiceByCode(String code) {
        WebElement input = getWait().until(ExpectedConditions.visibilityOfElementLocated(searchInvoice));
        input.clear();
        input.sendKeys(code);
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    }

    /** Loc theo trang thai */
    public void filterByStatus(String statusText) {
        List<WebElement> selects = Constant.WEBDRIVER.findElements(ddlStatusFilter);
        if (!selects.isEmpty()) {
            new Select(selects.get(0)).selectByVisibleText(statusText);
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }
    }

    /** Loc theo khoang thoi gian (yyyy-MM-dd) */
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

    /** Kiem tra chuc nang loc theo thoi gian co ton tai khong */
    public boolean isDateFilterAvailable() {
        return !Constant.WEBDRIVER.findElements(dateFrom).isEmpty()
                && !Constant.WEBDRIVER.findElements(dateTo).isEmpty();
    }

    // ================================================================
    // Chi tiet hoa don -- Trang thai
    // ================================================================

    /** Lay text trang thai hoa don hien tai */
    public String getInvoiceStatus() {
        try {
            return getWait().until(ExpectedConditions.visibilityOfElementLocated(invoiceStatus))
                    .getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /** Kiem tra trang thai hoa don co chua text mong doi khong */
    public boolean isInvoiceStatusEquals(String expectedStatus) {
        return getInvoiceStatus().contains(expectedStatus);
    }

    /** Kiem tra thong tin thanh toan (ngay gio + phuong thuc) co hien thi khong */
    public boolean isPaymentInfoDisplayed() {
        By paymentInfo = By.xpath(
                "//*[contains(text(),'Ti\u1ec1n m\u1eb7t') or contains(text(),'Chuy\u1ec3n kho\u1ea3n') " +
                        "or contains(text(),'CK') or contains(@class,'payment-method')]");
        By paymentTime = By.xpath(
                "//*[contains(@class,'payment-time') or contains(@class,'paid-at') " +
                        "or contains(text(),'Th\u1eddi gian') or contains(text(),'thanh to\u00e1n')]");
        try {
            boolean hasMethod = !Constant.WEBDRIVER.findElements(paymentInfo).isEmpty();
            boolean hasTime   = !Constant.WEBDRIVER.findElements(paymentTime).isEmpty();
            return hasMethod && hasTime;
        } catch (Exception e) {
            return false;
        }
    }

    // ================================================================
    // Huy hoa don
    // ================================================================

    /** Kiem tra nut Huy hoa don co hien thi khong */
    public boolean isCancelButtonDisplayed() {
        return !Constant.WEBDRIVER.findElements(btnCancelInvoice).isEmpty();
    }

    public void clickCancelInvoice() {
        getWait().until(ExpectedConditions.elementToBeClickable(btnCancelInvoice)).click();
        // Cho dialog xac nhan
        getWait().until(ExpectedConditions.visibilityOfElementLocated(btnConfirmCancel));
    }

    public void confirmCancel() {
        getWait().until(ExpectedConditions.elementToBeClickable(btnConfirmCancel)).click();
    }

    public void denyCancel() {
        getWait().until(ExpectedConditions.elementToBeClickable(btnDenyCancel)).click();
    }

    // ================================================================
    // Tim hoa don theo trang thai trong danh sach
    // ================================================================

    /** Mo hoa don dau tien co trang thai chua statusText */
    public void openFirstInvoiceWithStatus(String statusText) {
        By row = By.xpath(
                "//table//tbody//tr[.//*[contains(text(),'" + statusText + "')]][1]");
        getWait().until(ExpectedConditions.elementToBeClickable(row)).click();
        getWait().until(ExpectedConditions.visibilityOfElementLocated(invoiceStatus));
    }

    /** Kiem tra tat ca dong trong bang deu co trang thai chua statusText */
    public boolean allRowsHaveStatus(String statusText) {
        List<WebElement> rows = Constant.WEBDRIVER.findElements(invoiceTableRows);
        if (rows.isEmpty()) return false;
        for (WebElement row : rows) {
            if (!row.getText().contains(statusText)) return false;
        }
        return true;
    }
}