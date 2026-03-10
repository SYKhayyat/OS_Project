import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread implements  Runnable{
    private ServerSocket serverSocket = null;
    int id;
    public ServerThread(ServerSocket serverSocket, int id){
        this.serverSocket = serverSocket;
        this.id = id;
    }
    @Override
    public void run(){
        try(Socket clientSocket = serverSocket.accept();
            InputStream is = clientSocket.getInputStream();
            BufferedReader bis = new BufferedReader(new InputStreamReader(is));
            OutputStream os = clientSocket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
        ){

        } catch (Exception e){

        }
    }
}
