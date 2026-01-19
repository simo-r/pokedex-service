package org.simor.config;

import org.simor.adapter.client.TranslationClient;
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
@EnableCaching
public class CacheConfig {

    @Bean
    CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }
}
