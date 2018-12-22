/**************************************************
 *                   DotPlotModel                 *
 *                     05/14/18                   *
 *                      12:00                     *
 *************************************************/

package proceduresOneUnivariate;

import genericClasses.QuantitativeDataVariable;
import genericClasses.UnivariateContinDataObj;

public class DotPlot_Model {  
    // POJOs
    int nRows, numberOfBins; 
    int frequencies[];
    
    double binWidth, univDataMin, univDataMax, maximumFreq, minimumFreq, 
           dataRange, freqRange;
    double[] minmax, fiveNumSummary;
    
    String dotPlotLabel;
    
    // My classes
    Exploration_Dashboard univ_Dashboard;
    QuantitativeDataVariable qdv;
    UnivariateContinDataObj ucdo;

    
    public DotPlot_Model() { }
        
    public DotPlot_Model(QuantitativeDataVariable qdv) {  
        this.qdv = new QuantitativeDataVariable();
        this.qdv = qdv;
        dotPlotLabel = qdv.getTheDataLabel();

        ucdo = new UnivariateContinDataObj(qdv);
    }
    
    public QuantitativeDataVariable getQDV_Model() { return qdv; }
    public UnivariateContinDataObj getUCDO()  {return ucdo; }
}

