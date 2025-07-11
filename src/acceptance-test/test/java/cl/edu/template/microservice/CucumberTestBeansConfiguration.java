package cl.edu.template.microservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CucumberTestBeansConfiguration {

    private int fixedPort = 8080;

    @Bean
    public WebClient webClient() {
        return WebClient.builder().baseUrl("http://localhost:" + fixedPort).build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}