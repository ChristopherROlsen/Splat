/******************************************************************************
 *                                                                            *
 *                    ExponentialDistribution                                 *
 *                          12/26/17                                          *
 *                            09:00                                           *
 * ***************************************************************************/

package probabilityDistributions;

public class ExponentialDistribution 
{
    double beta; 

    double[] middleInterval;
    GammaDistribution expo;
    StandardNormal aNormal;
    RVUtilities rvUtilities;
    
    public ExponentialDistribution(double beta) { this.beta = beta; }

    public double getLeftTailArea(double x) {
        return 1.0 - Math.exp(-x / beta);
    }
    
    public double getInvLeftTailArea(double p) {
        return -beta * Math.log(1.0 - p);
    }

    public double getRightTailArea( double x)  { 
        return 1.0 - getLeftTailArea(x);
    }
    
    public double getInvRightTailArea(double p) { 
        return getInvLeftTailArea(1.0 - p);
    }
        
    public double[] getInverseMiddleArea(double middleArea)  {
        middleInterval = new double[2];
        middleInterval[0] = getInvLeftTailArea((1.0 - middleArea) / 2.0);
        middleInterval[1] = getInvLeftTailArea((1.0 + middleArea) / 2.0);
        return middleInterval;
    }
   
    public double getDensity(double x) { return Math.exp(-x / beta) / beta; }
    
    double generateRandom()
    {
        double unifCont = rvUtilities.getUniformZeroOne();
        return -beta * Math.log(unifCont);
    }   
}