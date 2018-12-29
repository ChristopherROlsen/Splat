/**************************************************
 *             Explore_2Ind_PrepareStructs        *
 *                    12/25/18                    *
 *                      18:00                     *
 *************************************************/
package proceduresTwoUnivariate;

import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import splat.Data_Manager;
import proceduresOneUnivariate.*;

public class Explore_2Ind_PrepareStructs  
{ 
    // POJOs
    int nUnchecked, nChecked;
    String returnStatus, subTitle;
    
    // My classes
    BBSL_Model bbsl_Model;
    private final Explore_2Ind_Dashboard xPlore_2Ind_Dashboard;
    HorizontalBoxPlot_Model hBox_Model;
    QQPlot_Model qqPlot_Model;
    Data_Manager dm;
    ArrayList<QuantitativeDataVariable> theThreeQDVs, allTheQDVs;
    VerticalBoxPlot_Model vBox_Model;

    public Explore_2Ind_PrepareStructs(Explore_2Ind_Procedure ex2Proc, Data_Manager dm, ArrayList<QuantitativeDataVariable> allTheQDVs) { 
        subTitle = ex2Proc.getSubTitle();
        hBox_Model = new HorizontalBoxPlot_Model(this, subTitle, allTheQDVs);
        vBox_Model = new VerticalBoxPlot_Model(this, subTitle, allTheQDVs);
        qqPlot_Model = new QQPlot_Model(this, subTitle, allTheQDVs);
        bbsl_Model = new BBSL_Model(this, subTitle, allTheQDVs);

        xPlore_2Ind_Dashboard = new Explore_2Ind_Dashboard(this, allTheQDVs);
    }  
    
    public String showTheDashboard() {
        xPlore_2Ind_Dashboard.populateTheBackGround();
        xPlore_2Ind_Dashboard.putEmAllUp();
        xPlore_2Ind_Dashboard.showAndWait();
        returnStatus = xPlore_2Ind_Dashboard.getReturnStatus();
        return returnStatus;           
    }
    
    HorizontalBoxPlot_Model getHBox_Model() { return hBox_Model; }
    VerticalBoxPlot_Model getVBox_Model() { return vBox_Model; }
    QQPlot_Model getQQ_Model() { return qqPlot_Model; }
    BBSL_Model getBBSL_Model() { return bbsl_Model; }  
    
    public String getSubTitle() { return subTitle; }
    
    public String getReturnStatus() { return returnStatus; }
}
