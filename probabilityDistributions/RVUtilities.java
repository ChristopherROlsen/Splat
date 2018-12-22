/******************************************************************************
 *                                                                            *
 *                  HyperGeometricDistribution                                *
 *                          12/26/17                                          *
 *                            09:00                                           *
 * ***************************************************************************/
 /***************************************************************************** 
 *   Tests: getFracGamma, 05/25/14                                            *
 *          getLnGamma,   03/22/15                                            * 
 *                                                                            *
 *   Lanczos, Agree with Abramwitz & Stegun, 1.0 <= x <= 2.0 to 10 places     *
 *   lnFracGamma tested for on line 0 < x < 1, need to verify                 *
 *   lnHalfGammas tested, 1 to 50, agree A & S to 10 decimals                 *
 *   betai, 02/07/15, Tables of Incomplete Beta (2nd), Karl Pearson, 2nd ed.  *
 *                                                                            *
 *****************************************************************************/

package probabilityDistributions;

import java.util.Random;

public class RVUtilities 
{
    static public final double M_E          =   2.71828182845904523536;     // e
    static public final double M_LOG2E      =   1.44269504088896340736;     // log(e)/log(2)
    static public final double M_LOG10E     =   0.434294481903251827651;    // log(e)/log(10)
    static public final double M_LN2        =   0.693147180559945309417;    // ln(2)
    static public final double M_LN10       =   2.30258509299404568402;     // ln(10)
    static public final double M_PI         =   3.14159265358979323846;     // pi -- duh!
    static public final double M_PI_2       =   1.57079632679489661923;     // pi / 2
    static public final double M_PI_4       =   0.785398163397448309616;    // pi / 4
    static public final double M_1_PI       =   0.318309886183790671538;    // 1 / pi
    static public final double M_2_PI       =   0.636619772367581343076;    // 2 / pi
    static public final double M_1_SQRTPI   =   0.564189583547756286948;    // 1 / sqr(pi)
    static public final double M_1_SQRT2PI  =   0.39894228040143267793;     // 1 / sqr(2pi)
    static public final double M_2_SQRTPI   =   1.12837916709551257390;     // 2 / sqr(pi)
    static public final double M_SQRT2      =   1.41421356237309504880;     // sqr(2) duh!
    static public final double M_SQRT_2     =   0.707106781186547524401;    // sqr(2) / 2
    static public final double M_SQRT2PI    =   2.5066282746310005024;      // sqr(2*pi)
    static public final double M_SQRT_PI    =   1.7724538509055160272;      //  sqr(pi)
   
    /**********************************************************************
     *  Constants are for implementation of lnGamma, Lanczos, C.  (1964)  *
     *  A Precision Approximation of the Gamma Function, SIAM Journal on  *
     *  Numerical Analysis, series B, vol 1, pp 86 - 96.  Code is from    *
     *  Press, W. H., et al. (2007) Numerical Recipes: The Art of         *
     *  Scientific Computing (3rd).  Cambridge University Press, 259.     *
     *                   "(Software version 3.02)"                        *
     *                  My translation from the FORTRAN                   *
     **********************************************************************/ 
    
    private static final double[] COF = {
        57.1562356658629235,
        -59.5979603554754912,
        14.1360979747417471,
        -0.491913816097620199,
        .339946499848118887e-4,
        .465236289270485756e-4,
        -.983744753048795646e-4,
        .158088703224912494e-3,
        -.210264441724104883e-3,
        .217439618115212643e-3,
        -.164318106536763890e-3,
        .844182239838527433e-4,
        -.261908384015814087e-4,
        .368991826595316234e-5,
    };    
 
    static final int MAXIT = 1000;

    static final double EPS = 3.0*Math.pow(10, -10.0);
    static final double	FPMIN = Math.pow(10.0, -30.0);    
    
    Random random;
    
    RVUtilities()
    {
        random = new Random();
    }

    public double getUniformZeroOne() { return random.nextDouble(); }
    
/**********************************************************
*       Abramowitz and Stegun Formula 6.1.36,  p257       *
*       This function evaluates: Gamma(x) = (x - 1)!      *
*       with absolute error LT 3 * 10^-7.                *
     * @param xx
     * @return 
**********************************************************/
    
    public static final double getLnGammaX(double xx) {
        int j;
        double x, tmp, y, ser;
        y = x = xx;
        tmp = x + 671. / 128.;
        tmp = (x + 0.5) * Math.log(tmp)-tmp;
        ser = 0.999999999999997092;
        for (j = 0; j < 14; j++) {
            ser += COF[j]/++y;
        }
        return tmp + Math.log(2.5066282746310005 * ser / x);
    }
    
    public static double  getLnFact(int n)
    {
        double logFact = 0;
        if (n < 2)
            return logFact;
        
        // 0! and 1! are defined as 1. (loop not executed when x < 2)
        for (double i = 2; i <= n; ++i) {
            logFact += Math.log(i);
        }
        return logFact;
    }
    
/************************************************************
*        Translated from Numerical Recipes in C.            *
     * @param a
     * @param b
     * @param x
     * @return 
************************************************************/
    public static double getBetai(double a, double b, double x)
    {
        double bt;

        if (x < 0.0 || 1.0 < x)
        {
            System.out.println("Bad x into betai.");
            System.exit(0);
        }

        if (x == 0.0 || x == 1.0)
            bt = 0.0;
        else
            bt = Math.exp(getLnGammaX(a + b)
                              - getLnGammaX(a)
                                     - getLnGammaX(b)
                                            + a * Math.log(x) + b * Math.log(1.0 - x));
        if (x < (a + 1.0) / (a + b + 2.0))
                return bt * getBetacf(a, b, x) / a;
        else
                return 1.0 - bt * getBetacf(b, a, 1.0 - x)/b;
    }

/************************************************************
*  Translated from Numerical Recipes in C.  Need to verify  *
*  correctness; seems to work when used by other methods.   *
     * @param a
     * @param b
     * @param x
     * @return 
************************************************************/
    public static double getBetacf(double a, double b, double x)
    {
        int m, m2;
        double aa, c, d, del, h;

        double qab = a + b;
        double qap = a + 1.0;
        double qam = a - 1.0;

        c = 1.0;
        d = 1.0 - qab * x / qap;
        if (Math.abs(d) < FPMIN)
                d = FPMIN;
        d = 1.0 / d;
        h = d;
        for(m = 1; m <= MAXIT; m++)
        {
            m2 = 2 * m;
            aa = m * (b - m) * x / ((qam + m2) * (a + m2));
            d = 1.0 + aa * d;
            if (Math.abs(d) < FPMIN)
               d = FPMIN;

            c = 1.0 + aa / c;
            if (Math.abs(c) < FPMIN)
               c = FPMIN;

            d = 1.0 / d;
            h *= d * c;
            aa = -(a + m) * (qab + m) * x / ((a + m2) * (qap + m2));
            d = 1.0 + aa * d;
            if (Math.abs(d) < FPMIN)
               d = FPMIN;

            c = 1.0 + aa / c;
            if (Math.abs(c) < FPMIN)
               c = FPMIN;

            d = 1.0 / d;
            del = d * c;
            h *= del;
            if (Math.abs(del - 1.0) < EPS)
               break;
        }
        if (m > MAXIT)
        {
            System.out.println("a or b too big, or MAXIT too small in betacf");
            System.out.println("a = " + a);
            System.out.println("b = " + b);
            System.out.println("MAXIT = " + MAXIT);
            System.exit(0);
        }
        return h;
    }
    
    static public double log_nCx(int n, int x)
    {
        double log_ncx = getLnFact(n) - getLnFact(x) - getLnFact(n - x);
        return log_ncx;
    }
}
