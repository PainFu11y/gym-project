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
public class CucumberWorkloadComponentTest {
}
