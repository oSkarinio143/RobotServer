package connection.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 5001;

    public void connectSerwer(){
        try(Socket socket = new Socket(SERVER_ADDRESS, PORT)){
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            String serverResponse;
            while((serverResponse = fromServer.readLine())!= null) {
                System.out.println(serverResponse);
                System.out.println("Wybor: ");
                String userChoice = userInput.readLine();
                while(userChoice==null || userChoice.trim().isEmpty()) {
                    System.out.println("Podana pusta odpowiedz, sproboj ponownie: ");
                    userChoice=userInput.readLine();
                }
                toServer.println(userChoice);
            }
            }catch (Exception e){
            e.printStackTrace();
        }
    }
}
