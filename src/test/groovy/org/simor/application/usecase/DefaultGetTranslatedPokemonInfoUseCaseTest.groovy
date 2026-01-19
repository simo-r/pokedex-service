package org.simor.application.usecase

import org.simor.adapter.client.TranslationClient
import org.simor.adapter.client.TranslationRestClientException
import org.simor.application.strategy.TranslationStrategy
import org.simor.entity.domain.Pokemon
import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Unroll

class DefaultGetTranslatedPokemonInfoUseCaseTest extends Specification {

    private GetPokemonInfoUseCase getPokemonInfoUseCase
    private Map<TranslationStrategy, TranslationClient> strategyTranslationClientMap;
    private GetTranslatedPokemonInfoUseCase getTranslatedPokemonInfo

    def setup() {
        getPokemonInfoUseCase = Mock(GetPokemonInfoUseCase)
        strategyTranslationClientMap = Mock(Map)
        getTranslatedPokemonInfo = new DefaultGetTranslatedPokemonInfoUseCase(getPokemonInfoUseCase, strategyTranslationClientMap)
    }

    def "Given pokemon name it returns translated pokemon info"() {
        given:
        def pokemonName = "mewtwo"
        def habitat = "cave"
        def isLegendary = false
        def description = "Red sample description"
        def pokemonInfo = new Pokemon(pokemonName, description, habitat, isLegendary)
        def mockStrategy = Mock(TranslationStrategy)
        def mockTranslationClient = Mock(TranslationClient)
        def expectedTranslatedDescription = "A translated description"

        when:
        def translatedPokemonInfo = getTranslatedPokemonInfo.execute(pokemonName)
        then:
        1 * getPokemonInfoUseCase.execute(pokemonName) >> pokemonInfo
        1 * strategyTranslationClientMap.entrySet() >> Set.of(Map.entry(mockStrategy, mockTranslationClient))
        1 * mockStrategy.isApplicable(habitat, isLegendary) >> true
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
        def mockStrategy = Mock(TranslationStrategy)
        def mockTranslationClient = Mock(TranslationClient)

        when:
        def translatedPokemonInfo = getTranslatedPokemonInfo.execute(pokemonName)
        then:
        1 * getPokemonInfoUseCase.execute(pokemonName) >> pokemonInfo
        0 * strategyTranslationClientMap.entrySet()
        0 * mockStrategy.isApplicable(_, _)
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
        def mockStrategy = Mock(TranslationStrategy)
        def mockTranslationClient = Mock(TranslationClient)

        when:
        def translatedPokemonInfo = getTranslatedPokemonInfo.execute(pokemonName)
        then:
        1 * getPokemonInfoUseCase.execute(pokemonName) >> pokemonInfo
        1 * strategyTranslationClientMap.entrySet() >> Collections.emptySet()
        0 * mockStrategy.isApplicable(_, _)
        0 * mockTranslationClient.getTranslation(_)
        translatedPokemonInfo == new Pokemon(pokemonName, description, habitat, isLegendary)
    }

    def "Given pokemon name but no applicable translation strategy it returns pokemon info"() {
        given:
        def pokemonName = "mewtwo"
        def habitat = "cave"
        def isLegendary = false
        def description = "Red sample description"
        def pokemonInfo = new Pokemon(pokemonName, description, habitat, isLegendary)
        def mockStrategy = Mock(TranslationStrategy)
        def mockTranslationClient = Mock(TranslationClient)

        when:
        def translatedPokemonInfo = getTranslatedPokemonInfo.execute(pokemonName)
        then:
        1 * getPokemonInfoUseCase.execute(pokemonName) >> pokemonInfo
        1 * strategyTranslationClientMap.entrySet() >> Set.of(Map.entry(mockStrategy, mockTranslationClient))
        1 * mockStrategy.isApplicable(habitat, isLegendary) >> false
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
        def mockStrategy = Mock(TranslationStrategy)
        def mockTranslationClient = Mock(TranslationClient)

        when:
        def translatedPokemonInfo = getTranslatedPokemonInfo.execute(pokemonName)
        then:
        1 * getPokemonInfoUseCase.execute(pokemonName) >> pokemonInfo
        1 * strategyTranslationClientMap.entrySet() >> Set.of(Map.entry(mockStrategy, mockTranslationClient))
        1 * mockStrategy.isApplicable(habitat, isLegendary) >> true
        1 * mockTranslationClient.getTranslation(description) >>
                { throw new TranslationRestClientException(HttpStatus.BAD_GATEWAY, "Error") }
        translatedPokemonInfo == new Pokemon(pokemonName, description, habitat, isLegendary)
    }
}
