package persons;

import documents.Contract;
import documents.MonthlyStat;
import entities.EnergyType;
import interfaces.IPerson;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public final class Producer extends Observable implements IPerson {
    private final int id;
    private final String energyType;
    private boolean isRenewable;
    private final int maxDistributors;
    private int nrDistributors;
    private final double priceKW;
    private int energyPerDistributor;
    private ArrayList<Integer> currentDistributorsIds = new ArrayList<>();
    private List<MonthlyStat> monthlyStats = new ArrayList<>();

    public Producer(final int id,
                    final String energyType,
                    final int maxDistributors,
                    final double priceKW,
                    final int energyPerDistributor) {
        this.id = id;
        this.maxDistributors = maxDistributors;
        this.energyType = energyType;
        this.isRenewable = renew();
        this.priceKW = priceKW;
        this.energyPerDistributor = energyPerDistributor;
        this.nrDistributors = 0;
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

    public void setMonthlyStats(List<MonthlyStat> monthlyStats) {
        this.monthlyStats = monthlyStats;
    }

    public boolean isRenewable() {
        return isRenewable;
    }

    public void setRenewable(boolean renewable) {
        isRenewable = renewable;
    }

    public int getNrDistributors() {
        return nrDistributors;
    }

    public void setNrDistributors(int nrDistributors) {
        this.nrDistributors = nrDistributors;
    }

    public void setEnergyPerDistributor(int energyPerDistributor) {
        this.energyPerDistributor = energyPerDistributor;
    }

    public ArrayList<Integer> getCurrentDistributorsIds() {
        return currentDistributorsIds;
    }

    public void setCurrentDistributorsIds(ArrayList<Integer> currentDistributorsIds) {
        this.currentDistributorsIds = currentDistributorsIds;
    }

    /**
     * check what type of energy (renewable or not) for GREEN Strategy
     * @return boolean isRenewable
     */
    public boolean renew() {
        if (this.energyType.equals(EnergyType.COAL.getLabel())) {
            isRenewable = EnergyType.COAL.isRenewable();
        } else if (this.energyType.equals(EnergyType.HYDRO.getLabel())) {
            isRenewable = EnergyType.HYDRO.isRenewable();
        } else if (this.energyType.equals(EnergyType.NUCLEAR.getLabel())) {
            isRenewable = EnergyType.NUCLEAR.isRenewable();
        } else if (this.energyType.equals(EnergyType.SOLAR.getLabel())) {
            isRenewable = EnergyType.SOLAR.isRenewable();
        } else if (this.energyType.equals(EnergyType.WIND.getLabel())) {
            isRenewable = EnergyType.WIND.isRenewable();
        }
        return isRenewable;
    }

    @Override
    public String toString() {
        return "Producer{" + "id=" + id
                + ", energyType='" + energyType
                + ", isRenewable='" + isRenewable
                + ", maxDistributors=" + maxDistributors
                + ", priceKW=" + priceKW
                + ", energyPerDistributor=" + energyPerDistributor
                + ", currentDistributorsIds=" + currentDistributorsIds
                + ", monthlyStats=" + monthlyStats + '}' + "\n";
    }


    @Override
    public void monthlyPay(int costs, Contract contract) {

    }

    @Override
    public void monthlyReceive(int sum) {

    }

    public void changeEnergy (int newEnergy) {
        this.setEnergyPerDistributor(newEnergy);
        this.setChanged();
        this.notifyObservers(this.id);
        this.deleteObservers();
    }
}
