package cl.edu.template.microservice;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.reactive.server.WebTestClient;
import java.time.Duration;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = MicroserviceApplication.class)
public class CucumberSpringContextConfiguration {

    @LocalServerPort
    private int port;

    /**
     * Define un bean para WebTestClient directamente en el contexto de prueba.
     * Esto asegura que se cree dentro del contexto de aplicación web completo
     * iniciado por @SpringBootTest.
     * @param applicationContext El contexto de la aplicación, inyectado por Spring.
     * @return Una instancia de WebTestClient configurada.
     */
    @Bean
    public WebTestClient webTestClient(ApplicationContext applicationContext) {
        return WebTestClient.bindToApplicationContext(applicationContext)
                .build();
    }

    public int getPort() {
        return port;
    }
}
