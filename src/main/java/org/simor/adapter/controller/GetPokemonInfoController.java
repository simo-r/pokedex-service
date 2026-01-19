package org.simor.adapter.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.simor.application.usecase.GetTranslatedPokemonInfoUseCase;
import org.simor.application.usecase.GetPokemonInfoUseCase;
import org.simor.entity.domain.Pokemon;
import org.simor.entity.model.GetPokemonInfoResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/pokemon")
@RequiredArgsConstructor
@Slf4j
public class GetPokemonInfoController {

    public static final String GET_POKEMON_INFO_ENDPOINT = "/{pokemonName}";
    public static final String GET_TRANSLATED_POKEMON_INFO_ENDPOINT = "/translated/{pokemonName}";
    private final GetPokemonInfoUseCase getPokemonInfoUseCase;
    private final GetTranslatedPokemonInfoUseCase getTranslatedPokemonInfoUseCase;

    @GetMapping(value = GET_POKEMON_INFO_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    public GetPokemonInfoResponse getPokemonInfo(@PathVariable String pokemonName) {
        log.debug("Inbound HTTP Request. Path: {} pokemonName {}", GET_POKEMON_INFO_ENDPOINT, pokemonName);
        Pokemon pokemon = getPokemonInfoUseCase.execute(pokemonName);
        GetPokemonInfoResponse getPokemonInfoResponse = new GetPokemonInfoResponse(pokemon.name(), pokemon.description(), pokemon.habitat(), pokemon.isLegendary());
        log.debug("Inbound HTTP Response. Path: {} pokemonName {} Response {}",
                GET_POKEMON_INFO_ENDPOINT, pokemonName, getPokemonInfoResponse);
        return getPokemonInfoResponse;
    }

    @GetMapping(value = GET_TRANSLATED_POKEMON_INFO_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    public GetPokemonInfoResponse getTranslatedPokemonInfo(@PathVariable String pokemonName) {
        log.debug("Inbound HTTP Request. Path: {} pokemonName {}", GET_POKEMON_INFO_ENDPOINT, pokemonName);
        Pokemon pokemon = getTranslatedPokemonInfoUseCase.execute(pokemonName);
        GetPokemonInfoResponse getPokemonInfoResponse = new GetPokemonInfoResponse(pokemon.name(), pokemon.description(), pokemon.habitat(), pokemon.isLegendary());
        log.debug("Inbound HTTP Response. Path: {} pokemonName {} Response {}",
                GET_POKEMON_INFO_ENDPOINT, pokemonName, getPokemonInfoResponse);
        return getPokemonInfoResponse;
    }
}
