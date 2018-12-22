/**************************************************
 *           BivariateCategoricalDataObj          *
 *                    05/16/18                    *
 *                     12:00                      *
 *************************************************/
package genericClasses;

import java.util.ArrayList;

public class BivariateCategoricalDataObj {
    // POJOs
    int nOriginalDataPoints, nLegalDataPoints, nDataPointsMissing;
    String xLabel, yLabel;
    ArrayList<String> al_xVariable, al_yVariable,
                      al_legalXVariable, al_legalYVariable;
    
    // My classes
    ArrayList<ColumnOfData> inBivDat, outBivDat;
    
    public BivariateCategoricalDataObj(ArrayList<ColumnOfData> inBivDat) {
        
        this.inBivDat = inBivDat;
        xLabel = inBivDat.get(0).getVarLabel();
        yLabel = inBivDat.get(1).getVarLabel();
        al_legalXVariable = new ArrayList<>();
        al_legalYVariable = new ArrayList<>();

        nOriginalDataPoints = inBivDat.get(0).getColumnSize();
        for (int ithPoint = 0; ithPoint < nOriginalDataPoints; ithPoint++) {
            String xTemp = inBivDat.get(0).getTheCases().get(ithPoint);
            String yTemp = inBivDat.get(1).getTheCases().get(ithPoint);
            if (!xTemp.equals("*") && !yTemp.equals("*")) {
                nLegalDataPoints++;
                al_legalXVariable.add(xTemp); al_legalYVariable.add(yTemp);
            }
        }
        
        nLegalDataPoints = al_legalXVariable.size();        
        nDataPointsMissing = nOriginalDataPoints - nLegalDataPoints;  
        
        outBivDat = new ArrayList();
        outBivDat.add(new ColumnOfData(xLabel, al_legalXVariable));
        outBivDat.add(new ColumnOfData(yLabel, al_legalYVariable));
    }    
    
    public ArrayList<ColumnOfData> getLegalColumns() { return outBivDat; }
    public int getNLegalDataPoints() { return nLegalDataPoints; }
    public int getNMissingDataPoints() { return nDataPointsMissing; }
}
