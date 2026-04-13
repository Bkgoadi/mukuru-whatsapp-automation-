package com.mukuru.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page object for the Mukuru Customer Sales Summary page.
 * URL: https://admin-staging8.internal.mukuru.com/sales/customer/{id}/summary/
 *
 * Black tab navigation includes:
 * Summary | Details | Edit Customer | Documents | Sales | Orders |
 * Subscriptions | Cards | Notes | Messages | Verification SMSs | ...
 */
public class CustomerSummaryPage {

    private final WebDriver     driver;
    private final WebDriverWait wait;

    private static final By PAGE_HEADING  = By.cssSelector("h1, h2, .page-title");
    private static final By MESSAGES_TAB  = By.xpath(
        "//a[normalize-space(text())='Messages'] | //li/a[normalize-space(text())='Messages']"
    );

    public CustomerSummaryPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public boolean isPageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getPageHeading() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING)).getText().trim();
    }

    public boolean isMessagesTabDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(MESSAGES_TAB)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickMessagesTab() {
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(MESSAGES_TAB));
        System.out.println("[SummaryPage] Clicking Messages tab");
        tab.click();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
