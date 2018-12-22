/****************************************************************************
 *                      X2Assoc_PrintStats                                  *
 *                           06/02/18                                       *
 *                             00:00                                        *
 ***************************************************************************/

package chiSquare;

import genericClasses.PrintTextReport_View;
import java.util.ArrayList;


public class X2Assoc_PrintStats extends PrintTextReport_View {
    // POJOs
    double chiSquare, pValue, totalN; 
    double[] rowProportion, colProportion, cumRowProps, cumColProps, rowTotal,
             colTotal, cumMarginalRowProps;   
    double[][] observedValues, expectedValues, chiSquareContribution,
               resids, standResids, proportions,
               cumProps;
    
    int nRows, nCols, df, maxSpaces, spacesNeeded, spacesAvailableForTitle,
        spacesAvailableInTotal, nCellsBelow5;

    String topVariable, leftVariable, titleString, subtitleString,
           categoryAxisLabel, verticalAxisLabel, thisLine;
    String[] topLabels, leftLabels, titleStrings;

    
    // My classes

    MyX2StringUtilities myX2StringUtils;
    X2Assoc_Model association_Model; 
    X2Assoc_Dashboard association_Dashboard;

    
    public  X2Assoc_PrintStats(X2Assoc_Model association_Model, 
            X2Assoc_Dashboard association_Dashboard,  
            double placeHoriz, double placeVert,
            double withThisWidth, double withThisHeight) {

        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        this.association_Model = association_Model;
        this.association_Dashboard = association_Dashboard;
        stringsToPrint = new ArrayList<>();
        myX2StringUtils = new MyX2StringUtilities();           
        categoryAxisLabel = " "; verticalAxisLabel = " ";
        topVariable = association_Model.getTopVariable();
        leftVariable = association_Model.getLeftVariable();
        titleString = leftVariable + " and " + topVariable;    
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
        strTitleText = "Elementary chi square statistics";
    }
    
    public void constructPrintLines() {
        int leftPadSpaces;
        String tempString, leftFill;
        stringsToPrint.add("\n\n");
        titleString = "Association between: " + titleString; //  Center this!
        spacesAvailableInTotal = 23 + 12 * nCols;  //  12 spaces for each col
        leftPadSpaces = 23;
        leftFill = myX2StringUtils.getStringOfNSpaces(leftPadSpaces);
        spacesAvailableForTitle = spacesAvailableInTotal - leftPadSpaces;
        String centeredTitle = myX2StringUtils.centerTextInString(titleString, spacesAvailableForTitle) + "\n";
        stringsToPrint.add(leftFill + centeredTitle + "\n");
        leftPadSpaces = 23;
        leftFill = myX2StringUtils.getStringOfNSpaces(leftPadSpaces);
        
        tempString = "\n" + leftFill;
        for (int col = 0; col < nCols; col++) {  
            String smallTop = myX2StringUtils.leftMostChars(topLabels[col], 8);
            tempString += myX2StringUtils.centerTextInString(smallTop, 12);   
        }
        
        stringsToPrint.add(tempString);

        for (int iRow = 0; iRow < nRows; iRow ++)  {
            tempString = "\n\n"; 
            stringsToPrint.add(tempString);
            tempString = myX2StringUtils.centerTextInString(leftLabels[iRow], 15) + "\n";
            stringsToPrint.add(tempString);
            
            //  Observed values
            tempString = "";
            tempString += String.format(myX2StringUtils.leftMostChars("\nObserved values", 20));
            for (int col = 0; col < nCols; col++) {
                tempString += String.format(" %11.2f", observedValues[iRow][col]);
            }
            stringsToPrint.add(tempString);       
            
            //  Expected values
            tempString = "";
            tempString += String.format(myX2StringUtils.leftMostChars("\nExpected values", 20));
            for (int col = 0; col < nCols; col++) {
                tempString += String.format(" %11.2f", expectedValues[iRow][col]);
            }
            stringsToPrint.add(tempString);
            
            //  Expected values
            tempString = "";
            tempString += String.format(myX2StringUtils.leftMostChars("\nContrib to X2", 20));            
            for (int col = 0; col < nCols; col++) {
                tempString += String.format(" %11.2f", chiSquareContribution[iRow][col]);
            }
            
            stringsToPrint.add(tempString);              
            tempString = "";
            tempString += String.format(myX2StringUtils.leftMostChars("\nStand. Resid (z)", 20));
            for (int col = 0; col < nCols; col++) {
                tempString += String.format(" %11.2f", standResids[iRow][col]);
            }
            stringsToPrint.add(tempString);  
            addNBlankLines(2);
        }   //  Next iRow
        
        df = (nRows - 1)*(nCols - 1);
        addNBlankLines(1);
        stringsToPrint.add(String.format("               Chi Square = %7.3f", chiSquare));
        addNBlankLines(1);
        stringsToPrint.add(String.format("                       df =  %2d", df));  
        addNBlankLines(1);
        stringsToPrint.add(String.format("                  p-Value = %7.3f", pValue));
        
        nCellsBelow5 = association_Model.getNumberOfCellsBelow5();

        if (nCellsBelow5 > 0) {
            addNBlankLines(2);
            stringsToPrint.add(String.format("********************       Warning!    ********************"));  
            addNBlankLines(2);
            
            if (nCellsBelow5 == 1) {
                stringsToPrint.add(String.format("*** There is one cell with an expected value less than 5 ***"));
            } 
            
            if (nCellsBelow5 > 1) {
                stringsToPrint.add(String.format("*** There are %d cells with expected values less than 5 ***", nCellsBelow5));
            }            
            
        }
        addNBlankLines(4);
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
