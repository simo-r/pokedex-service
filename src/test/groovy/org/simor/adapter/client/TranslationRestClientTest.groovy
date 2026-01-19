package org.simor.adapter.client

import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
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
class TranslationRestClientTest extends Specification {

    private static final CB_TRANSLATION = 'cb-translation'

    @Shared
    private static WireMockContainer MOCK_SERVER = new WireMockContainer("wiremock/wiremock")
            .withMappingFromResource("stub/translation/translation_success.json")
            .withMappingFromResource("stub/translation/translation_bad_request.json")
            .withMappingFromResource("stub/translation/translation_no_success.json")
            .withMappingFromResource("stub/translation/translation_internal_server_error.json")
            .withMappingFromResource("stub/translation/translation_malformed_response.json")
            .withMappingFromResource("stub/translation/translation_success_retry_timeout_scenario.json")
            .withMappingFromResource("stub/translation/translation_success_retry_5xx_scenario.json")
            .withMappingFromResource("stub/translation/translation_success_cache_scenario.json")

    @Autowired
    @Qualifier("shakespeareTranslation")
    private TranslationRestClient translationRestClient

    @Autowired
    protected CircuitBreakerRegistry circuitBreakerRegistry

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

    def setup() {
        circuitBreakerRegistry.circuitBreaker(CB_TRANSLATION).transitionToClosedState()
    }

    def cleanup() {
        circuitBreakerRegistry.circuitBreaker(CB_TRANSLATION).transitionToClosedState()
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

    def "Given open circuit breaker it throws exception"() {
        given:
        circuitBreakerRegistry.circuitBreaker(CB_TRANSLATION).transitionToOpenState()
        when:
        translationRestClient.getTranslation("my fancy error description")
        then:
        thrown CallNotPermittedException
    }

    def "Given existing description in cache it is returned"() {
        given:
        translationRestClient.getTranslation("my cached description")
        expect:
        translationRestClient.getTranslation("my cached description") == "Mine cached description"
    }

}
