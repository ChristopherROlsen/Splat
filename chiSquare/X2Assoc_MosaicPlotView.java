/**************************************************
 *                 MosaicPlotView                 *
 *                    05/15/18                    *
 *                      15:00                     *
 *************************************************/

package chiSquare;

import genericClasses.JustAnAxis;
import genericClasses.DragableAnchorPane;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class X2Assoc_MosaicPlotView {
    
    // POJOs
    boolean dragging;
    
    double xMin, xMax, yMin, yMax, xRange, yRange, xPix_InitialPress, 
           yPix_InitialPress, xPix_MostRecentDragPoint, yPix_MostRecentDragPoint,
           newX_Lower, newX_Upper, newY_Lower, newY_Upper, deltaX, deltaY,
           dispLowerBound, dispUpperBound, initHoriz, initVert, initWidth,
           initHeight, text1Width, text2Width, maxXPosition;
    
    double[] cumRowProps, cumColProps, cumMarginalRowProps, columnProps;
    double[][] cumProps;
    
    int nRowsCat, nColsCat, nSquaresRow1, nSquaresRow2, nLittleSquares;
    
    String strTopVariable, strLeftVariable, graphsCSS;
    String[] leftLabels, preTopLabels, topLabels;
    
    // My classes
    DragableAnchorPane dragableAnchorPane;   
    JustAnAxis xAxis, yAxis;
    MyX2StringUtilities myStringUtilities;
    X2Assoc_Model x2Assoc_Model;
    X2Assoc_Dashboard x2Assoc_Dashboard;    
    
    // POJOs / FX 
    AnchorPane anchorPane;
    Canvas mosaicCanvas;
    
/*****************************************************************************
 * GREEN is last b/c color blind folks have trouble with RED / GREEN,        *
 * so hopefully Red and Green should seldom appear.  Probably these colors   *
 * should be arranged so that the most distinguishable ones are early.       *
 ****************************************************************************/
    Color[] graphColors = { Color.BLUE, Color.RED, Color.SADDLEBROWN, 
                            Color.TURQUOISE, Color.BLUEVIOLET, 
                            Color.ALICEBLUE, Color.LIGHTCORAL, Color.GREEN};

    GraphicsContext mosaicGC;     
    HBox mosaicCategoryBoxes;    
    HBox[] squaresNText;
    Label xLabelPad, yLabelPad, xScalePad, yScalePad, titlePad, title;    
    Pane mosaicPane, containingPane, xAxisPane;
    Rectangle[] littleSquares;
    Text txtTitle1, txtTitle2;
    Text[] littleSquaresText;

    public X2Assoc_MosaicPlotView(X2Assoc_Model association_Model, 
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
        
        txtTitle1 = new Text("Mosaic Plot"); 
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 14));        
        txtTitle1.getStyleClass().add("titleLabel");              
        txtTitle2 = new Text("Title2Text"); 
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 8));        
        txtTitle2.getStyleClass().add("titleLabel");   
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();            
    }
    
    public void completeTheDeal() {
        constructMosaicInfo();
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
        mosaicCanvas = new Canvas(0.95 * initWidth, 0.8 * initHeight);
        mosaicGC = mosaicCanvas.getGraphicsContext2D();   
        
        mosaicCategoryBoxes = new HBox(nLittleSquares);
        mosaicCategoryBoxes.setAlignment(Pos.CENTER);
        mosaicCategoryBoxes.setStyle("-fx-padding: 2;"+
                                     "-fx-border-style: solid inside;" +
                                     "-fx-border-width: 0;" +
                                     "-fx-border-insets: 5;"+
                                     "-border-radius: 5;");
        
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
            
            mosaicCategoryBoxes.getChildren().add(squaresNText[i]);
        } 
    }
    
    private void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        mosaicCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        mosaicCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
        
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    

        dragableAnchorPane.getTheAP()
                                 .getChildren()
                                 .addAll(txtTitle1, mosaicCategoryBoxes, mosaicCanvas, yAxis, xAxis);
        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    public void doThePlot()
    {    
        double daXPosition, x1, y1, x2, y2, height, width, label_x;
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double hBoxWidth = mosaicCategoryBoxes.getWidth();
        
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
        AnchorPane.setLeftAnchor(title2Text, txt2Edge * draggableAnchorPane.getWidth());
        AnchorPane.setRightAnchor(title2Text, txt2Edge * draggableAnchorPane.getWidth());
        AnchorPane.setBottomAnchor(title2Text, 0.2 * tempHeight);
        */
        
        AnchorPane.setTopAnchor(mosaicCategoryBoxes, 0.10 * tempHeight);
        AnchorPane.setLeftAnchor(mosaicCategoryBoxes, hBoxEdge * tempWidth);
        AnchorPane.setRightAnchor(mosaicCategoryBoxes, hBoxEdge * tempWidth);
        AnchorPane.setBottomAnchor(mosaicCategoryBoxes, 0.85 * tempHeight);        
        
        AnchorPane.setTopAnchor(xAxis, 0.9 * tempHeight);
        AnchorPane.setLeftAnchor(xAxis, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(xAxis, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(xAxis, 0.0 * tempHeight);
        
        AnchorPane.setTopAnchor(yAxis, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(yAxis, 0.0 * tempWidth);
        AnchorPane.setRightAnchor(yAxis, 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(yAxis, 0.2 * tempHeight);
        
        AnchorPane.setTopAnchor(mosaicCanvas, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(mosaicCanvas, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(mosaicCanvas, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(mosaicCanvas, 0.2 * tempHeight);
        
        mosaicGC.clearRect(0, 0 , mosaicCanvas.getWidth(), mosaicCanvas.getHeight());
        mosaicGC.setLineWidth(3);
        mosaicGC.setFill(Color.BLACK);
        mosaicGC.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, 12));   
        
        maxXPosition = 0.0; // for horiz line under the plot 
        for (int col = 0; col < nColsCat; col++) {
            x1 = xAxis.getDisplayPosition(cumColProps[col]);   
            x2 = xAxis.getDisplayPosition(cumColProps[col + 1]);  

            width = x2 - x1;
            for (int row = 0; row < nRowsCat; row++) {
            mosaicGC.setFill(graphColors[row]); 
                
                double preY1 = cumProps[row][col] / columnProps[col];
                double preY2 = cumProps[row + 1][col] / columnProps[col];

                y1 = yAxis.getDisplayPosition(preY1);                
                y2 = yAxis.getDisplayPosition(preY2);  
                height = (y2 - y1);

                mosaicGC.fillRect(x1, y1, width, height);   
                mosaicGC.setStroke(Color.WHITE);
                mosaicGC.strokeRect(x1, y1, width, height);
            }   //  End row
        }   //  End col
        
        mosaicGC.setStroke(Color.BLACK);
        mosaicGC.setFill(Color.BLACK);
        mosaicGC.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, 14));
        mosaicGC.setLineWidth(2);
        
        double xText = xAxis.getDisplayPosition(0.0) - 45.;
        double xText35 = xText + 38;
        double xText50 = xText + 46;

        String prop025 = "0.25";
        double yText025 = yAxis.getDisplayPosition(0.25) + 2.5;
        mosaicGC.fillText(prop025, xText, yText025 + 2);
        mosaicGC.strokeLine(xText35, yText025, xText50, yText025);
        
        String prop050 = "0.50";
        double yText050 = yAxis.getDisplayPosition(0.50) + 2.5;
        mosaicGC.fillText(prop050, xText, yText050 + 2);
        mosaicGC.strokeLine(xText35, yText050, xText50, yText050);
        
        String prop075 = "0.75";
        double yText075 = yAxis.getDisplayPosition(0.75) + 2.5;
        mosaicGC.fillText(prop075, xText, yText075 + 2);
        mosaicGC.strokeLine(xText35, yText075, xText50, yText075);
        
        String prop100 = "1.00";
        double yText100 = yAxis.getDisplayPosition(1.00) + 2.5;
        mosaicGC.fillText(prop100, xText, yText100 + 2);
        mosaicGC.strokeLine(xText35, yText100, xText50, yText100);
        
        mosaicGC.setStroke(Color.BLACK);
        double leftXBaseLine = xAxis.getDisplayPosition(0.01);
        double rightXBaseLine = xAxis.getDisplayPosition(0.99);
        double bottomYBaseLine = yAxis.getDisplayPosition(0.0);
        double topYBaseLine = yAxis.getDisplayPosition(0.99);

        mosaicGC.strokeLine(leftXBaseLine, bottomYBaseLine - 1., 
                            rightXBaseLine, bottomYBaseLine - 1.);        
        mosaicGC.strokeLine(leftXBaseLine - 2., bottomYBaseLine, 
                            leftXBaseLine - 2., topYBaseLine + 1.);        
        doTheMarginalPlot();
        doTheXAxis();
    }   //  End doThePlot
    
    private void doTheMarginalPlot() {

        double mPlotx1, mPloty1, mPlotx2, mPloty2, mPlotHeight, mPlotWidth;
        //  Marginal rows
        mPlotx1 = xAxis.getDisplayPosition(1.05);   
        mPlotx2 = xAxis.getDisplayPosition(1.15);
       
        mPlotWidth = mPlotx2 - mPlotx1;
        for (int row = 0; row < nRowsCat; row++) {
            mosaicGC.setFill(graphColors[row]); 
            mPloty1 = yAxis.getDisplayPosition(cumMarginalRowProps[row]);                
            mPloty2 = yAxis.getDisplayPosition(cumMarginalRowProps[row + 1]);  

            //  Labels for cumulative proportions
            double ylabel_x = mPlotx2 + 2;
            double labelHeight = (mPloty1 + mPloty2)/2.0;
            mosaicGC.fillText(leftLabels[row], ylabel_x, labelHeight + 2);
            
            mPlotHeight = mPloty2 - mPloty1;

            mosaicGC.fillRect(mPlotx1, mPloty1, mPlotWidth, mPlotHeight);   
            mosaicGC.setStroke(Color.WHITE);
            mosaicGC.strokeRect(mPlotx1, mPloty1, mPlotWidth, mPlotHeight);
        }   //  End row               
    }
    
    private void doTheXAxis() {
        double x1, x2, topLabelXValue, topLabelYValue, preTopLabelXValue;
        mosaicGC.setFill(Color.BLACK);
        for (int col = 0; col < nColsCat; col++) {
            
            x1 = cumColProps[col];   
            x2 = cumColProps[col + 1];  
            String stringToPrint = topLabels[col];
            int lenString = stringToPrint.length();
            if (lenString > 8) {
                stringToPrint = myStringUtilities.leftMostChars(stringToPrint, 8);
            }
            stringToPrint = myStringUtilities.centerTextInString(stringToPrint, 8);
            //  .015 is a hack hack to center the labels under the bars
            preTopLabelXValue = (x1 + x2) / 2. - 0.02 - 0.0025 * lenString;  //  Hack to center string
            topLabelXValue = xAxis.getDisplayPosition(preTopLabelXValue);
            if (col % 2 > 0) { //  Odd column
                topLabelYValue = yAxis.getDisplayPosition(-0.14);
            } else {    //  Even column
                topLabelYValue = yAxis.getDisplayPosition(-0.08);
            }
            mosaicGC.fillText(stringToPrint, topLabelXValue, topLabelYValue);
        }
    }
    
    
    EventHandler<MouseEvent> dragableAnchorPaneMouseHandler = new EventHandler<MouseEvent>() 
    {
        @Override
        public void handle(MouseEvent mouseEvent) 
        {

        }
    }; 
    
    private void constructMosaicInfo()
    {
        xMin = -.10; xMax = 1.40; yMin = -0.15; yMax = 1.0;
        xRange = xMax - xMin; yRange = yMax - yMin;  
        nRowsCat = x2Assoc_Model.getNumberOfRows();
        nColsCat = x2Assoc_Model.getNumberOfColumns();
        cumRowProps = new double[nRowsCat + 1];
        cumRowProps = x2Assoc_Model.getCumRowProps(); 
        columnProps = new double[nColsCat];
        columnProps = x2Assoc_Model.getColumnProportions();      
        
        cumMarginalRowProps = new double[nRowsCat + 1];
        cumMarginalRowProps = x2Assoc_Model.getCumMarginalRowProps();  
        cumColProps = new double[nColsCat + 1];
        cumColProps = x2Assoc_Model.getCumColProps();
  
        cumProps = new double[nRowsCat + 1][nColsCat + 1];
        cumProps = x2Assoc_Model.getCellCumProps();
        
        topLabels = new String[nColsCat];
        leftLabels = new String[nRowsCat];
        strTopVariable = x2Assoc_Model.getTopVariable();
        strLeftVariable = x2Assoc_Model.getLeftVariable();
        leftLabels = x2Assoc_Model.getLeftLabels();
    }  
    
    public Pane getTheContainingPane() { return containingPane; }
}