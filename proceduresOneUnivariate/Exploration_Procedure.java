/************************************************************
 *                    Exploration_Procedure                 *
 *                          09/02/18                        *
 *                            12:00                         *
 ***********************************************************/
package proceduresOneUnivariate;

import dialogs.ExploreUniv_Dialog;
import genericClasses.ColumnOfData;
import splat.Splat_DataManager;

public class Exploration_Procedure {
    // POJOs
    String returnStatus;
    
    // My classes
    Exploration_PrepareStructs prepExploreStructs;
    Splat_DataManager myData;
    
        public Exploration_Procedure(Splat_DataManager myData) {
            this.myData = myData;
        }  
        
        public String doTheProcedure() {
            try {
                System.out.println("26 Exp Proc, making dialog");
                ExploreUniv_Dialog explore_Diag = new ExploreUniv_Dialog(myData, "QUANTITATIVE");
                returnStatus = explore_Diag.getReturnStatus();
                System.out.println("29 Exp Proc, back from making dialog, returnStatus = " + returnStatus);
                if (!returnStatus.equals("Ok")) {
                    returnStatus = "Cancel";
                    return returnStatus;
                }
                else {
                    ColumnOfData data = explore_Diag.getData();
                    System.out.println("36 Exp Proc, making structs, dataCol = " + data);
                    prepExploreStructs = new Exploration_PrepareStructs(data);
                    returnStatus = prepExploreStructs.showTheDashboard();
                    return returnStatus;  
                }
            }
            catch (Exception ex) {
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
