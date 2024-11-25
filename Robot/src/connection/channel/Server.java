package connection.channel;

import lombok.Getter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.*;

public class Server {
    private static final String SERVER_ADDRESS = "192.168.1.15";
    private static final int PORT = 5002;
    private static ServerSocketChannel serverChannel;
    private static Selector selector;
    private static SelectionKey previousKey;

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

    private static void divideConnection(Iterator<SelectionKey> iter) throws IOException {
        while (iter.hasNext()) {
            iter = initialSelectorOperations();
            iter.next();
            SelectionKey key = returnKeyInOrder();
            if (key.isAcceptable()) {
                connectionAccept(key);
                iter.remove();
            }else {
                passClientHandler(key);
            }
            previousKey = key;
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
        ServerClientHandler clientHandler = new ServerClientHandler(clientChannel);
        clientHandler.handleClient(previousKey, key, selector);

        if(clientHandler.getIsChannelOpen()==0){
            clientChannel.close();
        }
    }

    public static SelectionKey returnKeyInOrder() throws IOException {
        selector.select();
        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        for (SelectionKey selectedKey : selectedKeys) {
            if(!selectedKey.isAcceptable()){
                return selectedKey;
            }
        }
        return selectedKeys.stream().findFirst().get();
    }
}
