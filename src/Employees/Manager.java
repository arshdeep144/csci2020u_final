package Employees;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.StringTokenizer;

/**
 * Created by michael-setnyk on 09/03/17.
 */

/**
 * only people with the manager role can be created as a manager
 */
public class Manager extends Person {

    public Manager(){
        super();
        setRole("Manager");
    }

}
