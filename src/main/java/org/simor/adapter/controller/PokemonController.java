package org.simor.adapter.controller;

import lombok.RequiredArgsConstructor;
import org.simor.application.usecase.PokemonInfoUseCase;
import org.simor.entity.model.PokemonInfoResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class PokemonController {

    private final PokemonInfoUseCase pokemonInfoUseCase;

    @GetMapping(value = "/pokemon/{pokemonName}", produces = "application/json")
    public PokemonInfoResponse getPokemonInfo(@PathVariable String pokemonName) {
        return pokemonInfoUseCase.getBasicPokemonInfo(pokemonName);
    }
}
