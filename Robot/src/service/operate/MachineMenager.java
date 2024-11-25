package service.operate;

import modules.User;

public class MachineMenager {
    private static User user = UserMenager.getUser();

    public static void unlockMachine(){
        user.unlockMachine();
    }

    public static void performWorkMultiple(int howManyTimes){
        user.getMachine().performWork(howManyTimes);
    }

    public static void performInvestmentMultiple(int howManyTimes, int goldAmount){
        user.getMachine().performInvestition(howManyTimes, goldAmount);
    }

    public static void performWorkInvestmentMultiple(int howManyTimes, int goldAmount){
        performInvestmentMultiple(howManyTimes, goldAmount);
        performWorkMultiple(howManyTimes);
    }

    public static boolean isMachineUnlocked(){
        if(user.getMachine()==null)
            return false;
        return true;
    }
}
