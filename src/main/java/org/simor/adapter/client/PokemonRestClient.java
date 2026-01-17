package org.simor.adapter.client;

import org.simor.entity.dto.PokemonSpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class PokemonRestClient {

    private final RestClient pokemonRestClient;

    PokemonRestClient(@Value("${rest-client.pokemon.base-url}") String baseUrl) {
        pokemonRestClient = RestClient.builder().baseUrl(String.format("%s/api/v2/pokemon-species/", baseUrl)).build();
    }

    public PokemonSpec getPokemonSpec(String pokemonName) {
        // FIXME Check for spec presence
        // FIXME Handle RestClientException like 404, 5xx
        try {
            return pokemonRestClient.get()
                    .uri(pokemonName)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(PokemonSpec.class);
        } catch (RestClientResponseException ex) {
            // exception thrown by ResponseSpec when status code >= 400
            throw new PokemonRestClientException(ex.getStatusCode(), ex.getMessage());
        }
    }
}
