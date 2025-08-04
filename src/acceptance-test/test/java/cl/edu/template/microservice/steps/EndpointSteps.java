package cl.edu.template.microservice.steps;

import cl.edu.template.microservice.utils.ApiResponse;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
// Removido: import org.springframework.beans.factory.annotation.Autowired;
// Removido: import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// Extiende de la clase base CucumberBaseTest
public class EndpointSteps extends CucumberBaseTest {

    @When("envío una petición POST a {string} con un cuerpo vacío")
    public void envioUnaPeticionPOSTAConUnCuerpoVacio(String path) {
        performPost(path, ""); // Pasa una cadena vacía como cuerpo
    }

    @Then("la respuesta Status HTTP debe ser {int}")
    public void laRespuestaStatusHTTPDebeSer(int expectedStatus) {
        assertNotNull(latestApiResponse, "La respuesta de la API no debería ser nula.");
        assertNotNull(latestApiResponse.getStatusCode(), "El código de estado de la respuesta no debería ser nulo.");
        assertEquals(expectedStatus, latestApiResponse.getStatusCode().value(),
                "Se esperaba una respuesta con status " + expectedStatus + ", pero la petición falló con: " +
                        (latestApiResponse.getError() != null ? latestApiResponse.getError().getMessage() : "Desconocido"));
    }

    @Override
    public ApiResponse getApiResponse() {
        return latestApiResponse;
    }
}