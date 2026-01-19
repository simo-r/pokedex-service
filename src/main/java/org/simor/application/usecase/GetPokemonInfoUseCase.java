package org.simor.application.usecase;

import org.simor.entity.domain.Pokemon;

public interface GetPokemonInfoUseCase {

    Pokemon execute(String pokemonName);
}
