package org.simor.entity.model

import org.simor.entity.domain.Pokemon
import spock.lang.Specification
import spock.lang.Unroll

class PokemonRestClientResponseTest extends Specification {

    def "Given a PokemonRestClientResponse it is converted to the Pokemon domain"() {
        given:
        def pokemonRestClientResponse = new PokemonRestClientResponse("mewtwo",
                [new PokemonRestClientResponse.FlavorTextEntry("Red sample description",
                        new PokemonRestClientResponse.FlavorTextEntry.Language("en"),
                        new PokemonRestClientResponse.FlavorTextEntry.Version("red"))
                ], new PokemonRestClientResponse.Habitat("rare"), true)
        when:
        def pokemon = pokemonRestClientResponse.toDomain()
        then:
        pokemon == new Pokemon("mewtwo", "Red sample description", "rare", true)
    }

    @Unroll
    def "Given PokemonRestClientResponse without required flavor description it returns Pokemon domain with empty description"() {
        when:
        def pokemon = invalidPokemonSpec.toDomain()
        then:
        pokemon == new Pokemon("mewtwo", "", "rare", true)
        where:
        invalidPokemonSpec <<
                [new PokemonRestClientResponse("mewtwo",
                        [new PokemonRestClientResponse.FlavorTextEntry("Red sample description",
                                new PokemonRestClientResponse.FlavorTextEntry.Language("XX"),
                                new PokemonRestClientResponse.FlavorTextEntry.Version("red"))
                        ], new PokemonRestClientResponse.Habitat("rare"), true),
                 new PokemonRestClientResponse("mewtwo",
                         [new PokemonRestClientResponse.FlavorTextEntry("Red sample description",
                                 new PokemonRestClientResponse.FlavorTextEntry.Language("en"),
                                 new PokemonRestClientResponse.FlavorTextEntry.Version("blue"))
                         ], new PokemonRestClientResponse.Habitat("rare"), true),
                 new PokemonRestClientResponse("mewtwo",
                         [new PokemonRestClientResponse.FlavorTextEntry("Red sample description",
                                 new PokemonRestClientResponse.FlavorTextEntry.Language("xx"),
                                 new PokemonRestClientResponse.FlavorTextEntry.Version("color"))
                         ], new PokemonRestClientResponse.Habitat("rare"), true)
                ]
    }
}
