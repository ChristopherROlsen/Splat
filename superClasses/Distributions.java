package superClasses;

public class Distributions {

    int ifault = 0;
    final double pi = 3.141592653589793 /* math constant pi       */;
    final double xln2sp = 9.18938533204673e-01 /* loge( sqrt( 2 * pi ) ) */;
    final double rmax = 9.99999999999999e+37 /* maximum flt pt number  */;
    final double rsmall = 9.99999999999999e-37 /* smallest flt pt number */;
    final double rinf = 9.99999999999999e+37 /* machine "infinity"     */;
    final double zeta = 1.0e-16 /* approx. machine prec.  */;
    final int maxprec = 16 /* max. precision         */;
    final double sqrt2 = 1.4142135623730950 /* square root of 2       */;
    final double lnteninv = 0.4342944819032520 /* 1 / ln(10)             */;
    final double lntwo = 0.6931471805599450 /* ln(2)                  */;

    public Distributions() {
    }

    private double LogGamma(double Z) {

        double S = 1 + 76.18009173 / Z - 86.50532033
                / (Z + 1) + 24.01409822
                / (Z + 2) - 1.231739516
                / (Z + 3) + .00120858003
                / (Z + 4) - .00000536382
                / (Z + 5);
        double LG = (Z - .5) * Math.log(Z + 4.5)
                - (Z + 4.5)
                + Math.log(S * 2.50662827465);

        return LG;
    }

    public double ncchisqdf(double x, double f, double theta) {

        double n = 1,
                lam = theta / 2,
                pois = Math.exp(-lam),
                v = pois,
                x2 = x / 2,
                f2 = f / 2,
                t = Math.pow(x2, f2) * Math.exp(-x2 - LogGamma(f2 + 1)),
                chisq = v * t;

        while (n <= (x - f) / 2) {
            pois = pois * lam / n;
            v = v + pois;
            t = t * x / (f + 2 * n);
            chisq = chisq + v * t;
            n = n + 1;
        }
        while (t * x / (f + 2 * n - x) > .000001) {
            pois = pois * lam / n;
            v = v + pois;
            t = t * x / (f + 2 * n);
            chisq = chisq + v * t;
            n = n + 1;
        }
        return chisq;
    }

    public double prho(int n, int is) {

//        Algorithm AS 89   Appl. Statist. (1975) Vol.24, No. 3, P377.
//        To evaluate the probability of obtaining a value greater than or
//        equal to is, where is=(n**3-n)*(1-r)/6, r=Spearman's rho and n
//        must be greater than 1
//     Auxiliary function required: ALNORM = algorithm AS66
//     Code coverted to Java by Victor Bissonnette, Berry College        
        double b, x, y, z, u;
        double c1 = 0.2274, c2 = 0.2531, c3 = 0.1745,
                c4 = 0.0758, c5 = 0.1033, c6 = 0.3932,
                c7 = 0.0879, c8 = 0.0151, c9 = 0.0072,
                c10 = 0.0831, c11 = 0.0131, c12 = 0.00046;

        double temp = 1.0;
        final int maxN = 12;

//        Test admissibility of arguments and initialize
        if (n <= 1) {
            return temp;
        }
        if (is <= 0.0) {
            return temp;
        }
        temp = 0.0;

        int js = is;
        if (js > n * (n * n - 1) / 3) {
            return temp;
        }

        if (js != (2 * (js / 2))) { // only allow even numbers for js
            js = js + 1;
        }
        boolean exact = true;
        if (n > maxN) {
            exact = false;
        }

//        Exact evaluation of probability
        if (exact) {

            int nfac = 1;
            int i;
            int[] l = new int[maxN + 1];

            for (i = 1; i <= n; i++) {
                nfac = nfac * i;
                l[i] = i;
            }

            temp = 1.0 / nfac;

            if (js == (n * (n * n - 1) / 3)) {
                return temp;
            }

            int ifr = 0;

            for (int m = 1; m <= nfac; m++) {
                mloop:
                {

                    int ise = 0;

                    for (i = 1; i <= n; i++) {

                        ise = ise + (int) Math.pow((i - l[i]), 2.0);
                    }

                    if (js < ise) {
                        ifr = ifr + 1;
                    }

                    int n1 = n;

                    do {

                        int mt = l[1];
                        int nn = n1 - 1;

                        for (i = 1; i <= nn; i++) {
                            l[i] = l[i + 1];
                        }

                        l[n1] = mt;

                        if (l[n1] != n1) {
                            break mloop;
                        }
                        if (n1 == 2) {
                            break mloop;
                        }

                        n1 = n1 - 1;
                    } while (m != nfac);
                } // mloop block
            } // m loop

            temp = Double.valueOf(ifr) / Double.valueOf(nfac);

        } else {   // exact test

//        Evaluation by Edgeworth series expansion
            b = 1.0 / Double.valueOf(n);

            x = (6.0 * (Double.valueOf(js) - 1.0) * b / (1.0 / (b * b) - 1.0)
                    - 1.0) * Math.sqrt(1.0 / b - 1.0);

            y = x * x;

            u = x * b * (c1 + b * (c2 + c3 * b) + y * (-c4
                    + b * (c5 + c6 * b) - y * b * (c7 + c8 * b
                    - y * (c9 - c10 * b + y * b * (c11 - c12 * y)))));

//      Call to algorithm AS 66
            temp = u / Math.exp(y / 2.0) + pnorm(x, 1);

            if (temp < 0.0) {
                temp = 0.0;
            }
            if (temp > 1.0) {
                temp = 1.0;
            }

        } // aproximate p

        return temp;

    } // prho

    public int CritIS(int n, double p) {

        int is = -0;

        double check = 0.0;
        int increment = 10;
        int i = 0;

        while (check < p) {
            is = is + increment;
            check = 1.0 - prho(n, is);
        }
        is = is - increment;
        check = 0.0;
        increment = 2;

        while (check < p) {
            is = is + increment;
            check = 1.0 - prho(n, is);
        }
        is = is - increment;

        return is;

    } // critrho

    public double udist(int M, int N, int U, double P, int purpose) {

//     ALGORITHM AS 62  APPL. STATIST. (1973) VOL.22, NO.2
//
//     The distribution of the Mann-Whitney U-statistic is generated for
//     the two given sample sizes
//
//     The original Fortran code was translated and adapted to Java by
//     Victor Bissonnette, Department of Psychology, Berry College
//     Last updated: 1/28/2010
//
//     Input parameters:
//
//       M,N:     sample size of each group
//       U:       value of Mann-Whitney U statistic (if computing probability)
//                (pass a zero if computing critical value)
//       P:       tail probability value (if computing critical value of U)
//                (pass a zero if computing probability of U)
//       purpose: purpose of analysis -- when:
//                1: udist will return the tail probability of U
//                2: udist will return the critical value of U given p
        int IFAULT = 0;
        int MINMN, MN1, MAXMN, N1, I, IN, L, K, J;
        double ZERO = 0.0, ONE = 1.0, SUM;
        double temp = 0.0;

        MINMN = Math.min(M, N);

        int LFR = (M * N) + 1;
        int LWRK = (1 + MINMN + (M * N / 2));

        double[] FRQNCY = new double[LFR];
        double[] WORK = new double[LWRK];

        //      Check smaller sample size
        IFAULT = 1;
        if (MINMN < 1) {
            return temp;
        }

        //      Check size of results array
        IFAULT = 2;
        MN1 = M * N + 1;
        if (LFR < MN1) {
            return temp;
        }

        //     Set up results for 1st cycle and return if MINMN = 1
        MAXMN = Math.max(M, N);
        N1 = MAXMN + 1;

        for (I = 1; I <= N1; I++) {
            FRQNCY[I - 1] = ONE;
        }

        block1:
        {

            if (MINMN == 1) {
                break block1;
            }

            //      Check length of work array
            IFAULT = 3;

            if (LWRK < (((MN1 + 1) / 2) + MINMN)) {
                return temp;
            }

            //      Clear rest of FREQNCY
            N1 = N1 + 1;

            for (I = N1; I <= MN1; I++) {

                FRQNCY[I - 1] = ZERO;

            }

            //      Generate successively higher order distributions
            WORK[0] = ZERO;

            IN = MAXMN;

            for (I = 2; I <= MINMN; I++) {

                WORK[I - 1] = ZERO;
                IN = IN + MAXMN;
                N1 = IN + 2;
                L = 1 + (IN / 2);
                K = I;

                //        Generate complete distribution from outside inwards
                for (J = 1; J <= L; J++) {

                    K = K + 1;
                    N1 = N1 - 1;
                    SUM = FRQNCY[J - 1] + WORK[J - 1];
                    FRQNCY[J - 1] = SUM;
                    WORK[K - 1] = SUM - FRQNCY[N1 - 1];
                    FRQNCY[N1 - 1] = SUM;

                } // J loop

            } // I loop

        } // block1:

        //  Convert frequencies to probabilities
        SUM = ZERO;

        for (I = 1; I <= MN1; I++) {

            SUM = SUM + FRQNCY[I - 1];
            FRQNCY[I - 1] = SUM;

        }

        for (I = 1; I <= MN1; I++) {

            FRQNCY[I - 1] = FRQNCY[I - 1] / SUM;

        }

        IFAULT = 0;

        //  Find chance p of a particular U:
        if (purpose == 1) {

            temp = FRQNCY[U];

            return temp;

        }

        //  Find critical value of U given p
        if (purpose == 2) {

            for (I = 0; I <= (LFR - 1); I++) {

                if (FRQNCY[I] >= P) {

                    temp = I - 1;

                    return temp;
                }
            }

        }

        return temp;

    } // udist

    private long numsr(int n, int k) {

        long temp = 0;

        if (k < 0) {
            temp = 0;
        } else if ((k == 0) && (n == 0)) {
            temp = 1;
        } else if ((k != 0) && (n == 0)) {
            temp = 0;
        } else {
            temp = numsr(n - 1, k) + numsr(n - 1, k - n);
        }

        return temp;

    } /* numsr */


    public double sigsr(int sr, int n) {

        // This procedure will return the chance probability of the signed-
        // rank test.  Be careful -- as n increases, the time it takes to
        // run this procedure goes up quickly.  
        // Written by Victor Bissonnette, Berry College
        double temp = 0.0, p = 0.0;
        int i, j;

        i = 0;

        while (i <= sr) {
            p = numsr(n, i);
            p = p * 2;
            for (j = 1; j <= n; j++) {
                p = p / 2;
            }
            temp = temp + p;
            i = i + 1;
        }

        return temp;

    }

    private double logten(double x) {

        double temp = 0.0;
        if (x <= 0.0) {
            temp = 0.0;
        } else {
            temp = Math.log(x) * lnteninv;
        }
        return temp;

    } /* logten */


    private double algama(double arg) {

        double rarg, alinc, scale, top, bot, frac, algval;
        int i, iapprox, iof, ilo, ihi;
        boolean qminus;
        boolean qdoit;

        double[] p = {
            4.12084318584770e+00, 8.56898206283132e+01, 2.43175243524421e+02,
            -2.61721858385614e+02, -9.22261372880152e+02, -5.17638349802321e+02,
            -7.74106407133295e+01, -2.20884399721618e+00,
            5.15505761764082e+00, 3.77510679797217e+02, 5.26898325591498e+03,
            1.95536055406304e+04, 1.20431738098716e+04, -2.06482942053253e+04,
            -1.50863022876672e+04, -1.51383183411507e+03,
            -1.03770165173298e+04, -9.82710228142049e+05, -1.97183011586092e+07,
            -8.73167543823839e+07, 1.11938535429986e+08, 4.81807710277363e+08,
            -2.44832176903288e+08, -2.40798698017337e+08,
            8.06588089900001e-04, -5.94997310888900e-04, 7.93650067542790e-04,
            -2.77777777688189e-03, 8.33333333333330e-02};

        double[] q = {
            1.00000000000000e+00, 4.56467718758591e+01, 3.77837248482394e+02,
            9.51323597679706e+02, 8.46075536202078e+02, 2.62308347026946e+02,
            2.44351966250631e+01, 4.09779292109262e-01, 1.00000000000000e+0,
            1.28909318901296e+02, 3.03990304143943e+03, 2.20295621441566e+04,
            5.71202553960250e+04, 5.26228638384119e+04, 1.44020903717009e+04,
            6.98327414057351e+02,
            1.00000000000000e+00, -2.01527519550048e+03, -3.11406284734067e+05,
            -1.04857758304994e+07, -1.11925411626332e+08, -4.04435928291436e+08,
            -4.35370714804374e+08, -7.90261111418763e+07};

        /* initialize */
        algval = rinf;
        scale = 1.0;
        alinc = 0.0;
        frac = 0.0;
        rarg = arg;
        iof = 1;
        qminus = false;
        qdoit = true;

        /* adjust for negative argument */
        if (rarg < 0.0) {

            qminus = true;
            rarg = -rarg;
            top = (int) (rarg);
            bot = 1.0;

            if (((int) (top / 2.0) * 2.0) == 0.0) {
                bot = -1.0;
            }

            top = rarg - top;

            if (top == 0.0) {
                qdoit = false;
            } else {
                frac = bot * pi / Math.sin(top * pi);
                rarg = rarg + 1.0;
                frac = Math.log(Math.abs(frac));
            }

        }
        /* choose approximation interval */
        /* based upon argument range     */

        if (rarg == 0.0) {
            qdoit = false;
        } else if (rarg <= 0.5) {

            alinc = -Math.log(rarg);
            scale = rarg;
            rarg = rarg + 1.0;

            if (scale < zeta) {
                algval = alinc;
                qdoit = false;
            }
        } else if (rarg <= 1.5) {
            scale = rarg - 1.0;
        } else if (rarg <= 4.0) {
            scale = rarg - 2.0;
            iof = 9;
        } else if (rarg <= 12.0) {
            iof = 17;
        } else if (rarg <= rmax) {
            alinc = (rarg - 0.5) * Math.log(rarg) - rarg + xln2sp;
            scale = 1.0 / rarg;
            rarg = scale * scale;

            top = p[ 24];

            for (i = 25; i <= 28; i++) {
                top = top * rarg + p[i];
            }

            algval = scale * top + alinc;

            qdoit = false;

        }

        /* common evaluation code for arg <= 12. */
        /* horner's method is used, which seems  */
        /* to give better accuracy than          */
        /* continued fractions.                  */
        if (qdoit) {

            ilo = iof + 1;
            ihi = iof + 7;

            top = p[iof - 1];
            bot = q[iof - 1];

            for (i = ilo; i <= ihi; i++) {
                top = top * rarg + p[i - 1];
                bot = bot * rarg + q[i - 1];
            }

            algval = scale * (top / bot) + alinc;

        }

        if (qminus) {
            algval = frac - algval;
        }

        return algval;

    }   /* algama */


    private double cdbeta(double xx, double alpha, double beta,
            int dprec, int maxiter) {

        double epsz, a, b, c, f, fx, x, apb, zm, alo, ahi, blo, bhi, bod, bev, zm1, d1, aev, aod;
        double temp;
        int ntries, iter, cprec;
        boolean qswap, qdoit, qconv, skip, ready;

        /* initialize */
        if (dprec > maxprec) {
            dprec = maxprec;
        } else if (dprec <= 0) {
            dprec = 1;
        }

        cprec = dprec;

        epsz = Math.pow(10, -dprec);

        x = xx;
        a = alpha;
        b = beta;
        qswap = false;
        temp = -1.0;
        qdoit = true;
        /* check arguments */
        /* error if:       */
        /*    x <= 0       */
        /*    a <= 0       */
        /*    b <= 0       */

        ifault = 1;

        ready = true;

        if (x <= 0.0) {
            ready = false;
        }
        if ((a <= 0.0) || (b <= 0.0)) {
            ready = false;
        }

        temp = 1.0;
        ifault = 0;
        /* if x >= 1, return 1.0 as prob */

        if (x >= 1.0) {
            ready = false;
        }

        /* if x > a / ( a + b ) then swap */
        /* a, b for more efficient eval.  */
        if (x > (a / (a + b))) {
            x = 1.0 - x;
            a = beta;
            b = alpha;
            qswap = true;
        }

        /* check for extreme values */
        skip = false;

        if ((x == a) || (x == b)) {
            skip = true;
        }

        if (!skip) {
            if (a == ((b * x) / (1.0 - x))) {
                skip = true;
            }
        }

        if (!skip) {
            if (Math.abs(a - (x * (a + b))) <= epsz) {
                skip = true;
            }
        }

        if (!skip) {

            c = algama(a + b)
                    + (a * Math.log(x))
                    + (b * Math.log(1.0 - x))
                    - algama(a)
                    - algama(b)
                    - Math.log(a - x * (a + b));

            if ((c < -36.0) && qswap) {
                ready = false;
            }

            temp = 0.0;

            if (c < -180.0) {
                ready = false;
            }

        }

        /*  set up continued fraction expansion */
        /*  evaluation.                         */
        if (ready) {
            apb = a + b;
            zm = 0.0;
            alo = 0.0;
            bod = 1.0;
            bev = 1.0;
            bhi = 1.0;
            blo = 1.0;

            ahi = Math.exp(algama(apb)
                    + (a * Math.log(x))
                    + (b * Math.log(1.0 - x))
                    - algama(a + 1.0)
                    - algama(b));

            f = ahi;
            fx = 0.0;

            iter = 0;

            /* continued fraction loop {s here. */
            /* evaluation continues until maximum   */
            /* iterations are exceeded, ||          */
            /* convergence achieved.                */
            qconv = false;

            while ((iter <= maxiter) && !qconv) {

                fx = f;

                zm1 = zm;
                zm = zm + 1.0;
                d1 = a + zm + zm1;
                aev = -(a + zm1) * (apb + zm1) * x / d1 / (d1 - 1.0);
                aod = zm * (b - zm) * x / d1 / (d1 + 1.0);
                alo = bev * ahi + aev * alo;
                blo = bev * bhi + aev * blo;
                ahi = bod * alo + aod * ahi;
                bhi = bod * blo + aod * bhi;

                if (Math.abs(bhi) < rsmall) {
                    bhi = 0.0;
                }

                if (bhi != 0.0) {
                    f = ahi / bhi;
                    qconv = (Math.abs((f - fx) / f) < epsz);
                }

                iter = iter + 1;

            }

            /* arrive here when convergence    */
            /* achieved, || maximum iterations */
            /* exceeded.                       */
            if (qswap) {
                temp = 1.0 - f;
            } else {
                temp = f;
            }

            /* calculate precision of result */
            if (Math.abs(f - fx) != 0.0) {
                cprec = (int) -logten(Math.abs(f - fx));
            } else {
                cprec = maxprec;
            }
        }

        return temp;

    }   /* cdbeta */


    public double sigf(double f, double dfn, double dfd) {

        int dprec = 12, maxiter = 200;

        int iter;
        double pval = -1.0;

        if ((dfn > 0.0) && (dfd > 0.0)) {
            pval = cdbeta(dfd / (dfd + f * dfn), dfd / 2.0, dfn / 2.0,
                    dprec, maxiter);

            if (ifault != 0) {
                pval = -1.0;
            }
        }

        return pval;

    }   /* sigf */


    private double gammain(double y, double p, int dprec, int maxiter) {

        double oflo = 1.0e+37;
        double minexp = -87.0;

        double f, c, a, b, term, gin, an, rn, dif, eps, temp;
        int iter, cprec;
        double[] pn = new double[6];
        boolean done, ready;

        /* check arguments */
        dif = 0.0;
        ifault = 1;
        temp = 1.0;
        ready = true;

        if ((y <= 0.0) || (p <= 0.0)) {
            ready = false;
        }

        /* check value of f */
        ifault = 0;

        f = (p * Math.log(y))
                - algama(p + 1.0) - y;

        if (f < minexp) {
            ready = false;
        }

        f = Math.exp(f);
        if (f == 0.0) {
            ready = false;
        }

        if (ready) {

            /* set precision */
            if (dprec > maxprec) {
                dprec = maxprec;
            } else if (dprec <= 0) {
                dprec = 1;
            }

            cprec = dprec;

            eps = Math.pow(10, -dprec);

            /* choose infinite series || */
            /* continued fraction.       */
            if ((y > 1.0) && (y >= p)) { /* continued fraction */

                a = 1.0 - p;
                b = a + y + 1.0;
                term = 0.0;
                pn[ 0] = 1.0;
                pn[ 1] = y;
                pn[ 2] = y + 1.0;
                pn[ 3] = y * b;
                gin = pn[ 2] / pn[ 3];
                done = false;
                iter = 0;

                while ((iter <= maxiter) && !done) {
                    iter = iter + 1;
                    a = a + 1.0;
                    b = b + 2.0;
                    term = term + 1.0;
                    an = a * term;

                    pn[ 4] = b * pn[ 2] - an * pn[ 0];
                    pn[ 5] = b * pn[ 3] - an * pn[ 1];

                    if (pn[ 5] != 0.0) {

                        rn = pn[ 4] / pn[ 5];
                        dif = Math.abs(gin - rn);

                        if (dif <= eps) {
                            if (dif <= (eps * rn)) {
                                done = true;
                            }
                        }

                        gin = rn;

                    }

                    pn[ 0] = pn[ 2];
                    pn[ 1] = pn[ 3];
                    pn[ 2] = pn[ 4];
                    pn[ 3] = pn[ 5];

                    if (Math.abs(pn[ 4]) >= oflo) {
                        pn[ 0] = pn[ 0] / oflo;
                        pn[ 1] = pn[ 1] / oflo;
                        pn[ 2] = pn[ 2] / oflo;
                        pn[ 3] = pn[ 3] / oflo;
                    }

                }

                gin = 1.0 - (f * gin * p);

                temp = gin;
                /* calculate precision of result */

                if (dif != 0.0) {
                    cprec = (int) -logten(dif);
                } else {
                    cprec = maxprec;
                }

            } /* continued fraction */ else { /* infinite series */

                iter = 0;

                term = 1.0;
                c = 1.0;
                a = p;
                done = false;

                while ((iter <= maxiter) && ((term / c) > eps)) {

                    a = a + 1.0;
                    term = term * y / a;
                    c = c + term;

                    iter = iter + 1;

                }

                temp = c * f;
                /* calculate precision of result */

                cprec = (int) -logten(term / c);

            }   /* infinite series */

        } /* ready */

        return temp;

    }    /* gammain */


    public double sigchi(double chisq, double df) {

        int maxiter = 200;
        int dprec = 12;
        double pval = 0.0;

        pval = 1.0 - gammain(chisq / 2.0, df / 2.0, dprec, maxiter);

        if (ifault != 0) {
            pval = -1.0;
        }

        return pval;

    }   /* sigchi */


    /**
     * *******************************************************
     * @param Z
     * @param function
     * @return 
     */
    public double pnorm(double Z, int function) {

        /*      Normal distribution probabilities accurate to 1.e-15.
         Z = no. of standard deviations from the mean.
         P, Q = probabilities to the left & right of Z.   P + Q = 1.
         PDF = the probability density.

         Based upon algorithm 5666 for the error function, from:
         Hart, J.F. et al, 'Computer Approximations', Wiley 1968

         Programmer: Alan Miller

         Latest revision - 30 March 1986

         Converted to Java: Victor Bissonnette, Berry College

         function: 1: returns Q
         2: returns P
         3: returns PDF                                  */
        double P0 = 220.2068679123761,
                P1 = 221.2135961699311,
                P2 = 112.0792914978709,
                P3 = 33.91286607838300,
                P4 = 6.373962203531650,
                P5 = .7003830644436881,
                P6 = .3526249659989109e-01;

        double Q0 = 440.4137358247522,
                Q1 = 793.8265125199484,
                Q2 = 637.3336333788311,
                Q3 = 296.5642487796737,
                Q4 = 86.78073220294608,
                Q5 = 16.06417757920695,
                Q6 = 1.755667163182642,
                Q7 = .8838834764831844e-1;

        double CUTOFF = 7.071,
                ROOT2PI = 2.506628274631001,
                ZABS = 0.0,
                P = 0.0, Q = 0.0, PDF = 0.0,
                pval = 0.0;

        ZABS = Math.abs(Z);

        block1:
        {

            if (ZABS > 37.0) {
                PDF = 0.0;
                if (Z > 0.0) {
                    P = 1.0;
                    Q = 0.0;
                } else {
                    P = 0.0;
                    Q = 1.0;
                }

                break block1;

            } /* if zabs > 37 */

            double EXPNTL = 0.0;
            EXPNTL = Math.exp(-0.50 * ZABS * ZABS);
            PDF = EXPNTL / ROOT2PI;

            if (ZABS < CUTOFF) {
                P = EXPNTL * ((((((P6 * ZABS + P5) * ZABS + P4) * ZABS + P3) * ZABS
                        + P2) * ZABS + P1) * ZABS + P0) / (((((((Q7 * ZABS + Q6) * ZABS
                        + Q5) * ZABS + Q4) * ZABS + Q3) * ZABS + Q2) * ZABS + Q1) * ZABS
                        + Q0);
            } else {
                P = PDF / (ZABS + 1.0 / (ZABS + 2.0 / (ZABS + 3.0 / (ZABS + 4.0
                        / (ZABS + 0.650)))));
            }

            if (Z < 0.0) {
                Q = 1.0 - P;
            } else {
                Q = P;
                P = 1.0 - Q;
            }

        } /* block1 */

        switch (function) {

            case 1:
                pval = Q;
                break;
            case 2:
                pval = P;
                break;
            case 3:
                pval = PDF;
                break;
        }

        return pval;

    } /* normp */


    public double pnorminv(double p) {

// original name: PPND16
// 128 Rev. Mat. Estat., SÃ£o Paulo, v.25, n.1, p.117-135, 2007
// ALGORITHM AS241 APPL. STATIST. (1988) VOL. 37, NO. 3
// Produces the normal deviate Z corresponding to a given lower
// tail area of P; Z is accurate to about 1 part in 10**16.
// The hash sums below are the sums of the mantissas of the
// coefficients. They are included for use in checking
// transcription.
// Delphi version
// converted to Java by Victor Bissonnette, Berry College
        double zero = 0.e0,
                one = 1.e0,
                half = 0.5e0,
                split1 = 0.425e0,
                split2 = 5.e0,
                const1 = 0.180625e0,
                const2 = 1.6e0;

// Coefficients for P close to 0.5
        double a0 = 3.3871328727963666080e0, a1 = 1.3314166789178437745e+2,
                a2 = 1.9715909503065514427e+3, a3 = 1.3731693765509461125e+4,
                a4 = 4.5921953931549871457e+4, a5 = 6.7265770927008700853e+4,
                a6 = 3.3430575583588128105e+4, a7 = 2.5090809287301226727e+3,
                b1 = 4.2313330701600911252e+1, b2 = 6.8718700749205790830e+2,
                b3 = 5.3941960214247511077e+3, b4 = 2.1213794301586595867e+4,
                b5 = 3.9307895800092710610e+4, b6 = 2.8729085735721942674e+4,
                b7 = 5.2264952788528545610e+3;

// Coefficients for P not close to 0, 0.5 or 1.
        double c0 = 1.42343711074968357734e0, c1 = 4.63033784615654529590e0,
                c2 = 5.76949722146069140550e0, c3 = 3.64784832476320460504e0,
                c4 = 1.27045825245236838258e0, c5 = 2.41780725177450611770e-1,
                c6 = 2.27238449892691845833e-2, c7 = 7.74545014278341407640e-4,
                d1 = 2.05319162663775882187e0, d2 = 1.67638483018380384940e0,
                d3 = 6.89767334985100004550e-1, d4 = 1.48103976427480074590e-1,
                d5 = 1.51986665636164571966e-2, d6 = 5.47593808499534494600e-4,
                d7 = 1.05075007164441684324e-9;

// Coefficients for P near 0 or 1.
        double e0 = 6.65790464350110377720e0, e1 = 5.46378491116411436990e0,
                e2 = 1.78482653991729133580e0, e3 = 2.96560571828504891230e-1,
                e4 = 2.65321895265761230930e-2, e5 = 1.24266094738807843860e-3,
                e6 = 2.71155556874348757815e-5, e7 = 2.01033439929228813265e-7,
                f1 = 5.99832206555887937690e-1, f2 = 1.36929880922735805310e-1,
                f3 = 1.48753612908506148525e-2, f4 = 7.86869131145613259100e-4,
                f5 = 1.84631831751005468180e-5, f6 = 1.42151175831644588870e-7,
                f7 = 2.04426310338993978564e-15;

        double temp = 0, ppnd = 0, q = 0, r = 0;
        ifault = 0;

        q = p - half;

        if (Math.abs(q) <= split1) {
            r = const1 - q * q;
            ppnd = q * (((((((a7 * r + a6) * r + a5) * r + a4) * r + a3) * r + a2) * r + a1) * r + a0) / (((((((b7 * r + b6) * r + b5) * r + b4) * r + b3) * r + b2) * r + b1) * r + one);
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

                    ppnd = (((((((c7 * r + c6) * r + c5) * r + c4) * r + c3) * r + c2) * r + c1) * r + c0) / (((((((d7 * r + d6) * r + d5) * r + d4) * r + d3) * r + d2) * r + d1) * r + one);
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

    private boolean qsmall(double x, double eps, double sum) {

        boolean temp = false;

        if ((sum < 1.0e-20) || (x < (eps * sum))) {
            temp = true;
        }

        return temp;

    }

    public double noncenf(double f, double dfn,
            double dfd, double pnonc) {

        double sum = 0.00,
                eps = 1.0e-4,
                dsum, dummy, prod, xx, yy, adn, aup, b, betdn, betup,
                centwt, dnterm, upterm, xmult, xnonc;

        int i, icent, ierr;

        double t1, t2, t3, t4, t5, t6;

        if (pnonc < .0001) {
            sum = sigf(f, dfn, dfd);
            return sum;
        }

        xnonc = pnonc / 2.0;
        icent = (int) (xnonc);
        if (icent == 0) {
            icent = 1;
        }

        t1 = icent + 1;
        centwt = Math.exp(-xnonc + (icent * Math.log(xnonc)) - algama(t1));

        prod = dfn * f;
        dsum = dfd + prod;
        yy = dfd / dsum;

        if (yy > 0.50) {
            xx = prod / dsum;
            yy = 1.0 - xx;
        } else {
            xx = 1.0 - yy;
        }

        t2 = (dfn / 2.0) + icent;
        t3 = dfd / 2.0;

        betdn = cdbeta(xx, t2, t3, 12, 200);

        adn = (dfn / 2.0) + icent;
        aup = adn;

        b = dfd / 2.0;
        betup = betdn;
        sum = centwt * betdn;

        xmult = centwt;
        i = icent;
        t4 = adn + b;
        t5 = adn + 1.0;

        dnterm = Math.exp(algama(t4)
                - algama(t5)
                - algama(b)
                + (adn * Math.log(xx))
                + (b * Math.log(yy)));

        while (!qsmall((xmult * betdn), eps, sum) || (!(i <= 0))) {

            xmult = xmult * (i / xnonc);
            i = i - 1;
            adn = adn - 1;
            dnterm = dnterm * ((adn + 1) / ((adn + b) * xx));
            betdn = betdn + dnterm;
            sum = sum + (xmult * betdn);

        }

        i = icent + 1;

        xmult = centwt;
        if ((aup - 1.0 + b) == 0) {
            upterm = Math.exp(-algama(aup)
                    - algama(b)
                    + ((aup - 1.0) * Math.log(xx))
                    + (b * Math.log(yy)));
        } else {
            t6 = aup - 1.0 + b;
            upterm = Math.exp(algama(t6)
                    - algama(aup)
                    - algama(b)
                    + ((aup - 1.0) * Math.log(xx))
                    + (b * Math.log(yy)));

        }

        while (!qsmall((xmult * betup), eps, sum)) {

            xmult = xmult * (xnonc / i);
            i = i + 1;
            aup = aup + 1.0;

            upterm = upterm * ((aup + b - 2.0) * xx / (aup - 1.0));

            betup = betup - upterm;

            sum = sum + (xmult * betup);

        }

        return sum;

    } /* noncenf */
    
    public double critstat(char distribution, double dfa,
            double dfb, double p) {

        // this procedure will return the critical/tabled values of
        // common statistics: z, t, f, and chi-square
        int decimals = 8;

        double check = 1.0, temp = 0.0, increment = 100.0;
        int i = 0;

        for (i = 1; i <= decimals; i++) {

            increment = increment * 0.10;
            check = 1.0;

            while (check >= p) {

                temp = temp + increment;

                switch (distribution) {

                    case 'z':
                        check = pnorm(temp, 1);
                        break;
                    case 't':
                        check = sigf(Math.pow(temp, 2.0), 1, dfb);
                        break;
                    case 'f':
                        check = sigf(temp, dfa, dfb);
                        break;
                    case 'c':
                        check = sigchi(temp, dfb);
                        break;
                }
            }

            temp = temp - increment;

        }

        for (i = 1; i <= (decimals - 2); i++) {
            temp = temp * 10.0;
        }
        temp = Math.round(temp);
        for (i = 1; i <= (decimals - 2); i++) {
            temp = temp / 10.0;
        }

        return temp;

    }

}
