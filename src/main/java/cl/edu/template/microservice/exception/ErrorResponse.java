// src/main/java/cl/edu/template/microservice/exception/ErrorResponse.java
package cl.edu.template.microservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private String mensaje;
    private List<ValidationError> errores;

}