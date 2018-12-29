/**************************************************
 *                    OgiveModel                  *
 *                     12/25/18                   *
 *                      15:00                     *
 *************************************************/

package proceduresOneUnivariate;

import dataObjects.QuantitativeDataVariable;
import dataObjects.UnivariateContinDataObj;

public class Ogive_Model {  
    // POJOs
    int nRows, numberOfBins; 
    int frequencies[];
    
    double binWidth, univDataMin, univDataMax, maximumFreq, minimumFreq, 
           dataRange, freqRange;
    double[] minmax, fiveNumSummary;
    
    String histogramLabel, descriptionOfVariable;
    
    // My classes
    Exploration_Dashboard univ_Dashboard;
    QuantitativeDataVariable qdv;
    UnivariateContinDataObj ucdo;

    public Ogive_Model() { }
        
    public Ogive_Model(String descriptionOfVariable, QuantitativeDataVariable qdv_Model) {  
        qdv = new QuantitativeDataVariable();
        this.descriptionOfVariable = descriptionOfVariable;
        histogramLabel = qdv_Model.getTheDataLabel();

        qdv = qdv_Model;
        ucdo = new UnivariateContinDataObj(qdv_Model);
    }
    
    public String getDescriptionOfVariable() { return descriptionOfVariable; }
    public QuantitativeDataVariable getQDV() { return qdv; }
    public UnivariateContinDataObj getUCDO()  {return ucdo; }
}

