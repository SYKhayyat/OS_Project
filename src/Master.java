// Shaul Khayyat T002317
import java.net.*;
import java.io.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class Master {

    public static void main(String[] args) throws IOException {
        BlockingQueue<Job> unfinishedJobsSharedObj = new LinkedBlockingQueue<>();

        List<SlaveHandler> slaveListSharedObj = new CopyOnWriteArrayList<>();
        List<ClientHandler> clientListSharedObj = new CopyOnWriteArrayList<>();

        if (args.length != 1) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

        try {
            ServerSocket serverCommSock = new ServerSocket(Integer.parseInt(args[0]));
            Thread scheduler = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        try {
                            Job job = unfinishedJobsSharedObj.take();
                            while(slaveListSharedObj.isEmpty()){
                                Thread.sleep(100);
                            }
                            System.out.println("[Scheduler] Current slaves: " + slaveListSharedObj.size());
                            for (SlaveHandler sh : slaveListSharedObj) {
                                System.out.println("  Slave type " + sh.getType() + ", remainingTime=" + sh.getRemainingTime());
                            }
                            long[] times = new long[slaveListSharedObj.size()];
                            for (int i = 0; i < times.length; i++) {
                                SlaveHandler sh = slaveListSharedObj.get(i);
                                long jobTime = job.getType() == sh.getType() ? 2000 : 10000;
                                times[i] = sh.getRemainingTime() + jobTime;
                            }
                            int min = 0;
                            long minTime = Long.MAX_VALUE;
                            for (int i = 0; i < times.length; i++) {
                                if (times[i] < minTime){
                                    min = i;
                                    minTime = times[i];
                                }
                            }
                            SlaveHandler sh = slaveListSharedObj.get(min);
                            assert sh != null;
                            sh.addToQueue(job);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }
            });
            scheduler.start();
            Thread acceptingThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Socket clientSocket = serverCommSock.accept();
                            InputStream is = clientSocket.getInputStream();
                            ObjectInputStream ois = new ObjectInputStream(is);
                            OutputStream os = clientSocket.getOutputStream();
                            ObjectOutputStream oos = new ObjectOutputStream(os);
                            String s = (String) ois.readObject();
                            if (s.equals("SLAVE")) {
                                System.out.println("[Master] Slave added.    " + slaveListSharedObj.size());
                                SlaveHandler sh = new SlaveHandler(clientSocket, ois, oos, clientListSharedObj, (Integer) ois.readObject());
                                slaveListSharedObj.add(sh);
                                sh.start();
                            }
                            if (s.equals("CLIENT")) {
                                ClientHandler ch = new ClientHandler(clientSocket, ois, oos, slaveListSharedObj, unfinishedJobsSharedObj, (Integer) ois.readObject());
                                clientListSharedObj.add(ch);
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
            acceptingThread.start();
            acceptingThread.join(); // wait until it ends (it won't, but this keeps the main thread alive)
        } catch (Exception e){
            System.err.println("[Master] Fatal error: " + e.getMessage());
            e.printStackTrace();
        }

}}

