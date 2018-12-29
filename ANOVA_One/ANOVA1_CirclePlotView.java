/**************************************************
 *             ANOVA1_CirclePlotView              *
 *                    12/27/18                    *
 *                      15:00                     *
 *************************************************/
package ANOVA_One;

import dataObjects.UnivariateContinDataObj;
import dataObjects.QuantitativeDataVariable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class ANOVA1_CirclePlotView extends ANOVA1_View { 
    // POJOs
    private double theMean;

    private String[] strCBDescriptions; 
    
    // My classes
    private UnivariateContinDataObj tempUCDO;
    
    ANOVA1_CirclePlotView(ANOVA1_Model anova1Model, ANOVA1_Dashboard anova1Dashboard, 
                         double placeHoriz, double placeVert, 
                         double withThisWidth, double withThisHeight) {
        
        super(anova1Model, anova1Dashboard, "CirclePlot",
              placeHoriz, placeVert,  withThisWidth, withThisHeight);   
        
        nCheckBoxes = 3;
        strCheckBoxDescriptions = new String[3];
        strCheckBoxDescriptions[0] = " Best Fit Line ";
        strCheckBoxDescriptions[1] = " Outliers ";
        strCheckBoxDescriptions[2] = " Influential points ";      
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.anova1_Model = anova1Model;
        this.anova1Dashboard = anova1Dashboard;
        allTheLabels = anova1Model.getCategoryLabels();
        gcANOVA1 = anova1Canvas.getGraphicsContext2D();  
        explanVar = anova1Dashboard.getExplanVar();
        responseVar = anova1Dashboard.getResponseVar();
        String strForTitle2 = responseVar + " vs. " + explanVar;
        txtTitle2 = new Text (60, 45, strForTitle2);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
   
    public void doTheGraph() {   
        yAxis.setForcedAxisEndsFalse(); // Just in case
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

            for (int dataPoint = 0; dataPoint < nDataPoints; dataPoint++) {
                double xx = daXPosition;
                double yy = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(dataPoint));
                gcANOVA1.strokeOval(xx - 5, yy - 5, 10, 10);
            }
            theMean = yAxis.getDisplayPosition(tempUCDO.getTheMean());
            gcANOVA1.fillRect(daXPosition - 15, theMean - 1, 30, 2);
        }   //  Loop through batches
    }
}
