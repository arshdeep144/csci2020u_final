package Modules;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import Client.ProjectFile;

import java.io.File;

/**
 * The file pane module at the bottom of the screen
 *
 * Created by michael-setnyk on 29/03/17.
 */
public class FilePaneModule {
    private TreeView<ProjectFile> localTreeView;

    /**
     * constuctor inalizes everything
     */
    public FilePaneModule(){
        localTreeView = new TreeView<>();
        // initially, load the current directory
        File initialDirectory = (new File("").getAbsoluteFile());
        TreeItem<ProjectFile> rootItem = new TreeItem<>(new ProjectFile(initialDirectory));
        populateDirectory(initialDirectory, rootItem);
        rootItem.setExpanded(true);
        localTreeView.setRoot(rootItem);
    }

    /**
     * this function is recursive
     * populates the localTree
     * @param dir dir File
     * @param parentItem the parent item
     */
    private void populateDirectory(File dir, TreeItem<ProjectFile> parentItem) {
        File[] files = dir.listFiles();
        for (File file : files) {
            TreeItem<ProjectFile> fileItem = new TreeItem<>(new ProjectFile(file));
            parentItem.getChildren().add(fileItem);
            if (file.isDirectory()) {
                populateDirectory(file, fileItem);
            }
        }
    }

    /**
     * cretes and returns the UI
     * @return
     */
    public StackPane getfilePane()
    {
        BorderPane layout = new BorderPane();
        layout.setCenter(localTreeView);
        layout.setID("root");
        StackPane fileListPane = new StackPane(layout);
        return fileListPane;
    }

}
