package org.simor.adapter.client;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
public class PokemonRestClientException extends RuntimeException {

    private final HttpStatusCode statusCode;
    private final String message;
}
