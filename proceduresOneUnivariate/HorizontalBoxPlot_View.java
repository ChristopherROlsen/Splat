/**************************************************
 *             HorizontalBoxPlot_View             *
 *                    12/25/18                    *
 *                     18:00                      *
 *************************************************/

package proceduresOneUnivariate;

import genericClasses.JustAnAxis;
import genericClasses.DragableAnchorPane;
import dataObjects.QuantitativeDataVariable;
import dataObjects.UnivariateContinDataObj;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import proceduresTwoUnivariate.*;
import t_Procedures.*;

public class HorizontalBoxPlot_View extends Region 
{ 
    // POJOs
    boolean dragging;
    boolean[] hBoxCheckBoxSettings;
    
    int nVariables, nCheckBoxes, nDataPoints;
    int[] whiskerEndRanks;
    
    double initial_xMin, initial_xMax, initial_xRange, xMin, xMax, xRange,
           xPix_InitialPress, yPix_InitialPress, xPix_MostRecentDragPoint, 
           yPix_MostRecentDragPoint, newX_Lower, newX_Upper, newY_Lower, 
           newY_Upper, deltaX, deltaY, dispLowerBound, dispUpperBound, 
           bottomOfLowWhisker, topOfHighWhisker, initHoriz, initVert, initWidth, 
           initHeight;
    
    double[] fiveNumberSummary, means, stDevs;
    
    String descriptionOfVariable;
    String[] hBoxCheckBoxDescr;
    ObservableList<String> categoryLabels;
        
    // My classes
    DragableAnchorPane dragableAnchorPane;
    JustAnAxis xAxis;    
    QuantitativeDataVariable tempQDV;
    ArrayList<QuantitativeDataVariable> allTheQDVs;
    HorizontalBoxPlot_Model hBoxModel;
    UnivariateContinDataObj tempUCDO;
    ArrayList<UnivariateContinDataObj> allTheUCDOs; 
    
    // POJOs / FX
    AnchorPane anchorPane, checkBoxRow;
    Canvas graphCanvas;
    CategoryAxis yAxis;
    CheckBox[] hBoxCheckBoxes;
    GraphicsContext gcHBox; // Required for drawing on the Canvas
    Line line;
    Pane theContainingPane;    
    Text txtTitle1, txtTitle2;

    public HorizontalBoxPlot_View() { }

    public HorizontalBoxPlot_View(HorizontalBoxPlot_Model hBoxModel, Exploration_Dashboard explore_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight)  
    {
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.hBoxModel = hBoxModel;
        descriptionOfVariable = hBoxModel.getDescriptionOfVariable();
        System.out.println("88 hBox, descr = " + descriptionOfVariable);
        nCheckBoxes = 2;
        hBoxCheckBoxDescr = new String[nCheckBoxes];
        hBoxCheckBoxDescr[0] = " Means diamond ";
        hBoxCheckBoxDescr[1] = " Extreme Outliers ";
        initStuff();
    }
    
    // In the case of more than one QDV, allTheUDMs(0) is all, 
    // individual QDVs go from 1 to n    
    public HorizontalBoxPlot_View(HorizontalBoxPlot_Model hBoxModel, Explore_2Ind_Dashboard comp2Ind_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight)  
    {
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.hBoxModel = hBoxModel;
        nCheckBoxes = 2;
        hBoxCheckBoxDescr = new String[nCheckBoxes];
        hBoxCheckBoxDescr[0] = " Means diamond ";
        hBoxCheckBoxDescr[1] = " Extreme Outliers ";
        initStuff();
    }
    
    public HorizontalBoxPlot_View(HorizontalBoxPlot_Model hBoxModel, Indep_t_Dashboard independent_t_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight)  
    {
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.hBoxModel = hBoxModel;
        nCheckBoxes = 2;
        hBoxCheckBoxDescr = new String[nCheckBoxes];
        hBoxCheckBoxDescr[0] = " Means diamond ";
        hBoxCheckBoxDescr[1] = " Extreme Outliers ";
        initStuff();
    }
    
    public HorizontalBoxPlot_View(HorizontalBoxPlot_Model hBoxModel, Single_t_Dashboard independent_t_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight)  
    {
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.hBoxModel = hBoxModel;
        nCheckBoxes = 2;
        hBoxCheckBoxDescr = new String[nCheckBoxes];
        hBoxCheckBoxDescr[0] = " Means diamond ";
        hBoxCheckBoxDescr[1] = " Extreme Outliers ";
        initStuff();
    }
    
    private void initStuff() {
        categoryLabels = FXCollections.observableArrayList();
        this.allTheQDVs = new ArrayList<>();
        this.allTheQDVs = hBoxModel.getAllTheQDVs();
        nVariables = allTheQDVs.size() - 1;   //  Excluding 0
        means = new double[nVariables];
        stDevs = new double[nVariables];
        for (int iVars = 0; iVars < nVariables; iVars++) {
            categoryLabels.add(allTheQDVs.get(iVars + 1).getDataLabel());
            means[iVars] = allTheQDVs.get(iVars + 1).getTheMean();
            stDevs[iVars] = allTheQDVs.get(iVars + 1).getTheStandDev();
        }
        
        String graphsCSS = getClass().getResource("/css/Graphs.css").toExternalForm();

        graphCanvas = new Canvas(600, 600);
        gcHBox = graphCanvas.getGraphicsContext2D();
        
        makeTheCheckBoxes();    
        makeItHappen();        
    }
    
    private void makeItHappen() {       
        theContainingPane = new Pane();
        gcHBox = graphCanvas.getGraphicsContext2D();
        gcHBox.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        graphCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        graphCanvas.widthProperty().addListener(ov-> {doTheGraph();});
    }
    
    public void completeTheDeal() { 
        initializeGraphParameters();
        yAxis.setTickLabelFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        yAxis.setTickLabelRotation(270);
        setUpUI();       
        setUpAnchorPane();
        setHandlers();
        doTheGraph();   
        
        theContainingPane = dragableAnchorPane.getTheContainingPane();       
    }
        
    public void initializeGraphParameters() {          
        // constructDataArray();
        initial_xMin = allTheQDVs.get(0).getMinValue();
        initial_xMax = allTheQDVs.get(0).getMaxValue();
        initial_xRange = initial_xMax - initial_xMin; 
        
        xMin = initial_xMin;
        xMax = initial_xMax;
        xRange = initial_xRange;
        xAxis = new JustAnAxis(xMin, xMax);
        xAxis.setSide(Side.BOTTOM);
        xAxis.setLabel(allTheQDVs.get(0).getDataUnits());        
        deltaX = 0.005 * xRange;        
        yAxis = new CategoryAxis(categoryLabels);
        yAxis.setSide(Side.LEFT);  
        yAxis.setAutoRanging(true);
        yAxis.setMinWidth(40);  //  Controls the Min Y Axis width (for labels)
        yAxis.setPrefWidth(40);          
    }
    
    private void constructDataArray() { /*  */ }
    
    public void setUpUI() {
        txtTitle1 = new Text(50, 25, " Horizontal Box Plot ");
        txtTitle2 = new Text (60, 45, descriptionOfVariable);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
    
    private void makeTheCheckBoxes() {
        // Determine which graphs are initially shown
        hBoxCheckBoxSettings = new boolean[nCheckBoxes];
        for (int ithSetting = 0; ithSetting < nCheckBoxes; ithSetting++) {
            hBoxCheckBoxSettings[ithSetting] =  false;
        } 
        
        checkBoxRow = new AnchorPane();
        checkBoxRow.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        hBoxCheckBoxes = new CheckBox[nCheckBoxes];

        for (int i = 0; i < nCheckBoxes; i++) {
            hBoxCheckBoxes[i] = new CheckBox(hBoxCheckBoxDescr[i]);
            
            hBoxCheckBoxes[i].setMaxWidth(Double.MAX_VALUE);
            hBoxCheckBoxes[i].setId(hBoxCheckBoxDescr[i]);
            hBoxCheckBoxes[i].setSelected(hBoxCheckBoxSettings[i]);

            hBoxCheckBoxes[i].setStyle(
                                "-fx-font-size: 14;" +
                                "-fx-border-insets: -5; " + 
                                "-fx-border-radius: 5;" +
                                "-fx-border-style: dotted;" +
                                "-fx-border-width: 0;" );

            
            if (hBoxCheckBoxes[i].isSelected() == true) 
                hBoxCheckBoxes[i].setTextFill(Color.GREEN);
            else
                hBoxCheckBoxes[i].setTextFill(Color.RED);
            
                hBoxCheckBoxes[i].setOnAction(e->{
                CheckBox tb = ((CheckBox) e.getTarget());
                
                String daID = tb.getId();
                Boolean checkValue = tb.selectedProperty().getValue();
                // Reset selected color
                if (checkValue == true)
                    tb.setTextFill(Color.GREEN);
                else 
                    tb.setTextFill(Color.RED);
                
                switch (daID) {    
                    case " Means diamond ":
                        hBoxCheckBoxSettings[0] = (checkValue == true);
                        doTheGraph();
                        break;
                        
                    case " Extreme Outliers ":  
                        hBoxCheckBoxSettings[1] = (checkValue == true);
                        doTheGraph();
                        break;
                        
                    default:
                        System.out.println("YIKES!!!  Best fit checkbox failure!!!");
                        System.exit(0);
                }

            }); //  end setOnAction
        }  
        
        checkBoxRow.getChildren().addAll(hBoxCheckBoxes);
    }
    
    private void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        graphCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        graphCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));

      
        for (int iChex = 0; iChex < nCheckBoxes; iChex++) {
            
            hBoxCheckBoxes[iChex].translateXProperty()
                                        .bind(graphCanvas.widthProperty()
                                        .divide(250.0)
                                        .multiply(5 * iChex)
                                        .subtract(50.0));
        }
     
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        String graphsCSS = getClass().getResource("/css/Graphs.css").toExternalForm();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(checkBoxRow, txtTitle1, txtTitle2, xAxis, yAxis, graphCanvas);
        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);

    }
    
    public void doTheGraph()
    {
        double xx0, yy0, xx1, yy1;
        String tempString;
        double text1Width = txtTitle1.getLayoutBounds().getWidth();
        double text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        
        AnchorPane.setTopAnchor(checkBoxRow, 0.01 * tempHeight);
        AnchorPane.setLeftAnchor(checkBoxRow, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(checkBoxRow, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(checkBoxRow, 0.95 * tempHeight);
       
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
        
        for (int chex = 0; chex < nCheckBoxes; chex++) {
            AnchorPane.setLeftAnchor(hBoxCheckBoxes[chex], (chex) * tempWidth / 5.0);
        }
        gcHBox.clearRect(0, 0 , graphCanvas.getWidth(), graphCanvas.getHeight());

        for (int theBatch = 1; theBatch <= nVariables; theBatch++)
        {
            tempQDV = new QuantitativeDataVariable();
            tempQDV = allTheQDVs.get(theBatch);
            tempUCDO = new UnivariateContinDataObj(tempQDV);
            double daYPosition = yAxis.getDisplayPosition(categoryLabels.get(theBatch - 1));
            nDataPoints = tempUCDO.getLegalN();
            fiveNumberSummary = new double[5];
            fiveNumberSummary = tempUCDO.get_5NumberSummary();
            whiskerEndRanks = tempUCDO.getWhiskerEndRanks();
            double theMean = tempUCDO.getTheMean();
            double theStDev = tempUCDO.getTheStandDev();
            double theIQR = tempUCDO.getTheIQR();
            bottomOfLowWhisker = xAxis.getDisplayPosition(fiveNumberSummary[0]);
 
            if (whiskerEndRanks[0] != -1)
                bottomOfLowWhisker = xAxis.getDisplayPosition(tempUCDO.getIthSortedValue(whiskerEndRanks[0]));

            topOfHighWhisker = xAxis.getDisplayPosition(fiveNumberSummary[4]);

            if (whiskerEndRanks[1] != -1)
                topOfHighWhisker = xAxis.getDisplayPosition(tempUCDO.getIthSortedValue(whiskerEndRanks[1]));

            double min_display = xAxis.getDisplayPosition(fiveNumberSummary[0]);
            double q1_display = xAxis.getDisplayPosition(fiveNumberSummary[1]);
            double q2_display = xAxis.getDisplayPosition(fiveNumberSummary[2]);
            double q3_display = xAxis.getDisplayPosition(fiveNumberSummary[3]);
            double max_display = xAxis.getDisplayPosition(fiveNumberSummary[4]);
            
            double mean_display = xAxis.getDisplayPosition(theMean);
            double upperStDev_display = xAxis.getDisplayPosition(theMean + theStDev);
            double lowerStDev_display = xAxis.getDisplayPosition(theMean - theStDev);
            double iqr_display = q3_display - q1_display;
            double iqr = fiveNumberSummary[3] - fiveNumberSummary[1];
            
            double spacing = 100.;

            gcHBox.setLineWidth(2);
            gcHBox.setStroke(Color.BLACK);
            double spaceFraction = 0.25 * spacing;

            // x, y, w, h
            gcHBox.strokeRect(q1_display, daYPosition - spaceFraction, iqr_display, 2 * spaceFraction);    //  box
            gcHBox.strokeLine(q2_display, daYPosition - spaceFraction, q2_display, daYPosition + spaceFraction);    //  Median
            gcHBox.strokeLine(bottomOfLowWhisker, daYPosition, q1_display, daYPosition);  //  Low whisker
            gcHBox.strokeLine(q3_display, daYPosition, topOfHighWhisker, daYPosition);  //  High whisker
            
            // means & stDev diamond
            
            if (hBoxCheckBoxes[0].isSelected() == true){
                gcHBox.setLineWidth(1);
                gcHBox.setStroke(Color.RED);
                gcHBox.strokeLine(mean_display, daYPosition - spaceFraction, upperStDev_display, daYPosition);  
                gcHBox.strokeLine(mean_display, daYPosition - spaceFraction, lowerStDev_display, daYPosition);
                gcHBox.strokeLine(mean_display, daYPosition + spaceFraction, upperStDev_display, daYPosition);  
                gcHBox.strokeLine(mean_display, daYPosition + spaceFraction, lowerStDev_display, daYPosition);
                gcHBox.setLineWidth(2);
                gcHBox.setStroke(Color.BLACK);
            }
            // Low outliers
            if (whiskerEndRanks[0] != -1)    //  Are there low outliers?
            {
                int dataPoint = 0;
                while (dataPoint < whiskerEndRanks[0])
                {
                    double yy = daYPosition;
                    double tempX = tempUCDO.getIthSortedValue(dataPoint);
                    double xx = xAxis.getDisplayPosition(tempX);
                    
                    
                    // Extreme outlier
                    double tempLowBall = fiveNumberSummary[1] - 1.5 * theIQR;
                    double tempHighBall = fiveNumberSummary[3] + 1.5 * theIQR; 
                    
                    if ((tempX < tempLowBall) && (hBoxCheckBoxes[1].isSelected() == true)) {
                        gcHBox.strokeOval(xx - 6, yy - 6, 12, 12);
                    }
                    else {
                        gcHBox.fillOval(xx - 3, yy - 3, 6, 6);
                    }
                    
                    dataPoint++;
                }
            }

            // High outliers
            if (whiskerEndRanks[1] != -1)    //  Are there high outliers?
            {
                for (int dataPoint = whiskerEndRanks[1] + 1; dataPoint < nDataPoints; dataPoint++)
                {
                    double yy = daYPosition;
                    double tempX = tempUCDO.getIthSortedValue(dataPoint);
                    double xx = xAxis.getDisplayPosition(tempX);
                    
                    double tempHighBall = fiveNumberSummary[3] + 1.5 * theIQR; 
                    if ((tempX > tempHighBall) && (hBoxCheckBoxes[1].isSelected() == true)) {
                        gcHBox.strokeOval(xx - 6, yy - 6, 12, 12);
                    }
                    else {
                        gcHBox.fillOval(xx - 3, yy - 3, 6, 6);   
                    }
                }
            }
        }   //  Loop through batches
    }
    
    public void setHandlers()
    {
        // xAxis.setOnMouseClicked(xAxisMouseHandler); 
        xAxis.setOnMouseDragged(xAxisMouseHandler); 
        // xAxis.setOnMouseEntered(xAxisMouseHandler); 
        // xAxis.setOnMouseExited(xAxisMouseHandler); 
        // xAxis.setOnMouseMoved(xAxisMouseHandler); 
        xAxis.setOnMousePressed(xAxisMouseHandler); 
        xAxis.setOnMouseReleased(xAxisMouseHandler); 
        
        /*
        scatterplot.setOnMouseClicked(scatterplotMouseHandler); 
        scatterplot.setOnMouseDragged(scatterplotMouseHandler); 
        scatterplot.setOnMouseEntered(scatterplotMouseHandler); 
        scatterplot.setOnMouseExited(scatterplotMouseHandler); 
        scatterplot.setOnMouseMoved(scatterplotMouseHandler); 
        scatterplot.setOnMousePressed(scatterplotMouseHandler); 
        scatterplot.setOnMouseReleased(scatterplotMouseHandler);
        */
    }
    
    EventHandler<MouseEvent> xAxisMouseHandler = new EventHandler<MouseEvent>() 
    {
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
                {   
                    if (frac < 0.5) // Left of center
                    {
                        newX_Lower = xAxis.getLowerBound() + deltaX;
                    }
                    else    // Right of center -- OK
                    {
                        newX_Upper = xAxis.getUpperBound() + deltaX;
                    }
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
     
    EventHandler<MouseEvent> scatterplotMouseHandler = new EventHandler<MouseEvent>() 
    {
        public void handle(MouseEvent mouseEvent) 
        {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) 
            { 
                xPix_InitialPress = mouseEvent.getX();  
                yPix_InitialPress = mouseEvent.getY();  
            }
        }
    };  
    
   public Pane getTheContainingPane() { return theContainingPane; }
}