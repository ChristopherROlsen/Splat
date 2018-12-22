/**************************************************
 *           Scatterplot_W_CheckBoxes_View        *
 *                    12/13/18                    *
 *                      21:00                     *
 *************************************************/

// *******************************************************************
// *   Subclasses:                                                   *
// *        QANOVA1_View                                             *
// *        LogisticResids_View                                      *
// *        LogisticView                                             *
// *******************************************************************
package regressionSimple;

import ANOVA_One.QANOVA1_Dashboard;
import ANOVA_One.QANOVA1_Model;
import genericClasses.DragableAnchorPane;
import javafx.geometry.Side;
import javafx.scene.control.CheckBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import matrixProcedures.Matrix;
import javafx.scene.paint.Color;
import genericClasses.*;
import regressionLogistic.*;

public class Scatterplot_W_CheckBoxes_View extends Scatterplot_View
{

    public boolean[] checkBoxSettings;
    
    public double radius, diameter;
    
    Logistic_Model logRegModel;
    Logistic_Dashboard logisticDashboard;
    
    QANOVA1_Model qanova1Model;
    QANOVA1_Dashboard anova1Dashboard;
    
    double dataArray[][];
    
    final public int NUMBER_OF_DXs = 600;   
    public int nDataPoints, nCheckBoxes;
    
    public String[] scatterPlotCheckBoxDescr;

    // My classes
    public AnchorPane checkBoxRow;
    public CheckBox[] scatterPlotCheckBoxes;

    public Matrix X, Y;
    Regression_Dashboard regrDashboard;
    Regression_Model regrModel;

    //  POJO / FX
    AnchorPane anchorPane;
    Pane regrContainingPane;
    public Text txtTitle1, txtTitle2;

    public Scatterplot_W_CheckBoxes_View(Regression_Model regModel, Regression_Dashboard regDashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.regrModel = regModel;
        this.regrDashboard = regDashboard;
        xAxisLabel = regModel.getXVariable().getDataLabel();
        yAxisLabel = regModel.getYVariable().getDataLabel();
        nDataPoints = regrModel.getNRows();
    } 
    
    public Scatterplot_W_CheckBoxes_View(Logistic_Model logRegModel, Logistic_Dashboard logisticDashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {

        super(placeHoriz, placeVert, withThisWidth, withThisHeight);            
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.logRegModel = logRegModel;
        this.logisticDashboard = logisticDashboard; 
        
        xAxisLabel = logRegModel.getXAxisLabel();
        yAxisLabel = "Probability";
    } 
    
    public Scatterplot_W_CheckBoxes_View(QANOVA1_Model qanova1Model, QANOVA1_Dashboard anova1Dashboard,
                         double placeHoriz, double placeVert, 
                         double withThisWidth, double withThisHeight) {

        super(placeHoriz, placeVert, withThisWidth, withThisHeight);            
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.qanova1Model = qanova1Model;
        this.anova1Dashboard = anova1Dashboard; 
        
        xAxisLabel = qanova1Model.getXAxisLabel();
        
        //allTheLabels = qanova1Model.getCategoryLabels();
       // System.out.println("44 crcPlotView, allTheLabels = " + allTheLabels);
        
        yAxisLabel = "Probability";
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
        xAxis.setLabel(xAxisLabel);
        yAxis = new genericClasses.JustAnAxis(yDataMin, yDataMax);
        yAxis.setSide(Side.LEFT);
        yAxis.setLabel(yAxisLabel);
        newX_Lower = xDataMin; newX_Upper = xDataMax;
        newY_Lower = yDataMin; newY_Upper = yDataMax;
        xAxis.setLowerBound(newX_Lower ); 
        xAxis.setUpperBound(newX_Upper );
        yAxis.setLowerBound(newY_Lower ); 
        yAxis.setUpperBound(newY_Upper );            
    }
    
    public void makeTheCheckBoxes() {       
        // Determine which graphs are initially shown
        checkBoxSettings = new boolean[nCheckBoxes];
        for (int ithBox = 0; ithBox < nCheckBoxes; ithBox++) {
            checkBoxSettings[ithBox] = false;
        }
        
        checkBoxRow = new AnchorPane();
        checkBoxRow.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        scatterPlotCheckBoxes = new CheckBox[nCheckBoxes];

        for (int i = 0; i < nCheckBoxes; i++) {
            scatterPlotCheckBoxes[i] = new CheckBox(scatterPlotCheckBoxDescr[i]);
            
            scatterPlotCheckBoxes[i].setMaxWidth(Double.MAX_VALUE);
            scatterPlotCheckBoxes[i].setId(scatterPlotCheckBoxDescr[i]);
            scatterPlotCheckBoxes[i].setSelected(checkBoxSettings[i]);

            scatterPlotCheckBoxes[i].setStyle(
                                "-fx-font-size: 14;" +
                                "-fx-border-insets: -5; " + 
                                "-fx-border-radius: 5;" +
                                "-fx-border-style: dotted;" +
                                "-fx-border-width: 0;" );

            
            if (scatterPlotCheckBoxes[i].isSelected() == true) 
                scatterPlotCheckBoxes[i].setTextFill(Color.GREEN);
            else
                scatterPlotCheckBoxes[i].setTextFill(Color.RED);
            
            scatterPlotCheckBoxes[i].setOnAction(e->{
                CheckBox tb = ((CheckBox) e.getTarget());
                
                String daID = tb.getId();
                Boolean checkValue = tb.selectedProperty().getValue();
                // Reset selected color
                if (checkValue == true)
                    tb.setTextFill(Color.GREEN);
                else 
                    tb.setTextFill(Color.RED);
                
                for (int ithID = 0; ithID < nCheckBoxes; ithID++) {
                    if (daID.equals(scatterPlotCheckBoxDescr[ithID])) {
                        checkBoxSettings[ithID] = (checkValue == true);
                        doTheGraph();
                    }
                }

            }); //  end setOnAction
        }  
        
        checkBoxRow.getChildren().addAll(scatterPlotCheckBoxes);
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

        for (int iChex = 0; iChex < nCheckBoxes; iChex++) {
            // Position checkboxes in the more or less middle
            switch (nCheckBoxes) {
                
                case 1:  //  Etched in lemon marangue
                    scatterPlotCheckBoxes[iChex].translateXProperty()
                                            .bind(graphCanvas.widthProperty()
                                            .divide(250.0)
                                            .multiply(65. * iChex)
                                            .subtract(125.0));
                break;
            
                case 2: //  Etched in stone
                    scatterPlotCheckBoxes[iChex].translateXProperty()
                                            .bind(graphCanvas.widthProperty()
                                            .divide(250.0)
                                            .multiply(65. * iChex)
                                            .subtract(125.0));
                break;
                
                case 3:  //  Etched in stone
                    scatterPlotCheckBoxes[iChex].translateXProperty()
                                            .bind(graphCanvas.widthProperty()
                                            .divide(250.0)
                                            .multiply(25. * iChex)
                                            .subtract(175.0));
                break;
                
                case 4:  //  Etched in stone
                    scatterPlotCheckBoxes[iChex].translateXProperty()
                                            .bind(graphCanvas.widthProperty()
                                            .divide(250.0)
                                            .multiply(65. * iChex)
                                            .subtract(225.0));
                break;
            }
        }
     
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        String graphsCSS = getClass().getResource("/css/Graphs.css").toExternalForm();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    

        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(checkBoxRow, txtTitle1, txtTitle2, xAxis, yAxis, graphCanvas);
        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
 

   public Pane getTheContainingPane() { return regrContainingPane; }
}