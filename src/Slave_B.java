import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class Slave_B implements Slave{
    private Queue<Job> jobs = new LinkedList<>();
    private Socket socket;

    @Override
    public void process() {
        try(Socket socket = new Socket("127.0.0.1", 30121);
            InputStream is = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            OutputStream os = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os);
        ) {
            acceptJob(ois);
            doJob(pw);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void acceptJob(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        Object obj;
        while ((obj = ois.readObject()) != null) {
            if (obj instanceof Job) {
                jobs.add((Job) obj);
            }
        }
    }

    @Override
    public void doJob(PrintWriter pw) throws InterruptedException {
        Job currentJob;
        while (! jobs.isEmpty()){
            currentJob = jobs.remove();
            if (currentJob.getType() == 1){
                System.out.println("Running job " + currentJob.getPid() + " of Type B...");
                Thread.sleep(2000);
                System.out.println("Finished job " + currentJob.getPid() + " of Type B...");
            } else if(currentJob.getType() == 0){
                System.out.println("Running job " + currentJob.getPid() + " of Type A...");
                Thread.sleep(10000);
                System.out.println("Finished job " + currentJob.getPid() + " of Type A...");
            }
            alertMaster(pw, currentJob);
        }
    }

    @Override
    public void alertMaster(PrintWriter pw, Job currentJob) throws InterruptedException {
        pw.println(currentJob.getPid() + ": done.");
    }
}
