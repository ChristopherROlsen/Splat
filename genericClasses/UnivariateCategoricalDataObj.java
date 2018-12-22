/**************************************************
 *             UnivariateCategoricalDataObj       *
 *                    05/16/18                    *
 *                     12:00                      *
 *************************************************/
package genericClasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UnivariateCategoricalDataObj {
    // POJOs
    int nLegalValues, nCategories, nUniques;
    int[] observedCounts;
    
    String varLabel;
    String[] strLegalValues, uniqueCategories;
    ArrayList<String> al_LegalValues;
    
    // My classes
    ArrayList<CatQuantPair> al_CatQuantPairs;
    
    public UnivariateCategoricalDataObj(ColumnOfData colOfData) {
        
        int colSize = colOfData.getColumnSize();
        varLabel = colOfData.getVarLabel();
        al_LegalValues = new ArrayList<>();
        for (int ithDat = 0; ithDat < colSize; ithDat++) {
            String tempString = colOfData.getTextInIthRow(ithDat);
            if (!tempString.equals("*")) {
                al_LegalValues.add(tempString);
            }
        }
        
        nLegalValues = al_LegalValues.size();
        strLegalValues = new String[nLegalValues];
        for (int ithLegalDat = 0; ithLegalDat < nLegalValues; ithLegalDat++) {
            strLegalValues[ithLegalDat] = al_LegalValues.get(ithLegalDat);
        } 
        
        doObservedCounts();
        makeALCatQuantPairs();   
    }
    
    
    private void doObservedCounts() {
        Map<String, Integer> mapOfStrings = new HashMap<>();
        for (int c = 0; c < nLegalValues; c++) {
            if (mapOfStrings.containsKey(strLegalValues[c])) {
                int value = mapOfStrings.get(strLegalValues[c]);
                mapOfStrings.put(strLegalValues[c], value + 1);
            } else {
                mapOfStrings.put(strLegalValues[c], 1);
            }
        }       
        nCategories = mapOfStrings.size();
        Set<Map.Entry<String, Integer>> entrySet = mapOfStrings.entrySet();
        uniqueCategories = new String[nCategories];
        observedCounts = new int[nCategories];
        
        int index = 0;        
        for (Map.Entry<String, Integer> entry: entrySet) {
            uniqueCategories[index] = entry.getKey();
            observedCounts[index] = entry.getValue();
            index++;
        }
        
        nUniques = mapOfStrings.size();
    }
    
    private void makeALCatQuantPairs() {
        al_CatQuantPairs = new ArrayList();
        for (int ithValue = 0; ithValue < nUniques; ithValue++) {
            al_CatQuantPairs.add(new CatQuantPair(uniqueCategories[ithValue], observedCounts[ithValue]));
        }
    }
    
    public int getNUniques() { return nUniques; }
    
    public String getIthValue(int ith) { return al_LegalValues.get(ith); }
    
    public String[] getUniqueValues() {return uniqueCategories; }
    
    public int[] getObservedCounts() { return observedCounts; }
    
    public UnivariateCategoricalDataObj getUnivCatDataObj() { return this; }
    
}
