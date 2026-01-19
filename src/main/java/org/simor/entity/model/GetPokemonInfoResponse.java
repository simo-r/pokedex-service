package org.simor.entity.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetPokemonInfoResponse(String name, String description, String habitat,
                                     @JsonProperty("is_legendary") boolean isLegendary) {
}
