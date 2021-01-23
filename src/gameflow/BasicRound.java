package gameflow;

import documents.Contract;
import documents.MonthlyStat;
import factories.IPersonFactory;
import input.ConsumerInputData;
import input.DistributorChanges;
import input.Input;
import input.ProducerChanges;
import interfaces.IPerson;
import persons.Consumer;
import persons.Distributor;
import persons.Producer;

import java.util.*;

public class BasicRound {
    private LinkedList<Distributor> distributorsAllUpdate = new LinkedList<>();
    private LinkedList<Consumer> consumersAllUpdate = new LinkedList<>();
    private LinkedList<Producer> producersAllUpdate = new LinkedList<>();
    private Contract contractaux = new Contract(0, 0, 0);

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

    /**
     * play basic round
     * @param consumers
     * @param distributors
     * @param input
     * @param pos
     */
    public void doBasicRound(final List<Consumer> consumers,
                             final List<Distributor> distributors,
                             final List<Producer> producers,
                             final Input input, final int pos) {

        LinkedHashMap<Integer, Integer> distributorsByPrice = new LinkedHashMap<>();
        LinkedHashMap<Integer, Integer> sorted = new LinkedHashMap<>();

        distributorsAllUpdate.addAll(distributors);
        consumersAllUpdate.addAll(consumers);
        producersAllUpdate.addAll(producers);

      //  System.out.println(pos);

        for (Producer producer : producersAllUpdate) {
            MonthlyStat newMonth = new MonthlyStat(pos + 1);
            producer.getMonthlyStats().add(newMonth);
            if (input.getMonthlyUpdatesData().get(pos).getProducerChanges().isEmpty()) {
                producer.getMonthlyStats().get(pos).setDistributorsIds(producer.getCurrentDistributorsIds());
            }
        }

        // reading monthlyUpdates
        readUpdate(input, pos);
        // recalculating contracts price
        for (Distributor distributor : distributorsAllUpdate) {
            if (!distributor.isBankrupt()) {
                int price = 0;
                if ( distributor.getContracts().size() > 0 ) {
                    price = (int) (distributor.getInitialInfrastructureCost()
                            / distributor.getContracts().size()
                            + distributor.getProductionCost()
                            + 0.2 * distributor.getProductionCost());
                } else if ( distributor.getContracts().size() == 0 ) {
                    price = (int) (distributor.getInitialInfrastructureCost()
                            + distributor.getProductionCost()
                            + 0.2 * distributor.getProductionCost());
                }
                distributorsByPrice.put(distributor.getId(), price);
            }
        }
        // sorted the distributors by the contract's price
        distributorsByPrice.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));
        // removing the contract when it expires
        // from distributors database
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
        // from consumers
        for (Consumer consumer : getConsumersAllUpdate()) {
            if (!consumer.getContractInfo().isEmpty()) {
                if (consumer.getContractInfo().get(0).getRemainedContractMonths() == 0) {
                    consumer.getContractInfo().remove(consumer.getContractInfo().get(0));
                }
            }
        }
        // consumers receive salaries
        for (Consumer consumer : consumersAllUpdate) {
            if (!consumer.isBankrupt()) {
                consumer.monthlyReceive(consumer.getMonthlyIncome());
            }
        }
        Map.Entry<Integer, Integer> entry = sorted.entrySet().iterator().next();
        Integer key = entry.getKey();
        Integer value = entry.getValue();
        // choosing the most advantageous contract for each consumer
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
        // distributors pay the costs
        for (Distributor distributor : distributorsAllUpdate) {
            if (!distributor.isBankrupt()) {
                distributor.monthlyPay(distributor.getCosts(), contractaux);
            }
        }
        // consumers pay Rata and restants bills
        for (Consumer consumer : consumersAllUpdate) {
            if (!consumer.isBankrupt()) {
                if (consumer.isAlert()) {
                    if (!consumer.getContractInfo().isEmpty()) {
                        // pay the restant bill if the consumer can afford it
                        if (consumer.getInitialBudget()
                                >= (int) (consumer.getContractInfo().get(0)
                                .getPrice() + 1.2 * consumer.getRest())) {
                            consumer.setInitialBudget(consumer
                                    .getInitialBudget() - (int) (consumer
                                    .getContractInfo().get(0).getPrice()
                                    + 1.2 * consumer.getRest()));
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
        // removing a consumer if it is Bankrupt
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
        // receiving money from clients
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
        // clear its contract list if a distributor is Bankrupt
        for (Distributor distributor : distributorsAllUpdate) {
            if (distributor.getInitialBudget() < 0) {
                distributor.setBankrupt(true);
                distributor.getContracts().clear();
            }
        }

        // updates for Producers
        readUpdateProducer(input, pos);


        // 1 month has passed
        for (Distributor distributor : distributorsAllUpdate) {
            if (!distributor.getContracts().isEmpty()) {
                for (Contract contract : distributor.getContracts()) {
                    contract.setRemainedContractMonths(contract
                            .getRemainedContractMonths() - 1);
                }
            }
        }
        // 1 month has passed
        for (Consumer consumer : consumersAllUpdate) {
            if (!consumer.getContractInfo().isEmpty()) {
                consumer.getContractInfo().get(0).setRemainedContractMonths(
                        consumer.getContractInfo().get(0).getRemainedContractMonths() - 1);
            }
        }
    }

    /**
     * read monthlyUpdates one by one
     * @param input
     * @param pos
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
                        distributor.setInitialInfrastructureCost(distributorChange.getInfrastructureCost());
                    }
                }
            }
        }

    }

    public void readUpdateProducer(final Input input, final int pos) {

        if (!input.getMonthlyUpdatesData().get(pos).getProducerChanges().isEmpty()) {
            for (ProducerChanges producerChange : input.getMonthlyUpdatesData()
                    .get(pos).getProducerChanges()) {
                for (Producer producer : producersAllUpdate) {
                    if (producerChange.getId() == producer.getId()) {
                        producer.setEnergyPerDistributor(producerChange.getEnergyPerDistributor());
                    }
                }
            }
        }

    }
}
