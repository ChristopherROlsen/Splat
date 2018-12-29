/**************************************************
 *              Single_t_PrepareStructs           *
 *                    12/25/18                    *
 *                      12:00                     *
 *************************************************/
package t_Procedures;

import dataObjects.QuantitativeDataVariable;
import proceduresOneUnivariate.*;
import proceduresTwoUnivariate.*;

public class Single_t_PrepStructs  
{ 
    // POJOs
    int nUnchecked, nChecked;
    
    String descriptionOfVariable;
    // My classes
    StemNLeaf_Model stemNLeaf_Model;
    private final Single_t_Dashboard single_t_Dashboard;
    Single_t_procedure single_t_procedure;
    HorizontalBoxPlot_Model hBox_Model;
    QQPlot_Model qqPlot_Model;
    QuantitativeDataVariable theQDV;
    VerticalBoxPlot_Model vBox_Model;
    Single_t_Model single_t_Model;

    public Single_t_PrepStructs(Single_t_procedure single_t_procedure, QuantitativeDataVariable theQDV) { 
        // descriptionOfVariable = single_t_procedure.g
        hBox_Model = new HorizontalBoxPlot_Model(descriptionOfVariable, theQDV);
        vBox_Model = new VerticalBoxPlot_Model(descriptionOfVariable, theQDV);
        System.out.println("31 Constructing Single_t_PrepStructs");
        // ****************************************************************
        // *  The stemNLeaf_Model parameters are also supporting a back-  *
        // *  to-back stem and leaf plot.                                 *
        // ****************************************************************
        stemNLeaf_Model = new StemNLeaf_Model("Null", theQDV, false, 0, 0, 0);
        this.single_t_procedure = single_t_procedure;
        single_t_Model = new Single_t_Model(single_t_procedure, theQDV);
        single_t_Model.doTAnalysis();
        single_t_Dashboard = new Single_t_Dashboard(this, theQDV);
    }  
    
    public boolean showTheDashboard() {
        boolean finished = false;
        String returnStatus;
        single_t_Dashboard.populateTheBackGround();
        single_t_Dashboard.putEmAllUp();
        single_t_Dashboard.showAndWait();
        returnStatus = single_t_Dashboard.getReturnStatus();
        finished = true;
        if (finished == true)
            return finished;           
        // ************************************************************
        return finished;           
    }
    
    HorizontalBoxPlot_Model getHBox_Model() { return hBox_Model; }
    VerticalBoxPlot_Model getVBox_Model() { return vBox_Model; }
    StemNLeaf_Model getStemNLeaf_Model() { return stemNLeaf_Model; }    
    Single_t_Model getSingleTModel() { return single_t_Model; }
}

