package Modules;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import Employees.Person;

/**
 * The graphical representation for each peron on the system
 * it's the module on the right side of the screen
 * the logged in person is at the top of the pane
 *
 * Created by michael-setnyk on 29/03/17.
 */
public class PeoplePaneModule {
    private ObservableList<Person> globalPersonList= FXCollections.observableArrayList();
    private ObservableList<Person> onlineList= FXCollections.observableArrayList();

    Person person;
    private String[] idArr;

    /**
     * idealizes everything
     * @param globalPersonList
     * @param p
     */
    public PeoplePaneModule(ObservableList<Person> globalPersonList, Person p){
        this.globalPersonList=globalPersonList;
        person =p;
    }

    /**
     * updates the online status based on changes
     * green if they are online
     * red if they are offline
     * @param idString
     */
    public void updateOnline(String idString) {

        idArr = idString.split(",");
        for (Person p: globalPersonList){
        boolean b=false;
            for (String s: idArr)
            {
                    if(p.getId().equalsIgnoreCase(s.trim())) {
                        p.setOnline(true);
                        onlineList.add(p);
                        b=true;
                        break;
                    }
                }
                if(!b) {
                    if(onlineList.size()>1)
                    p.setOnline(false);
                }

        }
    }


    /**
     * creates the UI
     * @return StackPane
     */
    public StackPane getPeoplePane(){
        BorderPane layout = new BorderPane();
        VBox peopleBox;
        VBox listOfPeople = new VBox();
        for (Person e: globalPersonList) {
            peopleBox = new VBox();
            Button profileButton = new Button();
            profileButton.setId(e.getId());
            profileButton.setGraphic(e.getPeoplePaneImageView());

            //open profile page
            profileButton.setOnAction(event -> {
                if (profileButton.getId()==person.getId() && !person.getIsProfileShowing()) {
                    person.drawPersonalPopout();
                }
                else if (profileButton.getId()!=person.getId() && !e.getIsProfileShowing())
                {
                    e.drawOtherPersonPopout(person);
                }

            });
            peopleBox.getChildren().add(profileButton);
            Label name = new Label();
            name.setText(e.getName());
            peopleBox.getChildren().add(name);
            name.textProperty().bind(e.getNameProp());
            peopleBox.setPadding(new Insets(0,0,15,0));

            //person being the current client
            if(e == person)
            {
                layout.setTop(peopleBox);
            }
            else {
                listOfPeople.getChildren().add(peopleBox);
            }
            name.setID("text");
            profileButton.setID("btn");
        }

        layout.setRight(listOfPeople);
        layout.setID("root");

        ScrollPane sPane = new ScrollPane(layout);
        sPane.setFitToHeight(true);
        sPane.setPannable(true);
        sPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        StackPane taskListPane = new StackPane(sPane);
        return taskListPane;
    }

}
