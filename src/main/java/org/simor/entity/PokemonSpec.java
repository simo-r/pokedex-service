package org.simor.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

// FIXME Simplification: assuming fields can't be null or empty
public record PokemonSpec(String name, @JsonProperty("flavor_text_entries") List<FlavorTextEntry> flavorTextEntries,
                          Habitat habitat, @JsonProperty("is_legendary") boolean isLegendary) {
}
