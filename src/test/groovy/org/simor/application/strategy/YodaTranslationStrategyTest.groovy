package org.simor.application.strategy

import spock.lang.Specification

class YodaTranslationStrategyTest extends Specification {

    private YodaTranslationStrategy strategy

    def setup(){
        strategy = new YodaTranslationStrategy()
    }

    def "Given cave habitat it returns true"(){
        expect:
        strategy.isApplicable("cave", false)
    }

    def "Given isLegendary true it returns true"(){
        expect:
        strategy.isApplicable("none", true)
    }

    def "Given no cave habitat and no legendary it returns false"(){
        expect:
        !strategy.isApplicable("some", false)
    }
}
