package modules;

import exceptions.IncorrectIdRuntimeException;
import lombok.Getter;

@Getter
public enum StatsSeller {
    MIND(0, 0, 10), SPEED(1, 0, 10), NEGOTIATION(2, 0, 10), EFFICIENCY(3, 0, 10);
    private int id;
    private int minRange;
    private int maxRange;

    StatsSeller(int id, int minRange, int maxRange){
        this.id=id;
        this.minRange=minRange;
        this.maxRange=maxRange;
    }

    public static StatsSeller getById(int id){
        for(StatsSeller stat : StatsSeller.values()){
            if(stat.id==id){
                return stat;
            }
        }
        throw new IncorrectIdRuntimeException("Incorrect Id");
        //Tworzenie wyjatku bez bloku tryCatch bez podawania throwable, sama wiadomosc
    }
}
