/*******************************************************************************
 *                  GOF_DataFromFileDialog                                     *
 *                        09/01/18                                             *
 *                         21:00                                               *
 ******************************************************************************/
package dialogs;

import utilityClasses.MyAlerts;
import chiSquare.X2GOF_DataDialogObj;
import java.util.ArrayList;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import smarttextfield.*;
import chiSquare.*;
import javafx.scene.control.TextField;

public class X2GOF_DataFromFileDialog extends Splat_Dialog {
    // POJOs
    boolean closeRequested, cleanReturn, equalPropsSelected;
    
    double chiSquare, sumExpectedProps;
    double[] expectedProps;
    
    int nCategories, df;
    int[] observedValues;
    
    String theVariable, choice, grid_Information_Style;
    final String adjust = "Adjust";
    final String fix = "Fix";
    final String cancel = "Cancel";
    String[] theGOFCategories;
    
    // My classes
    MyAlerts myAlerts;
    ArrayList<SmartTextField> stfArrayList;
    SmartTextFieldHandler stfHandler; 
    SmartTextField[] stf_gofExpProps;
    TextField stf_gofVariable, stf_nGOFCategories;    
    X2GOF_Model x2GOF_Model;
    X2GOF_DataDialogObj x2GOF_DataFromFileDialogObj;
   
    // POJOs / FX
    Button btnGOF, btnCancel, btnOK, btnOkForGOFAnalysis, btnCancelGOFAnalysis,
           btnDone, btn_4ObservedValues;
    CheckBox doEqualProps;
    ColumnConstraints columnConstraints;
    GridPane gridChooseX2, gridGOF, gridObsVal, gridGofDirectionBtns, 
             gridObsValDirectionBtns;
    HBox hBoxGOFDirectionBtns;
    HBox[] hBoxGOFInfo; 
    Label[] x2Categories;
    Pane gofDescr, pane4_GOFButtons;
    Scene gofSummaryScene;
    Text txtX2ControlTitle, txtGOF, txtGofControlTitle, txtX2VariableDescr, 
         txtX2VariableName, txtNCategories, txtNCategoriesDescr, 
         txtCategoryDescr, txtExpPropDescr, txtObsCountDescr; 
    Text[] txtGOFCategories;
    TextField[] tfGOFObservedCounts;
    VBox vBoxVisual, vBoxNext, vBoxGOF, vBoxHomog, vBoxObsVal;
    
    public X2GOF_DataFromFileDialog(X2GOF_Model gof_Model) {
        super();
        System.out.println("88, X2GOF_DataFromFileDialog");
        this.x2GOF_Model = gof_Model;
        myAlerts = new MyAlerts();
 
        stfHandler = new SmartTextFieldHandler();
        
        nCategories = x2GOF_Model.getNCategories();
        df = nCategories - 1;
        cleanReturn = false;
        
        vBoxVisual = new VBox();
        StackPane root = new StackPane();
        root.getChildren().add(vBoxVisual);
        gofSummaryScene = new Scene(root, 600, 200);
        setScene(gofSummaryScene);
        
        setResizable(false);
        setWidth(600);
        setHeight(200);  
        
        hide(); //  ?????     
    }   //  End constructor
        
/****************************************************************************
 *                      Guts of the dialog                                  * 
 ***************************************************************************/
    public void constructDialogGuts() {   
        vBoxGOF = new VBox();
        txtGofControlTitle = new Text("X2 Goodness of Fit");  
        vBoxGOF.getChildren().add(txtGofControlTitle);
        txtX2VariableDescr = new Text("Variable name: ");
        txtX2VariableName = new Text(x2GOF_Model.getGOFVariable());
        txtNCategoriesDescr = new Text("#Categories: ");
        txtCategoryDescr = new Text("Category "); 
        txtExpPropDescr = new Text("Expected\n   prop");
        txtObsCountDescr = new Text("Observed\n  Count");
        pane4_GOFButtons = new Pane();
 
        constructSomeGUI();

        doEqualProps.setStyle("-fx-border-color: black");
        doEqualProps.setSelected(false);
        doEqualProps.setPadding(new Insets(5, 5, 5, 5)); 

        stf_gofVariable = new TextField();
        txtNCategories = new Text(String.valueOf(nCategories));
        stf_nGOFCategories = new TextField(); 
        stfArrayList = new ArrayList<>(nCategories); 

        //  Construct the Grid
        gridGOF = new GridPane();
        columnConstraints = new ColumnConstraints(125);
        gridGOF.getColumnConstraints().add(columnConstraints);
        gridGOF.setPadding(new Insets(10, 10, 10, 10));
        gridGOF.setVgap(10);
        gridGOF.setHgap(10);
        gridGOF.add(txtX2VariableDescr, 0, 0);  //  Variable descr
        
        gridGOF.add(txtX2VariableName, 1, 0); //  Variable name
        gridGOF.add(txtNCategoriesDescr, 2, 0); // N categories descrr        
        gridGOF.add(txtNCategories, 3, 0);
        
        stf_nGOFCategories.setPrefColumnCount(4);
        gridGOF.add(pane4_GOFButtons, 3, 1); 
        GridPane.setHalignment(btnOkForGOFAnalysis, HPos.CENTER);
        hBoxGOFDirectionBtns = new HBox();
        vBoxVisual.getChildren().addAll(txtGofControlTitle, gridGOF, hBoxGOFDirectionBtns);
        
        constructMoreGUI();
        
    }   //  End constructDialogGuts 
    
    private void constructSomeGUI() {   //  and CheckBox
        doEqualProps = new CheckBox("H0 Equal Props");
        doEqualProps.setOnAction( new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if (doEqualProps.isSelected()) {
                    equalPropsSelected = true;           
                    if (stf_gofExpProps != null) {  //  Resetting from blanks
                        for (int iCategories = 0; iCategories < nCategories; iCategories++) {
                            double daEqualProp = 1.0 / nCategories;
                            stf_gofExpProps[iCategories].setSmartTextDouble(daEqualProp);
                            stf_gofExpProps[iCategories].setText(String.valueOf(daEqualProp));
                            stf_gofExpProps[iCategories].setEditable(false);
                        }                          
                    }
                }
                else {
                    equalPropsSelected = false;
                    for (int iCategories = 0; iCategories < nCategories; iCategories++) {
                        stf_gofExpProps[iCategories].setSmartTextDouble(0.0);
                        stf_gofExpProps[iCategories].setText("");
                        stf_gofExpProps[iCategories].setEditable(true);
                    }
                }
            }
        });

        btnCancelGOFAnalysis = new Button ("Cancel");
        btnCancelGOFAnalysis.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                returnStatus = "Cancel";
                close();
            }
        });
        
        btnOkForGOFAnalysis = new Button("Proceed to analysis");
        btnOkForGOFAnalysis.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
/***************************************************************************
 *                    Check for Data Entry Problems                        *
 **************************************************************************/            
                if(checkOKForAnalysis() == true) {
    /***************************************************************************
     *                    Wrap up final calculations                       *
     **************************************************************************/
                    sumExpectedProps = 0.0;
                    for (int iCategories = 0; iCategories < nCategories; iCategories++) {                    
                        expectedProps[iCategories] = stf_gofExpProps[iCategories].getSmartTextDouble(); 
                        sumExpectedProps += expectedProps[iCategories];
                    }

                    if (Math.abs(sumExpectedProps - 1.0) < .01) {   //  .01 Arbrtrary!!!
                        for (int iExpProps = 0; iExpProps < nCategories; iExpProps++) {
                            expectedProps[iExpProps] /= sumExpectedProps;
                        }  

    /***************************************************************************
     *              Construct the object and send it to Parent                 *
     **************************************************************************/
                    x2GOF_DataFromFileDialogObj = new X2GOF_DataDialogObj(nCategories);
                    x2GOF_DataFromFileDialogObj.setGOFVariable(x2GOF_Model.getGOFVariable());

                    String[] theGOFCats = new String[nCategories];
                    for (int i = 0; i < nCategories; i++) {
                        theGOFCats[i] = txtGOFCategories[i].getText();
                    }

                    x2GOF_DataFromFileDialogObj.setTheGOFCategories(theGOFCats);

                    expectedProps = new double[nCategories];
                    for (int i = 0; i < nCategories; i++) {
                        expectedProps[i] = stf_gofExpProps[i].getSmartTextDouble();
                    } 
                    x2GOF_DataFromFileDialogObj.setExpectedProps(expectedProps);   
                    x2GOF_DataFromFileDialogObj.setObservedValues(observedValues); 
                    returnStatus = "OK";
                    close();
                    
                    } else {  // Math.abs(sumExpectedProps - 1.0) >=.01)
                        
                        String choice = doSumExpPropsDialog();

                        switch (choice) {

                            case adjust:
                            {
                                for (int iExpProps = 0; iExpProps < nCategories; iExpProps++) {
                                    expectedProps[iExpProps] /= sumExpectedProps;
                                    stf_gofExpProps[iExpProps].setText(String.valueOf(expectedProps[iExpProps]));
                                    stf_gofExpProps[iExpProps].setSmartTextDouble(expectedProps[iExpProps]);
                                } 
                                sumExpectedProps = 1.0;
                                btnOkForGOFAnalysis.fire();
                            break;
                            }

                            case fix:
                            {
                                for (int iCategories = 0; iCategories < nCategories; iCategories++) {
                                    stf_gofExpProps[iCategories].setText("");
                                }
                                break;
                            }

                            case cancel:
                                returnStatus = "Cancel";
                                if (returnStatus.equals("Cancel"))   //  Making the
                                    return;                          // compiler happy
                            break;

                            default:
                                System.out.println("Ack!! Error in switch adjust/fix/cancel");
                        }    
                    }   //  end // Math.abs(sumExpectedProps - 1.0) >=.01) 
                }
            }   //  end handle btnOkForGOFAnalysis
        });    
    }
    
    public void constructMoreGUI() {      
        theGOFCategories = new String[nCategories];
        theGOFCategories = x2GOF_Model.getObservedValuesFromFile();

        txtGOFCategories = new Text[nCategories];
        for (int ithCat = 0; ithCat < nCategories; ithCat++) {
            txtGOFCategories[ithCat] = new Text(theGOFCategories[ithCat]);
        }

        stf_gofExpProps = new SmartTextField[nCategories];
        tfGOFObservedCounts = new TextField[nCategories];
        observedValues = new int[nCategories];
        observedValues = x2GOF_Model.getObservedCountsFromFile();
        expectedProps = new double[nCategories];

        for (int ithCat = 0; ithCat < nCategories; ithCat++) {
            tfGOFObservedCounts[ithCat] = new TextField();
            tfGOFObservedCounts[ithCat].setText(String.valueOf(observedValues[ithCat]));
        }

        gridGOF.add(doEqualProps, 0, 1);
        gridGOF.add(txtCategoryDescr, 1, 2);
        GridPane.setHalignment(txtCategoryDescr, HPos.CENTER);
        gridGOF.add(txtExpPropDescr, 2, 2);
        GridPane.setHalignment(txtExpPropDescr, HPos.LEFT);
        gridGOF.add(txtObsCountDescr, 3, 2);
        GridPane.setHalignment(txtObsCountDescr, HPos.LEFT);
        setHeight(250 + 40 * nCategories);

        for (int iCategories = 0; iCategories < nCategories; iCategories++) {
            gridGOF.add(txtGOFCategories[iCategories], 1, iCategories + 3);

            //listPosition =  3 * iCategories + 3;

            // Construct a "circular" transfersal
            if (iCategories == 0) {
                stf_gofExpProps[iCategories] = new SmartTextField(stfHandler, nCategories - 1, 1);
            }
            else if (iCategories == nCategories - 1) {
                stf_gofExpProps[iCategories] = new SmartTextField(stfHandler, nCategories - 2, 0);
            }
            else {
                stf_gofExpProps[iCategories] = new SmartTextField(stfHandler, iCategories - 1, iCategories +1);
            }

            stf_gofExpProps[iCategories].setSmartTextField_MB_PROBABILITY(true);
            stfArrayList.add(iCategories, stf_gofExpProps[iCategories]); 
            stf_gofExpProps[iCategories].getTextField().setMaxWidth(50);
            gridGOF.add(stf_gofExpProps[iCategories].getTextField(), 2, iCategories + 3);

            //listPosition =  3 * iCategories + 4;
            tfGOFObservedCounts[iCategories].setMaxWidth(50);
            gridGOF.add(tfGOFObservedCounts[iCategories], 3, iCategories + 3);

            if (equalPropsSelected == true) {
                double daEqualProp = 1.0 / nCategories;
                stf_gofExpProps[iCategories].setSmartTextDouble(daEqualProp);
                stf_gofExpProps[iCategories].setText(String.valueOf(daEqualProp));
            }    
        }

        gridGOF.add(btnCancelGOFAnalysis, 1, nCategories + 3);
        gridGOF.add(btnOkForGOFAnalysis, 3, nCategories + 3);

        stfHandler.setHandlerArrayList(stfArrayList);
        stfHandler.setHandlerTransversal(true);
        //stfHandler.prepareTextFields();
        stfHandler.setHandlerTransversalIndex(0);
        stfHandler.setFocusRequest(0);
    }   //  end whatever
    
    private boolean checkOKForAnalysis() {        
        boolean okToContinue;
        int tempSize = stfArrayList.size();
        okToContinue = stfHandler.finalCheckForBlanksInArray(0, tempSize - 1);
        if (okToContinue  == false) {
            myAlerts.showMissingInformationAlert();
            return false;
        }
        // Check for unique categories.  Necessary for Category Axis
        for (int ithCat = 0; ithCat < nCategories - 1; ithCat++) {    
            for (int jthCat = ithCat + 1; jthCat < nCategories; jthCat++) {
                String temp1 = txtGOFCategories[ithCat].getText();
                String temp2 = txtGOFCategories[jthCat].getText();
                if (temp1.equals(temp2)) {
                    myAlerts.showNonUniqueCategoriesAlert();
                    return false;
                } 
            }
        }
        return true;
    }

/*******************************************************************************
*                          Ancillary routines                                  *
* @return 
*******************************************************************************/    
    
    public String doSumExpPropsDialog() {
        String returnString;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Uh-oh, possible problem here...");
        alert.setHeaderText("There is a difficulty with your numbers.");
        alert.setContentText("The sum of your expected proportions is different from 1.0."
                           + "\nThis could be due to roundoff error, in which case your  "
                           + "\nproportions can be (slightly) adjusted.  Or, if "
                           + "\nyour proportions are  in deeper doo-doo, you can fix them."
                           + "\nYour call. "
        );

        ButtonType bt_Adjust = new ButtonType("Adjust & Continue");
        ButtonType bt_Fix = new ButtonType("I will Fix them");
        ButtonType bt_Cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(bt_Adjust, bt_Fix, bt_Cancel);
        boolean keepGoing = true;
        do {
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == bt_Adjust){
                returnString = adjust;
                keepGoing = false;
            } else if (result.get() == bt_Fix) {
                returnString = fix;
                keepGoing = false;
            } else {
                returnString = cancel;
                keepGoing = false;
            }
        } while (keepGoing == true);      

        return returnString;
    }   //  end doSumExpPropsDialog()
    
    public String getReturnStatus() { return returnStatus; }
    
    public SmartTextFieldHandler getSmartTextFieldHandler() { return stfHandler; }   
    
    public X2GOF_DataDialogObj getTheDialogObject() { return x2GOF_DataFromFileDialogObj; }
}


