package modules;

import exceptions.IncorrectIdRuntimeException;
import lombok.Getter;

@Getter
public enum StatsInvestor {
    MIND(0, 0, 10), PREDICTION(1, 0, 10), RISKTENDENCY(2, 0, 10), PATIENCE(3, 0, 10);
    int id;
    int minRange;
    int maxRange;

    StatsInvestor(int id, int minRange, int maxRange){
        this.id=id;
        this.minRange=minRange;
        this.maxRange=maxRange;
    }

    public static StatsInvestor getById(int id){
        for(StatsInvestor stat : StatsInvestor.values()){
            if(stat.id==id){
                return stat;
            }
        }
        throw new IncorrectIdRuntimeException("Incorrect Id");
    }
}
