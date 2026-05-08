package testcases;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;
import pageobjects.InventoryPage;
import java.util.List;

public class InventoryTest extends BaseTest {

    @Test(priority = 1)
    public void testINV01_ViewAllInventory() {
        login("admin", "123456");
        InventoryPage inventoryPage = new InventoryPage(driver);
        inventoryPage.navigateToInventory();
        Assert.assertTrue(inventoryPage.getNumberOfRows() > 0);
    }

    @Test(priority = 2)
    public void testINV02_SearchByProductName() {
        login("admin", "123456");
        InventoryPage inventoryPage = new InventoryPage(driver);
        inventoryPage.navigateToInventory();
        String searchKeyword = "Paracetamol";
        inventoryPage.search(searchKeyword);
        List<String> productNames = inventoryPage.getDisplayedProductNames();
        Assert.assertTrue(productNames.size() > 0);
        for (String name : productNames) Assert.assertTrue(name.toLowerCase().contains(searchKeyword.toLowerCase()));
    }

    @Test(priority = 3)
    public void testINV03_SearchByBatchNumber() {
        login("admin", "123456");
        InventoryPage inventoryPage = new InventoryPage(driver);
        inventoryPage.navigateToInventory();
        String batchKeyword = "LOT";
        inventoryPage.search(batchKeyword);
        List<String> batches = inventoryPage.getDisplayedBatchNumbers();
        Assert.assertTrue(batches.size() > 0);
        for (String batch : batches) Assert.assertTrue(batch.contains(batchKeyword));
    }

    @Test(priority = 4)
    public void testINV04_EmptySearchKeyword() {
        login("admin", "123456");
        InventoryPage inventoryPage = new InventoryPage(driver);
        inventoryPage.navigateToInventory();
        inventoryPage.search("");
        Assert.assertTrue(inventoryPage.getNumberOfRows() > 0);
    }

    @Test(priority = 5)
    public void testINV05_ProductNotFound() {
        login("admin", "123456");
        InventoryPage inventoryPage = new InventoryPage(driver);
        inventoryPage.navigateToInventory();
        inventoryPage.search("XyzSPSai123");
        Assert.assertTrue(inventoryPage.getEmptyMessage().contains("Không có dữ liệu tồn kho"));
    }

    @Test(priority = 6)
    public void testINV06_FilterValidStatus() {
        login("admin", "123456");
        InventoryPage inventoryPage = new InventoryPage(driver);
        inventoryPage.navigateToInventory();
        inventoryPage.filterByStatus("Bình thường");
        Assert.assertTrue(inventoryPage.getNumberOfRows() > 0, "Lỗi: Lọc 'Bình thường' không ra dữ liệu");
    }

    @Test(priority = 7)
    public void testINV07_FilterInvalidCondition() {
        login("admin", "123456");
        InventoryPage inventoryPage = new InventoryPage(driver);
        inventoryPage.navigateToInventory();
        inventoryPage.search("ThuocAo");
        inventoryPage.filterByStatus("Hết hạn");
        Assert.assertTrue(inventoryPage.getEmptyMessage().contains("Không có dữ liệu tồn kho"));
    }

    @Test(priority = 8)
    public void testINV08_DisplayOutOfStockProducts() {
        login("admin", "123456");
        InventoryPage inventoryPage = new InventoryPage(driver);
        inventoryPage.navigateToInventory();
        Assert.assertTrue(inventoryPage.isOutOfStockProductDisplayed(), "BUG (INV_08): Không hiển thị sản phẩm đã hết hàng!");
    }

    @Test(priority = 9)
    public void testINV09_NearExpiryWarning() {
        login("admin", "123456");
        InventoryPage inventoryPage = new InventoryPage(driver);
        inventoryPage.navigateToInventory();
        Assert.assertTrue(inventoryPage.isNearExpiryWarningDisplayed());
    }

    @Test(priority = 10)
    public void testINV10_Pagination() {
        login("admin", "123456");
        InventoryPage inventoryPage = new InventoryPage(driver);
        inventoryPage.navigateToInventory();
        Assert.assertTrue(inventoryPage.isPaginationDisplayed());
    }

    @Test(priority = 11)
    public void testINV11_LoadDataError() throws InterruptedException {
        login("admin", "123456");

        String mockScript = "var origFetch = window.fetch; window.fetch = async function(...args) { var url = args[0] || ''; if (typeof url === 'string' && (url.includes('inventory') || url.includes('products') || url.includes('stock'))) { return Promise.reject(new TypeError('Failed to fetch')); } return origFetch.apply(this, args); }; " +
                "var origOpen = XMLHttpRequest.prototype.open; XMLHttpRequest.prototype.open = function(method, url) { this._interceptUrl = url; origOpen.apply(this, arguments); }; var origSend = XMLHttpRequest.prototype.send; XMLHttpRequest.prototype.send = function(body) { var self = this; if (self._interceptUrl && (self._interceptUrl.includes('inventory') || self._interceptUrl.includes('products') || self._interceptUrl.includes('stock'))) { setTimeout(function() { if (self.onerror) self.onerror(new ProgressEvent('network error')); }, 100); return; } origSend.apply(this, arguments); };";
        ((JavascriptExecutor) driver).executeScript(mockScript);

        org.openqa.selenium.WebElement menuInventory = wait.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(By.xpath("//nav//a[contains(@href, '/inventory')]")));
        menuInventory.click();

        Thread.sleep(1500);

        boolean hasErrorMsg = !driver.findElements(By.xpath("//*[contains(text(),'Không tải được dữ liệu tồn kho') or contains(text(),'lỗi') or contains(@class, 'error') or contains(@class, 'toast')]")).isEmpty();
        Assert.assertTrue(hasErrorMsg, "Lỗi: Hệ thống không hiển thị thông báo 'Không tải được dữ liệu tồn kho' khi bị ngắt kết nối mạng");
    }
}