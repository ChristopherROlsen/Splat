/**************************************************
 *                 MultReg_Dialog                 *
 *                    11/12/18                    *
 *                     18:00                      *
 *************************************************/
package dialogs;

import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import splat.*;
import genericClasses.*;

public class MultReg_Dialog extends Splat_Dialog {
    // POJOs
    private boolean runAnalysis = false;
    private boolean ok = true; 
    
    private int numSelected;
    private final int minVars = 2, maxIVs = 12;    
    private int dvSelected = -1;    
    private final int minSampleSize = 5;
    private ArrayList<Integer> ivSelected; 

    private String DVText;    
    private ArrayList<String> Y, varLabel;    
    private ArrayList<String>[] X;    
    
    // My classes
    ColumnOfData tempCol;
    private MyDialogs msgDiag;    
    ArrayList<ColumnOfData> theMultRegData;
    
    // POJOs / FX
    private Stage dialogStage;

    public MultReg_Dialog(Splat_DataManager myData) {
        super(myData);
        msgDiag = new MyDialogs();
        ivSelected = new ArrayList();
        varLabel = new ArrayList();

        VBox mainPanel = new VBox();
        mainPanel.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Multiple Regression");
        titleLabel.getStyleClass().add("dialogTitle");
        titleLabel.setPadding(new Insets(10, 10, 10, 10));
        Separator sepTitle = new Separator();
        mainPanel.getChildren().addAll(titleLabel, sepTitle);

        HBox middlePanel = new HBox();
        middlePanel.setAlignment(Pos.CENTER);

        VBox vList1 = new VBox();
        vList1.setAlignment(Pos.TOP_LEFT);
        Label vLabel1 = new Label("Variables in Data:");
        vLabel1.setPadding(new Insets(0, 0, 5, 0));
        Splat_VarList varList1 = new Splat_VarList(myData, false, null, null);
        vList1.getChildren().add(vLabel1);
        vList1.getChildren().add(varList1.getPane());
        vList1.setPadding(new Insets(0, 10, 0, 10));
        middlePanel.getChildren().add(vList1);

        Button selectDV = new Button("===>");
        Button selectIV = new Button("===>");

        VBox vList2 = new VBox();
        vList2.setAlignment(Pos.TOP_LEFT);
        Label vLabel2 = new Label("Predictor Variable(s)");
        vLabel2.setPadding(new Insets(0, 0, 5, 0));
        Splat_VarList varList2 = new Splat_VarList(myData, false, 125.0, 125.0);
        varList2.clearList();
        vList2.getChildren().add(vLabel2);
        vList2.getChildren().add(varList2.getPane());

        VBox vList3 = new VBox();
        vList3.setAlignment(Pos.TOP_LEFT);
        Label vLabel3 = new Label("Outcome Variable");
        vLabel3.setPadding(new Insets(0, 0, 5, 0));
        TextField ivText = new TextField("");
        ivText.setPrefWidth(125.0);
        vList3.getChildren().addAll(vLabel3, ivText);

        GridPane rightPanel = new GridPane();
        rightPanel.setHgap(10);
        rightPanel.setVgap(15);
        rightPanel.add(selectDV, 0, 0);
        rightPanel.add(vList3, 1, 0);
        rightPanel.add(selectIV, 0, 1);
        rightPanel.add(vList2, 1, 1);
        GridPane.setValignment(selectDV, VPos.BOTTOM);
        GridPane.setValignment(selectIV, VPos.CENTER);
        rightPanel.setPadding(new Insets(0, 10, 0, 0));

        middlePanel.getChildren().add(rightPanel);
        middlePanel.setPadding(new Insets(10, 0, 10, 0));

        mainPanel.getChildren().add(middlePanel);
        Separator sepButtons = new Separator();
        mainPanel.getChildren().add(sepButtons);

        HBox buttonPanel = new HBox(10);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.setPadding(new Insets(10, 10, 10, 10));

        okButton.setText("Compute");
        cancelButton.setText("Cancel");
        Button resetButton = new Button("Reset");
        buttonPanel.getChildren().addAll(okButton, cancelButton, resetButton);

        mainPanel.getChildren().add(buttonPanel);
        Scene myScene = new Scene(mainPanel);
        String css = getClass().getResource("/css/StatDialogs.css").toExternalForm();
        myScene.getStylesheets().add(css);

        setScene(myScene);

        resetButton.setOnAction((ActionEvent event) -> {
            varList1.resetList();
            varList2.clearList();
            ivText.setText("");
            ivSelected.clear();
            numSelected = 0;
        });

        selectIV.setOnAction((ActionEvent event) -> {
            ArrayList<String> selected = varList1.getNamesSelected();
            ok = true;
            for (String tmpVar : selected) {
                if (!myData.getVariableIsNumeric(myData.getVariableIndex(tmpVar))) {
                    msgDiag.Msg(2, "Non Numeric Data",
                            "You have selected a variable that is not numeric. "
                            + "This procedure requires numeric data.");
                    ok = false;
                }
            }

            int tempNum = varList2.getVarIndices().size();

            if ((ok) && (tempNum < maxIVs)) {
                varList2.addVarName(selected);
                varList1.delVarName(selected);
            }
        });

        selectDV.setOnAction((ActionEvent event) -> {
            ok = true;
            if (varList1.getNamesSelected().size() == 1) {
                DVText = varList1.getNamesSelected().get(0);
                dvSelected = myData.getVariableIndex(DVText);
                ivText.setText(DVText);
                varList1.delVarName(varList1.getNamesSelected());
            }
            if (!myData.getVariableIsNumeric(dvSelected)) {
                msgDiag.Msg(2, "Non Numeric Data",
                        "You have selected an outcome variable that is not numeric. "
                        + "This procedure requires numeric data.");
                ok = false;
            }

        });

        okButton.setOnAction((ActionEvent event) -> {

            ok = true;

            ivSelected = varList2.getVarIndices();
            numSelected = ivSelected.size();
            int dvPresent = 0;
            if (dvSelected > -1) {
                dvPresent = 1;
            }

            if ((numSelected + dvPresent) < minVars) {
                msgDiag.Msg(2, "Variable Selection",
                        "You must select at least " + minVars + " variable(s) to analyze.");
                ok = false;
            }
            
            theMultRegData = new ArrayList<>();

            if (ok) {

                X = new ArrayList[numSelected];
                Y = new ArrayList();
                Y = myData.getSpreadsheetColumnAsStrings(dvSelected, -1, null);
                varLabel.add(myData.getVariableName(dvSelected));
                
                // The label for the variable and the ArrayList of strings
                tempCol = new ColumnOfData(myData.getVariableName(dvSelected), Y);
                
                // Column 0 contains the Y variable
                theMultRegData.add(tempCol);
                for (int j = 0; j < numSelected; j++) {
                    varLabel.add(myData.getVariableName(ivSelected.get(j)));
                    X[j] = new ArrayList();
                    X[j] = myData.getSpreadsheetColumnAsStrings(ivSelected.get(j), -1, null);
                    tempCol = new ColumnOfData(myData.getVariableName(ivSelected.get(j)), X[j]);
                    theMultRegData.add(tempCol);
                    if (X[j].size() < minSampleSize) {
                        ok = false;
                    }
                }

                if (Y.size() < minSampleSize) {
                    ok = false;
                }

                if (!ok) {
                    MyDialogs msgDiag = new MyDialogs();
                    msgDiag.Msg(1, "Low Sample Size", "This procedure requires a sample size of "
                            + minSampleSize + " in each group/variable.");
                } else {
                    returnStatus = "Ok";
                    close();
                }

            }

        });

        setTitle("Multiple Regression");
        showAndWait();

    } // twoSampDialog
    
    public ArrayList<ColumnOfData> getData() { 
        return theMultRegData; }

    public ArrayList<String> getVarLabels() {
        return varLabel;
    }

    public ArrayList<String>[] getXMatrix() {
        return X;
    }

    public ArrayList<String> getYMatrix() {
        return Y;
    }

    public int getNumVars() {
        return numSelected;
    }

    public boolean runTheAnalysis() {
        return runAnalysis;
    }
    
    public boolean getOK() {return true; }

} // class
