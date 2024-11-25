package connection.channel.clientBlocking;


public class ConnectionMenager {
    private static Thread serverThread;

    private ConnectionMenager(){}

    public static void runConnection(){
        startServer();
        startClient();
        startClient();
    }

    public static void startServer(){
        serverThread = new Thread(Server::startServer);
        serverThread.start();
    }

    public static void startClient(){
        Client newClient = new Client();
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
