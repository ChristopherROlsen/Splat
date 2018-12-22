/**************************************************
 *               QANOVA1_BoxPlotView               *
 *                    12/18/18                    *
 *                      12:00                     *
 *************************************************/
package ANOVA_One;

import genericClasses.QuantitativeDataVariable;
import genericClasses.UnivariateContinDataObj;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class QANOVA1_BoxPlotView extends QANOVA1_View { 
    // POJOs
    
    double bottomOfLowWhisker, topOfHighWhisker;
    double[] fiveNumberSummary;
    
    int[] whiskerEndRanks;


    //  My classes
    UnivariateContinDataObj tempUCDO;
    
    // FX classes

    QANOVA1_BoxPlotView(QANOVA1_Model qanova1Model, QANOVA1_Dashboard qanova1Dashboard, 
                         double placeHoriz, double placeVert, 
                         double withThisWidth, double withThisHeight) {  
        super(qanova1Model, qanova1Dashboard, "BoxPlot",
              placeHoriz, placeVert,  withThisWidth, withThisHeight);
        
        nCheckBoxes = 2;
        strCheckBoxDescriptions = new String[3];
        strCheckBoxDescriptions[0] = " Means diamond";
        strCheckBoxDescriptions[1] = " Extreme Outliers ";
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.qanova1_Model = qanova1Model;
        this.qanova1Dashboard = qanova1Dashboard;
        allTheLabels = qanova1Model.getCategoryLabels();
        gcQANOVA1 = qanova1Canvas.getGraphicsContext2D(); 
        gcQANOVA1.setFont(Font.font("Courier New",
                                    FontWeight.BOLD,
                                    FontPosture.REGULAR,
                                    12));
        txtTitle1 = new Text(50, 25, " One way ANOVA ");
        txtTitle2 = new Text (60, 45, " One way ANOVA ");
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 

    }
    
    public void doTheGraph() {    
        double daXPosition, text1Width, text2Width, paneWidth,
               txt1Edge, txt2Edge, downShift;
        
        yAxis.setForcedAxisEndsFalse(); // Just in case
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();
        paneWidth = dragableAnchorPane.getWidth();
        txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        
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
            daXPosition = xAxis.getDisplayPosition(Double.parseDouble(allTheLabels.get(theBatch)));
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

            fiveNumberSummary = new double[5];
            fiveNumberSummary = tempUCDO.get_5NumberSummary();
            whiskerEndRanks = tempUCDO.getWhiskerEndRanks();

            bottomOfLowWhisker = yAxis.getDisplayPosition(fiveNumberSummary[0]);
 
            if (whiskerEndRanks[0] != -1)
                bottomOfLowWhisker = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(whiskerEndRanks[0]));

            topOfHighWhisker = yAxis.getDisplayPosition(fiveNumberSummary[4]);

            if (whiskerEndRanks[1] != -1)
                topOfHighWhisker = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(whiskerEndRanks[1]));

            double min_display = yAxis.getDisplayPosition(fiveNumberSummary[0]);
            double q1_display = yAxis.getDisplayPosition(fiveNumberSummary[1]);
            double q2_display = yAxis.getDisplayPosition(fiveNumberSummary[2]);
            double q3_display = yAxis.getDisplayPosition(fiveNumberSummary[3]);
            double max_display = yAxis.getDisplayPosition(fiveNumberSummary[4]);
            double iqr_display = q3_display - q1_display;
            double iqr = fiveNumberSummary[3] - fiveNumberSummary[1];
            
            double spacing = 100.;

            gcQANOVA1.setLineWidth(2);
            gcQANOVA1.setStroke(Color.BLACK);
            double spaceFraction = 0.25 * spacing;

            // x, y, w, h
            gcQANOVA1.strokeRect(daXPosition - spaceFraction, q3_display, 2 * spaceFraction, -iqr_display);    //  box
            gcQANOVA1.strokeLine(daXPosition - spaceFraction, q2_display, daXPosition + spaceFraction, q2_display);    //  Median

            gcQANOVA1.strokeLine(daXPosition, bottomOfLowWhisker, daXPosition, q1_display);  //  Low whisker
            gcQANOVA1.strokeLine(daXPosition, q3_display, daXPosition, topOfHighWhisker);  //  High whisker

            // Low outliers
            if (whiskerEndRanks[0] != -1)    //  Are there low outliers?
            {
                int dataPoint = 0;
                while (dataPoint < whiskerEndRanks[0])
                {
                    double xx = daXPosition;
                    double yy = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(dataPoint));
                    gcQANOVA1.fillOval(xx - 3, yy - 3, 6, 6);
                    dataPoint++;
                }
            }

            // High outliers
            if (whiskerEndRanks[1] != -1)    //  Are there high outliers?
            {
                for (int dataPoint = whiskerEndRanks[1] + 1; dataPoint < nDataPoints; dataPoint++)
                {
                    double xx = daXPosition;
                    double yy = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(dataPoint));
                    gcQANOVA1.fillOval(xx - 3, yy - 3, 6, 6);
                }
            }  
        }   //  Loop through batches
    }
}
