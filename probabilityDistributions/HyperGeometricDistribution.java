/******************************************************************************
 *                                                                            *
 *                  HyperGeometricDistribution                                *
 *                          05/12/18                                          *
 *                            12:00                                           *
 * ***************************************************************************/
package probabilityDistributions;

public class HyperGeometricDistribution 
{
    int nPopSize, nPopYesses, nPopFails;
    int nSampleSize, nSampleYesses, nSampleFails;
    RVUtilities rvUtilities;
    
    
    public HyperGeometricDistribution()
    {
        nPopSize = 0; nPopYesses = 0; nPopFails = 0;
        rvUtilities = new RVUtilities();
    }
    
    
    public HyperGeometricDistribution(int populationSize, int popYesses)
    {
        nPopSize = populationSize;
        nPopYesses = popYesses;
        nPopFails = nPopSize - nPopYesses;
        rvUtilities = new RVUtilities();
    }
    
    public double getPDF(int sampleSize, int sampleSuccesses)
    {
        nSampleSize = sampleSize;
        nSampleYesses = sampleSuccesses;
        nSampleFails = nSampleSize - nSampleYesses;
        
        double log_nCx = RVUtilities.log_nCx(nPopYesses, nSampleYesses);      
        double log_TheRest = RVUtilities.log_nCx(nPopFails, nSampleFails);
        double log_Denominator = RVUtilities.log_nCx(nPopSize, nSampleSize);
        
        return Math.exp(log_nCx + log_TheRest - log_Denominator);

    }
    
    public double getCDF(int x)
    {
        double cdf = 0.0;
	for (int iSuccesses = 0; iSuccesses <= x; iSuccesses++)
		cdf += getPDF(nSampleSize, iSuccesses);
	return cdf;
    }
    
    public double generateRandom()
    {
        int randomHyperGeometric = 0;
        double randy = rvUtilities.getUniformZeroOne();
        
	for (int iSuccesses = 0; iSuccesses <= nSampleSize; iSuccesses++)
        {
            
	    if (getCDF(iSuccesses) < randy)
			randomHyperGeometric++;
        }
	return randomHyperGeometric;

    }
   
    
}
