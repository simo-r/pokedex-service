package org.simor.application.usecase;

import lombok.RequiredArgsConstructor;
import org.simor.adapter.client.PokemonClient;
import org.simor.entity.domain.Pokemon;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class DefaultGetPokemonUseCase implements GetPokemonInfoUseCase {

    private final PokemonClient pokemonClient;

    @Override
    public Pokemon execute(String pokemonName) {
        return pokemonClient.getPokemonSpecies(pokemonName);
    }
}