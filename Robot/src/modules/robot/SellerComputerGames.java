package modules.robot;

import lombok.Getter;
import lombok.Setter;
import modules.Level;
import modules.Rarity;

import java.io.Serializable;
import java.util.Map;

public class SellerComputerGames extends SellerGames implements Serializable {
    private static final long serialVersionUID = 1L;
    private int sellerComputerGamesId;

    @Getter
    @Setter
    private static int sellerComputerGamesQuantity = 0;

    @Getter
    private static final int uniqueId = 2;

    public SellerComputerGames(Map<Integer, Integer> map, Rarity rarity, Level level){
        super(map, rarity, level);
    }

    @Override
    public double earnMoney() {
        return super.earnMoney()*COMPUTER_GAMES_SELLER_EARN_RATE;
    }
}
