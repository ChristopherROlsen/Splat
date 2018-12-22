/******************************************************************
 *                     DataCleaner                                *
 *                       10/21/18                                 *
 *                        00:00                                   *
 *****************************************************************/
package genericClasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DataCleaner {
    // POJOs
    boolean inListToClean, doubleTrouble;
    
    int nEdits, nDataToClean, currentlyEditing, nUniques, nStringsToClean,
        nNumericStringsToClean, nNonMissingData, nTransforms;
    int[] categoryCount;
    ArrayList<Integer> countUniques;
    
    String changeFrom, changeTo, dataType, dataToCheck;    
    String[] stringsToClean, str_CopyOfOriginalData, str_CleanedData, 
             finalCategories, str_NonMissing, str_CleanedNonMissing;
    ArrayList<String> uniques, listView_From, listView_To, al_NonMissing;
    ListView<String> lv_Uniques, lv_PreChoice, lv_PostChoice;
    
    // POJOs / FX
    Button btn_Cancel, btn_OK;
    HBox buttons, lists;
    Stage stage;
    Text captions;
    Text directions;
    VBox root;
    
    public DataCleaner(ColumnOfData columnToClean) {
        dataType = columnToClean.getDataTypeOfThisColumn();
        if (dataType.equals(null)) {
            columnToClean.assignDataType();
            dataType = columnToClean.getDataTypeOfThisColumn();
        }
        nDataToClean = columnToClean.getColumnSize();
        str_CopyOfOriginalData = new String[nDataToClean];
        al_NonMissing = new ArrayList();  
        System.out.println("57 dc, this.dataType = " + dataType);
        
        for (int ith = 0; ith < nDataToClean; ith++) {
            str_CopyOfOriginalData[ith] = columnToClean.getTextInIthRow(ith);
        }   
    }   //  end constructor
    
    public void cleanAway () {
        stage = new Stage();
        // System.out.println("\n\n66 cleanAway!");
        // ------------------------------------------------
        
        for (int ith = 0; ith < nDataToClean; ith++) {
            dataToCheck = str_CopyOfOriginalData[ith];
            // System.out.println("71 dc, dataToCheck = " + dataToCheck);
            if (dataToCheck.equals("*"))  {
               continue;
            }
            // System.out.println("75 dc, dataToCheck = " + dataToCheck);
            if (dataType.equals("real") 
                    && (DataUtilities.stringIsADouble(dataToCheck) == false
                    && DataUtilities.stringIsAnInteger(dataToCheck) == false)) {  
                    // System.out.println("79 dc, real: adding " + dataToCheck);
                    al_NonMissing.add(dataToCheck);
            }
      
            else 
            if (dataType.equals("categorical"))  {
                // System.out.println("92 dc, dataToCheck = " + dataToCheck);
                if (!dataToCheck.equals("*"))  {
                    // System.out.println("79 dc, categorical: adding " + dataToCheck);
                   al_NonMissing.add(dataToCheck);
                } 
            }
        }
        
        nStringsToClean = al_NonMissing.size();  
        
        // System.out.println("114 dc, nStringsToClean = " + nStringsToClean);
        
        str_NonMissing = new String[nStringsToClean];
        for (int ith = 0; ith < nStringsToClean; ith++) {
            str_NonMissing[ith] = al_NonMissing.get(ith);
        }
        
        nNonMissingData = str_NonMissing.length;
        
        if (nNonMissingData > 0) {
            str_CleanedData = new String[nNonMissingData];
            lv_Uniques = new ListView<>();
            listView_From = new ArrayList<>();
            listView_To = new ArrayList<>();
            lv_Uniques.setPrefSize(200, 150);
            lv_Uniques.setEditable(true);
            lv_Uniques.setCellFactory(TextFieldListCell.forListView());
            nEdits = 0;    

            // Add 0th item
            lv_Uniques.getItems().add(str_NonMissing[0]);
            // Add only different items

            for (int ith = 1; ith < nNonMissingData; ith++) {
                boolean differentFromEarlier = true;
                for (int jth = 0; jth < ith; jth++) {
                    if (str_NonMissing[jth].equals(str_NonMissing[ith]))
                        differentFromEarlier = false;
                }
                if (differentFromEarlier == true)
                     lv_Uniques.getItems().add(str_NonMissing[ith]);
            }

            lv_PreChoice = new ListView<>();
            lv_PreChoice.setPrefSize(200, 120);
            lv_PreChoice.setEditable(false);
            lv_PreChoice.setCellFactory(TextFieldListCell.forListView());

            lv_PostChoice = new ListView<>();
            lv_PostChoice.setPrefSize(200, 120);
            lv_PostChoice.setEditable(false);
            lv_PostChoice.setCellFactory(TextFieldListCell.forListView());

            // Add Edit-related event handlers
            lv_Uniques.setOnEditStart(this::editStart);
            lv_Uniques.setOnEditCommit(this::editCommit);

            cleanTheStrings();   
        }
        
        else {
            System.out.println("153 dc, Data are clean!!!");
        }
    }   //  end cleanAway
    
    private void cleanTheStrings() {
            root = new VBox();
            nStringsToClean = str_NonMissing.length;
            str_CleanedNonMissing = new String[nStringsToClean];
            directions = new Text();
            directions.prefWidth(750);

            String daDirections = "\n      Double-click to edit incorrect values.  When the list on the left contains only correct values," +
                                  "\n      press OK to continue.  Here, it is only possible to change an incorrect value to an existing" + 
                                  "\n      correct value; if you wish to create a new value you must do so in the data grid.\n";

            directions.setText(daDirections);
            String captionText = "               Double click to edit                                         Old Value                                       Edited Value";

            directions.setFill(Color.RED);
            directions.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 16));
            captions = new Text(captionText);
            captions.setFill(Color.BLUE);
            captions.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 14));                
            HBox lists = new HBox();
            lists.getChildren().addAll(lv_Uniques, lv_PreChoice, lv_PostChoice);
            HBox buttons = new HBox();

            btn_Cancel = new Button("Cancel");
            btn_Cancel.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent event) {
                    System.out.println("Cancel pressed");
                    System.exit(1);
                }
            });

            btn_OK = new Button("OK");
            btn_OK.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent event) {                 
                    // For the edits made
                    // Make changes in the original list
                    // Clean if needed, else just copy
                    for (int i = 0; i < nDataToClean; i++) {
                        for (int fromTos = 0; fromTos < nEdits; fromTos++) {
                            if (str_CopyOfOriginalData[i].equals(listView_From.get(fromTos))) {
                                str_CopyOfOriginalData[i] = listView_To.get(fromTos);
                                break;
                            }
                        }                          
                    }

                    collectUniqueValues();
                    stage.hide();
                }
            });                

            buttons.getChildren().addAll(btn_Cancel, btn_OK);

            lists.setSpacing(20);
            lists.setStyle("-fx-padding: 10;" + 
                          "-fx-border-style: solid inside;" + 
                          "-fx-border-width: 2;" +
                          "-fx-border-insets: 5;" + 
                          "-fx-border-radius: 5;" + 
                          "-fx-border-color: blue;");
            
            root.getChildren().addAll(directions, captions, lists, buttons);

            Scene scene = new Scene(root);	    
            stage.setScene(scene);		
            stage.setTitle("Editing categorical list from DataManager");
            stage.showAndWait();
    }   //  end cleanTheseStrings
    
    public boolean isInListToClean(String thisOne) {
        inListToClean = false;
        for (int ith = 0; ith < nNonMissingData; ith++) {
            if (thisOne.equals(str_NonMissing[ith])) {
                inListToClean = true;
            }
        }
        return inListToClean;
    }   //  end isInListToClean
    
	
    public void editStart(ListView.EditEvent<String> e) {
        currentlyEditing = e.getIndex();
        changeFrom = new String(lv_Uniques.getSelectionModel().getSelectedItem());
    }   //  end editStart

    public void editCommit(ListView.EditEvent<String> e) {
        boolean inOriginalList = false;        
        changeTo = new String(e.getNewValue());

        for (int i = 0; i < nStringsToClean; i++) {
            if (changeTo.equals(str_CleanedData[i])) {
                inOriginalList = true;
            }
        }

        if ((isInListToClean(changeTo) == true) && (dataType.equals("categorical"))) {
            lv_PreChoice.getItems().add(changeFrom);   //  Add to the listView
            lv_PostChoice.getItems().add(changeTo);    //  Add to the listView
            listView_From.add(changeFrom);                //  Add to the ArrayList
            listView_To.add(changeTo);                    //  Add to the ArrayList
            nEdits++;
            lv_Uniques.getItems().remove(currentlyEditing);
        }
        else if (dataType.equals("real") && (check4Double(changeTo) == true)) {
            lv_PreChoice.getItems().add(changeFrom);   //  Add to the listView
            lv_PostChoice.getItems().add(changeTo);    //  Add to the listView
            listView_From.add(changeFrom);                //  Add to the ArrayList
            listView_To.add(changeTo);                    //  Add to the ArrayList
            nEdits++;
            lv_Uniques.getItems().remove(currentlyEditing);        
        }
                
        else {
            if (dataType.equals("real")) {
                Alert alert = new Alert (AlertType.INFORMATION);
                alert.setTitle("OK, so you messed up a bit here...");
                alert.setHeaderText("    Your fix does not appear to be a number.");
                alert.setContentText("       Let's try again with a different fix...");
                alert.showAndWait();
            }
            else {  //  CATEGORICAL
                Alert alert = new Alert (AlertType.INFORMATION);
                alert.setTitle("OK, so you messed up a bit here...");
                alert.setHeaderText("You are cleaning, not creating values!");
                alert.setContentText("    If there is a legal choice in the list, retry now; if not" + 
                                      "\n    you will have to fix this one back in the spreadsheet.");
                alert.showAndWait();
            }
        }
    }   //  end editCommit   
        
    // Find and collect the unique values
    private void collectUniqueValues() {
        
        Map<String, Integer> mapOfStrings = new HashMap<String, Integer>();
        for (int c = 0; c < nDataToClean; c++) {
            if (mapOfStrings.containsKey(str_CopyOfOriginalData[c])) {
                int value = mapOfStrings.get(str_CopyOfOriginalData[c]);
                mapOfStrings.put(str_CopyOfOriginalData[c], value + 1);
            } else {
                mapOfStrings.put(str_CopyOfOriginalData[c], 1);
            }
        }
        
        nUniques = mapOfStrings.size();
        Set<Map.Entry<String, Integer>> entrySet = mapOfStrings.entrySet();
        finalCategories = new String[nUniques];
        categoryCount = new int[nUniques];
        int index = 0;
        
        for (Map.Entry<String, Integer> entry: entrySet) {
            finalCategories[index] = entry.getKey();
            categoryCount[index] = entry.getValue();
            index++;
        }
    }   //  End collectUniqueValues
    
    public boolean check4Double(String allegedDouble) {
        double tempDouble;
        boolean doubleQuery = true;
        String tempText = allegedDouble;
        try {
            tempDouble = Double.valueOf(tempText);
        } catch (NumberFormatException e) {
            doubleQuery = false;
        }
        return doubleQuery;
    }   //  end stringIsADouble
    
    public int getNUniques() { return nUniques; }
    public String[] getCleanStrings() { return str_CopyOfOriginalData; }
    public int[] getCategoryCount() { return categoryCount; }
    public String[] getFinalCategories() { return finalCategories; }
    public boolean getDoubleTrouble() { return doubleTrouble; }
    public int getNStringsToClean() { return nStringsToClean; }
}


