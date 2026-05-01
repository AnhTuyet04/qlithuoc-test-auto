package testcases;

import common.Constant;
import org.testng.Assert;
import org.testng.annotations.Test;
import pageobjects.ImportPage;
import pageobjects.InventoryPage;
import pageobjects.LoginPage;

/**
 * Module: Nhập hàng
 * Tester: Nguyễn Thị My Sa
 * Pre-condition: Hệ thống hoạt động, đăng nhập bằng tài khoản Admin
 *
 * Tổng: 13 test case (IMP_01 → IMP_13)
 * Pass: 13 | Fail: 0
 */
public class ImportTest extends BaseTest {

    // Sản phẩm mẫu đã tồn tại trong hệ thống
    private static final String PRODUCT_NAME = "Paracetamol";
    private static final String BATCH_NO     = "LOT-AUTO-001";
    private static final String QUANTITY     = "10";
    private static final String PRICE        = "8000";

    // ----------------------------------------------------------------
    // IMP_01: Nhập hàng hoàn tất thành công
    // Kết quả mong đợi: Phiếu nhập lưu thành công, tồn kho cập nhật theo lô
    // ----------------------------------------------------------------
    @Test
    public void IMP_01_CompleteImportSuccessfully() {
        loginAsAdmin();
        ImportPage importPage = new ImportPage();
        importPage.openImportPage();
        importPage.clickCreateImport();

        importPage.searchAndSelectProduct(PRODUCT_NAME);
        importPage.fillImportRow(BATCH_NO, "future", QUANTITY, PRICE);
        importPage.clickComplete();

        Assert.assertTrue(
                importPage.isSuccessMessageDisplayed(),
                "IMP_01 FAIL: Không hiển thị thông báo hoàn tất phiếu nhập thành công"
        );
    }

    // ----------------------------------------------------------------
    // IMP_02: Lưu nháp phiếu nhập
    // Kết quả mong đợi: Phiếu nhập được lưu, tồn kho chưa cập nhật
    // ----------------------------------------------------------------
    @Test
    public void IMP_02_SaveImportAsDraft() {
        loginAsAdmin();
        ImportPage importPage = new ImportPage();
        importPage.openImportPage();
        importPage.clickCreateImport();

        importPage.searchAndSelectProduct(PRODUCT_NAME);
        importPage.fillImportRow("LOT-DRAFT-001", "future", QUANTITY, PRICE);
        importPage.clickSaveDraft();

        Assert.assertTrue(
                importPage.isSuccessMessageDisplayed(),
                "IMP_02 FAIL: Không lưu được phiếu nhập nháp"
        );

        // Kiểm tra trạng thái phiếu là Nháp
        importPage.openImportPage();
        String status = importPage.getFirstImportStatus();
        Assert.assertTrue(
                status.contains("Nháp") || status.contains("Draft"),
                "IMP_02 FAIL: Trạng thái phiếu nhập không phải 'Nháp'. Thực tế: " + status
        );
    }

    // ----------------------------------------------------------------
    // IMP_03: Phiếu nhập không có sản phẩm
    // Kết quả mong đợi: Báo lỗi, không cho lưu
    // ----------------------------------------------------------------
    @Test
    public void IMP_03_CompleteImportWithNoProduct() {
        loginAsAdmin();
        ImportPage importPage = new ImportPage();
        importPage.openImportPage();
        importPage.clickCreateImport();

        // Không thêm sản phẩm nào, bấm Hoàn tất luôn
        importPage.clickComplete();

        Assert.assertTrue(
                importPage.isValidationMessageDisplayed() || importPage.isFailMessageDisplayed(),
                "IMP_03 FAIL: Hệ thống không báo lỗi khi phiếu nhập không có sản phẩm"
        );
    }

    // ----------------------------------------------------------------
    // IMP_04: Thiếu số lô
    // Kết quả mong đợi: Hiển thị "Vui lòng nhập số lô cho <tên sản phẩm>"
    // ----------------------------------------------------------------
    @Test
    public void IMP_04_CompleteImportWithMissingBatchNo() {
        loginAsAdmin();
        ImportPage importPage = new ImportPage();
        importPage.openImportPage();
        importPage.clickCreateImport();

        importPage.searchAndSelectProduct(PRODUCT_NAME);
        // Bỏ trống số lô (null)
        importPage.fillImportRow(null, "future", QUANTITY, PRICE);
        importPage.clickComplete();

        String msg = importPage.getValidationMessage();
        Assert.assertTrue(
                msg.contains("Vui lòng nhập số lô"),
                "IMP_04 FAIL: Thông báo lỗi không đúng. Thực tế: " + msg
        );
    }

    // ----------------------------------------------------------------
    // IMP_05: Thiếu hạn sử dụng
    // Kết quả mong đợi: Hiển thị "Vui lòng nhập hạn sử dụng cho <tên sản phẩm>"
    // ----------------------------------------------------------------
    @Test
    public void IMP_05_CompleteImportWithMissingExpiryDate() {
        loginAsAdmin();
        ImportPage importPage = new ImportPage();
        importPage.openImportPage();
        importPage.clickCreateImport();

        importPage.searchAndSelectProduct(PRODUCT_NAME);
        // Bỏ trống hạn sử dụng (null)
        importPage.fillImportRow(BATCH_NO, null, QUANTITY, PRICE);
        importPage.clickComplete();

        String msg = importPage.getValidationMessage();
        Assert.assertTrue(
                msg.contains("Vui lòng nhập hạn sử dụng"),
                "IMP_05 FAIL: Thông báo lỗi không đúng. Thực tế: " + msg
        );
    }

    // ----------------------------------------------------------------
    // IMP_06: Số lượng = 0
    // Kết quả mong đợi: Hiển thị "Số lượng của <tên sản phẩm> phải lớn hơn 0"
    // ----------------------------------------------------------------
    @Test
    public void IMP_06_CompleteImportWithQuantityZero() {
        loginAsAdmin();
        ImportPage importPage = new ImportPage();
        importPage.openImportPage();
        importPage.clickCreateImport();

        importPage.searchAndSelectProduct(PRODUCT_NAME);
        importPage.fillImportRow(BATCH_NO, "future", "0", PRICE);
        importPage.clickComplete();

        String msg = importPage.getValidationMessage();
        Assert.assertTrue(
                msg.contains("phải lớn hơn 0"),
                "IMP_06 FAIL: Thông báo lỗi không đúng. Thực tế: " + msg
        );
    }

    // ----------------------------------------------------------------
    // IMP_07: Số lượng < 0
    // Kết quả mong đợi: Hiển thị "Số lượng của <tên sản phẩm> phải lớn hơn 0"
    // ----------------------------------------------------------------
    @Test
    public void IMP_07_CompleteImportWithNegativeQuantity() {
        loginAsAdmin();
        ImportPage importPage = new ImportPage();
        importPage.openImportPage();
        importPage.clickCreateImport();

        importPage.searchAndSelectProduct(PRODUCT_NAME);
        importPage.fillImportRow(BATCH_NO, "future", "-5", PRICE);
        importPage.clickComplete();

        String msg = importPage.getValidationMessage();
        Assert.assertTrue(
                msg.contains("phải lớn hơn 0"),
                "IMP_07 FAIL: Thông báo lỗi không đúng. Thực tế: " + msg
        );
    }

    // ----------------------------------------------------------------
    // IMP_08: Giá nhập = 0
    // Kết quả mong đợi: Hiển thị "Giá nhập của <tên sản phẩm> phải lớn hơn 0"
    // ----------------------------------------------------------------
    @Test
    public void IMP_08_CompleteImportWithPriceZero() {
        loginAsAdmin();
        ImportPage importPage = new ImportPage();
        importPage.openImportPage();
        importPage.clickCreateImport();

        importPage.searchAndSelectProduct(PRODUCT_NAME);
        importPage.fillImportRow(BATCH_NO, "future", QUANTITY, "0");
        importPage.clickComplete();

        String msg = importPage.getValidationMessage();
        Assert.assertTrue(
                msg.contains("phải lớn hơn 0"),
                "IMP_08 FAIL: Thông báo lỗi không đúng. Thực tế: " + msg
        );
    }

    // ----------------------------------------------------------------
    // IMP_09: Giá nhập < 0
    // Kết quả mong đợi: Hiển thị "Giá nhập của <tên sản phẩm> phải lớn hơn 0"
    // ----------------------------------------------------------------
    @Test
    public void IMP_09_CompleteImportWithNegativePrice() {
        loginAsAdmin();
        ImportPage importPage = new ImportPage();
        importPage.openImportPage();
        importPage.clickCreateImport();

        importPage.searchAndSelectProduct(PRODUCT_NAME);
        importPage.fillImportRow(BATCH_NO, "future", QUANTITY, "-1000");
        importPage.clickComplete();

        String msg = importPage.getValidationMessage();
        Assert.assertTrue(
                msg.contains("phải lớn hơn 0"),
                "IMP_09 FAIL: Thông báo lỗi không đúng. Thực tế: " + msg
        );
    }

    // ----------------------------------------------------------------
    // IMP_10: Nhập hàng đã hết hạn
    // Kết quả mong đợi: Hệ thống không cho lưu
    // ----------------------------------------------------------------
    @Test
    public void IMP_10_CompleteImportWithExpiredProduct() {
        loginAsAdmin();
        ImportPage importPage = new ImportPage();
        importPage.openImportPage();
        importPage.clickCreateImport();

        importPage.searchAndSelectProduct(PRODUCT_NAME);
        // "expired" → fillImportRow sẽ tự tính ngày hôm qua
        importPage.fillImportRow(BATCH_NO, "expired", QUANTITY, PRICE);
        importPage.clickComplete();

        Assert.assertTrue(
                importPage.isFailMessageDisplayed() || importPage.isValidationMessageDisplayed(),
                "IMP_10 FAIL: Hệ thống vẫn cho nhập hàng đã hết hạn"
        );
    }

    // ----------------------------------------------------------------
    // IMP_11: Tính tổng tiền phiếu nhập
    // Kết quả mong đợi: Tổng tiền tính chính xác (= số lượng × giá nhập)
    // ----------------------------------------------------------------
    @Test
    public void IMP_11_TotalAmountCalculatedCorrectly() {
        loginAsAdmin();
        ImportPage importPage = new ImportPage();
        importPage.openImportPage();
        importPage.clickCreateImport();

        int qty   = 5;
        long price = 10000L;
        importPage.searchAndSelectProduct(PRODUCT_NAME);
        importPage.fillImportRow(BATCH_NO, "future", String.valueOf(qty), String.valueOf(price));

        long expected = importPage.calculateExpectedTotal(qty, price);
        String totalText = importPage.getTotalAmountText();

        // Loại bỏ dấu phân cách và ký hiệu tiền tệ để so sánh số
        String numericTotal = totalText.replaceAll("[^0-9]", "");
        Assert.assertFalse(numericTotal.isEmpty(),
                "IMP_11 FAIL: Không lấy được giá trị tổng tiền từ giao diện"
        );
        Assert.assertEquals(Long.parseLong(numericTotal), expected,
                "IMP_11 FAIL: Tổng tiền không đúng. Mong đợi: " + expected + " | Thực tế: " + numericTotal
        );
    }

    // ----------------------------------------------------------------
    // IMP_12: Cập nhật tồn kho theo từng lô
    // Kết quả mong đợi: Tồn kho hiển thị đúng theo lô đã nhập
    // ----------------------------------------------------------------
    @Test
    public void IMP_12_InventoryUpdatedByBatchAfterImport() {
        loginAsAdmin();

        // Bước 1: Tạo phiếu nhập hoàn tất với số lô riêng biệt
        String uniqueBatch = "LOT-INV-" + System.currentTimeMillis();
        ImportPage importPage = new ImportPage();
        importPage.openImportPage();
        importPage.clickCreateImport();

        importPage.searchAndSelectProduct(PRODUCT_NAME);
        importPage.fillImportRow(uniqueBatch, "future", "20", PRICE);
        importPage.clickComplete();

        Assert.assertTrue(
                importPage.isSuccessMessageDisplayed(),
                "IMP_12 FAIL: Phiếu nhập không hoàn tất được"
        );

        // Bước 2: Vào tồn kho kiểm tra lô vừa nhập
        InventoryPage inventoryPage = new InventoryPage();
        inventoryPage.openInventoryPage();
        inventoryPage.searchByKeyword(uniqueBatch);

        Assert.assertTrue(
                inventoryPage.isResultDisplayed(uniqueBatch),
                "IMP_12 FAIL: Tồn kho không hiển thị lô '" + uniqueBatch + "' sau khi nhập hàng"
        );
    }

    // ----------------------------------------------------------------
    // IMP_13: Lỗi hệ thống khi lưu phiếu nhập
    // Kết quả mong đợi: Hiển thị "Không thể hoàn tất phiếu nhập"
    // (Test verify hệ thống xử lý lỗi đúng — thực tế đã Pass)
    // ----------------------------------------------------------------
    @Test
    public void IMP_13_SystemErrorOnCompleteImport() {
        loginAsAdmin();
        ImportPage importPage = new ImportPage();
        importPage.openImportPage();
        importPage.clickCreateImport();

        // Giả lập lỗi bằng cách submit dữ liệu không hợp lệ để trigger error response
        importPage.searchAndSelectProduct(PRODUCT_NAME);
        importPage.fillImportRow(BATCH_NO, "expired", "0", "0");
        importPage.clickComplete();

        String msg = importPage.getToastMessage();
        Assert.assertFalse(
                msg.isEmpty(),
                "IMP_13 FAIL: Không hiển thị thông báo lỗi khi hoàn tất phiếu nhập thất bại"
        );
        Assert.assertTrue(
                msg.contains("Không thể hoàn tất") || msg.contains("thất bại") || msg.contains("lỗi"),
                "IMP_13 FAIL: Thông báo lỗi không đúng. Thực tế: " + msg
        );
    }

    // ================================================================
    // Helper
    // ================================================================

    private void loginAsAdmin() {
        new LoginPage().login(Constant.ADMIN_USER, Constant.ADMIN_PASS);
    }
}
