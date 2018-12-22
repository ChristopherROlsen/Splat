/************************************************************
 *                     Logistic_Procedure                   *
 *                          09/02/18                        *
 *                            12:00                         *
 ***********************************************************/
package regressionLogistic;

import dialogs.Logistic_Dialog;
import genericClasses.BivariateContinDataObj;
import genericClasses.ColumnOfData;
import java.util.ArrayList;
import genericClasses.QuantitativeDataVariable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import matrixProcedures.Matrix;
import splat.Splat_DataManager;

public class Logistic_Procedure {
    // POJOs
    int nPoints, nUniques;    
    int[] nSuccesses, nFailures, nTotal, finalObserved, finalOnes;
    ArrayList<Integer> countUniques;
    
    double[] finalCategories, dbl_xValue, props_OF_Xs;
    double[][] rawData, initialUniques;    
    String returnStatus, horizAxisLabel;
    String[] strDistinctTableLabels, dataXYLabels;
    String explanVar, defNSuccesses, defNTotal;
    String definitionOfSuccess;
    
    ArrayList<String> uniques, strXVar, strYVar, xStrings, yStrings;

    // My classes
    BivariateContinDataObj bivContin;
    private Logistic_View logRegView;
    private Logistic_Model logRegModel;
    private Logistic_Dashboard logRegDashboard;
    Matrix X, Y;
    QuantitativeDataVariable qdv_XVariable, qdv_YVariable;
    Splat_DataManager myData;
    
    public Logistic_Procedure(Splat_DataManager myData) {
        this.myData = myData;       
    }  
        
    public String doTheProcedure() {
        try {
            Logistic_Dialog logistic_Dialog = new Logistic_Dialog(myData, "QUANTITATIVE");
            returnStatus = logistic_Dialog.getReturnStatus();
            if (!returnStatus.equals("Ok")) {
                return returnStatus;
            }

            ArrayList<ColumnOfData> data = logistic_Dialog.getData();
            qdv_XVariable = new QuantitativeDataVariable(data.get(0));
            qdv_YVariable = new QuantitativeDataVariable(data.get(1)); 
            definitionOfSuccess = logistic_Dialog.getSuccessDef();

            // Transfer to previously coded variables
            dataXYLabels = new String[2];
            explanVar = qdv_XVariable.getDataLabel();
            dataXYLabels[0] = explanVar;
            dataXYLabels[1] = "Probability of " + definitionOfSuccess; 

            strDistinctTableLabels = new String[4];
            strDistinctTableLabels[0] = explanVar;
            strDistinctTableLabels[1] = "# " + definitionOfSuccess; 
            strDistinctTableLabels[2] = "N values"; 
            strDistinctTableLabels[3] = "Prop of " + definitionOfSuccess; 

            bivContin = new BivariateContinDataObj(data);

            xStrings = bivContin.getLegalXsAs_AL_OfStrings();
            yStrings = bivContin.getLegalYsAs_AL_OfStrings();



            nPoints = qdv_XVariable.getLegalN();
            rawData = new double[nPoints][2]; 
            // Keep original data in Matrix for future use (need nPoints for plot)
            X = new Matrix(nPoints, 1);
            Y = new Matrix(nPoints, 1);
            for (int ithPoint = 0; ithPoint < nPoints; ithPoint++) {
                X.set(ithPoint, 0, qdv_XVariable.getIthDataPtAsDouble(ithPoint));
                Y.set(ithPoint, 0, qdv_YVariable.getIthDataPtAsDouble(ithPoint));
            }

            setUpTheData();

            logRegModel = new Logistic_Model(this);

            logRegDashboard = new Logistic_Dashboard(this, logRegModel);
            logRegDashboard.populateTheBackGround();
            logRegDashboard.putEmAllUp();
            logRegDashboard.showAndWait();
            returnStatus = logRegDashboard.getReturnStatus();
            returnStatus = logistic_Dialog.getReturnStatus();

            return returnStatus;
        
        }
        catch (Exception ex) {
            // ex.printStackTrace();  ?? p466 Liang
            System.out.println("\n" + ex.getMessage());
            System.out.println("\n" + ex.toString());
            System.out.println("\nTrace Info Obtained from getStatckTrace");
            StackTraceElement[] traceElements = ex.getStackTrace();
            for (int i = 0; i < traceElements.length; i++) {
                System.out.print("method " + traceElements[i].getMethodName());
                System.out.print("(" + traceElements[i].getClassName() + ":");
                System.out.print(traceElements[i].getLineNumber() + ")");
            }            
        }
        
        return returnStatus;
    }
    
    public void setUpTheData() {

        strXVar = new ArrayList<>();
        strYVar = new ArrayList<>();
        
        strXVar = qdv_XVariable.getColumnOfData().getTheCases();
        strYVar = qdv_YVariable.getColumnOfData().getTheCases();

        sortStrings();
        doObservedCounts();

        dbl_xValue = new double[nUniques];
        nSuccesses = new int[nUniques];
        nTotal = new int[nUniques];
        nFailures = new int[nUniques];
        props_OF_Xs = new double[nUniques];
        
        initialUniques = new double[nUniques][3];
        
        for (int ithUnique = 0; ithUnique < nUniques; ithUnique++) {
            initialUniques[ithUnique][0] = finalCategories[ithUnique];
            initialUniques[ithUnique][1] = finalOnes[ithUnique];
            initialUniques[ithUnique][2] = finalObserved[ithUnique];
        }
        
        for (int ithDistinct = 0; ithDistinct < nUniques; ithDistinct++) {
            dbl_xValue[ithDistinct] = initialUniques[ithDistinct][0];
            nSuccesses[ithDistinct] = (int)initialUniques[ithDistinct][1];
            nTotal[ithDistinct] = (int)initialUniques[ithDistinct][2];
            nFailures[ithDistinct] = nTotal[ithDistinct] - nSuccesses[ithDistinct];
            props_OF_Xs[ithDistinct] = initialUniques[ithDistinct][1] / initialUniques[ithDistinct][2];
        }
        
        double tempDouble;
        int tempInt;
        for (int i = 0; i < nUniques - 1; i++) {
            for (int j = i + 1; j < nUniques; j++) {
                if (dbl_xValue[i] > dbl_xValue[j]) { 
                    
                    tempDouble = dbl_xValue[i];
                    dbl_xValue[i] = dbl_xValue[j];
                    dbl_xValue[j] = tempDouble;
                    
                    tempInt = nSuccesses[i];
                    nSuccesses[i] = nSuccesses[j];
                    nSuccesses[j] = tempInt;
                    
                    tempInt = nTotal[i];
                    nTotal[i] = nTotal[j];
                    nTotal[j] = tempInt;
                    
                    tempInt = nFailures[i];
                    nFailures[i] = nFailures[j];
                    nFailures[j] = tempInt;
                    
                    tempDouble = props_OF_Xs[i];
                    props_OF_Xs[i] = props_OF_Xs[j];
                    props_OF_Xs[j] = tempDouble;
                }
            }
        }
  
    }
    
    public double[][] getDataMatrix() {return initialUniques; }
    
    public String getXAxisLabel()  {return horizAxisLabel; } 
    
    private void sortStrings() {    
        for (int i = 0; i < nPoints - 1; i++) {
            for (int j = i + 1; j < nPoints; j++) {
                double tempI = Double.parseDouble(strXVar.get(i));
                double tempJ = Double.parseDouble(strXVar.get(j));
                
                if (tempI > tempJ) { 
                    
                    String tempStringVal_I0 = strXVar.get(i);
                    String tempStringval_I1 = strYVar.get(i);
                    
                    strXVar.set(i, strXVar.get(j));
                    strYVar.set(i, strYVar.get(j));
                    
                    strXVar.set(j, tempStringVal_I0);
                    strYVar.set(j, tempStringval_I1);                    
                }
            }
        }
        
        // Can I get this in one call from the bivContinDataObject???
        for (int ithPoint = 0; ithPoint < nPoints; ithPoint++) {
            rawData[ithPoint][0] = Double.parseDouble(strXVar.get(ithPoint));
            rawData[ithPoint][1] = Double.parseDouble(strYVar.get(ithPoint));
        }
    }
    
    private void doObservedCounts() {
        Map<Double, Integer> mapOfStrings = new HashMap<Double, Integer>();
        for (int c = 0; c < nPoints; c++) {
            if (mapOfStrings.containsKey(rawData[c][0])) {
                int value = mapOfStrings.get(rawData[c][0]);
                mapOfStrings.put(rawData[c][0], value + 1);
            } else {
                mapOfStrings.put(rawData[c][0], 1);
            }
        }
        
        nUniques = mapOfStrings.size();
        Set<Map.Entry<Double, Integer>> entrySet = mapOfStrings.entrySet();
        finalCategories = new double[nUniques];
        finalObserved = new int[nUniques];
        finalOnes = new int[nUniques];
        int index = 0;
        
        for (Map.Entry<Double, Integer> entry: entrySet) {
            finalCategories[index] = entry.getKey();
            finalObserved[index] = entry.getValue();
            index++;
        }
        
        for (int iUnique = 0; iUnique < nUniques; iUnique++) {
            finalOnes[iUnique] = 0;
            for (int jthPoint = 0; jthPoint < nPoints; jthPoint++) {
                if ((rawData[jthPoint][0] == finalCategories[iUnique])
                        && (rawData[jthPoint][1] == 1.0)) {
                    finalOnes[iUnique]++;
                }
            }
        }       
    }
    
    public Matrix getMatrix_X() { return X; }
    public Matrix getMatrix_Y() { return Y; }

    public QuantitativeDataVariable getQdvXVariable() { return qdv_XVariable; } 
    
    public String[] getCountTable() { return strDistinctTableLabels; }
    
    public String getDefOfSuccess() { return definitionOfSuccess; }
    
    public double[] getXValues() { return dbl_xValue; }
    
    public int getNPoints() { return nPoints; }
    public int getNUniques() { return nUniques; }
    
    public double[] getPropsOfXValues() { return props_OF_Xs; }
    
    public int[] getNSuccesses() { return nSuccesses; }
    
    public int[] getNFailures() { return nFailures; }
        
    public int[] getNTotal() { return nTotal; }
}
