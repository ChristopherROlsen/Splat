/**************************************************
 *                  NormalScores                  *
 *                Algorithm AS 177                *
 *                    05/16/18                    *
 *                     12:00                      *
 *************************************************/
/**************************************************************************
*                                                                         *
* From Royston, J. P. (1982) Algorithm AS 177: Expected Normal Order      *
* Statistics (Exact and Approximate).  Journal of the Royal Statistical   *
* Society.  Series C (Applied Statistics), vol. 31, No 2. pp 161 - 165.   *
*                                                                         *
**************************************************************************/
package genericClasses;
import probabilityDistributions.*;

public class NormalScores {
    // POJOs
    int nStep, n, n2, i1, ni;
    double c1, d, c, scor, ai1, ani, an, h;
    double[][] work;
    
    // My classes
    StandardNormal standNorm;
    
    public NormalScores() {
        h = 0.025;
        nStep = 721;
        work = new double[5][722];
        standNorm = new StandardNormal();
        initialize();
    }
    
    // 2 < n < 2000
    public double[] getNormalScores(int n) {
        double[] s, normalScores;

        n2 = n / 2;
        s = new double[n2 + 1];
        
        an = n;
        c1 = RVUtilities.getLnFact(n);
        d = c1 - Math.log(an);

        for (int ith = 1; ith <= n2; ith++) {
            i1 = ith - 1;
            ni = n - ith;
            ai1 = i1;
            ani =  ni;
            c = c1 - d;
            scor = 0.0;
            
            for (int jth = 1; jth <= nStep; jth++) {
                scor = scor + Math.exp(work[2][jth] + ai1 * work[3][jth] + ani * work[4][jth]
                        + c) * work[1][jth];
            }

            s[ith] = scor * h;
            d = d + Math.log((ai1 + 1) / ani);
        }        
        
        if (n % 2 == 0) { //  n is even
            normalScores = new double[n];   
            for (int ith = 0; ith < n2; ith++) {
                normalScores[ith] = -s[ith + 1];
                normalScores[n2 + ith] = s[n2 - ith];
            }   //  end n is even loop
            
        } else { //   n is odd
            normalScores = new double[n];   
            for (int ith = 0; ith < n2; ith++) {
                normalScores[ith] = -s[ith + 1];
                normalScores[n2 + ith + 1] = s[n2 - ith];
            }   //  end n is odd loop, supply the middle, always 0.00
            normalScores[n2] = 0.;           
        }
        
        return normalScores;  
    }
    
    private void initialize() {
        double xStart, pi2, half, xx;
        xStart = -9.;
        pi2 = -0.918938533;
        half = 0.5;
        xx = xStart;
        
        for (int ith = 1; ith <= nStep; ith++) {
            work[1][ith] = xx;
            work[2][ith] = pi2 - xx * xx * half;
            // Workaround b/c normal curve algorith delivers -Inf below z = -8.5
            double actualXX;
                actualXX = xx;
                if (xx < -8.5)
                   actualXX = -8.5;
                if (xx > 8.5)
                    actualXX = 8.5;
                
            work[3][ith] = Math.log(standNorm.getRightTailArea(actualXX));
            work[4][ith] = Math.log(standNorm.getLeftTailArea(actualXX));                    

            xx = xStart + (double)ith * h;
        }
    }
}

