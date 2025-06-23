package cl.edu.template.microservice.controller;

import cl.edu.template.microservice.controller.dto.EndpointRequestDto;
import cl.edu.template.microservice.controller.dto.EndpointResponseDto;
import jakarta.validation.Valid;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RestController
@RequestMapping("/base")
@Log
public class EndpointController {

    @GetMapping("/endpoint/ping")
    @ResponseStatus(code= HttpStatus.NO_CONTENT)
    public void ping(){
        log.info("[microservice][INI] Inicia endpoint PING");

        log.info("[microservice][FIN] Finaliza HTTP 204 NoContent");
    }

    @PostMapping("/endpoint/noContent")
    @ResponseStatus(code= HttpStatus.NO_CONTENT)
    public void noContent(){
        log.info("[microservice][INI] Inicia endpoint noContent");

        log.info("[microservice][FIN] Finaliza HTTP 204 NoContent");
    }

    @PostMapping("/endpoint/text/validated")
    @ResponseStatus(code= HttpStatus.NO_CONTENT)
    public void validatedRequest(@Valid @RequestBody EndpointRequestDto endpointRequestDto){
        log.info("[microservice][INI] Inicia endpoint noContent");

        log.info("[microservice][FIN] Finaliza HTTP 204 NoContent");
    }

    @PostMapping("/endpoint/text/multiplicado")
    public ResponseEntity<EndpointResponseDto> multiplyText(@Valid @RequestBody EndpointRequestDto endpointRequestDto){
        log.info("[microservice][INI] Inicia endpoint noContent");


        List<String> lista = Stream.generate(endpointRequestDto::getTexto)
                .limit(endpointRequestDto.getNumero())
                .collect(Collectors.toList());
        log.info("[microservice][FIN] Finaliza HTTP 204 NoContent");
        return ResponseEntity.ok(new EndpointResponseDto(lista));
    }

}
