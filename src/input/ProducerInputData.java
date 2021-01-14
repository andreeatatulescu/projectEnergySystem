package input;

public final class ProducerInputData {

    private final int id;
    private final String energyType;
    private final int maxDistributors;
    private final double price;
    private final int energyPerDistributor;

    public ProducerInputData(final int id, String energyType,
                             final int maxDistributors, final double price,
                             final int energyPerDistributor) {
        this.id = id;
        this.energyType = energyType;
        this.maxDistributors = maxDistributors;
        this.price = price;
        this.energyPerDistributor = energyPerDistributor;
    }

    public int getId() {
        return id;
    }

    public String getEnergyType() {
        return energyType;
    }

    public int getMaxDistributors() {
        return maxDistributors;
    }

    public double getPrice() {
        return price;
    }

    public int getEnergyPerDistributor() {
        return energyPerDistributor;
    }

    @Override
    public String toString() {
        return "ProducerInputData{"
                + "id=" + id
                + ", energyType='" + energyType + '\''
                + ", maxDistributors=" + maxDistributors
                + ", price=" + price
                + ", energyPerDistributor=" + energyPerDistributor
                + '}';
    }
}
