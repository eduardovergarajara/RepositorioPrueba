package cl.edu.template.microservice.steps;

import cl.edu.template.microservice.CucumberSpringContextConfiguration; // Importa la clase de configuración
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.ScenarioScope;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@ScenarioScope
public class EndpointSteps {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final int port; // Obtener el puerto de la configuración

    private String requestBody;
    private String latestResponseBody;
    private HttpStatusCode latestResponseStatus;
    private Throwable latestError;
    private String latestCurlCommand;


    public EndpointSteps(CucumberSpringContextConfiguration config, WebClient webClient, ObjectMapper objectMapper) {
        this.port = config.getPort(); // Obtener el puerto de la instancia de configuración
        this.webClient = webClient; // Usar el WebClient inyectado
        this.objectMapper = objectMapper; // Usar el ObjectMapper inyectado
    }


    @Given("el cuerpo de la petición:")
    public void elCuerpoDeLaPeticion(String docString) {
        this.requestBody = docString;
    }

    @When("envío una petición POST a {string} con un cuerpo vacío")
    public void envioUnaPeticionPOSTAConUnCuerpoVacio(String path) {
        String url = "http://localhost:" + port + path;

        // GENERACIÓN DEL CURL para cuerpo vacío
        StringBuilder curl = new StringBuilder("curl -X POST ");
        curl.append(url);
        curl.append(" -H \"Content-Type: application/json\"");
        this.latestCurlCommand = curl.toString();
        // FIN GENERACIÓN DEL CURL

        try {
            webClient.post().uri(path) // WebClient ya tiene el baseUrl
                    .contentType(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(String.class)
                    .doOnSuccess(response -> {
                        latestResponseStatus = response.getStatusCode();
                        latestResponseBody = response.getBody();
                        latestError = null;
                    })
                    .doOnError(WebClientResponseException.class, e -> {
                        latestResponseStatus = e.getStatusCode();
                        latestResponseBody = e.getResponseBodyAsString();
                        latestError = e;
                    })
                    .block();
        } catch (Exception e) {
            latestError = e;
        }
    }

    @Then("la respuesta Status HTTP debe ser {int}")
    public void laRespuestaStatusHTTPDebeSer(int statusCode) {
        if (latestResponseStatus != null) {
            assertEquals(statusCode, latestResponseStatus.value(), "El código de estado HTTP no coincide");
        } else if (latestError != null) {
            fail("Se esperaba una respuesta con status " + statusCode + ", pero la petición falló con: " + latestError.getMessage());
        } else {
            fail("No se recibió ninguna respuesta ni error.");
        }
    }

    @AfterStep
    public void addCurlToReport(Scenario scenario) {
        if (latestCurlCommand != null && !latestCurlCommand.isEmpty()) {
            scenario.attach(latestCurlCommand, "text/plain", "Comando cURL Ejecutado");
            latestCurlCommand = null;
        }
    }
}