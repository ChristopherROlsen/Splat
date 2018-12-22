/************************************************************
 *                    StructOfRawData_Dialog                *
 *                          12/09/18                        *
 *                            00:00                         *
 ***********************************************************/
package dialogs;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

    // *****************************************************************
    // *  Called by:                                                   *
    // *      Independent_t_Procedure                                  *
    // *      ANOVA1_Procedure                                         *
    // *****************************************************************

public class StructOfRawData_Dialog extends Splat_Dialog {
    
    String theChoice;
    String selectedLabel;
    Label msg;
    RadioButton sepColumns, stacked, summarized;
    ToggleGroup group;
    VBox buttonBox;
    
    // *****************************************************************
    // *  Called by:                                                   *
    // *      Independent_t_Procedure                                  *
    // *      ANOVA1_Procedure                                         *
    // *****************************************************************
    
    public StructOfRawData_Dialog() {
        
        sepColumns = new RadioButton("Data is in separate columns");
        stacked = new RadioButton("Data is stacked");
        summarized = new RadioButton("Data is summarized");
        // userSelectionMsg = new Label("How is your data structured?");    
        // Add all buttons to a toggle group
        group = new ToggleGroup();
        group.getToggles().addAll(sepColumns, stacked, summarized);
        group.selectedToggleProperty().addListener(this::changed);
        sepColumns.setSelected(true);
  
        msg = new Label("How is your data structured?");
        buttonBox = new VBox(sepColumns, stacked, summarized);
        buttonBox.setSpacing(10);
        
        HBox buttonPanel = new HBox(10);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.setPadding(new Insets(10, 10, 10, 10));

        okButton.setText("OK!");
        cancelButton.setText("Cancel");
        buttonPanel.getChildren().addAll(okButton, cancelButton);
        
        VBox root = new VBox(msg, buttonBox, buttonPanel);
        root.setMinWidth(300);
        root.setMaxWidth(300);
        root.setMinHeight(175);
        root.setMaxHeight(175);
        root.setSpacing(10);
        
        root.setStyle("-fx-padding: 10;" +
                      "-fx-border-style: solid inside;" +
                      "-fx-border-width: 2;" +
                      "-fx-border-insets: 5;" +
                      "-fx-border-radius: 5;" +
                      "-fx-border-color: blue;");
        
        Scene scene = new Scene(root);
        setScene(scene);
        setTitle("Data structure inquiry...");
        showAndWait();
    }
    
    // A change listener to track the selection in the group
    public void changed(ObservableValue<? extends Toggle> observable,
                        Toggle oldBtn,
                        Toggle newBtn) {
        selectedLabel = "None";
        if (newBtn != null) {
            selectedLabel = ((Labeled)newBtn).getText();
        }
        //userSelectionMsg.setText("Your selection: " + selectedLabel);
    }
    
    public String getTheChoice() { return selectedLabel; }
}
