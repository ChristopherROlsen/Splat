/**************************************************
 *                    DotPlotView                 *
 *                     12/28/18                   *
 *                      18:00                     *
 *************************************************/

package proceduresOneUnivariate;

import genericClasses.JustAnAxis;
import genericClasses.DragableAnchorPane;
import dataObjects.UnivariateContinDataObj;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.layout.*;
import superClasses.*;

public class DotPlot_View extends Scatterplot_View {

    // POJOs
    boolean dragging, canFit;
    
    int nBins, nLegalDataPoints, nMajorIntervals, nSquaresThisFreq, nCheckBoxes,
        possibleBarsIndex, nMajorTickPositions, intervalsPerMajorTick, maximumFreq, 
        orderOfMag;
    int[] frequencies;
    
    final int nBinSizes = 9;
    final int[] possibleBarsPerTik = {1, 2, 4, 5, 10, 20, 40, 50, 100};

    double yMin, yMax, initial_xMin, initial_xMax, initial_xRange, xMin, xMax,
           pixWidthPane, pixHeightPane, pixBinSize, ww, hh, startSquaresAt,
           univDataMin, univDataMax, maxFreq, pixBinWidth, pixSpaceSize,
           startSquaresAtmaximumFreq, leftEndOfBin, rightEndOfBin,
           xScale_Range, yScale_Range,
           pixSmallestSpace, pixSquareSize, pixSquarePlusSpace,
           minMajorTick, maxMajorTick, majorTickRange, binWidth,
           majorTikInterval, firstBin, lastBin, rangeOfBins, m, b;
    double[] binRange, univDataArray; 
    String[] str_Labels;  // Why array?   
    
    Toolkit tk;
    
    // My classes
    DotPlot_Model dotPlot_Model;  
    Exploration_Dashboard explore_Dashboard;      
    UnivariateContinDataObj ucdo;    
    
    // POFOs / FX
    AnchorPane checkBoxRow;
    MouseEvent mickeyEvent;
    Pane theContainingPane;
    StackPane dotPlot;  // Why stack pane?
    Text txtTitle1, txtTitle2;    
  
    int startBins, missingData, minBins, maxBins, n, numVals, numBins;
    int[] freq;
    double min, max, range, minY;
    double[] workingData;
    double [][] limit;
    
    MenuBar mainMenu;
    Menu setupMenu;

    
   //  public DotPlot_View() { }

    public DotPlot_View(DotPlot_Model dotPlot_Model, Exploration_Dashboard explore_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);    
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        
        this.dotPlot_Model = dotPlot_Model;
        this.explore_Dashboard = explore_Dashboard;
        univDataArray = dotPlot_Model.getQDV_Model().getLegalDataAsDoubles();
        nLegalDataPoints = dotPlot_Model.getQDV_Model().getLegalN();

        String graphsCSS = getClass().getResource("/css/Graphs.css").toExternalForm();
        dragableAnchorPane = new DragableAnchorPane();
        graphCanvas = new Canvas(600, 600);
        gc = graphCanvas.getGraphicsContext2D();
        
        showDotPlot();    
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
        initial_xMin = dotPlot_Model.getQDV_Model().getMinValue();
        initial_xMax = dotPlot_Model.getQDV_Model().getMaxValue();
        initial_xRange = initial_xMax - initial_xMin;
        
        xMin = initial_xMin;
        xMax = initial_xMax;
        xRange = initial_xRange;
    
        // This constant controls the rate of scale change when dragging
        deltaX = 0.005 * xRange;
        xAxis = new JustAnAxis(xMin, xMax);
        xAxis.setSide(Side.BOTTOM);
        xAxis.setLabel(dotPlot_Model.getQDV_Model().getDataUnits());
        
        newX_Lower = xMin; 
        newX_Upper = xMax;
        xAxis.setLowerBound(newX_Lower ); 
        xAxis.setUpperBound(newX_Upper );
        
        establishBinSizes(possibleBarsIndex);  // <-------------
        
        yMin = 0;
        yMax = maximumFreq;
        newY_Lower = yMin;
        newY_Upper = yMax;
        
        yAxis = new JustAnAxis(yMin, yMax); 
        yAxis.forceLowScaleEndToBe(0.5);
        yAxis.setVisible(true);
        if (yAxis.getHasForcedLowScaleEnd()) {
            newY_Lower = yAxis.getForcedLowScaleEnd();
        }
        yAxis.setLowerBound(newY_Lower ); 
        yAxis.setUpperBound(newY_Upper );
        yRange = yMax - yMin;      
        deltaY = 0.005 * yRange;

        yAxis.setSide(Side.LEFT);
        yAxis.setLabel(" Frequency ");
    }
    
    private void constructDataArray()
    {
        /*
            What does this do generically?
        */
    }
    
    public void setUpUI() {
        txtTitle1 = new Text(50, 25, " Dot plot ");
        txtTitle2 = new Text (60, 45, dotPlot_Model.getSubTitle());
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
    
    private void setUpAnchorPane() {
        graphCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        graphCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));

        dragableAnchorPane.makeDragable();
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

            if (binFrequency > 0) {
                for (int ithDot = 1; ithDot <= binFrequency; ithDot++) {
                    pixSquareLeft = xAxis.getDisplayPosition(binRange[0]);
                    pixSquareTop = yAxis.getDisplayPosition(ithDot);
                    hh = yAxis.getDisplayPosition(0.0) - yAxis.getDisplayPosition(1.0); //  Unit height
                    ww = xAxis.getDisplayPosition(binRange[1]) - xAxis.getDisplayPosition(binRange[0]);
                    hh = Math.min(hh, ww);
                    gc.fillOval(pixSquareLeft, pixSquareTop - 0.5 * hh, hh, hh);
                }
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
        double tempMin = dotPlot_Model.getQDV_Model().getMinValue();
        double tempMax = dotPlot_Model.getQDV_Model().getMaxValue();
        // --------------------------------------------------
        nMajorIntervals = (int)Math.floor(majorTickRange / majorTikInterval + .001) + 1; 
        nBins = (int)Math.floor(nMajorIntervals * intervalsPerMajorTick + .001);
        frequencies = new int[nBins + 1]; //  nBins+1 in case data is on right end of max interval

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
    
    private void showDotPlot() {
        
        mainMenu = new MenuBar();
        mainMenu.setStyle("-fx-background-color: #ffffff;");

        setupMenu = new Menu("Plot Options");
        setupMenu.setStyle("-fx-background-color: #ff69b4; -fx-text-color: black");
        MenuItem moreBins = new MenuItem("More Bins");
        MenuItem fewerBins = new MenuItem("Fewer Bins");
        MenuItem autoBins = new MenuItem("Auto Bins");

        setupMenu.getItems().addAll(moreBins, fewerBins, autoBins);
        mainMenu.getMenus().addAll(setupMenu);

        VBox mainPanel = new VBox();
        mainPanel.getStyleClass().add("hbox");
        mainPanel.setAlignment(Pos.CENTER);
        mainPanel.getChildren().addAll(mainMenu, dragableAnchorPane);
        Scene scene = new Scene(mainPanel);
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
   
   public Pane getTheContainingPane() { return theContainingPane; }

}