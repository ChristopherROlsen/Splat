/**************************************************
 *              X2Assoc_PieChartView              *
 *                    05/15/18                    *
 *                     15:00                      *
 *************************************************/
package chiSquare;

import genericClasses.JustAnAxis;
import genericClasses.DragableAnchorPane;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.shape.ArcType;

public class X2Assoc_PieChartView 
{
    // POJOs
    boolean dragging;
    
    int nRowsCat, nColsCat, nSquaresRow1, nSquaresRow2, nLittleSquares;
    
    double xMin, xMax, yMin, yMax, xRange, width, halfWidth,
           xPix_InitialPress, yPix_InitialPress, xPix_MostRecentDragPoint, 
           yPix_MostRecentDragPoint, newX_Lower, newX_Upper, newY_Lower, 
           newY_Upper, deltaX, deltaY, dispLowerBound, dispUpperBound,
           bigTickInterval, startSquareFactor_1, jumpSquareFactor_1, 
           startSquareFactor_2, jumpSquareFactor_2, daLeftEndOfLine, 
           daRightEndOfLine, initHoriz, initVert, initWidth, initHeight, 
           maxXPosition, sqrt2, halfRoot2, onePlusOver, initialCircleRadii, 
           radii, sliderValue, piOver180, text1Width, text2Width;
    
    double[] columnProps;
    double[][] cumRowProps;

    String strTopVariable, strLeftVariable, strTitle, graphsCSS;
    String[] leftLabels, preTopLabels, topLabels;
    
    //  My classes
    DragableAnchorPane dragableAnchorPane;
    JustAnAxis xAxis, yAxis;
    MyX2StringUtilities myStringUtilities;
    X2Assoc_Dashboard x2Assoc_Dashboard;
    X2Assoc_Model x2Assoc_Model;

    //  FX objects
    AnchorPane segBarAnchorPane;
    Canvas segBarCanvas;
    
 /*****************************************************************************
 * GREEN is last b/c color blind folks have trouble with RED / GREEN,        *
 * so hopefully Red and Green should seldom appear.  Probably these colors   *
 * should be arranged so that the most distinguishable ones are early.       *
 ****************************************************************************/
    Color[] graphColors = { Color.BLUE, Color.RED, Color.SADDLEBROWN, 
                            Color.TURQUOISE, Color.BLUEVIOLET, 
                            Color.ALICEBLUE, Color.LIGHTCORAL, Color.GREEN};   
    
    GraphicsContext pieChartGC; // Required for drawing on the Canvas
    HBox segBarCategoryBoxes;
    HBox[] squaresNText;
    Label sliderControlLabel;
    Pane segBarPane, containingPane;
    Rectangle[] littleSquares;
    Slider radiiSlider;
    Text txtTitle1, txtTitle2, sliderControlText;
    Text[] littleSquaresText;

    public X2Assoc_PieChartView(X2Assoc_Model association_Model, 
            X2Assoc_Dashboard association_Dashboard,
            double placeHoriz, double placeVert,
            double withThisWidth, double withThisHeight) 
    {
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;         
        this.x2Assoc_Model = association_Model;
        this.x2Assoc_Dashboard = association_Dashboard;
        graphsCSS = getClass().getResource("/css/Graphs.css").toExternalForm();
        myStringUtilities = new MyX2StringUtilities();
        containingPane = new Pane();
        
        txtTitle1 = new Text("Pie Chart"); 
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 14));        
        txtTitle1.getStyleClass().add("titleLabel");              
        txtTitle2 = new Text("Title2Text"); 
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 8));        
        txtTitle2.getStyleClass().add("titleLabel");   
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();
        
        radiiSlider = new Slider(0.1, 2.0, 1.0);
        radiiSlider.setValue(1.0);
        
        radiiSlider.valueProperty()
                   .addListener(ov ->
                        {
                            radii = radiiSlider.getValue();
                            doThePlot();  
                        });
        
        sliderControlText = new Text("Fine radius control " + "\u2192");
        sliderControlText.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 14));
        
        // --------------------------------------------------------------------
            sqrt2 = Math.sqrt(2.0);        
            halfRoot2 = sqrt2 / 2.0;
            onePlusOver = (1.0 + halfRoot2);
            piOver180 = Math.PI / 180.0;
        // --------------------------------------------------------------------
    }
    
    public void completeTheDeal() {
        constructPieInfo(); 
        initializeGraphParams();
        setUpUI();
        setUpAnchorPane();
        dragableAnchorPane.heightProperty()
                          .addListener(ov-> {
                                radiiSlider.setValue(1.0);
                                doThePlot();
                          });
        
        dragableAnchorPane.widthProperty()
                          .addListener(ov-> {
                              radiiSlider.setValue(1.0);
                              doThePlot();});
        
        containingPane = dragableAnchorPane.getTheContainingPane(); 
        
        dragableAnchorPane.setOnMouseReleased(dragableAnchorPaneMouseHandler);
    }

    private void initializeGraphParams() {
        for (int lab = 0; lab < nColsCat; lab++) {
            topLabels = x2Assoc_Model.getTopLabels();
        }

        xAxis = new JustAnAxis(-0.05, 0.95);
        xAxis.setSide(Side.BOTTOM);

        xAxis.setLabel("This is xAxis");
        xAxis.setVisible(false);    //  Used only for positioning other stuff
        xAxis.forceLowScaleEndToBe(-0.05);
        xAxis.forceHighScaleEndToBe(0.95);

        yAxis = new JustAnAxis(0.0, 1.01);
        yAxis.forceLowScaleEndToBe(0.0);
        yAxis.forceHighScaleEndToBe(1.01);
        yAxis.setSide(Side.LEFT);

        yAxis.setVisible(false);    //  Used only for positioning other stuff
    }
    
    private void setUpUI() {
        segBarCanvas = new Canvas(0.95 * initWidth, 0.8 * initHeight);
        pieChartGC = segBarCanvas.getGraphicsContext2D();
        segBarCategoryBoxes = new HBox(nLittleSquares);
        segBarCategoryBoxes.setAlignment(Pos.CENTER);
        segBarCategoryBoxes.setStyle("-fx-padding: 2;"+
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
            littleSquaresText[i] = new Text(0, 0, leftLabels[i]);
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
            segBarCategoryBoxes.getChildren().add(squaresNText[i]);
        }       
    }
    
    private void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        segBarCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        segBarCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
        segBarAnchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    

        dragableAnchorPane.getTheAP()
                                 .getChildren()
                                 .addAll(txtTitle1, segBarCategoryBoxes, segBarCanvas, 
                                         yAxis, xAxis, radiiSlider, sliderControlText);
        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }

    public void doThePlot()
    {    
        double daXPosition, x1, y1, y2, angleSizeInProps, yPosition, yPositionUp,
                yPositionDown, daPreXPosition, xPosition, pixRadius,
                radiusPerWidth, radiusPerHeight, xxCosineHalfAngle, yySineHalfAngle,
                xxHalfCircleForPercent, yyHalfCircleForPercent;
        
        double radii;
        
        //Positions the up/down for circles
        yPositionUp = yAxis.getDisplayPosition(0.65);
        yPositionDown = yAxis.getDisplayPosition(0.20);
        
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double hBoxWidth = segBarCategoryBoxes.getWidth();
        
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        double hBoxEdge = (paneWidth - hBoxWidth) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();

        AnchorPane.setTopAnchor(txtTitle1, 0.0 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle1, 0.95 * tempHeight);
        
        /*
        AnchorPane.setTopAnchor(title2Text, 0.1 * tempHeight);
        AnchorPane.setLeftAnchor(title2Text, txt2Edge * tempWidth);
        AnchorPane.setRightAnchor(title2Text, txt2Edge * tempWidth);
        AnchorPane.setBottomAnchor(title2Text, 0.2 * tempHeight);
        */
        
        AnchorPane.setTopAnchor(segBarCategoryBoxes, 0.05 * tempHeight);
        AnchorPane.setLeftAnchor(segBarCategoryBoxes, hBoxEdge * tempWidth);
        AnchorPane.setRightAnchor(segBarCategoryBoxes, hBoxEdge * tempWidth);
        AnchorPane.setBottomAnchor(segBarCategoryBoxes, 0.90 * tempHeight);        
        
        AnchorPane.setTopAnchor(xAxis, 0.9 * tempHeight);
        AnchorPane.setLeftAnchor(xAxis, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(xAxis, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(xAxis, 0.05 * tempHeight);
        
        AnchorPane.setTopAnchor(yAxis, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(yAxis, 0.0 * tempWidth);
        AnchorPane.setRightAnchor(yAxis, 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(yAxis, 0.2 * tempHeight);
        
        AnchorPane.setTopAnchor(segBarCanvas, 0.11 * tempHeight);
        AnchorPane.setLeftAnchor(segBarCanvas, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(segBarCanvas, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(segBarCanvas, 0.10 * tempHeight);
       
        AnchorPane.setTopAnchor(radiiSlider, 0.95 * tempHeight);
        AnchorPane.setLeftAnchor(radiiSlider, 0.75 * tempWidth);
        AnchorPane.setRightAnchor(radiiSlider, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(radiiSlider, 0.0 * tempHeight);
        
        AnchorPane.setTopAnchor(sliderControlText, 0.96 * tempHeight);
        AnchorPane.setLeftAnchor(sliderControlText, 0.50 * tempWidth);
        AnchorPane.setRightAnchor(sliderControlText, 0.74 * tempWidth);
        AnchorPane.setBottomAnchor(sliderControlText, 0.01 * tempHeight);
        
        pieChartGC.clearRect(0, 0 , segBarCanvas.getWidth(), segBarCanvas.getHeight());
        pieChartGC.setLineWidth(2.5);
        pieChartGC.setFill(Color.BLACK);
        pieChartGC.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, 14));

        double fudgeFactorForWidth = 1.15;
        double fudgeFactorForHeight = 1.25;
        if (nColsCat % 2 == 0) {
            radiusPerWidth = fudgeFactorForWidth * segBarCanvas.getWidth() /(tempWidth * (nColsCat * onePlusOver));
        } else {
            radiusPerWidth = fudgeFactorForWidth * segBarCanvas.getWidth() / (tempWidth * (nColsCat * (nColsCat - 1.) * onePlusOver));
        }
        
        radiusPerHeight = fudgeFactorForHeight * segBarCanvas.getHeight() / (2. * tempHeight * (1. + sqrt2));
        
        // This will be modified via the slider
        initialCircleRadii = Math.min(radiusPerWidth, radiusPerHeight);
        radii = radiiSlider.getValue() * initialCircleRadii;
        pixRadius = radii * tempWidth;
        for (int col = 0; col < nColsCat; col++) {

            if (col % 2 == 0) {
                yPosition = yPositionUp;
            }
            else {
                yPosition = yPositionDown;  
            }
      
            daPreXPosition = ((double)col + 0.5)/((double)nColsCat + 1.0);
            daXPosition = xAxis.getDisplayPosition(daPreXPosition);

            for (int row = 0; row < nRowsCat; row++) {
                pieChartGC.setFill(graphColors[row]); 

                double preY1 = cumRowProps[row][col] / columnProps[col];
                double preY2 = cumRowProps[row + 1][col] / columnProps[col];

                angleSizeInProps = preY2 - preY1;

                double xx = daXPosition - pixRadius;
                double yy = yPosition - pixRadius;
                double ww = 2. * pixRadius;
                double hh = 2. * pixRadius;

                double startAngle = 360.0 * preY1;
                double arcAngle = 360.0 * angleSizeInProps;

                int xxInt = (int)Math.round(xx);
                int yyInt = (int)Math.round(yy);
                int wwInt = (int)Math.round(ww);
                int hhInt = (int)Math.round(hh);
                int intSA = (int)Math.round(startAngle);
                int intAA = (int)Math.round(arcAngle);
                pieChartGC.fillArc(xxInt, yyInt, wwInt, hhInt, intSA, intAA, ArcType.ROUND);                          
            }   //  End row
        }   //  End col
        
        for (int col = 0; col < nColsCat; col++) {
        
            if (col % 2 == 0) {
                yPosition = yPositionUp;
            }
            else {
                yPosition = yPositionDown;  
            }
            
            daPreXPosition = ((double)col + 0.5)/((double)nColsCat + 1.0);
            daXPosition = xAxis.getDisplayPosition(daPreXPosition);

            for (int row = 0; row < nRowsCat; row++) {
                double preY1 = cumRowProps[row][col] / columnProps[col];
                double preY2 = cumRowProps[row + 1][col] / columnProps[col];          

                double centerX = daXPosition;
                double centerY = yPosition;

                double halfAngleInProps = 0.5 * (preY1  + preY2);
                double halfAngle = 360 * halfAngleInProps;

                double xx = daXPosition - pixRadius;
                double yy = yPosition - pixRadius;
                double ww = 2. * pixRadius;
                double hh = 2. * pixRadius;

                double startAngle = 360.0 * preY1;

                int xxInt = (int)Math.round(xx);
                int yyInt = (int)Math.round(yy);
                int wwInt = (int)Math.round(ww);
                int hhInt = (int)Math.round(hh);
                int intSA = (int)Math.round(startAngle);

                double xxCosineStartAngle = Math.cos(startAngle * piOver180);
                double yySineStartAngle = Math.sin(startAngle* piOver180);

                xxCosineHalfAngle = Math.cos(halfAngle * piOver180);
                yySineHalfAngle = Math.sin(halfAngle* piOver180);

                double xxCircleEdge = centerX + 1.0 * pixRadius * xxCosineStartAngle;
                double yyCircleEdge = centerY - 1.0 * pixRadius * yySineStartAngle;

                double xxHalfCircleEdge = centerX + 1.01 * pixRadius * xxCosineHalfAngle;
                double yyHalfCircleEdge = centerY - 1.01 * pixRadius * yySineHalfAngle;

                double xxHalfCircleOtherEnd = centerX + 1.15 * pixRadius * xxCosineHalfAngle;
                double yyHalfCircleOtherEnd = centerY - 1.15 * pixRadius * yySineHalfAngle;

                pieChartGC.setStroke(Color.BLACK);
                pieChartGC.setLineWidth(1.6);
                pieChartGC.strokeLine(centerX, centerY, xxCircleEdge, yyCircleEdge);
                pieChartGC.strokeLine(xxHalfCircleEdge, yyHalfCircleEdge, 
                           xxHalfCircleOtherEnd, yyHalfCircleOtherEnd);

                // Print the percent
                if (xxCosineHalfAngle >= 0) {
                    xxHalfCircleForPercent = centerX + 1.17 * pixRadius * xxCosineHalfAngle;
                } else {
                    xxHalfCircleForPercent = centerX + 1.17 * pixRadius * xxCosineHalfAngle - 35;  
                }

                if (yySineHalfAngle >= 0) {
                    yyHalfCircleForPercent = centerY - 1.17 * pixRadius * yySineHalfAngle;
                } else { 
                    yyHalfCircleForPercent = centerY - 1.17 * pixRadius * yySineHalfAngle + 10;
                }            

                double percent = 100.0 * (preY1 - preY2);
                String strPercent = String.format("%4.1f", percent) + "%";
                pieChartGC.setFill(Color.BLACK);
                pieChartGC.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, 12));
                pieChartGC.fillText(strPercent, xxHalfCircleForPercent, yyHalfCircleForPercent);     
            }   //  End row
        }   //  End col

        pieChartGC.setStroke(Color.BLACK);
        // doTheYAxis();
        doTheXAxis();
        
    }   //  End doThePlot

    private void doTheXAxis() {
        double x1, x2, topLabelXValue, topLabelYValue, preTopLabelXValue;
        pieChartGC.setFill(Color.BLACK);
        for (int col = 0; col < nColsCat; col++) {  
            String stringToPrint = topLabels[col];
            int lenString = topLabels[col].length();
            if (lenString > 12) {
                stringToPrint = myStringUtilities.leftMostChars(stringToPrint, 12);
            }
            stringToPrint = myStringUtilities.centerTextInString(stringToPrint, 12);
            double pre_pre = ((double)col + 0.5)/((double)nColsCat + 1.0);
            preTopLabelXValue = pre_pre - .047 - 0.0025 * lenString;  //  Center string
            topLabelXValue = xAxis.getDisplayPosition(preTopLabelXValue);
            if (col % 2 > 0) { //  Odd column
                topLabelYValue = yAxis.getDisplayPosition(-0.16);
            } else {    //  Even column
                topLabelYValue = yAxis.getDisplayPosition(-0.10);
            }
            pieChartGC.fillText(stringToPrint, topLabelXValue, topLabelYValue);
        }
    }  
    
    private void constructPieInfo()
    {
        nRowsCat = x2Assoc_Model.getNumberOfRows();
        nColsCat = x2Assoc_Model.getNumberOfColumns();

        cumRowProps = new double[nRowsCat + 1][nColsCat + 1];
        cumRowProps = x2Assoc_Model.getCellCumProps();
        
        columnProps = new double[nColsCat];
        columnProps = x2Assoc_Model.getColumnProportions();
        
        preTopLabels = new String[nColsCat];
        leftLabels = new String[nRowsCat];
        
        strTopVariable = x2Assoc_Model.getTopVariable();
        strLeftVariable = x2Assoc_Model.getLeftVariable();
        preTopLabels = x2Assoc_Model.getTopLabels();
        leftLabels = x2Assoc_Model.getLeftLabels();
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
