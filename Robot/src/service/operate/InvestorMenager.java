package service.operate;

import exceptions.IllegalOperation;
import exceptions.IncorrectIdRuntimeException;
import modules.OperationInvestor;
import modules.StatsInvestor;
import modules.User;
import modules.robot.AbstractSeller;
import modules.robot.Investor;
import service.Generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static service.Generator.*;

public class InvestorMenager {
    private static User user;
    private static List<Investor> ownedInvestors = new ArrayList<>();

    public static void setUserInv(){
        user = UserMenager.actualUsedUser();
        ownedInvestors = user.getOwnedInvestors();
        Investor.setQuantityInv(ownedInvestors.size());
    }

    public static Investor findInvestorById(int idInv){
        List<Investor> listInvestors = user.getOwnedInvestors();

        for (Investor investor : listInvestors) {
            if(investor.getInvId()==idInv)
                return investor;
        }
        throw new IncorrectIdRuntimeException();
    }

    public static void displayInvestorStats(int idInv){
        Investor investor = findInvestorById(idInv);
        Map<Integer, Integer> intMap = investor.getStatistics();
        intMap.forEach((k, v)->{
            System.out.println(StatsInvestor.getById(k)+" - "+v);
        });
    }

    public static String getStatisticsString(int idInv){
        Investor investor = findInvestorById(idInv);
        Map<Integer, Integer> intMap = investor.getStatistics();
        String statsView="";
        for (Map.Entry<Integer, Integer> entry : intMap.entrySet()) {
            Integer value = entry.getValue();
            Integer key = entry.getKey();
            String part = StatsInvestor.getById(key)+" - "+value+"|";
            statsView+=part;
        }
        return statsView;
    }

    public static void createInvestor(){
        int levelNumber = 1;
        Investor investor = generateBasicInvestor(levelNumber);
        upgradeBasicInvestor(investor);
        user.addToList(investor);
    }

    public static void removeInvestor(int idInv){
        user.removeFromList(Investor.class, idInv);
    }

    public static void upgradeInvestor (int idInv){
        Investor investor = findInvestorById(idInv);
        if (investor.getLevel().getId()<3){
            Generator.upgradeLevelInvestor(investor);
        }else{
            throw new IllegalOperation();
        }
    }

    private static boolean isRevolt (){
        double revoltChance=0;
        for (Investor investor : user.getOwnedInvestors()) {
            revoltChance += investor.revolt();
        }
        return Generator.checkingRevolt((int) revoltChance*10);
    }

    private static void makeInvestition(int goldAmount){
        double userGold = user.getGold();

        for (Investor investor : user.getOwnedInvestors()) {
            double earnedAmount = investor.invest(goldAmount);
            userGold+=earnedAmount;
        }
        userGold=Math.round(userGold*100.0)/100.0;
        user.setGold(userGold);
    }

    public static void investMoney(int goldAmount){
        if(isRevolt()){
            user.setGold(user.getGold()-goldAmount);
        }else{
            makeInvestition(goldAmount);
        }
    }

    public static int countBuyCost(){
        double rate = Investor.getBuyCostMultiplier();
        for(int i = 0; i<Investor.getQuantityInv(); i++){
            rate *= Investor.getBuyCostMultiplier();
        }
        int cost = (int) (OperationInvestor.CREATE.getCost()*rate);
        return cost;
    }

    public static int countValue(){
        double rate = Investor.getBuyCostMultiplier();
        for(int i = 0; i<Investor.getQuantityInv(); i++){
            rate *= Investor.getBuyCostMultiplier();
        }
        int cost = (int) (OperationInvestor.SELL.getCost()*rate);
        return cost;
    }

    public static boolean checkIfAboveNumber(Map<Integer, Integer> map, int number){
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if(entry.getValue()>number){
                return true;
            }
        }
        return false;
    }

    public static List<Integer> returnIdsList(){
        List<Integer> idList = new ArrayList<>();
        user.getOwnedInvestors().forEach(v -> idList.add(v.getInvId()));
        return idList;
    }

}
