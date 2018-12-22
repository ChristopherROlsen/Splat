/**************************************************
 *                FDistPDFView                    *
 *                  12/01/18                      *
 *                    12:00                       *
 *************************************************/

package ANOVA_One;

import genericClasses.JustAnAxis;
import genericClasses.DragableAnchorPane;
import genericClasses.Scatterplot_View;
import java.util.ArrayList;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import probabilityDistributions.*;

public class FDistPDFView extends Scatterplot_View
{
    // POJOs
    boolean dragging, shadeLeftTail, shadeMiddleTail, shadeRightTail, 
            identifyPValue, identifyAlphas, hasLeftTailStat, hasRightTailStat;
    
    final int NUMBER_OF_DXs = 600;
    
    int spacesNeeded, maxSpaces, df1, df2;

    double fStat, leftTailCutPoint, rightTailCutPoint, middle_ForGraph, 
           fromHere, toThere, delta, bigDelta, theCriticalValue;
    
    final double MIDDLE_CHISQ = 0.9999;
    final double[] alphas = {0.10, 0.05, 0.025, 0.01};
    double[] initialInterval;
    
    String theModelName;
    ArrayList<String> stringOfNSpaces; 

    Text txtTitle1, txtTitle2;
    
    // My classes
    FDistribution fDistr;   
    ANOVA1_Model anova1_Model;
    QANOVA1_Model qanova1_Model;
    
    //  POJOs / FX
    AnchorPane anchorPane;
    Line line;  
    Pane fPdfContainingPane;

    public FDistPDFView(ANOVA1_Model anova1_Model, ANOVA1_Dashboard anova1Dashboard, 
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {

        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        this.anova1_Model = anova1_Model;
        df1 = anova1_Model.getDFLevels();
        df2 = anova1_Model.getDFError();
        fStat = anova1_Model.getFStat();
        rightTailCutPoint = fStat;
        fDistr = anova1_Model.getFDist();
        initialInterval = new double[2];
        makeItHappen();
    } 
    
    public FDistPDFView(QANOVA1_Model qanova1_Model, QANOVA1_Dashboard qanova1Dashboard, 
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {

        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        this.qanova1_Model = qanova1_Model;
        df1 = qanova1_Model.getDFLevels();
        df2 = qanova1_Model.getDFError();
        fStat = qanova1_Model.getFStat();
        rightTailCutPoint = fStat;
        fDistr = qanova1_Model.getFDist();
        initialInterval = new double[2];
        makeItHappen();
    }
  
        private void makeItHappen() {
        for (int spaces = 1; spaces < maxSpaces; spaces++)
            stringOfNSpaces.add(stringOfNSpaces.get(spaces - 1) + " ");
         
        middle_ForGraph = MIDDLE_CHISQ; 

        initialInterval = fDistr.getInverseMiddleArea(middle_ForGraph);
        identifyPValue = true; identifyAlphas = true;
        
        fPdfContainingPane = new Pane();
        graphCanvas = new Canvas(initWidth, initHeight);
        gc = graphCanvas.getGraphicsContext2D();
        gc.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        graphCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        graphCanvas.widthProperty().addListener(ov-> {doTheGraph();});
    }
        
    public void completeTheDeal() {
        initializeGraphParameters();
        setUpUI();       
        setUpGridPane();
        setHandlers();

        doTheGraph();     
        fPdfContainingPane = dragableAnchorPane.getTheContainingPane();     
    }
        
    private void setUpUI() { 
        String title2String;
        txtTitle1 = new Text(50, 25, "F distribution");
        title2String = String.valueOf(df1) + " and " + String.valueOf(df2) + " degree of freedom";

        txtTitle2 = new Text (60, 45, title2String);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
    
    private void setUpGridPane() {
        dragableAnchorPane = new DragableAnchorPane();
        graphCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        graphCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        String graphsCSS = getClass().getResource("/css/Graphs.css").toExternalForm();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    

        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(txtTitle1, txtTitle2, xAxis, yAxis, graphCanvas);
        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    private void initializeGraphParameters() {
        initialInterval = fDistr.getInverseMiddleArea(MIDDLE_CHISQ);
        fromHere = initialInterval[0];
        toThere = initialInterval[1];
        xAxis = new JustAnAxis(fromHere, toThere);
        xAxis.setSide(Side.BOTTOM);       
        prepareTheDensityAxis();
        yAxis = new JustAnAxis(yDataMin, yDataMax);
        yAxis.setSide(Side.LEFT);
        yAxis.forceLowScaleEndToBe(0.0);  
        yAxis.setBounds(0.0, yDataMax);
        newX_Lower = fromHere; newX_Upper = toThere;
        newY_Lower = yDataMin; newY_Upper = yDataMax;
        xAxis.setLowerBound(newX_Lower ); 
        xAxis.setUpperBound(newX_Upper );
        yAxis.setLowerBound(newY_Lower ); 
        yAxis.setUpperBound(newY_Upper );  
        shadeLeftTail = false;  //  These values are the defaults
        shadeRightTail = true;  //  These values are the defaults
    }

    private void prepareTheDensityAxis()
    {
        xGraphLeft = fromHere;
        xAxis.forceLowScaleEndToBe(0.0);
        xGraphLeft = 0.0000001;   
        xGraphRight = toThere;
        bigDelta = (xGraphRight - xGraphLeft) / NUMBER_OF_DXs;
        delta = bigDelta;
        xDataMin = xDataMax = xGraphLeft;
        xRange = xGraphRight - xGraphLeft;        
        yRange = yDataMax = getInitialYMax();
        // These constants control the rate of axis scale change when dragging
        deltaX = 0.005 * xRange; deltaY = 0.005 * yRange;   
    }
    
    public void setIntervalOfInterest(double startHere, double endHere)  {
        fromHere = startHere; toThere = endHere;
        delta = (endHere - startHere) / bigDelta * NUMBER_OF_DXs; 
    }
    
    public double getInitialYMax() {
        yDataMax = 1.0;                
        if (df1 > 2)
            yDataMax = 0.35;
        if (df1 > 5)
            yDataMax = 0.25;                
        return yDataMax;
    }

    public void doTheGraph()
    {   
        double xx0, yy0, xx1, yy1;
        String tempString;
        double text1Width = txtTitle1.getLayoutBounds().getWidth();
        double text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        AnchorPane.setTopAnchor(txtTitle1, 0.0 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle1, 0.1 * tempHeight);
                
        AnchorPane.setTopAnchor(txtTitle2, 0.1 * tempHeight);
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
        
        AnchorPane.setTopAnchor(graphCanvas, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(graphCanvas, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(graphCanvas, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(graphCanvas, 0.1 * tempHeight);
        
        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());
        
        //  Start point for graph
        xx0 = xGraphLeft; yy0 = fDistr.getDensity(xx0);
        
        for (double x = xGraphLeft; x <= xGraphRight; x += delta)
        {
            xx1 = x;
            yy1 = fDistr.getDensity(xx1);
            xStart = xAxis.getDisplayPosition(xx0); 
            yStart = yAxis.getDisplayPosition(yy0); 
            xStop = xAxis.getDisplayPosition(xx1);
            yStop = yAxis.getDisplayPosition(yy1);
            
            gc.setLineWidth(2);
            gc.setStroke(Color.BLACK);
            gc.strokeLine(xStart, yStart, xStop, yStop);

            if ((shadeLeftTail == true) && (x < leftTailCutPoint)) {
                yStart = yAxis.getDisplayPosition(0.0);                
                gc.strokeLine(xStart, yStart, xStop, yStop);
            }
            
            if ((shadeRightTail == true) && (x > rightTailCutPoint)) {
                yStart = yAxis.getDisplayPosition(0.0);                
                gc.strokeLine(xStart, yStart, xStop, yStop);
            }     

            xx0 = xx1; yy0 = yy1;   //  Next start point for line segment
        }   
        
        if (identifyPValue == true) {
            double elFactoro = 7.0;
            xStart = xStop = xAxis.getDisplayPosition(rightTailCutPoint);
            yStart = yAxis.getDisplayPosition(0.0);
            
            
            double heightAtPoint_10 = yAxis.getDisplayPosition(fDistr.getDensity(fDistr.getInvRightTailArea(0.10))) - elFactoro * 0.1 * yStart;
            double heightAtPoint_05 = yAxis.getDisplayPosition(fDistr.getDensity(fDistr.getInvRightTailArea(0.05))) - elFactoro * 0.05 * yStart;            
            
            yStop = (heightAtPoint_10 +  heightAtPoint_05) / 2.0;        
 
            gc.setLineWidth(2);
            gc.setStroke(Color.RED);
            gc.strokeLine(xStart, yStart, xStop, yStop);
            double thePValue = fDistr.getRightTailArea(fStat);

            tempString = String.format("F = %6.3f, pValue = %4.3f", fStat, thePValue);              
   
            gc.setFill(Color.RED);
            gc.fillText(tempString, xStop + 5, yStop - 5);
        }
        
        if (identifyAlphas == true) {
            // elFactoro is there to help the alphas to avoid each other. It is intended
            //  to represent a fraction of the vertical size of the window
            double elFactoro = 7.0;
            for (double e: alphas) {
                theCriticalValue = fDistr.getInvRightTailArea(e); 
                xStart = xStop = xAxis.getDisplayPosition(theCriticalValue);
                yStart = yAxis.getDisplayPosition(0.0);
                yStop = yAxis.getDisplayPosition(fDistr.getDensity(theCriticalValue)) - elFactoro * e *yStart;   // Hard-wired test statistic  
                gc.setLineWidth(2);
                gc.setStroke(Color.BLUE);
                gc.strokeLine(xStart, yStart, xStop, yStop);
                double thePValue = fDistr.getRightTailArea(rightTailCutPoint);
                gc.setFill(Color.BLUE);
                tempString = String.format("Critical Value (\u03B1 = %4.3f) =%7.3f", e, theCriticalValue);
                gc.fillText(tempString, xStop - 00, yStop - 5);  // <---
            }
        }                 
    }

    public Pane getTheContainingPane() { return fPdfContainingPane; }

}



