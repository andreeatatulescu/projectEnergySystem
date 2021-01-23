package gameflow;

import documents.Contract;
import factories.IPersonFactory;
import factories.StrategyFactory;
import input.ConsumerInputData;
import input.DistributorInputData;
import input.Input;
import input.ProducerInputData;
import interfaces.IPerson;
import interfaces.ProducerStrategy;
import persons.Consumer;
import persons.Distributor;
import persons.Producer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class InitialRound {
    /**
     * calculates init prices for contracts
     * @param distributor
     * @return init prices
     */
    public int initPrices(final Distributor distributor) {
        return  (int) (distributor.getInitialInfrastructureCost()
                + distributor.getProductionCost()
                + 0.2 * distributor.getProductionCost());
    }

    private Contract contractaux = new Contract(0, 0, 0);
    private ArrayList<Distributor> distributorsAll = new ArrayList<>();
    private ArrayList<Consumer> consumersAll = new ArrayList<>();
    private ArrayList<Producer> producersAll = new ArrayList<>();

    public ArrayList<Distributor> getDistributorsAll() {
        return distributorsAll;
    }
    public ArrayList<Consumer> getConsumersAll() {
        return consumersAll;
    }
    public ArrayList<Producer> getProducersAll() {
        return producersAll;
    }

    /**
     * play Round0
     * @param input
     */
    public void doInitialRound(final Input input) {

        LinkedHashMap<Integer, Integer> distributorsByPrice = new LinkedHashMap<>();
        LinkedHashMap<Integer, Integer> sorted = new LinkedHashMap<>();

        IPersonFactory personFactory = IPersonFactory.getInstance();
        // forming the lists of consumers and distributors
        for (ConsumerInputData consumer : input.getConsumersData()) {
            IPerson newConsumator = personFactory.getPerson("CONSUMER", consumer);
            consumersAll.add((Consumer) newConsumator);
        }

        for (DistributorInputData distributor : input.getDistributorsData()) {
            IPerson newDistributor = personFactory.getPerson("DISTRIBUTOR", distributor);
            distributorsAll.add((Distributor) newDistributor);
        }

        for (ProducerInputData producer : input.getProducersData()) {
            IPerson newProducer = personFactory.getPerson("PRODUCER", producer);
            producersAll.add((Producer) newProducer);
        }

        for (Distributor distributor : distributorsAll) {
            ProducerStrategy strategy = StrategyFactory.createStrategy(distributor.getProducerStrategy(), producersAll);
            strategy.sortByStrategy();

            int energyCnt = 0;

            for (Producer producer : producersAll) {
                while (energyCnt < distributor.getEnergyNeededKW() && producer.getNrDistributors() < producer.getMaxDistributors()) {
                    distributor.getProducersList().add(producer);
                    producer.getCurrentDistributorsIds().add(distributor.getId());
                    producer.setNrDistributors(producer.getNrDistributors() +1);
                    energyCnt += producer.getEnergyPerDistributor();
                    break;
                }
            }
        }



        for (Distributor distributor : distributorsAll) {
            distributor.setProductionCost(distributor.calculateProductionCost());
        }

        for (Distributor distributor : distributorsAll) {
            int price = distributor.getContractPrice();
            distributorsByPrice.put(distributor.getId(), price);
        }
        // sorted the distributors by the contract's price
        distributorsByPrice.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));
        // every consumer chooses his contract
        for (Consumer consumer : consumersAll) {
            for (Integer key : sorted.keySet()) {
                for (Distributor distributor : distributorsAll) {
                    if (distributor.getId() == key) {
                        distributor.getContracts().add(new Contract(
                                consumer.getId(),
                                initPrices(distributor),
                                distributor.getContractLength()));
                        consumer.setContract(true);
                        consumer.getContractInfo().add(new Contract(
                                consumer.getId(),
                                initPrices(distributor),
                                distributor.getContractLength()));
                    }
                }
                break;
            }
            // consumers receive salaries
            consumer.monthlyReceive(consumer.getMonthlyIncome());
        }
        // consumers pay Rata
        for (Distributor distributor : distributorsAll) {
            for (Contract contract : distributor.getContracts()) {
                for (Consumer consumer: consumersAll) {
                    if (contract.getConsumerId() == consumer.getId()) {
                        consumer.monthlyPay(initPrices(distributor),
                                consumer.getContractInfo().get(0));
                    }
                }
            }
            // distributors receive money from their clients
            for (Contract contract : distributor.getContracts()) {
                for (Consumer consumer: consumersAll) {
                    if (contract.getConsumerId() == consumer.getId()
                            && !consumer.isAlert()) {
                        distributor.monthlyReceive(contract.getPrice());
                    }
                }
            }

            // distributors pay the costs
            distributor.monthlyPay(distributor.getCosts(), contractaux);
        }
        // 1 month has passed
        for (Distributor distributor : distributorsAll) {
            for (Contract contract : distributor.getContracts()) {
                contract.setRemainedContractMonths(contract.getRemainedContractMonths() - 1);
            }
        }
        // 1 month has passed
        for (Consumer consumer : consumersAll) {
            consumer.getContractInfo().get(0).setRemainedContractMonths(
                    consumer.getContractInfo().get(0).getRemainedContractMonths() - 1);
        }
    }
}
