package com.gym_report.cucumber.component;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/component/workload")
@ConfigurationParameter(key = "cucumber.glue",
        value = "com.gym_report.cucumber.component")
@ConfigurationParameter(key = "cucumber.plugin",
        value = "pretty,html:build/cucumber-reports/gym-report-component-cucumber.html,json:build/cucumber-reports/gym-report-component-cucumber.json")
public class CucumberWorkloadComponentTest {
}
