/**************************************************
 *      Independent_t_SummaryStats_procedure      *
 *                    12/09/18                    *
 *                     03:00                      *
 *************************************************/
package t_Procedures;

import genericClasses.CatQuantDataVariable;
import javafx.scene.control.TextArea;
import splat.*;
import dialogs.*;

public class Indep_t_SummaryStats_procedure {
    // POJOs  
    
    String daProcedure, returnStatus;
        
    // My classes
    TwoMeans_SummaryStats_Dialog ind_t_SummaryStats_Dialog;
    CatQuantDataVariable cqdv;
    Indep_t_SummaryStats_Model independent_t_SummaryStats_Model;
    Indep_t_PrepStructs indep_t_prepStructs;    
    Splat_DataManager dataManager;
    TextArea myText;

    public Indep_t_SummaryStats_procedure(/* Splat_DataManager dataManager */) {
        // this.dataManager = dataManager;
    }
    
    public String doTheProcedure() {
        returnStatus = "Cancel";
        ind_t_SummaryStats_Dialog = new TwoMeans_SummaryStats_Dialog();
        independent_t_SummaryStats_Model = new Indep_t_SummaryStats_Model(this, ind_t_SummaryStats_Dialog);
        independent_t_SummaryStats_Model.doIndepTAnalysis();
        //finished = indep_t_prepStructs.showTheDashboard();
        returnStatus = "Finished";
        return returnStatus;
    } // doTheProcedure
    
    public double getAlpha() { return ind_t_SummaryStats_Dialog.getAlpha(); }
    public String getHypotheses() { return ind_t_SummaryStats_Dialog.getHypotheses(); }
    public double getHypothesizedDiff() { return ind_t_SummaryStats_Dialog.getHypothesizedDiff(); }

}
