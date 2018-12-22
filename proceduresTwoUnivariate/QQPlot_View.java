/**************************************************
 *                   QQPlot_View                  *
 *                    06/04/18                    *
 *                      03:00                     *
 *************************************************/
/**************************************************
* 
*  A clearly stated algorithm for the general QQ plot is VERY     *
*    difficult to find.  The algorithem coded here is from        *
*   Chambers, J. M., et al.  (1983) Graphical methods for Data    *
*   Analysis.  Duxbury Pres: Boston.  Chapter 3.                  *
*                                                                 *
******************************************************************/
package proceduresTwoUnivariate;

import genericClasses.DragableAnchorPane;
import genericClasses.QuantitativeDataVariable;
import genericClasses.Scatterplot_View;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import java.util.ArrayList;
import javafx.scene.control.CheckBox;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import t_Procedures.*;

public class QQPlot_View extends Scatterplot_View
{
    // POJOs
    boolean dragging;
    boolean[] checkBoxSettings;
    
    int nOneDataPoints, nTwoDataPoints, nCheckBoxes, smallerN, largerN;
    
    String dataOneLabel, dataTwoLabel;
    
    Canvas graphCanvas;
    GraphicsContext gc; // Required for drawing on the Canvas

    double[] dataArrayOne, dataArrayTwo, quantSmall, quantLarge, xSmaller, 
             xLarger, slope, intercept,  tempLarger;

    AnchorPane anchorPane;    
    final String[] scatterPlotCheckBoxDescr = {" Best Fit Line ", 
                                           " Outliers ", 
                                           " Influential points "}; 
    // My classes
    Explore_2Ind_Dashboard compare2Ind_Dashboard;
    Indep_t_Dashboard independent_t_Dashboard;
    AnchorPane checkBoxRow;
    Pane theContainingPane;
    QuantitativeDataVariable qdv_DataOne, qdv_DataTwo;
    ArrayList<QuantitativeDataVariable> allTheQDVs;
    QQPlot_Model qqPlot_Model;
    Line line;
    Text title1Text, title2Text;
    CheckBox[] scatterPlotCheckBoxes;


    public QQPlot_View(QQPlot_Model qqPlot_Model, Explore_2Ind_Dashboard compare2Ind_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) 
    {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        graphCanvas = new Canvas(initWidth, initHeight); 
        gc = graphCanvas.getGraphicsContext2D();
        this.qqPlot_Model = qqPlot_Model;
        this.compare2Ind_Dashboard = compare2Ind_Dashboard;
        allTheQDVs = new ArrayList<>();
        allTheQDVs = qqPlot_Model.getAllTheUDMs();
        
        qdv_DataOne = new QuantitativeDataVariable();
        qdv_DataOne = allTheQDVs.get(1);
        nOneDataPoints = qdv_DataOne.getLegalN();
        dataArrayOne = new double[nOneDataPoints];
        dataArrayOne = qdv_DataOne.getTheDataSorted();        
        dataOneLabel = qdv_DataOne.getTheDataLabel();
        
        qdv_DataTwo = new QuantitativeDataVariable();
        qdv_DataTwo = allTheQDVs.get(2); 
        nTwoDataPoints = qdv_DataTwo.getLegalN();
        dataArrayTwo = new double[nTwoDataPoints];
        dataArrayTwo = qdv_DataTwo.getTheDataSorted();
        dataTwoLabel = qdv_DataTwo.getTheDataLabel();
        makeTheCheckBoxes();    
        makeItHappen();         
        if (nOneDataPoints == nTwoDataPoints) {
            //System.out.println("qq plot, 109 -- doEqualSizeProcedure()");
            doEqualSizeProcedure(); 
        } else {
            //System.out.println("qq plot, 113 -- doUnEqualSizeProcedure()");
            doUnEqualSizeProcedure(); 
        }
    }


    public QQPlot_View(QQPlot_Model qqPlot_Model, Indep_t_Dashboard independent_t_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) 
    {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.qqPlot_Model = qqPlot_Model;
        this.independent_t_Dashboard = independent_t_Dashboard;
        graphCanvas = new Canvas(initWidth, initHeight); 
        gc = graphCanvas.getGraphicsContext2D();        
        allTheQDVs = new ArrayList<>();
        allTheQDVs = qqPlot_Model.getAllTheUDMs();
        
        qdv_DataOne = new QuantitativeDataVariable();
        qdv_DataOne = allTheQDVs.get(1);
        nOneDataPoints = qdv_DataOne.getLegalN();
        dataArrayOne = new double[nOneDataPoints];
        dataArrayOne = qdv_DataOne.getTheDataSorted();        
        dataOneLabel = qdv_DataOne.getTheDataLabel();
        
        qdv_DataTwo = new QuantitativeDataVariable();
        qdv_DataTwo = allTheQDVs.get(2); 
        nTwoDataPoints = qdv_DataTwo.getLegalN();
        dataArrayTwo = new double[nTwoDataPoints];
        dataArrayTwo = qdv_DataTwo.getTheDataSorted();
        dataTwoLabel = qdv_DataTwo.getTheDataLabel();
        makeTheCheckBoxes();    
        makeItHappen(); 
        
    if (nOneDataPoints == nTwoDataPoints) {
        //System.out.println("qq plot, 109 -- doEqualSizeProcedure()");
        doEqualSizeProcedure(); 
    } else {
        //System.out.println("qq plot, 113 -- doUnEqualSizeProcedure()");
        doUnEqualSizeProcedure(); 
    }

    
// -------------------------------------------------------------------------
        
        
        String graphsCSS = getClass().getResource("/css/Graphs.css").toExternalForm();
        
        
    }
    
    private void doEqualSizeProcedure() {
        //System.out.println("qq 128, doEqualSizeProcedure()");
        smallerN = nOneDataPoints;  // These should be equal; distinction is
        largerN = nTwoDataPoints;   // for the plotting
        xSmaller = new double[smallerN];  
        xLarger = new double[smallerN];   
        System.arraycopy(dataArrayOne, 0, xSmaller, 0, nOneDataPoints);
        System.arraycopy(dataArrayTwo, 0, xLarger, 0, nTwoDataPoints);
    }
    
    private void doUnEqualSizeProcedure() {
        //System.out.println("qq 138, doUnEqualSizeProcedure()");
        int smallSize, leftQIndex, indexLeftInterval;
        double oneThird = 1.0 / 3.0;
        double smallDenom;
        if (nOneDataPoints < nTwoDataPoints) {
            smallerN = nOneDataPoints;  // These should be equal; distinction is
            largerN = nTwoDataPoints;   // for the plotting
            xSmaller = new double[smallerN]; 
            xLarger = new double[smallerN]; 
            System.arraycopy(dataArrayOne, 0, xSmaller, 0, nOneDataPoints);
            // tempLarger holds largerN points; xLarger will eventually
            // hold only smallerN points for graphing 
            tempLarger = new double[nTwoDataPoints];
            System.arraycopy(dataArrayTwo, 0, tempLarger, 0, nTwoDataPoints);
            
        } else {
            smallerN = nTwoDataPoints;  // These should be equal; distinction is
            largerN = nOneDataPoints;   // for the plotting
            xSmaller = new double[smallerN];  // These should be
            xLarger = new double[smallerN];   // equal in size
            System.arraycopy(dataArrayTwo, 0, xSmaller, 0, nTwoDataPoints);
            // tempLarger holds largerN points; xLarger will eventually
            // hold only smallerN points for graphing 
            tempLarger = new double[nOneDataPoints];
            System.arraycopy(dataArrayOne, 0, tempLarger, 0, nOneDataPoints);
        }
        
        /********************************************************************
        *   At this point xSmaller should be finished, but not xLarger.     *
        *   Now get percentiles for the xSmaller and xLarger values, using  *
        *   definition 8 from Hyndman, R., & Fan, Y.  Sample quantiles in   *
        *   Statistical Packages (1996).  The American Statistician,        * 
        *   50(4): 361-5.                                                   *
        ********************************************************************/
        // quantiles for smaller data set
        smallDenom = smallerN + oneThird;
        quantSmall = new double[smallerN];
        for (int qth = 0; qth < smallerN; qth++) {
            quantSmall[qth] = ((qth+1) - oneThird) / smallDenom;
        }       
        // quantiles for larger data set
        double largeDenom = largerN + oneThird;
        quantLarge = new double[largerN];
        slope = new double[largerN];
        intercept = new double[largerN];
        for (int qth = 0; qth < largerN; qth++) {
            quantLarge[qth] = ((qth+1) - oneThird) / largeDenom;
        }
        
        /********************************************************************
        *  Now prepare for estimation of xLarge values via interpolation    *
        *  We will be estimating xLarge from the xLarge percentiles.            *
        ********************************************************************/
        slope = new double[largerN - 1];
        for (int qth = 0; qth < largerN - 1; qth++) {
            double tempNumer = tempLarger[qth + 1] - tempLarger[qth];
            double tempDenom = quantLarge[qth + 1] - quantLarge[qth];
            slope[qth] = (tempNumer / tempDenom);
            intercept[qth] = tempLarger[qth] - slope[qth] * quantLarge[qth];
        }
        
        /********************************************************************
        *            Estimate the xSmall quantiles for xLarge               *
        ********************************************************************/        

        for (int smallQuant = 0; smallQuant < smallerN; smallQuant++) {
            //  Find the left end of the interval where the large quantile
            //  crosses the small quantile
            leftQIndex = 0;
            for (int smallQIndex = 0; smallQIndex < smallerN - 1; smallQIndex++) {
                for (int largeQIndex = 0; largeQIndex < largerN - 1; largeQIndex++) {
                    if ((quantLarge[largeQIndex] <= quantSmall[smallQIndex])
                       && ((quantLarge[largeQIndex + 1] > quantSmall[smallQIndex])))  {
                        leftQIndex = smallQIndex;
                    }
                    xLarger[smallQIndex] = slope[leftQIndex] * quantSmall[leftQIndex] + intercept[leftQIndex]; 
                }
             }
        } 
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
        title1Text = new Text(50, 25, " QQ Plot ");
        title2Text = new Text (60, 45, " QQ Plot ");
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
                           .addAll(checkBoxRow, title1Text, title2Text, xAxis, yAxis, graphCanvas);
        
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
        //System.out.println("qq 351, nOneDataPoints = " + nOneDataPoints);
        

        for (int i = 0; i < smallerN; i++)
        {
            double xx = xAxis.getDisplayPosition(dataArrayOne[i]);
            double yy = yAxis.getDisplayPosition(dataArrayTwo[i]);
            gc.fillOval(xx - 4, yy - 4, 8, 8); //  0.5*radius to get dot to center
        }
        
        line = new Line();
        double x1 = xAxis.getDisplayPosition(xSmaller[0]);
        double y1 = yAxis.getDisplayPosition(xSmaller[0]);
        double x2 = xAxis.getDisplayPosition(xSmaller[smallerN - 1]);
        double y2 = yAxis.getDisplayPosition(xSmaller[smallerN - 1]);
        gc.setLineWidth(2);
        gc.setStroke(Color.TOMATO);
        gc.strokeLine(x1, y1, x2, y2);  

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
        xDataMin = xDataMax = xSmaller[0];
        yDataMin = yDataMax = xLarger[0];
        for (int iRow = 0; iRow < smallerN; iRow++)
        {
            double tempDoubleX = xSmaller[iRow];
            double tempDoubleY = xLarger[iRow];
  
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

        xDataMin = Math.min(xDataMin, yDataMin);
        yDataMin = xDataMin;
        xDataMax = Math.max(xDataMax, yDataMax);
        
        xRange = xDataMax - xDataMin;
        yRange = yDataMax - yDataMin;      
    }

   public Pane getTheContainingPane() { return theContainingPane; }  
    
}