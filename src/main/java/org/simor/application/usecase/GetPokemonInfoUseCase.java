package org.simor.application.usecase;

import org.simor.entity.PokemonInfoResponse;

public interface GetPokemonInfoUseCase {

    PokemonInfoResponse execute(String pokemonName);
}
