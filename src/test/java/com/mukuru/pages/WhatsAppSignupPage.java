package com.mukuru.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

/**
 * Page object for the Mukuru USSD emulator — WhatsApp/Infobip flow.
 *
 * THREE interaction styles seen across the flow:
 *
 *  1. TEXT INPUT    — type a number/text into the option field + Submit
 *  2. RADIO BUTTON  — click a radio label + Submit
 *  3. SCREEN BUTTON — click a standalone button directly (NO Submit needed)
 *                     e.g. "Yes", "No", "Yes, Opt-In", "No additional photo",
 *                          "Accept & Sign up", "Retry"
 *
 *  IMAGE UPLOAD — file input above the option field.
 *                 Send file path to input, then click Submit.
 */
public class WhatsAppSignupPage {

    private final WebDriver     driver;
    private final WebDriverWait wait;

    // Selectors confirmed working (same /ussd/ page as USSD project)
    private static final By OPTION_INPUT    = By.id("msg");
    private static final By SUBMIT_BTN      = By.id("submit");
    private static final By PROVIDER_SELECT = By.cssSelector("select[name='provider'], select#provider, select[id*='provider']");
    private static final By CHANGE_MSISDN   = By.id("change_msisdn");
    private static final By CHANGE_BTN      = By.id("change");
    private static final By RESPONSE_AREA   = By.id("screen");
    private static final By FILE_INPUT      = By.cssSelector("input[type='file']");
    private static final By RADIO_BUTTONS   = By.cssSelector("input[type='radio']");

    private static final Path RESULTS_BASE = Paths.get(
            System.getProperty("user.home"), "Downloads", "test-results");

    public WhatsAppSignupPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // =========================================================================
    // Setup
    // =========================================================================

    public void selectInfobipProvider() {
        WebElement sel = wait.until(ExpectedConditions.visibilityOfElementLocated(PROVIDER_SELECT));
        new Select(sel).getOptions().stream()
            .filter(o -> o.getText().toLowerCase().contains("infobip")
                      || o.getText().toLowerCase().contains("whatsapp"))
            .findFirst()
            .ifPresent(o -> new Select(sel).selectByVisibleText(o.getText()));
        System.out.println("[WA] Provider: Infobip/WhatsApp selected");
        pause(500);
    }

    public void changePhoneNumber(String msisdn) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(CHANGE_MSISDN));
        field.clear();
        field.sendKeys(msisdn);
        wait.until(ExpectedConditions.elementToBeClickable(CHANGE_BTN)).click();
        System.out.println("[WA] MSISDN: " + msisdn);
        pause(1500);
    }

    // =========================================================================
    // Style 1 — Text input
    // =========================================================================

    public String sendOption(String option) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(OPTION_INPUT));
        input.clear();
        input.sendKeys(option);
        clickSubmit();
        pause(1500);
        String r = getResponseText();
        System.out.println("[WA] Input='" + option + "' | Screen: " + r);
        return r;
    }

    // =========================================================================
    // Style 2 — Radio button
    // =========================================================================

    public String selectRadioOption(String labelText) {
        System.out.println("[WA] Radio: " + labelText);
        // Strategy 1: label XPath
        try {
            WebElement label = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//label[contains(normalize-space(.),'" + labelText + "')]")));
            label.click();
            pause(300);
            clickSubmit();
            pause(1500);
            return getResponseText();
        } catch (Exception ignored) {}
        // Strategy 2: index match
        try {
            List<WebElement> labels = driver.findElements(By.tagName("label"));
            List<WebElement> radios = driver.findElements(RADIO_BUTTONS);
            for (int i = 0; i < labels.size(); i++) {
                if (labels.get(i).getText().trim().contains(labelText)) {
                    (i < radios.size() ? radios.get(i) : labels.get(i)).click();
                    pause(300);
                    clickSubmit();
                    pause(1500);
                    return getResponseText();
                }
            }
        } catch (Exception ignored) {}
        // Fallback: numeric
        System.out.println("[WA] Radio fallback to sendOption(1) for: " + labelText);
        return sendOption("1");
    }

    // =========================================================================
    // Style 3 — Screen button click (no Submit needed)
    // e.g. "Yes", "No", "Yes, Opt-In", "No additional photo", "Accept & Sign up"
    // =========================================================================

    public String clickScreenButton(String buttonText) {
        System.out.println("[WA] Button click: " + buttonText);
        // Strategy 1: exact button text
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space(text())='" + buttonText + "']")));
            btn.click();
            pause(1500);
            return getResponseText();
        } catch (Exception ignored) {}
        // Strategy 2: partial button text
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(normalize-space(text()),'" + buttonText + "')]")));
            btn.click();
            pause(1500);
            return getResponseText();
        } catch (Exception ignored) {}
        // Strategy 3: any element with button role or btn class
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@role='button' and contains(normalize-space(text()),'" + buttonText + "')] | " +
                         "//*[contains(@class,'btn') and contains(normalize-space(text()),'" + buttonText + "')]")));
            btn.click();
            pause(1500);
            return getResponseText();
        } catch (Exception ignored) {}
        // Fallback: numeric mapping
        System.out.println("[WA] Button not found, numeric fallback for: " + buttonText);
        return sendOption(mapButtonToNumber(buttonText));
    }

    private String mapButtonToNumber(String text) {
        String t = text.toLowerCase();
        if (t.contains("yes") || t.contains("opt-in") || t.contains("accept") || t.contains("continue")) return "1";
        if (t.contains("no") || t.contains("retry") || t.contains("no additional")) return "2";
        if (t.contains("home")) return "0";
        return "1";
    }

    // =========================================================================
    // Screen type detection
    // =========================================================================

    public boolean isRadioButtonScreen() {
        try {
            return !driver.findElements(RADIO_BUTTONS).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /** True when the screen has standalone action buttons (not Submit/Quit/Change). */
    public boolean isButtonScreen() {
        try {
            List<WebElement> buttons = driver.findElements(By.tagName("button"));
            for (WebElement b : buttons) {
                String t = b.getText().trim().toLowerCase();
                if (b.isDisplayed() && !t.isEmpty()
                        && !t.equals("submit") && !t.equals("quit") && !t.equals("change")) {
                    return true;
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    /** True when the image file upload input is visible. */
    public boolean isImageUploadScreen() {
        try {
            List<WebElement> inputs = driver.findElements(FILE_INPUT);
            return !inputs.isEmpty() && inputs.get(0).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // =========================================================================
    // Image upload
    // =========================================================================

    public String uploadImage(String absoluteImagePath) {
        System.out.println("[WA] Uploading: " + absoluteImagePath);
        try {
            WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(FILE_INPUT));
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].style.display='block'; arguments[0].style.visibility='visible';", fileInput);
            fileInput.sendKeys(absoluteImagePath);
            System.out.println("[WA] File selected");
            pause(1000);
            clickSubmit();
            pause(2500);
        } catch (Exception e) {
            System.err.println("[WA] Upload failed: " + e.getMessage());
        }
        return getResponseText();
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private void clickSubmit() {
        wait.until(ExpectedConditions.elementToBeClickable(SUBMIT_BTN)).click();
    }

    public String getResponseText() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(RESPONSE_AREA))
                       .getText().trim();
        } catch (Exception e) {
            return driver.findElement(By.tagName("body")).getText();
        }
    }

    public boolean responseContains(String text) {
        return getResponseText().toLowerCase().contains(text.toLowerCase());
    }

    public void takeScreenshot(String filename) {
        try {
            Path dir = RESULTS_BASE.resolve("whatsapp").resolve("passed");
            Files.createDirectories(dir);
            Path file = dir.resolve(filename + ".png");
            byte[] data = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Files.write(file, data);
            System.out.println("SCREENSHOT SAVED: " + file);
        } catch (Exception e) {
            System.err.println("Screenshot failed: " + e.getMessage());
        }
    }

    private void pause(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
