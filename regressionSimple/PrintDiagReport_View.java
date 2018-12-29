/****************************************************************************
 *                   PrintDiagReport_View                                   * 
 *                         06/24/18                                         *
 *                          21:00                                           *
 ***************************************************************************/
package regressionSimple;

import genericClasses.DragableAnchorPane;
import utilityClasses.StringUtilities;
import genericClasses.PrintTextReport_View;
import genericClasses.ResizableTextPane;


public class PrintDiagReport_View extends PrintTextReport_View {
    // POJOs
    int nCategories, df, maxSpaces, spacesNeeded, nCellsBelow5;                                                                                                                                                                                  
    int[] observedValues; 
    
    // My classes
    DragableAnchorPane dragableAnchorPane;
    StringUtilities myStringUtilities;  
    Simple_Regression_Dashboard regrDashboard;
    Regression_Model regrModel;
    ResizableTextPane rtp;

    public PrintDiagReport_View(Regression_Model regrModel,  Simple_Regression_Dashboard regrDashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        
        this.regrModel = regrModel;
        myStringUtilities = new StringUtilities();
        
        sourceString = new String();
        stringsToPrint = regrModel.getDiagnostics();
        strTitleText = "Regression Diagnostics";
    }
}


