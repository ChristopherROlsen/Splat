/************************************************************
 *                      Explore_2Ind_Dialog                 *
 *                           11/10/18                       *
 *                            03:00                         *
 ***********************************************************/
package dialogs;

import splat.Splat_DataManager;

public class Explore_2Ind_Dialog extends Two_Variables_Dialog{ 
    
    public Explore_2Ind_Dialog(Splat_DataManager dm, String variableType) {
        super(dm, "QUANTITATIVE", "QUANTITATIVE");
        
        minSampleSize = 3;
        lbl_Title.setText("Explore 2 Independent variables");
        lblFirstVar.setText("1st Variable:");
        lblSecondVar.setText("2nd Variable:");
        setTitle("Explore 2 Independent Variables");
        showAndWait();
    }  
}
