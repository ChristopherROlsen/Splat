/**************************************************
 *                 NormProb_Model                 *
 *                    12/25/18                    *
 *                      15:00                     *
 *************************************************/
package proceduresOneUnivariate;

import utilityClasses.Transformations_Calculations;
import dataObjects.QuantitativeDataVariable;
import probabilityDistributions.StandardNormal;

public class NormProb_Model {  
    // POJOs
    int nDataPoints;
    
    double univDataMin, univDataMax;
    double[] theData, minmax, normalScores;
    
    String normProbLabel, normProbUnits, subTitle;    
    String[] strNormalScores;

    // My classes
    Exploration_Dashboard univ_Dashboard;
    NormProb_Model normProb_Model;
    NormProb_View normProb_View;
    QuantitativeDataVariable qdv, qdvData;
    QuantitativeDataVariable qdvNSs;
    StandardNormal standNorm;
    Transformations_Calculations transCalc;
   
    public NormProb_Model()  { }
        
    public NormProb_Model(String subTitle, QuantitativeDataVariable qdv_Data) { 
        this.subTitle = subTitle;
        qdv = new QuantitativeDataVariable();
        qdv = qdv_Data;
        normProbLabel = qdv.getTheDataLabel();
        normProbUnits = qdv.getDataUnits();
        transCalc = new Transformations_Calculations();
        theData = qdv_Data.getLegalDataAsDoubles();
        nDataPoints = theData.length;
        strNormalScores = new String[nDataPoints];        
        qdvData = new QuantitativeDataVariable(normProbLabel, theData);
        strNormalScores = transCalc.unaryOpsOfVars(theData, "rankits");
        qdvNSs = new QuantitativeDataVariable("Normal score", strNormalScores);
    }
    
    public String getSubTitle() { return subTitle; }
    public NormProb_View getNormProb_View() { return normProb_View; }
    public QuantitativeDataVariable getData() { return qdvData; }
    public QuantitativeDataVariable getNormalScores() { return qdvNSs; }    
    public int getNDataPoints() { return nDataPoints; }
    public String getNormProbLabel() { return normProbLabel; }
    public String getNormProbUnits() { return normProbUnits; }
    
    public String toString() {
        String daString = "NormProb_Model toString()";
        return daString;
    }

}

