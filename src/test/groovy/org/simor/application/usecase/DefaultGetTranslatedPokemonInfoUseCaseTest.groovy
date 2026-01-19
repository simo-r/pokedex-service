package org.simor.application.usecase

import org.simor.adapter.client.TranslationClient
import org.simor.adapter.client.TranslationRestClientException
import org.simor.application.strategy.TranslationStrategy
import org.simor.application.strategy.TranslationStrategyRegistry
import org.simor.entity.domain.Pokemon
import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Unroll

class DefaultGetTranslatedPokemonInfoUseCaseTest extends Specification {

    private GetPokemonInfoUseCase getPokemonInfoUseCase
    private TranslationStrategyRegistry translationStrategyRegistry;
    private GetTranslatedPokemonInfoUseCase getTranslatedPokemonInfo

    def setup() {
        getPokemonInfoUseCase = Mock(GetPokemonInfoUseCase)
        translationStrategyRegistry = Mock(TranslationStrategyRegistry)
        getTranslatedPokemonInfo = new DefaultGetTranslatedPokemonInfoUseCase(getPokemonInfoUseCase, translationStrategyRegistry)
    }

    def "Given pokemon name it returns translated pokemon info"() {
        given:
        def pokemonName = "mewtwo"
        def habitat = "cave"
        def isLegendary = false
        def description = "Red sample description"
        def pokemonInfo = new Pokemon(pokemonName, description, habitat, isLegendary)
        def mockTranslationClient = Mock(TranslationClient)
        def expectedTranslatedDescription = "A translated description"

        when:
        def translatedPokemonInfo = getTranslatedPokemonInfo.execute(pokemonName)
        then:
        1 * getPokemonInfoUseCase.execute(pokemonName) >> pokemonInfo
        1 * translationStrategyRegistry.get(habitat, isLegendary) >> Optional.of(mockTranslationClient)
        1 * mockTranslationClient.getTranslation(description) >> expectedTranslatedDescription
        translatedPokemonInfo == new Pokemon(pokemonName, expectedTranslatedDescription, habitat, isLegendary)
    }

    @Unroll
    def "Given empty pokemon description it returns same pokemon info"() {
        given:
        def pokemonName = "mewtwo"
        def habitat = "cave"
        def isLegendary = false
        def pokemonInfo = new Pokemon(pokemonName, description, habitat, isLegendary)
        def mockTranslationClient = Mock(TranslationClient)

        when:
        def translatedPokemonInfo = getTranslatedPokemonInfo.execute(pokemonName)
        then:
        1 * getPokemonInfoUseCase.execute(pokemonName) >> pokemonInfo
        0 * translationStrategyRegistry.get(_, _)
        0 * mockTranslationClient.getTranslation(_)
        translatedPokemonInfo == new Pokemon(pokemonName, description, habitat, isLegendary)
        where:
        description << [null, "", "  "]
    }

    def "Given pokemon name but no translation strategy it returns pokemon info"() {
        given:
        def pokemonName = "mewtwo"
        def habitat = "cave"
        def isLegendary = false
        def description = "Red sample description"
        def pokemonInfo = new Pokemon(pokemonName, description, habitat, isLegendary)
        def mockTranslationClient = Mock(TranslationClient)

        when:
        def translatedPokemonInfo = getTranslatedPokemonInfo.execute(pokemonName)
        then:
        1 * getPokemonInfoUseCase.execute(pokemonName) >> pokemonInfo
        1 * translationStrategyRegistry.get(habitat, isLegendary) >> Optional.empty()
        0 * mockTranslationClient.getTranslation(_)
        translatedPokemonInfo == new Pokemon(pokemonName, description, habitat, isLegendary)
    }

    def "Given pokemon name but exception during translation it returns pokemon info"() {
        given:
        def pokemonName = "mewtwo"
        def habitat = "cave"
        def isLegendary = false
        def description = "Red sample description"
        def pokemonInfo = new Pokemon(pokemonName, description, habitat, isLegendary)
        def mockTranslationClient = Mock(TranslationClient)

        when:
        def translatedPokemonInfo = getTranslatedPokemonInfo.execute(pokemonName)
        then:
        1 * getPokemonInfoUseCase.execute(pokemonName) >> pokemonInfo
        1 * translationStrategyRegistry.get(habitat, isLegendary) >> Optional.of(mockTranslationClient)
        1 * mockTranslationClient.getTranslation(description) >>
                { throw new TranslationRestClientException(HttpStatus.BAD_GATEWAY, "Error") }
        translatedPokemonInfo == new Pokemon(pokemonName, description, habitat, isLegendary)
    }
}
