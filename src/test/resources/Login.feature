Feature: Admin Login

  Scenario: Successfully log in to the Mukuru admin portal
    Given user enters "admin.user" as their username
    And user enters "v@ltarI" as their password
    When user clicks login button
    Then user is logged in successfully
