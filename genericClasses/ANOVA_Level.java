/**************************************************
 *                   ANOVA_Level                  *
 *                    05/16/18                    *
 *                      15:00                     *
 *************************************************/
package genericClasses;

import java.util.ArrayList;

/**************************************************
 *  This class is intended to support the various *
 *  ANOVA programs in splat.  It is ancient, and  *
 *  probably needs to be culled.                  *
 *************************************************/

public class ANOVA_Level  {
    // POJOs
    
    int nValuesInThisLevel = 0;
    double[] dataInThisLevel;    
    String theLevelVariableName, theCatLevel;;
    
    // My classes
    
    ArrayList<CatQuantPair> al_CatQuantPairs;  //  Need this as a pair???
    QuantitativeDataVariable levelQDV;
    
    public ANOVA_Level (String theLevelValue)  {
        //System.out.println("33, Creating Level in CategoricalLevel, LevelValue = " + theLevelValue);
        al_CatQuantPairs = new ArrayList<>();
        theLevelVariableName = theLevelValue;
    }    //  end Constructor
    
    public ANOVA_Level (String theLevelValue, ArrayList<CatQuantPair> pre_alcqp)  {
        //System.out.println("33, Creating Level in CategoricalLevel, LevelValue = " + theLevelValue);
        al_CatQuantPairs = new ArrayList(pre_alcqp);
        theLevelVariableName = theLevelValue;
        dataInThisLevel = new double[al_CatQuantPairs.size()];
        for (int ith = 0; ith < al_CatQuantPairs.size(); ith++) {
            dataInThisLevel[ith] = al_CatQuantPairs.get(ith).getQuantValueDouble();
        }
        //System.out.println("40, Level to string");
        //toString();
    }    //  end Constructor

/*
    public void addValue(Double daValue) {
        dataInThisLevel.add(daValue);
        nValuesInThisLevel ++;
    } 
*/
    public double getIthValue(int ith)  { return dataInThisLevel[ith]; }   
    
    public void createNDV() {
        //System.out.println("52. CategoricalLevel, createUCDO()");
        levelQDV = new QuantitativeDataVariable(theLevelVariableName, dataInThisLevel);
    }
    
    public QuantitativeDataVariable getQDV() { return levelQDV; }
    public UnivariateContinDataObj getUCDO () {return levelQDV.getUCDO(); }

    public String getLevelName() {return theLevelVariableName; }

    public int getNValues() {return nValuesInThisLevel; } 

    public void setNValues(int numValues) {nValuesInThisLevel = numValues; }

    //**************************************************************
    // *   The next two methods return the data column label and    *
    // *   the data in string form, for the spreadsheet.            *
    // **************************************************************
    
    public String getLevelLabel() {return theLevelVariableName; }
    
    public double[] getLevelDataAsDoubles() {return dataInThisLevel; }

    public String toString() { 
        //System.out.println("82, CategoricalLevel toString()");
        String theTo = new String(theLevelVariableName);
        theTo += "\n";
        for (int ithValue = 0; ithValue < nValuesInThisLevel; ithValue++) {
            theTo += "\n";
            theTo += getIthValue(ithValue);
        }
        return theTo;
    }
}   //  End CategoricalLevel class 
