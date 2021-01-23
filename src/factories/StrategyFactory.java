package factories;

import interfaces.ProducerStrategy;
import persons.Producer;
import strategies.GreenStrategy;
import strategies.PriceStrategy;
import strategies.QuantityStrategy;

import java.util.List;

public final class StrategyFactory {

    private StrategyFactory() {
    }

    /**
     * creating the needed strategy
     * @param strategyType - from distributor
     * @param producers - the list that will be sorted using the strategy
     * @return a new strategy
     */
    public static ProducerStrategy createStrategy(String strategyType, List<Producer> producers) {
        if (strategyType.equals("GREEN")) {
            return new GreenStrategy(producers);
        } else if (strategyType.equals("PRICE")) {
            return new PriceStrategy(producers);
        } else if (strategyType.equals("QUANTITY")) {
            return new QuantityStrategy(producers);
        }
        return null;
    }
}
