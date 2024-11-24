package modules;

import exceptions.IncorrectIdRuntimeException;
import lombok.Getter;
import lombok.Setter;

@Getter
public enum Level {
    BEGINNER(1, 0, 7), INTERMEDIATE(2, 4, 9), ADVANCED(3, 5, 10);
    private int id;
    private int additionalStats;
    private int constraint;

    Level(int id, int additionalStats, int constraint){
        this.id=id;
        this.additionalStats=additionalStats;
        this.constraint=constraint;
    }

    public static Level getById(int idNumber){
        for (Level value : Level.values()) {
            if (value.id==idNumber) return value;
        }
        throw new IncorrectIdRuntimeException();
    }

    public static int getAdditionalStatsForLevel(int levelNumber){
        int sumAdditionalStats=0;
        for (int i = 1; i <= levelNumber; i++) {
            sumAdditionalStats+=getById(i).additionalStats;
        }
        return sumAdditionalStats;
    }
}
