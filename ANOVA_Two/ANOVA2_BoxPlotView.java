/**************************************************
 *              ANOVA2_BoxPlotView                *
 *                  05/15/18                      *
 *                    12:00                       *
 *************************************************/
package ANOVA_Two;

import dataObjects.UnivariateContinDataObj;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class ANOVA2_BoxPlotView extends ANOVA2_BoxCircleInterActView { 

    ANOVA2_BoxPlotView(ANOVA2_Model anova2_Model, 
            ANOVA2_Dashboard anova2_Dashboard,
            double placeHoriz, double placeVert,
            double withThisWidth, double withThisHeight) {
        
            super(anova2_Model, anova2_Dashboard,placeHoriz, placeVert,
                withThisWidth, withThisHeight);

        strTitle2 = "Boxy Woxy Twozie";
        whiskerEndRanks = new int[2]; 
    }
    
    public void doThePlot() {
        
        double daXPosition, x1, y1, y2, height;
        
        text1Width = title1Text.getLayoutBounds().getWidth();
        text2Width = title2Text.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double hBoxWidth = anova2CategoryBoxes.getWidth();
        
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        double hBoxEdge = (paneWidth - hBoxWidth) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
         
        AnchorPane.setTopAnchor(anchorTitleInfo, 0.0 * tempHeight);
        AnchorPane.setLeftAnchor(anchorTitleInfo, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(anchorTitleInfo, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(anchorTitleInfo, 0.85 * tempHeight);       

        /*
        AnchorPane.setTopAnchor(title2Text, 0.1 * tempHeight);
        AnchorPane.setLeftAnchor(title2Text, txt2Edge * tempWidth);
        AnchorPane.setRightAnchor(title2Text, txt2Edge * tempWidth);
        AnchorPane.setBottomAnchor(title2Text, 0.2 * tempHeight);
        */
        
        AnchorPane.setTopAnchor(anova2CategoryBoxes, 0.15 * tempHeight);
        AnchorPane.setLeftAnchor(anova2CategoryBoxes, hBoxEdge * tempWidth);
        AnchorPane.setRightAnchor(anova2CategoryBoxes, hBoxEdge * tempWidth);
        AnchorPane.setBottomAnchor(anova2CategoryBoxes, 0.70 * tempHeight);        
        
        AnchorPane.setTopAnchor(xAxis, 0.9 * tempHeight);
        AnchorPane.setLeftAnchor(xAxis, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(xAxis, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(xAxis, 0.0 * tempHeight);
        
        AnchorPane.setTopAnchor(yAxis, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(yAxis, 0.0 * tempWidth);
        AnchorPane.setRightAnchor(yAxis, 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(yAxis, 0.2 * tempHeight);
        
        AnchorPane.setTopAnchor(anova2Canvas, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(anova2Canvas, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(anova2Canvas, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(anova2Canvas, 0.2 * tempHeight);
        
        tempPos1 = xAxis.getDisplayPosition(categoryLabels.get(1));
        tempPos0 = xAxis.getDisplayPosition(categoryLabels.get(0));
        bigTickInterval = tempPos1 - tempPos0;
        yAxis.setForcedAxisEndsFalse();
        
        positionTopInfo();
        
        horizPositioner = new HorizontalPositioner(nFactorA_Levels, nFactorB_Levels, bigTickInterval);        
        anova2GC.clearRect(0, 0 , anova2Canvas.getWidth(), anova2Canvas.getHeight());

        for (int theBetweenBatch = 1; theBetweenBatch <= nFactorA_Levels; theBetweenBatch++)
        {
            double daMiddleXPosition = xAxis.getDisplayPosition(categoryLabels.get(theBetweenBatch - 1));
            for (int theWithinBatch = 1; theWithinBatch <= nFactorB_Levels; theWithinBatch++) {
                int theAppropriateLevel = (theBetweenBatch - 1) * nFactorB_Levels + theWithinBatch;
                UnivariateContinDataObj tempUCDO = new UnivariateContinDataObj();
                tempUCDO = anova2Model.getPrelimAB().getIthUCDO(theAppropriateLevel);

                nDataPoints = tempUCDO.getLegalN();
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

                anova2GC.setLineWidth(2);
                anova2GC.setStroke(Color.BLACK);

                int relativePositionInA = theAppropriateLevel % nFactorB_Levels;
                setColor(theWithinBatch - 1);              
                horizPosition = horizPositioner.getIthLeftPosition(theAppropriateLevel, daMiddleXPosition);
                anova2GC.strokeRect(horizPosition.getX(), q3_display, horizPosition.getY(), -iqr_display);    //  box
                anova2GC.strokeLine(horizPosition.getX(), q2_display, horizPosition.getX() + horizPosition.getY(), q2_display);    //  Median

                anova2GC.strokeLine(horizPosition.getX() + 0.5 * horizPosition.getY(), bottomOfLowWhisker, horizPosition.getX() + 0.5 * horizPosition.getY(), q1_display);  //  Low whisker
                anova2GC.strokeLine(horizPosition.getX() + 0.5 * horizPosition.getY(), q3_display, horizPosition.getX() + 0.5 * horizPosition.getY(), topOfHighWhisker);  //  High whisker

                //  Top & bottom of whisker
                double topBottomLength = horizPositioner.getCIEndWidthFrac();
                double midBar = horizPositioner.getMidBarPosition(theAppropriateLevel, daMiddleXPosition);
                anova2GC.strokeLine(midBar - topBottomLength, bottomOfLowWhisker, midBar + topBottomLength, bottomOfLowWhisker);  
                anova2GC.strokeLine(midBar - topBottomLength, topOfHighWhisker, midBar + topBottomLength, topOfHighWhisker);  

                if (whiskerEndRanks[0] != -1)    //  Are there low outliers?
                {
                    int dataPoint = 0;
                    while (dataPoint < whiskerEndRanks[0])
                    {
                        double xx = horizPosition.getX() + 0.5 * horizPosition.getY();
                        double yy = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(dataPoint));
                        anova2GC.fillOval(xx - 3, yy - 3, 6, 6);
                        dataPoint++;
                    }
                }

                if (whiskerEndRanks[1] != -1) //  Are there high outliers?
                {
                    for (int dataPoint = whiskerEndRanks[1] + 1; dataPoint < nDataPoints; dataPoint++)
                    {
                        double xx = horizPosition.getX() + 0.5 * horizPosition.getY();
                        double yy = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(dataPoint));
                        anova2GC.fillOval(xx - 3, yy - 3, 6, 6);
                    }
                }
            }  // Loop through within batches
        }   //  Loop through between batches
    }   

}


