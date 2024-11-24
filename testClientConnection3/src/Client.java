import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.Set;

public class Client {
    private static final String SERVER_ADDRESS = "192.168.1.15";
    private static final int PORT = 5002;
    private SocketChannel clientChannel;
    Selector selector;
    Scanner scanner = new Scanner(System.in);

    public void connectServer() {
        try {
            initalClientChannel();
            continueCommunication();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initalClientChannel() throws IOException {
        clientChannel = SocketChannel.open(new InetSocketAddress(SERVER_ADDRESS, PORT));
        clientChannel.configureBlocking(false);

        selector = Selector.open();
        clientChannel.register(selector, SelectionKey.OP_READ);
    }

    public void continueCommunication() throws IOException {
        while(true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();

            for(SelectionKey key : selectedKeys){
                if (key.isReadable()) {
                    connectionHandle();
                }
            }
        }
    }

    private void connectionHandle() throws IOException {
        System.out.println(receiveMessage());
        sendMessage();
    }

    public String receiveMessage() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(256);

        isConnectionOpen(buffer);

        buffer.flip();
        return new String(buffer.array(), 0, buffer.limit());
    }

    public void sendMessage() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(256);

        if (clientChannel.isOpen()) {
            String response = scanner.nextLine();

            while(response.trim().isEmpty()){
                System.out.println("Wprowadziles pusta linijke, sproboj jeszcze raz: ");
                response=scanner.nextLine();
            }

            buffer.put(response.getBytes());
            buffer.flip();

            clientChannel.write(buffer);
        }
    }

    public void isConnectionOpen(ByteBuffer buffer) throws IOException {
        int bytesRead = clientChannel.read(buffer);
        if (bytesRead == -1) {
            clientChannel.close();
        }
    }
}

