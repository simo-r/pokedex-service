package org.simor.application.usecase;

import lombok.RequiredArgsConstructor;
import org.simor.adapter.client.PokemonRestClient;
import org.simor.entity.FlavorTextEntry;
import org.simor.entity.PokemonSpec;
import org.simor.entity.PokemonInfoResponse;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PokemonInfoUseCaseImpl implements PokemonInfoUseCase {

    public static final String DEFAULT_LANGUAGE = "en";
    public static final String DEFAULT_FLAVOR = "red";
    private final PokemonRestClient repository;

    @Override
    public PokemonInfoResponse getBasicPokemonInfo(String pokemonName) {
        PokemonSpec spec = repository.getPokemonSpec(pokemonName);
        // FIXME if flavor does not exist -> npe
        // FIXME if combination of en/red does not exist -> .get() -> noSuchElementexception
        String description = spec.flavorTextEntries()
                .stream()
                .filter(flavorTextEntry -> DEFAULT_LANGUAGE.equalsIgnoreCase(flavorTextEntry.language().name()))
                .filter(flavorTextEntry -> DEFAULT_FLAVOR.equalsIgnoreCase(flavorTextEntry.version().name()))
                .map(FlavorTextEntry::flavorText)
                .findFirst()
                .orElseThrow(() ->
                        new PokemonDescriptionFlavorException(
                                String.format("Flavor with language %s and version %s does not exist",
                                        DEFAULT_LANGUAGE, DEFAULT_FLAVOR)));
        return new PokemonInfoResponse(spec.name(), description, spec.habitat().name(), spec.isLegendary());
    }

}
