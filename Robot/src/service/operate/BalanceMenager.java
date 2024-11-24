package service.operate;

import exceptions.InsufficientBalanceException;
import lombok.Getter;
import lombok.Setter;
import modules.User;

@Getter
@Setter
public class BalanceMenager {
    private static User user = User.getInstance();

    public static double returnGoldAmount(){
        return user.getGold();
    }

    public static boolean checkBalance(int change) throws InsufficientBalanceException {
        double userGold = user.getGold();
        if(userGold>=change)
            return true;
        else
            throw new InsufficientBalanceException();
    }

    public static void changeBalance(int change) throws InsufficientBalanceException {
        if (checkBalance(change)) {
            double userGold = user.getGold();
            userGold-=change;
            user.setGold(userGold);
        }
        //Operator warunkowy nie do instrukcji które mają efekty uboczne
    }

    public static boolean safeCheckBalance(int amount){
        try{
            checkBalance(amount);
            return true;
        }catch (InsufficientBalanceException e){
            System.out.println("User doesn't have enough money");
            return false;
        }
    }

    public static boolean safeChangeBalance(int change){
        try{
            changeBalance(change);
            return true;
        }catch (InsufficientBalanceException e){
            System.out.println("User doesn't have enough money");
            return false;
        }
    }
}
