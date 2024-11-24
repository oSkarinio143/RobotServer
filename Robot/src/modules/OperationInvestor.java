package modules;

import exceptions.IncorrectIdRuntimeException;
import lombok.Getter;
import lombok.Setter;

@Getter
public enum OperationInvestor {
    DISPLAY(0, 0), CREATE(1, 1000), UPGRADE(2, 5000), SELL(3, -500);
    int id;
    int cost;

    OperationInvestor(int id, int cost) {
        this.id = id;
        this.cost = cost;
    }

    public static OperationInvestor getById(int id) {
        for (OperationInvestor value : OperationInvestor.values()) {
            if (value.id == id) {
                return value;
            }
        }
        throw new IncorrectIdRuntimeException();
    }
}
