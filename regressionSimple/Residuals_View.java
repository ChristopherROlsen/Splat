/**************************************************
 *                 Residuals_View                 *
 *                    10/14/18                    *
 *                      12:00                     *
 *************************************************/
package regressionSimple;

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
import probabilityDistributions.FDistribution;

public class Residuals_View extends Scatterplot_W_CheckBoxes_View
{
    // POJOs
    double slope, intercept, outlierCircleRadius, influenceCircleRadius,
           influenceTrigger;
    // My classes    
    Matrix r_Student, cooks_D;
    FDistribution fInfluence;
    //  POJO / FX

    Line line;
    Pane theContainingPane;
    
    public Residuals_View(Regression_Model regModel, Regression_Dashboard regDashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        
        super(regModel, regDashboard, placeHoriz, placeVert,
                        withThisWidth, withThisHeight);   
        
        X = regModel.getXVar(); 
        Y = regModel.getResids();
        slope = 0.0;
        intercept = 0.0;
        

        nCheckBoxes = 3;
        scatterPlotCheckBoxDescr = new String[nCheckBoxes];        
        scatterPlotCheckBoxDescr[0] = " Horizontal Line ";
        scatterPlotCheckBoxDescr[1] = " Outliers ";
        scatterPlotCheckBoxDescr[2] = " Influential points ";
        
        txtTitle1 = new Text(50, 25, " Residual plot ");
        txtTitle2 = new Text (60, 45, " Residual plot ");
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 

        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.regrModel = regModel;
        this.regrDashboard = regDashboard;
        
        radius = 4.0; diameter = 2.0 * radius;  //  For the dots
        outlierCircleRadius = 1.75;  // drawing factor 
        influenceCircleRadius = 3.0; // drawing factor

        r_Student = new Matrix(nDataPoints, 1);
        r_Student = regModel.getR_StudentizedResids();
        
        cooks_D = new Matrix(nDataPoints, 1);
        cooks_D = regModel.getCooksD();
        
        // ******************************************************************
        // Cook, R.  Detection of Influential Observation in Linear         *
        // Regresion (1977).  Technometrics 19(1): 15-18.                   *
        // Hines, R. J., & Hines, W. G. (1995).  Exploring Cook's Statistic *
        // Graphically.  The American Statistician. 49(4): 1995, 389-394    *
        // Letters to the Editor (Obenchain, Cook). (1977). Technometrics   *
        // 19(1): 348-351.                                                  *
        // ******************************************************************
        
        fInfluence = new FDistribution(1, nDataPoints);
        influenceTrigger = fInfluence.getInvLeftTailArea(0.5);        
        
        checkBoxHeight = 350.0;
        X = regModel.getXVar();
        Y = regModel.getResids();
        slope = 0.0;
        intercept = 0.0;
        graphCanvas = new Canvas(initWidth, initHeight);        
        makeTheCheckBoxes();    
        makeItHappen();
    }  
  
        public void makeItHappen() {       
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
        yAxis.setLabel("Residuals");
        doTheGraph();   
        
        theContainingPane = dragableAnchorPane.getTheContainingPane();       
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
        boolean outlierPlotDesired = checkBoxSettings[1];
        boolean influencePlotDesired = checkBoxSettings[2];
        
        for (int i = 0; i < nDataPoints; i++)
        {
            double xx = xAxis.getDisplayPosition(dataArray[i][0]);
            double yy = yAxis.getDisplayPosition(dataArray[i][1]);
            
            //  radius, diameter for centering the dots on point
            gc.setFill(Color.GREEN);
            gc.fillOval(xx - radius, yy - radius, diameter, diameter); 
            
            if (outlierPlotDesired == true) {
                gc.setFill(Color.RED);
                double outRadius = outlierCircleRadius * Math.abs(r_Student.get(i, 0));
                double outDiameter = 2.5 * outRadius;
                if (outRadius > 3.0) {  //  Arbitrary!
                    gc.fillOval(xx - outRadius, yy - outRadius, outDiameter, outDiameter);
                }
            }
            if (influencePlotDesired == true) {
                gc.setStroke(Color.BLUE);
                gc.setLineWidth(2);
                double cooksD = cooks_D.get(i, 0);
                double influenceDiameter = 10.0 * cooksD + .5;
                if (cooksD > influenceTrigger) {    //  Arbitrary! 
                    gc.strokeLine(xx, yy - influenceDiameter, xx, yy + influenceDiameter);
                    gc.strokeLine(xx - influenceDiameter, yy, xx + influenceDiameter, yy);
                }
                gc.setStroke(Color.GREEN);
                gc.setLineWidth(1);
            }             

            if (bestFitLineDesired == true) {
                line = new Line();
                double x1 = xAxis.getDisplayPosition(xDataMin);
                double y1 = yAxis.getDisplayPosition(slope * xDataMin + intercept);
                double x2 = xAxis.getDisplayPosition(xDataMax);
                double y2 = yAxis.getDisplayPosition(slope * xDataMax + intercept);
                gc.setLineWidth(1);
                gc.setStroke(Color.TOMATO);
                gc.strokeLine(x1, y1, x2, y2);  
            }
        }            
    }   // end doTheGraph
    
   public Pane getTheContainingPane() { return theContainingPane; }
}
