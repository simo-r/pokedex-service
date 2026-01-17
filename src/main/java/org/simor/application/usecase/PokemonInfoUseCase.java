package org.simor.application.usecase;

import org.simor.entity.PokemonInfoResponse;

public interface PokemonInfoUseCase {

    PokemonInfoResponse getBasicPokemonInfo(String pokemonName);
}
