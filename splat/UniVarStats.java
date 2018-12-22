/************************************************************
 *                        UniVarStats                       *
 *                          08/29/17                        *
 *                            21:00                         *
 ***********************************************************/

// Called by Explore, LevensTest, MultReg, TTest_RM, LineCharts

package splat;

import genericClasses.DataUtilities;
import java.util.ArrayList;
import java.util.Arrays;

public class UniVarStats {

    // Formal definition of missing data:
    public final double missingData = -9916543.46157441;

    public double Sum(ArrayList<String> data) {

        double tempSum = 0.0;            

        return tempSum;

    } // Sum

    public double SumX2(ArrayList<String> data) {

        double tempSumX2 = 0.0;

        for (String tempData : data) {
            if (DataUtilities.stringIsADouble(tempData)) {
                double tempNum = Double.parseDouble(tempData);
                tempSumX2 = tempSumX2 + (tempNum * tempNum);
            }
        } // loop            

        return tempSumX2;

    } // Sum of Squared Xs

    public double Mean(ArrayList<String> data) {

        double tempMean = 0.0;

        return tempMean;

    } // Mean

    public double Min(ArrayList<String> data) {

        double tempMin = 9E15;
        
        return tempMin;

    } // Min

    public double Max(ArrayList<String> data) {

        double tempMax = -9E15;
        
        return tempMax;

    } // Max

    public double SS(ArrayList<String> data) {

        //  Sum of squares of DEVIATIONS from the mean
        double tempSS = 0.0;

        return tempSS;

    } // SS

    public double Variance(ArrayList<String> data) { // Sample Variance

        double tempVar = 0.0;

        return tempVar;

    } // Variance

    public double StdDev(ArrayList<String> data) { // Sample SD

        double tempSD = 0.0;

        return tempSD;

    } // Mean
    
    public double StdErr(ArrayList<String> data) {
        
        double tempSE = 0.0;
        
        // tempSE = StdDev(data) / Math.sqrt(validN(data));
        
        return tempSE;
    
    } // StdErr
    
    public double CV(ArrayList<String> data) {
        
        double tempCV = 0.0;

        return tempCV;
    
    } // CV
    
    public boolean anyMissData (ArrayList<String> data) {
        
        boolean result = false;
        for (String tempData: data) {
            if (!DataUtilities.stringIsADouble(tempData)) {
                result = true;
            }
        }
        
        return result;
        
    } // anyMissData
    
    private double q1 = 0.0, q2 = 0.0, q3 = 0.0;

    // make sure to call computeQuants before calling these:
    
    public double getQ1() {
        return q1;
    }

    public double getQ2() {
        return q2;
    }

    public double getQ3() {
        return q3;
    }
   

    public void computeQuants (ArrayList<String> data) {
        
        q1 = missingData;
        q2 = missingData;
        q3 = missingData;

        double[] temp = new double[data.size()];
        boolean found;

        // initialize
        int n = 0, i;
        for (i = 0; i < data.size(); i++) {
            temp[i] = 0.00;
        }

/*
        // copy valid data over to temp array
        for (i = 0; i < data.size(); i++) {
            if (DataUtilities.stringIsADouble(data.get(i))) {
                temp[n] = getDouble(data.get(i));
                n = n + 1;
            }
        }
*/
        if (n < 3) {
            return;
        }

        // sort from low to high
        Arrays.sort(temp, 0, n);

        // figure out values of quarters (q2 = median)
        int mid, qtr;
        if ((n % 2) == 1) {  //odd number of scores
            mid = ((n + 1) / 2) - 1;
            q2 = temp[mid];
            qtr = ((mid + 1) / 2) - 1;
            q1 = temp[qtr];
            q3 = temp[n - qtr - 1];
        } else {  // even number of scores
            mid = n / 2;
            q2 = (temp[mid - 1] + temp[mid]) / 2.00;
            qtr = ((mid + 1) / 2) - 1;
            q1 = temp[qtr];
            q3 = temp[n - qtr - 1];
        }

    } // compute quantiles
 
    //Routines for paired data:

    public boolean validPair(ArrayList<String>[] data, int pair) {
        
        if ((DataUtilities.stringIsADouble(data[0].get(pair))) && (DataUtilities.stringIsADouble(data[1].get(pair)))) {
            return true;
        } else {
            return false;
        }
        
    } // validPair
/*
    public double diffScore(ArrayList<String>[] data, int pair) {
        
        //you must call isValid to check this pair before calling this:
        double tempDiff = getDouble(data[0].get(pair)) -
                getDouble(data[1].get(pair));
        
        return tempDiff;
        
    } // diffScore 
*/
} // Univariate Stats