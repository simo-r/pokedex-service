package org.simor.application.strategy;

public class YodaTranslationStrategy implements TranslationStrategy {

    private static final String STRATEGY_HABITAT = "cave";

    @Override
    public boolean isApplicable(String habitat, boolean isLegendary) {
        return STRATEGY_HABITAT.equalsIgnoreCase(habitat) || isLegendary;
    }
}
