/**************************************************
 *              QANOVA1_PrintReportView           *
 *                    12/01/18                    *
 *                      12:00                     *
 *************************************************/

package ANOVA_One;

import genericClasses.PrintTextReport_View;

public class QANOVA1_PrintReportView extends PrintTextReport_View{
    // POJOs
    int nCategories, df, maxSpaces, spacesNeeded, nCellsBelow5;                                                                                                                                                                                  
    int[] observedValues;
    
    // My classes
    QANOVA1_Dashboard qanova1Dashboard;
    QANOVA1_Model qanova1Model; 

    QANOVA1_PrintReportView(QANOVA1_Model qanova1Model,  QANOVA1_Dashboard qanova1Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        
        this.qanova1Model = qanova1Model;

        stringsToPrint = qanova1Model.getANOVA1Report();
        strTitleText = "One-way Analysis of Variance";
    }
}
