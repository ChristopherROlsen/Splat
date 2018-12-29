/****************************************************************************
 *                         X2GOF_Model                                      *
 *                           05/15/18                                       *
 *                            15:00                                         *
 ***************************************************************************/

package chiSquare;

import dialogs.X2GOF_DataByHandDialog;
import dialogs.X2GOF_DataFromFileDialog;
import utilityClasses.StringUtilities;
import dataObjects.ColumnOfData;
import dataObjects.UnivariateCategoricalDataObj;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import probabilityDistributions.*;

public class X2GOF_Model {
    //POJOs
    boolean summaryObjectReturned, dataIsFromFile, cleanReturn, hasLeftTailStat, 
            hasRightTailStat;;
    
    int df, nCategories, nStringsInOriginalData, nUniques, nCellsBelow_5;
    int[] observedCounts, observedCountsFromFile;
    
    double chiSquare, leftTailStat, rightTailStat, pValue, observedTotal, 
           propTotal, expectedTotal, standResidsTotal, contribTotal, cohens_W;
    double[] expectedProps, residuals, expectedValues, chiSquareContribution, 
             resids, standResids, expectedProportions;
    
    String dataSource, theGOFVariable, returnStatus, sourceString, tempString;
    String[] observedValuesFromFile, categoriesAsStrings; 
    ObservableList<String> categoryLabels;
    static ArrayList<String> stringsToPrint;
    
    // My objects
    ChiSquareDistribution chi2Dist;
    ColumnOfData colOfData;
    StringUtilities myStringUtilities;
    UnivariateCategoricalDataObj uCatDataObj;
    X2GOF_Dashboard gofDashboard;
    X2GOF_DataByHandDialog x2GOF_Dialog;
    X2GOF_DataFromFileDialog x2GOF_DataDialog;
    X2GOF_DataDialogObj x2GOF_DataDialogObj;  
    X2GOF_ObsExpView chiSqGOFView;
    X2GOF_PrintStats gof_PrintStats;
    X2GOF_Procedure x2GOF_procedure;

/*******************************************************************************
 *          Define the dialog objects and their return info objects            *
 ******************************************************************************/
    
    public X2GOF_Model() {  }
    
    public String doX2FromFile(X2GOF_Procedure x2GOF_procedure) {
        dataIsFromFile = true;
        colOfData  = new ColumnOfData(x2GOF_procedure.getColumnOfData());
        theGOFVariable = colOfData.getVarLabel();
        uCatDataObj = new UnivariateCategoricalDataObj(colOfData); 
        nCategories = uCatDataObj.getNUniques();
        df = nCategories - 1;
        chi2Dist = new ChiSquareDistribution(df);
        makeTheArrays();
        observedCountsFromFile = new int[nCategories];
        observedValuesFromFile = new String[nCategories];

        observedCountsFromFile = uCatDataObj.getObservedCounts();
        observedValuesFromFile = uCatDataObj.getUniqueValues();
        createGOFDialog_File();
        if (returnStatus.equals("OK")) {
            doChiSqGOFCalculations();
        } else {
            returnStatus = "Cancel";
        }
        
        return returnStatus;
    }  
    
    public String doX2FromTable() {
        dataIsFromFile = false;
        returnStatus = "";
        createGOFDialog();
        if (returnStatus.equals("OK")) {
            doChiSqGOFCalculations();
        }
        return returnStatus;
    }
    
    private void makeTheArrays() {
        categoriesAsStrings = new String[nCategories]; 
        observedCounts = new int[nCategories];
        expectedValues = new double[nCategories];
        expectedProportions = new double[nCategories];
        resids = new double[nCategories];
        chiSquareContribution = new double[nCategories];
        standResids = new double[nCategories];        
    }
    
/******************************************************************************
*          Instantiate the dialog objects and their return info objects       *
******************************************************************************/ 
    // GOF information added manually
    private String createGOFDialog() {
        x2GOF_Dialog = new X2GOF_DataByHandDialog(this);
        x2GOF_Dialog.constructDialogGuts();
        x2GOF_Dialog.showAndWait();
        returnStatus = x2GOF_Dialog.getReturnStatus();
        if (returnStatus.equals("OK")) {
           x2GOF_DataDialogObj = new X2GOF_DataDialogObj();
           x2GOF_DataDialogObj = x2GOF_Dialog.getTheDialogObject();
           theGOFVariable = x2GOF_DataDialogObj.getGOFVariable();
           nCategories = x2GOF_DataDialogObj.getNCategories();
           df = nCategories - 1;
           chi2Dist = new ChiSquareDistribution(df);
           makeTheArrays();
           // Fill some of arrays
           categoriesAsStrings = x2GOF_DataDialogObj.getTheGOFCategories();
           categoryLabels = FXCollections.observableArrayList(categoriesAsStrings);
           observedCounts = x2GOF_DataDialogObj.getObservedValues();
           expectedProportions = x2GOF_DataDialogObj.getExpectedProps();
       }
       return returnStatus; 
    }
    
    //// GOF information provided via UnivCatDataObj
    private void createGOFDialog_File() {   
        x2GOF_DataDialog = new X2GOF_DataFromFileDialog(this);
        x2GOF_DataDialog.constructDialogGuts();
        x2GOF_DataDialog.showAndWait();
        returnStatus = x2GOF_DataDialog.getReturnStatus();
        if (returnStatus.equals("OK")) {
           x2GOF_DataDialogObj = new X2GOF_DataDialogObj();
           x2GOF_DataDialogObj = x2GOF_DataDialog.getTheDialogObject();
           nCategories = x2GOF_DataDialogObj.getNCategories();
           df = nCategories - 1;
           chi2Dist = new ChiSquareDistribution(df);
           makeTheArrays();
           // Fill some of arrays
           categoriesAsStrings = x2GOF_DataDialogObj.getTheGOFCategories();
           categoryLabels = FXCollections.observableArrayList(categoriesAsStrings);
           observedCounts = x2GOF_DataDialogObj.getObservedValues();
           expectedProportions = x2GOF_DataDialogObj.getExpectedProps();
       }
    }
    
    private void doChiSqGOFCalculations() {
        observedTotal = 0.0;  expectedTotal = 0.0; propTotal = 0.0;
        chiSquare = 0.0; standResidsTotal = 0.0; contribTotal = 0.0;
        nCellsBelow_5 = 0;
        
        for (int col = 0; col < nCategories; col++) {  
            observedTotal += observedCounts[col]; 
            propTotal += expectedProportions[col]; 
        }    
        for (int col = 0; col < nCategories; col++) {           
            expectedValues[col] = observedTotal * expectedProportions[col];
            if (expectedValues[col] < 5.0) {
                nCellsBelow_5++;
            }
            expectedTotal += expectedValues[col];
            resids[col] = observedCounts[col] - expectedValues[col];
            chiSquareContribution[col] = resids[col] * resids[col] / expectedValues[col];
            contribTotal += chiSquareContribution[col];
            
            if (resids[col] >= 0) {
                standResids[col] = Math.sqrt(chiSquareContribution[col]);
            } else {
               standResids[col] = -Math.sqrt(chiSquareContribution[col]); 
            }
            standResidsTotal += standResids[col];
            chiSquare += chiSquareContribution[col];
        }  
        cohens_W = Math.sqrt(chiSquare / observedTotal);
        pValue = chi2Dist.getRightTailArea(chiSquare);
    }       

/*******************************************************************************
*                          Ancillary methods                                   *
* @return 
*******************************************************************************/  
    
    // public X2GOF_Model getX2GOF_Model() {return this; }
    public String getGOFVariable() {return theGOFVariable;}
    public String[] getCategoriesAsStrings() {return categoriesAsStrings; }
    public double getIthObservedCount(int ith) { return observedCounts[ith]; }
        
    public int getNCategories() { return nCategories; }
    public int getDF() { return df; } 
    
    public String getIthCategory(int ith) { return categoryLabels.get(ith); }
    
    public ObservableList<String> getCategoryLabels () {return categoryLabels; }
    
    public int[] getObservedCounts () { return observedCounts; }
    public int getNCellsBelow5 () { return nCellsBelow_5; }
    
    public int[] getObservedCountsFromFile() { return observedCountsFromFile; }
    public String[] getObservedValuesFromFile() { return observedValuesFromFile; }    
    public double[] getExpectedValues () { return expectedValues; }
    public double[] getExpectedProportions() {return expectedProportions; }
    public double[] getX2Contributions() {return chiSquareContribution; }
    public double[] getResids () { return resids; }
    public double[] getStandResids () { return standResids; }   
    
    public double getX2() { return chiSquare; }
    public double getObservedTotal() { return observedTotal; }
    public double getPropTotal() { return propTotal; }
    public double getExpectedTotal() { return expectedTotal; }
    public double getContribTotal() { return contribTotal; }
    public double getPValue() {return pValue; }
    public double getCohensW() {return cohens_W; }
    
    public String getReturnStatus() { return returnStatus; }
    
}


