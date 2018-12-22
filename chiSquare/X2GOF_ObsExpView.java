/****************************************************************************
 *                       X2GOF_ObsExpView                                   * 
 *                           08/29/18                                       *
 *                            00:00                                         *
 ***************************************************************************/
package chiSquare;

import genericClasses.JustAnAxis;
import genericClasses.DragableAnchorPane;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;


public class X2GOF_ObsExpView {
    // POJOs
    boolean dragging;
    
    int nCategories;
    int[] observedValues;

    double initial_yMin, initial_yMax, initial_yRange, yMin, yMax, yRange,
           xPix_InitialPress, yPix_InitialPress, xPix_MostRecentDragPoint, 
           yPix_MostRecentDragPoint, newY_Lower, newY_Upper, deltaY,
           dispLowerBound, dispUpperBound, initHoriz, initVert, initWidth, 
           initHeight;
    double[] categoryCounts, expectedValues, resids, standResids;
    
    ObservableList<String> allTheLabels, categoryLabels; 

    // My classes
    DragableAnchorPane dragableAnchorPane;
    JustAnAxis yAxis;
    X2GOF_Dashboard gofDashboard;
    X2GOF_Model x2GOF_Model;    
    
    // POJOs / FX
    AnchorPane anchorPane;    
    Canvas obsExpCanvas;
    CategoryAxis xAxis;
    GraphicsContext gc; // Required for drawing on the Canvas
    HBox obsLabelHBox, expLabelHBox, obsExpLabelsHBox, obsExpHBox; 
    Pane containingPane;  
    Rectangle blueSquare, orangeSquare; 
    Text txtTitle, txtBlue, txtOrange;
    
    public X2GOF_ObsExpView(X2GOF_Model x2GOF_Model, X2GOF_Dashboard x2GOF_Dashboard,
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
        obsExpCanvas = new Canvas(initWidth, initHeight);
        gc = obsExpCanvas.getGraphicsContext2D();
        obsExpCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        obsExpCanvas.widthProperty().addListener(ov-> {doTheGraph();}); 
    }
    
    public void completeTheDeal() {
        initializeGraphParams();
        setUpUI();       
        setUpGridPane();        
        setHandlers();   
        containingPane = dragableAnchorPane.getTheContainingPane();  
        dragableAnchorPane.setOnMouseReleased(dragableAnchorPaneMouseHandler);        
    }
  
    private void setUpUI() {    
        blueSquare = new Rectangle(10, 10, 15, 15);
        blueSquare.setStroke(Color.BLUE);
        blueSquare.setFill(Color.BLUE);
        orangeSquare = new Rectangle(10, 10, 15, 15);
        orangeSquare.setStroke(Color.ORANGE);
        orangeSquare.setFill(Color.ORANGE);        
        txtTitle = new Text(250, 25, "Observed & Expected Counts");
        txtTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));  
        txtBlue = new Text(250, 50, "Observed");
        txtBlue.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,18));        
        txtOrange = new Text(250, 50, "Expected");
        txtOrange.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,18));        

        obsLabelHBox = new HBox(15.0, blueSquare, txtBlue);
        expLabelHBox = new HBox(15.0, orangeSquare, txtOrange);
        obsExpHBox = new HBox(40.0, obsLabelHBox, expLabelHBox); 
        obsExpHBox.setAlignment(Pos.CENTER);     
    }
    
        private void setUpGridPane() {
        dragableAnchorPane = new DragableAnchorPane();
        obsExpCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        obsExpCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        String graphsCSS = getClass().getResource("/css/Graphs.css").toExternalForm();
        dragableAnchorPane.getStylesheets().add(graphsCSS);  

        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(obsExpHBox, txtTitle, obsExpCanvas, xAxis, yAxis);
        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
            
    private void initializeGraphParams() {
        initial_yMin = 0.0;
        //  Find max observed and expected values for the vertical axis
        initial_yMax = 0.0;
        for (int i = 0; i < nCategories; i++) {
            if (observedValues[i] > initial_yMax)
                initial_yMax = observedValues[i];
            if (expectedValues[i] > initial_yMax)
                initial_yMax = expectedValues[i];            
        }
        
        anchorPane = new AnchorPane();
        
        initial_yMax *= 1.025; //  create room for title.  
        
        initial_yRange = initial_yMax - initial_yMin;
        yMin = initial_yMin;
        yMax = initial_yMax;
        yRange = initial_yRange;
        
        for (int iCats = 0; iCats < nCategories; iCats++) {
            categoryLabels.add(allTheLabels.get(iCats));
        }
    
        // This constant controls the rate of scale change when dragging
        deltaY = 0.005 * yRange;       
        xAxis = new CategoryAxis(categoryLabels);
        xAxis.setSide(Side.BOTTOM);
        xAxis.setAutoRanging(true);
        // xAxis.setLabel(/*   */);
        xAxis.setMinWidth(40);
        xAxis.setPrefWidth(40);
        
        xAxis.setPrefSize(anchorPane.getWidth() - 50, 40);
        xAxis.setLayoutX(500); xAxis.setLayoutY(50);
        
        yAxis = new JustAnAxis(yMin, yMax);
        yAxis.setSide(Side.LEFT);
        yAxis.setPrefSize(20, anchorPane.getHeight() - 50);
        yAxis.setLayoutX(500); yAxis.setLayoutY(50);
        yAxis.forceLowScaleEndToBe(0.0);           
    }

    public void doTheGraph() {
        
        double text1Width = txtTitle.getLayoutBounds().getWidth();
        double hBoxWidth = obsExpHBox.getWidth();
        double paneWidth = dragableAnchorPane.getWidth();

        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double hBoxEdge = (paneWidth - hBoxWidth) / (2 * paneWidth);       
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        
        AnchorPane.setTopAnchor(txtTitle, 0.0 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle, 0.1 * tempHeight);
                
        AnchorPane.setTopAnchor(obsExpHBox, 0.1 * tempHeight);
        AnchorPane.setLeftAnchor(obsExpHBox, hBoxEdge * tempWidth);
        AnchorPane.setRightAnchor(obsExpHBox, hBoxEdge * tempWidth);
        AnchorPane.setBottomAnchor(obsExpHBox, 0.2 * tempHeight);
        
        AnchorPane.setTopAnchor(xAxis, 0.9 * tempHeight);
        AnchorPane.setLeftAnchor(xAxis, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(xAxis, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(xAxis, 0.0 * tempHeight);
        
        AnchorPane.setTopAnchor(yAxis, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(yAxis, 0.0 * tempWidth);
        AnchorPane.setRightAnchor(yAxis, 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(yAxis, 0.1 * tempHeight);
        
        AnchorPane.setTopAnchor(obsExpCanvas, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(obsExpCanvas, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(obsExpCanvas, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(obsExpCanvas, 0.1 * tempHeight);
        
        gc.clearRect(0, 0 , obsExpCanvas.getWidth(), obsExpCanvas.getHeight());   
        double spacing = 100.0;
        double spaceFraction = 0.10 * spacing;  //  Controls widths and separations?
        gc.setLineWidth(2);
        
        for (int theCategory = 0; theCategory < nCategories; theCategory++)
        {
            //  daXPosition is generated by CategoryAxis when AutoRanging == true
            double daXPosition = xAxis.getDisplayPosition(categoryLabels.get(theCategory));

            //  Plot the observed rectangles
            double observedBarTop = yAxis.getDisplayPosition(observedValues[theCategory]);
            double observedBarHeight = yAxis.getDisplayPosition(0.0) - yAxis.getDisplayPosition(observedValues[theCategory]);        

            gc.setStroke(Color.BLUE);   // For observed counts
            gc.setFill(Color.BLUE);
            gc.fillRect(daXPosition - 4 * spaceFraction, observedBarTop, 4 * spaceFraction, observedBarHeight);
            
            //  Plot the expected rectangles
            double expectedBarTop = yAxis.getDisplayPosition(expectedValues[theCategory]);
            double expectedBarHeight = yAxis.getDisplayPosition(0.0) - yAxis.getDisplayPosition(expectedValues[theCategory]);           

            gc.setStroke(Color.ORANGE);   // For expected counts
            gc.setFill(Color.ORANGE);
            gc.fillRect(daXPosition, expectedBarTop, 4 * spaceFraction, expectedBarHeight);            
        }   //  Loop through batches 
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
                        if (!yAxis.getHasForcedHighScaleEnd())
                            newY_Upper = yAxis.getUpperBound() + deltaY;
                    }
                    else  
                    {
                        if (!yAxis.getHasForcedLowScaleEnd())
                            newY_Lower = yAxis.getLowerBound() + deltaY;
                    }
                }
                else 
                if ((yPix_Dragging < yPix_InitialPress) && (yPix_Dragging < yPix_MostRecentDragPoint))
                {   
                    if (frac < 0.5)
                    {
                        if (!yAxis.getHasForcedHighScaleEnd())
                            newY_Upper = yAxis.getUpperBound() - deltaY;
                    }
                    else
                    {
                        if (!yAxis.getHasForcedLowScaleEnd())
                            newY_Lower = yAxis.getLowerBound() - deltaY;
                    }
                }    

                if (yAxis.getHasForcedLowScaleEnd())
                    newY_Lower = yAxis.getForcedLowScaleEnd();
            
                if (yAxis.getHasForcedHighScaleEnd())
                    newY_Upper = yAxis.getForcedHighScaleEnd();
                
                yAxis.setLowerBound(newY_Lower ); 
                yAxis.setUpperBound(newY_Upper ); 

                dispLowerBound = yAxis.getDisplayPosition(yAxis.getLowerBound());
                dispUpperBound = yAxis.getDisplayPosition(yAxis.getUpperBound());
                
                yPix_MostRecentDragPoint = mouseEvent.getY();
                doTheGraph();
            }   // end if mouse dragged
        }   //  end handle
    };   
     
    EventHandler<MouseEvent> dragableAnchorPaneMouseHandler = new EventHandler<MouseEvent>() 
    {
        @Override
        public void handle(MouseEvent mouseEvent) 
        {
              
        }
    }; 

    public Pane getTheContainingPane() { return containingPane; }
}

