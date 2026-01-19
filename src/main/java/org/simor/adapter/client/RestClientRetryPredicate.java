package org.simor.adapter.client;

import java.util.function.Predicate;

// simple retry predicate on server side errors, includes also timeouts
public class RestClientRetryPredicate implements Predicate<Throwable> {
    @Override
    public boolean test(Throwable throwable) {
        if (throwable instanceof PokemonRestClientException ex) {
            return ex.getStatusCode().is5xxServerError();
        }
        if (throwable instanceof TranslationRestClientException ex) {
            return ex.getStatusCode().is5xxServerError();
        }
        return false;
    }
}