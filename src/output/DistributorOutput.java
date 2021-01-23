package output;

import documents.Contract;

import java.util.ArrayList;

public final class DistributorOutput {
    private final int id;
    private final int energyNeededKW;
    private final int contractCost;
    private final int budget;
    private final String producerStrategy;
    private final boolean isBankrupt;
    private final ArrayList<Contract> contracts;

    public DistributorOutput(final int id, int energyNeededKW,
                             final int contractCost, final int budget,
                             final String producerStrategy,
                             final boolean isBankrupt,
                             final ArrayList<Contract> contracts) {
        this.id = id;
        this.energyNeededKW = energyNeededKW;
        this.contractCost = contractCost;
        this.budget = budget;
        this.producerStrategy = producerStrategy;
        this.isBankrupt = isBankrupt;
        this.contracts = contracts;
    }

    public int getId() {
        return id;
    }

    public int getEnergyNeededKW() {
        return energyNeededKW;
    }

    public int getContractCost() {
        return contractCost;
    }

    public int getBudget() {
        return budget;
    }

    public String getProducerStrategy() {
        return producerStrategy;
    }
    /**
     * getter for isBankrupt in order to write in results.out according to ref files
     * @return boolean isBankrupt
     */
    public boolean getisBankrupt() {
        return isBankrupt;
    }

    public ArrayList<Contract> getContracts() {
        return contracts;
    }
}
