/**************************************************
 *            ANOVA1_NotStacked_Dialog            *
 *                    12/01/18                    *
 *                     12:00                      *
 *************************************************/

// ************************************************
// *         Called by ANOVA1_Procedure           *
// ************************************************
package dialogs;

import genericClasses.ColumnOfData;
import java.util.ArrayList;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import splat.*;

public class ANOVA1_NotStacked_Dialog extends Splat_Dialog {

    // POJOs
    private boolean runAnalysis = false;
    private boolean ok = true;
    private int numSelected, minVars, maxVars, quantVar;
    Separator sepTitle, sepDirections;
    TextField tfExplanVar, tfResponseVar;
    
    private ArrayList<Integer> al_IndexVarsSelected;

    private String quantVarText;
    
    private ArrayList<String> al_QuantsSelected, str_ChosenLabels, preData;
    
    // My classes
    private ColumnOfData col_UnivData;
    private ArrayList<ColumnOfData> dataSet;
    private MyDialogs msgDiag;
    
    
    // FX objects
    private HBox middlePanel;
    private Label lblTitle, vLabel1, vLabel2;
    private Stage dialogStage;
    
    // ******************************************************************
    // *            The data are in separate columns                    *
    // ******************************************************************
    
    public ANOVA1_NotStacked_Dialog(Splat_DataManager dm) {
        this.dm = dm;
        createANOVA_NS_Dialog();
    }

    private void createANOVA_NS_Dialog() {
        msgDiag = new MyDialogs();
        str_ChosenLabels = new ArrayList();

        VBox mainPanel = new VBox();
        mainPanel.setAlignment(Pos.CENTER);

        lblTitle = new Label("");
        lblTitle.getStyleClass().add("dialogTitle");
        lblTitle.setPadding(new Insets(10, 10, 10, 10));
        sepTitle = new Separator();
        sepTitle = new Separator();
        sepDirections = new Separator();
        
        String directions = "These are anova directions for the circumstance" +
                            "\nthat you have data in separate columns.";
        Text t1 = new Text(directions);
        Font directionsFont = Font.font("Times New Roman, FontWeight.BOLD, 14");
        mainPanel.getChildren().addAll(lblTitle, sepTitle, t1, sepDirections);

        middlePanel = new HBox();
        middlePanel.setAlignment(Pos.CENTER);

        VBox vList1 = new VBox();
        vList1.setAlignment(Pos.TOP_LEFT);
        vLabel1 = new Label();
        vLabel1.setPadding(new Insets(0, 0, 5, 0));
        Splat_VarList varsAvailable = new Splat_VarList(dm, false, null, null);
        vList1.getChildren().add(vLabel1);
        vList1.getChildren().add(varsAvailable.getPane());
        vList1.setPadding(new Insets(0, 10, 0, 10));
        middlePanel.getChildren().add(vList1);

        Button selectQuantVariable = new Button("===>");
        VBox vList2 = new VBox();
        vList2.setAlignment(Pos.TOP_LEFT);
        vLabel2 = new Label();
        vLabel2.setPadding(new Insets(0, 0, 5, 0));
        Splat_VarList varsSelected = new Splat_VarList(dm, false, 125.0, 125.0);
        varsSelected.clearList();
        vList2.getChildren().add(vLabel2);
        vList2.getChildren().add(varsSelected.getPane());
        
        Label lblExplanVar =   new Label(" Explanatory variable: ");
        Label lblResponseVar = new Label("    Response variable: ");
        
        tfExplanVar = new TextField("Explanatory variable");
        tfResponseVar = new TextField("Response variable");
        
        tfExplanVar.setPrefColumnCount(15);
        tfResponseVar.setPrefColumnCount(15);
        
        tfExplanVar.textProperty().addListener(this::changeExplanVar);
        tfResponseVar.textProperty().addListener(this::changeResponseVar);

        GridPane rightPanel = new GridPane();
        rightPanel.setHgap(10);
        rightPanel.setVgap(15);
        rightPanel.add(selectQuantVariable, 0, 0);
        rightPanel.add(vList2, 1, 0);
        rightPanel.add(lblExplanVar, 0, 3);
        rightPanel.add(lblResponseVar, 0, 4);
        rightPanel.add(tfExplanVar, 1, 3);
        rightPanel.add(tfResponseVar, 1, 4);
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
        myScene.getStylesheets().add(css);

        dialogStage = new Stage();
        dialogStage.setResizable(true);
        dialogStage.setScene(myScene);

        cancelButton.setOnAction((ActionEvent event) -> {
            returnStatus = "Cancel";
            dialogStage.close();
        });

        resetButton.setOnAction((ActionEvent event) -> {
            varsAvailable.resetList();
            varsSelected.clearList();
            numSelected = 0;
        });

        selectQuantVariable.setOnAction((ActionEvent event) -> {
            al_QuantsSelected = varsAvailable.getNamesSelected();
            ok = true;
            
            // If only one quant is selected, it may be a stacked variable
            if (varsAvailable.getNamesSelected().size() == 1) { 
                quantVarText = varsAvailable.getNamesSelected().get(0);
                quantVar = dm.getVariableIndex(quantVarText);                            
            }                  
            // -----------------------------------------------------------

            for (String tmpVar : al_QuantsSelected) {
                if (!dm.getVariableIsNumeric(dm.getVariableIndex(tmpVar))) {
                    msgDiag.Msg(2, "Non Numeric Data",
                            "You have selected a variable that is not numeric. "
                            + "This procedure requires numeric data.");
                    ok = false;
                }
            }

            int tempNum = varsSelected.getVarIndices().size();

            if (ok == true) {
                varsSelected.addVarName(al_QuantsSelected);
                varsAvailable.delVarName(al_QuantsSelected);
            }
        });

        okButton.setOnAction((ActionEvent event) -> {
            ok = true;

            al_IndexVarsSelected = varsSelected.getVarIndices();
            numSelected = al_IndexVarsSelected.size();
            
            if (numSelected < minVars) {
                msgDiag.Msg(2, "Variable Selection",
                        "You must select at least " + minVars + " variable(s) to analyze.");
                ok = false;
            }
            
            dataSet =  new ArrayList(); // ArrayList[] of chosen variables?

            if (ok) {
                for (int j = 0; j < numSelected; j++) {
                    str_ChosenLabels.add(dm.getVariableName(al_IndexVarsSelected.get(j)));
                    col_UnivData = new ColumnOfData();
                    preData = dm.getSpreadsheetColumnAsStrings(al_IndexVarsSelected.get(j), -1, null);
                    col_UnivData = new ColumnOfData(str_ChosenLabels.get(j), preData);

                    // an ArrayList of Strings
                    col_UnivData = col_UnivData.getColumnOfData();                       
                    dataSet.add(col_UnivData);
                }
            }   //  end if(ok)   
            dialogStage.close();
            
            System.out.println(" 211 Not Stacked, txt Exp/Resp = " + tfExplanVar.getText() + " / " + tfResponseVar.getText());
            returnStatus = "Ok";
        });
 


    } // End createANOVA_NS_Dialog
    
        public void changeExplanVar(ObservableValue<? extends String> prop,
                String oldValue,
                String newValue) {
                tfExplanVar.setText(newValue); 
        }

        public void changeResponseVar(ObservableValue<? extends String> prop,
                String oldValue,
                String newValue) {
                tfResponseVar.setText(newValue); 
        }
    
    public void show_ANOVA1_NS_Dialog() {
        minVars = 2;
        maxVars = 6;
        lblTitle.setText("One way ANOVA");
        dialogStage.setTitle("One way ANOVA");
        dialogStage.showAndWait();
    }
    
    public ArrayList<String> getChosenLabels() { return str_ChosenLabels; }
    
    public ArrayList<ColumnOfData> getDataSet() {
        return dataSet; 
    }
    
    public boolean runTheAnalysis() { return true; }

    public String getReturnStatus() { return returnStatus; }
    
} // End class

