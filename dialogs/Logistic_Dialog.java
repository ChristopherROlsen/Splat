/************************************************************
 *                       Logistic_Dialog                    *
 *                          11/10/18                        *
 *                            21:00                         *
 ***********************************************************/
package dialogs;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import splat.Splat_DataManager;
import genericClasses.*;

public class Logistic_Dialog extends Two_Variables_Dialog{ 
    
    String defOfSuccess;
    Label successLabel;
    TextField successDef;
    
    public Logistic_Dialog(Splat_DataManager myData, String variableType) {
        super(myData, "Quantitative", "Quantitative");
        
        minSampleSize = 3;
        lbl_Title.setText("Logistic Regression");
        lblFirstVar.setText("Explanatory variable:");
        lblSecondVar.setText("Zero-One variable:");
        successLabel = new Label("   Def of success:");
        successDef = new TextField("");
        
        successDef.setOnAction((ActionEvent event) -> {
            defOfSuccess = successDef.getText();
            defOfSuccess = StringUtilities.truncateString(defOfSuccess, 12);
            okButton.requestFocus();
        });
        
        resetButton.setOnAction((ActionEvent event) -> {
            listOfVars.resetList();
            tf_FirstVar.setText("");
            tf_SecondVar.setText("");
            successDef.setText("");
        });
        
        gridChoicesMade.add(successLabel, 1, 2);
        gridChoicesMade.add(successDef, 1, 3);
        
        setTitle("Logistic Regression");
        showAndWait();
    }  
    
    public String getSuccessDef() { return defOfSuccess; }

}

