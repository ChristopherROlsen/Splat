/**************************************************
 *             ANOVA2_MargOfErrView               *
 *                  05/15/18                      *
 *                    12:00                       *
 *************************************************/
package ANOVA_Two;

import genericClasses.JustAnAxis;
import genericClasses.UnivariateContinDataObj;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ANOVA2_MargOfErrView extends Region { 
    
    // POJOs
    boolean dragging;
    
    int nFactorA_Levels, nFactorB_Levels, nDataPoints, nSquaresRow1, nSquaresRow2;
    int[] whiskerEndRanks;
    
    double  initial_yMin, initial_yMax, initial_yRange, yMin, yMax, yRange,
            xPix_InitialPress, yPix_InitialPress, xPix_MostRecentDragPoint, 
            yPix_MostRecentDragPoint, newX_Lower, newX_Upper, newY_Lower, 
            newY_Upper, deltaX, deltaY, dispLowerBound, dispUpperBound, 
            bottomOfLowWhisker, topOfHighWhisker, tempPos0, tempPos1, 
            bigTickInterval, confidenceLevel, startSquareFactor_1, 
            jumpSquareFactor_1, startSquareFactor_2, jumpSquareFactor_2,
            rvfMinResid, rvfMaxResid, rvfAxisBound, rvfMinMean, rvfMaxMean;
    
    double[] fiveNumberSummary;

    String  choiceOfPlot, meansOrBars, strTitle, strErrBarDescr, strResponseVar,
            strFactorA, strFactorB;
    ObservableList<String> categoryLabels, factorA_Levels, factorB_Levels;
    
    // My objects
    ANOVA2_Model twoWayANOVAModel;
    CategoryAxis categoricalXAxis;
    HorizontalPositioner horizPositioner;
    JustAnAxis numericalYAxis;
    UnivariateContinDataObj allData_UCDO;
    UnivariateContinDataObj[] arrayOfUCOs;
    
    // POJOs / FX
    AnchorPane anchorTitleInfo;
    Canvas twoWayANOVACanvas;
    // GREEN is last b/c color blind folks have trouble with RED / GREEN,
    // so Red and Green should seldom appear.  Probably these colors should
    // be arranged so that the most distinguishable ones are early.
    Color[] graphColors = { Color.BLUE, Color.RED, Color.SADDLEBROWN, 
                            Color.LIGHTSEAGREEN, Color.BLUEVIOLET, 
                            Color.ALICEBLUE, Color.LIGHTCORAL, Color.GREEN};
    GraphicsContext gc; // Required for drawing on the Canvas
    GridPane gridPane;    
    Label xLabelPad, yLabelPad, xScalePad, yScalePad, titlePad;
    Line line;
    Point2D horizPosition;
    Rectangle[] littleSquares;
    Scene scene;
    Stage stage;
    Text txtTitle, txtErrBarDescr;
    Text[] textForSquares;

    ANOVA2_MargOfErrView(ANOVA2_Model twoWayANOVAModel, String choiceOfPlot, String meansOrBars) {
        this.twoWayANOVAModel = twoWayANOVAModel;
        String graphsCSS = getClass().getResource("/css/Graphs.css").toExternalForm();
        categoryLabels = FXCollections.observableArrayList();        
        factorA_Levels = FXCollections.observableArrayList();
        factorB_Levels = FXCollections.observableArrayList();   
        categoryLabels = twoWayANOVAModel.getFactorALevels();
        factorA_Levels = twoWayANOVAModel.getFactorALevels();
        factorB_Levels = twoWayANOVAModel.getFactorBLevels();
        nFactorA_Levels = factorA_Levels.size();
        nFactorB_Levels = factorB_Levels.size();
        
        allData_UCDO = this.twoWayANOVAModel.getAllDataUCDO();
        
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage = new Stage(); 
        stage.setX(screenBounds.getWidth() - stage.getWidth()/4);
        stage.setY(screenBounds.getHeight() - stage.getHeight()/4);
        stage.setResizable(true); 
        stage.setTitle("Two-way Analysis of Variance");
        stage.setResizable(true);

        gridPane = new GridPane();  // gridPane.setGridLinesVisible(true);
        
        scene = new Scene(gridPane, 825, 825); 
        scene.getStylesheets().add(graphsCSS);  

        twoWayANOVACanvas = new Canvas(625, 625);
        gc = twoWayANOVACanvas.getGraphicsContext2D(); 
        
        allData_UCDO = twoWayANOVAModel.getAllDataUCDO();
        
        this.choiceOfPlot = choiceOfPlot;
        this.meansOrBars = meansOrBars;
        
        strResponseVar = twoWayANOVAModel.getResponseLabel();
        strFactorA = twoWayANOVAModel.getFactorALabel();
        strFactorB = twoWayANOVAModel.getFactorBLabel();
        
        strTitle = strResponseVar + " vs " + strFactorA + " & " + strFactorB;
        strErrBarDescr = "        ";
        txtTitle = new Text(50, 25, strTitle);
        txtTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,25));
        txtErrBarDescr = new Text (60, 45, strErrBarDescr);
        txtErrBarDescr.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 20)); 
           
        initialize();
 
        Point2D xxx = new Point2D(0.0, 0.0);  // Need to initialize for later
       
        nFactorA_Levels = this.twoWayANOVAModel.getNFactorA_Levels();
        nFactorB_Levels = this.twoWayANOVAModel.getNFactorB_Levels();
        whiskerEndRanks = new int[2];
        categoryLabels = this.twoWayANOVAModel.getFactorALevels();
      
        initial_yMin = twoWayANOVAModel.getMinVertical();
        initial_yMax = twoWayANOVAModel.getMaxVertical();
        initial_yRange = initial_yMax - initial_yMin;
        
        numericalYAxis = new JustAnAxis(initial_yMin, initial_yMax);
        numericalYAxis.setSide(Side.LEFT);
        numericalYAxis.setBounds(initial_yMin, initial_yMax);
        yMin = initial_yMin; yMax = initial_yMax; yRange = initial_yRange;

        // This constant controls the rate of scale change when dragging
        deltaY = 0.005 * yRange;
        
        categoricalXAxis = new CategoryAxis(this.twoWayANOVAModel.getFactorALevels());
        categoricalXAxis.setSide(Side.BOTTOM); 
        categoricalXAxis.setAutoRanging(true);
        categoricalXAxis.setLabel(twoWayANOVAModel.getFactorALabel());
        categoricalXAxis.setMinWidth(40);  //  Controls the Min X Axis width (for labels)
        categoricalXAxis.setPrefWidth(40);
        categoricalXAxis.setPrefSize(gridPane.getWidth() - 50, 40);   
        categoricalXAxis.setLayoutX(500); categoricalXAxis.setLayoutY(50); 

        numericalYAxis.setPrefSize(20, gridPane.getHeight() - 50);
        numericalYAxis.setLayoutX(500); numericalYAxis.setLayoutY(50);
        numericalYAxis.setLabel(twoWayANOVAModel.getResponseLabel());

        // Insets: Top, right, bottom, left
        gridPane.setPadding(new Insets(10, 15, 5, 10));

        titlePad.setMinHeight(60); // To give the X Label some room       
        xScalePad.setMinHeight(80); // To give the X scale some room 
        yScalePad.setMinWidth(60); // To give the Y scale some room 
        gridPane.add(xScalePad, 0, 2);
        gridPane.add(yScalePad, 0, 0);
        gridPane.add(titlePad, 1, 0);
        
        gridPane.add(numericalYAxis, 0, 1);
        gridPane.add(categoricalXAxis, 1, 2);   
 
        //  **************   Y Labels & Titles  **********************
        gridPane.add(anchorTitleInfo, 1, 0);    
        
        setHandlers();
        
        gridPane.prefHeightProperty().bind(scene.heightProperty());
        gridPane.prefWidthProperty().bind(scene.widthProperty());

        gridPane.add(twoWayANOVACanvas, 1, 1);
        
        stage.setScene(scene);   
        stage.sizeToScene();
        stage.show();
        
        twoWayANOVACanvas.heightProperty().bind(scene.heightProperty().subtract(155));
        twoWayANOVACanvas.widthProperty().bind(scene.widthProperty().subtract(110));
   
        // ****************  Switch for doing the plots  ***************
        switch (choiceOfPlot) {
          
            case "MarginOfError":       // Bar +/- me
                scene.heightProperty().addListener(ov-> {doTheGraph();});
                scene.widthProperty().addListener(ov-> {doTheGraph();}); 
                confidenceLevel = this.twoWayANOVAModel.getConfidenceLevel();
                double confLevelAsPercent = 100 * confidenceLevel;
                strErrBarDescr = "mean +/- margin of error";
                numericalYAxis.forceLowScaleEndToBe(0.0);
                doTheGraph();                
                break;
                
            default:
                System.out.println("Arrggg!  No known choice #2");
                System.exit(2);
                break;
        }
        
        txtErrBarDescr.setText(strErrBarDescr);
    }
    
        
    public void doTheGraph() {  
        tempPos1 = categoricalXAxis.getDisplayPosition(categoryLabels.get(1));
        tempPos0 = categoricalXAxis.getDisplayPosition(categoryLabels.get(0));
        bigTickInterval = tempPos1 - tempPos0;
        positionTopInfo();
        
        horizPositioner = new HorizontalPositioner(nFactorA_Levels, nFactorB_Levels, bigTickInterval);
        double errorBarLength = 0.0;
        gc.clearRect(0, 0 , twoWayANOVACanvas.getWidth(), twoWayANOVACanvas.getHeight());

        for (int theBetweenBatch = 1; theBetweenBatch <= nFactorA_Levels; theBetweenBatch++)
        {
            double daMiddleXPosition = categoricalXAxis.getDisplayPosition(categoryLabels.get(theBetweenBatch - 1));           
            for (int theWithinBatch = 1; theWithinBatch <= nFactorB_Levels; theWithinBatch++) {
                int theAppropriateLevel = (theBetweenBatch - 1) * nFactorB_Levels + theWithinBatch;
                UnivariateContinDataObj tempUCDO = new UnivariateContinDataObj();
                
                /*
                        There are different parameters for each batch -- switch below cannot be deleted
                */
                
                tempUCDO = twoWayANOVAModel.getPrelimAB().getIthUCDO(theAppropriateLevel);

                nDataPoints = tempUCDO.getLegalN();
                gc.setLineWidth(2);

                double boxTop = numericalYAxis.getDisplayPosition(tempUCDO.getTheMean());
                double boxheight = numericalYAxis.getDisplayPosition(0.0) - numericalYAxis.getDisplayPosition(tempUCDO.getTheMean());

                int relativePositionInA = theAppropriateLevel % nFactorB_Levels;
                horizPosition = horizPositioner.getIthLeftPosition(theAppropriateLevel, daMiddleXPosition);
                
                switch (choiceOfPlot) {
                 
                    case "MarginOfError":
                        confidenceLevel = twoWayANOVAModel.getConfidenceLevel();
                        int degreesOfFreedom = tempUCDO.getLegalN() - 1;
                        // errorBarLength = tempUCDO.getTheMarginOfErr(degreesOfFreedom, confidenceLevel);
                        break;

                    default:
                        System.out.println("Arrggg!  No known choice #3");
                        System.exit(3);
                        break;
            }
                // Horizontal lines (Top & bottom on screen, bottom & top in coordinates)
                double topOfErrorfBar = numericalYAxis.getDisplayPosition(tempUCDO.getTheMean() + errorBarLength);
                double bottomOfErrorBar = numericalYAxis.getDisplayPosition(tempUCDO.getTheMean() - errorBarLength);

                double topBottomLength = horizPositioner.getCIEndWidthFrac();
                double midBar = horizPositioner.getMidBarPosition(theAppropriateLevel, daMiddleXPosition);

                if (meansOrBars.equals("Means")) {  // Error bars drawn last
                    gc.setStroke(Color.BLACK);
                    gc.strokeLine(horizPosition.getX() + 0.5 * horizPosition.getY(), topOfErrorfBar, horizPosition.getX() + 0.5 * horizPosition.getY(), bottomOfErrorBar);
                    gc.strokeLine(midBar - topBottomLength, bottomOfErrorBar, midBar + topBottomLength, bottomOfErrorBar);  
                    gc.strokeLine(midBar - topBottomLength, topOfErrorfBar, midBar + topBottomLength, topOfErrorfBar);
                    double theMean = numericalYAxis.getDisplayPosition(tempUCDO.getTheMean());
                    setColor(theWithinBatch - 1); 
                    
                    gc.fillOval(horizPosition.getX() + 0.5 * horizPosition.getY() - 5, theMean - 5, 10, 10);
                } else {    // "Bars" -- Error bars drawn first
                    horizPosition = horizPositioner.getIthLeftPosition(theAppropriateLevel, daMiddleXPosition);
                    
                    // setColor(relativePositionInA);
                    setColor(theWithinBatch - 1);
                    
                    gc.fillRect(horizPosition.getX(), boxTop, horizPosition.getY(), boxheight); 
                    gc.setStroke(Color.BLACK);
                    gc.strokeLine(horizPosition.getX() + 0.5 * horizPosition.getY(), topOfErrorfBar, horizPosition.getX() + 0.5 * horizPosition.getY(), bottomOfErrorBar);
                    gc.strokeLine(midBar - topBottomLength, bottomOfErrorBar, midBar + topBottomLength, bottomOfErrorBar);  
                    gc.strokeLine(midBar - topBottomLength, topOfErrorfBar, midBar + topBottomLength, topOfErrorfBar);
                }
            }  // Loop through within batches
        }   //  Loop through between batches      
    }

/*    
    private void doBarAndMean() {
        tempPos1 = categoricalXAxis.getDisplayPosition(categoryLabels.get(1));      
        tempPos0 = categoricalXAxis.getDisplayPosition(categoryLabels.get(0));      
        bigTickInterval = tempPos1 - tempPos0; 
        
        positionTopInfo();    
                
        horizPositioner = new HorizontalPositioner(nFactorA_Levels, nFactorB_Levels, bigTickInterval);      
        gc.clearRect(0, 0 , twoWayANOVACanvas.getWidth(), twoWayANOVACanvas.getHeight());
        
        for (int theBetweenBatch = 1; theBetweenBatch <= nFactorA_Levels; theBetweenBatch++)
        {
            double daMiddleXPosition = categoricalXAxis.getDisplayPosition(categoryLabels.get(theBetweenBatch - 1));
            for (int theWithinBatch = 1; theWithinBatch <= nFactorB_Levels; theWithinBatch++) {
                int theAppropriateLevel = (theBetweenBatch - 1) * nFactorB_Levels + theWithinBatch;
                UnivariateContinDataObj tempUCDO = new UnivariateContinDataObj();
                tempUCDO = twoWayANOVAModel.getPrelimAB().getIthUCDO(theAppropriateLevel);

            nDataPoints = tempUCDO.getLegalN();
            gc.setLineWidth(1);
            
            double boxTop = numericalYAxis.getDisplayPosition(tempUCDO.getTheMean());
            double boxheight = numericalYAxis.getDisplayPosition(0.0) - numericalYAxis.getDisplayPosition(tempUCDO.getTheMean());

            // horizontalPosition.x is the left side of bar, horizontalPosition.y  is the bar width
            Point2D horizontalPosition = horizPositioner.getIthLeftPosition(theAppropriateLevel, daMiddleXPosition);
            int relativePositionInA = theAppropriateLevel % nFactorB_Levels;

            setColor(theWithinBatch - 1);             

            gc.fillRect(horizontalPosition.getX(), boxTop, horizontalPosition.getY(), boxheight);         
            gc.setStroke(Color.BLACK);

            double theMean = tempUCDO.getTheMean();
            String theMeanAsString = String.format("%7.2f", theMean);
            
            float width = com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().computeStringWidth(theMeanAsString, gc.getFont());
            float height = com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().getFontMetrics(gc.getFont()).getLineHeight();

            // Position text to the middle of the bar
            double startTextAt = horizontalPosition.getX() + (horizontalPosition.getY() - width) / 2;
            gc.strokeText(theMeanAsString, startTextAt, boxTop - 10);
            }  // Loop through within batches
        }   //  Loop through between batches 
    }   // End doBarAndMean
*/
    
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
        txtTitle.setX(atIWidth / 2. - k1 * strTitle.length() / 2.);
        txtErrBarDescr.setX(atIWidth / 2. - k2 * strErrBarDescr.length() / 2.);
        txtErrBarDescr.setY(50);
    }
    
    private void setHandlers() {
        numericalYAxis.setOnMouseDragged(yAxisMouseHandler); 
        numericalYAxis.setOnMousePressed(yAxisMouseHandler); 
        numericalYAxis.setOnMouseReleased(yAxisMouseHandler); 
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
                    numericalYAxis.setLowerBound(newY_Lower ); 
                    numericalYAxis.setUpperBound(newY_Upper );
                    dragging = false;
                }
            }
            else 
            if(mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                dragging = true;
                double yPix_Dragging = mouseEvent.getY();  
                newY_Lower = numericalYAxis.getLowerBound();
                newY_Upper = numericalYAxis.getUpperBound(); 
                dispLowerBound = numericalYAxis.getDisplayPosition(numericalYAxis.getLowerBound());
                dispUpperBound = numericalYAxis.getDisplayPosition(numericalYAxis.getUpperBound());
                double frac = mouseEvent.getY() / dispLowerBound;

                if((yPix_Dragging > yPix_InitialPress) && (yPix_Dragging > yPix_MostRecentDragPoint))
                {    
                    if (frac < 0.5) 
                    {
                        if (!numericalYAxis.getHasForcedHighScaleEnd()) {
                            newY_Upper = numericalYAxis.getUpperBound() + deltaY;
                        }
                    }
                    else  
                    {
                        if (!numericalYAxis.getHasForcedLowScaleEnd()) {
                            newY_Lower = numericalYAxis.getLowerBound() + deltaY;
                        }
                    }
                }
                else 
                if ((yPix_Dragging < yPix_InitialPress) && (yPix_Dragging < yPix_MostRecentDragPoint))
                {   
                    if (frac < 0.5)
                    {
                        if (!numericalYAxis.getHasForcedHighScaleEnd()) {
                            newY_Upper = numericalYAxis.getUpperBound() - deltaY;
                        }
                    }
                    else
                    {
                        if (!numericalYAxis.getHasForcedLowScaleEnd()) {
                            newY_Lower = numericalYAxis.getLowerBound() - deltaY;
                        }
                    }
                }    

                if (numericalYAxis.getHasForcedLowScaleEnd()) {
                    newY_Lower = numericalYAxis.getForcedLowScaleEnd();
                }
            
                if (numericalYAxis.getHasForcedHighScaleEnd()) {
                    newY_Upper = numericalYAxis.getForcedHighScaleEnd();
                }
                
                numericalYAxis.setLowerBound(newY_Lower ); 
                numericalYAxis.setUpperBound(newY_Upper ); 

                dispLowerBound = numericalYAxis.getDisplayPosition(numericalYAxis.getLowerBound());
                dispUpperBound = numericalYAxis.getDisplayPosition(numericalYAxis.getUpperBound());
   
                yPix_MostRecentDragPoint = mouseEvent.getY();
                
                doTheGraph();
                
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
        gc.setStroke(graphColors[relPos]);
        gc.setFill(graphColors[relPos]);     
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
    
    private void initialize() { 
        xScalePad = new Label(" "); yScalePad = new Label(" ");  
        xLabelPad = new Label(" "); yLabelPad = new Label(" ");
        titlePad  = new Label(" ");
        
        // These values are for positioning the colored squares
        nSquaresRow1 = Math.min(4, nFactorB_Levels);
        nSquaresRow2 = nFactorB_Levels - nSquaresRow1;
        startSquareFactor_1 = 1.0 / (2. * (nSquaresRow1 + 1.));
        jumpSquareFactor_1 = 1.0 / (nSquaresRow1 + 1.);
        startSquareFactor_2 = 1.0 / (2. * (nSquaresRow2 + 1.));
        jumpSquareFactor_2 = 1.0 / (nSquaresRow2 + 1.);
        
        anchorTitleInfo = new AnchorPane();
        anchorTitleInfo.getChildren().addAll(txtTitle, txtErrBarDescr);
        
        //  Set up little Squares and text
        int nLittleSquares = graphColors.length;
        littleSquares = new Rectangle[nLittleSquares];
        textForSquares = new Text[nLittleSquares];
        for (int i = 0; i < nFactorB_Levels; i++) {
            littleSquares[i] = new Rectangle(10, 10, 10, 10);
            littleSquares[i].setStroke(graphColors[i]);
            littleSquares[i].setFill(graphColors[i]);

            // Formula for placement?  Odd/Even?
            textForSquares[i] = new Text(0, 0, factorB_Levels.get(i));
            textForSquares[i].setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR,18));
            textForSquares[i].setFill(graphColors[i]);
            
            anchorTitleInfo.getChildren().addAll(littleSquares[i]);
            anchorTitleInfo.getChildren().addAll(textForSquares[i]);
        }
    }
}
