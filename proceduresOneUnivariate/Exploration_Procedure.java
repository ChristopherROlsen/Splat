/************************************************************
 *                    Exploration_Procedure                 *
 *                          12/25/18                        *
 *                            15:00                         *
 ***********************************************************/
package proceduresOneUnivariate;

import dialogs.ExploreUniv_Dialog;
import dataObjects.ColumnOfData;
import splat.Data_Manager;
import utilityClasses.*;

public class Exploration_Procedure {
    // POJOs
    String returnStatus, descriptionOfVar;
    
    // My classes
    Exploration_PrepareStructs prepExploreStructs;
    Data_Manager myData;
    
        public Exploration_Procedure(Data_Manager myData) {
            this.myData = myData;
        }  
        
        public String doTheProcedure() {
            try {
                ExploreUniv_Dialog explore_Diag = new ExploreUniv_Dialog(myData, "QUANTITATIVE");
                returnStatus = explore_Diag.getReturnStatus();
                if (!returnStatus.equals("Ok")) {
                    returnStatus = "Cancel";
                    return returnStatus;
                }
                else {
                    descriptionOfVar = explore_Diag.getDescriptionOfVariable();
                    ColumnOfData data = explore_Diag.getData();
                    prepExploreStructs = new Exploration_PrepareStructs(data, descriptionOfVar);
                    returnStatus = prepExploreStructs.showTheDashboard();
                    return returnStatus;  
                }
            }
            catch (Exception ex) {
                PrintExceptionInfo pei = new PrintExceptionInfo(ex, "In Exploration Procedure");   
            }
        return returnStatus;
    }
        
    public String getDescriptionOfVariable() {return descriptionOfVar; }
}
