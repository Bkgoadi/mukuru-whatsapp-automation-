Feature: WhatsApp New Customer Signup

  As a new user
  I want to register via WhatsApp (Infobip) self-signup
  So that I can access Mukuru services

  Scenario: Successfully create a new Mukuru customer via WhatsApp signup
    Given the admin opens the USSD emulator for WhatsApp signup
    And a new unique customer is generated for WhatsApp signup
    When the admin selects Infobip WhatsApp as the provider
    And the admin enters the generated number and clicks Change
    And the customer selects English as their language
    And the customer selects they are new to Mukuru
    And the customer selects Sign up for Mukuru
    And the customer selects Passport as their ID document type
    And the customer selects DR Congo as their country
    And the customer enters their passport number for WhatsApp
    And the customer enters their first name for WhatsApp
    And the customer enters their surname for WhatsApp
    And the customer selects their gender for WhatsApp
    And the customer enters their date of birth for WhatsApp
    And the customer confirms their name details
    And the customer enters their city for WhatsApp
    And the customer enters their suburb for WhatsApp
    And the customer enters their street address for WhatsApp
    And the customer confirms their address for WhatsApp
    And the customer accepts terms and signs up
    And the customer opts out of marketing communications
    And the customer selects Continue to upload their identity document
    And the customer selects occupation Government Public Service Worker
    And the customer uploads their passport image
    And the customer confirms the passport image is clear
    And the customer uploads their selfie image
    And the customer selects no additional photo for the back of document
    And the customer uploads the document image again
    And the customer confirms the selfie image is clear
    And the customer navigates home
    Then the WhatsApp customer signup is completed successfully
