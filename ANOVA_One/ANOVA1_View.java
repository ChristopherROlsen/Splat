/**************************************************
 *                  ANOVA1_View                   *
 *                    06/13/18                    *
 *                      21:00                     *
 *************************************************/
package ANOVA_One;

import genericClasses.JustAnAxis;
import genericClasses.DragableAnchorPane;
import genericClasses.UnivariateContinDataObj;
import genericClasses.QuantitativeDataVariable;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class ANOVA1_View extends Region { 
    // POJOs
    boolean dragging, yAxisHasForcedLowEnd, yAxisHasForcedHighEnd;
    boolean[] checkBoxSettings;
    
    int nVariables, nLevels, nCheckBoxes, nDataPoints;
    
    double initial_yMin, initial_yMax, initial_yRange, yMin, yMax, yRange,
           yPix_InitialPress, yPix_MostRecentDragPoint, newY_Lower, newY_Upper,
           deltaY, dispLowerBound, dispUpperBound, yDispLowerBound, 
           yDispUpperBound, errorBarLength, initHoriz, initVert, initWidth, 
           initHeight, yAxisForcedLowEnd, yAxisForcedHighEnd;
    
    double[] means, stDevs;
    
    String whichView;
    String[] strCheckBoxDescriptions;
    
    ObservableList<String> allTheLabels, categoryLabels;

    // My classes
    ANOVA1_Dashboard anova1Dashboard;
    ANOVA1_Model anova1_Model;
    DragableAnchorPane dragableAnchorPane;
    JustAnAxis yAxis;
    QuantitativeDataVariable allData_QDV;
    QuantitativeDataVariable tempQDV;
    ArrayList<QuantitativeDataVariable> allTheQDVs;
    ArrayList<UnivariateContinDataObj> allTheUCDOs;
    
    //  POJOs / FX
    AnchorPane anchorTitleInfo, checkBoxRow, anchorPane;
    Canvas anova1Canvas;
    CategoryAxis xAxis;
    CheckBox[] anova1_CheckBoxes;
    GraphicsContext gcANOVA1; // Required for drawing on the Canvas
    Pane anova1_ContainingPane;
    Text errorBarDescription, txtTitle1, txtTitle2;

    ANOVA1_View(ANOVA1_Model anova1Model, ANOVA1_Dashboard anova1Dashboard,
                         String whichView, 
                         double placeHoriz, double placeVert, 
                         double withThisWidth, double withThisHeight) {
        
        allTheLabels = FXCollections.observableArrayList();
        categoryLabels = FXCollections.observableArrayList();
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.whichView = whichView;
        this.anova1_Model = anova1Model;
        allTheLabels = anova1Model.getCategoryLabels();
        anova1Canvas = new Canvas(withThisWidth, withThisHeight);
        anchorTitleInfo = new AnchorPane();
        anova1Canvas.heightProperty().addListener(ov-> {doTheGraph();});
        anova1Canvas.widthProperty().addListener(ov-> {doTheGraph();});
        gcANOVA1 = anova1Canvas.getGraphicsContext2D();
        gcANOVA1.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        anova1_ContainingPane = new Pane();
    }
        
    public void completeTheDeal() { 
        initializeGraphParameters();
        // setUpUI(); 
        makeTheCheckBoxes();    
        setUpAnchorPane();
        setHandlers();
        anova1_ContainingPane = dragableAnchorPane.getTheContainingPane();
        doTheGraph();   
    }
    
    public void initializeGraphParameters() {  
        double tempUpDown;
        allTheQDVs = anova1_Model.getAllTheQDVs();
    
        nVariables = allTheQDVs.size() - 1;   //  Excluding 0
        means = new double[nVariables];
        stDevs = new double[nVariables];
        allTheUCDOs = new ArrayList<>();
        for (int iVars = 0; iVars <= nVariables; iVars++) {
            UnivariateContinDataObj tempUCDO = new UnivariateContinDataObj(allTheQDVs.get(iVars));
            allTheUCDOs.add(tempUCDO);
            tempUCDO.doMedianBasedCalculations();
        }
        
        // Reconstruct category labels w/o the "All"
        for (int iVars = 0; iVars < nVariables; iVars++) {
            categoryLabels.add(allTheLabels.get(iVars + 1));
            means[iVars] = allTheQDVs.get(iVars + 1).getTheMean();
            stDevs[iVars] = allTheQDVs.get(iVars + 1).getTheStandDev();
        }
        
        nLevels = anova1_Model.getNLevels();
        anchorPane = new AnchorPane();

        xAxis = new CategoryAxis(categoryLabels);
        xAxis.setSide(Side.BOTTOM);
        xAxis.setAutoRanging(true);
        xAxis.setLabel(anova1_Model.getExplanatoryVariable());
        
        xAxis.setMinWidth(40);  //  Controls the Min X Axis width (for labels)
        xAxis.setPrefWidth(40);
        
        xAxis.setPrefSize(anchorPane.getWidth() - 50, 40);   
        xAxis.setLayoutX(500); xAxis.setLayoutY(50); 

        //  Find the maximum mean plus standard error
        initial_yMin = Double.MAX_VALUE;
        initial_yMax = -Double.MAX_VALUE;
        
        for (int ithLevel = 1; ithLevel <= nLevels; ithLevel++) {
            tempQDV = anova1_Model.getIthQDV(ithLevel);
            switch(whichView) {
                case "BoxPlot":
                    initial_yMin = Math.min(initial_yMin, tempQDV.getMinValue());
                    initial_yMax = Math.max(initial_yMax, tempQDV.getMaxValue());                    
                break;
                    
                case "CirclePlot":
                    initial_yMin = Math.min(initial_yMin, tempQDV.getMinValue());
                    initial_yMax = Math.max(initial_yMax, tempQDV.getMaxValue());
                break;

                case "PostHoc":
                    double tempLow = tempQDV.getTheMean() - anova1_Model.getPostHocPlusMinus();
                    double tempHigh = tempQDV.getTheMean() + anova1_Model.getPostHocPlusMinus();
                    initial_yMin = Math.min(initial_yMin, tempLow); 
                    initial_yMax = Math.max(initial_yMax, tempHigh);                         
                break;

                case "MeanAndError":
                    // Bars assumed -- Will think about it tomorrow
                    initial_yMin = 0.0;
                    tempUpDown = tempQDV.getTheMean() + 2.0 * tempQDV.getTheStandDev();
                    initial_yMax = Math.max(initial_yMax, tempUpDown);   
                    yAxisHasForcedLowEnd = true;
                    yAxisForcedLowEnd = 0.0;
                break;

                default:
                    System.out.println("Ack!!  Switch failure in ANOVA1_View");
                    System.exit(171);
            }   
        }
            
        initial_yRange = initial_yMax - initial_yMin;
        yAxis = new JustAnAxis(initial_yMin, initial_yMax);

        yAxis.setSide(Side.LEFT);
        yAxis.setPrefSize(20, anchorPane.getHeight() - 50);
        yAxis.setLayoutX(500); yAxis.setLayoutY(50);
        
        yAxis.setLabel(anova1_Model.getResponseVariable());        
        yMin = initial_yMin - .05 * initial_yRange;
        yMax = initial_yMax + .05 * initial_yRange;
        yRange = initial_yRange;    
        deltaY = .005 * yRange;
        newY_Lower = yMin; newY_Upper = yMax;

        // For some graphs the LowerBound will be reset to 0.0
        yAxis.setLowerBound(newY_Lower ); 
        yAxis.setUpperBound(newY_Upper );
    }
   
    public void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        anova1Canvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        anova1Canvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));

        for (int iChex = 0; iChex < nCheckBoxes; iChex++) {
            // Position checkboxes in the more or less middle
            switch (nCheckBoxes) {
                
                case 1:  //  Etched in lemon marangue
                    anova1_CheckBoxes[iChex].translateXProperty()
                                            .bind(anova1Canvas.widthProperty()
                                            .divide(250.0)
                                            .multiply(65. * iChex)
                                            .subtract(125.0));
                break;
            
                case 2: //  Etched in stone
                    anova1_CheckBoxes[iChex].translateXProperty()
                                            .bind(anova1Canvas.widthProperty()
                                            .divide(250.0)
                                            .multiply(65. * iChex)
                                            .subtract(125.0));
                break;
                
                case 3:  //  Etched in stone
                    anova1_CheckBoxes[iChex].translateXProperty()
                                            .bind(anova1Canvas.widthProperty()
                                            .divide(250.0)
                                            .multiply(25. * iChex)
                                            .subtract(175.0));
                break;
                
                case 4:  //  Etched in stone
                    anova1_CheckBoxes[iChex].translateXProperty()
                                            .bind(anova1Canvas.widthProperty()
                                            .divide(250.0)
                                            .multiply(65. * iChex)
                                            .subtract(225.0));
                break;
            }
        }
     
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        String graphsCSS = getClass().getResource("/css/Graphs.css").toExternalForm();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    

        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(checkBoxRow, txtTitle1, txtTitle2, xAxis, yAxis, anova1Canvas);
        
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    public void doTheGraph() { }
    
    public void makeTheCheckBoxes() {       
        // Determine which graphs are initially shown
        checkBoxSettings = new boolean[nCheckBoxes];
        for (int ithBox = 0; ithBox < nCheckBoxes; ithBox++) {
            checkBoxSettings[ithBox] = false;
        }
        
        checkBoxRow = new AnchorPane();
        checkBoxRow.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        anova1_CheckBoxes = new CheckBox[nCheckBoxes];

        for (int i = 0; i < nCheckBoxes; i++) {
            anova1_CheckBoxes[i] = new CheckBox(strCheckBoxDescriptions[i]);
            
            anova1_CheckBoxes[i].setMaxWidth(Double.MAX_VALUE);
            anova1_CheckBoxes[i].setId(strCheckBoxDescriptions[i]);
            anova1_CheckBoxes[i].setSelected(checkBoxSettings[i]);

            anova1_CheckBoxes[i].setStyle(
                                "-fx-font-size: 14;" +
                                "-fx-border-insets: -5; " + 
                                "-fx-border-radius: 5;" +
                                "-fx-border-style: dotted;" +
                                "-fx-border-width: 0;" );

            
            if (anova1_CheckBoxes[i].isSelected() == true) 
                anova1_CheckBoxes[i].setTextFill(Color.GREEN);
            else
                anova1_CheckBoxes[i].setTextFill(Color.RED);
            
            anova1_CheckBoxes[i].setOnAction(e->{
                CheckBox tb = ((CheckBox) e.getTarget());
                
                String daID = tb.getId();
                Boolean checkValue = tb.selectedProperty().getValue();
                // Reset selected color
                if (checkValue == true)
                    tb.setTextFill(Color.GREEN);
                else 
                    tb.setTextFill(Color.RED);
                
                for (int ithID = 0; ithID < nCheckBoxes; ithID++) {
                    if (daID.equals(strCheckBoxDescriptions[ithID])) {
                        checkBoxSettings[ithID] = (checkValue == true);
                        doTheGraph();
                    }
                }

            }); //  end setOnAction
        }  
        
        checkBoxRow.getChildren().addAll(anova1_CheckBoxes);
    }
    
    public void setHandlers() {
        // yAxis.setOnMouseClicked(yAxisMouseHandler); 
        yAxis.setOnMouseDragged(yAxisMouseHandler); 
        // yAxis.setOnMouseEntered(yAxisMouseHandler); 
        // yAxis.setOnMouseExited(yAxisMouseHandler); 
        // yAxis.setOnMouseMoved(yAxisMouseHandler); 
        yAxis.setOnMousePressed(yAxisMouseHandler); 
        yAxis.setOnMouseReleased(yAxisMouseHandler);
        
        dragableAnchorPane.setOnMouseReleased(anovaplotMouseHandler);
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
                        if (!yAxisHasForcedHighEnd)
                            newY_Upper = yAxis.getUpperBound() + deltaY;
                    }
                    else  
                    {
                        if (!yAxisHasForcedLowEnd)
                            newY_Lower = yAxis.getLowerBound() + deltaY;
                    }
                }
                else 
                if ((yPix_Dragging < yPix_InitialPress) && (yPix_Dragging < yPix_MostRecentDragPoint))
                {   
                    if (frac < 0.5)
                    {
                        if (!yAxisHasForcedHighEnd)
                            newY_Upper = yAxis.getUpperBound() - deltaY;
                    }
                    else
                    {
                        if (!yAxisHasForcedLowEnd)
                            newY_Lower = yAxis.getLowerBound() - deltaY;
                    }
                }    

                if (yAxisHasForcedLowEnd) {
                    newY_Lower = yAxisForcedLowEnd;                   
                }
            
                if (yAxis.getHasForcedHighScaleEnd()) {
                    newY_Upper = yAxisForcedHighEnd;                    
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
     
    EventHandler<MouseEvent> anovaplotMouseHandler = new EventHandler<MouseEvent>() 
    {
        @Override
        public void handle(MouseEvent mouseEvent) 
        {

        }
    }; 
    
    public Pane getTheContainingPane() { return anova1_ContainingPane; }
    
}
