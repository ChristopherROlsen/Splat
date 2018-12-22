/**************************************************
 *           ANOVA2_BoxCircleInterActView         *
 *                  05/15/18                      *
 *                    12:00                       *
 *************************************************/
package ANOVA_Two;

import genericClasses.StringUtilities;
import genericClasses.JustAnAxis;
import genericClasses.DragableAnchorPane;
import genericClasses.UnivariateContinDataObj;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class ANOVA2_BoxCircleInterActView extends Region { 
    // POJOs
    boolean dragging;
    
    int nRowsCat, nColsCat, nLittleSquares, nFactorA_Levels, nFactorB_Levels, 
        nDataPoints, nSquaresRow1, nSquaresRow2;
    int[] whiskerEndRanks;
    
    double  initial_yMin, initial_yMax, initial_yRange, yMin, yMax, yRange,
            xPix_InitialPress, yPix_InitialPress, xPix_MostRecentDragPoint, 
            yPix_MostRecentDragPoint, newX_Lower, newX_Upper, newY_Lower, 
            newY_Upper, deltaX, deltaY, dispLowerBound, dispUpperBound, 
            bottomOfLowWhisker, topOfHighWhisker, tempPos0, tempPos1, 
            bigTickInterval, confidenceLevel, startSquareFactor_1, 
            jumpSquareFactor_1, startSquareFactor_2, jumpSquareFactor_2,
            rvfMinResid, rvfMaxResid, rvfAxisBound, rvfMinMean, rvfMaxMean,
            text1Width, text2Width, initHoriz, initVert, initWidth, initHeight; 
    double[] fiveNumberSummary;

    String  choiceOfPlot, meansOrBars, strTitle1, strTitle2, strResponseVar,
            strFactorA, strFactorB, graphsCSS;
    
    ObservableList<String> preStrTopLabels, preStrLeftLabels, strTopLabels, 
                           strLeftLabels,categoryLabels, factorA_Levels, 
                           factorB_Levels;
    
    // My classes
    ANOVA2_Model anova2Model;
    DragableAnchorPane dragableAnchorPane;
    HorizontalPositioner horizPositioner;
    JustAnAxis yAxis;
    StringUtilities myStringUtilities;
    UnivariateContinDataObj allData_UCDO;
    UnivariateContinDataObj[] arrayOfUCOs;
    
    //  FX objects
/*****************************************************************************
 * GREEN is last b/c color blind folks have trouble with RED / GREEN,        *
 * so hopefully Red and Green should seldom appear.  Probably these colors   *
 * should be arranged so that the most distinguishable ones are early.       *
 ****************************************************************************/
    Color[] graphColors = { Color.BLUE, Color.RED, Color.SADDLEBROWN, 
                            Color.TURQUOISE, Color.BLUEVIOLET, 
                            Color.ALICEBLUE, Color.LIGHTCORAL, Color.GREEN};

    AnchorPane anchorTitleInfo, boxPlotAnchorPane;
    Canvas anova2Canvas;
    CategoryAxis xAxis;
    GraphicsContext anova2GC;
    HBox anova2CategoryBoxes;
    HBox[] squaresNText;
    Line line;
    Pane containingPane;
    Point2D horizPosition;
    Rectangle[] littleSquares;
    Text title1Text, title2Text;
    Text[] textForSquares, littleSquaresText;

    ANOVA2_BoxCircleInterActView(ANOVA2_Model anova2_Model, 
            ANOVA2_Dashboard anova2_Dashboard,
            double placeHoriz, double placeVert,
            double withThisWidth, double withThisHeight) {
        
        this.anova2Model = anova2_Model;
        
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        graphsCSS = getClass().getResource("/css/Graphs.css").toExternalForm();
        containingPane = new Pane();
        
        nFactorA_Levels = anova2_Model.str_ALevels.size();
        nFactorB_Levels = anova2_Model.str_BLevels.size();
        
        categoryLabels = FXCollections.observableArrayList();        
        factorA_Levels = FXCollections.observableArrayList();
        factorB_Levels = FXCollections.observableArrayList();  
        strTopLabels = FXCollections.observableArrayList();
        strLeftLabels = FXCollections.observableArrayList();
        
        preStrTopLabels = anova2Model.getFactorALevels();
        preStrLeftLabels = anova2Model.getFactorBLevels();
        int nTopLabels = preStrTopLabels.size() - 1;
        int nLeftLabels = preStrLeftLabels.size() - 1;
       
        for (int ithTopLabel = 0; ithTopLabel < nTopLabels; ithTopLabel++) {
            strTopLabels.add(preStrTopLabels.get(ithTopLabel + 1)); 
        }
        
        for (int ithLeftLabel = 1; ithLeftLabel < nLeftLabels; ithLeftLabel++) {
            strLeftLabels.add(preStrLeftLabels.get(ithLeftLabel + 1));
        }
        
        categoryLabels.addAll(strTopLabels);
        factorA_Levels.addAll(anova2Model.getFactorALevels());
        factorB_Levels.addAll(anova2Model.getFactorBLevels()); 

        allData_UCDO = anova2Model.getAllDataUCDO();
        
        strResponseVar = anova2Model.getResponseLabel();
        strFactorA = anova2Model.getFactorALabel();
        strFactorB = anova2Model.getFactorBLabel();
        

        strTitle1 = strResponseVar + " vs " + strFactorA + " & " + strFactorB;
        strTitle2 = "";
        
        title1Text = new Text(strTitle1); 
        //  title1Text.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 18));        
        title1Text.getStyleClass().add("titleLabel");              
        title2Text = new Text("Title2Text"); 
        // title2Text.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 16));        
        title2Text.getStyleClass().add("titleLabel");   
        text1Width = title1Text.getLayoutBounds().getWidth();
        text2Width = title2Text.getLayoutBounds().getWidth();  

        Point2D xxx = new Point2D(0.0, 0.0);  // Need to initialize for later
       
        nFactorA_Levels = this.anova2Model.getNFactorA_Levels();
        nFactorB_Levels = this.anova2Model.getNFactorB_Levels();
    }
    
    
    public void completeTheDeal() {
        initializeGraphParams();
        setUpUI();
        initialize();
        setUpAnchorPane();
        dragableAnchorPane.heightProperty().addListener(ov-> {doThePlot();});
        dragableAnchorPane.widthProperty().addListener(ov-> {doThePlot();});
        containingPane = dragableAnchorPane.getTheContainingPane(); 
        dragableAnchorPane.setOnMouseReleased(dragableAnchorPaneMouseHandler);
    }

    private void initializeGraphParams() { 
        anova2Canvas = new Canvas(625, 625);
        anova2GC = anova2Canvas.getGraphicsContext2D(); 
        
        initial_yMin = anova2Model.getMinVertical();
        initial_yMax = anova2Model.getMaxVertical();
        initial_yRange = initial_yMax - initial_yMin;
        
        yAxis = new JustAnAxis(initial_yMin, initial_yMax);
        yAxis.setSide(Side.LEFT);
        yAxis.setBounds(initial_yMin, initial_yMax);
        yMin = initial_yMin; yMax = initial_yMax; yRange = initial_yRange;

        // This constant controls the rate of scale change when dragging
        deltaY = 0.005 * yRange;
        
        xAxis = new CategoryAxis(categoryLabels);
        xAxis.setSide(Side.BOTTOM); 
        xAxis.setAutoRanging(true);
        xAxis.setLabel(anova2Model.getFactorALabel());
        xAxis.setMinWidth(40);  //  Controls the Min X Axis width (for labels)
        xAxis.setPrefWidth(40);   
        xAxis.setLayoutX(500); xAxis.setLayoutY(25); 
        yAxis.setLayoutX(500); yAxis.setLayoutY(50);
        yAxis.setLabel(anova2Model.getResponseLabel());
        
        setHandlers();
    }
    
    private void setUpUI() {        
        anova2Canvas = new Canvas(0.95 * initWidth, 0.8 * initHeight);
        anova2GC = anova2Canvas.getGraphicsContext2D();
        anova2CategoryBoxes = new HBox(nLittleSquares);
        anova2CategoryBoxes.setAlignment(Pos.CENTER);
        anova2CategoryBoxes.setStyle("-fx-padding: 2;"+
                                     "-fx-border-style: solid inside;" +
                                     "-fx-border-width: 0;" +
                                     "-fx-border-insets: 5;"+
                                     "-border-radius: 5;");
        
        //  Set up little Squares and text
        nLittleSquares = graphColors.length;
        littleSquares = new Rectangle[nLittleSquares];
        littleSquaresText = new Text[nLittleSquares];
        squaresNText = new HBox[nLittleSquares];
        
        for (int i = 0; i < nRowsCat; i++) {
            littleSquares[i] = new Rectangle(10, 10, 10, 10);
            littleSquares[i].setStroke(graphColors[i]);
            littleSquares[i].setFill(graphColors[i]);
            littleSquaresText[i] = new Text(0, 0, strLeftLabels.get(i));
            littleSquaresText[i].setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR,18));
            littleSquaresText[i].setFill(graphColors[i]);
            squaresNText[i] = new HBox(10);
            squaresNText[i].setFillHeight(false);
            squaresNText[i].setAlignment(Pos.CENTER);
            squaresNText[i].setStyle("-fx-padding: 2;" +
                                     "-fx-border-width: 0;" +
                                     "-fx-border-style: solid inside;" +
                                     "-fx-border-insets: 5;" +
                                     "-fx-border-radius: 5;");
            squaresNText[i].getChildren().addAll(littleSquares[i], littleSquaresText[i]);
            anova2CategoryBoxes.getChildren().add(squaresNText[i]);
        }       
    }
    
    private void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        anova2Canvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        anova2Canvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
        boxPlotAnchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    

        dragableAnchorPane.getTheAP()
                                 .getChildren()
                                 .addAll(anchorTitleInfo, anova2CategoryBoxes, anova2Canvas, xAxis, yAxis);
        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    private void initialize() { 
        // These values are for positioning the colored squares
        nSquaresRow1 = Math.min(4, nFactorB_Levels);
        nSquaresRow2 = nFactorB_Levels - nSquaresRow1;
        startSquareFactor_1 = 1.0 / (2. * (nSquaresRow1 + 1.));
        jumpSquareFactor_1 = 1.0 / (nSquaresRow1 + 1.);
        startSquareFactor_2 = 1.0 / (2. * (nSquaresRow2 + 1.));
        jumpSquareFactor_2 = 1.0 / (nSquaresRow2 + 1.);
        
        anchorTitleInfo = new AnchorPane();
        anchorTitleInfo.getChildren().addAll(title1Text, title2Text);
        
        //  Set up little Squares and text
        nLittleSquares = graphColors.length;
        littleSquares = new Rectangle[nLittleSquares];
        textForSquares = new Text[nLittleSquares];
        for (int i = 0; i < nFactorB_Levels; i++) {
            littleSquares[i] = new Rectangle(10, 10, 10, 10);
            littleSquares[i].setStroke(graphColors[i]);
            littleSquares[i].setFill(graphColors[i]);

            // Formula for placement?  Odd/Even?
            textForSquares[i] = new Text(0, 0, factorB_Levels.get(i));
            textForSquares[i].setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR,14));
            textForSquares[i].setFill(graphColors[i]);
            
            anchorTitleInfo.getChildren().addAll(littleSquares[i]);
            anchorTitleInfo.getChildren().addAll(textForSquares[i]);
        }
    }
    
    public void doThePlot() {
        
        double daXPosition, x1, y1, y2, height;
        
        text1Width = title1Text.getLayoutBounds().getWidth();
        text2Width = title2Text.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double hBoxWidth = anova2CategoryBoxes.getWidth();
        
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        double hBoxEdge = (paneWidth - hBoxWidth) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();

        AnchorPane.setTopAnchor(anchorTitleInfo, 0.0 * tempHeight);
        AnchorPane.setLeftAnchor(anchorTitleInfo, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(anchorTitleInfo, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(anchorTitleInfo, 0.85 * tempHeight);       

        /*
        AnchorPane.setTopAnchor(title2Text, 0.1 * tempHeight);
        AnchorPane.setLeftAnchor(title2Text, txt2Edge * tempWidth);
        AnchorPane.setRightAnchor(title2Text, txt2Edge * tempWidth);
        AnchorPane.setBottomAnchor(title2Text, 0.2 * tempHeight);
        */
        
        AnchorPane.setTopAnchor(anova2CategoryBoxes, 0.15 * tempHeight);
        AnchorPane.setLeftAnchor(anova2CategoryBoxes, hBoxEdge * tempWidth);
        AnchorPane.setRightAnchor(anova2CategoryBoxes, hBoxEdge * tempWidth);
        AnchorPane.setBottomAnchor(anova2CategoryBoxes, 0.70 * tempHeight);        
        
        AnchorPane.setTopAnchor(xAxis, 0.9 * tempHeight);
        AnchorPane.setLeftAnchor(xAxis, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(xAxis, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(xAxis, 0.0 * tempHeight);
        
        AnchorPane.setTopAnchor(yAxis, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(yAxis, 0.0 * tempWidth);
        AnchorPane.setRightAnchor(yAxis, 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(yAxis, 0.2 * tempHeight);
        
        AnchorPane.setTopAnchor(anova2Canvas, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(anova2Canvas, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(anova2Canvas, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(anova2Canvas, 0.2 * tempHeight);
        
        tempPos1 = xAxis.getDisplayPosition(categoryLabels.get(1));
        tempPos0 = xAxis.getDisplayPosition(categoryLabels.get(0));
        bigTickInterval = tempPos1 - tempPos0;
        yAxis.setForcedAxisEndsFalse();
        positionTopInfo();
        
        horizPositioner = new HorizontalPositioner(nFactorA_Levels, nFactorB_Levels, bigTickInterval);        
        anova2GC.clearRect(0, 0 , anova2Canvas.getWidth(), anova2Canvas.getHeight());

        for (int theBetweenBatch = 1; theBetweenBatch <= nFactorA_Levels; theBetweenBatch++)
        {
            double daMiddleXPosition = xAxis.getDisplayPosition(categoryLabels.get(theBetweenBatch - 1));
            for (int theWithinBatch = 1; theWithinBatch <= nFactorB_Levels; theWithinBatch++) {
                int theAppropriateLevel = (theBetweenBatch - 1) * nFactorB_Levels + theWithinBatch;
                UnivariateContinDataObj tempUCDO = new UnivariateContinDataObj();
                tempUCDO = anova2Model.getPrelimAB().getIthUCDO(theAppropriateLevel);

                nDataPoints = tempUCDO.getLegalN();
                fiveNumberSummary = new double[5];
                fiveNumberSummary = tempUCDO.get_5NumberSummary();
                whiskerEndRanks = tempUCDO.getWhiskerEndRanks();
                bottomOfLowWhisker = yAxis.getDisplayPosition(fiveNumberSummary[0]);

                if (whiskerEndRanks[0] != -1)
                    bottomOfLowWhisker = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(whiskerEndRanks[0]));

                topOfHighWhisker = yAxis.getDisplayPosition(fiveNumberSummary[4]);

                if (whiskerEndRanks[1] != -1)
                    topOfHighWhisker = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(whiskerEndRanks[1]));

                double min_display = yAxis.getDisplayPosition(fiveNumberSummary[0]);
                double q1_display = yAxis.getDisplayPosition(fiveNumberSummary[1]);
                double q2_display = yAxis.getDisplayPosition(fiveNumberSummary[2]);
                double q3_display = yAxis.getDisplayPosition(fiveNumberSummary[3]);
                double max_display = yAxis.getDisplayPosition(fiveNumberSummary[4]);
                double iqr_display = q3_display - q1_display;
                double iqr = fiveNumberSummary[3] - fiveNumberSummary[1];

                anova2GC.setLineWidth(2);
                anova2GC.setStroke(Color.BLACK);

                int relativePositionInA = theAppropriateLevel % nFactorB_Levels;
                setColor(theWithinBatch - 1);              
                horizPosition = horizPositioner.getIthLeftPosition(theAppropriateLevel, daMiddleXPosition);
                anova2GC.strokeRect(horizPosition.getX(), q3_display, horizPosition.getY(), -iqr_display);    //  box
                anova2GC.strokeLine(horizPosition.getX(), q2_display, horizPosition.getX() + horizPosition.getY(), q2_display);    //  Median

                anova2GC.strokeLine(horizPosition.getX() + 0.5 * horizPosition.getY(), bottomOfLowWhisker, horizPosition.getX() + 0.5 * horizPosition.getY(), q1_display);  //  Low whisker
                anova2GC.strokeLine(horizPosition.getX() + 0.5 * horizPosition.getY(), q3_display, horizPosition.getX() + 0.5 * horizPosition.getY(), topOfHighWhisker);  //  High whisker

                //  Top & bottom of whisker
                double topBottomLength = horizPositioner.getCIEndWidthFrac();
                double midBar = horizPositioner.getMidBarPosition(theAppropriateLevel, daMiddleXPosition);
                anova2GC.strokeLine(midBar - topBottomLength, bottomOfLowWhisker, midBar + topBottomLength, bottomOfLowWhisker);  
                anova2GC.strokeLine(midBar - topBottomLength, topOfHighWhisker, midBar + topBottomLength, topOfHighWhisker);  

                if (whiskerEndRanks[0] != -1)    //  Are there low outliers?
                {
                    int dataPoint = 0;
                    while (dataPoint < whiskerEndRanks[0])
                    {
                        double xx = horizPosition.getX() + 0.5 * horizPosition.getY();
                        double yy = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(dataPoint));
                        anova2GC.fillOval(xx - 3, yy - 3, 6, 6);
                        dataPoint++;
                    }
                }

                if (whiskerEndRanks[1] != -1) //  Are there high outliers?
                {
                    for (int dataPoint = whiskerEndRanks[1] + 1; dataPoint < nDataPoints; dataPoint++)
                    {
                        double xx = horizPosition.getX() + 0.5 * horizPosition.getY();
                        double yy = yAxis.getDisplayPosition(tempUCDO.getIthSortedValue(dataPoint));
                        anova2GC.fillOval(xx - 3, yy - 3, 6, 6);
                    }
                }
            }  // Loop through within batches
        }   //  Loop through between batches
    }
    
    public void positionTopInfo() {
        int i, j;
        double atIWidth, tempPosition, startRect, jumpRect, k1, k2;
        atIWidth = anchorTitleInfo.getWidth();
        for (i = 0; i < nSquaresRow1; i++) {
            startRect = atIWidth * startSquareFactor_1;
            jumpRect = atIWidth * jumpSquareFactor_1;
            tempPosition = startRect + i * jumpRect;
            littleSquares[i].setX(tempPosition);
            littleSquares[i].setY(60);
            textForSquares[i].setX(tempPosition + 20);
            textForSquares[i].setY(70);
        }

        if (nSquaresRow2 > 0) {
            for (i = 0; i < nSquaresRow2; i++) {
                startRect = anchorTitleInfo.getWidth() * startSquareFactor_2;
                jumpRect = anchorTitleInfo.getWidth() * jumpSquareFactor_2;
                tempPosition = startRect + i * jumpRect;
                littleSquares[nSquaresRow1 + i].setX(tempPosition);
                littleSquares[nSquaresRow1 + i].setY(80);
                textForSquares[nSquaresRow1 + i].setX(tempPosition + 20);
                textForSquares[nSquaresRow1 + i].setY(90);
            }
        }
        k1 = 12.0;  //  Hack for font 25 
        k2 = 10.0;    //  Hack for font 20
        title1Text.setX(atIWidth / 2. - k1 * strTitle1.length() / 2.);
        title2Text.setX(atIWidth / 2. - k2 * strTitle2.length() / 2.);
        title2Text.setY(50);
    }
    
        private void setHandlers() {
        yAxis.setOnMouseDragged(yAxisMouseHandler); 
        yAxis.setOnMousePressed(yAxisMouseHandler); 
        yAxis.setOnMouseReleased(yAxisMouseHandler); 
    }
      
     EventHandler<MouseEvent> yAxisMouseHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent mouseEvent) 
        {           
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) 
            { 
                yPix_InitialPress = mouseEvent.getY(); 
                yPix_MostRecentDragPoint = mouseEvent.getY();
                dragging = false;
            }
            else 
            if(mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED)
            {
                if (dragging == true)
                {
                    yAxis.setLowerBound(newY_Lower ); 
                    yAxis.setUpperBound(newY_Upper );
                    dragging = false;
                }
            }
            else 
            if(mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                dragging = true;
                double yPix_Dragging = mouseEvent.getY();  
                newY_Lower = yAxis.getLowerBound();
                newY_Upper = yAxis.getUpperBound(); 
                dispLowerBound = yAxis.getDisplayPosition(yAxis.getLowerBound());
                dispUpperBound = yAxis.getDisplayPosition(yAxis.getUpperBound());
                double frac = mouseEvent.getY() / dispLowerBound;

                if((yPix_Dragging > yPix_InitialPress) && (yPix_Dragging > yPix_MostRecentDragPoint))
                {    
                    if (frac < 0.5) 
                    {
                        if (!yAxis.getHasForcedHighScaleEnd()) {
                            newY_Upper = yAxis.getUpperBound() + deltaY;
                        }
                    }
                    else  
                    {
                        if (!yAxis.getHasForcedLowScaleEnd()) {
                            newY_Lower = yAxis.getLowerBound() + deltaY;
                        }
                    }
                }
                else 
                if ((yPix_Dragging < yPix_InitialPress) && (yPix_Dragging < yPix_MostRecentDragPoint))
                {   
                    if (frac < 0.5)
                    {
                        if (!yAxis.getHasForcedHighScaleEnd()) {
                            newY_Upper = yAxis.getUpperBound() - deltaY;
                        }
                    }
                    else
                    {
                        if (!yAxis.getHasForcedLowScaleEnd()) {
                            newY_Lower = yAxis.getLowerBound() - deltaY;
                        }
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
                
                doThePlot();
            }   // end if mouse dragged
        }   //  end handle
    };   
     
    EventHandler<MouseEvent> scatterplotMouseHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent mouseEvent) 
        {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) 
            { 
                xPix_InitialPress = mouseEvent.getX();  
                yPix_InitialPress = mouseEvent.getY();  
            }
        }
    };  
    
    public void setColor( int relPos) {           
        anova2GC.setStroke(graphColors[relPos]);
        anova2GC.setFill(graphColors[relPos]);     
    }
    
    public class HorizontalPositioner {
        
        int nBars, nLevelsA, nLevelsB;
        double rangeOfBars;
        double endRangeBarFrac = 0.2; // in fraction of bar width
        double betweenBarsBarFrac = 0.1; // in fraction of bar width
        double fullBarWidth, halfBarWidth, fracBarWidth, confIntEndWidthFrac ;
          
        public HorizontalPositioner(int nLevelsOfFactorA, int nLevelsOfFactorB, double rangeBtwnBigTicks) { 
            nLevelsA = nLevelsOfFactorA;            
            nLevelsB = nLevelsOfFactorB;            
            nBars = nLevelsA * nLevelsB;
            rangeOfBars = rangeBtwnBigTicks;           
            double denom = 2  * endRangeBarFrac + nLevelsB + (nLevelsB - 1.0) * betweenBarsBarFrac; // Check this!!
            fullBarWidth = rangeBtwnBigTicks / denom;   //  Bar width if all space used for bar
            halfBarWidth = 0.5 * fullBarWidth;
            fracBarWidth = 0.75 * fullBarWidth; // Controls the horizontal room to draw
            confIntEndWidthFrac = 0.20 * fullBarWidth; // Controls the top & bottom horizontals 
        }
        
        public double GetEndSpace() {return endRangeBarFrac; }
        public void SetEndSpace(double fractionOfBar) {
            endRangeBarFrac = fractionOfBar;
        }
        
        public double getBarWidth() { return fullBarWidth; }
        
        public double getCIEndWidthFrac() { return confIntEndWidthFrac; }
        
        public double getMidBarPosition(int ith, double daMid) {
            double midBarPosition = 0.0;
            double m = 0.; //    slope
            double b = 0.; // intercept
            int half_nLevelsB = nLevelsB / 2;   //  round down
            int relativePositionInA = ith % nLevelsB;
            Point2D leftX_and_Width;
            if (relativePositionInA == 0 )
                relativePositionInA = nLevelsB; 
            
            if ((nLevelsB / 2) * 2 == nLevelsB)  {// Even # of B levels
                m = fullBarWidth;
                b = daMid - 0.5 * fullBarWidth - half_nLevelsB * fullBarWidth;
                midBarPosition = m * relativePositionInA + b;
            }
            else {  // Odd # of B levels
                m = fullBarWidth;
                b = daMid - fullBarWidth * (half_nLevelsB + 1.0);
                midBarPosition = m * relativePositionInA + b;
            }   
            return midBarPosition;
        }
        
        public double GetBetweenSpace() {return betweenBarsBarFrac; }
        
        public void SetBetweenSpace(double fractionOfBar) {
            betweenBarsBarFrac = fractionOfBar;
        }
        
        public double GetRangeBetweenCatTicks() {return betweenBarsBarFrac; }
        
        public void SetRangeBetweenCatTicks(double fractionOfBar) {
            betweenBarsBarFrac = fractionOfBar;
        }    
        
        public Point2D getIthLeftPosition(int ith, double daMid) {
            double ithLeft = 0.0;
            double midBarPosition = 0.0;
            double m = 0.; //    slope
            double b = 0.; // intercept
            int half_nLevelsB = nLevelsB / 2;   //  round down
            int relativePositionInA = ith % nLevelsB;
            Point2D leftX_and_Width;
            if (relativePositionInA == 0 )
                relativePositionInA = nLevelsB; 
            
            if ((nLevelsB / 2) * 2 == nLevelsB)  {// Even # of B levels
                m = fullBarWidth;
                b = daMid - 0.5 * fullBarWidth - half_nLevelsB * fullBarWidth;
                midBarPosition = m * relativePositionInA + b;
                ithLeft = midBarPosition - 0.5 * fracBarWidth;
            }
            else {  // Odd # of B levels
                m = fullBarWidth;
                b = daMid - fullBarWidth * (half_nLevelsB + 1.0);
                midBarPosition = m * relativePositionInA + b;
                ithLeft = midBarPosition - 0.5 * fracBarWidth;
            }
            
            double widthOfBar = 2.0 * (midBarPosition - ithLeft);
            leftX_and_Width = new Point2D(ithLeft, widthOfBar);
            
            return leftX_and_Width;
        }
    }
    
    EventHandler<MouseEvent> dragableAnchorPaneMouseHandler = new EventHandler<MouseEvent>() 
    {
        @Override
        public void handle(MouseEvent mouseEvent) 
        {

        }
    }; 

    public Pane getTheContainingPane() { return containingPane; }
}


