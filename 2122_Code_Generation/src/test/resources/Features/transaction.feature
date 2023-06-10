Feature: Transaction

  Scenario: A Customer can transfer funds to another account
    Given User is customer
    And User has balance in their account to transfer 10
    When User initiates a fund transfer to another account with "valid" details
    Then the transaction is "successful"
    And the "recipient" account balance is updated
    And the "sender" account balance is updated

  Scenario: A Customer can transfer funds to another inactive account
    Given User is customer
    And User has balance in their account to transfer 10
    When User initiates a fund transfer to another account with "invalid" details
    Then the transaction is "unsuccessful"