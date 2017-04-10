package Modules;

import Client.LoggedinClient;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import Employees.Person;
import javafx.stage.WindowEvent;

/**
 * Seperate from the Logged in Client
 * in order to keep file size down
 *
 * * Created by michael-setnyk on 29/03/17.
 */
public class ClientUI {
    protected  Person person;
    private LoggedinClient client;
    private ObservableList<Person> globalPersonList;
    Stage stage;
    public PeoplePaneModule peoplePaneModule;
    FilePaneModule filePaneModule;
    TaskListModule taskListModule;
    MostRecentModule mostRecentModule;
   public ChatViewModule chatViewModule;

    /**
     * initializes all the modules
     * @param client the client that has logged in
     * @param globalPersonList the gloabl person list
     * @param stage the stage
     */

    public ClientUI(LoggedinClient client, ObservableList<Person> globalPersonList, Stage stage){
        this.globalPersonList =globalPersonList;
        this.stage=stage;
        person=client.getLoggedInPerson();
        this.client = client;

        peoplePaneModule= new PeoplePaneModule(globalPersonList,person);
        filePaneModule= new FilePaneModule();
        taskListModule= new TaskListModule(person);
        mostRecentModule = new MostRecentModule();
        chatViewModule = new ChatViewModule(client);
    }

    /**
     * draws the client UI that is identical
     * between clients
     */
    public void drawClientUI(){
        Stage stage = new Stage();
        BorderPane layout = new BorderPane();
        Scene scene = new Scene(layout,800,600);
        stage.setScene(scene);
        stage.show();

        //create calendar
        ButtonBar buttonBar = new ButtonBar();


        SplitPane leftSplitPlane = new SplitPane();
        leftSplitPlane.setDividerPositions(0.25);
        leftSplitPlane.setOrientation(Orientation.VERTICAL);

        SplitPane middleSplitPane = new SplitPane();
        middleSplitPane.setDividerPositions(0.5);
        middleSplitPane.setOrientation(Orientation.VERTICAL);

        //add left stack panes
        leftSplitPlane.getItems().add(mostRecentModule.getMostRecentLayout(stage));
        leftSplitPlane.getItems().add(chatViewModule.getchatView(stage));

        //add to middle split pane
        middleSplitPane.getItems().add(taskListModule.gettaskListPane(stage));
        middleSplitPane.getItems().add(filePaneModule.getfilePane());

        layout.setCenter(middleSplitPane);

        layout.setTop(buttonBar);
        layout.setLeft(leftSplitPlane);
        layout.setRight(peoplePaneModule.getPeoplePane());
        layout.setID("root");
        scene.getStylesheets().add(getClass().getClassLoader().getResource("client.css").toExternalForm());
        stage.setTitle(person.getName());
        stage.setMinWidth(400);
        stage.setMinHeight(400);
        stage.show();

        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                leftSplitPlane.setDividerPositions(0.25);
                middleSplitPane.setDividerPositions(0.5);
                middleSplitPane.setOrientation(Orientation.VERTICAL);
                leftSplitPlane.setOrientation(Orientation.VERTICAL);
            }
        });

        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                leftSplitPlane.getItems().remove(0,2);
                leftSplitPlane.setDividerPositions(0.25);
                leftSplitPlane.setOrientation(Orientation.VERTICAL);
                leftSplitPlane.getItems().add(mostRecentModule.getMostRecentLayout(stage));
                leftSplitPlane.getItems().add(chatViewModule.getchatView(stage));

                middleSplitPane.getItems().remove(0,2);
                middleSplitPane.getItems().add(taskListModule.gettaskListPane(stage));
                middleSplitPane.getItems().add(filePaneModule.getfilePane());
            }
        });
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                System.exit(0);
            }
        });
    }
}
