package me.razorblack.stepdefinition;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

public class TestStepDef {

    @Given("test started successfully")
    public void testStartedSuccessfully() {
        System.out.println("Test started");
    }

    @When("test perform something")
    public void testPerformSomething() {
        System.out.println("Test does something");
    }

    @Then("test passed successfully")
    public void testPassedSuccessfully() {
        System.out.println("Test got passed");
    }

    @Then("test failed successfully")
    public void testFailedSuccessfully() {
        System.out.println("Test Failed");
        Assert.assertTrue(false);
    }
}
