package com.gym_project.cucumber.component.training;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/component/training")
@ConfigurationParameter(key = "cucumber.glue",
        value = "com.gym_project.cucumber.component.training")
@ConfigurationParameter(key = "cucumber.plugin",
        value = "pretty,html:build/cucumber-reports/gym-project-training-cucumber.html,json:build/cucumber-reports/gym-project-training-cucumber.json")
public class CucumberTrainingComponentTest {
}
