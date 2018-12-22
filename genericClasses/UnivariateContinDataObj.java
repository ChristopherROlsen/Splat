/**************************************************
 *             UnivariateContinDataObj            *
 *                    11/25/18                    *
 *                     12:00                      *
 *************************************************/
package genericClasses;

import java.util.Arrays;
import probabilityDistributions.*;

public class UnivariateContinDataObj {
    // POJOs
    boolean meanBasedDone, medianBasedDone, qqCalcDone, binsCreated,
            hasForcedIntegerMajorTicks, hasForcedLowEndOfScale, 
            hasForcedHighEndOfScale, andersonDarlingCalculated;
        
    int nLegalDataPoints, nBins, nMajorIntervals, rankLoWhisker, rankHiWhisker;
    int[] frequency, cutOffRanks;
    
    double minimum, q1, q2, q3, maximum, iqr, range, marginOfError, sumDev2,
           sumOfX, sumOfX2, cv, sumX, mean, variance, standDev, skew, kurtosis,
           sumOfSquares, seMean, minMajorTick, maxMajorTick, majorTickRange, 
           binWidth, intervalsPerMajorTick, maximumFreq, forcedLowEndOfScaleIs, 
           forcedHighEndOfScaleIs;
    double[] sortedArray, middleInterval, deviations, unsortedArray, adStats;
    
    String dataLabel;
    
    // My classes
    QuantitativeDataVariable parent_QDV;
    TDistribution tDistribution;    //  For margin of error    
        
    public UnivariateContinDataObj ()  { };    

    public UnivariateContinDataObj(QuantitativeDataVariable qdv_Model) {
        parent_QDV = qdv_Model;
        dataLabel = parent_QDV.getTheDataLabel();
        nLegalDataPoints = parent_QDV.get_nDataPointsLegal();
        init_UCDO();
        unsortedArray = new double[nLegalDataPoints];
        sortedArray = new double[nLegalDataPoints];
        unsortedArray = parent_QDV.getLegalDataAsDoubles();
        
        // Only copying so far; not sorted
        System.arraycopy(unsortedArray, 0, sortedArray, 0, nLegalDataPoints); 
        doAllStatistics();        
    }

    
    private void init_UCDO()  {
        andersonDarlingCalculated = false;
        meanBasedDone = false;
        medianBasedDone = false;
        qqCalcDone = false; 
        binsCreated = false;
        cutOffRanks = new int[2];
        
        intervalsPerMajorTick = 10.0;   // This value controls the bin size
                                        // Will be settable eventually        
    }
       
    private void doAllStatistics(){
        doMedianBasedCalculations();
        doMeanBasedCalculations(); 
    }

    public void doMeanBasedCalculations() {
        calculateFirstFourMoments();
    }
    
    public void doMedianBasedCalculations() {
        double temp1, temp2, lowOutlierCutOff, highOutlierCutOff;       
        if (medianBasedDone == true)
            return;
        Arrays.sort(sortedArray);   // Now is sorted
        medianBasedDone = true;
        cutOffRanks = new int[2];
                
        if( nLegalDataPoints >= 4) {        
            int mod4 = nLegalDataPoints % 4;
            switch (mod4)
            {
                case 0: 
                    temp1 = sortedArray[nLegalDataPoints / 4 - 1];
                    temp2 = sortedArray[nLegalDataPoints / 4];                
                    q1 =(temp1 + temp2) / 2.0;

                    temp1 = sortedArray[nLegalDataPoints / 2 - 1];
                    temp2 = sortedArray[nLegalDataPoints / 2];
                    q2 = (temp1 + temp2) / 2.0;

                    temp1 = sortedArray[3 * nLegalDataPoints / 4 - 1];
                    temp2 = sortedArray[3 * nLegalDataPoints / 4]; 
                    q3 = (temp1 + temp2) / 2.0;   

                    break;

                case 1:
                    temp1 = sortedArray[nLegalDataPoints / 4 - 1];
                    temp2 = sortedArray[nLegalDataPoints / 4];                
                    q1 =(temp1 + temp2) / 2.0;

                    q2 = sortedArray[nLegalDataPoints / 2];

                    temp1 = sortedArray[3 * nLegalDataPoints / 4 ];
                    temp2 = sortedArray[3 * nLegalDataPoints / 4 + 1];  
                    q3 = (temp1 + temp2) / 2.0; 

                    break;

                case 2:
                    q1 = sortedArray[nLegalDataPoints / 4];

                    temp1 = sortedArray[nLegalDataPoints / 2 - 1];
                    temp2 = sortedArray[nLegalDataPoints / 2];
                    q2 = (temp1 + temp2) / 2.0;

                    q3 = sortedArray[3 * nLegalDataPoints / 4];                
                   break; 

                case 3:

                    q1 = sortedArray[nLegalDataPoints / 4];
                    q2 = sortedArray[nLegalDataPoints / 2];
                    q3 = sortedArray[3 * nLegalDataPoints / 4]; 
                   break;

                default:
                    break;
                    
            }   //  end switch  
        } // end if nDataPoints >= 4
        else
        {
            switch (nLegalDataPoints)
            {
                case 1:               
                    q1 = sortedArray[0];
                    q2 = sortedArray[0];
                    q3 = sortedArray[0];
                                                            
                    break;

                case 2:
                    q1 = sortedArray[0];
                    q2 = (sortedArray[0] + sortedArray[1]) / 2.0;
                    q3 = sortedArray[1];               
                   break; 

                case 3:

                    q1 = sortedArray[0];
                    q2 = sortedArray[1];
                    q3 = sortedArray[2];
                   break;

                default:
                    break;

            }   //  end switch  
        }
        minimum = sortedArray[0];
        maximum = sortedArray[nLegalDataPoints - 1];
        
        range = maximum - minimum;
        iqr = q3 - q1;
        
        lowOutlierCutOff = q1 - 1.5 * iqr;
        highOutlierCutOff = q3 + 1.5 * iqr;
        
        //  Seek out outliers
        rankLoWhisker = -1; 
        rankHiWhisker = -1;
        
        for (int ith_Rank = 0; ith_Rank < nLegalDataPoints; ith_Rank++){
            if (sortedArray[ith_Rank] < lowOutlierCutOff)
                rankLoWhisker = ith_Rank + 1;
            if (sortedArray[ith_Rank] <= highOutlierCutOff)
                rankHiWhisker = ith_Rank;
        }
        
        cutOffRanks[0] = rankLoWhisker;
        cutOffRanks[1] = rankHiWhisker;
        medianBasedDone = true;
    }   // doMedianBased()
    
    private void calculateFirstFourMoments() {   //  Any reason for kurtosis??
        
        if (meanBasedDone == true)
            return;

        meanBasedDone = true;
        sumX = 0.0;
        sumDev2 = 0.0;
        double sumDev3 = 0.0;
        double sumDev4 = 0.0;
        
        sumOfX = 0.0;
        sumOfX2 = 0.0;
        
        double[] deviation = new double[nLegalDataPoints];
        
        for (int ith_Data_Point = 0; ith_Data_Point < nLegalDataPoints; ith_Data_Point++){
            double temp = sortedArray[ith_Data_Point];
            sumX += temp;
            sumOfX += temp;
            sumOfX2 += (temp * temp);
        }
        mean = sumX / nLegalDataPoints;
        for (int ith_Data_Point = 0; ith_Data_Point < nLegalDataPoints; ith_Data_Point++){
            deviation[ith_Data_Point] = sortedArray[ith_Data_Point] - mean;
            sumDev2 = sumDev2 + deviation[ith_Data_Point] * deviation[ith_Data_Point];
        }
        
        sumOfSquares = sumDev2;
        variance = sumDev2 / (nLegalDataPoints - 1);
        standDev = Math.sqrt(variance);
        
        if (mean != 0.0) 
            cv = standDev / mean;
        else 
            cv = 0.0;   //  Error flag
                    
        seMean = standDev / Math.sqrt(nLegalDataPoints);
        
        /******************************************************************
        *  Adjusted Fisher-Pearson standardized moment coefficient        *
        *  Doane, D. P., & Seward, L. E.  Measuring Skewness: A Forgotten *
        *  Statistic?  Journal of Statistics Education, 19(2), 2011       *
        ******************************************************************/
        
        for (int ith_Data_Point = 0; ith_Data_Point < nLegalDataPoints; ith_Data_Point++){
            double temp1 = deviation[ith_Data_Point]/standDev;
            sumDev3 = sumDev3 + temp1 * temp1 * temp1;
            sumDev4 = sumDev3 + temp1 * temp1 * temp1 * temp1;    
        }
        
        skew = nLegalDataPoints * sumDev3 / ((nLegalDataPoints - 1) * (nLegalDataPoints - 2));
        kurtosis = sumDev4 / (nLegalDataPoints * variance * variance);  
    } 
    
    // 100 x fraction at or below x
    public double getPercentileRank(double x) {
        double rank = 0.;
        // Find rank of last data point less than or equal to x
        for (int ithDataPoint = 0; ithDataPoint < nLegalDataPoints; ithDataPoint ++) {
            if (sortedArray[ithDataPoint] > x) 
                break;
            
            rank = ithDataPoint;
        }
        double pcTile = 100. * (rank + 1.0)/ nLegalDataPoints;
        return pcTile;
    }
    
    public double[] getTheDeviations() {
        deviations = new double[nLegalDataPoints];
        for (int ith = 0; ith < nLegalDataPoints; ith++) {
            deviations[ith] = sortedArray[ith] - mean;
        }
        return deviations;
    }
    
    /******************************************************************************************************************
     *                                   Anderson-Darling Statistic                                                   *
     *    Algorithm from D'Agostino, R. B., * Stephens, M. M. (1986). Goodness-of-Fit-Techniques. Marcel Dekker.  NY. *
     *                                          p101ff                                                                *
     *     Also (and for variable names) see:                                                                         *
     *        https://www.spcforexcel.com/knowledge/basic-statistics/anderson-darling-test-for-normality              *
     *****************************************************************************************************************/
    
    public double[] getAndersonDarling() {
        
        // Need to be sure data are sorted
        if (medianBasedDone == false){
            doMedianBasedCalculations();
        }
    
        if (andersonDarlingCalculated == false) {
            calculateAndersonDarling();
            andersonDarlingCalculated = true;
        }
        return adStats;
    }
    
    private void calculateAndersonDarling() {
        double ad, adStar, pValue;
        adStats = new double[3];
        double[] z2c = new double[nLegalDataPoints];
        double[] fOfXi = new double[nLegalDataPoints];
        //double[] oneMinusFOfXi = new double[nLegalDataPoints];
        double[] oneMinusFOfXni1 = new double[nLegalDataPoints];
        
        StandardNormal standNorm = new StandardNormal();
        
        for (int i = 0; i < nLegalDataPoints; i++) {
            z2c[i] = (sortedArray[i] - mean) / standDev;
            fOfXi[i] = standNorm.getLeftTailArea(z2c[i]);
        }
        
        for (int i = 0; i < nLegalDataPoints; i++) {
            oneMinusFOfXni1[i] = 1.0 - fOfXi[nLegalDataPoints - i - 1];
        }        
         
        double sum = 0.0;
         for (int i = 0; i < nLegalDataPoints; i++) {
            double log1 = Math.log(fOfXi[i]);
            double log2 = Math.log(oneMinusFOfXni1[i]);
            sum = sum + (2.0 * i + 1) * (log1 + log2);
        }   
         
        double doubleN = nLegalDataPoints;
        
        // Anderson-Darling statistic...
        ad = -doubleN - 1.0 / doubleN * sum;
        // ... adjusted for small sample sizes
        adStar = ad * (1.0 + 0.75 / doubleN + 2.25 / (doubleN * doubleN));

        if (adStar >= 0.6) {
            pValue = Math.exp(1.2937 - 5.709 * adStar + 0.0186 * adStar * adStar);
        }
        else if ((0.34 < adStar) && (adStar < 0.6)) {
            pValue = Math.exp(0.9177 - 4.279 * adStar - 1.38 * adStar * adStar);
        }
        else if ((0.2 < adStar) && (adStar< 0.34)) {
            pValue = 1.0 - Math.exp(-8.318 + 42.796 * adStar - 59.938 * adStar * adStar);
        }  
        else {
            pValue = 1.0 - Math.exp(-13.436 + 101.14 * adStar - 223.73 * adStar * adStar);
        }   
        
        adStats[0] = ad;
        adStats[1] = adStar;
        adStats[2] = pValue;
    }
    
    public double getTheTrimmedMean(double trimProp) {
        double sum, trimmedMean;
        sum = 0.0;
        for (int ithLegal = 0; ithLegal < nLegalDataPoints; ithLegal++) {
            double ithProp = (ithLegal + 1.) / (double)nLegalDataPoints;
            if ((ithProp > trimProp) && (ithProp < (1.0 - trimProp))) {
                sum += sortedArray[ithLegal];
            }        
        }
        
        trimmedMean = sum / (double) nLegalDataPoints;
        
        return trimmedMean;
    }

    // *********************************************************************
    //                    Bin stuff begins 
    // *********************************************************************
    
    public void createTheBins( JustAnAxis justAnAxis) { // For histogram, dotPlot, other[?]
        if (binsCreated == true)
            return;
        
        binsCreated = true;

        int nMajorTickPositions = justAnAxis.getNMajorTix();
        minMajorTick = (justAnAxis.getMajorTickMarkPositions().get(0)).doubleValue();
        maxMajorTick = (justAnAxis.getMajorTickMarkPositions().get(nMajorTickPositions - 1)).doubleValue();   

        double majorTikInterval = (justAnAxis.getMajorTickMarkPositions().get(1)).doubleValue()
                                    - (justAnAxis.getMajorTickMarkPositions().get(0)).doubleValue();

        minMajorTick -= majorTikInterval;
        maxMajorTick += majorTikInterval;
        majorTickRange = maxMajorTick - minMajorTick;
     
        binWidth = majorTikInterval / intervalsPerMajorTick; 
        nMajorIntervals = (int)Math.floor(majorTickRange / majorTikInterval + .001) + 1;
        nBins = (int)Math.floor(nMajorIntervals * intervalsPerMajorTick + .001);

        frequency = new int[nBins + 1]; //  nBins+1 in case data is on right end of max interval
  
        double firstBin = minMajorTick;
        double lastBin = minMajorTick + nBins * binWidth;
        double rangeOfBins = lastBin - firstBin;
        double m = nBins / rangeOfBins;
        double b = - nBins * firstBin / rangeOfBins;
        
        for (int ith_Data_Point = 0; ith_Data_Point < nLegalDataPoints; ith_Data_Point++)
        {
            int ith_Bin = (int)Math.floor(m * unsortedArray[ith_Data_Point] + b);
            frequency[ith_Bin] = frequency[ith_Bin] + 1;
        }
        
        maximumFreq = 0.0;
        for (int ith_Bin = 0; ith_Bin < nBins; ith_Bin++)
        {
            if (frequency[ith_Bin] > maximumFreq)
                maximumFreq = frequency[ith_Bin];
        }
        
        maximumFreq += 1.0;  // Safety pad
    }
    
    // The binRange is needed by the View for graphing the bars
    public double[] getBinRange(int whichBin) {
        double[] binRange = new double[2];
        double leftEndOfBin = minMajorTick + whichBin * binWidth;
        double rightEndOfBin = leftEndOfBin + binWidth;
        binRange[0] = leftEndOfBin;
        binRange[1] = rightEndOfBin;
        return binRange;       
    }
       
    public boolean getHasForcedIntTix() { return hasForcedIntegerMajorTicks; }
    
    public void setForceIntTix(boolean forceIntTix) { 
        hasForcedIntegerMajorTicks = forceIntTix;
    }    
    
    public void forceLowScaleEndToBe( double thisLowEnd)  { 
        forcedLowEndOfScaleIs = thisLowEnd;
        hasForcedLowEndOfScale = true;
    }
    
    public void forceHighScaleEndToBe( double thisHighEnd)  { 
        forcedHighEndOfScaleIs = thisHighEnd;
        hasForcedHighEndOfScale = true;  
    }
    
    public void forceScaleLowAndHighEndsToBe(double thisLowEnd, double thisHighEnd)
    {
        forcedLowEndOfScaleIs = thisLowEnd;
        hasForcedLowEndOfScale = true;
        forcedHighEndOfScaleIs = thisHighEnd;
        hasForcedHighEndOfScale = true;
    }
    
    public boolean getHasForcedLowScaleEnd() {return hasForcedLowEndOfScale; }
    public boolean getHasForcedHighScaleEnd() {return hasForcedHighEndOfScale; }
    
    public double getForcedLowScaleEnd() {return forcedLowEndOfScaleIs; }
    public double getForcedHighScaleEnd() {return forcedHighEndOfScaleIs; }
    
    public void setForcedAxisEndsFalse()  { //  i.e. return to unforced state
        hasForcedLowEndOfScale = false;
        hasForcedHighEndOfScale = false;
    }
    
    // *********************************************************************
    //                    Bin stuff ends 
    // *********************************************************************
 
    public int getFrequencyForBin(int theBin) { return frequency[theBin]; }
    
    public int[] getWhiskerEndRanks() { return cutOffRanks; }
    public double getTheSum() { return sumX; }   
    public double getTheSumX2() { return sumOfX2; } 
    public double getTheMean() { return mean; }
    public double getTheStandDev() { return standDev; }
    public double getTheVariance() { return variance; }
    public double getTheSkew() { return skew; }
    public double getTheKurtosis() { return kurtosis; }
    
    public int getLegalN()  {return nLegalDataPoints; }
    public double getTheSS() {return sumOfSquares; }
    
    public double getStandErrMean() {return seMean;}
    
    public double getTheMarginOfErr(double middleOfDist) { 
        if (tDistribution == null)
            tDistribution = new TDistribution();
        int df = nLegalDataPoints - 1;
        tDistribution.set_df_for_t(df);
        middleInterval = new double[2];
        middleInterval = tDistribution.getInverseMiddleArea(middleOfDist);
        double critical_t = middleInterval[1];
        marginOfError = critical_t * seMean;
        return marginOfError; 
    }

    public double[] getTheMoments() {
        double[] moments = new double[4];
        moments[0] = mean;  moments[1] = standDev;
        moments[2] = skew;  moments[3] = kurtosis;
        return moments;
    }
    
    public double getSumOfX() { return sumOfX; }
    public double getSumOfX2() { return sumOfX2; }
    public double getSumSquaresOfDevs() { return sumDev2; }
    
    public double getMaxValue() { return maximum; }
    public double getMinValue() { return minimum; }
    public double getTheIQR() { return iqr; }
        
    public double getIthSortedValue(int rank) {return sortedArray[rank];} 
   
    public UnivariateContinDataObj getTheUnivContinDataObj() {return this; }
    
    public int getNumberOfBins() { return nBins; }
    
    public double getBinWidth() { return binWidth; }
    
    public double getMaxFreq() { return maximumFreq; }
      
    public double[] getTheDataSorted() { return sortedArray; }
    
    public double[] get_5NumberSummary(){
        double[] fiveNum = new double[5];
        fiveNum[0] = minimum;
        fiveNum[1] = q1;
        fiveNum[2] = q2;
        fiveNum[3] = q3;
        fiveNum[4] = maximum;
        return fiveNum;
    }
    
    public double getTheMedian() { return q2; }
    
    public String toString() {
        String daString = "ucdo ToString, mean = " + String.valueOf(mean);
        return daString;
    }
}
