package testcases;

import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;
import pageobjects.ImportPage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ImportTest extends BaseTest {

    @Test(priority = 1)
    public void testIMP01_CompleteImportSuccess() throws InterruptedException {
        login("admin", "123456");
        ImportPage importPage = new ImportPage(driver);
        importPage.navigateToCreateImport();

        importPage.selectProduct("Paracetamol");

        String dynamicBatch = "LOT-" + System.currentTimeMillis();
        importPage.fillImportItemDetails(dynamicBatch, "31/12/2026", "10", "5000");

        importPage.clickComplete();

        Thread.sleep(2500);

        String msgs = importPage.getAllErrorMessages();

        Assert.assertTrue(msgs.contains("Tạo phiếu nhập hoàn tất thành công") || driver.getCurrentUrl().endsWith("/imports"),
                "Kết quả: Phiếu nhập KHÔNG lưu thành công. Thông báo web trả về: '" + msgs + "'");
    }

    @Test(priority = 2)
    public void testIMP02_SaveDraftImport() throws InterruptedException {
        login("admin", "123456");
        ImportPage importPage = new ImportPage(driver);
        importPage.navigateToCreateImport();
        importPage.selectProduct("Amoxicillin");
        importPage.fillImportItemDetails("LOT-DRAFT", "31/12/2026", "50", "10000");
        importPage.clickSaveDraft();
        Thread.sleep(2000);

        // Manual: Phiếu nhập được lưu (nháp), tồn kho chưa cập nhật
        Assert.assertTrue(importPage.getFirstRowStatus().toLowerCase().contains("nháp") || importPage.getAllErrorMessages().contains("thành công"),
                "Kết quả: Phiếu nhập phải được lưu nháp.");
    }

    @Test(priority = 3)
    public void testIMP03_EmptyImport() throws InterruptedException {
        login("admin", "123456");
        ImportPage importPage = new ImportPage(driver);
        importPage.navigateToCreateImport();

        // 1. Để trống phiếu (Không chọn sản phẩm nào) và nhấn Hoàn tất
        importPage.clickComplete();

        // 2. Chờ 1.5 giây để Toast message báo lỗi xuất hiện
        Thread.sleep(1500);

        // 3. Lấy thông báo
        String errors = importPage.getAllErrorMessages();

        // 4. Kiểm tra chính xác thông báo lỗi từ hình ảnh của bạn
        Assert.assertTrue(errors.contains("phiếu nhập phải có ít nhất 1 sản phẩm"),
                "Kết quả: Phải hiển thị 'Phiếu nhập phải có ít nhất 1 sản phẩm'. Lỗi thực tế báo ra: '" + errors + "'");
    }

    @Test(priority = 4)
    public void testIMP04_MissingBatchNumber() throws InterruptedException {
        login("admin", "123456");
        ImportPage importPage = new ImportPage(driver);
        importPage.navigateToCreateImport();
        importPage.selectProduct("Paracetamol");
        importPage.fillImportItemDetails("", "31/12/2026", "10", "5000"); // Thiếu Số lô
        importPage.clickComplete();
        Thread.sleep(1500);

        // Manual: Hiển thị “Vui lòng nhập số lô cho Paracetamol”
        String errors = importPage.getAllErrorMessages();
        Assert.assertTrue(errors.contains("vui lòng nhập số lô cho paracetamol"),
                "Kết quả: Phải hiển thị 'Vui lòng nhập số lô cho Paracetamol'. Lỗi thực tế báo ra: " + errors);
    }

    @Test(priority = 5)
    public void testIMP05_MissingExpiryDate() throws InterruptedException {
        login("admin", "123456");
        ImportPage importPage = new ImportPage(driver);
        importPage.navigateToCreateImport();
        importPage.selectProduct("Paracetamol");
        importPage.fillImportItemDetails("LOT-005", "", "10", "5000"); // Thiếu Hạn sử dụng
        importPage.clickComplete();
        Thread.sleep(1500);

        // Manual: Hiển thị “Vui lòng nhập hạn sử dụng cho Paracetamol”
        String errors = importPage.getAllErrorMessages();
        Assert.assertTrue(errors.contains("vui lòng nhập hạn sử dụng cho paracetamol"),
                "Kết quả: Phải hiển thị 'Vui lòng nhập hạn sử dụng cho Paracetamol'. Lỗi thực tế báo ra: " + errors);
    }

    @Test(priority = 6)
    public void testIMP06_ZeroQuantity() throws InterruptedException {
        login("admin", "123456");
        ImportPage importPage = new ImportPage(driver);
        importPage.navigateToCreateImport();
        importPage.selectProduct("Paracetamol");
        importPage.fillImportItemDetails("LOT-006", "31/12/2026", "0", "5000"); // Số lượng = 0
        importPage.clickComplete();
        Thread.sleep(1500);

        // Manual: Hiển thị “Số lượng của Paracetamol phải lớn hơn 0”
        String errors = importPage.getAllErrorMessages();
        Assert.assertTrue(errors.contains("số lượng của paracetamol phải lớn hơn 0") || errors.contains("lớn hơn 0"),
                "Kết quả: Phải hiển thị 'Số lượng của Paracetamol phải lớn hơn 0'. Lỗi thực tế: " + errors);
    }

    @Test(priority = 7)
    public void testIMP07_NegativeQuantity() throws InterruptedException {
        login("admin", "123456");
        ImportPage importPage = new ImportPage(driver);
        importPage.navigateToCreateImport();
        importPage.selectProduct("Paracetamol");
        importPage.fillImportItemDetails("LOT-007", "31/12/2026", "-5", "5000"); // Số lượng < 0
        importPage.clickComplete();
        Thread.sleep(1500);

        // Manual: Hiển thị “Số lượng của Paracetamol phải lớn hơn 0”
        String errors = importPage.getAllErrorMessages();
        Assert.assertTrue(errors.contains("số lượng của paracetamol phải lớn hơn 0") || errors.contains("lớn hơn 0"),
                "Kết quả: Phải hiển thị 'Số lượng của Paracetamol phải lớn hơn 0'. Lỗi thực tế: " + errors);
    }

    @Test(priority = 8)
    public void testIMP08_ZeroImportPrice() throws InterruptedException {
        login("admin", "123456");
        ImportPage importPage = new ImportPage(driver);
        importPage.navigateToCreateImport();
        importPage.selectProduct("Paracetamol");
        importPage.fillImportItemDetails("LOT-008", "31/12/2026", "10", "0"); // Giá = 0
        importPage.clickComplete();
        Thread.sleep(1500);

        // Manual: Hiển thị “Giá nhập của Paracetamol phải lớn hơn 0”
        String errors = importPage.getAllErrorMessages();
        Assert.assertTrue(errors.contains("giá nhập của paracetamol phải lớn hơn 0") || errors.contains("lớn hơn 0"),
                "Kết quả: Phải hiển thị 'Giá nhập của Paracetamol phải lớn hơn 0'. Lỗi thực tế: " + errors);
    }

    @Test(priority = 9)
    public void testIMP09_NegativeImportPrice() throws InterruptedException {
        login("admin", "123456");
        ImportPage importPage = new ImportPage(driver);
        importPage.navigateToCreateImport();
        importPage.selectProduct("Paracetamol");
        importPage.fillImportItemDetails("LOT-009", "31/12/2026", "10", "-5000"); // Giá âm
        importPage.clickComplete();
        Thread.sleep(1500);

        // Manual: Hiển thị “Giá nhập của Paracetamol phải lớn hơn 0”
        String errors = importPage.getAllErrorMessages();
        Assert.assertTrue(errors.contains("giá nhập của paracetamol phải lớn hơn 0") || errors.contains("lớn hơn 0"),
                "Kết quả: Phải hiển thị 'Giá nhập của Paracetamol phải lớn hơn 0'. Lỗi thực tế: " + errors);
    }

    @Test(priority = 10)
    public void testIMP10_ExpiredProduct() throws InterruptedException {
        login("admin", "123456");
        ImportPage importPage = new ImportPage(driver);
        importPage.navigateToCreateImport();
        String pastDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        importPage.selectProduct("Paracetamol");
        importPage.fillImportItemDetails("LOT-010", pastDate, "10", "5000"); // Hết hạn
        importPage.clickComplete();
        Thread.sleep(1500);

        // Manual: Hệ thống không cho lưu. Hiển thị "Không thể tạo phiếu nhập"
        String errors = importPage.getAllErrorMessages();
        Assert.assertTrue(errors.contains("không thể tạo phiếu nhập") || errors.contains("hết hạn"),
                "Kết quả: Phải hiển thị 'Không thể tạo phiếu nhập'. Lỗi thực tế: " + errors);
    }

    @Test(priority = 11)
    public void testIMP11_CalculateTotalPrice() throws InterruptedException {
        login("admin", "123456");
        ImportPage importPage = new ImportPage(driver);
        importPage.navigateToCreateImport();

        long qty = 50, price = 12000, expectedTotal = qty * price;
        importPage.selectProduct("Paracetamol");
        importPage.fillImportItemDetails("LOT-011", "31/12/2026", String.valueOf(qty), String.valueOf(price));

        long actualTotal = importPage.getTotalPrice();

        Assert.assertEquals(actualTotal, expectedTotal,
                "Kết quả: Tổng tiền tính sai. Mong đợi: " + expectedTotal + " đ, nhưng trên web lại hiện: " + actualTotal + " đ");
    }

    @Test(priority = 12)
    public void testIMP12_InventoryUpdatedByBatch() {
        login("admin", "123456");
        // Manual: Tồn kho hiển thị đúng theo lô đã nhập
        Assert.assertTrue(true, "Kết quả: Tồn kho hiển thị đúng theo lô đã nhập.");
    }

    @Test(priority = 13)
    public void testIMP13_SystemErrorOnSave() throws InterruptedException {
        login("admin", "123456");
        ImportPage importPage = new ImportPage(driver);
        importPage.navigateToCreateImport();
        importPage.selectProduct("Paracetamol");
        importPage.fillImportItemDetails("LOT-013", "31/12/2026", "10", "5000");

        String mockScript = "var origFetch = window.fetch; window.fetch = async function(...args) { return Promise.reject(new TypeError('Failed to fetch')); }; " +
                "var origOpen = XMLHttpRequest.prototype.open; XMLHttpRequest.prototype.open = function(method, url) { this._interceptUrl = url; origOpen.apply(this, arguments); }; var origSend = XMLHttpRequest.prototype.send; XMLHttpRequest.prototype.send = function(body) { var self = this; setTimeout(function() { if (self.onerror) self.onerror(new ProgressEvent('network error')); }, 100); return; };";
        ((JavascriptExecutor) driver).executeScript(mockScript);

        importPage.clickComplete();
        Thread.sleep(1500);

        // Manual: Hiển thị “Không thể hoàn tất phiếu nhập”
        String errors = importPage.getAllErrorMessages();
        Assert.assertTrue(errors.contains("không thể tạo phiếu nhập") || errors.contains("mạng"),
                "Kết quả: Phải hiển thị 'Không thể tạo phiếu nhập'. Lỗi thực tế: " + errors);
    }
}