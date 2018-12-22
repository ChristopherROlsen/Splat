/**************************************************
 *                  HistogramModel                *
 *                     08/15/18                   *
 *                      18:00                     *
 *************************************************/

package proceduresOneUnivariate;

import genericClasses.QuantitativeDataVariable;
import genericClasses.UnivariateContinDataObj;

public class Histogram_Model {  
    // POJOs
    int nRows, numberOfBins; 
    int frequencies[];
    
    double binWidth, univDataMin, univDataMax, maximumFreq, minimumFreq, 
           dataRange, freqRange;
    double[] minmax, fiveNumSummary;
    
    String histogramLabel;
    
    // My classes
    Exploration_Dashboard univ_Dashboard;
    QuantitativeDataVariable qdv;
    UnivariateContinDataObj ucdo;

    public Histogram_Model() { }
        
    public Histogram_Model(QuantitativeDataVariable qdv_Model) {  
        qdv = new QuantitativeDataVariable();
        histogramLabel = qdv_Model.getTheDataLabel();

        qdv = qdv_Model;
        ucdo = new UnivariateContinDataObj(qdv_Model);
    }
    
    public QuantitativeDataVariable getQDV() { return qdv; }
    public UnivariateContinDataObj getUCDO()  {return ucdo; }
}

