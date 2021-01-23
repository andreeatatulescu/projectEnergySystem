package strategies;

import interfaces.ProducerStrategy;
import persons.Producer;

import java.util.Comparator;
import java.util.List;

public final class GreenStrategy implements ProducerStrategy {
    private List<Producer> producers;

    public GreenStrategy(List<Producer> producers) {
        this.producers = producers;
    }


    @Override
    public void sortByStrategy() {
        producers.sort(Comparator.comparing(Producer::isRenewable, Comparator.reverseOrder())
        .thenComparing(Producer::getPriceKW)
        .thenComparing(Producer::getEnergyPerDistributor, Comparator.reverseOrder())
        .thenComparing(Producer::getId));

    }

    public List<Producer> getProducers() {
        return producers;
    }

    public void setProducers(List<Producer> producers) {
        this.producers = producers;
    }
}
