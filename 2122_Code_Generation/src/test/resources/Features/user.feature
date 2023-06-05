Feature: Testing Users

  Scenario: As an Employee I can retrieve all users accounts
    Given the user is an employee
    When get all users
    Then show http status 200

  Scenario: Creating a user returns http status created
    Given the user is an employee
    When creating a new user "valid"
    Then show http status 201

  Scenario: Retrieving a user with Id returns http status ok
    Given the user is an employee
    When retrieving a user by id
    Then show http status 200

  Scenario: Deleting a user returns http status ok
    Given the user is an employee
    When deleting a user by id
    Then show http status 200

  Scenario: Creating null user throws exception
    Given  the user is an employee
    When creating a new user "invalid"
    Then show http status 400