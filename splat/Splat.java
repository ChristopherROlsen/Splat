/************************************************************
 *                           Splat                          *
 *                          11/21/18                        *
 *                            12:00                         *
 ***********************************************************/
package splat;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Splat extends Application {

    // POJOs
    int initialGridColumns = 6;
    int initialGridRows = 14;
    // My classes
    Data_Manager dm;
    
    // POJOs / FX
    
    @Override
    public void start(Stage primaryStage) {

        //                   initialGridCols, initialGridVars
        dm = new Data_Manager(initialGridRows, initialGridColumns);
        
        Label fileLabel = new Label("File: ");
        MainMenu myMenus = new MainMenu(this, dm, fileLabel);
        BorderPane myGrid = new BorderPane();
        myGrid = dm.getMainPane();
        VBox mainPane = new VBox();
        mainPane.getChildren().addAll(myMenus, myGrid, fileLabel);
        Scene mainScene = new Scene(mainPane);
        String css = getClass().getResource("/css/DataManager.css").toExternalForm();
        mainScene.getStylesheets().add(css);
          
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("SPLAT: Statistics Package for Learning And Teaching");
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        primaryStage.show();
        
        mainScene.widthProperty().addListener((obs, oldVal, newVal) -> {

            dm.getMainPane().setPrefWidth((double)newVal);          
            int newMaxColumnCount = (int)((double)newVal / 100);
            // System.out.println("53 Splat, New Column Count:" + newMaxColumnCount);
            dm.setMaxVisVars(newMaxColumnCount);
            dm.resizeColumnHeaderCellsArray(newMaxColumnCount);
            dm.resizeGrid(dm.getMaxVisCases(), dm.getMaxVisVars());    //  *********************
            dm.sendDataStructToGrid();   //  *****************
        });

        mainScene.heightProperty().addListener((obs, oldVal, newVal) -> {

            dm.getMainPane().setPrefHeight((double)newVal - 32);
            
            int newMaxRowCount = (int)((double)newVal / 31);
            // System.out.println("65 Splat, New Row Count:" + newMaxRowCount);
            
            dm.resizeRowHeaderCellsArray(newMaxRowCount);
            dm.setMaxVisCases(newMaxRowCount);
            dm.resizeGrid(newMaxRowCount, dm.getMaxVisVars());
            dm.sendDataStructToGrid();   
        });

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                event.consume();
                File_Ops runMe = new File_Ops(dm);
                runMe.ExitProgram(dm);
            }
        });

    } // void start

        public static void main(String[] args) {
        launch(args);
    }

} // Splat

