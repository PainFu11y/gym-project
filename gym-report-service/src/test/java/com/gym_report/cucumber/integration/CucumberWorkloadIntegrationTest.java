package com.gym_report.cucumber.integration;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/integration")
@ConfigurationParameter(key = "cucumber.glue",
        value = "com.gym_report.cucumber.integration")
@ConfigurationParameter(key = "cucumber.plugin",
        value = "pretty,html:build/cucumber-reports/gym-report-integration-cucumber.html,json:build/cucumber-reports/gym-report-integration-cucumber.json")
public class CucumberWorkloadIntegrationTest {
}
