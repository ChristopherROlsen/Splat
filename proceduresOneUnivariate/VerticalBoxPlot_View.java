/**************************************************
 *               VerticalBoxPlotView              *
 *                    12/09/18                    *
 *                     00:00                      *
 *************************************************/
package proceduresOneUnivariate;

import genericClasses.JustAnAxis;
import genericClasses.DragableAnchorPane;
import genericClasses.QuantitativeDataVariable;
import genericClasses.UnivariateContinDataObj;
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
import proceduresTwoUnivariate.Explore_2Ind_Dashboard;
import t_Procedures.*;
import ANOVA_One.*;

public class VerticalBoxPlot_View extends Region 
{ 
    // POJOs
    boolean dragging;
    boolean[] vBoxCheckBoxSettings;    
    
    int nVariables, nCheckBoxes, nDataPoints;
    int[] whiskerEndRanks;
    
    double initial_yMin, initial_yMax, initial_yRange, yMin, yMax, yRange,
           xPix_InitialPress, yPix_InitialPress, xPix_MostRecentDragPoint, 
           yPix_MostRecentDragPoint, newX_Lower, newX_Upper, newY_Lower, 
           newY_Upper, deltaX, deltaY, dispLowerBound, dispUpperBound, 
           bottomOfLowWhisker, topOfHighWhisker, initHoriz, initVert, 
           initWidth, initHeight;
    
    double[] fiveNumberSummary, means, stDevs;
    
    String[] vBoxCheckBoxDescr;
  
    ObservableList<String> categoryLabels;
    
    // My classes
    DragableAnchorPane dragableAnchorPane;
    JustAnAxis yAxis;   
    QuantitativeDataVariable tempQDV;
    ArrayList<QuantitativeDataVariable> allTheQDVs;
    UnivariateContinDataObj tempUCDO;
    ArrayList<UnivariateContinDataObj> allTheUCDOs;   
    VerticalBoxPlot_Model vBoxModel;
    
    // POJOs / FX
    AnchorPane checkBoxRow, anchorPane;
    Canvas graphCanvas;
    CheckBox[] vBoxCheckBoxes;
    GraphicsContext gcVBox; // Required for drawing on the Canvas

    CategoryAxis xAxis;
    Line line;
    Pane theContainingPane;    
    Text txtTitle1, txtTitle2;

    public VerticalBoxPlot_View(VerticalBoxPlot_Model vBoxModel, Exploration_Dashboard explore_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight)  
    {
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.vBoxModel = vBoxModel;
        
        nCheckBoxes = 2;
        vBoxCheckBoxDescr = new String[nCheckBoxes];
        vBoxCheckBoxDescr[0] = " Means diamond ";
        vBoxCheckBoxDescr[1] = " Extreme Outliers ";
        initStuff();
    }
    
    public VerticalBoxPlot_View(VerticalBoxPlot_Model vBoxModel, Explore_2Ind_Dashboard comp2Ind_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight)  
    {
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.vBoxModel = vBoxModel;
        nCheckBoxes = 2;
        vBoxCheckBoxDescr = new String[nCheckBoxes];
        vBoxCheckBoxDescr[0] = " Means diamond ";
        vBoxCheckBoxDescr[1] = " Extreme Outliers ";
        initStuff();
    }
    
    public VerticalBoxPlot_View(VerticalBoxPlot_Model vBoxModel, Indep_t_Dashboard independent_t_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight)  
    {
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.vBoxModel = vBoxModel;
        nCheckBoxes = 2;
        vBoxCheckBoxDescr = new String[nCheckBoxes];
        vBoxCheckBoxDescr[0] = " Means diamond ";
        vBoxCheckBoxDescr[1] = " Extreme Outliers ";
        initStuff();
    }
    
    public VerticalBoxPlot_View(VerticalBoxPlot_Model vBoxModel, Single_t_Dashboard independent_t_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight)  
    {
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.vBoxModel = vBoxModel;
        nCheckBoxes = 2;
        vBoxCheckBoxDescr = new String[nCheckBoxes];
        vBoxCheckBoxDescr[0] = " Means diamond ";
        vBoxCheckBoxDescr[1] = " Extreme Outliers ";
        initStuff();
    }
    
    public VerticalBoxPlot_View(VerticalBoxPlot_Model vBoxModel, ANOVA1_Dashboard anova1_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight)  
    {
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.vBoxModel = vBoxModel;
        nCheckBoxes = 2;
        vBoxCheckBoxDescr = new String[nCheckBoxes];
        vBoxCheckBoxDescr[0] = " Means diamond ";
        vBoxCheckBoxDescr[1] = " Extreme Outliers ";
        initStuff();
    }
    
    public VerticalBoxPlot_View(VerticalBoxPlot_Model vBoxModel, QANOVA1_Dashboard anova1_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight)  
    {
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.vBoxModel = vBoxModel;
        nCheckBoxes = 2;
        vBoxCheckBoxDescr = new String[nCheckBoxes];
        vBoxCheckBoxDescr[0] = " Means diamond ";
        vBoxCheckBoxDescr[1] = " Extreme Outliers ";
        initStuff();
    }
    
    private void initStuff() {
        categoryLabels = FXCollections.observableArrayList();
        this.allTheQDVs = new ArrayList<>();
        this.allTheQDVs = vBoxModel.getAllTheUDMs();
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
        gcVBox = graphCanvas.getGraphicsContext2D();
        
        makeTheCheckBoxes();    
        makeItHappen();        
    }
    
    private void makeItHappen() {       
        
        theContainingPane = new Pane();

        gcVBox = graphCanvas.getGraphicsContext2D();
        gcVBox.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
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
    
    private void makeTheCheckBoxes() {              
        // Determine which graphs are initially shown
        vBoxCheckBoxSettings = new boolean[nCheckBoxes];
        
        for (int ithSetting = 0; ithSetting < nCheckBoxes; ithSetting++) {
            vBoxCheckBoxSettings[ithSetting] =  false;
        }   
        
        checkBoxRow = new AnchorPane();
        checkBoxRow.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        vBoxCheckBoxes = new CheckBox[nCheckBoxes];

        for (int i = 0; i < nCheckBoxes; i++) {
            vBoxCheckBoxes[i] = new CheckBox(vBoxCheckBoxDescr[i]);
            
            vBoxCheckBoxes[i].setMaxWidth(Double.MAX_VALUE);
            vBoxCheckBoxes[i].setId(vBoxCheckBoxDescr[i]);
            vBoxCheckBoxes[i].setSelected(vBoxCheckBoxSettings[i]);

            vBoxCheckBoxes[i].setStyle(
                                "-fx-font-size: 14;" +
                                "-fx-border-insets: -5; " + 
                                "-fx-border-radius: 5;" +
                                "-fx-border-style: dotted;" +
                                "-fx-border-width: 0;" );

            
            if (vBoxCheckBoxes[i].isSelected() == true) 
                vBoxCheckBoxes[i].setTextFill(Color.GREEN);
            else
                vBoxCheckBoxes[i].setTextFill(Color.RED);
            
            vBoxCheckBoxes[i].setOnAction(e->{
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
                        vBoxCheckBoxSettings[0] = (checkValue == true);
                        doTheGraph();
                        break;
                        
                    case " Extreme Outliers ":  
                        vBoxCheckBoxSettings[1] = (checkValue == true);
                        doTheGraph();
                        break;
                        
                    default:
                        System.out.println("YIKES!!!  Best fit checkbox failure!!!");
                        System.exit(0);
                }

            }); //  end setOnAction
        }  
        
        checkBoxRow.getChildren().addAll(vBoxCheckBoxes);
    }
    
    
    public void setUpUI() {
        String title2String;
        txtTitle1 = new Text(50, 25, " Vertical Box Plot ");
        txtTitle2 = new Text (60, 45, " Vertical Box Plot ");
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
    
    private void setUpAnchorPane() {

        dragableAnchorPane = new DragableAnchorPane();
        graphCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        graphCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));

      
        for (int iChex = 0; iChex < nCheckBoxes; iChex++) {
            
            vBoxCheckBoxes[iChex].translateXProperty()
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
    
    public void initializeGraphParameters() {  
        constructDataArray();
        initial_yMin = allTheQDVs.get(0).getMinValue();
        initial_yMax = allTheQDVs.get(0).getMaxValue();
        initial_yRange = initial_yMax - initial_yMin; 

        yMin = initial_yMin;
        yMax = initial_yMax;
        yRange = initial_yRange;

        yAxis = new JustAnAxis(yMin, yMax);
        yAxis.setSide(Side.LEFT);
        yAxis.setLabel(allTheQDVs.get(0).getDataUnits()); 
        // This constant controls the rate of scale change when dragging
        deltaY = 0.005 * yRange;
        
        xAxis = new CategoryAxis(categoryLabels);
        xAxis.setSide(Side.BOTTOM);  
        xAxis.setAutoRanging(true);
        
        xAxis.setMinWidth(40);  //  Controls the Min Y Axis width (for labels)
        xAxis.setPrefWidth(40);              
    }
    
    private void constructDataArray()
    {
        /*

        */
    }
    
    public void doTheGraph(){
        yAxis.setForcedAxisEndsFalse(); // Just in case
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
            AnchorPane.setLeftAnchor(vBoxCheckBoxes[chex], (chex) * tempWidth / 5.0);
        }
        
        gcVBox.clearRect(0, 0 , graphCanvas.getWidth(), graphCanvas.getHeight());
        
        //  The zero batch is all the values
        for (int theBatch = 1; theBatch <= nVariables; theBatch++)
        {
            tempQDV = new QuantitativeDataVariable();
            tempQDV = allTheQDVs.get(theBatch);
            tempUCDO = new UnivariateContinDataObj(tempQDV);
            double daXPosition = xAxis.getDisplayPosition(categoryLabels.get(theBatch - 1));
            nDataPoints = tempUCDO.getLegalN();
            fiveNumberSummary = new double[5];
            fiveNumberSummary = tempUCDO.get_5NumberSummary();
            whiskerEndRanks = tempUCDO.getWhiskerEndRanks();
            double theMean = tempUCDO.getTheMean();
            double theStDev = tempUCDO.getTheStandDev();
            double theIQR = tempUCDO.getTheIQR();
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
            
            double mean_display = yAxis.getDisplayPosition(theMean);
            double upperStDev_display = yAxis.getDisplayPosition(theMean + theStDev);
            double lowerStDev_display = yAxis.getDisplayPosition(theMean - theStDev);
            double iqr_display = q3_display - q1_display;
            double iqr = fiveNumberSummary[3] - fiveNumberSummary[1];
            
            double spacing = 100.;

            gcVBox.setLineWidth(2);
            gcVBox.setStroke(Color.BLACK);
            double spaceFraction = 0.25 * spacing;

            // x, y, w, h
            gcVBox.strokeRect(daXPosition - spaceFraction, q3_display, 2 * spaceFraction, -iqr_display);    //  box
            gcVBox.strokeLine(daXPosition - spaceFraction, q2_display, daXPosition + spaceFraction, q2_display);    //  Median

            gcVBox.strokeLine(daXPosition, bottomOfLowWhisker, daXPosition, q1_display);  //  Low whisker
            gcVBox.strokeLine(daXPosition, q3_display, daXPosition, topOfHighWhisker);  //  High whisker
            
            // means & stDev diamond
            
            if (vBoxCheckBoxes[0].isSelected() == true){
                gcVBox.setLineWidth(1);
                gcVBox.setStroke(Color.RED);
                gcVBox.strokeLine(daXPosition - spaceFraction, mean_display, daXPosition, upperStDev_display);  
                gcVBox.strokeLine(daXPosition - spaceFraction, mean_display, daXPosition, lowerStDev_display);
                gcVBox.strokeLine(daXPosition + spaceFraction, mean_display, daXPosition, upperStDev_display);  
                gcVBox.strokeLine(daXPosition + spaceFraction, mean_display, daXPosition, lowerStDev_display);
                gcVBox.setLineWidth(2);
                gcVBox.setStroke(Color.BLACK);
            }
            
            // Low outliers
            if (whiskerEndRanks[0] != -1)    //  Are there low outliers?
            {
                int dataPoint = 0;
                while (dataPoint < whiskerEndRanks[0])
                {
                    double xx = daXPosition;
                    double tempY = tempUCDO.getIthSortedValue(dataPoint);
                    double yy = yAxis.getDisplayPosition(tempY);
                    
                    
                    // Extreme outlier
                    double tempLowBall = fiveNumberSummary[1] - 1.5 * theIQR;
                    double tempHighBall = fiveNumberSummary[3] + 1.5 * theIQR; 
                    
                    if ((tempY < tempLowBall) && (vBoxCheckBoxes[1].isSelected() == true)) {
                    // if (tempY < tempLowBall) {
                        gcVBox.strokeOval(xx - 6, yy - 6, 12, 12);
                    }
                    else {
                        gcVBox.fillOval(xx - 3, yy - 3, 6, 6);
                    }
                    
                    dataPoint++;
                }
            }

            // High outliers
            if (whiskerEndRanks[1] != -1)    //  Are there high outliers?
            {
                for (int dataPoint = whiskerEndRanks[1] + 1; dataPoint < nDataPoints; dataPoint++)
                {
                    double xx = daXPosition;
                    double tempY = tempUCDO.getIthSortedValue(dataPoint);
                    double yy = yAxis.getDisplayPosition(tempY);
                    
                    double tempHighBall = fiveNumberSummary[3] + 1.5 * theIQR; 
                    if ((tempY > tempHighBall) && (vBoxCheckBoxes[1].isSelected() == true)){
                        gcVBox.strokeOval(xx - 6, yy - 6, 12, 12);
                    }
                    else {
                        gcVBox.fillOval(xx - 3, yy - 3, 6, 6);   
                    }
                }
            }
        }   //  Loop through batches
    }
    
    public void setHandlers()
    {
        // yAxis.setOnMouseClicked(yAxisMouseHandler); 
        yAxis.setOnMouseDragged(yAxisMouseHandler); 
        // yAxis.setOnMouseEntered(yAxisMouseHandler); 
        // yAxis.setOnMouseExited(yAxisMouseHandler); 
        // yAxis.setOnMouseMoved(yAxisMouseHandler); 
        yAxis.setOnMousePressed(yAxisMouseHandler); 
        yAxis.setOnMouseReleased(yAxisMouseHandler); 
        
        /*
        vertBoxPlot.setOnMouseClicked(scatterplotMouseHandler); 
        vertBoxPlot.setOnMouseDragged(scatterplotMouseHandler); 
        vertBoxPlot.setOnMouseEntered(scatterplotMouseHandler); 
        vertBoxPlot.setOnMouseExited(scatterplotMouseHandler); 
        vertBoxPlot.setOnMouseMoved(scatterplotMouseHandler); 
        vertBoxPlot.setOnMousePressed(scatterplotMouseHandler); 
        vertBoxPlot.setOnMouseReleased(scatterplotMouseHandler);
        */
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