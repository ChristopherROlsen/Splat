/*******************************************************************************
 *                   GOF_DataByHandDialog                                      *
 *                        11/22/18                                             *
 *                         21:00                                               *
 ******************************************************************************/
package dialogs;

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

public class X2GOF_DataByHandDialog extends Splat_Dialog {
/*******************************************************************************
*          Define the return object and any necessary ancillary variables.     *
*******************************************************************************/
    // POJOs
    boolean closeRequested, isAnInteger, isADouble, equalPropsSelected, 
            cleanReturn;
    
    double chiSquare, sumExpectedProps;
    double[] expectedProps;
    
    int nCategories, df;
    int[] observedValues;
    
    String theVariable, choice, grid_Information_Style;//, returnStatus;
    
    final String adjust = "Adjust";
    final String fix = "Fix";
    final String cancel = "Cancel";
    
    // My classes
    MyX2Alerts myAlerts;    
    SmartTextField stf_gofVariable, stf_nGOFCategories;
    ArrayList<SmartTextField> stfArrayList;
    SmartTextFieldHandler stfHandler; 
    SmartTextField[] stf_gofCategories, stf_gofExpProps, stf_gofObs;  
    X2GOF_Model x2GOF_Model;
    X2GOF_DataDialogObj gof_SummaryDialog_Obj;
    
    // POJOs / FX
    Button btnGOF, btnCancel, btnOK, btnOkForGOFAnalysis, btnCancelGOFAnalysis,
           btnDone, btnOkForGOFCategories, btnCancelGOFCategories,
           btn_4ObservedValues, btnClearGOFCategories;
    CheckBox doEqualProps;
    ColumnConstraints columnConstraints_1;
    GridPane gridChooseX2, gridGOF, gridObsVal, gridGofDirectionBtns, 
             gridObsValDirectionBtns;
    HBox hBoxGOFDirectionBtns;
    HBox[] hBoxGOFInfo; 
    Label[] x2Categories; 
    Pane gofDescr, pane4_GOFButtons;
    Scene gofSummaryScene;
    Text txtX2ControlTitle, txtGOF, txtGofControlTitle, txtX2Variable,
         txtNCategories, txtCategoryDescr, txtExpProp, txtObsCount; 
    VBox vBoxVisual, vBoxNext, vBoxGOF, vBoxHomog, vBoxObsVal;
    
    X2GOF_Model gof_Model;
    
    public X2GOF_DataByHandDialog(X2GOF_Model x2GOFModel) {
    /************************************************************************
    *              Instantiate & Initialize any needed ancillary stuff      *
    ************************************************************************/
        super();
        this.x2GOF_Model = x2GOFModel;
        initialize();
    }
    
    private void initialize() {

        myAlerts = new MyX2Alerts();
        stfArrayList = new ArrayList<>();   
        stfHandler = new SmartTextFieldHandler();
        nCategories = x2GOF_Model.getNCategories();
        df = nCategories - 1;
        cleanReturn = false;
        
        // Initialize stage, scene stuff
        vBoxVisual = new VBox();
        StackPane root = new StackPane();
        root.getChildren().add(vBoxVisual);
        gofSummaryScene = new Scene(root, 600, 200);
        setScene(gofSummaryScene);
        
        setResizable(true);
        setWidth(600);
        setHeight(200);  
        
        hide();
    }   //  End constructor
        
/****************************************************************************
 *                      Guts of the dialog                                  * 
 ***************************************************************************/
    public void constructDialogGuts() {
        vBoxGOF = new VBox();
        txtGofControlTitle = new Text("X2 Goodness of Fit");  
        vBoxGOF.getChildren().add(txtGofControlTitle);
        txtX2Variable = new Text("Variable name: ");
        txtNCategories= new Text("#Categories: ");
        txtCategoryDescr = new Text("Category "); 
        txtExpProp = new Text("Expected\n   prop");
        txtObsCount = new Text("Observed\n  Count");
        pane4_GOFButtons = new Pane();
 
        constructButtons();
        
        stfArrayList = new ArrayList<>(nCategories);
        doEqualProps.setStyle("-fx-border-color: black");
        doEqualProps.setSelected(false);
        doEqualProps.setPadding(new Insets(5, 5, 5, 5)); 

        stf_gofVariable = new SmartTextField(stfHandler, 1, 1);
        stfArrayList.add(0, stf_gofVariable); 
          
        stf_nGOFCategories = new SmartTextField(stfHandler, 0, 0);
        stf_nGOFCategories.setSmartTextField_MB_POSITIVEINTEGER(true);   
        stfArrayList.add(1, stf_nGOFCategories);  
        
        stfHandler.setHandlerArrayList(stfArrayList);
        stfHandler.setHandlerTransversal(true);
        stfHandler.setHandlerTransversalIndex(0);
        stfHandler.setFocusRequest(0);

        //  Construct the Grid
        gridGOF = new GridPane();
        columnConstraints_1 = new ColumnConstraints(125);
        gridGOF.getColumnConstraints().add(columnConstraints_1);
        gridGOF.setPadding(new Insets(10, 10, 10, 10));
        gridGOF.setVgap(10);
        gridGOF.setHgap(10);
        gridGOF.add(txtX2Variable, 0, 0);  //  Variable name
        
        stf_gofVariable.getSmartTextField().getTextField().setPrefColumnCount(15);
        gridGOF.add(stf_gofVariable.getTextField(), 1, 0);
        gridGOF.add(txtNCategories, 2, 0);
        
        stf_nGOFCategories.getSmartTextField().getTextField().setPrefColumnCount(4);
        stf_nGOFCategories.getSmartTextField().getSmartTextField().setSmartTextField_MB_NONBLANK(true);
        gridGOF.add(stf_nGOFCategories.getTextField(), 3, 0);
              
        gridGOF.add(btnCancelGOFCategories, 2, 1);
        gridGOF.add(pane4_GOFButtons, 3, 1); 

        GridPane.setHalignment(btnOkForGOFCategories, HPos.CENTER);
        GridPane.setHalignment(btnClearGOFCategories, HPos.CENTER);
        GridPane.setHalignment(btnCancelGOFCategories, HPos.CENTER);
        GridPane.setHalignment(btnOkForGOFAnalysis, HPos.CENTER);
        
        pane4_GOFButtons.getChildren().add(btnOkForGOFCategories);

        hBoxGOFDirectionBtns = new HBox();
        vBoxVisual.getChildren().addAll(txtGofControlTitle, gridGOF, hBoxGOFDirectionBtns);
        
    }   //  End GetGOFInfo 
    
    private void constructButtons() {   //  and CheckBox
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
        
        btnClearGOFCategories = new Button ("Clear categories");
        btnClearGOFCategories.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent event) {

        //  ****************     Clear the props   **********************
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
            
        //  ****************     Clear the rest   **********************            
            for (int iCategories = 0; iCategories < nCategories; iCategories++) {
                stf_gofCategories[iCategories].setText("");
                stf_gofObs[iCategories].setText("");  
            }   
        }
    });    
        
        btnCancelGOFCategories = new Button ("Back to Menu");
        btnCancelGOFCategories.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                returnStatus = "BackToMenu";
                close();
            }
        });
        
        btnCancelGOFAnalysis = new Button ("Cancel");
        btnCancelGOFAnalysis.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                returnStatus = "Cancel";
                close();
            }
        });
        
        btnOkForGOFCategories = new Button("Proceed to categories");
        
        btnOkForGOFCategories.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {      
                //  true -> okToContinue
                if (stfHandler.finalCheckForBlanksInArray(0, 1) == true)  {
                    nCategories = stf_nGOFCategories.getSmartTextInteger();              
                    stf_gofCategories = new SmartTextField[nCategories];
                    stf_gofExpProps = new SmartTextField[nCategories];
                    stf_gofObs = new SmartTextField[nCategories];
                    observedValues = new int[nCategories];
                    expectedProps = new double[nCategories];
                    gridGOF.add(doEqualProps, 0, 1);
                    pane4_GOFButtons.getChildren().remove(btnOkForGOFCategories);
                    gridGOF.add(txtCategoryDescr, 1, 2);
                    GridPane.setHalignment(txtCategoryDescr, HPos.CENTER);
                    gridGOF.add(txtExpProp, 2, 2);
                    GridPane.setHalignment(txtExpProp, HPos.LEFT);
                    gridGOF.add(txtObsCount, 3, 2);
                    GridPane.setHalignment(txtObsCount, HPos.LEFT);
                    setHeight(250 + 40 * nCategories);

                    for (int iCategories = 0; iCategories < nCategories; iCategories++) {
                        int listPosition =  3 * iCategories + 2;
                        stf_gofCategories[iCategories] = new SmartTextField(stfHandler, listPosition - 1, listPosition +1);
                        stf_gofCategories[iCategories].setSmartTextField_MB_NONBLANK(true);
                        stfArrayList.add(listPosition, stf_gofCategories[iCategories]); 
                        gridGOF.add(stf_gofCategories[iCategories].getTextField(), 1, iCategories + 3);

                        listPosition =  3 * iCategories + 3;
                        stf_gofExpProps[iCategories] = new SmartTextField(stfHandler, listPosition - 1, listPosition +1);
                        stf_gofExpProps[iCategories].setSmartTextField_MB_PROBABILITY(true);
                        stfArrayList.add(listPosition, stf_gofExpProps[iCategories]); 
                        stf_gofExpProps[iCategories].getTextField().setMaxWidth(50);
                        gridGOF.add(stf_gofExpProps[iCategories].getTextField(), 2, iCategories + 3);

                        listPosition =  3 * iCategories + 4;
                        // System.out.println("305 Dialog, listPosition = " + listPosition);
                        stf_gofObs[iCategories] = new SmartTextField(stfHandler, listPosition - 1, listPosition +1);
                        stf_gofObs[iCategories].setSmartTextField_MB_POSITIVEINTEGER(true);
                        stf_gofObs[iCategories].setSmartTextField_MB_NONBLANK(true);
                        
                        // System.out.println("310 x2GOF_Dialog, MBNonBlank = " + stf_gofObs[iCategories].getSmartTextField_MB_NONBLANK());
                        
                        stfArrayList.add(listPosition, stf_gofObs[iCategories]); 
                        stf_gofObs[iCategories].getSmartTextField().getTextField().setMaxWidth(50);
                        gridGOF.add(stf_gofObs[iCategories].getTextField(), 3, iCategories + 3);

                        if (equalPropsSelected == true) {
                            double daEqualProp = 1.0 / (double)nCategories;
                            stf_gofExpProps[iCategories].setSmartTextDouble(daEqualProp);
                            stf_gofExpProps[iCategories].setText(String.valueOf(daEqualProp));
                        }    
                    }
                    stf_gofCategories[0].setPreAndPostSmartTF(3 * nCategories + 1, 3);
                    stf_gofObs[nCategories - 1].setPreAndPostSmartTF(3 * nCategories, 2);

                    gridGOF.getChildren().remove(btnCancelGOFCategories);
                    gridGOF.getChildren().remove(btnOkForGOFCategories);
                    gridGOF.add(btnCancelGOFAnalysis, 1, nCategories + 3);
                    gridGOF.add(btnClearGOFCategories, 2, nCategories + 3);
                    gridGOF.add(btnOkForGOFAnalysis, 3, nCategories + 3);
                    
                    stfHandler.setHandlerArrayList(stfArrayList);
                    stfHandler.setHandlerTransversal(true);
                    //stfHandler.prepareTextFields();
                    stfHandler.setHandlerTransversalIndex(0);
                    stfHandler.setFocusRequest(0);
                }   // end if (stfHandler.finalCheckForBlanks(0, 1) == true)
                else {
                    myAlerts.showMissingInformationAlert();
                }
            }   //  end handle
        });        
        
        btnOkForGOFAnalysis = new Button("Proceed to analysis");
        
        btnOkForGOFAnalysis.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
/***************************************************************************
 *                    Check for Data Entry Problems                        *
 **************************************************************************/ 

                if(checkOKForUniqueCategories() == false) {
                    return;
                }
                
                if(checkForNoBlanks() == false) {
                    return;
                }                
                
    /***************************************************************************
     *                    Wrap up any final calculations                       *
     **************************************************************************/
                sumExpectedProps = 0.0;
                for (int iCategories = 0; iCategories < nCategories; iCategories++) {
                    observedValues[iCategories] = stf_gofObs[iCategories].getSmartTextInteger();                     
                    expectedProps[iCategories] = stf_gofExpProps[iCategories].getSmartTextDouble(); 
                    sumExpectedProps += expectedProps[iCategories];
                }

                if (Math.abs(sumExpectedProps - 1.0) < .01) {   //  .01 Arbrtrary!!!
                    
                    // Adjusts to sum of expected props = 1.0 no matter what
                    for (int iExpProps = 0; iExpProps < nCategories; iExpProps++) {
                        expectedProps[iExpProps] /= sumExpectedProps;
                    } 
                    
                    sumExpectedProps = 1.0;

/***************************************************************************
 *                          Construct the object                           *
 **************************************************************************/
                    gof_SummaryDialog_Obj = new X2GOF_DataDialogObj(nCategories);
                    gof_SummaryDialog_Obj.setGOFVariable(stf_gofVariable.getText());

                    String[] theGOFCats = new String[nCategories];
                    for (int i = 0; i < nCategories; i++) {
                        theGOFCats[i] = stf_gofCategories[i].getText();
                    }

                    gof_SummaryDialog_Obj.setTheGOFCategories(theGOFCats);

                    expectedProps = new double[nCategories];
                    for (int i = 0; i < nCategories; i++) {
                        expectedProps[i] = stf_gofExpProps[i].getSmartTextDouble();
                    } 
                    gof_SummaryDialog_Obj.setExpectedProps(expectedProps);   

                    observedValues = new int[nCategories];
                    for (int i = 0; i < nCategories; i++) {
                        observedValues[i] = stf_gofObs[i].getSmartTextInteger();
                    } 
                    gof_SummaryDialog_Obj.setObservedValues(observedValues); 

/*****************************************************************************
 *                    The X2GOF_Model will grab the object                   *
 ****************************************************************************/  
                    returnStatus = "OK";
                    close();

                } else {    // Math.abs(sumExpectedProps - 1.0) > .01

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
                            
                            btnOkForGOFAnalysis.fire(); //  Actually re-fire
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
                            close(); 
                        break;

                        default:
                            System.out.println("Ack!! Error in switch adjust/fix/cancel");
                    }    
                }   //  end else  
            }
        });    
    }
    
    private boolean checkOKForUniqueCategories() {
        boolean okToContinue;
        // Check for unique categories.  Unique is necessary for Category Axis
        for (int ithCat = 0; ithCat < nCategories - 1; ithCat++) {    
            for (int jthCat = ithCat + 1; jthCat < nCategories; jthCat++) {
                String temp1 = stf_gofCategories[ithCat].getText();
                String temp2 = stf_gofCategories[jthCat].getText();
                if (temp1.equals(temp2)) {
                    myAlerts.showNonUniqueCategoriesAlert();
                    return false;
                } 
            }
        }
        return true;
    }
    
    private boolean checkForNoBlanks() {
        boolean okToContinue;
        int tempSize = stfArrayList.size();
        okToContinue = stfHandler.finalCheckForBlanksInArray(0, tempSize - 1);
        if (okToContinue  == false) {
            MyX2Alerts.showMissingInformationAlert();
            return false;
        }
        return true;
    }

/*******************************************************************************
*                          Ancillary routines                                  *
* @return 
*******************************************************************************/    
    
    public String doSumExpPropsDialog() {
        String returnString;
        Alert nonSumToOneAlert = new Alert(Alert.AlertType.CONFIRMATION);   
        nonSumToOneAlert.setTitle("Uh-oh, possible problem here...");
        nonSumToOneAlert.setHeaderText("There is a problem with your hypothesized proportions.");
        nonSumToOneAlert.setContentText("The sum of your expected proportions is different from 1.0. This could"
                                     + "\nbe due to roundoff error, in which case your proportions will be (only"
                                     + "\nslightly) adjusted for the chi square calculations. If this difference"
                                     + "\nindicates deeper doo-doo problems, you can fix them yourself. Your call.");        
        // nonSumToOneAlert.showAndWait(); 

        ButtonType bt_Adjust = new ButtonType("Adjust & Continue");
        ButtonType bt_Fix = new ButtonType("I will Fix them");
        ButtonType bt_Cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        nonSumToOneAlert.getButtonTypes().setAll(bt_Adjust, bt_Fix, bt_Cancel);
        
        boolean keepGoing = true;
        do {
            Optional<ButtonType> result = nonSumToOneAlert.showAndWait();
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
    
    public X2GOF_DataDialogObj getTheDialogObject() { return gof_SummaryDialog_Obj; }
}

