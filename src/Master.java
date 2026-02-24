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

        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
             Socket clientSocket1 = serverSocket.accept();
             PrintWriter responseWriter1 = new PrintWriter(clientSocket1.getOutputStream(), true);
             BufferedReader requestReader1 = new BufferedReader(new InputStreamReader(clientSocket1.getInputStream()));
        ) {
            String usersRequest;
            while ((usersRequest = requestReader1.readLine()) != null) {
                responseWriter1.println("Done");
            }
        } catch (IOException e) {
            System.out.println(
                    "Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }

    }
}
