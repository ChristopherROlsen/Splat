/**************************************************
 *            ANOVA1_MeanAndErrorView             *
 *                    12/24/18                    *
 *                      06:00                     *
 *************************************************/

package ANOVA_One;

import genericClasses.DragableAnchorPane;
import dataObjects.QuantitativeDataVariable;
import dataObjects.UnivariateContinDataObj;
import javafx.collections.FXCollections;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class ANOVA1_MeanAndErrorView extends ANOVA1_View { 
    
    // POJOs
    boolean[] radioButtonSettings;
    int nRadioButtons, errBarChoice;
    String errorString;
    String[] strRadioButtonDescriptions;
    
    // My classes
    UnivariateContinDataObj tempUCDO;
    
    // POJOs / FX
    AnchorPane radioButtonRow;
    RadioButton[] anova1_RadioButtons;

    ANOVA1_MeanAndErrorView(ANOVA1_Model anova1Model, ANOVA1_Dashboard anova1Dashboard, 
                         double placeHoriz, double placeVert, 
                         double withThisWidth, double withThisHeight) {
        
        super(anova1Model, anova1Dashboard, "MeanAndError",
              placeHoriz, placeVert,  withThisWidth, withThisHeight); 
        
        nRadioButtons = 4;
        strRadioButtonDescriptions = new String[4];
        strRadioButtonDescriptions[0] = " Standard Error ";
        strRadioButtonDescriptions[1] = " Margin of Error ";
        strRadioButtonDescriptions[2] = " Standard Deviation ";   
        strRadioButtonDescriptions[3] = " 95% Conf Int ";
        allTheLabels = FXCollections.observableArrayList();
        categoryLabels = FXCollections.observableArrayList();
        
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.anova1_Model = anova1Model;
        allTheLabels = anova1Model.getCategoryLabels();
        anova1Canvas = new Canvas(withThisWidth, withThisHeight);
        anchorTitleInfo = new AnchorPane();
        anova1Canvas.heightProperty().addListener(ov-> {doTheGraph();});
        anova1Canvas.widthProperty().addListener(ov-> {doTheGraph();});
        gcANOVA1 = anova1Canvas.getGraphicsContext2D();
        gcANOVA1.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        anova1_ContainingPane = new Pane();
        txtTitle1 = new Text(50, 25, " Mean +/- Error bars ");
        explanVar = anova1Dashboard.getExplanVar();
        responseVar = anova1Dashboard.getResponseVar();
        System.out.println("69 ANOVA_Mean&Error, explan/resp = " + explanVar + " / " + responseVar);
        String strForTitle2 = responseVar + " vs. " + explanVar;
        txtTitle2 = new Text (60, 45, strForTitle2);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
    
    public void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        anova1Canvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        anova1Canvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));

        for (int iBtns = 0; iBtns < nRadioButtons; iBtns++) {
            
            anova1_RadioButtons[iBtns].translateXProperty()
                                        .bind(anova1Canvas.widthProperty()
                                        .divide(250.0)
                                        .multiply(65. * iBtns)
                                        .subtract(225.0));
        }
     
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        String graphsCSS = getClass().getResource("/css/Graphs.css").toExternalForm();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    

        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(radioButtonRow, txtTitle1, txtTitle2, xAxis, yAxis, anova1Canvas);
        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    public void makeTheCheckBoxes() { makeTheRadioButtons(); }
    
    public void makeTheRadioButtons() { 
        // Determine which graphs are initially shown
        radioButtonSettings = new boolean[nRadioButtons];
        for (int ithBox = 0; ithBox < nRadioButtons; ithBox++) {
            radioButtonSettings[ithBox] = false;
        }
        
        radioButtonRow = new AnchorPane();
        radioButtonRow.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        anova1_RadioButtons = new RadioButton[nRadioButtons];
        
        ToggleGroup group = new ToggleGroup();

        for (int i = 0; i < nRadioButtons; i++) {
            anova1_RadioButtons[i] = new RadioButton(strRadioButtonDescriptions[i]);
            group.getToggles().add(anova1_RadioButtons[i]);
            anova1_RadioButtons[i].setMaxWidth(Double.MAX_VALUE);
            anova1_RadioButtons[i].setId(strRadioButtonDescriptions[i]);
            anova1_RadioButtons[i].setSelected(radioButtonSettings[i]);
            anova1_RadioButtons[i].setTextFill(Color.BLUE);
            anova1_RadioButtons[i].setStyle(
                                "-fx-font-size: 14;" +
                                "-fx-border-insets: -5; " + 
                                "-fx-border-radius: 5;" +
                                "-fx-border-style: dotted;" +
                                "-fx-border-width: 0;" );

            anova1_RadioButtons[i].setOnAction(e->{
                RadioButton tb = ((RadioButton) e.getTarget());
                
                String daID = tb.getId();
                Boolean checkValue = tb.selectedProperty().getValue();
                
                for (int ithID = 0; ithID < nRadioButtons; ithID++) {
                    if (daID.equals(strRadioButtonDescriptions[ithID])) {
                        radioButtonSettings[ithID] = (checkValue == true);
                        errBarChoice = ithID;
                        doTheGraph();
                    }
                }

            }); //  end setOnAction
        }  
        anova1_RadioButtons[0].setSelected(true);
        radioButtonRow.getChildren().addAll(anova1_RadioButtons);
        }

    public void doTheGraph() {
        double text1Width = txtTitle1.getLayoutBounds().getWidth();
        double text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        
        AnchorPane.setTopAnchor(radioButtonRow, 0.01 * tempHeight);
        AnchorPane.setLeftAnchor(radioButtonRow, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(radioButtonRow, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(radioButtonRow, 0.95 * tempHeight);
       
        AnchorPane.setTopAnchor(txtTitle1, 0.06 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle1, 0.10 * tempHeight);
                
        AnchorPane.setTopAnchor(txtTitle2, 0.11 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle2, txt2Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle2, txt2Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle2, 0.2 * tempHeight);
        
        AnchorPane.setTopAnchor(xAxis, 0.9 * tempHeight);
        AnchorPane.setLeftAnchor(xAxis, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(xAxis, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(xAxis, 0.0 * tempHeight);
        
        AnchorPane.setTopAnchor(yAxis, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(yAxis, 0.0 * tempWidth);
        AnchorPane.setRightAnchor(yAxis, 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(yAxis, 0.1 * tempHeight);
        
        AnchorPane.setTopAnchor(anova1Canvas, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(anova1Canvas, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(anova1Canvas, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(anova1Canvas, 0.1 * tempHeight);
        
        for (int chex = 0; chex < nCheckBoxes; chex++) {
            AnchorPane.setLeftAnchor(anova1_CheckBoxes[chex], (chex) * tempWidth / 5.0);
        }

        gcANOVA1.clearRect(0, 0 , anova1Canvas.getWidth(), anova1Canvas.getHeight());
        for (int theBatch = 1; theBatch <= nLevels; theBatch++)
        {
            tempQDV = new QuantitativeDataVariable();
            tempQDV = anova1_Model.getIthQDV(theBatch);
            tempUCDO = new UnivariateContinDataObj(tempQDV);
            double daXPosition = xAxis.getDisplayPosition(categoryLabels.get(theBatch - 1));
  
            nDataPoints = tempUCDO.getLegalN();
            
            double spacing = 100.;

            gcANOVA1.setLineWidth(2);
            gcANOVA1.setStroke(Color.GREEN);
            gcANOVA1.setFill(Color.GREEN);
            double spaceFraction = 0.15 * spacing;  // <-------------------------------------
            // ***********************  Bar  ***********************************
            double boxTop = yAxis.getDisplayPosition(tempUCDO.getTheMean());
            double boxheight = yAxis.getDisplayPosition(0.0) - yAxis.getDisplayPosition(tempUCDO.getTheMean());
            // x, y, w, h
            gcANOVA1.fillRect(daXPosition - 2 * spaceFraction, boxTop, 4 * spaceFraction, boxheight);
            // ***********************  Bar  ***********************************

            gcANOVA1.setStroke(Color.BLACK);

            switch (errBarChoice) {
                // Standard Error
                case 0:
                    errorBarLength = tempUCDO.getStandErrMean();
                    errorString = "Mean +/- Standard Error";                    
                break;
                
                // Margin of Error
                case 1:
                    errorBarLength = tempUCDO.getTheMarginOfErr(0.95);
                    errorString = "Mean +/- Margin of Error";                    
                break;
                
                // Standard deviation
                case 2:
                    errorBarLength = tempUCDO.getTheStandDev();
                    errorString = "Mean +/- Standard Deviation";                   
                break;
                
                // 95% confidence interval
                case 3:
                    errorBarLength = tempUCDO.getTheMarginOfErr(0.95);
                    errorString = "Mean +/- 95% Confidence Interval";                    
                break;
                
                default:
                    System.out.println("Ack!  Switch failure at StandErrView 256");
                    System.exit(1);
                
            }

            errorBarDescription = new Text(0, 0, errorString);

            //  Factors below (15 & 10) are functions of the text size
            errorBarDescription.setX(anchorTitleInfo.getWidth()/2. - 8 * errorString.length()/2);
            // Horizontal lines
            double topOfBar = yAxis.getDisplayPosition(tempUCDO.getTheMean() + errorBarLength);
            double bottomOfBar = yAxis.getDisplayPosition(tempUCDO.getTheMean() - errorBarLength);
            gcANOVA1.strokeLine(daXPosition - spaceFraction, topOfBar, daXPosition + spaceFraction, topOfBar);
            gcANOVA1.strokeLine(daXPosition - spaceFraction, bottomOfBar, daXPosition + spaceFraction, bottomOfBar);
            // ErrorBar
            gcANOVA1.strokeLine(daXPosition, topOfBar, daXPosition, bottomOfBar);  //  Low whisker
        }   //  Loop through batches   
    }    
}
