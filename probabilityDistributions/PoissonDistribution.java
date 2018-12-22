/**************************************************
 *            PoissonDistribution                 *
 *                  12/26/17                      *
 *                    09:00                       *
 *************************************************/

package probabilityDistributions;

public class PoissonDistribution 
{
    double lambda, expNegLambda;
    RVUtilities rvUtilities;
    
    PoissonDistribution()
    {
        lambda = 0;
        rvUtilities = new RVUtilities();
    }
    
    PoissonDistribution(double lambda)
    {
        this.lambda = lambda;
        expNegLambda = Math.exp(-lambda);
    }
    
    public double getPDF(int x)
    {
        // Law, p308
        double lnNumer = -lambda + x * Math.log(lambda);
        double lnDenom = RVUtilities.getLnFact(x);
        return Math.exp(lnNumer - lnDenom);
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
        //	Law, p466

	int randy = 0;
	double b = 1.0;
        double uIPlusOne;

	for ( ; ; )
	{
            uIPlusOne = rvUtilities.getUniformZeroOne();
	    b *= uIPlusOne;
	    if (b < expNegLambda)
		break;
	    randy++;
	}
	return randy;
    }   
}
