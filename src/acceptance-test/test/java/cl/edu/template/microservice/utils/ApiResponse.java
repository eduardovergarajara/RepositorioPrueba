package cl.edu.template.microservice.utils;

import org.springframework.http.HttpStatusCode;

/**
 * Clase para encapsular la respuesta de una llamada a la API,
 * incluyendo el estado HTTP, el cuerpo de la respuesta,
 * cualquier excepción/error que haya ocurrido y el comando cURL ejecutado.
 */
public class ApiResponse {

    private final HttpStatusCode statusCode;
    private final String responseBody;
    private final Exception error; // ¡Campo de error actualizado!
    private final String curlCommand;

    /**
     * Constructor para una respuesta exitosa o con cuerpo.
     *
     * @param statusCode El estado HTTP de la respuesta (HttpStatusCode).
     * @param responseBody El cuerpo de la respuesta como String.
     * @param error Cualquier excepción/error que haya ocurrido durante la petición (puede ser null).
     * @param curlCommand El comando cURL que se ejecutó.
     */
    public ApiResponse(HttpStatusCode statusCode, String responseBody, Exception error, String curlCommand) { // ¡Constructor actualizado!
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.error = error; // Asignación del campo de error
        this.curlCommand = curlCommand;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public Exception getError() { // ¡Método getter actualizado!
        return error;
    }

    public String getCurlCommand() {
        return curlCommand;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "statusCode=" + statusCode +
                ", responseBody='" + responseBody + '\'' +
                ", error=" + (error != null ? error.getClass().getSimpleName() : "null") +
                ", curlCommand='" + curlCommand + '\'' +
                '}';
    }
}
