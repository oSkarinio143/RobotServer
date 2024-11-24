package modules;

import exceptions.IncorrectIdRuntimeException;
import lombok.Getter;

@Getter
public enum Rarity {
    COMMON(0, 0), RARE(1, 2), EPIC(2, 5);
    private int id;
    private int additionalStats;

    Rarity(int id, int additionalStats){
        this.id=id;
        this.additionalStats=additionalStats;
    }

    public Rarity getById(int id){
        for(Rarity rarity : Rarity.values()){
            if(rarity.id==id){
                return rarity;
            }
        }
        throw new IncorrectIdRuntimeException("Incorrect Id");
    }
}
