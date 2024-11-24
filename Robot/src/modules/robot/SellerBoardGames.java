package modules.robot;

import lombok.Getter;
import lombok.Setter;
import modules.Level;
import modules.Rarity;

import java.util.Map;

public class SellerBoardGames extends SellerGames{
    private int sellerBoardGamesId;

    @Getter
    @Setter
    private static int sellerBoardGamesQuantity = 0;

    @Getter
    private static final int uniqueId = 1;

    public SellerBoardGames(Map<Integer, Integer> map, Rarity rarity, Level level){
        super(map, rarity, level);
    }

    @Override
    public double earnMoney() {
        return super.earnMoney()*BOARD_GAMES_SELLER_EARN_RATE;
    }
}
