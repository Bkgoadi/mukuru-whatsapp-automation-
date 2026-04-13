package com.mukuru.steps;

import com.mukuru.base.BrowserFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.testng.Assert.assertTrue;

/**
 * Login step definitions.
 * Matches the team's existing LoginStepDef pattern exactly.
 */
public class LoginStepDef extends BrowserFactory {

    @Given("user enters {string} as their username")
    public void user_enters_as_their_username(String username) {
        loginPage.enterUsername(username);
    }

    @And("user enters {string} as their password")
    public void user_enters_as_their_password(String password) {
        loginPage.enterPassword(password);
    }

    @When("user clicks login button")
    public void user_clicks_login_button() {
        loginPage.clickLoginButton();
    }

    @Then("user is logged in successfully")
    public void user_is_logged_in_successfully() {
        // After login the emulator page should be visible — not the login form
        assertTrue(
            !loginPage.isLoginPageDisplayed() || ussdPage.responseContains("msisdn") || ussdPage.responseContains("send"),
            "User is not logged in — still on login page"
        );
    }
}
