/**************************************************
 *              SegmentedBarChartView             *
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


public class X2Assoc_SegBarChartView 
{
    // POJOs
    boolean dragging;
    
    double xMin, xMax, yMin, yMax, xRange, width, halfWidth,
           xPix_InitialPress, yPix_InitialPress, xPix_MostRecentDragPoint, 
           yPix_MostRecentDragPoint, newX_Lower, newX_Upper, newY_Lower, 
           newY_Upper, deltaX, deltaY, dispLowerBound, dispUpperBound,
           bigTickInterval, startSquareFactor_1, jumpSquareFactor_1, 
           startSquareFactor_2, jumpSquareFactor_2, daLeftEndOfLine, 
           daRightEndOfLine, initHoriz, initVert, initWidth,
           initHeight, maxXPosition, text1Width, text2Width;
    
    double[] columnProps;
    double[][] cumRowProps;
    
    int nRowsCat, nColsCat, nSquaresRow1, nSquaresRow2, nLittleSquares;

    String strTopVariable, strLeftVariable, strTitle, graphsCSS;
    String[] leftLabels, preTopLabels, topLabels;
    
    //  My classes
    DragableAnchorPane dragableAnchorPane;
    JustAnAxis xAxis, yAxis;
    MyX2StringUtilities myStringUtilities;
    X2Assoc_Dashboard x2Assoc_Dashboard;
    X2Assoc_Model x2Assoc_Model;
    
    //  POJOs / FX
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
    
    GraphicsContext segBarGC; // Required for drawing on the Canvas
    HBox segBarCategoryBoxes;
    HBox[] squaresNText;
    Pane segBarPane, containingPane;
    Rectangle[] littleSquares;
    Text txtTitle1, txtTitle2;
    Text[] littleSquaresText;    

    public X2Assoc_SegBarChartView(X2Assoc_Model association_Model, 
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
        
        txtTitle1 = new Text("Segmented Bar Chart"); 
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 14));        
        txtTitle1.getStyleClass().add("titleLabel");              
        txtTitle2 = new Text("Title2Text"); 
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 8));        
        txtTitle2.getStyleClass().add("titleLabel");   
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();
    }
    
    public void completeTheDeal() {
        constructSegBarInfo(); 
        initializeGraphParams();
        setUpUI();
        setUpAnchorPane();
        dragableAnchorPane.heightProperty().addListener(ov-> {doThePlot();});
        dragableAnchorPane.widthProperty().addListener(ov-> {doThePlot();});
        containingPane = dragableAnchorPane.getTheContainingPane(); 
        
        dragableAnchorPane.setOnMouseReleased(dragableAnchorPaneMouseHandler);
    }

    private void initializeGraphParams() {
        for (int lab = 0; lab < nColsCat; lab++) {
            topLabels = x2Assoc_Model.getTopLabels();
        }

        xAxis = new JustAnAxis(-0.15, 1.25);
        xAxis.setSide(Side.BOTTOM);

        xAxis.setLabel("This is xAxis");
        xAxis.setVisible(false);    //  Used only for positioning other stuff
        xAxis.forceLowScaleEndToBe(-0.15);
        xAxis.forceHighScaleEndToBe(1.25);

        yAxis = new JustAnAxis(0.0, 1.05);
        yAxis.forceLowScaleEndToBe(0.0);
        yAxis.forceHighScaleEndToBe(1.05);
        yAxis.setSide(Side.LEFT);

        yAxis.setVisible(false);    //  Used only for positioning other stuff
    }
    
    private void setUpUI() {
        segBarCanvas = new Canvas(0.95 * initWidth, 0.8 * initHeight);
        segBarGC = segBarCanvas.getGraphicsContext2D();
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
                                 .addAll(txtTitle1, segBarCategoryBoxes, segBarCanvas, yAxis, xAxis);
        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }

    public void doThePlot()
    {    
        double daXPosition, x1, y1, y2, height;
        
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
        AnchorPane.setBottomAnchor(txtTitle1, 0.15 * tempHeight);
        
        /*
        AnchorPane.setTopAnchor(title2Text, 0.1 * tempHeight);
        AnchorPane.setLeftAnchor(title2Text, txt2Edge * tempWidth);
        AnchorPane.setRightAnchor(title2Text, txt2Edge * tempWidth);
        AnchorPane.setBottomAnchor(title2Text, 0.2 * tempHeight);
        */
        
        AnchorPane.setTopAnchor(segBarCategoryBoxes, 0.10 * tempHeight);
        AnchorPane.setLeftAnchor(segBarCategoryBoxes, hBoxEdge * tempWidth);
        AnchorPane.setRightAnchor(segBarCategoryBoxes, hBoxEdge * tempWidth);
        AnchorPane.setBottomAnchor(segBarCategoryBoxes, 0.85 * tempHeight);        
        
        AnchorPane.setTopAnchor(xAxis, 0.9 * tempHeight);
        AnchorPane.setLeftAnchor(xAxis, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(xAxis, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(xAxis, 0.0 * tempHeight);
        
        AnchorPane.setTopAnchor(yAxis, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(yAxis, 0.0 * tempWidth);
        AnchorPane.setRightAnchor(yAxis, 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(yAxis, 0.2 * tempHeight);
        
        AnchorPane.setTopAnchor(segBarCanvas, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(segBarCanvas, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(segBarCanvas, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(segBarCanvas, 0.2 * tempHeight);
        
        segBarGC.clearRect(0, 0 , segBarCanvas.getWidth(), segBarCanvas.getHeight());
        segBarGC.setLineWidth(2.5);
        segBarGC.setFill(Color.BLACK);
        segBarGC.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, 14));
        
        for (int col = 0; col < nColsCat; col++) {
            double daPreXPosition = ((double)col + 0.5)/((double)nColsCat + 1.0);
            daXPosition = xAxis.getDisplayPosition(daPreXPosition);
            width = 40.;
            for (int row = 0; row < nRowsCat; row++) {
                segBarGC.setFill(graphColors[row]); 
                double preY1 = cumRowProps[row][col] / columnProps[col];
                double preY2 = cumRowProps[row + 1][col] / columnProps[col];

                y1 = yAxis.getDisplayPosition(preY1);                
                y2 = yAxis.getDisplayPosition(preY2);  
                height = (y2 - y1);
                
                segBarGC.fillRect(daXPosition - halfWidth, y1, width, height);   
                segBarGC.setStroke(Color.BLACK);
                segBarGC.strokeRect(daXPosition - halfWidth, y1, width, height);
            }   //  End row
        }   //  End col

        segBarGC.setStroke(Color.BLACK);
        doTheYAxis();
        doTheXAxis();
        
    }   //  End doThePlot

    private void doTheXAxis() {
        double x1, x2, topLabelXValue, topLabelYValue, preTopLabelXValue;
        segBarGC.setFill(Color.BLACK);
        for (int col = 0; col < nColsCat; col++) {  
            String stringToPrint = topLabels[col];
            int lenString = stringToPrint.length();
            if (lenString > 8) {
                stringToPrint = myStringUtilities.leftMostChars(stringToPrint, 8);
            }
            stringToPrint = myStringUtilities.centerTextInString(stringToPrint, 8);
            //  .01 is a hack hack to center the labels under the bars
            double pre_pre = ((double)col + 0.5)/((double)nColsCat + 1.0);
            preTopLabelXValue = pre_pre + .01 - 0.0025 * lenString ;  //  Hack to center string
            topLabelXValue = xAxis.getDisplayPosition(preTopLabelXValue);
            if (col % 2 > 0) { //  Odd column
                topLabelYValue = yAxis.getDisplayPosition(-0.14);
            } else {    //  Even column
                topLabelYValue = yAxis.getDisplayPosition(-0.08);
            }
            segBarGC.fillText(stringToPrint, topLabelXValue, topLabelYValue);
        }
    }    
    private void doTheYAxis() {
        segBarGC.setStroke(Color.BLACK);
        segBarGC.setFill(Color.BLACK);
        segBarGC.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, 14));
        segBarGC.setLineWidth(2);
        
        double xText = xAxis.getDisplayPosition(0.0) - 45.;
        double xText35 = xText + 38;
        double xText50 = xText + 46;

       double rightEnd = 0.0;

        String prop025 = "0.25";
        double yText025 = yAxis.getDisplayPosition(0.25) + 2.5;
        segBarGC.fillText(prop025, xText, yText025 + 2);
        segBarGC.strokeLine(xText35, yText025, xText50, yText025);
        
        String prop050 = "0.50";
        double yText050 = yAxis.getDisplayPosition(0.50) + 2.5;
        segBarGC.fillText(prop050, xText, yText050 + 2);
        segBarGC.strokeLine(xText35, yText050, xText50, yText050);
        
        String prop075 = "0.75";
        double yText075 = yAxis.getDisplayPosition(0.75) + 2.5;
        segBarGC.fillText(prop075, xText, yText075 + 2);
        segBarGC.strokeLine(xText35, yText075, xText50, yText075);
        
        String prop100 = "1.00";
        double yText100 = yAxis.getDisplayPosition(1.00) + 2.5;
        segBarGC.fillText(prop100, xText, yText100 + 2);
        segBarGC.strokeLine(xText35, yText100, xText50, yText100);
        
        //mosaicGC.strokeLine(xText50, yText000, xText50, yText100);
        segBarGC.setStroke(Color.BLACK);
        double leftXBaseLine = xAxis.getDisplayPosition(0.01);
        double rightXBaseLine = xAxis.getDisplayPosition(0.99);
        double bottomYBaseLine = yAxis.getDisplayPosition(0.0);
        double topYBaseLine = yAxis.getDisplayPosition(0.99);

        segBarGC.strokeLine(leftXBaseLine, bottomYBaseLine, 
                            rightXBaseLine, bottomYBaseLine - 0.5);        
        segBarGC.strokeLine(leftXBaseLine - 2., bottomYBaseLine, 
                            leftXBaseLine - 2., topYBaseLine + 1.);   
    }
    
    private void constructSegBarInfo()
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
