package modules.robot;

import modules.Level;
import modules.Rarity;

import java.util.Map;

public class SellerGames extends AbstractSeller{
    public SellerGames(Map<Integer, Integer> map, Rarity rarity, Level level){
        super(map, rarity, level);
    }
}
