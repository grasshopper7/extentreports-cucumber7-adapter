@Test
  Feature: Extent report functionality test
    @Success
    Scenario: Success scenario for extent report
      Given test started successfully
      When  test perform something
      Then  test passed successfully

    @Fail
    Scenario: Failure scenario for extent report
      Given test started successfully
      When  test perform something
      Then  test failed successfully