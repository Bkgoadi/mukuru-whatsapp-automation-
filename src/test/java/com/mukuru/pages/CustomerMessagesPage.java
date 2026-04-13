package com.mukuru.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page object for the Mukuru Customer Messages (SMS) page.
 * URL: https://admin-staging8.internal.mukuru.com/sales/customer/{id}/sms/
 *
 * Shows "All SMS messages" with columns:
 * # | Date | Mobile | Type | OrderID | Content | SMS Gateway Provider | Delivery | Action
 */
public class CustomerMessagesPage {

    private final WebDriver     driver;
    private final WebDriverWait wait;

    private static final By PAGE_HEADING    = By.cssSelector("h3, h2, h1, .page-title");
    private static final By SMS_HEADING     = By.xpath("//*[contains(text(),'All SMS messages')]");
    private static final By TOTAL_RECORDS   = By.xpath("//*[contains(text(),'Total records')]");
    private static final By SMS_TABLE_ROWS  = By.cssSelector("table tbody tr");

    public CustomerMessagesPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    /**
     * Returns true when the "All SMS messages" heading is visible.
     */
    public boolean isPageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(SMS_HEADING)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns the total records text — e.g. "Total records : 1"
     */
    public String getTotalRecordsText() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(TOTAL_RECORDS))
                       .getText().trim();
        } catch (Exception e) {
            return "Total records not found";
        }
    }

    /**
     * Returns the number of SMS rows in the table.
     */
    public int getSmsCount() {
        try {
            return driver.findElements(SMS_TABLE_ROWS).size();
        } catch (Exception e) {
            return 0;
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
