/**************************************************
 *            Independent_t_PrepareStructs        *
 *                    12/25/18                    *
 *                      15:00                     *
 *************************************************/
package t_Procedures;

import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import proceduresOneUnivariate.*;
import proceduresTwoUnivariate.*;

public class Indep_t_PrepStructs  
{ 
    // POJOs
    int nUnchecked, nChecked;
    
    // My classes
    BBSL_Model bbsl_Model;
    private final Indep_t_Dashboard indep_t_Dashboard;
    Indep_t_procedure indep_t_procedure;
    HorizontalBoxPlot_Model hBox_Model;
    QQPlot_Model qqPlot_Model;
    ArrayList<QuantitativeDataVariable> theThreeQDVs, allTheQDVs;
    VerticalBoxPlot_Model vBox_Model;
    Indep_t_Model indep_t_Model;

    public Indep_t_PrepStructs(Indep_t_procedure indep_t_procedure, ArrayList<QuantitativeDataVariable> allTheQDVs) { 
        
        hBox_Model = new HorizontalBoxPlot_Model(this, "Null", allTheQDVs);
        vBox_Model = new VerticalBoxPlot_Model(this, "Null", allTheQDVs);
        qqPlot_Model = new QQPlot_Model(this, "Null", allTheQDVs);
        bbsl_Model = new BBSL_Model(this, "Null", allTheQDVs);
        this.indep_t_procedure = indep_t_procedure;
        indep_t_Model = new Indep_t_Model(indep_t_procedure, allTheQDVs);
        indep_t_Model.doIndepTAnalysis();
        indep_t_Dashboard = new Indep_t_Dashboard(this, allTheQDVs);
    }  
    
    public String showTheDashboard() {;
        String returnStatus;
        indep_t_Dashboard.populateTheBackGround();
        indep_t_Dashboard.putEmAllUp();
        indep_t_Dashboard.showAndWait();
        returnStatus = indep_t_Dashboard.getReturnStatus();
        returnStatus = "Ok";
        if (returnStatus.equals("Ok"))
            return returnStatus; 
        // ************************************************************
        return returnStatus;           
    }
    
    HorizontalBoxPlot_Model getHBox_Model() { return hBox_Model; }
    VerticalBoxPlot_Model getVBox_Model() { return vBox_Model; }
    QQPlot_Model getQQ_Model() { return qqPlot_Model; }
    BBSL_Model getBBSL_Model() { return bbsl_Model; }    
    Indep_t_Model getIndepTModel() { return indep_t_Model; }
}
