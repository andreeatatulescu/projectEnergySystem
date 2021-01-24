package input;

import java.util.List;

public final class Input {
    private final long numberofTurns;
    private final List<ConsumerInputData> consumersData;
    private final List<DistributorInputData> distributorsData;
    private final List<ProducerInputData> producersData;
    private final List<MonthlyUpdateInputData> monthlyUpdatesData;

    public Input() {
        this.numberofTurns = 0;
        this.consumersData = null;
        this.distributorsData = null;
        this.producersData = null;
        this.monthlyUpdatesData = null;
    }

    public Input(final long numberofTurns, final List<ConsumerInputData> consumersData,
                 final List<DistributorInputData> distributorsData,
                 final List<ProducerInputData> producersData,
                 final List<MonthlyUpdateInputData> monthlyUpdates) {
        this.numberofTurns = numberofTurns;
        this.consumersData = consumersData;
        this.distributorsData = distributorsData;
        this.producersData = producersData;
        this.monthlyUpdatesData = monthlyUpdates;
    }

    public long getNumberofTurns() {
        return numberofTurns;
    }

    public List<ConsumerInputData> getConsumersData() {
        return consumersData;
    }

    public List<DistributorInputData> getDistributorsData() {
        return distributorsData;
    }

    public List<ProducerInputData> getProducersData() {
        return producersData;
    }

    public List<MonthlyUpdateInputData> getMonthlyUpdatesData() {
        return monthlyUpdatesData;
    }

    @Override
    public String toString() {
        return "Input{"
                + "numberOfTurns=" + numberofTurns
                + ", consumersData=" + consumersData
                + ", distributorsData=" + distributorsData
                + ", producersData=" + producersData
                + ", monthlyUpdatesData=" + monthlyUpdatesData
                + '}';
    }
}
