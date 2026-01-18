package org.simor.adapter.client

import org.simor.entity.FlavorLanguage
import org.simor.entity.FlavorTextEntry
import org.simor.entity.FlavorVersion
import org.simor.entity.Habitat
import org.simor.entity.PokemonSpec
import org.springframework.http.HttpStatus
import org.testcontainers.spock.Testcontainers
import org.wiremock.integrations.testcontainers.WireMockContainer
import spock.lang.Shared
import spock.lang.Specification


@Testcontainers
class PokemonRestClientTest extends Specification {

    @Shared
    WireMockContainer mockServer = new WireMockContainer("wiremock/wiremock")
            .withMappingFromResource("pokemon_success_mewtwo.json")
            .withMappingFromResource("pokemon_not_found.json")
            .withMappingFromResource("pokemon_internal_server_error.json")
            .withMappingFromResource("pokemon_malformed_response.json")
    @Shared
    private PokemonRestClient pokemonRestClient

    def setupSpec() {
        mockServer.start()
        pokemonRestClient = new PokemonRestClient(mockServer.getBaseUrl())
    }

    def cleanupSpec() {
        mockServer.stop()
    }

    def "Given existing pokemon it returns its species"() {
        when:
        def pokemonSpec = pokemonRestClient.getPokemonSpec("mewtwo")
        then:
        pokemonSpec == new PokemonSpec(
                "mewtwo",
                [new FlavorTextEntry("Red sample description",
                        new FlavorLanguage("en"),
                        new FlavorVersion("red")),
                 new FlavorTextEntry("Blue sample description",
                         new FlavorLanguage("en"),
                         new FlavorVersion("blue"))
                ], new Habitat("rare"), true)

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

}
