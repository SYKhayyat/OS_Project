public interface Slave {
    void acceptJob();
    void doJob();
    String submitCompletion(); /* The client’s submission should include the type, and an ID number that 
    will be used to identify the job throughout the system. */
}
