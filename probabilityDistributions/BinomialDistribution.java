/**************************************************
 *              BinomialDistribution              *
 *                  12/26/17                      *
 *                    09:00                       *
 *************************************************/
package probabilityDistributions;

public class BinomialDistribution 
{
    int nTrials;
    double pSuccess;
    RVUtilities rvUtilities;
    
    public BinomialDistribution()
    {
        nTrials = 0; pSuccess = 0.0;
        rvUtilities = new RVUtilities();
    }
    
    public BinomialDistribution(int n, double p)
    {
        nTrials = n; pSuccess = p;
        rvUtilities = new RVUtilities();
    }
    
    public double getPDF(int x)
    {
        double log_nCr = RVUtilities.log_nCx(nTrials, x);
        double log_TheRest =  x * Math.log(pSuccess)
                                + (nTrials - x) * Math.log(1.0 - pSuccess);
        return Math.exp(log_nCr + log_TheRest);

    }
    
    public double getCDF(int x)
    {
        double cdf = 0.0;
	for (int i = 0; i <= x; i++)
		cdf += getPDF(i);
	return cdf;
    }
    
    public double generateRandom()
    {
        int randomBinomial = 0;
	for (int i = 1; i <= nTrials; i++)
        {
            double randy = rvUtilities.getUniformZeroOne();
	    if (randy <= pSuccess)
			randomBinomial++;
        }
	return randomBinomial;

    }
   
    
}
