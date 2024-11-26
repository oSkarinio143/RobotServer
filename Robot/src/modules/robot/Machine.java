package modules.robot;

import lombok.Getter;
import modules.User;
import modules.interfaces.Robot;
import modules.interfaces.RobotInvestor;
import modules.interfaces.RobotSeller;
import service.operate.OperationMenager;
import service.operate.UserMenager;

import java.util.ArrayList;
import java.util.List;

public class Machine {
    private User user = UserMenager.actualUsedUser();
    private List<Investor> investorsList = new ArrayList<>();
    private List<AbstractSeller> sellerList = new ArrayList<>();
    @Getter
    private static final int MACHINE_COST = 10000;
    @Getter
    private static final int MACHINE_SELLER_USE = 100;
    @Getter
    private static final int MACHINE_INVESTER_USE = 300;
    @Getter
    private static final int MACHINE_TOGETHER_USE = 500;


    public Machine(){
        investorsList = user.getOwnedInvestors();
        sellerList = user.getOwnedSellers();
    }

    public void performWork(int howManyTimes){
        for (int i = 0; i < howManyTimes; i++) {
            OperationMenager.earnGold();
        }
    }

    public void performInvestition(int howManyTimes, int goldAmount){
        for (int i = 0; i < howManyTimes; i++) {
            OperationMenager.investGold(goldAmount);
        }
    }





}
