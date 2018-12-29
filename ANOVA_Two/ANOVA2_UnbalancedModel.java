/**************************************************
 *             ANOVA2_UnbalancedModel             *
 *                    05/15/18                    *
 *                     12:00                      *
 *************************************************/
package ANOVA_Two;

import utilityClasses.StringUtilities;
import dataObjects.CategoricalDataVariable;
import dataObjects.QuantitativeDataVariable;
import probabilityDistributions.FDistribution;
import matrixProcedures.Matrix;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ANOVA2_UnbalancedModel {

    Matrix XFull, XAlphaBeta, XAlphaGamma, XBetaGamma, XAlpha, XBeta, Y; 
    
    String sourceString, factorAVar, factorBVar, yLabel;
    
    String[] fullModelLabels, alphaBetaModelLabels, alphaGammaModelLabels, 
             betaGammaModelLabels, alphaModelLabels, betaModelLabels, 
             levelsA, levelsB, rowRegressors, colRegressors, interactionRegressors;
    
    int nDP, nTotalCols,nAlphaGammaRegressors,
        nRegressorsA, nRegressorsB, nLevelsA, nLevelsB, nBetaGammaRegressors,
        nInteractionRegressors, nAlphaBetaRegressors, nTotalRegressors,
        dfFactorA, dfFactorB, dfInteraction, dfTotal, dfResiduals, totalN;
    
    int[][] codedRowRegressor, codedColRegressor;
    
    int interactionRegressorValues[];

    double  msFactorA, msFactorB, confLevel, msInteraction, msResiduals, 
            fStatFactorA,  fStatFactorB, fStatInteraction, pValueFactorA, 
            pValueFactorB, pValueInteraction, ssFactorA, ssFactorB, ssResiduals, 
            ssInteraction, ssTotal;
    
    ObservableList<String> factorA_Levels, factorB_Levels, factorAB_Levels;
    ArrayList<String> stringsToPrint;

    public ANOVA2_UnbalancedRegression fullModel, alphaBetaModel, 
                                   alphaGammaModel, betaGammaModel,
                                   alphaModel, betaModel;
    
    ANOVA2_Model twoWayANOVAModel;
    CategoricalDataVariable factor_A_Values;    //  The factor A data
    CategoricalDataVariable factor_B_Values;    //  The factor B data
    QuantitativeDataVariable responseVar;            //  The response data
     
    public ANOVA2_UnbalancedModel(ANOVA2_Model twoWayANOVAModel,
                                         CategoricalDataVariable factor_A_Values,
                                         CategoricalDataVariable factor_B_Values,
                                         QuantitativeDataVariable responseVar) { 
        
        nDP = twoWayANOVAModel.get_nDP();
        for (int ithDP = 0; ithDP < nDP; ithDP++) {
            String facA = factor_A_Values.getIthDataPtAsString(ithDP);
            String facB = factor_B_Values.getIthDataPtAsString(ithDP);
            String resp = responseVar.getIthDataPtAsString(ithDP);
        }  
        factorA_Levels = FXCollections.observableArrayList();
        factorB_Levels = FXCollections.observableArrayList();   
        factorAB_Levels = FXCollections.observableArrayList();  

        factorA_Levels = twoWayANOVAModel.getFactorALevels();
        factorB_Levels = twoWayANOVAModel.getFactorBLevels();

        this.twoWayANOVAModel = twoWayANOVAModel;
        this.factor_A_Values = factor_A_Values;
        this.factor_B_Values = factor_B_Values;
        this.responseVar = responseVar;


        // The following is a clunky conversion from an ObservableList to an
        // array of strings.  
        // Question: why is the list of strings observable?
        // ------->
        nLevelsA = factorA_Levels.size() - 1;
        nLevelsB = factorB_Levels.size() - 1;

        levelsA = new String[nLevelsA];
        levelsB = new String[nLevelsB];
        
        //  The first Levels in FactorA_Levels, factorB_Levels are 'all'
        for (int ithLevelsA = 0; ithLevelsA < nLevelsA; ithLevelsA++) {
            levelsA[ithLevelsA] = factorA_Levels.get(ithLevelsA + 1);
        }

        for (int ithLevelsB = 0; ithLevelsB < nLevelsB; ithLevelsB++) {
            levelsB[ithLevelsB] = factorB_Levels.get(ithLevelsB + 1);
        }
        // <-------

        doLevelsAndRegressors();       
        doBrutishCalculations();             
    }

    private void doLevelsAndRegressors() {
    
        nRegressorsA = nLevelsA - 1;
        nRegressorsB = nLevelsB - 1;
        nAlphaBetaRegressors = nRegressorsA + nRegressorsB;  

        nInteractionRegressors = nRegressorsA * nRegressorsB;
        nAlphaGammaRegressors = nRegressorsA + nInteractionRegressors;
        nBetaGammaRegressors = nRegressorsB + nInteractionRegressors;
        nTotalRegressors = nRegressorsA + nRegressorsB + nInteractionRegressors;
        
        fullModelLabels = new String[nTotalRegressors + 1];
        alphaBetaModelLabels = new String[nAlphaBetaRegressors + 1];
        alphaGammaModelLabels = new String[nAlphaGammaRegressors + 1];
        betaGammaModelLabels = new String[nBetaGammaRegressors + 1];
        alphaModelLabels = new String[nRegressorsA + 1];
        betaModelLabels = new String[nRegressorsB + 1]; 
        
        codedRowRegressor = new int[nDP][nRegressorsA];
        codedColRegressor = new int[nDP][nRegressorsB];
        rowRegressors = new String[nRegressorsA]; 
        colRegressors = new String[nRegressorsB];       
        
        /**********************************************************************
         *                     Full model labels                              *
         *********************************************************************/
        
        for (int ithRegressor = 0; ithRegressor < nRegressorsA; ithRegressor++) {
            rowRegressors[ithRegressor] = "alpha" + (ithRegressor + 1);
            //System.out.println("146 unb2, ithRegressorA / rowRegressors[ithRegressor] = " + ithRegressor + " / " + rowRegressors[ithRegressor]);
            fullModelLabels[ithRegressor] = rowRegressors[ithRegressor];
        }
        
        for (int ithRegressor = 0; ithRegressor < nRegressorsB; ithRegressor++) {
            colRegressors[ithRegressor] = "beta" + (ithRegressor + 1);
            //System.out.println("152 unb2, ithRegressorB / colRegressors[ithRegressor] = " + ithRegressor + " / " + colRegressors[ithRegressor]);
            fullModelLabels[ithRegressor + nRegressorsA] = colRegressors[ithRegressor];
        }
        
        for (int ithRegressor = 0; ithRegressor < nInteractionRegressors; ithRegressor++) {
            fullModelLabels[nAlphaBetaRegressors + ithRegressor] = "Interaction?";
        } 
        
        /**********************************************************************
         *                     Alpha model labels                              *
         *********************************************************************/        
        for (int ithRegressor = 0; ithRegressor < nRegressorsA; ithRegressor++) {
            alphaModelLabels[ithRegressor] = fullModelLabels[ithRegressor];
        }        
        
        /**********************************************************************
         *                     Beta model labels                              *
         *********************************************************************/        
        for (int ithRegressor = 0; ithRegressor < nRegressorsB; ithRegressor++) {
            betaModelLabels[ithRegressor] = fullModelLabels[nRegressorsA + ithRegressor];
        }         
        
        /**********************************************************************
         *             No interaction (alpha beta) labels                     *
         *********************************************************************/        
        for (int ithRegressor = 0; ithRegressor < nRegressorsA + nRegressorsB; ithRegressor++) {
            alphaBetaModelLabels[ithRegressor] = fullModelLabels[nRegressorsA + nRegressorsB +ithRegressor];
        }
        
               
        /**********************************************************************
         *                      AlphaGamma labels                             *
         *********************************************************************/        
        for (int ithRegressor = 0; ithRegressor < nRegressorsA; ithRegressor++) {
            alphaGammaModelLabels[ithRegressor] = fullModelLabels[ithRegressor];
        } 
        
        for (int ithRegressor = 0; ithRegressor < nRegressorsA; ithRegressor++) {
            alphaGammaModelLabels[ithRegressor] = fullModelLabels[nRegressorsA + nRegressorsB +ithRegressor];
        }         
        
        
        /**********************************************************************
         *                      BetaGamma labels                             *
         *********************************************************************/        
        for (int ithRegressor = 0; ithRegressor < nRegressorsB; ithRegressor++) {
            betaGammaModelLabels[ithRegressor] = fullModelLabels[nRegressorsA + ithRegressor];
        } 
        
        for (int ithRegressor = 0; ithRegressor < nRegressorsA; ithRegressor++) {
            alphaGammaModelLabels[ithRegressor] = fullModelLabels[nRegressorsA + nRegressorsB +ithRegressor];
        }  

        
        yLabel = responseVar.getDataLabel();
        
        fullModelLabels[nTotalRegressors] = yLabel;
        alphaBetaModelLabels[nRegressorsA + nRegressorsB] = yLabel;
        
        
        nTotalCols = nRegressorsA + nRegressorsB + nInteractionRegressors; 
    }
    
    private void doBrutishCalculations() {
        String lookingForThisLevelA, lookingForThisLevelB;
        // Matrix for full model -- other models steal from this matrix
        XFull = new Matrix(nDP, nTotalCols, 0.); //  Matrix of zeros
        Y = new Matrix(nDP, 1);

        int firstBetaColumn = nRegressorsA;
        int firstInteractionColumn = nRegressorsA + nRegressorsB; 

        for (int ithDP = 0; ithDP < nDP; ithDP++) {
            String facA = factor_A_Values.getIthDataPtAsString(ithDP);
            String facB = factor_B_Values.getIthDataPtAsString(ithDP);
            String resp = responseVar.getIthDataPtAsString(ithDP);
            //System.out.println("\n\n187 Unb2, *************ithDP/facA/facB/resp = " + ithDP + " / " + facA + " / " + facB + " / " + resp);
            
            Y.set(ithDP, 0, Double.parseDouble(responseVar.getIthDataPtAsString(ithDP)));
   
            interactionRegressorValues = new int[nInteractionRegressors];
            String dataLevelAThisDP = factor_A_Values.getIthDataPtAsString(ithDP);
            String dataLevelBThisDP = factor_B_Values.getIthDataPtAsString(ithDP);
            
            for (int athRegressor = 0; athRegressor < nRegressorsA; athRegressor++) {
                lookingForThisLevelA = levelsA[athRegressor]; //  This is the one I'm looking for
                codedRowRegressor[ithDP][athRegressor] = 0; //  default
                if (nRegressorsA == 1) {
                    lookingForThisLevelA = levelsA[0];
                    if (lookingForThisLevelA.equals(dataLevelAThisDP)) {
                        codedRowRegressor[ithDP][athRegressor] = 1;
                    }
                    else {
                        codedRowRegressor[ithDP][athRegressor] = -1;
                    }
                } else {    //  nRegressorsA > 1
                    if (dataLevelAThisDP.equals(levelsA[athRegressor])) {
                        codedRowRegressor[ithDP][athRegressor] = 1;
                    } else {
                        //System.out.println("229 unb2, A not equal!");
                    }
                    if (dataLevelAThisDP.equals(levelsA[nLevelsA - 1])) {
                        codedRowRegressor[ithDP][athRegressor] = -1;
                    }  
                    
                }   //  end nRegressorsA > 1
            }
            
            for (int bthRegressor = 0; bthRegressor < nRegressorsB; bthRegressor++) {
                lookingForThisLevelB = levelsB[bthRegressor]; //  This is the one I'm looking for
                codedColRegressor[ithDP][bthRegressor] = 0; //  default
                if (nRegressorsB == 1) {
                    lookingForThisLevelB = levelsB[0];
                    if (dataLevelBThisDP.equals(levelsB[bthRegressor])) {
                        codedColRegressor[ithDP][bthRegressor] = 1;
                    }
                    else {
                        codedColRegressor[ithDP][bthRegressor] = -1;
                        //System.out.println("253 unb2, B not equal!");
                    }
                } else {    //  nRegressorsB > 1
                    if (dataLevelBThisDP.equals(levelsB[bthRegressor])) {
                        codedColRegressor[ithDP][bthRegressor] = 1;
                    }
                    else {
                        //System.out.println("262 unb2, B not equal!");
                    }
                    if (dataLevelBThisDP.equals(levelsB[nLevelsB - 1])) {
                        codedColRegressor[ithDP][bthRegressor] = -1;
                    }  
                    
                }   //  end nRegressorsB > 1
            }
 
            // Fill the Row Regressors and Column Regressors in the Design Matrix            
            
            for (int ithARegressor = 0; ithARegressor < nRegressorsA; ithARegressor++) {
                XFull.set(ithDP, ithARegressor, codedRowRegressor[ithDP][ithARegressor]);
            }

            for (int ithBRegressor = 0; ithBRegressor < nRegressorsB; ithBRegressor++) {
                XFull.set(ithDP, nRegressorsA + ithBRegressor, codedColRegressor[ithDP][ithBRegressor]);
            }
            
            
            for (int ithRegA = 0; ithRegA < nRegressorsA; ithRegA++) {
                for (int ithRegB = 0; ithRegB < nRegressorsB; ithRegB++) {
                    int ithRegAB = nAlphaBetaRegressors + nRegressorsB * ithRegA + ithRegB;
                    int codedRegressorAB = codedRowRegressor[ithDP][ithRegA] * codedColRegressor[ithDP][ithRegB];               
                    XFull.set(ithDP, ithRegAB, codedRegressorAB);
                }
            }

        }   //  end ithDP

        fullModel = new ANOVA2_UnbalancedRegression(XFull, Y, fullModelLabels);
        fullModel.doRegressionAnalysis();
        ssTotal = fullModel.getSSTotal();
        dfTotal = nDP - 1;

        double SSRegFullModel = fullModel.getSSRegression();
       
        XAlphaBeta = new Matrix(nDP, nAlphaBetaRegressors);
        for (int ithRow = 0; ithRow < nDP; ithRow++) {
            for (int ithAlpha = 0; ithAlpha < nRegressorsA; ithAlpha++) {
                XAlphaBeta.set(ithRow, ithAlpha, XFull.get(ithRow, ithAlpha));
            }
            for (int jthBeta = 0; jthBeta < nRegressorsB; jthBeta++) {
                XAlphaBeta.set(ithRow, nRegressorsA + jthBeta, XFull.get(ithRow, nRegressorsA + jthBeta));
            }
        }
        
        alphaBetaModel = new ANOVA2_UnbalancedRegression(XAlphaBeta, Y, alphaBetaModelLabels);
        alphaBetaModel.doRegressionAnalysis();
        double SSRegAlphaBetaModel = alphaBetaModel.getSSRegression();

        // --------------------------------------------------------------------
        XAlphaGamma = new Matrix(nDP, nAlphaGammaRegressors);
        for (int ithRow = 0; ithRow < nDP; ithRow++) {
            for (int ithAlpha = 0; ithAlpha < nRegressorsA; ithAlpha++) {
                XAlphaGamma.set(ithRow, ithAlpha, XFull.get(ithRow, ithAlpha));
            }
            for (int kthInteraction = 0; kthInteraction < nInteractionRegressors; kthInteraction++) {
                XAlphaGamma.set(ithRow, nRegressorsA + kthInteraction, XFull.get(ithRow, nRegressorsA + nRegressorsB + kthInteraction));
            }
        }    

        alphaGammaModel = new ANOVA2_UnbalancedRegression(XAlphaGamma, Y, alphaGammaModelLabels);
        alphaGammaModel.doRegressionAnalysis();
        double SSRegAlphaGammaModel = alphaGammaModel.getSSRegression();

        // --------------------------------------------------------------------
        XBetaGamma = new Matrix(nDP, nBetaGammaRegressors);
        for (int ithRow = 0; ithRow < nDP; ithRow++) {
            for (int jthBeta = 0; jthBeta < nRegressorsB; jthBeta++) {
                XBetaGamma.set(ithRow, jthBeta, XFull.get(ithRow, nRegressorsA + jthBeta));
            }
            for (int kthInteraction = 0; kthInteraction < nInteractionRegressors; kthInteraction++) {
                XBetaGamma.set(ithRow, nRegressorsB + kthInteraction, XFull.get(ithRow, nRegressorsA + nRegressorsB + kthInteraction));
            }
        }   
        
        betaGammaModel = new ANOVA2_UnbalancedRegression(XBetaGamma, Y, betaGammaModelLabels);
        betaGammaModel.doRegressionAnalysis();
        double SSRegBetaGammaModel = betaGammaModel.getSSRegression();

        // -------------------------------------------------------------------
        XAlpha = new Matrix(nDP, nRegressorsA);
        for (int ithRow = 0; ithRow < nDP; ithRow++) {
            for (int ithAlpha = 0; ithAlpha < nRegressorsA; ithAlpha++) {
                XAlpha.set(ithRow, ithAlpha, XFull.get(ithRow, ithAlpha));
            }
        }  
        
        alphaModel = new ANOVA2_UnbalancedRegression(XAlpha, Y, alphaModelLabels);
        alphaModel.doRegressionAnalysis();
        double SSRegAlphaModel = alphaModel.getSSRegression();
        
        // -------------------------------------------------------------------
        XBeta = new Matrix(nDP, nRegressorsB);
        for (int ithRow = 0; ithRow < nDP; ithRow++) {
            for (int jthBeta = 0; jthBeta < nRegressorsB; jthBeta++) {
                XBeta.set(ithRow, jthBeta, XFull.get(ithRow, nRegressorsA + jthBeta));
            }
        }  
        
        betaModel = new ANOVA2_UnbalancedRegression(XBeta, Y, betaModelLabels);
        betaModel.doRegressionAnalysis();
        double SSRegBetaModel = betaModel.getSSRegression();
        
        double ssGammaGivenAlphaBeta = SSRegFullModel - SSRegAlphaBetaModel;
        double ssAlphaGivenBetaGamma = SSRegFullModel - SSRegBetaGammaModel;   
        double ssBetaGivenAlphaGamma = SSRegFullModel - SSRegAlphaGammaModel;
        double ssAlphaGivenBeta = SSRegAlphaBetaModel - SSRegBetaModel;
        double ssBetaGivenAlpha = SSRegAlphaBetaModel - SSRegAlphaModel;

        ssFactorA = ssAlphaGivenBetaGamma;
        dfFactorA = nRegressorsA;
        msFactorA = ssFactorA / dfFactorA;

        ssFactorB = ssBetaGivenAlphaGamma;
        dfFactorB = nRegressorsB;
        msFactorB = ssFactorB / dfFactorB; 

        ssInteraction = ssGammaGivenAlphaBeta;
        dfInteraction = nRegressorsA * nRegressorsB;
        msInteraction = ssInteraction / dfInteraction;  

        ssResiduals = ssTotal - SSRegFullModel;
        dfResiduals = nDP - nLevelsA * nLevelsB;
        msResiduals = ssResiduals / dfResiduals;

        fStatFactorA = msFactorA / msResiduals;
        fStatFactorB = msFactorB / msResiduals;
        fStatInteraction = msInteraction / msResiduals;
        
        FDistribution fDistFactorA = new FDistribution(dfFactorA, dfResiduals);
        pValueFactorA = fDistFactorA.getRightTailArea(fStatFactorA);
        
        FDistribution fDistFactorB = new FDistribution(dfFactorB, dfResiduals);
        pValueFactorB = fDistFactorB.getRightTailArea(fStatFactorB);
        
        FDistribution fDistInteraction = new FDistribution(dfInteraction, dfResiduals);
        pValueInteraction = fDistInteraction.getRightTailArea(fStatInteraction);     
    }
    
   private String getLeftMostChars(String original, int leftChars) {
       return StringUtilities.truncateString(original, leftChars);
   }
   
    public String getSourceStringFactorA() {return factorAVar; }
    public double getSSFactorA() {return ssFactorA; }
    public int getDFFactorA() {return dfFactorA; }
    public double getMSFactorA() { return msFactorA; }
   
    public String getSourceStringFactorB() {return factorBVar; }
    public double getSSFactorB() {return ssFactorB; }
    public int getDFFactorB() {return dfFactorB; }
    public double getMSFactorB() { return msFactorB; }
   
    public String getSourceStringInteraction() {return "Interaction"; }
    public double getSSInteraction() {return ssInteraction; }
    public int getDFInteraction() {return dfInteraction; }
    public double getMSInteraction() { return msInteraction; }
   
    public String getSourceStringError() {return "Error"; }
    public double getSSError() {return ssResiduals; }
    public int getDFError() {return dfResiduals; }
    public double getMSError() { return msResiduals; } 
   
    public String getSourceStringTotal() {return "Total"; }
    public double getSSTotal() {return ssTotal; }
    public int getDFTotal() {return dfTotal; }
}


