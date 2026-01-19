package org.simor.application.usecase;

import org.simor.entity.domain.Pokemon;

public interface GetTranslatedPokemonInfoUseCase {

    Pokemon execute(String pokemonName);
}
