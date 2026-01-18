package org.simor

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.spock.Testcontainers
import org.wiremock.integrations.testcontainers.WireMockContainer
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class TranslatedPokemonInfoIntegrationTest extends Specification {

    @Shared
    static WireMockContainer mockServer = new WireMockContainer("wiremock/wiremock")
            .withMappingFromResource("pokemon_success_mewtwo.json")
            .withMappingFromResource("pokemon_success_bulbasaur.json")
            .withMappingFromResource("pokemon_success_pikachu.json")
            .withMappingFromResource("pokemon_success_eevee.json")
            .withMappingFromResource("pokemon_not_found.json")
            .withMappingFromResource("pokemon_internal_server_error.json")
            .withMappingFromResource("pokemon_bad_request.json")
            .withMappingFromResource("pokemon_success_no_description.json")
            .withMappingFromResource("translation_success_mewtwo.json")
            .withMappingFromResource("translation_success_bulbasaur.json")
            .withMappingFromResource("translation_bad_request_pikachu.json")
            .withMappingFromResource("translation_internal_error_eevee.json")

    @Autowired
    MockMvc mockMvc

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("rest-client.pokemon.base-url", () -> {
            mockServer.getBaseUrl()
        })
        registry.add("rest-client.shakespeare-translation.base-url", () -> {
            mockServer.getBaseUrl()
        })
        registry.add("rest-client.yoda-translation.base-url", () -> {
            mockServer.getBaseUrl()
        })
    }

    def setupSpec() {
        mockServer.start()
    }

    def cleanupSpec() {
        mockServer.stop()
    }

    def "Given existing pokemon it returns its yoda translated basic information"() {
        when:
        def result = mockMvc.perform(get("/v1/pokemon/translated/mewtwo"))
        then:
        result.andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath('$.name').value("mewtwo"))
                .andExpect(jsonPath('$.description').value("Yoda translated description"))
                .andExpect(jsonPath('$.habitat').value("rare"))
                .andExpect(jsonPath('$.is_legendary').value(true))
    }

    def "Given existing pokemon it returns its shakespeare translated basic information"() {
        when:
        def result = mockMvc.perform(get("/v1/pokemon/translated/bulbasaur"))
        then:
        result.andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath('$.name').value("bulbasaur"))
                .andExpect(jsonPath('$.description').value("Shakespeare translated description"))
                .andExpect(jsonPath('$.habitat').value("grassland"))
                .andExpect(jsonPath('$.is_legendary').value(false))
    }

    def "Given existing pokemon without description it returns its basic information with empty description"() {
        when:
        def result = mockMvc.perform(get("/v1/pokemon/translated/noDescription"))
        then:
        result.andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath('$.name').value("mewtwo"))
                .andExpect(jsonPath('$.description').value(""))
                .andExpect(jsonPath('$.habitat').value("rare"))
                .andExpect(jsonPath('$.is_legendary').value(true))
    }

    def "Given unexisting pokemon it returns 404 Pokemon not found"() {
        when:
        def result = mockMvc.perform(get("/v1/pokemon/translated/notExist"))
        then:
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath('$.message').value("Pokemon not found"))
    }

    def "Given error while fetching pokemon info it returns 502 Unable to process request"() {
        when:
        def result = mockMvc.perform(get("/v1/pokemon/translated/error"))
        then:
        result.andExpect(status().isBadGateway())
                .andExpect(jsonPath('$.message').value("Unable to process request"))
    }

    def "Given bad request while fetching pokemon info it returns 500 Unexpected error fetching Pokemon information"() {
        when:
        def result = mockMvc.perform(get("/v1/pokemon/translated/bad"))
        then:
        result.andExpect(status().isInternalServerError())
                .andExpect(jsonPath('$.message').value("Unexpected error fetching Pokemon information"))
    }

    def "Given bad request while translating pokemon description it returns basic pokemon info"() {
        when:
        def result = mockMvc.perform(get("/v1/pokemon/translated/pikachu"))
        then:
        result.andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath('$.name').value("pikachu"))
                .andExpect(jsonPath('$.description').value("Cause lightning storms."))
                .andExpect(jsonPath('$.habitat').value("forest"))
                .andExpect(jsonPath('$.is_legendary').value(false))
    }

    def "Given internal server error while translating pokemon description it returns basic pokemon info"() {
        when:
        def result = mockMvc.perform(get("/v1/pokemon/translated/eevee"))
        then:
        result.andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath('$.name').value("eevee"))
                .andExpect(jsonPath('$.description').value("It may mutate"))
                .andExpect(jsonPath('$.habitat').value("urban"))
                .andExpect(jsonPath('$.is_legendary').value(false))
    }
}