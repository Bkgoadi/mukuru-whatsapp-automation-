package com.mukuru.TestRunner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * TestNG runner for all Cucumber scenarios.
 * Run this class directly in IntelliJ to execute all feature files.
 */
@CucumberOptions(
    features  = "src/test/resources",
    glue      = "com.mukuru.steps",
    plugin    = {
        "pretty",
        "html:target/cucumber-reports/cucumber.html",
        "json:target/cucumber-reports/cucumber.json"
    },
    monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {
}
