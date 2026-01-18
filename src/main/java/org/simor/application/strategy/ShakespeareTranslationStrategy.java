package org.simor.application.strategy;

public class ShakespeareTranslationStrategy implements TranslationStrategy {

    @Override
    public boolean isApplicable(String habitat, boolean isLegendary) {
        return true;
    }
}
