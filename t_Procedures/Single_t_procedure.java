/**************************************************
 *                Single_t_Procedure              *
 *                    12/25/18                    *
 *                     15:00                      *
 *************************************************/
package t_Procedures;

import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import javafx.scene.control.TextArea;
import splat.*;
import dialogs.*;

public class Single_t_procedure {
    // POJOs
    boolean ok = true;   
    String daProcedure;
        
    // My classes
    ColumnOfData colOfData;
    String varLabel, descriptionOfVariable;
    Single_t_Dialog single_t_Dialog;
    Single_t_Model single_t_Model;
    Single_t_PrepStructs single_t_prepStructs;    
    QuantitativeDataVariable theQDV;
    Data_Manager dm;
    TextArea myText;

    // ******  Constructor called from Main Menu  ******
    public Single_t_procedure(Data_Manager dm) {
        this.dm = dm; 
        System.out.println("32 Constructing Single_t_Proc");
    }

    // ******                 Called from Main Menu                 ******   
    /*
    public boolean chooseTheStructureOfData() {
            System.out.println("51 ANOVA1_Proc, doTheProcedure()");
            StructOfRawData_Dialog genChoice = new StructOfRawData_Dialog();
            
            String theChoice = genChoice.getTheChoice();
            if (theChoice.equals("Data is in separate columns")) {
                prepColumnsFromNonStacked();
            }
            if (theChoice.equals("Data is summarized")) {
                Indep_t_SummaryStats_procedure ind_t_SummaryStats_Proc = new Indep_t_SummaryStats_procedure();
                ok = ind_t_SummaryStats_Proc.doTheProcedure();
            } 
            
           if (theChoice.equals("Data in one, group in one")) {
                System.out.println("Data in one, group in one");
                prepColumnsFromStacked();
            }   
           return ok;
    }
*/
    
    public boolean prepColumnsFromNonStacked() {
        // prob = new Distributions();
        ok = false;
        single_t_Dialog = new Single_t_Dialog(dm, "QUANTITATIVE");
        descriptionOfVariable = single_t_Dialog.getDescriptionOfVariable();
        colOfData = single_t_Dialog.getData(); 
        theQDV = new QuantitativeDataVariable(colOfData);
        ok = doTheProcedure();
        return ok;

    } 
    
    private boolean doTheProcedure() {
        System.out.println("70 Single_t_Proc calling prepStructs");
        single_t_prepStructs = new Single_t_PrepStructs(this, theQDV);
        
        single_t_Model = new Single_t_Model(this, theQDV);
        single_t_Model.doTAnalysis();
        ok = single_t_prepStructs.showTheDashboard();
        return ok;        
    }
    
    public String getDescriptionOfVariable() { return descriptionOfVariable; }
    public String getHypotheses() { return single_t_Dialog.getHypotheses(); }
    public double getHypothesizedMean() { return single_t_Dialog.getHypothesizedMean(); }
    public Single_t_PrepStructs getTStructs() { return single_t_prepStructs; }
    
    public QuantitativeDataVariable getTheQDVs() { return theQDV; }

}

