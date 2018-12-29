/**************************************************
 *            Independent_t_Procedure             *
 *                    12/09/18                    *
 *                     03:00                      *
 *************************************************/
package t_Procedures;

import dataObjects.CatQuantDataVariable;
import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import javafx.scene.control.TextArea;
import splat.*;
import dialogs.*;

public class Indep_t_procedure {
    // POJOs  
    int nColumnsOfData;
    String daProcedure, returnStatus;
        
    // My classes
    ArrayList<ColumnOfData> indivColsOfData;
    ArrayList<String> varLabel = new ArrayList();
    Indep_t_Dialog ind_t_Dialog;
    CatQuantDataVariable cqdv;
    Indep_t_Model independent_t_Model;
    Indep_t_PrepStructs indep_t_prepStructs;    
    QuantitativeDataVariable tempQDV;
    ArrayList<QuantitativeDataVariable> allTheQDVs;
    Data_Manager dm;
    TextArea myText;

    // ******  Constructor called from Main Menu  ******
    public Indep_t_procedure(Data_Manager dm) {
        this.dm = dm; 
        ArrayList<ColumnOfData> indivColsOfData;
        ArrayList<String> varLabel = new ArrayList();
    }

    // ******                 Called from Main Menu                 ******    
    public String chooseTheStructureOfData() {
            StructOfRawData_Dialog genChoice = new StructOfRawData_Dialog();
            String returnStatus = genChoice.getReturnStatus();
            if (returnStatus.equals("Cancel")) { return returnStatus; }
            String theChoice = genChoice.getTheChoice();
            if (theChoice.equals("Data is in separate columns")) {
                prepColumnsFromNonStacked();
            }
            if (theChoice.equals("Data is summarized")) {
                Indep_t_SummaryStats_procedure ind_t_SummaryStats_Proc = new Indep_t_SummaryStats_procedure();
                returnStatus = ind_t_SummaryStats_Proc.doTheProcedure();
            } 
            
           if (theChoice.equals("Data is stacked")) {
                System.out.println("Data is stacked");
                prepColumnsFromStacked();
            }   
           return returnStatus;
    }
    
    private void prepColumnsFromStacked() {
        //System.out.println("63 inty proc, prepColumnsFromStacked()");
        T_ProcedureStackedDialog tProcStacked = new T_ProcedureStackedDialog( dm );
        indivColsOfData = tProcStacked.getData();
        nColumnsOfData = indivColsOfData.size();
        //System.out.println("68 indt procedure, nColumnsOfData = " + nColumnsOfData);
        cqdv = new CatQuantDataVariable(dm, indivColsOfData.get(0), indivColsOfData.get(1));
        cqdv.unstackToDataStruct();
    }
    
    public String prepColumnsFromNonStacked() {
        // prob = new Distributions();
        returnStatus = "Cancel";
        ind_t_Dialog = new Indep_t_Dialog(dm, "QUANTITATIVE");
        indivColsOfData = ind_t_Dialog.getData();
        nColumnsOfData = indivColsOfData.size();
        for (int ith = 0; ith < nColumnsOfData; ith++) {
            System.out.println("90 indt proc");
            // indivColsOfData.get(ith).toString();           
        } 

        returnStatus = doTheProcedure();
        return returnStatus;

    } 
    
    private String doTheProcedure() {
        String varName = "All";
        ArrayList<String> tempAlStr = new ArrayList<>();
        for (int ith = 0; ith < nColumnsOfData; ith++) {
            ColumnOfData tempCol = indivColsOfData.get(ith);
            int nColSize = tempCol.getColumnSize();
            for (int jth = 0; jth < nColSize; jth++) {
                tempAlStr.add(tempCol.getTheCases().get(jth));
            }
        }
        ColumnOfData tempCOD = new ColumnOfData(varName, tempAlStr);
        tempQDV = new QuantitativeDataVariable(tempCOD);

        allTheQDVs = new ArrayList();
        allTheQDVs.add(tempQDV);

        for (int ith = 0; ith < nColumnsOfData; ith++) {
            System.out.println("105 indt proc");
            // indivColsOfData.get(ith).toString();
            tempQDV = new QuantitativeDataVariable(indivColsOfData.get(ith)); 
            allTheQDVs.add(tempQDV);                 
        }   
        
        System.out.println("108 t_Proc, allTheQDVs.size = " + allTheQDVs.size());

        indep_t_prepStructs = new Indep_t_PrepStructs(this, allTheQDVs);
        
        independent_t_Model = new Indep_t_Model(this, allTheQDVs);
        independent_t_Model.doIndepTAnalysis();
        returnStatus = indep_t_prepStructs.showTheDashboard();
        return returnStatus;        
    }
    
    public String getHypotheses() { return ind_t_Dialog.getHypotheses(); }
    public double getHypothesizedDiff() { return ind_t_Dialog.getHypothesizedDiff(); }
    public Indep_t_PrepStructs getTStructs() { return indep_t_prepStructs; }
    
    public ArrayList<QuantitativeDataVariable> getTheQDVs() { return allTheQDVs; }

}
