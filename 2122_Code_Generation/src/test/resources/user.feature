Feature: Testing Users

  Scenario: Creating a user returns http status created
    Given the user is an employee
    When creating a new user
    Then show http status 201

  Scenario: Retrieving a user with Id returns http status ok
    Given the user is an employee
    When retrieving a user with id "1001"
    Then show http status 200

  Scenario: Deleting a user returns http status ok
    Given the user is an employee
    When deleting a user with id "1001"
    Then show http status 200


  Scenario: Logging in provides with token
    Given the user provides "JohnDoe@gmail.com" and "johnnie123"
    Then the web token is returned

  Scenario: Creating null user throws exception
    Given  the user is an employee
    When creating a null user
    Then show http status 400

Feature: Login features


  Scenario: User tries to login with correct credentials
    When User tries to enter "JohnDoe@gmail.com" as email and "johnnie123" as password
    Then Http Status 200 is displayed

