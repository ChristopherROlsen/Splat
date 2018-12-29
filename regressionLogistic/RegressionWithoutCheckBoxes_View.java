/**************************************************
 *         RegressionWithoutCheckBoxes_View       *
 *                    08/24/18                    *
 *                      12:00                     *
 *************************************************/
package regressionLogistic;

import superClasses.Scatterplot_View;
import genericClasses.DragableAnchorPane;
import javafx.geometry.Side;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import matrixProcedures.Matrix;
import genericClasses.*;

public class RegressionWithoutCheckBoxes_View extends Scatterplot_View
{
    public boolean[] checkBoxSettings;
    public double radius, diameter;
    
    Logistic_Model logRegModel;
    Logistic_Dashboard logisticDashboard;
    
    double dataArray[][];
    
    final public int NUMBER_OF_DXs = 600;   
    public int nDataPoints;
    
    public String[] XYLabels;

    // My classes
   public Matrix X, Y;

    //  POJO / FX
    AnchorPane anchorPane;
    Pane regrContainingPane;
    public Text txtTitle1, txtTitle2;

    
    public RegressionWithoutCheckBoxes_View(Logistic_Model logRegModel, Logistic_Dashboard logisticDashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {

        super(placeHoriz, placeVert, withThisWidth, withThisHeight);            
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.logRegModel = logRegModel;
        this.logisticDashboard = logisticDashboard;       
    } 

    public void setUpUI() {
        //txtTitle1 = new Text(50, 25, " Scatterplot ");
        //txtTitle2 = new Text (60, 45, " Scatterplot ");
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
   
    public void initializeGraphParameters() {  
        constructDataArray();
        xAxis = new genericClasses.JustAnAxis(xDataMin, xDataMax);
        xAxis.setSide(Side.BOTTOM);       
        yAxis = new genericClasses.JustAnAxis(yDataMin, yDataMax);
        yAxis.setSide(Side.LEFT);
        newX_Lower = xDataMin; newX_Upper = xDataMax;
        newY_Lower = yDataMin; newY_Upper = yDataMax;
        xAxis.setLowerBound(newX_Lower ); 
        xAxis.setUpperBound(newX_Upper );
        yAxis.setLowerBound(newY_Lower ); 
        yAxis.setUpperBound(newY_Upper );            
    }
    
    private void constructDataArray()
    {
        nDataPoints = X.getRowDimension();
        dataArray = new double[nDataPoints][2];
        
        xDataMin = xDataMax = X.get(0, 0);
        yDataMin = yDataMax = Y.get(0, 0);
        
        for (int iRow = 0; iRow < nDataPoints; iRow++)
        {
            double tempDoubleX = X.get(iRow, 0);
            double tempDoubleY = Y.get(iRow, 0);
            
            dataArray[iRow][0] = tempDoubleX;
            dataArray[iRow][1] = tempDoubleY;

            if (tempDoubleX < xDataMin) xDataMin = tempDoubleX;
            if (tempDoubleY < yDataMin) yDataMin = tempDoubleY;
            if (tempDoubleX > xDataMax) xDataMax = tempDoubleX;
            if (tempDoubleY > yDataMax) yDataMax = tempDoubleY;
        } 
        
        xRange = xDataMax - xDataMin;
        yRange = yDataMax - yDataMin;
        
        //  Make room for the circles
        xDataMin = xDataMin - .02 * xRange; xDataMax = xDataMax + .02 * xRange;
        yDataMin = yDataMin - .02 * yRange; yDataMax = yDataMax + .02 * yRange;  
    }
    

    
    public void setUpAnchorPane() {
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
    
 

   public Pane getTheContainingPane() { return regrContainingPane; }
}
