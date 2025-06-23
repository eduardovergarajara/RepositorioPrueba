package cl.edu.template.microservice.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EndpointRequestDto {

    @Size(min = 1, max = 10, message = "El texto debe contener entre 1 y 10 caracteres")
    @NotNull(message = "El campo texto no puede ser null")
    private String texto;


    @Max(value = 10, message = "El número debe estar entre 0 y 10")
    @Min(value = 0, message = "El número debe estar entre 0 y 10")
    private Integer numero;
}
