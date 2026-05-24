Feature: Workload Message Consumer to Storage Integration
  As the gym reporting system
  When a WorkloadMessage arrives from the ActiveMQ queue
  The trainer workload data in MongoDB should be updated accordingly

  Scenario: Consuming ADD message creates a new workload record
    Given no workload record exists for trainer "new.trainer"
    When the consumer receives an ADD message for trainer "new.trainer" with 60 minutes on "2024-01-15"
    Then the workload repository saves a record for trainer "new.trainer"
    And the saved record has 60 minutes in January 2024

  Scenario: Consuming ADD message adds duration to existing workload
    Given trainer "existing.trainer" already has 120 minutes in January 2024
    When the consumer receives an ADD message for trainer "existing.trainer" with 60 minutes on "2024-01-15"
    Then the workload repository saves a record for trainer "existing.trainer"
    And the saved record has 180 minutes in January 2024

  Scenario: Consuming DELETE message reduces existing workload
    Given trainer "active.trainer" already has 120 minutes in March 2024
    When the consumer receives a DELETE message for trainer "active.trainer" with 60 minutes on "2024-03-10"
    Then the workload repository saves a record for trainer "active.trainer"
    And the saved record has 60 minutes in March 2024

  Scenario: Consuming DELETE message removes month entry when duration reaches zero
    Given trainer "active.trainer" already has 60 minutes in March 2024
    When the consumer receives a DELETE message for trainer "active.trainer" with 60 minutes on "2024-03-10"
    Then the workload repository saves a record for trainer "active.trainer"
    And the saved record has no entry for March 2024

  Scenario: Consumer rejects message with missing trainer username
    When the consumer receives a message with a blank trainer username
    Then the message is rejected with an IllegalArgumentException

  Scenario: Consumer rejects message with missing training date
    When the consumer receives a message with a null training date
    Then the message is rejected with an IllegalArgumentException

  Scenario: Consumer rejects message with zero training duration
    When the consumer receives a message with duration 0
    Then the message is rejected with an IllegalArgumentException

  Scenario: Consumer rejects message with missing action type
    When the consumer receives a message with a null action type
    Then the message is rejected with an IllegalArgumentException
