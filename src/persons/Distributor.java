package persons;

import documents.Contract;
import factories.StrategyFactory;
import interfaces.IPerson;
import interfaces.ProducerStrategy;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public final class Distributor implements IPerson, Observer {
    static final int DIV = 10;
    static final double PROFIT = 0.2;
    private final int id;
    private final int contractLength;
    private int initialBudget;
    private int initialInfrastructureCost;
    private int productionCost;
    private int energyNeededKW;
    private final String producerStrategy;
    private int contractCost;
    private boolean isBankrupt;
    private ArrayList<Contract> contracts = new ArrayList<>();
    private ArrayList<Producer> producersList = new ArrayList<>();
    private boolean ok;

    public Distributor(final int id, final int contractLength,
                       final int initialBudget,
                       final int initialInfrastructureCost,
                       final int energyNeededKW,
                       final String producerStrategy) {
        this.id = id;
        this.contractLength = contractLength;
        this.initialBudget = initialBudget;
        this.initialInfrastructureCost = initialInfrastructureCost;
        this.productionCost = 0;
        this.energyNeededKW = energyNeededKW;
        this.producerStrategy = producerStrategy;
        this.contractCost = 0;
        this.isBankrupt = false;
        this.ok = true;
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

    public int getEnergyNeededKW() {
        return energyNeededKW;
    }

    public String getProducerStrategy() {
        return producerStrategy;
    }

    public int getContractCost() {
        return contractCost;
    }

    public boolean isBankrupt() {
        return isBankrupt;
    }

    public ArrayList<Contract> getContracts() {
        return contracts;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public int getProductionCost() {
        return productionCost;
    }

    public void setBankrupt(boolean bankrupt) {
        isBankrupt = bankrupt;
    }

    public void setProductionCost(int productionCost) {
        this.productionCost = productionCost;
    }

    public ArrayList<Producer> getProducersList() {
        return producersList;
    }

    public void setProducersList(ArrayList<Producer> producersList) {
        this.producersList = producersList;
    }

    public void setInitialInfrastructureCost(int initialInfrastructureCost) {
        this.initialInfrastructureCost = initialInfrastructureCost;
    }

    public void setContractCost(int contractCost) {
        this.contractCost = contractCost;
    }

    @Override
    public String toString() {
        return "Distributor{" + "id=" + id
                + ", contractLength=" + contractLength
                + ", initialBudget=" + initialBudget
                + ", initialInfrastructureCost=" + initialInfrastructureCost
                + ", energyNeededKW=" + energyNeededKW
                + ", producerStrategy='" + producerStrategy
                + ", contractCost=" + contractCost
                + ", isBankrupt=" + isBankrupt
                + ", contracts=" + contracts
                + ", producersLIST=" + producersList
                + '}';
    }

    @Override
    public void monthlyPay(int costs, Contract contract) {
        this.initialBudget -= costs;
    }

    @Override
    public void monthlyReceive(int sum) {
        this.initialBudget += sum;
    }

    /**
     * formula in order to calculate the productionCOst
     * @return productionCost
     */
    public int calculateProductionCost() {
        int prodCost = 0;
        for (Producer producer : producersList) {
            prodCost += producer.getEnergyPerDistributor() * producer.getPriceKW();
        }
        return prodCost / DIV;
    }

    /**
     * formul ain order to calculate contractPrice
     * @return contractPrice
     */
    public int getContractPrice() {
        int price = 0;
        if (this.contracts.size() > 0) {
            price = (int) (this.initialInfrastructureCost / this.contracts.size()
                    + this.getProductionCost()
                    + PROFIT * this.getProductionCost());
        } else if (this.contracts.size() == 0) {
            price = (int) (this.initialInfrastructureCost + this.getProductionCost()
                    + PROFIT * this.getProductionCost());
        }
        this.contractCost = price;
        return price;
    }

    /**
     * calculates costs for a distributor
     * @return costs
     */
    public int getCosts() {
        int costs = 0;
        if (this.ok) {
            if (!contracts.isEmpty()) {
                costs = (int) (this.initialInfrastructureCost)
                        + contracts.size() * this.productionCost;
            } else {
                costs = (int) (this.initialInfrastructureCost);
            }
        } else {
            if (!contracts.isEmpty()) {
                costs = (int) (this.initialInfrastructureCost)
                        + (contracts.size() + 1) * this.productionCost;
            } else {
                costs = (int) (this.initialInfrastructureCost);
            }
        }
        return costs;
    }

    @Override
    public void update(Observable o, Object id) {
        getProducersList().removeIf(producer -> producer.getId() == (int) id);
        getProducersList().clear();
    }
}
