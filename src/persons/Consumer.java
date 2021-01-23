package persons;

import documents.Contract;
import interfaces.IPerson;

import java.util.ArrayList;
import java.util.List;

public class Consumer implements IPerson {
    private final int id;
    private boolean isBankrupt;
    private int initialBudget;
    private final int monthlyIncome;
    private boolean alert;
    private boolean contract;
    private int rest;
    private final List<Contract> contractInfo = new ArrayList<>();

    public Consumer(final int id,
                    final int initialBudget,
                    final int monthlyIncome) {
        this.id = id;
        this.isBankrupt = false;
        this.initialBudget = initialBudget;
        this.monthlyIncome = monthlyIncome;
        this.alert = false;
        this.contract = false;
    }

    public int getId() {
        return id;
    }

    public boolean isBankrupt() {
        return isBankrupt;
    }

    public void setBankrupt(boolean bankrupt) {
        isBankrupt = bankrupt;
    }

    public int getInitialBudget() {
        return initialBudget;
    }

    public void setInitialBudget(int initialBudget) {
        this.initialBudget = initialBudget;
    }

    public int getMonthlyIncome() {
        return monthlyIncome;
    }

    public boolean isAlert() {
        return alert;
    }

    public void setAlert(boolean alert) {
        this.alert = alert;
    }

    public boolean isContract() {
        return contract;
    }

    public void setContract(boolean contract) {
        this.contract = contract;
    }

    public int getRest() {
        return rest;
    }

    public void setRest(int rest) {
        this.rest = rest;
    }

    public List<Contract> getContractInfo() {
        return contractInfo;
    }

    @Override
    public String toString() {
        return "Consumer{"
                + "id=" + id
                + ", isBankrupt=" + isBankrupt
                + ", initialBudget=" + initialBudget
                + ", monthlyIncome=" + monthlyIncome
                + '}';
    }

    @Override
    public void monthlyPay(int costs, Contract contractaux) {
        if (this.initialBudget >= costs) {
            this.initialBudget -= costs;
        } else if (!alert) {
            this.alert = true;
            this.rest = contractaux.getPrice();
        } else {
            this.isBankrupt = true;
            this.alert = false;
        }
    }

    @Override
    public void monthlyReceive(int sum) {
        this.initialBudget += sum;
    }
}
