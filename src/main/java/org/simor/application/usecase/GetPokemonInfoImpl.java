package org.simor.application.usecase;

import lombok.RequiredArgsConstructor;
import org.simor.adapter.client.PokemonRestClient;
import org.simor.entity.FlavorTextEntry;
import org.simor.entity.PokemonSpec;
import org.simor.entity.PokemonInfoResponse;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class GetPokemonInfoImpl implements GetPokemonInfoUseCase {

    public static final String DEFAULT_LANGUAGE = "en";
    public static final String DEFAULT_FLAVOR = "red";
    private final PokemonRestClient pokemonRestClient;

    @Override
    public PokemonInfoResponse execute(String pokemonName) {
        PokemonSpec spec = pokemonRestClient.getPokemonSpec(pokemonName);
        String description = spec.flavorTextEntries()
                .stream()
                .filter(flavorTextEntry -> DEFAULT_LANGUAGE.equalsIgnoreCase(flavorTextEntry.language().name()))
                .filter(flavorTextEntry -> DEFAULT_FLAVOR.equalsIgnoreCase(flavorTextEntry.version().name()))
                .map(FlavorTextEntry::flavorText)
                .findFirst()
                .orElseGet(() -> {
                    // TODO Emit log/metric to observe that unexpected behaviour
                    return "";
                });
        return new PokemonInfoResponse(spec.name(), description, spec.habitat().name(), spec.isLegendary());
    }

}
