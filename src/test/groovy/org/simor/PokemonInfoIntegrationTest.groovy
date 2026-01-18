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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class PokemonInfoIntegrationTest extends Specification {

    @Shared
    static WireMockContainer mockServer = new WireMockContainer(WireMockContainer.WIREMOCK_2_LATEST)
            .withMappingFromResource("pokemon_success_mewtwo.json")
            .withMappingFromResource("pokemon_not_found.json")
            .withMappingFromResource("pokemon_internal_server_error.json")
            .withMappingFromResource("pokemon_bad_request.json")
            .withMappingFromResource("pokemon_success_no_description.json")

    @Autowired
    MockMvc mockMvc

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("rest-client.pokemon.base-url", () -> {
            mockServer.getBaseUrl()
        });
    }

    def setupSpec() {
        mockServer.start()
    }

    def cleanupSpec() {
        mockServer.stop()
    }

    def "Given existing pokemon it return its basic information"() {
        when:
        def result = mockMvc.perform(get("/v1/pokemon/mewtwo"))
        then:
        result.andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath('$.name').value("mewtwo"))
                .andExpect(jsonPath('$.description').value("Red sample description"))
                .andExpect(jsonPath('$.habitat').value("rare"))
                .andExpect(jsonPath('$.is_legendary').value(true))
    }

    def "Given existing pokemon without description it returns its basic information with empty description"() {
        when:
        def result = mockMvc.perform(get("/v1/pokemon/noDescription"))
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
        def result = mockMvc.perform(get("/v1/pokemon/notExist"))
        then:
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath('$.message').value("Pokemon not found"))
    }

    def "Given error while fetching pokemon info it returns 502 Unable to process request"() {
        when:
        def result = mockMvc.perform(get("/v1/pokemon/error"))
        then:
        result.andExpect(status().isBadGateway())
                .andExpect(jsonPath('$.message').value("Unable to process request"))
    }

    def "Given bad request while fetching pokemon info it returns 500 Unexpected error fetching Pokemon information"() {
        when:
        def result = mockMvc.perform(get("/v1/pokemon/bad"))
        then:
        result.andExpect(status().isInternalServerError())
                .andExpect(jsonPath('$.message').value("Unexpected error fetching Pokemon information"))
    }
}