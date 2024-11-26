package service.operate;

import lombok.Getter;
import modules.User;

import java.util.ArrayList;
import java.util.List;

public class UserMenager {
    @Getter
    private static List<User> userList = new ArrayList<>();
    @Getter
    private static String actualUserNick;

    private UserMenager(){}

    public static User createNewUser(String nick){
        User user = new User(nick);
        actualUserNick = nick;
        userList.add(user);
        return user;
    }

    public static User findUserByNick(String nick){
        for (User userNick : userList) {
            if(userNick.getNick().equals(nick)) {
                actualUserNick = userNick.getNick();
                return userNick;
            }
        }
        throw new RuntimeException();
    }

    public static User actualUsedUser(){
        return findUserByNick(actualUserNick);
    }

    public static void setUserEverywhere() {
        InvestorMenager.setUserInv();
        SellerMenager.setUserSell();
        MachineMenager.setUserMachine();
        BalanceMenager.setUserBalance();
    }
}


