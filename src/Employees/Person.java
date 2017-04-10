package Employees;

import Modules.Task;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.*;

import static javafx.geometry.Pos.TOP_LEFT;
import static javafx.geometry.Pos.TOP_RIGHT;

/**
 * Created by michael-setnyk on 05/03/17.
 */

/**
 * This is the person class
 * Each client has to have a person
 * person contains everything that a person would have
 */
public class Person extends Observable implements Serializable{
    protected StringProperty name=new SimpleStringProperty();
    protected StringProperty role=new SimpleStringProperty();
    protected StringProperty number = new SimpleStringProperty();
    protected StringProperty email = new SimpleStringProperty();
    protected StringProperty otherPersonRole = new SimpleStringProperty();
    protected String sex;
    protected String id;
    protected String password;

    protected StringProperty profileImagePath = new SimpleStringProperty();
    protected ImageView profileImageView;
    protected ImageView peoplePaneImageView;
    protected ImageView roleImageView;

    protected ObservableList<Task> taskList = FXCollections.observableArrayList();
    protected ObservableList<Task> taskListHack = FXCollections.observableArrayList();
    protected ObservableList<String> changeRole = FXCollections.observableArrayList();


    protected TextField nameField;
    protected TextField numberField;
    protected TextField emailField;

    private boolean online;

    protected Label roleLabel;
    protected Map<String,String> rolePaths;
    protected boolean profileShowing;
    protected String taskLocation;

    /**
     * initializes the person
     */
        public Person(){
         intializeChangeRole();
         inatlizeRolePathList();
         intializeImages();

        setOnline(false);
        initializeStringPropertyListenrs();
    }

    /**
     * intializes the string properties
     */
    private void initializeStringPropertyListenrs(){
      profileImagePath.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String path="resources/LocalImages/"+profileImagePath.getName();
                Image tmp =new Image("file:"+profileImagePath.get(),100,100,false,false);
                //initalize image
                profileImageView.setImage(tmp);
                peoplePaneImageView.setImage(tmp);
            }
        });
      role.addListener(new ChangeListener<String>() {
          @Override
          public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
          }
      });
    }


    /**
     * intializes the image views
     */
    private void intializeImages(){
        roleImageView = new ImageView();
        Image tmp =new Image(rolePaths.get("Unassigned"),100,100,false,false);
        roleImageView = new ImageView(tmp);

        profileImagePath.set("file:resources/LocalImages/basicProfilePic.jpg");
        tmp =new Image(profileImagePath.get(),100,100,false,false);
        //initalize image
        profileImageView = new ImageView(tmp);
        peoplePaneImageView = new ImageView(tmp);
        profileImageView.setFitHeight(100);
        profileImageView.setFitWidth(100);
        peoplePaneImageView.setFitHeight(50);
        peoplePaneImageView.setFitWidth(50);





    }

    /**
     * fills the change role comboBox
     */
    private void intializeChangeRole(){
        changeRole.add("Manager");
        changeRole.add("Unassigned");
        changeRole.add("Accounting");
        changeRole.add("Engineer");
        changeRole.add("Help Desk");
        changeRole.add("IT");
        changeRole.add("Analyst");
        changeRole.add("Student");
        changeRole.add("Janitor");
    }

    /**
     * inialize the roles paths each role path is local
     */
    private void inatlizeRolePathList(){
        rolePaths = new TreeMap<>();
        rolePaths.put("Manager", "file:resources/RoleImages/maleManager.png");
        rolePaths.put("Unassigned", "file:resources/RoleImages/unassigned.png");
        rolePaths.put("Accounting", "file:resources/RoleImages/accounting.png");
        rolePaths.put("Engineer", "file:resources/RoleImages/engineer.png");
        rolePaths.put("Help Desk", "file:resources/RoleImages/help-desk-icon-8.png");
        rolePaths.put("IT", "file:resources/RoleImages/IT.png");
        rolePaths.put("Analyst", "file:resources/RoleImages/dataAnalyst.png");
        rolePaths.put("Student", "file:resources/RoleImages/student.png");
        rolePaths.put("Janitor", "file:resources/RoleImages/Janitor.png");
    }


    /**
     * reads the local tasks
     * each task list is a csv file that is named after the id
     * @param id the id determines the files
     */
    public void setLocalTasks(String id)
    {
   try {
       //fill task list for person
       String taskLocation =  "resources/TaskList/"+id+".csv";
       FileReader fileReader = new FileReader(taskLocation);
       BufferedReader input = new BufferedReader(fileReader);

       String line = input.readLine();//remove header
       while ((line = input.readLine()) != null) {
           String[] tokens = line.split(",");
           String num = tokens[0];
           boolean stat = false;
           String status = tokens[1];
           if (status.equalsIgnoreCase("true"))
               stat = true;

           String task = tokens[2];

           Task task1 = new Task(id);
           Task task2 = new Task(id);

           task1.addTask(task, Integer.parseInt(num), stat);
           task2.addTask(task, Integer.parseInt(num), stat);
           taskList.add(task1);
           taskListHack.add(task2);
       }

   }catch (IOException e){e.printStackTrace();}
    }

    /**
     * adds a task to the local task list
     * each task is set to in complete
     * @param task the string of the task
     */
    private void addTask(String task){
        try {
            taskLocation="resources/TaskList/"+id+".csv";
            FileWriter write = new FileWriter(taskLocation, true);
            Task newTask = new Task(id);
            newTask.addTask(task, taskList.size()+1,false);
            taskList.add(newTask);
            taskListHack.add(newTask);
            write.write(newTask.getCurrentNumber()+","+ newTask.getIsComplete()+","+newTask.getTask()+'\n');
            write.close();
        }
        catch (IOException e){e.printStackTrace();}
    }


    /**
     * sets task to the person's UI then writes to file
     * @param taskMessage task message
     */
    public void setTask(String taskMessage){
        Task task =new Task(id);
        task.addTask(taskMessage,taskList.size(),false);
        taskList.add(task);
    }

    /**
     * sets ID
     * @param id Strind
     */
    public void setId(String id){this.id = id;}

    /**
     * sets profile picture using UI
     */
    public void setProfilePic(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pick a image file");
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.jpg", "*.png");
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.getExtensionFilters().add(imageFilter);

        //Show open file dialog
        File file = fileChooser.showOpenDialog(null);
        profileImagePath.set(file.getPath());
        Image tmp = new Image("file:"+profileImagePath.get(),100,100,true,false);
        profileImageView.setImage(tmp);
        peoplePaneImageView.setImage(tmp);

    }

    /**
     * sets the name
     * @param name String
     */
    public void setName(String name){this.name.set(name);}

    /**
     * sets the Sex
     * @param sex String
     */
    public void setSex(String sex){this.sex =sex;}

    /**
     * sets the number
     * @param number String
     */
    public void setNumber(String number){this.number.set(number);}

    /**
     * sets the email
     * @param email String
     */
    public void setEmail(String email){this.email.set(email);}

    /**
     * sets the name in the UI
     * lets the person change their own name
     */
    public void setName() {
        Stage changeNameStage = new Stage();

        TextField changeNameField = new TextField();
        changeNameField.setEditable(true);

        changeNameField.setPromptText("change name");
        changeNameField.setOnKeyPressed(event -> {
            if (event.getCode()== KeyCode.ENTER) {
                this.name.set(changeNameField.getText());
                nameField.setText(this.name.get());
                changeNameStage.close();
                return;
            }
        });

        BorderPane layout = new BorderPane();
        layout.setCenter(changeNameField);

        Scene tmp = new Scene(layout,200,100);
        changeNameStage.setScene(tmp);
        changeNameStage.setTitle("change name");
        changeNameStage.show();
    }

    /**
     * sets the email in the UI
     * lets the person change their own email
     */
    public void setEmail()
    {
        Stage changeNameStage = new Stage();

        TextField changeNameField = new TextField();
        changeNameField.setPromptText("change email");
        changeNameField.setOnKeyPressed(event -> {
            if (event.getCode()== KeyCode.ENTER) {
                this.email.set(changeNameField.getText());
                emailField.setText(this.email.get());
                changeNameStage.close();
                return;
            }
        });

        BorderPane layout = new BorderPane();
        layout.setCenter(changeNameField);

        Scene tmp = new Scene(layout,200,100);
        changeNameStage.setScene(tmp);
        changeNameStage.setTitle("change email");
        changeNameStage.show();


    }

    /**
     * lets the manager change another employees role
     * uses string property listeners to make changes permanent
     * lets the person change their own name
     */
    public void setOtherPersonRole(String id, String role){
        otherPersonRole.set(id+","+role);
    }

    /**
     * sets the number in the UI
     * lets the person change their own phone number
     */
    public void setNumber()
    {
        Stage changeNameStage = new Stage();

        TextField changeNumberField = new TextField();
        changeNumberField.setPromptText("change number");
        changeNumberField.setOnKeyPressed(event -> {
            if (event.getCode()== KeyCode.ENTER) {
                this.number.set(changeNumberField.getText());
                numberField.setText(this.number.get());
                changeNameStage.close();
                return;
            }
        });

        BorderPane layout = new BorderPane();
        layout.setCenter(changeNumberField);

        Scene tmp = new Scene(layout,200,100);
        changeNameStage.setScene(tmp);
        changeNameStage.setTitle("change number");
        changeNameStage.show();
    }

    /**
     * sets the role
     * changes role
     */
    public void setRole(String role){
        this.role.set(role);
        Image tmp = new Image(rolePaths.get(role),100,100,false,false);
        roleImageView.setImage(tmp);
    }

    /**
     * sets the role in the UI
     * lets the manger change another person's role
     */
    public void setRole(){
        roleLabel.textProperty().bind(Bindings.concat("Role: ").concat(role.get()));
        Image tmp = new Image(rolePaths.get(role.get()),100,100,false,false);
        roleImageView.setImage(tmp);
    }

    public void setTaskLocation(String loc){taskLocation=loc;}

    /**
     * sets the password for the person
     * person chooses their own password
     */
    public void setPassword(String p){password=p;}

    /**
     * changes the person online status
     * @param online boolean
     */
    public void setOnline(boolean online) {
        this.online = online;
        Color color;

        if (online)
        {
            color = Color.GREEN;
        }
        else
        {
            color = Color.RED;
        }
        //create profile pic
        DropShadow ds = new DropShadow(25,color);
        peoplePaneImageView.setEffect(ds);
    }

    /**
     * change the profile image path
     * @param path
     */
    public void setProfileImagePath(String path ){profileImagePath.set(path);}

    /**
     * gets the person's online status
      * @return boolean
     */
    public boolean getOnline(){return  online;}

    /**
     * gets the name
     * @return String
     */
    public String getName(){return name.get();}

    /**
     * gets the person's email
     * @return String
     */
    public String getEmail(){return email.get();};

    /**
     * gets the task list
     * @return ObservableList<Task>
     */
    public ObservableList<Task> getTaskList()
    {
        return taskList;
    }

    /**
     * gets the imageView for the people pane module
     * @return ImageView
     */
    public ImageView getPeoplePaneImageView(){return peoplePaneImageView;}

    /**
     * gets the id fpr the person
     * @return String
     */
    public String getId(){return id;}

    /**
     * returns the role name of the person
     * @return String
     */
    public String getRole(){return role.get();}

    /**
     * returns the true if profile is showing false otherwise
     * @return boolean
     */
    public boolean getIsProfileShowing(){return profileShowing;}

    /**
     * returns the password of the person
     * @return String
     */
    public String getPassword(){return password;}

    /**
     * returns the String property for name
     * @return StringProperty
     */
    public StringProperty getNameProp(){return  name;}

    /**
     * returns the String property for image path
     * @return StringProperty
     */
    public StringProperty getProfileImagePath(){return profileImagePath;}

    /**
     * returns the String property for number
     * @return StringProperty
     */
    public StringProperty getNumberProp(){return  number;}

    /**
     * returns the String property for email
     * @return StringProperty
     */
    public StringProperty getEmailProp(){return  email;}

    /**
     * returns the String property for role
     * @return StringProperty
     */
    public StringProperty getRoleProp(){return  role;}

    /**
     * returns the String property for otherPersonRole
     * @return StringProperty
     */
    public StringProperty getOtherPersonRole(){return otherPersonRole;}


    /**
     * if the client is viewing someone else's profile the layout will change
     * all the options to change or alter the person's page will be removed
     * unless the person viewing is a manger
     * if the person viewing is a manager then there will be a text field to add tasks
     * and a combobos to change their role
      * @param personViewing the person that is viewing
     */
    public void drawOtherPersonPopout(Person personViewing){
        profileShowing=true;
        Stage profileStage = new Stage();
        GridPane gPane = new GridPane();

        //create text fields
        BorderPane layout = new BorderPane();
        nameField = new TextField(name.get());
        nameField.setEditable(false);
        numberField = new TextField(number.get());
        numberField.setEditable(false);
        emailField = new TextField(email.get());
        emailField.setEditable(false);
        roleLabel= new Label("Role: "+role.get());
        Button messageButton = new Button("Message");
        messageButton.setOnAction(event -> {
            //TODO implement private messageing time permiting
        });

        gPane.setHgap(20);
        gPane.setVgap(30);

        //here is the image view
        //left side
        gPane.add(profileImageView,0,0);
        gPane.add(nameField,0,1);
        gPane.add(numberField,0,2);
        gPane.add(emailField,0,3);
        //configure table

        TableView table = new TableView<>();
        table.setItems(taskList);

        TableColumn<Task,ImageView> statusCol = new TableColumn("Status");
        statusCol.setMinWidth(20);
        statusCol.setCellValueFactory(new PropertyValueFactory<>("image"));

        TableColumn<Task,Integer> taskNumberCol = new TableColumn("task number");
        taskNumberCol.setMinWidth(50);
        taskNumberCol.setCellValueFactory(new PropertyValueFactory<>("currentNumbeStringr"));

        TableColumn<Task,String> taskCol = new TableColumn("task");
        taskCol.setMinWidth(150);
        taskCol.setCellValueFactory(new PropertyValueFactory<>("task"));

        table.getColumns().add(taskNumberCol);
        table.getColumns().add(statusCol);
        table.getColumns().add(taskCol);
        table.setPrefWidth(500);

        ScrollPane sPane = new ScrollPane(table);

        sPane.setFitToHeight(true);
        sPane.setPannable(true);
        sPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);


        VBox topRight = new VBox();
        HBox roleAndMessageBox = new HBox();
        HBox textAreadBox = new HBox();

        messageButton.setAlignment(TOP_LEFT);
        roleAndMessageBox.getChildren().add(messageButton);
        VBox roleBox = new VBox();

        roleBox.getChildren().add(roleLabel);
        roleBox.getChildren().add(roleImageView);
        roleBox.setAlignment(TOP_RIGHT);

                if (personViewing.getRole().equalsIgnoreCase("manager")) {
                    ComboBox<String> changeRoleBox= new ComboBox<String>(changeRole);
                    changeRoleBox.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {

                            role.set(changeRoleBox.getValue());
                            setRole();
                            personViewing.setOtherPersonRole(role.get(),id);
                        }
                    });


                    roleBox.getChildren().add(changeRoleBox);
                    roleAndMessageBox.getChildren().add(roleBox);

                    TextArea addTaskArea = new TextArea();
                    addTaskArea.setPromptText("Add task");
                    addTaskArea.setWrapText(true);
                    addTaskArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
                        @Override
                        public void handle(KeyEvent event) {

                            if(event.getCode() ==KeyCode.ENTER && !addTaskArea.getText().isEmpty())
                            {
                                event.consume();//consumes the enter key
                                addTask(addTaskArea.getText());
                                addTaskArea.clear();

                            }
                            else if(event.getCode() ==KeyCode.ENTER)
                            {event.consume();}
                        }
                    });



                    textAreadBox.getChildren().add(addTaskArea);
                    topRight.getChildren().add(roleAndMessageBox);
                    topRight.getChildren().add(textAreadBox);
                      }

              else
        {        topRight.getChildren().add(roleBox);
        }

        roleAndMessageBox.setSpacing(180);


        layout.setRight(topRight);
        layout.setCenter(gPane);
        layout.setBottom(sPane);
        Scene scene = new Scene(layout,500,500);
        profileStage.setResizable(false);
        profileStage.setScene(scene);
        profileStage.show();
        profileStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                profileShowing=false;
            }
        });

    }

    /**
     * creates the UI if you are viewing your own profile
      */
    public void drawPersonalPopout(){
        profileShowing=true;

        Stage profileStage = new Stage();
        GridPane gPane = new GridPane();

        //create text fields
        BorderPane layout = new BorderPane();
        nameField = new TextField(name.get());
        nameField.setEditable(false);
        numberField = new TextField(number.get());
        numberField.setEditable(false);
        emailField = new TextField(email.get());
        emailField.setEditable(false);
        roleLabel= new Label("Role: "+role.get());

        gPane.setHgap(20);
        gPane.setVgap(30);
        gPane.setMaxHeight(100);

        //here is the image view
        //left side
        gPane.add(profileImageView,0,0);
        gPane.add(nameField,0,1);
        gPane.add(numberField,0,2);
        gPane.add(emailField,0,3);

        Button changeImageButton =new Button("change image");
        changeImageButton.setOnAction(event -> {
            setProfilePic();
        });

        Button changeNameButton = new Button("change name");
        changeNameButton.setOnAction(event -> {
            setName();
        });

        Button changeEmailButton = new Button("change email");
        changeEmailButton.setOnAction(event -> {
           setEmail();
        });

        Button changeNumberButton = new Button("change number");
        changeNumberButton.setOnAction(event -> {
            setNumber();
        });

        //change side
        gPane.add(changeImageButton,1,0);
        gPane.add(changeNameButton,1,1);
        gPane.add(changeNumberButton,1,2);
        gPane.add(changeEmailButton,1,3);

        //configure task table
            TableView table = new TableView<>();
            table.setItems(taskListHack );

            TableColumn<Task,Image> statusCol = new TableColumn("Status");
            statusCol.setMinWidth(20);
            statusCol.setCellValueFactory(new PropertyValueFactory<>("image"));

            TableColumn<Task,Integer> taskNumberCol = new TableColumn("task number");
            taskNumberCol.setMinWidth(50);
            taskNumberCol.setCellValueFactory(new PropertyValueFactory<>("currentNumber"));

            TableColumn<Task,String> taskCol = new TableColumn("task");
            taskCol.setMinWidth(150);
            taskCol.setCellValueFactory(new PropertyValueFactory<>("task"));

            table.getColumns().add(taskNumberCol);
            table.getColumns().add(statusCol);
            table.getColumns().add(taskCol);
            table.setPrefWidth(500);

            ScrollPane sPane = new ScrollPane(table);

            sPane.setFitToHeight(true);
            sPane.setPannable(true);
            sPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        VBox roleBox = new VBox();
        roleBox.getChildren().add(roleLabel);
        roleBox.getChildren().add(roleImageView);

        layout.setRight(roleBox);
        layout.setCenter(gPane);
        layout.setBottom(sPane);
        Scene scene = new Scene(layout,500,500);
        profileStage.setScene(scene);
        profileStage.show();
        profileStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                profileShowing=false;
            }
        });

    }

    /**
     * over ride the too string method to improve
     * error checking
     * @return
     */
    public String toString()
    {
        return "ID: " + String.valueOf(id)+" "+ name.get()+" " +role.get() + " Online: " +online;
    }
}

