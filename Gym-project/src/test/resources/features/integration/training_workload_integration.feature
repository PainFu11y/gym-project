Feature: Training to Workload Message Integration
  As the Gym system
  When a training session is created or deleted
  The trainer workload report service should receive a notification message

  Scenario: Creating a training publishes an ADD workload message
    Given a trainer "John.Trainer" with firstName "John" and lastName "Trainer" is registered
    And a trainee "Jane.Trainee" is registered
    When a training session is created for trainer "John.Trainer" and trainee "Jane.Trainee" lasting 60 minutes
    Then a workload message with action ADD is sent for trainer "John.Trainer"
    And the message contains training duration 60 minutes

  Scenario: Deleting a training publishes a DELETE workload message
    Given a training with id 5 belonging to trainer "John.Trainer" with duration 90 minutes exists
    When the training with id 5 is deleted by trainer "John.Trainer"
    Then a workload message with action DELETE is sent for trainer "John.Trainer"
    And the message contains training duration 90 minutes

  Scenario: No workload message is sent when trainee does not exist
    Given a trainer "John.Trainer" exists
    And no trainee with username "unknown.trainee" exists
    When a training is attempted for trainer "John.Trainer" and trainee "unknown.trainee"
    Then no workload message is sent

  Scenario: No workload message is sent when trainer does not exist
    Given a trainee "Jane.Trainee" exists
    And no trainer with username "unknown.trainer" exists
    When a training is attempted for trainer "unknown.trainer" and trainee "Jane.Trainee"
    Then no workload message is sent
