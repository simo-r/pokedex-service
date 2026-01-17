package org.simor.adapter.controller;

import lombok.RequiredArgsConstructor;
import org.simor.application.usecase.GetPokemonInfoUseCase;
import org.simor.entity.PokemonInfoResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class PokemonController {

    private final GetPokemonInfoUseCase getPokemonInfoUseCase;

    @GetMapping(value = "/pokemon/{pokemonName}", produces = "application/json")
    public PokemonInfoResponse getPokemonInfo(@PathVariable String pokemonName) {
        return getPokemonInfoUseCase.execute(pokemonName);
    }
}
