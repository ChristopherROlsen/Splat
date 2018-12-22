/****************************************************************************
 *                   PrintRegrReport_View                                   * 
 *                         06/28/18                                         *
 *                          15:00                                           *
 ***************************************************************************/
package regressionMultiple;

import genericClasses.StringUtilities;
import genericClasses.PrintTextReport_View;


public class PrintMultRegrReport_View extends PrintTextReport_View {
    // POJOs
    int nCategories, df, maxSpaces, spacesNeeded, nCellsBelow5;                                                                                                                                                                                  
    int[] observedValues; 
    
    // My classes
    StringUtilities myStringUtilities;  
    MultRegression_Dashboard regrDashboard;
    MultRegression_Model regrModel;
   
    public PrintMultRegrReport_View(MultRegression_Model regrModel,  MultRegression_Dashboard regrDashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        
        this.regrModel = regrModel;
        myStringUtilities = new StringUtilities();
        
        sourceString = new String();
        // stringsToPrint = new ArrayList<>();
        stringsToPrint = regrModel.getRegressionReport();
        strTitleText = "Multiple Regression Analysis";
    }
}
