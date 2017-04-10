package Modules;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.*;

/**
 * The task class
 * Created by michael-setnyk on 05/03/17.
 */
public class Task implements Serializable{
    private int currentNumber;
    private transient  ImageView image;
    private transient Button imageButton;
    private transient  Image img;
    private boolean getComplete;
    private String task;
    private static final long serialVersionUID = 5501668519310163575L;
    private String id;
    private String taskListPath ="resources/TaskList/";


    /**
     * constructor
     */
    public Task (String id){
        this.id =id;
        imageButton = new Button();
        imageButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String status;
                if (getComplete) {
                    getComplete = false;
                    status="false";
                }
                else {
                    getComplete = true;
                    status ="true";
                }

                if (getComplete)
                {
                    img = new Image("file:resources/LocalImages/checkmark-xxl.png",10,10,false,false);

                }
                else
                {
                    img = new Image("file:resources/LocalImages/xmark.png",10,10,false,false);
                }
                image.setImage(img);
                editTask(currentNumber, status);
            }
        });

    }



    //change local path
    private void editTask(int num,String status){
        try{
            String originalPath= taskListPath +id +".csv";
            String tmpPath= taskListPath +id +"TMP.csv";
            System.out.println("OG: "+originalPath);

            System.out.println("tmp: "+tmpPath);


            FileReader fileReader = new FileReader(originalPath);
            BufferedReader input = new BufferedReader(fileReader);
            String line;

            //creates a temporary file
            FileWriter fw = new FileWriter(tmpPath, false);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            while ((line = input.readLine()) != null)
            {
                String[] tokens = line.split(",");
                //if num==token[0]
                if(tokens[0].equals(String.valueOf(num)))
                {
                    System.out.println("change this line");

                    line=tokens[0]+","+status+","+tokens[2];

                }
                pw.println(line);

            }
            input.close();
            pw.close();
            fw.close();
            bw.close();
            //temp file created
            //copy tmp file into original one and delete tmp file
            fileReader = new FileReader(tmpPath);
            input = new BufferedReader(fileReader);

            //write to original file
            fw = new FileWriter(originalPath, false);
            bw = new BufferedWriter(fw);
            pw = new PrintWriter(bw);
            while ((line = input.readLine()) != null)
            {
                pw.println(line);
            }
            input.close();
            pw.close();
            fw.close();
            bw.close();

            File tmpFile = new File(tmpPath);
            tmpFile.delete();

        }catch (IOException e)
        {e.printStackTrace();}
        System.out.println("out of change task");

    }



    /**
     * creates fully initialized task
     * @param task String: task message
     * @param taskNumber int: task num
     * @param complete boolean: if task is complete or not
     */
    public void addTask(String task, int taskNumber, boolean complete){
        this.task = task;
        getComplete = false;
        currentNumber=taskNumber;//by default
        image=new ImageView();
        imageButton.setGraphic(image);
        if (complete)
        {
            img = new Image("file:resources/LocalImages/checkmark-xxl.png",10,10,false,false);

        }
        else
        {
            img = new Image("file:resources/LocalImages/xmark.png",10,10,false,false);
        }
        image.setImage(img);
    }

    /**
     * sets the task number
     * @param currentNumber int
     */
    public void setCurrentNumber(int currentNumber) {
        this.currentNumber = currentNumber;
    }

    /**
     * sets if the task is done or not
     * @param getComplete boolean
     */
    public void setGetComplete(boolean getComplete) {
        this.getComplete = getComplete;
    }

    /**
     * sets the task string
     * @param task String
     */
    public void setTask(String task) {
        this.task = task;
    }

    /**
     * gets the image (x or check mark)
      * @return ImageView
     */
    public ImageView getImage() {
        return image;
    }

    /**
     * gets the task number
     * @return
     */
    public int getCurrentNumber() {
        return currentNumber;
    }

    /**
     * get whether or not the task was complete or not
     * @return
     */
    public boolean getIsComplete() {
        return getComplete;
    }

    /**
     * get the string
     * @return String
     */
    public String getTask() {
        return task;
    }


    /**
     * get the img button
     * @return Button
     */
    public Button getImageButton() {
        return imageButton;
    }

    /**
     * converts task to string for error checking and whatnot
     * @return
     */
    public String toString(){
        return String.valueOf(getCurrentNumber()) +" " +String.valueOf(getIsComplete())
                +" "+ getTask();
    }
}
