package cucumber.examples.java.calculator;

import static org.testng.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import io.cucumber.java.DataTableType;
import io.cucumber.java.DocStringType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ShoppingStepdefs {
	private RpnCalculator calc = new RpnCalculator();

	@DataTableType
	public Grocery getGroceries(Map<String, String> entry) {
		return new Grocery(entry.get("name"), new Price(Integer.parseInt(entry.get("price"))));
	}

	@Given("the following groceries:")
	public void the_following_groceries(List<Grocery> groceries) {
		for (Grocery grocery : groceries) {
			calc.push(grocery.price.value);
			calc.push("+");
		}
	}

	@When("I pay {int}")
	public void i_pay(int amount) {
		calc.push(amount);
		calc.push("-");
	}

	@Then("my change should be {int}")
	public void my_change_should_be_(int change) {
		assertEquals(-calc.value().intValue(), change);
	}

	@DocStringType
	public Speech getSpeech(String text) {
		return new Speech(text);
	}

	@Given("the doc string is")
	public void the_doc_string_is(Speech speech) {
		System.out.println(speech);
	}

	public static class Speech {
		private String text;
		private int lines;
		private int words;

		public Speech(String text) {
			this.text = text;
			this.lines = text.split("[\\n\\r]+").length;
			this.words = text.split("[\\s]+").length;
		}
	}

	public static class Grocery {
		@SuppressWarnings("unused")
		private String name;
		private Price price;

		Grocery(String name, Price price) {
			this.name = name;
			this.price = price;
		}
	}

	public static final class Price {
		private int value;

		Price(int value) {
			this.value = value;
		}

		static Price fromString(String value) {
			return new Price(Integer.parseInt(value));
		}

	}
}
