import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientHandler {
    private Socket clientCommSocket = null;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private BlockingQueue<Job> finishedJobs;
    private int clientID;
    List<SlaveHandler> slaveList;
    private BlockingQueue<Job> unfinishedJobs;

    public ClientHandler(Socket clientSocket, ObjectInputStream ois, ObjectOutputStream oos, List<SlaveHandler> slaveList, BlockingQueue<Job> unfinishedJobs, Integer integer){
        this.clientCommSocket = clientSocket;
        this.ois = ois;
        this.oos = oos;
        finishedJobs = new LinkedBlockingQueue<>();
        this.slaveList = slaveList;
        this.unfinishedJobs = unfinishedJobs;
        clientID = integer;
    }

    public void start(){
        Thread clientReader = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Job job = (Job) ois.readObject();
                        unfinishedJobs.add(job);
                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        Thread clientWriter = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    Job job = null;
                    try {
                        job = finishedJobs.take();
                        oos.writeObject(job);
                        oos.flush();
                    } catch (InterruptedException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        clientWriter.start();
        clientReader.start();
    }
    public int getClientID(){
        return clientID;
    }
    public synchronized void addToQueue(Job j){
        finishedJobs.add(j);
    }
}
