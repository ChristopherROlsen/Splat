/**************************************************
 *                  QQPlot_Model                  *
 *                    12/25/18                    *
 *                      15:00                     *
 *************************************************/
package proceduresTwoUnivariate;

import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import t_Procedures.Indep_t_PrepStructs;

public class QQPlot_Model {
    // POJOs
    int nVarsChosen, n_QDVs; 
    String descriptionOfVariable, graphTitle;
     ArrayList<String> allTheLabels;
     
    // My classes
    Explore_2Ind_Dashboard explore_2Ind_Dashboard;
    QQPlot_View qqPlot_View;
    ArrayList<QuantitativeDataVariable> allTheQDVs;   // Array list of DataVariables
    
    public QQPlot_Model() { }

        // This constructor is for multiple boxplots
    public QQPlot_Model(Explore_2Ind_PrepareStructs exp_2Ind_Structs, String descriptionOfVariable, ArrayList<QuantitativeDataVariable> allTheQDVs) 
    {
        this.allTheQDVs = new ArrayList<>();
        this.allTheQDVs = allTheQDVs;
        this.descriptionOfVariable = descriptionOfVariable;
        n_QDVs = allTheQDVs.size();
        allTheLabels = new ArrayList<>();
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            allTheLabels.add(allTheQDVs.get(iVars).getTheDataLabel());
        }         
    } 
    
    // This constructor is for independent t
    public QQPlot_Model(Indep_t_PrepStructs ind_t_Struct, String descriptionOfVariable, ArrayList<QuantitativeDataVariable> allTheQDVs) 
    {
        this.allTheQDVs = new ArrayList<>();
        this.allTheQDVs = allTheQDVs;
        n_QDVs = allTheQDVs.size();
        this.descriptionOfVariable = descriptionOfVariable;
        allTheLabels = new ArrayList<>();
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            allTheLabels.add(allTheQDVs.get(iVars).getTheDataLabel());
        }         
    }
    
    public String getTheGraphTitle() { return descriptionOfVariable; }
    public ArrayList<String> getTheLabels() { return allTheLabels; }
    public ArrayList<QuantitativeDataVariable> getAllTheUDMs() { return allTheQDVs; }
    // public QQPlot_View getQQPlotView() { return qqPlot_View; }
    
}
