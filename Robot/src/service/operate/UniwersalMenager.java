package service.operate;

import java.util.ArrayList;
import java.util.List;

public class UniwersalMenager {
    public static boolean isInRange(int number, int minRange, int maxRange){
        if(number<=maxRange && number>=minRange)
            return true;
        else
            return false;
    }

    public static boolean isInteger(String idChoice){
        char[] charki = idChoice.toCharArray();
        List<Character> digitList
                = new ArrayList<>(List.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'));
        for (char charek : charki) {
            if(!digitList.contains(charek)){
                return false;
            }
        }
        return true;
    }

    public static int stringToInteger(String stringToConvert){
        int convertedInteger = Integer.parseInt(stringToConvert);
        return convertedInteger;
    }

    public static boolean checkStringIntList(String stringToCheck, List<Integer> list){
        if (stringToCheck==null)
            return false;
        if(isInteger(stringToCheck)){
            int convertedInteger = stringToInteger(stringToCheck);
            return list.contains(convertedInteger);
        }
        return false;
    }

    public static boolean checkStringIntRange(String stringToCheck, int minRange, int maxRange){
        if(isInteger(stringToCheck)) {
            int convertedInteger = stringToInteger(stringToCheck);
            return isInRange(convertedInteger, minRange, maxRange);
        }
        return false;
    }

}
