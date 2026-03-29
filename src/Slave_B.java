// Shaul Khayyat T002317
import java.io.*;
import java.net.*;

public class Slave_B  {
    static final int ID = 1;

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
                Socket clientCommSock = new Socket(hostName, portNumber);
                OutputStream os = clientCommSock.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(os);
                InputStream is = clientCommSock.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(is)
        ) {
            oos.writeObject("SLAVE");
            oos.writeObject(ID);
            while(true){
                Job job = (Job) ois.readObject();
                if (job.getType() == ID){
                    System.out.println("Starting optimal job...");
                    Thread.sleep(2000);
                    System.out.println("Finished " + job.getPid() + ":");
                    System.out.println("Optimal - 2 sec. sleep.");
                } else {
                    System.out.println("Starting non-optimal job...");
                    Thread.sleep(10000);
                    System.out.println("Finished " + job.getPid() + ":");
                    System.out.println("Non-optimal - 10 sec. sleep.");
                }
                oos.writeObject(job);
                oos.flush();
            }
        } catch(UnknownHostException e){
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch(IOException e){
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        } catch (ClassNotFoundException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public int getType(){
        return ID;
    }
}
