Feature: Accounts

  Scenario: As an Employee get all accounts
    Given User is employee
    When an Employee request list of all accounts
    Then all accounts returned and show status 200

  Scenario: As an Employee I can create an account
    Given User is employee
    When Create account with valid details
    Then show http status code 201

  Scenario: Retrieve an account by valid IBAN as an Employee
    Given User is employee
    When Retrieve an account by "valid" user ID
    Then show http status code 200