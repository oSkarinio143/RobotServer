package modules.robot;

import lombok.Getter;
import lombok.Setter;
import modules.Level;
import modules.Rarity;
import modules.StatsInvestor;
import modules.interfaces.RobotInvestor;
import service.Generator;
import service.operate.InvestorMenager;

import java.io.Serializable;
import java.util.*;

@Setter
@Getter
public class Investor extends AbstractRobot implements RobotInvestor, Comparable<Investor>, Serializable {
    private static final long serialVersionUID = 1L;
    private int invId;
    private Map<StatsInvestor, Integer> statistics = new LinkedHashMap<>();

    @Setter
    @Getter
    private static int quantityInv = 0;

    @Getter
    private static double buyCostMultiplier = 1.1;

    public Investor(Map<Integer, Integer> stats, Rarity rarity, Level level) {
        super(rarity, level);
        invId = quantityInv;
        setStatistics(stats);
    }

    @Override
    public double invest(int goldAmount) {
        double earnedGold = (goldAmount * (investitionRate * Generator.sumStats(this.getStatistics(), 1, 2, 3)));
        if(InvestorMenager.checkIfAboveNumber(getStatistics(), 8))
            specialFunction();
        return earnedGold;
    }

    public void setStatistics(int... stats){
        int i=0;
        for (int stat : stats) {
            if(i<4)
                statistics.put(StatsInvestor.getById(i), stat);
            i++;
        }
    }

    public void setStatistics(Map<Integer, Integer> stats) {
        for (Map.Entry statNumber : stats.entrySet()) {
            StatsInvestor key = StatsInvestor.getById((Integer) statNumber.getKey());
            Integer value = (Integer) statNumber.getValue();
            statistics.put(key, value);
        }
    }

    public Map<Integer, Integer> getStatistics() {
        Iterator<Map.Entry<StatsInvestor, Integer>> iterator = statistics.entrySet().iterator();
        Map<Integer, Integer> statsMap = new LinkedHashMap<>();

        while (iterator.hasNext()) {
            Map.Entry<StatsInvestor, Integer> entry = iterator.next();
            statsMap.put(entry.getKey().getId(), entry.getValue());
        }
        return statsMap;
    }

    public int compareTo(Investor other) {
        return Integer.compare(this.invId, other.invId);
    }
}
