/**************************************************
 *            Single_t_SummaryStats_Model         *
 *                    12/08/18                    *
 *                     12:00                      *
 *************************************************/

package t_Procedures;

import dialogs.*;
import probabilityDistributions.TDistribution;

public class Single_t_SummaryStats_Model {
    
    int df, n1;
    
    double tStatistic, pValue, xBar_1, xBar_2, var_1, var_2,
           dbl_n1, dbl_n2, hypothMean, stDev_1, stDev_2, xBarDiff;
    
    double df_Satterthwaite_Num, df_Satterthwaite_Den, df_Satterthwaite,
           stErr_Diff_NotPooled, t_Not_Pooled, t_Pooled, pooledVarNum, 
           pooledVarDen, pValueDiff_NotPooled, alpha, alphaOverTwo;
    
    double  ciLow_mu_1, ciHigh_mu_1, ciLow_mu_2, ciHigh_mu_2, s_sub_p,
            critTDiff_XBar1_XBar2_Pooled, critTDiff_XBar1_XBar2_NotPooled,
            critT_XBar_1, critT_XBar_2, pValueDiff_Pooled;
    
    double ciDiff_Low_NotPooled, ciDiff_High_NotPooled, ciDiff_Low_Pooled,
            ciDiff_High_Pooled, stErr_Diff_Pooled, satterthwaite_numerator,
            satterthwaite_denominator;
    
    double stErr_xBar1, stErr_xBar2;
    
    String hypotheses;
    Single_t_SummaryStats_procedure single_t_SummaryStats_Procedure;
    OneMean_SummaryStats_Dialog oneMeanDialog;
    TDistribution tDistDiffNotPooled, tDistDiffPooled;
    
    static TDistribution tDist;
    String theHypotheses;
    
    public Single_t_SummaryStats_Model (Single_t_SummaryStats_procedure single_t_SummaryStats_Procedure, 
                                       OneMean_SummaryStats_Dialog oneMeanDialog) {
        this.single_t_SummaryStats_Procedure = this.single_t_SummaryStats_Procedure;
        this.oneMeanDialog = oneMeanDialog;
    }
    
    public void doSingleTAnalysis() {
        
        hypotheses = single_t_SummaryStats_Procedure.getHypotheses();
        hypothMean = single_t_SummaryStats_Procedure.getHypothesizedMean();
        n1 = oneMeanDialog.getN1();
        xBar_1 = oneMeanDialog.getXBar1();
        stDev_1 = oneMeanDialog.getStDev1();
        var_1 = stDev_1 * stDev_1;      
        
        df = n1 - 1;
        
        dbl_n1 = n1;
        theHypotheses =  single_t_SummaryStats_Procedure.getHypotheses();
        hypothMean = single_t_SummaryStats_Procedure.getHypothesizedMean();

        alpha = single_t_SummaryStats_Procedure.getAlpha();

        hypothMean = single_t_SummaryStats_Procedure.getHypothesizedMean();
        alphaOverTwo = alpha / 2.0;

        tDist = new TDistribution(df);
        pValue = tDist.getRightTailArea(tStatistic);


        TDistribution tDist1 = new TDistribution(n1 - 1);

        critT_XBar_1 = tDist1.getInvLeftTailArea(alphaOverTwo);

        stErr_xBar1 = stDev_1 / Math.sqrt(dbl_n1);

        ciLow_mu_1 = xBar_1 - critT_XBar_1 * stErr_xBar1; 
        ciHigh_mu_1 = xBar_1 + critT_XBar_1 * stErr_xBar1;
        ciLow_mu_2 = xBar_2 - critT_XBar_2* stErr_xBar2;
        ciHigh_mu_2 = xBar_2 + critT_XBar_2 * stErr_xBar2;

        printStatistics();
    }
    
    public void printStatistics() {

            switch (single_t_SummaryStats_Procedure.getHypotheses()) {
                case "NotEqual":
                    System.out.println("133 TMD, alt hyp = Not equal to");
                    
                    critTDiff_XBar1_XBar2_Pooled = tDistDiffPooled.getInvLeftTailArea(alphaOverTwo);
                    critTDiff_XBar1_XBar2_NotPooled = tDistDiffNotPooled.getInvLeftTailArea(alphaOverTwo);
                    
                    ciDiff_Low_NotPooled = (xBar_1 - xBar_2) - critTDiff_XBar1_XBar2_NotPooled * stErr_Diff_NotPooled;
                    ciDiff_High_NotPooled = (xBar_1 - xBar_2) + critTDiff_XBar1_XBar2_NotPooled * stErr_Diff_NotPooled;
                    ciDiff_Low_Pooled = (xBar_1 - xBar_2) - critTDiff_XBar1_XBar2_Pooled * stErr_Diff_Pooled;
                    ciDiff_High_Pooled = (xBar_1 - xBar_2) + critTDiff_XBar1_XBar2_Pooled * stErr_Diff_Pooled;
                    
                    pValueDiff_NotPooled = 2.0 * tDistDiffNotPooled.getRightTailArea(Math.abs(t_Not_Pooled));
                    pValueDiff_Pooled = 2.0 * tDistDiffPooled.getRightTailArea(Math.abs(t_Pooled));

                    System.out.printf("                  N        Mean        StDev      StErr       ciLow       ciHigh\n");
                    System.out.printf("   %10s   %4d      %5.3f     %5.3f     %5.3f      %5.3f     %5.3f\n",
                            "Mean #1",
                            n1,
                            xBar_1,
                            stDev_1,
                            stErr_xBar1,
                            ciLow_mu_1,
                            ciHigh_mu_1);

                    System.out.printf("\n\n    Choice     xbar1 - xbar2     Stand Err   t_Statistic      df       pValDiff      ciLow      ciHigh\n");
                    System.out.printf("  Not pooled      %5.3f         %5.3f      %5.3f       %5.3f      %5.3f       %5.3f    %5.3f \n",  
                                       xBarDiff,
                                       stErr_Diff_NotPooled,
                                       t_Not_Pooled,
                                       df_Satterthwaite,
                                       pValueDiff_NotPooled,
                                       ciDiff_Low_NotPooled,
                                       ciDiff_High_NotPooled);
                    
                    break;
                    
                case "LessThan":
                    System.out.println("185 TMD, alt hyp = Less than");

                    ciDiff_High_NotPooled = (xBar_1 - xBar_2) + tDistDiffNotPooled.getInvRightTailArea(alpha) * stErr_Diff_NotPooled;
                    ciDiff_High_Pooled = (xBar_1 - xBar_2) + tDistDiffNotPooled.getInvRightTailArea(alpha) * stErr_Diff_Pooled;
                    
                    pValueDiff_NotPooled = tDistDiffNotPooled.getLeftTailArea(t_Not_Pooled);
                    pValueDiff_Pooled = tDistDiffPooled.getLeftTailArea(t_Pooled);
                    
                    System.out.printf("                  N        Mean        StDev      StErr       ciLow       ciHigh\n");
                    System.out.printf("   %10s   %4d      %5.3f     %5.3f     %5.3f      %5.3f     %5.3f\n",
                            "Mean #1",
                            n1,
                            xBar_1,
                            stDev_1,
                            stErr_xBar1,
                            ciLow_mu_1,
                            ciHigh_mu_1);

                    System.out.printf("\n\n    Choice     xbar1 - xbar2     Stand Err   t_Statistic   pValDiff       ciLow    ciHigh\n");
                    System.out.printf("  Not pooled       %5.3f         %5.3f       %5.3f        %5.3f        %3s      %5.3f \n",  
                                       xBarDiff,
                                       stErr_Diff_NotPooled,
                                       t_Not_Pooled,
                                       pValueDiff_NotPooled,
                                       "-\u221E",
                                       ciDiff_High_NotPooled);

                    break;
                case "GreaterThan":
                    System.out.println("231 TMD, alt hyp = Greate than");
                    ciDiff_Low_NotPooled = (xBar_1 - xBar_2) - tDistDiffNotPooled.getInvLeftTailArea(alpha) * stErr_Diff_NotPooled;
                    ciDiff_Low_Pooled = (xBar_1 - xBar_2) - tDistDiffNotPooled.getInvLeftTailArea(alpha) * stErr_Diff_Pooled;
                    
                    pValueDiff_NotPooled = tDistDiffNotPooled.getLeftTailArea(t_Not_Pooled);
                    pValueDiff_Pooled = tDistDiffPooled.getLeftTailArea(t_Pooled);
                    
                    System.out.printf("                  N        Mean        StDev      StErr       ciLow       ciHigh\n");
                    System.out.printf("   %10s   %4d      %5.3f     %5.3f     %5.3f      %5.3f     %5.3f\n",
                            "Mean #1",
                            n1,
                            xBar_1,
                            stDev_1,
                            stErr_xBar1,
                            ciLow_mu_1,
                            ciHigh_mu_1);

                    System.out.printf("\n\n    Choice     xbar1 - xbar2     Stand Err   t_Statistic   pValDiff       ciLow    ciHigh\n");
                    System.out.printf("  Not pooled       %5.3f            %5.3f      %5.3f       %5.3f        %5.3f     %3s \n",  
                                       xBarDiff,
                                       stErr_Diff_NotPooled,
                                       t_Not_Pooled,
                                       pValueDiff_NotPooled,
                                       ciDiff_Low_NotPooled,
                                       "+\u221E");

                    break;
                    
                default:
                    break;

        }
    }    
    public int getDF() { return df; }
    
    public double getTStat() { return tStatistic; }
    
    public double getPValue() { return pValue; }
    
}


