package cl.edu.template.microservice.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.stereotype.Component;
import io.cucumber.spring.ScenarioScope;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@ScenarioScope
public class BadRequestSteps {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final int port;

    private String requestBody;
    private String latestResponseBody;
    private HttpStatusCode latestResponseStatus;
    private Throwable latestError;
    private String latestCurlCommand;

    private Scenario scenario;

    public BadRequestSteps(cl.edu.template.microservice.CucumberSpringContextConfiguration config, WebClient webClient, ObjectMapper objectMapper) {
        this.port = config.getPort();
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    @Before
    public void setupScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    @Given("el body del request:")
    public void queElCuerpoDeLaPeticionEs(String requestBody) {
        this.requestBody = requestBody;
        this.latestResponseBody = null;
        this.latestResponseStatus = null;
        this.latestError = null;
        this.latestCurlCommand = null;
    }

    @When("realizo una petición POST a {string}")
    public void realizoUnaPeticionPOSTA(String path) {
        String url = "http://localhost:" + port + path;
        latestCurlCommand = "curl -X POST -H \"Content-Type: application/json\" -d '" + requestBody + "' " + url;

        try {
            webClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(requestBody))
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
                    .doOnError(throwable -> {
                        if (!(throwable instanceof WebClientResponseException)) {
                            System.err.println("Error genérico en el flujo WebClient: " + throwable.getClass().getName() + " - " + throwable.getMessage());
                            latestError = throwable;
                        }
                    })
                    .block();
        } catch (Exception e) {
            System.err.println("Excepción capturada durante la llamada WebClient.block(): " + e.getClass().getName() + " - " + e.getMessage());
            latestError = e;
        } finally {
            // <--- MODIFICACIÓN 1: Solo adjuntar el comando cURL aquí
            if (scenario != null) {
                if (latestCurlCommand != null && !latestCurlCommand.isEmpty()) {
                    scenario.attach(latestCurlCommand, "text/plain", "Comando cURL Ejecutado");
                }
                // La respuesta obtenida NO se adjunta aquí ahora
            }
        }
    }

    @Then("la respuesta HTTP debe ser {int}")
    public void laRespuestaHTTPDebeSer(int statusCode) {
        if (latestResponseStatus != null) {
            assertEquals(statusCode, latestResponseStatus.value(), "El código de estado HTTP no coincide");
        } else if (latestError != null) {
            fail("Se esperaba una respuesta con status " + statusCode + ", pero la petición falló con: " + latestError.getMessage());
        } else {
            fail("No se recibió ninguna respuesta ni error.");
        }
    }

    @Then("el cuerpo de la respuesta JSON debe contener:")
    public void elCuerpoDeLaRespuestaJSONDebeContener(String expectedJson) throws Exception {
        assertNotNull(latestResponseBody, "El cuerpo de la respuesta no puede ser nulo.");
        JSONAssert.assertEquals(expectedJson, latestResponseBody, JSONCompareMode.LENIENT);

        // <--- MODIFICACIÓN 2: Adjuntar la respuesta obtenida aquí
        if (scenario != null && latestResponseBody != null && !latestResponseBody.isEmpty()) {
            scenario.attach(latestResponseBody, "application/json", "Respuesta Obtenida");
        }
    }

    // El método @AfterStep ha sido completamente eliminado en las versiones anteriores para este propósito.
}