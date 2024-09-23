package me.razorblack.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

import static me.razorblack.extentreports.constants.BaseConstants.*;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {PRETTY, CUSTOM_EXTENT_REPORT_PLUGIN},
        features = {TEST_SPECS_PATH},
        glue = {STEP_DEF_PATH},
        tags = "@Test",
        monochrome = true
)
public class TestRunner {
}
