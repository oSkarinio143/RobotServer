package modules.robot;

import lombok.Getter;
import lombok.Setter;
import modules.Level;
import modules.Rarity;
import modules.StatsSeller;
import modules.interfaces.Robot;
import modules.interfaces.RobotSeller;
import service.Generator;
import service.operate.InvestorMenager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Setter
@Getter
public abstract class AbstractSeller extends AbstractRobot implements RobotSeller{
    private static final long serialVersionUID = 1L;
    private int sellerId;
    private HashMap<StatsSeller, Integer> statistics = new HashMap<>();

    @Setter
    @Getter
    private static int quantitySel = 0;

    public AbstractSeller(Map<Integer, Integer> stats, Rarity rarity, Level level){
        super(rarity, level);
        sellerId = quantitySel;
        setStatistics(stats);
    }

    public double earnMoney(){
        double earnings = RobotSeller.EARNINGS* Generator.sumStats(getStatistics(), 1, 2, 3);
        if(InvestorMenager.checkIfAboveNumber(getStatistics(), 8))
            specialFunction();
        return earnings;
    }

    public double revolt(){
        double revoltChance = super.revolt();
        if(statistics.get(StatsSeller.MIND)<7){
            return revoltChance;
        }
        else{
            int mind = statistics.get(StatsSeller.MIND);
            double autodestructionChance = revoltChance*0.0001*Generator.sinew(mind, 4);
            return autodestructionChance;
        }
    }

    public void setStatistics(int... statNumber){
        Map<Integer, Integer> intMap = new HashMap<>();
        int i=0;
        for (int stat : statNumber) {
            if(intMap.size()<=4){
                statistics.remove(i);
                statistics.put(StatsSeller.getById(i), stat);
                intMap.put(i, stat);
                i++;
            }
        }
    }

    public void setStatistics(Map<Integer, Integer> intMap){
        statistics.putAll((convertStatsToOrg(intMap)));
    }

    public Map<Integer, Integer> getStatistics(){
        return convertStatsToInt(statistics);
    }

    //W investorze jest inaczej, chciałem sprawdzić czy taki kod z dodatkową metodą do konwertowania będzie lepszy
    public Map<Integer, Integer> convertStatsToInt(Map<StatsSeller, Integer> statMap){
        Map<Integer, Integer> intMap = new HashMap<>();
        statMap.forEach((k, v) ->{
            intMap.put(k.getId(), v);
        });
        return intMap;
    }

    public Map<StatsSeller, Integer> convertStatsToOrg(Map<Integer, Integer> intMap){
        Map<StatsSeller, Integer> statMap = new HashMap<>();
        intMap.forEach((k, v) ->{
           statMap.put(StatsSeller.getById(k), v);
        });
        return statMap;
    }
}
