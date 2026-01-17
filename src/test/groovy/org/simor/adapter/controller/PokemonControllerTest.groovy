package org.simor.adapter.controller

import org.simor.application.usecase.PokemonInfoUseCase
import org.simor.entity.PokemonInfoResponse
import spock.lang.Specification

class PokemonControllerTest extends Specification {

    private PokemonInfoUseCase pokemonInfoUseCase
    private PokemonController controller

    def setup() {
        pokemonInfoUseCase = Mock(PokemonInfoUseCase)
        controller = new PokemonController(pokemonInfoUseCase)
    }

    def "Given a pokemon name it returns its basic information"() {
        given:
        def pokemonName = "aName"
        def pokemonInfo = new PokemonInfoResponse(pokemonName, "Description", "Habitat", false)
        when:
        def info = controller.getPokemonInfo(pokemonName)
        then:
        1 * pokemonInfoUseCase.getBasicPokemonInfo(pokemonName) >> pokemonInfo
        0 * pokemonInfoUseCase.getBasicPokemonInfo(_)
        info == pokemonInfo
    }
}
