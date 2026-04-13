package com.mukuru.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page object for the Mukuru Admin Users list page.
 * URL: https://admin-staging8.internal.mukuru.com/users/
 * Always targets the FIRST row — the newest customer.
 */
public class UsersPage {

    private final WebDriver     driver;
    private final WebDriverWait wait;

    private static final By TABLE_ROWS = By.cssSelector("table tbody tr");

    public UsersPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void navigateTo() {
        driver.get("https://admin-staging8.internal.mukuru.com/users/");
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(TABLE_ROWS));
    }

    public boolean isUsersTableDisplayed() {
        try {
            return !wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(TABLE_ROWS)).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public String getFirstCustomerUsername() {
        return getFirstRow().findElements(By.tagName("td")).get(1).getText().trim();
    }

    public String getFirstCustomerName() {
        return getFirstRow().findElements(By.tagName("td")).get(3).getText().trim();
    }

    public String getFirstCustomerMobile() {
        return getFirstRow().findElements(By.tagName("td")).get(5).getText().trim();
    }

    public void clickViewForFirstCustomer() {
        WebElement row = getFirstRow();
        WebElement viewBtn;
        try {
            // Prefer a link whose href contains "view/details"
            viewBtn = row.findElement(By.cssSelector("a[href*='view/details']"));
        } catch (Exception e1) {
            try {
                // Fall back to a link with text "View" or "Show" in the row
                viewBtn = row.findElement(
                    By.xpath(".//a[normalize-space()='View' or normalize-space()='Show']"));
            } catch (Exception e2) {
                // Last resort: first link in the row
                viewBtn = row.findElement(By.tagName("a"));
            }
        }
        System.out.println("[UsersPage] Clicking VIEW: href=" + viewBtn.getAttribute("href")
                + " text='" + viewBtn.getText() + "'");
        viewBtn.click();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    private WebElement getFirstRow() {
        List<WebElement> rows = wait.until(
            ExpectedConditions.presenceOfAllElementsLocatedBy(TABLE_ROWS));
        if (rows.isEmpty()) throw new RuntimeException("No rows found in Users table");
        return rows.get(0);
    }
}
