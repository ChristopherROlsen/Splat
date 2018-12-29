// **************************************************
// *                 CSV_FileParser                 *
// *                    12/24/18                    *
// *                     15:00                      *
// *************************************************/

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

public class CSV_FileParser {
    // POJOs
    boolean firstLineIsLabels, secondLineIsDataTypes;
    
    int /*nDataRowsSoFar,*/ nColumnsThisFile, nDataRecordsThisFile;
    int nDataElementsThisLine, nDataTypesRead, nsbLines;
    
    String currently_Read_String, xx, returnStatus;
    String missingDataString = "*";
    String contentText;
    ArrayList<String> tempAL, preParsedDataLine, parsedDataLine, 
                      firstParsedDataLine, parsedLabelsLine;     
    ObservableList<ObservableList<String>> allParsedLines;

    Alert alert;
    BufferedReader bufferedReader;    
    File selectedFile;
    FileReader fileReader;
    Set variableNames;  // This is a HashSet
    
    // My classes
    CSV_LineParser csvParser;
    
    public CSV_FileParser (File selectedFile, char fieldSeparator) { 
        this.selectedFile = selectedFile;
        nColumnsThisFile = 0; 
        nDataRecordsThisFile = 0;
        nsbLines = 0;
        csvParser = new CSV_LineParser(fieldSeparator);
        firstParsedDataLine = new ArrayList();
        allParsedLines = FXCollections.observableArrayList();
        parsedDataLine = new ArrayList();
        preParsedDataLine = new ArrayList();
        variableNames = new HashSet();
        //System.out.println("52 fp");
        StringBuilder sb = new StringBuilder(1024);
        firstLineIsLabels = true;
        returnStatus = "Ok";
        String noLabel_String = "MissingLabel";
    }
    
    public String parseTheFile() {
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

    // ****************************************************************************
// *  The first line must be parsed carefully because for ANOVA1 this program *
// *  Allows numeric values as labels for the variables (e.g. 'doses.') The   *
// *  more usual situation would be that a data file has been downloaded w/o  *
// *  labels, which must now be supplied (or not) by the user.                *
// ****************************************************************************
            firstLineIsLabels = false;
            boolean someAreNotNumbers = false;
            boolean someAreNumbers = false;
            for (int iCol = 0; iCol < nColumnsThisFile; iCol++) {
                try {
                    tmpString = parsedLabelsLine.get(iCol);
                    double d_abc = Double.parseDouble(tmpString);
                    someAreNumbers = true;
                } catch (NumberFormatException ex) {
                    someAreNotNumbers = true;
                    firstLineIsLabels = true;
                }   // end catch
            }        

            ArrayList<String> tmpParsedLine = new ArrayList();
            if (someAreNumbers == true) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Your first line is all numeric!");
                alert.setHeaderText("Pardon this interruption, but a clarification is needed...");
                contentText = "Usually an all-numeric first line indicates an absence of predetermined" +
                             "\ndetermined labels for the variables. In some cases, such as a treend  " +
                             "\nanalysis in ANOVA, variable labels might actually be all numeric.  Is " +
                             "\nit your intention that the numeric labels in the first line should be" +
                             "\ntreated as quantitatve labels for the variables?";
                alert.setContentText(contentText);
                
                ButtonType buttonTypeYes = new ButtonType("Yes");
                ButtonType buttonTypeNo = new ButtonType("No");
                ButtonType buttonTypeCancel = new ButtonType("Cancel");
                
                alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);
                Optional<ButtonType> result = alert.showAndWait();;
                
                if (result.get() == buttonTypeYes) {
                    returnStatus = "Ok";
                    firstLineIsLabels = true;
                }
                else if (result.get() == buttonTypeNo) {
                    returnStatus = "Ok";
                    alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Well, OK, then -- read this carefully.");
                    alert.setHeaderText("This is what we'll do...");
                    contentText = "Fixing this is easy, but a bit tedious. I will create some generic" + 
                                "\nlabels and put your labels in the first data row.  Fix the generic" +
                                         "\nones and then delete that first row by clicking on it some-" + 
                                         "\nwhere and deleting it via the Edit menu. (Be sure the mouse" +
                                         "\nis in the first row!!)";
                    alert.setContentText(contentText);
                    alert.showAndWait();
                    
                    ArrayList<String> tmpFirstParsedLine = new ArrayList();
                    for (int jj = 0; jj < nColumnsThisFile; jj++) {
                        tmpFirstParsedLine.add("Var # " + (jj + 1));
                    }   
                    addToAllParsedLines(tmpFirstParsedLine);    //  Generic labels
                }
                else { 
                    returnStatus = "Cancel";
                    return returnStatus; 
                }
            }

           if (!firstLineIsLabels) {
               addToAllParsedLines(tmpParsedLine);
               addToAllParsedLines(parsedLabelsLine);
              // nDataRowsSoFar = 1;
           } else {
               addToAllParsedLines(parsedLabelsLine);
              // nDataRowsSoFar = 0;
           }
           
            //  *****************   Now read the data   *********************************
            while(currently_Read_String != null) {
                currently_Read_String = bufferedReader.readLine();
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
             //   nDataRowsSoFar++;               
            }  // end while(currently_Read_String != null)

            fileReader.close();
        }   //  end try to read file   //  end try to read file   //  end try to read file   //  end try to read file
        catch (Exception ex) {
            // System.out.println("USER ALERT -- error reading file. " + e.getMessage());
        }
        nDataRecordsThisFile = allParsedLines.size() - 1;  // Data rows        
        return returnStatus;
    }
       
    private ArrayList<String> adjustTheArrayList(ArrayList<String> thePreStringArray) {
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
            xx = oneLineOfStrings.get(i);
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
    public int getNCases()  {return nDataRecordsThisFile; }
    
     public String getIthDataVariableName(int ithVariable) {
        return parsedLabelsLine.get(ithVariable);
    }   

    private void addToAllParsedLines(ArrayList<String> thisParsedLine) {
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