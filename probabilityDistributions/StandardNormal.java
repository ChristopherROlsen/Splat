/**************************************************
 *                  StandardNormal                *
 *                    12/26/17                    *
 *                     09:00                      *
 *************************************************/
 /***************************************************************
 *    Tests:                                                    *
 *       getLeftTail, getRightTail, getMiddle   05/31/2014      *  
 *       getDensity, erf                        05/31/2014      *
 *       getInvLeftTail                         12/11/2014      *
 *       getInvMiddle                           03/21/2015      *
 *                                                              *
 ***************************************************************/

package probabilityDistributions;

import java.util.Random; 


public class StandardNormal 
{   
    final static double M_1_SQRT2PI = 0.398942280401432;        //1 / sqr(2*pi)
    final static double M_1_SQRTPI  = 0.564189583547756286948;   //1 / sqr(pi)
    final static double M_SQRT2     = 1.41421356237309504880;    // sqrt(2)
    
    final static double LIM = Double.MIN_VALUE; // In Jave, the smallest POSITIVE double
    
    final static double P0 = -0.322232431088;
    final static double P1 = -1.0;
    final static double P2 = -0.342242088547;
    final static double P3 = -0.020431210245;
    final static double P4 = -0.453642210148 * Math.pow(10., -4.);
            
    final static double Q0 = 0.0993484626060;
    final static double Q1 = 0.588581570495;
    final static double Q2 = 0.531103462366;
    final static double Q3 = 0.103537752850;
    final static double Q4 = 0.38560700634 * Math.pow(10., -2.);
                                                                                    
    private static double zValue;
    
    static Random randomNumberGenerator;
    
    RVUtilities rvUtilities;

    public StandardNormal()
    {
        randomNumberGenerator = new Random();
        rvUtilities = new RVUtilities();
    }
    
    public double getLeftTailArea(double z_Value)
    {
        double pseudo_z;
 //       Tail area delivers -inf for Z < -8.5 
        if (z_Value < -8.5)
            z_Value = -8.5;
        
        if (z_Value < 0)
            return 1.0 - 0.5 * (1.0 + erf(-z_Value / M_SQRT2));
        else
            return 0.5 * (1.0 + erf(z_Value/ M_SQRT2));
    }
    
    /**************************************************************************
     * Algorithm from:                                                        *                
     * Odeh, R. E., and Evans, J. O. (1974).  Algorithm AS 70: Percentage     *
     * Points of the Normal Distribution, Appl. Stat. 23, 96 - 97.            *
     * Found in Kennedy, W. J., Gentle, J. E.  Statistical Computing          *
     *
     * @param p
     * @return  ***********************************************************************/
    
    public double getInvLeftTailArea(double p)
    {
        double error = 1.0; 
        double xp = 0.0;
        
        double leftTailArea = p;
        
        if (leftTailArea > 0.5)
            leftTailArea = 1.0 - leftTailArea;
        
        // if (leftTailArea < lim) //  Both lTA and xp must be zero?
        //     return xp;
        
        error = 0.0;
        
        if (leftTailArea == 0.5)
            return 0.0; //  xp in algorithm
        else
            {
            double y = Math.sqrt(-2.0 * Math.log(leftTailArea));
            double xp_num = ((((y * P4 + P3) * y + P2) * y) + P1) * y + P0;
            double xp_den = ((((y * Q4 + Q3) * y + Q2) * y) + Q1) * y + Q0;

            xp = y + xp_num / xp_den;
            if (p < 0.5)
                xp = -xp;

            return xp;   
            }
    }

     public double getRightTailArea(double z_Value)
     {
            zValue = z_Value;
            return (1.0 - getLeftTailArea(zValue));
     }
     
     
     public double getInvRightTailArea(double p) {
         return getInvLeftTailArea(1 - p);
     }

    public double getDensity( double z_Value)
    {
        zValue = z_Value;
        double theExponent = -0.5 * zValue * zValue;
        double density = M_1_SQRT2PI * Math.exp(theExponent);	

        return (density);
    }

    public double getMiddleArea(double zLow, double zHigh)
    {
        double lowValue = getLeftTailArea(zLow);
        double highValue = getLeftTailArea(zHigh);
        return highValue - lowValue;
    }
    
    public double[] getInverseMiddleArea(double middleArea) {   // for graphing & crit values
        double[] middleInterval = new double[2];
        double tailArea = (1.0 - middleArea) / 2.0;
        middleInterval[0] = getInvLeftTailArea(tailArea);
        middleInterval[1] = getInvRightTailArea(tailArea);
        return middleInterval;
    }
    
    public double norm_0_to_z(double z)
    {
        return erf(z / M_SQRT2) / 2.0;
    }

    public double generateRandom()
    {
        double randomNormal = randomNumberGenerator.nextGaussian();
        return randomNormal;
    }
    
/************************************************************************
 *                                 erf(X)				*
 *      Subroutine to evaluate the error function translated from      	*
 *      Kennedy, W. J., and Gentle, J. E.  Statistical Computing.  	*
 *      Method is from: Cody, W. J.  Rational Chebyshev Approximations	*
 *      for the Error Function.   Math. Comp. (23), 1969, p631 - 638.   *
 *      Note:  Accuracy over 0 LT z LT ~9 only for positive values.    *
 *             For negative z-, find 1 = erf(z+).                       *   
 *             Java truncates the decimals in the rational approx--     *
 *             consider using BigDecimal?  Cody article has more        *
 *             accurate approx than reported in K and G                 *
     * @param z_Value
     * @return 
 ***********************************************************************/
    
    public double erf(double z_Value)
    {
	int loop1;
	double numerator = 0.0;
        double denominator = 0.0;
        double erfOfX, thePow;

//	Coefficients for rational expressions

	double[][] rationalCoefOne =

	{
            {242.66795523053175, 21.979261618294152, 6.9963834886191355, -0.035609843701815385},
            {215.0588758698612, 91.164905404514901, 15.082797630407787, 1.0}
	};

	double[][] rationalCoefTwo =

	{
            {300.4592610201616005, 451.9189537118729422, 339.3208167343436870,
             152.9892850469404039, 43.1622272220567353, 7.211758250883093659,
             0.5641955174789739711, -1.368648573827167067e-7},

            {300.4592609569832933, 790.9509253278980272, 931.3540948506096211,
             638.9802644656311665, 277.5854447439876434, 77.00015293522947295,
             12.78272731962942351, 1.0}
	};

	double[][] rationalCoefThree =

	{
            {-2.99610707703542174e-3, -0.0494730910623250734, -0.22695659353968693,
             -0.278661308609647788, -0.0223192459734184686},

            { 0.0106209230528467918, 0.191308926107829841, - 1.05167510706793207,
              1.98733201817135256, 1.0}
	};


	if (z_Value <= 0.5)
	{
            for(loop1 = 0; loop1 <= 3; loop1++)
            {
                thePow = 2.0 * loop1;
                numerator   += rationalCoefOne[0][loop1]*Math.pow(z_Value, thePow);
                denominator += rationalCoefOne[1][loop1]*Math.pow(z_Value, thePow);
            }

            erfOfX = z_Value * numerator / denominator;
            return erfOfX;
	}
	else if (z_Value <= 4.0)
	{
            for(loop1 = 0; loop1 <= 7; loop1++)
            {
                thePow = (double)loop1;
                numerator   += rationalCoefTwo[0][loop1]*Math.pow(z_Value, thePow);
                denominator += rationalCoefTwo[1][loop1]*Math.pow(z_Value, thePow);
            }

            erfOfX = 1.0 - Math.exp(-z_Value*z_Value) * numerator / denominator;
            return erfOfX;

	}
	else
	{
            double tempOne, tempTwo, tempThree;

            double xR2 = 1 / (z_Value * z_Value);

            for(loop1 = 0; loop1 <= 4; loop1++)
            {
                thePow = 2 * loop1;
                numerator   += rationalCoefThree[0][loop1]*Math.pow(xR2, thePow);
                denominator += rationalCoefThree[1][loop1]*Math.pow(xR2, thePow);
            }

            tempOne = Math.exp(-z_Value * z_Value);
            tempTwo = M_1_SQRTPI;
            tempThree = numerator / (denominator * z_Value * z_Value);

            erfOfX = 1.0 - tempOne / (tempTwo + tempThree);
            return erfOfX;
	}
   
    }
    
}