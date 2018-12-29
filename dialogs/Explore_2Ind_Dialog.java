/************************************************************
 *                      Explore_2Ind_Dialog                 *
 *                           12/25/18                       *
 *                            15:00                         *
 ***********************************************************/
package dialogs;

import splat.Data_Manager;

public class Explore_2Ind_Dialog extends Two_Variables_Dialog{ 
    
    public Explore_2Ind_Dialog(Data_Manager dm, String variableType) {
        super(dm, "QUANTITATIVE", "QUANTITATIVE");
        lbl_Title.setText("Explore 2 Independent variables");
        lblFirstVar.setText("Title for graphs:");
        gridChoicesMade.getChildren().remove(lblResponseVar);
        gridChoicesMade.getChildren().remove(tfResponseVar);
        setTitle("Explore 2 Independent Variables");
        showAndWait();
    }  
}
