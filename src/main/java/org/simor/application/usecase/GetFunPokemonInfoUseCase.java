package org.simor.application.usecase;

import org.simor.entity.PokemonInfoResponse;

public interface GetFunPokemonInfoUseCase {

    PokemonInfoResponse execute(String pokemonName);
}
