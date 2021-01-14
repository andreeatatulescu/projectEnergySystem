package input;

import common.Constants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class InputLoader {
    private final String inputPath;

    public InputLoader(final String inputPath) {
        this.inputPath = inputPath;
    }

    public String getInputPath() {
        return inputPath;
    }

    /**
     * reads data from file
     * @return input
     */
    public Input readData() {
        JSONParser jsonParser = new JSONParser();
        long numberofTurns = 0;
        List<ConsumerInputData> consumers = new ArrayList<>();
        List<DistributorInputData> distributors = new ArrayList<>();
        List<ProducerInputData> producers = new ArrayList<>();
        List<MonthlyUpdateInputData> monthlyUpdates = new ArrayList<>();

        try {
            JSONObject jsonObject = (JSONObject) jsonParser
                    .parse(new FileReader(inputPath));
            numberofTurns = (long) jsonObject.get(Constants.TURNS);
            JSONObject initialData = (JSONObject) jsonObject
                    .get(Constants.INITIALDATA);

            consumers = readConsumers(initialData);
            distributors = readDistributors(initialData);
            producers = readProducers(initialData);

            monthlyUpdates = readMonthlyUpdates(jsonObject);

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return new Input(numberofTurns, consumers,
                distributors, producers, monthlyUpdates);
    }

    /**
     * read Consumers from input file
     * @param initialData
     * @return consumers list
     */
    public List<ConsumerInputData> readConsumers(final JSONObject initialData) {
        List<ConsumerInputData> consumers = new ArrayList<>();
        JSONArray jsonConsumers = (JSONArray)
                initialData.get(Constants.CONSUMERS);

        if (jsonConsumers != null) {
            for (Object jsonConsumer : jsonConsumers) {
                consumers.add(new ConsumerInputData(
                        Integer.parseInt(((JSONObject) jsonConsumer)
                                .get(Constants.ID).toString()),
                        Integer.parseInt(((JSONObject) jsonConsumer)
                                .get(Constants.INIT_BUDGET)
                                .toString()),
                        Integer.parseInt(((JSONObject) jsonConsumer)
                                .get(Constants.INCOME).toString())
                ));
            }
        } else {
            System.out.println("CONSUMERS DO NO EXIST");
            consumers = null;
        }
        return consumers;
    }

    /**
     * read Distributors from input file
     * @param initialData
     * @return distributors list
     */
    public List<DistributorInputData> readDistributors(final JSONObject initialData) {
        List<DistributorInputData> distributors = new ArrayList<>();
        JSONArray jsonDistributors = (JSONArray)
                initialData.get(Constants.DISTRIBUTORS);

        if (jsonDistributors != null) {
            for (Object jsonDistributor : jsonDistributors) {
                distributors.add(new DistributorInputData(
                        Integer.parseInt(((JSONObject) jsonDistributor)
                                .get(Constants.ID).toString()),
                        Integer.parseInt(((JSONObject) jsonDistributor)
                                .get(Constants.CONTRACT_LENGTH)
                                .toString()),
                        Integer.parseInt(((JSONObject) jsonDistributor)
                                .get(Constants.INIT_BUDGET)
                                .toString()),
                        Integer.parseInt(((JSONObject) jsonDistributor)
                                .get(Constants.INIT_INFRASTRUCTURE)
                                .toString()),
                        Integer.parseInt(((JSONObject) jsonDistributor)
                                .get(Constants.ENERGY_NEEDED).toString()),
                        (String) ((JSONObject) jsonDistributor)
                                .get(Constants.STRATEGY)
                ));
            }
        } else {
            System.out.println("DISTRIBUTORS DO NOT EXIST");
            distributors = null;
        }
        return distributors;
    }

    /**
     * read Producers from input file
     * @param initialData
     * @return producers list
     */
    public List<ProducerInputData> readProducers(final JSONObject initialData) {
        List<ProducerInputData> producers = new ArrayList<>();
        JSONArray jsonProducers = (JSONArray)
                initialData.get(Constants.PRODUCERS);

        if (jsonProducers != null) {
            for (Object jsonProducer : jsonProducers) {
                producers.add(new ProducerInputData(
                        Integer.parseInt(((JSONObject) jsonProducer)
                                .get(Constants.ID).toString()),
                        (String) ((JSONObject) jsonProducer)
                                .get(Constants.ENERGY_TYPE),
                        Integer.parseInt(((JSONObject) jsonProducer)
                                .get(Constants.MAX_DISTRIB).toString()),
                        (Double) ((JSONObject) jsonProducer)
                                .get(Constants.PRICE_KW),
                        Integer.parseInt(((JSONObject) jsonProducer)
                                .get(Constants.ENERGY_PER_DISTRIB).toString())
                ));
            }
        } else {
            System.out.println("PRODUCERS DO NOT EXIST");
            producers = null;
        }

        return producers;
    }

    /**
     * reads MonthlyUpdates from input file
     * @param jsonObject
     * @return monthlyUpdates list
     */
    public List<MonthlyUpdateInputData> readMonthlyUpdates(final JSONObject jsonObject) {
        JSONArray jsonMonthlyUpdates = (JSONArray)
                jsonObject.get(Constants.UPDATES);

        List<MonthlyUpdateInputData> monthlyUpdates = new ArrayList<>();

        if (jsonMonthlyUpdates != null) {
            for (Object jsonIterator : jsonMonthlyUpdates) {

                ArrayList<ConsumerInputData> newConsumers = new ArrayList<>();

                if (((JSONObject) jsonIterator).
                        get(Constants.NEW_CONSUMERS) != null) {
                    for (Object iterator : (JSONArray)
                            ((JSONObject) jsonIterator).get(Constants
                                    .NEW_CONSUMERS)) {
                        newConsumers.add(new ConsumerInputData(
                                Integer.parseInt(((JSONObject) iterator)
                                        .get(Constants.ID).toString()),
                                Integer.parseInt(((JSONObject) iterator)
                                        .get(Constants.INIT_BUDGET)
                                        .toString()),
                                Integer.parseInt(((JSONObject) iterator).
                                        get(Constants.INCOME)
                                        .toString())
                        ));
                    }
                } else {
                    newConsumers = null;
                }
                ArrayList<DistributorChanges> distributorChanges =
                        new ArrayList<>();

                if (((JSONObject) jsonIterator).
                        get(Constants.DISTRIBUTOR_CHANGES) != null) {
                    for (Object iterator : (JSONArray)
                            ((JSONObject) jsonIterator)
                                    .get(Constants.DISTRIBUTOR_CHANGES)) {
                        distributorChanges.add(new DistributorChanges(
                                Integer.parseInt(((JSONObject) iterator)
                                        .get(Constants.ID)
                                        .toString()),
                                Integer.parseInt(((JSONObject) iterator)
                                        .get(Constants.INFRASTRUCTURE)
                                        .toString())
                        ));
                    }
                } else {
                    distributorChanges = null;
                }

                ArrayList<ProducerChanges> producersChanges =
                        new ArrayList<>();

                if (((JSONObject) jsonIterator).
                        get(Constants.PRODUCER_CHANGES) != null) {
                    for (Object iterator : (JSONArray)
                            ((JSONObject) jsonIterator)
                                    .get(Constants.PRODUCER_CHANGES)) {
                        producersChanges.add(new ProducerChanges(
                                Integer.parseInt(((JSONObject) iterator)
                                        .get(Constants.ID)
                                        .toString()),
                                Integer.parseInt(((JSONObject) iterator)
                                        .get(Constants.ENERGY_PER_DISTRIB)
                                        .toString())
                        ));
                    }
                } else {
                    producersChanges = null;
                }
                monthlyUpdates.add(new MonthlyUpdateInputData(newConsumers,
                        distributorChanges, producersChanges));
            }
        } else {
            System.out.println("MONTHLY UPDATES DO NOT EXIST");
            monthlyUpdates = null;
        }

        return monthlyUpdates;
    }
}
