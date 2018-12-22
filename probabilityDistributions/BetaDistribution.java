/**************************************************
 *               BetaDistribution                 *
 *                    12/26/17                    *
 *                     09:00                      *
 *************************************************/

package probabilityDistributions;

public class BetaDistribution 
{

    static double areaTolerance = .0000000001;
    final static double LEFTEND = 0.0;
    
    GammaDistribution gamma1, gamma2;
    RVUtilities rvUtilities;
    
    double mean, h, x, newtonCotesFactor, rightEnd, alphaParameter, betaParameter;
    
    BetaDistribution(double alphaParameter, double betaParameter)
    {
        this.alphaParameter = alphaParameter;
        this.betaParameter = betaParameter;
        gamma1 = new GammaDistribution(alphaParameter, betaParameter);
        gamma2 = new GammaDistribution(alphaParameter, betaParameter);
        rvUtilities = new RVUtilities();
        
    }
 
    public double getLeftTailArea(double xValue)
    {
        /* *********************************************************************
         *		Newton_Cotes Integration, closed form, n = 4           *
         *		Ref:	Numerical Analysis, 9th ed.                    *
         *			Burden, R. L. & Faires, J. D.                  *
         *                               p199                                  *
         ***********************************************************************/
        double x_sub_i, leftEndOfSlice, sizeOfSlice, newtonCotesStuff;
        final double[] newtonCotesCoefficient = {7.0, 32.0, 12.0, 32.0, 7.0};
        double nSlices = 5000.0;
        double	areaOfSlice, f_of_x_sub_i, leftTailArea;
        
        rightEnd = xValue;
        sizeOfSlice = (rightEnd - LEFTEND) / nSlices;
        h = sizeOfSlice / 4.0;
        newtonCotesFactor = 2.0 * h / 45.0;
        
        leftTailArea = 0.0;
        for (int ithSlice = 0; ithSlice < nSlices; ithSlice++)
        {
            leftEndOfSlice = LEFTEND + (double)(ithSlice) * sizeOfSlice;  
            
            areaOfSlice = 0.0;
            newtonCotesStuff = 0.0;
            for(int i = 0; i <= 4; i++)
            {
                x_sub_i = leftEndOfSlice + (double)i * h;
    //		Evaluate f(x) =  Beta Density
                f_of_x_sub_i = getDensity(x_sub_i);
                newtonCotesStuff = newtonCotesStuff + newtonCotesCoefficient[i] * f_of_x_sub_i;
            }  // endfor

            areaOfSlice = newtonCotesFactor * newtonCotesStuff;
            leftTailArea = leftTailArea + areaOfSlice;
        } 
        return leftTailArea;
    }
    
    public double getRightTailArea(double xValue)
    {
        return 1.0 - getLeftTailArea(xValue);
    }

    public double getDensity(double xValue)
    {
    /************************************************************************
     *      Ref:  Law, Averill M.                                                                                                                 *
     *               Simulation Modeling and Analysis, 4th.                                                                      *
     *            and Analysis, 2nd.  McGraw-Hill, 2007, p291f                                                              *
     ************************************************************************/

        if ((xValue <= 0.0) || (xValue >= 1.0))
            return 0.0;

        double lnBeta_a1_a2 = RVUtilities.getLnGammaX(alphaParameter) 
                   + RVUtilities.getLnGammaX(betaParameter)
                     - RVUtilities.getLnGammaX(alphaParameter + betaParameter);

        return Math.exp((alphaParameter - 1.0) * Math.log(xValue)
                                                + (betaParameter - 1.0) * Math.log(1.0 - xValue)
                                                  - lnBeta_a1_a2);
    }

    public double generateRandom()
    {
    /************************************************************************
     *      Ref:  Law, Averill M.                                           *
     *            Simulation Modeling and Analysis, 4th.                    *
     *            and Analysis, 2nd.  McGraw-Hill, 2007, p455f              *
     ************************************************************************/

        double y1 = gamma1.generateRandom();
        double y2 = gamma2.generateRandom();

        return (y1 / (y1 + y2));
    }

    
    
}
