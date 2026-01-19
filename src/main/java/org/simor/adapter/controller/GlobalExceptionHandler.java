package org.simor.adapter.controller;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.simor.adapter.client.PokemonRestClientException;
import org.simor.entity.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = PokemonRestClientException.class)
    public ResponseEntity<ErrorResponse> handlePokemonRestClientException(PokemonRestClientException exception) {
        if (HttpStatus.NOT_FOUND == exception.getStatusCode()) {
            return new ResponseEntity<>(new ErrorResponse("Pokemon not found"), exception.getStatusCode());
        } else if (exception.getStatusCode().is5xxServerError()) {
            return new ResponseEntity<>(new ErrorResponse("Unable to process request"), HttpStatus.BAD_GATEWAY);
        }
        return new ResponseEntity<>(new ErrorResponse("Unexpected error fetching Pokemon information"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = CallNotPermittedException.class)
    public ResponseEntity<ErrorResponse> handleCircuitBreakerOpenStateException(CallNotPermittedException exception) {
        return new ResponseEntity<>(new ErrorResponse("Service temporary unavailable"), HttpStatus.SERVICE_UNAVAILABLE);
    }

}
