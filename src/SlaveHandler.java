import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SlaveHandler {
    private int type;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private BlockingQueue<Job> unfinishedJobs;
    List<ClientHandler> clientList;
    long remainingTime = 0;
    public SlaveHandler(ObjectInputStream ois, ObjectOutputStream oos, List<ClientHandler> clientList, Integer type){
        this.ois = ois;
        this.oos = oos;
        unfinishedJobs = new LinkedBlockingQueue<>();
        // This is the queue which will hold the jobs it needs to do; it will get jobs from the scheduler.
        this.clientList = clientList;
        this.type = type;
    }
    public void start(){
        Thread slaveReader = new Thread(new Runnable() {
            // This thread reads finished jobs from the slave.
            @Override
            public void run() {
                while (true) {
                    try {
                        Job job = (Job) ois.readObject();
                        // get the job
                        subtractRemainingTime(job);
                        // change the amount of time until this slave is free.
                        int id = job.getSender();
                        for (ClientHandler ch : clientList){
                            if (ch.getClientID() == id){
                                // send to the right client.
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
            // This thread sends jobs to the slave.
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
