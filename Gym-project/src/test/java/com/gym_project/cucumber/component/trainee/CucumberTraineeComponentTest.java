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
public class CucumberTraineeComponentTest {
}
