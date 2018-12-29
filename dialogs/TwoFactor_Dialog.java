/**************************************************
 *              TwoFactor_Dialog                  *
 *                  11/04/18                      *
 *                   18:00                        *
 *************************************************/
package dialogs;

import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import splat.*;

public class TwoFactor_Dialog extends Splat_Dialog {
    //  POJOs
    private boolean codedData = false;    
    private boolean readyForAnalysis = false;
    
    private final int minSampleSize = 3;
    private int currentCol, currentRow, IV1num, IV2num, DVnum, numCols, numRows;
    private int[][] varSelected;
    
    private String colName, rowName, designType;
    private String[] colLabel, rowLabel;
    private String[][] cellLabel;
    private final String normStyle = "-fx-control-inner-background: white;";
    private final String inputStyle = "-fx-control-inner-background: bisque;";
    private ArrayList<String>[][] data;    
    
    
    // My classes
    
    // POJOs / FX
    public CheckBox doES, doCrit, doSupp, doChart;
    private Scene diagScene0, diagScene1;
    private Stage diagStage;
    private TextField colNameText, rowNameText, numColsText, numRowsText;
    private TextField[][] varNameText;
    
    public TwoFactor_Dialog( Data_Manager dm, String typeOfTwoFactor) {
        super(dm);
        System.out.println("56 TwoFactor_Dialog");
       designType = typeOfTwoFactor; 
    }

    public void Step0() { 
        System.out.println("60 TwoFactor_Dialog");
        diagStage = new Stage();
        diagStage.setScene(diagScene0);
        diagStage.setTitle("Two-way ANOVA");
        ShowStep1();
        diagStage.showAndWait();

    } // showStep0
    
    public void ShowStep1() {
        System.out.println("71 TwoFactor_Dialog");
        VBox mainPanel = new VBox(10);
        mainPanel.setAlignment(Pos.CENTER);
        mainPanel.setPadding(new Insets(10, 10, 10, 10));

        Label titleLabel = new Label("Two-Factor ANOVA");
        titleLabel.getStyleClass().add("dialogTitle");
        Separator sepTitle = new Separator();
        mainPanel.getChildren().addAll(titleLabel, sepTitle);

        HBox middlePanel = new HBox(15);
        middlePanel.setAlignment(Pos.CENTER);
        VBox leftPanel = new VBox(10);
        leftPanel.setAlignment(Pos.CENTER_LEFT);

        doChart = new CheckBox("Show Line Chart");
        doES = new CheckBox("Effect Size Statistics");
        doCrit = new CheckBox("Critical Values");
        doSupp = new CheckBox("Supplemental Statistics");
        leftPanel.getChildren().addAll(doChart, doES, doCrit, doSupp);
        middlePanel.getChildren().add(leftPanel);

        VBox vList1 = new VBox();
        vList1.setAlignment(Pos.TOP_LEFT);
        Label vlable1 = new Label("Variables in Data:");
        vlable1.setPadding(new Insets(0, 0, 5, 0));
        Var_List varList1 = new Var_List(dm, false, null, null);
        vList1.getChildren().add(vlable1);
        vList1.getChildren().add(varList1.getPane());
        vList1.setPadding(new Insets(0, 10, 0, 10));
        middlePanel.getChildren().add(vList1);

        Button selectExplanVar_1 = new Button("===>");
        Button selectExplanVar_2 = new Button("===>");
        Button selectDV = new Button("===>");

        VBox IV1Panel = new VBox();
        IV1Panel.setAlignment(Pos.TOP_LEFT);
        Label IV1Label = new Label("Choose IV #1:");
        IV1Label.setPadding(new Insets(0, 0, 5, 0));
        TextField IV1Text = new TextField("");
        IV1Text.setPrefWidth(125.0);
        IV1Panel.getChildren().addAll(IV1Label, IV1Text);

        VBox IV2Panel = new VBox();
        IV2Panel.setAlignment(Pos.TOP_LEFT);
        Label IV2Label = new Label("Choose IV #2:");
        IV2Label.setPadding(new Insets(0, 0, 5, 0));
        TextField IV2Text = new TextField("");
        IV2Text.setPrefWidth(125.0);
        IV2Panel.getChildren().addAll(IV2Label, IV2Text);

        VBox DVPanel = new VBox();
        DVPanel.setAlignment(Pos.TOP_LEFT);
        Label DVLabel = new Label("Choose DV:");
        DVLabel.setPadding(new Insets(0, 0, 5, 0));
        TextField DVText = new TextField("");
        DVText.setPrefWidth(125.0);
        DVPanel.getChildren().addAll(DVLabel, DVText);

        GridPane rightPanel = new GridPane();
        rightPanel.setHgap(10);
        rightPanel.setVgap(15);
        rightPanel.add(selectExplanVar_1, 0, 0);
        rightPanel.add(IV1Panel, 1, 0);
        rightPanel.add(selectExplanVar_2, 0, 1);
        rightPanel.add(IV2Panel, 1, 1);
        rightPanel.add(selectDV, 0, 2);
        rightPanel.add(DVPanel, 1, 2);
        GridPane.setValignment(selectExplanVar_1, VPos.BOTTOM);
        GridPane.setValignment(selectExplanVar_2, VPos.BOTTOM);
        GridPane.setValignment(selectDV, VPos.BOTTOM);

        middlePanel.getChildren().add(rightPanel);

        mainPanel.getChildren().add(middlePanel);
        Separator sepButtons = new Separator();
        mainPanel.getChildren().add(sepButtons);

        HBox buttonPanel = new HBox(10);
        buttonPanel.setAlignment(Pos.CENTER);

        Button prevButton1 = new Button("Previous Step");
        Button nextButton1 = new Button("Compute");
        Button cancelButton = new Button("Cancel");
        Button resetButton = new Button("Reset");
        buttonPanel.getChildren().addAll(nextButton1, prevButton1, cancelButton, resetButton);

        mainPanel.getChildren().add(buttonPanel);
        diagScene1 = new Scene(mainPanel);
        String css = getClass().getResource("/css/StatDialogs.css").toExternalForm();
        diagScene1.getStylesheets().add(css);

        diagScene1.setOnKeyReleased(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                String kCode = ke.getCode().toString();
                if (kCode.equals("ESCAPE")) {
                    returnStatus = "Cancel";
                    diagStage.close();
                }
            }
        });

        cancelButton.setOnAction((ActionEvent event) -> {
            diagStage.close();
        });

        prevButton1.setOnAction((ActionEvent event) -> {
            diagStage.setScene(diagScene0);
            diagStage.setTitle("Data Organization");
        });

        resetButton.setOnAction((ActionEvent event) -> {
            varList1.resetList();
            IV1Text.setText("");
            IV2Text.setText("");
            DVText.setText("");
        });

        selectExplanVar_1.setOnAction((ActionEvent event) -> {
            boolean ok = true;
            if (varList1.getNamesSelected().size() == 1) {
                String IVtext = varList1.getNamesSelected().get(0);
                IV1num = dm.getVariableIndex(IVtext);
                int numGroups = dm.numDistinctVals(IV1num);
                if (numGroups < 2) {
                    MyDialogs msgDiag = new MyDialogs();
                    msgDiag.Msg(2, "Levels of IV?",
                            "Each IV must have 2 or more different values.");
                    ok = false;
                } else if (numGroups > 99) {    // To handle RCB
                    MyDialogs msgDiag = new MyDialogs();
                    msgDiag.Msg(2, "Levels of IV?",
                            "Each IV must have no more than 5 different values.");
                    ok = false;
                } else {
                    IV1Text.setText(IVtext);
                    varList1.delVarName(varList1.getNamesSelected());
                    rowName = IVtext;
                }
            }

        });

        selectExplanVar_2.setOnAction((ActionEvent event) -> {
            boolean ok = true;
            if (varList1.getNamesSelected().size() == 1) {
                String IVtext = varList1.getNamesSelected().get(0);
                IV2num = dm.getVariableIndex(IVtext);
                int numGroups = dm.numDistinctVals(IV2num);
                if (numGroups < 2) {
                    MyDialogs msgDiag = new MyDialogs();
                    msgDiag.Msg(2, "Levels of IV?",
                            "Each IV must have 2 or more different values.");
                    ok = false;
                } else if (numGroups > 99) {    //  To handle RCB
                    MyDialogs msgDiag = new MyDialogs();
                    msgDiag.Msg(2, "Levels of IV?",
                            "Each IV must have no more than 5 different values.");
                    ok = false;
                } else {
                    IV2Text.setText(IVtext);
                    varList1.delVarName(varList1.getNamesSelected());
                    colName = IVtext;
                }
            }
        });

        selectDV.setOnAction((ActionEvent event) -> {
            String tempVar;
            if (varList1.getNamesSelected().size() == 1) {
                tempVar = varList1.getNamesSelected().get(0);
                DVnum = dm.getVariableIndex(tempVar);
                boolean ok = true;
                if (!dm.getVariableIsNumeric(dm.getVariableIndex(tempVar))) {
                    MyDialogs msgDiag = new MyDialogs();
                    msgDiag.Msg(2, "Non Numeric Data",
                            "You have selected a variable that is not numeric. "
                            + "This procedure requires a numeric DV.");
                    ok = false;
                }

                DVText.setText(tempVar);
                varList1.delVarName(varList1.getNamesSelected());
            }
        });

        nextButton1.setOnAction((ActionEvent event) -> {

            boolean ok = true;
            if ((IV1Text.getText().equals(""))
                    || (IV2Text.getText().equals(""))
                    || (DVText.getText().equals(""))) {
                ok = false;
                MyDialogs msgDiag = new MyDialogs();
                msgDiag.Msg(2, "Missing Variable(s)",
                        "You must specify two IVs and one DV to use this procedure.");
            }

            if (ok) {

                ArrayList<String> tmpCodes1;
                tmpCodes1 = dm.getDummyCodes(IV1num);
                numRows = tmpCodes1.size();
                rowLabel = new String[numRows];
                for (int i = 0; i < numRows; i++) {
                    rowLabel[i] = tmpCodes1.get(i);
                }

                ArrayList<String> tmpCodes2;
                tmpCodes2 = dm.getDummyCodes(IV2num);
                numCols = tmpCodes2.size();
                colLabel = new String[numCols];
                for (int i = 0; i < numCols; i++) {
                    colLabel[i] = tmpCodes2.get(i);
                }
                
                int tempN = dm.getSampleSize(DVnum);

                data = new ArrayList[numCols][numRows];
                for (int row = 0; row < numRows; row++) {
                    for (int col = 0; col < numCols; col++) {
                        data[col][row] = new ArrayList();
                        for (int i = 0; i < dm.getSampleSize(DVnum); i++) {
                            if ((dm.getDataElementFromStruct(IV1num, i).equals(rowLabel[row]))
                                    && (dm.getDataElementFromStruct(IV2num, i).equals(colLabel[col]))) {
                                data[col][row].add(dm.getDataElementFromStruct(DVnum, i));
                            }
                        }
                    }
                }

                readyForAnalysis = true;
                codedData = true;
                diagStage.close();
            }

        });

        diagStage.setScene(diagScene1);
        diagStage.setTitle("Step #1");

    } // step1Dialog
    
    public int getNumRows() {return numRows; }

    public int getNumCols() { return numCols; }

    public ArrayList<String>[][] getData() {return data; }

    public String getRowName() {return rowName; }

    public String getColName() { return colName; }

    public String[] getRowLabels() {return rowLabel; }

    public String[] getColLabels() { return colLabel; }

    public boolean amReady() { return readyForAnalysis; }
    
    public boolean isCoded () { return codedData; }

    public String[][] getCellLabels() { return cellLabel; }

    private String truncString(String inpString, int maxLength) {
        int len = inpString.length();
        String temp = inpString;
        if (len > maxLength) {
            temp = inpString.substring(0, maxLength);
        }
        return temp;
    } // truncString

} // TwoFactor_Dialog Class