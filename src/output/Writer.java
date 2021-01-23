package output;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class Writer {
    private List<ConsumerOutput> consumers;
    private List<DistributorOutput> distributors;
    private List<ProducerOutput> producers;

    public Writer(final List<ConsumerOutput> consumers,
                  final List<DistributorOutput> distributors,
                  final List<ProducerOutput> producers) {
        this.consumers = consumers;
        this.distributors = distributors;
        this.producers = producers;
    }

    @JsonGetter("consumers")
    public List<ConsumerOutput> getConsumers() {
        return consumers;
    }

    @JsonGetter("distributors")
    public List<DistributorOutput> getDistributors() {
        return distributors;
    }

    @JsonGetter("energyProducers")
    public List<ProducerOutput> getProducers() {
        return producers;
    }

    /**
     * writes in file
     * @param objectMapper
     * @param string
     * @throws IOException
     */
    public void writeFile(final ObjectMapper objectMapper, final String string) throws IOException {
        ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
        File file = new File(string);
        writer.writeValue(file, this);
    }
}
