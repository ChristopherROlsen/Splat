/**************************************************
 *              BivariateContinDataObj            *
 *                    07/03/18                    *
 *                     00:00                      *
 *************************************************/
package genericClasses;

import java.util.ArrayList;

public class BivariateContinDataObj {
    // POJOs
    int nOriginalDataPoints, nLegalDataPoints, nDataPointsMissing;
    
    double[] xDataAsDoubles, yDataAsDoubles;
    ArrayList<double[]> al_bivDataAsDoubles;  
    
    String xLabel, yLabel;
    String[] xDataAsStrings, yDataAsStrings;
    ArrayList<String> al_xVariable, al_yVariable, al_bivLegalXVariable,
                      al_bivLegalYVariable;
    ArrayList<String[]> al_bivDataAsStrings;
    
    //My classes
    DataUtilities dataUtil;
    
    public BivariateContinDataObj(ArrayList<ColumnOfData> inBivDat) {
        dataUtil = new DataUtilities();
        al_xVariable = new ArrayList<>();
        al_yVariable = new ArrayList<>();
        al_bivLegalXVariable = new ArrayList<>();
        al_bivLegalYVariable = new ArrayList<>();
        al_bivDataAsStrings = new ArrayList<>();
        al_bivDataAsDoubles = new ArrayList<>();
        xLabel = inBivDat.get(0).getVarLabel();
        yLabel = inBivDat.get(1).getVarLabel();
        al_xVariable = inBivDat.get(0).getTheCases();
        al_yVariable = inBivDat.get(1).getTheCases();
        nOriginalDataPoints = al_xVariable.size();
        for (int ithPoint = 0; ithPoint < nOriginalDataPoints; ithPoint++) {
            String xTemp = al_xVariable.get(ithPoint);
            String yTemp = al_yVariable.get(ithPoint);
            if ((DataUtilities.stringIsADouble(xTemp) == true) && (DataUtilities.stringIsADouble(yTemp) == true)) {
                al_bivLegalXVariable.add(xTemp); al_bivLegalYVariable.add(yTemp);
            }
            else {
                // System.out.println("45 oops!  !!!!!!!!!!!!!!!!!!!!  xTemp / yTemp = " + xTemp + " / " + yTemp);
            }
        }
        
        nLegalDataPoints = Math.min(al_bivLegalXVariable.size(), al_bivLegalXVariable.size());
        
        if (nLegalDataPoints == 0) {
            System.out.println("53 bivCon, nLegalDataPoints = " + nLegalDataPoints);
            System.exit(0);
        }
        
        xDataAsStrings = new String[nLegalDataPoints];
        yDataAsStrings = new String[nLegalDataPoints];
        xDataAsDoubles = new double[nLegalDataPoints];
        yDataAsDoubles = new double[nLegalDataPoints];
        
        for (int ith = 0; ith < nLegalDataPoints; ith++) {
            xDataAsStrings[ith] = al_bivLegalXVariable.get(ith);
            yDataAsStrings[ith] = al_bivLegalYVariable.get(ith);
            xDataAsDoubles[ith] = Double.parseDouble(al_bivLegalXVariable.get(ith));
            yDataAsDoubles[ith] = Double.parseDouble(al_bivLegalYVariable.get(ith));
        }  
        
        al_bivDataAsStrings.add(xDataAsStrings);
        al_bivDataAsStrings.add(yDataAsStrings);
        al_bivDataAsDoubles.add(xDataAsDoubles);
        al_bivDataAsDoubles.add(yDataAsDoubles);
        
        nDataPointsMissing = nOriginalDataPoints - nLegalDataPoints;  
    }    
    
    public String getXLabel() { return xLabel; }
    public String getYLabel() { return yLabel; }
    
    public double[] getXAs_arrayOfDoubles() {  return xDataAsDoubles;  }
    public double[] getYAs_arrayOfDoubles() {  return yDataAsDoubles;  }
    
    public int getNLegalDataPoints() { return nLegalDataPoints; }
    public int getNMissingDataPoints() { return nDataPointsMissing; }
    
    public ArrayList<String> getLegalXsAs_AL_OfStrings() { return al_bivLegalXVariable; }
    public ArrayList<String> getLegalYsAs_AL_OfStrings() { return al_bivLegalYVariable; }
    public ArrayList<String[]> getBivDataAsStrings() { return al_bivDataAsStrings; }
    public ArrayList<double[]> getBivDataAsDoubles() { return al_bivDataAsDoubles; }
    
}
