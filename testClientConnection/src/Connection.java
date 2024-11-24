public class Connection {
    public static void runConnection(){
        startClient();
    }

    public static void startClient(){
        Client newClient = new Client();
        Thread threadClient = new Thread(newClient::connectServer);
        threadClient.start();
    }
}
