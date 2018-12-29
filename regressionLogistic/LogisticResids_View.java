/**************************************************
 *             LogisticResiduals_View             *
 *                    08/24/18                    *
 *                      12:00                     *
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

public class LogisticResids_View extends RegressionWithoutCheckBoxes_View
{
    // POJOs
    double dotRadius, dotDiameter;

    // My classes   
    Logistic_Model logisticModel;

    //  POJO / FX

    Line line;
    Pane theContainingPane;
    
    public LogisticResids_View(Logistic_Model logisticModel, Logistic_Dashboard logisticDashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        
        super(logisticModel, logisticDashboard, placeHoriz, placeVert,
                        withThisWidth, withThisHeight);   
        this.logisticModel = logisticModel;
        this.logisticDashboard = logisticDashboard;
        X = logisticModel.getEstimatedProbs(); 
        Y = logisticModel.getDevianceResids();

        dotRadius = 3.0;
        dotDiameter = 6.0;
        
        txtTitle1 = new Text(50, 25, " Residual plot ");
        String strTxtTitle2 = "Deviance residuals vs. " + logisticModel.getLogisticProcedure().getExplanVar();
        txtTitle2 = new Text (60, 45, strTxtTitle2);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 

        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        
        graphCanvas = new Canvas(initWidth, initHeight);    
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

        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());
        
        for (int i = 0; i < nDataPoints; i++)
        {
            double xx = xAxis.getDisplayPosition(X.get(i, 0));
            double yy = yAxis.getDisplayPosition(Y.get(i, 0));
            
            //  radius, diameter for centering the dots on point
            gc.setFill(Color.BLACK);
            gc.fillOval(xx - dotRadius, yy - dotRadius, dotDiameter, dotDiameter); 
        }    

                line = new Line();
                double x1 = xAxis.getDisplayPosition(xDataMin);
                double y1 = yAxis.getDisplayPosition(0.0);
                double x2 = xAxis.getDisplayPosition(xDataMax);
                double y2 = yAxis.getDisplayPosition(0.0);
                gc.setLineWidth(2);
                gc.setStroke(Color.TOMATO);
                gc.strokeLine(x1, y1, x2, y2);  
                
    }   // end doTheGraph

   public Pane getTheContainingPane() { return theContainingPane; }
}

