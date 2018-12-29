/**************************************************
 *            Exploration_PrepareStructs          *
 *                    09/01/18                    *
 *                      18:00                     *
 *************************************************/

package proceduresOneUnivariate;

import dataObjects.BivariateContinDataObj;
import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;

public class Exploration_PrepareStructs  
{ 
    // POJOs
    
    int nUnchecked, nChecked;
    String subTitle;   //  From dialog
    ArrayList<String> xStrings, yStrings;
    
    // My classes
    BivariateContinDataObj bivContin;    
    private final Exploration_Dashboard exploration_Dashboard;
    QuantitativeDataVariable qdv_XVariable, qdv_YVariable;
    private final QuantitativeDataVariable theQDV;
    private final ArrayList<QuantitativeDataVariable> qdvsForBoxPlots;
    
    Histogram_Model histModel;
    NormProb_Model normProbModel;
    StemNLeaf_Model stemNLeafModel;
    DotPlot_Model dotPlotModel;    
    HorizontalBoxPlot_Model hBoxModel;
    HorizontalBoxPlot_View hBoxView;
    VerticalBoxPlot_Model vBoxModel;
    Ogive_Model ogiveModel;
    
    public Exploration_PrepareStructs(ColumnOfData data, String descrVar) 
    {               
        theQDV = new QuantitativeDataVariable(data);  
        subTitle = descrVar;
        // Box plots require qdvs for All and Each
        qdvsForBoxPlots = new ArrayList<>();
        qdvsForBoxPlots.add(theQDV);
        qdvsForBoxPlots.add(theQDV);
        histModel = new Histogram_Model(subTitle, theQDV);
        normProbModel = new NormProb_Model(subTitle, theQDV);
        
        // The StemNLeaf parameters are also for BBSL
        stemNLeafModel = new StemNLeaf_Model(subTitle, theQDV, false, 0, 0, 0);
        dotPlotModel = new DotPlot_Model(subTitle, theQDV);
        hBoxModel = new HorizontalBoxPlot_Model(subTitle, theQDV);
        vBoxModel = new VerticalBoxPlot_Model(subTitle, theQDV); 
        ogiveModel = new Ogive_Model(subTitle, theQDV);

        exploration_Dashboard = new Exploration_Dashboard(this, theQDV);   
    }  
    
    public String showTheDashboard() {
        String returnStatus;
        exploration_Dashboard.populateTheBackGround();
        exploration_Dashboard.putEmAllUp();
        exploration_Dashboard.showAndWait();
        returnStatus = exploration_Dashboard.getReturnStatus();
        return returnStatus;           
    }
    
    public Histogram_Model getHistModel() {return histModel;}
    public NormProb_Model getNormProbModel() { return normProbModel; }
    public StemNLeaf_Model getStemNLeafModel() { return stemNLeafModel; }
    public DotPlot_Model getDotPlotModel()  { return dotPlotModel; }
    public HorizontalBoxPlot_Model getHBoxModel() { return hBoxModel; }
    public VerticalBoxPlot_Model getVBoxModel() { return vBoxModel; }  
    public Ogive_Model getOgiveModel() { return ogiveModel; }
}

