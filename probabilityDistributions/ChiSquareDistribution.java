/******************************************************************************
 *                                                                            *
 *                    ExponentialDistribution                                 *
 *                          12/26/17                                          *
 *                            09:00                                           *
 * ***************************************************************************/
/******************************************************************************
*   Tests:  03/22/2015 Critical F's agree with Moore/McCabe/Craig F-table     *
*   Tested:  cdf, density                                                     *
******************************************************************************/

package probabilityDistributions;

public class ChiSquareDistribution 
{
    int df; 

    double[] middleInterval;
    RVUtilities rvUtilities;
    GammaDistribution x2Dist;
    StandardNormal aNormal;
    
    public ChiSquareDistribution(int df) {
        this.df = df;
        // The Chi square distribution with v df is a gamma (v/2, 1/2)
        x2Dist = new GammaDistribution((double)df / 2., 0.5);
    }

    public double getLeftTailArea(double x2Value)
    {
        return x2Dist.getLeftTailArea(x2Value);
    }
    
    public double getInvLeftTailArea(double p) {
        return x2Dist.getInvLeftTailArea(p);
    }

    public double getRightTailArea( double x2Value)  {
        if(Double.isNaN(x2Value)) {
            System.out.println("Ack! NAN in Chi Square distribution");
            System.exit(41);
        }
        return x2Dist.getRightTailArea(x2Value);
    }
    
    public double getInvRightTailArea(double p) {
        double iRTA = x2Dist.getInvLeftTailArea(1 - p);
        return iRTA;
    }
        
    public double[] getInverseMiddleArea(double middleArea)  {
        middleInterval = new double[2];
        middleInterval = x2Dist.getInverseMiddleArea(middleArea);
        return middleInterval;
    }
   
    public double getDensity(double x2Value) {
        return x2Dist.getDensity(x2Value);
    }

    public int getDegreesOfFreedom() { return df; }
    
   public double generateRandom()
    {
        aNormal = new StandardNormal();
        double randChiSquare = 0.0;
        for (int i = 1; i<= df; i++)
        {
            double aUnitNormal = aNormal.generateRandom();
            randChiSquare += aUnitNormal * aUnitNormal;
        }
        return randChiSquare;
    }    
}