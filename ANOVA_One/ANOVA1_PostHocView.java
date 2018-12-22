/**************************************************
 *              ANOVA1_PostHocView                *
 *                    05/15/18                    *
 *                      12:00                     *
 *************************************************/

package ANOVA_One;

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

public class ANOVA1_PostHocView extends ANOVA1_View { 
    // POJOs
    String errorString;
    
    // My classes
    UnivariateContinDataObj tempUCDO;
   
    ANOVA1_PostHocView(ANOVA1_Model anova1Model, ANOVA1_Dashboard anova1Dashboard, 
                         double placeHoriz, double placeVert, 
                         double withThisWidth, double withThisHeight) {
        
        super(anova1Model, anova1Dashboard, "PostHoc",
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
        this.anova1_Model = anova1Model;
        allTheLabels = anova1Model.getCategoryLabels();
        anova1Canvas = new Canvas(withThisWidth, withThisHeight);
        anchorTitleInfo = new AnchorPane();
        anova1Canvas.heightProperty().addListener(ov-> {doTheGraph();});
        anova1Canvas.widthProperty().addListener(ov-> {doTheGraph();});
        gcANOVA1 = anova1Canvas.getGraphicsContext2D();
        gcANOVA1.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        anova1_ContainingPane = new Pane();
        txtTitle1 = new Text(50, 25, " Post Hoc View ");
        txtTitle2 = new Text (60, 45, " Post Hoc View ");
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
    
    public void doTheGraph() {
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

            errorBarLength = anova1_Model.getPostHocPlusMinus();
            boolean sampleSizesAreEqual = anova1_Model.getAreSampleSizesEqual();
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
            gcANOVA1.strokeLine(daXPosition - spaceFraction, topOfBar, daXPosition + spaceFraction, topOfBar);
            gcANOVA1.strokeLine(daXPosition - spaceFraction, bottomOfBar, daXPosition + spaceFraction, bottomOfBar);
            // ErrorBar
            gcANOVA1.strokeLine(daXPosition, topOfBar, daXPosition, bottomOfBar);  //  Low whisker
        }   //  Loop through batches   
    }
}
