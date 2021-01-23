package factories;

import input.ConsumerInputData;
import input.DistributorInputData;
import input.ProducerInputData;
import interfaces.IPerson;
import persons.Consumer;
import persons.Distributor;
import persons.Producer;

public class IPersonFactory {
    private static IPersonFactory instance = null;

    private IPersonFactory() {

    }

    public static IPersonFactory getInstance() {
        if (instance == null) {
            instance = new IPersonFactory();
        }
        return instance;
    }
    /**
     * getPERSON
     * @param personType
     * @param obj
     * @return
     */
    public IPerson getPerson(final String personType, final Object obj) {
        if (personType == null) {
            return null;
        }
        if (personType.equalsIgnoreCase("CONSUMER")) {
            return new Consumer(((ConsumerInputData) obj).getId(),
                    ((ConsumerInputData) obj).getInitialBudget(),
                    ((ConsumerInputData) obj).getMonthlyIncome());
        } else if (personType.equalsIgnoreCase("DISTRIBUTOR")) {
            return new Distributor(((DistributorInputData) obj).getId(),
                    ((DistributorInputData) obj).getContractLength(),
                    ((DistributorInputData) obj).getInitialBudget(),
                    ((DistributorInputData) obj).getInitialInfrastructureCost(),
                    ((DistributorInputData) obj).getEnergyNeeded(),
                    ((DistributorInputData) obj).getProducerStrategy());
        } else if (personType.equalsIgnoreCase("PRODUCER")) {
            return new Producer(((ProducerInputData) obj).getId(),
                    ((ProducerInputData) obj).getEnergyType(),
                    ((ProducerInputData) obj).getMaxDistributors(),
                    ((ProducerInputData) obj).getPrice(),
                    ((ProducerInputData) obj).getEnergyPerDistributor());
        }
        return null;
    }
}
