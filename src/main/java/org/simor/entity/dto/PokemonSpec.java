package org.simor.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PokemonSpec(String name, @JsonProperty("flavor_text_entries") List<FlavorTextEntry> flavorTextEntries,
                          Habitat habitat, @JsonProperty("is_legendary") boolean isLegendary) {
}
