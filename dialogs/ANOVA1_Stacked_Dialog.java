/************************************************************
 *                   ANOVA1_Stacked_Dialog                  *
 *                          11/12/18                        *
 *                            00:00                         *
 ***********************************************************/
package dialogs;

import splat.Data_Manager;

public class ANOVA1_Stacked_Dialog extends Two_Variables_Dialog{ 
    
    public ANOVA1_Stacked_Dialog(Data_Manager dm) {
        super(dm, "Categorical", "Quantitative");
        
        minSampleSize = 3;
        lbl_Title.setText("Analysis of Variance (Stacked data)");
        lblFirstVar.setText("Group / Treatment Variable:");
        lblSecondVar.setText("            Data Variable:");
        setTitle("One way ANOVA");
        showAndWait();
    }  
}
