Feature: Testing login

  Scenario: Login with valid credentials return a valid token
    Given Credentials are "employee@g.com" as a username and "user" as a password
    When Login
    Then show login http status 200


  Scenario: Login with invalid credentials throws an error
    Given Credentials are "invalidUser" as a username and "invalid" as a password
    When Login
    Then show login http status 401