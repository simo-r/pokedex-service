package org.simor.application.strategy

import spock.lang.Specification

class ShakespeareTranslationStrategyTest extends Specification {

    private ShakespeareTranslationStrategy strategy

    def setup(){
        strategy = new ShakespeareTranslationStrategy()
    }

    def "Given an input it returns true"(){
        expect:
        strategy.isApplicable("anHabitat", true)
    }
}
