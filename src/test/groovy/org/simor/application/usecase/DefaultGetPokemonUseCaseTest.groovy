package org.simor.application.usecase

import org.simor.adapter.client.PokemonRestClient
import org.simor.entity.domain.Pokemon
import spock.lang.Specification

class DefaultGetPokemonUseCaseTest extends Specification {

    private PokemonRestClient repository
    private DefaultGetPokemonUseCase pokemonInfoUseCase

    def setup() {
        repository = Mock(PokemonRestClient)
        pokemonInfoUseCase = new DefaultGetPokemonUseCase(repository)
    }

    def "Given valid pokemon spec it returns its basic information"() {
        given:
        def validPokemonName = "mewtwo"
        def validPokemonSpec = new Pokemon("mewtwo", "Red sample description", "rare", true)
        when:
        def pokemonInfo = pokemonInfoUseCase.execute(validPokemonName)
        then:
        1 * repository.getPokemonSpecies(validPokemonName) >> validPokemonSpec
        0 * repository.getPokemonSpecies(_)
        pokemonInfo == validPokemonSpec
    }
}
