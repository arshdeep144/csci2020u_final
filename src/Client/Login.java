package Client;

import Employees.Manager;
import Employees.Person;
import Employees.Worker;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by michael-setnyk on 30/03/17.
 * Edited by Arshdeep Benipal on 09/04/17
 */
public class Login extends Application{
    protected TextField usernamefield;
    protected Label usernameLabel;
    protected Label passwordLabel;
    protected Label warningLabel = new Label("");
    protected PasswordField passwordField;
    private Button login;
    private Button signup;
    private Person person;
    private BufferedReader in =null;
    protected PrintWriter out = null;
    public  static String SERVER_ADDRESS = "localhost";
    public  static int    SERVER_PORT = 50000;
    protected Socket clientSocket;
    private static ObservableList<Person> globalPersonList= FXCollections.observableArrayList();

    /**
     * start method
     * @param primaryStage stage that the ui will be on
     * @throws Exception throws exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        clientSocket = new Socket(SERVER_ADDRESS,SERVER_PORT);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);

        usernameLabel=new Label("User name");
        passwordLabel=new Label("Password");
        usernamefield = new TextField();
        usernamefield.setPromptText("enter email");
        passwordField = new PasswordField();
        passwordField.setPromptText("enter password");
        login = new Button("Login");
        signup = new Button("Sign up");

        //read employees has to wait or there will be conflicts
        while(!in.ready())
        {}
        readPersonList();
        drawLoginScreen(primaryStage);


        login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                for (int i = 0; i < globalPersonList.size(); i++) {
                    Person p = globalPersonList.get(i);
                    if (usernamefield.getText().equals(p.getName())
                            && passwordField.getText().equals(p.getPassword())) {
                        person = p;
                        primaryStage.close();
                        LoggedinClient loggedinClient = new LoggedinClient(globalPersonList, person);
                        try{
                            loggedinClient.start(primaryStage);

                        }catch (Exception e){e.printStackTrace();}
                        break;
                    }
                }
                warningLabel.setText("Person doesn't exist, try again");
            }
        });

        signup.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                SignUp signUp = new SignUp(primaryStage, globalPersonList);
            }
        });




    }


    /**
     * sends a request to server for employees
     * server sends back confirmation and data
     */
       private void readPersonList(){
        out.println("READ_EMPLOYEES");
        out.flush();
        try {
            String x =in.readLine().trim();
            while(x==null)
                x =in.readLine().trim();
            while (!x.equalsIgnoreCase("READ_EMPLOYEES"))
            {
                x =in.readLine();
            }
            while (!clientSocket.isClosed())
            {
                Person newPerson;
                Thread.sleep(10);
                String role = in.readLine();
                if (role == null)
                    break;
                role = role.replaceAll("[^a-zA-Z0-9]", "");
                if (role.equalsIgnoreCase("Manager"))
                    newPerson = new Manager();
                else
                    newPerson = new Worker();

                newPerson.setRole(role);
                newPerson.setId(in.readLine());
                newPerson.setName(in.readLine());
                newPerson.setSex(in.readLine());
                newPerson.setNumber(in.readLine());
                newPerson.setEmail(in.readLine());
                newPerson.setProfileImagePath(in.readLine());
                newPerson.setTaskLocation(in.readLine());
                newPerson.setLocalTasks(newPerson.getId());
                newPerson.setPassword(in.readLine());
                globalPersonList.add(newPerson);
            }
        }
        catch (IOException e){e.printStackTrace();}
        catch (InterruptedException i){i.printStackTrace();}
    }

    /**
     * creates the UI for login
     */
    public void drawLoginScreen(Stage stage) {
        BorderPane layout = new BorderPane();
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(30, 10, 20, 10));
        gridPane.add(usernameLabel, 0, 0);
        gridPane.add(usernamefield, 1, 0);
        gridPane.add(passwordLabel, 0, 1);
        gridPane.add(passwordField, 1, 1);
        gridPane.add(login, 0, 2);
        gridPane.add(signup, 1, 2);
        gridPane.setId("root");
        layout.setTop(warningLabel);
        layout.setCenter(gridPane);
        Scene scene = new Scene(layout, 500, 400);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("login.css").toExternalForm());
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
