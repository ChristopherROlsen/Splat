/**************************************************
 *              VerticalBoxPlot_Model             *
 *                    12/27/18                    *
 *                      15:00                     *
 *************************************************/

package proceduresOneUnivariate;

import ANOVA_One.*;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import proceduresTwoUnivariate.Explore_2Ind_PrepareStructs;
import t_Procedures.*;

public class VerticalBoxPlot_Model {
    // POJOs
    private int nVarsChosen, n_QDVs;    //  QuantitativeDataVariables
    private String subTitle, explanatoryVariable, responseVariable;
    private ArrayList<String> allTheLabels;
    private ArrayList<QuantitativeDataVariable> allTheQDVs;   // Array list of DataVariables
    
    public VerticalBoxPlot_Model() { }
    
    // This constructor is for a single boxplot
    public VerticalBoxPlot_Model(Exploration_PrepareStructs exPrepStruct, String descriptionOfVariable, QuantitativeDataVariable theQDV) 
    {
        allTheQDVs = new ArrayList<>();
            allTheLabels = new ArrayList<>();    
        // !!!!!!!!!!!!!!!  Yup, two copies !!!!!!!!!!!!!!!!!!!
        allTheQDVs.add(theQDV);
        allTheQDVs.add(theQDV);  
        allTheLabels.add(theQDV.getTheDataLabel()); 
        allTheLabels.add(theQDV.getTheDataLabel()); 
        n_QDVs = 1; 
        this.subTitle = descriptionOfVariable;
    }
    
    // This constructor is for a single t
    public VerticalBoxPlot_Model(String descriptionOfVariable, QuantitativeDataVariable theQDV) 
    {
        allTheQDVs = new ArrayList<>();
            allTheLabels = new ArrayList<>();    
        // !!!!!!!!!!!!!!!  Yup, two copies !!!!!!!!!!!!!!!!!!!
        allTheQDVs.add(theQDV);
        allTheQDVs.add(theQDV);  
        allTheLabels.add(theQDV.getTheDataLabel()); 
        allTheLabels.add(theQDV.getTheDataLabel()); 
        n_QDVs = 1; 
        this.subTitle = descriptionOfVariable;
    }
    
    // This constructor is for two boxplots
    public VerticalBoxPlot_Model(Explore_2Ind_PrepareStructs exPrepStructs, String descriptionOfVariable, ArrayList<QuantitativeDataVariable> allTheQDVs) 
    {
        this.allTheQDVs = new ArrayList<>();
        this.allTheQDVs = allTheQDVs;
        n_QDVs = allTheQDVs.size();
        this.subTitle = descriptionOfVariable;
        allTheLabels = new ArrayList<>();
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            allTheLabels.add(allTheQDVs.get(iVars).getTheDataLabel());
        }         
    }
    
    // This constructor is for independent t
    public VerticalBoxPlot_Model(Indep_t_PrepStructs ind_t_Struct, String descriptionOfVariable, ArrayList<QuantitativeDataVariable> allTheQDVs) 
    {
        this.allTheQDVs = new ArrayList<>();
        this.allTheQDVs = allTheQDVs;
        n_QDVs = allTheQDVs.size();
        this.subTitle = descriptionOfVariable;
        allTheLabels = new ArrayList<>();
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            allTheLabels.add(allTheQDVs.get(iVars).getTheDataLabel());
        }         
    }
    
    public VerticalBoxPlot_Model(ANOVA1_Model anova1Model, ArrayList<QuantitativeDataVariable> allTheQDVs) 
    {
        this.allTheQDVs = new ArrayList<>();
        this.allTheQDVs = allTheQDVs;
        n_QDVs = allTheQDVs.size();
        subTitle = anova1Model.getSubTitle();
        
        allTheLabels = new ArrayList<>();
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            allTheLabels.add(allTheQDVs.get(iVars).getTheDataLabel());
        }         
    }
    
    public VerticalBoxPlot_Model(QANOVA1_Model qanova1Model, ArrayList<QuantitativeDataVariable> allTheQDVs) 
    {
        this.allTheQDVs = new ArrayList<>();
        this.allTheQDVs = allTheQDVs;
        n_QDVs = allTheQDVs.size();
        subTitle = qanova1Model.getSubTitle();
        
        allTheLabels = new ArrayList<>();
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            allTheLabels.add(allTheQDVs.get(iVars).getTheDataLabel());
        }         
    }

    // This constructor is for multiple boxplots -- description of variables supplied elsewhere
    public VerticalBoxPlot_Model(ArrayList<QuantitativeDataVariable> allTheQDVs)
    {
        this.allTheQDVs = new ArrayList<>();
        this.allTheQDVs = allTheQDVs;
        n_QDVs = allTheQDVs.size();
        allTheLabels = new ArrayList<>();
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            allTheLabels.add(allTheQDVs.get(iVars).getTheDataLabel());
        }         
    } 
   
    public String getDescriptionOfVariable() { return subTitle; }
    public ArrayList<String> getTheLabels() { return allTheLabels; }
    public ArrayList<QuantitativeDataVariable> getAllTheUDMs() { return allTheQDVs; }
}