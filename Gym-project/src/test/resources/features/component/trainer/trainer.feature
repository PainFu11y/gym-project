Feature: Trainer Management
  As a gym administrator
  I want to manage trainer profiles
  So that trainers can be registered and assigned to trainees

  Scenario: Successfully register a new trainer
    Given a trainer registration with firstName "John" lastName "Smith" and trainingTypeId 1
    When the trainer registration request is submitted
    Then the response status is 200
    And the response contains trainer username "John.Smith"

  Scenario Outline: Trainer registration fails when required fields are missing
    Given a trainer registration with firstName "<firstName>" lastName "<lastName>" and trainingTypeId <typeId>
    When the trainer registration request is submitted
    Then the response status is 400

    Examples:
      | firstName | lastName | typeId |
      |           | Smith    | 1      |
      | John      |          | 1      |

  Scenario: Get trainer profile by username
    Given a trainer with username "John.Smith" exists
    When I request the profile of trainer "John.Smith"
    Then the response status is 200
    And the trainer firstName is "John"

  Scenario: Get profile for non-existent trainer returns 404
    Given no trainer with username "ghost" exists
    When I request the profile of trainer "ghost"
    Then the response status is 404

  Scenario: Update trainer profile successfully
    Given trainer "John.Smith" profile can be updated
    When I update trainer "John.Smith" with firstName "John" lastName "Smith" and active status true
    Then the response status is 200
    And the trainer username in response is "John.Smith"

  Scenario: Update trainer fails when username is blank
    When I update trainer with an empty username
    Then the response status is 400

  Scenario: Toggle trainer active status
    Given trainer "John.Smith" active status can be toggled
    When I toggle the status of trainer "John.Smith"
    Then the response status is 200

  Scenario: Toggle trainer status for non-existent trainer returns 404
    Given toggling trainer "ghost" status raises not found exception
    When I toggle the status of trainer "ghost"
    Then the response status is 404

  Scenario: Get list of trainers not assigned to a trainee
    Given trainee "Jane.Doe" has unassigned active trainers available
    When I request unassigned trainers for trainee "Jane.Doe"
    Then the response status is 200
    And the unassigned trainer list is not empty

  Scenario: Get filtered training list for trainer
    Given trainer "John.Smith" has training sessions
    When I request filtered trainings for trainer "John.Smith"
    Then the response status is 200
