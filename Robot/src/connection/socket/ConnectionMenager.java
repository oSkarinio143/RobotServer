package connection.socket;

public class ConnectionMenager {
    private static Server server;
    private static Thread serverThread;

    public static void runConnection(){
        startServer();
        startClient();
    }

    public static void startServer(){
        server = Server.getInstance();
        serverThread = new Thread(() -> server.startServer());
        serverThread.start();
    }

    public static void startClient(){
        Client client = new Client();
        new Thread(client::connectSerwer).start();
    }

    public void stopServer(){
        if (server != null){
            server.stopSerwer();
        }
    }

    public boolean isServerRunning(){
        return serverThread != null && serverThread.isAlive();
    }
}
