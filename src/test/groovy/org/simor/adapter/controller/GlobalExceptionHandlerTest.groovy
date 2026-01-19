package org.simor.adapter.controller

import org.simor.adapter.client.PokemonRestClientException
import org.simor.entity.model.GetPokemonInfoErrorResponse
import org.springframework.http.HttpStatus
import spock.lang.Shared
import spock.lang.Specification

class GlobalExceptionHandlerTest extends Specification {

    @Shared
    private GlobalExceptionHandler handler

    def setupSpec() {
        handler = new GlobalExceptionHandler();
    }

    def "Given Pokemon not found exception it returns an error with status not found"() {
        given:
        def exception = new PokemonRestClientException(HttpStatus.NOT_FOUND, "Not found")
        when:
        def responseEntity = handler.handlePokemonRestClientException(exception)
        then:
        responseEntity.getStatusCode() == HttpStatus.NOT_FOUND
        responseEntity.getBody() == new GetPokemonInfoErrorResponse("Pokemon not found")
    }

    def "Given Pokemon 5xx exception it returns an error with status bad gateway"() {
        given:
        def exception = new PokemonRestClientException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error")
        when:
        def responseEntity = handler.handlePokemonRestClientException(exception)
        then:
        responseEntity.getStatusCode() == HttpStatus.BAD_GATEWAY
        responseEntity.getBody() == new GetPokemonInfoErrorResponse("Unable to process request")
    }

    def "Given Pokemon 4xx exception it returns an error with status internal server error"() {
        given:
        def exception = new PokemonRestClientException(HttpStatus.BAD_REQUEST, "Bad request")
        when:
        def responseEntity = handler.handlePokemonRestClientException(exception)
        then:
        responseEntity.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
        responseEntity.getBody() == new GetPokemonInfoErrorResponse("Unexpected error fetching Pokemon information")
    }
}
