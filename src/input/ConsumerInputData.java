package input;

public final class ConsumerInputData {
    private final int id;
    private final int initialBudget;
    private final int monthlyIncome;

    public ConsumerInputData(final int id, final int initialBudget, final int monthlyIncome) {
        this.id = id;
        this.initialBudget = initialBudget;
        this.monthlyIncome = monthlyIncome;
    }

    public int getId() {
        return id;
    }

    public int getInitialBudget() {
        return initialBudget;
    }

    public int getMonthlyIncome() {
        return monthlyIncome;
    }

    @Override
    public String toString() {
        return "ConsumerInputData{"
                + "id=" + id
                + ", initialBudget=" + initialBudget
                + ", monthlyIncome=" + monthlyIncome
                + '}';
    }
}
