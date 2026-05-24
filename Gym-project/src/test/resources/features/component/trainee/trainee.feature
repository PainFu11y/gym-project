Feature: Trainee Management
  As a gym administrator
  I want to manage trainee profiles
  So that trainees can register and track their training history

  Scenario: Successfully register a new trainee
    Given a trainee registration with firstName "Jane" and lastName "Doe"
    When the trainee registration request is submitted
    Then the response status is 200
    And the response contains username "Jane.Doe" and a generated password

  Scenario Outline: Registration fails when required fields are blank
    Given a trainee registration with firstName "<firstName>" and lastName "<lastName>"
    When the trainee registration request is submitted
    Then the response status is 400

    Examples:
      | firstName | lastName |
      |           | Doe      |
      | Jane      |          |

  Scenario: Get trainee profile by username
    Given a trainee with username "Jane.Doe" exists
    When I request the profile of trainee "Jane.Doe"
    Then the response status is 200
    And the trainee firstName is "Jane"

  Scenario: Get profile for non-existent trainee returns 404
    Given no trainee with username "ghost" exists
    When I request the profile of trainee "ghost"
    Then the response status is 404

  Scenario: Update trainee profile successfully
    Given trainee "Jane.Doe" profile can be updated successfully
    When I update trainee "Jane.Doe" with firstName "Jane" lastName "Doe" and active status true
    Then the response status is 200
    And the trainee username in response is "Jane.Doe"

  Scenario: Update trainee fails when username is blank
    When I update trainee with an empty username
    Then the response status is 400

  Scenario: Delete an existing trainee
    Given trainee "Jane.Doe" can be deleted
    When I delete trainee "Jane.Doe"
    Then the response status is 200

  Scenario: Delete non-existent trainee returns 404
    Given deleting trainee "ghost" raises not found exception
    When I delete trainee "ghost"
    Then the response status is 404

  Scenario: Toggle trainee active status
    Given trainee "Jane.Doe" active status can be toggled
    When I toggle the status of trainee "Jane.Doe"
    Then the response status is 200

  Scenario: Toggle status for non-existent trainee returns 404
    Given toggling status of "ghost" raises not found exception
    When I toggle the status of trainee "ghost"
    Then the response status is 404

  Scenario: Get filtered training list for trainee
    Given trainee "Jane.Doe" has one training session
    When I request filtered trainings for trainee "Jane.Doe"
    Then the response status is 200
    And the training list contains 1 entry

  Scenario: Get trainings fails when username is blank
    When I request filtered trainings with a blank username
    Then the response status is 400

  Scenario: Update trainee trainer list
    Given trainee "Jane.Doe" trainer list can be updated with "John.Smith"
    When I update trainer list for trainee "Jane.Doe" with trainer "John.Smith"
    Then the response status is 200
    And the trainer list contains trainer "John.Smith"

  Scenario: Update trainer list fails when trainee username is blank
    When I update trainer list with a blank trainee username
    Then the response status is 400
