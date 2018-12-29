/************************************************************
 *                    Regression_Procedure                  *
 *                          12/27/18                        *
 *                            15:00                         *
 ***********************************************************/
package regressionSimple;

import dialogs.Regression_Dialog;
import dataObjects.BivariateContinDataObj;
import dataObjects.ColumnOfData;
import java.util.ArrayList;
import dataObjects.QuantitativeDataVariable;
import splat.Data_Manager;
import utilityClasses.PrintExceptionInfo;

public class Regression_Procedure {
    // POJOs
    private String explanatoryVariable, responseVariable, subTitle, saveTheResids, returnStatus;
    private ArrayList<String> xStrings, yStrings;
    
    // My classes
    private BivariateContinDataObj bivContin;
    private QuantitativeDataVariable qdv_XVariable, qdv_YVariable, qdv_Resids;
    private Regression_Model regModel;
    private Simple_Regression_Dashboard regDashboard;
    private Data_Manager dm;
    
    // POJOs / FX
    
    public Regression_Procedure(Data_Manager dm) {
        this.dm = dm;
    }  
        
    public String doTheProcedure() {
        try {
            Regression_Dialog regressionDialog = new Regression_Dialog(dm, "QUANTITATIVE");
            returnStatus = regressionDialog.getReturnStatus();
            if (!returnStatus.equals("Ok")) {
                return returnStatus;
            }

            explanatoryVariable = regressionDialog.getExplanVar();
            responseVariable = regressionDialog.getResponseVar();
            subTitle = regressionDialog.getSubTitle();
            saveTheResids = regressionDialog.getSaveTheResids();
            ArrayList<ColumnOfData> data = regressionDialog.getData();

            bivContin = new BivariateContinDataObj(data);

            xStrings = bivContin.getLegalXsAs_AL_OfStrings();
            yStrings = bivContin.getLegalYsAs_AL_OfStrings();

            qdv_XVariable = new QuantitativeDataVariable(data.get(0));
            qdv_YVariable = new QuantitativeDataVariable(data.get(1));    

            regModel = new Regression_Model(this);

            regModel.setupRegressionAnalysis(qdv_XVariable, qdv_YVariable);   // 0 is the y-var
            regModel.doRegressionAnalysis();

            regDashboard = new Simple_Regression_Dashboard(this, regModel);
            regDashboard.populateTheBackGround();
            regDashboard.putEmAllUp();
            regDashboard.showAndWait();
            returnStatus = regDashboard.getReturnStatus();

            returnStatus = "Ok";
            return returnStatus;
        }
        catch (Exception ex) { // Constructs stack trace?
            PrintExceptionInfo pei = new PrintExceptionInfo(ex, "RegressionProcedure");
        }
     
        return returnStatus;
    }
    
    public Data_Manager getDataManager() { return dm; }
    public String getSaveTheResids() { return saveTheResids; }
    public String getExplanVar() { return explanatoryVariable; }
    public String getResponseVar() { return responseVariable; }
    public String getSubTitle() { return subTitle; }
}
