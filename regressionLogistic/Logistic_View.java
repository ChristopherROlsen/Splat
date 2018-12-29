/**************************************************
 *                  Logistic_View                 *
 *                    08/25/18                    *
 *                      00:00                     *
 *************************************************/
package regressionLogistic;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import matrixProcedures.Matrix;
import regressionSimple.*;

public class Logistic_View extends Scatterplot_W_CheckBoxes_View
{
    // POJOs
    boolean dragging;
    
    int nUniques, nOriginalPoints;
    int[] nZerosInBin, nOnesInBin;
   
    double beta0, beta1, density, xDataMin, xDataMax, binSize, gcBinSize;
    double[] theUniqueXValues, theXValueProps, propNZerosInBin, propNOnesInBin;    
    double[][] dataArray;    

    
    String xAxisLabel;
            
    // My classes    
    Matrix dataMatrix, originalX, originalY;
    
    Logistic_Model logRegModel;
    Logistic_Dashboard logRegDashboard;

    //  POJO / FX
    Line line;
    Pane theContainingPane;

    Logistic_View(Logistic_Model logRegModel, Logistic_Dashboard regDashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight)  {
        
        super(logRegModel, regDashboard, placeHoriz, placeVert,
                withThisWidth, withThisHeight);

        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.logRegModel = logRegModel; 
        this.logRegDashboard = regDashboard;
        originalX = logRegModel.getLogisticProcedure().getMatrix_X();
        originalY = logRegModel.getLogisticProcedure().getMatrix_Y();
        nOriginalPoints = originalX.getRowDimension();
        nUniques = logRegModel.getLogisticProcedure().getNUniques();
        theUniqueXValues = new double[nUniques];
        theXValueProps = new double[nUniques];
        theUniqueXValues = logRegModel.getLogisticProcedure().getXValues();
        theXValueProps = logRegModel.getLogisticProcedure().getPropsOfXValues();
        
        nCheckBoxes = 3;
        scatterPlotCheckBoxDescr = new String[nCheckBoxes];
        scatterPlotCheckBoxDescr[0] = " Logistic Best Fit ";
        scatterPlotCheckBoxDescr[1] = "     Raw Data      ";
        scatterPlotCheckBoxDescr[2] = "   Distr 0/ 1's    ";
        
        txtTitle1 = new Text(50, 25, " Logistic plot ");
        txtTitle2 = new Text (60, 45, logRegModel.getRespVsExplSubtitle());
        
        radius = 4.0; diameter = 2.0 * radius;  //  For the dots
        
        checkBoxHeight = 350.0;
        X = logRegModel.getLogisticProcedure().getMatrix_X();
        Y = logRegModel.getLogisticProcedure().getMatrix_Y();
        nDataPoints = X.getRowDimension();
        dataArray = new double[nDataPoints][2];
        for (int ithPoint = 0; ithPoint < nDataPoints; ithPoint++) {
            dataArray[ithPoint][0] = X.get(ithPoint, 0);
            dataArray[ithPoint][1] = Y.get(ithPoint, 0);
        }
        
        // xAxisLabel = logRegModel.getXAxisLabel();
        beta0 = logRegModel.getBeta0();
        beta1 = logRegModel.getBeta1();

        graphCanvas = new Canvas(initWidth, initHeight);  
        
        makeTheCheckBoxes();    
        makeItHappen();
    }  
  
        private void makeItHappen() {       
        
        theContainingPane = new Pane();

        gc = graphCanvas.getGraphicsContext2D();
        gc.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        graphCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        graphCanvas.widthProperty().addListener(ov-> {doTheGraph();});
    }
        
    public void completeTheDeal() { 
        initializeGraphParameters();
        setUpUI();       
        setUpAnchorPane();
        setHandlers();
        calculateTheZeroOneDistributions();
        
        doTheGraph();     
        theContainingPane = dragableAnchorPane.getTheContainingPane();       
    }
    
/******************************************************************************
*  de la Cruz Rot, M.  (2005).  Improving the presentation of Results of      *
*  Logistic Regression with R.  Bulletin of the Ecological Society of America,*
*  86(1): 41-48.                                                              *
*                                                                             *
*  Smart, J., et al. (2004).  A New Means of Presenting the Results of        *
*  Logistic Regression.  Bulletin of the Ecological Society of America,       *
*  85(3): 100-102.                                                            *
******************************************************************************/
    
    public void calculateTheZeroOneDistributions() {
        xDataMin = logRegModel.getLogisticProcedure().getQdvXVariable().getMinValue();
        xDataMax = logRegModel.getLogisticProcedure().getQdvXVariable().getMaxValue();
        
        // Redo the min and max so that end points are included in histo
        double tempXMin = xDataMin;
        double tempXMax = xDataMax;
        double tempBinSize = tempXMax - tempXMin;
        xDataMin = xDataMin - 0.01*tempBinSize;
        xDataMax = xDataMax + 0.01 * tempBinSize;
        
        binSize = (xDataMax - xDataMin) / 10.0;
        nZerosInBin = new int[10];
        nOnesInBin = new int[10];
        propNZerosInBin = new double[10];
        propNOnesInBin = new double[10];
        
        for (int ithDataPt = 0; ithDataPt < nOriginalPoints; ithDataPt++) {
            double xx = originalX.get(ithDataPt, 0);
            double yy = originalY.get(ithDataPt, 0);
            for (int ithBin = 0; ithBin < 10; ithBin++) {
                if ((xDataMin  + ithBin * binSize <= xx) &&
                    (xx < xDataMin  + (ithBin + 1) * binSize))
                {
                    if (yy == 0.0) {
                        nZerosInBin[ithBin]++;
                    }
                    else
                    if (yy == 1.0) {
                        nOnesInBin[ithBin]++;
                    }
                    else
                    {
                        System.out.println("147 log_view, Yikes!  Zero-One violation!!");
                        System.exit(148);
                    }
                    break;
                }   
            }
        }
        
        for (int ithBin = 0; ithBin < 10; ithBin++) {
            propNZerosInBin[ithBin] = (double)nZerosInBin[ithBin] / (nOriginalPoints);
            propNOnesInBin[ithBin] = (double)nOnesInBin[ithBin] / (nOriginalPoints);
        }      
        
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
        
        AnchorPane.setTopAnchor(graphCanvas, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(graphCanvas, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(graphCanvas, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(graphCanvas, 0.1 * tempHeight);
        
        for (int chex = 0; chex < 3; chex++) {
            AnchorPane.setLeftAnchor(scatterPlotCheckBoxes[chex], (chex) * tempWidth / 5.0);
        }

        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());
        
        boolean bestFitLineDesired = checkBoxSettings[0];
        boolean rawDataPlotDesired = checkBoxSettings[1];
        boolean rawHistogramDesired = checkBoxSettings[2];

        for (int i = 0; i < nUniques; i++)
        {
            double xx = xAxis.getDisplayPosition(theUniqueXValues[i]);
            double yy = yAxis.getDisplayPosition(theXValueProps[i]);
            
            //  radius, diameter for centering the dots on point
            gc.setFill(Color.BLACK);
            gc.fillOval(xx - radius, yy - radius, diameter, diameter); 

            if (bestFitLineDesired == true) {
                double delta = (super.xDataMax - super.xDataMin) / NUMBER_OF_DXs;
                xx0 = super.xDataMin; yy0 = getDensityAt(xx0);
                
                for (double x = super.xDataMin; x <= super.xDataMax; x += delta)
                {
                    xx1 = x;
                    yy1 = getDensityAt(xx1);
                    xStart = xAxis.getDisplayPosition(xx0); 
                    yStart = yAxis.getDisplayPosition(yy0); 
                    xStop = xAxis.getDisplayPosition(xx1);
                    yStop = yAxis.getDisplayPosition(yy1);

                    gc.setLineWidth(2);
                    gc.setStroke(Color.BLACK);
                    gc.strokeLine(xStart, yStart, xStop, yStop);    

                    xx0 = xx1; yy0 = yy1;   //  Next start point for line segment
                }   
            }
        }  
        
        if (rawDataPlotDesired == true) {
            double dotRadius = 3.0; 
            double dotDiameter = 6.0;
            gc.setFill(Color.RED);
            for (int ithOrig = 0; ithOrig < nOriginalPoints; ithOrig++) {
            double xx = xAxis.getDisplayPosition(originalX.get(ithOrig, 0));
            double yy = yAxis.getDisplayPosition(originalY.get(ithOrig, 0));
                gc.fillOval(xx - dotRadius, yy - dotRadius, dotDiameter, dotDiameter);
            }
        }
        if (rawHistogramDesired == true) {
            gcBinSize = xAxis.getDisplayPosition(xDataMin + binSize) - xAxis.getDisplayPosition(xDataMin);
            gc.setLineWidth(1);
            gc.setStroke(Color.BLUE);
            for (int ithBin = 0; ithBin < 10; ithBin++) {
                //  Rectangles for zeros
                double heightX0 = 1.75 * propNZerosInBin[ithBin];
                double upperLeftX0 =  xDataMin  + (double)ithBin * binSize;
                double upperLeftY0 = heightX0;              
                double gcUpperLeftX0 = xAxis.getDisplayPosition(upperLeftX0);
                double gcUpperLeftY0 = yAxis.getDisplayPosition(upperLeftY0);
                double gcHeightX0 = yAxis.getDisplayPosition(0.0) - yAxis.getDisplayPosition(heightX0);
                gc.strokeRect(gcUpperLeftX0, gcUpperLeftY0, gcBinSize, gcHeightX0);
                
                // Rectangles for ones
                double heightX1 = 1.75 *propNOnesInBin[ithBin];
                double upperLeftX1 =  xDataMin  + (double)ithBin * binSize;
                double upperLeftY1 = 1.0;                
                double gcUpperLeftX1 = xAxis.getDisplayPosition(upperLeftX1);
                double gcUpperLeftY1 = yAxis.getDisplayPosition(upperLeftY1);
                double gcHeightX1 =  yAxis.getDisplayPosition(1.0 - heightX1);
                gc.strokeRect(gcUpperLeftX1, gcUpperLeftY1, gcBinSize, gcHeightX1);  
            }
        }           
    }   // end doTheGraph
    
    double getDensityAt(double xValue) {
        density = 1.0 / (1.0 + Math.exp(-(beta0 + beta1 * xValue)));
        return density;
    }
    
   public Pane getTheContainingPane() { return theContainingPane; }
}
