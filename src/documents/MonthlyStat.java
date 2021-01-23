package documents;

import java.util.ArrayList;
import java.util.List;

public class MonthlyStat {
    private final int month;
    private ArrayList<Integer> distributorsIds;

    public MonthlyStat(int month) {
        this.month = month;
        this.distributorsIds = new ArrayList<>();
    }

    public int getMonth() {
        return month;
    }

    public List<Integer> getDistributorsIds() {
        return distributorsIds;
    }

    public void setDistributorsIds(ArrayList<Integer> distributorsId) {
        this.distributorsIds = distributorsId;
    }

    @Override
    public String toString() {
        return "MonthlyStat{"
                + "month=" + month
                + ", distributorsId=" + distributorsIds + '}';
    }
}
