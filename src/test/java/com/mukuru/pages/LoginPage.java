package com.mukuru.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page object for the Mukuru USSD emulator login screen.
 * URL: https://admin-staging8.internal.mukuru.com/login/
 */
public class LoginPage {

    private final WebDriver     driver;
    private final WebDriverWait wait;

    private static final By USERNAME_FIELD = By.cssSelector("input[name='username'], input[type='text'], input[id='username']");
    private static final By PASSWORD_FIELD = By.cssSelector("input[name='password'], input[type='password'], input[id='password']");
    private static final By LOGIN_BUTTON   = By.cssSelector("button[type='submit'], input[type='submit'], button.btn-login, .login-btn");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void enterUsername(String username) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME_FIELD));
        field.clear();
        field.sendKeys(username);
    }

    public void enterPassword(String password) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(PASSWORD_FIELD));
        field.clear();
        field.sendKeys(password);
    }

    public void clickLoginButton() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(LOGIN_BUTTON));
        button.click();
    }

    public void loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    public boolean isLoginPageDisplayed() {
        return driver.getCurrentUrl().contains("/login/");
    }
}
