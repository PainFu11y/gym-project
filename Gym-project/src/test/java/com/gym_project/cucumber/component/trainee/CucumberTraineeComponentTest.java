package com.gym_project.cucumber.component.trainee;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/component/trainee")
@ConfigurationParameter(key = "cucumber.glue",
        value = "com.gym_project.cucumber.component.trainee")
@ConfigurationParameter(key = "cucumber.plugin",
        value = "pretty,html:build/cucumber-reports/gym-project-trainee-cucumber.html,json:build/cucumber-reports/gym-project-trainee-cucumber.json")
public class CucumberTraineeComponentTest {
}
