/************************************************************
 *                      X2Assoc_Procedure                   *
 *                          06/01/18                        *
 *                            18:00                         *
 ***********************************************************/
package chiSquare;

import dialogs.X2Assoc_Dialog;
import javafx.scene.control.TextArea;
import splat.Splat_DataManager;
import genericClasses.BivariateCategoricalDataObj;
import genericClasses.ColumnOfData;
import java.util.ArrayList;

public class X2Assoc_Procedure {
    // POJOs
    boolean dataIsFromFile, cleanReturnFromAssocModel;
    
    int nUnchecked, nChecked;
    
    String returnStatus, assocType;
    
    // My classes
    BivariateCategoricalDataObj bivCategorical;
    ArrayList<ColumnOfData> outData;
    Splat_DataManager myData;
    X2Assoc_Model x2Assoc_Model;
    X2Assoc_Dashboard x2Assoc_Dashboard;
    
    // FX Objects
    
    public X2Assoc_Procedure(String assocType) { this.assocType = assocType;}
    
    public String doAssoc_FromCounts() {
        returnStatus = "";
        dataIsFromFile = false;
        x2Assoc_Model = new X2Assoc_Model(this, assocType);
        cleanReturnFromAssocModel = x2Assoc_Model.doModelNotFromFile();
        if (cleanReturnFromAssocModel == true) {
            x2Assoc_Model.doChiSqAnalysisCalculations();
            x2Assoc_Dashboard = new X2Assoc_Dashboard(this, x2Assoc_Model); 
            x2Assoc_Dashboard.populateTheBackGround();
            x2Assoc_Dashboard.putEmAllUp();
            x2Assoc_Dashboard.showAndWait();
            returnStatus = x2Assoc_Dashboard.getReturnStatus();
            return returnStatus;
        }
        else {
            return "Cancel";
        }
    }  

    public String doAssoc_FromFile(Splat_DataManager myData) {
        this.myData = myData;
        returnStatus = "";
        X2Assoc_Dialog x2Assoc_Dialog = new X2Assoc_Dialog(myData, "CATEGORICAL");
        returnStatus = x2Assoc_Dialog.getReturnStatus();
        switch (returnStatus) {
            case "Cancel":
                break; 
            case "Ok":    
                bivCategorical = new BivariateCategoricalDataObj(x2Assoc_Dialog.getData());
                outData = new ArrayList();
                outData = bivCategorical.getLegalColumns(); // Missing data deleted
                x2Assoc_Model = new X2Assoc_Model(this, assocType);
                boolean cleanReturnFromModel = x2Assoc_Model.doModelFromFile();
                if (cleanReturnFromModel == true) {
                    x2Assoc_Model.doChiSqAnalysisCalculations();
                    x2Assoc_Dashboard = new X2Assoc_Dashboard(this, x2Assoc_Model); 
                    x2Assoc_Dashboard.populateTheBackGround();
                    x2Assoc_Dashboard.putEmAllUp();
                    x2Assoc_Dashboard.showAndWait();
                    returnStatus = x2Assoc_Dashboard.getReturnStatus();
                }
                break;
            default:
                System.out.println("69 Ack!!  switch failur in X2Assoc_Procedure!!!");
            }
        return returnStatus;
    }
    
    public void setCleanReturnFromAssocModel(boolean toThis) {
        cleanReturnFromAssocModel = toThis;
    }
    
    public ArrayList<ColumnOfData> getData() { return outData; }
}
