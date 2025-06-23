package cl.edu.template.microservice.controller

import cl.edu.template.microservice.controller.dto.EndpointRequestDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import com.fasterxml.jackson.databind.ObjectMapper // Para serializar DTOs a JSON
import spock.lang.Specification

@WebMvcTest(EndpointController) // Enfoca las pruebas en EndpointController
class EndpointControllerSpec extends Specification {

	@Autowired
	private MockMvc mockMvc // Usado para simular peticiones HTTP

	// ObjectMapper para convertir DTOs a JSON para los cuerpos de las peticiones
	private ObjectMapper objectMapper = new ObjectMapper()

	// --- Prueba para GET /base/endpoint/ping ---
	def "GET /base/endpoint/ping debe retornar 204 No Content"() {
		when: "Se realiza una petición GET al endpoint /base/endpoint/ping"
		def result = mockMvc.perform(MockMvcRequestBuilders.get("/base/endpoint/ping"))

		then: "La respuesta debe tener un estado HTTP 204 (No Content)"
		result.andExpect(MockMvcResultMatchers.status().isNoContent())
				.andExpect(MockMvcResultMatchers.content().string("")) // Aseguramos que el cuerpo esté vacío
	}



	// --- Prueba para POST /base/endpoint/noContent ---
	def "POST /base/endpoint/noContent debe retornar 204 No Content"() {
		when: "Se realiza una petición POST al endpoint /base/endpoint/noContent"
		// Un cuerpo JSON vacío es típico para peticiones POST, incluso si no se consume estrictamente
		def emptyBody = "{}"

		def result = mockMvc.perform(MockMvcRequestBuilders.post("/base/endpoint/noContent")
				.contentType(MediaType.APPLICATION_JSON)
				.content(emptyBody))

		then: "La respuesta debe tener un estado HTTP 204 (No Content)"
		result.andExpect(MockMvcResultMatchers.status().isNoContent())
				.andExpect(MockMvcResultMatchers.content().string("")) // Aseguramos que el cuerpo esté vacío
	}


	// --- Pruebas para POST /base/endpoint/text/validated ---
	// Este endpoint espera un EndpointRequestDto válido y retorna 204 No Content
	def "POST /base/endpoint/text/validated debe retornar 204 No Content para una petición válida"() {
		given: "Un EndpointRequestDto válido"
		def validRequest = new EndpointRequestDto("textoValid", 5)

		when: "Se realiza una petición POST a /base/endpoint/text/validated con el DTO válido"
		def result = mockMvc.perform(MockMvcRequestBuilders.post("/base/endpoint/text/validated")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validRequest)))

		then: "La respuesta debe tener un estado HTTP 204 (No Content)"
		result.andExpect(MockMvcResultMatchers.status().isNoContent())
				.andExpect(MockMvcResultMatchers.content().string("")) // Aseguramos que el cuerpo esté vacío
	}

	def "POST /base/endpoint/text/validated debe retornar 400 Bad Request para texto nulo"() {
		given: "Un EndpointRequestDto con texto nulo"
		def invalidRequest = new EndpointRequestDto(null, 5)

		when: "Se realiza una petición POST a /base/endpoint/text/validated con el DTO inválido"
		def result = mockMvc.perform(MockMvcRequestBuilders.post("/base/endpoint/text/validated")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest)))

		then: "La respuesta debe tener un estado HTTP 400 (Bad Request)"
		result.andExpect(MockMvcResultMatchers.status().isBadRequest())

		//and: "El cuerpo de la respuesta debe contener el mensaje de error de validación para texto nulo"
		//result.andExpect(MockMvcResultMatchers.jsonPath('$.mensaje').value('Errores de validación'))
		//		.andExpect(MockMvcResultMatchers.jsonPath('$.errores[0].campo').value('texto'))
		//		.andExpect(MockMvcResultMatchers.jsonPath('$.errores[0].mensaje').value('El campo texto no puede ser null'))
	}

	def "POST /base/endpoint/text/validated debe retornar 400 Bad Request para número inválido (fuera de rango)"() {
		given: "Un EndpointRequestDto con un número fuera de rango"
		def invalidRequest = new EndpointRequestDto("texto", 15) // 15 es > 10

		when: "Se realiza una petición POST a /base/endpoint/text/validated con el DTO inválido"
		def result = mockMvc.perform(MockMvcRequestBuilders.post("/base/endpoint/text/validated")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest)))

		then: "La respuesta debe tener un estado HTTP 400 (Bad Request)"
		result.andExpect(MockMvcResultMatchers.status().isBadRequest())

		//and: "El cuerpo de la respuesta debe contener el mensaje de error de validación para el rango del número"
		//result.andExpect(MockMvcResultMatchers.jsonPath('$.mensaje').value('Errores de validación'))
		//		.andExpect(MockMvcResultMatchers.jsonPath('$.errores[0].campo').value('numero'))
		//		.andExpect(MockMvcResultMatchers.jsonPath('$.errores[0].mensaje').value('El número debe estar entre 0 y 10'))
	}


	// --- Pruebas para POST /base/endpoint/text/multiplicado ---
	// Este endpoint espera un EndpointRequestDto válido y retorna 200 OK con EndpointResponseDto
	def "POST /base/endpoint/text/multiplicado debe retornar 200 OK y la lista de texto multiplicada para una petición válida"() {
		given: "Un EndpointRequestDto válido"
		def requestDto = new EndpointRequestDto("hola", 3)
		def expectedResponseList = ["hola", "hola", "hola"]

		when: "Se realiza una petición POST a /base/endpoint/text/multiplicado con el DTO válido"
		def result = mockMvc.perform(MockMvcRequestBuilders.post("/base/endpoint/text/multiplicado")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))

		then: "La respuesta debe tener un estado HTTP 200 (OK)"
		result.andExpect(MockMvcResultMatchers.status().isOk())

		and: "El cuerpo de la respuesta debe contener la lista de texto multiplicada"
		result.andExpect(MockMvcResultMatchers.jsonPath('$.listado').isArray())
				.andExpect(MockMvcResultMatchers.jsonPath('$.listado[0]').value('hola'))
				.andExpect(MockMvcResultMatchers.jsonPath('$.listado[1]').value('hola'))
				.andExpect(MockMvcResultMatchers.jsonPath('$.listado[2]').value('hola'))
				.andExpect(MockMvcResultMatchers.jsonPath('$.listado.length()').value(expectedResponseList.size()))
	}

	def "POST /base/endpoint/text/multiplicado debe retornar 400 Bad Request para DTO de petición inválido"() {
		given: "Un EndpointRequestDto inválido (ej., texto demasiado largo)"
		def invalidRequest = new EndpointRequestDto("esteTextoEsDemasiadoLargo", 5) // > 10 caracteres

		when: "Se realiza una petición POST a /base/endpoint/text/multiplicado con el DTO inválido"
		def result = mockMvc.perform(MockMvcRequestBuilders.post("/base/endpoint/text/multiplicado")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest)))

		then: "La respuesta debe tener un estado HTTP 400 (Bad Request)"
		result.andExpect(MockMvcResultMatchers.status().isBadRequest())

		//and: "El cuerpo de la respuesta debe contener el mensaje de error de validación para el tamaño del texto"
		//result.andExpect(MockMvcResultMatchers.jsonPath('$.mensaje').value('Errores de validación'))
		//		.andExpect(MockMvcResultMatchers.jsonPath('$.errores[0].campo').value('texto'))
		//		.andExpect(MockMvcResultMatchers.jsonPath('$.errores[0].mensaje').value('El texto debe contener entre 1 y 10 caracteres'))
	}
}