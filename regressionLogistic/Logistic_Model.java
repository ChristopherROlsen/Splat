/**************************************************
 *             LogisticRegressionModel            *
 *                    09/01/18                    *
 *                     06:00                      *
 *************************************************/

package regressionLogistic;

import genericClasses.QuantitativeDataVariable;
import java.util.ArrayList;
import matrixProcedures.Matrix;
import probabilityDistributions.*;

public class Logistic_Model {
    
/*******************************************************************
 *                                                                 *
 *     Note: "fleiss_" variable names are from:                    *
 *          Fleiss, J. L., Levin, B., Paik, M. C. (2003)           *
 *          Statistical Methods for Rates and Proportions (3rd).   *
 *          Wiley Inter-science.                                   *
 *                                                                 *
 *     The logistic regression algorithm is from:                  *
 *                                                                 *
 *          Montgomery, D. C., Peck, E. A, & Vining,G. G.          *
 *          Introduction to Linear Regression Analysis (4th)       *
 *          Wiley Series in Probability and Statistics             *
 *                                                                 *
 ******************************************************************/

    boolean convergenceReached;

    int nRows, nDistinct, nDataPoints;
    int fl_i, df;

    double[] dbl_xValue, propThisValue, expectedProp;
    int[] nSuccesses, nFailures, nTotal;
    
    double zBeta0, zBeta1, pBeta1, oddsRatio, ciLower, ciUpper, pValue;

    double sumOfSquaresError, pearsonChiSquare;
    double [] estPi, fleissPHatX, fleissWi, nObservations, originalProps, 
              logitProps, expectedSuccesses; 
    double beta0, beta1, seBeta0, seBeta1, covBetas;
    
    String defOfSuccess;
    
    StandardNormal standNorm;
    ChiSquareDistribution chiSqDist;
    Matrix devResids, estProbs;
    
    QuantitativeDataVariable qdv_Resids;
    
    ArrayList<String> logisticReport, logisticDiagnostics;

    Matrix mat_DataMatrix, mat_V, mat_X, mat_XTranspose, mat_Y, mat_Z, 
           mat_Beta, mat_XTr_VInv, mat_Temp1, mat_Temp2, mat_Temp3, 
           mat_OrigProps, mat_LogitProps;

    Logistic_Procedure logisticProcedure;
    Logistic_View logRegView;
    String xAxisLabel;
    String[] countTable;

    public Logistic_Model(Logistic_Procedure logisticProcedure){
        this.logisticProcedure = logisticProcedure;
        convergenceReached = false;
        nDataPoints = logisticProcedure.getNPoints();
        defOfSuccess = logisticProcedure.getDefOfSuccess();
        // Build the necessary arrays
        mat_DataMatrix = new Matrix (logisticProcedure.getDataMatrix());
        xAxisLabel = logisticProcedure.getXAxisLabel();
        nRows = mat_DataMatrix.getRowDimension();

        nObservations = new double[nRows];
        originalProps = new double[nRows];
        logitProps = new double[nRows];
        estPi = new double [nRows];

        fleissPHatX = new double[nRows];
        fleissWi = new double [nRows];

        countTable = new String[4];

        dbl_xValue = new double[nDistinct];
        propThisValue = new double[nDistinct];
        nSuccesses = new int[nDistinct];
        nFailures = new int[nDistinct];
        nTotal = new int[nDistinct];

        countTable = logisticProcedure.getCountTable();
        dbl_xValue = logisticProcedure.getXValues();
        propThisValue = logisticProcedure.getPropsOfXValues();
        nSuccesses = logisticProcedure.getNSuccesses();
        nFailures = logisticProcedure.getNFailures();
        nTotal = logisticProcedure.getNTotal();
        nDistinct = logisticProcedure.getNUniques();

        mat_OrigProps = new Matrix (nRows, 1);
        mat_LogitProps = new Matrix (nRows, 1);
        mat_Beta = new Matrix(2, 1);
        mat_V = new Matrix (nRows, nRows, 0.0);
        mat_X = new Matrix (nRows, 2);
        mat_XTranspose = new Matrix (2, nRows);
        mat_Y = new Matrix (nRows, 1);
        mat_Z = new Matrix (nRows, 1);
        mat_XTr_VInv = new Matrix (2, nRows);

        mat_Temp1 = new Matrix (2, 2);
        mat_Temp2 = new Matrix (2, 2);
        mat_Temp3 = new Matrix (2, 1);


      for (int i = 0; i<nRows; i++)
      {
         nObservations[i] = mat_DataMatrix.get(i, 2);
         mat_X.set(i, 0, 1.0);
         mat_X.set(i, 1, mat_DataMatrix.get(i, 0));

         originalProps[i] = mat_DataMatrix.get(i,1) / mat_DataMatrix.get(i,2);

         // Logit transform
         double temp_OP = originalProps[i];
         if (temp_OP == 0.0)
                 temp_OP = 0.01;
         if (temp_OP == 1.0)
                 temp_OP = 0.99;

         logitProps[i] = Math.log(temp_OP / (1 - temp_OP));

         mat_LogitProps.set(i, 0, logitProps[i]);
      }

      // Calculate initial betas
      mat_Beta = mat_X.solve(mat_LogitProps);

      beta0 = this.mat_Beta.get(0,0);
      beta1 = this.mat_Beta.get(1,0);

      for (int i = 0; i < 20; i++)
      {
         // Max out at 20 iterations...
         sumOfSquaresError = 0.0;
         iterationCycle();
         calcSSE();
      }

      double fleiss_SumWiXi = 0.0;
      double fleiss_SumWi = 0.0;

      for (fl_i = 0;fl_i < nRows; fl_i++)
      {
         double X = mat_DataMatrix.get(fl_i,0);
         double temp1 = Math.exp(beta0 + beta1 * X);
         fleissPHatX[fl_i] = temp1 / (1.0 + temp1);

         // Weighted least squares -- binomial variance.  (Not obvious in Fleiss)
         fleissWi[fl_i] = mat_DataMatrix.get(fl_i,2) * fleissPHatX[fl_i] * (1.0 - fleissPHatX[fl_i]);

         fleiss_SumWiXi += fleissWi[fl_i] * X;
         fleiss_SumWi += fleissWi[fl_i];
      }

      double fleiss_xBarW = fleiss_SumWiXi / fleiss_SumWi;
      double fleiss_SSw = 0.0;

      for (fl_i = 0;fl_i < nRows; fl_i++)
      {
         double X = mat_DataMatrix.get(fl_i,0);
         fleiss_SSw += (fleissWi[fl_i] * (X - fleiss_xBarW) * (X - fleiss_xBarW));
      }

      seBeta0 = Math.sqrt(1.0 / fleiss_SumWi + fleiss_xBarW * fleiss_xBarW / fleiss_SSw);
      seBeta1 = 1.0 / Math.sqrt(fleiss_SSw);
      covBetas = - fleiss_xBarW / fleiss_SSw;
      
      zBeta0 = beta0 / seBeta0;
      zBeta1 = beta1 / seBeta1;
      
      oddsRatio = Math.exp(beta1);
      ciLower = Math.exp(beta1 - 1.96 * seBeta1);
      ciUpper = Math.exp(beta1 + 1.96 * seBeta1);
      
      
      //  Pearson chi square
      nDistinct = logisticProcedure.getNUniques();
      expectedProp = new double[nDistinct];
      expectedSuccesses = new double[nDistinct];
      pearsonChiSquare = 0;
      for (int ithDistinct = 0; ithDistinct < nDistinct; ithDistinct++) {
          expectedProp[ithDistinct] = 1.0 / (1.0 + Math.exp(-(beta0 + beta1 * dbl_xValue[ithDistinct])));
          expectedSuccesses[ithDistinct] = nTotal[ithDistinct] * expectedProp[ithDistinct];
          double x2Num = nSuccesses[ithDistinct] - nTotal[ithDistinct] * expectedProp[ithDistinct];
          double x2Den = nTotal[ithDistinct] * expectedProp[ithDistinct] * (1.0 - expectedProp[ithDistinct]);
          pearsonChiSquare = pearsonChiSquare + x2Num * x2Num / x2Den; 
      }
      
       logisticReport = new ArrayList<>();
       logisticDiagnostics = new ArrayList<>();
      
      printStatistics();
      printDiagnostics();
      
   }  // end origOKArray constructor
    
    public void printStatistics() {
        logisticReport = new ArrayList<>();
        String expSucc = "Exp successes";
        addNBlankLinesToLogisticReport(5);
        String lrTable0 = "***************     Response information     **********\n";
        logisticReport.add(String.format("             %25s \n", lrTable0));
        logisticReport.add(String.format("%15s     %12s  %18s    %12s   %15s\n", countTable[0], countTable[1], expSucc, countTable[2], countTable[3]));
        addNBlankLinesToLogisicRegressionReport(1);
        for (int ithDistinct = 0; ithDistinct < nDistinct; ithDistinct++) {
            logisticReport.add(String.format("   %9.3f        %9d          %9.2f             %5d            %6.3f\n", dbl_xValue[ithDistinct],  
                                                                                                              nSuccesses[ithDistinct], 
                                                                                                              expectedSuccesses[ithDistinct],
                                                                                                              nTotal[ithDistinct], 
                                                                                                              propThisValue[ithDistinct]));
        }
        
        String lrTable1 = "**********     Logistic Regression Table     **********\n\n";
        logisticReport.add(String.format("\n\n          %10s", lrTable1));
        String lrTable2 = "Odds    95%     CI";
        logisticReport.add(String.format("                                                         %15s\n", lrTable2));
        String lrTable3 = "Predictor      Coef    SE Coef     Z        P     Ratio   Lower   Upper\n";
        logisticReport.add(String.format("      %30s      \n", lrTable3));
        String lrTable4 = "Constant";
        logisticReport.add(String.format("%15s  %9.3f   %6.3f   %6.3f\n", lrTable4,
                                                                        beta0,
                                                                        seBeta0,
                                                                        zBeta0));
        df = nDistinct - 2;
        standNorm = new StandardNormal();
        chiSqDist = new ChiSquareDistribution(df);
        pValue = 2.0 * standNorm.getRightTailArea(Math.abs(zBeta1));

        logisticReport.add(String.format("%15s  %9.3f   %6.3f   %6.3f    %5.3f   %5.3f   %5.3f   %5.3f\n\n\n", countTable[0], 
                                                                                                               beta1 ,  
                                                                                                               seBeta1,  
                                                                                                               zBeta1,
                                                                                                               pValue,
                                                                                                               oddsRatio,
                                                                                                               ciLower,
                                                                                                               ciUpper));

        double x2Pvalue = chiSqDist.getRightTailArea(pearsonChiSquare);
        
        String lrTable5 = "**********     Goodness-of-Fit Test     **********\n";
        logisticReport.add(String.format("       %10s", lrTable5));
        
        String lrTable6 = "Pearson Chi-sqare       DF      P\n";
        logisticReport.add(String.format("\n               %10s", lrTable6));
          
        logisticReport.add(String.format("                %9.3f             %3d   %6.3f", pearsonChiSquare,
                                                                df,  
                                                                x2Pvalue));  
        addNBlankLinesToLogisticReport(5);
        
        for (int ithLine = 0; ithLine < logisticReport.size(); ithLine++) {
            System.out.println(logisticReport.get(ithLine));
        }
    }

   public void printDiagnostics()
   {
       double ithResid, ithObsProb, ithEstProb, ithDevianceResid, ithPearsonResid;    
       
       addNBlankLinesToDiagnosticReport(2);
        logisticDiagnostics.add("                               Logistic Regression Diagnostics");
        addNBlankLinesToDiagnosticReport(1);
        logisticDiagnostics.add(String.format("-----------------------------------------------------------------------------------------------"));
        addNBlankLinesToDiagnosticReport(1);
        logisticDiagnostics.add("                                          Studentized     Studentized");
        addNBlankLinesToDiagnosticReport(1);
        logisticDiagnostics.add(String.format("                           Standardized   (Internal)      (External)"));
        addNBlankLinesToDiagnosticReport(1);
        logisticDiagnostics.add(String.format("Observation     Residual     Residual       Residual       Residual   Leverage      Cook's D"));
        addNBlankLinesToDiagnosticReport(1);
        logisticDiagnostics.add(String.format("-----------------------------------------------------------------------------------------------"));
        addNBlankLinesToDiagnosticReport(1);
       
        // double leverageWarningTrigger = 2.0 * p / n; //  p213
        
        devResids = new Matrix(nDistinct, 1); 
        estProbs = new Matrix(nDistinct, 1);
        
        for (int ith = 0; ith < nDistinct; ith++) {
            int ithIndex = ith + 1;
            double ySubi = (double)nSuccesses[ith];
            double nSubiMinusySubi = (double)nFailures[ith];
            double nSubi = (double)nTotal[ith];
            double piHatSubi = expectedProp[ith];
            double oneMinuspiHatSubi = 1.0 - piHatSubi;
            double nSubiypiHatSubi = nSubi * piHatSubi;
            double nSubiyOneMinuspiHatSubi = nSubi * (1.0 - piHatSubi);
            ithObsProb =  propThisValue[ith];
            ithEstProb = piHatSubi;
            estProbs.set(ith, 0, ithEstProb);
            ithResid = ySubi - nSubi * piHatSubi;
            double signOfResid = Math.signum(ithResid);
            double term1 = ySubi * Math.log(ySubi / (nSubiypiHatSubi));
            double term2 = nSubiMinusySubi * Math.log(nSubiMinusySubi / nSubiyOneMinuspiHatSubi);
            double preSqr = 2.0 * (term1 + term2);          
            if (ySubi == 0.) {
                ithDevianceResid = -Math.sqrt(-2.0 * nSubi * Math.log(oneMinuspiHatSubi));
            }
            else if (ySubi == nSubi) {
                ithDevianceResid = Math.sqrt(-2.0 * nSubi * Math.log(piHatSubi));
            }
            else {

                ithDevianceResid = signOfResid * Math.sqrt(preSqr);
            }
            
            devResids.set(ith, 0, ithDevianceResid);
            
            ithPearsonResid = ithResid / Math.sqrt(nSubi * piHatSubi * oneMinuspiHatSubi);
            
            logisticDiagnostics.add(String.format(" %5d         %8.3f     %8.3f       %8.3f       %8.3f", 
                             ithIndex, ithObsProb, ithEstProb, ithDevianceResid, ithPearsonResid));
            addNBlankLinesToDiagnosticReport(1);

            /*
            System.out.println("315 logMod = " + ithIndex  
                                                                                        + " / " + ithObsProb  
                                                                                        + " / " + ithEstProb
                                                                                        + " / " + ithDevianceResid
                                                                                        + " / " + ithPearsonResid);
            */
        }
        
        qdv_Resids = new QuantitativeDataVariable("Residuals", devResids);
       
        //  Print diagnostic advisories
       
        addNBlankLinesToDiagnosticReport(1);
        logisticDiagnostics.add(String.format("-----------------------------------------------------------------------------------------------"));
        addNBlankLinesToDiagnosticReport(3);
   }
   
    private void addNBlankLinesToLogisicRegressionReport(int thisMany) {
        for (int ithBlank = 0; ithBlank < thisMany; ithBlank++) {
            logisticReport.add("\n");
        }
    }
    
    private void addNBlankLinesToDiagnosticReport(int thisMany) {
        for (int ithBlank = 0; ithBlank < thisMany; ithBlank++) {
            logisticDiagnostics.add("\n");
        }
    }
    
    

   private void iterationCycle()
   {
      Matrix mat_Eta = new Matrix (nRows, 1);
      Matrix mat_VInverse = new Matrix (nRows, nRows);
      mat_Eta = mat_X.times(mat_Beta); //Step 3  MPV

      for (int i = 0; i < nRows; i++)
      {
         estPi[i] = 1.0 / (1.0 + Math.exp(- mat_Eta.get(i,0)));
         double temp1 = estPi[i] * (1.0 - estPi[i]);
         double temp2 = 1.0 / (nObservations[i] * temp1);

         mat_V.set(i,i, temp2);

         double dEta_dPi = 1.0 / temp1;
         double temp3 = mat_Eta.get(i,0) + (originalProps[i]  - estPi[i]) * dEta_dPi;
         mat_Z.set(i, 0, temp3);
      }

      mat_VInverse = mat_V.inverse();
      mat_XTranspose = mat_X.transpose();

      // X^T * V^-1
      mat_XTr_VInv = mat_XTranspose.times(mat_VInverse);

      // (X^T * V^-1 * X)
      mat_Temp1 = mat_XTr_VInv.times(mat_X);

//    (X^T * V^-1 * X)^-1
      mat_Temp2 = mat_Temp1.inverse();
      mat_Temp3 = mat_XTr_VInv.times(mat_Z);
      mat_Beta = mat_Temp2.times(mat_Temp3);

      beta0 = this.mat_Beta.get(0,0);
      beta1 = this.mat_Beta.get(1,0);
   }

   private void calcSSE()
   {
      sumOfSquaresError = 0.0;
      for (int i = 0; i < nRows; i++)
      {
         sumOfSquaresError += (originalProps[i] - estPi[i]) * (originalProps[i] - estPi[i]);
      }
   }
   
    private void addNBlankLinesToLogisticReport(int thisMany) {
        for (int ithBlank = 0; ithBlank < thisMany; ithBlank++) {
            logisticReport.add("\n");
        }
    }
  
   public String getDefOfSuccess() { return defOfSuccess; }
   public double getBeta0()  {return beta0;}
   public double getBeta1()  {return beta1;}
   public double getSEBeta0()  {return seBeta0;}
   public double getSEBeta1()  {return seBeta1;}
   public double getCovBetas() {return covBetas;}
   public Matrix getDataMatrix() {return mat_DataMatrix; }
   public String getXAxisLabel()  {return xAxisLabel;}
   public QuantitativeDataVariable getQDVResids() {return qdv_Resids;}   
   public Logistic_Procedure getLogisticProcedure() {return logisticProcedure; }
   public ArrayList<String> getLogisticReport() { return logisticReport; }
   public ArrayList<String> getDiagnostics() { return logisticDiagnostics; }
   public Matrix getDevianceResids() { return devResids; }
   public Matrix getEstimatedProbs() { return estProbs; }
   
}