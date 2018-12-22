/**************************************************
 *                  DataUtilities                 *
 *                    12/22/18                    *
 *                      12:00                     *
 *************************************************/
package genericClasses;

import java.util.ArrayList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;

public class DataUtilities {
    // POJOs
    static boolean doubleIsOK;
    static int n_XValues, n_YValues, nLevels;
    
    static Double[] arrayOfDoubles;
    static ArrayList<Double> arrayListOfDoubles;
    
    static String xVar_Label, yVar_Label, the_XLabel, quantLabel, tempString;
    static ArrayList<String> offendingStrings;

    public DataUtilities ()  { }
    
    public static boolean stringIsADouble(String allegedDouble) {
        double tempDouble;
        boolean doubleQuery = true;
        String tempText = allegedDouble;
        try {
            tempDouble = Double.valueOf(tempText);
        } catch (NumberFormatException e) {
            doubleQuery = false;
        }
        return doubleQuery;
    }
    
    public static boolean stringIsAnInteger(String allegedInteger) {
        int tempInteger;
        boolean integerQuery = true;
        String tempText = allegedInteger;
        try {
            tempInteger = Integer.valueOf(tempText);
        } catch (NumberFormatException e) {
            integerQuery = false;
        }
        return integerQuery;
    }
    
    public static boolean stringIsANonNegativeInteger(String allegedInteger) {
        int tempInteger = 0;
        boolean nonNegIntegerQuery = true;
        String tempText = allegedInteger;
        try {
            tempInteger = Integer.valueOf(tempText);
        } catch (NumberFormatException e) {
            nonNegIntegerQuery = false;
        }
        
        if (tempInteger < 0)
            nonNegIntegerQuery = false;
        return nonNegIntegerQuery;
    }
    
    public static boolean stringIsAPositiveInteger(String allegedInteger) {
        int tempInteger = 0;
        boolean positiveQuery = true;
        String tempText = allegedInteger;
        try {
            tempInteger = Integer.valueOf(tempText);
        } catch (NumberFormatException e) {
            positiveQuery = false;
        }
        
        if (tempInteger <= 0)
            positiveQuery = false;
        return positiveQuery;
    }
    
    public static boolean stringIsAPositiveDouble(String allegedDouble) {
        double tempDouble = 0.0;
        boolean positiveQuery = true;
        String tempText = allegedDouble;
        try {
            tempDouble = Double.valueOf(tempText);
        } catch (NumberFormatException e) {
            positiveQuery = false;
        }
        
        if (tempDouble <= 0)
            positiveQuery = false;
        return positiveQuery;
    }
    
    public static boolean textFieldHasDouble(TextField theTF) {
        return stringIsADouble(theTF.getText());        
    }
    
    public static boolean textFieldHasPositiveDouble(TextField theTF) {
        return stringIsAPositiveDouble(theTF.getText());        
    }

    public static boolean textFieldHasPositiveInteger(TextField theTF) {
        return stringIsAPositiveInteger(theTF.getText());        
    }
    
    public static boolean textFieldHasProportion(TextField theTF) {
        return stringIsAProportion(theTF.getText());        
    }
    
    public static boolean stringIsAProportion(String theString) {
        boolean propIsGood = true;
        double suspectedProp = 0.0;
        try {
            suspectedProp = Double.valueOf(theString);
        }    
        catch (NumberFormatException ex ){ 
            propIsGood = false;
        }
        if ((suspectedProp <= 0.0) || (suspectedProp >= 1.0)) {
            propIsGood = false;
        }
        return propIsGood;
    }    
       
    public static boolean convertArrayListOfStringsToArrayListOfDoubles (ArrayList<String> arrayListOfStrings) {
        boolean allStringsAreDoubles = true;
        int nNonDoubles = 0;
        offendingStrings = new ArrayList<>();
        int nDataPoints = arrayListOfStrings.size();
        arrayOfDoubles = new Double[nDataPoints];
        for (int ith = 0; ith < nDataPoints; ith++) {
            tempString = arrayListOfStrings.get(ith);
            doubleIsOK = stringIsADouble(tempString);
            if (doubleIsOK == true) {
                arrayListOfDoubles.set(ith, Double.valueOf(tempString));
            } else {
                offendingStrings.add(tempString);
                allStringsAreDoubles = false;
            }       
        }
        
        if (allStringsAreDoubles == false) {
            doNonDoubleAlert();
        }
        return allStringsAreDoubles;
    }

    public static boolean convertArrayOfStringsToArrayOfDoubles (String[] arrayOfStrings) {
        boolean allStringsAreDoubles = true;
        int nNonDoubles = 0;
        offendingStrings = new ArrayList<>();
        int nDataPoints = arrayOfStrings.length;
        arrayOfDoubles = new Double[nDataPoints];
        for (int ith = 0; ith < nDataPoints; ith++) {
            tempString = arrayOfStrings[ith];
            doubleIsOK = stringIsADouble(tempString);
            if (doubleIsOK == true) {
                arrayOfDoubles[ith] = Double.valueOf(tempString);
            } else {
                offendingStrings.add(tempString);
                allStringsAreDoubles = false;
            }       
        }
        
        if (allStringsAreDoubles == false) {
            doNonDoubleAlert();
        }
        return allStringsAreDoubles;
    }
    
    public static boolean convertColumnOfDataToArrayOfDoubles (ColumnOfData colOfData) {
        boolean allStringsAreDoubles = true;
        ArrayList<String> arrayListFromColumn = new ArrayList<>();
        arrayListFromColumn = colOfData.getTheCases();
        
        int nNonDoubles = 0;
        offendingStrings = new ArrayList<>();
        int nDataPoints = arrayListFromColumn.size();
        arrayOfDoubles = new Double[nDataPoints];
        for (int ith = 0; ith < nDataPoints; ith++) {
            tempString = arrayListFromColumn.get(ith);
            doubleIsOK = stringIsADouble(tempString);
            if (doubleIsOK == true) {
                arrayListOfDoubles.set(ith, Double.valueOf(tempString));
            } else {
                offendingStrings.add(tempString);
                allStringsAreDoubles = false;
            }       
        }
        
        if (allStringsAreDoubles == false) {
            doNonDoubleAlert();
        }
        return allStringsAreDoubles;
    }
    
    public static Double convertStringToDouble( String fromThis) {
        Double toThis = Double.parseDouble(fromThis);
        return toThis;
    }
    
    private static void doNonDoubleAlert() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Oops, we have a problem here!!!");
        alert.setHeaderText("At least one of the data points is not a real number.");
        String contentText = "Obsequious and sycophantic I, Program, are, I cannot in good " +
                      "\nconscience proceed with your perfectly reasonable request. " +
                      "\nThe reason for my perfidy is that I do not clearly know your" +
                      "\nbest strategy in the face of this unfortunate occurence. I " +
                      "\nwill thus attempt to gracefully get you back to a safe place.";
        alert.setContentText(contentText);
        alert.showAndWait();        
    }
    
    public ArrayList<String> getOffendingStrings() { return offendingStrings; }
    public Double[] getArrayOfDoubles() { return arrayOfDoubles; }
    public ArrayList<Double> getArrayListOfDoubles() { return arrayListOfDoubles; }
}   

