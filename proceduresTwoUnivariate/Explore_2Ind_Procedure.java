/************************************************************
 *                   Explore2Ind__Procedure                 *
 *                          12/28/18                        *
 *                            21:00                         *
 ***********************************************************/
package proceduresTwoUnivariate;

import dialogs.*;
import dataObjects.ColumnOfData;
import java.util.ArrayList;
import dataObjects.QuantitativeDataVariable;
import splat.Data_Manager;
import utilityClasses.*;

public class Explore_2Ind_Procedure {
    // POJOs
    private String returnStatus, firstVariable, subTitle;
    private ArrayList<String> firstDataStrings, secondDataStrings;
    
    // My classes
    private Explore_2Ind_PrepareStructs explore_2Ind_prepStructs;
    private QuantitativeDataVariable qdv_FirstVariable, qdv_SecondVariable;
    private ArrayList<QuantitativeDataVariable> allTheQDVs;
    private Data_Manager dm;
    
    // POJOs / FX
    
    public Explore_2Ind_Procedure(Data_Manager dm) {
        this.dm = dm;
    }  
        
    public String doTheProcedure() {
        try {
            Explore_2Ind_Dialog twoIndDialog = new Explore_2Ind_Dialog(dm, "QUANTITATIVE");
            returnStatus = twoIndDialog.getReturnStatus();
            if (!returnStatus.equals("Ok")) {
                return returnStatus;
            }
            firstVariable = twoIndDialog.getResponseVar();
            subTitle = twoIndDialog.getSubTitle();
            ArrayList<ColumnOfData> data = twoIndDialog.getData();

            qdv_FirstVariable = new QuantitativeDataVariable(data.get(0));
            qdv_SecondVariable = new QuantitativeDataVariable(data.get(1));    

            ArrayList<String> tempAlStr = new ArrayList<>();
            for (int ithColumn = 0; ithColumn < 2; ithColumn++) {
                ColumnOfData tempCol = data.get(ithColumn);
                int nColSize = tempCol.getColumnSize();
                for (int jthCase = 0; jthCase < nColSize; jthCase++) {
                    tempAlStr.add(tempCol.getTheCases().get(jthCase));
                }
            }
            
            ColumnOfData tempCOD = new ColumnOfData("All", tempAlStr);
            QuantitativeDataVariable tempQDV = new QuantitativeDataVariable(tempCOD);
            allTheQDVs = new ArrayList();
            allTheQDVs.add(tempQDV);    //  0th QDV is All
            allTheQDVs.add(qdv_FirstVariable);
            allTheQDVs.add(qdv_SecondVariable);
            
            explore_2Ind_prepStructs = new Explore_2Ind_PrepareStructs(this, dm, allTheQDVs);
            returnStatus = explore_2Ind_prepStructs.showTheDashboard();
            return returnStatus;
        }
        catch (Exception ex) {
            PrintExceptionInfo pei = new PrintExceptionInfo(ex, "Explore 2Ind Dialog");
        }
     
        return returnStatus;
    }
    
    public String getSubTitle() { return subTitle; }
    public String getFirstVariable() { return firstVariable; }
}