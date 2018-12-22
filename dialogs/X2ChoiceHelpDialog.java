/****************************************************************************
 *                      X2ChoiceHelpDialog                                  * 
 *                           09/01/18                                       *
 *                            21:00                                         *
 ***************************************************************************/
package dialogs;

import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import smarttextfield.*;
import chiSquare.*;
        
/****************************************************************************
 *                         Control algebra                                  * 
 *        GridPane is (column index, row index)                             *
 *   Var1 categories go DOWN the left column from row 1 to row nCategories  *
 *   Var2 categories go ACROSS the top row from col 1 to col nCategories    *
 ***************************************************************************/

public class X2ChoiceHelpDialog extends Splat_Dialog {
    // POJOs
    boolean closeRequested;
    
    double chiSquare = 0.0; 
    
    int df, nRowCategories, nColCategories, nTotalCategories;
    int[][] observedValues;
    
    //  These strings are for keeping track of which control is 'up.'
    final String cc_X2Chosen = "X2CHOSEN";
    final String cc_CategoriesGrid = "CATEGORIES";
    final String cc_Observed = "OBSERVED";
    final String experiment = "EXPERIMENT";
    final String homogeneity = "HOMOGENEITY";    
    final String independence = "INDEPENDENCE";    
    
    String currentControl, grid_Information_Style, strMiddle_Independence,
           strTop_Independence, strTop_Homogeneity, strTop_Experiment,
           strMiddle_Homogeneity, strMiddle_Experiment, studyType;
    String[] var1, var2;
    
    // My classes
    MyX2Alerts myAlerts;
    MyX2Utilities myX2Utilities;
    SmartTextField stf_variable1, stf_variable2, stf_nVar1, stf_nVar2;
    SmartTextField[] stf_var1Categories, stf_var2Categories; 
    ArrayList<SmartTextField> variablesPanel, categoriesPanel;
    SmartTextFieldHandler variablesPanelHandler, categoriesPanelHandler;
    X2Assoc_Model association_Control;
    X2GriddyWiddy x2GriddyWiddy;
    
    // POJOs / FX
    BorderPane observedValuesGrid;
    Button btnCancel, btnGoBack, btnClearControl, btnGoForward;
    ColumnConstraints columnConstraints;    
    GridPane gridChoiceControl, gridAssociation, gridCategories;
    HBox hBoxWhereToNext;
    Label lbl_nCategories_Var1, lbl_nVar2, lbl_nVar1, lbl_variable1,
          lbl_Var1, lbl_Var2, label_ULCorner, lbl_variable2;
    Scene assocDialogScene;    
    Text txtTitleIndependence, txtTitleHomog, txtTitleObserved,
         txtDescr_Var1, txtDescr_Var2, txtDescr_Pops,
         txt_Top, txt_Middle, strBottom; 
    VBox vBoxVisControl, vBoxChoiceControl, vBoxIndHomogControl, vBoxObsValuesControl;

     
    public X2ChoiceHelpDialog(X2Assoc_Model association_Control, String thisTypeOfAssociation) {
        
        this.association_Control = association_Control;
        myX2Utilities = new MyX2Utilities();
        myAlerts = new MyX2Alerts();
        studyType = thisTypeOfAssociation;
        variablesPanel = new ArrayList<>();
        variablesPanelHandler = new SmartTextFieldHandler();
        categoriesPanel = new ArrayList<>();
        categoriesPanelHandler = new SmartTextFieldHandler();
        
        initializeUIComponents();   
        doX2ChosenPanel();
        
        setResizable(true);
        setWidth(640); setHeight(325);
        setOnCloseRequest((WindowEvent we) -> {
            close();
        });
        
        closeRequested = false;
    }
    
/****************************************************************************
 *                       Independence/Homogeneity                           * 
 ***************************************************************************/

    private void doX2ChosenPanel() {
        currentControl = cc_X2Chosen;
 
        lbl_nVar1.setText("N categories:");
        lbl_nVar1.setPrefWidth(130);

        lbl_nVar2.setText("N categories:");
        lbl_nVar2.setPrefWidth(130);  
        
        gridAssociation = new GridPane();
        gridAssociation.setGridLinesVisible(false);   
        gridAssociation.setPadding(new Insets(10, 10, 10, 10));
        gridAssociation.setVgap(10);
        gridAssociation.setHgap(10);    
   
        gridAssociation.add(lbl_Var1, 0, 1);

        stf_variable1.getSmartTextField().setText("");
        gridAssociation.add(stf_variable1.getSmartTextField().getTextField(), 1, 1);
        
        gridAssociation.add(lbl_nVar1, 2, 1);
        stf_nVar1.getSmartTextField().getTextField().setPrefWidth(30);
        stf_nVar1.getSmartTextField().getTextField().setText("");
        stf_nVar1.setSmartTextField_MB_NONBLANK(true);
        gridAssociation.add(stf_nVar1.getSmartTextField().getTextField(), 3, 1);
        
        gridAssociation.add(lbl_Var2, 0, 3);  
        stf_variable2.getSmartTextField().setText("");
        gridAssociation.add(stf_variable2.getSmartTextField().getTextField(), 1, 3);
        
        gridAssociation.add(lbl_nVar2, 2, 3);  
        stf_nVar2.getSmartTextField().getTextField().setPrefWidth(30);
        stf_nVar2.getSmartTextField().setText("");
        stf_nVar2.setSmartTextField_MB_NONBLANK(true);       
        gridAssociation.add(stf_nVar2.getSmartTextField().getTextField(), 3, 3);
        
        armDirectionsButtons();
        stf_variable1.getSmartTextField().getTextField().requestFocus();
        vBoxVisControl.getChildren().addAll(txt_Top, gridAssociation, hBoxWhereToNext);  
    }
    
    public void constructCategoriesPanel()  {  
        
        currentControl = cc_CategoriesGrid;
        gridCategories = new GridPane();
        gridCategories.setPadding(new Insets(10, 10, 10, 10));
        gridCategories.setVgap(5);
        gridCategories.setHgap(5);
        lbl_variable1.setText(stf_variable1.getSmartTextField().getText());
        lbl_variable2.setText(stf_variable2.getSmartTextField().getText());
              
        gridCategories.add(lbl_variable1, 0, 5); 
        gridCategories.add(lbl_variable2, 3, 4); 

        GridPane.setHalignment(lbl_nVar1, HPos.LEFT);  
        GridPane.setHalignment(lbl_nVar2, HPos.LEFT);
        
        nRowCategories = stf_nVar1.getSmartTextInteger();
        nColCategories = stf_nVar2.getSmartTextInteger();

        nTotalCategories = nRowCategories + nColCategories;
                 
        stf_var1Categories = new SmartTextField[nRowCategories];
        stf_var2Categories = new SmartTextField[nColCategories];
        setWidth(275 + 100 * nColCategories);
        if (nColCategories < 3)
            setWidth(600);
        
        setHeight(225 + 50 * nRowCategories);
        for (int iCategories = 0; iCategories < nRowCategories; iCategories++) {
            int tempNext = (iCategories + 1) % nTotalCategories;
            int tempPrev = (iCategories - 1) % nTotalCategories;
            stf_var1Categories[iCategories] = new SmartTextField(categoriesPanelHandler, tempPrev, tempNext);
            categoriesPanel.add(iCategories, stf_var1Categories[iCategories]);  
            gridCategories.add(stf_var1Categories[iCategories].getSmartTextField().getTextField(), 0, iCategories + 6);    
        }
        
        stf_var1Categories[0].setPreAndPostSmartTF(nTotalCategories - 1, 1);
        
        for (int iCategories = 0; iCategories < nColCategories; iCategories++) {
            int tempNext = (nRowCategories + iCategories + 1) % nTotalCategories;
            int tempPrev = (nRowCategories + iCategories - 1) % nTotalCategories;
            stf_var2Categories[iCategories] = new SmartTextField(categoriesPanelHandler, tempPrev, tempNext); 
            categoriesPanel.add(nRowCategories + iCategories, stf_var2Categories[iCategories]); 
            gridCategories.add(stf_var2Categories[iCategories].getSmartTextField().getTextField(), iCategories + 2, 5); 
        }
        
        categoriesPanelHandler.setHandlerArrayList(categoriesPanel);
        categoriesPanelHandler.setHandlerTransversal(true);
        categoriesPanelHandler.setHandlerTransversalIndex(0);
        categoriesPanelHandler.setFocusRequest(0);

        stf_var2Categories[nColCategories - 1].setPreAndPostSmartTF(nTotalCategories - 2, 0);
        
        armDirectionsButtons();
        vBoxVisControl.getChildren().addAll(txt_Middle, gridCategories, hBoxWhereToNext);  
        stf_var1Categories[0].getSmartTextField().getTextField().requestFocus();
    }   //  End constructCategoriesGridControl
    
    public void constructObservedValuesPanel() {
        currentControl = cc_Observed;
        
        setWidth(200 + 175 * nColCategories);
        if (nColCategories < 4)
            setWidth(625);
        
        setHeight(175 + 50 * nRowCategories);       

        var1 = new String[nRowCategories];
        var2 = new String[nColCategories];
        x2GriddyWiddy = new X2GriddyWiddy(nRowCategories + 1, nColCategories + 1); 
        
        // Style for left colum and top row
        String theStyle = "-fxpadding: 10;" +
                          "-fx-border-style: solid inside;" + 
                          "-fx-font-weight: bold;" +
                          "-fx-border-width: 2;" +
                          "-fx-border-color: blue;" +
                          "-fx-background-color: blue;" +
                          "-fx-text-fill: white;";
        
            x2GriddyWiddy.getTF_col_row(0, 0).setStyle(theStyle);
            x2GriddyWiddy.getTF_col_row(0, 0).setEditable(false);
            x2GriddyWiddy.getTF_col_row(0, 0).setText("Categories");

            x2GriddyWiddy.getTF_col_row(0, 0).setAlignment(Pos.CENTER);
        
        for (int iCategories = 0; iCategories < nRowCategories; iCategories++) {
            var1[iCategories] = stf_var1Categories[iCategories].getSmartTextField().getText();
            x2GriddyWiddy.getTF_col_row(0, iCategories+1).setText(var1[iCategories]);
            x2GriddyWiddy.getTF_col_row(0, iCategories + 1).setStyle(theStyle);
            x2GriddyWiddy.getTF_col_row(0, iCategories + 1).setEditable(false);
            x2GriddyWiddy.getTF_col_row(0, iCategories + 1).setAlignment(Pos.CENTER);
        }
        
        for (int iCategories = 0; iCategories < nColCategories; iCategories++) {
            var2[iCategories] = stf_var2Categories[iCategories].getSmartTextField().getText(); 
            x2GriddyWiddy.getTF_col_row(iCategories + 1, 0).setText(var2[iCategories]);
            x2GriddyWiddy.getTF_col_row(iCategories + 1, 0).setStyle(theStyle);
            x2GriddyWiddy.getTF_col_row(iCategories + 1, 0).setEditable(false);
            x2GriddyWiddy.getTF_col_row(iCategories + 1, 0).setAlignment(Pos.CENTER);
        }

        observedValuesGrid = x2GriddyWiddy.getGridPane();
       
        armDirectionsButtons();
        vBoxVisControl.getChildren().addAll(strBottom, observedValuesGrid, hBoxWhereToNext);       
    }
  
    private void initializeUIComponents() {   
        
    // **********************   Buttons  ***********************************
    
        btnCancel = new Button("Back to Menu");
        btnCancel.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                close();
            }
        });

        btnClearControl = new Button("Clear Entries");
        btnClearControl.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) { 
                switch (currentControl) {

                    case cc_X2Chosen: 
                        stf_variable1.getSmartTextField().setText(""); 
                        stf_nVar1.getSmartTextField().setText("");
                        stf_variable2.getSmartTextField().setText(""); 
                        stf_nVar2.getSmartTextField().setText("");
                        break;   

                    case cc_CategoriesGrid:
                        for (int iCategories = 0; iCategories < nTotalCategories; iCategories++) {
                            if (iCategories < nRowCategories) {
                                stf_var1Categories[iCategories].getSmartTextField().setText("");                        
                            }

                            if (iCategories < nColCategories) {
                                stf_var2Categories[iCategories].getSmartTextField().setText("");                       
                            }
                        }
                        break;

                    case cc_Observed:
                        for (int i = 0; i < nRowCategories; i++) {
                            for (int j = 0; j < nColCategories; j++) {
                                x2GriddyWiddy.getTF_col_row(j + 1, i + 1).setText("");
                            } 
                        } 
                        break;

                    default:
                        System.out.println("Ack!!!  Fatal error!!  In switch(currentControl, cc = " + currentControl);
                        System.exit(4);   
                    }   
            }
        });
               
        btnGoForward = new Button("Continue");
        btnGoForward.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                boolean OK_toGoForward = true;   
                
                switch (currentControl) {

                    case cc_X2Chosen:
                        if (checkOKChosen() == true) {                          
                            vBoxVisControl.getChildren().removeAll(txt_Top, gridAssociation, hBoxWhereToNext);  
                            constructCategoriesPanel();  
                        }
                        else {System.out.println("x2Chosen known false");}
                        break;   

                    case cc_CategoriesGrid:
                        if (checkOKCategoriesGrid() == true) {                        
                            vBoxVisControl.getChildren().removeAll(txt_Middle, gridCategories, hBoxWhereToNext);                
                            constructObservedValuesPanel();
                        }
                        else {System.out.println("Categories Grid known false");}
                        break;

                    case cc_Observed:   
                        /**************************************************
                         *  Check for all #s non-blank -- false = blank   *
                         *************************************************/
                        for (int i = 1; i <= nRowCategories; i++) {
                            for (int j = 1; j <= nColCategories; j++) {;
                                if (myX2Utilities.check_TextField_4Blanks(x2GriddyWiddy.getTF_col_row(j, i) ) == false) {
                                    myAlerts.showMissingInformationAlert();
                                    return;
                                }
                            } 
                        }  
                       
                        vBoxVisControl.getChildren().removeAll(strBottom, observedValuesGrid, hBoxWhereToNext); 
                        hide();
                        break;

                    default:
                        System.out.println("Ack!!!  Fatal error!!  In switch(currentControl, cc = " + currentControl);
                        System.exit(4);   
                    } 
                }
        });         
        
        btnGoBack = new Button("goBack");
        btnGoBack.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {  
                
                switch (currentControl) {

                    case cc_X2Chosen: 
                        btnCancel.fire();
                        break;  // back to menu 

                    case cc_CategoriesGrid:
                        vBoxVisControl.getChildren().removeAll(txt_Middle, gridCategories, hBoxWhereToNext);              
                        doX2ChosenPanel();
                        break;

                    case cc_Observed:
                        vBoxVisControl.getChildren().removeAll(strBottom, observedValuesGrid, hBoxWhereToNext); 
                        constructCategoriesPanel();
                        break;

                    default:
                        System.out.println("Ack!!!  Fatal error!!  IHG");
                        System.exit(2);   
                } 
        }
    });       
        
    // **********************   Strings and Text  ***********************************
    
    strTop_Independence = "\n\n         *****   Chi square test of independence   *****" +
                           "\n\n     In the fields below, indicate the two variables under study," +                
                           "\n     and also the number of categories for each variable. ";

    strTop_Homogeneity = "\n\n          *****   Chi square test of homogeneity   *****" +
                          "\n\n     In the fields below, generally describe the overall population" +                
                          "\n     under study, and the number of categories in the variable under " +
                          "\n     study.";     

    strTop_Experiment = "\n\n                   *****        Experimental study        *****" +
                         "\n\n     In the fields below, globally define the experimental variable," +                
                         "\n     the response variable, the number of treatments, and the number of" +
                         "\n     categorical response categories.";  


    strMiddle_Independence = "\n\n     *****   Chi square test of independence   *****" +
                              "\n\n     In the fields below, indicate the specific values of the " +                
                              "\n     two variables under study.";

    strMiddle_Homogeneity = "\n\n      *****   Chi square test of homogeneity   *****" +
                             "\n\n     In the fields below, indicate the sub-populations and the" +                
                             "\n     values of the categorical variable under study.";     

    strMiddle_Experiment = "\n\n                 *****       Experimental study            *****" +
                            "\n\n         In the fields below, indicate the specific treatments and " +                
                            "\n         categorical values of the response variable under study."; 

    strBottom = new Text("\n\n      ******  In the fields below, enter the observed values.  *****\n\n");

// **********************   Labels  *********************************** 
    lbl_Var1 = new Label("");        
    label_ULCorner = null;       
    label_ULCorner = new Label("UL Corner");        
    lbl_nVar1 = new Label(""); 
    lbl_Var2 = new Label("");
    lbl_nVar2 = new Label("");
    lbl_variable1 = new Label("");
    lbl_variable2 = new Label("");        

    label_ULCorner = new Label("UL Corner");      

    switch (studyType) {

        case experiment: 
            txt_Top = new Text(strTop_Experiment); 
            txt_Middle = new Text(strMiddle_Experiment);  
            lbl_Var1.setText("  Response variable: "); 
            lbl_Var2.setText(" Treatment variable: "); 
            break;   

        case homogeneity: 
            txt_Top = new Text(strTop_Homogeneity); 
            txt_Middle = new Text(strMiddle_Homogeneity);
            lbl_Var1.setText("  Response variable: "); 
            lbl_Var2.setText("Population variable: "); 
            break;  

        case independence: 
            txt_Top = new Text(strTop_Independence); 
            txt_Middle = new Text(strMiddle_Independence);
            lbl_Var1.setText("Row variable: "); 
            lbl_Var2.setText("Col variable: "); 
            break;

        default:
            System.out.println("Ack!!!  Fatal error!!  Bad study type!!");
            System.exit(2);              
    }

// **********************   TextFields  ***********************************         

    stf_variable1 = new SmartTextField(variablesPanelHandler, 3, 1);
    variablesPanel.add(0, stf_variable1);     


    stf_nVar1 = new SmartTextField(variablesPanelHandler, 0, 2); 
    stf_nVar1.setSmartTextField_MB_POSITIVEINTEGER(true); 
    variablesPanel.add(1, stf_nVar1);

    stf_variable2 = new SmartTextField(variablesPanelHandler, 1, 3);
    variablesPanel.add(2, stf_variable2);

    stf_nVar2 = new SmartTextField(variablesPanelHandler, 2, 0); 
    stf_nVar2.setSmartTextField_MB_POSITIVEINTEGER(true); 
    variablesPanel.add(3, stf_nVar2);   
    
    variablesPanelHandler.setHandlerArrayList(variablesPanel);
    variablesPanelHandler.setHandlerTransversal(true);
    variablesPanelHandler.setHandlerTransversalIndex(0);
    variablesPanelHandler.setFocusRequest(0);

 // **********************   HBoxes, VBoxes *******************************   
    vBoxVisControl = new VBox(); 
    vBoxChoiceControl = new VBox(); 
    vBoxIndHomogControl = new VBox();        
    vBoxObsValuesControl = new VBox();  

    hBoxWhereToNext = new HBox();
    hBoxWhereToNext.setAlignment(Pos.CENTER);
    // Insets top, right, bottom, left
    Insets margin = new Insets(5, 10, 0, 10);
    hBoxWhereToNext.setMargin(btnCancel, margin);
    hBoxWhereToNext.setMargin(btnGoBack, margin);
    hBoxWhereToNext.setMargin(btnClearControl, margin);
    hBoxWhereToNext.setMargin(btnGoForward, margin);
    //whereToNext.setStyle("-fx-border-width: 10.0; -fx-border-color: navy;");
    hBoxWhereToNext.getChildren().addAll(btnCancel, btnGoBack, btnClearControl, btnGoForward);

     // *************************   Misc  ***********************************  
    
    Font CourierNew_14 = Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, 14);
    txt_Top.setFont(CourierNew_14);
    txt_Middle.setFont(CourierNew_14);
    strBottom.setFont(CourierNew_14);
    
    lbl_Var1.setFont(CourierNew_14);
    lbl_Var2.setFont(CourierNew_14);
    lbl_nVar1.setFont(CourierNew_14);
    lbl_nVar2.setFont(CourierNew_14);
    lbl_variable1.setFont(CourierNew_14); 
    lbl_variable2.setFont(CourierNew_14);    
}    
    
    
    public void setCurrentFocusOn(int thisListArrayElement) {
        switch (currentControl) {
            case cc_X2Chosen: 
                variablesPanel.get(thisListArrayElement).getSmartTextField().getTextField().requestFocus();
            break;   

            case cc_CategoriesGrid:
                categoriesPanel.get(thisListArrayElement).getSmartTextField().getTextField().requestFocus();
            break;

            case cc_Observed:

            break;

            default: {  }
 
            }
    }

    // 'Final' check on any data entry problems for the X2Chosen Panel
    private boolean checkOKChosen() {
        boolean[] okToContinue = new boolean[4];
        okToContinue[0] = variablesPanelHandler.finalCheck4IndividualSTFBlank(stf_variable1);
        okToContinue[1] = variablesPanelHandler.finalCheck4IndividualSTFBlank(stf_variable2);
        okToContinue[2] = variablesPanelHandler.finalCheck4IndividualSTFBlank(stf_nVar1);
        okToContinue[3] = variablesPanelHandler.finalCheck4IndividualSTFBlank(stf_nVar2);
        
        for (int booly = 0; booly < 4; booly++) {
            if (okToContinue[booly]  == false) {
                myAlerts.showMustBeNonBlankAlert();
                return false;
            }
        }
        return true;
    }

    private boolean checkOKCategoriesGrid() {
        boolean okToContinue;
        int nCatsToCheck = categoriesPanel.size();
        okToContinue = categoriesPanelHandler.finalCheckForBlanksInArray(0, nCatsToCheck - 1);
        if (okToContinue  == false) {
            myAlerts.showMissingInformationAlert();
            return false;
        }
        
        // Check for unique row and column categories.  
        for (int ithCat = 0; ithCat < nRowCategories - 1; ithCat++) {    
            for (int jthCat = ithCat + 1; jthCat < nRowCategories; jthCat++) {
                String temp1 = stf_var1Categories[ithCat].getText();
                String temp2 = stf_var1Categories[jthCat].getText();
                if (temp1.equals(temp2)) {
                    myAlerts.showNonUniqueCategoriesAlert();
                    return false;
                } 
            }
        }

        // Column categories MUST be unique b/c they are associated with the
        // categorical horizontal axis.
        for (int ithCat = 0; ithCat < nColCategories - 1; ithCat++) {    
            for (int jthCat = ithCat + 1; jthCat < nColCategories; jthCat++) {
                String temp1 = stf_var2Categories[ithCat].getText();
                String temp2 = stf_var2Categories[jthCat].getText();
                if (temp1.equals(temp2)) {
                    myAlerts.showNonUniqueCategoriesAlert();
                    return false;
                } 
            }
        }
        
        return true;
    }
   
    public void armDirectionsButtons() {
        btnCancel.arm(); btnGoBack.arm(); btnClearControl.arm(); btnGoForward.arm(); 
    }
    
    public String getVariable_1()  { return stf_variable1.getSmartTextField().getText(); }
    public int getNCategories_1()  { return Integer.parseInt(stf_nVar1.getSmartTextField().getText()); }
    
    public String getIthCategory_1(int ith) {
        return stf_var1Categories[ith].getSmartTextField().getText();
    } 
    
    public String getVariable_2()  { return stf_variable2.getSmartTextField().getText(); }
    public int getNCategories_2()  { return Integer.parseInt(stf_nVar2.getSmartTextField().getText()); }
    
    public String getIthCategory_2(int ith) {
        return stf_var2Categories[ith].getSmartTextField().getText();
    }     
    
    public int getObservedValue(int ithRow, int jthCol)  {
        String preTrimmed = new String(x2GriddyWiddy.getTF_col_row(jthCol + 1, ithRow + 1).getText());
        int theInt = Integer.parseInt(preTrimmed.trim());        
        return theInt;
    }    
    
    public void showAndWait() {
        assocDialogScene = new Scene(vBoxVisControl);
        setScene(assocDialogScene); 
    }

    public boolean getCloseRequested() { return closeRequested; }    
}
