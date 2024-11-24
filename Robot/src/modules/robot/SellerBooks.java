package modules.robot;

import lombok.Getter;
import lombok.Setter;
import modules.Level;
import modules.Rarity;

import java.util.Map;

public class SellerBooks extends AbstractSeller{
    private int sellerBookId;

    @Getter
    @Setter
    private static int sellerBookQuantity = 0;

    @Getter
    private static final int uniqueId = 0;

    public SellerBooks(Map<Integer, Integer> map, Rarity rarity, Level level){
        super(map, rarity, level);
        sellerBookId=sellerBookQuantity;
    }

    @Override
    public double earnMoney() {
        return super.earnMoney()*BOOK_SELLER_EARN_RATE;
    }
}
