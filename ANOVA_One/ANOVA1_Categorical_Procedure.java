/**************************************************
 *           ANOVA1_CategoricalProcedure          *
 *                    12/27/18                    *
 *                     00:00                      *
 *************************************************/
// ************************************************
//             Called by Splat_Menu               *
// ************************************************

package ANOVA_One;

import dataObjects.CatQuantDataVariable;
import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import dialogs.*;
import java.util.ArrayList;

import splat.*;

public class ANOVA1_Categorical_Procedure extends ANOVA1_Procedure {
    //  POJOs

    private ANOVA1_Model anova1Model;
    private ANOVA1_Dashboard anova1Dashboard;
    
    public ANOVA1_Categorical_Procedure(Data_Manager dm) {
        super(dm);
        this.dm = dm;
        anova1_ColsOfData = new ArrayList();
        varLabel = new ArrayList();
        returnStatus = "OK";
    }

    protected String doStacked() {
        do {
            ANOVA1_Stacked_Dialog anova1_S_Dialog = new ANOVA1_Stacked_Dialog( dm );
            anova1_ColsOfData = anova1_S_Dialog.getData();
            checkForLegalChoices = validateStackChoices();
        } while (checkForLegalChoices == false);
  
        //                                Categorical,             Quantitative
        cqdv = new CatQuantDataVariable(dm, anova1_ColsOfData.get(0), anova1_ColsOfData.get(1));    
        allTheQDVs = new ArrayList();
        allTheQDVs = cqdv.getAllQDVs();

        prepareTheStructs();
        
        return returnStatus;
    }

    protected String doNotStacked() {
        returnStatus = "Ok";
        ANOVA1_NotStacked_Dialog anova1_NS_Dialog = new ANOVA1_NotStacked_Dialog( dm );
        anova1_NS_Dialog.show_ANOVA1_NS_Dialog();
        returnStatus = anova1_NS_Dialog.getReturnStatus();
        if (!returnStatus.equals("Ok")) 
            { return returnStatus; }
        // else...
        explanatoryVariable = anova1_NS_Dialog.getExplanatoryVariable();
        responseVariable = anova1_NS_Dialog.getResponseVariable();
        anova1_ColsOfData = anova1_NS_Dialog.getData();
         
         int nColumnsOfData = anova1_ColsOfData.size();
        // Stack the columns into one, put in allTheQDVs[0]
        // Construct a ColumnOfData, make the QDV
        String varName = "All";
        ArrayList<String> tempAlStr = new ArrayList<>();
        for (int ith = 0; ith < nColumnsOfData; ith++) {
            ColumnOfData tempCol = anova1_ColsOfData.get(ith);
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
            tempQDV = new QuantitativeDataVariable(anova1_ColsOfData.get(ith));  
            allTheQDVs.add(tempQDV);                 
        } 
        
        prepareTheStructs();
        return returnStatus;
    }
    
    protected String prepareTheStructs() {
        System.out.println("148 ANOVA_Proc, daProcedure = " + daProcedure);

        n_QDVs = allTheQDVs.size();
        allTheLabels = new ArrayList<>();
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            allTheLabels.add(allTheQDVs.get(iVars).getTheDataLabel());
        }

        // The QDVs already have labels -- dump allTheLabels & get from QDV?
        anova1Model = new ANOVA1_Model(this, explanatoryVariable, responseVariable, allTheQDVs, allTheLabels); 
        anova1Dashboard = new ANOVA1_Dashboard(this, anova1Model);
        anova1Dashboard.populateTheBackGround();
        anova1Dashboard.putEmAllUp();
        anova1Dashboard.showAndWait();
        returnStatus = anova1Dashboard.getReturnStatus();
        returnStatus = "Ok";
        return returnStatus;
        
    } // OneFactor CR
    
    private boolean validateStackChoices() {
        String[] dataType = new String[2];
        for (int ithCol = 0; ithCol < 2; ithCol++){
            anova1_ColsOfData.get(ithCol).assignDataType();
            dataType[ithCol] = anova1_ColsOfData.get(ithCol).getDataTypeOfThisColumn();  
            System.out.println("116 Cat_Proc, dataType[ithCol] = " + dataType[ithCol]);
        }

        return true;
    }
}