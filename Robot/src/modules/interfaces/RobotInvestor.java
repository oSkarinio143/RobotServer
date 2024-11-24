package modules.interfaces;

public interface RobotInvestor extends Robot {
    double investitionRate = 0.003;

    double invest(int goldAmount);
}
