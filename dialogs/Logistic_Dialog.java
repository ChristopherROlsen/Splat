/************************************************************
 *                       Logistic_Dialog                    *
 *                          12/24/18                        *
 *                            15:00                         *
 ***********************************************************/
package dialogs;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import splat.Data_Manager;

public class Logistic_Dialog extends Two_Variables_Dialog{ 
    
    String defOfSuccess;
    Label successLabel;
    TextField successDef;
    
    public Logistic_Dialog(Data_Manager myData, String variableType) {
        super(myData, "Quantitative", "Quantitative");
        lbl_Title.setText("Logistic Regression");
        lblFirstVar.setText("Explanatory variable:");
        lblSecondVar.setText("Zero-One variable:");

        resetButton.setOnAction((ActionEvent event) -> {
            listOfVars.resetList();
            tf_FirstVar.setText("");
            tf_SecondVar.setText("");
            successDef.setText("");
        });
        
        setTitle("Logistic Regression");
        showAndWait();
    }  

}

