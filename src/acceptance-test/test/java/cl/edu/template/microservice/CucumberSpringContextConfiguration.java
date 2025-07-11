package cl.edu.template.microservice;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = MicroserviceApplication.class)
public class CucumberSpringContextConfiguration {

    private int fixedPort = 8080;

    public int getPort() {
        return fixedPort;
    }


}