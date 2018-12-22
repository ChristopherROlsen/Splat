/************************************************************
 *                 TwoProp_SummaryStats_Dialog              *
 *                          12/22/18                        *
 *                            12:00                         *
 ***********************************************************/
package dialogs;

import java.util.ArrayList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import smarttextfield.*;
import genericClasses.*;

public class TwoProp_SummaryStats_Dialog extends Stage { 
    
    // POJOs
    boolean okToContinue, runAnalysis, ok, caughtYa, dataPresent;
    boolean bool_Prop1Good, bool_Prop2Good, bool_Succ1Good, bool_Succ2Good, 
            bool_N1Good, bool_N2Good, bool_AlphaGood;
    boolean allFieldsGood;
    
    int succ1, succ2, n1, n2, alphaIndex, ciIndex;
    Integer suspectedCount;
    double prop1, prop2, significanceLevel, confidenceLevel, daNullDiff;
    double hypothesizedDifference, suspectedProp;
    double[] theAlphaLevs, theCILevs; 
    
    String strHypNE, strHypLT, strHypGT, strHypNull, strNullAndAlt, strAltHypChosen,  
           str_Group1_Title, str_Group1_SumInfo, str_OROne, str_Group1_N,
           str_Group2_Title, str_Group2_SumInfo, str_ORTwo, str_Group2_N,
           resultAsString;
    
    String strProp1, strProp2, strSucc1, strSucc2, strN1, strN2;
    final String toBlank = "";
    
    final String currNullDiff = "Current null difference (\u03BC\u2081 - \u03BC\u2082) = ";
    
    final String strBadDouble = "Ok, so here's the deal.  There are numbers, and there are other"
                        + "\nthan numbers, like words and punctuation.  What you must enter in"
                        + "\nthis field are numbers, specifically numbers of the Arabic persuation."
                        + "\nThe Decline and Fall of the Roman Empire included the Decline and"
                        + "\nFall of Roman Numerals.  Now, let's try this number thing again...";
    
    // My classes
    
    // JavaFX POJOs
    Button changeNull, okButton, cancelButton, resetButton;;
    RadioButton hypNE, hypLT, hypGT, hypNull;

    Label lblNullAndAlt, lbl_Title, lblSigLevel, ciLabel, alphaLabel, lbl_nullDiffInfo;
    HBox middlePanel, bottomPanel, hBox_GPOne_SuccessRow, hBox_Group2_SuccessRow,
         alphaAndCI, nullDiffInfo;

    VBox root, nullsPanel, numValsPanel, group_1, group_Prop_2,
         ciBox, alphaBox, infChoicesPanel; 
    
    TextInputDialog txtDialog;
  
    Scene scene;
    Separator sep_NullsFromInf, sep_InfFromNumbers, sep_MiddleAndBottom,
              sep_Prop1_and_Prop2, sep_Alpha, sep;  
    
    Text txt_Group1_Title, txt_Group1_SumInfo, txt_OROne, txt_Group1_N,
         txt_Group2_Title, txt_Group2_SumInfo, txt_ORTwo, txt_Group2_N;
    
    ArrayList<SmartTextField> al_stfForEntry; 
    SmartTextField stf_Prop1, stf_Succ1, stf_N1, stf_Prop2, stf_Succ2, stf_N2;

    ObservableList<String> ciLevels, alphaLevels;
    ListView<String> ciView, alphaView;
    
    SmartTextFieldHandler propNStuffHandler;

    public TwoProp_SummaryStats_Dialog() {
        theAlphaLevs = new double[] { 0.10, 0.05, 0.01};
        theCILevs = new double[] {0.90, 0.95, 0.99};
        // significanceLevel = 0.05;   //  Initial value
        // confidenceLevel = 0.95;     //  Initial value
        sep = new Separator();
        sep.setOrientation(Orientation.VERTICAL);

        root = new VBox();
        dataPresent = false;
        root.setAlignment(Pos.CENTER);
        
        propNStuffHandler = new SmartTextFieldHandler();
        al_stfForEntry = new ArrayList<>();
        propNStuffHandler.setHandlerArrayList(al_stfForEntry);
        propNStuffHandler.setHandlerTransversal(true);
        propNStuffHandler.setHandlerTransversalIndex(0);
        
        
        lbl_Title = new Label("Inference for two independent proportions");
        lbl_Title.setPadding(new Insets(10, 10, 10, 10));        
        lbl_Title.getStyleClass().add("dialogTitle");
        
        lbl_Title.setPadding(new Insets(10, 10, 10, 10));
        sep_NullsFromInf = new Separator();
        sep_NullsFromInf.setOrientation(Orientation.VERTICAL);
        sep_InfFromNumbers = new Separator();
        sep_InfFromNumbers.setOrientation(Orientation.VERTICAL);
        sep_MiddleAndBottom = new Separator();
        sep_Prop1_and_Prop2 = new Separator();
        sep_Alpha = new Separator();
        sep_Alpha.setMinHeight(10);

        makeInfDecisionsPanel();
        makeNullsPanel();
        makeNumericValuesPanel();
        makeBottomPanel();
        
        middlePanel = new HBox();
        middlePanel.setSpacing(30);
        middlePanel.getChildren().addAll(nullsPanel, sep_NullsFromInf,
                                         infChoicesPanel,sep_InfFromNumbers,
                                         numValsPanel);
        middlePanel.setAlignment(Pos.CENTER);
        
        root.getChildren().addAll(lbl_Title, 
                                  sep_NullsFromInf,
                                  middlePanel,
                                  sep_MiddleAndBottom,
                                  bottomPanel);        
        
        scene = new Scene (root, 725, 400);
        setTitle("Inference for a difference in proportions");
        setScene(scene);
        showAndWait();
    }  

    
    private void makeNullsPanel() { 
        nullsPanel = new VBox();

        strAltHypChosen = "NotEqual";
        changeNull = new Button("Change null difference");
        strNullAndAlt = "  Choose from the null and \n  alternate hypothesis pairs \n  listed below:";
        lblNullAndAlt = new Label(strNullAndAlt);
        
        //             Props
        strHypNull = "p\u2081 - p\u2082 = 0";
        strHypNE = "p\u2081 - p\u2082 \u2260 0";
        strHypLT = "p\u2081 - p\u2082 < 0";
        strHypGT = "p\u2081 - p\u2082 > 0";
        
        hypNE = new RadioButton(strHypNull + "\n" + strHypNE);
        hypLT = new RadioButton(strHypNull + "\n" + strHypLT);
        hypGT = new RadioButton(strHypNull + "\n" + strHypGT);
        
        // top, right, bottom, left
        hypNE.setPadding(new Insets(10, 10, 10, 10));
        hypLT.setPadding(new Insets(10, 10, 10, 10));
        hypGT.setPadding(new Insets(10, 10, 10, 10));
        
        hypNE.setSelected(true);
        hypLT.setSelected(false);
        hypGT.setSelected(false);
 
        nullsPanel.getChildren().addAll(lblNullAndAlt, 
                                       hypNE, 
                                       hypLT, 
                                       hypGT);
        
        hypNE.setOnAction(e->{
            RadioButton tb = ((RadioButton) e.getTarget());
            String daID = tb.getId();
            Boolean checkValue = tb.selectedProperty().getValue();
            hypNE.setSelected(true);
            hypLT.setSelected(false);
            hypGT.setSelected(false);
            strAltHypChosen = "NotEqual";
        });
            
        hypLT.setOnAction(e->{
            RadioButton tb = ((RadioButton) e.getTarget());

            String daID = tb.getId();
            Boolean checkValue = tb.selectedProperty().getValue();
            hypNE.setSelected(false);
            hypLT.setSelected(true);
            hypGT.setSelected(false);
            strAltHypChosen = "LessThan";
        });
            
        hypGT.setOnAction(e->{
            RadioButton tb = ((RadioButton) e.getTarget());

            String daID = tb.getId();
            Boolean checkValue = tb.selectedProperty().getValue();
            hypNE.setSelected(false);
            hypLT.setSelected(false);
            hypGT.setSelected(true);
            strAltHypChosen = "GreaterThan";
        });
        
            changeNull.setOnAction((ActionEvent event) -> {
                        
                Alert cantDoNull = new Alert(Alert.AlertType.INFORMATION);
                cantDoNull.setTitle("Just so you know...");
                cantDoNull.setHeaderText("You can't actually 'test' this hypothesis");
                cantDoNull.setContentText("Ok, so here's the deal.  Given the usual information about two "
                            + "\nproportions, the standard error of the sampling distribution is"
                            + "\nnot uniquely determined.  The best that can be done -- which, of"
                            + "\ncourse what this program will do -- is find a confidence interval"
                            + "\nfor the difference in proportions.");
                cantDoNull.showAndWait();
                okToContinue = true;
        });
    }
 
    private void makeNumericValuesPanel() {
        numValsPanel = new VBox();
        group_1 = new VBox();
        group_1.setAlignment(Pos.CENTER);
        group_1.setPadding(new Insets(5, 5, 5, 5));
        str_Group1_Title = "Treatment / Population #1";
        txt_Group1_Title = new Text(str_Group1_Title);
    
        str_Group1_SumInfo = "   Summary Information";
        txt_Group1_SumInfo = new Text(str_Group1_SumInfo);
        
        str_OROne = "  Prop #1    OR   Count #1";
        txt_OROne = new Text(str_OROne);    
        
        hBox_GPOne_SuccessRow = new HBox();
        
        stf_Prop1 = new SmartTextField(propNStuffHandler, 6, 2);
        stf_Prop1.getTextField().setPrefColumnCount(12);
        stf_Prop1.getTextField().setMaxWidth(65);
        stf_Prop1.getTextField().setPadding(new Insets(5, 10, 5, 5));
        stf_Prop1.getTextField().setText(toBlank);
        stf_Prop1.getTextField().setId("Prop1");
        stf_Prop1.setSmartTextField_MB_NONBLANK(false);
        al_stfForEntry.add(stf_Prop1);
        
        stf_Prop1.getTextField().setOnAction(e -> {            
            bool_Prop1Good = checkProportion(stf_Prop1.getTextField());
            if ((bool_Prop1Good == true) && (bool_N1Good == true)) {
                strProp1 = stf_Prop1.getText();
                succ1 = (int)Math.floor(Double.valueOf(strProp1) * Double.valueOf(stf_N1.getText()) + 0.5);
                stf_Succ1.setText(String.valueOf(succ1));
            }
        });
       
        stf_Succ1 = new SmartTextField(propNStuffHandler, 0, 2);
        stf_Succ1.getTextField().setPrefColumnCount(12);
        stf_Succ1.getTextField().setMaxWidth(65);
        stf_Succ1.getTextField().setText(toBlank); 
        stf_Succ1.getTextField().setId("Successes1");
        stf_Succ1.setSmartTextField_MB_NONBLANK(false);
        al_stfForEntry.add(stf_Succ1);
        stf_Succ1.getTextField().setOnAction(e -> {
            bool_Succ1Good = checkCount(stf_Succ1.getTextField());
            if ((bool_Succ1Good == true) && (bool_N1Good == true)) {
                prop1 = Double.valueOf(stf_Succ1.getText()) / n1;
                strProp1 = String.valueOf(prop1);
                stf_Prop1.setText(strProp1);
                bool_Prop1Good = true;
            }
        });
        hBox_GPOne_SuccessRow.setAlignment(Pos.CENTER);
        hBox_GPOne_SuccessRow.getChildren()
                             .addAll(stf_Prop1.getTextField(),
                                     stf_Succ1.getTextField());
        hBox_GPOne_SuccessRow.setSpacing(25);

        str_Group1_N = "   Group / Sample Size #1";
        
        txt_Group1_N = new Text(str_Group1_N);   
        
        stf_N1 = new SmartTextField(propNStuffHandler, 1, 3);
        stf_N1.getTextField().setPrefColumnCount(12);
        stf_N1.getTextField().setMaxWidth(65);
        stf_N1.getTextField().setPadding(new Insets(5, 10, 5, 5));
        stf_N1.getTextField().setText(toBlank);
        stf_N1.getTextField().setId("SampleSize1");
        // stf_N1.setSmartTextField_MB_NONBLANK(false);
        al_stfForEntry.add(stf_N1);

        stf_N1.getTextField().setOnAction(e -> {
            bool_N1Good = checkCount(stf_N1.getTextField());
            if ((bool_N1Good == true) && (bool_Prop1Good == true)) {
                succ1 = (int)Math.floor(n1 * prop1 + .5);
                strSucc1 = String.valueOf(succ1);
                stf_Succ1.setText(strSucc1);
                bool_Succ1Good = true;
            }
        });
        
        group_1.getChildren().addAll(txt_Group1_Title,
                                         txt_Group1_SumInfo,
                                         txt_OROne,
                                         hBox_GPOne_SuccessRow,
                                         txt_Group1_N,
                                         stf_N1.getTextField());
        
        group_Prop_2 = new VBox();
        group_Prop_2.setAlignment(Pos.CENTER);
        group_Prop_2.setPadding(new Insets(5, 5, 5, 5));
        str_Group2_Title = "Treatment / Population #2";
        txt_Group2_Title = new Text(str_Group2_Title);

        str_Group2_SumInfo = "   Summary Information";
        txt_Group2_SumInfo = new Text(str_Group1_SumInfo);
        
        str_ORTwo = "  Prop #2    OR   Count #2";
        txt_ORTwo = new Text(str_ORTwo);

        hBox_Group2_SuccessRow = new HBox();
        
        stf_Prop2 = new SmartTextField(propNStuffHandler, 2, 5);
        stf_Prop2.getTextField().setPrefColumnCount(12);
        stf_Prop2.getTextField().setMaxWidth(65);
        stf_Prop2.getTextField().setPadding(new Insets(5, 10, 5, 5));
        stf_Prop2.getTextField().setText(toBlank);
        stf_Prop2.getTextField().setId("Prop2"); 
        stf_Prop2.setSmartTextField_MB_NONBLANK(false);
        al_stfForEntry.add(stf_Prop2);
        
        stf_Prop2.getTextField().setOnAction(e -> {
            bool_Prop2Good = checkProportion(stf_Prop2.getTextField());
            if ((bool_Prop2Good == true) && (bool_N2Good == true)) {
                strProp2 = stf_Prop2.getText();
                succ2 = (int)Math.floor(Double.valueOf(strProp2) * Double.valueOf(stf_N2.getText()) + 0.5);
                stf_Succ2.setText(String.valueOf(succ2));
            }
        });

        stf_Succ2 = new SmartTextField(propNStuffHandler, 3, 5);
        stf_Succ2.getTextField().setPrefColumnCount(8);
        stf_Succ2.getTextField().setMaxWidth(50);
        stf_Succ2.getTextField().setText(toBlank);    
        stf_Succ2.getTextField().setId("Successes2");
        stf_Succ2.setSmartTextField_MB_NONBLANK(false);
        al_stfForEntry.add(stf_Succ2);
        
        stf_Succ2.getTextField().setOnAction(e -> {
            bool_Succ2Good = checkCount(stf_Succ2.getTextField());
            if ((bool_Succ2Good == true) && (bool_N2Good == true)) {
                prop2 = Double.valueOf(stf_Succ2.getText()) / n2;
                strProp2 = String.valueOf(prop2);
                stf_Prop2.setText(strProp2);
                bool_Prop2Good = true;
            }
        });
        hBox_Group2_SuccessRow.setAlignment(Pos.CENTER);
        hBox_Group2_SuccessRow.getChildren()
                             .addAll(stf_Prop2.getTextField(),
                                     txt_ORTwo,
                                     stf_Succ2.getTextField());
        hBox_Group2_SuccessRow.setSpacing(25);
        str_Group2_N = "   Group / Sample Size #2";
        txt_Group2_N = new Text(str_Group2_N);  
        
        stf_N2 = new SmartTextField(propNStuffHandler, 4, 0);
        stf_N2.getTextField().setPrefColumnCount(8);
        stf_N2.getTextField().setMaxWidth(50);
        stf_N2.getTextField().setPadding(new Insets(5, 10, 5, 5));
        stf_N2.getTextField().setText(toBlank);    
        stf_N2.getTextField().setId("SampleSize2");
        // stf_N2.setSmartTextField_MB_NONBLANK(false);
        al_stfForEntry.add(stf_N2);
        
        stf_N2.getTextField().setOnAction(e -> {
            bool_N2Good = checkCount(stf_N2.getTextField());
            if ((bool_N2Good == true) && (bool_Prop2Good == true)) {
                succ2 = (int)Math.floor(n2 * prop2 + .5);
                strSucc2 = String.valueOf(succ2);
                stf_Succ2.setText(strSucc2);
                bool_Succ2Good = true;
            }
        });    
        
        propNStuffHandler.setFocusRequest(0);
        
        group_Prop_2.getChildren().addAll(txt_Group2_Title,
                                         txt_Group2_SumInfo,
                                         txt_ORTwo,
                                         hBox_Group2_SuccessRow,
                                         txt_Group2_N,
                                         stf_N2.getTextField());      
        
        numValsPanel.getChildren()
                  .addAll(group_1, 
                          sep_Prop1_and_Prop2,
                          group_Prop_2);
    }
    
    private void makeInfDecisionsPanel() {
        hypothesizedDifference = 0.;
        daNullDiff = 0.0;
       
        ciLabel = new Label("   Select conf level");
        ciLabel.setMaxWidth(120);
        ciLabel.setMinWidth(120);
        ciLevels = FXCollections.<String>observableArrayList("          90%", "          95%", "          99%");
        ciView = new ListView<>(ciLevels);
        ciView.setOrientation(Orientation.VERTICAL);
        ciView.setPrefSize(120, 100);
        
        ciView.getSelectionModel()
              .selectedItemProperty()
              .addListener((new ChangeListener<String>() {
                  public void changed(ObservableValue<? extends String> ov,
                     final String oldvalue, final String newvalue) {
                       ciChanged(ov, oldvalue, newvalue);
                  }
              }));
 
        alphaLabel = new Label("   Select alpha level");
        alphaLabel.setMaxWidth(120);
        alphaLabel.setMinWidth(120);
        alphaLevels = FXCollections.<String>observableArrayList("          0.10", "          0.05", "          0.01");
        alphaView = new ListView<>(alphaLevels);
        alphaView.setOrientation(Orientation.VERTICAL);
        alphaView.setPrefSize(120, 100);
        
        alphaView.getSelectionModel()
              .selectedItemProperty()
              .addListener((new ChangeListener<String>() {
                  public void changed(ObservableValue<? extends String> ov,
                     final String oldvalue, final String newvalue) {
                       alphaChanged(ov, oldvalue, newvalue);
                  }
              }));
        
        alphaView.getSelectionModel().select(1);    //  Set at .05
        ciView.getSelectionModel().select(1);   //  Set at 95%
        ciBox = new VBox();
        
        ciBox.getChildren().addAll(ciLabel, ciView);
        alphaBox = new VBox();
        alphaBox.getChildren().addAll(alphaLabel, alphaView);

        alphaAndCI = new HBox();
        alphaAndCI.getChildren().addAll(alphaBox, sep, ciBox);

        alphaAndCI = new HBox();
        alphaAndCI.setPadding(new Insets(10, 5, 5, 5));
        alphaAndCI.getChildren().addAll(alphaBox, sep, ciBox);  
        
        infChoicesPanel = new VBox();
        infChoicesPanel.setAlignment(Pos.CENTER);
        infChoicesPanel.getChildren().add(alphaAndCI);           
    }
    
    private void makeBottomPanel() {        
        bottomPanel = new HBox(10);
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setPadding(new Insets(5, 5, 5, 5));
        
        okButton = new Button("Compute");
        cancelButton = new Button("Cancel");
        resetButton = new Button("Reset");
        
        okButton.setOnAction((ActionEvent event) -> { 
            
        bool_Prop1Good = DataUtilities.textFieldHasProportion(stf_Prop1.getTextField());
        bool_Prop2Good = DataUtilities.textFieldHasProportion(stf_Prop2.getTextField());
        bool_Succ1Good = checkCount(stf_Succ1.getTextField());
        bool_Succ2Good = checkCount(stf_Succ2.getTextField());
        bool_N1Good = checkCount(stf_N1.getTextField());
        bool_N2Good = checkCount(stf_N2.getTextField());  

       allFieldsGood = bool_Prop1Good && bool_Succ1Good  
                      && bool_Prop2Good && bool_Succ2Good 
                      && bool_N1Good && bool_N2Good;

        if (allFieldsGood == true) {
            dataPresent = true;
            close();
        } 
    });
        
        setOnCloseRequest((WindowEvent t) -> {
            dataPresent = false;
            close();
        });
        
        cancelButton.setOnAction((ActionEvent event) -> {
            dataPresent = false;
            close();
        });

        resetButton.setOnAction((ActionEvent event) -> {
            stf_Prop1.setText(toBlank); 
            stf_Prop2.setText(toBlank);
            stf_Succ1.setText(toBlank); 
            stf_Succ2.setText(toBlank);
            stf_N1.setText(toBlank);
            stf_N2.setText(toBlank);
            
            bool_Prop1Good = false; 
            bool_Prop2Good = false; 
            bool_Succ1Good = false; 
            bool_Succ2Good = false; 
            bool_N1Good = false; 
            bool_N2Good = false;
            
        });
        
        bottomPanel.getChildren().addAll(okButton, cancelButton, resetButton);
    }
    
    public boolean checkProportion(TextField theTF) {
        boolean propIsGood = false; 
        double goodProp;

        propIsGood = DataUtilities.textFieldHasProportion(theTF);
        //  If it really was a number, check for Prop
        if (propIsGood == false) {
             MyAlerts.showBadFractionAlert();
        }    
        else {
            goodProp = Double.valueOf(theTF.getText());
            if (theTF.getId().equals("Prop1")) {
                prop1 = goodProp;
                bool_Prop1Good = true;
                if (bool_N1Good == true) {
                    strProp1 = stf_Prop1.getText();
                    succ1 = (int)Math.floor(prop1 * n1 + 0.5);
                    stf_Succ1.setText(String.valueOf(succ1));    //  Set to blank???
                }      
            }
            if (theTF.getId().equals("Prop2")) {
                prop2 = goodProp;
                bool_Prop2Good = true;
                if (bool_N2Good == true) {
                    strProp2 = stf_Prop2.getText();
                    succ2 = (int)Math.floor(prop2 * n2 + 0.5);
                    stf_Succ2.setText(String.valueOf(succ2));    //  Set to blank???
                }
            }
        }
        return propIsGood;
    }
    
    public boolean checkCount(TextField theTF) {
        boolean countIsGood = false;
        caughtYa = false;
        suspectedCount = 0;
        try {
            suspectedCount = StringUtilities.TextFieldToPrimitiveInt( theTF );
        }
        catch (NumberFormatException ex ){ 
            MyAlerts.showMustBePositiveIntegerAlert();
            caughtYa = true;
        }
        
        //  If it really was a number, check for legal count
        if ((caughtYa == false) && (suspectedCount < 1)) {
            DataUtilities.textFieldHasPositiveInteger(theTF);
            caughtYa = true;
        }  
        
        // Seems to be OK...
        countIsGood = true;
        if (caughtYa == false) {
            if (theTF.getId().equals("SampleSize1")) {
                n1 = suspectedCount;
                bool_N1Good = true;
                if (bool_Succ1Good == true) {
                    prop1 = (double)succ1 / (double)n1;
                    strProp1 = String.valueOf(prop1);
                    stf_Prop1.setText(strProp1);
                    bool_Prop1Good = true;
                }
            }
            
            if (theTF.getId().equals("SampleSize2")) {
                n2 = suspectedCount;
                bool_N2Good = true;
                if (bool_Succ2Good == true) {
                    prop2 = (double)succ2 / (double)n2;
                    strProp2 = String.valueOf(prop2);
                    stf_Prop2.setText(strProp2);
                    bool_Prop2Good = true;
                }
            }
            
            if (theTF.getId().equals("Successes1")) {
                succ1 = suspectedCount;
                if (bool_N1Good == true) {
                    prop1 = (double)StringUtilities.TextFieldToPrimitiveInt(stf_Succ1.getTextField()) / (double)n1;
                    strProp1 = String.valueOf(prop1);
                    stf_Prop1.setText(strProp1); //  Set to blank???
                    bool_Prop1Good = true;
                }
            }
            if (theTF.getId().equals("Successes2")) {
                succ2 = suspectedCount;
                if (bool_N2Good == true) {
                    prop2 = (double)StringUtilities.TextFieldToPrimitiveInt(stf_Succ2.getTextField()) / (double)n2;
                    strProp2 = String.valueOf(prop2);
                    stf_Prop2.setText(strProp2); //  Set to blank???
                    bool_Prop2Good = true;
                }
            }
        }
        return countIsGood;
    }
    
    public void ciChanged(ObservableValue<? extends String> observable,
                                                    String oldValue,
                                                    String newValue) {
        ciIndex = ciView.getSelectionModel().getSelectedIndex();
        alphaView.getSelectionModel().select(ciIndex);
        significanceLevel = theAlphaLevs[ciIndex];
        System.out.println("604, significanceLevel = " + significanceLevel);
        confidenceLevel = theCILevs[ciIndex];
        System.out.println("606, confidenceLevel = " + confidenceLevel);
    }

    public void alphaChanged(ObservableValue<? extends String> observable,
                                                    String oldValue,
                                                    String newValue) {
        alphaIndex = alphaView.getSelectionModel().getSelectedIndex();
        ciView.getSelectionModel().select(alphaIndex);
        significanceLevel = theAlphaLevs[alphaIndex];
        System.out.println("615, significanceLevel = " + significanceLevel);
        confidenceLevel = theCILevs[alphaIndex];    
        System.out.println("617, confidenceLevel = " + confidenceLevel);
    }
    
    public void printTheLot() {
        System.out.println("prop1Good = " + bool_Prop1Good);
        System.out.println("prop2Good = " + bool_Prop2Good);
        System.out.println("succ1Good  = " + bool_Succ1Good );
        System.out.println("succ2Good  = " + bool_Succ2Good );
        System.out.println("sampSize1Good = " + bool_N1Good);
        System.out.println("sampSize2Good = " + bool_N2Good);     
        
        System.out.println("\nprop1Good = " + stf_Prop1.getText());
        System.out.println("prop2Good = " + stf_Prop2.getText());
        System.out.println("succ1Good  = " + stf_Succ1.getText());
        System.out.println("succ2Good  = " + stf_Succ2.getText());
        System.out.println("sampSize1Good = " + stf_N1.getText());
        System.out.println("sampSize2Good = " + stf_N2.getText()); 
    }
    
    public boolean getDataPresent() { return dataPresent; }
    
    public double getLevelOfSignificance() { return significanceLevel; }
 
    public String getAltHypothesis() { return strAltHypChosen; }
    public double getHypothesizedDiff() { return hypothesizedDifference; }
   
    public int getN1() { return n1; }
    
    public int getN2() { return n2; }
    
    public double getP1() {return prop1; }
    public double getP2() {return prop2; }
    
    public int getX1() { return succ1; }
    
    public int getX2() { return succ2; }
    
    public double getTheNullDiff() { return daNullDiff; }
}

