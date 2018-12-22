/************************************************************
 *                  MultRegression_Procedure                *
 *                          09/02/18                        *
 *                            12:00                         *
 ***********************************************************/
package regressionMultiple;

import dialogs.MultReg_Dialog;
import genericClasses.ColumnOfData;
import java.util.ArrayList;
import splat.Splat_DataManager;
import genericClasses.*;

public class MultRegression_Procedure {
    // POJOs
    String returnStatus;
    
    // My classes
    MultiVariateContinDataObj multVarContinObj;
    MultRegression_Model multRegModel;
    MultRegression_Dashboard multRegDashboard;
    Splat_DataManager myData;
    ArrayList<ColumnOfData> data;    
    // POJOs / FX
    
    public MultRegression_Procedure() { }
    
    public MultRegression_Procedure(Splat_DataManager myData) {
        this.myData = myData;
    }  
        
    public String doTheProcedure() {
        try {
            MultReg_Dialog multRegDiag = new MultReg_Dialog(myData);
            data = new ArrayList<>();
            data = multRegDiag.getData();

            multVarContinObj = new MultiVariateContinDataObj(data);

            multRegModel = new MultRegression_Model(this, multVarContinObj);
            multRegModel.setupRegressionAnalysis();
            multRegModel.doRegressionAnalysis();
            multRegModel.printStatistics();

            multRegDashboard = new MultRegression_Dashboard(this, multRegModel);
            multRegDashboard.populateTheBackGround();
            multRegDashboard.putEmAllUp();
            multRegDashboard.showAndWait();
            returnStatus = multRegDashboard.getReturnStatus();

            return returnStatus;  
        }
        catch(Exception ex) {
            // ex.printStackTrace();  ?? p466 Liang
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
