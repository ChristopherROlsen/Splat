/**************************************************
 *             GeometricDistribution              *
 *                  12/26/17                      *
 *                    09:00                       *
 *************************************************/

package probabilityDistributions;

public class GeometricDistribution 
{
    double pSuccess;
    double logP, log_1_P;
    
    static RVUtilities rvUtilities;
    
    public GeometricDistribution()
    {
        pSuccess = 0.0;
    }
    
    public GeometricDistribution(double probSuccess)
    {
        logP = Math.log(pSuccess);
        log_1_P = Math.log(1.0 - pSuccess);
    }
    
    public void setPSuccess(double p)
    {
        logP = Math.log(pSuccess);
        log_1_P = Math.log(1.0 - pSuccess);
    }
    
    public double get_pdf(int x)
    {
        return Math.exp(logP + x * log_1_P);
    }
    
    public double get_cdf(int x)
    {
        double cdf = 1.0 - Math.pow((1.0 - pSuccess), x + 1);
	return cdf;
    }
    
    public int generateRandom()
    {
        //  Law, p465
        int randy;
        double logNum = Math.log(rvUtilities.getUniformZeroOne());
        double logDen = log_1_P;
        randy = (int) Math.floor(logNum / logDen);
        return randy;
    }
}
