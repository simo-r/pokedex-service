package org.simor.application.usecase.impl

import org.simor.adapter.client.PokemonRestClient
import org.simor.application.usecase.exception.PokemonDescriptionFlavorException
import org.simor.entity.dto.FlavorLanguage
import org.simor.entity.dto.FlavorTextEntry
import org.simor.entity.dto.FlavorVersion
import org.simor.entity.dto.Habitat
import org.simor.entity.dto.PokemonSpec
import org.simor.entity.model.PokemonInfoResponse
import spock.lang.Specification
import spock.lang.Unroll

class PokemonInfoUseCaseImplTest extends Specification {

    private PokemonRestClient repository
    private PokemonInfoUseCaseImpl pokemonInfoUseCase

    def setup() {
        repository = Mock(PokemonRestClient)
        pokemonInfoUseCase = new PokemonInfoUseCaseImpl(repository)
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
        def pokemonInfo = pokemonInfoUseCase.getBasicPokemonInfo(validPokemonName)
        then:
        1 * repository.getPokemonSpec(validPokemonName) >> validPokemonSpec
        0 * repository.getPokemonSpec(_)
        pokemonInfo == new PokemonInfoResponse("mewtwo", "Red sample description", "rare", true)
    }

    @Unroll
    def "Given pokemon spec without required flavor description it throws PokemonDescriptionFlavorNotPresent"() {
        given:
        def validPokemonName = "mewtwo"
        when:
        pokemonInfoUseCase.getBasicPokemonInfo(validPokemonName)
        then:
        1 * repository.getPokemonSpec(validPokemonName) >> invalidPokemonSpec
        0 * repository.getPokemonSpec(_)
        def e = thrown PokemonDescriptionFlavorException
        e.getMessage() == "Flavor with language en and version red does not exist"
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
