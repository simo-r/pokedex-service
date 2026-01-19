package org.simor.adapter.controller;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.simor.adapter.client.PokemonRestClientException;
import org.simor.entity.model.GetPokemonInfoErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = PokemonRestClientException.class)
    public ResponseEntity<GetPokemonInfoErrorResponse> handlePokemonRestClientException(PokemonRestClientException exception) {
        if (HttpStatus.NOT_FOUND == exception.getStatusCode()) {
            return new ResponseEntity<>(new GetPokemonInfoErrorResponse("Pokemon not found"), exception.getStatusCode());
        } else if (exception.getStatusCode().is5xxServerError()) {
            return new ResponseEntity<>(new GetPokemonInfoErrorResponse("Unable to process request"), HttpStatus.BAD_GATEWAY);
        }
        return new ResponseEntity<>(new GetPokemonInfoErrorResponse("Unexpected error fetching Pokemon information"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = CallNotPermittedException.class)
    public ResponseEntity<GetPokemonInfoErrorResponse> handleCircuitBreakerOpenStateException(CallNotPermittedException exception) {
        return new ResponseEntity<>(new GetPokemonInfoErrorResponse("Service temporary unavailable"), HttpStatus.SERVICE_UNAVAILABLE);
    }

}
