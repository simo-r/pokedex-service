package org.simor.application.strategy

import org.simor.adapter.client.TranslationClient
import spock.lang.Specification

class TranslationStrategyRegistryTest extends Specification {

    private Map<TranslationStrategy, TranslationClient> map
    private TranslationStrategyRegistry translationStrategyRegistry

    def setup() {
        map = new LinkedHashMap<>()
        translationStrategyRegistry = new TranslationStrategyRegistry(map)
    }

    def "Given a registered strategy it is returned"() {
        given:
        def translationStrategy = Mock(TranslationStrategy)
        def translationClient = Mock(TranslationClient)
        map.put(translationStrategy, translationClient)
        when:
        def selectedTranslationClient = translationStrategyRegistry.get("a", true)
        then:
        1 * translationStrategy.isApplicable("a", true) >> true
        selectedTranslationClient.isPresent()
        selectedTranslationClient.get() == translationClient
    }

    def "Given multiple strategies it is selected based on register order"() {
        given:
        def firstStrategy = Mock(TranslationStrategy)
        def firstTranslationClient = Mock(TranslationClient)
        def secondStrategy = Mock(TranslationStrategy)
        def secondTranslationClient = Mock(TranslationClient)
        map.put(firstStrategy, firstTranslationClient)
        map.put(secondStrategy, secondTranslationClient)
        when:
        def selectedTranslationClient = translationStrategyRegistry.get("a", true)
        then:
        1 * firstStrategy.isApplicable("a", true) >> true
        0 * secondStrategy.isApplicable(_, _)
        selectedTranslationClient.isPresent()
        selectedTranslationClient.get() == firstTranslationClient
    }
}
