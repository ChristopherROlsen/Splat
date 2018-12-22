/**************************************************
 *            ANOVA2_MainEffect_AView             *
 *                   05/15/18                     *
 *                     12:00                      *
 *************************************************/
package ANOVA_Two;

import genericClasses.UnivariateContinDataObj;
import genericClasses.JustAnAxis;
import genericClasses.DragableAnchorPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class ANOVA2_MainEffect_AView extends Region { 
   
    // POJOs
    boolean dragging;
    
    int nFactorA_Levels, nFactorB_Levels, nDataPoints;
    
    double  initial_yMin, initial_yMax, initial_yRange, yMin, yMax, yRange,
            xPix_InitialPress, yPix_InitialPress, xPix_MostRecentDragPoint, 
            yPix_MostRecentDragPoint, newX_Lower, newX_Upper, newY_Lower, 
            newY_Upper, deltaX, deltaY, dispLowerBound, dispUpperBound, 
            bottomOfLowWhisker, topOfHighWhisker, tempPos0, tempPos1, 
            bigTickInterval, confidenceLevel, startSquareFactor_1, 
            jumpSquareFactor_1, startSquareFactor_2, jumpSquareFactor_2,
            initHoriz, initVert, initWidth, initHeight, daMiddleXPosition,
            text1Width, text2Width; 
    
    String graphsCSS;
    String  strTitle1, strTitle2, strResponseVar,
            strFactorA, strFactorB;
    ObservableList<String> preStrTopLabels, preStrLeftLabels, strTopLabels, 
                           strLeftLabels,  categoryLabels, factorA_Levels, 
                           factorB_Levels;
    
    // My objects
    ANOVA2_Model anova2_Model;
    DragableAnchorPane dragableAnchorPane;
    JustAnAxis yAxis;   
    UnivariateContinDataObj tempUCDO;
 
    // FX Objects
    AnchorPane mainEffectA_AnchorPane, anchorTitleInfo;
    Canvas anova2Canvas;
    CategoryAxis xAxis;
    GraphicsContext anova2GC;
    Label xLabelPad, yLabelPad, xScalePad, yScalePad, titlePad;  
    Pane containingPane;
    Text txtTitle1, txtTitle2;

    ANOVA2_MainEffect_AView(ANOVA2_Model anova2_Model, 
            ANOVA2_Dashboard anova2_Dashboard,
            double placeHoriz, double placeVert,
            double withThisWidth, double withThisHeight) {
        
        this.anova2_Model = anova2_Model;
        
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        
        nFactorA_Levels = anova2_Model.str_ALevels.size();
        nFactorB_Levels = anova2_Model.str_BLevels.size();
        
        strTopLabels = FXCollections.observableArrayList();
        
        graphsCSS = getClass().getResource("/css/Graphs.css").toExternalForm();
        containingPane = new Pane();
        
        nFactorA_Levels = anova2_Model.str_ALevels.size();
        nFactorB_Levels = anova2_Model.str_BLevels.size();
        
        categoryLabels = FXCollections.observableArrayList();        
        factorA_Levels = FXCollections.observableArrayList();
        factorB_Levels = FXCollections.observableArrayList();  
        strTopLabels = FXCollections.observableArrayList();
        strLeftLabels = FXCollections.observableArrayList();
        
        preStrTopLabels = anova2_Model.getFactorALevels();
        preStrLeftLabels = anova2_Model.getFactorBLevels();
        int nTopLabels = preStrTopLabels.size() - 1;
        int nLeftLabels = preStrLeftLabels.size() - 1;
       
        for (int ithTopLabel = 0; ithTopLabel < nTopLabels; ithTopLabel++) {
            strTopLabels.add(preStrTopLabels.get(ithTopLabel + 1)); 
        }
        
        for (int ithLeftLabel = 0; ithLeftLabel < nLeftLabels; ithLeftLabel++) {
            strLeftLabels.add(preStrLeftLabels.get(ithLeftLabel + 1));
        }
        
        categoryLabels.addAll(strTopLabels);
        factorA_Levels.addAll(anova2_Model.getFactorALevels());
        factorB_Levels.addAll(anova2_Model.getFactorBLevels()); 
    }
    
    public void completeTheDeal() {
        initializeGraphParams();
        setUpUI();
        setUpAnchorPane();
        dragableAnchorPane.heightProperty().addListener(ov-> {doThePlot();});
        dragableAnchorPane.widthProperty().addListener(ov-> {doThePlot();});
        containingPane = dragableAnchorPane.getTheContainingPane(); 
        dragableAnchorPane.setOnMouseReleased(dragableAnchorPaneMouseHandler);
    }
    
    private void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        anova2Canvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        anova2Canvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
        mainEffectA_AnchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    

        dragableAnchorPane.getTheAP()
                                 .getChildren()
                                 .addAll(anchorTitleInfo, anova2Canvas, xAxis, yAxis);
        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    private void setUpUI() {        
        anova2Canvas = new Canvas(0.95 * initWidth, 0.8 * initHeight);
        anova2GC = anova2Canvas.getGraphicsContext2D();        
    }
        
    public void doThePlot() {
        double xx0, yy0, xx, yy;
        xx0 = 0.0; yy0 = 0.0;   //  Satisfy the compiler
       
        double daXPosition, x1, y1, y2, height;
        
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double hBoxWidth = paneWidth;
        
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
        
        anova2GC.setStroke(Color.BLACK);
        anova2GC.setLineWidth(2);
        tempPos1 = xAxis.getDisplayPosition(factorA_Levels.get(1));      
        tempPos0 = xAxis.getDisplayPosition(factorA_Levels.get(0));      
        bigTickInterval = tempPos1 - tempPos0;
     
        // positionTopInfo();    
        anova2GC.clearRect(0, 0 , anova2Canvas.getWidth(), anova2Canvas.getHeight());

        for (int theBetweenBatch = 1; theBetweenBatch < nFactorA_Levels; theBetweenBatch++)
        {
            daMiddleXPosition = xAxis.getDisplayPosition(factorA_Levels.get(theBetweenBatch));
            int theAppropriateLevel = theBetweenBatch;
            tempUCDO = anova2_Model.getPrelimA().getIthUCDO(theAppropriateLevel);   
            nDataPoints = tempUCDO.getLegalN();
            anova2GC.setLineWidth(2); 
            double theMean = tempUCDO.getTheMean();
            xx = daMiddleXPosition;
            yy = yAxis.getDisplayPosition(theMean); 
            anova2GC.fillOval(xx - 6, yy - 6, 12, 12);
            
            if (theBetweenBatch == 1) {
                xx0 = xx;
                yy0 = yy;
                anova2GC.moveTo(xx0, yy0);
            } 
            else
            {
                anova2GC.setStroke(Color.BLACK);
                anova2GC.setLineWidth(2);
                
                anova2GC.strokeLine(xx, yy, xx0, yy0);
                xx0 = xx;
                yy0 = yy;
            }
        }   //  Loop through between batches 
    }   // End Plot
   
    private void initializeGraphParams() { 
        /****************************************************************
        * Find range of means for both levels.  This will determine the *
        * initial vertical scale limits.                                *
        ****************************************************************/
        
        anchorTitleInfo = new AnchorPane();
        strTitle1 = "Main A, Title1Text";
        strTitle2 = "Main A, Title2Text";
        txtTitle1 = new Text(strTitle1); 
        //  title1Text.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 18));        
        txtTitle1.getStyleClass().add("titleLabel");              
        txtTitle2 = new Text("Title2Text"); 
        // title2Text.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 16));        
        txtTitle2.getStyleClass().add("titleLabel");   
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth(); 
        anchorTitleInfo.getChildren().addAll(txtTitle1, txtTitle2);
        
        //initial_yMin = anova2_Model.getMinVertical();
        //initial_yMax = anova2_Model.getMaxVertical();
        //initial_yRange = initial_yMax - initial_yMin;
        
        initial_yMin = Double.MAX_VALUE;
        initial_yMax = Double.MIN_VALUE;
  
        for (int levelsA = 1; levelsA < nFactorA_Levels; levelsA++) {
            tempUCDO = anova2_Model.getPrelimA().getIthUCDO(levelsA);
            double tempMean = tempUCDO.getTheMean();
            initial_yMin = Math.min(tempMean, initial_yMin);
            initial_yMax = Math.max(tempMean, initial_yMax);
        }

        for (int levelsB = 1; levelsB < nFactorB_Levels; levelsB++) {
            tempUCDO = anova2_Model.getPrelimB().getIthUCDO(levelsB);
            double tempMean = tempUCDO.getTheMean();
            initial_yMin = Math.min(tempMean, initial_yMin);
            initial_yMax = Math.max(tempMean, initial_yMax);
        }        
       
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
        xAxis.setLabel(anova2_Model.getFactorALabel());
        xAxis.setMinWidth(40);  //  Controls the Min X Axis width (for labels)
        xAxis.setPrefWidth(40);   
        xAxis.setLayoutX(500); xAxis.setLayoutY(25); 
        yAxis.setLayoutX(500); yAxis.setLayoutY(50);
        yAxis.setLabel(anova2_Model.getResponseLabel());
        
        setHandlers();
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
    
    EventHandler<MouseEvent> dragableAnchorPaneMouseHandler = new EventHandler<MouseEvent>() 
    {
        @Override
        public void handle(MouseEvent mouseEvent) 
        {

        }
    }; 

    public Pane getTheContainingPane() { return containingPane; }
}
