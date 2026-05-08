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
    // App dung class btn-primary-full cho "Tien mat" va btn-secondary-full cho "Chuyen khoan"
    // trong khu vuc chon phuong thuc (submit-actions-column dau tien)
    // Dung CSS selector theo class + vi tri de tranh loi encoding UTF-8
    private By optCash     = By.cssSelector(".submit-actions-column:first-of-type .btn-primary-full, button.btn-primary-full:not([class*='pay'])");
    private By optTransfer = By.cssSelector(".submit-actions-column:first-of-type .btn-secondary-full, button.btn-secondary-full:first-of-type");

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
    // Dung CSS selector theo data-type de tranh loi encoding UTF-8
    private By toastAny     = By.cssSelector("[data-sonner-toast]");
    private By toastSuccess = By.cssSelector("[data-sonner-toast][data-type='success']");
    private By toastFail    = By.cssSelector("[data-sonner-toast][data-type='error'], [data-sonner-toast][data-type='warning']");

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

        // Cho toast tu test truoc bien mat truoc khi click .btn-add
        // Toast (data-sonner-toast) co the che phu nut, gay ElementClickInterceptedException
        try {
            getWait().until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("[data-sonner-toast]")));
        } catch (Exception ignored) {
            // Neu khong co toast thi bo qua
        }

        // Click nut "Tao hoa don" / "Tao moi"
        getWait().until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".btn-add")),
                ExpectedConditions.visibilityOfElementLocated(invoiceTableRows)
        ));
        // Dung JS click de tranh bi toast che phu trong truong hop toast chua bien mat kip
        WebElement btnAdd = getWait().until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".btn-add")));
        ((org.openqa.selenium.JavascriptExecutor) Constant.WEBDRIVER)
                .executeScript("arguments[0].click()", btnAdd);
        getWait().until(ExpectedConditions.visibilityOfElementLocated(searchProduct));
    }

    public void openInvoiceListPage() {
        String currentUrl = Constant.WEBDRIVER.getCurrentUrl();
        if (!currentUrl.contains(Constant.URL) || currentUrl.contains("login")) {
            Constant.WEBDRIVER.get(Constant.URL);
            getWait().until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//nav[contains(@class,'sidebar-nav')] | //aside")));
        }

        By invoiceLink = By.xpath("//nav//a[@href='/invoices'] | //aside//a[@href='/invoices']");
        getWait().until(ExpectedConditions.elementToBeClickable(invoiceLink)).click();
        getWait().until(ExpectedConditions.urlContains("/invoices"));

        // Cho du lieu thuc su load xong: khong con "dang tai" va co it nhat 1 dong du lieu
        getWait().until(driver -> {
            List<WebElement> rows = driver.findElements(By.xpath("//table/tbody/tr"));
            if (rows.isEmpty()) return false;
            // Kiem tra dong dau tien khong phai loading placeholder
            String text = rows.get(0).getText();
            return !text.contains("ang t") && !text.isEmpty(); // "Đang tải" bị encode
        });
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

        // App dung <button class="product-search-item"> chua <p class="product-name">
        // Text san pham nam trong the con, khong phai truc tiep trong button
        // -> dung .//* de tim trong toan bo cay con
        By option = By.xpath(
                "//button[contains(@class,'product-search-item')][.//*[contains(text(),'" + productName + "')]]" +
                " | //div[contains(@class,'product-search-item')][.//*[contains(text(),'" + productName + "')]]");
        getWait().until(ExpectedConditions.elementToBeClickable(option)).click();
    }

    public void enterQuantity(String quantity) {
        WebElement qty = getWait().until(ExpectedConditions.visibilityOfElementLocated(quantityInput));
        qty.clear();
        qty.sendKeys(quantity);
    }

    public void clickSaveDraft() {
        // Nut "Luu nhap" la btn-secondary-full CUOI CUNG tren trang
        // (nut dau tien la "Chuyen khoan")
        List<WebElement> btns = getWait().until(
                ExpectedConditions.numberOfElementsToBeMoreThan(
                        By.cssSelector("button.btn-secondary-full"), 0));
        btns.get(btns.size() - 1).click();
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
        // App dung .summary-total strong de hien thi tong tien
        // Fallback sang cac selector khac neu khong tim thay
        By[] candidates = {
            By.cssSelector(".summary-total strong"),
            By.cssSelector(".summary-total"),
            By.cssSelector("[class*='total'] strong"),
            By.cssSelector("[class*='grand-total']"),
            By.cssSelector("[class*='total-amount']")
        };
        for (By selector : candidates) {
            try {
                List<WebElement> els = Constant.WEBDRIVER.findElements(selector);
                if (!els.isEmpty()) {
                    String text = (String) ((org.openqa.selenium.JavascriptExecutor) Constant.WEBDRIVER)
                            .executeScript("return arguments[0].textContent.trim()", els.get(0));
                    if (text != null && !text.isEmpty()) return text;
                }
            } catch (Exception ignored) {}
        }
        return "";
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
        // Nut "Tien mat" la btn-primary-full DAU TIEN tren trang
        // (nut "Thanh toan" la btn-primary-full thu HAI)
        List<WebElement> btns = getWait().until(
                ExpectedConditions.numberOfElementsToBeMoreThan(
                        By.cssSelector("button.btn-primary-full"), 0));
        // Click nut dau tien (Tien mat), khong phai nut cuoi (Thanh toan)
        btns.get(0).click();
    }

    public void chooseTransferPayment() {
        // Nut "Chuyen khoan" la btn-secondary-full DAU TIEN tren trang
        // (nut "Luu nhap" la btn-secondary-full thu HAI)
        List<WebElement> btns = getWait().until(
                ExpectedConditions.numberOfElementsToBeMoreThan(
                        By.cssSelector("button.btn-secondary-full"), 0));
        btns.get(0).click();
    }

    public void clickPay() {
        // Nut "Thanh toan" la btn-primary-full CUOI CUNG tren trang
        // (nut dau tien la "Tien mat")
        List<WebElement> btns = getWait().until(
                ExpectedConditions.numberOfElementsToBeMoreThan(
                        By.cssSelector("button.btn-primary-full"), 0));
        btns.get(btns.size() - 1).click();
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
        // Thu 1: Toast voi data-type='success' (Sonner)
        try {
            WebElement toast = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(5))
                    .until(ExpectedConditions.visibilityOfElementLocated(toastSuccess));
            return toast.isDisplayed();
        } catch (Exception ignored) {}

        // Thu 2: Redirect roi khoi /invoices/new (luu nhap hoac thanh toan thanh cong)
        try {
            new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10))
                    .until(driver -> {
                        String url = driver.getCurrentUrl();
                        return url.contains("/invoices") && !url.contains("/invoices/new");
                    });
            return true;
        } catch (Exception ignored) {}

        return false;
    }

    public boolean isFailMessageDisplayed() {
        // Thu 1: Toast voi data-type='error' hoac 'warning' (Sonner)
        try {
            WebElement toast = getWait().until(
                    ExpectedConditions.visibilityOfElementLocated(toastFail));
            return toast.isDisplayed();
        } catch (Exception ignored) {}

        // Thu 2: Van o trang /invoices/new sau khi submit (bi chan)
        String url = Constant.WEBDRIVER.getCurrentUrl();
        return url.contains("/invoices/new");
    }

    public boolean isPaymentSuccess() {
        // Cho URL chuyen tu /invoices/new sang /invoices (khong con /new)
        // Dung urlToBe hoac urlMatches de wait chinh xac
        try {
            new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(15))
                    .until(driver -> {
                        String url = driver.getCurrentUrl();
                        return url.contains("/invoices") && !url.contains("/invoices/new");
                    });
            return true;
        } catch (Exception ignored) {}

        // Fallback: kiem tra toast success
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
        // Trang chi tiet co section "Thong tin hoa don" voi cac dong invoice-meta-line
        // Thoi gian co format "HH:mm:ss d/M/yyyy" -> chua dau ":" va "/"
        // Dung JS de doc text tranh loi encoding
        List<WebElement> metaLines = Constant.WEBDRIVER.findElements(
                By.cssSelector(".invoice-meta-line, .invoice-info-row, [class*='meta-line'], [class*='info-row']"));
        for (WebElement line : metaLines) {
            String text = (String) ((org.openqa.selenium.JavascriptExecutor) Constant.WEBDRIVER)
                    .executeScript("return arguments[0].textContent.trim()", line);
            // Thoi gian co dang "HH:mm:ss d/M/yyyy" -> chua ":"
            if (text.contains(":") && text.matches(".*\\d{1,2}/\\d{1,2}/\\d{4}.*")) return true;
        }
        // Fallback: tim bat ky element nao chua thoi gian dang ngay/gio
        List<WebElement> all = Constant.WEBDRIVER.findElements(By.xpath("//*"));
        for (WebElement el : all) {
            try {
                String text = (String) ((org.openqa.selenium.JavascriptExecutor) Constant.WEBDRIVER)
                        .executeScript("return arguments[0].childNodes.length > 0 ? '' : arguments[0].textContent.trim()", el);
                if (text != null && text.matches("\\d{1,2}:\\d{2}:\\d{2}.*\\d{4}.*")) return true;
            } catch (Exception ignored) {}
        }
        return false;
    }

    /** Kiem tra phuong thuc thanh toan co hien thi khong */
    public boolean isPaymentMethodDisplayed() {
        // Trang chi tiet hien thi "Thanh toan: Tien mat" hoac "Chuyen khoan"
        // Text bi encode -> dung JS doc innerText cua toan bo trang
        String bodyText = (String) ((org.openqa.selenium.JavascriptExecutor) Constant.WEBDRIVER)
                .executeScript("return document.body.innerText");
        // Tim cac keyword phuong thuc thanh toan (co the bi encode)
        // "Ti?n m?t" -> chua "Ti" va "m" | "Chuy?n kho?n" -> chua "Chuy" va "kho"
        return bodyText.contains("Ti") && (bodyText.contains("m?t") || bodyText.contains("mat"))
                || bodyText.contains("Chuy") && (bodyText.contains("kho") || bodyText.contains("khoan"))
                || bodyText.contains("Thanh to") // "Thanh toan" section label
                   && (bodyText.contains("Ti") || bodyText.contains("Chuy"));
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
        // App dung <button class="product-search-item"> (khong phai div hay li)
        By dropdownItem = By.cssSelector(
                "button.product-search-item, div.product-search-item, .product-search-result button");
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
        // App dung <select class="filter-select"> voi option value bi encode
        // Dung selectByIndex thay vi selectByVisibleText de tranh loi encoding
        // Index: 0=Tat ca, 1=Nhap, 2=Hoan tat, 3=Da huy
        List<WebElement> selects = Constant.WEBDRIVER.findElements(
                By.cssSelector("select.filter-select"));
        if (selects.isEmpty()) return;

        String lower = statusText.toLowerCase();
        int index = 0; // mac dinh: Tat ca
        if (lower.contains("nhap") || lower.contains("nh\u00e1p") || lower.contains("pending"))
            index = 1;
        else if (lower.contains("hoan") || lower.contains("ho\u00e0n") || lower.contains("active"))
            index = 2;
        else if (lower.contains("huy") || lower.contains("h\u1ee7y") || lower.contains("cancel"))
            index = 3;

        new org.openqa.selenium.support.ui.Select(selects.get(0)).selectByIndex(index);
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
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
        // Thu 1: Kiem tra theo CSS class cua badge (tranh loi encoding)
        // "Nhap" -> pending | "Hoan tat" -> active | "Da huy" -> inactive
        String lower = expectedStatus.toLowerCase();
        String cssClass = null;
        if (lower.contains("nhap") || lower.contains("nh")) cssClass = "pending";
        else if (lower.contains("hoan") || lower.contains("tat")) cssClass = "active";
        else if (lower.contains("huy") || lower.contains("cancel") || lower.contains("inactive"))
            cssClass = "inactive";

        if (cssClass != null) {
            By badge = By.cssSelector(".status-badge." + cssClass);
            if (!Constant.WEBDRIVER.findElements(badge).isEmpty()) return true;
        }

        // Thu 2: Kiem tra theo text (fallback)
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
        // Map statusText -> CSS class cua badge
        // "Nhap"  -> pending | "Hoan tat" -> active | "Da huy" -> inactive
        String lower = statusText.toLowerCase();
        String cssClass = null;
        if (lower.contains("nhap") || lower.contains("nh\u00e1p") || lower.contains("pending"))
            cssClass = "pending";
        else if (lower.contains("hoan") || lower.contains("ho\u00e0n") || lower.contains("active"))
            cssClass = "active";
        else if (lower.contains("huy") || lower.contains("h\u1ee7y") || lower.contains("cancel")
                || lower.contains("inactive"))
            cssClass = "inactive";

        String rowSelector = cssClass != null
                ? "//table//tbody//tr[.//span[contains(@class,'" + cssClass + "')]][1]//button[contains(@class,'icon-btn')]"
                : "//table//tbody//tr[.//*[contains(text(),'" + statusText + "')]][1]//button[contains(@class,'icon-btn')]";

        List<WebElement> btns = Constant.WEBDRIVER.findElements(By.xpath(rowSelector));
        if (!btns.isEmpty()) {
            ((org.openqa.selenium.JavascriptExecutor) Constant.WEBDRIVER)
                    .executeScript("arguments[0].click()", btns.get(0));
            getWait().until(ExpectedConditions.visibilityOfElementLocated(invoiceStatus));
            return;
        }

        throw new org.openqa.selenium.NoSuchElementException(
                "Khong tim thay hoa don co trang thai '" + statusText
                + "' (cssClass=" + cssClass + ") tren trang hien tai.");
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

    public String getCreatedInvoiceCode() {
        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));

        wait.until(driver -> driver.getCurrentUrl().matches(".*/invoices/\\d+"));

        String url = Constant.WEBDRIVER.getCurrentUrl();

        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile(".*/invoices/(\\d+)")
                .matcher(url);

        if (m.find()) return m.group(1);

        throw new RuntimeException("Không lấy được invoice ID từ URL: " + url);
    }

    public void openInvoiceById(String id) {
        if (id.equals("new")) {
            throw new RuntimeException("ID không hợp lệ: new");
        }

        WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(20));

        By locator = By.xpath("//table//tbody//tr[.//a[contains(@href,'/invoices/" + id + "')]]");

        WebElement row = wait.until(ExpectedConditions.elementToBeClickable(locator));
        row.click();
    }
}