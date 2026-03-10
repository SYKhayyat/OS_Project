import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;

public interface Slave {
    void process();

    void acceptJob(ObjectInputStream ois) throws IOException, ClassNotFoundException;

    void doJob(PrintWriter pw) throws InterruptedException;

    void alertMaster(PrintWriter pw, Job currentJob) throws InterruptedException;
}
