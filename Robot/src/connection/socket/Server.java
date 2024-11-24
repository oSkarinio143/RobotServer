package connection.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 5001;
    private static Server instance;
    private ServerSocket serverSocket;

    private Server(){}

    public static synchronized Server getInstance(){
        if(instance==null){
            instance = new Server();
        }
        return instance;
    }

    public void startServer(){
        try{
            serverSocket = new ServerSocket(PORT);
            while(true){
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void handleClient(Socket clientSocket){
        //new ServerClientHandler(clientSocket).start();
        ServerClientHandler clientHandler = new ServerClientHandler(clientSocket);
        clientHandler.start();
        try{
            clientHandler.join();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stopSerwer() {
        try{
            if(serverSocket != null && !serverSocket.isClosed()){
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
