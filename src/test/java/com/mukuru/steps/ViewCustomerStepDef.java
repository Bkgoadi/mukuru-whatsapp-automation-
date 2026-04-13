package com.mukuru.steps;

import com.mukuru.base.BrowserFactory;
import com.mukuru.pages.CustomerDetailPage;
import com.mukuru.pages.CustomerMessagesPage;
import com.mukuru.pages.CustomerSummaryPage;
import com.mukuru.pages.UsersPage;
import io.cucumber.java.en.*;

import static org.testng.Assert.*;

/**
 * Complete step definitions for the customer view flow:
 *
 *  1. Users list → select newest customer → click VIEW
 *  2. Customer detail page → click name in orange banner
 *  3. Sales summary page → click Messages tab
 *  4. Messages page → verify SMS records → end
 */
public class ViewCustomerStepDef extends BrowserFactory {

    private UsersPage            usersPage;
    private CustomerDetailPage   customerDetailPage;
    private CustomerSummaryPage  customerSummaryPage;
    private CustomerMessagesPage customerMessagesPage;

    private String firstCustomerUsername;
    private String firstCustomerName;
    private String firstCustomerMobile;

    // ── Step 1: Users list ────────────────────────────────────────────────

    @Given("the admin is on the Users list page")
    public void the_admin_is_on_the_users_list_page() {
        usersPage = new UsersPage(driver);
        usersPage.navigateTo();
        assertTrue(usersPage.isUsersTableDisplayed(),
            "Users table not visible — may not be logged in");
        System.out.println("[Users Page] Loaded successfully");
    }

    @When("the admin selects the first customer in the list")
    public void the_admin_selects_the_first_customer_in_the_list() {
        firstCustomerUsername = usersPage.getFirstCustomerUsername();
        firstCustomerName     = usersPage.getFirstCustomerName();
        firstCustomerMobile   = usersPage.getFirstCustomerMobile();

        System.out.println("==============================================");
        System.out.println("Newest customer found:");
        System.out.println("Username : " + firstCustomerUsername);
        System.out.println("Name     : " + firstCustomerName);
        System.out.println("Mobile   : " + firstCustomerMobile);
        System.out.println("==============================================");

        assertFalse(firstCustomerUsername.isBlank(), "First customer username is blank");
    }

    @And("the admin clicks the View button for that customer")
    public void the_admin_clicks_the_view_button_for_that_customer() {
        usersPage.clickViewForFirstCustomer();
        customerDetailPage = new CustomerDetailPage(driver);
        assertTrue(customerDetailPage.isPageDisplayed(),
            "Customer detail page did not load after clicking VIEW");
        System.out.println("[Detail Page] Heading: " + customerDetailPage.getPageHeading());
    }

    // ── Step 2: Click name in orange banner ───────────────────────────────

    @When("the admin clicks the customer name in the top banner")
    public void the_admin_clicks_the_customer_name_in_the_top_banner() {
        String bannerName = customerDetailPage.getBannerCustomerName();
        System.out.println("[Banner] Customer name: " + bannerName);
        assertFalse(bannerName.isBlank(), "Banner customer name is blank");
        customerDetailPage.clickCustomerNameInBanner();
        customerSummaryPage = new CustomerSummaryPage(driver);
        assertTrue(customerSummaryPage.isPageDisplayed(),
            "Sales summary page did not load after clicking banner name");
        System.out.println("[Summary Page] Heading: " + customerSummaryPage.getPageHeading());
    }

    // ── Step 3: Click Messages tab ────────────────────────────────────────

    @When("the admin clicks the Messages tab")
    public void the_admin_clicks_the_messages_tab() {
        assertTrue(customerSummaryPage.isMessagesTabDisplayed(),
            "Messages tab not visible on summary page");
        customerSummaryPage.clickMessagesTab();
        customerMessagesPage = new CustomerMessagesPage(driver);
    }

    // ── Step 4: Verify Messages page ─────────────────────────────────────

    @Then("the admin sees the Messages page for the customer")
    public void the_admin_sees_the_messages_page_for_the_customer() {
        assertTrue(customerMessagesPage.isPageDisplayed(),
            "Messages page (All SMS messages) not visible");

        String url     = customerMessagesPage.getCurrentUrl();
        String records = customerMessagesPage.getTotalRecordsText();
        int    count   = customerMessagesPage.getSmsCount();

        assertTrue(url.contains("sms") || url.contains("messages"),
            "Expected SMS/messages URL but got: " + url);

        System.out.println("==============================================");
        System.out.println("SUCCESS: Messages page verified");
        System.out.println("Customer  : " + firstCustomerName);
        System.out.println("Mobile    : " + firstCustomerMobile);
        System.out.println("Records   : " + records);
        System.out.println("SMS rows  : " + count);
        System.out.println("URL       : " + url);
        System.out.println("==============================================");
    }

    @And("a screenshot of the Messages page is taken")
    public void a_screenshot_of_the_messages_page_is_taken() {
        try {
            byte[] bytes = ((org.openqa.selenium.TakesScreenshot) driver)
                    .getScreenshotAs(org.openqa.selenium.OutputType.BYTES);
            java.nio.file.Path dir = java.nio.file.Paths.get(
                    System.getProperty("user.dir"), "screenshots", "passed");
            java.nio.file.Files.createDirectories(dir);
            String filename = "customer_messages_"
                    + firstCustomerName.replace(" ", "_")
                    + "_" + System.currentTimeMillis() + ".png";
            java.nio.file.Files.write(dir.resolve(filename), bytes);
            System.out.println("SCREENSHOT SAVED: " + dir.resolve(filename));
        } catch (Exception e) {
            System.out.println("[Screenshot] Failed: " + e.getMessage());
        }
        System.out.println("[Screenshot] Messages page captured — test complete");
    }
}
