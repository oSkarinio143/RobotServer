package service.operate;

import connection.channel.Server;
import connection.channel.ServerClientHandler;
import lombok.Getter;
import lombok.Setter;
import modules.OperationInvestor;
import modules.OperationSeller;
import modules.User;
import modules.interfaces.RobotSeller;
import modules.robot.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Setter
public class OperationMenager {
    private static User user;
    private static List<Class> classes = new ArrayList<>(List.of(SellerBooks.class, SellerBoardGames.class, SellerComputerGames.class, SellerHouses.class));
    private static List<Double> values = new ArrayList<>(List.of(RobotSeller.BOOK_SELLER_COST_RATE, RobotSeller.BOARD_GAMES_SELLER_COST_RATE,
            RobotSeller.COMPUTER_GAMES_SELLER_COST_RATE, RobotSeller.HOUSES_SELLER_COST_RATE));
    @Getter
    private static List<Map> operationsList = new ArrayList<>();

    private OperationMenager() {
    }

    public static void displayOperations() {
        user = UserMenager.actualUsedUser();
        System.out.println(user.getNick() + ", Operation:" +
                "\n1. Investor" +
                "\n2. Seller" +
                "\n3. Buy" +
                "\n4. Other" +
                "\n5. Close connection");
    }

    public static void displayInvestorOperations() {
        System.out.println("Operation for investor:" +
                "\n1. Display investor stats" +
                "\n2. Invest gold " +
                "\n3. Upgrade investor - cost - " + OperationInvestor.UPGRADE.getCost() +
                "\n4. Sell investor - value - " + -1 * OperationInvestor.SELL.getCost());
    }

    public static void displaySellerOperations() {
        System.out.println("Operation for seller:" +
                "\n1. Display seller stats" +
                "\n2. Earn gold" +
                "\n3. Upgrade seller (basic cost - " + OperationSeller.UPGRADE.getCost() + ", rate" + values + ") - " +
                "\n4. Sell seller (baisc value - " + -1 * SellerMenager.countSellValue(Optional.of(SellerBooks.class)) + ", rate" + values + ") - ");
    }

    public static void displayBuyOperations() {
        System.out.println("Buy something:" +
                "\n1. Buy investor - cost - " + InvestorMenager.countBuyCost() +
                "\n2. Buy book seller - cost - " + SellerMenager.countBuyCost(SellerBooks.class) +
                "\n3. Buy board games seller - cost - " + SellerMenager.countBuyCost(SellerBoardGames.class) +
                "\n4. Buy computer games seller - cost - " + SellerMenager.countBuyCost(SellerComputerGames.class) +
                "\n5. Buy house seller - cost - " + SellerMenager.countBuyCost(SellerHouses.class) +
                "\n6. Buy Machine - cost - " + Machine.getMACHINE_COST());
    }

    public static void displayOtherOperations() {
        System.out.println("Other operations:" +
                "\n1. Check gold " +
                "\n2. Perform job (cost - 100)" +
                "\n3. Perform investment (cost - 300)" +
                "\n4. Perform job and investment (cost - 500)");
    }

    public static void checkGold() {
        System.out.println(user.getNick() + " - Gold: " + user.getGold());
    }

    public static void showInvestor(int idInv) {
        Investor thisInvestor = InvestorMenager.findInvestorById(idInv);
        System.out.println("------------------INVESTOR----------------");
        System.out.println("ID - " + thisInvestor.getInvId() +
                "\nRarity - " + thisInvestor.getRarity() +
                "\nLevel - " + thisInvestor.getLevel());
        InvestorMenager.displayInvestorStats(idInv);
        System.out.println("----------------------------------------");
    }

    public static boolean investGold(int goldAmount) {
        boolean isSuccesful = BalanceMenager.safeCheckBalance(goldAmount);
        if (isSuccesful) {
            InvestorMenager.investMoney(goldAmount);
            return true;
        }
        return false;
    }

    public static boolean upgradeInvestor(int idInv) {
        boolean isSuccesful = BalanceMenager.safeChangeBalance(OperationInvestor.UPGRADE.getCost());
        if (isSuccesful) {
            InvestorMenager.upgradeInvestor(idInv);
            return true;
        }
        return false;
    }

    public static void sellInvestor(int idInv) {
        InvestorMenager.removeInvestor(idInv);
        BalanceMenager.safeChangeBalance(OperationInvestor.SELL.getCost());
    }

    public static void showSeller(int idSel) {
        AbstractSeller thisSeller = SellerMenager.findSellerById(idSel).orElse(null);
        System.out.println("------------------Seller----------------");
        SellerMenager.displaySellerClass(thisSeller);
        System.out.println("ID - " + thisSeller.getSellerId() +
                "\nRarity - " + thisSeller.getRarity() +
                "\nLevel - " + thisSeller.getLevel());
        SellerMenager.displaySellerStats(idSel);
        System.out.println("----------------------------------------");
    }

    public static void earnGold() {
        SellerMenager.earnGold();
    }

    public static boolean upgradeSeller(int selId) {
        Class expectedClass = SellerMenager.findSellerById(selId).get().getClass();
        int basicCost = OperationSeller.UPGRADE.getCost();
        int upgradeCost = 0;
        for (int i = 0; i < classes.size(); i++) {
            if (expectedClass == classes.get(i)) {
                upgradeCost = (int) (values.get(i) * basicCost);
                break;
            }
        }
        boolean isSuccessful=BalanceMenager.safeChangeBalance(upgradeCost);
        if(isSuccessful){
            SellerMenager.upgradeSeller(selId);
            return true;
        }
        return false;
    }

    public static void sellSeller(int selId) {
        Optional<Class> optionalClass = SellerMenager.removeSeller(selId);
        Class<? extends AbstractSeller> sellerClass = optionalClass.get();
        int basicValue = OperationSeller.SELL.getCost();
        for (int i = 0; i < classes.size(); i++) {
            if (classes.get(i) == sellerClass) {
                BalanceMenager.safeChangeBalance((int) (values.get(i) * basicValue));
                break;
            }
        }
    }

    public static boolean buyInvestor() {
        boolean isSuccesful = BalanceMenager.safeChangeBalance(InvestorMenager.countBuyCost());
        if (isSuccesful) {
            InvestorMenager.createInvestor();
            return true;
        }
        return false;
    }

    public static boolean buyBooksSeller() {
        boolean isSuccesful = BalanceMenager.safeChangeBalance(SellerMenager.countBuyCost(SellerBooks.class));
        if (isSuccesful) {
            SellerMenager.createConcreteSeller(SellerBooks.class);
            return true;
        }
        return false;
    }

    public static boolean buyBoardGamesSeller() {
        boolean isSuccesful = BalanceMenager.safeChangeBalance(SellerMenager.countBuyCost(SellerBoardGames.class));
        if (isSuccesful) {
            SellerMenager.createConcreteSeller(SellerBoardGames.class);
            return true;
        }
        return false;
    }

    public static boolean buyComputerGamesSeller() {
        boolean isSuccesful = BalanceMenager.safeChangeBalance(SellerMenager.countBuyCost(SellerComputerGames.class));
        if (isSuccesful){
            SellerMenager.createConcreteSeller(SellerComputerGames.class);
            return true;
        }
        return false;
    }

    public static boolean buyHousesSeller() {
        boolean isSuccesful = BalanceMenager.safeChangeBalance(SellerMenager.countBuyCost(SellerHouses.class));
        if (isSuccesful) {
            SellerMenager.createConcreteSeller(SellerHouses.class);
            return true;
        }
        return false;
    }

    public static boolean buyMachine() {
        boolean isSuccesful = BalanceMenager.safeChangeBalance(Machine.getMACHINE_COST());
        if (isSuccesful) {
            MachineMenager.unlockMachine();
            return true;
        }
        return false;
    }

    public static boolean performWork(int howManyTimes) {
        boolean isSuccessfulCondition2 = BalanceMenager.safeCheckBalance(Machine.getMACHINE_SELLER_USE());
        if (isSuccessfulCondition2) {
            BalanceMenager.safeChangeBalance(Machine.getMACHINE_SELLER_USE()*howManyTimes);
            MachineMenager.performWorkMultiple(howManyTimes);
            return true;
        }
        return false;
    }

    public static boolean performInvestment(int howManyTimes, int goldAmount) {
        boolean isSuccessfulCondition2 = BalanceMenager.safeCheckBalance(Machine.getMACHINE_INVESTER_USE());
        boolean isSuccessfulCondition3 = BalanceMenager.safeCheckBalance(goldAmount);
        if (isSuccessfulCondition2 && isSuccessfulCondition3) {
            BalanceMenager.safeChangeBalance(Machine.getMACHINE_INVESTER_USE()*howManyTimes);
            MachineMenager.performInvestmentMultiple(howManyTimes, goldAmount);
            return true;
        }
        return false;
    }

    public static boolean performWorkInvestment(int howManyTimes, int goldAmount) {
        boolean isSuccessfulCondition2 = BalanceMenager.safeCheckBalance(Machine.getMACHINE_TOGETHER_USE());
        boolean isSuccessfulCondition3 = BalanceMenager.safeCheckBalance(goldAmount);
        if (isSuccessfulCondition2 && isSuccessfulCondition3) {
            BalanceMenager.safeChangeBalance(Machine.getMACHINE_TOGETHER_USE()*howManyTimes);
            MachineMenager.performWorkInvestmentMultiple(howManyTimes, goldAmount);
            return true;
        }
        return false;
    }

    public static void clearUserList(){
        String file = "userRecords.txt";
        try(FileWriter writer = new FileWriter(file)){
            writer.write("");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ServerClientHandler.setIsChannelOpen(0);
        Server.setNickList(new ArrayList<>());
    }

    public static void makeMethodTree() {
        operationsList.add(Map.of((Object) (Consumer<Integer>) OperationMenager::showInvestor, List.of(1, 1, 1)));
        operationsList.add(Map.of((Object) (Consumer<Integer>) OperationMenager::investGold, List.of(1, 2, 3)));
        operationsList.add(Map.of((Object) (Consumer<Integer>) OperationMenager::upgradeInvestor, List.of(1, 3, 1)));
        operationsList.add(Map.of((Object) (Consumer<Integer>) OperationMenager::sellInvestor, List.of(1, 4, 1)));
        operationsList.add(Map.of((Object) (Consumer<Integer>) OperationMenager::showSeller, List.of(2, 1, 2)));
        operationsList.add(Map.of((Object) (Runnable) OperationMenager::earnGold, List.of(2, 2, 0)));
        operationsList.add(Map.of((Object) (Consumer<Integer>) OperationMenager::upgradeSeller, List.of(2, 3, 2)));
        operationsList.add(Map.of((Object) (Consumer<Integer>) OperationMenager::sellSeller, List.of(2, 4, 2)));
        operationsList.add(Map.of((Object) (Runnable) OperationMenager::buyInvestor, List.of(3, 1, 0)));
        operationsList.add(Map.of((Object) (Runnable) OperationMenager::buyBooksSeller, List.of(3, 2, 0)));
        operationsList.add(Map.of((Object) (Runnable) OperationMenager::buyBoardGamesSeller, List.of(3, 3, 0)));
        operationsList.add(Map.of((Object) (Runnable) OperationMenager::buyComputerGamesSeller, List.of(3, 4, 0)));
        operationsList.add(Map.of((Object) (Runnable) OperationMenager::buyHousesSeller, List.of(3, 5, 0)));
        operationsList.add(Map.of((Object) (Runnable) OperationMenager::buyMachine, List.of(3, 6, 0)));
        operationsList.add(Map.of((Object) (Runnable) OperationMenager::checkGold, List.of(4, 1, 0)));
        operationsList.add(Map.of((Object) (Consumer<Integer>) OperationMenager::performWork, List.of(4, 2, 4)));
        operationsList.add(Map.of((Object) (BiConsumer<Integer, Integer>) OperationMenager::performInvestment, List.of(4, 3, 5)));
        operationsList.add(Map.of((Object) (BiConsumer<Integer, Integer>) OperationMenager::performWorkInvestment, List.of(4, 4, 5)));
    }
}
