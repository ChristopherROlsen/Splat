/**************************************************
 *                CatQuantDataVariable            *
 *                    05/16/18                    *
 *                      12:00                     *
 *************************************************/
package genericClasses;


import splat.*;
import java.util.ArrayList;

public class CatQuantDataVariable {
    // POJOs
    int nLevels, nLegalCQPairs;
    
    ArrayList<String> categoryLabels;
    
    // My classes
    ArrayList<ANOVA_Level> al_OfLevels;
    CategoricalDataVariable catDataVar;
    ArrayList<CatQuantPair> al_LegalCatQuantPairs;
    ColumnOfData originalCatData, originalQuantData;
    QuantitativeDataVariable tempQDV, qdvResponseData;
    ArrayList<QuantitativeDataVariable> al_QDVs;
    Splat_DataManager dm;
    public CatQuantDataVariable(Splat_DataManager dm, ColumnOfData catData, ColumnOfData quantData) {
        this.dm = dm;
        //System.out.println("25 cqdv, CatQuantDataVariable(ColumnOfData catData, ColumnOfData quantData)");
        //System.out.println("26 cqdv, originalCatData...");
        catData.toString();
        //System.out.println("28 cqdv, CatQuantDataVariable(ColumnOfData catData, ColumnOfData quantData)");
        //System.out.println("29 cqdv, originalquantData..."); 
        quantData.toString();
        
        originalCatData = new ColumnOfData(catData);
        originalQuantData = new ColumnOfData(quantData);  
        
        System.out.print( "35 cqdv, originalCatData = " + originalCatData.toString());
        System.out.print( "36 cqdv, originalQuantData = " + originalQuantData.toString());
        int tempCatN = originalCatData.getColumnSize();
        int tempQuantN = originalQuantData.getColumnSize();
        
        if (tempCatN != tempQuantN) {
            System.out.println("cqdv 41, Whoa there!!! Different column size!");
            System.exit(42);
        }
        
        al_LegalCatQuantPairs = new ArrayList<>();
        
        for (int ith = 0; ith < tempCatN; ith++) {
            String tempCatString = originalCatData.getTextInIthRow(ith);
            String tempQuantString = originalQuantData.getTextInIthRow(ith);
            
            if ((!tempCatString.equals("*")) &&(!tempQuantString.equals("*"))) {
                al_LegalCatQuantPairs.add(new CatQuantPair(tempCatString, Double.parseDouble(tempQuantString)));
            }
        }

        nLegalCQPairs = al_LegalCatQuantPairs.size();
        System.out.println("57 cqdv, CatQuantDataVariable");        
        unstackLegalPairs();
    }
    
    public CatQuantDataVariable(CategoricalDataVariable catDataVar, QuantitativeDataVariable qdvResponseData) {    
        System.out.println("56 cqdv, CatQuantDataVariable(ColumnOfData catData, ColumnOfData quantData)");
        this.catDataVar = catDataVar;
        this.qdvResponseData = qdvResponseData;
        int tempCatN = catDataVar.get_N();
        int tempQuantN = qdvResponseData.getLegalN();
        
        if (tempCatN != tempQuantN) {
            System.out.println("cqdv 69, Whoa there!!! Different column size!");
            System.exit(70);
        }
        
        al_LegalCatQuantPairs = new ArrayList<>();

        for (int ith = 0; ith < tempCatN; ith++) {
            String tempCatString = catDataVar.getIthDataPtAsString(ith);
            String tempQuantString = qdvResponseData.getIthDataPtAsString(ith);
            if ((!tempCatString.equals("*")) &&(!tempQuantString.equals("*"))) {
                al_LegalCatQuantPairs.add(new CatQuantPair(tempCatString, Double.parseDouble(tempQuantString)));
            }
        }

        nLegalCQPairs = al_LegalCatQuantPairs.size();
        unstackLegalPairs();
    }
    
    private void unstackLegalPairs() {
        boolean endOfStory;
        int startOfTie, endOfTie, cqpCompare;
        // Sort the data points by first dataVariable, but NOT second dataVariable.  Simple bubble sort.
        System.out.println("86 cqdv, unstackLegalCQPairs()");         
        for (int k = 1; k < nLegalCQPairs; k++) {
            for (int i = 0; i < nLegalCQPairs - k; i++)  {
                cqpCompare = (al_LegalCatQuantPairs.get(i).getCatValue()).compareTo(al_LegalCatQuantPairs.get(i + 1).getCatValue());
                if (cqpCompare > 0)  {
                    
                    String ithCat = al_LegalCatQuantPairs.get(i).getCatValue();
                    double ithQuant = al_LegalCatQuantPairs.get(i).getQuantValueDouble();
                    String ithPlus1Cat = al_LegalCatQuantPairs.get(i + 1).getCatValue();
                    double ithPlus1Quant = al_LegalCatQuantPairs.get(i + 1).getQuantValueDouble();

                    String tempCatValue = al_LegalCatQuantPairs.get(i).getCatValue();
                    double tempQuantValue = al_LegalCatQuantPairs.get(i).getQuantValueDouble();
                    
                    al_LegalCatQuantPairs.get(i).setCatValue(al_LegalCatQuantPairs.get(i + 1).getCatValue());
                    al_LegalCatQuantPairs.get(i).setQuantValueDouble(al_LegalCatQuantPairs.get(i + 1).getQuantValueDouble());
                    
                    al_LegalCatQuantPairs.get(i + 1).setCatValue(tempCatValue);
                    al_LegalCatQuantPairs.get(i + 1).setQuantValueDouble(tempQuantValue);   
                    
                    ithCat = al_LegalCatQuantPairs.get(i).getCatValue();
                    ithQuant = al_LegalCatQuantPairs.get(i).getQuantValueDouble();
                    ithPlus1Cat = al_LegalCatQuantPairs.get(i + 1).getCatValue();
                    ithPlus1Quant = al_LegalCatQuantPairs.get(i + 1).getQuantValueDouble();
 
                }
            }
        }
  
        // Create the QDVs
        al_QDVs = new ArrayList<>();
        categoryLabels = new ArrayList<>();
        tempQDV = createNewQDV("All", 0, nLegalCQPairs - 1);
        
        al_QDVs.add(tempQDV);

        startOfTie = 0;    //  Start process at first number;
        endOfTie = 0;      // subscript is as in ArrayList
        endOfStory = false;
        do {
            for (int ithPair = startOfTie; ithPair < nLegalCQPairs; ithPair++) {
                cqpCompare = (al_LegalCatQuantPairs.get(ithPair).getCatValue()).compareTo(al_LegalCatQuantPairs.get(startOfTie).getCatValue());
                if ( cqpCompare <= 0) {
                    endOfTie = ithPair;
                }
            }       
        tempQDV = createNewQDV(al_LegalCatQuantPairs.get(startOfTie).getCatValue(), startOfTie, endOfTie);
        
        al_QDVs.add(tempQDV);      
            startOfTie = endOfTie + 1;
            endOfTie = startOfTie;
            if (endOfTie == nLegalCQPairs)
                endOfStory = true;      
        }   while (endOfStory == false);  
        
        System.out.println("141 cqdv, end unstackLegalCQPairs()");
    }
    
    private QuantitativeDataVariable createNewQDV(String qdvLabel, int fromHere, int toThere) {
        // System.out.println("145 cqdv, createNewQDV(String qdvLabel, int fromHere, int toThere)");
        categoryLabels.add(qdvLabel);
        QuantitativeDataVariable theNewQDV;
        int thisMany = toThere - fromHere + 1;
        double[] theQuants = new double[thisMany];
        for (int ith = 0; ith < thisMany; ith++) {
            theQuants[ith] = al_LegalCatQuantPairs.get(ith + fromHere).getQuantValueDouble();
        }
        
        theNewQDV = new QuantitativeDataVariable(qdvLabel, theQuants);
        //System.out.println("155 cqdv, endcreateNewQDV(String qdvLabel, int fromHere, int toThere)");
        return theNewQDV;
    }
    
    public ArrayList<QuantitativeDataVariable> getAllQDVs() {
        System.out.println("160 cqdv, getting all qdvs");
        for (int ithQ = 0; ithQ < al_QDVs.size(); ithQ++) {
          al_QDVs.get(ithQ).toString();
        }
        System.out.println("164 cqdv, getting all qdvs");
        return al_QDVs; 
    }
    
    public void unstackToDataStruct() {
        System.out.println("174 cqdv,unstackToDataStruct()");
        dm.addNStackedVariables(al_QDVs);
    }
    
    public int getCountOfLabels() { return categoryLabels.size(); }
    public ArrayList<String> getCategoryLabels() { return categoryLabels; }
    
    public String toString() {
        System.out.println("172 cqdv, toString");
        for (int ithLegal = 0; ithLegal < nLegalCQPairs; ithLegal++) {
            String catValue = al_LegalCatQuantPairs.get(ithLegal).getCatValue();
            double quantValue = al_LegalCatQuantPairs.get(ithLegal).getQuantValueDouble();
        }
        
        String retString = "Done de Dune dune with cqdv.toString";
        return retString;
    }
}
