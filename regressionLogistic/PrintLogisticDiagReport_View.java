/****************************************************************************
 *                 PrintLogisticDiagReport_View                             * 
 *                         08/21/18                                         *
 *                          00:00                                           *
 ***************************************************************************/
package regressionLogistic;

import genericClasses.DragableAnchorPane;
import utilityClasses.StringUtilities;
import genericClasses.PrintTextReport_View;
import genericClasses.ResizableTextPane;


public class PrintLogisticDiagReport_View extends PrintTextReport_View {
    // POJOs
    int nCategories, df, maxSpaces, spacesNeeded, nCellsBelow5;                                                                                                                                                                                  
    int[] observedValues; 
    
    // My classes
    DragableAnchorPane dragableAnchorPane;
    StringUtilities myStringUtilities;  
    Logistic_Dashboard logisticDashboard;
    Logistic_Model logisticModel;
    ResizableTextPane rtp;

    public PrintLogisticDiagReport_View(Logistic_Model logisticModel,  Logistic_Dashboard logisticDashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        
        this.logisticModel = logisticModel;
        myStringUtilities = new StringUtilities();
        
        sourceString = new String();
        stringsToPrint = logisticModel.getDiagnostics();
        strTitleText = "Logistic Diagnostics";
    }
}



