package org.simor.adapter.client

import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.simor.entity.domain.Pokemon
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.spock.Testcontainers
import org.wiremock.integrations.testcontainers.WireMockContainer
import spock.lang.Shared
import spock.lang.Specification

// SpringBootTest is required to bootstrap resilience4j autoconfiguration and annotation
// Scenarios are mocked based on the input pokemon name
@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class PokemonRestClientTest extends Specification {

    private static final CB_POKEMON = 'cb-pokemon'

    @Shared
    private static WireMockContainer MOCK_SERVER = new WireMockContainer("wiremock/wiremock")
            .withMappingFromResource("stub/pokemon/pokemon_success_mewtwo.json")
            .withMappingFromResource("stub/pokemon/pokemon_not_found.json")
            .withMappingFromResource("stub/pokemon/pokemon_internal_server_error.json")
            .withMappingFromResource("stub/pokemon/pokemon_malformed_response.json")
            .withMappingFromResource("stub/pokemon/pokemon_success_retry_timeout_scenario.json")
            .withMappingFromResource("stub/pokemon/pokemon_success_retry_5xx_scenario.json")
            .withMappingFromResource("stub/pokemon/pokemon_success_cache_scenario.json")
    @Autowired
    private PokemonRestClient pokemonRestClient

    @Autowired
    protected CircuitBreakerRegistry circuitBreakerRegistry

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("rest-client.pokemon.base-url", () -> {
            MOCK_SERVER.getBaseUrl()
        })
    }

    def setupSpec() {
        MOCK_SERVER.start()
    }

    def setup() {
        circuitBreakerRegistry.circuitBreaker(CB_POKEMON).transitionToClosedState()
    }

    def cleanup() {
        circuitBreakerRegistry.circuitBreaker(CB_POKEMON).transitionToClosedState()
    }

    def cleanupSpec() {
        MOCK_SERVER.stop()
    }

    def "Given existing pokemon it returns its species"() {
        when:
        def pokemonSpec = pokemonRestClient.getPokemonSpec("mewtwo")
        then:
        pokemonSpec == new Pokemon("mewtwo", "Red sample description", "rare", true)

    }

    def "Given unexisting pokemon it throws a not found exception"() {
        when:
        pokemonRestClient.getPokemonSpec("notExist")
        then:
        def e = thrown PokemonRestClientException
        e.getStatusCode() == HttpStatus.NOT_FOUND
        e.getMessage() == "404 Not Found: \"Not found\""
    }

    def "Given server error it throws internal server error exception"() {
        when:
        pokemonRestClient.getPokemonSpec("error")
        then:
        def e = thrown PokemonRestClientException
        e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
    }

    def "Given malformed response it throws bad gateway error exception"() {
        when:
        pokemonRestClient.getPokemonSpec("malformed")
        then:
        def e = thrown PokemonRestClientException
        e.getStatusCode() == HttpStatus.BAD_GATEWAY
    }

    def "Given existing pokemon it returns its species after retry for timeout"() {
        when:
        def pokemonSpec = pokemonRestClient.getPokemonSpec("delay")
        then:
        pokemonSpec == new Pokemon("mewtwo", "Red sample description", "rare", true)
    }

    def "Given existing pokemon it returns its species after retry for 5xx error"() {
        when:
        def pokemonSpec = pokemonRestClient.getPokemonSpec("errorRetry")
        then:
        pokemonSpec == new Pokemon("mewtwo", "Red sample description", "rare", true)

    }

    def "Given open circuit breaker it throws exception"() {
        given:
        circuitBreakerRegistry.circuitBreaker(CB_POKEMON).transitionToOpenState()
        when:
        pokemonRestClient.getPokemonSpec("mewtwo")
        then:
        thrown CallNotPermittedException
    }

    def "Given existing pokemon in cache it returns its species"() {
        given: 'caches the success response'
        pokemonRestClient.getPokemonSpec("cache")
        when:
        def cachedResponse = pokemonRestClient.getPokemonSpec("cache")
        then:
        cachedResponse == new Pokemon("mewtwo", "Red sample description", "rare", true)

    }
}
