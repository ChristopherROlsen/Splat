/**************************************************
 *             WeibullDistribution                *
 *                  12/26/17                      *
 *                    09:00                       *
 *************************************************/

package probabilityDistributions;

public class WeibullDistribution 
{
    double alpha, beta;
    RVUtilities rvUtilities;
    
    public WeibullDistribution()
    {
        alpha = 0.0;
        beta = 0.0;
        rvUtilities = new RVUtilities();
    }
    
    public WeibullDistribution(double alpha, double beta)
    {
        this.alpha = alpha;
        this.beta = beta;
    }
    
    public double getPDF(double x)
    {
        double logPDF = 0.0;
        logPDF += Math.log(alpha);
        logPDF += (alpha - 1.0) * Math.log(x);
        double temp = -Math.pow(x/beta, alpha);
        logPDF += temp;
        return Math.exp(logPDF);
    }
    
    public double getCDF(double x)
    {
        double temp = -Math.pow(x/beta, alpha);
        return 1.0 - Math.exp(temp);
    }
    
    public double generateRandom()
    {
        //  Law, p452
        double U = rvUtilities.getUniformZeroOne();
        double lnX = Math.log(beta);
        double lnTheRest = (1.0 / alpha) * Math.log(-Math.log(U));
        double X = Math.exp(lnX + lnTheRest);
        return X;           
    }
}
