package modules.robot;

import lombok.Getter;
import lombok.Setter;
import modules.Level;
import modules.Rarity;
import modules.interfaces.Robot;
import modules.StatsSeller;
import service.Generator;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public abstract class AbstractRobot implements Robot{
    private int robotId;
    private Level level;
    private Rarity rarity;
    private static int quantity=0;
    private Map<Integer, Integer> statistics;


    public AbstractRobot(Rarity rarity, Level level){
        robotId=quantity;
        quantity++;
        this.level=level;
        this.rarity=rarity;
    }
}
