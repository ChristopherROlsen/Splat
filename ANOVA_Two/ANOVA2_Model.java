/**************************************************
 *                 ANOVA2_Model                   *
 *                  12/22/18                      *
 *                    12:00                       *
 *************************************************/

package ANOVA_Two;

import genericClasses.StringUtilities;
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

public class ANOVA2_Model {
    
    boolean lowEndIsFixed, highEndYIsFixed, tempEmptyCellExists,
            emptyCellExists, thereIsUnbalance;
    
    int nDataTriples, nFactorA, nFactorB, nResponse, nLevels, dfRows, dfColumns,
        nFactorA_Levels, nFactorB_Levels, dfInteraction, dfCells, dfTotal, 
        dfError, totalN;
    
    double  msRows, msColumns,  confLevel,
            msInteraction, msError, fStat_Rows, fStat_Cols, fStat_Interaction,
            pValRows, pValColumns, pValInteraction, ssRows, ssColumns, ssSubgr,
            ssWithin, ssCells, ssInteraction, ssError, ssTotal;
    
    String  str_FactorA, str_FactorB, str_ResponseVar, sourceString, 
            displayChoice, titleString, meansOrBars, sourceStringRows, 
            sourceStringCols, sourceStringInteraction, sourceStringError, 
            sourceStringTotal;
    
    ArrayList<String> stringsToPrint;  // Are these
    
    // These are names for the levels
    ObservableList<String> str_ALevels, str_BLevels;//, factorAB_Levels;

    // My classes
    ArrayList<ANOVA_Level> al_LevelsOfA, al_LevelsOfB, al_LevelsOfAB;   
    ANOVA2_Procedure anova2_Procedure; 
    ANOVA2_UnbalancedModel tWAUP;  
    CategoricalDataVariable cat_DV_A, cat_DV_B, cat_DV_AB;
    ArrayList<CatQuantPair> allTheData;
    ANOVA2_PrelimANOVA1 prelimA, prelimB, prelimAB;
    QuantitativeDataVariable allData;
    QuantitativeDataVariable qdv_Responses;
    StudentizedRangeQ studRangeQ;
    Splat_DataManager dm;
    
    public ANOVA2_Model(Splat_DataManager dm,
                        ANOVA2_Procedure anova2_Platform,
                        CategoricalDataVariable catDV_FactorA, 
                        CategoricalDataVariable catDV_FactorB,
                        QuantitativeDataVariable quantDV_Responses) {   
        this.anova2_Procedure = anova2_Platform;
        this.cat_DV_A = catDV_FactorA;
        this.cat_DV_B = catDV_FactorB;
        this.qdv_Responses = quantDV_Responses;
        
        stringsToPrint = new ArrayList();
        
        nDataTriples = quantDV_Responses.get_nDataPointsLegal();
        
        str_FactorA = catDV_FactorA.getDataLabel();
        str_FactorB = catDV_FactorB.getDataLabel();

        nResponse = quantDV_Responses.get_nDataPointsLegal();
        
        doTwoWayANOVA();
    }   // End of constructor    
    
    
    private void doTwoWayANOVA() {
        initializeTwoWayANOVA();  
        performInitialOneWays();

        if (anova2_Procedure.getDataAreBalanced() == true) {
            //System.out.println("83, ANOVA2 Model, Balanced two way ANOVA");
            doTwoWayAnalysis();
            print_ANOVA2_Results();
        }
        else // ... there is unbalance
        {
            //System.out.println("89 ANOVA2 Model, UNbalanced two way ANOVA");
            tWAUP = new ANOVA2_UnbalancedModel(this,
                                                    cat_DV_A,
                                                    cat_DV_B,
                                                    qdv_Responses);
            doTwoWayAnalysis();
            print_ANOVA2_Results();
        }
    }

    private void initializeTwoWayANOVA() {
        confLevel = 0.95;
        lowEndIsFixed = false;
        highEndYIsFixed = false;
        nLevels = 0;  //  Number of listOfLevels = as determined below
        str_ALevels = FXCollections.observableArrayList();
        str_BLevels = FXCollections.observableArrayList();
        allTheData = new ArrayList<>();
        titleString = "Holy Shapsagen";
    } 

    private void performInitialOneWays() {
        prelimA = new ANOVA2_PrelimANOVA1(dm, cat_DV_A, qdv_Responses);
        prelimA.doOneWayANOVA();       
        nFactorA_Levels = prelimA.getNLevels();
        str_ALevels = prelimA.getCategoryLabels();
        prelimB = new ANOVA2_PrelimANOVA1(dm, cat_DV_B, qdv_Responses);
        prelimB.doOneWayANOVA();
        nFactorB_Levels = prelimB.getNLevels();       
        str_BLevels = prelimB.getCategoryLabels();
        nDataTriples = nResponse;
        cat_DV_AB = new CategoricalDataVariable ("Interactions", nDataTriples);
        String interactionABString;
        
        for (int ithDataPoint = 0; ithDataPoint < nResponse; ithDataPoint++) {
            String dataA = cat_DV_A.getIthDataPtAsString(ithDataPoint);
            String dataB = cat_DV_B.getIthDataPtAsString(ithDataPoint);
            interactionABString = dataA + "&" + dataB;
            cat_DV_AB.setIthDataPtAsString(ithDataPoint, interactionABString);
        }   //  next ithDataPoint 

        prelimAB = new ANOVA2_PrelimANOVA1(dm, cat_DV_AB, qdv_Responses);
        prelimAB.doOneWayANOVA();
        
    }   //  end performOneWays
    
    private void doTwoWayAnalysis() {  
        if (anova2_Procedure.getDataAreBalanced() == true) { 
            ssRows = prelimA.getSSTreatments();
            dfRows = prelimA.getDFLevels();
            msRows = ssRows / dfRows;

            ssColumns = prelimB.getSSTreatments();
            dfColumns = prelimB.getDFLevels(); 
            msColumns = ssColumns / dfColumns;

            ssCells = prelimAB.getSSTreatments();   // SS 'explained'
            dfCells = prelimAB.getDFLevels();       // df 'explained'  

            ssError = prelimAB.getSSError();
            dfError = prelimAB.getDFError();
            msError = ssError / dfError;

            ssTotal = prelimAB.getSSTotal();
            dfTotal = prelimAB.getDFTotal(); 

            ssInteraction = ssCells - ssRows - ssColumns;
            dfInteraction = dfRows * dfColumns;
            msInteraction = ssInteraction / dfInteraction;      
        }
        else    // Get info from UnbalancedPlatform
        {
            sourceStringRows = tWAUP.getSourceStringFactorA();
            ssRows = tWAUP.getSSFactorA();
            dfRows = tWAUP.getDFFactorA();
            msRows = tWAUP.getMSFactorA();

            sourceStringCols = tWAUP.getSourceStringFactorB();
            ssColumns = tWAUP.getSSFactorB();
            dfColumns = tWAUP.getDFFactorB();
            msColumns = tWAUP.getMSFactorB();  
            
            sourceStringInteraction = tWAUP.getSourceStringInteraction();
            ssInteraction = tWAUP.getSSInteraction();
            dfInteraction = tWAUP.getDFInteraction();
            msInteraction = tWAUP.getMSInteraction(); 
            
            sourceStringError = tWAUP.getSourceStringError();
            ssError = tWAUP.getSSError();
            dfError = tWAUP.getDFError();
            msError = tWAUP.getMSError(); 
            
            sourceStringTotal = tWAUP.getSourceStringTotal();
            ssTotal = tWAUP.getSSTotal();
            dfTotal = tWAUP.getDFTotal();
        }

        fStat_Rows = msRows / msError;
        FDistribution fDist_Rows = new FDistribution( dfRows, dfError);
        pValRows = fDist_Rows.getRightTailArea(fStat_Rows);
        
        fStat_Cols = msColumns / msError;
        FDistribution fDist_Columns = new FDistribution( dfColumns, dfError);
        pValColumns = fDist_Columns.getRightTailArea(fStat_Cols);
        
        fStat_Interaction = msInteraction / msError;
        FDistribution fDist_Interaction = new FDistribution( dfInteraction, dfError);
        pValInteraction = fDist_Interaction.getRightTailArea(fStat_Interaction); 
    }  // end doTwoWayAnalysis
    
    private void print_ANOVA2_Results() {  
        stringsToPrint = new ArrayList<>();    
        System.out.printf("-----------------------------------------------------------------------------------\n");
        stringsToPrint.add(String.format("-----------------------------------------------------------------------------------\n"));
        System.out.printf("Source of      Degrees of       Sum of\n");
        stringsToPrint.add(String.format("Source of      Degrees of       Sum of\n"));
        System.out.printf("Variation       Freedom        Squares         Mean Square       F       P-value\n");
        stringsToPrint.add(String.format("Variation       Freedom        Squares         Mean Square       F       P-value\n"));
        System.out.printf("-----------------------------------------------------------------------------------\n");
        stringsToPrint.add(String.format("-----------------------------------------------------------------------------------\n"));
        sourceString = getLeftMostChars(str_FactorA, 12);
        System.out.printf("%10s      %4d     %13.3f        %8.3f     %8.3f     %6.4f\n", sourceString, dfRows, ssRows,
                                                                                          msRows, fStat_Rows, pValRows);
        
        sourceString = getLeftMostChars(str_FactorB, 12);

        System.out.printf("%10s      %4d     %13.3f        %8.3f     %8.3f     %6.4f\n", sourceString, dfColumns, ssColumns,
                                                                                          msColumns,fStat_Cols, pValColumns);

        sourceString = getLeftMostChars(str_FactorA, 6) + "*" + getLeftMostChars(str_FactorB, 6);
        System.out.printf("%10s      %4d     %13.3f        %8.3f     %8.3f     %6.4f\n", sourceString, dfInteraction, ssInteraction,
                                                                                         msInteraction,fStat_Interaction, pValInteraction); 

        sourceString = getLeftMostChars("Error", 12);
        System.out.printf("%10s      %4d     %13.3f        %8.3f\n", sourceString, dfError,ssError, msError);
        stringsToPrint.add(String.format("%10s      %4d     %13.3f        %8.3f\n", sourceString, dfError, ssError, msError));
        
        sourceString = getLeftMostChars("Total", 12);
        System.out.printf("%10s      %4d     %13.3f\n", sourceString, dfTotal, ssTotal);
        stringsToPrint.add(String.format("%10s      %4d     %13.3f\n", sourceString, dfTotal, ssTotal));
        
        System.out.printf("-----------------------------------------------------------------------------------\n"); 
        stringsToPrint.add(String.format("-----------------------------------------------------------------------------------\n"));
   }    // end printANOVA_Results
        
    public double getConfidenceLevel() { return confLevel; }
    
    public void setConfidenceLevel( double atThisLevel) {
        confLevel = atThisLevel;
    }
    
   private String getLeftMostChars(String original, int leftChars) {
       return StringUtilities.truncateString(original, leftChars);
   }
   
     public String getFactorALabel() {return cat_DV_A.getDataLabel();}
     public String getFactorBLabel() {return cat_DV_B.getDataLabel();}
     public String getResponseLabel() { return qdv_Responses.getTheDataLabel(); }

     public int getNFactorA_Levels() {  return nFactorA_Levels; }
     public int getNFactorB_Levels() {  return nFactorB_Levels; }

     public String getChoiceOfPlot() { return displayChoice; }
     public String getMeansOrBars() { return meansOrBars; }

     public ObservableList <String> getCategoryLevels() { 
         return str_ALevels; 
     }     
     
     public ObservableList <String> getFactorALevels() { 
         return str_ALevels; 
     }
     
     public ObservableList <String> getFactorBLevels() { 
         return str_BLevels; 
     } 

     public double getMinVertical() {return prelimA.getMinVertical(); }
     public double getMaxVertical() {return prelimA.getMaxVertical(); }  
     
     public UnivariateContinDataObj  getAllDataUCDO() {
         UnivariateContinDataObj dummyUCDO = new UnivariateContinDataObj();
         return dummyUCDO;
         // return responseVar.getUCDO(); 
     }

     public ANOVA2_PrelimANOVA1 getPrelimA() { return prelimA; }
     public ANOVA2_PrelimANOVA1 getPrelimB() { return prelimB; }
     public ANOVA2_PrelimANOVA1 getPrelimAB() { return prelimAB; }
     
     public ArrayList<String> getANOVA2Report() {
          return stringsToPrint;
     }

     public int get_nDP() {return nDataTriples; }
}
