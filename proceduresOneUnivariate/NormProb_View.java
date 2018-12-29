/**************************************************
 *                  NormProb_View                 *
 *                    12/28/18                    *
 *                      18:00                     *
 *************************************************/
package proceduresOneUnivariate;

import genericClasses.DragableAnchorPane;
import dataObjects.QuantitativeDataVariable;
import superClasses.Scatterplot_View;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import regressionSimple.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import regressionMultiple.*;
import regressionLogistic.*;

public class NormProb_View extends Scatterplot_View 
{
    boolean[] checkBoxSettings;
    int nDataPoints, nCheckBoxes;
    double slope, intercept;

    double[] dataArray, normalScoresArray, adStats;
    String dataLabel, normalScoresLabel, npModelThisTime, adString, adPValue;
    boolean dragging;
    AnchorPane anchorPane;    
    final String[] scatterPlotCheckBoxDescr = {" Best Fit Line ", 
                                           " Outliers ", 
                                           " Influential points "}; 
        // My classes
    AnchorPane checkBoxRow;
    Pane theContainingPane;
    QuantitativeDataVariable qdv_Data, qdv_NormalScores;
    NormProb_Model normProb_Model;
    Line line;
    Text title1Text, title2Text;
    CheckBox[] scatterPlotCheckBoxes;

    public NormProb_View(NormProb_Model normProb_Model, Exploration_Dashboard explore_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) 
    {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);    
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.normProb_Model = normProb_Model;
        npModelThisTime = "UnivExploration";
        constructTheModel();
        title2Text = new Text (60, 45, normProb_Model.getSubTitle());
        makeTheCheckBoxes();    
        makeItHappen();         
    }
    
    public NormProb_View(NormProb_Model normProb_Model, MultRegression_Dashboard multReg_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) 
    {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);  
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.normProb_Model = normProb_Model;
        npModelThisTime = "MultipleRegression";
        constructTheModel();
        title2Text = new Text (60, 45, normProb_Model.getSubTitle());
        makeTheCheckBoxes();    
        makeItHappen();         
    }
    
    public NormProb_View(NormProb_Model normProb_Model, Simple_Regression_Dashboard reg_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) 
    {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.normProb_Model = normProb_Model; 
        npModelThisTime = "SimpleRegression";
        constructTheModel();
        title2Text = new Text (60, 45, "Standardized residuals vs. " + normProb_Model.getNormProbLabel());       
        makeTheCheckBoxes();    
        makeItHappen();         
    }
    
    
    public NormProb_View(NormProb_Model normProb_Model, Logistic_Dashboard logistic_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) 
    {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);  
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.normProb_Model = normProb_Model;
        npModelThisTime = "LogisticRegression";
        constructTheModel();
        
        title2Text = new Text (60, 45, "Standardized residuals vs. " + normProb_Model.getNormProbLabel());
        
        makeTheCheckBoxes();    
        makeItHappen();         
    }
    
    private void constructTheModel() {
        qdv_Data = new QuantitativeDataVariable();
        qdv_Data = normProb_Model.getData();
        title2Text = new Text (60, 45, " Normal Prob Plot ");
        qdv_NormalScores = new QuantitativeDataVariable();
        qdv_NormalScores = normProb_Model.getNormalScores();  
        
        nDataPoints = qdv_Data.getLegalN();
        dataArray = new double[nDataPoints];
        dataArray = qdv_Data.getTheDataSorted();
        adStats = new double[3];
        adStats = qdv_Data.getADStats();
        normalScoresArray = new double[nDataPoints];
        normalScoresArray = qdv_NormalScores.getTheDataSorted();

        dataLabel = qdv_Data.getTheDataLabel();
        normalScoresLabel = qdv_Data.getTheDataLabel();
        
        String graphsCSS = getClass().getResource("/css/Graphs.css").toExternalForm();

        graphCanvas = new Canvas(600, 600);
        gc = graphCanvas.getGraphicsContext2D();        
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

    public void setUpUI() {
        String title2String;
        title1Text = new Text(50, 25, " Normal Probability Plot ");
        title1Text.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        title2Text.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
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
    
    private void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        graphCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        graphCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));

      
        for (int iChex = 0; iChex < nCheckBoxes; iChex++) {
            
            scatterPlotCheckBoxes[iChex].translateXProperty()
                                        .bind(graphCanvas.widthProperty()
                                        .divide(250.0)
                                        .multiply(5 * iChex)
                                        .subtract(50.0));
        }
     
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        String graphsCSS = getClass().getResource("/css/Graphs.css").toExternalForm();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(/* checkBoxRow, */ title1Text, title2Text, xAxis, yAxis, graphCanvas);
        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }

    
    public void doTheGraph()
    {    
        double text1Width = title1Text.getLayoutBounds().getWidth();
        double text2Width = title2Text.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        
        AnchorPane.setTopAnchor(checkBoxRow, 0.01 * tempHeight);
        AnchorPane.setLeftAnchor(checkBoxRow, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(checkBoxRow, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(checkBoxRow, 0.95 * tempHeight);
       
        AnchorPane.setTopAnchor(title1Text, 0.06 * tempHeight);
        AnchorPane.setLeftAnchor(title1Text, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(title1Text, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(title1Text, 0.10 * tempHeight);
                
        AnchorPane.setTopAnchor(title2Text, 0.11 * tempHeight);
        AnchorPane.setLeftAnchor(title2Text, txt2Edge * tempWidth);
        AnchorPane.setRightAnchor(title2Text, txt2Edge * tempWidth);
        AnchorPane.setBottomAnchor(title2Text, 0.2 * tempHeight);
        
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
            //System.out.println("278 np_View, dataArray/NormalScoresArray = " + dataArray[i] + " / " + normalScoresArray[i]);
            double xx = xAxis.getDisplayPosition(dataArray[i]);
            double yy = yAxis.getDisplayPosition(normalScoresArray[i]);
            gc.fillOval(xx - 4, yy - 4, 8, 8); //  0.5*radius to get dot to center
        }
        
        if (npModelThisTime.equals("SimpleRegression") || npModelThisTime.equals("UnivExploration")) {
            adString = "Anderson-Darling = " + String.format("%5.3f", adStats[1]);
            adPValue = "pValue = " + String.format("%5.3f", adStats[2]);
            gc.fillText(adString, 10, 10);
            gc.fillText(adPValue, 75, 25);
        }
    }
    
    private void makeTheCheckBoxes() {
        nCheckBoxes = 3;
                
        // Determine which graphs are initially shown
        checkBoxSettings = new boolean[nCheckBoxes];
        checkBoxSettings[0] = false;    //  Best fit line
        checkBoxSettings[1] = false;    //  Outliers
        checkBoxSettings[2] = false;   //  Influential points
        
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
                
                switch (daID) {    
                    case " Best Fit Line ":
                        checkBoxSettings[0] = (checkValue == true);
                        doTheGraph();
                        break;
                        
                    case " Outliers ":  
                        checkBoxSettings[1] = (checkValue == true);
                        doTheGraph();
                        break;

                    case " Influential points ":
                        checkBoxSettings[2] = (checkValue == true);
                        doTheGraph();
                        break;
                        
                    default:
                        System.out.println("YIKES!!!  Best fit checkbox failure!!!");
                        System.exit(0);
                }

            }); //  end setOnAction
        }  
        
        checkBoxRow.getChildren().addAll(scatterPlotCheckBoxes);
    }
    
    private void constructDataArray()
    {
        xDataMin = xDataMax = dataArray[0];
        yDataMin = yDataMax = normalScoresArray[0];

        for (int iRow = 0; iRow < nDataPoints; iRow++)
        {
            // System.out.println("\n 201 QQV iRow = " + iRow);
            double tempDoubleX = dataArray[iRow];
            double tempDoubleY = normalScoresArray[iRow];
  
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

   public Pane getTheContainingPane() { return theContainingPane; }  
    
}