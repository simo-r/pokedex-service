package org.simor.config;

import org.simor.adapter.client.TranslationClient;
import org.simor.adapter.client.TranslationRestClient;
import org.simor.application.strategy.ShakespeareTranslationStrategy;
import org.simor.application.strategy.TranslationStrategy;
import org.simor.application.strategy.YodaTranslationStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClientProperties.RestClient pokemonConfig(RestClientProperties restClientProperties) {
        return restClientProperties.getPokemon();
    }

    @Bean
    public RestClientProperties.RestClient shakespeareTranslationConfig(RestClientProperties restClientProperties) {
        return restClientProperties.getShakespeareTranslation();
    }

    @Bean("shakespeareTranslation")
    public TranslationClient shakespeareTranslationRestClient(
            @Qualifier("shakespeareTranslationConfig") RestClientProperties.RestClient shakespeareConfig) {
        return new TranslationRestClient(shakespeareConfig);
    }

    @Bean
    public RestClientProperties.RestClient yodaTranslationConfig(RestClientProperties restClientProperties) {
        return restClientProperties.getYodaTranslation();
    }

    @Bean("yodaTranslation")
    public TranslationClient yodaTranslationRestClient(
            @Qualifier("yodaTranslationConfig") RestClientProperties.RestClient yodaConfig) {
        return new TranslationRestClient(yodaConfig);
    }
}
