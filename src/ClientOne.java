// Shaul Khayyat T002317
import java.io.*;
import java.net.*;
import java.util.Random;

public class ClientOne implements Client {
    public static void main(String[] args) throws IOException{

        // Hardcode in IP and Port here if required
        //args = new String[] {"127.0.0.1", "30121"};

        if (args.length != 2) {
            System.err.println(
                    "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
                Socket clientSocket = new Socket(hostName, portNumber);
                OutputStream os = clientSocket.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(os);
                InputStream is = clientSocket.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(is);
//                BufferedReader stdIn = // standard input stream to get user's requests
//                        new BufferedReader(
//                                new InputStreamReader(System.in))
        ) {
//            String userInput;
//            String serverResponse;
//            userInput = stdIn.readLine();
//            while (userInput != null) {
//                requestWriter.println(userInput);// send request to server
//                userInput = stdIn.readLine();
//            }
            Random rand = new Random();
            int r;
            for (int i = 0; i < 100; i++) {
                r = rand.nextInt(0, 2);
                Job job = new Job(r, "1-" + i);
                job.setClient(1);
                oos.writeObject(job);
            }
        } catch(UnknownHostException e){
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch(IOException e){
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }
}
