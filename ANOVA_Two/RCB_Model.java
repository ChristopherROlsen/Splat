/**************************************************
 *                  RCB_Model                     *
 *                  12/22/18                      *
 *                    12:00                       *
 *************************************************/
package ANOVA_Two;

import utilityClasses.StringUtilities;
import dataObjects.CategoricalDataVariable;
import probabilityDistributions.StudentizedRangeQ;
import dataObjects.CatQuantPair;
import genericClasses.ANOVA_Level;
import dataObjects.UnivariateContinDataObj;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import probabilityDistributions.FDistribution;
import splat.*;

public class RCB_Model {
    // POJOs
    boolean lowEndIsFixed, highEndYIsFixed;
    
    int nDataTriples, nBlockValues, nTreatValues, nLegalResponses, nLevels, dfBlocks, dfTreats,
        nBlocks, nTreats, dfInteraction, dfCells, dfTotal, 
        dfError, totalN, nReplications;
    
    double  lowEndYScaleFixedAt, highEndYScaleFixedAt, msBlocks, msTreats, 
            minHorizontal, maxHorizontal, minVertical, maxVertical, confLevel,
            msInteraction, msError, fStatRows, fStatColumns, fStatInteraction,
            pValRows, pValColumns, ssBlocks, ssTreats, ssSubgr, ssInteraction,
            ssWithin, ssCells, ssError, ssTotal, pValInteraction;
    
    String  blocksLabel, treatsLabel, theRespVar, sourceString, displayChoice, 
            titleString, meansOrBars, sourceStringRows, sourceStringCols, 
            sourceStringError, sourceStringTotal;
    
    String[] levelLabels;
    
    ArrayList<String> stringsToPrint;  // Are these
    
    // These are names for the levels
    ObservableList<String> blockLevels, treatLevels;//, factorAB_Levels;

    StudentizedRangeQ studRangeQ;
    
    // These are lists of CategoricalLevel objects
    ArrayList<ANOVA_Level> listOfBlocks, listOfTreats, listOfCategLevels_AB;
    ArrayList<CatQuantPair> allTheData;
    ANOVA2_BarViews twoWayANOVAStDev; 
    ANOVA2_BarViews twoWayANOVAStErr, twoWayANOVAMarginOfErr, twoWayANOVABarAndMean;
    ANOVA2_BoxCircleInterActView twoWayANOVABoxPlot;
    
    ANOVA2_BoxCircleInterActView twoWayANOVACirclePlot,  twoWayANOVAInteraction;
    
    ANOVA2_ContViews twoWayANOVAResVsFitPlot; 
    ANOVA2_ContViews twoWayANOVAResidualsPlot;
    
    ANOVA2_MainEffect_AView mainEffectBlocks;
    ANOVA2_MainEffect_BView mainEffectTreats;
    Data_Manager dm;
    ANOVA2_Procedure rcb_Procedure;    
    QuantitativeDataVariable allData;
    QuantitativeDataVariable qdv_Responses;
    CategoricalDataVariable blockCatVar, treatCatVar, factor_AB_CatDataVar;
    ANOVA2_PrelimANOVA1 prelimBlocks, prelimTreats, prelimAB;
       
    public RCB_Model( Data_Manager dm,
                        ANOVA2_Procedure rcbPlatform,
                            CategoricalDataVariable blockValues, 
                            CategoricalDataVariable treatValues,
                            QuantitativeDataVariable responseVar) { 
        this.dm = dm;
        this.rcb_Procedure = rcbPlatform;
        this.blockCatVar = blockValues;
        this.treatCatVar = treatValues;
        this.qdv_Responses = responseVar;
        
        stringsToPrint = new ArrayList();
        
        nDataTriples = responseVar.get_nDataPointsLegal();
        
        blocksLabel = blockValues.getDataLabel();
        treatsLabel = treatValues.getDataLabel();

        nLegalResponses = responseVar.get_nDataPointsLegal();
        
        doTwoWayANOVA();
    }   // End of constructor    
    
    
    private void doTwoWayANOVA() {
        initializeTwoWayANOVA();  
        performInitialOneWays();

        if (rcb_Procedure.getDataAreBalanced() == true) {
            System.out.println("100, ANOVA2 Model, Balanced two way ANOVA");
            
            if (rcb_Procedure.getReplicatesExist() == false) {
                System.out.println("103, ANOVA2 Model, no replicates");
                doRCBAnalysis_1();
                print_ANOVA2_Results_1();
            }
            else {
                System.out.println("108, ANOVA2 Model, replicates");
                doRCBAnalysis_n();
                print_ANOVA2_Results_n();
            }
                
        }
        else // ... there is unbalance
        {
            System.out.println("116 ANOVA2 Model, UNbalanced two way ANOVA");
            
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
        prelimBlocks = new ANOVA2_PrelimANOVA1(dm, blockCatVar, qdv_Responses);
        prelimBlocks.doOneWayANOVA();       
        nBlocks = prelimBlocks.getNLevels();
        blockLevels = prelimBlocks.getCategoryLabels();
         
        prelimTreats = new ANOVA2_PrelimANOVA1(dm, treatCatVar, qdv_Responses);
        prelimTreats.doOneWayANOVA();
        nTreats = prelimTreats.getNLevels();       
        treatLevels = prelimTreats.getCategoryLabels();
        
        nDataTriples = nLegalResponses;
        factor_AB_CatDataVar = new CategoricalDataVariable ("Interactions", nDataTriples);


        // Create complete list of interactions so that empty cells can be detected. 
        // Also check for uniqueness -- Collection / count?
        System.out.println("152 RCB_Model, nLegalResponses = " + nLegalResponses);
        String[] listOfInteractions = new String[nLegalResponses];
        
    }   //  end performOneWays
    
    //  Randomized Block, no replicates
    private void doRCBAnalysis_1() {  
        ssBlocks = prelimBlocks.getSSTreatments();
        dfBlocks = prelimBlocks.getDFLevels();
        msBlocks = ssBlocks / dfBlocks;

        ssTreats = prelimTreats.getSSTreatments();
        dfTreats = prelimTreats.getDFLevels(); 
        msTreats = ssTreats / dfTreats;

        ssTotal = qdv_Responses.getTheSS();
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

        fStatRows = msBlocks / msError;
        FDistribution fDistRows = new FDistribution( dfBlocks, dfError);
        pValRows = fDistRows.getRightTailArea(fStatRows);
        
        fStatColumns = msTreats / msError;
        FDistribution fDistColumns = new FDistribution( dfTreats, dfError);
        pValColumns = fDistColumns.getRightTailArea(fStatColumns);

    }  // end doTwoWayAnalysis
    
        //  Randomized Block, n replicates
    private void doRCBAnalysis_n() {  
        System.out.println("278 RCB_Model doRCBAnalysis_n()");
        nReplications = rcb_Procedure.getNReplications();
        String interactionABString;
        factor_AB_CatDataVar = new CategoricalDataVariable ("Interactions", nDataTriples);
        for (int ithDataPoint = 0; ithDataPoint < nLegalResponses; ithDataPoint++) {
            String dataA = blockCatVar.getIthDataPtAsString(ithDataPoint);
            String dataB = treatCatVar.getIthDataPtAsString(ithDataPoint);
            interactionABString = dataA + "&" + dataB;
            factor_AB_CatDataVar.setIthDataPtAsString(ithDataPoint, interactionABString);
        }   //  next ithDataPoint         
        
        prelimAB = new ANOVA2_PrelimANOVA1(dm, factor_AB_CatDataVar, qdv_Responses);
        prelimAB.doOneWayANOVA();
        
        ssBlocks = prelimBlocks.getSSTreatments();
        dfBlocks = prelimBlocks.getDFLevels();
        msBlocks = ssBlocks / dfBlocks;

        ssTreats = prelimTreats.getSSTreatments();
        dfTreats = prelimTreats.getDFLevels(); 
        msTreats = ssTreats / dfTreats;
        
        ssCells = prelimAB.getSSTreatments();
        dfCells = prelimAB.getDFError();
        
        ssInteraction = ssCells - ssBlocks - ssTreats;
        dfInteraction = dfBlocks * dfTreats;
        msInteraction = ssInteraction / dfInteraction;

        ssTotal = qdv_Responses.getTheSS();
        dfTotal = nLegalResponses - 1;

        ssError = ssTotal - ssTreats - ssBlocks - ssInteraction;
        dfError = nBlocks * nTreats * (nReplications - 1);
        msError = ssError / dfError;
        
        double rSquare = (ssTotal - ssError) / ssTotal;

        double p = dfTreats + dfBlocks + dfInteraction;
        double adjRSquare = 1.0 - (1 - rSquare) * (nLegalResponses - 1.) / (nLegalResponses - p - 1);
        double stErrPred = Math.sqrt(msError);
        
        //System.out.println("235 RCB_Model, rSquare = " + rSquare);
        //System.out.println("236 RCB_Model, adjRSquare = " + adjRSquare);
        //System.out.println("237 RCB_Model, stErrPred = " + stErrPred);

        fStatRows = msBlocks / msError;
        FDistribution fDistRows = new FDistribution( dfBlocks, dfError);
        pValRows = fDistRows.getRightTailArea(fStatRows);
        
        fStatColumns = msTreats / msError;
        FDistribution fDistColumns = new FDistribution( dfTreats, dfError);
        pValColumns = fDistColumns.getRightTailArea(fStatColumns);
        
        fStatInteraction = msInteraction / msError;
        FDistribution fDistInteraction = new FDistribution( dfInteraction, dfError);
        pValInteraction = fDistInteraction.getRightTailArea(fStatInteraction); 

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

        System.out.printf("%10s      %4d     %13.3f        %8.3f     %8.3f     %6.4f\n", sourceString, dfTreats, ssTreats,
                                                                                          msTreats,fStatColumns, pValColumns);
        
        sourceString = leftMostChars(blocksLabel, 12);
        System.out.printf("%10s      %4d     %13.3f        %8.3f     %8.3f     %6.4f\n", sourceString, dfBlocks, ssBlocks,
                                                                                          msBlocks, fStatRows, pValRows);
        
        sourceString = leftMostChars("Error", 12);
        System.out.printf("%10s      %4d     %13.3f        %8.3f\n", sourceString, dfError,ssError, msError);
        stringsToPrint.add(String.format("%10s      %4d     %13.3f        %8.3f\n", sourceString, dfError, ssError, msError));
        
        sourceString = leftMostChars("Total", 12);
        System.out.printf("%10s      %4d     %13.3f\n", sourceString, dfTotal, ssTotal);
        stringsToPrint.add(String.format("%10s      %4d     %13.3f\n", sourceString, dfTotal, ssTotal));
        
        System.out.printf("-----------------------------------------------------------------------------------\n"); 
        stringsToPrint.add(String.format("-----------------------------------------------------------------------------------\n"));
   }    // end printANOVA_Results
    
    
    private void print_ANOVA2_Results_n() {  
        stringsToPrint = new ArrayList<>();    
        System.out.printf("-----------------------------------------------------------------------------------\n");
        stringsToPrint.add(String.format("-----------------------------------------------------------------------------------\n"));
        System.out.printf("Source of       Degrees of       Sum of\n");
        stringsToPrint.add(String.format("Source of     Degrees of       Sum of\n"));
        System.out.printf("Variation        Freedom         Squares        Mean Square       F       P-value\n");
        stringsToPrint.add(String.format("Variation        Freedom         Squares        Mean Square       F       P-value\n"));
        System.out.printf("-----------------------------------------------------------------------------------\n");
        stringsToPrint.add(String.format("-----------------------------------------------------------------------------------\n"));
        
        sourceString = leftMostChars(treatsLabel, 16);

        System.out.printf("%16s  %4d     %13.3f        %8.3f     %8.3f     %6.4f\n", sourceString, dfTreats, ssTreats,
                                                                                          msTreats,fStatColumns, pValColumns);
        
        sourceString = leftMostChars(blocksLabel, 16);
        System.out.printf("%16s  %4d     %13.3f        %8.3f     %8.3f     %6.4f\n", sourceString, dfBlocks, ssBlocks,
                                                                                          msBlocks, fStatRows, pValRows);
        
        // sourceString = getLeftMostChars("Interaction", 12);
        
        sourceString = leftMostChars(treatsLabel, 8) + "*" + leftMostChars(blocksLabel, 8);
        System.out.printf("%16s  %4d     %13.3f        %8.3f     %8.3f     %6.4f\n", sourceString, dfInteraction, ssInteraction,
                                                                                          msInteraction, fStatInteraction, pValInteraction);        

        sourceString = leftMostChars("Error", 16);
        System.out.printf("%16s  %4d     %13.3f        %8.3f\n", sourceString, dfError,ssError, msError);
        stringsToPrint.add(String.format("%10s      %4d     %13.3f        %8.3f\n", sourceString, dfError, ssError, msError));
        
        sourceString = leftMostChars("Total", 16);
        System.out.printf("%16s  %4d     %13.3f\n", sourceString, dfTotal, ssTotal);
        stringsToPrint.add(String.format("%10s      %4d     %13.3f\n", sourceString, dfTotal, ssTotal));
        
        System.out.printf("-----------------------------------------------------------------------------------\n"); 
        stringsToPrint.add(String.format("-----------------------------------------------------------------------------------\n"));
   }    // end printANOVA_Results
        
    public double getConfidenceLevel() { return confLevel; }
    
    public void setConfidenceLevel( double atThisLevel) {
        confLevel = atThisLevel;
    }
    
   //  Print the leftmost leftChars of original
    private String leftMostChars(String original, int leftChars) {
        return StringUtilities.truncateString(original, leftChars);
    }
   
    public String getFactorALabel() {return blockCatVar.getDataLabel();}
    public String getFactorBLabel() {return treatCatVar.getDataLabel();}
    public String getResponseLabel() { return qdv_Responses.getTheDataLabel(); }

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
