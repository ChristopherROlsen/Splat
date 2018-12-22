/**************************************************
 *             LognormalDistribution              *
 *                  12/26/17                      *
 *                    09:00                       *
 *************************************************/

package probabilityDistributions;

public class LognormalDistribution 
{
    double mu, mu2, sigma, sigma2, muPrime, sigma2Prime, muPrime_2, sigma2Prime_2; 
    double factor, twoSigma2, deviation_2, exponent, density;
    double ncRightEndLimit; //    For NewtonCotes
    double mu_4Randy, sigma_4Randy, sigma2_4Randy;
 
    
    final double[] newtonCotesCoefficient = {7.0, 32.0, 12.0, 32.0, 7.0};
    final double areaTolerance = .0000000001;
    
    RVUtilities rvUtilities;
    StandardNormal standNorm;
    
    LognormalDistribution()
    {
        rvUtilities = new RVUtilities();
        mu = 0.0; sigma2 = 1.0;
        doInitializations();
    }
    

    LognormalDistribution(double mu, double sigma2)
    {
        this.mu = mu;
        this.sigma2 = sigma2;
        doInitializations();
    }
    
    private void doInitializations()
    {
        sigma = Math.sqrt(sigma2);
        factor = RVUtilities.M_1_SQRT2PI / sigma;
        twoSigma2 = 2.00 * sigma2;
        standNorm = new StandardNormal();
        
        //  These constants are for random number generation
        //  See Law, p454
        
        muPrime = Math.exp(mu + 0.5 * sigma2);
        sigma2Prime = Math.exp(2. * mu + sigma2) * (Math.exp(sigma2) - 1.0);
        muPrime_2 = muPrime * muPrime;
        sigma2Prime_2 = sigma2Prime * sigma2Prime;
        mu_4Randy = Math.log(muPrime_2 / Math.sqrt(muPrime_2 + sigma2Prime_2));
        sigma2_4Randy = Math.log(1.0 + sigma2Prime_2 / muPrime_2);
        sigma_4Randy = Math.sqrt(sigma2_4Randy);
    }
    
    
    public double getDensity(double xValue)
    {
        if (xValue <= 0.0)
            return 0.0;
        deviation_2 = (Math.log(xValue) - mu) * (Math.log(xValue) - mu);
        exponent = -deviation_2/ twoSigma2;
        density = factor * Math.exp(exponent) / xValue;
        //System.out.println("density = " + density);
        return density;
    }
    
    double altGetLeftArea(double xValue)
    {
        double pseudo_z, zToErf, ltaReturned;
        //double temp1 = (Math.log(xValue) - mu) / sigma;
        //double temp2 = standNorm.getLeftTailArea(temp1);
        //System.out.println("x = " + xValue + " mu = " + mu + " sigma = " + sigma + "temp1 = " + temp1 +  "  Duh answer = " + temp2);
        //return temp2;
        
        pseudo_z = (Math.log(xValue) - mu) / sigma;
        
        if (pseudo_z < 0)
            ltaReturned = 1.0 - standNorm.getLeftTailArea(-pseudo_z);
        else
            ltaReturned = standNorm.getLeftTailArea(pseudo_z);
        
        System.out.println("pseudo_z = " + pseudo_z +  "  ltaReturned = " + ltaReturned);
        return ltaReturned;
    }
   
    double getLeftArea(double xValue)
    {
    /************************************************************************
     *      Ref:  Law, Averill M.Simulation Modeling                        *
     *            and Analysis, 4th.  p290                                  *
     ************************************************************************/

        double	sliceOfArea, f_of_x, leftTailArea, x;

    /***********************************************************************
     *		Newton_Cotes Integration, closed form, n = 4           *
     *		Ref:	Numerical Analysis, 9th ed.                    *
     *		    Burden, R. L. & Faires, J. D.                      *
     *                         PSEE BETA                                   *
     ***********************************************************************/
        double leftEnd, rightEnd, h, newtonCotesFactor;

        leftEnd = 1. / 5000.0;
        rightEnd = xValue;   
        h = (rightEnd - leftEnd) / 5000.0;
        newtonCotesFactor = 2.0 * h / 45.0;

        leftTailArea = 0.0;
        x = 0.; //  Compiler complaint about x not being initialized...

        for (int slice = 0; slice < 5000; slice++)
        {
            double xZero = leftEnd + slice * h; //	Left end of slice
            sliceOfArea = 0.0;

            for(int i = 0; i <= 4; i++)
            {
                x = xZero + i * h;  // ... here.
    //		Evaluate f(x) = Lognormal Density
                f_of_x = getDensity(x);
                //System.out.println(f_of_x);
                sliceOfArea += newtonCotesCoefficient[i] * f_of_x;
            }  // endfor
            sliceOfArea *= newtonCotesFactor;
            leftTailArea += sliceOfArea;
            if ((sliceOfArea < areaTolerance) // If additional areas are small,
                      && (x > 10.0 * mu))  				 // and we are not in left tail...
                    return leftTailArea;           // we're outta here!
        }  // endfor

        return leftTailArea;
    }
    
    public double generateRandom()
    {
     /************************************************************************
     *      Ref:  Law, Averill M.Simulation Modeling                        *
     *            and Analysis, 4th.  p454                                  *
     ************************************************************************/   
        double zRandy = standNorm.generateRandom();
        double randy = sigma2_4Randy * zRandy +  mu_4Randy;
        return Math.exp(randy);
    }
    
}
