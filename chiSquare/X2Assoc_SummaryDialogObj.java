/****************************************************************************
 *                   X2Assoc_SummaryDialogObj                               * 
 *                           05/15/18                                       *
 *                            15:00                                         *
 ***************************************************************************/
package chiSquare;

import smarttextfield.*;

public class X2Assoc_SummaryDialogObj {
    // POJOs
    int nRows, nCols;
    int[][] observedValues;
    
    String xVariable, yVariable;
    String[] xValues, yValues;
    
    public X2Assoc_SummaryDialogObj(SmartTextField xVar,
                                    SmartTextField yVar,
                                    SmartTextField[] strXValues,
                                    SmartTextField[] strYValues,
                                    X2GriddyWiddy x2GriddyWiddy) {
    
        nRows = strYValues.length;
        nCols = strXValues.length;
        
        xVariable = xVar.getText();
        yVariable = yVar.getText();
        
        yValues = new String[nRows];
        xValues =  new String[nCols];
        
        observedValues = new int[nRows][nCols];
        
        for (int ithRow = 0; ithRow < nRows; ithRow++) {
            yValues[ithRow] = strYValues[ithRow].getText();      
        }
        
        for (int ithCol = 0; ithCol < nCols; ithCol++) {
            xValues[ithCol] = strXValues[ithCol].getText();      
        }
        
        for (int ithRow = 0; ithRow < nRows; ithRow++) {
            for (int jthCol = 0; jthCol < nCols; jthCol++) {
                observedValues[ithRow][jthCol] =
                        x2GriddyWiddy.getGriddyWiddy_IJ(ithRow, jthCol);
            }
        }
    }
    
    public int getNRows() { return nRows; }
    public int getNCols() { return nCols; }
    
    public String getTopLabel() { return xVariable; }
    public String getLeftLabel() { return yVariable; }
    
    public String[] getYValues() { return yValues; }
    public String[] getXValues() { return xValues; }
    
    public int getObsVal_IJ(int ithRow, int jthCol) {
        return observedValues[ithRow][jthCol];
    }
    
}
