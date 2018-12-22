/**************************************************
 *              ANOVA1_PrintReportView            *
 *                    06/02/18                    *
 *                      09:00                     *
 *************************************************/

package ANOVA_One;

import genericClasses.PrintTextReport_View;

public class ANOVA1_PrintReportView extends PrintTextReport_View{
    // POJOs
    int nCategories, df, maxSpaces, spacesNeeded, nCellsBelow5;                                                                                                                                                                                  
    int[] observedValues;
    
    // My classes
    ANOVA1_Dashboard anova1Dashboard;
    ANOVA1_Model anova1Model; 

    ANOVA1_PrintReportView(ANOVA1_Model anova1Model,  ANOVA1_Dashboard anova1Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        
        this.anova1Model = anova1Model;

        stringsToPrint = anova1Model.getANOVA1Report();
        strTitleText = "One-way Analysis of Variance";
    }
}
