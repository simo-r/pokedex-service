package org.simor.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

// TODO Validate presence of fields and usage of possible enums in habitat
public record PokemonInfoResponse(String name, String description, String habitat,
                                  @JsonProperty("is_legendary") boolean isLegendary) {
}
