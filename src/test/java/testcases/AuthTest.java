package testcases;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import common.Constant;
import pageobjects.InvoicePage;
import pageobjects.LoginPage;

/**
 * Module: Phan quyen
 * Tester: Phan Le Nghi
 * Pre-condition: He thong hoat dong, co tai khoan Admin va Nhan vien
 *
 * Tong: 8 test case (AUTH_01 -> AUTH_08)
 * Pass: 8 | Fail: 0
 *
 * Chien luoc:
 *   AUTH_01-04: Nhan vien KHONG duoc thuc hien cac chuc nang quan tri
 *               -> Kiem tra nut bi an hoac URL bi chan
 *   AUTH_05-06: Admin va Nhan vien DEU duoc xem ton kho
 *   AUTH_07:    Admin duoc xem va loc hoa don
 *   AUTH_08:    Nhan vien duoc tao hoa don va thanh toan
 */
public class AuthTest extends BaseTest {

    // ================================================================
    // AUTH_01: Nhan vien khong duoc vao chuc nang Them san pham
    // Ket qua mong doi: Khong hien thi nut "Them san pham" hoac bi chan truy cap
    // ================================================================
    @Test
    public void AUTH_01_StaffCannotAddProduct() {
        loginAsStaff();

        Constant.WEBDRIVER.get(Constant.URL + "/products");
        waitForPageLoad();

        // Nut "Them san pham" phai bi an voi Nhan vien
        By btnAdd = By.xpath(
                "//button[contains(.,'Them san pham') or contains(.,'Them moi')]");
        boolean btnVisible = !Constant.WEBDRIVER.findElements(btnAdd).isEmpty()
                && Constant.WEBDRIVER.findElements(btnAdd).get(0).isDisplayed();

        Assert.assertFalse(btnVisible,
                "AUTH_01 FAIL: Nhan vien van thay nut 'Them san pham'");
    }

    // ================================================================
    // AUTH_02: Nhan vien khong duoc cap nhat san pham
    // Ket qua mong doi: Khong hien thi nut "Sua" hoac khong thao tac duoc
    // ================================================================
    @Test
    public void AUTH_02_StaffCannotUpdateProduct() {
        loginAsStaff();

        Constant.WEBDRIVER.get(Constant.URL + "/products");
        waitForPageLoad();

        // Nut "Sua" phai bi an voi Nhan vien
        By btnEdit = By.xpath(
                "//button[contains(@title,'Sua') or contains(.,'Sua')] " +
                "| //a[contains(@title,'Sua') or contains(.,'Sua')]");
        boolean btnVisible = !Constant.WEBDRIVER.findElements(btnEdit).isEmpty()
                && Constant.WEBDRIVER.findElements(btnEdit).get(0).isDisplayed();

        Assert.assertFalse(btnVisible,
                "AUTH_02 FAIL: Nhan vien van thay nut 'Sua' san pham");
    }

    // ================================================================
    // AUTH_03: Nhan vien khong duoc nhap hang
    // Ket qua mong doi: Khong hien thi nut "Tao phieu nhap" hoac bi chan
    // ================================================================
    @Test
    public void AUTH_03_StaffCannotCreateImport() {
        loginAsStaff();

        Constant.WEBDRIVER.get(Constant.URL + "/imports");
        waitForPageLoad();

        // Nut "Tao phieu nhap" phai bi an voi Nhan vien
        By btnCreate = By.xpath(
                "//button[contains(.,'Tao phieu nhap') or contains(.,'Tao phieu')]");
        boolean btnVisible = !Constant.WEBDRIVER.findElements(btnCreate).isEmpty()
                && Constant.WEBDRIVER.findElements(btnCreate).get(0).isDisplayed();

        // Hoac trang bi redirect ve trang khac (bi chan truy cap)
        boolean isBlocked = !Constant.WEBDRIVER.getCurrentUrl().contains("/imports");

        Assert.assertTrue(!btnVisible || isBlocked,
                "AUTH_03 FAIL: Nhan vien van thay nut 'Tao phieu nhap' hoac truy cap duoc trang nhap hang");
    }

    // ================================================================
    // AUTH_04: Nhan vien khong duoc huy hoa don
    // Ket qua mong doi: Khong hien thi nut "Huy hoa don" hoac bi chan
    // ================================================================
    @Test
    public void AUTH_04_StaffCannotCancelInvoice() {
        loginAsStaff();

        // Vao danh sach hoa don va mo chi tiet
        InvoicePage invoicePage = new InvoicePage();
        invoicePage.openInvoiceListPage();
        invoicePage.openFirstInvoiceDetail();

        // Nut "Huy hoa don" phai bi an voi Nhan vien
        Assert.assertFalse(invoicePage.isCancelButtonDisplayed(),
                "AUTH_04 FAIL: Nhan vien van thay nut 'Huy hoa don'");
    }

    // ================================================================
    // AUTH_05: Admin duoc phep xem ton kho
    // Ket qua mong doi: Truy cap duoc man hinh ton kho
    // ================================================================
    @Test
    public void AUTH_05_AdminCanAccessInventory() {
        loginAsAdmin();

        Constant.WEBDRIVER.get(Constant.URL + "/inventory");
        waitForPageLoad();

        Assert.assertTrue(
                Constant.WEBDRIVER.getCurrentUrl().contains("/inventory"),
                "AUTH_05 FAIL: Admin khong truy cap duoc trang ton kho");
    }

    // ================================================================
    // AUTH_06: Nhan vien duoc phep xem ton kho
    // Ket qua mong doi: Truy cap duoc man hinh ton kho
    // ================================================================
    @Test
    public void AUTH_06_StaffCanAccessInventory() {
        loginAsStaff();

        Constant.WEBDRIVER.get(Constant.URL + "/inventory");
        waitForPageLoad();

        Assert.assertTrue(
                Constant.WEBDRIVER.getCurrentUrl().contains("/inventory"),
                "AUTH_06 FAIL: Nhan vien khong truy cap duoc trang ton kho");
    }

    // ================================================================
    // AUTH_07: Admin duoc phep xem va loc hoa don
    // Ket qua mong doi: Hien thi danh sach va loc dung
    // ================================================================
    @Test
    public void AUTH_07_AdminCanViewAndFilterInvoices() {
        loginAsAdmin();

        InvoicePage invoicePage = new InvoicePage();
        invoicePage.openInvoiceListPage();

        // Kiem tra truy cap duoc
        Assert.assertTrue(
                Constant.WEBDRIVER.getCurrentUrl().contains("/invoices"),
                "AUTH_07 FAIL: Admin khong truy cap duoc trang hoa don");

        // Kiem tra chuc nang loc hoat dong
        invoicePage.filterByStatus("Hoan tat");
        Assert.assertTrue(
                invoicePage.hasInvoiceData() || invoicePage.isEmptyMessageDisplayed(),
                "AUTH_07 FAIL: Chuc nang loc hoa don khong hoat dong voi Admin");
    }

    // ================================================================
    // AUTH_08: Nhan vien duoc phep tao hoa don va thanh toan
    // Ket qua mong doi: Tao va thanh toan thanh cong
    // ================================================================
    @Test
    public void AUTH_08_StaffCanCreateInvoiceAndPay() {
        loginAsStaff();

        InvoicePage invoicePage = new InvoicePage();
        invoicePage.openCreateInvoicePage();
        invoicePage.searchAndSelectProduct("Paracetamol");
        invoicePage.enterQuantity("1");
        invoicePage.chooseCashPayment();
        invoicePage.clickPay();

        Assert.assertTrue(invoicePage.isPaymentSuccess(),
                "AUTH_08 FAIL: Nhan vien khong tao va thanh toan duoc hoa don");
    }

    // ================================================================
    // Helper
    // ================================================================

    private void loginAsAdmin() {
        new LoginPage().login(Constant.ADMIN_USER, Constant.ADMIN_PASS);
    }

    private void loginAsStaff() {
        new LoginPage().login(Constant.STAFF_USER, Constant.STAFF_PASS);
    }

    /** Cho trang load xong (table hoac noi dung chinh hien thi) */
    private void waitForPageLoad() {
        new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10))
                .until(ExpectedConditions.or(
                        ExpectedConditions.presenceOfElementLocated(By.xpath("//table | //main | //h1")),
                        ExpectedConditions.urlContains("login")
                ));
    }
}
