/**************************************************
 *                 Single_t_Model                 *
 *                    12/09/18                    *
 *                     21:00                      *
 *************************************************/

package t_Procedures;

import genericClasses.QuantitativeDataVariable;
import probabilityDistributions.TDistribution;


public class Single_t_Model {
    
    int df, n;
    
    double tStatistic, pValue, xBar, variance, stDev, v1, double_n1, hypothMean,
           critical_t, stErr, ciMean_Low, ciMean_High;
    
    String hypotheses;
    Single_t_procedure single_t_Procedure;
    
    QuantitativeDataVariable theQDV;
    
    static TDistribution tDist;
    String theHypotheses;
    
    
// ***************  Called by Independent t procedure  **********************    
    public Single_t_Model (Single_t_procedure single_t_Procedure,
                                QuantitativeDataVariable theQDV) {
        System.out.println("32 1tModel = " + theQDV);
        this.single_t_Procedure = single_t_Procedure;
        this.theQDV = new QuantitativeDataVariable();
        this.theQDV = theQDV;
    }
    
    // ***************  Called by Independent t procedure  *******************
    public void doTAnalysis() {
        
        hypotheses = single_t_Procedure.getHypotheses();
        hypothMean = single_t_Procedure.getHypothesizedMean();
        n = theQDV.getLegalN();
        xBar = theQDV.getTheMean();
        variance = theQDV.getTheVariance();  
        stDev = Math.sqrt(variance);
        double_n1 = n;
        v1 = variance / double_n1;
        df = n - 1;
        stErr = Math.sqrt(v1);
        
        theHypotheses =  single_t_Procedure.getHypotheses();
        hypothMean = single_t_Procedure.getHypothesizedMean();
        tStatistic = (xBar - hypothMean) / Math.sqrt(v1);
        
        tDist = new TDistribution(df);
        pValue = tDist.getRightTailArea(tStatistic);
        
        printStatistics();
    }
    
    public void printStatistics() {
        System.out.println("115 indepTModel, AltHyp = " + single_t_Procedure.getHypotheses());        
        switch (single_t_Procedure.getHypotheses()) {

            case "NotEqual":  
                
                critical_t = tDist.getCriticalT(0.025);
                
                double tLow = -critical_t ;
                double tHigh = critical_t ;
                System.out.println("127 t-Model, critT pooled/un = " + critical_t);
                
                ciMean_Low = xBar - critical_t * stErr;
                ciMean_High = xBar + critical_t * stErr;

                

            
                pValue = 1.0 - tDist.getMiddleArea(tLow, tHigh);
                
                System.out.println("144 t-Model, tLo/Hi_Unpooled = " + tLow + " / " + tHigh);
                
                
                System.out.printf("                    NSize        Mean        StDev     StErr       95Low       95High\n");
                System.out.printf("   %10s      %4d        %5.3f      %5.3f     %5.3f      %5.3f      %5.3f\n",     "Mean   ",
                                                                                           n,
                                                                                           xBar,
                                                                                           stDev,
                                                                                           stErr,
                                                                                           ciMean_Low,
                                                                                           ciMean_High);
                
                System.out.printf("\n\n                 StandErr       df      t_Value       pValue\n");
                System.out.printf("     x1 - x2     Unpooled    Unpooled   Unpooled     Unpooled   ciLow   ciHighHigh\n");
                System.out.printf("   %8.3f     %8.3f      %4d    %8.3f      %8.3f   %8.3f %8.3f\n",
                                                                                                  xBar,
                                                                                                  stErr,
                                                                                                  df,
                                                                                                  tStatistic,
                                                                                                  pValue,
                                                                                                  ciMean_Low,
                                                                                                  ciMean_High);               

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
    
    public int getDF() { return df; }
    
    public double getTStat() { return tStatistic; }
    
    public double getPValue() { return pValue; }
    
}
