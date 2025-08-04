package cl.edu.template.microservice.steps;

import cl.edu.template.microservice.utils.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.es.Entonces;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.skyscreamer.jsonassert.JSONAssert;

import org.json.JSONException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Extiende de la nueva clase base CucumberBaseTest
public class BadRequestSteps extends CucumberBaseTest {

    private String requestBody;

    @Autowired
    protected ObjectMapper objectMapper;

    @io.cucumber.java.es.Dado("el body del request:")
    public void queElCuerpoDeLaPeticionEs(String requestBody) {
        this.requestBody = requestBody;
    }

    @io.cucumber.java.es.Cuando("realizo una petición POST a {string}")
    public void realizoUnaPeticionPOSTA(String path) {
        performPost(path, requestBody);
    }

    @io.cucumber.java.es.Entonces("la respuesta HTTP debe ser {int}")
    public void laRespuestaHTTPDebeSer(int expectedStatus) {
        assertNotNull(latestApiResponse, "La respuesta de la API no debería ser nula.");
        assertNotNull(latestApiResponse.getStatusCode(), "El código de estado de la respuesta no debería ser nulo.");
        assertEquals(expectedStatus, latestApiResponse.getStatusCode().value(),
                "Se esperaba una respuesta con status " + expectedStatus + ", pero la petición falló con: " +
                        (latestApiResponse.getError() != null ? latestApiResponse.getError().getMessage() : "Desconocido"));
    }

    /**
     * Compara dos JSONs, ignorando el orden de los campos y los elementos de las listas.
     * Utiliza la biblioteca JSONAssert para realizar una comparación robusta y flexible.
     * @param expectedJson El JSON esperado en formato String.
     */
    @Entonces("el cuerpo de la respuesta JSON debe contener:")
    public void elCuerpoDeLaRespuestaJSONDebeContener(String expectedJson) {
        assertNotNull(latestApiResponse.getResponseBody(), "El cuerpo de la respuesta no debería ser nulo.");

        // Verifica que el cuerpo de la respuesta no esté vacío antes de intentar la comparación JSON.
        String responseBody = latestApiResponse.getResponseBody();
        if (responseBody == null || responseBody.trim().isEmpty()) {
            assertTrue(false, "El cuerpo de la respuesta está vacío, no se puede comparar con el JSON esperado.");
        }

        try {
            JSONAssert.assertEquals(expectedJson, responseBody, JSONCompareMode.LENIENT);

        } catch (AssertionError e) {
            assertTrue(false,
                    "El cuerpo de la respuesta no contiene el JSON esperado.\n" +
                            "Esperado: " + expectedJson + "\n" +
                            "Actual:   " + responseBody + "\n" +
                            "Diferencias: " + e.getMessage());
        } catch (JSONException e) {
            assertTrue(false,
                    "Error al parsear el JSON. Asegúrate de que el JSON esperado y el de la respuesta sean válidos.\n" +
                            "Error: " + e.getMessage() + "\n" +
                            "JSON de la Respuesta: " + responseBody);
        }
    }

    @Override
    public ApiResponse getApiResponse() {
        return latestApiResponse;
    }
}
