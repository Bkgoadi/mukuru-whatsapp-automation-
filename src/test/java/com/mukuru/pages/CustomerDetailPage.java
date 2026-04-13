package com.mukuru.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page object for the Mukuru Admin Customer Detail page.
 * URL: https://admin-staging8.internal.mukuru.com/users/view/details/{id}/
 */
public class CustomerDetailPage {

    private final WebDriver     driver;
    private final WebDriverWait wait;

    private static final By PAGE_HEADING      = By.cssSelector("h1, h2, .page-title");
    // Link on the customer detail page that navigates to the customer's sales summary.
    // Tries /sales/customer/ link first (most specific), then falls back to header xpath.
    private static final By BANNER_CUSTOMER_NAME = By.cssSelector("a[href*='/sales/customer/']");
    private static final By BANNER_FALLBACK       = By.xpath("//*[@id='header']/div[2]/p[1]/a");

    public CustomerDetailPage(WebDriver driver) {
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

    public String getBannerCustomerName() {
        WebElement link = resolveBannerLink();
        return link.getText().trim();
    }

    public void clickCustomerNameInBanner() {
        // Wait for any loading overlay to clear before clicking
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("overlay")));
        } catch (Exception ignored) {}

        WebElement link = resolveBannerLink();
        System.out.println("[CustomerDetailPage] Clicking banner link: "
                + link.getText().trim() + " → " + link.getAttribute("href"));
        // JS click bypasses any residual overlay
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
    }

    /** Returns the customer sales-summary link, falling back to the header xpath. */
    private WebElement resolveBannerLink() {
        try {
            return wait.until(ExpectedConditions.presenceOfElementLocated(BANNER_CUSTOMER_NAME));
        } catch (Exception e) {
            return wait.until(ExpectedConditions.presenceOfElementLocated(BANNER_FALLBACK));
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
