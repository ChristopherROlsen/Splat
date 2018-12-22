/**************************************************
 *            Exploration_PrepareStructs          *
 *                    09/01/18                    *
 *                      18:00                     *
 *************************************************/

package proceduresOneUnivariate;

import genericClasses.BivariateContinDataObj;
import genericClasses.ColumnOfData;
import genericClasses.QuantitativeDataVariable;
import java.util.ArrayList;

public class Exploration_PrepareStructs  
{ 
    // POJOs
    
    int nUnchecked, nChecked;
        
    ArrayList<String> xStrings, yStrings;
    
    // My classes
    BivariateContinDataObj bivContin;    
    private final Exploration_Dashboard exploration_Dashboard;
    QuantitativeDataVariable qdv_XVariable, qdv_YVariable;
    private final QuantitativeDataVariable qdv_Model;
    private final ArrayList<QuantitativeDataVariable> qdvsForBoxPlots;
    
    Histogram_Model histModel;
    NormProb_Model normProbModel;
    StemNLeaf_Model stemNLeafModel;
    DotPlot_Model dotPlotModel;    
    HorizontalBoxPlot_Model hBoxModel;
    HorizontalBoxPlot_View hBoxView;
    VerticalBoxPlot_Model vBoxModel;
    Ogive_Model ogiveModel;
    
    public Exploration_PrepareStructs(ColumnOfData data) 
    {               
        qdv_Model = new QuantitativeDataVariable(data);  
        
        // Box plots require qdvs for All and Each
        qdvsForBoxPlots = new ArrayList<>();
        qdvsForBoxPlots.add(qdv_Model);
        qdvsForBoxPlots.add(qdv_Model);
        histModel = new Histogram_Model(qdv_Model);
        normProbModel = new NormProb_Model(qdv_Model);
        
        // The StemNLeaf parameters are also for BBSL
        stemNLeafModel = new StemNLeaf_Model(qdv_Model, false, 0, 0, 0);
        dotPlotModel = new DotPlot_Model(qdv_Model);
        hBoxModel = new HorizontalBoxPlot_Model(qdvsForBoxPlots);
        vBoxModel = new VerticalBoxPlot_Model(qdvsForBoxPlots); 
        ogiveModel = new Ogive_Model(qdv_Model);

        exploration_Dashboard = new Exploration_Dashboard(this, qdv_Model);   
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

