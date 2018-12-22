/**************************************************
 *          NegativeBinomialDistribution          *
 *                  12/26/17                      *
 *                    09:00                       *
 *************************************************/

package probabilityDistributions;

public class NegativeBinomialDistribution 
{
    int nSuccesses;
    
    double pSuccess;
    double logP, log_1_P;
    
    GeometricDistribution geomDist;
    
    public NegativeBinomialDistribution()
    {
        nSuccesses = 0;
        geomDist = new GeometricDistribution();
    }
    
    public NegativeBinomialDistribution(double probSuccess, int nSuccess)
    {
        nSuccesses = nSuccess;
        pSuccess = probSuccess;
        logP = Math.log(pSuccess);
        log_1_P = Math.log(1 - pSuccess);
        geomDist = new GeometricDistribution(probSuccess);
    }
    
    public double getPDF(int x)
    {
        double pdf;
        double logNCR = RVUtilities.log_nCx(nSuccesses + x - 1, x);
        double logOther = nSuccesses * logP + x * log_1_P;
        return Math.exp(logNCR + logOther);
    }
    
    public double getCDF(int x)
    {
        double cdf = 0.0;
	for (int i = 0; i <= x; i++)
		cdf += getPDF(i);
	return cdf;
    }
        
    public int generateRandom()
    {
    	int randy = 0;
	for (int i = 1; i <= nSuccesses; i++)
            randy += geomDist.generateRandom();

	return randy;
    }   
}
