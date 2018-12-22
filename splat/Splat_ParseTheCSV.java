/**************************************************
 *                Splat_ParseTheCSV               *
 *                    08/25/18                    *
 *                     00:00                      *
 *                                                *
 *            Programmer: Chris Olsen             *
 *************************************************/

package splat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class Splat_ParseTheCSV {
    // POJOs
    boolean firstLineIsLabels, secondLineIsDataTypes;
    
    int nDataRowsSoFar, nColumnsThisFile, nRecordsThisFile;
    int nDataElementsThisLine, nDataTypesRead, nsbLines;
    
    String currently_Read_String, xx;
    String missingDataString = "*";
    ArrayList<String> tempAL, preParsedDataLine, parsedDataLine, 
                      firstParsedDataLine, parsedLabelsLine;     
    ObservableList<ObservableList<String>> allParsedLines;

    BufferedReader bufferedReader;    
    File selectedFile;
    FileReader fileReader;
    Set variableNames;  // This is a HashSet
    
    // My classes
    Splat_CSVParser csvParser;
    
    public Splat_ParseTheCSV (File selectedFile, char fieldSeparator) {        
        nColumnsThisFile = 0; 
        nRecordsThisFile = 0;
        nsbLines = 0;
        csvParser = new Splat_CSVParser(fieldSeparator);
        firstParsedDataLine = new ArrayList();
        allParsedLines = FXCollections.observableArrayList();
        parsedDataLine = new ArrayList();
        preParsedDataLine = new ArrayList();
        variableNames = new HashSet();
        //System.out.println("52 fp");
        StringBuilder sb = new StringBuilder(1024);
        firstLineIsLabels = true;
        
        String noLabel_String = "MissingLabel";
        
        try {
            fileReader = new FileReader(selectedFile);
            bufferedReader = new BufferedReader(fileReader);     
            
            // ***********************************************************
            // ************ Read and Parse the Labels Line ***************
            // ***********************************************************
            currently_Read_String = new String(bufferedReader.readLine());
            parsedLabelsLine = new ArrayList<>(csvParser.parse(currently_Read_String));
            nColumnsThisFile = parsedLabelsLine.size();
            String tmpString;
            // Do initial check for missing labels -- could be just missing data
            for (int iCol = 0; iCol < nColumnsThisFile; iCol++) {
                tmpString = parsedLabelsLine.get(iCol);
                if (tmpString.equals("")) {
                    parsedLabelsLine.set(iCol, "Var # " + (iCol + 1));
                }            
            }
    
            //  Now ask if the first line is non-numeric
            // If ANY parses are unsuccessful, this is a label line
            firstLineIsLabels = false;
            for (int iCol = 0; iCol < nColumnsThisFile; iCol++) {
                try {
                    tmpString = parsedLabelsLine.get(iCol);
                    double d_abc = Double.parseDouble(tmpString);
                } catch (NumberFormatException ex) {
                    firstLineIsLabels = true;
                }   // end catch
            }
            
            ArrayList<String> tmpParsedLine = new ArrayList();
            if (!firstLineIsLabels) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Your first line is all numeric!");
                alert.setHeaderText("Pardon this interruption, but a clarification is needed...");
                alert.setContentText("Usually an all-numeric first line indicates an absence of pre-" +
                                     "\ndetermined labels for the variables.  If that is your intent" +
                                     "\nthis program will create generic modifiable labels for each of" +
                                     "\nyour variables. In some (admittedly rare) circumstances the" +
                                     "\nvariable labels might actually be all numeric.  Is it your" +
                                     "\nintention that the first line should be treated as numeric" +
                                     "\nlabels for the variables?"

                );
                
                ButtonType buttonTypeYes = new ButtonType("Yes");
                ButtonType buttonTypeNo = new ButtonType("No");
                
                alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
                Optional<ButtonType> result = alert.showAndWait();
                
                if (result.get() == buttonTypeYes) {
                    firstLineIsLabels = true;
                }
                else    //  Create generic labels
                {
                    for (int jj = 0; jj < nColumnsThisFile; jj++) {
                        tmpParsedLine.add("Var # " + (jj + 1));
                    }                     
                }               
            }

           if (!firstLineIsLabels) {
               addToAllParsedLines(tmpParsedLine);
               addToAllParsedLines(parsedLabelsLine);
               nDataRowsSoFar = 1;
           } else {
               addToAllParsedLines(parsedLabelsLine);
               nDataRowsSoFar = 0;
           }
           //System.out.println("105 fp, nColumnsThisFile = " + nColumnsThisFile);            
            //  *****************   Now read the data   *********************************
            while(currently_Read_String != null) {
                currently_Read_String = new String(bufferedReader.readLine());
                preParsedDataLine = csvParser.parse(currently_Read_String); 
                parsedDataLine = new ArrayList<>(adjustTheArrayList(preParsedDataLine));            
                nDataElementsThisLine = parsedDataLine.size(); 
                
                if (nDataElementsThisLine < nColumnsThisFile) { // append last element 
                    int thisManyDataElementsShort = nColumnsThisFile - nDataElementsThisLine;
                    for (int missingData = 0; missingData < thisManyDataElementsShort; missingData++) {
                        currently_Read_String += missingDataString;
                    }
                    // reparse
                    parsedDataLine = new ArrayList<>(csvParser.parse(currently_Read_String)); 
                }

                if (nDataElementsThisLine > nColumnsThisFile) {
                    System.out.println("Fatal error -- more data than labels");
                    System.exit(0);
                }

                // re-parse
                addToAllParsedLines(parsedDataLine);
                //System.out.println("pDL = " + parsedDataLine);
                nDataRowsSoFar++;
                // System.out.println("131 fp, nDataRowsSoFar = " + nDataRowsSoFar);                
            }  // end while(currently_Read_String != null)

            fileReader.close();
        }   //  end try to read file   //  end try to read file   //  end try to read file   //  end try to read file
        catch (Exception e) {
            // System.out.println("USER ALERT -- error reading file. " + e.getMessage());
        }

        nRecordsThisFile = nDataRowsSoFar + 1;  // Data rows + labels       
        //Create error return code here??
    }
       
    public ArrayList<String> adjustTheArrayList(ArrayList<String> thePreStringArray) {
        int iCol;
        ArrayList<String> adjustedArrayList = new ArrayList(nColumnsThisFile);
        int preStringArraySize = thePreStringArray.size();
        if (preStringArraySize >= nColumnsThisFile) {
            // IF GREATER, ALERT USER TO ADJUSTMENT!
            // Copy first nLegal columns
            for (iCol = 0; iCol < nColumnsThisFile; iCol++) {
                adjustedArrayList.add(thePreStringArray.get(iCol));  
            }
        } else {
            //  Pad on end with Missing
            for (iCol = 0; iCol < preStringArraySize; iCol++) {
                adjustedArrayList.add(iCol, thePreStringArray.get(iCol));   // simple copy
            }  
            
            for (iCol = preStringArraySize; iCol < nColumnsThisFile; iCol++) {
                adjustedArrayList.add(missingDataString);
            }             
        }
        
            for (iCol = 0; iCol < nColumnsThisFile; iCol++) {
                if (adjustedArrayList.get(iCol).equals(""))
                    adjustedArrayList.set(iCol, missingDataString);   
            }  
        
        return adjustedArrayList;
    }
  
    
    private ArrayList<String> constructTheDataLine(ArrayList<String> oneLineOfStrings) {
        preParsedDataLine.clear();
        
        for (int i = 0; i < nDataElementsThisLine; i++) {
            String xx = oneLineOfStrings.get(i);
            if (xx.equals(""))
                xx = missingDataString;
            try {
                double d_abc = Double.parseDouble(xx);
            }
            catch (NumberFormatException ex)    //  Something other than a double
            {
                // System.out.println(xx + " did not survive parseDouble");
            }
        }
        return preParsedDataLine;
    }

    public int getNVariables()  {return nColumnsThisFile; }
    
    //  *****  First row is Labels  *****
    public int getNCases()  {return nRecordsThisFile - 1; }
    
     public String getIthDataVariableName(int ithVariable) {
        return parsedLabelsLine.get(ithVariable);
    }   

    public void addToAllParsedLines(ArrayList<String> thisParsedLine) {
        ObservableList<String> observedParsedLine;
        observedParsedLine = FXCollections.observableArrayList();
        for (int i = 0; i < thisParsedLine.size(); i++) {
            String tempString = new String((thisParsedLine.get(i)).trim()); // ###########################
            observedParsedLine.add(tempString);
        }
        allParsedLines.add(observedParsedLine); 
    }

    // The data rows begin after the label and data type lines
    public String getDataElementColRow(int col, int row) {
        String tempString = new String((allParsedLines.get(row)).get(col));
        return tempString;
    }

    public void setDataElementColRow(int col, int row, String stringValue) {
        allParsedLines.get(row).set(col, stringValue);
    }    
    
    public ObservableList<String> getParsedLine(int ithLine) {
        return allParsedLines.get(ithLine);
    }
              
    public ObservableList<ObservableList<String>> getAllParsedLines() {
       return allParsedLines;
    }

}