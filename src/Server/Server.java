package Server;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;

/**
 * The server keeps track of active clients
 * must be on an FX thread due to ObservableList List
 *
 * Created by michael-setnyk on 21/03/17.
 */
public class Server extends Application {
    public  static int SERVER_PORT = 50000;
    private static String SERVER_LOCATION = "ServerFiles";
    private ServerSocket serverSocket = null;
    protected Socket clientSocket     = null;
    protected static ObservableList<ChatServerThread> activeClients = FXCollections.observableArrayList();

    /**
     * constructor
     */
    public Server (){
        //intilize objects
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
        }catch (IOException e){e.printStackTrace();}
        try {
            while (true) {
                clientSocket = serverSocket.accept();
                ChatServerThread newThread = new ChatServerThread(clientSocket,SERVER_LOCATION,activeClients);//closes after each action
                newThread.start();
                activeClients.add(newThread);
                removeClosed();
            }
        }
        catch (IOException e) {
            System.err.println("IOEXception while creating server connection");
        }
    }

    /**
     * checks to see if any clients have went off line
     * removes them if that is the case
     */
    public void removeClosed(){
        for (Iterator<ChatServerThread> iter = activeClients.listIterator(); iter.hasNext(); ) {
            ChatServerThread a = iter.next();
            if (a.socketClosed()) {
                iter.remove();
            }
        }

    }

    /**
     * start
     * @param clientStage
     * @throws Exception
     */
    public void start(Stage clientStage) throws Exception {
    }

    /**
     * main
     * @param args
     */
    public static void main (String[] args){
        {launch(args);};
    }
}
