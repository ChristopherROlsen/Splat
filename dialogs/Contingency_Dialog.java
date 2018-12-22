/**************************************************
 *               Contingency_Dialog               *
 *                    05/15/18                    *
 *                     18:00                      *
 *************************************************/
package dialogs;

import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import splat.*;

public class Contingency_Dialog {
    // POJOs
    private boolean rawData = false;
    private boolean runTheAnalysis = false;
    
    private int numCols, numRows, colSelected, rowSelected;

    private double[][] expFreq, obsFreq;
    
    private String colName, rowName;
    private String[] colLabel, rowLabel;
    
    // POJOs / FX
    private TextField colNameText, rowNameText, numColsText, numRowsText;

    private Button nextButton1, cancelButton1, nextButton2, prevButton2, 
                   cancelButton2, nextButton3, prevButton3, cancelButton3;

    private CheckBox doES, doCrit, doSupp;
    
    private Scene diagScene1, diagScene2, diagScene3;
    private Stage diagStage;
    private TextField[][] obsFreqText;

    public void ShowContingencyDialog(Splat_DataManager myData, TextArea myText, boolean showOpt) {
        System.out.println("56 Contingency_Dialog");
        VBox mainPanel = new VBox();
        Insets pad10 = new Insets(10, 10, 10, 10);
        mainPanel.setPadding(pad10);
        mainPanel.setAlignment(Pos.CENTER);
        Label diagTitle = new Label("Contingency Test");
        diagTitle.getStyleClass().add("dialogTitle");
        diagTitle.setPadding(pad10);
        Separator sepTitle = new Separator();
        mainPanel.getChildren().addAll(diagTitle, sepTitle);

        ToggleGroup buttons1 = new ToggleGroup();
        RadioButton sumInp = new RadioButton("Manually input frequency data:");
        RadioButton rawInp = new RadioButton("Analyze frequencies in a variable:");
        sumInp.setToggleGroup(buttons1);
        rawInp.setToggleGroup(buttons1);
        sumInp.setSelected(true);

        HBox centerPanel = new HBox(15);
        centerPanel.setAlignment(Pos.TOP_CENTER);
        centerPanel.setPadding(new Insets(10, 0, 0, 0));

        if (showOpt) {
            VBox optPanel = new VBox(10);
            optPanel.setAlignment(Pos.CENTER_LEFT);
            doES = new CheckBox("Effect Size Statistic");
            //doES.setSelected(true);
            doCrit = new CheckBox("Critical Values");
            //doCrit.setSelected(true);
            doSupp = new CheckBox("Supplemental Statistics");
            optPanel.getChildren().addAll(doES, doCrit, doSupp);
            centerPanel.getChildren().add(optPanel);
        }

        VBox leftPanel = new VBox();
        VBox leftPanB = new VBox(10);
        leftPanB.setPadding(new Insets(15, 0, 0, 20));

        Label colNameLabel = new Label("Column Heading:");
        colNameText = new TextField();
        colNameText.setPrefWidth(200);
        Label rowNameLabel = new Label("Row Heading:");
        rowNameText = new TextField();
        rowNameText.setPrefWidth(200);
        Label numColLabel = new Label("Number of Columns (max = 5):");
        numColsText = new TextField();
        numColsText.setPrefWidth(100);
        Label numRowLabel = new Label("Number of Rows:");
        numRowsText = new TextField();
        numRowsText.setPrefWidth(100);
        leftPanB.getChildren().addAll(colNameLabel, colNameText,
                rowNameLabel, rowNameText,
                numColLabel, numColsText,
                numRowLabel, numRowsText);
        leftPanel.getChildren().addAll(sumInp, leftPanB);

        VBox rightPanel = new VBox();
        VBox rightPanB = new VBox(5);
        rightPanB.setPadding(new Insets(15, 0, 0, 20));
        Label msgLabel = new Label("Choose two variables:");
        Splat_VarCheckList varList = new Splat_VarCheckList(myData, false, null, null);
        rightPanB.getChildren().addAll(msgLabel, varList.getPane());
        rightPanel.getChildren().addAll(rawInp, rightPanB);
        rightPanB.setDisable(true);
        centerPanel.getChildren().add(leftPanel);
        centerPanel.getChildren().add(rightPanel);
        centerPanel.setPadding(new Insets(10, 0, 10, 0));
        mainPanel.getChildren().add(centerPanel);

        HBox buttonPanel = new HBox(15);
        buttonPanel.setPadding(pad10);
        buttonPanel.setAlignment(Pos.CENTER);
        nextButton1 = new Button("Next Step");
        cancelButton1 = new Button("Cancel");
        buttonPanel.getChildren().addAll(nextButton1, cancelButton1);
        Separator sep = new Separator();
        mainPanel.getChildren().addAll(sep, buttonPanel);

        diagScene1 = new Scene(mainPanel);
        String css = getClass().getResource("/css/StatDialogs.css").toExternalForm();
        diagScene1.getStylesheets().add(css);

        cancelButton1.setOnAction((ActionEvent event) -> {
            diagStage.close();
        });

        rawInp.setOnAction((ActionEvent event) -> {
            rightPanB.setDisable(false);
            leftPanB.setDisable(true);
            rawData = true;
        });

        sumInp.setOnAction((ActionEvent event) -> {
            rightPanB.setDisable(true);
            leftPanB.setDisable(false);
            rawData = false;
        });

        nextButton1.setOnAction((ActionEvent event) -> {

            if (rawData) { // compute freqs from data:

                boolean ok = true;

                if (varList.getSelected().size() != 2) {
                    MyDialogs newDiag = new MyDialogs();
                    newDiag.Msg(2, "Variable not selected",
                            "You must select two variables.");
                    ok = false;
                } else {
                    colSelected = varList.getSelected().get(0);
                    rowSelected = varList.getSelected().get(1);
                }

                if (ok) {

                    numCols = myData.numDistinctVals(colSelected);
                    numRows = myData.numDistinctVals(rowSelected);
                    colName = myData.getVariableName(colSelected);
                    rowName = myData.getVariableName(rowSelected);

                    // ArrayList<String> colNames = myData.getDummyCodes(colSelected);
                    // ArrayList<String> rowNames = myData.getDummyCodes(rowSelected);
                    ArrayList<String> colNames = myData.getAllTheColumns().get(colSelected).getDistinctValues();
                    ArrayList<String> rowNames = myData.getAllTheColumns().get(rowSelected).getDistinctValues();
                    colLabel = new String[numCols];
                    rowLabel = new String[numRows];   
                    //System.out.println("cd 179, numCols = " + numCols);
                    //System.out.println("cd 180, numRows = " + numRows);
                    //System.out.println("cd 181, colNames = " + colNames);
                    //System.out.println("cd 182, rowNames = " + rowNames);                    
                    for (int i = 0; i < numCols; i++) {
                        //System.out.println("cd 184, i = " + i);    
                        //System.out.println("cd 185, colNames.get(i) = " + colNames.get(i));                        
                        colLabel[i] = colNames.get(i);
                        //System.out.println("cd 187, colLabel[i] = " + colLabel[i]);
                    }
                    for (int i = 0; i < numRows; i++) {
                        rowLabel[i] = rowNames.get(i);
                        System.out.println("cd 191, rowLabel[i] = " + rowLabel[i]);
                    }

                    obsFreq = new double[numCols][numRows];
                    for (int i = 0; i < numCols; i++) {
                        for (int j = 0; j < numRows; j++) {
                            obsFreq[i][j] = 0.0;
                        }
                    }

                    for (int i = 0; i < myData.getSampleSize(colSelected); i++) {
                        for (int col = 0; col < numCols; col++) {
                            for (int row = 0; row < numRows; row++) {
                                if ((myData.getDataElementFromStruct(colSelected, i).equals(colLabel[col]))
                                        && (myData.getDataElementFromStruct(rowSelected, i).equals(rowLabel[row]))) {
                                    obsFreq[col][row]++;
                                }
                            }
                        }
                    }

                    ShowStep3(myText);

                } // ok

            } else { // enter summary data

                boolean ok = true;

                if (colNameText.getText().equals("")) {
                    MyDialogs newDiag = new MyDialogs();
                    newDiag.Msg(2, "Input Error", "The column heading is blank.");
                    ok = false;
                } else {
                    colName = colNameText.getText();
                }

                if (rowNameText.getText().equals("") && ok) {
                    MyDialogs newDiag = new MyDialogs();
                    newDiag.Msg(2, "Input Error", "The row heading is blank.");
                    ok = false;
                } else {
                    rowName = rowNameText.getText();
                }

                if (numColsText.getText().equals("") && ok) {
                    MyDialogs newDiag = new MyDialogs();
                    newDiag.Msg(2, "Input Error", "The number of columns is blank.");
                    ok = false;
                }

                if (numRowsText.getText().equals("") && ok) {
                    MyDialogs newDiag = new MyDialogs();
                    newDiag.Msg(2, "Input Error", "The number of rows is blank.");
                    ok = false;
                }

                if (ok) {
                    try {
                        Integer tempNumber = Integer.valueOf(numColsText.getText());
                        numCols = tempNumber;
                    } catch (NumberFormatException num_e) {
                        ok = false;
                        MyDialogs newDiag = new MyDialogs();
                        newDiag.Msg(2, "Input Error", "You have entered non-numeric data for the number of columns.");
                    }
                }

                if (ok) {
                    try {
                        Integer tempNumber = Integer.valueOf(numRowsText.getText());
                        numRows = tempNumber;
                    } catch (NumberFormatException num_e) {
                        ok = false;
                        MyDialogs newDiag = new MyDialogs();
                        newDiag.Msg(2, "Input Error", "You have entered non-numeric data for the number of rows.");
                    }
                }

                if ((numCols < 2) && ok) {
                    ok = false;
                    MyDialogs newDiag = new MyDialogs();
                    newDiag.Msg(2, "Input Error", "The number of columns must be 2 or more.");
                }
                
                if ((numRows < 2) && ok) {
                    ok = false;
                    MyDialogs newDiag = new MyDialogs();
                    newDiag.Msg(2, "Input Error", "The number of rows must be 2 or more.");
                }

                if (ok) {
                    ShowStep2(myText);
                }
            }

        });

        diagStage = new Stage();
        diagStage.setScene(diagScene1);
        diagStage.setTitle("Step #1");
        diagStage.showAndWait();

    } // layoutScene1

    private void ShowStep2(TextArea myText) {
        System.out.println("301 Contingency_Dialog");
        VBox mainPanel = new VBox();
        Insets pad10 = new Insets(10, 10, 10, 10);
        mainPanel.setPadding(pad10);
        mainPanel.setAlignment(Pos.CENTER);
        Label diagTitle = new Label("Enter Your Column and Row Labels");
        diagTitle.getStyleClass().add("dialogTitle");
        diagTitle.setPadding(new Insets(0, 0, 10, 0));
        Separator sepTitle = new Separator();
        mainPanel.getChildren().addAll(diagTitle, sepTitle);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);
        Label colHead = new Label(colName);
        grid.add(colHead, 1, 0, numCols, 1);
        GridPane.setHalignment(colHead, HPos.CENTER);
        TextField[] colLabelText = new TextField[numCols];
        for (int i = 0; i < numCols; i++) {
            colLabelText[i] = new TextField();
            grid.add(colLabelText[i], i + 1, 1);
        }
        Label rowHead = new Label(rowName + ":");
        grid.add(rowHead, 0, 1);
        TextField[] rowLabelText = new TextField[numRows];
        for (int i = 0; i < numRows; i++) {
            rowLabelText[i] = new TextField();
            grid.add(rowLabelText[i], 0, i + 2);
        }
        grid.setPadding(new Insets(10, 10, 10, 10));

        ScrollPane scrollGrid = new ScrollPane(grid);
        Separator sep2 = new Separator();
        mainPanel.getChildren().addAll(scrollGrid, sep2);

        HBox buttonPanel = new HBox(15);
        buttonPanel.setPadding(pad10);
        buttonPanel.setAlignment(Pos.CENTER);
        nextButton2 = new Button("Next Step");
        prevButton2 = new Button("Previous Step");
        cancelButton2 = new Button("Cancel");
        buttonPanel.getChildren().addAll(nextButton2, prevButton2, cancelButton2);
        mainPanel.getChildren().add(buttonPanel);

        diagScene2 = new Scene(mainPanel);
        String css = getClass().getResource("/css/StatDialogs.css").toExternalForm();
        diagScene2.getStylesheets().add(css);

        cancelButton2.setOnAction((ActionEvent event) -> {
            diagStage.close();
        });

        prevButton2.setOnAction((ActionEvent event) -> {
            diagStage.setScene(diagScene1);
            diagStage.setTitle("Step #1");
        });

        nextButton2.setOnAction((ActionEvent event) -> {

            boolean ok = true;
            boolean okRow = true, okCol = true;

            colLabel = new String[numCols];

            for (int i = 0; i < numCols; i++) {

                if (colLabelText[i].getText().equals("")) {
                    ok = false;
                    okCol = false;
                } else {
                    colLabel[i] = colLabelText[i].getText();
                }
            }

            if (!okCol) {
                MyDialogs newDiag = new MyDialogs();
                newDiag.Msg(2, "Input Error", "One or more of the column labels is blank.");
            }

            rowLabel = new String[numRows];

            if (ok) {
                for (int i = 0; i < numRows; i++) {

                    if (rowLabelText[i].getText().equals("")) {
                        ok = false;
                        okRow = false;
                    } else {
                        rowLabel[i] = rowLabelText[i].getText();
                    }
                }
            }
            if (!okRow) {
                MyDialogs newDiag = new MyDialogs();
                newDiag.Msg(2, "Input Error", "One or more of the row labels is blank.");
            }

            if (ok) {
                ShowStep3(myText);
            }

        });

        diagStage.setScene(diagScene2);
        diagStage.setTitle("Step #2");
        diagStage.setMaxHeight(600);
        diagStage.setMaxWidth(1000);

    } // layoutScene2

    private void ShowStep3(TextArea myText) {
        System.out.println("412 Contingency_Dialog");
        VBox mainPanel = new VBox();
        Insets pad10 = new Insets(10, 10, 10, 10);
        mainPanel.setPadding(pad10);
        mainPanel.setAlignment(Pos.CENTER);
        Label diagTitle = new Label("Enter Your Observed Frequencies");
        if (rawData) {
            diagTitle.setText("Check Your Observed Frequencies");
        }
        diagTitle.getStyleClass().add("dialogTitle");
        diagTitle.setPadding(new Insets(0, 0, 10, 0));
        Separator sepTitle = new Separator();
        mainPanel.getChildren().addAll(diagTitle, sepTitle);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);
        Label colHead = new Label(colName);
        grid.add(colHead, 1, 0, numCols, 1);
        GridPane.setHalignment(colHead, HPos.CENTER);
        Label[] colLabelText = new Label[numCols];
        for (int i = 0; i < numCols; i++) {
            colLabelText[i] = new Label(colLabel[i]);
            grid.add(colLabelText[i], i + 1, 1);
            GridPane.setHalignment(colLabelText[i], HPos.CENTER);
        }
        Label rowHead = new Label(rowName + ":");
        grid.add(rowHead, 0, 1);
        Label[] rowLabelText = new Label[numRows];  // <------------------------
        for (int i = 0; i < numRows; i++) {
            System.out.println("cd 437, rowLabel[i] = " + rowLabel[i]);
            rowLabelText[i] = new Label(rowLabel[i]);
            grid.add(rowLabelText[i], 0, i + 2);
        }
        TextField[][] obsFreqText = new TextField[numCols][numRows];
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                obsFreqText[col][row] = new TextField();
                grid.add(obsFreqText[col][row], col + 1, row + 2);
            }
        }

        if (rawData) {
            for (int row = 0; row < numRows; row++) {
                for (int col = 0; col < numCols; col++) {
                    obsFreqText[col][row].setText(String.format("%1.0f", obsFreq[col][row]));
                }
            }
        }

        grid.setPadding(new Insets(10, 10, 10, 10));

        ScrollPane scrollGrid = new ScrollPane(grid);
        Separator sep2 = new Separator();
        mainPanel.getChildren().addAll(scrollGrid, sep2);

        HBox buttonPanel = new HBox(15);
        buttonPanel.setPadding(pad10);
        buttonPanel.setAlignment(Pos.CENTER);
        nextButton3 = new Button("Compute");
        prevButton3 = new Button("Previous Step");
        cancelButton3 = new Button("Cancel");
        buttonPanel.getChildren().addAll(nextButton3, prevButton3, cancelButton3);
        mainPanel.getChildren().add(buttonPanel);

        diagScene3 = new Scene(mainPanel);
        String css = getClass().getResource("/css/StatDialogs.css").toExternalForm();
        diagScene3.getStylesheets().add(css);

        cancelButton3.setOnAction((ActionEvent event) -> {
            diagStage.close();
        });

        prevButton3.setOnAction((ActionEvent event) -> {
            diagStage.setScene(diagScene2);
            diagStage.setTitle("Step #2");
        });

        nextButton3.setOnAction((ActionEvent event) -> {

            boolean ok1 = true;
            boolean ok2 = true;

            for (int row = 0; row < numRows; row++) {
                for (int col = 0; col < numCols; col++) {
                    if (obsFreqText[col][row].getText().equals("")) {
                        ok1 = false;
                    } else {
                    }
                }
            }

            if (!ok1) {
                MyDialogs newDiag = new MyDialogs();
                newDiag.Msg(2, "Input Error", "At least one of the observed frequencies is blank.");
            }

            obsFreq = new double[numCols][numRows];

            if (ok1) {
                for (int row = 0; row < numRows; row++) {
                    for (int col = 0; col < numCols; col++) {
                        try {
                            Double tempNumber = Double.valueOf(obsFreqText[col][row].getText());
                            obsFreq[col][row] = tempNumber;
                        } catch (NumberFormatException num_e) {
                            ok2 = false;
                        }
                    }
                }
            }

            if (!ok2) {
                MyDialogs newDiag = new MyDialogs();
                newDiag.Msg(2, "Input Error", "At least one of the observed frequencies is not numeric.");
            }

            boolean ok3 = true;

            if (ok1 && ok2 && ok3) {
                runTheAnalysis = true;
                diagStage.close();
            }

        });

        diagStage.setScene(diagScene3);
        diagStage.setTitle("Step #3");
        diagStage.setMaxHeight(600);
        diagStage.setMaxWidth(1000);

    } // layoutScene3

    public String getColName() {
        return colName;
    }

    public String getRowName() {
        return rowName;
    }

    public String[] getColLabels() {
        return colLabel;
    }

    public String[] getRowLabels() {
        return rowLabel;
    }

    public int getNumCols() {
        return numCols;
    }

    public int getNumRows() {
        return numRows;
    }

    public boolean runAnalysis() {
        return runTheAnalysis;
    }

    public double[][] getObsFreq() {
        return obsFreq;
    }

    public double[][] getExpFreq() {
        return expFreq;
    }

    public boolean showES() {
        return doES.isSelected();
    }

    public boolean showCrit() {
        return doCrit.isSelected();
    }

    public boolean showSupp() {
        return doSupp.isSelected();
    }

}
