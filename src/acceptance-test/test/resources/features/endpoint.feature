# language: es
Característica: Prueba Basica de Endpoint

  Como usuario del servicio,
  Quiero verificar un endpoint simple,
  Para confirmar que la integración funciona.

  Escenario: Verificar el endpoint noContent
    Cuando envío una petición POST a "/base/endpoint/noContent" con un cuerpo vacío
    Entonces la respuesta Status HTTP debe ser 204