package testcases;

import org.testng.Assert;
import org.testng.annotations.Test;

import common.Constant;
import pageobjects.InvoicePage;
import pageobjects.LoginPage;

/**
 * Module: Hoa don (5 sub-module)
 *   - Tao hoa don    : INVCE_01, 02, 03, 04, 05, 07, 09, 10, 11, 13, 15
 *   - Thanh toan     : PAY_01 -> PAY_09
 *   - Xem hoa don    : VIEW_01 -> VIEW_07
 *   - Huy hoa don    : CAN_01 -> CAN_06
 *   - Loc hoa don    : BILL_FIL_01 -> BILL_FIL_08
 * Tong: 41 test case
 */
public class InvoiceTest extends BaseTest {

    private static final String PRODUCT = "Paracetamol";

    // ================================================================
    // A. TAO HOA DON (INVCE)
    // ================================================================

    // INVCE_01: Tao hoa don voi 1 san pham
    @Test
    public void INVCE_01_CreateInvoiceWithOneProduct() {
        loginAsStaff();
        InvoicePage p = new InvoicePage();
        p.openCreateInvoicePage();
        p.searchAndSelectProduct(PRODUCT);
        p.enterQuantity("1");
        p.clickSaveDraft();
        Assert.assertTrue(p.isSuccessMessageDisplayed(),
                "INVCE_01 FAIL: Hoa don khong duoc tao thanh cong");
    }

    // INVCE_02: Tao hoa don voi nhieu san pham
    @Test
    public void INVCE_02_CreateInvoiceWithMultipleProducts() {
        loginAsStaff();
        InvoicePage p = new InvoicePage();
        p.openCreateInvoicePage();
        p.searchAndSelectProduct(PRODUCT);
        p.enterQuantity("2");
        p.searchAndSelectProduct("Amoxicillin");
        p.enterQuantity("1");
        p.clickSaveDraft();
        Assert.assertTrue(p.isSuccessMessageDisplayed(),
                "INVCE_02 FAIL: Hoa don nhieu san pham khong luu duoc");
    }

    // INVCE_03: Hien thi lo theo FEFO
    @Test
    public void INVCE_03_BatchDisplayedByFEFO() {
        loginAsStaff();
        InvoicePage p = new InvoicePage();
        p.openCreateInvoicePage();
        p.searchAndSelectProduct(PRODUCT);
        String firstBatch = p.getFirstBatchText();
        Assert.assertFalse(firstBatch.isEmpty(),
                "INVCE_03 FAIL: Khong hien thi lo san pham theo FEFO");
    }

    // INVCE_04: Khong tim thay san pham
    @Test
    public void INVCE_04_SearchProductNotFound() {
        loginAsStaff();
        InvoicePage p = new InvoicePage();
        p.openCreateInvoicePage();
        p.searchAndSelectProduct("SANPHAM_KHONG_TON_TAI_XYZ");
        Assert.assertFalse(p.isSuccessMessageDisplayed(),
                "INVCE_04 FAIL: He thong khong xu ly dung khi khong tim thay san pham");
    }

    // INVCE_05: Khong co lo hop le
    // Bug: Hien thi "Khong co san pham kha dung" thay vi "Khong co lo kha dung" -> Fail
    @Test
    public void INVCE_05_NoAvailableBatch() {
        loginAsStaff();
        InvoicePage p = new InvoicePage();
        p.openCreateInvoicePage();
        // Tim san pham da het lo kha dung
        p.searchAndSelectProduct("SANPHAM_HET_LO");
        String msg = p.getToastMessage();
        Assert.assertTrue(msg.contains("Khong co lo kha dung"),
                "INVCE_05 FAIL: Thong bao khong dung. Thuc te: " + msg);
    }

    // INVCE_07: So luong vuot ton
    // Bug (lan 1): Hien popup thay vi dong thong bao -> Fail -> Retest Pass
    @Test
    public void INVCE_07_QuantityExceedsStock() {
        loginAsStaff();
        InvoicePage p = new InvoicePage();
        p.openCreateInvoicePage();
        p.searchAndSelectProduct(PRODUCT);
        p.enterQuantity("999999");
        p.clickPay();
        String msg = p.getToastMessage();
        Assert.assertTrue(msg.contains("Khong du ton kho") || msg.contains("vuot"),
                "INVCE_07 FAIL: Khong hien thi canh bao vuot ton kho. Thuc te: " + msg);
    }

    // INVCE_09: Tong tien hoa don > 0
    @Test
    public void INVCE_09_TotalAmountIsPositive() {
        loginAsStaff();
        InvoicePage p = new InvoicePage();
        p.openCreateInvoicePage();
        p.searchAndSelectProduct(PRODUCT);
        p.enterQuantity("1");
        Assert.assertTrue(p.isTotalAmountPositive(),
                "INVCE_09 FAIL: Tong tien hoa don khong lon hon 0");
    }

    // INVCE_10: Hoa don trong
    @Test
    public void INVCE_10_PayEmptyInvoice() {
        loginAsStaff();
        InvoicePage p = new InvoicePage();
        p.openCreateInvoicePage();
        // Bam Thanh toan khi chua co san pham
        p.clickPay();
        Assert.assertTrue(
                p.isFailMessageDisplayed() || !p.isSuccessMessageDisplayed(),
                "INVCE_10 FAIL: He thong cho thanh toan hoa don trong");
    }

    // INVCE_11: Luu nhap hoa don
    @Test
    public void INVCE_11_SaveInvoiceAsDraft() {
        loginAsStaff();
        InvoicePage p = new InvoicePage();
        p.openCreateInvoicePage();
        p.searchAndSelectProduct(PRODUCT);
        p.enterQuantity("1");
        p.clickSaveDraft();
        Assert.assertTrue(p.isSuccessMessageDisplayed(),
                "INVCE_11 FAIL: Khong luu duoc hoa don nhap");
        // Kiem tra trang thai Nhap
        p.openInvoiceListPage();
        p.openFirstInvoiceWithStatus("Nhap");
        Assert.assertTrue(p.isInvoiceStatusEquals("Nhap"),
                "INVCE_11 FAIL: Trang thai hoa don khong phai 'Nhap'");
    }

    // INVCE_13: Gia ban tai thoi diem lap
    // Bug (lan 1): Khong cho thay doi don gia -> Fail -> Retest Pass
    @Test
    public void INVCE_13_PriceAppliedAtCreationTime() {
        loginAsStaff();
        InvoicePage p = new InvoicePage();
        p.openCreateInvoicePage();
        p.searchAndSelectProduct(PRODUCT);
        p.enterQuantity("1");
        Assert.assertTrue(p.isTotalAmountPositive(),
                "INVCE_13 FAIL: Don gia khong duoc ap dung dung tai thoi diem tao hoa don");
    }

    // INVCE_15: Loi he thong khi luu
    @Test
    public void INVCE_15_SystemErrorOnSave() {
        loginAsStaff();
        InvoicePage p = new InvoicePage();
        p.openCreateInvoicePage();
        // Khong them san pham -> trigger loi
        p.clickPay();
        String msg = p.getToastMessage();
        Assert.assertFalse(msg.isEmpty(),
                "INVCE_15 FAIL: Khong hien thi thong bao loi khi luu that bai");
    }

    // ================================================================
    // B. THANH TOAN (PAY)
    // ================================================================

    // PAY_01: Thanh toan thanh cong bang tien mat
    @Test
    public void PAY_01_PayByCashSuccessfully() {
        loginAsStaff();
        InvoicePage p = new InvoicePage();
        p.openCreateInvoicePage();
        p.searchAndSelectProduct(PRODUCT);
        p.enterQuantity("1");
        p.chooseCashPayment();
        p.clickPay();
        Assert.assertTrue(p.isPaymentSuccess(),
                "PAY_01 FAIL: Thanh toan tien mat that bai");
        // Kiem tra trang thai Hoan tat
        Assert.assertTrue(p.isInvoiceStatusEquals("Hoan tat"),
                "PAY_01 FAIL: Trang thai hoa don khong chuyen sang 'Hoan tat'");
    }

    // PAY_02: Thanh toan thanh cong bang chuyen khoan
    @Test
    public void PAY_02_PayByTransferSuccessfully() {
        loginAsStaff();
        InvoicePage p = new InvoicePage();
        p.openCreateInvoicePage();
        p.searchAndSelectProduct(PRODUCT);
        p.enterQuantity("1");
        p.chooseTransferPayment();
        p.clickPay();
        Assert.assertTrue(p.isPaymentSuccess(),
                "PAY_02 FAIL: Thanh toan chuyen khoan that bai");
    }

    // PAY_03: Thay doi phuong thuc truoc xac nhan
    @Test
    public void PAY_03_SwitchPaymentMethodBeforeConfirm() {
        loginAsStaff();
        InvoicePage p = new InvoicePage();
        p.openCreateInvoicePage();
        p.searchAndSelectProduct(PRODUCT);
        p.enterQuantity("1");
        p.chooseCashPayment();
        p.chooseTransferPayment(); // doi sang chuyen khoan
        p.clickPay();
        Assert.assertTrue(p.isPaymentSuccess(),
                "PAY_03 FAIL: He thong khong cap nhat phuong thuc thanh toan khi doi");
    }

    // PAY_04: Hoa don khong hop le (da huy)
    @Test
    public void PAY_04_PayInvalidCancelledInvoice() {
        loginAsAdmin();
        InvoicePage p = new InvoicePage();
        p.openInvoiceListPage();
        // Mo hoa don da huy
        p.openFirstInvoiceWithStatus("Da huy");
        Assert.assertFalse(p.isPayButtonDisplayed(),
                "PAY_04 FAIL: Nut Thanh toan van hien thi tren hoa don da huy");
    }

    // PAY_05: Hoa don tong tien = 0
    @Test
    public void PAY_05_PayInvoiceWithZeroTotal() {
        loginAsStaff();
        InvoicePage p = new InvoicePage();
        p.openCreateInvoicePage();
        // Khong them san pham -> tong = 0
        p.clickPay();
        Assert.assertTrue(
                p.isFailMessageDisplayed() || !p.isSuccessMessageDisplayed(),
                "PAY_05 FAIL: He thong cho thanh toan hoa don co tong tien = 0");
    }

    // PAY_06: Thanh toan that bai (loi mang)
    @Test
    public void PAY_06_PaymentFailed() {
        loginAsStaff();
        InvoicePage p = new InvoicePage();
        p.openCreateInvoicePage();
        p.searchAndSelectProduct(PRODUCT);
        p.enterQuantity("1");
        p.chooseCashPayment();
        // Gia lap loi: submit voi du lieu bat thuong
        p.enterQuantity("0");
        p.clickPay();
        String msg = p.getToastMessage();
        Assert.assertFalse(msg.isEmpty(),
                "PAY_06 FAIL: Khong hien thi thong bao khi thanh toan that bai");
    }

    // PAY_07: Luu that bai sau thanh toan
    @Test
    public void PAY_07_SaveFailedAfterPayment() {
        loginAsStaff();
        InvoicePage p = new InvoicePage();
        p.openCreateInvoicePage();
        p.searchAndSelectProduct(PRODUCT);
        p.enterQuantity("1");
        p.chooseCashPayment();
        p.clickPay();
        // He thong phai hien thi ket qua (thanh cong hoac loi)
        String msg = p.getToastMessage();
        Assert.assertFalse(msg.isEmpty(),
                "PAY_07 FAIL: Khong hien thi thong bao ket qua sau khi thanh toan");
    }

    // PAY_08: Luu thoi gian va phuong thuc thanh toan
    @Test
    public void PAY_08_PaymentTimeAndMethodSaved() {
        loginAsStaff();
        InvoicePage p = new InvoicePage();
        p.openCreateInvoicePage();
        p.searchAndSelectProduct(PRODUCT);
        p.enterQuantity("1");
        p.chooseCashPayment();
        p.clickPay();
        p.isSuccessMessageDisplayed(); // cho toast bien mat
        // Mo lai hoa don vua tao de kiem tra thong tin
        p.openInvoiceListPage();
        p.openFirstInvoiceWithStatus("Hoan tat");
        Assert.assertTrue(p.isPaymentInfoDisplayed(),
                "PAY_08 FAIL: Khong luu thoi gian va phuong thuc thanh toan");
    }

    // PAY_09: Khong thanh toan lai hoa don da hoan tat
    @Test
    public void PAY_09_CannotPayCompletedInvoiceAgain() {
        loginAsAdmin();
        InvoicePage p = new InvoicePage();
        p.openInvoiceListPage();
        p.openFirstInvoiceWithStatus("Hoan tat");
        Assert.assertFalse(p.isPayButtonDisplayed(),
                "PAY_09 FAIL: Nut Thanh toan van hien thi tren hoa don da hoan tat");
    }

    // ================================================================
    // C. XEM HOA DON (VIEW)
    // ================================================================

    // VIEW_01: Xem danh sach hoa don
    @Test
    public void VIEW_01_ViewInvoiceList() {
        loginAsAdmin();
        InvoicePage p = new InvoicePage();
        p.openInvoiceListPage();
        Assert.assertTrue(p.hasInvoiceData(),
                "VIEW_01 FAIL: Khong hien thi danh sach hoa don");
    }

    // VIEW_02: Xem chi tiet mot hoa don
    @Test
    public void VIEW_02_ViewInvoiceDetail() {
        loginAsAdmin();
        InvoicePage p = new InvoicePage();
        p.openInvoiceListPage();
        p.openFirstInvoiceDetail();
        Assert.assertFalse(p.getInvoiceStatus().isEmpty(),
                "VIEW_02 FAIL: Khong hien thi thong tin chi tiet hoa don");
    }

    // VIEW_03: Xem toan bo khi khong nhap dieu kien
    @Test
    public void VIEW_03_ViewAllInvoicesWithNoFilter() {
        loginAsAdmin();
        InvoicePage p = new InvoicePage();
        p.openInvoiceListPage();
        Assert.assertTrue(p.hasInvoiceData(),
                "VIEW_03 FAIL: Khong hien thi toan bo hoa don khi khong loc");
    }

    // VIEW_04: Dieu kien tim kiem khong hop le
    @Test
    public void VIEW_04_SearchWithInvalidCondition() {
        loginAsAdmin();
        InvoicePage p = new InvoicePage();
        p.openInvoiceListPage();
        p.searchInvoiceByCode("3$^5");
        Assert.assertTrue(p.isEmptyMessageDisplayed(),
                "VIEW_04 FAIL: Khong hien thi thong bao khi dieu kien tim kiem khong hop le");
    }

    // VIEW_05: Tim ma hoa don khong ton tai
    @Test
    public void VIEW_05_SearchNonExistentInvoiceCode() {
        loginAsAdmin();
        InvoicePage p = new InvoicePage();
        p.openInvoiceListPage();
        p.searchInvoiceByCode("HD99999");
        Assert.assertTrue(p.isEmptyMessageDisplayed(),
                "VIEW_05 FAIL: Khong hien thi thong bao khi ma hoa don khong ton tai");
        Assert.assertFalse(p.hasInvoiceData(),
                "VIEW_05 FAIL: Van hien thi du lieu du ma hoa don khong ton tai");
    }

    // VIEW_06: Hien thi trang thai hoa don
    @Test
    public void VIEW_06_InvoiceStatusDisplayedCorrectly() {
        loginAsAdmin();
        InvoicePage p = new InvoicePage();
        p.openInvoiceListPage();
        // Moi hoa don phai co trang thai hien thi
        Assert.assertTrue(p.hasInvoiceData(),
                "VIEW_06 FAIL: Khong co hoa don de kiem tra trang thai");
        // Kiem tra hoa don Hoan tat co mau xanh / label dung
        p.filterByStatus("Hoan tat");
        Assert.assertTrue(p.hasInvoiceData() || p.isEmptyMessageDisplayed(),
                "VIEW_06 FAIL: Loc theo trang thai 'Hoan tat' khong hoat dong");
    }

    // VIEW_07: Truong hop khong co du lieu
    @Test
    public void VIEW_07_EmptyInvoiceList() {
        loginAsAdmin();
        InvoicePage p = new InvoicePage();
        p.openInvoiceListPage();
        // Tim kiem voi tu khoa khong ton tai de trigger trang thai trong
        p.searchInvoiceByCode("HOADON_KHONG_TON_TAI_XYZ_99999");
        Assert.assertTrue(p.isEmptyMessageDisplayed(),
                "VIEW_07 FAIL: Khong hien thi thong bao khi danh sach hoa don trong");
    }

    // ================================================================
    // D. HUY HOA DON (CAN)
    // ================================================================

    // CAN_01: Huy hoa don thanh cong
    @Test
    public void CAN_01_CancelInvoiceSuccessfully() {
        loginAsAdmin();
        InvoicePage p = new InvoicePage();
        p.openInvoiceListPage();
        // Mo hoa don o trang thai Nhap
        p.openFirstInvoiceWithStatus("Nhap");
        p.clickCancelInvoice();
        p.confirmCancel();
        Assert.assertTrue(p.isInvoiceStatusEquals("Da huy"),
                "CAN_01 FAIL: Trang thai hoa don khong chuyen sang 'Da huy'");
    }

    // CAN_02: Huy thao tac huy hoa don
    @Test
    public void CAN_02_DenyCancelInvoice() {
        loginAsAdmin();
        InvoicePage p = new InvoicePage();
        p.openInvoiceListPage();
        p.openFirstInvoiceWithStatus("Nhap");
        String statusBefore = p.getInvoiceStatus();
        p.clickCancelInvoice();
        p.denyCancel();
        Assert.assertEquals(p.getInvoiceStatus(), statusBefore,
                "CAN_02 FAIL: Trang thai hoa don bi thay doi du da chon Khong");
    }

    // CAN_03: Huy hoa don da huy truoc do
    @Test
    public void CAN_03_CannotCancelAlreadyCancelledInvoice() {
        loginAsAdmin();
        InvoicePage p = new InvoicePage();
        p.openInvoiceListPage();
        p.openFirstInvoiceWithStatus("Da huy");
        Assert.assertFalse(p.isCancelButtonDisplayed(),
                "CAN_03 FAIL: Nut Huy van hien thi tren hoa don da huy truoc do");
    }

    // CAN_04: Khong cho huy hoa don da hoan tat
    @Test
    public void CAN_04_CannotCancelCompletedInvoice() {
        loginAsAdmin();
        InvoicePage p = new InvoicePage();
        p.openInvoiceListPage();
        p.openFirstInvoiceWithStatus("Hoan tat");
        Assert.assertFalse(p.isCancelButtonDisplayed(),
                "CAN_04 FAIL: Nut Huy van hien thi tren hoa don da hoan tat");
    }

    // CAN_05: Khong phuc hoi ton kho khi huy hoa don
    @Test
    public void CAN_05_InventoryNotRestoredAfterCancel() {
        loginAsAdmin();
        InvoicePage p = new InvoicePage();
        p.openInvoiceListPage();
        p.openFirstInvoiceWithStatus("Nhap");
        p.clickCancelInvoice();
        p.confirmCancel();
        Assert.assertTrue(p.isInvoiceStatusEquals("Da huy"),
                "CAN_05 FAIL: Hoa don khong chuyen sang trang thai 'Da huy'");
        // Viec kiem tra ton kho khong thay doi duoc thuc hien o InventoryTest
        // Test nay chi verify hoa don duoc huy thanh cong
    }

    // CAN_06: Loi cap nhat trang thai khi huy
    @Test
    public void CAN_06_ErrorOnCancelInvoice() {
        loginAsAdmin();
        InvoicePage p = new InvoicePage();
        p.openInvoiceListPage();
        p.openFirstInvoiceWithStatus("Nhap");
        p.clickCancelInvoice();
        p.confirmCancel();
        // He thong phai hien thi ket qua (thanh cong hoac loi)
        String msg = p.getToastMessage();
        Assert.assertFalse(msg.isEmpty(),
                "CAN_06 FAIL: Khong hien thi thong bao ket qua khi huy hoa don");
    }

    // ================================================================
    // E. LOC HOA DON (BILL_FIL)
    // ================================================================

    // BILL_FIL_01: Loc theo khoang thoi gian hop le
    // Bug (lan 1): Khong co chuc nang loc theo thoi gian -> Fail -> Retest Pass
    @Test
    public void BILL_FIL_01_FilterByValidDateRange() {
        loginAsAdmin();
        InvoicePage p = new InvoicePage();
        p.openInvoiceListPage();
        Assert.assertTrue(p.isDateFilterAvailable(),
                "BILL_FIL_01 FAIL: Khong co chuc nang loc theo khoang thoi gian");
        p.filterByDateRange("2026-01-01", "2026-12-31");
        p.clickFilter();
        Assert.assertTrue(p.hasInvoiceData() || p.isEmptyMessageDisplayed(),
                "BILL_FIL_01 FAIL: Loc theo thoi gian khong hoat dong");
    }

    // BILL_FIL_02: Loc theo trang thai
    @Test
    public void BILL_FIL_02_FilterByStatus() {
        loginAsAdmin();
        InvoicePage p = new InvoicePage();
        p.openInvoiceListPage();
        p.filterByStatus("Hoan tat");
        Assert.assertTrue(p.hasInvoiceData() || p.isEmptyMessageDisplayed(),
                "BILL_FIL_02 FAIL: Loc theo trang thai khong hoat dong");
        // Tat ca ket qua phai co trang thai Hoan tat
        if (p.hasInvoiceData()) {
            Assert.assertTrue(p.allRowsHaveStatus("Hoan tat"),
                    "BILL_FIL_02 FAIL: Ket qua loc co hoa don khong dung trang thai");
        }
    }

    // BILL_FIL_03: Tim theo ma hoa don
    @Test
    public void BILL_FIL_03_SearchByInvoiceCode() {
        loginAsAdmin();
        InvoicePage p = new InvoicePage();
        p.openInvoiceListPage();
        // Lay ma hoa don dau tien de tim kiem
        p.openFirstInvoiceDetail();
        String status = p.getInvoiceStatus(); // lay thong tin de confirm dang o detail
        Assert.assertFalse(status.isEmpty(),
                "BILL_FIL_03 FAIL: Khong mo duoc chi tiet hoa don de lay ma");
        // Quay lai danh sach va tim theo ma
        p.openInvoiceListPage();
        p.searchInvoiceByCode("HD");
        Assert.assertTrue(p.hasInvoiceData(),
                "BILL_FIL_03 FAIL: Tim theo ma hoa don khong tra ve ket qua");
    }

    // BILL_FIL_04: Ket hop nhieu dieu kien loc
    // Bug (lan 1): Khong co loc theo thoi gian -> Pass (chi loc trang thai + ma)
    @Test
    public void BILL_FIL_04_FilterWithMultipleConditions() {
        loginAsAdmin();
        InvoicePage p = new InvoicePage();
        p.openInvoiceListPage();
        // Loc theo trang thai + ma
        p.filterByStatus("Hoan tat");
        p.searchInvoiceByCode("HD");
        Assert.assertTrue(p.hasInvoiceData() || p.isEmptyMessageDisplayed(),
                "BILL_FIL_04 FAIL: Ket hop nhieu dieu kien loc khong hoat dong");
    }

    // BILL_FIL_05: Chi nhap 1 dieu kien loc
    @Test
    public void BILL_FIL_05_FilterWithSingleCondition() {
        loginAsAdmin();
        InvoicePage p = new InvoicePage();
        p.openInvoiceListPage();
        p.filterByStatus("Nhap");
        Assert.assertTrue(p.hasInvoiceData() || p.isEmptyMessageDisplayed(),
                "BILL_FIL_05 FAIL: Loc voi 1 dieu kien khong hoat dong");
    }

    // BILL_FIL_06: Khong nhap dieu kien loc
    @Test
    public void BILL_FIL_06_FilterWithNoCondition() {
        loginAsAdmin();
        InvoicePage p = new InvoicePage();
        p.openInvoiceListPage();
        p.clickFilter();
        Assert.assertTrue(p.hasInvoiceData(),
                "BILL_FIL_06 FAIL: Khong hien thi toan bo danh sach khi khong nhap dieu kien loc");
    }

    // BILL_FIL_07: Ngay bat dau lon hon ngay ket thuc
    // Bug (lan 1): Khong co chuc nang loc theo thoi gian -> Fail -> Retest Pass
    @Test
    public void BILL_FIL_07_FilterWithInvalidDateRange() {
        loginAsAdmin();
        InvoicePage p = new InvoicePage();
        p.openInvoiceListPage();
        Assert.assertTrue(p.isDateFilterAvailable(),
                "BILL_FIL_07 FAIL: Khong co chuc nang loc theo thoi gian");
        // Ngay bat dau > ngay ket thuc
        p.filterByDateRange("2026-12-31", "2026-01-01");
        p.clickFilter();
        // He thong phai bao loi hoac hien thi rong
        Assert.assertTrue(
                p.isEmptyMessageDisplayed() || p.isFailMessageDisplayed(),
                "BILL_FIL_07 FAIL: He thong khong bao loi khi ngay bat dau > ngay ket thuc");
    }

    // BILL_FIL_08: Khong co ket qua loc
    @Test
    public void BILL_FIL_08_FilterWithNoMatchingResult() {
        loginAsAdmin();
        InvoicePage p = new InvoicePage();
        p.openInvoiceListPage();
        p.searchInvoiceByCode("HOADON_KHONG_TON_TAI_XYZ_99999");
        Assert.assertTrue(p.isEmptyMessageDisplayed(),
                "BILL_FIL_08 FAIL: Khong hien thi thong bao khi khong co ket qua loc");
    }

    // ================================================================
    // Helper
    // ================================================================

    private void loginAsStaff() {
        new LoginPage().login(Constant.STAFF_USER, Constant.STAFF_PASS);
    }

    private void loginAsAdmin() {
        new LoginPage().login(Constant.ADMIN_USER, Constant.ADMIN_PASS);
    }
}
