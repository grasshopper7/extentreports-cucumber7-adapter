package cucumber.examples.java.calculator;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import io.cucumber.java.AfterAll;
import io.cucumber.java.AfterStep;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.github.bonigarcia.wdm.WebDriverManager;

public class ScreenShotStepDefinition {

	private WebDriver driver;

	@And("Go to {word}")
	public void visitweb(String site) throws Exception {
		System.out.println(site);
		driver.get(site);
		Thread.sleep(5000);
	}

	@BeforeAll()
	public static void beforeAll() {
		System.out.println("universe before");
	}

	@AfterAll()
	public static void afterAll() {
		System.out.println("universe all");
	}

	@BeforeStep(value = "@website")
	public void beforeSite() {
		System.out.println("BEFORE SITE");
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().window().maximize();
	}

	@AfterStep(value = "@website")
	public void afterSite(Scenario scenario) {
		System.out.println("AFTER SITE");

		TakesScreenshot ts = (TakesScreenshot) driver;
		byte[] screenshot = ts.getScreenshotAs(OutputType.BYTES);
		scenario.log("this is my failure message……….");
		scenario.attach(screenshot, "image/png", "");
		driver.quit();
	}
}
