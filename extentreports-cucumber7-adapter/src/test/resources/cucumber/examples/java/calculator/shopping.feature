@shop
Feature: Shopping

  Scenario: Give correct change
    Given the following groceries:
      | name  | price |
      | milk  |     9 |
      | bread |     7 |
      | soap  |     5 |
    When I pay 25
    Then my change should be 4

  Scenario: Doc String to Custom Object
    Given the doc string is
      """
      Hello there how r u?
      
      Doing great.
      Whats new?
      """
