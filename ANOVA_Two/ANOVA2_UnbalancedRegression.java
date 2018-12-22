/**************************************************
 *             ANOVA2_UnbalancedRegression        *
 *                    12/22/18                    *
 *                     12:00                      *
 *************************************************/
package ANOVA_Two;

import genericClasses.StringUtilities;
import probabilityDistributions.TDistribution;
import probabilityDistributions.FDistribution;
import matrixProcedures.Matrix;
import java.util.ArrayList;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class ANOVA2_UnbalancedRegression {
    // POJOs
    
    static int n, p, k, dfReg, dfResid, dfTotal, jjIndex, nColumns, nRows, 
           theYVariable, iRow, iColumn;
  
    double jjResid, jjStandResid, jjStudResid, jjLeverage, jjCooksD, jjRStud,
           tempDouble_01, tempDouble_02, tempDouble_03, tempDouble_04,
           meanY, ssTotal, ssResiduals, ssRegression, sumY_Squared_Over_n,
           msReg, msResid, fStatistic, pValue_F, s, r2, adj_r2, paramEst, 
           paramStdErr, paramTRatio, paramPValue;

    String lineToPrint, labelForY, sourceString;
    String[] labelsForX;
    String paramTerm[];
    static ArrayList<String> stringsToPrint;   
    
    // My classes
    static FDistribution fDist;
    static ANOVA2_UnbalancedModel twoWayUnbalancedPlatform;
    static ANOVA2_UnbalancedView regView;
    
    Matrix X, XVar, Y, X_Prime, X_Prime_X, X_Prime_X_Inv, HatMatrix, BetaHats, 
           YHats, XVar_4Scatter, YVar_4Scatter, BXY, SSRes, Y_Prime_Y, StErrCoef, 
           TStat, PValue_T, Resids, StandResids, StudResids, Cooks_D, R_Student;
    
    static TDistribution tDist;

    // POJOs / FX
    Pane pane;
    static Scene statisticsScene;   
    static ScrollPane statisticsPane;
    static Stage statisticsStage;
    Text text;
            
    public ANOVA2_UnbalancedRegression(Matrix incomingX, Matrix inComingY, String[] incomingLabels)
    {
        nRows = incomingX.getRowDimension();

        nColumns = incomingX.getColumnDimension();
        theYVariable = 1;   
        
        // X = new Matrix (nRows, nColumns);   
        X = new Matrix (nRows, nColumns + 1);  // Regr will add a col of ones
        Y = new Matrix (nRows, 1);

        labelsForX = new String[nColumns + 1];
        for (int ithCol = 0; ithCol < nColumns; ithCol++) {
            labelsForX[ithCol] = incomingLabels[ithCol];
        }
        
        labelForY = incomingLabels[nColumns]; //  The last label standing
        // Add column of 1's to X matrix as column[0]
        for (int ithRow = 0; ithRow < nRows; ithRow++)
        {
            X.set(ithRow, 0, 1.0);
            for (int jthCol = 0; jthCol < nColumns; jthCol++) {
                X.set(ithRow, jthCol + 1, incomingX.get(ithRow, jthCol));
            }
            Y.set(ithRow, 0, inComingY.get(ithRow, 0));
        }
        
        double sumY = 0.0;
        for (int ithY = 0; ithY < nRows; ithY++) {
            sumY += Y.get(ithY, 0);
        }
        sumY_Squared_Over_n = sumY * sumY / nRows;
        
        paramTerm = new String[nColumns + 1];
        paramTerm[0] = "Intercept";
        for (iColumn = 1; iColumn <= nColumns; iColumn++)
        {
            paramTerm[iColumn] = labelsForX[iColumn - 1];   //  x, y
        }    
    }
    
    public void doRegressionAnalysis()
    {
        //  Set up variables and matrices
        n = Y.getRowDimension();
        p = X.getColumnDimension();    
        k = p - 1;                      // k is number of explanatory variables
        Resids = new Matrix(n, 1);
        StandResids = new Matrix(n, 1);
        StudResids = new Matrix(n, 1);
        Cooks_D = new Matrix(n, 1);
        R_Student = new Matrix(n, 1);

        // MVP, p73
        X_Prime = X.transpose();
        X_Prime_X = X_Prime.times(X);
        X_Prime_X_Inv = X_Prime_X.inverse();      
        HatMatrix = X.times(X_Prime_X_Inv.times(X_Prime));         
        BetaHats = X_Prime_X_Inv.times(X_Prime.times(Y));         
        YHats = HatMatrix.times(Y);
        BXY = (BetaHats.transpose()).times(X_Prime.times(Y));
        Y_Prime_Y = (Y.transpose()).times(Y);        
        ssRegression = BXY.get(0,0) - sumY_Squared_Over_n;
        SSRes = Y_Prime_Y.minus(BXY);
        ssResiduals = SSRes.get(0,0);
        ssTotal = Y_Prime_Y.get(0,0) - sumY_Squared_Over_n;
       
        r2 = ssRegression / ssTotal;
        s = Math.sqrt(ssResiduals / (n - k - 1));
        adj_r2 = 1.0 - (ssResiduals / (n - k - 1)) / (ssTotal / (n - 1));
       
        dfReg = k;
        dfResid = n - k - 1;
        dfTotal = n - 1;
        msReg = ssRegression / (double)k;
        msResid = ssResiduals / (double)(n - k - 1);
        fStatistic = msReg / msResid;
        fDist = new FDistribution(k, n - k - 1);
        pValue_F = fDist.getRightTailArea(fStatistic);
        Resids = Y.minus(YHats);

        tempDouble_01 = 1.0 / Math.sqrt(msResid);

        StandResids = Resids.times(tempDouble_01);      
        for (int jj = 0; jj < n; jj++)
        { 
           tempDouble_02 = HatMatrix.get(jj, jj);
           StudResids.set(jj, 0, Resids.get(jj, 0) * tempDouble_01 / Math.sqrt(1.0 - tempDouble_02));
           tempDouble_03 = StudResids.get(jj, 0) * StudResids.get(jj, 0) / (double)p;
           tempDouble_04 = tempDouble_02 / (1.0 - tempDouble_02);
           Cooks_D.set(jj, 0, tempDouble_03 * tempDouble_04);
           
           // Student-R calculations from 4.12, 4.13, p135
           jjResid = Resids.get(jj, 0);
           double e_i_sq = jjResid * jjResid;   
           double oneMinus_hii = 1.0 - HatMatrix.get(jj, jj);
           double s_i_sq = ((n - p)*msResid - e_i_sq/oneMinus_hii) / (n - p - 1);
           R_Student.set(jj, 0, jjResid / Math.sqrt(s_i_sq * oneMinus_hii));        
        }

        StErrCoef = new Matrix(k + 1, 1);  // Explanatory variables + intercept 
        TStat = new Matrix(k + 1, 1);  
        PValue_T = new Matrix(k + 1, 1); 
        tDist = new TDistribution (n - k - 1);

        for (int preds = 0; preds <= k; preds++)
        {
           StErrCoef.set(preds, 0, Math.sqrt(msResid * X_Prime_X_Inv.get(preds, preds)));
           TStat.set(preds, 0, BetaHats.get(preds, 0) / StErrCoef.get(preds, 0));
           PValue_T.set(preds, 0, 2.0 * tDist.getRightTailArea(Math.abs(TStat.get(preds, 0))));
        }
   }
    
   private String getLeftMostChars(String original, int leftChars) {
       return StringUtilities.truncateString(original, leftChars);
   }  
  
   public int getNRows()    {return nRows;}
   
   public Matrix getXVar() {return XVar;}
   public Matrix getY() {return Y;}
   public Matrix getYHats() {return YHats;}
   public Matrix getResids() {return Resids;}
   public Matrix getStandardizedResids() {return StandResids;}
   public Matrix getStudentizedResids() {return StudResids;}
   public Matrix getR_StudentizedResids() {return R_Student;}
   
   public double getSimpleRegSlope()  {return BetaHats.get(1, 0);}
   public double getSimpleRegIntercept()  {return  BetaHats.get(0, 0);}   
   
   public double getSSRegression() {return ssRegression; }
   public double getSSResiduals() {return ssResiduals; }   
   public double getSSTotal() {return ssTotal; }   
}
