import connection.channel.clientBlocking.ConnectionMenager;

public class Main {
    public static void main(String[] args) {
        //connection.socket.ConnectionMenager.runConnection();
        //connection.channel.clientWithoutBlocking.ConnectionMenager.runConnection();
        connection.channel.clientBlocking.ConnectionMenager.runConnection();
    }
}
