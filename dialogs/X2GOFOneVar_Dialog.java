/************************************************************
 *                       X2GOFOneVar_Dialog                 *
 *                          12/09/18                        *
 *                            18:00                         *
 ***********************************************************/
package dialogs;

import javafx.scene.control.CheckBox;
import splat.Splat_DataManager;

public class X2GOFOneVar_Dialog extends One_Variable_Dialog{ 
    
    public X2GOFOneVar_Dialog(Splat_DataManager myData, String variableType) {
        super(myData, "CATEGORICAL"); 
        System.out.println("16 X2Gof1Var");
        minSampleSize = 3;
        lbl_Title.setText("Chi square Goodness Of Fit");
        lblFirstVar.setText("X Variable:");
        defineTheCheckBoxes();
        leftPanel.getChildren().addAll(dashBoardOptions);
        setTitle("Chi square Goodness Of Fit");
        showAndWait();
    }  
    
    protected void defineTheCheckBoxes() {
        // Check box strings must match the order of dashboard strings
        // Perhaps pass them to dashboard in future?
        nCheckBoxes = 4;
        String[] chBoxStrings = { " Best fit line ", " Residuals ",
                                         " RegrReport ", " DiagReport "}; 
        dashBoardOptions = new CheckBox[nCheckBoxes];
        for (int ithCBx = 0; ithCBx < nCheckBoxes; ithCBx++) {
            dashBoardOptions[ithCBx] = new CheckBox(chBoxStrings[ithCBx]);
        }
    } 
}
