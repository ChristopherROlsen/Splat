/**************************************************
 *              HorizontalBoxPlot_Model           *
 *                    12/08/18                    *
 *                      15:00                     *
 *************************************************/

package proceduresOneUnivariate;

import genericClasses.QuantitativeDataVariable;
import java.util.ArrayList;

public class HorizontalBoxPlot_Model {
    
    int nVarsChosen, n_QDVs; 
    ArrayList<String> allTheLabels;
    ArrayList<QuantitativeDataVariable> allTheQDVs;   // Array list of DataVariables
    
    public HorizontalBoxPlot_Model() { }
    
    // This constructor is for single boxplots
    public HorizontalBoxPlot_Model(QuantitativeDataVariable theQDV) 
    {
        allTheQDVs = new ArrayList<>();
            allTheLabels = new ArrayList<>();    
        // Yup, two copies
        allTheQDVs.add(theQDV);
        allTheQDVs.add(theQDV);  
        allTheLabels.add(theQDV.getTheDataLabel()); 
        allTheLabels.add(theQDV.getTheDataLabel()); 
        n_QDVs = 1;      
    } 

    // This constructor is for multiple boxplots
    public HorizontalBoxPlot_Model(ArrayList<QuantitativeDataVariable> allTheQDVs) 
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
    public ArrayList<QuantitativeDataVariable> getAllTheQDVs() { return allTheQDVs; }   
}