package factories;

import interfaces.ProducerStrategy;
import persons.Producer;
import strategies.EnergyChoiceStrategyType;
import strategies.GreenStrategy;
import strategies.PriceStrategy;
import strategies.QuantityStrategy;

import java.util.List;

public class StrategyFactory {

    public static ProducerStrategy createStrategy(String strategyType, List<Producer> producers) {
        if (strategyType.equals("GREEN")) {
            return new GreenStrategy(producers);
        } else if (strategyType.equals("PRICE")) {
            return new PriceStrategy(producers);
        } else if (strategyType.equals("QUANTITY")) {
           // System.out.println("ia");
            return new QuantityStrategy(producers);
        }
        return null;
    }
}
