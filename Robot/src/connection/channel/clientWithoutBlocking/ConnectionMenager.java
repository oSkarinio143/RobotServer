package connection.channel.clientWithoutBlocking;


import connection.channel.clientBlocking.Client;
import connection.channel.clientBlocking.Server;

public class ConnectionMenager {
    private static Thread serverThread;

    private ConnectionMenager(){}

    public static void runConnection(){
        startServer();
        sleep(1000);
        startClient();
        sleep(1000);
        startClient();
    }

    public static void startServer(){
        serverThread = new Thread(connection.channel.clientWithoutBlocking.Server::startServer);
        serverThread.start();
    }

    public static void startClient(){
        connection.channel.clientWithoutBlocking.Client newClient = new connection.channel.clientWithoutBlocking.Client();
        Thread threadClient = new Thread(newClient::connectServer);
        threadClient.start();
    }

    public static void sleep(int time){
        try{
            Thread.sleep(time);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
