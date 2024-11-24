package modules.interfaces;

import java.util.ArrayList;
import java.util.List;

public interface RobotSeller extends Robot {
    double BOOK_SELLER_COST_RATE = 1;
    double BOARD_GAMES_SELLER_COST_RATE = 5;
    double COMPUTER_GAMES_SELLER_COST_RATE = 20;
    double HOUSES_SELLER_COST_RATE = 50;
    double BUYING_RATE = 1.1;
    double BOOK_SELLER_EARN_RATE = 1;
    double BOARD_GAMES_SELLER_EARN_RATE = 6;
    double COMPUTER_GAMES_SELLER_EARN_RATE = 22;
    double HOUSES_SELLER_EARN_RATE = 54;
    double EARNINGS = 1;
    List<Double> costRates = new ArrayList<>(List.of(BOOK_SELLER_COST_RATE, BOARD_GAMES_SELLER_COST_RATE, COMPUTER_GAMES_SELLER_COST_RATE, HOUSES_SELLER_COST_RATE));
    List<Double> earnRates = new ArrayList<>(List.of(BOOK_SELLER_EARN_RATE, BOARD_GAMES_SELLER_EARN_RATE, COMPUTER_GAMES_SELLER_EARN_RATE, HOUSES_SELLER_EARN_RATE));
}
