package gameflow;

import documents.Contract;
import documents.MonthlyStat;
import factories.IPersonFactory;
import factories.StrategyFactory;
import input.ConsumerInputData;
import input.DistributorChanges;
import input.Input;
import input.ProducerChanges;
import interfaces.IPerson;
import interfaces.ProducerStrategy;
import persons.Consumer;
import persons.Distributor;
import persons.Producer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class BasicRound {
    static final double PROFIT = 0.2;
    static final double REST = 1.2;
    private LinkedList<Distributor> distributorsAllUpdate = new LinkedList<>();
    private LinkedList<Consumer> consumersAllUpdate = new LinkedList<>();
    private LinkedList<Producer> producersAllUpdate = new LinkedList<>();
    private Contract contractaux = new Contract(0, 0, 0);
    private LinkedHashMap<Integer, Integer> distributorsByPrice = new LinkedHashMap<>();

    /**
     * play basic round
     * @param consumers from input data
     * @param distributors from input data
     * @param input all known data
     * @param pos current month - 1 (current position)
     */
    public void doBasicRound(final List<Consumer> consumers,
                             final List<Distributor> distributors,
                             final List<Producer> producers,
                             final Input input, final int pos) {

        LinkedHashMap<Integer, Integer> sorted = new LinkedHashMap<>();

        distributorsAllUpdate.addAll(distributors);
        consumersAllUpdate.addAll(consumers);
        producersAllUpdate.addAll(producers);

        currentDistributors(pos);
        readUpdate(input, pos);
        recalculateContractPrice();

        // sorted the distributors by the contract's price
        distributorsByPrice.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));
        Map.Entry<Integer, Integer> entry = sorted.entrySet().iterator().next();
        Integer key = entry.getKey();
        Integer value = entry.getValue();

        deleteContract();

        receiveSalaries();

        chooseSmart(key, value);
        payTime();

        noBankruptsConsumers();
        receiveMoney();

        noBankruptsDistributors();

        readUpdateProducer(input, pos);
        redistributionDistributors(pos);

        oneMonthLater();
    }

    /**
     * read monthlyUpdates one by one
     * @param input - initial data
     * @param pos - current month - 1 (current position)
     */
    public void readUpdate(final Input input, final int pos) {
        IPersonFactory personFactory = IPersonFactory.getInstance();

        if (!input.getMonthlyUpdatesData().get(pos).getNewConsumers().isEmpty()) {
            for (ConsumerInputData consumer : input.getMonthlyUpdatesData()
                    .get(pos).getNewConsumers()) {
                IPerson newConsumator = personFactory.getPerson("CONSUMER", consumer);
                consumersAllUpdate.add((Consumer) newConsumator);
            }
        }

        if (!input.getMonthlyUpdatesData().get(pos).getDistributorChanges().isEmpty()) {
            for (DistributorChanges distributorChange : input.getMonthlyUpdatesData()
                    .get(pos).getDistributorChanges()) {
                for (Distributor distributor : distributorsAllUpdate) {
                    if (distributorChange.getId() == distributor.getId()) {
                        distributor.setInitialInfrastructureCost(distributorChange
                                .getInfrastructureCost());
                    }
                }
            }
        }

    }

    /**
     * read ProducerUpdates
     * @param input - initial data
     * @param pos - current month - 1 (current position)
     */
    public void readUpdateProducer(final Input input, final int pos) {
        if (!input.getMonthlyUpdatesData().get(pos).getProducerChanges().isEmpty()) {

            for (ProducerChanges producerChange : input.getMonthlyUpdatesData()
                    .get(pos).getProducerChanges()) {

                for (Producer producerAux : producersAllUpdate) {
                    if (producerChange.getId() == producerAux.getId()) {
                        producerAux.changeEnergy(producerChange.getEnergyPerDistributor());
                        producerAux.getMonthlyStats().get(pos)
                                .setDistributorsIds(new ArrayList<>());
                    }

                }
            }
        }
    }

    /**
     * distributors choose again the producers and recalculate tge productionCost
     * @param pos current month - 1 (current position)
     */
    public void redistributionDistributors(final int pos) {
        for (Distributor distributor : distributorsAllUpdate) {
            if (distributor.getProducersList().isEmpty()) {
                ProducerStrategy strategy = StrategyFactory
                        .createStrategy(distributor.getProducerStrategy(), producersAllUpdate);
                strategy.sortByStrategy();

                int energyCnt = 0;
                for (Producer producer : producersAllUpdate) {
                    if (producer.getCurrentDistributorsIds().contains(distributor.getId())) {
                        Collections.sort(producer.getCurrentDistributorsIds());
                        producer.getCurrentDistributorsIds().remove(Integer
                                .valueOf(distributor.getId()));

                        producer.deleteObserver(distributor);
                        producer.setNrDistributors(producer.getNrDistributors() - 1);
                        ArrayList<Integer> currentDistrib = new ArrayList<>();
                        currentDistrib.addAll(producer.getCurrentDistributorsIds());
                        producer.getMonthlyStats().get(pos)
                                .setDistributorsIds(currentDistrib);
                    }
                    while (energyCnt < distributor.getEnergyNeededKW()
                            && producer.getNrDistributors() < producer.getMaxDistributors()) {
                        distributor.getProducersList().add(producer);
                        if (!producer.getCurrentDistributorsIds().contains(distributor.getId())) {
                            producer.getCurrentDistributorsIds().add(distributor.getId());
                            Collections.sort(producer.getCurrentDistributorsIds());
                        }
                        ArrayList<Integer> currentDistrib = new ArrayList<>();
                        currentDistrib.addAll(producer.getCurrentDistributorsIds());
                        Collections.sort(producer.getCurrentDistributorsIds());
                        producer.getMonthlyStats().get(pos)
                                .setDistributorsIds(currentDistrib);
                        producer.addObserver(distributor);
                        producer.setNrDistributors(producer.getNrDistributors() + 1);

                        energyCnt += producer.getEnergyPerDistributor();
                        break;
                    }
                }
                for (Producer producer : producersAllUpdate) {
                    if (energyCnt < distributor.getEnergyNeededKW()
                            && producer.getNrDistributors() < producer.getMaxDistributors()) {
                        if (!distributor.getProducersList().contains(producer)) {
                            distributor.getProducersList().add(producer);
                            if (!producer.getCurrentDistributorsIds()
                                    .contains(distributor.getId())) {
                                producer.getCurrentDistributorsIds().add(distributor.getId());
                                Collections.sort(producer.getCurrentDistributorsIds());
                            }
                            ArrayList<Integer> currentDistrib = new ArrayList<>();
                            currentDistrib.addAll(producer.getCurrentDistributorsIds());
                            Collections.sort(producer.getCurrentDistributorsIds());
                            producer.getMonthlyStats().get(pos)
                                    .setDistributorsIds(currentDistrib);
                            producer.addObserver(distributor);
                            producer.setNrDistributors(producer.getNrDistributors() + 1);

                            energyCnt += producer.getEnergyPerDistributor();
                        }
                    }
                }
            }
        }
        for (Distributor distributor : distributorsAllUpdate) {
            distributor.setProductionCost(distributor.calculateProductionCost());
        }
    }

    /**
     * info about the current distributors
     * @param pos current month - 1 (current position)
     */
    public void currentDistributors(final int pos) {
        for (Producer producer : producersAllUpdate) {
            ArrayList<Integer> currentDistrib = new ArrayList<>();
            currentDistrib.addAll(producer.getCurrentDistributorsIds());
            MonthlyStat newMonth = new MonthlyStat(pos + 1);
            producer.getMonthlyStats().add(newMonth);
            producer.getMonthlyStats().get(pos)
                    .setDistributorsIds(currentDistrib);

        }

    }

    /**
     * recalculating contracts price
     */
    public void recalculateContractPrice() {
        for (Distributor distributor : distributorsAllUpdate) {
            if (!distributor.isBankrupt()) {
                int price = 0;
                if (distributor.getContracts().size() > 0) {
                    price = (int) (distributor.getInitialInfrastructureCost()
                            / distributor.getContracts().size()
                            + distributor.getProductionCost()
                            + PROFIT * distributor.getProductionCost());
                    distributor.setContractCost(price);
                } else if (distributor.getContracts().size() == 0) {
                    price = (int) (distributor.getInitialInfrastructureCost()
                            + distributor.getProductionCost()
                            + PROFIT * distributor.getProductionCost());
                    distributor.setContractCost(price);
                }
                distributorsByPrice.put(distributor.getId(), price);
            }
        }
    }

    /**
     *  removing the contract when it expires from distributors database and consumers
     */
    public void deleteContract() {
        for (Distributor distributor : distributorsAllUpdate) {
            List<Contract> found = new ArrayList<>();
            for (Contract contract : distributor.getContracts()) {
                if (contract.getRemainedContractMonths() == 0) {
                    found.add(contract);
                    for (Consumer consumer : consumersAllUpdate) {
                        if (contract.getConsumerId() == consumer.getId()) {
                            consumer.setContract(false);
                        }
                    }
                }
            }
            distributor.getContracts().removeAll(found);
        }
        for (Consumer consumer : getConsumersAllUpdate()) {
            if (!consumer.getContractInfo().isEmpty()) {
                if (consumer.getContractInfo().get(0).getRemainedContractMonths() == 0) {
                    consumer.getContractInfo().remove(consumer.getContractInfo().get(0));
                }
            }
        }
    }

    /**
     * consumers receive salaries
     */
    public void receiveSalaries() {
        for (Consumer consumer : consumersAllUpdate) {
            if (!consumer.isBankrupt()) {
                consumer.monthlyReceive(consumer.getMonthlyIncome());
            }
        }
    }

    /**
     * // choosing the most advantageous contract for each consumer
     * @param key distributor's id
     * @param value contract price
     */
    public void chooseSmart(Integer key, Integer value) {
        for (Consumer consumer : consumersAllUpdate) {
            if (!consumer.isContract()) {
                for (Distributor distributor : distributorsAllUpdate) {
                    if (distributor.getId() == key) {
                        distributor.getContracts().add(new Contract(
                                consumer.getId(), value,
                                distributor.getContractLength()));
                        consumer.setContract(true);
                        consumer.getContractInfo().add(new Contract(
                                consumer.getId(), value,
                                distributor.getContractLength()));
                    }
                }
            }
        }
    }

    /**
     *  distributors pay the costs && consumers pay Rata and restants bills
     */
    public void payTime() {
        for (Distributor distributor : distributorsAllUpdate) {
            if (!distributor.isBankrupt()) {
                distributor.monthlyPay(distributor.getCosts(), contractaux);
            }
        }

        for (Consumer consumer : consumersAllUpdate) {
            if (!consumer.isBankrupt()) {
                if (consumer.isAlert()) {
                    if (!consumer.getContractInfo().isEmpty()) {
                        // pay the restant bill if the consumer can afford it
                        if (consumer.getInitialBudget()
                                >= (int) (consumer.getContractInfo().get(0)
                                .getPrice() + REST * consumer.getRest())) {
                            consumer.setInitialBudget(consumer
                                    .getInitialBudget() - (int) (consumer
                                    .getContractInfo().get(0).getPrice()
                                    + REST * consumer.getRest()));
                        } else {
                            consumer.setBankrupt(true);
                            consumer.setAlert(false);
                        }
                    }
                } else {
                    if (!consumer.getContractInfo().isEmpty()) {
                        consumer.monthlyPay(consumer.getContractInfo()
                                .get(0).getPrice(), consumer
                                .getContractInfo().get(0));
                    }
                }
            }
        }
    }

    /**
     * removing a consumer if it is Bankrupt
     */
    public void noBankruptsConsumers() {
        for (Distributor distributor : distributorsAllUpdate) {
            List<Contract> found = new ArrayList<>();
            for (Contract contract : distributor.getContracts()) {
                for (Consumer consumer : consumersAllUpdate) {
                    if (contract.getConsumerId() == consumer.getId()
                            && consumer.isBankrupt()) {
                        found.add(contract);
                    }
                }
            }
            distributor.getContracts().removeAll(found);
        }
    }

    /**
     * receiving money from clients
     */
    public void receiveMoney() {
        for (Distributor distributor : distributorsAllUpdate) {
            if (!distributor.getContracts().isEmpty()) {
                for (Contract contract : distributor.getContracts()) {
                    for (Consumer consumer : consumersAllUpdate) {
                        if (contract.getConsumerId() == consumer.getId()
                                && !consumer.isAlert()
                                && !consumer.isBankrupt()) {
                            distributor.monthlyReceive(contract.getPrice());
                        }
                    }
                }
            }
        }
    }

    /**
     *  clear its contract list if a distributor is Bankrupt
     */
    public void noBankruptsDistributors() {
        for (Distributor distributor : distributorsAllUpdate) {
            if (distributor.getInitialBudget() < 0) {
                distributor.setBankrupt(true);
                distributor.getContracts().clear();
            }
        }
    }

    /**
     * 1 month has passed
     */
    public void oneMonthLater() {
        for (Distributor distributor : distributorsAllUpdate) {
            if (!distributor.getContracts().isEmpty()) {
                for (Contract contract : distributor.getContracts()) {
                    contract.setRemainedContractMonths(contract
                            .getRemainedContractMonths() - 1);
                }
            }
        }

        for (Consumer consumer : consumersAllUpdate) {
            if (!consumer.getContractInfo().isEmpty()) {
                consumer.getContractInfo().get(0).setRemainedContractMonths(
                        consumer.getContractInfo().get(0).getRemainedContractMonths() - 1);
            }
        }
    }
    public LinkedList<Distributor> getDistributorsAllUpdate() {
        return distributorsAllUpdate;
    }

    public void setDistributorsAllUpdate(final LinkedList<Distributor> distributorsAllUpdate) {
        this.distributorsAllUpdate = distributorsAllUpdate;
    }

    public LinkedList<Consumer> getConsumersAllUpdate() {
        return consumersAllUpdate;
    }

    public void setConsumersAllUpdate(final LinkedList<Consumer> consumersAllUpdate) {
        this.consumersAllUpdate = consumersAllUpdate;
    }

    public LinkedList<Producer> getProducersAllUpdate() {
        return producersAllUpdate;
    }

    public void setProducersAllUpdate(LinkedList<Producer> producersAllUpdate) {
        this.producersAllUpdate = producersAllUpdate;
    }

    public LinkedHashMap<Integer, Integer> getDistributorsByPrice() {
        return distributorsByPrice;
    }

    public void setDistributorsByPrice(LinkedHashMap<Integer, Integer> distributorsByPrice) {
        this.distributorsByPrice = distributorsByPrice;
    }

}
