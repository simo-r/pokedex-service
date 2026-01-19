package org.simor.adapter.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.simor.config.RestClientProperties;
import org.simor.entity.domain.Pokemon;
import org.simor.entity.model.PokemonRestClientResponse;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.cache.annotation.Cacheable;
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
                .withConnectTimeout(Duration.ofMillis(pokemonConfig.getConnectTimeout()))
                .withReadTimeout(Duration.ofMillis(pokemonConfig.getReadTimeout()));
        JdkClientHttpRequestFactory requestFactory = ClientHttpRequestFactoryBuilder.jdk().build(requestFactorySettings);
        pokemonRestClient = RestClient.builder()
                .baseUrl(String.format("%s/api/v2/pokemon-species/", pokemonConfig.getBaseUrl()))
                .requestFactory(requestFactory)
                .build();
    }

    @Retry(name = "retry-pokemon")
    @CircuitBreaker(name = "cb-pokemon")
    @Cacheable("cache-pokemon")
    public Pokemon getPokemonSpec(String pokemonName) {
        try {
            PokemonRestClientResponse pokemonRestClientResponse = pokemonRestClient.get()
                    .uri(pokemonName)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(PokemonRestClientResponse.class);
            if (pokemonRestClientResponse == null) {
                throw new PokemonRestClientException(HttpStatus.BAD_GATEWAY, "Unexpected response ");
            }
            return pokemonRestClientResponse.toDomain();
        } catch (RestClientResponseException ex) {
            // exception thrown by ResponseSpec when status code >= 400
            throw new PokemonRestClientException(ex.getStatusCode(), ex.getMessage());
        } catch (RestClientException ex) {
            throw new PokemonRestClientException(HttpStatus.BAD_GATEWAY, "Unexpected error occurred");
        }
    }
}
