package org.simor.adapter.client

import org.springframework.http.HttpStatus
import spock.lang.Specification

class RestClientRetryPredicateTest extends Specification {

    private RestClientRetryPredicate predicate = new RestClientRetryPredicate()

    def "Given PokemonRestClientException with 5xx status code it returns true"(){
        expect:
        predicate.test(new PokemonRestClientException(HttpStatus.INTERNAL_SERVER_ERROR, "Error"))
    }

    def "Given PokemonRestClientException with 4xx status code it returns false"(){
        expect:
        !predicate.test(new PokemonRestClientException(HttpStatus.BAD_REQUEST, "Error"))
    }

    def "Given generic throwable it returns false"(){
        expect:
        !predicate.test(new RuntimeException("Error"))
    }
}
