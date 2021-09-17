package cucumber.examples.java.calculator;

import static java.text.DateFormat.MEDIUM;
import static java.text.DateFormat.getDateInstance;
import static java.util.Locale.ENGLISH;
import static org.testng.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


public class DateStepdefs {
    private String result;
    private DateCalculator calculator;

    @ParameterType(".*?")
	public Date isodate(String date) throws ParseException {
		return new SimpleDateFormat("yyyy-mm-dd").parse(date);
	}
    
    @Given("today is {isodate}")
    public void today_is(Date date) {
        calculator = new DateCalculator(date);
    }
    
    @ParameterType(".*?")
	public Date date(String date) throws ParseException {
		return getDateInstance(MEDIUM, ENGLISH).parse(date);
	}

    @When("I ask if {date} is in the past")
    public void I_ask_if_date_is_in_the_past(Date date) {
        result = calculator.isDateInThePast(date);
    }

    @Then("the result should be {string}")
    public void the_result_should_be(String expectedResult) {
        assertEquals(expectedResult, result);
    }
}
