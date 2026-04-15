package com.mukuru.steps;

import com.mukuru.base.BrowserFactory;
import com.mukuru.pages.WhatsAppSignupPage;
import io.cucumber.java.en.*;

import static org.testng.Assert.*;

public class WhatsAppReferAndEarnStepDef extends BrowserFactory {

    private WhatsAppSignupPage whatsAppPage;

    // =========================================================================
    // Setup — reuses existing browser session from signup scenario
    // =========================================================================

    @Given("the WhatsApp home screen is displayed after signup")
    public void the_whatsapp_home_screen_is_displayed_after_signup() {
        whatsAppPage = new WhatsAppSignupPage(driver);
        String screen = whatsAppPage.getResponseText();
        System.out.println("[RAE] Refer & Earn starting. Screen: " + screen);
    }

    // =========================================================================
    // Personalised advertising — may or may not appear
    // =========================================================================

    @And("the customer opts in to personalised advertising if prompted")
    public void the_customer_opts_in_to_personalised_advertising_if_prompted() {
        String screen = whatsAppPage.getResponseText();
        if (screen.toLowerCase().contains("personalised advertising")) {
            String r = whatsAppPage.isButtonScreen()
                ? whatsAppPage.clickScreenButton("Yes, Opt-In")
                : whatsAppPage.sendOption("1");
            System.out.println("[RAE] Personalised advertising: opted in. Screen: " + r);
        } else {
            System.out.println("[RAE] No personalised advertising prompt — skipped.");
        }
    }

    // =========================================================================
    // Home menu — select Refer and Earn (8th option)
    // =========================================================================

    @When("the customer selects Refer and Earn from the home menu")
    public void the_customer_selects_refer_and_earn_from_the_home_menu() {
        String r = whatsAppPage.isRadioButtonScreen()
            ? whatsAppPage.selectRadioOption("Refer and Earn")
            : whatsAppPage.sendOption("8");
        assertFalse(r.isBlank());
        System.out.println("[RAE] Refer and Earn selected. Screen: " + r);
    }

    // =========================================================================
    // "Refer a friend and earn R300" screen — Continue
    // =========================================================================

    @And("the customer selects Continue for the refer and earn offer")
    public void the_customer_selects_continue_for_the_refer_and_earn_offer() {
        String r = whatsAppPage.isButtonScreen()
            ? whatsAppPage.clickScreenButton("Continue")
            : whatsAppPage.sendOption("1");
        assertFalse(r.isBlank());
        System.out.println("[RAE] Continue (offer). Screen: " + r);
    }

    // =========================================================================
    // "Please confirm if you want to use this number" screen — Use this number
    // =========================================================================

    @And("the customer selects to use their current number for the airtime reward")
    public void the_customer_selects_to_use_their_current_number_for_the_airtime_reward() {
        String r = whatsAppPage.isButtonScreen()
            ? whatsAppPage.clickScreenButton("Use this number")
            : whatsAppPage.sendOption("1");
        assertFalse(r.isBlank());
        System.out.println("[RAE] Use this number. Screen: " + r);
    }

    // =========================================================================
    // "Here's how it works" screen — Get My Link
    // =========================================================================

    @And("the customer taps Get My Link")
    public void the_customer_taps_get_my_link() {
        String r = whatsAppPage.isButtonScreen()
            ? whatsAppPage.clickScreenButton("Get My Link")
            : whatsAppPage.sendOption("1");
        assertFalse(r.isBlank());
        whatsAppPage.takeScreenshot("rae_01_get_my_link");
        System.out.println("[RAE] Get My Link. Screen: " + r);
    }

    // =========================================================================
    // "Your unique referral link has been created" screen — Continue
    // =========================================================================

    @And("the customer selects Continue after the referral link is created")
    public void the_customer_selects_continue_after_the_referral_link_is_created() {
        String r = whatsAppPage.isButtonScreen()
            ? whatsAppPage.clickScreenButton("Continue")
            : whatsAppPage.sendOption("1");
        assertFalse(r.isBlank());
        whatsAppPage.takeScreenshot("rae_02_link_created");
        System.out.println("[RAE] Continue (link created). Screen: " + r);
    }

    // =========================================================================
    // Share message screen — Home
    // =========================================================================

    @And("the customer navigates home from the referral share message")
    public void the_customer_navigates_home_from_the_referral_share_message() {
        String r = whatsAppPage.isButtonScreen()
            ? whatsAppPage.clickScreenButton("Home")
            : whatsAppPage.sendOption("0");
        assertFalse(r.isBlank());
        whatsAppPage.takeScreenshot("rae_03_home");
        System.out.println("[RAE] Home. Screen: " + r);
    }

    // =========================================================================
    // Verify
    // =========================================================================

    @Then("the Refer and Earn flow is completed successfully")
    public void the_refer_and_earn_flow_is_completed_successfully() {
        String screen = whatsAppPage.getResponseText();
        boolean success =
            screen.contains("good to see you") ||
            screen.contains("Send money now") ||
            screen.contains("Refer and Earn") ||
            screen.contains("referral");
        assertTrue(success, "Expected home screen after Refer & Earn. Got: " + screen);
        whatsAppPage.takeScreenshot("rae_04_complete");
        System.out.println("==============================================");
        System.out.println("SUCCESS: Refer and Earn flow complete!");
        System.out.println("==============================================");
    }
}
