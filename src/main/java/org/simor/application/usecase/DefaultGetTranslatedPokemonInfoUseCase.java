package org.simor.application.usecase;

import lombok.RequiredArgsConstructor;
import org.simor.adapter.client.TranslationClient;
import org.simor.adapter.client.TranslationRestClientException;
import org.simor.application.strategy.TranslationStrategy;
import org.simor.entity.domain.Pokemon;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DefaultGetTranslatedPokemonInfoUseCase implements GetTranslatedPokemonInfoUseCase {

    private final GetPokemonInfoUseCase getPokemonInfoUseCase;
    private final Map<TranslationStrategy, TranslationClient> strategyTranslationClientMap;

    @Override
    public Pokemon execute(String pokemonName) {
        Pokemon pokemonInfoResponse = getPokemonInfoUseCase.execute(pokemonName);
        if (!StringUtils.hasText(pokemonInfoResponse.description())) {
            return pokemonInfoResponse;
        }
        String translatedDescription = strategyTranslationClientMap
                .entrySet()
                .stream()
                .filter(entry ->
                        entry.getKey().isApplicable(pokemonInfoResponse.habitat(), pokemonInfoResponse.isLegendary()))
                .findFirst()
                .map(entry ->
                        getTranslation(entry.getValue(), pokemonInfoResponse))
                .orElse(pokemonInfoResponse.description());
        return new Pokemon(
                pokemonInfoResponse.name(),
                translatedDescription,
                pokemonInfoResponse.habitat(),
                pokemonInfoResponse.isLegendary());
    }

    private static String getTranslation(TranslationClient translationClient,
                                         Pokemon pokemon) {
        try {
            return translationClient.getTranslation(pokemon.description());
        } catch (TranslationRestClientException e) {
            return pokemon.description();
        }
    }
}
