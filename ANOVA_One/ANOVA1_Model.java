/**************************************************
 *                  ANOVA1_Model                  *
 *                    12/22/18                    *
 *                      12:00                     *
 *************************************************/

/**************************************************
 *           HSD and T-K last verified            *
 *                   02/06/18                     *
 *************************************************/

package ANOVA_One;

import genericClasses.StringUtilities;
import genericClasses.StudentizedRangeQ;
import genericClasses.DataUtilities;
import genericClasses.QuantitativeDataVariable;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import probabilityDistributions.FDistribution;

import probabilityDistributions.TDistribution;

public class ANOVA1_Model {
    // POJOs
    boolean lowEndYScaleIsFixed, highEndYScaleIsFixed, sampleSizesAreEqual;
    
    int nDataPairs, nExplanatory, nResponse, nLevels, dfLevels, dfError, 
        dfTotal, totalN;
    
    double lowEndYScaleFixedAt, highEndYScaleFixedAt, minHorizontal, 
           maxHorizontal, minVertical, maxVertical, ssTreatments, ssError, 
           ssTotal, msTreatments, msError, fStat, pValue, confidenceLevel,
           lowCI_TK, highCI_TK, lowCI_HSD, highCI_HSD, qCritPlusMinus;
    
    String explanatoryVariable, responseVariable, sourceString, displayChoice, 
           strIthLevel, strJthLevel;
    
    ArrayList<String> anova1Report, postHocReport, alStr_AllTheLabels;
    ObservableList<String> categoryLabels;

    // My classes
    ANOVA1_Procedure anova1_procedure;   //  Need to rename these 
    ArrayList<QuantitativeDataVariable> allTheQDVs;
    QuantitativeDataVariable tempQDV;   
    QuantitativeDataVariable allData_QDV;
    StudentizedRangeQ studRangeQ;
    TDistribution tDistribution;
    FDistribution fDist;

    public ANOVA1_Model (ANOVA1_Procedure anovaProc, 
                                 String explanatoryVariable, 
                                 String responseVariable,
                                 ArrayList<QuantitativeDataVariable>  al_AllTheQDVs,
                                 ArrayList<String> alStr_AllTheLabels) {
        categoryLabels = FXCollections.observableArrayList();
        tDistribution = new TDistribution();
        this.alStr_AllTheLabels = alStr_AllTheLabels;
        
        for (int ithLabel = 0; ithLabel < alStr_AllTheLabels.size(); ithLabel++) {
            categoryLabels.add(alStr_AllTheLabels.get(ithLabel));
        }
        sampleSizesAreEqual = true;    //  Needed to distinguish T-K and HSD
              
        DataUtilities dataUtil = new DataUtilities();
        this.explanatoryVariable = explanatoryVariable;
        this.responseVariable = responseVariable;
        this.allTheQDVs = new ArrayList();
        this.allTheQDVs = al_AllTheQDVs;
        
        int nQDVs = al_AllTheQDVs.size();
        
        allData_QDV = al_AllTheQDVs.get(0);
        
        nLevels = al_AllTheQDVs.size() - 1;
        initializeTheANOVA();        
        setupAnalysis();
        doOneWayANOVA();
        LevenesTest lev = new LevenesTest(allTheQDVs);
    }
   
    private void doOneWayANOVA() {        
        doAnalysis();        
        printANOVA_Results();       
    }
    
    public void doOneWay4TwoWayANOVA() {
        doAnalysis();
    }

    private void initializeTheANOVA () {
        confidenceLevel = 0.95;
        lowEndYScaleIsFixed = false;
        highEndYScaleIsFixed = false; 
        anova1Report = new ArrayList<>();
        postHocReport = new ArrayList<>();
        } 
    
    private void setupAnalysis() {
        // System.out.println("102 ANOVA1 Model, setupAnalysis()");
        minVertical = allData_QDV.getMinValue();
        maxVertical = allData_QDV.getMaxValue();
        
        // Determine if sample sizes are equal (for T-K vs. HSD)
        // And while at it, get the level labels
        for (int ithLevel = 1; ithLevel < nLevels; ithLevel++) {
            int n1 = allTheQDVs.get(ithLevel).getLegalN();
            int n2 = allTheQDVs.get(ithLevel + 1).getLegalN();
            
            if (n1 != n2)
                sampleSizesAreEqual = false;
        }        
        
        for (int ithLevel = 0; ithLevel <= nLevels; ithLevel++) {
            allTheQDVs.get(ithLevel).doMedianBasedCalculations();
            allTheQDVs.get(ithLevel).doMeanBasedCalculations();
        }
    }   //  end setupFixedEffectsAnalysis
    
    public void doAnalysis() {  
        /********************************************************
         *           Calculate inferential statistics           *
         *******************************************************/
        ssTotal = allTheQDVs.get(0).getTheSS();   
        ssError = 0.0;
        for (int ithLevel = 1; ithLevel <= nLevels; ithLevel++) {
            ssError += allTheQDVs.get(ithLevel).getTheSS();
        }   
        
        ssTreatments = ssTotal - ssError;

        totalN = allTheQDVs.get(0).getLegalN();
        dfTotal = totalN - 1;
        dfError = totalN - nLevels;
        dfLevels = nLevels - 1;
        
        msTreatments = ssTreatments / dfLevels;
        msError = ssError / dfError;

        fStat = msTreatments / msError;
        fDist = new FDistribution( dfLevels, dfError);
        pValue = fDist.getRightTailArea(fStat);
        /*********************************************************************
         *           Calculations needed for post hoc testing                *
         *********************************************************************/
        
        if (sampleSizesAreEqual == true)
            doHonestlySigDiff();
        else
            doTukeyKramer();
        
    }  // end doFixedEffectsAnalysis
    
private void doTukeyKramer() {
        /********************************************************************
        *   Tukey Honestly Significant Difference.  If n1 = n2, T-K and     *
        *   HSD appear to be algebraically equivalent, but I'm taking no    *
        *   chances.  System.out's are there b/c I'm not quite sure what    *
        *   or where or the format to print this information                *
        ********************************************************************/
        studRangeQ = new StudentizedRangeQ();
        double qTK = studRangeQ.qrange(0.95, // cumulative p -- use .95 if alpha = .05
                                    (double)nLevels, // number of groups
                                    (double)dfError, // df error
                                     1.0);  // use a 1.0 to get stu range stat)
        double wT = qTK / Math.sqrt(2.0); // Dean & Voss, p85
        
        
        System.out.println("   Treatment/   Treatment/     Mean        Standard    95%CI Lower    95% CI Upper");
        postHocReport.add(String.format("   Treatment/   Treatment/     Mean        Standard      95P CI Lower    95PC CI Upper\n"));
        System.out.println("     Group       Group      Difference       Error        Bound          Bound");
        postHocReport.add(String.format("     Group       Group      Difference       Error          Bound          Bound\n"));        
        for (int ithLevel = 1; ithLevel < nLevels; ithLevel++) {
            for (int jthLevel = ithLevel + 1; jthLevel < nLevels  + 1; jthLevel++) {
                int nIth = allTheQDVs.get(ithLevel).getLegalN();
                int nJth = allTheQDVs.get(jthLevel).getLegalN();
                double xBarIth = allTheQDVs.get(ithLevel).getTheMean();
                double xBarJth = allTheQDVs.get(jthLevel).getTheMean();
                double diff_xBar = xBarIth - xBarJth;
                qCritPlusMinus = wT * Math.sqrt(msError * (1.0/nIth + 1.0/nJth));
                lowCI_TK = diff_xBar - qCritPlusMinus;
                highCI_TK = diff_xBar + qCritPlusMinus;
                
                strIthLevel = alStr_AllTheLabels.get(ithLevel);
                strJthLevel = alStr_AllTheLabels.get(jthLevel);

                System.out.printf("%10s  %10s       %8.3f     %8.3f       %8.3f       %8.3f\n", strIthLevel,  
                                                                                          strJthLevel,
                                                                                          diff_xBar,
                                                                                          qCritPlusMinus,
                                                                                          lowCI_TK,
                                                                                          highCI_TK);
                
                postHocReport.add(String.format("%10s  %10s       %8.3f     %8.3f       %8.3f       %8.3f\n", strIthLevel,  
                                                                                          strJthLevel,
                                                                                          diff_xBar,
                                                                                          qCritPlusMinus,
                                                                                          lowCI_TK,
                                                                                          highCI_TK));
            }
        }
        
        // ***********************   Printing stuff   ******************************************
        System.out.println("\n\n");
        postHocReport.add(String.format("\n\n"));
        System.out.println("   Treatment/   Sample     Sample      Sample     Std Err     Margin     Lower 95%    Upper 95%");
        postHocReport.add(String.format("   Treatment/   Sample     Sample      Sample     Std Err     Margin     Lower 95PC    Upper 95PC\n"));
        System.out.println("     Group       Size       Mean       St Dev     of mean    of Error      Bound        Bound");
        postHocReport.add(String.format("     Group       Size       Mean       St Dev     of mean    of Error      Bound        Bound\n"));
        for (int ithLevel = 1; ithLevel <= nLevels; ithLevel++) {

            strIthLevel = alStr_AllTheLabels.get(ithLevel);
            int iSampleSize = allTheQDVs.get(ithLevel).getLegalN();
            double iMean = allTheQDVs.get(ithLevel).getTheMean();
            double iStandDev = allTheQDVs.get(ithLevel).getTheStandDev();
            double iStandErr = iStandDev / Math.sqrt(iSampleSize - 1.0);
            
            tDistribution.set_df_for_t(iSampleSize - 1);
            double middleInterval[] = new double[2];
            middleInterval = tDistribution.getInverseMiddleArea(0.95);
            double critical_t = middleInterval[1];
            double iMarginOfError = critical_t * iStandErr;
            double iLowerBound = iMean - iMarginOfError;
            double iUpperBound = iMean + iMarginOfError;

        System.out.printf("%10s      %4d     %8.3f    %8.3f    %8.3f   %8.3f     %8.3f     %8.3f\n", strIthLevel,  
                                                                                                            iSampleSize,
                                                                                                            iMean,
                                                                                                            iStandDev,
                                                                                                            iStandErr,
                                                                                                            iMarginOfError,
                                                                                                            iLowerBound,
                                                                                                            iUpperBound);  
        postHocReport.add(String.format("%10s  %10s       %8.3f     %8.3f       %8.3f       %8.3f\n", strIthLevel,  
                                                                                                            iSampleSize,
                                                                                                            iMean,
                                                                                                            iStandDev,
                                                                                                            iStandErr,
                                                                                                            iMarginOfError,
                                                                                                            iLowerBound,
                                                                                                            iUpperBound));
        }     
}
    
private void doHonestlySigDiff() {
        /********************************************************************
        *   Tukey Honestly Significant Difference.  If n1 = n2, T-K and     *
        *   HSD appear to be algebraically equivalent, but I'm taking no    *
        *   chances.  System.out's are there b/c I'm not quite sure what    *
        *   or where or the format to print this information                *
        ********************************************************************/  
        studRangeQ = new StudentizedRangeQ();
        double qHSD = studRangeQ.qrange(0.95,           // cumulative p -- use .95 if alpha = .05
                                    (double)nLevels,    // number of groups
                                    (double)dfError,    // df error
                                     1.0);              // use a 1.0 to get stu range stat)
        
        System.out.println("   Treatment/   Treatment/     Mean        Standard    95%CI Lower    95% CI Upper");
        postHocReport.add(String.format("   Treatment/   Treatment/     Mean        Standard      95P CI Lower    95PC CI Upper\n"));
        System.out.println("     Group       Group      Difference       Error        Bound          Bound");
        postHocReport.add(String.format("     Group       Group      Difference       Error          Bound          Bound\n"));        
        for (int ithLevel = 1; ithLevel < nLevels; ithLevel++) {
            for (int jthLevel = ithLevel + 1; jthLevel < nLevels  + 1; jthLevel++) {
                int nIth = allTheQDVs.get(ithLevel).getLegalN();
                int nJth = allTheQDVs.get(jthLevel).getLegalN();
                double xBarIth = allTheQDVs.get(ithLevel).getTheMean();
                double xBarJth = allTheQDVs.get(jthLevel).getTheMean();
                double diff_xBar = xBarIth - xBarJth;
                double stErrDiff = Math.sqrt(msError/nIth);
                qCritPlusMinus = qHSD * stErrDiff;
                lowCI_HSD = diff_xBar - qCritPlusMinus;
                highCI_HSD = diff_xBar + qCritPlusMinus;
                
                strIthLevel = alStr_AllTheLabels.get(ithLevel);
                strJthLevel = alStr_AllTheLabels.get(jthLevel);

                System.out.printf("%10s  %10s       %8.3f     %8.3f       %8.3f       %8.3f\n", strIthLevel,  
                                                                                          strJthLevel,
                                                                                          diff_xBar,
                                                                                          stErrDiff,
                                                                                          lowCI_HSD,
                                                                                          highCI_HSD);  
                postHocReport.add(String.format("%10s  %10s       %8.3f     %8.3f       %8.3f       %8.3f\n", strIthLevel,  
                                                                                          strJthLevel,
                                                                                          diff_xBar,
                                                                                          stErrDiff,
                                                                                          lowCI_HSD,
                                                                                          highCI_HSD));
            }
        }
        
        // ***********************   Printing stuff   ******************************************
        System.out.println("\n\n");
        postHocReport.add(String.format("\n\n"));
        System.out.println("   Treatment/   Sample     Sample      Sample     Std Err     Margin     Lower 95%    Upper 95%");
        postHocReport.add(String.format("   Treatment/   Sample     Sample      Sample     Std Err     Margin     Lower 95PC    Upper 95PC\n"));
        System.out.println("     Group       Size       Mean       St Dev     of mean    of Error      Bound        Bound");
        postHocReport.add(String.format("     Group       Size       Mean       St Dev     of mean    of Error      Bound        Bound\n"));
        for (int ithLevel = 1; ithLevel <= nLevels; ithLevel++) {

            strIthLevel = alStr_AllTheLabels.get(ithLevel);
            int iSampleSize = allTheQDVs.get(ithLevel).getLegalN();
            double iMean = allTheQDVs.get(ithLevel).getTheMean();
            double iStandDev = allTheQDVs.get(ithLevel).getTheStandDev();
            double iStandErr = iStandDev / Math.sqrt(iSampleSize - 1.0);
            
            tDistribution.set_df_for_t(iSampleSize - 1);
            double middleInterval[] = new double[2];
            middleInterval = tDistribution.getInverseMiddleArea(0.95);
            double critical_t = middleInterval[1];
            double iMarginOfError = critical_t * iStandErr;
            double iLowerBound = iMean - iMarginOfError;
            double iUpperBound = iMean + iMarginOfError;

        System.out.printf("%10s      %4d     %8.3f    %8.3f    %8.3f   %8.3f     %8.3f     %8.3f\n", strIthLevel,  
                                                                                                            iSampleSize,
                                                                                                            iMean,
                                                                                                            iStandDev,
                                                                                                            iStandErr,
                                                                                                            iMarginOfError,
                                                                                                            iLowerBound,
                                                                                                            iUpperBound);  
        postHocReport.add(String.format("%10s      %4d     %8.3f    %8.3f    %8.3f   %8.3f     %8.3f     %8.3f\n", strIthLevel,  
                                                                                                            iSampleSize,
                                                                                                            iMean,
                                                                                                            iStandDev,
                                                                                                            iStandErr,
                                                                                                            iMarginOfError,
                                                                                                            iLowerBound,
                                                                                                            iUpperBound));
        }        
}
        
    public void printANOVA_Results() {
        
        System.out.printf("-----------------------------------------------------------------------------------\n");
        anova1Report.add(String.format("-----------------------------------------------------------------------------------\n"));
        System.out.printf("Source of      Degrees of       Sum of\n");
        anova1Report.add(String.format("Source of      Degrees of       Sum of\n"));
        System.out.printf("Variation       Freedom        Squares         Mean Square       F       P-value\n");
        anova1Report.add(String.format("Variation       Freedom        Squares         Mean Square       F       P-value\n"));
        System.out.printf("-----------------------------------------------------------------------------------\n");
        anova1Report.add(String.format("-----------------------------------------------------------------------------------\n"));
        sourceString = leftMostChars("Treatments", 12);
        System.out.printf("%10s      %4d     %13.2f        %8.2f     %8.2f     %6.4f\n", sourceString,  
                                                                                          dfLevels,
                                                                                          ssTreatments,
                                                                                          msTreatments,
                                                                                          fStat, pValue);
        
        anova1Report.add(String.format("%10s      %4d     %13.2f        %8.2f     %8.2f     %6.4f\n", 
                                                                                              sourceString,  
                                                                                              dfLevels,
                                                                                              ssTreatments,
                                                                                              msTreatments,
                                                                                              fStat, pValue)); 
        
        sourceString = leftMostChars("Error", 12);
        System.out.printf("%10s      %4d     %13.2f        %8.2f\n", sourceString, dfError,
                                                                         ssError, msError);
        anova1Report.add(String.format("%10s      %4d     %13.2f        %8.2f\n", sourceString, dfError,
                                                                                        ssError, msError));
        
        sourceString = leftMostChars("Total", 12);
        System.out.printf("%10s      %4d     %13.2f\n", sourceString, dfTotal, ssTotal);
        anova1Report.add(String.format("%10s      %4d     %13.2f\n", sourceString, dfTotal, ssTotal));
        
        System.out.printf("-----------------------------------------------------------------------------------\n"); 
        anova1Report.add(String.format("-----------------------------------------------------------------------------------\n"));
        
        //  Add the Post Hoc linwa
        int nPostHocLines = postHocReport.size();
        for (int ithPHLine = 0; ithPHLine < nPostHocLines; ithPHLine++) {
            anova1Report.add(postHocReport.get(ithPHLine));
        }
        
   }    // end printANOVA_Results
    
    public String getExplanatoryVariable() {return explanatoryVariable; }
    public String getResponseVariable() {return responseVariable; }
    
    public ArrayList<QuantitativeDataVariable> getAllQDVs() { return allTheQDVs; }
    
    public ArrayList<String> getANOVA1Report() { return anova1Report; }  
    
    public boolean getAreSampleSizesEqual() {return sampleSizesAreEqual; }

    public double getConfidenceLevel() { return confidenceLevel; }
    
    public void setConfidenceLevel( double atThisLevel) {
        confidenceLevel = atThisLevel;
    }
    
    private String leftMostChars(String original, int leftChars) {
       return StringUtilities.truncateString(original, leftChars);
    }
   
    public QuantitativeDataVariable getIthQDV(int ith) {
       return allTheQDVs.get(ith);
    }
   
    public FDistribution getFDist() { return fDist; }
    public int getDFLevels() {  return dfLevels; }
    public int getDFError() {  return dfError; }
    public int getNLevels() {  return nLevels; }
    public double getFStat() { return fStat; }
   
    public ObservableList <String> getCategoryLabels() {return categoryLabels; }
   
    public QuantitativeDataVariable getAllData_QDV() { return allData_QDV; }
   
    public ArrayList<QuantitativeDataVariable> getAllTheQDVs() {return allTheQDVs; }
   
    public double getPostHocPlusMinus() { return qCritPlusMinus; }

}
