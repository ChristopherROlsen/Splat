/**************************************************
 *                F-Distribution                  *
 *                  11/22/18                      *
 *                    21:00                       *
 *************************************************/
 /************************************************************************** 
 *                 03/22/2015                                              *
 *                   12:00                                                 *
 *   Tests:  03/22/2015 Critical F's agree with Moore/McCabe/Craig F-table *
 *   Tested:  cdf, density                                                 *
 **************************************************************************/

package probabilityDistributions;

import java.util.Random; 

public class FDistribution 
{
    static Random randomNumberGenerator;
    int dfNumerator, dfDenominator;
    boolean closeEnough;
    final double tolerance = 0.0000000000001;
    double lowEnd, highEnd;
    double iBParam1, iBParam2, iBParam3;
    double[] middleInterval;
    
    final static double[] HIGHTARGET =  // alpha = .001, ceiling on sig F's
    {
        999999.0,   // Dummy
        700000.0,   // 1 df denominator
          1100.0,   // 2 df denominator
           150.0,   // 3 df denominator
            50.0,   // 4 df denominator
            25.0    // 5 df denominator
    };

    static RVUtilities rvUtil;
    
    public FDistribution(int dfNumerator, int dfDenominator)
    {
        this.dfNumerator = dfNumerator;
        this.dfDenominator = dfDenominator;
        rvUtil = new RVUtilities();
        randomNumberGenerator = new Random();  
        
        //  Constants for rightTailArea
        iBParam1 = (double)dfDenominator / 2.0;
        iBParam2 = (double)dfNumerator / 2.0;
    }

    public double getLeftTailArea(double fValue)
    {
        FDistribution theOtherF = new FDistribution(dfDenominator, dfNumerator);
        double leftTailArea = theOtherF.getRightTailArea(1/fValue);
        return leftTailArea;
    }

    public double getRightTailArea(double fValue)
    {
        iBParam1 = (double)dfDenominator / 2.0;
        iBParam2 = (double)dfNumerator / 2.0;
        iBParam3 = dfDenominator/ (dfDenominator + dfNumerator*fValue);

        //	Call the incomplete beta function
        double rightTailArea = RVUtilities.getBetai(iBParam1, iBParam2, iBParam3);
        return rightTailArea;
    }
    
    public double getInvLeftTailArea(double p)  {
        double targetLeftTailArea = p;
        double critical_F, lowWindow, highWindow, guess_F, thisCDF;
        lowWindow = 0.0; highWindow = 999999.0; guess_F = 1.0;
        
        if (dfDenominator < 6)
            highWindow = HIGHTARGET[dfDenominator];
        
        do {
            thisCDF = getLeftTailArea(guess_F);
            if (targetLeftTailArea < thisCDF) {  // Guessed too high
                highWindow = guess_F;
            }
            else
            if (targetLeftTailArea > thisCDF) {   //  Guessed too low
                lowWindow = guess_F;
            }
            
            guess_F = (lowWindow + highWindow) / 2.0;

            
        } while (Math.abs(targetLeftTailArea - thisCDF) > tolerance);
        
        return guess_F;
    }
    
    public double getInvRightTailArea(double p) {
        return getInvLeftTailArea(1.0 - p);
    }
    
    public double[] getInverseMiddleArea(double middleArea)  {
        middleInterval = new double[2];
        double leftArea = (1.0 - middleArea) / 2.0;
        double rightArea = 1.0 - leftArea;
        middleInterval[0] = getInvLeftTailArea(leftArea);
        middleInterval[1] = getInvLeftTailArea(rightArea);
        return middleInterval;
    }
    
    public double generateRandom()
    {
        double randy = rvUtil.getUniformZeroOne();
        double preF = getInvLeftTailArea(randy);
        //if (Math.abs(preT) > 10.0)
        //    preT = 0.0;
        return preF;  
    }

    public double getDensity(double fValue)
    {
/************************************************************************
 *      Ref:  Meyer, Stuart L                                           *
 *               Data Analysis for Scientists & Engineers               *
 *             John Wiley & Sons, 1975.  p285                           *
 ************************************************************************/
        
        double theLnGamma, theLnNumerator, theLnDenominator;
        double ddf_v1 = (double)dfNumerator, ddf_v2 = (double)dfDenominator;
        theLnGamma = RVUtilities.getLnGammaX((dfNumerator + dfDenominator) / 2.0)
                                        - RVUtilities.getLnGammaX(dfNumerator / 2.0)
                                                - RVUtilities.getLnGammaX(dfDenominator/ 2.0);
        
        theLnNumerator = (0.5 * ddf_v1) * Math.log(ddf_v1 / ddf_v2)
                                                         + 0.5*(ddf_v1 - 2.0) * Math.log(fValue);
        theLnDenominator = 0.5 * (ddf_v1 + ddf_v2)*Math.log(1.0 + ddf_v1*fValue/ddf_v2);

        return Math.exp(theLnGamma + theLnNumerator - theLnDenominator);
    }
    
    public int getDFNumerator() { return dfNumerator; }
    public int getDFDenominator() { return dfDenominator; }    
}
