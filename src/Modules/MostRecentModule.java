package Modules;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * logged in client searches for a logged in person
 * time permitting
 * Created by michael-setnyk on 29/03/17.
 */
public class MostRecentModule {
    /**
     * constructor initializes everything
     */
    public MostRecentModule(){

    }


    /**
     * crates the UI
     * @param stage the stage
     * @return StackPane
     */
    public StackPane getMostRecentLayout(Stage stage){
        BorderPane layout = new BorderPane();

        layout.setPrefWidth(stage.getWidth()/4);

        //create the search field using Hbox
        TextField searchField = new TextField();
        searchField.setMinWidth(layout.getWidth());
        searchField.setPromptText("Search");
        ImageView searchIcon = new ImageView("file:resources/LocalImages/searchIcon.png");
        searchIcon.setFitHeight(20);
        searchIcon.setFitWidth(20);
        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(searchIcon,searchField);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        layout.setBottom(hBox);
        //add scroll pane
        ScrollPane sPane = new ScrollPane(layout);
        sPane.setFitToHeight(true);
        sPane.setPannable(true);
        sPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        layout.setID("root");
        //add hbox to bottom of scroll pane
        StackPane mostRecentPane = new StackPane(sPane);

        return mostRecentPane;
    }

}
