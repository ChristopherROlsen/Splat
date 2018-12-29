/************************************************************
 *                       X2GOF_Procedure                    *
 *                          06/01/18                        *
 *                            18:00                         *
 ***********************************************************/
package chiSquare;

import dialogs.X2GOFOneVar_Dialog;
import dataObjects.ColumnOfData;
import splat.Data_Manager;

public class X2GOF_Procedure {
    // POJOs
    String returnStatus;   
    
    // My classes
    private ColumnOfData columnOfData;
    Data_Manager myData;
    X2GOF_Model x2GOF_Model;
    X2GOF_Dashboard x2GOF_Dashboard;
    
    // POJOs / FX

    public X2GOF_Procedure() { }
    
    public String doGOF_FromCounts() {
        returnStatus = "";
        x2GOF_Model = new X2GOF_Model();
        returnStatus = x2GOF_Model.doX2FromTable();
        switch(returnStatus) {
            case "OK":
                x2GOF_Dashboard = new X2GOF_Dashboard(this, x2GOF_Model);
                x2GOF_Dashboard.populateTheBackGround();
                x2GOF_Dashboard.putEmAllUp();
                x2GOF_Dashboard.showAndWait();
                returnStatus = x2GOF_Dashboard.getReturnStatus();
                break;
            case "Cancel":
                break;
        }
        return returnStatus;
    }
    
    public String doGOF_FromFileData(Data_Manager myData) {
        this.myData = myData;

        X2GOFOneVar_Dialog x2GOF_Dialog = new X2GOFOneVar_Dialog(myData, "CATEGORICAL");
        returnStatus = x2GOF_Dialog.getReturnStatus();
        System.out.println("53 GOF Proc, returnStatus = " + returnStatus);
        switch (returnStatus) {
            case "Cancel":
                break; 
            case "RunAnalysis":    
                ColumnOfData data = x2GOF_Dialog.getData();
                this.columnOfData = data;
                x2GOF_Model = new X2GOF_Model();
                System.out.println("61, GOFProc");
                returnStatus = x2GOF_Model.doX2FromFile(this);
                System.out.println("63, GOFProc, returnStatus = " + returnStatus);
                if (returnStatus.equals("Ok")) {
                    x2GOF_Dashboard = new X2GOF_Dashboard(this, x2GOF_Model);
                    x2GOF_Dashboard.populateTheBackGround();
                    x2GOF_Dashboard.putEmAllUp();
                    x2GOF_Dashboard.showAndWait();
                    returnStatus = x2GOF_Dashboard.getReturnStatus();
                }
                break;
            default:
        }
        return returnStatus;
    }
    
    public ColumnOfData getColumnOfData() {
        return columnOfData;
    }
}
