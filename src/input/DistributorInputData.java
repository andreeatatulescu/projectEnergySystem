package input;

public final class DistributorInputData {
    private final int id;
    private final int contractLength;
    private final int initialBudget;
    private final int initialInfrastructureCost;
    private final int energyNeeded;
    private final String producerStrategy;

    public DistributorInputData(final int id, final int contractLength,
                                final int initialBudget,
                                final int initialInfrastructureCost,
                                final int energyNeeded,
                                final String producerStrategy) {
        this.id = id;
        this.contractLength = contractLength;
        this.initialBudget = initialBudget;
        this.initialInfrastructureCost = initialInfrastructureCost;
        this.energyNeeded = energyNeeded;
        this.producerStrategy = producerStrategy;
    }

    public int getId() {
        return id;
    }

    public int getContractLength() {
        return contractLength;
    }

    public int getInitialBudget() {
        return initialBudget;
    }

    public int getInitialInfrastructureCost() {
        return initialInfrastructureCost;
    }

    public int getEnergyNeeded() {
        return energyNeeded;
    }

    public String getProducerStrategy() {
        return producerStrategy;
    }

    @Override
    public String toString() {
        return "DistributorInputData{"
                + "id=" + id
                + ", contractLength=" + contractLength
                + ", initialBudget=" + initialBudget
                + ", initialInfrastructureCost=" + initialInfrastructureCost
                + ", energyNeeded=" + energyNeeded
                + ", producerStrategy=" + producerStrategy
                + '}';
    }
}
