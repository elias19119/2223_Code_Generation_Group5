Feature: Accounts

  Scenario: get all accounts
    Given User is employee
    When an Employee request list of all accounts
    Then all accounts returned
