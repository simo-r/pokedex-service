package org.simor.application.usecase

import org.simor.adapter.client.PokemonRestClient
import org.simor.entity.FlavorLanguage
import org.simor.entity.FlavorTextEntry
import org.simor.entity.FlavorVersion
import org.simor.entity.Habitat
import org.simor.entity.PokemonSpec
import org.simor.entity.PokemonInfoResponse
import spock.lang.Specification
import spock.lang.Unroll

class GetPokemonInfoImplTest extends Specification {

    private PokemonRestClient repository
    private GetPokemonInfoImpl pokemonInfoUseCase

    def setup() {
        repository = Mock(PokemonRestClient)
        pokemonInfoUseCase = new GetPokemonInfoImpl(repository)
    }

    def "Given valid pokemon spec it returns its basic information"() {
        given:
        def validPokemonName = "mewtwo"
        def validPokemonSpec = new PokemonSpec(
                "mewtwo",
                [new FlavorTextEntry("Red sample description",
                        new FlavorLanguage("en"),
                        new FlavorVersion("red")),
                 new FlavorTextEntry("Blue sample description",
                         new FlavorLanguage("en"),
                         new FlavorVersion("blue"))
                ], new Habitat("rare"), true)
        when:
        def pokemonInfo = pokemonInfoUseCase.execute(validPokemonName)
        then:
        1 * repository.getPokemonSpec(validPokemonName) >> validPokemonSpec
        0 * repository.getPokemonSpec(_)
        pokemonInfo == new PokemonInfoResponse("mewtwo", "Red sample description", "rare", true)
    }

    @Unroll
    def "Given pokemon spec without required flavor description it return empty description"() {
        given:
        def validPokemonName = "mewtwo"
        when:
        def pokemonInfo = pokemonInfoUseCase.execute(validPokemonName)
        then:
        1 * repository.getPokemonSpec(validPokemonName) >> invalidPokemonSpec
        0 * repository.getPokemonSpec(_)
        pokemonInfo == new PokemonInfoResponse("mewtwo", "", "rare", true)
        where:
        invalidPokemonSpec <<
                [new PokemonSpec("mewtwo",
                        [new FlavorTextEntry("Red sample description",
                                new FlavorLanguage("XX"),
                                new FlavorVersion("red"))
                        ], new Habitat("rare"), true),
                 new PokemonSpec("mewtwo",
                         [new FlavorTextEntry("Red sample description",
                                 new FlavorLanguage("en"),
                                 new FlavorVersion("blue"))
                         ], new Habitat("rare"), true),
                 new PokemonSpec("mewtwo",
                         [new FlavorTextEntry("Red sample description",
                                 new FlavorLanguage("xx"),
                                 new FlavorVersion("color"))
                         ], new Habitat("rare"), true)
                ]
    }
}
