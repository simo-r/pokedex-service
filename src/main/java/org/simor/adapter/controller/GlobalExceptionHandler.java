package org.simor.adapter.controller;

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
        // FIXME Implement exception handling for each possible status
        return new ResponseEntity<>(new ErrorResponse("Unexpected error fetching Pokemon information"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
