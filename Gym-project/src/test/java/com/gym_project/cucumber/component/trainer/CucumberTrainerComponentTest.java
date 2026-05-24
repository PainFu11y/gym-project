package com.gym_project.cucumber.component.trainer;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/component/trainer")
@ConfigurationParameter(key = "cucumber.glue",
        value = "com.gym_project.cucumber.component.trainer")
@ConfigurationParameter(key = "cucumber.plugin",
        value = "pretty,html:build/cucumber-reports/gym-project-trainer-cucumber.html,json:build/cucumber-reports/gym-project-trainer-cucumber.json")
public class CucumberTrainerComponentTest {
}
