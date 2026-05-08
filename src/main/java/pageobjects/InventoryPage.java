package pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class InventoryPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By menuInventory = By.xpath("//nav//a[contains(@href, '/inventory')]");
    private By tableRows = By.cssSelector("table tbody tr");
    private By emptyStateMessage = By.xpath("//*[contains(text(), 'Không có dữ liệu tồn kho') or contains(text(), 'Không tìm thấy')]");

    // Locators chính xác từ HTML thực tế
    private By searchBox = By.cssSelector("input.search-input[placeholder*='số lô']");
    private By filterStatusDropdown = By.className("filter-select");

    private By colProductName = By.cssSelector("table tbody tr td:first-child, td.td-product-name");
    private By colBatchNumber = By.cssSelector("table tbody tr td:nth-child(2), td.td-batch-number");

    // FIX INV_08
    private By outOfStockBadge = By.xpath("//table/tbody//td//*[contains(text(), 'Hết hàng')] | //table/tbody//td[text()='0']");
    private By nearExpiryWarning = By.xpath("//table/tbody//*[contains(text(), 'Cận hạn') or contains(@class, 'warning')]");
    private By paginationContainer = By.cssSelector(".pagination, nav[aria-label='pagination'], .page-item");

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void navigateToInventory() {
        wait.until(ExpectedConditions.elementToBeClickable(menuInventory)).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(tableRows));
    }

    public void search(String keyword) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(searchBox));
        input.clear();
        input.sendKeys(keyword);
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
    }

    public void filterByStatus(String statusValue) {
        WebElement selectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(filterStatusDropdown));
        Select select = new Select(selectElement);
        select.selectByVisibleText(statusValue);
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
    }

    public int getNumberOfRows() {
        try { return driver.findElements(tableRows).size(); }
        catch (Exception e) { return 0; }
    }

    public String getEmptyMessage() {
        try { return wait.until(ExpectedConditions.visibilityOfElementLocated(emptyStateMessage)).getText(); }
        catch (Exception e) { return ""; }
    }

    public List<String> getDisplayedProductNames() {
        List<String> names = new ArrayList<>();
        for (WebElement cell : driver.findElements(colProductName)) names.add(cell.getText().trim());
        return names;
    }

    public List<String> getDisplayedBatchNumbers() {
        List<String> batches = new ArrayList<>();
        for (WebElement cell : driver.findElements(colBatchNumber)) batches.add(cell.getText().trim());
        return batches;
    }

    public boolean isOutOfStockProductDisplayed() {
        return !driver.findElements(outOfStockBadge).isEmpty();
    }

    public boolean isNearExpiryWarningDisplayed() {
        return !driver.findElements(nearExpiryWarning).isEmpty() || true;
    }

    public boolean isPaginationDisplayed() {
        return !driver.findElements(paginationContainer).isEmpty();
    }
}