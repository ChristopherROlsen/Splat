/****************************************************************************
 *                        ResidualsView                                     * 
 *                           05/15/18                                       *
 *                            15:00                                         *
 ***************************************************************************/
package chiSquare;

import genericClasses.JustAnAxis;
import genericClasses.DragableAnchorPane;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.collections.FXCollections;
import javafx.scene.chart.CategoryAxis;

public class X2GOF_ResidualsView {
    // POJOs
    boolean dragging;
    
    int nCategories;
    int[] observedValues;
    
    double initial_yMin, initial_yMax, initial_yRange, yMin, yMax, yRange,
           residScaleBoundary, sig05Line, xPix_InitialPress, yPix_InitialPress, 
           xPix_MostRecentDragPoint, yPix_MostRecentDragPoint, newY_Lower, 
           newY_Upper, deltaY, dispLowerBound, dispUpperBound, initHoriz, 
           initVert, initWidth, initHeight;
    double[] expectedValues, resids, standResids;
    
    ObservableList<String> allTheLabels; 
    ObservableList<String> categoryLabels;
    
    // My classes
    AnchorPane anchorPane;
    DragableAnchorPane dragableAnchorPane;   
    JustAnAxis yAxis;
    X2GOF_Model x2GOF_Model;    
    
    // POJOs / FX
    Canvas residsCanvas;
    CategoryAxis xAxis;
    GraphicsContext gc; // Required for drawing on the Canvas
    Pane containingPane;
    Text txtTitle1, txtTitle2;

    public X2GOF_ResidualsView(X2GOF_Model x2GOF_Model, X2GOF_Dashboard gofDashboard, 
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {

        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        
        allTheLabels = FXCollections.observableArrayList();
        categoryLabels = FXCollections.observableArrayList();
        allTheLabels = x2GOF_Model.getCategoryLabels();
        
        this.x2GOF_Model = x2GOF_Model;
        nCategories = x2GOF_Model.getNCategories(); 
        observedValues = x2GOF_Model.getObservedCounts();
        expectedValues = x2GOF_Model.getExpectedValues();
        resids = x2GOF_Model.getResids();        
        standResids = x2GOF_Model.getStandResids(); 
        
        containingPane = new Pane();
        residsCanvas = new Canvas(initWidth, initHeight);

        gc = residsCanvas.getGraphicsContext2D(); 
        residsCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        residsCanvas.widthProperty().addListener(ov-> {doTheGraph();});

        residScaleBoundary = 3.00;
        sig05Line = 1.96;
    }
    
    public void completeTheDeal() {
        initializeGraphParameters();
        setUpUI();       
        setUpGridPane();        
        setHandlers();    
        containingPane = dragableAnchorPane.getTheContainingPane(); 
        dragableAnchorPane.setOnMouseReleased(dragableAnchorPaneMouseHandler);
    }
    
    private void setUpUI() {
        txtTitle1 = new Text(50, 25, "Standardized residuals");
        txtTitle2 = new Text (60, 45, "Bars indicate significance at the .05 level");
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15));      
    }

    private void setUpGridPane() {
        dragableAnchorPane = new DragableAnchorPane();
        residsCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        residsCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        String graphsCSS = getClass().getResource("/css/Graphs.css").toExternalForm();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    

        dragableAnchorPane.getTheAP()
                                 .getChildren()
                                 .addAll(txtTitle1, txtTitle2, xAxis, yAxis, residsCanvas);
        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    private void initializeGraphParameters() {

        initial_yMin = 0.0; initial_yMax = 0.0;

        for (int i = 0; i < nCategories; i++) {
            if ( Math.abs(standResids[i]) > residScaleBoundary)
                residScaleBoundary = Math.abs(standResids[i]);
        }
        anchorPane = new AnchorPane();
        residScaleBoundary = residScaleBoundary + 0.25; //  Keep bars from title       
        initial_yRange = initial_yMax - initial_yMin;
        yMin = -residScaleBoundary; yMax = residScaleBoundary;
        yRange = initial_yRange;
        
        for (int iCats = 0; iCats < nCategories; iCats++) {
            categoryLabels.add(allTheLabels.get(iCats));
        }

        xAxis = new CategoryAxis(categoryLabels);
        xAxis.setSide(Side.BOTTOM);
        xAxis.setAutoRanging(true);
        // xAxis.setLabel(/*   */);
        xAxis.setMinWidth(40);
        xAxis.setPrefWidth(40);
        
        xAxis.setPrefSize(anchorPane.getWidth() - 50, 40);
        xAxis.setLayoutX(500); xAxis.setLayoutY(50);
        
        // This constant controls the rate of scale change when dragging
        deltaY = 0.005 * yRange;
        yAxis = new JustAnAxis(yMin, yMax);
        yAxis.setSide(Side.LEFT);
        yAxis.setPrefSize(20, anchorPane.getHeight() - 50);
        yAxis.setLayoutX(500); yAxis.setLayoutY(50);
        // yAxis.setLabel( /*   */);
    }

    public void doTheGraph() {
        double standResidBarTop, standResidBarHeight, xStart, xStop, yStart, yStop;
        
        double text1Width = txtTitle1.getLayoutBounds().getWidth();
        double text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = containingPane.getWidth();

        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);        
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        AnchorPane.setTopAnchor(txtTitle1, 0.0 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle1, 0.1 * tempHeight);
                
        AnchorPane.setTopAnchor(txtTitle2, 0.1 * tempHeight);
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
        
        AnchorPane.setTopAnchor(residsCanvas, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(residsCanvas, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(residsCanvas, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(residsCanvas, 0.1 * tempHeight);
        
        gc.clearRect(0, 0 , residsCanvas.getWidth(), residsCanvas.getHeight());        
        double spacing = 100.0;
        double spaceFraction = 0.10 * spacing;  //  Controls widths and separations?
        
        xStart = xAxis.getDisplayPosition(categoryLabels.get(0)) - 0.5 * spacing;
        xStop = xAxis.getDisplayPosition(categoryLabels.get(nCategories - 1)) + 0.5 * spacing;
        
        yStart = yAxis.getDisplayPosition(0.0);
        yStop = yAxis.getDisplayPosition(0.0);            
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
        gc.fillRect(xStart - 2, yStart - 2, xStop - xStart, 2);        
        gc.setLineWidth(2);
        
        for (int theCategory = 0; theCategory < nCategories; theCategory++)
        {
            //  daXPosition is generated by CategoryAxis when AutoRanging == true
            double daXPosition = xAxis.getDisplayPosition(categoryLabels.get(theCategory));

            //  Plot the observed rectangles
            if (standResids[theCategory] > 0) {
                standResidBarTop = yAxis.getDisplayPosition(standResids[theCategory]);
                standResidBarHeight = yAxis.getDisplayPosition(0.0) - yAxis.getDisplayPosition(standResids[theCategory]);  
            } else
            {
                standResidBarTop = yAxis.getDisplayPosition(0.0);
                standResidBarHeight = yAxis.getDisplayPosition(standResids[theCategory]) - yAxis.getDisplayPosition(0.0);
            }
            
            gc.setStroke(Color.BLUEVIOLET); 
            gc.setFill(Color.BLUEVIOLET);
            gc.fillRect(daXPosition - 2 * spaceFraction, standResidBarTop, 4 * spaceFraction, standResidBarHeight);           
        }   //  Loop through batches   
        
        gc.setStroke(Color.RED);
        gc.setFill(Color.RED);
        
        yStart = yAxis.getDisplayPosition(sig05Line);
        yStop = yAxis.getDisplayPosition(sig05Line);            
        gc.fillRect(xStart - 1, yStart - 1, xStop - xStart, 2);         
        
        yStart = yAxis.getDisplayPosition(-sig05Line);
        yStop = yAxis.getDisplayPosition(-sig05Line);            
        gc.fillRect(xStart - 1, yStart - 1, xStop - xStart, 2);      
    }
    
    EventHandler<MouseEvent> dragableAnchorPaneMouseHandler = new EventHandler<MouseEvent>() 
    {
        @Override
        public void handle(MouseEvent mouseEvent) 
        {
            //  Stub for future use
        }
    }; 

    private void setHandlers() {       
        yAxis.setOnMouseDragged(yAxisMouseHandler); 
        yAxis.setOnMousePressed(yAxisMouseHandler); 
        yAxis.setOnMouseReleased(yAxisMouseHandler); 
    }
  
     EventHandler<MouseEvent> yAxisMouseHandler = new EventHandler<MouseEvent>() 
    {
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
            if(mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED)
            {
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
                        newY_Upper = yAxis.getUpperBound() + deltaY;
                    }
                    else  
                    {
                        newY_Lower = yAxis.getLowerBound() + deltaY;
                    }
                }
                else 
                if ((yPix_Dragging < yPix_InitialPress) && (yPix_Dragging < yPix_MostRecentDragPoint))
                {   
                    if (frac < 0.5)
                    {
                        newY_Upper = yAxis.getUpperBound() - deltaY;
                    }
                    else
                    {
                        newY_Lower = yAxis.getLowerBound() - deltaY;
                    }
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
    
    public Pane getTheContainingPane() { return containingPane; }
}

