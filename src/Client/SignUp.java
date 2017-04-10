package Client;

import Employees.Person;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
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
 * The sign up page for those who will sign in
 *
 * Created by michael-setnyk on 08/04/17.
 */
public class SignUp{
    private boolean correctLogin = false;
    private Label warningLabel;
    private Label emailLabel;
    private PasswordField confirmPasswordField;
    private Stage stage;
    private Button signupButton;
    private List<String> emailList = new ArrayList<>();
    private ObservableList<Person> globalPersonList = FXCollections.observableArrayList();
    protected TextField usernamefield;
    protected Label usernameLabel;
    protected Label passwordLabel;
    private Label confirmPasswordLabel;
    protected TextField phoneField;
    protected Label phoneLabel;
    protected Label mangerLabel;
    private PasswordField managerPasswordField;
    private String managerPassword ="1234";
    ToggleGroup group = new ToggleGroup();
    RadioButton maleButton = new RadioButton("Male");
    RadioButton femaleButton = new RadioButton("Female");

    protected PasswordField passwordField;


    private TextField emailField;

    public  static String SERVER_ADDRESS = "localhost";
    public  static int    SERVER_PORT = 50000;
    protected Socket clientSocket;
    private BufferedReader in =null;
    protected PrintWriter out = null;

    /**
     * sign up Constructor
     * this initializes the majority of the elements
     * @param stage the stage
     * @param globalPersonList the gloabl person list
     */
    public SignUp(Stage stage, ObservableList<Person> globalPersonList){
    try {
        clientSocket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);

    } catch (UnknownHostException e) {
        System.err.println("Unknown host: " + SERVER_ADDRESS);
    } catch (ConnectException e) {
        System.err.println(e);
    } catch (IOException e) {
        e.printStackTrace();
    }


    this.stage=stage;
    this.globalPersonList = globalPersonList;
    initializeEmailList();
    warningLabel = new Label();
    confirmPasswordField=new PasswordField();
    confirmPasswordField.setPromptText("Confirm Password");
    emailLabel = new Label("Enter Email");
    emailField = new TextField();
    emailField.setPromptText("enter Email");
    signupButton = new Button("Sing Up");
    usernameLabel=new Label("User name");
    passwordLabel=new Label("Password");
    confirmPasswordLabel=new Label("Confirm Password");
    usernamefield = new TextField();
    usernamefield.setPromptText("enter email");
    passwordField = new PasswordField();
    passwordField.setPromptText("enter password");
    phoneLabel = new Label("Enter Phone Number");
    phoneField = new TextField();
    phoneField.setPromptText("Enter Number");
    mangerLabel=new Label("enter manager password");
    managerPasswordField = new PasswordField();
    managerPasswordField.setPromptText("Enter manager password");
    femaleButton.setToggleGroup(group);
    maleButton.setToggleGroup(group);


    signupButton.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if(emailList.contains(emailField.getText()))
            {
                warningLabel.setText("email already exists");
            }
            else if (!passwordField.getText().equals(confirmPasswordField.getText()))
            {
                warningLabel.setText("passwords do not match");
            }
            else if (passwordField.getText()=="" || emailField.getText()=="" || usernamefield.getText()=="" || !group.getSelectedToggle().isSelected())
            {
                warningLabel.setText("A field(s) is empty");
            }
            else{
                signUp();
            }
        }
    });

    drawSignup();
    }

    /**
     * initializes the email list
     * this is made for simplicity to make sure
     * that no two people have the same email
     */
    private void initializeEmailList(){
    for (Person p: globalPersonList)
    {
        emailList.add(p.getEmail());
    }
    }


    /**
     * assuming all conditions are met
     * the person signs up
     * writes the person's information to the employee file
     * and asks the person to login
     */
    private void signUp(){
        String maxID ="0";
        out.println("ADD_EMPLOYEE");

        for (Person p: globalPersonList)
        {
            maxID=p.getId();
        }
        maxID = String.valueOf(Integer.parseInt(maxID)+1);
        if(managerPasswordField.getText().equals(managerPassword))
            out.println("Manager");
        else
            out.println("Unassigned");

        out.println(maxID);
        out.println(usernamefield.getText());
        String t;
        if(group.getSelectedToggle().equals(femaleButton))
            t="female";
        else
            t="male";
        out.println(t);
        out.println(phoneField.getText());
        out.println(emailField.getText());
        out.println(passwordField.getText());




        BorderPane layout = new BorderPane();
        layout.setBottom(signupButton);
        Label label = new Label("Please exit and login");
        layout.setCenter(label);
        Button exit = new Button("Exit");
        exit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });
        layout.setBottom(exit);
        exit.setId("btn");
        label.setId("text");
        layout.setId("root");
        Scene scene = new Scene(layout,500,400);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("signup.css").toExternalForm());
        stage.setTitle("All Done");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * draws the UI
     */
    private void drawSignup(){
        BorderPane layout = new BorderPane();
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(30,10,20,10));
        gridPane.add(usernameLabel,0,0);
        gridPane.add(usernamefield,1,0);
        gridPane.add(passwordLabel,0,1);
        gridPane.add(passwordField,1,1);
        gridPane.add(confirmPasswordLabel,0,2);
        gridPane.add(confirmPasswordField,1,2);
        gridPane.add(emailLabel,0,3);
        gridPane.add(emailField,1,3);
        gridPane.add(maleButton,0,4);
        gridPane.add(femaleButton,1,4);
        gridPane.add(phoneLabel,0,5);
        gridPane.add(phoneField,1,5);
        gridPane.add(mangerLabel,0,6);
        gridPane.add(managerPasswordField,1,6);



        layout.setTop(warningLabel);
        layout.setBottom(signupButton);
        layout.setCenter(gridPane);
        Scene scene = new Scene(layout,500,400);
        stage.setTitle("Sing Up");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

}
