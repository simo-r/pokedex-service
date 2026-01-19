package org.simor.application.usecase;

import lombok.RequiredArgsConstructor;
import org.simor.adapter.client.PokemonRestClient;
import org.simor.entity.domain.Pokemon;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class GetPokemonInfoService implements GetPokemonInfoUseCase {

    private final PokemonRestClient pokemonRestClient;

    @Override
    public Pokemon execute(String pokemonName) {
        return pokemonRestClient.getPokemonSpec(pokemonName);
    }

}
