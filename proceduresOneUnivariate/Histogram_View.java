/**************************************************
 *                  Histogram_View                *
 *                     12/25/18                   *
 *                      15:00                     *
 *************************************************/
package proceduresOneUnivariate;

import genericClasses.JustAnAxis;
import genericClasses.DragableAnchorPane;
import dataObjects.UnivariateContinDataObj;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import java.awt.Toolkit;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class Histogram_View extends Region {
    // POJOs
    boolean dragging, canFit;
    
    int nBins, nLegalDataPoints, nMajorIntervals, nSquaresThisFreq, nCheckBoxes,
        nMajorTickPositions, intervalsPerMajorTick, maximumFreq, orderOfMag,
        possibleBarsIndex;
    final int nBinSizes = 9;
    
    int[] frequencies;
    final int[] possibleBarsPerTik = {1, 2, 4, 5, 10, 20, 40, 50, 100};

    double initHoriz, initVert, initWidth, initHeight, yMin, yMax, yRange, 
           initial_xMin, initial_xMax, initial_xRange, xMin, xMax, xRange,
           pixWidthPane, pixHeightPane, pixBinSize, ww, hh, startSquaresAt,
           univDataMin, univDataMax, maxFreq, pixBinWidth, pixSpaceSize,
           startSquaresAtmaximumFreq, leftEndOfBin, rightEndOfBin,
           xPix_InitialPress, yPix_InitialPress,
           xPix_MostRecentDragPoint, yPix_MostRecentDragPoint,
           newX_Lower, newX_Upper, newY_Lower, newY_Upper, deltaX, deltaY,
           dispLowerBound, dispUpperBound, xScale_Range, yScale_Range,
           pixSmallestSpace, pixSquareSize, pixSquarePlusSpace,
           minMajorTick, maxMajorTick, majorTickRange, binWidth,
           majorTikInterval, firstBin, lastBin, rangeOfBins, m, b;
    double[] binRange, univDataArray;
    
    String descriptionOfVariable;
    String[] label;  // Why array?  

    // My classes
    DragableAnchorPane dragableAnchorPane;
    Exploration_Dashboard explore_Dashboard;
    Histogram_Model histogram_Model;
    JustAnAxis xAxis, yAxis;    
    UnivariateContinDataObj univDataObj;     
    
    // POJOs / FX
    AnchorPane checkBoxRow;
    AnchorPane anchorPane;
    Canvas graphCanvas;
    GraphicsContext gc; // Required for drawing on the Canvas
    MouseEvent mickeyEvent;
    Pane theContainingPane;
    StackPane histogram;  // Stack pane so things can be added?
    Text txtTitle1, txtTitle2;  
    Toolkit tk;

    int startBins, missingData, minBins, maxBins, n, numVals, numBins;
    int[] freq;
    double min, max, range, minY;
    double[] workingData;
    double [][] limit;
    
    MenuBar mainMenu;
    Menu fileMenu, setupMenu;

    
    public Histogram_View() { }

    public Histogram_View(Histogram_Model histogram_Model, Exploration_Dashboard explore_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        
        this.histogram_Model = histogram_Model;
        this.explore_Dashboard = explore_Dashboard;
        univDataArray = histogram_Model.getQDV().getLegalDataAsDoubles();
        nLegalDataPoints = histogram_Model.getQDV().getLegalN();
        descriptionOfVariable = histogram_Model.getDescriptionOfVariable();
        String graphsCSS = getClass().getResource("/css/Graphs.css").toExternalForm();

        graphCanvas = new Canvas(600, 600);
        gc = graphCanvas.getGraphicsContext2D();
        
        showHistogram();    
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
    
    public void initializeGraphParameters() {  
        constructDataArray();
        nCheckBoxes = 3;
        possibleBarsIndex = 3;
        initial_xMin = histogram_Model.getQDV().getMinValue();
        initial_xMax = histogram_Model.getQDV().getMaxValue();
        initial_xRange = initial_xMax - initial_xMin;
        
        xMin = initial_xMin;
        xMax = initial_xMax;
        xRange = initial_xRange;
    
        // This constant controls the rate of scale change when dragging
        deltaX = 0.005 * xRange;
        xAxis = new JustAnAxis(xMin, xMax);
        xAxis.setSide(Side.BOTTOM);
        xAxis.setLabel(histogram_Model.getQDV().getDataLabel());
        
        newX_Lower = xMin; 
        newX_Upper = xMax;
        xAxis.setLowerBound(newX_Lower ); 
        xAxis.setUpperBound(newX_Upper );
        
        establishBinSizes(possibleBarsIndex);
        
        yMin = 0;
        yMax = maximumFreq;
        newY_Lower = yMin;
        newY_Upper = yMax;
        
        yAxis = new JustAnAxis(yMin, yMax); 
        yAxis.forceLowScaleEndToBe(0.0);
        yAxis.setVisible(true);

        yAxis.setLowerBound(newY_Lower ); 
        yAxis.setUpperBound(newY_Upper );
        yRange = yMax - yMin;      
        deltaY = 0.005 * yRange;

        yAxis.setSide(Side.LEFT);
        yAxis.setLabel(" Frequency ");
    }
    

    private void constructDataArray() { }   // ???  Dump?

    
    public void setUpUI() {
        String title2String;
        txtTitle1 = new Text(50, 25, " Histogram ");
        txtTitle2 = new Text (60, 45, descriptionOfVariable);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
    
    private void setUpAnchorPane() {

        dragableAnchorPane = new DragableAnchorPane();
        graphCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        graphCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
    
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        String graphsCSS = getClass().getResource("/css/Graphs.css").toExternalForm();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    

        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(mainMenu, txtTitle1, txtTitle2, xAxis, yAxis, graphCanvas);
        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
  
    public void doTheGraph() {
        binRange = new double[2];
        double pixSquareLeft, pixSquareTop, binHeight;
        double leftEdgeThisBin, pixMiddleThisBin, basePixAtFreqZero;
        int binFrequency;  
        String tempString;
        
        double text1Width = txtTitle1.getLayoutBounds().getWidth();
        double text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        
        mainMenu.setMaxWidth(tempWidth/4.0);
        AnchorPane.setTopAnchor(mainMenu, 0.01 * tempHeight);
        AnchorPane.setLeftAnchor(mainMenu, 0.01 * tempWidth);
        AnchorPane.setRightAnchor(mainMenu, 0.25 * tempWidth);
        AnchorPane.setBottomAnchor(mainMenu, 0.95 * tempHeight);

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
        
        gc.clearRect(0, 0 , graphCanvas.getWidth(), graphCanvas.getHeight()); 
        
        pixWidthPane = graphCanvas.getWidth();
        pixHeightPane = graphCanvas.getHeight();

        binRange = getBinRange(0);
        pixBinWidth = xAxis.getDisplayPosition(binRange[1]) - xAxis.getDisplayPosition(binRange[0]);
        basePixAtFreqZero = yAxis.getDisplayPosition(0.0);

        for (int ithBin = 0; ithBin < nBins; ithBin++ ) {

            binFrequency = frequencies[ithBin];
                
            if (binFrequency > 0) {

                binRange = getBinRange(ithBin);               
                leftEdgeThisBin = xAxis.getDisplayPosition(binRange[0]);
                pixMiddleThisBin = leftEdgeThisBin + 0.5 * pixBinWidth;
                pixSpaceSize = (pixBinWidth - nSquaresThisFreq) / (nSquaresThisFreq + 1.0);
                pixSquarePlusSpace = pixSpaceSize + pixSquareSize;
                              
                int nSquares = nSquaresThisFreq;
                int nSpaces = nSquares + 1;
                
                //  Starting point for first square
                startSquaresAt = (pixMiddleThisBin - 0.5 * pixSpaceSize) - (0.5 * nSquares - 1) * pixSpaceSize - 0.5 * pixSquareSize;

            } //    end binFreq > 0
            //  Does this do anything?

            gc.setFill(Color.GREEN);
            if (binFrequency > 0) {
                pixSquareLeft = xAxis.getDisplayPosition(binRange[0]);
                pixSquareTop = yAxis.getDisplayPosition(binFrequency);

                hh = yAxis.getDisplayPosition(0.0) - yAxis.getDisplayPosition(binFrequency);
                ww = xAxis.getDisplayPosition(binRange[1]) - xAxis.getDisplayPosition(binRange[0]);
                gc.fillRect(pixSquareLeft, pixSquareTop, ww, hh);
            }

        }   // for iBin
    }   //  end doThePlot 
    
    public void establishBinSizes(int possBarsIndex) { 

        intervalsPerMajorTick = possibleBarsPerTik[possBarsIndex];

        nMajorTickPositions = xAxis.getNMajorTix(); 
        minMajorTick = (xAxis.getMajorTickMarkPositions().get(0)).doubleValue();
        maxMajorTick = (xAxis.getMajorTickMarkPositions().get(nMajorTickPositions - 1)).doubleValue();
        majorTikInterval = (xAxis.getMajorTickMarkPositions().get(1)).doubleValue()
                                    - (xAxis.getMajorTickMarkPositions().get(0)).doubleValue();   

        minMajorTick -= majorTikInterval;
        maxMajorTick += majorTikInterval;
        majorTickRange = maxMajorTick - minMajorTick;
        binWidth = majorTikInterval / intervalsPerMajorTick; 

        // --------------------------------------------------
        double tempMin = histogram_Model.getQDV().getMinValue();
        double tempMax = histogram_Model.getQDV().getMaxValue();
        // --------------------------------------------------
        nMajorIntervals = (int)Math.floor(majorTickRange / majorTikInterval + .001) + 1; 
        nBins = (int)Math.floor(nMajorIntervals * intervalsPerMajorTick + .001);
        frequencies = new int[nBins + 1]; //  nBins+1 in case data is on right end of max interval
        
        //  Frequencies for bin -- these are 100 bins per difference in 
        //  adjacent major tix.  Bin sizes will be adjusted later by 
        //  combining adjacent 100-per bins.
        
        firstBin = minMajorTick;
        lastBin = minMajorTick + nBins * binWidth;
        rangeOfBins = lastBin - firstBin;
        m = nBins / rangeOfBins;
        b = - nBins * firstBin / rangeOfBins;
        
        for (int ith_Data_Point = 0; ith_Data_Point < nLegalDataPoints; ith_Data_Point++)
        {
            int ith_Bin = (int)Math.floor(m * univDataArray[ith_Data_Point] + b);  
            frequencies[ith_Bin] = frequencies[ith_Bin] + 1;
        }
        
        maximumFreq = 0;
        for (int ith_Bin = 0; ith_Bin < nBins; ith_Bin++)
        {
            if (frequencies[ith_Bin] > maximumFreq)
                maximumFreq = frequencies[ith_Bin];
        }
        
        maximumFreq += 1.0;  // Safety pad
    }
    
    // The binRange is needed by the View for graphing the bars
    public double[] getBinRange(int whichBin) {
        binRange = new double[2];
        leftEndOfBin = minMajorTick + whichBin * binWidth;
        rightEndOfBin = leftEndOfBin + binWidth;
        binRange[0] = leftEndOfBin;
        binRange[1] = rightEndOfBin;
        return binRange;       
    } 
    
    private void showHistogram() {
        
        mainMenu = new MenuBar();
        mainMenu.setStyle("-fx-background-color: #ffffff;");

        setupMenu = new Menu("Plot Options");
        setupMenu.setStyle("-fx-background-color: #ff69b4; -fx-text-color: black");
        MenuItem moreBins = new MenuItem("More Bins");
        MenuItem fewerBins = new MenuItem("Fewer Bins");
        MenuItem autoBins = new MenuItem("Auto Bins");

        setupMenu.getItems().addAll(moreBins, fewerBins, autoBins);
        mainMenu.getMenus().add(setupMenu);

        VBox mainPanel = new VBox();
        mainPanel.getStyleClass().add("hbox");
        mainPanel.setAlignment(Pos.CENTER);
        ScrollPane mainScroll = new ScrollPane(/* mainPanel */);
        Scene scene = new Scene(mainScroll);
        String css = getClass().getResource("/css/Histogram.css").toExternalForm();
        scene.getStylesheets().add(css);
        
        moreBins.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                if (possibleBarsIndex < nBinSizes - 1) {
                    possibleBarsIndex++;
                    establishBinSizes(possibleBarsIndex); 
                    doTheGraph();
                }
            }
        });

        fewerBins.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                if (possibleBarsIndex > 0) {
                    possibleBarsIndex--;
                    establishBinSizes(possibleBarsIndex); 
                    doTheGraph();
                }
            }
        });

        autoBins.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                possibleBarsIndex = 3;
                establishBinSizes(possibleBarsIndex); 
                doTheGraph();
            }
        });

    }    
    

    
public void setHandlers(){
    
        //graphCanvas.setOnMousePressed(dotPlotMouseHandler); 
        
        // xAxis.setOnMouseClicked(xAxisMouseHandler); 
        xAxis.setOnMouseDragged(xAxisMouseHandler); 
        // xAxis.setOnMouseEntered(xAxisMouseHandler); 
        // xAxis.setOnMouseExited(xAxisMouseHandler); 
        // xAxis.setOnMouseMoved(xAxisMouseHandler); 
        xAxis.setOnMousePressed(xAxisMouseHandler); 
        xAxis.setOnMouseReleased(xAxisMouseHandler); 
        
        // yAxis.setOnMouseClicked(yAxisMouseHandler); 
        yAxis.setOnMouseDragged(yAxisMouseHandler); 
        // yAxis.setOnMouseEntered(yAxisMouseHandler); 
        // yAxis.setOnMouseExited(yAxisMouseHandler); 
        // yAxis.setOnMouseMoved(yAxisMouseHandler); 
        yAxis.setOnMousePressed(yAxisMouseHandler); 
        yAxis.setOnMouseReleased(yAxisMouseHandler); 

    }
    
    EventHandler<MouseEvent> xAxisMouseHandler = new EventHandler<MouseEvent>() 
    {
        @Override
        public void handle(MouseEvent mouseEvent) 
        {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) 
            { 
                xPix_InitialPress = mouseEvent.getX();  
                xPix_MostRecentDragPoint = mouseEvent.getX();
                dragging = false;   
            }
            else 
            if(mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED)
            {
                if (dragging == true)
                {
                    xAxis.setLowerBound(newX_Lower ); 
                    xAxis.setUpperBound(newX_Upper );
                    xRange = newX_Upper - newX_Lower;
                    deltaX = 0.005 * xRange;
                    dragging = false;
                }
            }
            else 
            if(mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED)
            {
                dragging = true;
                double xPix_Dragging = mouseEvent.getX();
                newX_Lower = xAxis.getLowerBound();
                newX_Upper = xAxis.getUpperBound(); 

                dispLowerBound = xAxis.getDisplayPosition(xAxis.getLowerBound());
                dispUpperBound = xAxis.getDisplayPosition(xAxis.getUpperBound());

                double frac = mouseEvent.getX() / dispUpperBound;
                
                // Still dragging right
                if((xPix_Dragging > xPix_InitialPress) && (xPix_Dragging > xPix_MostRecentDragPoint))
                {    
                    // Which half of scale?
                    if (frac > 0.5) //  Right of center -- OK
                    {
                        newX_Upper = xAxis.getUpperBound() - deltaX;
                    }
                    else  // Left of Center
                    {
                        newX_Lower = xAxis.getLowerBound() - deltaX;
                    }
                }
                else 
                if ((xPix_Dragging < xPix_InitialPress) && (xPix_Dragging < xPix_MostRecentDragPoint))
                {   // On right, dragging left
                    if (frac < 0.5) // Left of center
                    {
                        newX_Lower = xAxis.getLowerBound() + deltaX;
                    }
                    else    // Right of center -- OK
                    {
                        newX_Upper = xAxis.getUpperBound() + deltaX;
                    }
                }    
                
                //  Make this call in a separate method from the mouseEventHandler?
                
                if (xAxis.getHasForcedLowScaleEnd()) {
                    newX_Lower = xAxis.getForcedLowScaleEnd();
                }
                if (xAxis.getHasForcedHighScaleEnd()) {
                    newX_Upper = xAxis.getForcedHighScaleEnd();                 
                }

                xAxis.setLowerBound(newX_Lower ); 
                xAxis.setUpperBound(newX_Upper );

                dispLowerBound = xAxis.getDisplayPosition(xAxis.getLowerBound());
                dispUpperBound = xAxis.getDisplayPosition(xAxis.getUpperBound());
                xPix_MostRecentDragPoint = mouseEvent.getX();
                doTheGraph();
            }
        }
    };
    
     EventHandler<MouseEvent> yAxisMouseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) { 
                yPix_InitialPress = mouseEvent.getY(); 
                yPix_MostRecentDragPoint = mouseEvent.getY();
                dragging = false;
            }
            else 
            if(mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                if (dragging == true) {
                    yAxis.setLowerBound(newY_Lower ); 
                    yAxis.setUpperBound(newY_Upper );
                    yRange = newY_Upper - newY_Lower;
                    deltaY = 0.005 * yRange;
                    dragging = false;
                }
            }
            else 
            if(mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                dragging = true;
                double yPix_Dragging = mouseEvent.getY();  
                dispLowerBound = yAxis.getDisplayPosition(yAxis.getLowerBound());
                dispUpperBound = yAxis.getDisplayPosition(yAxis.getUpperBound());

                double frac = mouseEvent.getY() / dispLowerBound;
                
                if((yPix_Dragging > yPix_InitialPress) && (yPix_Dragging > yPix_MostRecentDragPoint)) {    
                    if (frac < 0.5) {
                        newY_Upper = yAxis.getUpperBound() + deltaY;
                    } else {
                        newY_Lower = yAxis.getLowerBound() + deltaY;
                    }
                }
                else 
                if ((yPix_Dragging < yPix_InitialPress) && (yPix_Dragging < yPix_MostRecentDragPoint)){  
                    if (frac < 0.5){
                        newY_Upper = yAxis.getUpperBound() - deltaY;
                    }
                    else {
                        newY_Lower = yAxis.getLowerBound() - deltaY;
                    }
                }  
                
                if (yAxis.getHasForcedLowScaleEnd()) {
                    newY_Lower = yAxis.getForcedLowScaleEnd();
                }
                if (yAxis.getHasForcedHighScaleEnd()) {
                    newY_Upper = yAxis.getForcedHighScaleEnd();
                }    
                
                yAxis.setLowerBound(newY_Lower ); 
                yAxis.setUpperBound(newY_Upper ); 
                dispLowerBound = yAxis.getDisplayPosition(yAxis.getLowerBound());
                dispUpperBound = yAxis.getDisplayPosition(yAxis.getUpperBound());
                
                yPix_MostRecentDragPoint = mouseEvent.getY();                
                doTheGraph();
            }
        }
    };      
    
   public Pane getTheContainingPane() { return theContainingPane; }

}