package org.simor.application.usecase

import org.simor.adapter.client.PokemonRestClient
import org.simor.entity.domain.Pokemon
import spock.lang.Specification

class GetPokemonInfoServiceTest extends Specification {

    private PokemonRestClient repository
    private GetPokemonInfoService pokemonInfoUseCase

    def setup() {
        repository = Mock(PokemonRestClient)
        pokemonInfoUseCase = new GetPokemonInfoService(repository)
    }

    def "Given valid pokemon spec it returns its basic information"() {
        given:
        def validPokemonName = "mewtwo"
        def validPokemonSpec = new Pokemon("mewtwo", "Red sample description", "rare", true)
        when:
        def pokemonInfo = pokemonInfoUseCase.execute(validPokemonName)
        then:
        1 * repository.getPokemonSpec(validPokemonName) >> validPokemonSpec
        0 * repository.getPokemonSpec(_)
        pokemonInfo == validPokemonSpec
    }
}
