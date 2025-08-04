package cl.edu.template.microservice.utils;

import io.cucumber.java.Scenario;

/**
 * Clase utilitaria para mantener el objeto Scenario de Cucumber
 * en un ThreadLocal. Esto permite que los Step Definitions
 * y los Hooks accedan al Scenario actual para, por ejemplo,
 * adjuntar información al informe de Cucumber.
 */
public class ScenarioHolder {

    // ThreadLocal para almacenar el objeto Scenario.
    // Cada hilo de ejecución (cada escenario de Cucumber) tendrá su propia instancia.
    private static final ThreadLocal<Scenario> scenarioThreadLocal = new ThreadLocal<>();

    /**
     * Establece el objeto Scenario para el hilo actual.
     *
     * @param scenario El objeto Scenario de Cucumber.
     */
    public static void set(Scenario scenario) {
        scenarioThreadLocal.set(scenario);
    }

    /**
     * Obtiene el objeto Scenario para el hilo actual.
     *
     * @return El objeto Scenario de Cucumber, o null si no se ha establecido.
     */
    public static Scenario get() {
        return scenarioThreadLocal.get();
    }

    /**
     * Elimina el objeto Scenario del ThreadLocal del hilo actual.
     * Esto es importante para evitar fugas de memoria en entornos de pruebas.
     */
    public static void remove() {
        scenarioThreadLocal.remove();
    }
}
