/****************************************************************************
 *                         X2Assoc_Model                                    *
 *                           05/15/18                                       *
 *                             12:00                                        *
 ***************************************************************************/

package chiSquare;

import dialogs.X2Assoc_SummaryDialog;
import javafx.scene.text.Text;
import dataObjects.ColumnOfData;
import java.util.ArrayList;
import probabilityDistributions.* ;
import dataObjects.UnivariateCategoricalDataObj;


public class X2Assoc_Model {
    // POJOs
    boolean cleanReturnToProcedure, cleanReturnFromSummaryDialog, dataIsFromFile;
    
    int nLegalValues, nRows, nCols, nCells, nCellsBelow5, df;
    
    double chiSquare, pValue, cramersV, dblNLegalValues;    
    double[] rowProportions, columnProportions, cumulativeRowProps, 
             cumulativeColumnProps, rowTotals, columnTotals, cumMarginalRowProps;    
    double[][] observedValues, expectedValues, chiSquareContributions,
               residuals, standardizedResiduals, observedProportion,
               cumulativeProportions;

    String strTopVariable, strLeftVariable, strTitle, strSubTitle, tempString, 
           strCategoryAxisLabel, strVerticalAxisLabel, strThisLine, assocType;
    String[] strTopValues, strLeftValues, strTitles;

//  My classes   
    ArrayList<ColumnOfData> data;
    ChiSquareDistribution chi2Dist;
    UnivariateCategoricalDataObj catUCDOTop, catUCDOLeft;
    X2Assoc_MosaicPlotView mosaicPlot;
    X2Assoc_Procedure x2Assoc_Procedure;
    X2Assoc_SegBarChartView segmentedBarChart;
    X2Assoc_SummaryDialog x2Assoc_SummaryDialog; 
    X2Assoc_SummaryDialogObj x2Assoc_SummaryDialogObj;

    // POJOs / FX
    Text txtThis;
    
    public  X2Assoc_Model(X2Assoc_Procedure x2Assoc_Procedure, String assocType) { 
        this.x2Assoc_Procedure = x2Assoc_Procedure;
        this.assocType = assocType;
    }    
                
    public boolean doModelFromFile() {
        data = new ArrayList();
        data = x2Assoc_Procedure.getData(); 

        nLegalValues = data.get(0).getColumnSize();
        dblNLegalValues = nLegalValues;
        strTopVariable = data.get(0).getVarLabel();
        strLeftVariable =  data.get(1).getVarLabel();

        catUCDOTop = new UnivariateCategoricalDataObj(data.get(0));
        catUCDOLeft = new UnivariateCategoricalDataObj(data.get(1));

        nRows = catUCDOLeft.getNUniques();
        nCols = catUCDOTop.getNUniques();

        constructNecessaryArrays();

        strTopValues = catUCDOTop.getUniqueValues();
        strLeftValues = catUCDOLeft.getUniqueValues();

        for (int iRow = 0; iRow < nRows; iRow++) {
            String tempLeftValue = strLeftValues[iRow];
            for (int jCol = 0; jCol < nCols; jCol++) {
                String tempTopValue = strTopValues[jCol];
                for (int ithPoint = 0; ithPoint < nLegalValues; ithPoint++) {
                    String tempTopDataPt = catUCDOTop.getIthValue(ithPoint);
                    String tempLeftDataPt = catUCDOLeft.getIthValue(ithPoint);
                    if ((tempTopValue.equals(tempTopDataPt)
                        && (strLeftValues[iRow].equals(tempLeftDataPt)))) {
                            observedValues[iRow][jCol]++;
                    }
                }
            }
        }
        cleanReturnToProcedure = true;
        return cleanReturnToProcedure;
    } 
    
    public boolean doModelNotFromFile() {
        x2Assoc_SummaryDialog = new X2Assoc_SummaryDialog(this);
        x2Assoc_SummaryDialog.showAndWait();
        if (cleanReturnFromSummaryDialog == true) {
            nRows = x2Assoc_SummaryDialogObj.getNRows();
            nCols = x2Assoc_SummaryDialogObj.getNCols();

            nLegalValues = 0;
            df = (nRows - 1) * (nCols - 1);
            constructNecessaryArrays();

            strTopVariable = x2Assoc_SummaryDialogObj.getTopLabel();
            strLeftVariable = x2Assoc_SummaryDialogObj.getLeftLabel();

            strTopValues = x2Assoc_SummaryDialogObj.getXValues();
            strLeftValues = x2Assoc_SummaryDialogObj.getYValues();

            for (int iRow = 0; iRow < nRows; iRow++) {
                for (int jCol = 0; jCol < nCols; jCol++) {
                   observedValues[iRow][jCol] = x2Assoc_SummaryDialogObj.getObsVal_IJ(iRow, jCol);
                   nLegalValues += observedValues[iRow][jCol];
                }
            }
            dblNLegalValues = nLegalValues;
            cleanReturnToProcedure = true;
        } else {
            cleanReturnToProcedure = false;
        }  
        return cleanReturnToProcedure;
    }
        
    private void constructNecessaryArrays() {
        observedValues = new double[nRows][nCols];
        residuals = new double[nRows][nCols];
        standardizedResiduals = new double[nRows][nCols];
        expectedValues = new double[nRows][nCols];
        observedProportion = new double[nRows][nCols];
        chiSquareContributions = new double[nRows][nCols];
        rowTotals = new double[nRows];
        rowProportions = new double[nRows];
        cumulativeRowProps = new double[nRows + 1];    //  0 at the top
        cumMarginalRowProps = new double[nRows + 1];
        columnTotals = new double[nCols];
        columnProportions = new double[nCols];
        cumulativeColumnProps = new double[nCols + 1];    //  0 at the left
        cumulativeProportions = new double[nRows + 1][nCols + 1];    //  Internal cum props
        strLeftValues = new String[nRows]; 
        strTopValues = new String[nCols];
    }
    
/******************************************************************************
*          Instantiate the dialog objects and their return info objects       *
     * @param toThis
******************************************************************************/ 
    
    public void setCleanReturnFromSummaryDialog(boolean toThis) {
        cleanReturnFromSummaryDialog = toThis;
    }


    public void closeTheAssocDialog(boolean cleanReturn) {
        if (cleanReturnFromSummaryDialog == true) {
            cleanReturn = true;
            x2Assoc_SummaryDialogObj = x2Assoc_SummaryDialog.getTheDialogObject();
        }
        x2Assoc_SummaryDialog.close();
    }

        
    public void doChiSqAnalysisCalculations() {         
        nCells = nRows * nCols;
        nCellsBelow5 = 0;
        // Construct the table counts and proportions
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                observedProportion[row][col] = observedValues[row][col] / dblNLegalValues;
            }
        }

        // calculate row marginals, print at side
        for (int row = 0; row < nRows; row++) {
            rowTotals[row] = 0;
            for (int col = 0; col < nCols; col++) {
                rowTotals[row] += observedValues[row][col];
            }
        }
        
        // calculate column marginals, print at bottom
        for (int col = 0; col < nCols; col++) {
            columnTotals[col] = 0;
            for (int row = 0; row < nRows; row++) {
                columnTotals[col] += observedValues[row][col];
            }
        }   
            
        //  calculate row proportions
        for (int row = 0; row < nRows; row++) {
            rowProportions[row] = rowTotals[row] / dblNLegalValues; 
        }
        
        //  calculate col proportions
        for (int col = 0; col < nCols; col++) {
            columnProportions[col] = columnTotals[col] / dblNLegalValues;
        }

        //  Calculate proportions, expectedValues, and resids
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                expectedValues[row][col] = rowTotals[row] * columnTotals[col] / dblNLegalValues ;
                if (expectedValues[row][col] < 5)
                    nCellsBelow5++;
                residuals[row][col] = observedValues[row][col] - expectedValues[row][col];
            }
        }  
        
        //  Chi square and contributions
        chiSquare = 0.0;
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                chiSquareContributions[row][col] = residuals[row][col] * residuals[row][col] / expectedValues[row][col];
                chiSquare += chiSquareContributions[row][col];
            }
        } 
        //  Standardized residuals
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                double temp = (1.0 - rowProportions[row]) * (1.0 - columnProportions[col]);
                standardizedResiduals[row][col] = residuals[row][col] / Math.sqrt(expectedValues[row][col] * temp);
            }
        }
                 
        cumulativeColumnProps[0] = 0;
        for (int col = 1; col <= nCols; col++) {
            cumulativeColumnProps[col] = cumulativeColumnProps[col - 1] + columnProportions[col - 1];
        } 
       
        cumulativeRowProps[nRows] = 0;
        for (int row = 1; row <= nRows; row++) {
            cumulativeRowProps[nRows - row] = cumulativeRowProps[nRows - row + 1] + rowProportions[nRows - row];
        }

        cumMarginalRowProps[nRows] = 0.0;
        for (int row = nRows - 1; row >= 0; row--) {
            cumMarginalRowProps[row] = cumMarginalRowProps[row + 1] + rowProportions[row];
        }    

        cumMarginalRowProps[0] = 1.0;      

        for (int col = 0; col < nCols; col++) {
            cumulativeProportions[nRows][nCols] = 0.0;
            for (int row = nRows - 1; row >= 0; row--) {
                cumulativeProportions[row][col] = cumulativeProportions[row + 1][col] + observedProportion[row][col];
            }   //  end row         
        } 

        // Cramer's V
        double temp = Math.min(nRows - 1, nCols - 1);
        cramersV = Math.sqrt(chiSquare / (dblNLegalValues * temp)); 
        df = (nRows - 1)*(nCols - 1);
        chi2Dist = new ChiSquareDistribution(df);
        pValue = chi2Dist.getRightTailArea(chiSquare);
    }

    public int getDF() { return df; }
    
    public double getChiSquare()  {return chiSquare; }
    public double getCramersV() {return cramersV; }
    public double getPValue() { return pValue; }
    public double getTotalN() {return nLegalValues; }
    
    public int getNumberOfRows() { return nRows; }
    public int getNumberOfColumns() { return nCols; }
    
    public int getNumberOfCells() { return nCells; }
    public int getNumberOfCellsBelow5() { return nCellsBelow5; }
    
    public String getTopVariable() {return strTopVariable; }
    public String getLeftVariable() {return strLeftVariable; } 

    public String[] getTopLabels() {return strTopValues; }
    public String[] getLeftLabels() {return strLeftValues; }  
    
    public double[] getRowProportions() {return rowProportions; }
    public double[] getColumnProportions() {return columnProportions; } 
    public double[] getCumRowProps() { return cumulativeRowProps; }
    public double[] getCumColProps() { return cumulativeColumnProps; } 
    public double[] getRowTotals() { return rowTotals; }
    public double[] getColumnTotals() {return columnTotals; }
    public double[] getCumMarginalRowProps() { return cumMarginalRowProps; }
    
    public double[][] getCellCumProps() { return cumulativeProportions; }
    public double[][] getObservedValues() {return observedValues; }
    public double[][] getExpectedValues() {return expectedValues; }
    public double[][] getX2Contributions() {return chiSquareContributions; }
    public double[][] getStandardizedResiduals() {return standardizedResiduals; }
    public double[][] getProportions() {return observedProportion; }
    
    
    public void setCleanReturnToProcedure(boolean toThis) { cleanReturnToProcedure = toThis; }
    
    public void setLabelForCategoryAxis(String toThisLabel) {
        strCategoryAxisLabel = toThisLabel;
    }
    
    public String getLabelForCategoryAxis() { return strCategoryAxisLabel; }
    
    public void setLabelForVerticalAxis(String toThisLabel) {
        strVerticalAxisLabel = toThisLabel;
    }
    
    public String getLabelForVerticalAxis() { return strCategoryAxisLabel; }
    
    public String getAssociationType() { return assocType; }
    
    // ********************   Debugging ***************************
    // private void makeALine() { /* System.out.println("\n\n"); */}

}
