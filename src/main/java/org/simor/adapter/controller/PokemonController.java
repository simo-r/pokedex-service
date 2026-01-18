package org.simor.adapter.controller;

import lombok.RequiredArgsConstructor;
import org.simor.application.usecase.GetTranslatedPokemonInfoUseCase;
import org.simor.application.usecase.GetPokemonInfoUseCase;
import org.simor.entity.PokemonInfoResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/pokemon")
@RequiredArgsConstructor
public class PokemonController {

    private final GetPokemonInfoUseCase getPokemonInfoUseCase;
    private final GetTranslatedPokemonInfoUseCase getTranslatedPokemonInfoUseCase;

    @GetMapping(value = "/{pokemonName}", produces = "application/json")
    public PokemonInfoResponse getPokemonInfo(@PathVariable String pokemonName) {
        return getPokemonInfoUseCase.execute(pokemonName);
    }

    @GetMapping(value = "/translated/{pokemonName}", produces = "application/json")
    public PokemonInfoResponse getTranslatedPokemonInfo(@PathVariable String pokemonName) {
        return getTranslatedPokemonInfoUseCase.execute(pokemonName);
    }
}
