/**************************************************
 *                  QQPlot_Model                  *
 *                    06/04/18                    *
 *                      03:00                     *
 *************************************************/
package proceduresTwoUnivariate;

import genericClasses.QuantitativeDataVariable;
import java.util.ArrayList;

public class QQPlot_Model {
    // POJOs
    int nVarsChosen, n_QDVs; 
     ArrayList<String> allTheLabels;
     
    // My classes
    Explore_2Ind_Dashboard xplore_2Ind_Dashboard;
    QQPlot_View qqPlot_View;
    ArrayList<QuantitativeDataVariable> allTheQDVs;   // Array list of DataVariables
    
    public QQPlot_Model() { }

        // This constructor is for multiple boxplots
    public QQPlot_Model(ArrayList<QuantitativeDataVariable> allTheQDVs) 
    {
        this.allTheQDVs = new ArrayList<>();
        this.allTheQDVs = allTheQDVs;
        n_QDVs = allTheQDVs.size();
        allTheLabels = new ArrayList<>();
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            allTheLabels.add(allTheQDVs.get(iVars).getTheDataLabel());
        }         
    } 
    
    public ArrayList<String> getTheLabels() { return allTheLabels; }
    public ArrayList<QuantitativeDataVariable> getAllTheUDMs() { return allTheQDVs; }
    public QQPlot_View getQQPlotView() { return qqPlot_View; }
    
}
