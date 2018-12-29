/**************************************************
 *                PrelimANOVA1                    *
 *                  05/15/18                      *
 *                    12:00                       *
 *************************************************/

package ANOVA_Two;

import dataObjects.CatQuantDataVariable;
import dataObjects.CategoricalDataVariable;
import probabilityDistributions.StudentizedRangeQ;
import utilityClasses.DataUtilities;
import dataObjects.ColumnOfData;
import dataObjects.UnivariateContinDataObj;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import probabilityDistributions.FDistribution;
import probabilityDistributions.TDistribution;
import splat.*;

public class ANOVA2_PrelimANOVA1 {
    // POJOs
    boolean lowEndYScaleIsFixed, highEndYScaleIsFixed, sampleSizesAreEqual;
    
    int nDataPairs, nExplanatory, nResponse, nLevels, dfLevels, dfError, 
        dfTotal, totalN, n_QDVs;
    
    double lowEndYScaleFixedAt, highEndYScaleFixedAt,  minHorizontal, 
           maxHorizontal, minVertical, maxVertical, ssTreatments, ssError, 
           ssTotal, msTreatments, msError, fStat, pValue, confidenceLevel, 
           lowCI_TK, highCI_TK, lowCI_HSD, highCI_HSD, qCritPlusMinus;
    
    String theExplanVar, theRespVar, sourceString, displayChoice, strIthLevel, 
           strJthLevel;
    ArrayList<String> anova1Report, postHocReport, allTheLabels;;
    ObservableList<String> categoryLabels;
    
    // My classes
    CatQuantDataVariable cqdv;
    ArrayList<QuantitativeDataVariable> allTheQDVs;
    StudentizedRangeQ studRangeQ;
    TDistribution tDistribution; 
    UnivariateContinDataObj tempUCDO, allData_UCDO;
    UnivariateContinDataObj[] allTheUCDOs;
    Data_Manager dm;

    public ANOVA2_PrelimANOVA1 (Data_Manager dm, CategoricalDataVariable explanatoryVar, QuantitativeDataVariable responseVar) {
        this.dm = dm;
        ColumnOfData colExplanVar = new ColumnOfData(explanatoryVar);    
        ColumnOfData colResponseVar = new ColumnOfData(responseVar);
        int tempNSize = explanatoryVar.get_N();
        
        cqdv = new CatQuantDataVariable(dm, colExplanVar, colResponseVar);
        allTheQDVs = new ArrayList<>();
        allTheQDVs = cqdv.getAllQDVs();   

        n_QDVs = allTheQDVs.size();
        allTheLabels = new ArrayList<>();
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            allTheLabels.add(allTheQDVs.get(iVars).getTheDataLabel());
        }        
        
        categoryLabels = FXCollections.observableArrayList();
        tDistribution = new TDistribution();

        for (int ithLabel = 0; ithLabel < allTheLabels.size(); ithLabel++) {
            categoryLabels.add(allTheLabels.get(ithLabel));
        }
        sampleSizesAreEqual = true;    //  Needed to distinguish T-K and HSD
              
        DataUtilities dataUtil = new DataUtilities();
        theExplanVar = explanatoryVar.getDataLabel();
        theRespVar = responseVar.getDataLabel();
        
        int numUDMs = allTheQDVs.size();
        allTheUCDOs = new UnivariateContinDataObj[numUDMs];
        for (int ithUDM = 0; ithUDM < numUDMs; ithUDM++) {
            allTheUCDOs[ithUDM] = new UnivariateContinDataObj(allTheQDVs.get(ithUDM));
        }
        
        allData_UCDO = allTheUCDOs[0];
        
        nLevels = allTheQDVs.size() - 1;
        initializeTheANOVA();        
        setupAnalysis();
        doOneWayANOVA();
    }
   
    public void doOneWayANOVA() {        
        doAnalysis();        
        // printANOVA_Results();       
    }
   
    private void initializeTheANOVA () {
        confidenceLevel = 0.95;
        lowEndYScaleIsFixed = false;
        highEndYScaleIsFixed = false; 
        anova1Report = new ArrayList<>();
        postHocReport = new ArrayList<>();
        } 
    
    private void setupAnalysis() {
        minVertical = allData_UCDO.getMinValue();
        maxVertical = allData_UCDO.getMaxValue();
        
        // Determine if sample sizes are equal (for T-K vs. HSD)
        // And while at it, get the level labels
        for (int ithLevel = 1; ithLevel < nLevels; ithLevel++) {
            int n1 = allTheUCDOs[ithLevel].getLegalN();
            int n2 = allTheUCDOs[ithLevel + 1].getLegalN();
            
            if (n1 != n2)
                sampleSizesAreEqual = false;
        }        
        
        for (int ithLevel = 0; ithLevel <= nLevels; ithLevel++) {
            allTheUCDOs[ithLevel].doMedianBasedCalculations();
            allTheUCDOs[ithLevel].doMeanBasedCalculations();
        }
    }   //  end setupFixedEffectsAnalysis
    
    public void doAnalysis() {  
        /********************************************************
         *           Calculate inferential statistics           *
         *******************************************************/
        ssTotal = allTheUCDOs[0].getTheSS();      
        ssError = 0.0;
        for (int ithLevel = 1; ithLevel <= nLevels; ithLevel++) {
            ssError += allTheUCDOs[ithLevel].getTheSS();
        }   
        
        ssTreatments = ssTotal - ssError;

        totalN = allTheUCDOs[0].getLegalN();
        dfTotal = totalN - 1;
        dfError = totalN - nLevels;
        dfLevels = nLevels - 1;
        
        msTreatments = ssTreatments / dfLevels;
        msError = ssError / dfError;
        
        // System.out.println("142 ANOVA_Model, msError = " + msError);

        fStat = msTreatments / msError;
        FDistribution fDist = new FDistribution( dfLevels, dfError);
        
        pValue = fDist.getRightTailArea(fStat);

    }  // end doFixedEffectsAnalysis
    
    public String getExplanatoryVariable() {return theExplanVar; }
    public String getResponseVariable() {return theRespVar; }
    
    public ArrayList<String> getANOVA1Report() { return anova1Report; }  
    
    public boolean getAreSampleSizesEqual() {return sampleSizesAreEqual; }

    public double getConfidenceLevel() { return confidenceLevel; }
    
    public void setConfidenceLevel( double atThisLevel) {
        confidenceLevel = atThisLevel;
    }
    
   //  Print the leftmost leftChars of original
   public static String leftMostChars(String original, int leftChars) {
       String longString = original + "                       ";
       String truncated = longString.substring(0, leftChars - 1);
       return truncated;
   }
   
      public QuantitativeDataVariable getIthUDM(int ith) {
       return allTheQDVs.get(ith);
   }
   
   public UnivariateContinDataObj getIthUCDO(int ith) {
       return allTheUCDOs[ith];
   }
   
   public int getNLevels() {  return nLevels; }
   
   public ObservableList <String> getCategoryLabels() {return categoryLabels; }
   
   public UnivariateContinDataObj getAllData_UCDO() { return allData_UCDO; }
   
   public ArrayList<QuantitativeDataVariable> getAllTheUDMs() {return allTheQDVs; }
   
   public double getPostHocPlusMinus() { return qCritPlusMinus; }
   
   // ***********************************************************
   //   The gets() below are for Two-Way ANOVA calculations
   // ***********************************************************
   
   public double getSSTreatments() {return ssTreatments; }
   public int getDFLevels() {return dfLevels; } 
   
   public double getSSError() {return ssError; }
   public int getDFError() {return dfError; } 
   
   public double getSSTotal() {return ssTotal; }
   public int getDFTotal() {return dfTotal; } 
   
   public double getMinVertical() { return minVertical; }
   public double getMaxVertical() { return maxVertical; }

}