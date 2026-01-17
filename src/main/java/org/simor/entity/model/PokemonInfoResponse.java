package org.simor.entity.model;

// TODO Validate presence of fields and usage of possible enums in habitat
public record PokemonInfoResponse(String name, String description, String habitat, boolean is_legendary) {
}
