package cl.edu.template.microservice.steps;

import cl.edu.template.microservice.utils.ApiResponse;
import cl.edu.template.microservice.utils.ScenarioHolder;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Clase que contiene los hooks de Cucumber.
 * Utiliza @Autowired para inyectar dependencias de Spring en los hooks.
 */
public class CucumberHooks {

    // Inyectamos las Step Definitions para acceder a la última respuesta de la API.
    // Es importante que estas Step Definitions sean beans de Spring.
    @Autowired
    private BadRequestSteps badRequestSteps; // Asume que ya tienes esta clase
    @Autowired
    private EndpointSteps endpointSteps; // Asume que ya tienes esta clase

    /**
     * Hook que se ejecuta antes de cada escenario de Cucumber.
     * Establece el objeto Scenario en ScenarioHolder para que sea accesible globalmente.
     *
     * @param scenario El objeto Scenario actual de Cucumber.
     */
    @Before(order = 1) // Se ejecuta primero
    public void beforeScenario(Scenario scenario) {
        ScenarioHolder.set(scenario);
    }

    /**
     * Hook que se ejecuta después de cada escenario de Cucumber.
     * Adjunta el comando cURL y la respuesta al informe de Cucumber.
     * Limpia el ScenarioHolder.
     *
     * @param scenario El objeto Scenario actual de Cucumber.
     */
    @After
    public void afterScenario(Scenario scenario) {
        // Obtener la última respuesta de la API de las Step Definitions
        ApiResponse latestApiResponse = null;
        if (badRequestSteps != null) {
            latestApiResponse = badRequestSteps.getApiResponse();
        }
        if (latestApiResponse == null && endpointSteps != null) {
            latestApiResponse = endpointSteps.getApiResponse();
        }

        if (latestApiResponse != null) {
            // Adjuntar el comando cURL al informe
            if (latestApiResponse.getCurlCommand() != null) {
                scenario.attach(latestApiResponse.getCurlCommand(), "text/plain", "Comando cURL Ejecutado");
            }

            // Adjuntar el cuerpo de la respuesta (si existe)
            if (latestApiResponse.getResponseBody() != null) {
                scenario.attach(latestApiResponse.getResponseBody(), "application/json", "Cuerpo de la Respuesta");
            }

            // Adjuntar información de la excepción (si existe)
            if (latestApiResponse.getError() != null) { // ¡Cambiado a getError()!
                scenario.attach("Excepción: " + latestApiResponse.getError().getMessage(), "text/plain", "Detalles de la Excepción");
            }
        }

        // Limpiar el ThreadLocal para evitar fugas de memoria
        ScenarioHolder.remove();
    }
}
