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
public class CucumberWorkloadIntegrationTest {
}
