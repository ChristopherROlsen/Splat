/************************************************************
 *                    One_Variable_Dialog                  *
 *                          12/25/18                        *
 *                            15:00                         *
 ***********************************************************/
package dialogs;

import dataObjects.ColumnOfData;
import javafx.beans.value.ObservableValue;
import utilityClasses.DataCleaner;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import splat.Data_Manager;
import splat.Var_List;

public class One_Variable_Dialog extends Splat_Dialog {
    // POJOs
    private boolean runAnalysis, ok;  
    
    int minVars, maxVars, varIndexForX, nCheckBoxes, minSampleSize;
    
    String var_1_DataType;
    public String strVarLabel;
    String descriptionOfVariable, subTitle;
    // My classes
    public ColumnOfData dataColumn;
    Var_List listOfVars;    
    
    // POJOs / FX
    public Button resetButton, selectXVariable;
    public CheckBox[] dashBoardOptions;
    public GridPane gridChoicesMade;
    public HBox middlePanel, dataDescriptions;
    public VBox mainPanel, leftPanel, rightPanel, vBoxVars2ChooseFrom, 
                vBoxXVarChoices;
    public Label lbl_Title, lbl_VarsInData, lblFirstVar, lblExplanVar; 
    public Label lbl_QuantReal, lbl_QuantInt, lbl_Categorical;
    
    public Rectangle quantReal, quantInt, categorical;
    public TextField tf_FirstVar, tfExplanVar;


    
    One_Variable_Dialog(Data_Manager dm, String var_1_DataType) {
        super(dm);
        this.dm = dm;
        this.var_1_DataType = var_1_DataType;
        //System.out.println("55 TV-Dial");
        returnStatus = "Ok";
        runAnalysis = false;
        ok = true;
        this.var_1_DataType = var_1_DataType;

        lbl_Title = new Label("One-variable dialog");
        lbl_Title.getStyleClass().add("dialogTitle");
        lbl_Title.setPadding(new Insets(10, 10, 10, 10));
        
        // Classify the data columns
        quantReal = new Rectangle(10, 10, Color.RED);
        quantInt = new Rectangle(10, 10, Color.BLUE);
        categorical = new Rectangle(10, 10, Color.GREEN);
        dataDescriptions = new HBox(30);
        dataDescriptions.setAlignment(Pos.CENTER);
        
        lbl_QuantReal = new Label("Real", quantReal);
        lbl_QuantInt = new Label("Integer", quantInt);
        lbl_Categorical = new Label("Categorical", categorical);
        dataDescriptions.getChildren().addAll(lbl_QuantReal, lbl_QuantInt, lbl_Categorical);        

        vBoxVars2ChooseFrom = new VBox();
        vBoxVars2ChooseFrom.setAlignment(Pos.TOP_LEFT);
        lbl_VarsInData = new Label("Variables in Data:");
        lbl_VarsInData.setPadding(new Insets(0, 0, 5, 0));
        listOfVars = new Var_List(dm, true, null, null);
        vBoxVars2ChooseFrom.getChildren().add(lbl_VarsInData);
        vBoxVars2ChooseFrom.getChildren().add(listOfVars.getPane());
        vBoxVars2ChooseFrom.setPadding(new Insets(0, 10, 0, 10));
        
        selectXVariable = new Button("===>");

        vBoxXVarChoices = new VBox();
        vBoxXVarChoices.setAlignment(Pos.TOP_LEFT);
        lblFirstVar = new Label();
        lblFirstVar.setPadding(new Insets(0, 0, 5, 0));
        tf_FirstVar = new TextField("");
        tf_FirstVar.setPrefWidth(125.0);
        vBoxXVarChoices.getChildren().addAll(lblFirstVar, tf_FirstVar);
        
        lblExplanVar =   new Label(" Description of Variable: ");
        tfExplanVar = new TextField("Description of Variable");
        tfExplanVar.setPrefColumnCount(15);
        tfExplanVar.textProperty().addListener(this::changeExplanVar);
                
        gridChoicesMade = new GridPane();
        gridChoicesMade.setHgap(10);
        gridChoicesMade.setVgap(15);
        gridChoicesMade.add(selectXVariable, 0, 0);
        gridChoicesMade.add(vBoxXVarChoices, 1, 0);
        
        gridChoicesMade.add(lblExplanVar, 0, 3);
        gridChoicesMade.add(tfExplanVar, 1, 3);      
                
        GridPane.setValignment(selectXVariable, VPos.BOTTOM);
        gridChoicesMade.setPadding(new Insets(0, 10, 0, 0));

        leftPanel = new VBox(10);
        leftPanel.setAlignment(Pos.CENTER_LEFT);
        leftPanel.setPadding(new Insets(0, 25, 0, 10));
        
        rightPanel = new VBox(10);
        rightPanel.setAlignment(Pos.CENTER_LEFT);
        rightPanel.setPadding(new Insets(0, 25, 0, 10));
        rightPanel.getChildren().add(gridChoicesMade);
        
        middlePanel = new HBox();
        middlePanel.setAlignment(Pos.CENTER);
        middlePanel.getChildren().add(leftPanel);     
        middlePanel.getChildren().add(vBoxVars2ChooseFrom);
        middlePanel.getChildren().add(rightPanel);
        middlePanel.setPadding(new Insets(10, 0, 10, 0));

        HBox buttonPanel = new HBox(10);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.setPadding(new Insets(10, 10, 10, 10));

        okButton.setText("Compute");
        cancelButton.setText("Cancel");
        resetButton = new Button("Reset");
        buttonPanel.getChildren().addAll(okButton, cancelButton, resetButton);
        
        mainPanel = new VBox();
        mainPanel.setAlignment(Pos.CENTER);    
        Separator sepTitle = new Separator();
        mainPanel.getChildren().addAll(lbl_Title, sepTitle, dataDescriptions);    
        mainPanel.getChildren().add(middlePanel);
        Separator sepButtons = new Separator();
        mainPanel.getChildren().add(sepButtons);    
        mainPanel.getChildren().add(buttonPanel);
        
        Scene myScene = new Scene(mainPanel);
        // String css = getClass().getResource("/css/StatDialogs.css").toExternalForm();
        myScene.getStylesheets().add(css);
        setScene(myScene);

        resetButton.setOnAction((ActionEvent event) -> {
            listOfVars.resetList();
            tf_FirstVar.setText("");
        });

        selectXVariable.setOnAction((ActionEvent event) -> {
            if (listOfVars.getNamesSelected().size() == 1) {
                String tempIndicator = listOfVars.getNamesSelected().get(0);
                tf_FirstVar.setText(tempIndicator);
                listOfVars.delVarName(listOfVars.getNamesSelected());
            }
        });

        okButton.setOnAction((ActionEvent event) -> {

            ok = true;
            String strSelected = tf_FirstVar.getText();
            varIndexForX = dm.getVariableIndex(strSelected);
            if (varIndexForX == -1) {
                ok = false;
            }
            
            dataColumn = new ColumnOfData(dm.getSpreadsheetColumn(varIndexForX));

            if (!ok) {
                MyDialogs msgDiag = new MyDialogs();
                msgDiag.Msg(2, "Variable not selected",
                        "You must select a variable.");
            }

            if (ok) {
                //  Should var_2 be consideredhere for consistency?
                //  ANOVA perhaps?
                if (var_1_DataType.equals("Categorical")) {
                    System.out.println("183 one_var_dialog, WARNING -- cleaning the column");
                    cleanTheColumn(varIndexForX);
                }

                if (!ok) {
                    returnStatus = "NotOk";
                } else {
                    returnStatus = "Ok";
                    close();
                }
            }
            descriptionOfVariable = tfExplanVar.getText();
            subTitle = "Variable: " + descriptionOfVariable;            
        });       
    }
    
    private void cleanTheColumn(int thisCol) {
        // System.out.println("231 Twovars_Dialog, cleanTheColumn(int thisCol), thisCol = " + thisCol);
        ColumnOfData tempCol = new ColumnOfData(dm.getAllTheColumns()
                                                  .get(thisCol));
        tempCol.assignDataType();   //  Categorical?  Something else?
        int tempSize = tempCol.getColumnSize();
        String[] tempString = new String[tempSize];
        for (int ithCase = 0; ithCase < tempSize; ithCase++) {
            tempString[ithCase] = tempCol.getTextInIthRow(ithCase);
        }
        DataCleaner dc = new DataCleaner(tempCol);
        dc.cleanAway();
        String[] cleanedStrings = new String[tempSize];
        cleanedStrings = dc.getCleanStrings();
        
         for (int ithCase = 0; ithCase < tempSize; ithCase++) {
            dm.getAllTheColumns()
              .get(thisCol)
              .setData(ithCase, cleanedStrings[ithCase]); 
        } 
        dm.resetTheGrid(); 
    }
    
        public void changeExplanVar(ObservableValue<? extends String> prop,
                String oldValue,
                String newValue) {
                tfExplanVar.setText(newValue); 
        }
        
    public String getDescriptionOfVariable() { return descriptionOfVariable; }
    
    public ColumnOfData getData() {
        return dataColumn;
    }
    
    public String getReturnStatus() { return returnStatus; }
    
    public CheckBox[] getCheckBoxes() { return dashBoardOptions; }
    
    // Used by regression -- change to ReturnStatus
    public boolean getOK() { return ok; }
    
}
