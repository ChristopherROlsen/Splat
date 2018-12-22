/**************************************************
 *              QANOVA1_PostHocView               *
 *                    12/19/18                    *
 *                      18:00                     *
 *************************************************/

package ANOVA_One;

import genericClasses.JustAnAxis;
import genericClasses.QuantitativeDataVariable;
import genericClasses.UnivariateContinDataObj;
import javafx.scene.paint.Color;
import javafx.collections.FXCollections;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class QANOVA1_PostHocView extends QANOVA1_View { 
    // POJOs
    String errorString;
    
    // My classes
    // JustAnAxis xAxis;
    UnivariateContinDataObj tempUCDO;
    
    Text errorBarDescription;
   
    QANOVA1_PostHocView(QANOVA1_Model qanova1Model, QANOVA1_Dashboard qanova1Dashboard, 
                         double placeHoriz, double placeVert, 
                         double withThisWidth, double withThisHeight) {
        
        super(qanova1Model, qanova1Dashboard, "PostHoc",
              placeHoriz, placeVert,  withThisWidth, withThisHeight); 
        allTheLabels = FXCollections.observableArrayList();
        categoryLabels = FXCollections.observableArrayList();
        nCheckBoxes = 3;
        strCheckBoxDescriptions = new String[3];
        strCheckBoxDescriptions[0] = " Best Fit Line ";
        strCheckBoxDescriptions[1] = " Outliers ";
        strCheckBoxDescriptions[2] = " Influential points ";    
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.qanova1_Model = qanova1Model;
        allTheLabels = qanova1Model.getCategoryLabels();
        qanova1Canvas = new Canvas(withThisWidth, withThisHeight);
        anchorTitleInfo = new AnchorPane();
        qanova1Canvas.heightProperty().addListener(ov-> {doTheGraph();});
        qanova1Canvas.widthProperty().addListener(ov-> {doTheGraph();});
        gcQANOVA1 = qanova1Canvas.getGraphicsContext2D();
        gcQANOVA1.setFont(Font.font("Courier New",
                                    FontWeight.BOLD,
                                    FontPosture.REGULAR,
                                    12));
        
        // gcQANOVA1.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        qanova1_ContainingPane = new Pane();
        txtTitle1 = new Text(50, 25, " Post Hoc View ");
        txtTitle2 = new Text (60, 45, " Post Hoc View ");
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
    
    public void doTheGraph() {
        double downShift;
        double errorBarLength = 0.0;
        
        double text1Width = txtTitle1.getLayoutBounds().getWidth();
        double text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        
        AnchorPane.setTopAnchor(checkBoxRow, 0.01 * tempHeight);
        AnchorPane.setLeftAnchor(checkBoxRow, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(checkBoxRow, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(checkBoxRow, 0.95 * tempHeight);
       
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
        
        AnchorPane.setTopAnchor(qanova1Canvas, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(qanova1Canvas, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(qanova1Canvas, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(qanova1Canvas, 0.1 * tempHeight);
        
        for (int chex = 0; chex < nCheckBoxes; chex++) {
            AnchorPane.setLeftAnchor(qanova1_CheckBoxes[chex], (chex) * tempWidth / 5.0);
        }

        gcQANOVA1.clearRect(0, 0 , qanova1Canvas.getWidth(), qanova1Canvas.getHeight());
        for (int theBatch = 1; theBatch <= nLevels; theBatch++)
        {
            tempQDV = new QuantitativeDataVariable();
            tempQDV = qanova1_Model.getIthQDV(theBatch);
            tempUCDO = new UnivariateContinDataObj(tempQDV);
            
            double daXPosition = xAxis.getDisplayPosition(Double.parseDouble(allTheLabels.get(theBatch)));
  
            nDataPoints = tempUCDO.getLegalN();
            
            // Get the Batch label and position it at the top of the graph
            String batchLabel = tempQDV.getTheDataLabel();
            int labelSize = batchLabel.length();
            double leftShift = 5.0 * labelSize;
            double labelXPosition = daXPosition - leftShift;
            double tempYAxisRange = yAxis.getUpperBound() - yAxis.getLowerBound();
            double baseYPosition = yAxis.getUpperBound() - 0.10 * tempYAxisRange;
            boolean theBatchIsEven = (theBatch % 2 == 0);
            if (theBatchIsEven) {
                downShift = 0.0;
            }
            else {
                downShift = 0.05 * tempYAxisRange;
            }
            double labelYPosition = yAxis.getDisplayPosition(baseYPosition - downShift);
            gcQANOVA1.fillText(batchLabel, labelXPosition, labelYPosition);            
             
            double spacing = 100.;

            gcQANOVA1.setLineWidth(2);
            gcQANOVA1.setStroke(Color.GREEN);
            gcQANOVA1.setFill(Color.GREEN);
            double spaceFraction = 0.15 * spacing;  // <-------------------------------------
            // ***********************  Bar  ***********************************
            double boxTop = yAxis.getDisplayPosition(tempUCDO.getTheMean());
            double boxheight = yAxis.getDisplayPosition(0.0) - yAxis.getDisplayPosition(tempUCDO.getTheMean());
            // x, y, w, h
            gcQANOVA1.fillRect(daXPosition - 2 * spaceFraction, boxTop, 4 * spaceFraction, boxheight);
            // ***********************  Bar  ***********************************

            gcQANOVA1.setStroke(Color.BLACK);

            errorBarLength = qanova1_Model.getPostHocPlusMinus();
            boolean sampleSizesAreEqual = qanova1_Model.getAreSampleSizesEqual();
            if (sampleSizesAreEqual == true) 
                errorString = "mean +/- Honestly Significant Difference";
            else
                errorString = "mean +/- Tukey-Cramer Significant Difference";  

            errorBarDescription = new Text(0, 0, errorString);
  
            //  Factors below (15 & 10) are functions of the text size
            errorBarDescription.setX(anchorTitleInfo.getWidth()/2. - 8 * errorString.length()/2);
            // Horizontal lines
            double topOfBar = yAxis.getDisplayPosition(tempUCDO.getTheMean() + errorBarLength);
            double bottomOfBar = yAxis.getDisplayPosition(tempUCDO.getTheMean() - errorBarLength);
            gcQANOVA1.strokeLine(daXPosition - spaceFraction, topOfBar, daXPosition + spaceFraction, topOfBar);
            gcQANOVA1.strokeLine(daXPosition - spaceFraction, bottomOfBar, daXPosition + spaceFraction, bottomOfBar);
            // ErrorBar
            gcQANOVA1.strokeLine(daXPosition, topOfBar, daXPosition, bottomOfBar);  //  Low whisker
        }   //  Loop through batches   
    }
}
