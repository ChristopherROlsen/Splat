/**************************************************
 *                 CooksDist_View                 *
 *                    11/14/18                    *
 *                      15:00                     *
 *************************************************/
package regressionMultiple;

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
import genericClasses.*;
import javafx.geometry.Side;
import javafx.scene.control.CheckBox;

public class CooksDist_View extends Scatterplot_View
{
    // POJOs
    boolean[] checkBoxSettings;    
    int nRows, nCheckBoxes;
    double slope, intercept, outlierCircleRadius, influenceCircleRadius,
           radius, diameter;  
    double dataArray[][];
        
        
    String[] cooksDistCheckBoxDescr;

    // My classes    
    Matrix cooks_D, R_Student, DFFITS;
    MultRegression_Dashboard multRegDashboard;
    MultRegression_Model multRegModel;

    //  POJO / FX
    Line line;
    Pane theContainingPane;
    Text txtTitle1, txtTitle2;
    AnchorPane checkBoxRow;
    CheckBox[] cooksDistCheckBoxes;
    AnchorPane anchorPane;
    Pane cooksDContainingPane;

    public CooksDist_View(MultRegression_Model multRegModel, MultRegression_Dashboard multRegDashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        nRows = multRegModel.getNRows();
        cooks_D = new Matrix(nRows, 1);
        cooks_D = multRegModel.getCooksD();
        
        R_Student = new Matrix(nRows, 1);
        R_Student = multRegModel.getR_StudentizedResids();
        
        DFFITS = new Matrix(nRows, 1);
        DFFITS = multRegModel.getDFFITS();
        
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.multRegModel = multRegModel;
        this.multRegDashboard = multRegDashboard;
        
        nCheckBoxes = 3;
        cooksDistCheckBoxDescr = new String[nCheckBoxes];
        cooksDistCheckBoxDescr[0] = " Observation # ";
        cooksDistCheckBoxDescr[1] = " Outliers ";
        cooksDistCheckBoxDescr[2] = " Influential points ";
        
        txtTitle1 = new Text(50, 25, " Cook's Distance ");
        txtTitle2 = new Text (60, 45, " Cook's Distance ");
        
        radius = 4.0; diameter = 2.0 * radius;  //  For the dots
        outlierCircleRadius = 1.75;  // drawing factor 
        influenceCircleRadius = 3.0; // drawing factor
        
        checkBoxHeight = 350.0;
        graphCanvas = new Canvas(initWidth, initHeight);  
        
        makeTheCheckBoxes();    
        makeItHappen();
    }  
    
    public void makeTheCheckBoxes() {       
        // Determine which graphs are initially shown
        checkBoxSettings = new boolean[nCheckBoxes];
        for (int ithBox = 0; ithBox < nCheckBoxes; ithBox++) {
            checkBoxSettings[ithBox] = false;
        }
        
        checkBoxRow = new AnchorPane();
        checkBoxRow.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        cooksDistCheckBoxes = new CheckBox[nCheckBoxes];

        for (int i = 0; i < nCheckBoxes; i++) {
            cooksDistCheckBoxes[i] = new CheckBox(cooksDistCheckBoxDescr[i]);
            
            cooksDistCheckBoxes[i].setMaxWidth(Double.MAX_VALUE);
            cooksDistCheckBoxes[i].setId(cooksDistCheckBoxDescr[i]);
            cooksDistCheckBoxes[i].setSelected(checkBoxSettings[i]);

            cooksDistCheckBoxes[i].setStyle(
                                "-fx-font-size: 14;" +
                                "-fx-border-insets: -5; " + 
                                "-fx-border-radius: 5;" +
                                "-fx-border-style: dotted;" +
                                "-fx-border-width: 0;" );

            
            if (cooksDistCheckBoxes[i].isSelected() == true) 
                cooksDistCheckBoxes[i].setTextFill(Color.GREEN);
            else
                cooksDistCheckBoxes[i].setTextFill(Color.RED);
            
            cooksDistCheckBoxes[i].setOnAction(e->{
                CheckBox tb = ((CheckBox) e.getTarget());
                
                String daID = tb.getId();
                Boolean checkValue = tb.selectedProperty().getValue();
                // Reset selected color
                if (checkValue == true)
                    tb.setTextFill(Color.GREEN);
                else 
                    tb.setTextFill(Color.RED);
                
                for (int ithID = 0; ithID < nCheckBoxes; ithID++) {
                    if (daID.equals(cooksDistCheckBoxDescr[ithID])) {
                        checkBoxSettings[ithID] = (checkValue == true);
                        doTheGraph();
                    }
                }

            }); //  end setOnAction
        }  
        
        checkBoxRow.getChildren().addAll(cooksDistCheckBoxes);
    }
  
        private void makeItHappen() {       
        
        theContainingPane = new Pane();

        gc = graphCanvas.getGraphicsContext2D();
        // gc.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        gc.setFont(new Font("default", 16));
        graphCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        graphCanvas.widthProperty().addListener(ov-> {doTheGraph();});
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
        dataArray = new double[nRows][2];
        xDataMin = 0.0;
        xDataMax = (double)(nRows + 1);
        yDataMin = yDataMax = 0.0;
        
        for (int iRow = 0; iRow < nRows; iRow++)
        {
            double tempDoubleX = (double)iRow;
            double tempDoubleY = cooks_D.get(iRow, 0);
            
            dataArray[iRow][0] = tempDoubleX;
            dataArray[iRow][1] = tempDoubleY;

            //if (tempDoubleX < xDataMin) xDataMin = tempDoubleX;
            if (tempDoubleY < yDataMin) yDataMin = tempDoubleY;
            //if (tempDoubleX > xDataMax) xDataMax = tempDoubleX;
            if (tempDoubleY > yDataMax) yDataMax = tempDoubleY;
        } 
        
        xRange = xDataMax - xDataMin;
        yRange = yDataMax - yDataMin;
        
        //  Make room for the circles
        xDataMin = xDataMin - .02 * xRange; xDataMax = xDataMax + .02 * xRange;
        yDataMin = yDataMin - .02 * yRange; yDataMax = yDataMax + .02 * yRange;  
    }
        
    public void completeTheDeal() { 
        initializeGraphParameters();
        setUpUI();       
        setUpAnchorPane();
        setHandlers();
        
        doTheGraph();     
        theContainingPane = dragableAnchorPane.getTheContainingPane();       
    }
    
    public void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        graphCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        graphCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));

        for (int iChex = 0; iChex < nCheckBoxes; iChex++) {
            // Position checkboxes in the more or less middle
            switch (nCheckBoxes) {
                
                case 1:  //  Etched in lemon marangue
                    cooksDistCheckBoxes[iChex].translateXProperty()
                                            .bind(graphCanvas.widthProperty()
                                            .divide(250.0)
                                            .multiply(65. * iChex)
                                            .subtract(125.0));
                break;
            
                case 2: //  Etched in stone
                    cooksDistCheckBoxes[iChex].translateXProperty()
                                            .bind(graphCanvas.widthProperty()
                                            .divide(250.0)
                                            .multiply(65. * iChex)
                                            .subtract(125.0));
                break;
                
                case 3:  //  Etched in stone
                    cooksDistCheckBoxes[iChex].translateXProperty()
                                            .bind(graphCanvas.widthProperty()
                                            .divide(250.0)
                                            .multiply(25. * iChex)
                                            .subtract(175.0));
                break;
                
                case 4:  //  Etched in stone
                    cooksDistCheckBoxes[iChex].translateXProperty()
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
            AnchorPane.setLeftAnchor(cooksDistCheckBoxes[chex], (chex) * tempWidth / 5.0);
        }

        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());
        
        boolean caseNumbersDesired = checkBoxSettings[0];
        boolean outlierPlotDesired = checkBoxSettings[1];
        boolean influencePlotDesired = checkBoxSettings[2];
        
        for (int i = 0; i < nRows; i++)
        {
            double xx = xAxis.getDisplayPosition(dataArray[i][0]);
            double yy = yAxis.getDisplayPosition(dataArray[i][1]);
            
            //  radius, diameter for centering the dots on point

            gc.fillOval(xx - radius, yy - radius, diameter, diameter); 
            
            if (outlierPlotDesired == true) {
                gc.setFill(Color.BLUE);
                double outRadius = outlierCircleRadius * Math.abs(R_Student.get(i, 0));
                double outDiameter = 2.5 * outRadius;
                if (outRadius > 3.0) {  //  Arbitrary!
                    gc.fillOval(xx - outRadius, yy - outRadius, outDiameter, outDiameter);
                }
            }
            
            if (influencePlotDesired == true) {
                gc.setStroke(Color.GREEN);
                gc.setLineWidth(2);
                double cooksD = cooks_D.get(i, 0);
                double influenceDiameter = 10.0 * cooksD + .5;
                
                // Cooks recommendations are that point with a CooksD > 0.5 
                // might be usefully studied, and cooksD > 1.0 are always 
                // important to study.  Cook, R., & Weisberg, S.  (1999).  
                // Applied Regression Including Computing and Graphicss. p357.
                // Wiler-Interscience, New York.
                
                    if (cooksD > 0.5) {
                        gc.setStroke(Color.ORANGE);
                    }
                    if (cooksD > 1.0) {
                        gc.setStroke(Color.RED);
                    }
                    
                    if (cooksD > 0.5) {
                        gc.strokeLine(xx, yy - influenceDiameter, xx, yy + influenceDiameter);
                        gc.strokeLine(xx - influenceDiameter, yy, xx + influenceDiameter, yy);
                    }


                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1);
            }   
            
            if (caseNumbersDesired == true) {
                line = new Line();
                double x1 = xx;
                double y1 = yy;
                gc.strokeText(String.valueOf(i + 1), xx - 10.0, yy - 10.0);
            }            
        }            
    }   // end doTheGraph

   public Pane getTheContainingPane() { return theContainingPane; }
}

