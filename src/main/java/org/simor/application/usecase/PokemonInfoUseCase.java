package org.simor.application.usecase;

import org.simor.entity.model.PokemonInfoResponse;

public interface PokemonInfoUseCase {

    PokemonInfoResponse getBasicPokemonInfo(String pokemonName);
}
