package connection.channel;

import lombok.Getter;
import lombok.Setter;
import modules.OperationInvestor;
import modules.OperationSeller;
import modules.User;
import modules.interfaces.RobotSeller;
import modules.robot.*;
import service.operate.*;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Setter
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
    private static final int MAX_RANGE_OTHER_OPERATION = 5;
    @Setter
    @Getter
    private static List<User> nickList = new ArrayList<>();
    private static final List<Double> values = new ArrayList<>(List.of(RobotSeller.BOOK_SELLER_COST_RATE, RobotSeller.BOARD_GAMES_SELLER_COST_RATE,
            RobotSeller.COMPUTER_GAMES_SELLER_COST_RATE, RobotSeller.HOUSES_SELLER_COST_RATE));
    @Getter
    @Setter
    private static int isChannelOpen;
    private User user;
    private final SocketChannel clientChannel;
    private Selector selector;
    private SelectionKey key;
    private SelectionKey previousKey;


    public ServerClientHandler(SocketChannel clientChannel) {
        this.clientChannel = clientChannel;
    }

    public void handleClient(SelectionKey previousKey, SelectionKey key, Selector selector, List<User> nickList) throws IOException {
        this.selector = selector;
        this.key = key;
        this.previousKey = previousKey;
        this.nickList = nickList;
        UserMenager.setUserList(nickList);
        configureUser();
        choiceTypeOperation();
    }

    public void sendMessage(String messageToClient) throws IOException {
        selector.select();
        if (key.isWritable()) {
            ByteBuffer buffer = ByteBuffer.allocate(256);

            buffer.put(("WITH_REPLY|"+messageToClient+"|").getBytes());
            buffer.flip();

            clientChannel.write(buffer);

            key.interestOps(SelectionKey.OP_READ);
        }
    }

    public void sendMessageWithoutResponse(String messageToClient) throws IOException {
        selector.select();
        if (key.isWritable()) {
            ByteBuffer buffer = ByteBuffer.allocate(256);

            buffer.put(("WITHOUT_REPLY|"+messageToClient+"|").getBytes());
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

    private int onlyReceiveCorrectResponseRange(int minRange, int maxRange) throws IOException {
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

    private void saveAllUsers() throws IOException {
        User.saveUser(nickList, "userRecords.txt");
    }

    public void choiceTypeOperation() throws IOException {
        isChannelOpen = 1;
        int choiceUser = sendOperationTypeChoice();
        sendMessageWithoutResponse("Uzytkownik wybral operacje numer "+choiceUser);
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
                break;
        }
    }

    public void investorOperationChoice() throws IOException {
        sendInvestorOperationChoice();
        int userChoice;
        boolean isDone=true;
        int investorOperationChoice = receiveCorrectResponseRange(MIN_RANGE_INVESTOR_OPERATION, MAX_RANGE_INVESTOR_OPERATION);
        switch (investorOperationChoice) {
            case 1:
                userChoice = receiveCorrectResponseList(InvestorMenager.returnIdsList());
                sendShowInvestor(userChoice);
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
        sendSellerOperationChoice();
        int userChoice;
        boolean isDone=true;
        int sellerOperationChoice = receiveCorrectResponseRange(MIN_RANGE_SELLER_OPERATION, MAX_RANGE_SELLER_OPERATION);
        switch (sellerOperationChoice) {
            case 1:
                userChoice = receiveCorrectResponseList(SellerMenager.returnIdsList());
                sendShowSeller(userChoice);
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
        sendBuyOperationChoice();
        int buyOperationChoice = receiveCorrectResponseRange(MIN_RANGE_BUY_OPERATION, MAX_RANGE_BUY_OPERATION);
        boolean isDone = true;
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
        sendOtherOperationChoice();
        boolean isDone = true;
        boolean isMachineUnlocked;
        int otherOperationChoice = receiveCorrectResponseRange(MIN_RANGE_OTHER_OPERATION, MAX_RANGE_OTHER_OPERATION);
        switch (otherOperationChoice) {
            case 1:
                sendGoldAmount();
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
            case 5:
                OperationMenager.clearUserList();
                operationRealization(isDone);
                break;
            default:
                throw new RuntimeException("Incorrect number");
        }
    }

    public int sendOperationTypeChoice() throws IOException{
        sendMessage(("\n"+user.getNick() + ", Operation:" +
                "\n1. Investor" +
                "\n2. Seller" +
                "\n3. Buy" +
                "\n4. Other" +
                "\n5. Close connection"+
                "\nWybor Uzytkownika: "));
        return onlyReceiveCorrectResponseRange(MIN_RANGE_TYPE_OPERATION, MAX_RANGE_TYPE_OPERATION);
    }

    public void sendInvestorOperationChoice() throws IOException{
        sendMessageWithoutResponse("Operation for investor:" +
                "\n1. Display investor stats" +
                "\n2. Invest gold " +
                "\n3. Upgrade investor - cost - " + OperationInvestor.UPGRADE.getCost() +
                "\n4. Sell investor - value - " + -1 * OperationInvestor.SELL.getCost());
    }

    public void sendSellerOperationChoice() throws IOException{
        sendMessageWithoutResponse("Operation for seller:" +
                "\n1. Display seller stats" +
                "\n2. Earn gold" +
                "\n3. Upgrade seller (basic cost - " + OperationSeller.UPGRADE.getCost() + ", rate" + values + ") - " +
                "\n4. Sell seller (baisc value - " + -1 * SellerMenager.countSellValue(Optional.of(SellerBooks.class)) + ", rate" + values + ") - ");
    }

    public void sendBuyOperationChoice() throws IOException{
        sendMessageWithoutResponse("Buy something:" +
                "\n1. Buy investor - cost - " + InvestorMenager.countBuyCost() +
                "\n2. Buy book seller - cost - " + SellerMenager.countBuyCost(SellerBooks.class) +
                "\n3. Buy board games seller - cost - " + SellerMenager.countBuyCost(SellerBoardGames.class) +
                "\n4. Buy computer games seller - cost - " + SellerMenager.countBuyCost(SellerComputerGames.class) +
                "\n5. Buy house seller - cost - " + SellerMenager.countBuyCost(SellerHouses.class) +
                "\n6. Buy Machine - cost - " + Machine.getMACHINE_COST());
    }

    public void sendOtherOperationChoice() throws IOException{
        sendMessageWithoutResponse("Other operations:" +
                "\n1. Check gold " +
                "\n2. Perform job (cost - 100)" +
                "\n3. Perform investment (cost - 300)" +
                "\n4. Perform job and investment (cost - 500)"+
                "\n5. Clear users list");
    }

    public void sendGoldAmount() throws IOException{
        sendMessageWithoutResponse(user.getNick() + " - Gold: " + user.getGold());
    }

    public void sendShowInvestor(int idInv) throws IOException{
        Investor thisInvestor = InvestorMenager.findInvestorById(idInv);
        String stats = InvestorMenager.getStatisticsString(idInv);
        String[] statsParts = stats.split("\\|");
        sendMessageWithoutResponse("------------------INVESTOR----------------"+
                "\nID - " + thisInvestor.getInvId() +
                "\nRarity - " + thisInvestor.getRarity() +
                "\nLevel - " + thisInvestor.getLevel()+
                "\n"+statsParts[0]+
                "\n"+statsParts[1]+
                "\n"+statsParts[2]+
                "\n"+statsParts[3]+
                "\n----------------------------------------");
    }

    public void sendShowSeller(int idSel) throws IOException{
        AbstractSeller thisSeller = SellerMenager.findSellerById(idSel).get();
        String stats = SellerMenager.getSellerStats(idSel);
        String[] statsParts = stats.split("\\|");
        String sellerClass = SellerMenager.getSellerClass(thisSeller);
        sendMessageWithoutResponse("------------------"+sellerClass+"----------------"+
                "\nID - " + thisSeller.getSellerId() +
                "\nRarity - " + thisSeller.getRarity() +
                "\nLevel - " + thisSeller.getLevel()+
                "\n"+statsParts[0]+
                "\n"+statsParts[1]+
                "\n"+statsParts[2]+
                "\n"+statsParts[3]+
                "\n----------------------------------------");
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

    private void displayUsersInformations() throws IOException {
        if(isChannelOpen!=1) {
            String nickString="";
            for (User user : nickList) {
                nickString+="\n"+user;
            }
            sendMessageWithoutResponse("Ilosc uzytkownikow zarejestrowanych - " + nickList.size()+
                    "\nNicki zarejestrowanych uzytkownikow:"+nickString);
        }
    }

    private void configureUser() throws IOException {
        if(ifNewUser()){
            displayUsersInformations();
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
        isChannelOpen=1;
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
        sendMessage("\nPodaj nick uzytkownika: ");
        return receiveResponse();
    }

    private boolean ifNewUser(){
        return !previousKey.equals(key);
    }

    private boolean ifNewNick(String nick){
        return !nickList.toString().contains(nick);
    }
}