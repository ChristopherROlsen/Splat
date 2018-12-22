/******************************************************************************
 *                                                                            *
 *                       Gamma Distribution                                   *
 *                          12/26/17                                          *
 *                            09:00                                           *
 * ***************************************************************************/

 /*****************************************************************************
 *   Tests:  03/24/2015 cdf, density                                          *
 *           03/26/2015 random                                                *
 *                                                                            *
 *   Methods for Gamma guts are from Press, W. H, et al. Numerical Recipes:   *
 *   The Art of Scientific Computing, 3rd ed.  Cambridge University Press     *
 *****************************************************************************/

package probabilityDistributions;

import static java.lang.Math.abs;
import static java.lang.Math.floor;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import static java.lang.Math.exp;
import static java.lang.Math.log;
import java.util.Random;

public class GammaDistribution 
{
    double alphaParameter, betaParameter, fac;
    
      private static final int ASWITCH = 100;
      private static final double EPS = 2.2204460492503131E-16;//DBL_EPSILON
      private static final double FPMIN = Double.MIN_NORMAL/EPS;
      private double gln;
      
    double adGS1_alpha, chGBa, chGBb, chGBq, adGSb, chGBd, chGBTheta;
    double unifCont_1, unifCont_2, P, V, Y, Z, W;
  
    double[] middleInterval;
    
    Random rand;
  
    static final int NGAU = 18;
  
    static final double[] y = {0.0021695375159141994,
        0.011413521097787704,0.027972308950302116,0.051727015600492421,
        0.082502225484340941, 0.12007019910960293,0.16415283300752470,
        0.21442376986779355, 0.27051082840644336, 0.33199876341447887,
        0.39843234186401943, 0.46931971407375483, 0.54413605556657973,
        0.62232745288031077, 0.70331500465597174, 0.78649910768313447,
        0.87126389619061517, 0.95698180152629142};
    
    static final double[] w = {0.0055657196642445571,
        0.012915947284065419,0.020181515297735382,0.027298621498568734,
        0.034213810770299537,0.040875750923643261,0.047235083490265582,
        0.053244713977759692,0.058860144245324798,0.064039797355015485,
        0.068745323835736408,0.072941885005653087,0.076598410645870640,
        0.079687828912071670,0.082187266704339706,0.084078218979661945,
        0.085346685739338721,0.085983275670394821};
    
    private static double[] a = new double[171];

    private static final int NTOP = 2000;
    private static double[] aa = new double[NTOP];   
    
    public GammaDistribution(double aalph) {
      this(aalph, 1.);
    }
    
    public GammaDistribution(double aalph, double bbet) {
        alphaParameter = aalph;
        betaParameter = bbet;
        if (alphaParameter <= 0. || betaParameter <= 0.) throw new IllegalArgumentException("bad alpha or beta in Gammadist");
        fac = alphaParameter * log(betaParameter)-gammln(alphaParameter);
        rand = new Random();
      
        if (alphaParameter < 1.0)
        {
            adGSb = (RVUtilities.M_E + alphaParameter) / RVUtilities.M_E;
            adGS1_alpha = 1.0 / alphaParameter;
        }

        else if (alphaParameter > 1.0)
        {
            chGBa = 1.0 / Math.sqrt(2.0 * alphaParameter - 1.0);
            chGBb = alphaParameter - Math.log(4.0);
            chGBq = alphaParameter + 1.0 /chGBa;
        }
    }

    double getLeftTailArea(double xValue)
    {
        if (xValue < 0.) throw new IllegalArgumentException("bad x in Gammadist");
        return gammp(alphaParameter, betaParameter*xValue);
    }
    
    public double getInvLeftTailArea(double p) {
        return invcdf(p);
    }
    
    double getRightTailArea(double xValue)
    {
        if (xValue < 0.) throw new IllegalArgumentException("bad x in Gammadist");
        double lta = 1.0 - getLeftTailArea(xValue);
        return lta;
    }
    
    public double getInvRightTailArea(double p) {
        return invcdf(1.0 - p);
    }
    
    public double[] getInverseMiddleArea(double middleArea)  {
        middleInterval = new double[2];
        double leftArea = (1.0 - middleArea) / 2.0;
        double rightArea = 1.0 - leftArea;
        middleInterval[0] = invcdf(leftArea);
        middleInterval[1] = invcdf(rightArea);
        return middleInterval;
    }

    double getDensity(double xValue)
    {
        if (xValue <= 0.) throw new IllegalArgumentException("bad x in Gammadist");
        double density = exp(-betaParameter*xValue+(alphaParameter - 1.)*log(xValue)+fac);
           return density; 
           
    }

    public double generateRandom()
    {
    /************************************************************************
     *      Ref:  Law, Averill M. & Kelton, W. David.  Simulation Modeling  *
     *            and Analysis, 2nd.  McGraw-Hill, 1991, p449f              *
     ************************************************************************/
        
        if(alphaParameter == 1.0) {
            //  Law & Kelton, p448 (Exponential)
            double U = rand.nextDouble();
            return -betaParameter * Math.log(U);
        }
        else if (alphaParameter < 1.0)
                return betaParameter * ahrensDieterGS(); //	aD returns gamma(alpha,1)
        else
                return betaParameter * chengGB();        //	ch returns gamma(alpha, 1)
    }

    double ahrensDieterGS() //	For alpha < 1
    {
        for (;;)
        {
            P = adGSb * rand.nextDouble();  //  P = bU_1
            if (P <= 1.0)
            {
                    Y = Math.pow(P, 1.0 / alphaParameter);
                    if (rand.nextDouble() * Math.exp(Y) <= 1.0)
                            return Y;
            }  //	endif P <= 1.0
            else    //  P > 1
            {
                    Y = -Math.log((adGSb - P) / alphaParameter);
                    if (rand.nextDouble() <= Math.pow(Y, alphaParameter - 1.0))
                            return Y;
            }  //	end else
        }  //	end for
    }  //	end func

    double chengGB()         // For alpha > 1
    {
        for (;;)
        {
            unifCont_1 = rand.nextDouble();
            unifCont_2 = rand.nextDouble();
            V = chGBa * Math.log(unifCont_1 / (1.0 - unifCont_1));
            Y = alphaParameter * Math.exp(V);
            Z = unifCont_1 * unifCont_1 * unifCont_2;
            W = chGBb + chGBq * V - Y;

            boolean tempBool = (W + chGBd - chGBTheta * Z >= 0) || (W >= Math.log(Z));
            if(tempBool)
                return Y;
            }  //	end for
    }  //	end func

    
    public double gammp(final double a, final double x) {
      if (x < 0.0 || a <= 0.0) throw new IllegalArgumentException("bad args in gammp");
      if (x == 0.0) return 0.0;
      else if ((int)a >= ASWITCH) return gammpapprox(a,x,1);
      else if (x < a+1.0) return gser(a,x);
      else return 1.0-gcf(a,x);
    }

    public double gammq(final double a, final double x) {
      if (x < 0.0 || a <= 0.0) throw new IllegalArgumentException("bad args in gammq");
      if (x == 0.0) return 1.0;
      else if ((int)a >= ASWITCH) return gammpapprox(a,x,0);
      else if (x < a+1.0) return 1.0-gser(a,x);
      else return gcf(a,x);
    }

    public double gser(final double a, final double x) {
      double sum,del,ap;
      gln=gammln(a);
      ap=a;
      del=sum=1.0/a;
      for (;;) {
        ++ap;
        del *= x/ap;
        sum += del;
        if (abs(del) < abs(sum)*EPS) {
          return sum*exp(-x+a*log(x)-gln);
        }
      }
    }

    public double gcf(final double a, final double x) {
      int i;
      double an,b,c,d,del,h;
      gln=gammln(a);
      b=x+1.0-a;
      c=1.0/FPMIN;
      d=1.0/b;
      h=d;
      for (i=1;;i++) {
        an = -i*(i-a);
        b += 2.0;
        d=an*d+b;
        if (abs(d) < FPMIN) d=FPMIN;
        c=b+an/c;
        if (abs(c) < FPMIN) c=FPMIN;
        d=1.0/d;
        del=d*c;
        h *= del;
        if (abs(del-1.0) <= EPS) break;
      }
      return exp(-x+a*log(x)-gln)*h;
    }

    public double gammpapprox(final double a, final double x, final int psig) {
      int j;
      double xu,t,sum,ans;
      double a1 = a-1.0, lna1 = log(a1), sqrta1 = sqrt(a1);
      gln = gammln(a);
      if (x > a1) xu = max(a1 + 11.5*sqrta1, x + 6.0*sqrta1);
      else xu = max(0.,min(a1 - 7.5*sqrta1, x - 5.0*sqrta1));
      sum = 0;
      for (j=0;j<NGAU;j++) {
        t = x + (xu-x)*y[j];
        sum += w[j]*exp(-(t-a1)+a1*(log(t)-lna1));
      }
      ans = sum*(xu-x)*exp(a1*(lna1-1.)-gln);
      return (psig!=0 ?(x>a1? 1.0-ans:-ans):(x>a1? ans:1.0+ans));
    }

    public double invcdf(double p) {
        if (p < 0. || p >= 1.) throw new IllegalArgumentException("bad p in Gammadist");
        return invgammp(p, alphaParameter)/betaParameter;
    }
    public double invgammp(final double p, final double a) {
      int j;
      double x,err,t,u,pp,lna1=0,afac=0,a1=a-1;
      //final double EPS=1.e-8;
      gln=gammln(a);
      if (a <= 0.) throw new IllegalArgumentException("a must be pos in invgammap");
      if (p >= 1.) return max(100.,a + 100.*sqrt(a));
      if (p <= 0.) return 0.0;
      if (a > 1.) {
        lna1=log(a1);
        afac = exp(a1*(lna1-1.)-gln);
        pp = (p < 0.5)? p : 1. - p;
        t = sqrt(-2.*log(pp));
        x = (2.30753+t*0.27061)/(1.+t*(0.99229+t*0.04481)) - t;
        if (p < 0.5) x = -x;
        x = max(1.e-3,a*pow(1.-1./(9.*a)-x/(3.*sqrt(a)),3));
      } else {
        t = 1.0 - a*(0.253+a*0.12);
        if (p < t) x = pow(p/t,1./a);
        else x = 1.-log(1.-(p-t)/(1.-t));
      }
      for (j=0;j<12;j++) {
        if (x <= 0.0) return 0.0;
        err = gammp(a,x) - p;
        if (a > 1.) t = afac*exp(-(x-a1)+a1*(log(x)-lna1));
        else t = exp(-x+a1*log(x)-gln);
        u = err/t;
        x -= (t = u/(1.-0.5*min(1.,u*((a-1.)/x - 1))));
        if (x <= 0.) x = 0.5*(x + t);
        if (abs(t) < EPS*x ) break;
      }
      return x;
    } 
    
    private static final double[] COF = {57.1562356658629235,-59.5979603554754912,
        14.1360979747417471,-0.491913816097620199,.339946499848118887e-4,
        .465236289270485756e-4,-.983744753048795646e-4,.158088703224912494e-3,
        -.210264441724104883e-3,.217439618115212643e-3,-.164318106536763890e-3,
        .844182239838527433e-4,-.261908384015814087e-4,.368991826595316234e-5};
  
    static{
      a[0] = 1.;
      for (int i=1;i<171;i++) a[i] = i*a[i-1];

      for (int i=0;i<NTOP;i++) aa[i] = gammln(i+1.);
    }

    public static double gammln(final double xx) {
      int j;
      double x,tmp,y,ser;
      if (xx <= 0) throw new IllegalArgumentException("bad arg in gammln");
      y = x = xx;
      tmp = x+5.24218750000000000; // Rational 671/128
      tmp = (x+0.5)*log(tmp)-tmp;
      ser = 0.999999999999997092;
      for (j=0;j<14;j++) ser += COF[j]/++y;
      return tmp+log(2.5066282746310005*ser/x);
    }
  

    public static double nFactorial(final int n) {
      if (n < 0 || n > 170) throw new IllegalArgumentException("factrl out of range");
      return a[n];
    }
  

    public static double lnNFactorial(final int n) {
        if (n < 0) throw new IllegalArgumentException("negative arg in factln");
        if (n < NTOP) return aa[n];
        return gammln(n+1.);
  }
  
    public static double binomialCoefficient(final int n, final int k) {
        if (n<0 || k<0 || k>n) {
            throw new IllegalArgumentException("bad args in binomial coefficient");
        }
        if (n<171) {
            return floor(0.5+nFactorial(n)/(nFactorial(k)*nFactorial(n-k)));
        }
        return floor(0.5+exp(lnNFactorial(n)-lnNFactorial(k)-lnNFactorial(n-k)));
    }
}
