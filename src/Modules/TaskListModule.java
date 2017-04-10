package Modules;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import Employees.Person;

/**
 * Created by michael-setnyk on 29/03/17.
 */
public class TaskListModule {
    Person person;
    public TaskListModule(Person person){
        this.person =person;
    }


    public StackPane gettaskListPane(Stage stage){
        BorderPane layout = new BorderPane();
        HBox taskMenu = new HBox();

        MenuBar menuBar = new MenuBar();
        taskMenu.getChildren().add(new Label("Task List"));
        Menu sortDropDown = new Menu("Sort");
        taskMenu.setSpacing(20);

        taskMenu.getChildren().add(menuBar);
        menuBar.getMenus().add(sortDropDown);

        TableView table = new TableView<>();
        table.setItems(person.getTaskList());

        TableColumn<Task,ImageView> statusCol = new TableColumn("Status");
        statusCol.setMinWidth(20);
        statusCol.setCellValueFactory(new PropertyValueFactory<>("imageButton"));

        TableColumn<Task,Integer> taskNumberCol = new TableColumn("task number");
        taskNumberCol.setMinWidth(50);
        taskNumberCol.setCellValueFactory(new PropertyValueFactory<>("currentNumber"));

        TableColumn<Task,String> taskCol = new TableColumn("task");
        taskCol.setMinWidth(150);
        taskCol.setCellValueFactory(new PropertyValueFactory<>("task"));

        table.getColumns().add(taskNumberCol);
        table.getColumns().add(statusCol);
        table.getColumns().add(taskCol);
        table.setPrefWidth(stage.getWidth());

        ScrollPane sPane = new ScrollPane(table);

        sPane.setFitToHeight(true);
        sPane.setPannable(true);
        sPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        layout.setTop(taskMenu);

        layout.setCenter(sPane);
        StackPane taskListPane = new StackPane(layout);
        return taskListPane;
    }

}
