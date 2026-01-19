package org.simor.entity.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.simor.entity.domain.Pokemon;

import java.util.List;

public record PokemonRestClientResponse(String name,
                                        @JsonProperty("flavor_text_entries") List<FlavorTextEntry> flavorTextEntries,
                                        Habitat habitat, @JsonProperty("is_legendary") boolean isLegendary) {

    private static final String PREFERRED_LANGUAGE = "en";
    private static final String PREFERRED_FLAVOR_VERSION = "red";

    public Pokemon toDomain() {
        String description = extractPreferredDescription(this.flavorTextEntries());
        return new Pokemon(
                this.name,
                description,
                this.habitat.name(),
                this.isLegendary
        );
    }

    private String extractPreferredDescription(List<FlavorTextEntry> entries) {
        return entries.stream()
                .filter(e -> PREFERRED_LANGUAGE.equalsIgnoreCase(e.language().name()))
                .filter(e -> PREFERRED_FLAVOR_VERSION.equalsIgnoreCase(e.version().name()))
                .map(FlavorTextEntry::flavorText)
                .findFirst()
                .orElse("");
    }

    public record FlavorTextEntry(@JsonProperty("flavor_text") String flavorText, Language language,
                                  Version version) {

        public record Language(String name) {
        }

        public record Version(String name) {
        }
    }

    public record Habitat(String name) {
    }
}
