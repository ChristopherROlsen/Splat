/**************************************************
 *         Single_t_SummaryStats_procedure        *
 *                    12/08/18                    *
 *                     12:00                      *
 *************************************************/
package t_Procedures;

import genericClasses.CatQuantDataVariable;
import javafx.scene.control.TextArea;
import splat.*;
import dialogs.*;

public class Single_t_SummaryStats_procedure {
    // POJOs
    boolean finished = false;   
    
    String daProcedure;
        
    // My classes
    OneMean_SummaryStats_Dialog oneMean_SummaryStats_Dialog;
    Single_t_SummaryStats_Model single_t_SummaryStats_Model;
    Single_t_PrepStructs single_t_prepStructs;    
    Splat_DataManager dataManager;
    TextArea myText;

    public Single_t_SummaryStats_procedure(/* Splat_DataManager dataManager */) {
        // this.dataManager = dataManager;
    }
    
    public boolean doTheProcedure() {
        finished = false;
        oneMean_SummaryStats_Dialog = new OneMean_SummaryStats_Dialog();
        single_t_SummaryStats_Model = new Single_t_SummaryStats_Model(this, oneMean_SummaryStats_Dialog);
        single_t_SummaryStats_Model.doSingleTAnalysis();
        finished = single_t_prepStructs.showTheDashboard();
        return finished;
    } // doTheProcedure
    
    public double getAlpha() { return oneMean_SummaryStats_Dialog.getAlpha(); }
    public String getHypotheses() { return oneMean_SummaryStats_Dialog.getHypotheses(); }
    public double getHypothesizedMean() { return oneMean_SummaryStats_Dialog.getHypothesizedDiff(); }

}
