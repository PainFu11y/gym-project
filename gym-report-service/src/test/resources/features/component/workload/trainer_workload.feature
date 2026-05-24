Feature: Trainer Workload API
  As a reporting service consumer
  I want to manage trainer workload data via REST API
  So that training hours are accurately stored and retrievable

  Scenario: Successfully submit an ADD workload update
    Given the workload service processes updates without error
    When I send a POST request to update workload with trainer "john.smith" action ADD duration 60 on "2024-01-15"
    Then the workload response status is 200

  Scenario: Successfully submit a DELETE workload update
    Given the workload service processes updates without error
    When I send a POST request to update workload with trainer "john.smith" action DELETE duration 30 on "2024-01-15"
    Then the workload response status is 200

  Scenario: POST with transaction ID header uses that ID in processing
    Given the workload service processes updates without error
    When I send a POST request with transaction ID "tx-abc-123" for trainer "john.smith"
    Then the workload response status is 200
    And the workload service was called with transaction ID "tx-abc-123"

  Scenario Outline: Workload update fails when required fields are missing
    When I send a POST request with missing <field>
    Then the workload response status is 400

    Examples:
      | field             |
      | trainerUsername   |
      | trainingDate      |
      | actionType        |
      | trainingDuration  |

  Scenario: Empty request body is rejected
    When I send a POST request with an empty body
    Then the workload response status is 400

  Scenario: Get workload summary for existing trainer
    Given trainer "john.smith" has workload of 180 minutes in April 2025
    When I request workload for trainer "john.smith"
    Then the workload response status is 200
    And the workload summary contains trainer username "john.smith"
    And the workload summary contains 180 minutes in April 2025

  Scenario: Get workload for non-existent trainer returns 404
    Given no workload data exists for trainer "ghost.trainer"
    When I request workload for trainer "ghost.trainer"
    Then the workload response status is 404
