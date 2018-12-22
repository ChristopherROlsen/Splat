/************************************************************
 *                    Regression_Procedure                  *
 *                          10/02/18                        *
 *                            15:00                         *
 ***********************************************************/
package regressionSimple;

import dialogs.Regression_Dialog;
import genericClasses.BivariateContinDataObj;
import genericClasses.ColumnOfData;
import java.util.ArrayList;
import genericClasses.QuantitativeDataVariable;
import splat.Splat_DataManager;

public class Regression_Procedure {
    // POJOs
    String returnStatus;
    ArrayList<String> xStrings, yStrings;
    
    // My classes
    BivariateContinDataObj bivContin;
    QuantitativeDataVariable qdv_XVariable, qdv_YVariable, qdv_Resids;
    Regression_Model regModel;
    Regression_Dashboard regDashboard;
    Splat_DataManager myData;
    
    // POJOs / FX
    
    public Regression_Procedure(Splat_DataManager myData) {
        this.myData = myData;
    }  
        
    public String doTheProcedure() {
        try {
            Regression_Dialog regDiag = new Regression_Dialog(myData, "QUANTITATIVE");
            returnStatus = regDiag.getReturnStatus();
            if (!returnStatus.equals("Ok")) {
                return returnStatus;
            }

            ArrayList<ColumnOfData> data = regDiag.getData();


            bivContin = new BivariateContinDataObj(data);

            xStrings = bivContin.getLegalXsAs_AL_OfStrings();
            yStrings = bivContin.getLegalYsAs_AL_OfStrings();

            qdv_XVariable = new QuantitativeDataVariable(data.get(0));
            qdv_YVariable = new QuantitativeDataVariable(data.get(1));    

            regModel = new Regression_Model(this);

            regModel.setupRegressionAnalysis(qdv_XVariable, qdv_YVariable);   // 0 is the y-var
            regModel.doRegressionAnalysis();

            regDashboard = new Regression_Dashboard(this, regModel);
            regDashboard.populateTheBackGround();
            regDashboard.putEmAllUp();
            regDashboard.showAndWait();
            returnStatus = regDashboard.getReturnStatus();

            returnStatus = "Ok";
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
