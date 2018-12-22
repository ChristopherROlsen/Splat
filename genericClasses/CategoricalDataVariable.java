/**************************************************
 *              CategoricalDataVariable           *
 *                    05/16/18                    *
 *                      12:00                     *
 *************************************************/
package genericClasses;

import java.util.ArrayList;
import java.util.Arrays;

public class CategoricalDataVariable {
    // POJOs
    private int nDataPoints, nLevels;
    private String dataLabel;
    private String[] dataAsStrings;
    private ArrayList<String> listOfLevels;
    
    // My classes
    public CategoricalDataVariable() { }
    
    public CategoricalDataVariable (String dataLabel, int nDataPoints) {
        this.dataLabel = dataLabel;
        this.nDataPoints = nDataPoints;
        dataAsStrings = new String[nDataPoints];
        nLevels = 0;
    }
    
    public CategoricalDataVariable (String inLabel, String[] inDataStrings)  {
        dataLabel = inLabel;
        nDataPoints = inDataStrings.length;
        dataAsStrings = new String[nDataPoints];
        System.arraycopy(inDataStrings, 0, dataAsStrings, 0, nDataPoints);    
        nLevels = 0;
    }
    
    public void analyzeLevels() {
        String[] sortedArray = new String[nDataPoints];
        System.arraycopy(dataAsStrings, 0, sortedArray, 0, nDataPoints);  
        Arrays.sort(sortedArray);
        
        nLevels = 1;
        listOfLevels = new ArrayList<>();
        listOfLevels.add(sortedArray[0]);
        for (int ith = 1; ith < nDataPoints; ith++) {
            if (!sortedArray[ith].equals(sortedArray[ith - 1]))  {
                nLevels++;
                listOfLevels.add(sortedArray[ith]);
            } 
        }
    }
    
    public int get_N () {return nDataPoints;}
    
    public int getNumberOfLevels() { 
        if (nLevels == 0)
            analyzeLevels();
        
        return nLevels; }
    
    public ArrayList<String> getListOfLevels() { 
        if (nLevels == 0)
            analyzeLevels();        
        
        return listOfLevels; }
    
    public String getIthDataPtAsString(int ith) {return dataAsStrings[ith]; }
    
    public void setIthDataPtAsString(int ith, String ithString) {dataAsStrings[ith] = ithString; }
        
    public String[] getDataAsStrings() {return dataAsStrings; }
    
    public String getDataLabel() {return dataLabel; }
    public void setDataLabel( String daLabel) {dataLabel = daLabel; }
    

    
    public CategoricalDataVariable getDataVariable() {
        return this;
    }
    
    public String toString() {
        System.out.println ("\n\nCatagorical Data Variable toString(): " + dataLabel);
        System.out.println("n = " + nDataPoints);
        String theTo = "\n" + dataLabel;
        for (int ithDataPoint = 0; ithDataPoint < nDataPoints; ithDataPoint++)  {
            System.out.println("daData: " + dataAsStrings[ithDataPoint]);
            theTo += "\n";
            theTo += dataAsStrings[ithDataPoint];
        }
        return theTo;
    }
    
}
