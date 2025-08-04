package cl.edu.template.microservice;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@SuiteDisplayName("Pruebas de Aceptación con Cucumber")
@ConfigurationParameter(
        key = Constants.GLUE_PROPERTY_NAME,
        value = "cl.edu.template.microservice," + // Contiene CucumberSpringContextConfiguration
                "cl.edu.template.microservice.steps" // No contiene TestAcceptanceConfig
)
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty,html:build/reports/cucumber/acceptance-test-report/index.html")
public class TestRunner {
}
