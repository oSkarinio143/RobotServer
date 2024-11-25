package service.operate;

import lombok.Getter;
import modules.User;

public class UserMenager {
    @Getter
    private static User user;

    private UserMenager(){}

    public static void createNewUser(String nick){
        user = new User(nick);
    }
}


