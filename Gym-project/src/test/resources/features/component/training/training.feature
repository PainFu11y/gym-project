Feature: Training Session Management
  As a trainer
  I want to create and delete training sessions
  So that trainee progress is accurately recorded

  Scenario: Successfully create a training session
    Given the training service accepts new sessions
    When I create a training with trainee "Jane.Doe" trainer "John.Smith" name "Yoga" on "2024-03-15" for 60 minutes
    Then the response status is 200

  Scenario Outline: Create training fails when required fields are missing
    When I create a training with trainee "<trainee>" trainer "<trainer>" name "<name>" on "<date>" for <duration> minutes
    Then the response status is 400

    Examples:
      | trainee  | trainer    | name | date       | duration |
      |          | John.Smith | Yoga | 2024-03-15 | 60       |
      | Jane.Doe |            | Yoga | 2024-03-15 | 60       |
      | Jane.Doe | John.Smith |      | 2024-03-15 | 60       |

  Scenario: Successfully delete an existing training session
    Given a training session with id 1 exists
    When I delete training session with id 1
    Then the response status is 200

  Scenario: Delete non-existent training returns 404
    Given no training session with id 999 exists
    When I delete training session with id 999
    Then the response status is 404
