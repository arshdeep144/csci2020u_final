package Client;

import Employees.Person;
import Modules.ClientUI;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The logged in client
 * Created by michael-setnyk on 08/04/17.
 */
public class LoggedinClient extends Application{
    private StringProperty profileImagePath;
    protected StringProperty name=new SimpleStringProperty();
    protected StringProperty role=new SimpleStringProperty();
    protected StringProperty number = new SimpleStringProperty();
    protected StringProperty email = new SimpleStringProperty();
    protected StringProperty otherPersonRole = new SimpleStringProperty();
    private Person loggedInPerson;
    private BufferedReader in =null;
    protected PrintWriter out = null;
    public  static String SERVER_ADDRESS = "localhost";
    public  static int    SERVER_PORT = 50000;
    protected Socket clientSocket;
    private ClientUI clientUI;
    private boolean inUse;
    //networking required for this part
    private static ObservableList<Person> globalPersonList= FXCollections.observableArrayList();


    /**
     * constructor that initializes all the string properties and
     * other elements
     * @param globalPersonList the global person list from the server
     * @param p the person that has logged in
     */
    public LoggedinClient(ObservableList<Person> globalPersonList, Person p){
        this.globalPersonList = globalPersonList;
        loggedInPerson=p;

        profileImagePath=p.getProfileImagePath();
        name = p.getNameProp();
        otherPersonRole=p.getOtherPersonRole();
        number =p.getNumberProp();
        email = p.getEmailProp();

        profileImagePath.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                out.println("UPLOAD");
                out.flush();
            }
        });

        name.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                out.println("CHANGE_NAME");
                out.flush();
            }
        });

        number.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                out.println("CHANGE_NUMBER");
                out.flush();
            }
        });

        email.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                out.println("CHANGE_EMAIL");
                out.flush();
            }
        });
        otherPersonRole.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                out.println("CHANGE_ROLE");
                out.flush();
            }
        });

    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        clientSocket = new Socket(SERVER_ADDRESS,SERVER_PORT);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);

        out.println("ADD_ID");
        out.println(loggedInPerson.getId());
        out.flush();
        loggedInPerson.setOnline(true);

        //must re intalize socket since it is closed after reading employees
        clientUI = new ClientUI(this, globalPersonList, primaryStage);
        clientUI.drawClientUI();
        ProccessInputFromServer proccessInputFromServer = new ProccessInputFromServer();
        proccessInputFromServer.start();
    }

    /**
     * very important as the UI is on the main FX thread
     * and the behind the scenes commands and actions are on a seperate thread
     */
                private class ProccessInputFromServer extends Thread{
                public ProccessInputFromServer(){
                    try {
                        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    }catch (IOException e){e.printStackTrace();}
                }

                    /**
                     * the run method for the thread
                     */
                @Override
                public void run() {
                    super.run();
                    while (this.isAlive())
                    {
                        listen();
                    }
                }

                    /**
                     * listens for a command
                     */
                private void listen() {
                    String cmd;
                    try {
                        while (!inUse) {
                            if(clientSocket.isClosed()) {
                                clientSocket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                                out = new PrintWriter(clientSocket.getOutputStream(), true);
                                clientUI.chatViewModule.resetSocket(clientSocket);
                            }

                            cmd = in.readLine();
                            processCommand(cmd);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                    /**
                     * process the command sent from the server
                     * this is very important to keep all reading
                     * and writing between servers in order
                     * @param cmd
                     */
                private void processCommand(String cmd)
                {
                    try {
                        if (cmd.equalsIgnoreCase("MESSAGE")) {
                            inUse =true;
                            clientUI.chatViewModule.sendMessageToUI(in.readLine().trim());
                            inUse =false;

                        }
                        else if (cmd.equalsIgnoreCase("CHANGE")) {
                            inUse =true;
                            clientUI.peoplePaneModule.updateOnline(in.readLine());
                            inUse =false;

                        }
                        else if (cmd.equalsIgnoreCase("UPLOAD")) {
                            inUse =true;
                            upload();
                            inUse =false;
                        }
                        else if (cmd.equalsIgnoreCase("CHANGE_NAME")) {
                            inUse =true;
                            out.println(name.get()+","+loggedInPerson.getId());//uploads filename
                            out.flush();
                            inUse =false;
                        }
                        else if (cmd.equalsIgnoreCase("CHANGE_EMAIL")) {
                            inUse =true;
                            out.println(email.get()+","+loggedInPerson.getId());//uploads filename
                            out.flush();
                            inUse =false;
                        }
                        else if (cmd.equalsIgnoreCase("CHANGE_NUMBER")) {
                            inUse =true;
                            out.println(number.get()+","+loggedInPerson.getId());//uploads filename
                            out.flush();
                            inUse =false;
                        }
                        else if (cmd.equalsIgnoreCase("CHANGE_ROLE")) {
                            inUse =true;
                            out.println(otherPersonRole.get());//uploads filename
                            out.flush();
                            inUse =false;
                        }
                    }catch (IOException e){e.printStackTrace();}
                }

                private void upload(){
                    try {
                        inUse=true;
                        //uploads the command
                        File file = new File(profileImagePath.get());
                        out.println(file.getName()+","+loggedInPerson.getId());//uploads filename
                        out.flush();
                        DataOutputStream dOut = new DataOutputStream(clientSocket.getOutputStream());
                        //uploads the local file
                        String imagePath = profileImagePath.get();
                        Path path = Paths.get(imagePath);
                        byte[] data = Files.readAllBytes(path);

                        dOut.writeInt(data.length); // write length of the message
                        dOut.write(data);
                        dOut.close();

                                               //move to localFileFolder
                        String localPath ="resources/LocalImages/"+file.getName();
                        File newfile = new File(localPath);

                        FileOutputStream stream = new FileOutputStream(newfile);
                        try {
                            stream.write(data);
                        } finally {
                            stream.close();
                        }

                    } catch (IOException e){e.printStackTrace();}
                    inUse=false;


                }
            }

    /**
     * gets the Socket for the logged in client
     * @return Client
     */
    public Socket getClientSocket(){return clientSocket;}

    /**
     * gets the person
     * @return Person
     */
    public Person getLoggedInPerson(){return loggedInPerson;}


    public static void main(String[] args) {
        launch(args);
    }


}
