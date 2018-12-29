/**************************************************
 *              HorizontalBoxPlot_Model           *
 *                    12/25/18                    *
 *                      15:00                     *
 *************************************************/

package proceduresOneUnivariate;

import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import proceduresTwoUnivariate.*;
import t_Procedures.*;

public class HorizontalBoxPlot_Model {
    
    int nVarsChosen, n_QDVs; //  QuantitativeDataVariables
    String descriptionOfVariable;
    ArrayList<String> allTheLabels;
    ArrayList<QuantitativeDataVariable> allTheQDVs;   // Array list of DataVariables
    
    public HorizontalBoxPlot_Model() { }
    
    // This constructor is for a single boxplot
    public HorizontalBoxPlot_Model(String descriptionOfVariable, QuantitativeDataVariable theQDV) 
    {
        allTheQDVs = new ArrayList<>();
            allTheLabels = new ArrayList<>();    
        // !!!!!!!!!!!!!!!  Yup, two copies !!!!!!!!!!!!!!!!!!!
        allTheQDVs.add(theQDV);
        allTheQDVs.add(theQDV);  
        allTheLabels.add(theQDV.getTheDataLabel()); 
        allTheLabels.add(theQDV.getTheDataLabel()); 
        n_QDVs = 1; 
        this.descriptionOfVariable = descriptionOfVariable;
    }
    
    // This constructor is for single t
    public HorizontalBoxPlot_Model(Single_t_PrepStructs exPrepStruct, String descriptionOfVariable, QuantitativeDataVariable theQDV) 
    {
        allTheQDVs = new ArrayList<>();
            allTheLabels = new ArrayList<>();    
        // !!!!!!!!!!!!!!!  Yup, two copies !!!!!!!!!!!!!!!!!!!
        allTheQDVs.add(theQDV);
        allTheQDVs.add(theQDV);  
        allTheLabels.add(theQDV.getTheDataLabel()); 
        allTheLabels.add(theQDV.getTheDataLabel()); 
        n_QDVs = 1; 
        this.descriptionOfVariable = descriptionOfVariable;
    }

    // This constructor is for two boxplots
    public HorizontalBoxPlot_Model(Explore_2Ind_PrepareStructs exPrepStructs, String descriptionOfVariable, ArrayList<QuantitativeDataVariable> allTheQDVs) 
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
    
    // This constructor is for independent t
    public HorizontalBoxPlot_Model(Indep_t_PrepStructs ind_t_Struct, String descriptionOfVariable, ArrayList<QuantitativeDataVariable> allTheQDVs) 
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

    // This constructor is for multiple boxplots
    public HorizontalBoxPlot_Model(Exploration_PrepareStructs exPrepStructs, String descriptionOfVariable, ArrayList<QuantitativeDataVariable> allTheQDVs) 
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

    
    public String getDescriptionOfVariable() { return descriptionOfVariable; }
    public ArrayList<String> getTheLabels() { return allTheLabels; }
    public ArrayList<QuantitativeDataVariable> getAllTheQDVs() { return allTheQDVs; }   
}