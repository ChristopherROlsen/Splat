/************************************************************
 *                      Regression_Dialog                   *
 *                          12/25/18                        *
 *                            21:00                         *
 ***********************************************************/
package dialogs;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import splat.Data_Manager;

public class Regression_Dialog extends Two_Variables_Dialog{ 
    
    String strSaveTheResids;
    CheckBox cbxSaveTheResids;
    
    public Regression_Dialog(Data_Manager myData, String variableType) {
        super(myData, "Quantitative", "Quantitative");
        lbl_Title.setText("Simple Linear Regression");
        lblFirstVar.setText("X Variable:");
        lblSecondVar.setText("Y Variable:");
        setTitle("Regression");
        
        cbxSaveTheResids = new CheckBox("Save the residuals?");
        cbxSaveTheResids.selectedProperty().addListener(this::changed);
        cbxSaveTheResids.setSelected(false);
        strSaveTheResids = "No";
        gridChoicesMade.add(cbxSaveTheResids, 0, 5);
        showAndWait();
    }        
    
    public void changed(ObservableValue < ? extends Boolean> observable,
            Boolean oldValue,
            Boolean newValue) {
        String state = null;
        if (cbxSaveTheResids.isSelected() ) {
            strSaveTheResids = "Yes";
        }
        else
        { 
            strSaveTheResids = "No"; 
        }
    }
    
    public String getSaveTheResids() { return strSaveTheResids; }
}
