// Shaul Khayyat T002317
import java.net.*;
import java.io.*;

public class Master {

    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

        try {ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
             Thread acceptingThread = new Thread(new Runnable() {
                 @Override
                 public void run() {
                     while (true) {
                         try {
                             Socket ClientSocket = serverSocket.accept();
                         } catch (IOException e) {
                             throw new RuntimeException(e);
                         }
                     }
                 }
             });
             Socket clientSocket1 = serverSocket.accept();
             Socket clientSocket2 = serverSocket.accept();
             Socket clientSocket3 = serverSocket.accept();
             Socket clientSocket4 = serverSocket.accept();
             ObjectInputStream input1 = new ObjectInputStream(clientSocket1.getInputStream());
             ObjectInputStream input2 = new ObjectInputStream(clientSocket2.getInputStream());
             ObjectOutputStream output1 = new ObjectOutputStream(clientSocket1.getOutputStream());
             ObjectOutputStream output2 = new ObjectOutputStream(clientSocket2.getOutputStream());
             ObjectInputStream input3 = new ObjectInputStream(clientSocket3.getInputStream());
             ObjectInputStream input4 = new ObjectInputStream(clientSocket4.getInputStream());
             ObjectOutputStream output3 = new ObjectOutputStream(clientSocket3.getOutputStream());
             ObjectOutputStream output4 = new ObjectOutputStream(clientSocket4.getOutputStream());

//             PrintWriter responseWriter1 = new PrintWriter(clientSocket1.getOutputStream(), true);
//             BufferedReader requestReader1 = new BufferedReader(new InputStreamReader(clientSocket1.getInputStream()));

            Job usersRequest;
            while ((usersRequest = (Job) input1.readObject()) != null) {
                if (usersRequest.getType() == 0){
                    output3.writeObject(usersRequest);
                } else {
                    output4.writeObject(usersRequest);
                }
            }
        } catch (IOException e) {
            System.out.println(
                    "Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

