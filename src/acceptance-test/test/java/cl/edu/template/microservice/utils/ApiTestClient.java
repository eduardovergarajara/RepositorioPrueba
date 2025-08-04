package cl.edu.template.microservice.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
// Asegúrate de que esta línea NO esté presente: import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Clase utilitaria para encapsular la lógica de las llamadas a la API
 * usando WebClient y la generación de comandos cURL.
 */
// Asegúrate de que esta anotación NO esté presente.
// @Component
public class ApiTestClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl; // Campo para almacenar la base URL completa

    /**
     * Constructor para inyección de dependencias.
     * WebClient.Builder y ObjectMapper son inyectados por Spring.
     * El puerto se pasa directamente desde la Step Definition.
     *
     * @param webClientBuilder El builder para WebClient.
     * @param objectMapper El ObjectMapper para trabajar con JSON.
     * @param port El puerto dinámico asignado por Spring Boot.
     */
    public ApiTestClient(WebClient.Builder webClientBuilder, ObjectMapper objectMapper, int port) {
        // Construimos la base URL completa con el puerto dinámico
        String baseUrl = "http://localhost:" + port;

        // Configuramos el WebClient con la base URL
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl; // Asignar la base URL directamente
    }

    /**
     * Construye un comando cURL para una petición HTTP.
     *
     * @param method El método HTTP (POST, GET, etc.).
     * @param path La ruta del endpoint (ej. "/text/validated").
     * @param requestBody El cuerpo de la petición (para POST/PUT).
     * @return Una cadena que representa el comando cURL.
     */
    private String buildCurlCommand(String method, String path, String requestBody) {
        // Usamos la baseUrl completa para construir el comando cURL
        String fullUrl = baseUrl + path;
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
     * Realiza una petición POST a un endpoint dado.
     *
     * @param path La ruta del endpoint (ej. "/text/validated").
     * @param requestBody El cuerpo de la petición como String JSON.
     * @return Un objeto ApiResponse que contiene el estado, cuerpo, error y comando cURL.
     */
    public ApiResponse post(String path, String requestBody) {
        String curlCommand = buildCurlCommand("POST", path, requestBody);

        try {
            // El WebClient ya tiene la base URL configurada, solo necesitamos el path relativo
            return webClient.post()
                    .uri(path) // Usar solo el path relativo, ya que baseUrl está en webClient
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(requestBody))
                    .retrieve()
                    .toEntity(String.class)
                    .map(responseEntity -> new ApiResponse(responseEntity.getStatusCode(), responseEntity.getBody(), null, curlCommand))
                    .onErrorResume(WebClientResponseException.class, e -> {
                        System.err.println("WebClientResponseException en ApiTestClient: Status " + e.getStatusCode() + ", Body: " + e.getResponseBodyAsString() + ", Message: " + e.getMessage());
                        return Mono.just(new ApiResponse(e.getStatusCode(), e.getResponseBodyAsString(), e, curlCommand));
                    })
                    .onErrorResume(Exception.class, e -> {
                        System.err.println("Excepción general en ApiTestClient: " + e.getClass().getName() + " - " + e.getMessage());
                        return Mono.just(new ApiResponse(null, null, e, curlCommand));
                    })
                    .block(); // Bloquear para obtener el resultado de forma síncrona en los tests
        } catch (Exception e) {
            System.err.println("Excepción inesperada después del flujo reactivo en ApiTestClient.post(): " + e.getClass().getName() + " - " + e.getMessage());
            return new ApiResponse(null, null, e, curlCommand);
        }
    }
}
