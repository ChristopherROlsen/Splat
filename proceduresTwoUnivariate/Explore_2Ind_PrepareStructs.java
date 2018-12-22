/**************************************************
 *             Explore_2Ind_PrepareStructs        *
 *                    11/10/18                    *
 *                      03:00                     *
 *************************************************/
package proceduresTwoUnivariate;

import genericClasses.QuantitativeDataVariable;
import java.util.ArrayList;
import splat.Splat_DataManager;
import proceduresOneUnivariate.*;

public class Explore_2Ind_PrepareStructs  
{ 
    // POJOs
    int nUnchecked, nChecked;
    String returnStatus;
    
    // My classes
    BBSL_Model bbsl_Model;
    private final Explore_2Ind_Dashboard xPlore_2Ind_Dashboard;
    HorizontalBoxPlot_Model hBox_Model;
    QQPlot_Model qqPlot_Model;
    Splat_DataManager dm;
    ArrayList<QuantitativeDataVariable> theThreeQDVs, allTheQDVs;
    VerticalBoxPlot_Model vBox_Model;

    public Explore_2Ind_PrepareStructs(Splat_DataManager dm, ArrayList<QuantitativeDataVariable> allTheQDVs) { 
        hBox_Model = new HorizontalBoxPlot_Model(allTheQDVs);
        vBox_Model = new VerticalBoxPlot_Model(allTheQDVs);
        qqPlot_Model = new QQPlot_Model(allTheQDVs);
        bbsl_Model = new BBSL_Model(allTheQDVs);
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
    
    public String getReturnStatus() { return returnStatus; }
}
