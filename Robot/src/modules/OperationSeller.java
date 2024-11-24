package modules;

import exceptions.IncorrectIdRuntimeException;
import lombok.Getter;

@Getter
public enum OperationSeller {
    DISPLAY(0, 0), CREATE(1, 10), UPGRADE(2, 30), SELL(3, -5);
    private int id;
    private int cost;

    OperationSeller(int id, int cost){
        this.id=id;
        this.cost=cost;
    }

    public static OperationSeller getById(int idOperation){
        for (OperationSeller value : OperationSeller.values()) {
            if(value.id==idOperation)
                return value;
        }
        throw new IncorrectIdRuntimeException();
    }
}
