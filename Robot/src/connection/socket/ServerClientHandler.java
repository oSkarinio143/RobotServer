package connection.socket;

import lombok.Getter;
import modules.robot.AbstractSeller;
import modules.robot.Investor;
import service.operate.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Getter
public class ServerClientHandler extends Thread{
    private final Socket clientSocket;
    private ServerClientHandlerHelper helper;
    private PrintWriter toClient;
    private BufferedReader fromClient;
    private static final int MIN_RANGE_TYPE_OPERATION = 1;
    private static final int MAX_RANGE_TYPE_OPERATION = 5;
    private static final int MIN_RANGE_INVESTOR_OPERATION = 1;
    private static final int MAX_RANGE_INVESTOR_OPERATION = 4;
    private static final int MIN_RANGE_SELLER_OPERATION = 1;
    private static final int MAX_RANGE_SELLER_OPERATION = 4;
    private static final int MIN_RANGE_BUY_OPERATION = 1;
    private static final int MAX_RANGE_BUY_OPERATION = 6;
    private static final int MIN_RANGE_OTHER_OPERATION = 1;
    private static final int MAX_RANGE_OTHER_OPERATION = 4;

    public ServerClientHandler(Socket socket){
        this.clientSocket = socket;
    }

    @Override
    public void run(){
        try{
            initalRunOperations();
            communicationContinueTree();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                clientSocket.close();
                System.out.println("Serwer watek zakonczyl obsluge clienta");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void initalRunOperations() throws IOException {
        toClient = new PrintWriter(clientSocket.getOutputStream(),true);
        fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        helper = new ServerClientHandlerHelper();
        helper.setWriterReader(toClient, fromClient);
    }

    public void communicationContinueTree() throws IOException {
        OperationMenager.makeMethodTree();
        while(choiceOperationTree());
    }

    public boolean choiceOperationTree() throws IOException {
        return helper.choiceOperationTree();
    }

    public void communicationContinue() throws IOException {
        int whetherContinue;
        do{
            whetherContinue = choiceTypeOperation();
        }while(whetherContinue==1);
    }

    public int choiceTypeOperation() throws IOException {
        OperationMenager.displayOperations();
        int choiceUser = helper.giveOperation(MIN_RANGE_TYPE_OPERATION, MAX_RANGE_TYPE_OPERATION);
        switch (choiceUser){
            case 1:
                investorOperationChoice();
                break;
            case 2:
                sellerOperationChoice();
                break;
            case 3:
                buyOperationChoice();
                break;
            case 4:
                otherOperationChoice();
                break;
            case 5:
                return 0;
        }
        return 1;
    }

    public void investorOperationChoice() throws IOException {
        OperationMenager.displayInvestorOperations();
        int userChoice;
        int investorOperationChoice = helper.giveOperation(MIN_RANGE_INVESTOR_OPERATION, MAX_RANGE_INVESTOR_OPERATION);
        switch (investorOperationChoice){
            case 1:
                userChoice = helper.giveId(Investor.class);
                OperationMenager.showInvestor(userChoice); //P
                break;
            case 2:
                userChoice = helper.giveGold();
                OperationMenager.investGold(userChoice); //P
                break;
            case 3:
                userChoice = helper.giveId(Investor.class);
                OperationMenager.upgradeInvestor(userChoice); //P
                break;
            case 4:
                userChoice = helper.giveId(Investor.class);
                OperationMenager.sellInvestor(userChoice); //P
                break;
        }
    }

    public void sellerOperationChoice() throws IOException {
        OperationMenager.displaySellerOperations();
        int userChoice;
        int sellerOperationChoice = helper.giveOperation(MIN_RANGE_SELLER_OPERATION, MAX_RANGE_SELLER_OPERATION);
        switch (sellerOperationChoice){
            case 1:
                userChoice = helper.giveId(AbstractSeller.class);
                OperationMenager.showSeller(userChoice);
                break;
            case 2:
                OperationMenager.earnGold();
                break;
            case 3:
                userChoice = helper.giveId(AbstractSeller.class);
                OperationMenager.upgradeInvestor(userChoice);
                break;
            case 4:
                userChoice = helper.giveId(AbstractSeller.class);
                OperationMenager.sellSeller(userChoice);
                break;
        }
    }

    public void buyOperationChoice() throws IOException{
        OperationMenager.displayBuyOperations();
        int buyOperationChoice = helper.giveOperation(MIN_RANGE_BUY_OPERATION, MAX_RANGE_BUY_OPERATION);
        switch (buyOperationChoice){
            case 1:
                OperationMenager.buyInvestor();
                break;
            case 2:
                OperationMenager.buyBooksSeller();
                break;
            case 3:
                OperationMenager.buyBoardGamesSeller();
                break;
            case 4:
                OperationMenager.buyComputerGamesSeller();
                break;
            case 5:
                OperationMenager.buyHousesSeller();
                break;
            case 6:
                OperationMenager.buyHousesSeller();
                break;
        }
    }

    public void otherOperationChoice() throws IOException{
        OperationMenager.displayOtherOperations();
        int userChoiceFirst;
        int userChoiceSecond;
        int otherOperationChoice = helper.giveOperation(MIN_RANGE_OTHER_OPERATION, MAX_RANGE_OTHER_OPERATION);
        switch (otherOperationChoice){ //missing deafult clause
            case 1:
                OperationMenager.checkGold();
                break;
            case 2:
                userChoiceFirst = helper.giveAmountExecution();
                OperationMenager.performWork(userChoiceFirst);
                break;
            case 3:
                userChoiceFirst = helper.giveAmountExecution();
                userChoiceSecond = helper.giveGold();
                OperationMenager.performInvestment(userChoiceFirst, userChoiceSecond);
                break;
            case 4:
                userChoiceFirst = helper.giveAmountExecution();
                userChoiceSecond = helper.giveGold();
                OperationMenager.performWorkInvestment(userChoiceFirst, userChoiceSecond);
        }

    }

    /*Podzal Operacji

    //LISTA: LIST <Map <Object>(Method), List<Integer>(DataMethod (Tree, Distinct, Type))

    Distinct:
    Lacznie - 18
    Nie wymagaja podania czegokolwiek - 6 (buy (investor, machine, seller x4)) + 1 (machine work) + 1 (check gold) + 1 (seller work)  (9)
    Wymagaja podania id investor- 1 (sell) + 1 (upgrade) + 1 (display)                                                                (3)
    Wymagaja podania id seller- 1 (sell) + 1 (upgrade) + 1 (display)                                                                  (3)
    Wymagaja podania golda - 1 (invest)                                                                                               (1)
    Wymagaja podania ilosci wykonan - 1(performWork)                                                                                  (1)
    Wymagaja podania golda i ilosci wykonan - 1 (performInvestment) + 1 (perform job and investment)                                  (2)

    STWORZYC METODE KTORA BEDZIE SPRAWDZALA CZY DLA DANEJ METODY PODANA PRZEZ UZYTKOWNIKA LICZBA MIESCI SIE W ZAKRESIE
    (NIE WIEM CZY MOZLIWE) (moze uzyc jakies listy ktora by zawierala wszystkie metody)
     */
}











