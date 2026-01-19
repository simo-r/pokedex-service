package org.simor.application.usecase;

import lombok.RequiredArgsConstructor;
import org.simor.adapter.client.TranslationClient;
import org.simor.adapter.client.TranslationRestClientException;
import org.simor.application.strategy.TranslationStrategyRegistry;
import org.simor.entity.domain.Pokemon;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class DefaultGetTranslatedPokemonInfoUseCase implements GetTranslatedPokemonInfoUseCase {

    private final GetPokemonInfoUseCase getPokemonInfoUseCase;
    private final TranslationStrategyRegistry translationStrategyRegistry;

    @Override
    public Pokemon execute(String pokemonName) {
        Pokemon pokemon = getPokemonInfoUseCase.execute(pokemonName);
        if (!StringUtils.hasText(pokemon.description())) {
            return pokemon;
        }

        String translatedDescription = translationStrategyRegistry.get(pokemon.habitat(), pokemon.isLegendary())
                .map(translationClient -> getTranslation(translationClient, pokemon))
                .orElse(pokemon.description());

        return new Pokemon(
                pokemon.name(),
                translatedDescription,
                pokemon.habitat(),
                pokemon.isLegendary());
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
