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
// TODO Complete integration tests with failure scenarios
class PokemonInfoIntegrationTest extends Specification {

    @Shared
    static WireMockContainer mockServer = new WireMockContainer(WireMockContainer.WIREMOCK_2_LATEST)
            .withMappingFromResource("pokemon_success_mewtwo.json")
            .withMappingFromResource("pokemon_not_found.json")
            .withMappingFromResource("pokemon_internal_server_error.json")

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
}
