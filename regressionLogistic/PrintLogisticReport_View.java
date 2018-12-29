/****************************************************************************
 *                   PrintRegrReport_View                                   * 
 *                         08/20/18                                         *
 *                          15:00                                           *
 ***************************************************************************/
package regressionLogistic;

import utilityClasses.StringUtilities;
import genericClasses.PrintTextReport_View;

public class PrintLogisticReport_View extends PrintTextReport_View {
    // POJOs
    int nCategories, df, maxSpaces, spacesNeeded, nCellsBelow5;                                                                                                                                                                                  
    int[] observedValues; 
    
    // My classes
    StringUtilities myStringUtilities;  
    Logistic_Dashboard regrDashboard;
    Logistic_Model regrModel;
   
    public PrintLogisticReport_View(Logistic_Model regrModel,  Logistic_Dashboard regrDashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        
        this.regrModel = regrModel;
        myStringUtilities = new StringUtilities();
        
        sourceString = new String();
        stringsToPrint = regrModel.getLogisticReport();
        strTitleText = "Logisic Regression Analysis";
    }
}


