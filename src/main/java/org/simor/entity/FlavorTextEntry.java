package org.simor.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FlavorTextEntry(@JsonProperty("flavor_text") String flavorText, FlavorLanguage language, FlavorVersion version ) {
}
