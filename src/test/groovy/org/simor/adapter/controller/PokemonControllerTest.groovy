package org.simor.adapter.controller

import org.simor.application.usecase.GetPokemonInfoUseCase
import org.simor.entity.PokemonInfoResponse
import spock.lang.Specification

class PokemonControllerTest extends Specification {

    private GetPokemonInfoUseCase pokemonInfoUseCase
    private PokemonController controller

    def setup() {
        pokemonInfoUseCase = Mock(GetPokemonInfoUseCase)
        controller = new PokemonController(pokemonInfoUseCase)
    }

    def "Given a pokemon name it returns its basic information"() {
        given:
        def pokemonName = "aName"
        def pokemonInfo = new PokemonInfoResponse(pokemonName, "Description", "Habitat", false)
        when:
        def info = controller.getPokemonInfo(pokemonName)
        then:
        1 * pokemonInfoUseCase.execute(pokemonName) >> pokemonInfo
        0 * pokemonInfoUseCase.execute(_)
        info == pokemonInfo
    }
}
