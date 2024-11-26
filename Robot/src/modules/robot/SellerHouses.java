package modules.robot;

import lombok.Getter;
import lombok.Setter;
import modules.Level;
import modules.Rarity;

import java.io.Serializable;
import java.util.Map;

public class SellerHouses extends AbstractSeller implements Serializable {
    private static final long serialVersionUID = 1L;
    private int sellerHousesId;

    @Getter
    @Setter
    private static int sellerHousesQuantity = 0;

    @Getter
    private static final int uniqueId = 3;

    public SellerHouses(Map<Integer, Integer> map, Rarity rarity, Level level){
        super(map, rarity, level);
    }

    @Override
    public double earnMoney() {
        return super.earnMoney()*HOUSES_SELLER_EARN_RATE;
    }
}
