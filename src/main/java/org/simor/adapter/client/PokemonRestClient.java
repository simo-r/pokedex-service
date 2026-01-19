package org.simor.adapter.client;

import io.github.resilience4j.retry.annotation.Retry;
import org.simor.config.RestClientProperties;
import org.simor.entity.PokemonSpec;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.time.Duration;

@Component
public class PokemonRestClient {

    private final RestClient pokemonRestClient;

    PokemonRestClient(RestClientProperties.RestClient pokemonConfig) {
        ClientHttpRequestFactorySettings requestFactorySettings = ClientHttpRequestFactorySettings.defaults()
                //TODO Make them configurable
                .withConnectTimeout(Duration.ofMillis(pokemonConfig.getConnectTimeout()))
                .withReadTimeout(Duration.ofMillis(pokemonConfig.getReadTimeout()));
        JdkClientHttpRequestFactory requestFactory = ClientHttpRequestFactoryBuilder.jdk().build(requestFactorySettings);
        pokemonRestClient = RestClient.builder()
                .baseUrl(String.format("%s/api/v2/pokemon-species/", pokemonConfig.getBaseUrl()))
                .requestFactory(requestFactory)
                .build();
    }

    @Retry(name = "pokemon")
    public PokemonSpec getPokemonSpec(String pokemonName) {
        try {
            return pokemonRestClient.get()
                    .uri(pokemonName)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(PokemonSpec.class);
        } catch (RestClientResponseException ex) {
            // exception thrown by ResponseSpec when status code >= 400
            throw new PokemonRestClientException(ex.getStatusCode(), ex.getMessage());
        } catch (RestClientException ex) {
            throw new PokemonRestClientException(HttpStatus.BAD_GATEWAY, "Unexpected error occurred");
        }
    }
}
