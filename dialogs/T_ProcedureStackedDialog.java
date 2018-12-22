/************************************************************
 *                   T_Procedure_Stacked_Dialog             *
 *                          11/18/18                        *
 *                            09:00                         *
 ***********************************************************/
package dialogs;

import splat.Splat_DataManager;

// **************   Called by independent t procedure *****************
public class T_ProcedureStackedDialog extends Two_Variables_Dialog{ 
    
    public T_ProcedureStackedDialog(Splat_DataManager dm) {
        super(dm, "Categorical", "Quantitative");
        
        minSampleSize = 3;
        lbl_Title.setText("Independent t procedure (Stacked data)");
        lblFirstVar.setText("Group / Treatment Variable:");
        lblSecondVar.setText("            Data Variable:");
        setTitle("Independent t");
        showAndWait();
    }  
}
