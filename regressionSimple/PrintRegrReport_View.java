/****************************************************************************
 *                   PrintRegrReport_View                                   * 
 *                         06/00/18                                         *
 *                          00:00                                           *
 ***************************************************************************/
package regressionSimple;

import utilityClasses.StringUtilities;
import genericClasses.PrintTextReport_View;

public class PrintRegrReport_View extends PrintTextReport_View {
    // POJOs
    int nCategories, df, maxSpaces, spacesNeeded, nCellsBelow5;                                                                                                                                                                                  
    int[] observedValues; 
    
    // My classes
    StringUtilities myStringUtilities;  
    Simple_Regression_Dashboard regrDashboard;
    Regression_Model regrModel;
   
    public PrintRegrReport_View(Regression_Model regrModel,  Simple_Regression_Dashboard regrDashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        
        this.regrModel = regrModel;
        myStringUtilities = new StringUtilities();
        
        sourceString = new String();
        stringsToPrint = regrModel.getRegressionReport();
        strTitleText = "Regression Analysis";
    }
}

