package org.simor.config;

import org.simor.adapter.client.TranslationRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public TranslationRestClient shakespeareTranslationRestClient(
            @Value("${rest-client.shakespeare-translation.base-url}") String baseUrl,
            @Value("${rest-client.shakespeare-translation.path}") String path) {
        return new TranslationRestClient(baseUrl, path);
    }

    @Bean
    public TranslationRestClient yodaTranslationRestClient(
            @Value("${rest-client.yoda-translation.base-url}") String baseUrl,
            @Value("${rest-client.yoda-translation.path}") String path) {
        return new TranslationRestClient(baseUrl, path);
    }
}
