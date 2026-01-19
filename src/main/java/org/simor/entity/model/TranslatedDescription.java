package org.simor.entity.model;

public record TranslatedDescription(Content contents) {

    public record Content(String translated){}
}
