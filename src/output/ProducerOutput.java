package output;

import documents.MonthlyStat;

import java.util.List;

public final class ProducerOutput {
    private final int id;
    private final int maxDistributors;
    private final double priceKW;
    private final String energyType;
    private final int energyPerDistributor;
    private final List<MonthlyStat> monthlyStats;

    public ProducerOutput(final int id, int maxDistributors,
                          final double priceKW, final String energyType,
                          final int energyPerDistributor,
                          final List<MonthlyStat> monthlyStats) {
        this.id = id;
        this.maxDistributors = maxDistributors;
        this.priceKW = priceKW;
        this.energyType = energyType;
        this.energyPerDistributor = energyPerDistributor;
        this.monthlyStats = monthlyStats;
    }

    public int getId() {
        return id;
    }

    public int getMaxDistributors() {
        return maxDistributors;
    }

    public double getPriceKW() {
        return priceKW;
    }

    public String getEnergyType() {
        return energyType;
    }

    public int getEnergyPerDistributor() {
        return energyPerDistributor;
    }

    public List<MonthlyStat> getMonthlyStats() {
        return monthlyStats;
    }
}
