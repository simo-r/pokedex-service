package org.simor.config;

import org.simor.adapter.client.TranslationClient;
import org.simor.application.strategy.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;


@Configuration
public class TranslationStrategyConfig {

    // decouple strategy applicable from actual implementation
    @Bean
    Map<TranslationStrategy, TranslationClient> translationRegistry(
            @Qualifier("yodaTranslation") TranslationClient yodaTranslation,
            @Qualifier("shakespeareTranslation") TranslationClient shakespeareTranslation) {
        Map<TranslationStrategy, TranslationClient> map = new LinkedHashMap<>();
        map.put(new YodaTranslationStrategy(), yodaTranslation);
        map.put(new ShakespeareTranslationStrategy(), shakespeareTranslation);
        return map;
    }

    @Bean
    public TranslationStrategyRegistry translationStrategyRegistry(Map<TranslationStrategy, TranslationClient> translationRegistry) {
        return new TranslationStrategyRegistry(translationRegistry);
    }
}
