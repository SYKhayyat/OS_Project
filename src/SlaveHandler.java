import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SlaveHandler {
    private Socket clientCommSock = null;
    private int type;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private BlockingQueue<Job> unfinishedJobs;
    List<ClientHandler> clientList;
    long remainingTime = 0;
    public SlaveHandler(Socket clientSocket, ObjectInputStream ois, ObjectOutputStream oos, List<ClientHandler> clientList, Integer type){
        this.clientCommSock = clientSocket;
        this.ois = ois;
        this.oos = oos;
        unfinishedJobs = new LinkedBlockingQueue<>();
        this.clientList = clientList;
        this.type = type;
    }
    public void start(){
        Thread slaveReader = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Job job = (Job) ois.readObject();
                        subtractRemainingTime(job);
                        int id = job.getSender();
                        for (ClientHandler ch : clientList){
                            if (ch.getClientID() == id){
                                ch.addToQueue(job);
                            }
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        Thread slaveWriter = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    Job job = null;
                    try {
                        job = unfinishedJobs.take();
                        oos.writeObject(job);
                        oos.flush();
                    } catch (InterruptedException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        slaveWriter.start();
        slaveReader.start();
    }

    private synchronized void subtractRemainingTime(Job job) {
        if (job.getType() == type){
            remainingTime -= 2000;
        } else {
            remainingTime -= 10000;
        }
    }

    public synchronized void addToQueue(Job j){
        unfinishedJobs.add(j);
        addRemainingTime(j);
    }

    private synchronized void addRemainingTime(Job j) {
        if (type == j.getType()){
            remainingTime += 2000;
        } else {
            remainingTime += 10000;
        }
    }

    public int getType(){
        return type;
    }
    public synchronized long getRemainingTime(){
        return remainingTime;
    }
}
