/**************************************************
 *            ANOVA2_InteractionView              *
 *                  05/15/18                      *
 *                    12:00                       *
 *************************************************/
package ANOVA_Two;

import genericClasses.UnivariateContinDataObj;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;

public class ANOVA2_InteractionView extends ANOVA2_BoxCircleInterActView { 

    ANOVA2_InteractionView(ANOVA2_Model anova2_Model, 
            ANOVA2_Dashboard anova2_Dashboard,
            double placeHoriz, double placeVert,
            double withThisWidth, double withThisHeight) {
        
        super(anova2_Model, anova2_Dashboard, placeHoriz, placeVert,
            withThisWidth, withThisHeight);

        strTitle2 = "InterActy Wackty Twosie";

    }
    
    public void doThePlot() {
        
        double daXPosition, x1, y1, y2, height;
        double startX, startY, endX, endY; // Line coordinates
        double[][] daMeans;          
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
        daMeans = new double[nFactorA_Levels + 1][nFactorB_Levels + 1]; // +1 to fit the loop variables below
        anova2GC.clearRect(0, 0 , anova2Canvas.getWidth(), anova2Canvas.getHeight());

        for (int theBetweenBatch = 1; theBetweenBatch <= nFactorA_Levels; theBetweenBatch++) {
            for (int theWithinBatch = 1; theWithinBatch <= nFactorB_Levels; theWithinBatch++) {    
                int theAppropriateLevel = (theBetweenBatch - 1) * nFactorB_Levels + theWithinBatch;
                UnivariateContinDataObj tempUCDO = new UnivariateContinDataObj();
                tempUCDO = anova2Model.getPrelimAB().getIthUCDO(theAppropriateLevel);
                daMeans[theBetweenBatch][theWithinBatch] = tempUCDO.getTheMean();
            }   
        }
        
        for (int theWithinBatch = 1; theWithinBatch <= nFactorB_Levels; theWithinBatch++) {           
            for (int theBetweenBatch = 1; theBetweenBatch < nFactorA_Levels; theBetweenBatch++) {
                double daMiddleXPosition = xAxis.getDisplayPosition(categoryLabels.get(theBetweenBatch));
                int theAppropriateLevel = (theBetweenBatch - 1) * nFactorB_Levels + theWithinBatch; 
                UnivariateContinDataObj tempUCDO = new UnivariateContinDataObj();
                tempUCDO = anova2Model.getPrelimAB().getIthUCDO(theAppropriateLevel);

                nDataPoints = tempUCDO.getLegalN();
                anova2GC.setLineWidth(4);

                Point2D hpReturn = horizPositioner.getIthLeftPosition(theAppropriateLevel, daMiddleXPosition);

                int relativePositionInA = theAppropriateLevel % nFactorB_Levels;

                setColor(theWithinBatch - 1);
                
                startX = xAxis.getDisplayPosition(categoryLabels.get(theBetweenBatch - 1));
                startY = yAxis.getDisplayPosition(daMeans[theBetweenBatch][theWithinBatch]);    
                endX = xAxis.getDisplayPosition(categoryLabels.get(theBetweenBatch));
                endY = yAxis.getDisplayPosition(daMeans[theBetweenBatch + 1][theWithinBatch]);

                anova2GC.strokeLine(startX, startY, endX, endY);
                anova2GC.strokeOval(startX - 4, startY - 4, 8, 8);
                anova2GC.strokeOval(endX - 4, endY - 4, 8, 8);           
                // double theMean = tempUCDO.getTheMean();
                // String theMeanAsString = String.format("%7.2f", theMean);

                // float widthOfString = com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().computeStringWidth(theMeanAsString, anova2GC.getFont());
                // float heightOfString = com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().getFontMetrics(anova2GC.getFont()).getLineHeight();
            }  // Loop through between batches
        }   //  Loop through within batches
    }
}


