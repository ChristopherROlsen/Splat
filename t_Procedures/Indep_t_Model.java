/**************************************************
 *              Independent_t_Model               *
 *                    12/08/18                    *
 *                     21:00                      *
 *************************************************/

package t_Procedures;

import genericClasses.QuantitativeDataVariable;
import java.util.ArrayList;
import probabilityDistributions.TDistribution;


public class Indep_t_Model {
    
    int int_Satterthwaite_df, pooled_df, n1, n2;
    
    double t_Unpooled, t_Pooled, dbl_Unpooled_df, diffXBar, hypothDiff,
            xBar_1, xBar_2, var_1, var_2, double_n1, double_n2, v1, v2,  
            stErr_Pooled, stErr_Unpooled, pValue_Unpooled,
            ciDiff_Low_Unpooled, ciDiff_High_Unpooled, tForTwoTails_Unpooled,
            ciDiff_Low_Pooled, ciDiff_High_Pooled, tForTwoTails_Pooled,
            pValueDiff_Unpooled, pValueDiff_Pooled, stErr_Var1, stErr_Var2,
            mean1_low95, mean1_high95, mean2_low95, mean2_high95,
            critical_t_mean1, critical_t_mean2, critical_t_Unpooled,
            critical_t_Pooled;
    
    double  ciMean1_Low, ciMean1_High, 
            ciMean2_Low, ciMean2_High,
            stDev_Var1, stDev_Var2;
   
    String hypotheses;
    Indep_t_procedure independent_t_Procedure;
    
    ArrayList<QuantitativeDataVariable> allTheQDVs;
    
    static TDistribution tDist_Satterthwaite, tDist_Pooled, tDist_Mean1, tDist_Mean2;
    String theHypotheses;
    
    
// ***************  Called by Independent t procedure  **********************    
    public Indep_t_Model (Indep_t_procedure independent_t_Procedure,
                                ArrayList<QuantitativeDataVariable> allTheQDVs) {
        this.allTheQDVs = new ArrayList();
        this.independent_t_Procedure = independent_t_Procedure;
        this.allTheQDVs = new ArrayList();
        for (int ithQDV = 0; ithQDV < allTheQDVs.size(); ithQDV++) {
            this.allTheQDVs.add(allTheQDVs.get(ithQDV));
        }
    }
    
    // ***************  Called by Independent t procedure  *******************
    public void doIndepTAnalysis() {
        
        hypotheses = independent_t_Procedure.getHypotheses();
        hypothDiff = independent_t_Procedure.getHypothesizedDiff();
        n1 = allTheQDVs.get(1).getLegalN();
        xBar_1 = allTheQDVs.get(1).getTheMean();
        var_1 = allTheQDVs.get(1).getTheVariance();
        
        n2 = allTheQDVs.get(2).getLegalN();
        xBar_2 = allTheQDVs.get(2).getTheMean();
        var_2 = allTheQDVs.get(2).getTheVariance();  
        
        diffXBar = xBar_1 - xBar_2;
        
        double_n1 = n1;
        double_n2 = n2;
        v1 = var_1 / double_n1;
        v2 = var_2 / double_n2;
        stDev_Var1 = Math.sqrt(var_1);
        stDev_Var2 = Math.sqrt(var_2);
        stErr_Var1 = Math.sqrt(v1);
        stErr_Var2 = Math.sqrt(v2);
        
        double satterthwaite_numerator = (v1 + v2) * (v1 + v2);
        double temp1 = v1 * v1 / (double_n1 - 1.);
        double temp2 = v2 * v2 / (double_n2 - 1.);
        double satterthwaite_denominator = temp1 + temp2;
        
        dbl_Unpooled_df = satterthwaite_numerator / satterthwaite_denominator;
        System.out.println("82 t-Model, sat_df = " + dbl_Unpooled_df);
        int_Satterthwaite_df = (int)(dbl_Unpooled_df + 0.5);
        pooled_df = n1 + n2 - 2;
        tDist_Mean1 = new TDistribution(n1 - 1);
        critical_t_mean1 = tDist_Mean1.getCriticalT(.025);
        tDist_Mean2 = new TDistribution(n2 - 1);
        critical_t_mean2 = tDist_Mean2.getCriticalT(.025);
        tDist_Satterthwaite = new TDistribution(int_Satterthwaite_df);
        tDist_Pooled = new TDistribution(pooled_df);

        // ******************************************************************
        double s2_pooled_numerator = (n1 - 1) * var_1 + (n2 - 1) * var_2;
        double s2_pooled_denominator = pooled_df;
        double s2_pooled_ratio = s2_pooled_numerator / s2_pooled_denominator;
        double s2_pooled_ns = 1.0 / double_n1 + 1.0 / double_n2;
        System.out.println("99 t-Model, ratio / ns = " + s2_pooled_ratio + " / " + s2_pooled_ns);
        stErr_Pooled = Math.sqrt(s2_pooled_ratio * s2_pooled_ns);
        stErr_Unpooled = Math.sqrt(v1 + v2);
        System.out.println("102 t-Model, stErr_Pooled / Un = " + stErr_Pooled + " / " + stErr_Unpooled);
        tForTwoTails_Pooled = (diffXBar - hypothDiff) / stErr_Pooled;
        tForTwoTails_Unpooled = (diffXBar - hypothDiff) / stErr_Unpooled;    
        
        // ******************************************************************
        
        theHypotheses =  independent_t_Procedure.getHypotheses();
        hypothDiff = independent_t_Procedure.getHypothesizedDiff();
        t_Unpooled = (xBar_1 - xBar_2 - hypothDiff) / stErr_Unpooled;
        t_Pooled = (xBar_1 - xBar_2 - hypothDiff) / stErr_Pooled;

        printStatistics();
    }
    
    public void printStatistics() {
        System.out.println("115 indepTModel, AltHyp = " + independent_t_Procedure.getHypotheses());        
        switch (independent_t_Procedure.getHypotheses()) {

            case "NotEqual":  
                
                critical_t_Unpooled = tDist_Satterthwaite.getCriticalT(0.025);
                critical_t_Pooled = tDist_Pooled.getCriticalT(0.025);
                
                double tLo_Pooled = -critical_t_Pooled;
                double tHi_Pooled = critical_t_Pooled;
                double tLo_Unpooled = -critical_t_Unpooled;
                double tHi_Unpooled = critical_t_Unpooled;
                System.out.println("127 t-Model, critT pooled/un = " + critical_t_Pooled + " / " + critical_t_Unpooled);
                
                ciMean1_Low = xBar_1 - critical_t_mean1 * stErr_Var1;
                ciMean1_High = xBar_1 + critical_t_mean1 * stErr_Var1;
                ciMean2_Low = xBar_1 - critical_t_mean2 * stErr_Var2;
                ciMean2_High = xBar_1 + critical_t_mean2 * stErr_Var2;

                ciDiff_Low_Unpooled = diffXBar - critical_t_Unpooled * stErr_Unpooled;
                ciDiff_High_Unpooled = diffXBar + critical_t_Unpooled * stErr_Unpooled;
                ciDiff_Low_Pooled = diffXBar - critical_t_Pooled * stErr_Pooled;
                ciDiff_High_Pooled = diffXBar + critical_t_Pooled * stErr_Pooled;
                

            
                pValueDiff_Unpooled = 1.0 - tDist_Satterthwaite.getMiddleArea(tLo_Unpooled, tHi_Unpooled);
                pValueDiff_Pooled = 1.0 - tDist_Pooled.getMiddleArea(tLo_Pooled, tHi_Pooled);
                
                System.out.println("144 t-Model, tLo/Hi_Unpooled = " + tLo_Unpooled+ " / " + tHi_Unpooled);
                System.out.println("145 t-Model, tLo/Hi_Pooled = " + tLo_Pooled + " / " + tHi_Pooled);
                System.out.println("146 t-Model, pValDiff_Pooled/Un = " + pValueDiff_Pooled + " / " + pValueDiff_Unpooled);
                
                
                System.out.printf("                    NSize        Mean        StDev     StErr       95Low       95High\n");
                System.out.printf("   %10s      %4d        %5.3f      %5.3f     %5.3f      %5.3f      %5.3f\n",     "Mean #1",
                                                                                           n1,
                                                                                           xBar_1,
                                                                                           stDev_Var1,
                                                                                           stErr_Var1,
                                                                                           ciMean1_Low,
                                                                                           ciMean1_High);

                System.out.printf("   %10s      %4d        %5.3f      %5.3f     %5.3f      %5.3f      %5.3f\n",     "Mean #2",
                                                                                           n2,
                                                                                           xBar_2,
                                                                                           stDev_Var2,
                                                                                           stErr_Var2,
                                                                                           ciMean2_Low,
                                                                                           ciMean2_High);
                
                
                System.out.printf("\n\n                 StandErr       df      t_Value       pValue\n");
                System.out.printf("     x1 - x2     Unpooled    Unpooled   Unpooled     Unpooled   ciLow   ciHighHigh\n");
                System.out.printf("   %8.3f     %8.3f      %4d    %8.3f      %8.3f   %8.3f %8.3f\n",
                                                                                                  diffXBar,
                                                                                                  stErr_Unpooled,
                                                                                                  int_Satterthwaite_df,
                                                                                                  t_Unpooled,
                                                                                                  pValueDiff_Unpooled,
                                                                                                  ciDiff_Low_Unpooled,
                                                                                                  ciDiff_High_Unpooled);
                

                System.out.printf("\n\n                 StandErr       df      t_Value       pValue\n");
                System.out.printf("     x1 - x2      Pooled      Pooled     Pooled     Pooled   ciLow   ciHighHigh\n");
                System.out.printf("   %8.3f     %8.3f      %4d    %8.3f      %8.3f   %8.3f %8.3f\n",
                                                                                                  diffXBar,
                                                                                                  stErr_Pooled,
                                                                                                  pooled_df,
                                                                                                  t_Pooled,
                                                                                                  pValueDiff_Pooled,
                                                                                                  ciDiff_Low_Pooled,
                                                                                                  ciDiff_High_Pooled);

            break;
        
        case "LessThan":
            /*
            ciDiff_Low = -1.0;

            ciDiff_High = diffXBar + zForOneTail * stErr_Unpooled;
            pValueDiff = standNorm.getLeftTailArea(z_for_Pooled);

            System.out.printf("       Prop        NSize     NSucc     prop     95Low     95High\n");
            System.out.printf("   %10s      %4d     %4d      %5.3f     %5.3f      %5.3f\n",     "Prop #1",
                                                                                       n1,
                                                                                       x1,
                                                                                       p1,
                                                                                       ciLowP1,
                                                                                       ciHighP1);

            System.out.printf("   %10s      %4d     %4d      %5.3f     %5.3f      %5.3f\n",     "Prop #2",
                                                                                       n2,
                                                                                       x2,
                                                                                       p2,
                                                                                       ciLowP2,
                                                                                       ciHighP2);

            System.out.printf("\n\n   p1 - p2     SE_Pooled    SE_Unpooled    z_Pooled   z_Unpooled   pValDiff   ciLow   ciHighHigh\n");
            System.out.printf("    %5.3f     %5.3f         %5.3f        %5.3f       %5.3f      %5.3f      %5.3f     %5.3f\n",
                                                                                              diffP1_P2,
                                                                                              stErrPooled,
                                                                                              stErr_Unpooled,
                                                                                              z_for_Pooled,
                                                                                              z_for_Unpooled,
                                                                                              pValueDiff,
                                                                                              ciDiff_Low,
                                                                                              ciDiff_High);
            */
            break;
            
        case "GreaterThan":
            /*
            ciDiff_Low = diffXBar - zForOneTail * stErr_Unpooled;
            ciDiff_High = 1.0;
            pValueDiff = standNorm.getRightTailArea(z_for_Pooled);

            System.out.printf("       Prop        NSize     NSucc     prop     95Low     95High\n");
            System.out.printf("   %10s      %4d     %4d      %5.3f     %5.3f      %5.3f\n",     "Prop #1",
                                                                                       n1,
                                                                                       x1,
                                                                                       p1,
                                                                                       ciLowP1,
                                                                                       ciHighP1);

            System.out.printf("   %10s      %4d     %4d      %5.3f     %5.3f      %5.3f\n",     "Prop #2",
                                                                                       n2,
                                                                                       x2,
                                                                                       p2,
                                                                                       ciLowP2,
                                                                                       ciHighP2);

            System.out.printf("\n\n   p1 - p2     SE_Pooled    SE_Unpooled    z_Pooled   z_Unpooled   pValDiff   ciLow   ciHighHigh\n");
            System.out.printf("    %5.3f     %5.3f         %5.3f        %5.3f       %5.3f      %5.3f      %5.3f     %5.3f\n",
                                                                                              diffP1_P2,
                                                                                              stErrPooled,
                                                                                              stErr_Unpooled,
                                                                                              z_for_Pooled,
                                                                                              z_for_Unpooled,
                                                                                              pValueDiff,
                                                                                              ciDiff_Low,
                                                                                              ciDiff_High);
            */
            break;
            
        default:
            break;
            }           
    }
    
    public int getDF() { return int_Satterthwaite_df; }
    
    public double getTStat() { return t_Unpooled; }
    
    public double getPValue() { return pValue_Unpooled; }
    
}
