/************************************************************
 *                     Splat_DataManager                    *
 *                          12/01/18                        *
 *                            09:00                         *
 ***********************************************************/
package splat;

import dataObjects.QuantitativeDataVariable;
import dataObjects.ColumnOfData;
import java.io.File;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.text.SimpleDateFormat;
import java.util.Collections;
import genericClasses.*;

public final class Data_Manager {

    boolean dataExists, dataIsClean;

    int colHeadSelected, nVariablesInVisualGrid, nCasesInVisualGrid, nCasesInStruct, nVarsInStruct;

    private int maxCasesInGrid, maxVarsInGrid;   // Set the initial size of the grid displayed

    private File fileName = null;
    private File lastPath = new File(System.getProperty("user.dir") + File.separator);

    public String currentVersion;
    public final String newMissingData = "*";

    private char delimiter = ','; // default: csv files

    // My classes
    Data_Grid dg;
    ArrayList<ColumnOfData> allTheColumns;
    PositionTracker tracker;

    // POJOs / FX
    private Font outFont = Font.font("Courier New", FontWeight.NORMAL, 12);

    // Used by the data manager:
    private final BorderPane mainPane;  // this is sent to the main program
    private ArrayList<TextField> colHeading, rowHeading;
    
    private TextField corner;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

    public Data_Manager(int nCasesInVisualGrid, int nVariablesInVisualGrid) {
        // System.out.println("66 dm, Data_Manager(int nCasesInVisualGrid, int nVariablesInVisualGrid)");
        // System.out.println("67 dm, nCasesInVisualGrid = " + nCasesInVisualGrid);
        // System.out.println("68 dm, nVariablesInVisualGrid = " + nVariablesInVisualGrid);
        this.maxCasesInGrid = nCasesInVisualGrid;
        this.maxVarsInGrid = nVariablesInVisualGrid;
        currentVersion = "10/07/2018";
        dataExists = false;

        tracker = new PositionTracker(this, this.maxVarsInGrid, this.maxCasesInGrid);
        tracker.set_ulDG(0, 0);
        tracker.setNVarsInStruct(0);
        tracker.setNCasesInStruct(0);

        dg = new Data_Grid(this, tracker);
        tracker.setTrackerDataGrid(dg);

        colHeading = new ArrayList<>(); rowHeading = new ArrayList<>();

        // Generate all the headings and cells for the data manager:
        corner = new TextField("OBSERV.");
        corner.getStyleClass().add("rowHeadings");
        corner.setMinWidth(50); corner.setMaxWidth(50);
        corner.setEditable(false);

        for (int thisVar = 0; thisVar < nVariablesInVisualGrid; thisVar++) {
            String tempVarName = "V#" + (thisVar + 1);
            TextField element = new TextField(tempVarName);
            element.getStyleClass().add("colHeadings");
            element.setEditable(false);
            colHeading.add(element);
            colHeading.get(thisVar).setOnMousePressed((MouseEvent me) -> {
                editColumn();
                sendDataStructToGrid();
            });
        }

        for (int caseIndex = 0; caseIndex < nCasesInVisualGrid; caseIndex++) {
            TextField element = new TextField(String.format("%4d", (caseIndex + 1)));
            element.getStyleClass().add("rowHeadings");
            element.setEditable(false);
            rowHeading.add(element);
        }

        // Lay out the data manager.  First, layout the top row of column heading cells ('OBS', Var 1, Var 2, ... Var N)
        HBox colHeadingCells = new HBox(0);
        colHeadingCells.getChildren().add(corner);
        for (int thisVar = 0; thisVar < this.maxVarsInGrid; thisVar++) {
            colHeadingCells.getChildren()
                           .add(colHeading.get(thisVar));
        }
        // Next, lay out the numeric row 'header cells' on the left-hand side of the area by adding them to a Vertical Box.
        VBox rowHeadingCells = new VBox(0);
        for (int caseIndex = 0; caseIndex < nCasesInVisualGrid; caseIndex++) {
            rowHeadingCells.getChildren()
                           .add(rowHeading.get(caseIndex));
        }

        rowHeadingCells.setMinWidth(50); rowHeadingCells.setMaxWidth(50);

        mainPane = new BorderPane();
        mainPane.setTop(colHeadingCells);
        mainPane.setLeft(rowHeadingCells);
        mainPane.setCenter(dg.getGridPane());

        initializeDataStructure(nCasesInVisualGrid);  //  public to allow FileOps to clear
        // System.out.println("131 dm, end Data_Manager(int nCasesInVisualGrid, int nVariablesInVisualGrid)");
    } // End constructor

    //        Ack!!!   Need to do Column Header stuff.  
    public void deleteUnstacked() {
        for (int ithVar = nVarsInStruct - 1; ithVar >=0; ithVar--) {
            // System.out.println("141 dm.deleteUnstacked(), ithVar = " + ithVar);
            // System.out.println("142 dm, genInfo = " + allTheColumns.get(ithVar).getGenericVarInfo());
            // if (allTheColumns.get(ithVar).getGenericVarInfo().equals("Unstacked")) {
            //   allTheColumns.remove(ithVar);
            //    nVarsInStruct --;
            //}
        }
        
    }

    // public int getMaxCasesInGrid() { return maxCasesInGrid; }

    public void initializeDataStructure(int maxCasesInGrid) {
        // System.out.println("148 dm, initializeDataStructure(int maxCasesInGrid)");
        allTheColumns = new ArrayList();
        tracker.setNVarsInStruct(0);
        tracker.setNCasesInStruct(0);
        dataIsClean = true;
        tracker.set_CurrentDG(0, 0);
        tracker.set_CurrentDS(0, 0);

        for (int ithGridCol = 0; ithGridCol < maxVarsInGrid; ithGridCol++) {
            colHeading.get(ithGridCol).setText("Var #" + (ithGridCol + 1));
        }

        for (int jthGridRow = 0; jthGridRow < maxCasesInGrid; jthGridRow++) {
            rowHeading.get(jthGridRow).setText(String.format("%4d", jthGridRow + 1));
        }

        for (int ithGridCol = 0; ithGridCol < maxVarsInGrid; ithGridCol++) {
            for (int jthGridRow = 0; jthGridRow < maxCasesInGrid; jthGridRow++) {
                dg.setGridCell(ithGridCol, jthGridRow, "");
            }
        }

        for (int thisVar = 0; thisVar < maxVarsInGrid; thisVar++) {
            allTheColumns.add(new ColumnOfData(this, tracker.getNCasesInStruct(), "Var #" + (thisVar + 1)));
        }

        sendDataStructToGrid();
        fileName = null;
        delimiter = ',';
        // System.out.println("179 dm, end initializeDataStructure(int maxCasesInGrid)");
    } // end initialize data structure

    public void resizeColumnHeaderCellsArray(int newMaxVarsInGrid) {
        //System.out.println("180 dm, resizeColumnHeaderCellsArray(int newMaxVarsInGrid)");
        //System.out.println("181 dm, newMAxVarsInGrid = " + newMaxVarsInGrid);
        int oldMaxVars = colHeading.size();

        if (newMaxVarsInGrid > oldMaxVars) {
            for (int varIndex = oldMaxVars; varIndex < newMaxVarsInGrid; varIndex++) {
                TextField element = new TextField(String.format("Var #%d", + (varIndex + 1)));
                element.getStyleClass().add("colHeadings");
                element.setEditable(false);
                colHeading.add(element);      
            }
            AttachColumnHeaders(newMaxVarsInGrid);
        } else if (newMaxVarsInGrid < oldMaxVars)
        {
            for (int rowIndex = oldMaxVars - 1; rowIndex >= newMaxVarsInGrid; rowIndex--) {
                colHeading.remove(rowIndex);
            }
            AttachColumnHeaders(newMaxVarsInGrid);
        }
    }
   
    private void AttachColumnHeaders(int newMaxVarsInGrid) {
        //System.out.println("202 dm, AttachColumnHeaders(int newMaxVarsInGrid)");
        HBox colHeadingCells = new HBox(0);
        colHeadingCells.getChildren().add(corner);

        for (int thisVar = 0; thisVar < newMaxVarsInGrid; thisVar++) {
            colHeadingCells.getChildren()
                    .add(colHeading.get(thisVar));
        }
        mainPane.setTop(colHeadingCells);
    }
    
    public void resizeRowHeaderCellsArray(int newMaxCasesInGrid) {
        //System.out.println("214 dm, resizeRowHeaderCellsArray(int newMaxCasesInGrid)");
        int oldMaxCases = rowHeading.size();

        if (newMaxCasesInGrid > oldMaxCases) {
            for (int caseIndex = oldMaxCases; caseIndex < newMaxCasesInGrid; caseIndex++) {
                TextField element = new TextField(String.format("%4d", (caseIndex + 1)));
                element.getStyleClass().add("rowHeadings");
                element.setEditable(false);
                rowHeading.add(element);
            }

            AttachRowHeaders(newMaxCasesInGrid);
        } else if (newMaxCasesInGrid < oldMaxCases) {
            for (int caseIndex = oldMaxCases - 1; caseIndex >= newMaxCasesInGrid; caseIndex--) {
                rowHeading.remove(caseIndex);
            }

            AttachRowHeaders(newMaxCasesInGrid);
        }
    }    

    private void AttachRowHeaders(int newMaxCasesInGrid) {
        //System.out.println("236 dm, AttachRowHeaders(int newMaxCasesInGrid)");        
        VBox rowHeadingCells = new VBox(0);
        for (int caseIndex = 0; caseIndex < newMaxCasesInGrid; caseIndex++) {
            rowHeadingCells.getChildren()
                           .add(rowHeading.get(caseIndex));
        }
        
        mainPane.setLeft(rowHeadingCells);
    }

    public void resizeGrid(int newMaxRowCount, int newMaxColCount) {
        //System.out.println("247 dm, resizeGrid(int newMaxRowCount, int newMaxColCount)");         
        dg.adjustGridHeightAndWidth(newMaxRowCount, newMaxColCount);
        tracker.setTrackerDataGrid(dg);
        tracker.updateMaxCases(newMaxRowCount);
        tracker.updateMaxVars(newMaxColCount);
    }
    
    public void initialDataEntry(int dg_Col, int dg_Row, String dataValue) {
        // System.out.println("258 dm, initalizeForFileRead(int numVariables, int nDataLines)");
        int dgColPlus1 = dg_Col + 1;
        int dgRowPlus1 = dg_Row + 1;
        getTheGrid().setNVarsInGrid(Math.min(dgColPlus1, getMaxVisVars()));
        getTheGrid().setNCasesInGrid(Math.min(dgRowPlus1, getMaxVisCases()));
        
        allTheColumns = new ArrayList();
        tracker.setNVarsInStruct(dgColPlus1);
        tracker.setNCasesInStruct(dgRowPlus1);

        tracker.set_CurrentDG(0, 0);
        tracker.set_CurrentDS(0, 0);
        tracker.setFirstCaseIdentifier(0);
        tracker.setFirstVarIdentifier(0);

        // Fill with generic variable names
        for (int initVars = 0; initVars < maxVarsInGrid; initVars++) {
            allTheColumns.add(new ColumnOfData(this, tracker.getNCasesInStruct(), "Var #" + (initVars + 1)));
        }
        
        for (int ithVar = 0; ithVar <= dg_Col; ithVar++) {
            for (int jthCase = 0; jthCase <= dg_Row; jthCase++) {
                setDataElementInStruct(ithVar, jthCase, "*");                
            }
        }
        
        setDataElementInStruct(dg_Col, dg_Row, dataValue);
        sendDataStructToGrid();
        setDataExists(true);
       //  System.out.println("287 dm, end initalizeForFileRead(int numVariables, int nDataLines)");        
    }   // end initializeForFileRead

    public void initalizeForFileRead(int numVariables, int nDataLines) {
        // System.out.println("291 dm, initalizeForFileRead(int numVariables, int nDataLines)");
        // System.out.println("292 dm, numVars = " + numVariables);
        // System.out.println("293 dm, numLines = " + nDataLines);
        allTheColumns = new ArrayList();
        tracker.setNVarsInStruct(numVariables);
        tracker.setNCasesInStruct(nDataLines);
        tracker.set_CurrentDG(0, 0);
        tracker.set_CurrentDS(0, 0);
        tracker.setFirstCaseIdentifier(0);
        tracker.setFirstVarIdentifier(0);

        for (int ithInitVar = 0; ithInitVar < numVariables; ithInitVar++) {
           // System.out.println("303 dm, ithInitVar = " + ithInitVar);
            allTheColumns.add(new ColumnOfData(this, tracker.getNCasesInStruct(), "Var #" + (ithInitVar + 1)));
        }
        // Fill the rest with generic variable names
        if (tracker.getNVarsInStruct() <= maxVarsInGrid) {
            for (int thisVar = tracker.getNVarsInStruct(); thisVar < maxVarsInGrid; thisVar++) {
                //System.out.println("309 dm, ithInitVar = " + thisVar);
                allTheColumns.add(new ColumnOfData(this, maxCasesInGrid, "Var #" + (thisVar + 1)));
            }
        }

        sendDataStructToGrid();
        //System.out.println("313 dm, end initalizeForFileRead(int numVariables, int nDataLines)");
    }   // end initializeForFileRead

    // sends the data manager to the main program:
    public BorderPane getMainPane() {
        return mainPane;
    }

    public void editColumn() {
        // System.out.println("316 dm, editColumn()");        
        RadioButton numericData = new RadioButton("Numeric Data");
        RadioButton textData = new RadioButton("Text Data");
        ToggleGroup buttons = new ToggleGroup();
        numericData.setToggleGroup(buttons);
        textData.setToggleGroup(buttons);

        for (int col = 0; col < maxVarsInGrid; col++) {
            if (colHeading.get(col).isFocused()) {  //  colHeading is zero-based
                colHeadSelected = col;
                break;
            }
        }

        if (getVariableIsNumeric(tracker.getFirstVarIdentifier() + colHeadSelected)) {
            numericData.setSelected(true);
        } else {
            textData.setSelected(true);
        }
        Label title = new Label("Edit Variable Information");
        title.getStyleClass()
             .add("dialogTitle");
        Label labelName = new Label("Variable Name: ");
        TextField textName = new TextField();
        Button goButton = new Button("Update Variable");
        Button autoButton = new Button("Restore Default");
        Button noButton = new Button("Close");

        textName.setText(getVariableName(tracker.getFirstVarIdentifier() + colHeadSelected));

        VBox mainPanel = new VBox();
        mainPanel.setAlignment(Pos.CENTER);
        title.setPadding(new Insets(10, 0, 10, 0));
        Separator sep1 = new Separator();
        mainPanel.getChildren().addAll(title, sep1);

        GridPane centerPanel = new GridPane();
        centerPanel.setPadding(new Insets(10, 10, 10, 10));
        centerPanel.setHgap(0);
        centerPanel.setVgap(5);
        centerPanel.add(labelName, 0, 0);
        centerPanel.add(textName, 1, 0);
        Separator sep2 = new Separator();
        mainPanel.getChildren()
                 .addAll(centerPanel, sep2);

        HBox optPanel = new HBox(10);
        optPanel.setAlignment(Pos.CENTER);
        optPanel.setPadding(new Insets(10, 10, 10, 10));
        optPanel.getChildren()
                .addAll(numericData, textData);
        Separator sep3 = new Separator();
        mainPanel.getChildren()
                 .addAll(optPanel, sep3);

        HBox buttonPanel = new HBox(10);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.setPadding(new Insets(10, 5, 10, 5));
        buttonPanel.getChildren()
                   .addAll(goButton, autoButton, noButton);
        mainPanel.getChildren()
                 .add(buttonPanel);

        Scene changeScene = new Scene(mainPanel);
        String css = getClass().getResource("/css/DataManager.css").toExternalForm();
        changeScene.getStylesheets()
                   .add(css);
        Stage changeStage = new Stage();
        changeStage.setScene(changeScene);
        changeStage.show();

        noButton.setOnAction((ActionEvent event) -> {
            changeStage.close();
        });

        autoButton.setOnAction((ActionEvent event) -> {
            colHeading.get(colHeadSelected)
                      .setText("Var #" + (tracker.getFirstVarIdentifier() + colHeadSelected + 1));
            allTheColumns.get(tracker.getFirstVarIdentifier() + colHeadSelected)
                      .setVarLabel(colHeading.get(colHeadSelected).getText());
            changeStage.close();
        });

        goButton.setOnAction((ActionEvent event) -> {
            String temp = textName.getText();
            temp = truncString(temp, 10);
            colHeading.get(colHeadSelected).setText(temp);
            allTheColumns.get(tracker.getFirstVarIdentifier() + colHeadSelected).setVarLabel(colHeading.get(colHeadSelected)
                         .getText());
            changeStage.close();
        });

        textName.setOnAction((ActionEvent event) -> {
            String temp = textName.getText();
            temp = truncString(temp, 10);
            colHeading.get(colHeadSelected).setText(temp);
            allTheColumns.get(colHeadSelected)
                         .setVarLabel(colHeading.get(colHeadSelected).getText());
            changeStage.close();
        });

        numericData.setOnAction((ActionEvent event) -> {
            setVariableNumeric(tracker.getFirstVarIdentifier() + colHeadSelected, true);
        });

        textData.setOnAction((ActionEvent event) -> {
            setVariableNumeric(tracker.getFirstVarIdentifier() + colHeadSelected, false);
        });
    }   //  end EditColumn

   
    public void sendDataStructToGrid() {
        int actualRow, actualCol, firstCaseId, firstVarId;
        ArrayList<String> columnCases;
        // System.out.println("438 dm, sendDataStructToGrid()");
        // System.out.println("439 dm, tracker.getNCasesInStruct() = " + tracker.getNCasesInStruct());
        String tempText;
        firstCaseId = tracker.getFirstCaseIdentifier(); // this is the number that appears at the beginning of the row    
        firstVarId = tracker.getFirstVarIdentifier();     
        nCasesInStruct = tracker.getNCasesInStruct(); 
        nVarsInStruct = tracker.getNVarsInStruct();  
        
        // System.out.println("445 dm, nCasesInStruct = " + nCasesInStruct);
        
        nVariablesInVisualGrid = Math.min(nVarsInStruct - firstVarId, maxVarsInGrid);
        nCasesInVisualGrid = Math.min(nCasesInStruct - firstCaseId, maxCasesInGrid);
        // Render the column headers.
        for (int ithGridCol = 0; ithGridCol < maxVarsInGrid; ithGridCol++) {
            actualCol = ithGridCol + firstVarId;

            if (actualCol < nVarsInStruct) {
                String tempString = allTheColumns.get(actualCol)
                                                 .getVarLabel();
                colHeading.get(ithGridCol).setText(tempString);
            } else {
                colHeading.get(ithGridCol).setText("Var #" + (actualCol + 1));
            }
        }
        //System.out.println("460 dm, maxCasesInGrid = " + maxCasesInGrid);
        // Render the row headesr.
        for (int jthGridRow = 0; jthGridRow < maxCasesInGrid; jthGridRow++) {
            actualRow = jthGridRow + firstCaseId;
            if (actualRow < nCasesInVisualGrid) {
                rowHeading.get(jthGridRow).setText(String.format("%4d", (actualRow + 1)));
            } else {
                rowHeading.get(jthGridRow).setText(String.format("%4d", (actualRow + 1)));
            }
        }
        //System.out.println("470 dm, maxVarsInGrid = " + maxVarsInGrid);    
        //System.out.println("471 dm, allTheColumns.size = " + allTheColumns.size());        
        for (int ithGridCol = 0; ithGridCol < maxVarsInGrid; ithGridCol++) {  
            actualCol = ithGridCol + firstVarId;
            //System.out.println("473 dm, ithGridCol / actualCol = " + ithGridCol + " / " + actualCol);
            
            // -------------------------------------------------------------------------------------------
            if (ithGridCol < nVarsInStruct) {
                //System.out.println("479 dm, ithGridCol / actualCol = " + ithGridCol + " / " + actualCol);
                columnCases = new ArrayList(allTheColumns.get(actualCol).getTheCases());
            }
            else {
                //System.out.println("482 dm, ithGridCol  = 0");
                //System.out.println("483 dm, nCasesInStruct  = " + tracker.getNCasesInStruct());
                columnCases = new ArrayList();  
                for (int blankCases = 0; blankCases < tracker.getNCasesInStruct(); blankCases++) {
                    columnCases.add(" ");
                }
            }
            // --------------------------------------------------------------------------------------------
            
            // System.out.println("492 dm, nCasesInStruct = " + nCasesInStruct);
            // System.out.println("493 dm, firstCaseId = " + firstCaseId);
            for (int jthGridRow = 0; jthGridRow < maxCasesInGrid; jthGridRow++) {
                actualRow = jthGridRow + firstCaseId;
                // System.out.println("478 dm, jthGridRow / actualRow = " + jthGridRow + " / " + actualRow);
                if (jthGridRow < (nCasesInStruct - firstCaseId)) {
                    tempText = columnCases.get(actualRow);
                    dg.setGridCell(ithGridCol, jthGridRow, tempText);
                } else {
                    dg.setGridCell(ithGridCol, jthGridRow, "");
                }
            }
            //System.out.println("486 dm, end sendDataStructToGrid()");
        }   // Send data struct to grid  
        
    }

    public void checkDataThisCol(int theColumn) {
        allTheColumns.get(theColumn - 1).assignDataType();
    }

    public void checkAllData() {
        for (int col = 0; col < tracker.getNVarsInStruct(); col++) {
            allTheColumns.get(col).assignDataType();
        }
    } // Check data

    public int numDistinctVals(int groupingVar) {
        return allTheColumns.get(groupingVar).findNumberOfDistinctValues();
    }

    public String getDataElementFromStruct(int col, int row) {

        String dataString = allTheColumns.get(col)
                                         .getTheCases()
                                         .get(row);
        if (dataString.equals(" ")) {
            dataString = "";
        }
        return dataString;
    }

    public void setDataElementInGrid(int col, int row, String toThis) {
        //System.out.println("508 dm, setDataElementInGrid(int nCols, int row, String toThis)");        
        allTheColumns.get(col).getTheCases().set(row, toThis);
        dg.setGridCell(col, row, toThis);
    }

    public void setDataElementInStruct(int col, int row, String toThis) {
        //System.out.println("514 dm, *** setDataElementInStruct nCols / row / toThis = " + nCols + " / " + row  + " / " + toThis);
        allTheColumns.get(col)
                     .getTheCases()
                     .set(row, toThis);
    }

    public String getTextFromStruct(int col, int row) {
        //System.out.println("521 dm, getTextFromStruct(int nCols, int row) nCols / row  = " + nCols + " / " + row);
        String dataString = allTheColumns.get(col)
                                         .getTheCases()
                                         .get(row);
        if (dataString.equals(" ")) {
            dataString = "";
        }
        return dataString;
    }

    public void setTextinStruct(int col, int row, String toThis) {
        //System.out.println("532 dm, setTextinStruct nCols / row / toThis = " + nCols + " / " + row  + " / " + toThis);        
        if (toThis.equals(" ")) {
            toThis = "";
        }
        allTheColumns.get(col)
                     .setData(row, toThis);
    }
    
    public int getNVarsInStruct() { return tracker.getNVarsInStruct(); }

    public boolean getVariableIsNumeric(int curr) {
        return allTheColumns.get(curr)
                            .getIsReal();
    }

    public void setVariableNumeric(int curr, boolean setMe) {
        allTheColumns.get(curr)
                     .setIsReal(setMe);
    }
    
    public void addNStackedVariables(ArrayList<QuantitativeDataVariable> allTheQDVs) {
        // System.out.println("553 dm, addNStackedVariables(ArrayList<QuantitativeDataVariable> allTheQDVs)");
        int tempNVars = tracker.getNVarsInStruct();
        int tempNCases = tracker.getNCasesInStruct();
        int nVariablesToAdd = allTheQDVs.size();
        for (int ithQDV = 0; ithQDV < nVariablesToAdd; ithQDV++) {
            QuantitativeDataVariable tempQDV = allTheQDVs.get(ithQDV);
            // System.out.println("559 dm, label = " + tempQDV.getDataLabel());
            allTheColumns.add(new ColumnOfData(this, tracker.getNVarsInStruct(), tempQDV.getDataLabel()));
            allTheColumns.get(tempNVars + ithQDV).setVarLabel(tempQDV.getDataLabel());
            allTheColumns.get(tempNVars + ithQDV).setGenericVarInfo("Unstacked");
            for (int ithCase = 0; ithCase < tempNCases; ithCase++) {
                allTheColumns.get(tempNVars + ithQDV).setData(ithCase, tempQDV.getIthDataPtAsString(ithCase));
            }
            tracker.setNVarsInStruct(tempNVars + nVariablesToAdd);
            resetTheGrid();
            sendDataStructToGrid();
        }

        sendDataStructToGrid();
    }
    
     public void addAColumn(QuantitativeDataVariable qdv) {
        int nCols;     
        addNVariables(1);  
        nCols = tracker.getNVarsInStruct();
        setVariableNameInStruct(nCols, qdv.getDataLabel());
        int columnSize = allTheColumns.size();
        for (int ithCase = 0; ithCase < qdv.getOriginalN(); ithCase++) {
            setDataElementInStruct(nCols - 1, ithCase, qdv.getAllTheCasesAsALStrings().get(ithCase));
            String tempString = getDataElementFromStruct(nCols - 1, ithCase);
        }
        sendDataStructToGrid();
    }   


    public void addNVariables(int nVariablesToAdd) {
        int tempNVars, tempNCases;
        tempNVars = tracker.getNVarsInStruct();
        tempNCases = tracker.getNCasesInStruct();
        for (int iVar = 0; iVar < nVariablesToAdd; iVar++) {
            ColumnOfData newCol = new ColumnOfData(this, tracker.getNCasesInStruct(), "Var #" + (tempNVars + iVar + 1));
            if (tempNVars < allTheColumns.size()) {
                allTheColumns.remove(allTheColumns.get(tempNVars));
                allTheColumns.add(tempNVars, newCol);
            }
            else {
                allTheColumns.add(newCol);
            }
            tracker.setNVarsInStruct(tempNVars + 1);
            tempNVars = tracker.getNVarsInStruct();
            for (int ithCase = 0; ithCase < tempNCases; ithCase++) {      
                allTheColumns.get(tempNVars - 1)
                             .setData(ithCase, "*");
            }
            tempNVars = tracker.getNVarsInStruct();
            tempNCases = tracker.getNCasesInStruct();
        }

        sendDataStructToGrid();
    }


    public void addNCases(int nCasesToAdd) {
        for (int iVar = 0; iVar < tracker.getNVarsInStruct(); iVar++) {
            allTheColumns.get(iVar)
                         .addNCasesOfThese(nCasesToAdd, "");
        }
        tracker.setNCasesInStruct(tracker.getNCasesInStruct() + nCasesToAdd);
    }

    public void resetTheGrid() {
        tracker.setFirstVarIdentifier(0);
        tracker.setFirstCaseIdentifier(0);
        sendDataStructToGrid();
    }

    public Data_Grid getDataGrid() { return dg; }

    public ArrayList<ColumnOfData> getAllTheColumns() { return allTheColumns; }

    public ArrayList<TextField> getIthColumnHeading() { return colHeading; }

    public ArrayList<TextField> getJthRowHeading() {return rowHeading; }

    public String getVariableName(int col) {
        return allTheColumns.get(col).getVarLabel();
    }

    public int getVariableIndex(String varName) {
        int found = -1;
        int varsThisTime = tracker.getNVarsInStruct();
        for (int i = 0; i < varsThisTime; i++) {
            String checkMe = getVariableName(i);
            if (checkMe.equals(varName)) {
                found = i;
            }
        }
        return found;
    } // getVarIndex

    public void setVariableNameInStruct(int col, String toThis) {
        allTheColumns.get(col).setVarLabel(toThis);
    }

    public int getSampleSize(int col) {
        return allTheColumns.get(col).getColumnSize();
    }

    public boolean getDataIsClean() { return dataIsClean; }

    public void setDataIsClean(boolean newStatus) { dataIsClean = newStatus; }

    public boolean getdataExists() { return dataExists; }

    public void setDataExists(boolean trueOrFalse) { dataExists = trueOrFalse; }

    public File getFileName() { return fileName; }

    public void setFileName(File file) { fileName = file; }

    public char getDelimiter() { return delimiter; }

    public void setDelimiter(char delimit) { delimiter = delimit; }

    public File getLastPath() { return lastPath; }

    public void setLastPath(File pathName) {
        String tempString = pathName.getParent();
        lastPath = new File(tempString);
    }

    //  getDummyCodes is doomed to extinction -- it is legacy code from the
    //  old structure of dialogs associated with grouping variables, i.e. those
    //  variables that indicated grouping.  This strategy will be re-worked
    //  and each statistical method will handle the grouping variable in its
    //  own way.
    
    public ArrayList<String> getDummyCodes(int groupingVar) {

        ArrayList<String> tempCodes = new ArrayList();
        ArrayList<String> sortedTempCodes = new ArrayList();
        ArrayList<String> dumsToReturn = new ArrayList();
        ArrayList<String> tempData = getSpreadsheetColumnAsStrings(groupingVar, -1, null);

        for (int ithDumDum = 0; ithDumDum < tempData.size(); ithDumDum++) {
            sortedTempCodes.add(tempData.get(ithDumDum));
        }

        Collections.sort(sortedTempCodes);
        for (String sortedTempCode : sortedTempCodes) {
        }

        dumsToReturn.add(sortedTempCodes.get(0));
        for (int i = 1; i < tempData.size(); i++) {
            if (!sortedTempCodes.get(i).equals(sortedTempCodes.get(i - 1))) {
                dumsToReturn.add(sortedTempCodes.get(i));
            }
        }
        return dumsToReturn;
    }  //getDummyCodes

    
    public ColumnOfData getSpreadsheetColumn(int dataVar) {
        return allTheColumns.get(dataVar);
    }

    // This is used by many methods: retrieve a data vector from the data matrix:
    public ArrayList<String> getSpreadsheetColumnAsStrings(int dataVar, int indicatorVar, String indicatorVal) {
        indicatorVar = getVariableIndex(indicatorVal);
        ArrayList<String> tempAL = new ArrayList();
        ColumnOfData tempColumn = allTheColumns.get(dataVar);
        return tempColumn.getTheCases();
    } // getVector

    public String truncString(String inpString, int maxLength) {
        int len = inpString.length();
        String temp = inpString;
        if (len > maxLength) {
            temp = inpString.substring(0, maxLength);
        }
        return temp;
    }

    public int getMaxVisCases() {return maxCasesInGrid; }
    public void setMaxVisCases(int toThisMany) { maxCasesInGrid = toThisMany; }
    
    public int getNCasesInStruct() { return nCasesInStruct; };
    public void setNCasesInStruct(int newNCases) { nCasesInStruct = newNCases; }

    public int getMaxVisVars() { return maxVarsInGrid; }
    public void setMaxVisVars(int toThisMany) { maxVarsInGrid = toThisMany; }

    public PositionTracker getPositionTracker() { return tracker; }

    public Data_Grid getTheGrid() { return dg; }

} 
