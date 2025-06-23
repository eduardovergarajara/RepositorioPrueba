// src/main/java/cl/edu/template/microservice/exception/GlobalExceptionHandler.java
package cl.edu.template.microservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice // Indica que esta clase maneja excepciones globalmente
public class GlobalExceptionHandler {

    // Este método se ejecutará cuando se lance una MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Mapea cada error de campo a nuestra clase ValidationError
        List<ValidationError> errores = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    // Si el error es de un campo específico, obtenemos el nombre del campo
                    // De lo contrario, usamos el nombre del objeto (para validaciones a nivel de clase)
                    String fieldName = (error instanceof FieldError) ? ((FieldError) error).getField() : error.getObjectName();
                    return new ValidationError(fieldName, error.getDefaultMessage());
                })
                .collect(Collectors.toList());

        // Construye la respuesta de error con el formato deseado
        ErrorResponse errorResponse = new ErrorResponse("Errores de validación", errores);

        // Retorna la respuesta con estado HTTP 400 Bad Request
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Aquí podrías añadir más manejadores @ExceptionHandler para otros tipos de excepciones
    // que quieras manejar de forma global y dar una respuesta específica.
    // Por ejemplo:
    // @ExceptionHandler(ConstraintViolationException.class) // Para errores de validación a nivel de servicio/repositorio
    // public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) { ... }
}