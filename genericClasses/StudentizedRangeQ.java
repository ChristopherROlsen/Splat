/**************************************************
 *                StudentizedRangeQ               *
 *                  Gold Standard                 *
 *                    05/16/18                    *
 *************************************************/

// All of the code in this class was adapted from the
// Pascal/Delphi code published in:
// Ferreira, D., Demetrio, C., Manly, B., & Macado, A. (2007).
//     Quantiles from the maximum studentized range distribution.
//     Rev. Mat. Estat., São Paulo, v.25, n.1, p.117-135.
// Java version developed by Victor Bissonnette, Berry College
// last updated: 6/28/2010

package genericClasses;

public class StudentizedRangeQ {
    //  POJOs
    double root[] = {0.0,
        0.993128599185095, 0.963971927277914, 0.912234428251326, 0.839116971822219,
        0.746331906460151, 0.636053680726515, 0.510867001950827, 0.37370608871542,
        0.227785851141645, 0.0765265211334973, -0.0765265211334973, -0.227785851141645,
        -0.37370608871542, -0.510867001950827, -0.636053680726515, -0.746331906460151,
        -0.839116971822219, -0.912234428251326, -0.963971927277914, -0.993128599185095};
    
    double weight[] = {0.0,
        0.0176140071391521, 0.0406014298003869, 0.0626720483341091, 0.0832767415767048,
        0.10193011981724, 0.118194531961518, 0.131688638449177, 0.142096109318382,
        0.149172986472604, 0.152753387130726, 0.152753387130726, 0.149172986472604,
        0.142096109318382, 0.131688638449177, 0.118194531961518, 0.10193011981724,
        0.0832767415767048, 0.0626720483341091, 0.0406014298003869, 0.0176140071391521};
    int ifault = 0;

    public StudentizedRangeQ() {
    }

    public int getErrorCode() {
        return ifault;
    }

    private double apnorm(double z) {

// normal probabilities – accuracy of 1.e-15.
// Z = number of standard deviation from mean
// P, Q = Left and right probabilities from Z. P + Q = 1.
// PDF = the probability density.
// Based upon algorithm 5666 for the error function, from:
// Hart, J.F. et al, 'Computer Approximations'Wiley 1968
//
// Delphi version: 04/11/2004. Ferreira et al. (2007)
// Java version: 06/28/2010, Victor Bissonnette, Berry College


        double p0 = 220.2068679123761e0, p1 = 221.2135961699311e0,
                p2 = 112.0792914978709e0, p3 = 33.91286607838300e0,
                p4 = 6.373962203531650e0, p5 = 0.7003830644436881e0,
                p6 = 0.3526249659989109e-01,
                q0 = 440.4137358247522e0, q1 = 793.8265125199484e0,
                q2 = 637.3336333788311e0, q3 = 296.5642487796737e0,
                q4 = 86.78073220294608e0, q5 = 16.06417757920695e0,
                q6 = 1.755667163182642e0, q7 = 0.8838834764831844e-1,
                cutoff = 7.071e0, root2pi = 2.506628274631001e0;

        double zabs, expntl, p, q, pdf;

        zabs = Math.abs(z);

        expntl = Math.exp(-0.5e0 * zabs * zabs);
        pdf = expntl / root2pi;
        // |z| < cutoff = 10/sqrt(2).
        if (zabs < cutoff) {
            p = expntl * ((((((p6 * zabs + p5) * zabs + p4) * zabs + p3) * zabs
                    + p2) * zabs + p1) * zabs + p0) / (((((((q7 * zabs + q6) * zabs
                    + q5) * zabs + q4) * zabs + q3) * zabs + q2) * zabs + q1) * zabs + q0);
            //      |z| >= cutoff
        } else {
            p = pdf / (zabs + 1.0e0 / (zabs + 2.0e0 / (zabs + 3.0e0 / (zabs + 4.0e0
                    / (zabs + 0.65e0)))));
        }
        if (z < 0.0) {
            q = 1.0 - p;
        } else {
            q = p;
            p = 1.0 - q;
        }
        return p;
    } // apnorm

    private double apnorminv(double p) {

// original name: PPND16
// ALGORITHM AS241 APPL. STATIST. (1988) VOL. 37, NO. 3
// Produces the normal deviate Z corresponding to a given lower
// tail area of P; Z is accurate to about 1 part in 10**16.
// The hash sums below are the sums of the mantissas of the
// coefficients. They are included for use in checking
// transcription.
// Delphi version: 4/11/2004. Ferreira et al. (2007)
// Java version 6/28/2010, Victor Bissonnette, Berry College


        double zero = 0.e0, one = 1.e0, half = 0.5e0,
                split1 = 0.425e0, split2 = 5.e0,
                const1 = 0.180625e0, const2 = 1.6e0,
                //      coefficients for p close to 0.5
                a0 = 3.3871328727963666080e0, a1 = 1.3314166789178437745e+2,
                a2 = 1.9715909503065514427e+3, a3 = 1.3731693765509461125e+4,
                a4 = 4.5921953931549871457e+4, a5 = 6.7265770927008700853e+4,
                a6 = 3.3430575583588128105e+4, a7 = 2.5090809287301226727e+3,
                b1 = 4.2313330701600911252e+1, b2 = 6.8718700749205790830e+2,
                b3 = 5.3941960214247511077e+3, b4 = 2.1213794301586595867e+4,
                b5 = 3.9307895800092710610e+4, b6 = 2.8729085735721942674e+4,
                b7 = 5.2264952788528545610e+3,
                //     coefficients for p not close to 0, 0.5 or 1.

                c0 = 1.42343711074968357734e0, c1 = 4.63033784615654529590e0,
                c2 = 5.76949722146069140550e0, c3 = 3.64784832476320460504e0,
                c4 = 1.27045825245236838258e0, c5 = 2.41780725177450611770e-1,
                c6 = 2.27238449892691845833e-2, c7 = 7.74545014278341407640e-4,
                d1 = 2.05319162663775882187e0, d2 = 1.67638483018380384940e0,
                d3 = 6.89767334985100004550e-1, d4 = 1.48103976427480074590e-1,
                d5 = 1.51986665636164571966e-2, d6 = 5.47593808499534494600e-4,
                d7 = 1.05075007164441684324e-9,
                //      coefficients for p near 0 or 1.
                e0 = 6.65790464350110377720e0, e1 = 5.46378491116411436990e0,
                e2 = 1.78482653991729133580e0, e3 = 2.96560571828504891230e-1,
                e4 = 2.65321895265761230930e-2, e5 = 1.24266094738807843860e-3,
                e6 = 2.71155556874348757815e-5, e7 = 2.01033439929228813265e-7,
                f1 = 5.99832206555887937690e-1, f2 = 1.36929880922735805310e-1,
                f3 = 1.48753612908506148525e-2, f4 = 7.86869131145613259100e-4,
                f5 = 1.84631831751005468180e-5, f6 = 1.42151175831644588870e-7,
                f7 = 2.04426310338993978564e-15;

        double ppnd, q, r;

        ifault = 0;

        q = p - half;

        if (Math.abs(q) <= split1) {

            r = const1 - q * q;

            ppnd = q * (((((((a7 * r + a6) * r + a5) * r + a4) * r + a3)
                    * r + a2) * r + a1) * r + a0) / (((((((b7 * r + b6) * r + b5) * r + b4) * r + b3)
                    * r + b2) * r + b1) * r + one);

        } else {

            if (q < zero) {
                r = p;
            } else {
                r = one - p;
            }
            if (r <= zero) {

                ifault = 1;
                ppnd = zero;
            } else {

                r = Math.sqrt(-Math.log(r));
                if (r <= split2) {
                    r = r - const2;
                    ppnd = (((((((c7 * r + c6) * r + c5) * r + c4) * r + c3)
                            * r + c2) * r + c1) * r + c0) / (((((((d7 * r + d6) * r + d5) * r + d4) * r + d3)
                            * r + d2) * r + d1) * r + one);
                } else {
                    r = r - split2;
                    ppnd = (((((((e7 * r + e6) * r + e5) * r + e4) * r + e3) * r + e2) * r + e1) * r + e0)
                            / (((((((f7 * r + f6) * r + f5) * r + f4) * r + f3) * r + f2) * r + f1) * r + one);
                }

                if (q < zero) {
                    ppnd = -ppnd;
                }
            }
        }

        return ppnd;

    }//norminv

    private double lngammaf(double z) {

// Uses Lanczos’ approximation for ln(gamma) and z > 0. Reference:
//  Lanczos, C. 'A precision approximation of the gamma
//  function'J. SIAM Numer. Anal., B, 1, 86-96, 1964.
//  Accuracy: About 14 significant digits except for small regions
//   in the vicinity of 1 and 2.
//
// Programmer: Alan Miller - CSIRO Division of Mathematics & Statistics
// Latest revision - 17 April 1988
// Delphi version: Date: 04/11/2004. Ferreira et al. (2007)
// Java version: 6/28/2010, Victor Bissonnette, Berry College


        double a[] = new double[10];
        double lnsqrt2pi, tmp, lngamma;
        int j;

        double temp = 0.0;

        a[1] = 0.9999999999995183e0;
        a[2] = 676.5203681218835e0;
        a[3] = -1259.139216722289e0;
        a[4] = 771.3234287757674e0;
        a[5] = -176.6150291498386e0;
        a[6] = 12.50734324009056e0;
        a[7] = -0.1385710331296526e0;
        a[8] = 0.9934937113930748e-05;
        a[9] = 0.1659470187408462e-06;
        lnsqrt2pi = 0.9189385332046727e0;

        if (z <= 0.e0) {
            ifault = 1;
            return temp;
        }

        ifault = 0;

        lngamma = 0.e0;
        tmp = z + 7.e0;

        for (j = 9; j >= 2; j--) {

            lngamma = lngamma + a[j] / tmp;
            tmp = tmp - 1.e0;
        }

        lngamma = lngamma + a[1];
        lngamma = Math.log(lngamma) + lnsqrt2pi - (z + 6.5e0)
                + (z - 0.5e0) * Math.log(z + 6.5e0);

        temp = lngamma;

        return temp;

    } // lngammaf

    private double fint(double w,
            double yii,
            double aii,
            double bii,
            double r) {

        // adapted from: Ferreira et al. (2007)
        // converted to Java: 6/28/2010, Victor Bissonnette, Berry College

        double yyi, temp = 0.0;

        yyi = (bii - aii) * yii + bii + aii;

        temp = Math.pow(Math.exp(1), -yyi * yyi / 8)
                * Math.pow((apnorm(yyi / 2) - apnorm((yyi - 2 * w) / 2)), r - 1);

        return temp;

    } // fint

    private double gausslegendrequadrature(
            double w,
            double yii,
            double aii,
            double bii,
            double r,
            double a,
            double b,
            int n) {

        // adapted from: Ferreira et al. (2007)
        // converted to Java: 6/28/2010, Victor Bissonnette, Berry College

        double c, d, sum, temp;
        int j, jfirst, jlast;

        jfirst = 1;
        jlast = n;
        c = (b - a) / 2.0;
        d = (b + a) / 2.0;
        sum = 0.0;

        for (j = jfirst; j <= jlast; j++) {

            if (root[j] == 0.0) {

                sum = sum + weight[j] * fint(w, d, aii, bii, r);

            } else {

                sum = sum + weight[j] * (fint(w, root[j] * c + d, aii, bii, r));
            }

        } // j loop

        temp = c * sum;

        return temp;

    } // gausslegendrequadrature

    private double prange_v_inf(double w, double r) {

// adapted from: Ferreira et al. (2007)
// converted to Java: 6/28/2010, Victor Bissonnette, Berry College

        double k, ai, bi, soma, ii, temp;
        int i;

        if (w <= 0) {

            temp = 0.0;
            return temp;
        }

        if (w <= 3) {
            k = 3.0;
        } else {
            k = 2.0;
        }

        //inicializando valor de ai p/ i=1

        ai = w / 2.0;
        ii = 1.0;
        bi = ((k - ii) * (w / 2.0) + 8.0 * ii) / k;
        soma = 0.0;

        for (i = 1; i <= (int) k; i++) {

            ii = i;
            soma = soma + ((bi - ai) / 2.0)
                    * gausslegendrequadrature(w, 0.0, ai, bi, r, -1.0, +1.0, 20);

            ai = bi;

            if ((i + 1) == (int) k) {
                bi = 8.0;
            } else {
                bi = ((k - ii - 1.0) * (w / 2.0)
                        + 8.0 * (ii + 1.0)) / k;
            }
        }

        soma = soma * 2.0 * r / Math.sqrt(2.0 * Math.PI);
        soma = soma + Math.pow(Math.exp(1.0), r * Math.log(2.0 * apnorm(w / 2.0) - 1.0));

        temp = soma;
        return temp;
    }

    private double f26(double q,
            double za,
            double aii,
            double c,
            double r,
            double v,
            double l) {

        // adapted from: Ferreira et al. (2007)
        // converted to Java: 6/28/2010, Victor Bissonnette, Berry College

        double yyi, aux, aux1;

        yyi = (za * l + 2.0 * aii * l + l);

        aux1 = prange_v_inf(Math.sqrt(yyi / 2.0) * q, r);

        if (aux1 == 0.0) {
            aux1 = 1e-37;
        }

        aux = c * Math.log(aux1) + Math.log(l)
                + (v / 2.0) * Math.log(v)
                + (-yyi * v / 4.0) + (v / 2.0 - 1.0)
                * Math.log(yyi) - (v * Math.log(2.0)
                + lngammaf(v / 2.0));

        double temp;
        if (Math.abs(aux) >= 1e30) {
            temp = 0.0;
        } else {
            temp = Math.exp(aux);
        }

        return temp;

    } // f26

    private double gausslegdquad(
            double q,
            double yii,
            double aii,
            double r,
            double ci,
            double a,
            double b,
            double v,
            double l,
            int n) {

        // adapted from Ferreira et al. (2007)
        // converted to Java: 6/28/2010, Victor Bissonnette, Berry College

        double cmm, d, sum, temp;
        int j, jfirst, jlast;

        jfirst = 1;
        jlast = n;
        cmm = (b - a) / 2.0;
        d = (b + a) / 2.0;
        sum = 0.0;

        for (j = jfirst; j <= jlast; j++) {

            if (root[j] == 0.0) {
                sum = sum + weight[j]
                        * f26(q, d, aii, ci, r, v, l);
            } else {
                sum = sum + weight[j]
                        * f26(q, root[j] * cmm + d, aii, ci, r, v, l);

            }
        }

        temp = cmm * sum;
        return temp;

    } // {gausslegendrequadrature};

    public double prange(
            double q, // value of studentized range statistic
            double r, // number of groups
            double v, // degrees of freedom error
            double ci) { // pass a 1.0 to get p of stu range stat

// from Ferreira et al. (2007):
// This function returns the value of the cumulative probability function F(q) using
// equations (2.2) or (3.2). If c = 1, then the cumulative probability function of the
// studentized range is returned. If c = 2, then the distribution of the studentized
// maximum modulus is obtained (Dunn and Massey, 1965).

// Pascal code published in Ferreira et al. (2007)
// converted to Java: 6/28/2010, Victor Bissonnette, Berry College

        double precis, a, l, auxprob, probinic;
        boolean found;

        precis = 1e-10;

        ifault = 0;

        if (v == 1.0) {

            if (r < 10) {
                l = 1.0 + 1.0 / (2.0 * r + 3.0);
            } else if (r <= 100.0) {
                l = 1.0844 + (1.119 - 1.0844) / 90 * (r - 10.0);
            } else {
                l = 1.119 + 1.0 / r;
            }

        } else {

            if (v == 2.0) {
                l = 0.968;
            } else if (v <= 100.0) {
                l = 1.0;
            } else if (v <= 800.0) {
                l = 1.0 / 2.0;
            } else if (v <= 5000.0) {
                l = 1.0 / 4.0;
            } else {
                l = 1.0 / 8.0; //if v>25000 use (h(q))^c as approximation to the probability
            }
        }

        if (v > 25000.0) {

            auxprob = Math.pow(prange_v_inf(q, r), ci);
            return auxprob;

        } else {

            auxprob = 0.0;
        }

        found = false;
        a = 0.0;
        probinic = 0.0;

        do {

            auxprob = auxprob
                    + gausslegdquad(q, 0, a, r, ci, -1.0, +1.0, v, l, 20);

            if ((Math.abs(auxprob - probinic) / auxprob) <= precis) {
                found = true;
            } else {
                probinic = auxprob;
            }
            a = a + 1.0;


        } while (!found);

        return auxprob;

    } // prange

    private double qtrngo(
            double p,
            double v,
            double r) {

// algorithm AS 190.2 Appl. Stat. (1983) vol. 32 no.2
// Calculate a initial percentile P from studentized range dist. with v DF
// and r samples, and cumulative probabilities: P [0.80..0.995]
// uses normal inverse functions
//
// Java version: 6/28/2010, Victor Bissonnette, Berry College

        double vmax = 120.0;
        double half = 0.5;
        double one = 1.0;
        double four = 4.0;
        double c1 = 0.8843;
        double c2 = 0.2368;
        double c3 = 1.214;
        double c4 = 1.208;
        double c5 = 1.4142;

        double t = apnorminv(half + half * p);

        if (v < vmax) {
            t = t + (t * t * t + t) / v / four;
        }

        double q = c1 - c2 * t;

        if (v < vmax) {
            q = q - c3 / v + c4 * t / v;
        }

        double temp = t * (q * Math.log(r - one) + c5);
        return temp;

    } // qtrngo

    public double qrange(
            double p, // cumulative p -- use .95 if alpha = .05
            double r, // number of groups
            double v, // df error
            double ci) { // use a 1.0 to get stu range stat

// Adapted from Algorithm AS 190.1 Appl. Stat. (1983) vol. 32 no.2
// approximate the percentile P from studentized range dist. with v DF
// and r samples, and cumulative probabilities: P [0.0..1.00]
// uses functions: normal inverse, normal pdf, prange and qtrngo

// Java version: 6/28/2010, Victor Bissonnette, Berry College

        double jmax, pcut, one, two, five,
                j, q1, q2, p1, p2,
                aux, e1, e2,
                temp;

        jmax = 28.0;
        pcut = 1e-8;
        one = 1.0;
        two = 2.0;
        five = 5.0;

        ifault = 0;

        if ((v < one) || (r < two)) {
            ifault = 1;
            temp = 0.0;
            return temp;
        }

        q1 = qtrngo(p, v, r);

        if (ifault != 0) {

            if (ifault != 0) {
                ifault = 9;
            }
            temp = 0.0;
            return temp;
        }

        do {

            p1 = prange(q1, r, v, ci);
            if (p1 > p) {
                q1 = q1 - 0.4;
            }
            if (q1 < 0.00) {
                q1 = 0.1;
            }

        } while (p < p1);

        if (ifault != 0) {

            if (ifault != 0) {
                ifault = 9;
            }
            temp = 0.0;
            return temp;
        }

        aux = q1;

        if (Math.abs(p1 - p) < pcut) {
            if (ifault != 0.0) {
                ifault = 9;
            }
            temp = q1;
            return temp;
        }

        q2 = q1 + 0.5;

        do {

            p2 = prange(q2, r, v, ci);

            if (p2 < p) {
                q2 = q2 + 0.4;
            }
            if (q2 < 0.0) {
                q2 = 1.0;
            }

        } while (p > p2);

        if (q2 < q1) {
            q2 = q1 + 0.01;
        }

        if (ifault != 0) {

            if (ifault != 0) {
                ifault = 9;
            }
            temp = 0.0;
            return temp;
        }

        j = 2.0;

        do {
            p2 = prange(q2, r, v, ci);

            if (ifault != 0) {

                if (ifault != 0) {
                    ifault = 9;
                }
                j = jmax + 1;

            } else {
                e1 = p1 - p;
                e2 = p2 - p;
                if ((e2 - e1) != 0.0) {
                    aux = (e2 * q1 - e1 * q2) / (e2 - e1);
                }
                if (Math.abs(e1) < Math.abs(e2)) {

                    if (Math.abs(p1 - p) < (pcut * five)) {
                        if (ifault != 0) {
                            ifault = 9;
                        }
                        j = jmax + 2;
                    }

                    q1 = aux;
                    p1 = prange(q1, r, v, ci);
                } else {
                    q1 = q2;
                    p1 = p2;
                    q2 = aux;
                }
            }

            j = j + 1.0;

        } while (j <= jmax);

        return aux;

    } // qrange
} // StuRange Class
