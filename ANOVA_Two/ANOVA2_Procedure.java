/**************************************************
 *              ANOVA2_Procedure                  *
 *                  11/04/18                      *
 *                   18:00                        *
 *************************************************/
package ANOVA_Two;

import genericClasses.CategoricalDataVariable;
import genericClasses.QuantitativeDataVariable;
import dialogs.TwoFactor_Dialog;
import java.util.ArrayList;
import splat.*;

public class ANOVA2_Procedure {
    
    // POJOs
    boolean dataAreBalanced, replicatesExist, dataAreMissing;
    
    int nReplications;
            
    String whichANOVA2, returnStatus;
    
    // My classes
    ANOVA2_Dashboard anova2_Dashboard;
    ANOVA2_Model anova2_Model;
    RCB_Dashboard rcb_Dashboard;
    RCB_Model rcb_Model;
            
    public ANOVA2_Procedure (Splat_DataManager dm, String whichANOVA2) {
        this.whichANOVA2 = whichANOVA2;
        TwoFactor_Dialog dialog_TwoFactor = new TwoFactor_Dialog(dm, whichANOVA2);
        dialog_TwoFactor.Step0();
        
        if (dialog_TwoFactor.getReturnStatus().equals("Cancel")) {
            returnStatus = "Cancel";
            return;
        }

        int numRows = dialog_TwoFactor.getNumRows();
        int numCols = dialog_TwoFactor.getNumCols();

        String[] rowLevels = dialog_TwoFactor.getRowLabels();
        String[] colLevels = dialog_TwoFactor.getColLabels();  
        
        String rowName = dialog_TwoFactor.getRowName();
        String colName = dialog_TwoFactor.getColName();   
        
        // data is a row x col ArrayList of values;
        ArrayList <String> [][] data = dialog_TwoFactor.getData();
     
        int nDataPoints = 0;
        int maxReps = 0;
        int minReps = 9999;
        int [][] replications = new int[numRows][numCols];

        for (int i = 0; i < numRows; i++) { // Rows -- levels of A
            for (int j = 0; j < numCols; j++) { // Columns -- levels of B
                nReplications = data[j][i].size();
                if (nReplications == 0) {
                    //  missingDataAlert
                    // Abort
                }
                replications[i][j] = nReplications;
                minReps = Math.min(minReps, nReplications);
                maxReps = Math.max(maxReps, nReplications);
                nDataPoints += nReplications;
            } // j loop
        } // i loop  
        
        dataAreBalanced = true;
        if (minReps < maxReps) {
            dataAreBalanced = false;
        }
        replicatesExist = false;
        if (dataAreBalanced == true && minReps > 1) {
            replicatesExist = true;
        }

        String[] columnVar = new String[nDataPoints];
        String[] rowVar= new String[nDataPoints];
        ArrayList<String> responseVar= new ArrayList();
        
        dataAreMissing = false;
        String asterisk = "*";
        int ithDataIndex = 0;
        for (int i = 0; i < numRows; i++) { // Rows -- levels of A
            for (int j = 0; j < numCols; j++) { // Columns -- levels of B
                nReplications = data[j][i].size();
                for (int k = 0; k < nReplications; k++) {
                    rowVar[ithDataIndex] = rowLevels[i];
                    columnVar[ithDataIndex] = colLevels[j];
                    String dataValue = data[j][i].get(k);
                    if (dataValue.equals(asterisk)) {
                        dataAreMissing = true;
                    }
                    responseVar.add(dataValue);
                    ithDataIndex++;      
                }
            } // j loop
        } // i loop 
        
        if (dataAreMissing == true) {
            // Alert
            // Abort
        }
        
        CategoricalDataVariable dataVarColumn = new CategoricalDataVariable(colName, columnVar);
        CategoricalDataVariable dataVarRow = new CategoricalDataVariable(rowName, rowVar);        
        QuantitativeDataVariable dataVarResponse = new QuantitativeDataVariable("Response", responseVar); 
        
        switch (whichANOVA2) {

            case "Factorial":
                anova2_Model = new ANOVA2_Model(dm,
                                               this, 
                                               dataVarColumn,
                                               dataVarRow,
                                               dataVarResponse); 

                anova2_Dashboard = new ANOVA2_Dashboard(this, anova2_Model);
                anova2_Dashboard.populateTheBackGround();
                anova2_Dashboard.putEmAllUp();
                anova2_Dashboard.showAndWait();
                break;  
            
            case "RCB":     // Randomized complete block
                
                rcb_Model = new RCB_Model( dm,
                                           this, 
                                           dataVarColumn,
                                           dataVarRow,
                                           dataVarResponse); 

                rcb_Dashboard = new RCB_Dashboard(this, rcb_Model);
                rcb_Dashboard.populateTheBackGround();
                rcb_Dashboard.putEmAllUp();
                rcb_Dashboard.showAndWait();
                break;
                
            case "Repeat":     // Repeated measures
                
                // if replicates, need to Alert and Abort
                
                rcb_Model = new RCB_Model( dm,
                                           this, 
                                           dataVarColumn,
                                           dataVarRow,
                                           dataVarResponse); 

                rcb_Dashboard = new RCB_Dashboard(this, rcb_Model);
                rcb_Dashboard.populateTheBackGround();
                rcb_Dashboard.putEmAllUp();
                rcb_Dashboard.showAndWait();
                break;      
        }

    } // TwoFactor CR    
    
    public boolean getDataAreBalanced() { return dataAreBalanced; }
    public boolean getReplicatesExist() { return replicatesExist; }
    public int getNReplications() {return nReplications; }
    public String getReturnStatus() { return returnStatus; }
    // public boolean getDataAreMissing() { return dataAreMissing; }
}
