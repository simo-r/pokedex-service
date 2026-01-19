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
public class TranslationStrategyConfig {

    // decouple strategy applicable from actual implementation
    @Bean
    public Map<TranslationStrategy, TranslationClient> strategyTranslationClientMap(
            @Qualifier("yodaTranslation") TranslationClient yodaTranslation,
            @Qualifier("shakespeareTranslation") TranslationClient shakespeareTranslation) {
        // using LinkedHashMap insertion order determines priority, other possibility is to create exclusive strategies
        Map<TranslationStrategy, TranslationClient> strategyTranslationClientMap = new LinkedHashMap<>();
        strategyTranslationClientMap.put(new YodaTranslationStrategy(), yodaTranslation);
        strategyTranslationClientMap.put(new ShakespeareTranslationStrategy(), shakespeareTranslation);
        return strategyTranslationClientMap;
    }
}
