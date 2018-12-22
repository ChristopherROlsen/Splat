/************************************************************
 *                      Single_t_Dialog                     *
 *                          12/08/18                        *
 *                            12:00                         *
 ***********************************************************/
package dialogs;

import java.util.Optional;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import splat.Splat_DataManager;

public class Single_t_Dialog extends One_Variable_Dialog{ 
    
    int alphaIndex, ciIndex;
    boolean okToContinue;
    double hypothesizedMean, currentSigLevel, currentConfLevel, 
           daNullMean, alpha;
    Double daNewNullMean;  
    
    double[] alphaLevels, confLevels; 
    
    Button changeNull;
    RadioButton hypNE, hypLT, hypGT, hypNull;
    String strHypNE, strHypLT, strHypGT, strHypNull, strNullAndAlt, strHypChosen;
    String resultAsString;
    Label lblNullAndAlt, lblTitle, lblSigLevel, ciLabel, alphaLabel, 
          lblNullMean;
    
    Separator sep;
    final Text currNullMean = new Text("Current null diff: (\u03BC\u2081 - \u03BC\u2082) = ");
    TextField hypDiff;
    TextInputDialog txtDialog;
    HBox alphaAndCI, nullMeanInfo, hBoxCurrDiff;
    VBox ciBox, alphaBox;
    
    ObservableList<String> list_ConfLevels, list_AlphaLevels;
    
    ListView<String> list_CIViews, list_AlphaViews; 
    
    public Single_t_Dialog(Splat_DataManager myData, String variableType) {
        super(myData, "Quantitative");
        
        minSampleSize = 3;
        lbl_Title.setText("Independent t inference");
        lblFirstVar.setText("Variable #1:");
        alphaLevels = new double[] { 0.10, 0.05, 0.01};
        confLevels = new double[] {0.90, 0.95, 0.99};
        makeHypotheses();
        makeAlphaAndCIPanel();
        rightPanel.getChildren().add(alphaAndCI);
        setTitle("Independent t inference");
        showAndWait();
    }  

 
    protected void defineTheCheckBoxes() {
        // Check box strings must match the order of dashboard strings
        // Perhaps pass them to dashboard in future?
        nCheckBoxes = 4;
        String[] chBoxStrings = { " Best fit line ", " Residuals ",
                                         " RegrReport ", " DiagReport "}; 
        dashBoardOptions = new CheckBox[nCheckBoxes];
        for (int ithCBx = 0; ithCBx < nCheckBoxes; ithCBx++) {
            dashBoardOptions[ithCBx] = new CheckBox(chBoxStrings[ithCBx]);
        }
    } 

    
 public void makeHypotheses() {
        
        hypothesizedMean = 0.0;
        daNewNullMean = 0.0;
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
        
        hypothesizedMean = 0.0;
        daNullMean = 0.0; 
        hypDiff = new TextField("0.0");
        hypDiff.setMinWidth(75);
        hypDiff.setMaxWidth(75);
        hBoxCurrDiff = new HBox();
        hBoxCurrDiff.getChildren().addAll(currNullMean, hypDiff);
        
        leftPanel.getChildren()
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
                txtDialog.setHeaderText("What!?!?  My hypothesized null difference of zero is not good"
                                    + "\nenough for you? It is pretty unusual that a non-zero difference"
                                    + "\nwould be hypothesized, but I'm willing to believe you know what"
                                    + "\nyou're doing. No skin off MY nose...");
                txtDialog.setContentText("What difference between means would you like to test? ");                     

                Optional<String> result = txtDialog.showAndWait();
                if (result.isPresent()) {
                    resultAsString = result.get();                        
                }
                if (result.isPresent() == true) {
                    okToContinue = true;
                    try {
                        daNewNullMean = Double.valueOf(resultAsString);
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
                        daNewNullMean = 0.0;
                    }
                }
                else {
                    daNewNullMean = 0.0;    // Null returns to 0.0 if Cancel
                }
            }
            hypothesizedMean = daNewNullMean;
            hypDiff.setText(String.valueOf(hypothesizedMean));
            // System.out.println("daNewNullMean = " + hypothesizedMean);
        });
    }
 
 private void makeAlphaAndCIPanel() {
        ciLabel = new Label("   Select conf level");
        ciLabel.setMaxWidth(130);
        ciLabel.setMinWidth(130);
        list_ConfLevels = FXCollections.<String>observableArrayList("          90%", "          95%", "          99%");
        list_CIViews = new ListView<>(list_ConfLevels);
        list_CIViews.setOrientation(Orientation.VERTICAL);
        list_CIViews.setPrefSize(120, 100);
        
        list_CIViews.getSelectionModel()
              .selectedItemProperty()
              .addListener((new ChangeListener<String>() {
                  public void changed(ObservableValue<? extends String> ov,
                     final String oldvalue, final String newvalue) {
                       ciChanged(ov, oldvalue, newvalue);
                  }
              }));
 
        alphaLabel = new Label("   Select alpha level");
        alphaLabel.setMaxWidth(130);
        alphaLabel.setMinWidth(130);
        list_AlphaLevels = FXCollections.<String>observableArrayList("          0.10", "          0.05", "          0.01");
        list_AlphaViews = new ListView<>(list_AlphaLevels);
        list_AlphaViews.setOrientation(Orientation.VERTICAL);
        list_AlphaViews.setPrefSize(120, 100);
        
        list_AlphaViews.getSelectionModel()
              .selectedItemProperty()
              .addListener((new ChangeListener<String>() {
                  public void changed(ObservableValue<? extends String> ov,
                     final String oldvalue, final String newvalue) {
                       alphaChanged(ov, oldvalue, newvalue);
                  }
              }));
        
        list_AlphaViews.getSelectionModel().select(1);    //  Set at .05
        list_CIViews.getSelectionModel().select(1);   //  Set at 95%
        ciBox = new VBox();
        
        ciBox.getChildren().addAll(ciLabel, list_CIViews);
        alphaBox = new VBox();
        alphaBox.getChildren().addAll(alphaLabel, list_AlphaViews);
        sep = new Separator();
        sep.setOrientation(Orientation.VERTICAL);
        alphaAndCI = new HBox(10);

        //alphaAndCI.setPadding(new Insets(5, 5, 5, 5));
        alphaAndCI.getChildren().addAll(alphaBox, sep, ciBox);  

 }
 
     public void ciChanged(ObservableValue<? extends String> observable,
                                                    String oldValue,
                                                    String newValue) {
        ciIndex = list_CIViews.getSelectionModel().getSelectedIndex();
        alpha = alphaLevels[ciIndex];
        list_AlphaViews.getSelectionModel().select(ciIndex);
        currentSigLevel = alphaLevels[ciIndex];
        currentConfLevel = confLevels[ciIndex];
    }

    public void alphaChanged(ObservableValue<? extends String> observable,
                                                    String oldValue,
                                                    String newValue) {
        alphaIndex = list_AlphaViews.getSelectionModel().getSelectedIndex();
        alpha = alphaLevels[alphaIndex];
        list_CIViews.getSelectionModel().select(alphaIndex);
        currentSigLevel = alphaLevels[alphaIndex];
        currentConfLevel = confLevels[alphaIndex];        
    }
 
    public double getAlpha() { 
        System.out.println("283 ind_t_dial, getting alpha = " + alpha);
        return alpha; }
    
    public String getHypotheses() { return strHypChosen; }
    public double getHypothesizedMean() { return hypothesizedMean; }
}

