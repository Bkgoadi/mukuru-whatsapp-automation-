Feature: WhatsApp Refer and Earn

  As a registered Mukuru customer
  I want to use the Refer and Earn feature via WhatsApp
  So that I can earn airtime by referring friends

  Scenario: Customer completes Refer and Earn flow via WhatsApp
    Given the WhatsApp home screen is displayed after signup
    And the customer opts in to personalised advertising if prompted
    When the customer selects Refer and Earn from the home menu
    And the customer selects Continue for the refer and earn offer
    And the customer selects to use their current number for the airtime reward
    And the customer taps Get My Link
    And the customer selects Continue after the referral link is created
    And the customer navigates home from the referral share message
    Then the Refer and Earn flow is completed successfully
