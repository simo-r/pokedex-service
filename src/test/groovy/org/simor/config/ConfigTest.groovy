package org.simor.config

import org.simor.adapter.client.TranslationClient
import org.simor.adapter.client.TranslationRestClient
import org.simor.application.strategy.ShakespeareTranslationStrategy
import org.simor.application.strategy.TranslationStrategy
import org.simor.application.strategy.YodaTranslationStrategy
import spock.lang.Specification

class ConfigTest extends Specification {

    private Config config

    def setup() {
        config = new Config()
    }

    def "Given strategies they are inserted in correct order"() {
        given:
        def yodaTranslation = new TranslationRestClient("a", "b")
        def shakespeareTranslation = new TranslationRestClient("c", "d")
        when:
        def strategyMap = config.strategyTranslationClientMap(yodaTranslation, shakespeareTranslation)
        then:
        Iterator<Map.Entry<TranslationStrategy, TranslationClient>> set = strategyMap.entrySet().iterator()
        def yodaEntry = set.next()
        yodaEntry.getKey() instanceof YodaTranslationStrategy
        yodaEntry.getValue() == yodaTranslation
        def shakespeareEntry = set.next()
        shakespeareEntry.getKey() instanceof ShakespeareTranslationStrategy
        shakespeareEntry.getValue() == shakespeareTranslation
    }
}
