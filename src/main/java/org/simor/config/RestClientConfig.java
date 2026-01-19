package org.simor.config;

import org.simor.adapter.client.TranslationClient;
import org.simor.adapter.client.TranslationRestClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
