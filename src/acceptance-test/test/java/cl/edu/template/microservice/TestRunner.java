package cl.edu.template.microservice;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

// Clase de configuración para ejecutar Cucumber con JUnit 5 (JUnit Platform)
@Suite
@IncludeEngines("cucumber") // Indica a JUnit Platform que use el motor de Cucumber
@SelectClasspathResource("features") // Especifica la ubicación de tus archivos .feature (relativo al classpath de tests)
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "cl.edu.template.microservice") // Paquete base de tus Step Definitions y configuración de Spring
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty,html:build/reports/cucumber/acceptance-test-report/index.html") // Plugins para los reportes HTML
public class TestRunner {
}