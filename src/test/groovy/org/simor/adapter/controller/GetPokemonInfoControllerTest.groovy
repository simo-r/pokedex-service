package org.simor.adapter.controller

import org.simor.application.usecase.GetPokemonInfoUseCase
import org.simor.application.usecase.GetTranslatedPokemonInfoUseCase
import org.simor.entity.PokemonInfoResponse
import spock.lang.Specification

class GetPokemonInfoControllerTest extends Specification {

    private GetPokemonInfoUseCase getPokemonInfoUseCase
    private GetTranslatedPokemonInfoUseCase getTranslatedPokemonInfoUseCase
    private GetPokemonInfoController controller

    def setup() {
        getPokemonInfoUseCase = Mock(GetPokemonInfoUseCase)
        getTranslatedPokemonInfoUseCase = Mock(GetTranslatedPokemonInfoUseCase)
        controller = new GetPokemonInfoController(getPokemonInfoUseCase, getTranslatedPokemonInfoUseCase)
    }

    def "Given a pokemon name it returns its basic information"() {
        given:
        def pokemonName = "aName"
        def pokemonInfo = new PokemonInfoResponse(pokemonName, "Description", "Habitat", false)
        when:
        def info = controller.getPokemonInfo(pokemonName)
        then:
        1 * getPokemonInfoUseCase.execute(pokemonName) >> pokemonInfo
        0 * getPokemonInfoUseCase.execute(_)
        info == pokemonInfo
    }

    def "Given a pokemon name it returns its translated basic information"() {
        given:
        def pokemonName = "aName"
        def pokemonInfo = new PokemonInfoResponse(pokemonName, "Translated description", "Habitat", false)
        when:
        def info = controller.getTranslatedPokemonInfo(pokemonName)
        then:
        1 * getTranslatedPokemonInfoUseCase.execute(pokemonName) >> pokemonInfo
        0 * getTranslatedPokemonInfoUseCase.execute(_)
        info == pokemonInfo
    }
}
