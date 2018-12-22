/**************************************************
 *          ContinuousUniformDistribution         *
 *                  12/26/17                      *
 *                    09:00                       *
 *************************************************/

package probabilityDistributions;

public class ContinuousUniformDistribution 
{
    double a, b, density;    // left and right ends
    static RVUtilities rvUtilities;
        
    ContinuousUniformDistribution()
    {
        a = 0.0; b = 1.0; density = 1.0;
        rvUtilities = new RVUtilities();
    }
    
    ContinuousUniformDistribution(double leftEnd, double rightEnd)
    {
        a = leftEnd; b = rightEnd; density = 1.0 / (b - a);
        rvUtilities = new RVUtilities();
    }
    
    public double getDensity(double xValue)  {return density;}
    
    public double getLeftArea(double xValue) {return (xValue - a) / (b - a);}
    
    public double getRightArea(double xValue) { return (b - xValue) / (b - a);}
    
    public double generateRandom() 
    {
        double uniform_01 = rvUtilities.getUniformZeroOne();
        double randy = a + uniform_01 * (b - a);
        return randy; 
    }
}
