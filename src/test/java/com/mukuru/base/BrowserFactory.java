package com.mukuru.base;

import com.mukuru.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Base class for all step definitions.
 * Holds the shared static WebDriver and page objects.
 * Selenium 4.20+ includes Selenium Manager which automatically
 * downloads and manages ChromeDriver — no WebDriverManager needed.
 *
 * Lifecycle is managed by {@link com.mukuru.steps.Hooks}.
 */
public class BrowserFactory {

    public static WebDriver  driver;
    public static LoginPage  loginPage;

    public static void initDriver() {
        if (driver != null) {
            // Verify the existing session is still alive before reusing it
            try {
                driver.getCurrentUrl();
                return; // session is healthy, reuse it
            } catch (Exception e) {
                // Session is dead — reset and fall through to reinitialise
                driver    = null;
                loginPage = null;
            }
        }
        ChromeOptions options = new ChromeOptions();
        options.setAcceptInsecureCerts(true);
        options.addArguments("--start-maximized");
        // Uncomment to run without a visible browser window:
        // options.addArguments("--headless=new");

        // Selenium Manager (built into Selenium 4.20+) handles ChromeDriver automatically
        driver    = new ChromeDriver(options);
        loginPage = new LoginPage(driver);

        // Navigate to admin — will redirect to /login/ if not authenticated
        driver.get("https://admin-staging8.internal.mukuru.com/");
    }

    public static void quitDriver() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.out.println("[BrowserFactory] Warning during quit: " + e.getMessage());
            } finally {
                driver    = null;
                loginPage = null;
            }
        }
    }
}
