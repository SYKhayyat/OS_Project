// Shaul Khayyat T002317
import java.net.*;
import java.io.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class Master {

    public static void main(String[] args) throws IOException {
        // This implements the extra credit and should work for multiple servers and clients.
        BlockingQueue<Job> unfinishedJobs = new LinkedBlockingQueue<>();
        List<SlaveHandler> slaveList = new CopyOnWriteArrayList<>();
        List<ClientHandler> clientList = new CopyOnWriteArrayList<>();

        if (args.length != 1) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

        try {
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
            Thread scheduler = new Thread(new Runnable() {
                // This is a thread that loops through the unfinished jobs and assigns them.
                @Override
                public void run() {
                    while(true){
                        try {
                            Job job = unfinishedJobs.take();
                            // Take a job.
                            while(slaveList.isEmpty()){
                                Thread.sleep(100);
                                // Wait until there is a slave to send it to.
                            }
                            System.out.println("[Scheduler] Current slaves: " + slaveList.size());
                            for (SlaveHandler sh : slaveList) {
                                System.out.println("  Slave type " + sh.getType() + ", remainingTime=" + sh.getRemainingTime());
                            }
                            long[] times = new long[slaveList.size()];
                            // This will hold the amount of time each slave has left.
                            for (int i = 0; i < times.length; i++) {
                                SlaveHandler sh = slaveList.get(i);
                                long jobTime = job.getType() == sh.getType() ? 2000 : 10000;
                                times[i] = sh.getRemainingTime() + jobTime;
                                // Fills the array with time it will take to finish all jobs and the current job.
                            }
                            int min = 0;
                            long minTime = Long.MAX_VALUE;
                            for (int i = 0; i < times.length; i++) {
                                if (times[i] < minTime){
                                    min = i;
                                    minTime = times[i];
                                }
                                // Find the slave that will finish first with this job.
                            }
                            SlaveHandler sh = slaveList.get(min);
                            assert sh != null;
                            sh.addToQueue(job);
                            System.out.println("Sent job " + job.getPid() + " from " + job.getSender() + " to Slave " + sh.getType());
                            // Send the job to the slave.
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }
            });
            scheduler.start();
            Thread acceptingThread = new Thread(new Runnable() {
                // This thread will accept connections and hand them off to the appropriate objects.
                @Override
                public void run() {
                    while (true) {
                        try {
                            Socket clientSocket = serverSocket.accept();
                            InputStream is = clientSocket.getInputStream();
                            ObjectInputStream ois = new ObjectInputStream(is);
                            OutputStream os = clientSocket.getOutputStream();
                            ObjectOutputStream oos = new ObjectOutputStream(os);
                            String s = (String) ois.readObject();
                            if (s.equals("SLAVE")) {
                                System.out.println("[Master] Slave added.    " + slaveList.size());
                                // Recognizes that this is a slave calling, and so it makes a slaveHandler object.
                                SlaveHandler sh = new SlaveHandler(ois, oos, clientList, (Integer) ois.readObject());
                                slaveList.add(sh);
                                // Add this slave to our slave list.
                                sh.start();
                            }
                            if (s.equals("CLIENT")) {
                                System.out.println("Client connected.    " + clientList.size());
                                // Recognizes that this is a client calling, and so it makes a clientHandler object.
                                ClientHandler ch = new ClientHandler(ois, oos, slaveList, unfinishedJobs, (Integer) ois.readObject());
                                clientList.add(ch);
                                // Add this client to our client list.
                                ch.start();
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
            acceptingThread.start(); // not run().
            acceptingThread.join(); // wait until it ends (it won't, but this keeps the main thread alive)
        } catch (Exception e){
            System.err.println("[Master] Fatal error: " + e.getMessage());
            e.printStackTrace();
        }

}}

