/**************************************************
 *            MultiVariateContinDataObj           *
 *                    07/01/18                    *
 *                      12:00                     *
 *************************************************/
package genericClasses;

import java.util.ArrayList;

public class MultiVariateContinDataObj {

    // POJOs
    int nOriginalDataPoints, nLegalDataPoints, nDataPointsMissing, nDataColumns;
    
    double[] xDataAsDoubles, yDataAsDoubles;

    ArrayList<ArrayList<String>> al_DaData;
    
    String[] dataLabels;
    String[][] xDataAsStrings;
    
    //My classes
    DataUtilities dataUtil;
    MyUtilities myUtil;
    ArrayList<ColumnOfData> dataColumns;
    
    public MultiVariateContinDataObj(ArrayList<ColumnOfData> dataColumns) {
        dataUtil = new DataUtilities();
        myUtil = new MyUtilities();
        this.dataColumns = new ArrayList<>();
        this.dataColumns = dataColumns;
        nDataColumns = dataColumns.size();
        nOriginalDataPoints = dataColumns.get(0).getColumnSize();
        //System.out.println("31 MVCO, nOriginalDataPoints = " + nOriginalDataPoints);
        //System.out.println("32 MVCO, nDataColumns = " + nDataColumns);
        xDataAsStrings = new String[nOriginalDataPoints][nDataColumns];
        dataLabels = new String[nDataColumns];
        
        for (int ithColumn = 0; ithColumn < nDataColumns; ithColumn++) {
            dataLabels[ithColumn] = dataColumns.get(ithColumn).getVarLabel();
            //System.out.println("38 MVCO, dataLabels[ithColumn] = " + dataLabels[ithColumn]);
            ArrayList<String> strFromColumn = new ArrayList<>();
            strFromColumn = dataColumns.get(ithColumn).getTheCases();
            for (int ithRow = 0; ithRow < nOriginalDataPoints; ithRow++) {
                xDataAsStrings[ithRow][ithColumn] = strFromColumn.get(ithRow);
                // System.out.println("43 MVCO, ithRow / ithColumn / xDataAsStrings[ithRow][ithColumn] = " + ithRow + " / " + ithColumn + " / " + xDataAsStrings[ithRow][ithColumn]);
            }
        }
        
        al_DaData = new ArrayList<>();

        for (int ithPoint = 0; ithPoint < nOriginalDataPoints; ithPoint++) {
            // Check row of data for all doubles
            boolean rowOfDataIsOK = true;
            for (int ithInRow = 0; ithInRow < nDataColumns; ithInRow++) {
                //System.out.println("53 MCO, ithPoint / ithInRow = " + ithPoint + " / " + ithInRow);
                String dataToCheck = xDataAsStrings[ithPoint][ithInRow];
                rowOfDataIsOK = myUtil.check4Double(dataToCheck);
            }

            if (rowOfDataIsOK == true) {
                ArrayList<String> tempRow = new ArrayList<>();
                for (int jthInRow = 0; jthInRow < nDataColumns; jthInRow++) {
                    tempRow.add(xDataAsStrings[ithPoint][jthInRow]);
                }
                al_DaData.add(tempRow);
            }
        }
        
        nLegalDataPoints = al_DaData.size();
        
        if (nLegalDataPoints == 0) {
            System.out.println("70 MCDO, nLegalDataPoints = " + nLegalDataPoints);
            System.exit(0);
        }
        
        nDataPointsMissing = nOriginalDataPoints - nLegalDataPoints;  
    }    
    
    public ArrayList<ColumnOfData> getTheDataColumns() { return dataColumns; }
    
    public String[] getDataLabels() { return dataLabels; }
    
    public String getJthLabel(int thisJ) { return dataLabels[thisJ]; }
    
    public int getNDataRows() { return nLegalDataPoints; }
    
    public int getNVariables() { return nDataColumns; }
    
    public String getIthRowJthColAsString(int thisI, int thisJ) {
        return al_DaData.get(thisI).get(thisJ);
    }
    
    public Double getIthRowJthColAsDouble(int thisI, int thisJ) {
        Double daDouble = Double.parseDouble(al_DaData.get(thisI).get(thisJ));
        return daDouble;
    }
}
