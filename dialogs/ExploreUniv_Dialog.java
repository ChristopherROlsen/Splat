/************************************************************
 *                      ExploreUniv_Dialog                  *
 *                          12/08/18                        *
 *                            03:00                         *
 ***********************************************************/
package dialogs;

import splat.Data_Manager;

public class ExploreUniv_Dialog extends One_Variable_Dialog {
    
    public ExploreUniv_Dialog(Data_Manager myData, String variableType) {
        super(myData, "Quantitative");
        // System.out.println("16 EU-Dialog");
        minSampleSize = 3;
        //titleLabel.setText("Univariate Data Exploration");
        //vLabel2.setText("X Variable:");
        // vLabel3.setText("Y Variable:");
        // defineTheCheckBoxes();
        // leftPanel.getChildren().addAll(dashBoardOptions);
        setTitle("Univariate Data Exploration");
        showAndWait();
    }   
}
