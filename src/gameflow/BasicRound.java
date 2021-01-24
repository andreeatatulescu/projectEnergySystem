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

import java.util.*;

public final class BasicRound {
    static final double PROFIT = 0.2;
    static final double REST = 1.2;
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

        for (Producer producer : producersAllUpdate) {
            ArrayList<Integer> currentDistrib = new ArrayList<>();
            currentDistrib.addAll(producer.getCurrentDistributorsIds());
            MonthlyStat newMonth = new MonthlyStat(pos + 1);
            producer.getMonthlyStats().add(newMonth);
            producer.getMonthlyStats().get(pos)
                    .setDistributorsIds(currentDistrib);

        }

        // reading monthlyUpdates
        readUpdate(input, pos);

        // recalculating contracts price
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

//        for (Producer producer : producersAllUpdate) {
//            Collections.sort(producers.);
//        }



        System.out.println(consumersAllUpdate);
        System.out.println(distributorsAllUpdate);
        System.out.println(producersAllUpdate);


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
            System.out.println(input.getMonthlyUpdatesData().get(pos).getProducerChanges());
            for (ProducerChanges producerChange : input.getMonthlyUpdatesData()
                    .get(pos).getProducerChanges()) {


                for (Producer producerAux : producersAllUpdate) {
                    System.out.println(producersAllUpdate);
                    System.out.println("o data");
                    System.out.println(producerAux);
                    if (producerChange.getId() == producerAux.getId()) {
                        System.out.println(producerAux);
                        producerAux.changeEnergy(producerChange.getEnergyPerDistributor());
                        System.out.println("ia vezi" + producerChange);
                        producerAux.getMonthlyStats().get(pos).setDistributorsIds(new ArrayList<>());


                     //   if (!input.getMonthlyUpdatesData().get(pos).getProducerChanges().isEmpty()) {
                            for (Distributor distributor : distributorsAllUpdate) {
                                if (distributor.getProducersList().isEmpty()) {
                                    for (Producer producer : producersAllUpdate) {
                                        if ( producer.getCurrentDistributorsIds().contains(distributor.getId()) ) {
                                            Collections.sort(producer.getCurrentDistributorsIds());
                                            producer.getCurrentDistributorsIds().remove(Integer.valueOf(distributor.getId()));
                                            ArrayList<Integer> currentDistrib = new ArrayList<>();
                                            currentDistrib.addAll(producer.getCurrentDistributorsIds());
                                            producer.getMonthlyStats().get(pos)
                                                    .setDistributorsIds(currentDistrib);
                                        }
                                    }
                                }
                            }
                     //   }

                     //   if (!input.getMonthlyUpdatesData().get(pos).getProducerChanges().isEmpty()) {

                            for (Distributor distributor : distributorsAllUpdate) {
                                if (distributor.getProducersList().isEmpty()) {
                                    System.out.println(distributor);
                                    System.out.println("ia");
                                    ProducerStrategy strategy = StrategyFactory
                                            .createStrategy(distributor.getProducerStrategy(), producersAllUpdate);
                                    strategy.sortByStrategy();

                                    int energyCnt = 0;

                                    for (Producer producer : producersAllUpdate) {
                                        if (producer.getCurrentDistributorsIds().contains(distributor.getId())) {
                                            System.out.println("are?");
                                            ArrayList<Integer> currentDistrib = new ArrayList<>();
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
                                            System.out.println(currentDistrib);
                                            producer.getMonthlyStats().get(pos)
                                                    .setDistributorsIds(currentDistrib);
                                            producer.addObserver(distributor);
                                            producer.setNrDistributors(producer.getNrDistributors() + 1);
                                            energyCnt += producer.getEnergyPerDistributor();
                                            break;
                                        }
                                    }
                                    System.out.println(distributor);
                                }
                            }

                            for (Distributor distributor : distributorsAllUpdate) {
                                distributor.setProductionCost(distributor.calculateProductionCost());
                            }
                            break;
                    }
                    System.out.println("done=========================================================");
                }
            }
        }

    }
}
