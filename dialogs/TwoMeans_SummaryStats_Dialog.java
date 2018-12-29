/************************************************************
 *                 TwoMeans_SummaryStats_Dialog             *
 *                          12/22/18                        *
 *                            12:00                         *
 ***********************************************************/
package dialogs;

import utilityClasses.StringUtilities;
import utilityClasses.DataUtilities;
import java.util.ArrayList;
import java.util.Optional;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import smarttextfield.*;
import genericClasses.*;
import javafx.stage.WindowEvent;

public class TwoMeans_SummaryStats_Dialog extends Stage { 
    
    // POJOs
    boolean okToContinue, runAnalysis, ok, caughtYa, dataPresent;
    boolean bool_Mean1Good, bool_Mean2Good, bool_Sigma1Good, bool_Sigma2Good, 
            bool_N1Good, bool_N2Good, bool_AlphaGood;
    boolean allFieldsGood;
    
    int  succ2, n1, n2, alphaIndex, ciIndex;
    Integer suspectedCount;
    double sigma1, sigma2, alphaLevel, ciLevel, daNullDiff;
    double hypothesizedDifference, nullDiffRequested;
    double mean1, mean2;
    double[] theAlphaLevs, theCILevs; 
    
    String strHypNE, strHypLT, strHypGT, strHypNull, strNullAndAlt, strAltHypChosen,  
           str_Group1_Title, str_Group1_SumInfo, str_OROne, str_Group1_N,
           str_Group2_Title, str_Group2_SumInfo, str_ORTwo, str_Group2_N,
           resultAsString, strHypChosen;
    
    String strMean1, strMean2, strSigma1, strSigma2, strN1, strN2;
    final String toBlank = "";
    
    final String strBadDouble = "Ok, so here's the deal.  There are numbers, and there are other"
                        + "\nthan numbers, like words and punctuation.  What you must enter in"
                        + "\nthis field are numbers, specifically numbers of the Arabic persuation."
                        + "\nThe Decline and Fall of the Roman Empire included the Decline and"
                        + "\nFall of Roman Numerals.  Now, let's try this number thing again...";
    
    final String wtfString = "What!?!?  My hypothesized null difference of zero is not good"
                                    + "\nenough for you? It is pretty unusual that a non-zero difference"
                                    + "\nwould be hypothesized, but I'm willing to believe you know what"
                                    + "\nyou're doing. No skin off MY nose...";

    ObservableList<String> ciLevels, alphaLevels;
    ListView<String> ciView, alphaView;
    
    // My classes
    SmartTextField stf_Mean1, stf_Sigma1, stf_N1, stf_Mean2, stf_Sigma2, stf_N2;
    SmartTextFieldHandler meansHandler;
    ArrayList<SmartTextField> al_stfForEntry; 
    
    // JavaFX POJOs
    Button changeNull, okButton, cancelButton, resetButton;;
    RadioButton hypNE, hypLT, hypGT, hypNull;

    Label lblNullAndAlt, lbl_Title, lblSigLevel, ciLabel, alphaLabel, lbl_nullDiffInfo;
    HBox middlePanel, bottomPanel, hBox_GPOne_SuccessRow, hBox_Group2_SuccessRow,
         alphaAndCI, nullDiffInfo, hBoxCurrDiff;

    VBox root, nullsPanel, numValsPanel, group_1, group_Prop_2,
         ciBox, alphaBox, infChoicesPanel; 
    
    final Text currNullDiff = new Text("Current null diff: (\u03BC\u2081 - \u03BC\u2082) = ");
    TextField tf_HypDiff;
    TextInputDialog txtDialog;
  
    Scene scene;
    Separator sep_NullsFromInf, sep_InfFromNumbers, sep_MiddleAndBottom,
              sep_Prop1_and_Prop2, sep_Alpha, sep;  
    
    Text txt_Group1_Title, txt_Group1_SumInfo, txt_OROne, txt_Group1_N,
         txt_Group2_Title, txt_Group2_SumInfo, txt_ORTwo, txt_Group2_N;
    



    public TwoMeans_SummaryStats_Dialog() {
        theAlphaLevs = new double[] { 0.10, 0.05, 0.01};
        theCILevs = new double[] {0.90, 0.95, 0.99};
        // alphaLevel = 0.05;   //  Initial value
        // ciLevel = 0.95;     //  Initial value
        sep = new Separator();
        sep.setOrientation(Orientation.VERTICAL);

        root = new VBox();
        dataPresent = false;
        root.setAlignment(Pos.CENTER);
        
        meansHandler = new SmartTextFieldHandler();
        al_stfForEntry = new ArrayList<>();
        meansHandler.setHandlerArrayList(al_stfForEntry);
        meansHandler.setHandlerTransversal(true);
        meansHandler.setHandlerTransversalIndex(0);
        
        
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
        setTitle("Inference for a difference in means");
        setScene(scene);
        showAndWait();
    }  

    
private void makeNullsPanel() {
        
        hypothesizedDifference = 0.0;
        nullDiffRequested = 0.0;
        strHypChosen = "NotEqual";
        changeNull = new Button("Change null difference");
        strNullAndAlt = "  Choose from the null and \n  alternate hypothesis pairs \n  listed below:";
        lblNullAndAlt = new Label(strNullAndAlt);
        
        strHypNull = "\u03BC\u2081 - \u03BC\u2082 = 0";
        strHypNE = "\u03BC\u2081 - \u03BC\u2082 \u2260 0";
        strHypLT = "\u03BC\u2081 - \u03BC\u2082 < 0";
        strHypGT = "\u03BC\u2081 - \u03BC\u2082 > 0";
        
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
        
        // hypothesizedDifference = 0.0;
        // daNullDiff = 0.0; 
        tf_HypDiff = new TextField("0.0");
        tf_HypDiff.setMinWidth(75);
        tf_HypDiff.setMaxWidth(75);
        hBoxCurrDiff = new HBox();
        hBoxCurrDiff.getChildren().addAll(currNullDiff, tf_HypDiff);
        
        nullsPanel = new VBox();
        
        nullsPanel.getChildren()
                 .addAll(lblNullAndAlt, hypNE, hypLT, hypGT, 
                         changeNull, hBoxCurrDiff);
        
        hypNE.setOnAction(e->{
            RadioButton tb = ((RadioButton) e.getTarget());
            String daID = tb.getId();
            Boolean checkValue = tb.selectedProperty().getValue();
            System.out.println("hypNE chosen");
            hypNE.setSelected(true);
            hypLT.setSelected(false);
            hypGT.setSelected(false);
            strHypChosen = "NotEqual";

        });
            
        hypLT.setOnAction(e->{
            RadioButton tb = ((RadioButton) e.getTarget());

            String daID = tb.getId();
            Boolean checkValue = tb.selectedProperty().getValue();
            System.out.println("hypLT chosen");
            hypNE.setSelected(false);
            hypLT.setSelected(true);
            hypGT.setSelected(false);
            strHypChosen = "LessThan";

        });
            
        hypGT.setOnAction(e->{
            RadioButton tb = ((RadioButton) e.getTarget());

            String daID = tb.getId();
            Boolean checkValue = tb.selectedProperty().getValue();
            System.out.println("hypGT chosen");
            hypNE.setSelected(false);
            hypLT.setSelected(false);
            hypGT.setSelected(true);
            strHypChosen = "GreaterThan";
        });
            
        changeNull.setOnAction((ActionEvent event) -> {
            okToContinue = false;
            while (okToContinue == false) {
                okToContinue = true;
                txtDialog = new TextInputDialog("");
                txtDialog.setTitle("Null hypothesis change");
                
                /*
                txtDialog.setHeaderText("What!?!?  My hypothesized null difference of zero is not good"
                                    + "\nenough for you? It is pretty unusual that a non-zero difference"
                                    + "\nwould be hypothesized, but I'm willing to believe you know what"
                                    + "\nyou're doing. No skin off MY nose...");
                */
                txtDialog.setHeaderText(wtfString);
                
                txtDialog.setContentText("What difference between means would you like to test? ");                     

                Optional<String> result = txtDialog.showAndWait();
                if (result.isPresent()) {
                    resultAsString = result.get();                        
                }
                if (result.isPresent() == true) {
                    okToContinue = true;
                    try {
                        nullDiffRequested = Double.valueOf(resultAsString);
                    }
                    catch (NumberFormatException ex ){ 
                        okToContinue = false;
                        Alert badValue = new Alert(Alert.AlertType.ERROR);
                        badValue.setTitle("Warning! Must be a real number");
                        badValue.setHeaderText("You have entered something other than a number.");
                        badValue.setContentText("Ok, so here's the deal.  There are numbers, and there are other"
                                    + "\nthan numbers, like words and punctuation.  What you must enter in"
                                    + "\nthis field are numbers, specifically numbers of the Arabic persuation."
                                    + "\nThe Decline and Fall of the Roman Empire included the Decline and"
                                    + "\nFall of Roman Numerals.  Now, let's try this number thing again...");
                        badValue.showAndWait();
                        txtDialog.setContentText("");
                        okToContinue = false;
                        nullDiffRequested = 0.0;
                    }
                }
                else {
                    nullDiffRequested = 0.0;    // Null returns to 0.0 if Cancel
                }
            }
            hypothesizedDifference = nullDiffRequested;
            tf_HypDiff.setText(String.valueOf(hypothesizedDifference));
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
        
        str_OROne = "  Mean #1         StDev #1";
        txt_OROne = new Text(str_OROne);    
        
        hBox_GPOne_SuccessRow = new HBox();
        
        stf_Mean1 = new SmartTextField(meansHandler, 6, 1);
        stf_Mean1.getTextField().setPrefColumnCount(12);
        stf_Mean1.getTextField().setMaxWidth(65);
        stf_Mean1.getTextField().setPadding(new Insets(5, 10, 5, 5));
        stf_Mean1.getTextField().setText(toBlank);
        stf_Mean1.getTextField().setId("Prop1");
        stf_Mean1.setSmartTextField_MB_NONBLANK(false);
        al_stfForEntry.add(stf_Mean1);
        
        stf_Mean1.getTextField().setOnAction(e -> {            
            // bool_Mean1Good = StringUtilities.Check_String_For_Double(stf_Mean1.getTextField().getText());
            bool_Mean1Good = DataUtilities.textFieldHasDouble(stf_Mean1.getTextField());
            if (bool_Mean1Good == true) {
                strMean1 = stf_Mean1.getText();
                mean1 = Double.valueOf(strMean1);
                stf_Mean1.setText(String.valueOf(mean1));
            }
        });
       
        stf_Sigma1 = new SmartTextField(meansHandler, 0, 2);
        stf_Sigma1.getTextField().setPrefColumnCount(12);
        stf_Sigma1.getTextField().setMaxWidth(65);
        stf_Sigma1.getTextField().setText(toBlank); 
        stf_Sigma1.getTextField().setId("Successes1");
        stf_Sigma1.setSmartTextField_MB_NONBLANK(false);
        al_stfForEntry.add(stf_Sigma1);
        stf_Sigma1.getTextField().setOnAction(e -> {
            bool_Sigma1Good = StringUtilities.check4PosReal(stf_Sigma1.getTextField().getText());
            if (bool_Sigma1Good == true) {
                sigma1 = Double.valueOf(stf_Sigma1.getText());
                System.out.println("329 twosumStats, sigma1 = " + sigma1);
                strSigma1 = String.valueOf(sigma1);
                stf_Sigma1.setText(strSigma1);
                bool_Sigma1Good = true;
            }
        });
        hBox_GPOne_SuccessRow.setAlignment(Pos.CENTER);
        hBox_GPOne_SuccessRow.getChildren()
                             .addAll(stf_Mean1.getTextField(),
                                     stf_Sigma1.getTextField());
        hBox_GPOne_SuccessRow.setSpacing(25);

        str_Group1_N = "   Group / Sample Size #1";
        
        txt_Group1_N = new Text(str_Group1_N);   
        
        stf_N1 = new SmartTextField(meansHandler, 1, 3);
        stf_N1.getTextField().setPrefColumnCount(12);
        stf_N1.getTextField().setMaxWidth(65);
        stf_N1.getTextField().setPadding(new Insets(5, 10, 5, 5));
        stf_N1.getTextField().setText(toBlank);
        stf_N1.getTextField().setId("SampleSize1");
        al_stfForEntry.add(stf_N1);

        // ??????  Why am I setting the text again?  Vestigial from prop?
        stf_N1.getTextField().setOnAction(e -> {
            bool_N1Good = DataUtilities.textFieldHasPositiveInteger(stf_N1.getTextField());
            if (bool_N1Good == true) {
                n1 = Integer.parseInt(stf_N1.getText());
                strN1 = String.valueOf(stf_N1.getText());
                stf_N1.setText(strN1);
                bool_N1Good = true;
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
        
        str_ORTwo = "  Mean #2         StDev #2";
        txt_ORTwo = new Text(str_ORTwo);

        hBox_Group2_SuccessRow = new HBox();
        
        stf_Mean2 = new SmartTextField(meansHandler, 2, 4);
        stf_Mean2.getTextField().setPrefColumnCount(12);
        stf_Mean2.getTextField().setMaxWidth(65);
        stf_Mean2.getTextField().setPadding(new Insets(5, 10, 5, 5));
        stf_Mean2.getTextField().setText(toBlank);
        stf_Mean2.getTextField().setId("Prop2"); 
        stf_Mean2.setSmartTextField_MB_NONBLANK(false);
        al_stfForEntry.add(stf_Mean2);
        
        stf_Mean2.getTextField().setOnAction(e -> {
            bool_Mean2Good = DataUtilities.textFieldHasDouble(stf_Mean2.getTextField());
            if (bool_Mean2Good == true) {
                strMean2 = stf_Mean2.getText();
                mean2 = Double.valueOf(strMean2);
                stf_Mean2.setText(String.valueOf(mean2));
            }
        });

        stf_Sigma2 = new SmartTextField(meansHandler, 3, 5);
        stf_Sigma2.getTextField().setPrefColumnCount(8);
        stf_Sigma2.getTextField().setMaxWidth(50);
        stf_Sigma2.getTextField().setText(toBlank);    
        stf_Sigma2.getTextField().setId("Successes2");
        stf_Sigma2.setSmartTextField_MB_NONBLANK(false);
        al_stfForEntry.add(stf_Sigma2);
        
        stf_Sigma2.getTextField().setOnAction(e -> {
            bool_Sigma2Good = StringUtilities.check4PosReal(stf_Sigma1.getTextField().getText());
            if (bool_Sigma2Good == true) {
                sigma2 = Double.valueOf(stf_Sigma2.getText());
                System.out.println("415 twosumStats, sigma1 = " + sigma1);
                strSigma2 = String.valueOf(sigma2);
                stf_Sigma2.setText(strSigma2);
                bool_Sigma2Good = true;
            }
        });
        hBox_Group2_SuccessRow.setAlignment(Pos.CENTER);
        hBox_Group2_SuccessRow.getChildren()
                             .addAll(stf_Mean2.getTextField(),
                                     txt_ORTwo,
                                     stf_Sigma2.getTextField());
        hBox_Group2_SuccessRow.setSpacing(25);
        str_Group2_N = "   Group / Sample Size #2";
        txt_Group2_N = new Text(str_Group2_N);  
        
        stf_N2 = new SmartTextField(meansHandler, 4, 0);
        stf_N2.getTextField().setPrefColumnCount(8);
        stf_N2.getTextField().setMaxWidth(50);
        stf_N2.getTextField().setPadding(new Insets(5, 10, 5, 5));
        stf_N2.getTextField().setText(toBlank);    
        stf_N2.getTextField().setId("SampleSize2");
        // stf_N2.setSmartTextField_MB_NONBLANK(false);
        al_stfForEntry.add(stf_N2);
        
        // ??????  Why am I setting the text again?  Vestigial from prop?
        stf_N2.getTextField().setOnAction(e -> {
            bool_N2Good = DataUtilities.textFieldHasPositiveInteger(stf_N2.getTextField());
            if (bool_N2Good == true) {
                n2 = Integer.parseInt(stf_N2.getText());
                strN2 = String.valueOf(stf_N2.getText());
                stf_N2.setText(strN2);
                bool_N2Good = true;
            }
        });    
        
        meansHandler.setFocusRequest(0);
        
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
            
        bool_Mean1Good = DataUtilities.textFieldHasDouble(stf_Mean1.getTextField());
        bool_Mean2Good = DataUtilities.textFieldHasDouble(stf_Mean2.getTextField());
        bool_Sigma1Good = DataUtilities.textFieldHasPositiveDouble(stf_Sigma1.getTextField());
        bool_Sigma2Good = DataUtilities.textFieldHasPositiveDouble(stf_Sigma2.getTextField());
        bool_N1Good = DataUtilities.textFieldHasPositiveInteger(stf_N1.getTextField());
        bool_N2Good = DataUtilities.textFieldHasPositiveInteger(stf_N2.getTextField());

        allFieldsGood = bool_Mean1Good && bool_Sigma1Good  
                        && bool_Mean2Good && bool_Sigma2Good 
                        && bool_N1Good && bool_N2Good;
        System.out.println("531 2muSumDiag, allFieldsGood = " + allFieldsGood);
        
        printTheLot();

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
            stf_Mean1.setText(toBlank); 
            stf_Mean2.setText(toBlank);
            stf_Sigma1.setText(toBlank); 
            stf_Sigma2.setText(toBlank);
            stf_N1.setText(toBlank);
            stf_N2.setText(toBlank);
            
            bool_Mean1Good = false; 
            bool_Mean2Good = false; 
            bool_Sigma1Good = false; 
            bool_Sigma2Good = false; 
            bool_N1Good = false; 
            bool_N2Good = false;
            
        });
        
        bottomPanel.getChildren().addAll(okButton, cancelButton, resetButton);
    }
    
    public void ciChanged(ObservableValue<? extends String> observable,
                                                    String oldValue,
                                                    String newValue) {
        ciIndex = ciView.getSelectionModel().getSelectedIndex();
        alphaView.getSelectionModel().select(ciIndex);
        alphaLevel = theAlphaLevs[ciIndex];
        System.out.println("604, significanceLevel = " + alphaLevel);
        ciLevel = theCILevs[ciIndex];
        System.out.println("606, confidenceLevel = " + ciLevel);
    }

    public void alphaChanged(ObservableValue<? extends String> observable,
                                                    String oldValue,
                                                    String newValue) {
        alphaIndex = alphaView.getSelectionModel().getSelectedIndex();
        ciView.getSelectionModel().select(alphaIndex);
        alphaLevel = theAlphaLevs[alphaIndex];
        System.out.println("615, significanceLevel = " + alphaLevel);
        ciLevel = theCILevs[alphaIndex];    
        System.out.println("617, confidenceLevel = " + ciLevel);
    }
    
    public void printTheLot() {
        System.out.println("Mean1Good = " + bool_Mean1Good);
        System.out.println("Mean2Good = " + bool_Mean2Good);
        System.out.println("Sigma1Good  = " + bool_Sigma1Good );
        System.out.println("Sigma2Good  = " + bool_Sigma2Good );
        System.out.println("N1Good = " + bool_N1Good);
        System.out.println("N2Good = " + bool_N2Good);     
        
        System.out.println("\nmean1 = " + stf_Mean1.getText());
        System.out.println("mean2 = " + stf_Mean2.getText());
        System.out.println("sigma1  = " + stf_Sigma1.getText());
        System.out.println("sigma2  = " + stf_Sigma2.getText());
        System.out.println("n1 = " + stf_N1.getText());
        System.out.println("n2 = " + stf_N2.getText()); 
    }
    
    public double getAlpha() { 
        System.out.println("283 ind_t_dial, getting alpha = " + alphaLevel);
        return alphaLevel; 
    }
        
    public String getHypotheses() { return strHypChosen; }
    public boolean getDataPresent() { return dataPresent; }
    
    public double getLevelOfSignificance() { return alphaLevel; }
 
    public String getAltHypothesis() { return strAltHypChosen; }
    public double getHypothesizedDiff() { return hypothesizedDifference; }
   
    public int getN1() { return n1; }
    public int getN2() { return n2; }
    
    public double getStDev1() {return sigma1; }
    public double getStDev2() {return sigma2; }
    
    public double getXBar1() { return mean1; }
    public double getXBar2() { return mean2; }
    
    public double getTheNullDiff() { return daNullDiff; }
}

