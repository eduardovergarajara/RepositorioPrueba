# language: es
Característica: Pruebas de Validacion de Solicitudes (Bad Request)

  Como usuario del servicio,
  Quiero enviar solicitudes con datos inválidos,
  Para verificar que el sistema maneja los errores de validación correctamente.

  Esquema del escenario: Validar el endpoint text/validated con varios casos
    Dado el body del request:
      """
      { "texto": "<texto>", "numero": <numero> }
      """
    Cuando realizo una petición POST a "/base/endpoint/text/validated"
    Entonces la respuesta HTTP debe ser <status_code>
    Y el cuerpo de la respuesta JSON debe contener:
      """
      <response_body>
      """

    Ejemplos:
      | texto                        | numero | status_code | response_body                                                                                                                                                             |
      |                              | 5      | 400         | { "mensaje": "Errores de validación", "errores": [ { "campo": "texto", "mensaje": "El texto debe contener entre 1 y 10 caracteres" } ] }                               |
      | hola                         | 15     | 400         | { "mensaje": "Errores de validación", "errores": [ { "campo": "numero", "mensaje": "El número debe estar entre 0 y 10" } ] }                                 |
      | textoInvalido                | 15     | 400         | { "mensaje": "Errores de validación", "errores": [ { "campo": "texto", "mensaje": "El texto debe contener entre 1 y 10 caracteres" }, { "campo": "numero", "mensaje": "El número debe estar entre 0 y 10" } ] } |
      | muyLargoParaSerValido        | 5      | 400         | { "mensaje": "Errores de validación", "errores": [ { "campo": "texto", "mensaje": "El texto debe contener entre 1 y 10 caracteres" } ] }                               |
