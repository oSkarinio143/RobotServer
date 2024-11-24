package connection.channel.clientWithoutBlocking;

import connection.channel.clientBlocking.ServerClientHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

public class Server {
    private static final String SERVER_ADDRESS = "192.168.1.15";
    private static final int PORT = 5003;
    private static ServerSocketChannel serverChannel;
    private static Selector selector;


    public static void startServer() {
        try {
            initialChannelOperations();

            while (true) {
                Iterator<SelectionKey> iter = initialSelectorOperations();
                divideConnection(iter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initialChannelOperations() throws IOException {
        serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(SERVER_ADDRESS, PORT));
        serverChannel.configureBlocking(false);

        selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

    }

    private static Iterator<SelectionKey> initialSelectorOperations() throws IOException {
        selector.select();
        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        return selectedKeys.iterator();
    }

    private static void divideConnection(Iterator<SelectionKey> iter) throws IOException {
        SelectionKey lastKey=null;
        while (iter.hasNext()) {
            iter = initialSelectorOperations();
            SelectionKey key = iter.next();
            if (key.isAcceptable()) {
                connectionAccept(key);
                iter.remove();
            }else {
                displayCommunicate(lastKey, key);
                passClientHandler(key);
            }
            lastKey = key;
            System.out.println(key.channel());
        }
    }

    private static void connectionAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(key.selector(), SelectionKey.OP_WRITE);
    }

    private static void passClientHandler(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        connection.channel.clientBlocking.ServerClientHandler clientHandler = new ServerClientHandler(clientChannel);
        clientHandler.handleClient(key, selector);

        if(clientHandler.getIsChannelOpen()==0){
            //clientHandler.sendMessage("Koniec komunikacji");
            clientChannel.close();
        }
    }

    private static void displayCommunicate(SelectionKey lastKey, SelectionKey actualKey){
        if(!isNewClient(lastKey, actualKey)){
            System.out.println("\nKomunikacja z nowym Clientem :-)");
        }else{
            System.out.println("\nWykonaj kolejna operacje :-)");
        }
    }

    private static boolean isNewClient(SelectionKey lastKey, SelectionKey actualKey){
        boolean isLastKeyNull = Optional.ofNullable(lastKey).isEmpty();
        if(isLastKeyNull) {
            return false;
        }
        if(lastKey.equals(actualKey)){
            return true;
        }
        else{
            return false;
        }
    }
}
