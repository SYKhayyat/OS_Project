// Shaul Khayyat T002317
import java.io.*;
import java.net.*;
import java.sql.SQLOutput;
import java.util.Random;

public class ClientOne {
    public static void main(String[] args) throws IOException{
        final int ID = 1;

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
                Socket clientCommSock = new Socket(hostName, portNumber);
                OutputStream os = clientCommSock.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(os);
                InputStream is = clientCommSock.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(is)
        ) {
            System.out.println("This is Client 1, sending jobs.");
            oos.writeObject("CLIENT");
            oos.writeObject(ID);
            Thread writer = new Thread(new Runnable() {
                @Override
                public void run() {
                    Random rand = new Random();
                    int r;
                    for (int i = 0; i < 100; i++) {
                        r = rand.nextInt(0, 2);
                        if (rand.nextInt(0, 2) == 1){
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        Job job = new Job(r, "1-" + i);
                        job.setClient(ID);
                        try {
                            oos.writeObject(job);
                            oos.flush();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
            writer.start();
            Thread reader = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        try {
                            Job job = (Job) ois.readObject();
                            System.out.println("Job " + job.getPid() + " completed.");
                        } catch (EOFException e) {
                            break;
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
            reader.start();

            writer.join();
            reader.join();
        } catch(UnknownHostException e){
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch(IOException e){
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
