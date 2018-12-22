/**************************************************
 *             QuantitativeDataVariable           *
 *                    11/25/18                    *
 *                      12:00                     *
 *************************************************/

/********************************************************************
 *  To do:                                                          *
 *   (1) resolve whether there should be more constructors, one     *
 *       array of doubles, one ArrayList of strings.  AL strings is *
 *       used by Transformations_Calculations                       * 
 *   (2) Do I really need a getDoubles and a getDoublesAsSorted??   *
 *   (3) How should the UCDO and QDV interact?  Should a QDV be     *
 *       constructed without an UCDO or vice versa? Should one      *
 *       ever exist without the other?   When and why?              *
 *       Should they ever be created without existing data?         *
 *       Seems like the answer should be 'no.'  Data should be      *
 *       created elsewhere and UCDO/QDV created around the data(?)  *
 *******************************************************************/

package genericClasses;

import java.util.ArrayList;
import matrixProcedures.*;

public class QuantitativeDataVariable {
    // POJOs
    private boolean doublesFound = false;
    private int nDataPointsOriginal, nLegalDataPoints;
    private double[] dbl_legalData;
    ArrayList<Double> alDouble_theLegalCases; // Need to be Double??    
    private String dataLabel, dataUnits;    
    ArrayList<String> alString_theLegalCases, alString_AllTheCases; 
    
    // My classes
    UnivariateContinDataObj ucdo;
    ColumnOfData colOfData;
    QuantitativeDataVariable qdv;
    

    public QuantitativeDataVariable () { 
        dataLabel = "";
    }
    
    public QuantitativeDataVariable (String inDataLabel) {
        dataLabel = inDataLabel;
    }
    
    public QuantitativeDataVariable (String inDataLabel, String inDataUnits) {
        dataLabel = inDataLabel;
        dataUnits = inDataUnits;
    }

    
    public QuantitativeDataVariable(ColumnOfData colOfData) {
        // colOfData.toString();
        this.colOfData = new ColumnOfData();
        this.colOfData = colOfData;
        dataLabel = colOfData.getVarLabel();
        dataUnits = colOfData.getVarUnits();
        nDataPointsOriginal = colOfData.getColumnSize();
        alString_AllTheCases = colOfData.getTheCases();
        alString_theLegalCases = new ArrayList();
        alDouble_theLegalCases = new ArrayList();
        stripTheNonDoubles();
        ucdo = new UnivariateContinDataObj(this);
    }
    
    // Incoming Matrix must be m x 1, all legal
    public QuantitativeDataVariable(String strIncomingLabel, Matrix inMatrix) {
        dataLabel = strIncomingLabel;
        dataUnits = "";
        alString_AllTheCases = new ArrayList();
        alDouble_theLegalCases = new ArrayList();
        alString_theLegalCases = new ArrayList();
        nLegalDataPoints = inMatrix.getRowDimension();
        nDataPointsOriginal = nLegalDataPoints;
        for (int ithRow = 0; ithRow < nLegalDataPoints; ithRow++) {
            double daIthRowValue = inMatrix.get(ithRow, 0);
            String strOfDouble = daIthRowValue + "";
            alString_AllTheCases.add(strOfDouble);
        }
        stripTheNonDoubles();
        colOfData = new ColumnOfData(dataLabel, alString_theLegalCases);
        ucdo = new UnivariateContinDataObj(this);
    }
    
    // This constructor is used by the VerticalBoxPlotPlatform.
    public QuantitativeDataVariable (String strIncomingLabel, double[] dbl_IncomingData)  {
        dataLabel = strIncomingLabel;
        dataUnits = "";
        nDataPointsOriginal = dbl_IncomingData.length;
        alString_AllTheCases = new ArrayList<>();
        alString_theLegalCases = new ArrayList();
        alDouble_theLegalCases = new ArrayList();
        for (int ith = 0; ith < nDataPointsOriginal; ith++) {
            alString_AllTheCases.add(String.valueOf(dbl_IncomingData[ith]));
        }
        
        stripTheNonDoubles();
        ucdo = new UnivariateContinDataObj(this);
    }
 
    public QuantitativeDataVariable (String inLabel, ArrayList<String> inDataStrings)  {
        dataLabel = inLabel;
        dataUnits = "";
        nDataPointsOriginal = inDataStrings.size();
        alString_AllTheCases = inDataStrings;
        alString_theLegalCases = new ArrayList();
        alDouble_theLegalCases = new ArrayList();
        stripTheNonDoubles();
        ucdo = new UnivariateContinDataObj(this);
    }
    
    public QuantitativeDataVariable (String inLabel, String[] inDataStrings)  {
        dataLabel = inLabel;
        dataUnits = "";
        nDataPointsOriginal = inDataStrings.length;
        alString_AllTheCases = new ArrayList();
        for (int ithString = 0; ithString < nDataPointsOriginal; ithString++) {
            alString_AllTheCases.add(inDataStrings[ithString]);
        }
        alString_theLegalCases = new ArrayList();
        alDouble_theLegalCases = new ArrayList();
        stripTheNonDoubles();
        ucdo = new UnivariateContinDataObj(this);
    }

    public ColumnOfData getColumnOfData() { return colOfData; }
    public String getDataLabel() { return dataLabel; }
    public String getDataUnits() { return dataUnits; }
    public int getOriginalN () {return nDataPointsOriginal;}
    public int getLegalN() {return ucdo.getLegalN(); }
    public double getMinValue() { return ucdo.getMinValue(); }
    public double getMaxValue() { return ucdo.getMaxValue(); } 
    public double getTheSum() { return ucdo.getTheSum(); }  
    public double getTheSumX2() { return ucdo.getTheSumX2(); } 
    public double getTheSS() { return ucdo.getTheSS();}
    public double getTheMean() { return ucdo.getTheMean(); }
    public double getTheMedian() { return ucdo.getTheMedian(); }
    
    public double getTheTrimmedMean( double trimProp) {
        return ucdo.getTheTrimmedMean(trimProp);
    }
    
    public double getTheStandDev() {return ucdo.getTheStandDev(); }
    public double getTheVariance() {return ucdo.getTheVariance(); } 
    public double getTheSkew() { return ucdo.getTheSkew(); }
    public double getTheKurtosis() { return ucdo.getTheKurtosis(); }
    public double getStandErrMean() {return ucdo.getStandErrMean(); }    
    public double getTheMarginOfError() {return ucdo.getTheMarginOfErr(0.95);}
    public double getTheIQR() {return ucdo.getTheIQR(); } 
    public double[] getTheDeviations() {return ucdo.getTheDeviations(); }
    public double[] getTheDataSorted() { return ucdo.getTheDataSorted(); }
    
    // ithDataPoint is the value, not the index
    public double getIthPercentile(double ithDataPoint) {
        return ucdo.getPercentileRank(ithDataPoint);
    }
      
    public ArrayList<String> getOriginalDataAs_alString() { return alString_AllTheCases; }
    public int get_nDataPointsLegal() { return nLegalDataPoints; }  
    public double[] getLegalDataAsStrings() { return dbl_legalData; } 
    
    public double[] getLegalDataAsDoubles() { 
        dbl_legalData = new double[nLegalDataPoints];
        for (int ithPoint = 0; ithPoint < nLegalDataPoints; ithPoint++) {
            dbl_legalData[ithPoint] = alDouble_theLegalCases.get(ithPoint);
        }
        return dbl_legalData; }
    
    public void doMedianBasedCalculations() { ucdo.doMedianBasedCalculations(); }
    public void doMeanBasedCalculations() { ucdo.doMeanBasedCalculations(); }
    
    public String getIthDataPtAsString(int ith) {
        String ithCase;
        if (ith < alString_AllTheCases.size()) {
            ithCase = alString_AllTheCases.get(ith);
        }
        else {
            ithCase = "*";
        }
        return ithCase; 
    }
    
    public UnivariateContinDataObj getUCDO() { return ucdo; };

    public double getIthDataPtAsDouble(int ith) {
        double tempDouble = dbl_legalData[ith]; 
        return tempDouble; 
    }
    
    private void stripTheNonDoubles() {
        doublesFound = false;
        for (int ithCase = 0; ithCase < nDataPointsOriginal; ithCase++) {
            String tempString = alString_AllTheCases.get(ithCase);
            if (DataUtilities.stringIsADouble(tempString) == true) {
                doublesFound = true;
                alString_theLegalCases.add(tempString);
                alDouble_theLegalCases.add(Double.parseDouble(tempString));
            }  
            else
            {
                //if (tempString.equals("*")) {
                //    System.out.println("147 udmModel, Non-double found   ithCase / tempString = " + ithCase + " / " + tempString);
                //}
            }
        }         
        if (doublesFound) {
            nLegalDataPoints = alDouble_theLegalCases.size(); 
            dbl_legalData = new double[nLegalDataPoints];
            for (int ithPoint = 0; ithPoint < nLegalDataPoints; ithPoint++) {
                dbl_legalData[ithPoint] = Double.parseDouble(alString_theLegalCases.get(ithPoint));
            }
            ucdo = new UnivariateContinDataObj(this);           
        } else {
            System.out.println("qdv 195, Ack!!!  This variable is not numeric!!!");
        }        
    }
    
    public double[] getADStats() {
        return ucdo.getAndersonDarling();
    }
    
    public ArrayList<String> getAllTheCasesAsALStrings() { return alString_AllTheCases; }
    public ArrayList<Double> getLegalCases_AsALDoubles() { return alDouble_theLegalCases; }
    
    public ArrayList<String> getLegalCases_AsALStrings() { return alString_theLegalCases; }  
    
    public String getTheDataLabel() {return dataLabel; }

    public String toString() {
        // Force an error here to see the stacktrace
        //int x = 5;
        //int y = 0;
        //int z = x / y;
        System.out.println ("\n\nQuantitativeDataVariable LEGAL data points toString(): " + dataLabel);
        System.out.println("n = " + nDataPointsOriginal);
        System.out.println("Legal n = " + getLegalN());
        String theTo = "\n" + dataLabel;
        for (int ithDataPoint = 0; ithDataPoint < nLegalDataPoints; ithDataPoint++)  {
            System.out.println("ith = " + ithDataPoint + ", " + dbl_legalData[ithDataPoint]);
        }
        return "\n";
    } 

}