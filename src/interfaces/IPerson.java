package interfaces;

import documents.Contract;

public interface IPerson {
    /**
     * onorates payments
     * @param costs
     * @param contract
     */
    void monthlyPay(int costs, Contract contract);

    /**
     * gives money
     * @param sum
     */
    void monthlyReceive(int sum);
}
