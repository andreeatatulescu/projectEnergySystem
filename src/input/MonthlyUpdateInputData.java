package input;

import java.util.List;

public final class MonthlyUpdateInputData {
    private final List<ConsumerInputData> newConsumers;
    private final List<DistributorChanges> distributorChanges;
    private final List<ProducerChanges> producerChanges;

    public MonthlyUpdateInputData(final List<ConsumerInputData> newConsumers,
                                  final List<DistributorChanges> distributorChanges,
                                  final List<ProducerChanges> producerChanges) {
        this.newConsumers = newConsumers;
        this.distributorChanges = distributorChanges;
        this.producerChanges = producerChanges;
    }

    public List<ConsumerInputData> getNewConsumers() {
        return newConsumers;
    }

    public List<DistributorChanges> getDistributorChanges() {
        return distributorChanges;
    }

    public List<ProducerChanges> getProducerChanges() {
        return producerChanges;
    }

    @Override
    public String toString() {
        return "MonthlyUpdateInputData{ "
                + "newConsumers=" + newConsumers
                + ", distributorChanges=" + distributorChanges
                + ", producerChanges=" + producerChanges
                + '}';
    }
}
