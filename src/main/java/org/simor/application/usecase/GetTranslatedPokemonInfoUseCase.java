package org.simor.application.usecase;

import org.simor.entity.PokemonInfoResponse;

public interface GetTranslatedPokemonInfoUseCase {

    PokemonInfoResponse execute(String pokemonName);
}
