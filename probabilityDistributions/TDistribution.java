/******************************************************************************
 *                       t Distribution                                       *
 *                          12/09/18                                          *
 *                            00:00                                           *
 * ***************************************************************************/
 
/******************************************************************************
* Test: getCriticalT checked to 3 decimal places (Moore/McCabe/Craig).        *
*          Areas checked to at least 6 decimal places against Meyer           *
*          Inverse areas OK                                                   *
******************************************************************************/

package probabilityDistributions;

public class TDistribution 
{
    int df;
    double lowEnd, highEnd;
    double sqrtpi, double_df, sqrt_double_df;
    
    double pretCoef_1, pretCoef_2, pretCoef_3;
    double[] middleInterval;
    private double tValue;
    
    RVUtilities rvUtil;
   
    boolean closeEnough;
    
    static public final double M_2_SQRTPI =   1.12837916709551257390;	// 2 / sqr(pi)
    static public final double M_LN2      =   0.693147180559945309417;	// ln(2)
    
    final static double tolerance = 0.0000001;
        
    //	The target query arrays are upper bounds for t_values at various degreesof freedom.
    final static double[] HIGHTARGET = // alpha = .0001, ceiling sig t's
        {
           9999.0,  //  Dummy
           6500.0,  //	df = 1...
            400.0,
            200.0,
            100.0   // df = 4
        };
    
    public TDistribution() { 
        this.df = 99; 
        init_t(); 
    }
    
    public TDistribution(int df) {
        this.df = df; 
        init_t();
    }
    
    private void init_t() {
        rvUtil = new RVUtilities();
        sqrtpi = RVUtilities.M_SQRT_PI;
        double_df = (double)df;
        sqrt_double_df = Math.sqrt(double_df);
        
        // Constants for getLnGamGam
        pretCoef_1 = - Math.log(sqrtpi);
        pretCoef_2 = Math.log(sqrtpi) - RVUtilities.M_LN2;
        pretCoef_3 = Math.log(M_2_SQRTPI);      
    }
    
    public void set_df_for_t(int df) {
        
        double_df = (double)df;
        sqrt_double_df = Math.sqrt(double_df);
    }

    public double getLeftTailArea(double t_Value)
    {
        return 1.0 - getRightTailArea(t_Value);
    }
    
    public double getInvLeftTailArea(double p) {
        if ((p <= 0) || (p >= 1.0)) {
            System.out.println("Fatal error in TDistribution.getInvLeftTailArea(double p)");
            System.exit(81);
        }
        return getInvRightTailArea(1 - p);
     }

/* **********************************************************************
*	                Exact Series Expansion		       	        *		
*      Abramowitz, M., & Stegun, I. A.  Handbook of Mathematical        *
*    Functions with Formulas, Graphs, and Mathematical Tables. 	        *
*			Formulas and equations 26.7.3 and 26.7.4.       *
  **********************************************************************/
     public double getRightTailArea(double t_Value)
     {
        double t_tailArea;
        if (t_Value > 0.0)
            t_tailArea = 0.5 - tDist0tot(t_Value);
        else if (t_Value == 0.0)
            t_tailArea = 0.5;
        else
            t_tailArea = 0.5 + tDist0tot(-t_Value);

        return t_tailArea;
     }
     
    public double getInvRightTailArea(double p) {
        
        if ((p <= 0) || (p >= 1.0)) {
            System.out.println("Fatal error in TDistribution.getInvRightTailArea(double p)");
            System.exit(105);
        }

        if (p == 0.5)
            return 0.0;
        else 
        {  
            if (p < 0.5) {
                middleInterval = getInverseMiddleArea( 1.0 - 2.0 * p);
                return middleInterval[0];
            }
            else    //  p > 0.5
            {
                //                                  1.0 - 2.0*(1.0 - p)
               middleInterval = getInverseMiddleArea(2.0 * p - 1.0);
               return middleInterval[1];
            }
        }
    }
    
    public double getMiddleArea(double lowT, double highT) {
        double middleArea = getLeftTailArea(highT) - getLeftTailArea(lowT);
        return middleArea;
    }
    
/*****************************************************************************
* For reasons unknown the algorithm generates wild values (~2500 and ~5000)  *
* with regularity of about 1 in 10^6.  Hence, check for these wild values    *
* hack alert!! -- return a 0.0.                                              *
*
* @return  ***************************************************************************/
    
    public double generateRandom()
    {
        double randy = rvUtil.getUniformZeroOne();
        double preT = getInvLeftTailArea(randy);
        if (Math.abs(preT) > 10.0) {
            preT = 0.0;
        }
        return preT;  
    }

    public double getDensity(double t_Value) 
    {
        //  Density function is from Meyer
        //  pre_tCoef is the log of ((n-1)/2)! /(n-2)/2)!)
        double tCoef, pre_tCoef;
        
        pre_tCoef = getLnGamGam(df);
        tCoef = Math.exp(pre_tCoef) / (sqrtpi * sqrt_double_df);

        double temp_1 = 1.0 + t_Value *t_Value / double_df;
        double temp_2 = (double_df + 1.0)/2.0;
        double density = tCoef / Math.pow(temp_1, temp_2);
        return density;
    }
    
        //       ((n-1)/2)!
        // Ln of ----------
        //       ((n-2)/2)! 
    
     public double getLnGamGam(int df)
     {
        double lnGamGam;

        switch (df)
        {
            case 1:
                lnGamGam = pretCoef_1;
                break;
            case 2:
                lnGamGam = pretCoef_2;
                break;
            case 3:
                lnGamGam = pretCoef_3;
                break; 
            default:
                    //	((n-1)/2)!
                double lnNumer = RVUtilities.getLnGammaX((df+1.0) / 2.0);
                //	((n-2)/2)!
                double lnDenom = RVUtilities.getLnGammaX(df / 2.0);
                lnGamGam = lnNumer - lnDenom;
                break;
        }
        
        return lnGamGam;
     }

    public double tDist0tot (double tValue)
    {
        double iBParam1 = 0.5 * df;
        double iBParam2 = 0.5;
        double iBParam3 = df / (df + tValue * tValue);
        double twoTailArea = RVUtilities.getBetai(iBParam1, iBParam2, iBParam3);
        double zero_2_t = 0.5 * (1.0 - twoTailArea);
        return zero_2_t;
    }


    public double[] getInverseMiddleArea(double middleArea) {
        middleInterval = new double[2];
        double critical_t, halfMiddle, lowWindow, highWindow, guessHalfMiddle;
        double precision = 0.0000001;
        lowWindow = 0.0; highWindow = 10000.0; critical_t = 2.0; 
        if (df < 5)
            highWindow = HIGHTARGET[df];    //  Shave a few loop cycles
        halfMiddle = middleArea / 2.0;
        
        do {
            guessHalfMiddle = tDist0tot(critical_t);  
            if (guessHalfMiddle > halfMiddle) {
               highWindow = critical_t;
            }

            if (guessHalfMiddle < halfMiddle) {
               lowWindow = critical_t;
            }
            critical_t = (lowWindow + highWindow) / 2.0;
        } while (Math.abs(halfMiddle - guessHalfMiddle) > precision);

        middleInterval[1] = critical_t;
        middleInterval[0] = -critical_t;
        return middleInterval;
    }
    
    // Critical t for two-tail
    public double getCriticalT(double alpha) {
        double criticalT;
        criticalT = getInvRightTailArea(1.0 - alpha);
        // System.out.println("237 tDist, df/alpha/critT = " +  df + " / " + alpha + " / " + criticalT);
        return criticalT;
    }
}