package org.simor.adapter.repository

import org.simor.entity.dto.FlavorLanguage
import org.simor.entity.dto.FlavorTextEntry
import org.simor.entity.dto.FlavorVersion
import org.simor.entity.dto.Habitat
import org.simor.entity.dto.PokemonSpec
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.testcontainers.spock.Testcontainers
import org.wiremock.integrations.testcontainers.WireMockContainer
import spock.lang.Shared
import spock.lang.Specification


@Testcontainers
class PokemonRestClientTest extends Specification {

    @Shared
    WireMockContainer mockServer = new WireMockContainer(WireMockContainer.WIREMOCK_2_LATEST)
            .withMappingFromResource("pokemon_success_mewtwo.json")
            .withMappingFromResource("pokemon_not_found.json")
            .withMappingFromResource("pokemon_internal_server_error.json")
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

    def "Given unexisting pokemon it throws HttpClientErrorException.NotFound"() {
        when:
        pokemonRestClient.getPokemonSpec("notExist")
        then:
        def e = thrown HttpClientErrorException.NotFound
        e.getStatusCode() == HttpStatus.NOT_FOUND
        e.getResponseBodyAsString() == "Not found"
    }

    def "Given server error it throws HttpClientErrorException.InternalServerError"() {
        when:
        pokemonRestClient.getPokemonSpec("error")
        then:
        def e = thrown HttpServerErrorException.InternalServerError
        e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
    }

}
