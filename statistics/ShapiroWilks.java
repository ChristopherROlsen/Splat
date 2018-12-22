/************************************************************
 *                        ShapiroWilks                      *
 *                          08/31/17                        *
 *                            00:00                         *
 ***********************************************************/
package statistics;

import splat.UniVarStats;
import baseClasses.Distributions;
import genericClasses.DataUtilities;
import java.util.ArrayList;
import java.util.Arrays;

public class ShapiroWilks {

    double W = 0.0;
    double PW = 0.0;

    public double getW() {
        double temp = W;
        return temp;
    }

    public double getPW() {
        double temp = PW;
        return PW;
    }

    private double POLY(double[] c, int nord, double x) {

//     Algorithm AS 181.2   Appl. Statist.  (1982) Vol. 31, No. 2
//     Calculates the algebraic polynomial of order nored-1 with
//     array of coefficients c.  Zero order coefficient is c(1)
        double temp = c[0];

        if (nord == 1) {
            return temp;
        }

        double p = x * c[nord - 1];

        if (nord > 2) {

            int n2 = nord - 2;
            int j = n2 + 1;

            for (int i = 1; i <= n2; i++) {
                p = (p + c[j - 1]) * x;
                j = j - 1;
            }
        }

        temp = temp + p;
        return temp;

    } // POLY

    public void SWILK(ArrayList<String> data) {

        int i, j, I, J;

        // initialize data:
        int N = data.size();
        if (N < 3) {
            return;
        }
        double[] X = new double[N];

        for (i = 0; i < N; i++) {
            X[i] = 0.00;
        }

        int n = 0;
        // copy valid data over to temp array
        UniVarStats uniVar = new UniVarStats();
        for (i = 0; i < N; i++) {
            if (DataUtilities.stringIsADouble(data.get(i))) {
                X[n] = DataUtilities.convertStringToDouble(data.get(i));
                n = n + 1;
            }
        }

        N = n;

        // sort from low to high
        Arrays.sort(X, 0, n);

        Distributions findp = new Distributions();

        double[] C1 = {0.0E0, 0.221157E0, -0.147981E0, -0.207119E1,
            0.4434685E1, -0.2706056E1};
        double[] C2 = {0.0E0, 0.42981E-1, -0.293762E0, -0.1752461E1,
            0.5682633E1, -0.3582633E1};
        double[] C3 = {0.5440E0, -0.39978E0, 0.25054E-1, -0.6714E-3};
        double[] C4 = {0.13822E1, -0.77857E0, 0.62767E-1, -0.20322E-2};
        double[] C5 = {-0.15861E1, -0.31082E0, -0.83751E-1, 0.38915E-2};
        double[] C6 = {-0.4803E0, -0.82676E-1, 0.30302E-2};
        double[] C7 = {0.164E0, 0.533E0};
        double[] C8 = {0.1736E0, 0.315E0};
        double[] C9 = {0.256E0, -0.635E-2};
        double[] G = {-0.2273E1, 0.459E0};

        double Z90 = 0.12816E1, Z95 = 0.16449E1, Z99 = 0.23263E1;
        double ZM = 0.17509E1, ZSS = 0.56268E0;
        double BF1 = 0.8378E0, XX90 = 0.556E0, XX95 = 0.622E0;
        double SQRTH = 0.70711E0, QTR = 0.25E0, TH = 0.375E0, SMALL = 1E-19;
        double PI6 = 0.1909859E1, STQR = 0.1047198E1;

        boolean UPPER = true;

        int N2;
        if ((N % 2) == 1) {
            N2 = (N - 1) / 2;
        } else {
            N2 = N / 2;
        }

        double[] A = new double[N2];

        for (i = 0; i < N2; i++) {
            A[i] = 0.0;
        }

        int AN = N;

        // calculates coefficients for the test
        if (N == 3) {
            A[0] = SQRTH;
        } else {

            double AN25 = AN + QTR;
            double SUMM2 = 0.00;

            for (I = 1; I <= N2; I++) {

                A[I - 1] = findp.pnorminv((I - TH) / AN25);

                SUMM2 = SUMM2 + Math.pow(A[I - 1], 2.0);

            }

            SUMM2 = SUMM2 * 2.0;
            double SSUMM2 = Math.sqrt(SUMM2);
            double RSN = 1.0 / Math.sqrt(AN);
            double A1 = POLY(C1, 6, RSN) - A[0] / SSUMM2;

            // Normalize coefficients
            int I1 = 0;
            double A2 = 0.0, FAC = 0.0;

            if (N > 5) {
                I1 = 3;
                A2 = -A[1] / SSUMM2 + POLY(C2, 6, RSN);
                FAC = Math.sqrt((SUMM2 - 2.0 * Math.pow(A[0], 2.0) - 2.0
                        * Math.pow(A[1], 2.0)) / (1.0 - 2.0 * Math.pow(A1, 2.0) - 2.0
                        * Math.pow(A2, 2.0)));
                A[0] = A1;
                A[1] = A2;
            } else {
                I1 = 2;
                FAC = Math.sqrt((SUMM2 - 2.0 * Math.pow(A[0], 2.0))
                        / (1.0 - 2.0 * Math.pow(A1, 2.0)));
                A[0] = A1;
            }

            for (I = I1; I <= N2; I++) {
                A[I - 1] = -A[I - 1] / FAC;
            }

        } // else

        // Check for zero range
        double RANGE = X[N - 1] - X[0];
        if (RANGE < SMALL) {
            return;
        }

        // Check for correct sort order on range - scaled X
        double XX = X[0] / RANGE;
        double SX = XX;
        double SA = -A[0];
        J = N - 1;
        double XI;
        int sign = 0;

        for (I = 2; I <= N; I++) {
            XI = X[I - 1] / RANGE;
            if (XX - XI > SMALL) {
                System.out.println("Whoops");
            }
            SX = SX + XI;
            if ((I - J) < 0) {
                sign = -1;
            } else {
                sign = 1;
            }
            if (I != J) {
                SA = SA + sign * A[Math.min(I, J) - 1];
            }
            XX = XI;
            J = J - 1;
        }

        //  Calculate W statistic as squared correlation
        //  between data and coefficients
        SA = SA / N;
        SX = SX / N;
        double SSA = 0.0, SSX = 0.0, SAX = 0.0, ASA = 0.0, XSX = 0.0;
        J = N;
        for (I = 1; I <= N; I++) {

            if (I != J) {
                if ((I - J) < 0) {
                    sign = -1;
                } else {
                    sign = 1;
                }

                ASA = sign * A[Math.min(I, J) - 1] - SA;
            } else {
                ASA = -SA;
            }

            XSX = X[I - 1] / RANGE - SX;
            SSA = SSA + ASA * ASA;
            SSX = SSX + XSX * XSX;
            SAX = SAX + ASA * XSX;
            J = J - 1;
        }

        double SSASSX = Math.sqrt(SSA * SSX);
        double W1 = (SSASSX - SAX) * (SSASSX + SAX) / (SSA * SSX);

        W = 1.0 - W1;

        if (N == 3) {
            PW = PI6 * (Math.asin(Math.sqrt(W)) - STQR);
            return;
        }

        double Y = Math.log(W1);
        XX = Math.log(AN);
        double M = 0.0;
        double S = 1.0;

        if (N <= 11) {
            double GAMMA = POLY(G, 2, AN);
            if (Y >= GAMMA) {
                PW = SMALL;
                return;
            }
            Y = -Math.log(GAMMA - Y);
            M = POLY(C3, 4, AN);
            S = Math.exp(POLY(C4, 4, AN));
        } else {
            M = POLY(C5, 4, XX);
            S = Math.exp(POLY(C6, 3, XX));
        }

        PW = findp.pnorm((Y - M) / S, 1);
        return;

    } // SWILK
    
}// ShapiroWilks
