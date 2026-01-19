package org.simor.application.strategy;

import lombok.RequiredArgsConstructor;
import org.simor.adapter.client.TranslationClient;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class TranslationStrategyRegistry {
    private final Map<TranslationStrategy, TranslationClient> strategyTranslationClientMap;

    public Optional<TranslationClient> get(String habitat, boolean isLegendary) {
        return strategyTranslationClientMap
                .entrySet()
                .stream()
                .filter(entry ->
                        entry.getKey().isApplicable(habitat, isLegendary))
                .findFirst().map(Map.Entry::getValue);
    }
}