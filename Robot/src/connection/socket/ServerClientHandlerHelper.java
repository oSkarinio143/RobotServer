package connection.socket;

import modules.interfaces.Robot;
import modules.robot.AbstractSeller;
import modules.robot.Investor;
import service.operate.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ServerClientHandlerHelper {
    private PrintWriter toClient;
    private BufferedReader fromClient;

    public void setWriterReader(PrintWriter printWriter, BufferedReader bufferedReader){
        this.toClient = printWriter;
        this.fromClient = bufferedReader;
    }

    public int giveOperation(int minRange, int maxRange) throws IOException {
        String operationChoice = returnChoice("Podaj numer operacji ");
        return returnCorrectInRangeChoice(operationChoice, minRange, maxRange);
    }

    public <T extends Robot> int giveId(Class<T> type) throws IOException{
        if(type== AbstractSeller.class){
            String idChoice = returnChoice("Podaj ID " +SellerMenager.returnIdsList());
            List<Integer> rangeListSeller = SellerMenager.returnIdsList();
            return  returnCorrectInListChoice(idChoice, rangeListSeller);
        }else{
            String idChoice = returnChoice("Podaj ID " +InvestorMenager.returnIdsList());
            List<Integer> rangeListInvestor = InvestorMenager.returnIdsList();
            return returnCorrectInListChoice(idChoice, rangeListInvestor);
        }
    }

    public int giveGold() throws IOException {
        int maxGoldAmount = (int) BalanceMenager.returnGoldAmount();
        String goldAmountChoice = returnChoice("Podaj ilosc golda [0 - "+maxGoldAmount+"]");
        return returnCorrectInRangeChoice(goldAmountChoice, 0, maxGoldAmount);
    }

    public int giveAmountExecution() throws IOException {
        int minAmountExecutions = 1;
        int maxAmountExecutions = 100;
        String amountExecution = returnChoice("Podaj ilość wykonan ["+minAmountExecutions+" - "+maxAmountExecutions+"]");
        return returnCorrectInRangeChoice(amountExecution, minAmountExecutions, maxAmountExecutions);
    }

    public String returnChoice(String textToOutput) throws IOException {
        toClient.println(textToOutput);
        return fromClient.readLine();
    }

    public int returnCorrectInListChoice(String idChoice, List<Integer> idList) throws IOException {
        while(!UniwersalMenager.checkStringIntList(idChoice, idList)){
            idChoice = returnChoice("Liczba nie na liscie, podaj ponownie "+idList);
        }
        return UniwersalMenager.stringToInteger(idChoice);
    }

    public int returnCorrectInRangeChoice(String operationChoice, int minRange, int maxRange) throws IOException{
        while (!UniwersalMenager.checkStringIntRange(operationChoice, minRange, maxRange)){
            operationChoice = returnChoice("Liczba nie miesci sie w zakresie, podaj ponownie ["+minRange+" - "+maxRange+"]");
        }
        return UniwersalMenager.stringToInteger(operationChoice);
    }

    public boolean choiceOperationTree() throws IOException {
        //Metoda niezwykle dluga i skomplikowana, ciekawe czy latwiej by sie pisalo od razu rozdzielajac na mniejsze. hmm...
        List<Map> operationsList = OperationMenager.getOperationsList();
        List<Map<Integer, Integer>> operationsTree = new ArrayList<>(List.of(Map.of(0,0),Map.of(1,4), Map.of(2,4), Map.of(3,6), Map.of(4,4)));
        int minRange=1;

        //Wybor type method
        OperationMenager.displayOperations();
        int userTreeChoice=giveOperation(minRange, operationsTree.size());

        //Sprawdzacz ifUserContinue
        if(userTreeChoice==5)
            return false;

        //Wybor distinct method
        int maxOperationTree = operationsTree.get(userTreeChoice).get(userTreeChoice);
        displayOperationsGroup(userTreeChoice);
        int userMethodChoice = giveOperation(minRange, maxOperationTree);

        //Kazda operacje
        for (int i = 0; i < operationsList.size(); i++) {

            //Dane pojedynczej operacji
            Map<Object, List<Integer>> singleOperation = operationsList.get(i);

            //Operacja podzial na key, value
            for (Map.Entry<Object, List<Integer>> operation : singleOperation.entrySet()) {
                Object operationMethod = operation.getKey();
                List<Integer> operationData = operation.getValue();

                //Drzewo == wybor
                if(operationData.get(0)==userTreeChoice && operationData.get(1)==userMethodChoice) {
                        int typeOperation = operation.getValue().get(2);
                        int userDataChoice = choiceNothingIdGold(typeOperation);

                        if(operationMethod instanceof Runnable runnableOperation){
                            (runnableOperation).run();
                        }

                        if(operationMethod instanceof Consumer){
                            ((Consumer<Integer>) operationMethod).accept(userDataChoice);
                        }

                        if(operationMethod instanceof BiConsumer<?, ?>){
                            BiConsumer<Integer, Integer> operationMethodBi = (BiConsumer<Integer, Integer>)  operationMethod;
                            if(operation.getValue().get(2)==5){
                                int userDataChoiceGold = choiceNothingIdGold(3);
                                int userDataChoiceQuantityExecutions = choiceNothingIdGold(4);
                                operationMethodBi.accept(userDataChoiceQuantityExecutions, userDataChoiceGold);
                            }
                        }
                    }

            }
        }
        return true;
    }

    public int choiceNothingIdGold(int typeOperation) throws IOException {
        switch (typeOperation){
            case 0:
                System.out.println("Nie trzeba nic podac");
                return -1;
            case 1:
                System.out.println("Trzeba podac ID investora");
                return giveId(Investor.class);
            case 2:
                System.out.println("Trzeba podac ID sellera");
                return giveId(AbstractSeller.class);
            case 3:
                System.out.println("Trzeba podac golda");
                return giveGold();
            case 4:
                System.out.println("Trzeba podać ilość wykonań");
                return giveAmountExecution();
            case 5:
                System.out.println("Trzeba podać golda oraz ilość wykonań");
                return -1;
            default:
                return -1;
        }
    }

    public void displayOperationsGroup (int operationGroupNumber){
        switch (operationGroupNumber){
            case 1:
                OperationMenager.displayInvestorOperations();
                break;
            case 2:
                OperationMenager.displaySellerOperations();
                break;
            case 3:
                OperationMenager.displayBuyOperations();
                break;
            case 4:
                OperationMenager.displayOtherOperations();
                break;
        }
    }
}

