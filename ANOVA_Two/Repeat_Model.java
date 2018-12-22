/**************************************************
 *                Repeat_Model                    *
 *                  08/15/18                      *
 *                    12:00                       *
 *************************************************/
package ANOVA_Two;

import genericClasses.CategoricalDataVariable;
import genericClasses.StudentizedRangeQ;
import genericClasses.CatQuantPair;
import genericClasses.ANOVA_Level;
import genericClasses.UnivariateContinDataObj;
import genericClasses.QuantitativeDataVariable;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import probabilityDistributions.FDistribution;
import splat.*;

public class Repeat_Model {
    // POJOs
    boolean lowEndIsFixed, highEndYIsFixed, tempEmptyCellExists, emptyCellExists,
            nonUniqueExists, balanceInBlocks, replicationInBlocks;
    
    int nDataTriples, nBlockValues, nTreatValues, nLegalResponses, nLevels, 
        dfBlocks, dfTreats, nBlocks, nTreats, dfInteraction, dfCells, dfTotal, 
        dfError, totalN;
    
    double  lowEndYScaleFixedAt, highEndYScaleFixedAt, msBlocks, msTreats, 
            minHorizontal, maxHorizontal, minVertical, maxVertical, confLevel,
            msInteraction, msError, fStatRows, fStatColumns, pValRows, 
            pValColumns, ssBlocks, ssTreats, ssSubgr, ssWithin, ssCells, 
            ssError, ssTotal;
    
    String  blocksLabel, treatsLabel, theRespVar, sourceString, displayChoice, 
            titleString, meansOrBars, sourceStringRows, sourceStringCols, 
            sourceStringError, sourceStringTotal;  
    String[] levelLabels;
    ArrayList<String> stringsToPrint;  // Are these
    ObservableList<String> blockLevels, treatLevels;//, factorAB_Levels;

    // My classes
    ANOVA2_BarViews twoWayANOVAStDev, twoWayANOVAStErr, twoWayANOVAMarginOfErr, 
                    twoWayANOVABarAndMean;
    ANOVA2_BoxCircleInterActView twoWayANOVACirclePlot,  twoWayANOVAInteraction,
                                 twoWayANOVABoxPlot;
    ANOVA2_ContViews twoWayANOVAResVsFitPlot, twoWayANOVAResidualsPlot;
    ArrayList<ANOVA_Level> listOfBlocks, listOfTreats, listOfCategLevels_AB;
    ANOVA2_MainEffect_AView mainEffectBlocks;
    ANOVA2_MainEffect_BView mainEffectTreats;
    ANOVA2_Procedure rcb_Procedure;  
    
    CategoricalDataVariable blockCatVar, treatCatVar, factor_AB_CatDataVar;  
    ArrayList<CatQuantPair> allTheData;
    ANOVA2_PrelimANOVA1 prelimBlocks, prelimTreats, prelimAB;
    QuantitativeDataVariable allData;
    QuantitativeDataVariable responseVar;
    StudentizedRangeQ studRangeQ;
    Splat_DataManager dm;
    
    public Repeat_Model( Splat_DataManager dm,
                         ANOVA2_Procedure rcb_Procedure,
                         CategoricalDataVariable blockValues, 
                         CategoricalDataVariable treatValues,
                         QuantitativeDataVariable responseVar) {   
        this.dm = dm;
        this.rcb_Procedure = rcb_Procedure;
        this.blockCatVar = blockValues;
        this.treatCatVar = treatValues;
        this.responseVar = responseVar;
        
        stringsToPrint = new ArrayList();
        
        nDataTriples = responseVar.get_nDataPointsLegal();
        
        blocksLabel = blockValues.getDataLabel();
        treatsLabel = treatValues.getDataLabel();

        // Check for equal column size
        nBlockValues = blockValues.get_N();
        nTreatValues = treatValues.get_N();
        nLegalResponses = responseVar.get_nDataPointsLegal();
        
        for (int ith = 0; ith < nBlockValues; ith++) {
            String blockValue = blockCatVar.getIthDataPtAsString(ith);
            String treatValue = treatCatVar.getIthDataPtAsString(ith);
            double responseValue = responseVar.getIthDataPtAsDouble(ith);
        }
               
        if (((nBlockValues != nTreatValues) || (nBlockValues != nLegalResponses))) {
            System.out.println("Ack!!  nFactorA = nFactorB = nResponse not true");
            System.exit(1);
        }
        
        doTwoWayANOVA();
    }   // End of constructor    
    
    
    private void doTwoWayANOVA() {
        initializeTwoWayANOVA();  
        performInitialOneWays();
        if (emptyCellExists) {
            System.out.println("114 ANOVA2_Mod, empty cell exists");
            return;
        }
        if (balanceInBlocks) {
            System.out.println("118, ANOVA2 Model, Balanced two way ANOVA");
            
            if (!replicationInBlocks) {
                doRCBAnalysis_1();
                print_ANOVA2_Results_1();
            }
            else {
                doRCBAnalysis_n();
                print_ANOVA2_Results_n();
            }
                
        }
        else // ... there is unbalance
        {
            System.out.println("124 ANOVA2 Model, UNbalanced two way ANOVA");
        }

    }

    private void initializeTwoWayANOVA() {
        confLevel = 0.95;
        lowEndIsFixed = false;
        highEndYIsFixed = false;
        nLevels = 0;  //  Number of listOfLevels = as determined below
        blockLevels = FXCollections.observableArrayList();
        treatLevels = FXCollections.observableArrayList();
        allTheData = new ArrayList<>();
        titleString = "Holy Shapsagen";
    } 

    private void performInitialOneWays() {
        prelimBlocks = new ANOVA2_PrelimANOVA1(dm, blockCatVar, responseVar);
        prelimBlocks.doOneWayANOVA();       
        nBlocks = prelimBlocks.getNLevels();
        blockLevels = prelimBlocks.getCategoryLabels();
         
        prelimTreats = new ANOVA2_PrelimANOVA1(dm, treatCatVar, responseVar);
        prelimTreats.doOneWayANOVA();
        nTreats = prelimTreats.getNLevels();       
        treatLevels = prelimTreats.getCategoryLabels();
        
        nDataTriples = nLegalResponses;
        factor_AB_CatDataVar = new CategoricalDataVariable ("Interactions", nDataTriples);
        String interactionABString;
        
        // Code for replication RCB
        // -----------------------------------------------------------------
        
        factor_AB_CatDataVar = new CategoricalDataVariable ("Interactions", nDataTriples);
        for (int ithDataPoint = 0; ithDataPoint < nLegalResponses; ithDataPoint++) {
            String dataA = blockCatVar.getIthDataPtAsString(ithDataPoint);
            String dataB = treatCatVar.getIthDataPtAsString(ithDataPoint);
            interactionABString = dataA + "&" + dataB;
            factor_AB_CatDataVar.setIthDataPtAsString(ithDataPoint, interactionABString);
        }   //  next ithDataPoint    
        
        /*********************************************************************
         *                                                                   *
         * Count number of values for each interaction.  We want to know if  *
         * there is (a) balance, and (b) replication in the blocks.          *
         *                                                                   *
         ********************************************************************/
        balanceInBlocks = true;
        replicationInBlocks = true;
        
    }   //  end performOneWays
    
    //  Randomized Block, no replicates
    private void doRCBAnalysis_1() {  
            ssBlocks = prelimBlocks.getSSTreatments();
            dfBlocks = prelimBlocks.getDFLevels();
            msBlocks = ssBlocks / dfBlocks;

            ssTreats = prelimTreats.getSSTreatments();
            dfTreats = prelimTreats.getDFLevels(); 
            msTreats = ssTreats / dfTreats;
            
            ssTotal = responseVar.getTheSS();
            dfTotal = nLegalResponses - 1;
            
            ssError = ssTotal - ssTreats - ssBlocks;
            dfError = (nBlocks - 1) * (nTreats - 1);
            msError = ssError / dfError;
            
            double F_Treats = msTreats / msError;
            double F_Blocks = msBlocks / msError;
             
            double rSquare = (ssTotal - ssError) / ssTotal;

            // double p = dfTotal - (nTreats + nLevels - 2);
            double p = nTreats + nBlocks - 2;
            double adjRSquare = 1.0 - (1 - rSquare) * (nLegalResponses - 1.) / (nLegalResponses - p - 1);
            double stErrPred = Math.sqrt(msError);
            
            /*
            System.out.println("228 RCB_Model, ssBlocks / dfBlocks = " + ssBlocks + " / " + dfBlocks);
            System.out.println("229 RCB_Model, ssTreats / dfTreats = " + ssTreats + " / " + dfTreats);
            
            System.out.println("228 RCB_Model, msBlocks = " + msBlocks);
            System.out.println("229 RCB_Model, msTreats = " + msTreats);
            
            System.out.println("230 RCB_Model, ssTotal / dfTotal = " + ssTotal + " / " + dfTotal);
            System.out.println("231 RCB_Model, ssError / dfError = " + ssError + " / " + dfError);
            System.out.println("232 RCB_Model, F_Treats / F_Blocks = " + F_Treats + " / " + F_Blocks);   

            System.out.println("241 RCB_Model, rSquare / S = " + rSquare + " / " + stErrPred);
            System.out.println("241 RCB_Model, adjRSquare / S = " + adjRSquare + " / " + stErrPred);
            */

        fStatRows = msBlocks / msError;
        FDistribution fDistRows = new FDistribution( dfBlocks, dfError);
        pValRows = fDistRows.getRightTailArea(fStatRows);
        
        fStatColumns = msTreats / msError;
        FDistribution fDistColumns = new FDistribution( dfTreats, dfError);
        pValColumns = fDistColumns.getRightTailArea(fStatColumns);
        
        //fStatInteraction = msInteraction / msError;
        //FDistribution fDistInteraction = new FDistribution( dfInteraction, dfError);
        //pValInteraction = fDistInteraction.getRightTailArea(fStatInteraction); 
    }  // end doTwoWayAnalysis
    
        //  Randomized Block, n replicates
    private void doRCBAnalysis_n() {  
        System.out.println("278 RCB_Model doRCBAnalysis_n()");
        ssBlocks = prelimBlocks.getSSTreatments();
        dfBlocks = prelimBlocks.getDFLevels();
        msBlocks = ssBlocks / dfBlocks;

        ssTreats = prelimTreats.getSSTreatments();
        dfTreats = prelimTreats.getDFLevels(); 
        msTreats = ssTreats / dfTreats;

        ssTotal = responseVar.getTheSS();
        dfTotal = nLegalResponses - 1;

        ssError = ssTotal - ssTreats - ssBlocks;
        dfError = (nBlocks - 1) * (nTreats - 1);
        msError = ssError / dfError;

        double F_Treats = msTreats / msError;
        double F_Blocks = msBlocks / msError;

        double rSquare = (ssTotal - ssError) / ssTotal;

        // double p = dfTotal - (nTreats + nLevels - 2);
        double p = nTreats + nBlocks - 2;
        double adjRSquare = 1.0 - (1 - rSquare) * (nLegalResponses - 1.) / (nLegalResponses - p - 1);
        double stErrPred = Math.sqrt(msError);

        /*
        System.out.println("228 RCB_Model, ssBlocks / dfBlocks = " + ssBlocks + " / " + dfBlocks);
        System.out.println("229 RCB_Model, ssTreats / dfTreats = " + ssTreats + " / " + dfTreats);

        System.out.println("228 RCB_Model, msBlocks = " + msBlocks);
        System.out.println("229 RCB_Model, msTreats = " + msTreats);

        System.out.println("230 RCB_Model, ssTotal / dfTotal = " + ssTotal + " / " + dfTotal);
        System.out.println("231 RCB_Model, ssError / dfError = " + ssError + " / " + dfError);
        System.out.println("232 RCB_Model, F_Treats / F_Blocks = " + F_Treats + " / " + F_Blocks);   

        System.out.println("241 RCB_Model, rSquare / S = " + rSquare + " / " + stErrPred);
        System.out.println("241 RCB_Model, adjRSquare / S = " + adjRSquare + " / " + stErrPred);
        */

        fStatRows = msBlocks / msError;
        FDistribution fDistRows = new FDistribution( dfBlocks, dfError);
        pValRows = fDistRows.getRightTailArea(fStatRows);
        
        fStatColumns = msTreats / msError;
        FDistribution fDistColumns = new FDistribution( dfTreats, dfError);
        pValColumns = fDistColumns.getRightTailArea(fStatColumns);
        
        //fStatInteraction = msInteraction / msError;
        //FDistribution fDistInteraction = new FDistribution( dfInteraction, dfError);
        //pValInteraction = fDistInteraction.getRightTailArea(fStatInteraction); 
    }  // end doTwoWayAnalysis
    
    
    
    private void print_ANOVA2_Results_1() {  
        stringsToPrint = new ArrayList<>();    
        System.out.printf("-----------------------------------------------------------------------------------\n");
        stringsToPrint.add(String.format("-----------------------------------------------------------------------------------\n"));
        System.out.printf("Source of      Degrees of       Sum of\n");
        stringsToPrint.add(String.format("Source of      Degrees of       Sum of\n"));
        System.out.printf("Variation       Freedom        Squares         Mean Square       F       P-value\n");
        stringsToPrint.add(String.format("Variation       Freedom        Squares         Mean Square       F       P-value\n"));
        System.out.printf("-----------------------------------------------------------------------------------\n");
        stringsToPrint.add(String.format("-----------------------------------------------------------------------------------\n"));
        
        sourceString = leftMostChars(treatsLabel, 12);

        System.out.printf("%10s      %4d     %13.2f        %8.2f     %8.2f     %6.4f\n", sourceString, dfTreats, ssTreats,
                                                                                          msTreats,fStatColumns, pValColumns);
        
        sourceString = leftMostChars(blocksLabel, 12);
        System.out.printf("%10s      %4d     %13.2f        %8.2f     %8.2f     %6.4f\n", sourceString, dfBlocks, ssBlocks,
                                                                                          msBlocks, fStatRows, pValRows);

        sourceString = leftMostChars("Error", 12);
        System.out.printf("%10s      %4d     %13.2f        %8.2f\n", sourceString, dfError,ssError, msError);
        stringsToPrint.add(String.format("%10s      %4d     %13.2f        %8.2f\n", sourceString, dfError, ssError, msError));
        
        sourceString = leftMostChars("Total", 12);
        System.out.printf("%10s      %4d     %13.2f\n", sourceString, dfTotal, ssTotal);
        stringsToPrint.add(String.format("%10s      %4d     %13.2f\n", sourceString, dfTotal, ssTotal));
        
        System.out.printf("-----------------------------------------------------------------------------------\n"); 
        stringsToPrint.add(String.format("-----------------------------------------------------------------------------------\n"));
   }    // end printANOVA_Results
    
    private void print_ANOVA2_Results_n() {  
        stringsToPrint = new ArrayList<>();    
        System.out.printf("-----------------------------------------------------------------------------------\n");
        stringsToPrint.add(String.format("-----------------------------------------------------------------------------------\n"));
        System.out.printf("Source of      Degrees of       Sum of\n");
        stringsToPrint.add(String.format("Source of      Degrees of       Sum of\n"));
        System.out.printf("Variation       Freedom        Squares         Mean Square       F       P-value\n");
        stringsToPrint.add(String.format("Variation       Freedom        Squares         Mean Square       F       P-value\n"));
        System.out.printf("-----------------------------------------------------------------------------------\n");
        stringsToPrint.add(String.format("-----------------------------------------------------------------------------------\n"));
        
        sourceString = leftMostChars(treatsLabel, 12);

        System.out.printf("%10s      %4d     %13.2f        %8.2f     %8.2f     %6.4f\n", sourceString, dfTreats, ssTreats,
                                                                                          msTreats,fStatColumns, pValColumns);
        
        sourceString = leftMostChars(blocksLabel, 12);
        System.out.printf("%10s      %4d     %13.2f        %8.2f     %8.2f     %6.4f\n", sourceString, dfBlocks, ssBlocks,
                                                                                          msBlocks, fStatRows, pValRows);
        


        sourceString = leftMostChars("Error", 12);
        System.out.printf("%10s      %4d     %13.2f        %8.2f\n", sourceString, dfError,ssError, msError);
        stringsToPrint.add(String.format("%10s      %4d     %13.2f        %8.2f\n", sourceString, dfError, ssError, msError));
        
        sourceString = leftMostChars("Total", 12);
        System.out.printf("%10s      %4d     %13.2f\n", sourceString, dfTotal, ssTotal);
        stringsToPrint.add(String.format("%10s      %4d     %13.2f\n", sourceString, dfTotal, ssTotal));
        
        System.out.printf("-----------------------------------------------------------------------------------\n"); 
        stringsToPrint.add(String.format("-----------------------------------------------------------------------------------\n"));
   }    // end printANOVA_Results
        
    public double getConfidenceLevel() { return confLevel; }
    
    public void setConfidenceLevel( double atThisLevel) {
        confLevel = atThisLevel;
    }
    
   //  Print the leftmost leftChars of original
   public static String leftMostChars(String original, int leftChars) {
       String longString = original + "                       ";
       String truncated = longString.substring(0, leftChars - 1);
       return truncated;
   }
   
     public String getFactorALabel() {return blockCatVar.getDataLabel();}
     public String getFactorBLabel() {return treatCatVar.getDataLabel();}
     public String getResponseLabel() { return responseVar.getTheDataLabel(); }

     public int getNFactorA_Levels() {  return nBlocks; }
     public int getNFactorB_Levels() {  return nTreats; }

     public String getChoiceOfPlot() { return displayChoice; }
     public String getMeansOrBars() { return meansOrBars; }

     public ObservableList <String> getCategoryLevels() { 
         return blockLevels; 
     }     
     
     public ObservableList <String> getFactorALevels() { 
         return blockLevels; 
     }
     
     public ObservableList <String> getFactorBLevels() { 
         return treatLevels; 
     } 

     public double getMinVertical() {return prelimBlocks.getMinVertical(); }
     public double getMaxVertical() {return prelimBlocks.getMaxVertical(); }  
     
     public UnivariateContinDataObj  getAllDataUCDO() {
         UnivariateContinDataObj dummyUCDO = new UnivariateContinDataObj();
         return dummyUCDO;
         // return responseVar.getUCDO(); 
     }

     public ANOVA2_PrelimANOVA1 getPrelimA() { return prelimBlocks; }
     public ANOVA2_PrelimANOVA1 getPrelimB() { return prelimTreats; }
     public ANOVA2_PrelimANOVA1 getPrelimAB() { return prelimAB; }
     
     public ArrayList<String> getANOVA2Report() {
          return stringsToPrint;
     }

     public int get_nDP() {return nDataTriples; }
}

