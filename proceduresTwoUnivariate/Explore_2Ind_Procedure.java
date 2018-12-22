/************************************************************
 *                   Explore2Ind__Procedure                 *
 *                          11/10/18                        *
 *                            03:00                         *
 ***********************************************************/
package proceduresTwoUnivariate;

import dialogs.*;
import genericClasses.ColumnOfData;
import java.util.ArrayList;
import genericClasses.QuantitativeDataVariable;
import splat.Splat_DataManager;

public class Explore_2Ind_Procedure {
    // POJOs
    String returnStatus;
    ArrayList<String> firstDataStrings, secondDataStrings;
    
    // My classes
    Explore_2Ind_PrepareStructs explore_2Ind_prepStructs;
    QuantitativeDataVariable qdv_FirstVariable, qdv_SecondVariable;
    ArrayList<QuantitativeDataVariable> allTheQDVs;
    Splat_DataManager dm;
    
    // POJOs / FX
    
    public Explore_2Ind_Procedure(Splat_DataManager dm) {
        this.dm = dm;
    }  
        
    public String doTheProcedure() {
        try {
            Explore_2Ind_Dialog twoIndDialog = new Explore_2Ind_Dialog(dm, "QUANTITATIVE");
            returnStatus = twoIndDialog.getReturnStatus();
            if (!returnStatus.equals("Ok")) {
                return returnStatus;
            }

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
            
            explore_2Ind_prepStructs = new Explore_2Ind_PrepareStructs(dm, allTheQDVs);
            returnStatus = explore_2Ind_prepStructs.showTheDashboard();
            return returnStatus;
        }
        catch (Exception ex) {
            ex.printStackTrace();  // Constructs stack trace?
            System.out.println("\n" + ex.getMessage());
            System.out.println("\n" + ex.toString());
            System.out.println("\nTrace Info Obtained from getStatckTrace");
            StackTraceElement[] traceElements = ex.getStackTrace();
            for (int i = 0; i < traceElements.length; i++) {
                System.out.print("method " + traceElements[i].getMethodName());
                System.out.print("(" + traceElements[i].getClassName() + ":");
                System.out.print(traceElements[i].getLineNumber() + ")");
            }             
        }
     
        return returnStatus;
    }
}