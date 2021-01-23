package output;

public final class ConsumerOutput {
    private final int id;
    private final boolean isBankrupt;
    private final int budget;

    public ConsumerOutput(final int id, final boolean isBankrupt, final int budget) {
        this.id = id;
        this.isBankrupt = isBankrupt;
        this.budget = budget;
    }

    public int getId() {
        return id;
    }

    /**
     * getter for isBankrupt in order to write in results.out according to ref files
     * @return boolean isBankrupt
     */
    public boolean getisBankrupt() {
        return isBankrupt;
    }

    public int getBudget() {
        return budget;
    }
}
