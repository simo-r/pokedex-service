package org.simor.adapter.client;

import org.simor.entity.domain.Pokemon;

public interface PokemonClient {

    Pokemon getPokemonSpecies(String pokemonName);
}
