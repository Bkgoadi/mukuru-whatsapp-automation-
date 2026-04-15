package com.mukuru.steps;

import com.mukuru.base.BrowserFactory;
import com.mukuru.pages.WhatsAppSignupPage;
import com.mukuru.utils.TestDataGenerator;
import io.cucumber.java.en.*;

import java.io.File;
import java.net.URL;

import static org.testng.Assert.*;

public class WhatsAppSignupStepDef extends BrowserFactory {

    private WhatsAppSignupPage whatsAppPage;
    private TestDataGenerator  customer;

    private static final String IMAGE_PATH = resolveImagePath();

    private static String resolveImagePath() {
        try {
            URL r = WhatsAppSignupStepDef.class.getClassLoader()
                    .getResource("images/Test ID card.jpeg");
            if (r != null) return new File(r.toURI()).getAbsolutePath();
        } catch (Exception ignored) {}
        return System.getProperty("user.dir") + "/src/test/resources/images/Test ID card.jpeg";
    }

    // =========================================================================
    // Setup
    // =========================================================================

    @Given("the admin opens the USSD emulator for WhatsApp signup")
    public void the_admin_opens_the_ussd_emulator_for_whatsapp_signup() {
        whatsAppPage = new WhatsAppSignupPage(driver);
        if (loginPage.isLoginPageDisplayed()) {
            loginPage.loginAs("admin.user", "v@ltarI");
        }
        driver.get("https://admin-staging8.internal.mukuru.com/ussd/");
        System.out.println("[WA] Emulator opened");
    }

    @And("a new unique customer is generated for WhatsApp signup")
    public void a_new_unique_customer_is_generated_for_whatsapp_signup() {
        customer = new TestDataGenerator();
        System.out.println("=== Customer: " + customer + " ===");
        System.out.println("=== Image: " + IMAGE_PATH + " ===");
    }

    // =========================================================================
    // Provider + MSISDN
    // =========================================================================

    @When("the admin selects Infobip WhatsApp as the provider")
    public void the_admin_selects_infobip_whatsapp_as_the_provider() {
        whatsAppPage.selectInfobipProvider();
    }

    @And("the admin enters the generated number and clicks Change")
    public void the_admin_enters_the_generated_number_and_clicks_change() {
        whatsAppPage.changePhoneNumber(customer.getMsisdn());
    }

    // =========================================================================
    // Language (radio)
    // =========================================================================

    @And("the customer selects English as their language")
    public void the_customer_selects_english_as_their_language() {
        String r = whatsAppPage.isRadioButtonScreen()
            ? whatsAppPage.selectRadioOption("English")
            : whatsAppPage.sendOption("1");
        assertFalse(r.isBlank());
    }

    // =========================================================================
    // New to Mukuru (radio or numbered)
    // =========================================================================

    @And("the customer selects they are new to Mukuru")
    public void the_customer_selects_they_are_new_to_mukuru() {
        String r = whatsAppPage.isRadioButtonScreen()
            ? whatsAppPage.selectRadioOption("I'm new")
            : whatsAppPage.sendOption("1");
        assertFalse(r.isBlank());
    }

    @And("the customer selects Sign up for Mukuru")
    public void the_customer_selects_sign_up_for_mukuru() {
        String r = whatsAppPage.isRadioButtonScreen()
            ? whatsAppPage.selectRadioOption("Sign up for Mukuru")
            : whatsAppPage.sendOption("1");
        assertFalse(r.isBlank());
    }

    // =========================================================================
    // ID document type (radio) — "What ID document do you have?"
    // =========================================================================

    @And("the customer selects Passport as their ID document type")
    public void the_customer_selects_passport_as_their_id_document_type() {
        String r = whatsAppPage.isRadioButtonScreen()
            ? whatsAppPage.selectRadioOption("Passport")
            : whatsAppPage.sendOption("1");
        assertFalse(r.isBlank());
        System.out.println("[WA] Passport selected");
    }

    // =========================================================================
    // Country of document issuance — "Which country issued your document?"
    // =========================================================================

    @And("the customer selects DR Congo as their country")
    public void the_customer_selects_dr_congo_as_their_country() {
        String r = whatsAppPage.isRadioButtonScreen()
            ? whatsAppPage.selectRadioOption("DR Congo")
            : whatsAppPage.sendOption("5");
        assertFalse(r.isBlank());
        System.out.println("[WA] Country: DR Congo");
    }

    // =========================================================================
    // Personal details (text input)
    // =========================================================================

    @And("the customer enters their passport number for WhatsApp")
    public void the_customer_enters_their_passport_number_for_whatsapp() {
        assertFalse(whatsAppPage.sendOption(customer.getPassportNumber()).isBlank());
    }

    @And("the customer enters their first name for WhatsApp")
    public void the_customer_enters_their_first_name_for_whatsapp() {
        assertFalse(whatsAppPage.sendOption(customer.getFirstName()).isBlank());
    }

    @And("the customer enters their surname for WhatsApp")
    public void the_customer_enters_their_surname_for_whatsapp() {
        assertFalse(whatsAppPage.sendOption(customer.getLastName()).isBlank());
    }

    @And("the customer selects their gender for WhatsApp")
    public void the_customer_selects_their_gender_for_whatsapp() {
        assertFalse(whatsAppPage.sendOption(customer.getGender()).isBlank());
    }

    @And("the customer enters their date of birth for WhatsApp")
    public void the_customer_enters_their_date_of_birth_for_whatsapp() {
        assertFalse(whatsAppPage.sendOption(customer.getDateOfBirth()).isBlank());
    }

    // =========================================================================
    // Name review + address (USSD-style)
    // =========================================================================

    @And("the customer confirms their name details")
    public void the_customer_confirms_their_name_details() {
        assertFalse(whatsAppPage.sendOption("1").isBlank());
    }

    @And("the customer enters their city for WhatsApp")
    public void the_customer_enters_their_city_for_whatsapp() {
        assertFalse(whatsAppPage.sendOption("Johannesburg").isBlank());
    }

    @And("the customer enters their suburb for WhatsApp")
    public void the_customer_enters_their_suburb_for_whatsapp() {
        assertFalse(whatsAppPage.sendOption("Yeoville").isBlank());
    }

    @And("the customer enters their street address for WhatsApp")
    public void the_customer_enters_their_street_address_for_whatsapp() {
        assertFalse(whatsAppPage.sendOption("1 Baker St").isBlank());
    }

    @And("the customer confirms their address for WhatsApp")
    public void the_customer_confirms_their_address_for_whatsapp() {
        // Confirms address → navigates to "Accept & Sign up" T&C screen
        assertFalse(whatsAppPage.sendOption("1").isBlank());
    }

    // =========================================================================
    // Terms — "Accept & Sign up" screen (button or numbered)
    // =========================================================================

    @And("the customer accepts terms and signs up")
    public void the_customer_accepts_terms_and_signs_up() {
        String r = whatsAppPage.isButtonScreen()
            ? whatsAppPage.clickScreenButton("Accept")
            : whatsAppPage.sendOption("1");
        assertFalse(r.isBlank());
        System.out.println("[WA] T&C accepted");
    }

    // =========================================================================
    // Marketing opt-in — "Get the latest Mukuru product promotions..." screen
    // =========================================================================

    @And("the customer opts out of marketing communications")
    public void the_customer_opts_out_of_marketing_communications() {
        String r = whatsAppPage.isButtonScreen()
            ? whatsAppPage.clickScreenButton("No")
            : whatsAppPage.sendOption("2");
        assertTrue(
            r.contains("Champion") || r.contains("Mukuru Family") || r.contains("signing up"),
            "Expected champions screen after marketing opt-out. Got: " + r
        );
        System.out.println("[WA] Marketing: No selected → champions screen");
    }

    @And("the customer opts in to marketing communications")
    public void the_customer_opts_in_to_marketing_communications() {
        String r = whatsAppPage.isButtonScreen()
            ? whatsAppPage.clickScreenButton("Yes, Opt-In")
            : whatsAppPage.sendOption("1");
        assertTrue(
            r.contains("Champion") || r.contains("Mukuru Family") || r.contains("signing up"),
            "Expected champions screen after marketing opt-in. Got: " + r
        );
        System.out.println("[WA] Marketing: Yes Opt-In selected → champions screen");
    }

    // =========================================================================
    // Champions screen — Continue (1) → document upload flow
    // =========================================================================

    @And("the customer selects Continue to upload their identity document")
    public void the_customer_selects_continue_to_upload_their_identity_document() {
        String r = whatsAppPage.sendOption("1");
        assertFalse(r.isBlank());
        System.out.println("[WA] Continue (1) — moving to document upload");
    }

    // =========================================================================
    // Occupation
    // =========================================================================

    @And("the customer selects occupation Government Public Service Worker")
    public void the_customer_selects_occupation_government_public_service_worker() {
        String r = whatsAppPage.isRadioButtonScreen()
            ? whatsAppPage.selectRadioOption("Government")
            : whatsAppPage.sendOption("13");
        assertFalse(r.isBlank());
        System.out.println("[WA] Occupation: 13");
    }

    // =========================================================================
    // Passport image upload
    // =========================================================================

    @And("the customer uploads their passport image")
    public void the_customer_uploads_their_passport_image() {
        String r = whatsAppPage.uploadImage(IMAGE_PATH);
        assertFalse(r.isBlank());
        whatsAppPage.takeScreenshot("wa_01_passport_upload");
        System.out.println("[WA] Passport uploaded. Screen: " + r);
    }

    @And("the customer confirms the passport image is clear")
    public void the_customer_confirms_the_passport_image_is_clear() {
        String r = whatsAppPage.isButtonScreen()
            ? whatsAppPage.clickScreenButton("Yes")
            : whatsAppPage.sendOption("1");
        assertFalse(r.isBlank());
        whatsAppPage.takeScreenshot("wa_02_passport_confirmed");
        System.out.println("[WA] Passport clear confirmed. Screen: " + r);
    }

    // =========================================================================
    // Selfie upload
    // =========================================================================

    @And("the customer uploads their selfie image")
    public void the_customer_uploads_their_selfie_image() {
        String r = whatsAppPage.uploadImage(IMAGE_PATH);
        assertFalse(r.isBlank());
        whatsAppPage.takeScreenshot("wa_03_selfie_upload");
        System.out.println("[WA] Selfie uploaded. Screen: " + r);
    }

    // =========================================================================
    // Back of document — "No additional photo" button
    // =========================================================================

    @And("the customer selects no additional photo for the back of document")
    public void the_customer_selects_no_additional_photo_for_the_back_of_document() {
        String r = whatsAppPage.isButtonScreen()
            ? whatsAppPage.clickScreenButton("No additional photo")
            : whatsAppPage.sendOption("2");
        assertFalse(r.isBlank());
        whatsAppPage.takeScreenshot("wa_04_no_back_photo");
        System.out.println("[WA] No additional photo. Screen: " + r);
    }

    // =========================================================================
    // Upload document image again (back/additional photo prompt)
    // =========================================================================

    @And("the customer uploads the document image again")
    public void the_customer_uploads_the_document_image_again() {
        String r = whatsAppPage.uploadImage(IMAGE_PATH);
        assertFalse(r.isBlank());
        whatsAppPage.takeScreenshot("wa_05_doc_reupload");
        System.out.println("[WA] Document re-uploaded. Screen: " + r);
    }

    // =========================================================================
    // Confirm selfie/document clear — "Yes" button
    // =========================================================================

    @And("the customer confirms the selfie image is clear")
    public void the_customer_confirms_the_selfie_image_is_clear() {
        String r = whatsAppPage.isButtonScreen()
            ? whatsAppPage.clickScreenButton("Yes")
            : whatsAppPage.sendOption("1");
        assertFalse(r.isBlank());
        whatsAppPage.takeScreenshot("wa_06_selfie_confirmed");
        System.out.println("[WA] Selfie/doc clear confirmed. Screen: " + r);
    }

    // =========================================================================
    // Navigate home
    // =========================================================================

    @And("the customer navigates home")
    public void the_customer_navigates_home() {
        assertFalse(whatsAppPage.sendOption("0").isBlank());
        System.out.println("[WA] Home (0)");
    }

    // =========================================================================
    // Verify completion
    // =========================================================================

    @Then("the WhatsApp customer signup is completed successfully")
    public void the_whatsapp_customer_signup_is_completed_successfully() {
        String response = whatsAppPage.getResponseText();

        boolean success =
            response.contains("documents have been sent for verification") ||
            response.contains("MukuruChampions") ||
            response.contains("Mukuru Family") ||
            response.contains("Send money now") ||
            response.contains("Get Mukuru Card");

        assertTrue(success, "Expected success screen but got: " + response);

        whatsAppPage.takeScreenshot("wa_07_signup_complete");

        System.out.println("==============================================");
        System.out.println("SUCCESS: WhatsApp signup complete!");
        System.out.println("MSISDN   : " + customer.getMsisdn());
        System.out.println("Passport : " + customer.getPassportNumber());
        System.out.println("Name     : " + customer.getFullName());
        System.out.println("DOB      : " + customer.getDateOfBirth());
        System.out.println("==============================================");
    }
}
