package org.simor.adapter.client

import org.springframework.http.HttpStatus
import org.wiremock.integrations.testcontainers.WireMockContainer
import spock.lang.Shared
import spock.lang.Specification

class TranslationRestClientTest extends Specification {

    @Shared
    //TODO Remove deprecation warning
    WireMockContainer mockServer = new WireMockContainer("wiremock/wiremock")
            .withMappingFromResource("translation_success.json")
            .withMappingFromResource("translation_bad_request.json")
            .withMappingFromResource("translation_no_success.json")
            .withMappingFromResource("translation_internal_server_error.json")
            .withMappingFromResource("translation_malformed_response.json")
    @Shared
    private TranslationRestClient translationRestClient

    def setupSpec() {
        mockServer.start()
        translationRestClient = new TranslationRestClient(mockServer.getBaseUrl())
    }

    def cleanupSpec() {
        mockServer.stop()
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
}
