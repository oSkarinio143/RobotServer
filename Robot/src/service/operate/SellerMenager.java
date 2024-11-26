package service.operate;

import exceptions.IllegalOperation;
import modules.OperationSeller;
import modules.StatsSeller;
import modules.User;
import modules.interfaces.RobotSeller;
import modules.robot.*;
import service.Generator;
import service.Sorting;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SellerMenager {
    private static User user;
    private static List<AbstractSeller> ownedSellers = new ArrayList<>();

    public static void setUserSell(){
        user = UserMenager.actualUsedUser();
        ownedSellers = user.getOwnedSellers();
        SellerBooks.setSellerBookQuantity(0);
        SellerBoardGames.setSellerBoardGamesQuantity(0);
        SellerComputerGames.setSellerComputerGamesQuantity(0);
        SellerHouses.setSellerHousesQuantity(0);
        AbstractSeller.setQuantitySel(0);
        for (AbstractSeller seller : ownedSellers) {
            if(seller instanceof SellerBooks)
                SellerBooks.setSellerBookQuantity(SellerBooks.getSellerBookQuantity()+1);
            if(seller instanceof SellerBoardGames)
                SellerBoardGames.setSellerBoardGamesQuantity(SellerBoardGames.getSellerBoardGamesQuantity()+1);
            if(seller instanceof SellerComputerGames)
                SellerComputerGames.setSellerComputerGamesQuantity(SellerComputerGames.getSellerComputerGamesQuantity()+1);
            if(seller instanceof SellerHouses)
                SellerHouses.setSellerHousesQuantity(SellerHouses.getSellerHousesQuantity() + 1);
        }
    }

    public static Optional<AbstractSeller> findSellerById(int idSeller){
        for (AbstractSeller seller : user.getOwnedSellers()) {
            if(seller.getSellerId()==idSeller){
                return Optional.of(seller);
            }
        }
        return Optional.empty();
    }

    public static void displaySellerStats(int selId){
        Optional<AbstractSeller> optionalSeller = findSellerById(selId);
        AbstractSeller seller = optionalSeller.get();
        int i=0;
        for (StatsSeller value : StatsSeller.values()) {
            System.out.println(value+" - "+seller.getStatistics().get(i));
            i++;
        }

    }

    public static void displaySellerClass(AbstractSeller seller){
        if(seller instanceof SellerBooks){
            System.out.println("Specialisation - BOOKS");
        }
        else if(seller instanceof SellerBoardGames){
            System.out.println("Specialisation - BOARD GAMES");
        }
        else if(seller instanceof SellerComputerGames){
            System.out.println("Specialisation - COMPUTER GAMES");
        }
        else if(seller instanceof SellerHouses){
            System.out.println("Specialisation - HOUSES");
        }
    }

    public static <T extends AbstractSeller> void createConcreteSeller(Class<T> type){
        int levelNumber = 1;
        ArrayList<Class> classesList = new ArrayList<>(List.of(SellerBooks.class, SellerBoardGames.class, SellerComputerGames.class, SellerHouses.class));
        for (Class classSingle :classesList){

            if(type==classSingle) {
                AbstractSeller seller = Generator.generateBasicConcreteSeller(levelNumber, classSingle);
                Generator.upgradeBasicSeller(seller);
                user.addToList(seller);
            }
        }
    }

    public static void upgradeSeller(int selId){
        AbstractSeller seller = findSellerById(selId).get();
        if(seller.getLevel().getId()<3) {
            Generator.upgradeLevelSeller(seller);
        }
        else
            throw new IllegalOperation();
    }

    public static int returnQuantityForId(int idQuantity){
        List <Integer> quantiteis = new ArrayList<>(List.of(SellerBooks.getQuantitySel(), SellerBoardGames.getQuantitySel(), SellerBoardGames.getQuantitySel(), SellerHouses.getQuantitySel()));
        return quantiteis.get(idQuantity);
    }

    public static <T extends AbstractSeller> int countBuyCost(Class<T> type){
        double classRate = 0;
        if (type==SellerBooks.class) {
            classRate = SellerBooks.BOOK_SELLER_COST_RATE;
            classRate *= Generator.sinew(RobotSeller.BUYING_RATE, SellerBooks.getSellerBookQuantity());
        }
        if (type==SellerBoardGames.class){
            classRate =  SellerBoardGames.BOARD_GAMES_SELLER_COST_RATE;
            classRate *= Generator.sinew(RobotSeller.BUYING_RATE, SellerBoardGames.getSellerBoardGamesQuantity());
        }
        if (type==SellerComputerGames.class) {
            classRate = SellerComputerGames.COMPUTER_GAMES_SELLER_COST_RATE;
            classRate *= Generator.sinew(RobotSeller.BUYING_RATE, SellerComputerGames.getSellerComputerGamesQuantity());
        }
        if (type==SellerHouses.class) {
            classRate = SellerHouses.HOUSES_SELLER_COST_RATE;
            classRate *= Generator.sinew(RobotSeller.BUYING_RATE, SellerHouses.getSellerHousesQuantity());
        }
        classRate=Math.round(classRate*100.0) /100.0;
        return (int) (OperationSeller.CREATE.getCost()*classRate);
    }

    public static <T extends AbstractSeller> Optional<Class> removeSeller(int idSel){
        Optional<AbstractSeller> receivedSeller = findSellerById(idSel);
        if(receivedSeller.isEmpty()){
            return Optional.empty();
        }
        else{
            Class<? extends AbstractSeller> sellerClass = receivedSeller.get().getClass();
            user.removeFromList(AbstractSeller.class, idSel);

            return Optional.of(sellerClass);
        }
    }

    public static int countSellValue(Optional<Class> optionalClass){
        AtomicInteger rate = new AtomicInteger(0);
        AtomicInteger amount = new AtomicInteger(0);
        optionalClass.ifPresent(v -> {
            if(v==SellerBooks.class){
                rate.compareAndSet(0, (int) SellerBooks.BOOK_SELLER_COST_RATE);
                amount.compareAndSet(0, SellerBooks.getSellerBookQuantity());
            }
            if(v==SellerBoardGames.class){
                rate.compareAndSet(0, (int) SellerBoardGames.BOARD_GAMES_SELLER_COST_RATE);
                amount.compareAndSet(0, SellerBoardGames.getSellerBoardGamesQuantity());
            }
            if(v==SellerComputerGames.class){
                rate.compareAndSet(0, (int) SellerComputerGames.COMPUTER_GAMES_SELLER_COST_RATE);
                amount.compareAndSet(0, SellerBoardGames.getSellerBoardGamesQuantity());
            }
            if(v==SellerHouses.class){
                rate.compareAndSet(0, (int) SellerHouses.HOUSES_SELLER_COST_RATE);
                amount.compareAndSet(0, SellerBoardGames.getSellerBoardGamesQuantity());
            }
        });
        int value=rate.get();
        value=(int) (value*OperationSeller.SELL.getCost()*Generator.sinew(AbstractSeller.BUYING_RATE, amount.get()));
        return value;
    }

    public static void upgradeRandomSellersMind(int chance){
        for (int i = 0; i < user.getOwnedSellers().size(); i++) {
            for (int i1 = 0; i1 < chance; i1++) {
                int number = (int)(Math.random()*100)+1;
                if(number==1 && user.getOwnedSellers().get(i).getStatistics().get(0)<10){
                    user.getOwnedSellers().get(i).setStatistics(user.getOwnedSellers().get(i).getStatistics().get(0)+1);
                }
            }
        }
    }

    public static boolean isRevolt(){
        double revoltChance=0;
        for (AbstractSeller seller : user.getOwnedSellers()) {
            if(seller.getStatistics().get(0)<7) {
                revoltChance += seller.revolt();
            }
        }
        return Generator.checkingRevolt((int) revoltChance*10);
    }

    public static boolean isAutodestruction(){
        double autodestructionChance=0;
        for(AbstractSeller seller : user.getOwnedSellers()){
            if(seller.getStatistics().get(0)>=7){
                autodestructionChance += seller.revolt();
            }
        }
        return Generator.checkingRevolt((int) autodestructionChance*10);
    }

    public static void performWork(){
        double generalEarned=0;

        for (int i = 0; i < user.getOwnedSellers().size(); i++) {
            generalEarned += user.getOwnedSellers().get(i).earnMoney();
        }
        double earnedGold=Math.round(generalEarned*100.0)/100.0;
        double userGold=user.getGold()+earnedGold;
        user.setGold(userGold);
    }

    public static void removeSellerHighestStat(List<AbstractSeller> sellerList, int stat){
        List<AbstractSeller> sortedSellers = Sorting.sortSellerListByStat(sellerList, stat);
        removeSeller(sortedSellers.get(0).getSellerId());
    }

    public static void earnGold(){
        if(isRevolt()){
            System.out.println("Bunt");
            upgradeRandomSellersMind(100);
        }
        if(isAutodestruction()){
            System.out.println("Destrukcja");
            removeSellerHighestStat(user.getOwnedSellers(), 0);
        }else {
            performWork();
        }
    }

    public static boolean checkIfAboveNumber(Map<Integer, Integer> map, int number){
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if(entry.getValue()>number){
                return true;
            }
        }
        return false;
    }

    public static List<Integer> returnIdsList (){
        List<Integer> idsList = new ArrayList<>();
        user.getOwnedSellers().forEach(v -> idsList.add(v.getSellerId()));
        return idsList;
    }
}
