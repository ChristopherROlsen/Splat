/**************************************************
 *                   DotPlotModel                 *
 *                     12/27/18                   *
 *                      15:00                     *
 *************************************************/

package proceduresOneUnivariate;

import dataObjects.QuantitativeDataVariable;
import dataObjects.UnivariateContinDataObj;

public class DotPlot_Model {  
    // POJOs
    int nRows, numberOfBins; 
    int frequencies[];
    
    double binWidth, univDataMin, univDataMax, maximumFreq, minimumFreq, 
           dataRange, freqRange;
    double[] minmax, fiveNumSummary;
    
    String dotPlotLabel, subTitle;
    
    // My classes
    Exploration_Dashboard univ_Dashboard;
    QuantitativeDataVariable qdv;
    UnivariateContinDataObj ucdo;

    
    public DotPlot_Model() { }
        
    public DotPlot_Model(String subTitle, QuantitativeDataVariable qdv) {  
        this.qdv = new QuantitativeDataVariable();
        this.qdv = qdv;
        dotPlotLabel = qdv.getTheDataLabel();
        this.subTitle = subTitle;
        ucdo = new UnivariateContinDataObj(qdv);
    }
    
    public String getSubTitle() { return subTitle; }
    public QuantitativeDataVariable getQDV_Model() { return qdv; }
    public UnivariateContinDataObj getUCDO()  {return ucdo; }
}

