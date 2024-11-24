package connection.channel.clientBlocking;

import lombok.Getter;
import modules.User;
import service.operate.InvestorMenager;
import service.operate.OperationMenager;
import service.operate.SellerMenager;
import service.operate.UniwersalMenager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
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
    private User user = User.getInstance();
    private SocketChannel clientChannel;
    private SelectionKey key;
    private Selector selector;
    private int isChannelOpen = 1;

    public ServerClientHandler(SocketChannel clientChannel) {
        this.clientChannel = clientChannel;
    }

    public void handleClient(SelectionKey key, Selector selector) throws IOException {
        this.selector = selector;
        this.key = key;
        choiceTypeOperation();
    }

    public void sendMessage(String messageToClient) throws IOException {
        selector.select();
        if (key.isWritable()) {
            ByteBuffer buffer = ByteBuffer.allocate(256);

            buffer.put(messageToClient.getBytes());
            buffer.flip();

            clientChannel.write(buffer);

            key.interestOps(SelectionKey.OP_READ);
        }
    }

    private String receiveResponse() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(256);

        while (true) { // Pętla czekająca na dane od konkretnego klienta
        selector.select();
        Set<SelectionKey> temporaryKeys = selector.selectedKeys();

        for (SelectionKey readyKey : temporaryKeys) {
            if (readyKey == this.key && readyKey.isReadable()) { // Obsługujemy tylko właściwego klienta
                isConnectionOpen(buffer);

                buffer.flip();
                key.interestOps(SelectionKey.OP_WRITE);
                temporaryKeys.remove(key); // Usuwamy klucze, żeby selector nie zapętlał ich obsługi
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
            default:
                isChannelOpen = 0;
        }
    }

    public void investorOperationChoice() throws IOException {
        OperationMenager.displayInvestorOperations();
        int userChoice;
        int investorOperationChoice = receiveCorrectResponseRange(MIN_RANGE_INVESTOR_OPERATION, MAX_RANGE_INVESTOR_OPERATION);
        System.out.println("Wybor inwestora operacji: "+investorOperationChoice);
        switch (investorOperationChoice) {
            case 1:
                if(isOperationPossible(InvestorMenager.returnIdsList())) {
                    userChoice = receiveCorrectResponseList(InvestorMenager.returnIdsList());
                    OperationMenager.showInvestor(userChoice);
                }
                break;
            case 2:
                if(isOperationPossible(InvestorMenager.returnIdsList())) {
                    userChoice = receiveCorrectResponseRange(0, (int) user.getGold());
                    OperationMenager.investGold(userChoice);
                    operationRealizated();
                }
                break;
            case 3:
                if(isOperationPossible(InvestorMenager.returnIdsList())) {
                    userChoice = receiveCorrectResponseList(InvestorMenager.returnIdsList());
                    OperationMenager.upgradeInvestor(userChoice);
                    operationRealizated();
                }
                break;
            case 4:
                if(isOperationPossible(InvestorMenager.returnIdsList())) {
                    userChoice = receiveCorrectResponseList(InvestorMenager.returnIdsList());
                    OperationMenager.sellInvestor(userChoice);
                    operationRealizated();
                }
                break;
            default:
                throw new RuntimeException("Bad number");
        }
    }

    public void sellerOperationChoice() throws IOException{
        OperationMenager.displaySellerOperations();
        int userChoice;
        int sellerOperationChoice = receiveCorrectResponseRange(MIN_RANGE_SELLER_OPERATION, MAX_RANGE_SELLER_OPERATION);
        System.out.println("Wybor sellera operacji: "+sellerOperationChoice);
        switch (sellerOperationChoice) {
            case 1:
                if(isOperationPossible(SellerMenager.returnIdsList())) {
                    userChoice = receiveCorrectResponseList(SellerMenager.returnIdsList());
                    OperationMenager.showSeller(userChoice);
                }
                break;
            case 2:
                if(isOperationPossible(SellerMenager.returnIdsList())) {
                    OperationMenager.earnGold();
                    operationRealizated();
                }
                break;
            case 3:
                if(isOperationPossible(SellerMenager.returnIdsList())) {
                    userChoice = receiveCorrectResponseList(SellerMenager.returnIdsList());
                    OperationMenager.upgradeSeller(userChoice);
                    operationRealizated();
                }
                break;
            case 4:
                if(isOperationPossible(SellerMenager.returnIdsList())) {
                    userChoice = receiveCorrectResponseList(SellerMenager.returnIdsList());
                    OperationMenager.sellSeller(userChoice);
                    operationRealizated();
                }
                break;
            default:
                throw new RuntimeException("Bad number");
        }
    }

    public void buyOperationChoice() throws IOException{
        OperationMenager.displayBuyOperations();
        int buyOperationChoice = receiveCorrectResponseRange(MIN_RANGE_BUY_OPERATION, MAX_RANGE_BUY_OPERATION);
        System.out.println("Wybor buy operacji: "+buyOperationChoice);
        switch (buyOperationChoice) {
            case 1:
                OperationMenager.buyInvestor();
                operationRealizated();
                break;
            case 2:
                OperationMenager.buyBooksSeller();
                operationRealizated();
                break;
            case 3:
                OperationMenager.buyBoardGamesSeller();
                operationRealizated();
                break;
            case 4:
                OperationMenager.buyComputerGamesSeller();
                operationRealizated();
                break;
            case 5:
                OperationMenager.buyHousesSeller();
                operationRealizated();
                break;
            case 6:
                OperationMenager.buyMachine();
                operationRealizated();
                break;
            default:
                throw new RuntimeException("Bad number");
        }
    }

    public void otherOperationChoice() throws IOException{
        OperationMenager.displayOtherOperations();
        int timesChoice;
        int goldChoice;
        int otherOperationChoice = receiveCorrectResponseRange(MIN_RANGE_OTHER_OPERATION, MAX_RANGE_OTHER_OPERATION);
        System.out.println("Wybor other operacji: "+otherOperationChoice);
        switch (otherOperationChoice) {
            case 1:
                OperationMenager.checkGold();
                break;
            case 2:
                timesChoice = receiveCorrectResponseRange(1,100);
                OperationMenager.performWork(timesChoice);
                operationRealizated();
                break;
            case 3:
                timesChoice = receiveCorrectResponseRange(1,100);
                goldChoice = receiveCorrectResponseRange(0, (int) user.getGold());
                OperationMenager.performInvestment(timesChoice, goldChoice);
                operationRealizated();
                break;
            case 4:
                timesChoice = receiveCorrectResponseRange(1,100);
                goldChoice = receiveCorrectResponseRange(0, (int) user.getGold());
                OperationMenager.performWorkInvestment(timesChoice, goldChoice);
                operationRealizated();
                break;
            default:
                throw new RuntimeException("Bad number");
        }
    }

    public boolean isOperationPossible(List <Integer> integersList) throws IOException{
        if(integersList.size()>0){
            return true;
        }else{
            System.out.println("Operacja niemozliwa do zrealizowania z powodu braku elementow na ktorych mozna ja zrealizowac");
            return false;
        }
    }

    public void operationRealizated(){
        System.out.println("Operacja zostala zrealizowana pomyslnie");
    }
}