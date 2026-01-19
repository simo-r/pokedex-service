package org.simor.adapter.client


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
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
class TranslationRestClientTest extends Specification {

    @Shared
    private static WireMockContainer MOCK_SERVER = new WireMockContainer("wiremock/wiremock")
            .withMappingFromResource("translation_success.json")
            .withMappingFromResource("translation_bad_request.json")
            .withMappingFromResource("translation_no_success.json")
            .withMappingFromResource("translation_internal_server_error.json")
            .withMappingFromResource("translation_malformed_response.json")
            .withMappingFromResource("translation_success_retry_timeout_scenario.json")
            .withMappingFromResource("translation_success_retry_5xx_scenario.json")

    @Autowired
    @Qualifier("shakespeareTranslation")
    private TranslationRestClient translationRestClient

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("rest-client.shakespeare-translation.base-url", () -> {
            MOCK_SERVER.getBaseUrl()
        })
        registry.add("rest-client.yoda-translation.base-url", () -> {
            MOCK_SERVER.getBaseUrl()
        })
    }

    def setupSpec() {
        MOCK_SERVER.start()
    }

    def cleanupSpec() {
        MOCK_SERVER.stop()
    }

    def "Given a description it is translated"() {
        expect:
        translationRestClient.getTranslation("my fancy description") == "Mine plaited description"
    }

    def "Given no success translation it throws a bad gateway exception"() {
        when:
        translationRestClient.getTranslation("no success")
        then:
        def e = thrown TranslationRestClientException
        e.getStatusCode() == HttpStatus.BAD_GATEWAY
        e.getMessage() == "Unable to map response"
    }

    def "Given server error it throws a internal server error exception"() {
        when:
        translationRestClient.getTranslation("internal error")
        then:
        def e = thrown TranslationRestClientException
        e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
    }

    def "Given client error it throws a bad request exception"() {
        when:
        translationRestClient.getTranslation("a bad request")
        then:
        def e = thrown TranslationRestClientException
        e.getStatusCode() == HttpStatus.BAD_REQUEST
    }

    def "Given malformed response it throws a bad gateway exception"() {
        when:
        translationRestClient.getTranslation("malformed")
        then:
        def e = thrown TranslationRestClientException
        e.getStatusCode() == HttpStatus.BAD_GATEWAY
    }

    def "Given a description it is translated after retry for timeout"() {
        expect:
        translationRestClient.getTranslation("my fancy delayed description") == "Mine plaited delayed description"
    }

    def "Given a description it is translated after retry for 5xx error"() {
        expect:
        translationRestClient.getTranslation("my fancy error description") == "Mine plaited error description"
    }
}
