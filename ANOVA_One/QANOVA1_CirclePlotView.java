/**************************************************
 *             QANOVA1_CirclePlotView             *
 *                    12/19/18                    *
 *                      18:00                     *
 *************************************************/
package ANOVA_One;

import dataObjects.UnivariateContinDataObj;
import dataObjects.QuantitativeDataVariable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class QANOVA1_CirclePlotView extends QANOVA1_View { 
    // POJOs
    double theMean;
    String[] strCBDescriptions = {" Best Fit Line ", 
                                               " Outliers ", 
                                               " Influential points "};     
    // My classes
    UnivariateContinDataObj tempUCDO;
    
    QANOVA1_CirclePlotView(QANOVA1_Model qanova1Model, QANOVA1_Dashboard qanova1Dashboard, 
                         double placeHoriz, double placeVert, 
                         double withThisWidth, double withThisHeight) {
        
        super(qanova1Model, qanova1Dashboard, "CirclePlot",
              placeHoriz, placeVert,  withThisWidth, withThisHeight);   
        
        nCheckBoxes = 3;
        strCheckBoxDescriptions = new String[3];
        strCheckBoxDescriptions[0] = " Best Fit Line ";
        strCheckBoxDescriptions[1] = " Outliers ";
        strCheckBoxDescriptions[2] = " Influential points ";      
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.qanova1_Model = qanova1Model;
        this.qanova1Dashboard = qanova1Dashboard;
        gcQANOVA1 = qanova1Canvas.getGraphicsContext2D();  
        gcQANOVA1.setFont(Font.font("Courier New",
                                    FontWeight.BOLD,
                                    FontPosture.REGULAR,
                                    12));
        txtTitle1 = new Text(50, 25, " Circle Plot View ");
        txtTitle2 = new Text (60, 45, " Circle Plot View ");
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
   
    public void doTheGraph() {
        double downShift;
        
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
            
            
            

            for (int dataPoint = 0; dataPoint < nDataPoints; dataPoint++) {
                double xx = daXPosition;
                double yy = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(dataPoint));
                gcQANOVA1.strokeOval(xx - 5, yy - 5, 10, 10);
            }
            theMean = yAxis.getDisplayPosition(tempUCDO.getTheMean());
            gcQANOVA1.fillRect(daXPosition - 15, theMean - 1, 30, 2);
        }   //  Loop through batches
    }
}
