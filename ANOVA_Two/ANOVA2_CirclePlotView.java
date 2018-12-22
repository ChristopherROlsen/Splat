/**************************************************
 *              ANOVA2_CirclePlotView             *
 *                  05/15/18                      *
 *                    12:00                       *
 *************************************************/
package ANOVA_Two;

import genericClasses.UnivariateContinDataObj;
import javafx.scene.layout.AnchorPane;

public class ANOVA2_CirclePlotView extends ANOVA2_BoxCircleInterActView { 

    ANOVA2_CirclePlotView(ANOVA2_Model anova2_Model, 
        ANOVA2_Dashboard anova2_Dashboard,
        double placeHoriz, double placeVert,
        double withThisWidth, double withThisHeight) {

        super(anova2_Model, anova2_Dashboard, placeHoriz, placeVert,
        withThisWidth, withThisHeight);

        strTitle2 = "Circly Wirkly Twosie";

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
                int relativePositionInA = theAppropriateLevel % nFactorB_Levels;
                setColor(theWithinBatch - 1);
                horizPosition = horizPositioner.getIthLeftPosition(theAppropriateLevel, daMiddleXPosition);                

                for (int dataPoint = 0; dataPoint < nDataPoints; dataPoint++) {
                double xx = horizPosition.getX() + 0.5 * horizPosition.getY();
                double yy = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(dataPoint));
                anova2GC.strokeOval(xx - 5, yy - 5, 10, 10);
                }
            }  // Loop through within batches
        }   //  Loop through between batches
    }
}


