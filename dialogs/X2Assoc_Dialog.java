/************************************************************
 *                       X2Assoc_Dialog                     *
 *                          11/10/18                        *
 *                            21:00                         *
 ***********************************************************/
package dialogs;

import splat.Splat_DataManager;

public class X2Assoc_Dialog extends Two_Variables_Dialog{ 
    
    public X2Assoc_Dialog(Splat_DataManager myData, String variableType) {
        super(myData, "Categorical", "Categorical");
        minSampleSize = 3;
        lbl_Title.setText("Chi square association");
        lblFirstVar.setText("X Variable:");
        lblSecondVar.setText("Y Variable:");
        // defineTheCheckBoxes();
        leftPanel.getChildren().addAll(dashBoardOptions);
        setTitle("Chi square association");
        showAndWait();
    }  
}
