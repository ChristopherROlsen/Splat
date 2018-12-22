/************************************************************
 *                        Splat_FileOps                     *
 *                           11/23/18                       *
 *                            15:00                         *
 ***********************************************************/

/**************************************************
*  All coordinate systems are zero-based:         *
*  1.  The DataStruct of mCases x nCols           *
*  2.  The DataGrid                               *
*                                                 *
*  Only the presented-to-user cases and variables *
*  will be 1-based, and will appear in the code   *
*  with xxx + 1 subscripts.                       *
*                                                 *
**************************************************/
package splat;

import dialogs.MyDialogs;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;

public class Splat_FileOps {
    // POJOs
    int nVarsInFile, nCasesInFile, maxCasesInGrid;
    // My classes
    Splat_ParseTheCSV fileParser;
    Splat_DataManager dm;
    PositionTracker posTracker;

    public Splat_FileOps(Splat_DataManager dm) {
        this.dm = dm;
        posTracker = dm.getPositionTracker();
    }

    public void ClearTable() {
        //System.out.println("42 fo, ClearTable(Splat_DataManager myData))");
        if (!dm.getDataIsClean()) {
            MyDialogs newDiag = new MyDialogs();
            int temp = newDiag.YesNo(1, "Clear Data?",
                    "Your data have not been saved. \n"
                    + "Do you wish to clear these data?");

            if (temp != 1) {
                return;
            }
        }

        maxCasesInGrid = dm.getMaxVisCases();
        dm.initializeDataStructure(maxCasesInGrid);
        dm.setDataExists(false);
        //System.out.println("57 fo, end ClearTable(Splat_DataManager myData))");
    } // clearData

    public void getDataFromFile(int startVariable) {
        File fileName;
        //System.out.println("62 fo, getDataFromFile(int startVariable)");
        FileChooser fChoose = new FileChooser();

        fChoose.setTitle("Get Data");
        fChoose.setInitialDirectory(dm.getLastPath());
        FileChooser.ExtensionFilter extFilter1
                = new FileChooser.ExtensionFilter("CSV", "*.csv");
        FileChooser.ExtensionFilter extFilter2
                = new FileChooser.ExtensionFilter("Tab-Delimited", "*.*");
        fChoose.getExtensionFilters().addAll(extFilter1, extFilter2);

        fileName = fChoose.showOpenDialog(null);
        if (fileName != null) {
            if ("CSV".equals(fChoose.getSelectedExtensionFilter().getDescription())) {
                dm.setDelimiter(',');
            } else {
                dm.setDelimiter('\t');
            }
        }

        if ((fileName == null) || (fileName.getName().equals(""))) {
            return;
        }

        fileParser = new Splat_ParseTheCSV(fileName, dm.getDelimiter());

        nVarsInFile = fileParser.getNVariables();
        nCasesInFile = fileParser.getNCases();
        posTracker.setNVarsInStruct(nVarsInFile);
        
        dm.getTheGrid().setNVarsInGrid(Math.min(nVarsInFile, dm.getMaxVisVars()));
        dm.getTheGrid().setNCasesInGrid(Math.min(nCasesInFile, dm.getMaxVisCases()));
        
        dm.initalizeForFileRead(nVarsInFile, nCasesInFile);
        for (int j = 0; j < nVarsInFile; j++) {
            int tempInt = j + startVariable;
            dm.setVariableNameInStruct(j + startVariable, fileParser.getDataElementColRow(j, 0));
        }
        
        for (int iRow = 0; iRow < nCasesInFile; iRow++) {
            for (int jCol = 0; jCol < nVarsInFile; jCol++) {
                dm.setDataElementInStruct(jCol + startVariable, iRow, fileParser.getDataElementColRow(jCol, iRow + 1));
            }
        }

        dm.setFileName(fileName);
        dm.setLastPath(fileName);
        posTracker.set_CurrentDG(0, 0);
        posTracker.set_ulDG(0, 0);
        posTracker.set_ulDS(0, 0);
        dm.sendDataStructToGrid();
        posTracker.set_CurrentDG(0, 0);
        dm.setDataExists(true);
        
        for (int ithInitColumn = 0; ithInitColumn < nVarsInFile; ithInitColumn++) {
            dm.getAllTheColumns().get(ithInitColumn).assignDataType();
        }
    } // OpenData

    public void SaveData(Splat_DataManager dm, boolean getFileName) {
        int i, j, currVars, currCases;
        currVars = 0; currCases = 0;  //  To satisfy the compiler
        if (posTracker.getNVarsInStruct() == 0) {
            
            Alert noDataAlert = new Alert(AlertType.ERROR);
            noDataAlert.setTitle("Looking for Mr. GoodData...");
            noDataAlert.setHeaderText("Seeking but not finding...");
            noDataAlert.setContentText("Not being critical or anything, but there"
                                     + "\ndoes not appear to be any data to save.");
            noDataAlert.showAndWait();
            return;
        }

        File fileName = dm.getFileName();

        if (dm.getFileName() != null) {
            fileName = dm.getFileName();
        }

        if (getFileName || (dm.getFileName() == null)) {

            FileChooser fChoose = new FileChooser();
            fChoose.setInitialDirectory(dm.getLastPath());
            fChoose.setTitle("Save Data");
            FileChooser.ExtensionFilter extFilter1
                    = new FileChooser.ExtensionFilter("CSV", "*.csv");
            FileChooser.ExtensionFilter extFilter2
                    = new FileChooser.ExtensionFilter("Tab-Delimited", "*.*");
            fChoose.getExtensionFilters().addAll(extFilter1, extFilter2);

            fileName = fChoose.showSaveDialog(null);
            
            if (fChoose.getSelectedExtensionFilter().getDescription() == null) {
                return;
            }
            
            if (fChoose.getSelectedExtensionFilter().getDescription() == "CSV") {
                dm.setDelimiter(',');
            } else {
                dm.setDelimiter('\t');
            }

            if (fileName == null) {
                return;
            }
        }

        if ((fileName == null) || (fileName.getName().equals(""))) {
            return;
        }
        
        try {

            BufferedWriter writer = null;
            writer = new BufferedWriter(new FileWriter(fileName));
            currVars = posTracker.getNVarsInStruct();
            currCases = posTracker.getNCasesInStruct();
            for (j = 0; j < currVars; j++) {
                writer.write(dm.getVariableName(j));
                if (j < (currVars - 1)) {
                    writer.write(dm.getDelimiter());
                }
            }
            writer.write("\n");
            for (i = 0; i < currCases; i++) {
                for (j = 0; j < currVars; j++) {
                    writer.write(dm.getDataElementFromStruct(j, i));
                    if (j < (currVars - 1)) {
                        writer.write(dm.getDelimiter());
                    }
                }
                writer.write("\n");
            }

            writer.close();

            dm.setFileName(fileName);
            dm.setLastPath(fileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
            
    } // SaveData

    public void ExitProgram(Splat_DataManager myData) {
        Boolean exit = true;

        if (!myData.getDataIsClean()) {
            MyDialogs newDiag = new MyDialogs();
            int temp = newDiag.YesNo(1, "Exit Program?",
                    "Your data have not been saved. \n"
                    + "Do you wish to exit?");

            if (temp != 1) { exit = false; }
        }

        if (exit) { System.exit(0); }

    }
}
