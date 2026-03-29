import java.io.Serializable;

public class Job implements Serializable {
    private int type;
    private String pid;
    private int sender;
    public Job(int type, String pid){
        this.type = type;
        this.pid = pid;
    }
    public Job(String pid){
        this.pid = pid;
    }
    public int getType(){
        return type;
    }
    public String getPid(){
        return pid;
    }
    public int getSender(){
        return sender;
    }
    public void setClient(int sender){
        this.sender = sender;
    }
}
