/**************************************************
 *         Independent_t_SummaryStats_Model       *
 *                    10/24/18                    *
 *                     21:00                      *
 *************************************************/

package t_Procedures;

import dialogs.*;
import probabilityDistributions.TDistribution;

public class Indep_t_SummaryStats_Model {
    
    int df, df1, df2, df_Pooled, n1, n2, int_df_Satterthwaite;
    
    double tStatistic, pValue, xBar_1, xBar_2, var_1, var_2,
           dbl_n1, dbl_n2, hypothDiff, stDev_1, stDev_2, xBarDiff;
    
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
    Indep_t_SummaryStats_procedure independent_t_SummaryStats_Procedure;
    TwoMeans_SummaryStats_Dialog twoMeansDialog;
    TDistribution tDistDiffNotPooled, tDistDiffPooled;
    
    static TDistribution tDist;
    String theHypotheses;
    
    public Indep_t_SummaryStats_Model (Indep_t_SummaryStats_procedure independent_t_SummaryStats_Procedure, 
                                       TwoMeans_SummaryStats_Dialog twoMeansDialog) {
        this.independent_t_SummaryStats_Procedure = independent_t_SummaryStats_Procedure;
        this.twoMeansDialog = twoMeansDialog;
    }
    
    public void doIndepTAnalysis() {
        
        hypotheses = independent_t_SummaryStats_Procedure.getHypotheses();
        hypothDiff = independent_t_SummaryStats_Procedure.getHypothesizedDiff();
        n1 = twoMeansDialog.getN1();
        xBar_1 = twoMeansDialog.getXBar1();
        stDev_1 = twoMeansDialog.getStDev1();
        var_1 = stDev_1 * stDev_1;      
        
        n2 = twoMeansDialog.getN2();
        xBar_2 = twoMeansDialog.getXBar2();
        stDev_2 = twoMeansDialog.getStDev2();
        var_2 = stDev_2 * stDev_2; 
        
        System.out.println("61 tSumStats xbar 1/2 = " + xBar_1 + " / " + xBar_2);
        System.out.println("61 tSumStats stDev 1/2 = " + stDev_1 + " / " + stDev_2);
        System.out.println("61 tSumStats n 1/2 = " + n1 + " / " + n2);
        
        df1 = n1 - 1;
        df2 = n2 - 1;
        
        dbl_n1 = n1;
        dbl_n2 = n2;
        theHypotheses =  independent_t_SummaryStats_Procedure.getHypotheses();
        hypothDiff = independent_t_SummaryStats_Procedure.getHypothesizedDiff();

        alpha = independent_t_SummaryStats_Procedure.getAlpha();

        xBarDiff = xBar_1 - xBar_2;
        hypothDiff = independent_t_SummaryStats_Procedure.getHypothesizedDiff();
        alphaOverTwo = alpha / 2.0;

        df_Satterthwaite_Num = (var_1 + var_2) * (var_1 + var_2);
        df_Satterthwaite_Den = var_1 * var_1 / (dbl_n1 - 1) + var_2 * var_2 / (dbl_n2 - 1);      

        df_Satterthwaite = df_Satterthwaite_Num / df_Satterthwaite_Den; 
        int_df_Satterthwaite = (int)Math.floor(df_Satterthwaite + 0.5);

        tDist = new TDistribution(df);
        pValue = tDist.getRightTailArea(tStatistic);

        df_Pooled = n1 + n2 - 2;

        TDistribution tDist1 = new TDistribution(n1 - 1);
        TDistribution tDist2 = new TDistribution(n2 - 1);

        critT_XBar_1 = tDist1.getInvLeftTailArea(alphaOverTwo);
        critT_XBar_2 = tDist2.getInvLeftTailArea(alphaOverTwo);

        tDistDiffNotPooled = new TDistribution(int_df_Satterthwaite);
        tDistDiffPooled = new TDistribution(df_Pooled);

        stErr_xBar1 = stDev_1 / Math.sqrt(dbl_n1);
        stErr_xBar2 = stDev_2 / Math.sqrt(dbl_n2);

        stErr_Diff_NotPooled = Math.sqrt(stErr_xBar1 * stErr_xBar1 + stErr_xBar2 * stErr_xBar2);      
        pooledVarNum = (dbl_n1 - 1.0) * var_1 + (dbl_n2 - 1.0) * var_2;
        pooledVarDen = dbl_n1 + dbl_n2 - 2.0;
        s_sub_p = Math.sqrt(pooledVarNum / pooledVarDen);
        stErr_Diff_Pooled = s_sub_p * Math.sqrt(1.0 / dbl_n1 + 1.0 / dbl_n2);
        t_Not_Pooled = ((xBar_1 - xBar_2) - hypothDiff) / stErr_Diff_NotPooled;
        t_Pooled = ((xBar_1 - xBar_2) - hypothDiff) / stErr_Diff_Pooled;

        ciLow_mu_1 = xBar_1 - critT_XBar_1 * stErr_xBar1; 
        ciHigh_mu_1 = xBar_1 + critT_XBar_1 * stErr_xBar1;
        ciLow_mu_2 = xBar_2 - critT_XBar_2* stErr_xBar2;
        ciHigh_mu_2 = xBar_2 + critT_XBar_2 * stErr_xBar2;

        printStatistics();
    }
    
    public void printStatistics() {

            switch (independent_t_SummaryStats_Procedure.getHypotheses()) {
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
                    
                    System.out.printf("   %10s   %4d      %5.3f     %5.3f     %5.3f      %5.3f     %5.3f\n",
                            "Mean #2",
                            n2,
                            xBar_2,
                            stDev_2,
                            stErr_xBar2,
                            ciLow_mu_2,
                            ciHigh_mu_2); 

                    System.out.printf("\n\n    Choice     xbar1 - xbar2     Stand Err   t_Statistic      df       pValDiff      ciLow      ciHigh\n");
                    System.out.printf("  Not pooled      %5.3f         %5.3f      %5.3f       %5.3f      %5.3f       %5.3f    %5.3f \n",  
                                       xBarDiff,
                                       stErr_Diff_NotPooled,
                                       t_Not_Pooled,
                                       df_Satterthwaite,
                                       pValueDiff_NotPooled,
                                       ciDiff_Low_NotPooled,
                                       ciDiff_High_NotPooled);
                    
                    System.out.printf("    Pooled        %5.3f         %5.3f      %5.3f      %5d        %5.3f       %5.3f    %5.3f \n",     
                                       xBarDiff,
                                       s_sub_p,
                                       t_Pooled,
                                       df_Pooled,
                                       pValueDiff_Pooled,
                                       ciDiff_Low_Pooled,
                                       ciDiff_High_Pooled);
                    
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
                    
                    System.out.printf("   %10s   %4d      %5.3f     %5.3f     %5.3f      %5.3f     %5.3f\n",
                            "Mean #2",
                            n2,
                            xBar_2,
                            stDev_2,
                            stErr_xBar2,
                            ciLow_mu_2,
                            ciHigh_mu_2); 

                    System.out.printf("\n\n    Choice     xbar1 - xbar2     Stand Err   t_Statistic   pValDiff       ciLow    ciHigh\n");
                    System.out.printf("  Not pooled       %5.3f         %5.3f       %5.3f        %5.3f        %3s      %5.3f \n",  
                                       xBarDiff,
                                       stErr_Diff_NotPooled,
                                       t_Not_Pooled,
                                       pValueDiff_NotPooled,
                                       "-\u221E",
                                       ciDiff_High_NotPooled);
                    
                    System.out.printf("    Pooled         %5.3f         %5.3f       %5.3f        %5.3f        %3s      %5.3f \n",    
                                       xBarDiff,
                                       s_sub_p,
                                       t_Pooled,
                                       pValueDiff_Pooled,
                                       "-\u221E",
                                       ciDiff_High_Pooled);

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
                    
                    System.out.printf("   %10s   %4d      %5.3f     %5.3f     %5.3f      %5.3f     %5.3f\n",
                            "Mean #2",
                            n2,
                            xBar_2,
                            stDev_2,
                            stErr_xBar2,
                            ciLow_mu_2,
                            ciHigh_mu_2); 

                    System.out.printf("\n\n    Choice     xbar1 - xbar2     Stand Err   t_Statistic   pValDiff       ciLow    ciHigh\n");
                    System.out.printf("  Not pooled       %5.3f            %5.3f      %5.3f       %5.3f        %5.3f     %3s \n",  
                                       xBarDiff,
                                       stErr_Diff_NotPooled,
                                       t_Not_Pooled,
                                       pValueDiff_NotPooled,
                                       ciDiff_Low_NotPooled,
                                       "+\u221E");
                    
                    System.out.printf("    Pooled         %5.3f            %5.3f      %5.3f       %5.3f        %5.3f     %3s \n",    
                                       xBarDiff,
                                       s_sub_p,
                                       t_Pooled,
                                       pValueDiff_Pooled,
                                       ciDiff_Low_Pooled,
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

