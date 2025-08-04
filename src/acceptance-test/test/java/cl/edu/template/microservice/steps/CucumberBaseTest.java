package cl.edu.template.microservice.steps;

import cl.edu.template.microservice.utils.ApiResponse;
import cl.edu.template.microservice.utils.ScenarioHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Scenario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort; // Importar para inyectar el puerto
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Clase base para todas las Step Definitions de Cucumber.
 * Proporciona métodos comunes para interactuar con la API
 * utilizando WebTestClient y manejar las respuestas.
 */
public abstract class CucumberBaseTest {

    @Autowired // Inyectamos WebTestClient (viene de TestAcceptanceConfig)
    protected WebTestClient webTestClient;

    @Autowired // Inyectamos ObjectMapper (viene de Spring Boot)
    protected ObjectMapper objectMapper;

    @LocalServerPort // Inyectamos el puerto dinámico del servidor de prueba
    private int port;

    protected ApiResponse latestApiResponse; // Almacena la última respuesta de la API

    /**
     * Realiza una petición POST a un endpoint dado.
     *
     * @param path El ruta del endpoint (ej. "/text/validated").
     * @param requestBody El cuerpo de la petición como String JSON.
     */
    protected void performPost(String path, String requestBody) {
        String curlCommand = buildCurlCommand("POST", path, requestBody);

        try {
            // Ejecutamos la petición y obtenemos el FluxExchangeResult.
            FluxExchangeResult<String> result = webTestClient.post()
                    .uri(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(requestBody))
                    .exchange() // Esto ejecuta la petición y devuelve ResponseSpec
                    .returnResult(String.class); // Obtiene el resultado como FluxExchangeResult<String>

            HttpStatusCode statusCode = result.getStatus(); // Acceder al estado desde FluxExchangeResult
            String responseBody = null;
            Exception caughtError = null;

            try {
                // Intentamos leer el cuerpo de la respuesta como String.
                byte[] bodyBytes = result.getResponseBodyContent(); // ¡Corregido a getResponseBodyContent()!
                if (bodyBytes != null) {
                    responseBody = new String(bodyBytes);
                }
            } catch (Exception e) {
                // Capturamos cualquier error al leer el cuerpo
                System.err.println("Error al leer el cuerpo de la respuesta: " + e.getMessage());
                caughtError = e;
            }
            // Asignamos la respuesta procesada a latestApiResponse
            latestApiResponse = new ApiResponse(statusCode, responseBody, caughtError, curlCommand);

        } catch (WebClientResponseException e) {
            // Capturamos excepciones de respuesta HTTP (ej. 4xx, 5xx)
            System.err.println("WebClientResponseException en CucumberBaseTest: Status " + e.getStatusCode() + ", Body: " + e.getResponseBodyAsString() + ", Message: " + e.getMessage());
            latestApiResponse = new ApiResponse(e.getStatusCode(), e.getResponseBodyAsString(), e, curlCommand);
        } catch (Exception e) {
            // Capturamos otras excepciones generales (ej. problemas de conexión)
            System.err.println("Excepción general en CucumberBaseTest.performPost(): " + e.getClass().getName() + " - " + e.getMessage());
            latestApiResponse = new ApiResponse(null, null, e, curlCommand);
        }

        // Adjuntar el comando cURL y la respuesta al escenario si está disponible
        Scenario currentScenario = ScenarioHolder.get();
        if (currentScenario != null) {
            if (latestApiResponse != null) { // Asegurarse de que latestApiResponse no sea nulo
                if (latestApiResponse.getCurlCommand() != null) {
                    currentScenario.attach(latestApiResponse.getCurlCommand(), "text/plain", "Comando cURL Ejecutado");
                }
                // Adjuntar el cuerpo de la respuesta solo si no es nulo y no está vacío
                if (latestApiResponse.getResponseBody() != null && !latestApiResponse.getResponseBody().isEmpty()) {
                    currentScenario.attach(latestApiResponse.getResponseBody(), "application/json", "Cuerpo de la Respuesta");
                }
                // ¡Cambiado a getError()!
                if (latestApiResponse.getError() != null) {
                    currentScenario.attach("Excepción: " + latestApiResponse.getError().getMessage(), "text/plain", "Detalles de la Excepción");
                }
            }
        }
    }

    /**
     * Construye un comando cURL para una petición HTTP.
     *
     * @param method El método HTTP (POST, GET, etc.).
     * @param path La ruta del endpoint (ej. "/text/validated").
     * @param requestBody El cuerpo de la petición (para POST/PUT).
     * @return Una cadena que representa el comando cURL.
     */
    protected String buildCurlCommand(String method, String path, String requestBody) {
        // Usamos el puerto inyectado para construir el comando cURL
        String fullUrl = "http://localhost:" + port + path;

        StringBuilder curl = new StringBuilder("curl -X ").append(method);
        curl.append(" -H \"Content-Type: application/json\"");
        if (requestBody != null && !requestBody.isEmpty()) {
            // Escapar comillas simples dentro del body para el comando curl
            curl.append(" -d '").append(requestBody.replace("'", "'\\''")).append("'");
        }
        curl.append(" ").append(fullUrl);
        return curl.toString();
    }

    /**
     * Obtiene la última respuesta de la API procesada.
     *
     * @return El objeto ApiResponse que contiene los detalles de la última respuesta.
     */
    public ApiResponse getApiResponse() {
        return latestApiResponse;
    }
}
