package org.simor.adapter.controller;

import lombok.RequiredArgsConstructor;
import org.simor.application.usecase.GetTranslatedPokemonInfoUseCase;
import org.simor.application.usecase.GetPokemonInfoUseCase;
import org.simor.entity.domain.Pokemon;
import org.simor.entity.model.GetPokemonInfoResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/pokemon")
@RequiredArgsConstructor
public class GetPokemonInfoController {

    private final GetPokemonInfoUseCase getPokemonInfoUseCase;
    private final GetTranslatedPokemonInfoUseCase getTranslatedPokemonInfoUseCase;

    @GetMapping(value = "/{pokemonName}", produces = "application/json")
    public GetPokemonInfoResponse getPokemonInfo(@PathVariable String pokemonName) {
        Pokemon pokemon = getPokemonInfoUseCase.execute(pokemonName);
        return new GetPokemonInfoResponse(pokemon.name(), pokemon.description(), pokemon.habitat(), pokemon.isLegendary());
    }

    @GetMapping(value = "/translated/{pokemonName}", produces = "application/json")
    public GetPokemonInfoResponse getTranslatedPokemonInfo(@PathVariable String pokemonName) {
        Pokemon pokemon = getTranslatedPokemonInfoUseCase.execute(pokemonName);
        return new GetPokemonInfoResponse(pokemon.name(), pokemon.description(), pokemon.habitat(), pokemon.isLegendary());
    }
}
