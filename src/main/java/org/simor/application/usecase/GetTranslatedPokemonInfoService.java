package org.simor.application.usecase;

import lombok.RequiredArgsConstructor;
import org.simor.adapter.client.TranslationClient;
import org.simor.adapter.client.TranslationRestClientException;
import org.simor.application.strategy.TranslationStrategy;
import org.simor.entity.PokemonInfoResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class GetTranslatedPokemonInfoService implements GetTranslatedPokemonInfoUseCase {

    private final GetPokemonInfoUseCase getPokemonInfoUseCase;
    private final Map<TranslationStrategy, TranslationClient> strategyTranslationClientMap;

    @Override
    public PokemonInfoResponse execute(String pokemonName) {
        PokemonInfoResponse pokemonInfoResponse = getPokemonInfoUseCase.execute(pokemonName);
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
        return new PokemonInfoResponse(
                pokemonInfoResponse.name(),
                translatedDescription,
                pokemonInfoResponse.habitat(),
                pokemonInfoResponse.isLegendary());
    }

    private static String getTranslation(TranslationClient translationClient,
                                         PokemonInfoResponse pokemonInfoResponse) {
        try {
            return translationClient.getTranslation(pokemonInfoResponse.description());
        } catch (TranslationRestClientException e) {
            return pokemonInfoResponse.description();
        }
    }
}
