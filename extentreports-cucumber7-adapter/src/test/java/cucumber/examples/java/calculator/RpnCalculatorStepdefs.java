package cucumber.examples.java.calculator;

import static org.testng.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class RpnCalculatorStepdefs {
    private RpnCalculator calc;

    @Given("a calculator I just turned on")
    public void a_calculator_I_just_turned_on() {
        calc = new RpnCalculator();
    }

    @When("I add {int} and {int}")
    public void adding(int arg1, int arg2) {
        calc.push(arg1);
        calc.push(arg2);
        calc.push("+");
    }

    @Given("I press {string}")
    public void I_press(String what) {
        calc.push(what);
    }

    @Then("the result is {int}")
    public void the_result_is(double expected) {
        assertEquals(expected, calc.value());
    }

    @Before("not @foo")
    public void before(Scenario scenario) {
        scenario.log("Runs BEFORE scenarios *not* tagged with @foo");
    }

    @After("not @foo")
    public void after(Scenario scenario) {
    	scenario.log("Runs AFTER scenarios *not* tagged with @foo");
    }
    
    @DataTableType
	public Entry getEntries(Map<String, String> entry) {
		return new Entry(Integer.valueOf(entry.get("first")),
                Integer.valueOf(entry.get("second")),
                entry.get("operation"));
	}

    @Given("the previous entries:")
    public void thePreviousEntries(List<Entry> entries) {
        for (Entry entry : entries) {
            calc.push(entry.first);
            calc.push(entry.second);
            calc.push(entry.operation);
        }
    }

    static final class Entry {
        private final Integer first;
        private final Integer second;
        private final String operation;

        Entry(Integer first, Integer second, String operation) {
            this.first = first;
            this.second = second;
            this.operation = operation;
        }
    }
}
