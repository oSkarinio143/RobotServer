package service;

import exceptions.IncorrectIdRuntimeException;
import exceptions.IncorrectNumberRuntimeException;
import modules.Level;
import modules.Rarity;
import modules.robot.*;

import java.util.*;

public class Generator{
    private static final Random random = new Random();
    private static final int EPIC_CHANCE = 5;
    private static final int RARE_CHANCE = 20;
    private static final int BOOK_CHANCE = 80;
    private static final int BOARD_GAMES_CHANCE = 12;
    private static final int COMPUTER_GAMES_CHANCE = 7;
    private static final int HOUSE_CHANCE = 1;

    public static Iterator<Integer> generateMyIterator(int number, int expectedNumber){
        int[] numberArray = new int[1];
        numberArray[0] = number;
        return new Iterator<Integer>() {
            @Override
            public boolean hasNext() {
                if (numberArray[0] >= expectedNumber) {
                    numberArray[0] = numberArray[0] - expectedNumber;
                    return true;
                } else {
                    return false;
                }
            }
            @Override
            public Integer next() {
                return expectedNumber;
            }

        };
    }

    public static int returnOneRegardChance (int ...chance){
        int random = (int)(Math.random()*100)+1;
        int nextChance=0;
        for (int i : chance) {
            nextChance+=i;
            if(nextChance>=random){
                return i;
            }
        }
        throw new IncorrectNumberRuntimeException();
    }

    public static Integer generateNumber(int minRange, int maxRange){
        return random.nextInt((maxRange-minRange)+1)+minRange;
    }

    public static boolean generateByChance(int chanceEnd){
        int minRange=1;
        int maxRange=100;
        int chanceStart=1;
        for (int i = chanceStart; i<=chanceEnd; i++){
            int chosedNumber = random.nextInt(maxRange)+minRange;
            if(chosedNumber==1)
                return true;
            maxRange--;
        }
        return false;
    }

    private static Integer generateStatsPower(){
        int minStatsRange = 6;
        int maxStatsRange = 18;
        return generateNumber(minStatsRange, maxStatsRange);
    }

    private static Level generateLevel(int id){
        if(id==1) return Level.BEGINNER;
        if(id==2) return Level.INTERMEDIATE;
        if(id==3) return Level.ADVANCED;
        throw new IncorrectIdRuntimeException("Incorrect Number");
    }

    private static Rarity generateRarity(int epicChance, int rareChance){
        int randomNumber = random.nextInt(101);
        if(randomNumber<=epicChance){
            return Rarity.EPIC;
        }
        else if(randomNumber<=(rareChance+epicChance)){
            return Rarity.RARE;
        }
        else if(randomNumber > (rareChance+epicChance)){
            return Rarity.COMMON;
        }
        return null;
    }

    private static Map<Integer, Integer> generateEachStat(int statsPower, Level level){
        Map <Integer, Integer> statsMap = new HashMap<>();

        int loopRandomNumber;
        do {
            loopRandomNumber=statsPower;
            statsMap.put(0, random.nextInt(level.getConstraint()));
            statsMap.put(1, random.nextInt(level.getConstraint()));
            statsMap.put(2, random.nextInt(level.getConstraint()));
            statsMap.put(3, random.nextInt(level.getConstraint()));
            loopRandomNumber-=statsMap.get(0)+statsMap.get(1)+statsMap.get(2)+statsMap.get(3);
        }while(loopRandomNumber!=0);
        return statsMap;
    }

    public static Map<Integer, Integer> generateUpgradesStats(int upgradeStatsPower){
            Map<Integer, Integer> upgradeStats = new LinkedHashMap<>();
            Iterator <Integer> myIterator = generateMyIterator(upgradeStatsPower%4,1);
            int thisStat;

            for (int i = 0; i < 4; i++) {
                thisStat=upgradeStatsPower/4;
                if(myIterator.hasNext())
                    thisStat+=myIterator.next();
                upgradeStats.put(i, thisStat);
            }
            return upgradeStats;
        }

    public static Map<Integer, Integer> upgradeStatsNumbers(Map<Integer, Integer> statsMap, int upgradeStatsPower, Level level){
        Map<Integer, Integer> upgradeMap = generateUpgradesStats(upgradeStatsPower);
        statsMap=Sorting.sortMapStream(statsMap);
        int kUpg = 0;
        int nextUpg=0;
        for(Map.Entry<Integer, Integer> entryMap : statsMap.entrySet()){
            int v;
            int k = entryMap.getKey();
            v = entryMap.getValue() + upgradeMap.get(kUpg) + nextUpg;
            nextUpg=0;
            kUpg++;
            while (v>level.getConstraint()){
                v-=1;
                nextUpg++;
            }
            statsMap.put(k, v);
        }
        return statsMap;
    }

    public static int sumStats(Map<Integer, Integer> mapStats, int ...statsId){
        int sum=0;
        for (int i : statsId) {
            sum+=mapStats.get(i);
        }
        return sum;
    }

    public static int countBasicStatsUpgradeInvestor(Investor investor){
        return investor.getRarity().getAdditionalStats() + Level.getAdditionalStatsForLevel(investor.getLevel().getAdditionalStats());
    }

    public static int countBasicStatsUpgradeSeller(AbstractSeller seller){
        return seller.getRarity().getAdditionalStats() + Level.getAdditionalStatsForLevel(seller.getLevel().getAdditionalStats());
    }

    public static int countUpgradeLevelInvestor(Investor investor){
        int newLevelId = investor.getLevel().getId()+1;
        investor.setLevel(Level.getById(newLevelId));
        return Level.getById(newLevelId).getAdditionalStats();
    }

    public static int countUpgradeLevelSeller(AbstractSeller seller){
        int newlevelId = seller.getLevel().getId()+1;
        seller.setLevel(Level.getById(newlevelId));
        return Level.getById(newlevelId).getAdditionalStats();
    }

    public static Investor generateBasicInvestor(int levelNumber){
        int rareChance = 20;
        int epicChance = 5;

        Level level = generateLevel(levelNumber);
        Rarity rarity = generateRarity(epicChance, rareChance);
        Map<Integer, Integer> eachStat = generateEachStat(generateStatsPower(), level);

        return new Investor(eachStat, rarity, level);
    }

    public static AbstractSeller generateBasicRandomSeller(int levelNumber){
        int drawedValue = returnOneRegardChance(BOOK_CHANCE, BOARD_GAMES_CHANCE, COMPUTER_GAMES_CHANCE, HOUSE_CHANCE);
        Level level = generateLevel(levelNumber);
        Rarity rarity = generateRarity(EPIC_CHANCE, RARE_CHANCE);
        Map<Integer, Integer> eachStat = generateEachStat(generateStatsPower(), level);

        if(drawedValue==BOOK_CHANCE)
            return new SellerBooks(eachStat, rarity, level);

        else if(drawedValue==BOARD_GAMES_CHANCE)
            return new SellerBoardGames(eachStat, rarity, level);

        else if(drawedValue==COMPUTER_GAMES_CHANCE)
            return new SellerComputerGames(eachStat, rarity, level);

        else if(drawedValue==HOUSE_CHANCE)
            return new SellerHouses(eachStat, rarity, level);

        throw new IncorrectNumberRuntimeException();
    }

    public static <T extends AbstractSeller> AbstractSeller generateBasicConcreteSeller(int levelNumber, Class<T> type){
        Level level = generateLevel(levelNumber);
        Rarity rarity = generateRarity(EPIC_CHANCE, RARE_CHANCE);
        Map<Integer, Integer> eachStat = generateEachStat(generateStatsPower(), level);

        if(type==SellerBooks.class){
            return new SellerBooks(eachStat, rarity, level);
        }
        else if(type==SellerBoardGames.class){
            return new SellerBoardGames(eachStat, rarity, level);
        }
        else if(type==SellerComputerGames.class){
            return new SellerComputerGames(eachStat, rarity, level);
        }
        else if(type==SellerHouses.class){
            return new SellerHouses(eachStat, rarity, level);
        }
        throw new IncorrectNumberRuntimeException();
    }

    public static void upgradeBasicInvestor(Investor investor){
        int statsUpgrade = countBasicStatsUpgradeInvestor(investor);
        investor.setStatistics(upgradeStatsNumbers(Sorting.sortMapComparator(investor.getStatistics()), statsUpgrade, investor.getLevel()));
    }

    public static void upgradeBasicSeller(AbstractSeller seller){
        int upgradeStats = countBasicStatsUpgradeSeller(seller);
        seller.setStatistics(upgradeStatsNumbers(Sorting.sortMapComparator(seller.getStatistics()), upgradeStats, seller.getLevel()));
    }

    public static void upgradeLevelInvestor(Investor investor){
        int statsNumber = countUpgradeLevelInvestor(investor);
        investor.setStatistics(upgradeStatsNumbers(Sorting.sortMapComparator(investor.getStatistics()), statsNumber, investor.getLevel()));
    }

    public static void upgradeLevelSeller(AbstractSeller seller){
        int statsNumber = countUpgradeLevelSeller(seller);
        seller.setStatistics(upgradeStatsNumbers(Sorting.sortMapComparator(seller.getStatistics()), statsNumber, seller.getLevel()));
    }

    public static double sinew(double basis, int index){
        double number=basis;
        if(index>0) {
            for (int i = 1; i < index; i++) {
                number = number * basis;
            }
        }
        else
            number=1;
        return number;
    }

    public static boolean checkingRevolt(int revoltChance){
        for (int i = 0; i < revoltChance; i++) {
            int number = random.nextInt(1000) + 1;
            if(number==revoltChance){
                return true;
            }
        }
        return false;
    }
}
