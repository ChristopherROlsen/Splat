/****************************************************************************
 *                     X2Assoc_PrintAdvStats                                *
 *                           06/02/18                                       *
 *                             00:00                                        *
 ***************************************************************************/
package chiSquare;

import genericClasses.*;

public class X2Assoc_PrintAdvStats extends PrintTextReport_View {
    
    // POJOs
    double chiSquare, pValue, totalN;    
    double[] rowProportion, colProportion, cumRowProps, cumColProps, rowTotal,
             colTotal, cumMarginalRowProps;    
    double[][] observedValues, expectedValues, chiSquareContribution,
               resids, standResids, proportions,
               cumProps;
    
    int nRows, nCols, df, maxSpaces, spacesNeeded, spacesAvailableForTitle,
        spacesAvailableInTotal, nCellsBelow5, iRow, jCol;

    String topVariable, leftVariable, titleString, subtitleString, tempString, 
           categoryAxisLabel, verticalAxisLabel, thisLine;
    String[] topLabels, leftLabels, titleStrings;
    
    // My classes
    MyX2StringUtilities myX2StringUtils;
    X2Assoc_Dashboard association_Dashboard;   
    X2Assoc_Model association_Model; 

    public  X2Assoc_PrintAdvStats(X2Assoc_Model association_Model, 
            X2Assoc_Dashboard association_Dashboard,  
            double placeHoriz, double placeVert,
            double withThisWidth, double withThisHeight) {
        
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        this.association_Model = association_Model;
        this.association_Dashboard = association_Dashboard;
        myX2StringUtils = new MyX2StringUtilities();      
        categoryAxisLabel = " "; verticalAxisLabel = " ";
        topVariable = association_Model.getTopVariable();
        leftVariable = association_Model.getLeftVariable();
        titleString = leftVariable + " vs. " + topVariable;    
        nRows = association_Model.getNumberOfRows();  // Rows of observed
        nCols = association_Model.getNumberOfColumns();  // Cols of observed
        observedValues = new double[nRows][nCols];
        resids = new double[nRows][nCols];
        standResids = new double[nRows][nCols];
        expectedValues = new double[nRows][nCols];
        proportions = new double[nRows][nCols];
        chiSquareContribution = new double[nRows][nCols];
        rowTotal = new double[nRows];
        rowProportion = new double[nRows];
        cumRowProps = new double[nRows + 1];    //  0 at the top
        cumMarginalRowProps = new double[nRows + 1];
        colTotal = new double[nCols];
        colProportion = new double[nCols];
        cumColProps = new double[nCols + 1];    //  0 at the left
        cumProps = new double[nRows + 1][nCols];    //  Internal cum props
        leftLabels = new String[nRows]; 
        topLabels = new String[nCols];
        topLabels = association_Model.getTopLabels();
        leftLabels = association_Model.getLeftLabels();
        observedValues = association_Model.getObservedValues();
        totalN = association_Model.getTotalN();
        proportions = association_Model.getProportions();
        rowTotal = association_Model.getRowTotals();
        colTotal = association_Model.getColumnTotals();
        rowProportion = association_Model.getRowProportions();
        colProportion = association_Model.getColumnProportions();
        expectedValues = association_Model.getExpectedValues();
        chiSquare = association_Model.getChiSquare();
        pValue = association_Model.getPValue();
        chiSquareContribution = association_Model.getX2Contributions();
        standResids = association_Model.getStandardizedResiduals();
        cumColProps = association_Model.getCumColProps();
        cumRowProps = association_Model.getCumRowProps();
        cumMarginalRowProps = association_Model.getCumMarginalRowProps();
        cumProps = association_Model.getCellCumProps();  
        strTitleText = "Advanced chi square statistics";
    } 
    
  
    public void constructPrintLines() {
        int leftPadSpaces;
        String leftFill;
        addNBlankLines(2);
        titleString = "Association between: " + titleString; //  Center this!
        spacesAvailableInTotal = 23 + 12 * nCols;  //  12 spaces for each col
        
        leftPadSpaces = 23;
        leftFill = myX2StringUtils.getStringOfNSpaces(leftPadSpaces);
        spacesAvailableForTitle = spacesAvailableInTotal - leftPadSpaces;
        String centeredTitle = myX2StringUtils.centerTextInString(titleString, spacesAvailableForTitle);
        stringsToPrint.add(leftFill + centeredTitle);
        addNBlankLines(2);
        leftPadSpaces = 23;
        leftFill = myX2StringUtils.getStringOfNSpaces(leftPadSpaces);
        
        tempString = "\n" + leftFill;
        for (int col = 0; col < nCols; col++) {  
            String smallTop = myX2StringUtils.leftMostChars(topLabels[col], 8);
            tempString += myX2StringUtils.centerTextInString(smallTop, 12);   
        }
        
        stringsToPrint.add(tempString);
        addNBlankLines(1);

        for (iRow = 0; iRow < nRows; iRow ++)  {
            addNBlankLines(2);
            tempString = ""; 
            tempString += myX2StringUtils.centerTextInString(leftLabels[iRow], 15);
            stringsToPrint.add(tempString);
            addNBlankLines(2);
            
            //  Observed values
            tempString = "";
            tempString += String.format(myX2StringUtils.leftMostChars("Observed values", 20));
            for (jCol = 0; jCol < nCols; jCol++) {
                tempString += String.format(" %11.2f", observedValues[iRow][jCol]);
            }
            //  Marginal total for row
            tempString += String.format(" %11.2f", rowTotal[iRow]);
            stringsToPrint.add(tempString);
            //  Percent of total
            addNBlankLines(1);
            tempString = "";
            tempString += String.format(myX2StringUtils.leftMostChars("Percent of Total", 20));
            for (jCol = 0; jCol < nCols; jCol++) {
                tempString += String.format(" %11.2f", 100. * proportions[iRow][jCol]);
            }
 
            tempString += String.format(" %11.2f", 100. * rowProportion[iRow]);
            stringsToPrint.add(tempString);
            
            addNBlankLines(1);
            tempString = "";
            tempString += String.format(myX2StringUtils.leftMostChars("Percent of Row", 20));
            for (int col = 0; col < nCols; col++) {
                tempString += String.format(" %11.2f", 100. * proportions[iRow][col] / rowProportion[iRow]);
            }
            stringsToPrint.add(tempString);  
            
            //  Percent of col
            addNBlankLines(1);
            tempString = "";
            tempString += String.format(myX2StringUtils.leftMostChars("Percent of Column", 20));
            for (int iCol = 0; iCol < nCols; iCol++) {
                tempString += String.format(" %11.2f", 100. * proportions[iRow][iCol] / colProportion[iCol]);
            }
            stringsToPrint.add(tempString);
            //  Marginal proportion for row           
            
            //  Expected values
            addNBlankLines(1);
            tempString = "";
            tempString += String.format(myX2StringUtils.leftMostChars("Expected values", 20));
            for (int col = 0; col < nCols; col++) {
                tempString += String.format(" %11.2f", expectedValues[iRow][col]);
            }
            stringsToPrint.add(tempString);
            
            //  Contribution
            addNBlankLines(1);
            tempString = "";
            tempString += String.format(myX2StringUtils.leftMostChars("Contrib to X2", 20));            
            for (int col = 0; col < nCols; col++) {
                tempString += String.format(" %11.2f", chiSquareContribution[iRow][col]);
            }
            stringsToPrint.add(tempString); 
            addNBlankLines(1);
            tempString = "";
            tempString += String.format(myX2StringUtils.leftMostChars("Stand. Resid (z)", 20));
            for (int col = 0; col < nCols; col++) {
                tempString += String.format(" %11.2f", standResids[iRow][col]);
            }
            stringsToPrint.add(tempString);;         
        }   //  Next iRow
        
        //  Column marginal totals
        addNBlankLines(2);
        tempString = "";
        tempString += String.format(myX2StringUtils.leftMostChars("Total", 20));
        for (int col = 0; col < nCols; col++) {
            tempString += String.format(" %11.2f", colTotal[col]);
        }
        //  Marginal totals for column
        tempString += String.format(" %11.2f", totalN);
        stringsToPrint.add(tempString);

        //  Column marginal proportions
        addNBlankLines(1);
        tempString = "";
        tempString += String.format(myX2StringUtils.leftMostChars("Percent", 20));
        for (jCol = 0; jCol < nCols; jCol++) {
            tempString += String.format(" %11.2f", 100. * colProportion[jCol]);
        }
        stringsToPrint.add(String.format(tempString));
       addNBlankLines(2);
        
        df = (nRows - 1)*(nCols - 1);
        double cramersV = association_Model.getCramersV();
        addNBlankLines(1);
        stringsToPrint.add(String.format("                Chi Square = %7.3f", chiSquare));
        addNBlankLines(1);
        stringsToPrint.add(String.format("                        df =  %2d", df));
        addNBlankLines(1);
        stringsToPrint.add(String.format("                   p-Value = %7.3f", pValue));
        addNBlankLines(1);
        stringsToPrint.add(String.format("                Cramer's V = %7.3f", cramersV));
        
        nCellsBelow5 = association_Model.getNumberOfCellsBelow5();
        if (nCellsBelow5 > 0) {
            addNBlankLines(2);
            stringsToPrint.add(String.format("********************       Warning!    ********************"));       
            addNBlankLines(2);
            stringsToPrint.add(String.format("*** There are %d cells with expected values less than 5 ***", nCellsBelow5));
            addNBlankLines(2);
        }
        addNBlankLines(2);
    } 
    
    public int getDegreesOfFreedom() {return df; }
    public double getChiSquare()  {return chiSquare; }
    
    public int getNumberOfRows() { return nRows; }
    public int getNumberOfColumns() { return nCols; }
    
    public String getTopVariable() {return topVariable; }
    public String getLeftVariable() {return leftVariable; } 

    public String[] getTopLabels() {return topLabels; }
    public String[] getLeftLabels() {return leftLabels; }  
    
    public double[] getCumRowProps() { return cumRowProps; }
    public double[] getCumColProps() { return cumColProps; }
    
    public double[] getCumMarginalRowProps() { return cumMarginalRowProps; }
    
    public double[][] getCumProps() { return cumProps; }
    
    public void setLabelForCategoryAxis(String toThisLabel) {
        categoryAxisLabel = toThisLabel;
    }
    
    public String getLabelForCategoryAxis() { return categoryAxisLabel; }
    
    public void setLabelForVerticalAxis(String toThisLabel) {
        verticalAxisLabel = toThisLabel;
    }
    
    public String getLabelForVerticalAxis() { return categoryAxisLabel; }

    
}
