/************************************************************
 *                      Regression_Dialog                   *
 *                          11/10/18                        *
 *                            21:00                         *
 ***********************************************************/
package dialogs;

import splat.Splat_DataManager;

public class Regression_Dialog extends Two_Variables_Dialog{ 
    
    public Regression_Dialog(Splat_DataManager myData, String variableType) {
        super(myData, "Quantitative", "Quantitative");
        
        minSampleSize = 3;
        lbl_Title.setText("Simple Linear Regression");
        lblFirstVar.setText("X Variable:");
        lblSecondVar.setText("Y Variable:");
        setTitle("Regression");
        showAndWait();
    }  
}
