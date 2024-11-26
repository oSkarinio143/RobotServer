package connection.channel;

import lombok.Getter;
import modules.User;
import service.operate.*;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
public class ServerClientHandler {
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
    private static List<User> nickList = new ArrayList<>();
    private User user;
    private SocketChannel clientChannel;
    private Selector selector;
    private SelectionKey key;
    private SelectionKey previousKey;
    private int isChannelOpen = 1;

    public ServerClientHandler(SocketChannel clientChannel) {
        this.clientChannel = clientChannel;
    }

    public void handleClient(SelectionKey previousKey, SelectionKey key, Selector selector, List<User> nickList) throws IOException {
        this.selector = selector;
        this.key = key;
        this.previousKey = previousKey;
        this.nickList = nickList;
        UserMenager.setUserList(nickList);
        displayUsersInformations();
        configureUser();
        choiceTypeOperation();
    }

    public void sendMessage(String messageToClient) throws IOException {
        selector.select();
        if (key.isWritable()) {
            ByteBuffer buffer = ByteBuffer.allocate(256);

            buffer.put(("WITH_REPLY|"+messageToClient).getBytes());
            buffer.flip();

            clientChannel.write(buffer);

            key.interestOps(SelectionKey.OP_READ);
        }
    }

    public void sendMessageWithoutResponse(String messageToClient) throws IOException {
        selector.select();
        if (key.isWritable()) {
            ByteBuffer buffer = ByteBuffer.allocate(256);

            buffer.put(("WITHOUT_REPLY|"+messageToClient).getBytes());
            buffer.flip();

            clientChannel.write(buffer);
        }
    }

    private String receiveResponse() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(256);

        while (true) {
        selector.select();
        Set<SelectionKey> temporaryKeys = selector.selectedKeys();

        for (SelectionKey readyKey : temporaryKeys) {
            if (readyKey == this.key && readyKey.isReadable()) {
                isConnectionOpen(buffer);

                buffer.flip();
                key.interestOps(SelectionKey.OP_WRITE);
                temporaryKeys.remove(key);
                return new String(buffer.array(), 0, buffer.limit());
            }
        }
        }
    }

    private int receiveCorrectResponseRange(int minRange, int maxRange) throws IOException {
        sendMessage("Wybor:");
        ByteBuffer buffer = ByteBuffer.allocate(256);
        String firstResponse = receiveResponse();
            while (!UniwersalMenager.checkStringIntRange(firstResponse, minRange, maxRange)) {
                sendMessage("Niepoprawna wartosc, wybor: ");
                firstResponse = receiveResponse();
            }
        return UniwersalMenager.stringToInteger(firstResponse);
    }

    private int receiveCorrectResponseList(List<Integer> list) throws IOException {
        sendMessage("Wybor: " + list);
        String firstResponse = receiveResponse();
        while (!UniwersalMenager.checkStringIntList(firstResponse, list)) {
            sendMessage("Niepoprawna wartosc, wybor: " + list);
            firstResponse = receiveResponse();
        }
        return UniwersalMenager.stringToInteger(firstResponse);
    }

    public void isConnectionOpen(ByteBuffer buffer) throws IOException {
        int bytesRead = clientChannel.read(buffer);
        if (bytesRead == -1) {
            clientChannel.close();
        }
    }

    public void choiceTypeOperation() throws IOException {
        OperationMenager.displayOperations();
        int choiceUser = receiveCorrectResponseRange(MIN_RANGE_TYPE_OPERATION, MAX_RANGE_TYPE_OPERATION);
        System.out.println("Wybor operacji: "+choiceUser);
        switch (choiceUser) {
            case 1:
                doInvestorOperationIfPossible();
                break;
            case 2:
                doSellerOperationIfPossible();
                break;
            case 3:
                buyOperationChoice();
                break;
            case 4:
                otherOperationChoice();
                break;
            default:
                isChannelOpen = 0;
                saveAllUsers();

        }
    }

    private void saveAllUsers() throws IOException {
        User.saveUser(nickList, "userRecords.txt");
    }

    public void investorOperationChoice() throws IOException {
        OperationMenager.displayInvestorOperations();
        int userChoice;
        boolean isDone=true;
        int investorOperationChoice = receiveCorrectResponseRange(MIN_RANGE_INVESTOR_OPERATION, MAX_RANGE_INVESTOR_OPERATION);
        System.out.println("Wybor inwestora operacji: "+investorOperationChoice);
        switch (investorOperationChoice) {
            case 1:
                userChoice = receiveCorrectResponseList(InvestorMenager.returnIdsList());
                OperationMenager.showInvestor(userChoice);
                operationRealization(isDone);
                break;
            case 2:
                userChoice = receiveCorrectResponseRange(0, (int) user.getGold());
                isDone = OperationMenager.investGold(userChoice);
                operationRealization(isDone);
                break;
            case 3:
                userChoice = receiveCorrectResponseList(InvestorMenager.returnIdsList());
                isDone = OperationMenager.upgradeInvestor(userChoice);
                operationRealization(isDone);
                break;
            case 4:
                userChoice = receiveCorrectResponseList(InvestorMenager.returnIdsList());
                OperationMenager.sellInvestor(userChoice);
                operationRealization(isDone);
                break;
            default:
                throw new RuntimeException("Incorrect number");
        }
    }

    public void sellerOperationChoice() throws IOException{
        OperationMenager.displaySellerOperations();
        int userChoice;
        boolean isDone=true;
        int sellerOperationChoice = receiveCorrectResponseRange(MIN_RANGE_SELLER_OPERATION, MAX_RANGE_SELLER_OPERATION);
        System.out.println("Wybor sellera operacji: "+sellerOperationChoice);
        switch (sellerOperationChoice) {
            case 1:
                userChoice = receiveCorrectResponseList(SellerMenager.returnIdsList());
                OperationMenager.showSeller(userChoice);
                operationRealization(isDone);
                break;
            case 2:
                OperationMenager.earnGold();
                operationRealization(isDone);
                break;
            case 3:
                userChoice = receiveCorrectResponseList(SellerMenager.returnIdsList());
                isDone = OperationMenager.upgradeSeller(userChoice);
                operationRealization(isDone);
                break;
            case 4:
                userChoice = receiveCorrectResponseList(SellerMenager.returnIdsList());
                OperationMenager.sellSeller(userChoice);
                operationRealization(isDone);
                break;
            default:
                throw new RuntimeException("Incorrect number");
        }
    }

    public void buyOperationChoice() throws IOException{
        OperationMenager.displayBuyOperations();
        int buyOperationChoice = receiveCorrectResponseRange(MIN_RANGE_BUY_OPERATION, MAX_RANGE_BUY_OPERATION);
        boolean isDone = true;
        System.out.println("Wybor buy operacji: "+buyOperationChoice);
        switch (buyOperationChoice) {
            case 1:
                isDone = OperationMenager.buyInvestor();
                operationRealization(isDone);
                break;
            case 2:
                isDone = OperationMenager.buyBooksSeller();
                operationRealization(isDone);
                break;
            case 3:
                isDone = OperationMenager.buyBoardGamesSeller();
                operationRealization(isDone);
                break;
            case 4:
                isDone = OperationMenager.buyComputerGamesSeller();
                operationRealization(isDone);
                break;
            case 5:
                isDone = OperationMenager.buyHousesSeller();
                operationRealization(isDone);
                break;
            case 6:
                isDone = OperationMenager.buyMachine();
                operationRealization(isDone);
                break;
            default:
                throw new RuntimeException("Incorrect number");
        }
    }

    public void otherOperationChoice() throws IOException{
        OperationMenager.displayOtherOperations();
        int timesChoice;
        int goldChoice;
        boolean isDone = true;
        boolean isMachineUnlocked = true;
        int otherOperationChoice = receiveCorrectResponseRange(MIN_RANGE_OTHER_OPERATION, MAX_RANGE_OTHER_OPERATION);
        System.out.println("Wybor other operacji: "+otherOperationChoice);
        switch (otherOperationChoice) {
            case 1:
                OperationMenager.checkGold();
                operationRealization(isDone);
                break;
            case 2:
                isMachineUnlocked = MachineMenager.isMachineUnlocked();
                isDone = ifMachinePerformWork(isMachineUnlocked);
                operationRealization(isDone);
                break;
            case 3:
                isMachineUnlocked = MachineMenager.isMachineUnlocked();
                isDone = ifMachinePerformInvestment(isMachineUnlocked);
                operationRealization(isDone);
                break;
            case 4:
                isMachineUnlocked = MachineMenager.isMachineUnlocked();
                isDone = ifMachinePerformWorkInvestment(isMachineUnlocked);
                operationRealization(isDone);
                break;
            default:
                throw new RuntimeException("Incorrect number");
        }
    }

    public void doInvestorOperationIfPossible() throws IOException {
        if(haveListElements(InvestorMenager.returnIdsList())) {
            investorOperationChoice();
        }else
            sendMessageWithoutResponse("Operacja niemozliwa do zrealizowania, z powodu braku Investorow");
    }

    public void doSellerOperationIfPossible() throws IOException {
        if(haveListElements(SellerMenager.returnIdsList())){
            sellerOperationChoice();
        }else
            sendMessageWithoutResponse("Operacja niemozliwa do zrealizowania z powodu braku Sellerow");
    }

    public boolean ifMachinePerformWork(boolean isMachineUnlocked) throws IOException{
        if(isMachineUnlocked) {
            int timesChoice = receiveCorrectResponseRange(1, 100);
            boolean hasUserMoney = OperationMenager.performWork(timesChoice);
            if(hasUserMoney)
                return true;
            else
                return false;
        }
        else
            return false;
    }

    public boolean ifMachinePerformInvestment(boolean isMachineUnlocked) throws IOException{
        if(isMachineUnlocked) {
            int timesChoice = receiveCorrectResponseRange(1, 100);
            int goldAmount = receiveCorrectResponseRange(0, (int) BalanceMenager.returnGoldAmount());
            boolean hasUserMoney = OperationMenager.performInvestment(timesChoice, goldAmount);
            if(hasUserMoney)
                return true;
            else
                return false;
        }
        else
            return false;
    }

    public boolean ifMachinePerformWorkInvestment(boolean isMachineUnlocked) throws IOException{
        if(isMachineUnlocked) {
            int timesChoice = receiveCorrectResponseRange(1, 100);
            int goldAmount = receiveCorrectResponseRange(0, (int) BalanceMenager.returnGoldAmount());
            boolean hasUserMoney = OperationMenager.performWorkInvestment(timesChoice, goldAmount);
            if(hasUserMoney)
                return true;
            else
                return false;
        }
        else
            return false;
    }

    public boolean haveListElements(List <Integer> integersList) throws IOException{
        if(integersList.size()>0){
            return true;
        }else{
            return false;
        }
    }

    public void operationRealization(boolean isDone) throws IOException {
        if(isDone)
            sendMessageWithoutResponse("Operacja zostala zrealizowana pomyslnie");
        else
            sendMessageWithoutResponse("Operacja nie zostala zrealizowana");
    }

    private void displayUsersInformations(){
        System.out.println("Ilosc uzytkownikow zarejestrowanych - "+nickList.size());
        System.out.println("Nicki zarejestrowanych uzytkownikow:");
        for (User user : nickList) {
            System.out.println(user);
        }
    }

    private void configureUser() throws IOException {
        if(ifNewUser()){
            String nick = getNick();
            if(ifNewNick(nick)) {
                getNewUser(nick);
            }
            else {
                getExistUser(nick);
            }
        }else {
            getSameUser();
        }
        UserMenager.setUserEverywhere();
    }

    public void getNewUser(String nick){
        user = UserMenager.createNewUser(nick);
        //nickList.add(user);
    }

    public void getExistUser(String nick){
        user = UserMenager.findUserByNick(nick);
    }

    public void getSameUser(){
        user = UserMenager.findUserByNick(UserMenager.getActualUserNick());
    }

    public String getNick() throws IOException{
        sendMessage("Podaj nick uzytkownika: ");
        return receiveResponse();
    }

    private boolean ifNewUser(){
        return !previousKey.equals(key);
    }

    private boolean ifNewNick(String nick){
        return !nickList.toString().contains(nick);
    }
}