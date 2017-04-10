package Modules;

import Client.*;
import Employees.Person;
import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


/**
 * handles the group chat
 *
 * Created by michael-setnyk on 29/03/17.
 */
public class ChatViewModule extends  Thread{
    protected PrintWriter out = null;
    TextArea chatBox = new TextArea();
    TextArea chatLog = new TextArea();
    Socket chatSocket;//never closes unless client closes
    Person person;

    /**
     * constructor initializes the client
     * uses the logged in client to determine which person
     * sent a message
     * @param c LoggedinClient
     */
    public ChatViewModule(LoggedinClient c) {
        super();
        person =c.getLoggedInPerson();
        chatSocket=c.getClientSocket();
        initializeSocket();

        //creates a new socket based on clients socket adress and port
        chatBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER && !chatBox.getText().isEmpty())
                {
                    event.consume();//consumes the enter key
                    sendMessageToServer(chatBox.getText());
                    chatBox.clear();
                }
                else if(event.getCode() ==KeyCode.ENTER)
                {event.consume();}
            }
        });
    }


    /**
     * sends a message to server
     * @param message String
     */
    private void sendMessageToServer(String message){

        message= person.getName() +": " + message;

            out.println("MESSAGE");
            out.println(message);
            out.flush();
    }

    /**
     * writes messge to the chat log
     * @param message
     */
    public void sendMessageToUI(String message){
        chatLog.appendText(message+'\n');
    }

    /**
     * draws the UI and returns it
     * @param stage stage
     * @return StackPane
     */
    public StackPane getchatView(Stage stage){
        BorderPane layout = new BorderPane();
        layout.setPrefWidth(stage.getWidth()/4);
        chatBox.setWrapText(true);
        chatBox.setPromptText("enter to send");
        chatBox.setPrefWidth(layout.getWidth());
        chatBox.setPrefHeight(30);

        chatLog.setEditable(false);
        chatLog.setWrapText(true);
        HBox.setHgrow(chatLog, Priority.ALWAYS);
        HBox.setHgrow(chatBox, Priority.ALWAYS);
        layout.setBottom(chatBox);
        layout.setCenter(chatLog);
        layout.setID("root")
        //add scroll pane
        ScrollPane sPane = new ScrollPane(layout);
        sPane.setFitToHeight(true);
        sPane.setPannable(true);
        sPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        StackPane chatViewPane = new StackPane(sPane);
        return chatViewPane;
    }

    /**
     * initialize socket
     */
    public void initializeSocket() {
        try {
            out = new PrintWriter(chatSocket.getOutputStream(), true);
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: ");
        } catch (ConnectException e) {
            System.err.println(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * reset sockets
     * @param socket new Socket connection
     */
    public void resetSocket(Socket socket) {
        try {
            chatSocket=socket;
            out = new PrintWriter(chatSocket.getOutputStream(), true);
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: ");
        } catch (ConnectException e) {
            System.err.println(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
