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

  Scenario: Retrieve an account by invalid IBAN as an Employee
    Given User is employee
    When Retrieve an account by "invalid" user ID
    Then show http status code 400

  Scenario: Update an account details as an Employee
    Given User is employee
    When Update the details of an account with "valid" ID
    Then show http status code 200

  Scenario: Update an account details as an Employee with invalid ID
    Given User is employee
    When Update the details of an account with "invalid" ID
    Then show http status code 400

  Scenario: Delete an account as an Employee
    Given User is employee
    When Delete an account
    Then show http status code 200