// src/main/java/cl/edu/template/microservice/exception/ValidationError.java
package cl.edu.template.microservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ValidationError {
    private String campo;
    private String mensaje;

}