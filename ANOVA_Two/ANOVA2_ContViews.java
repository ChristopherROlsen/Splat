/**************************************************
 *              TwoWayANOVAContViews              *
 *                   05/15/18                     *
 *                     12:00                      *
 *************************************************/

package ANOVA_Two;

import genericClasses.JustAnAxis;
import dataObjects.UnivariateContinDataObj;
import dataObjects.QuantitativeDataVariable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.util.ArrayList;
import javafx.scene.paint.Color;

public class ANOVA2_ContViews extends Region { 
    
    // POJOs
    boolean dragging;
    
    int nFactorA_Levels, nFactorB_Levels, nDataPoints, nSquaresRow1, 
            nSquaresRow2, nResidualsInArrayList;
    int[] whiskerEndRanks;
    
    double  initial_yMin, initial_yMax, initial_yRange, yMin, yMax, yRange,
            xPix_InitialPress, yPix_InitialPress, xPix_MostRecentDragPoint, 
            yPix_MostRecentDragPoint, newX_Lower, newX_Upper, newY_Lower, 
            newY_Upper, deltaX, deltaY, dispLowerBound, dispUpperBound, 
            tempPos0, tempPos1, confidenceLevel, xRange;
    
    double rvfMinResid, rvfMaxResid, rvfAxisBound, rvfMinMean, rvfMaxMean;  
    double[][] theQQs;
    ArrayList<Double> alDouble_theResiduals;
    
    String  choiceOfPlot, strTitle, subTitleDescr, strResponseVar,
            strFactorA, strFactorB;
    ObservableList<String> factorA_Levels, factorB_Levels;
    
    // My classes
    ANOVA2_Model anova2_Model;
    JustAnAxis numericalXAxis, numericalYAxis;    
    UnivariateContinDataObj allData_UCDO;
    UnivariateContinDataObj[] arrayOfUCDOs;
    
    // POJO / FX
    AnchorPane anchorTitleInfo;
    Canvas twoWayANOVACanvas;
    GraphicsContext gc; // Required for drawing on the Canvas
    GridPane gridPane;
    Label xLabelPad, yLabelPad, xScalePad, yScalePad, titlePad;
    Stage stage;
    Scene scene;
    Text txtTitle, txtTitleDescr;

    ANOVA2_ContViews(ANOVA2_Model anova2_Model, String choiceOfPlot) {
        this.anova2_Model = anova2_Model;
        this.choiceOfPlot = choiceOfPlot;
        String graphsCSS = getClass().getResource("/css/ScatterPlot.css").toExternalForm();     
        factorA_Levels = FXCollections.observableArrayList();
        factorB_Levels = FXCollections.observableArrayList();   
        factorA_Levels = anova2_Model.getFactorALevels();
        factorB_Levels = anova2_Model.getFactorBLevels();
        nFactorA_Levels = factorA_Levels.size();
        nFactorB_Levels = factorB_Levels.size();
        
        allData_UCDO = this.anova2_Model.getAllDataUCDO();
        
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage = new Stage(); 
        stage.setX(screenBounds.getWidth() - stage.getWidth()/4);
        stage.setY(screenBounds.getHeight() - stage.getHeight()/4);
        stage.setResizable(true); 
        stage.setTitle("Two-way Analysis of Variance");
        gridPane = new GridPane();  // gridPane.setGridLinesVisible(true);
        
        scene = new Scene(gridPane, 825, 825);   
        
        scene.heightProperty().addListener(ov-> {doResVsFit();});
        scene.widthProperty().addListener(ov-> {doResVsFit();});

        twoWayANOVACanvas = new Canvas(625, 625);
        gc = twoWayANOVACanvas.getGraphicsContext2D(); 
        
        allData_UCDO = anova2_Model.getAllDataUCDO();

        strResponseVar = anova2_Model.getResponseLabel();
        strFactorA = anova2_Model.getFactorALabel();
        strFactorB = anova2_Model.getFactorBLabel();
        
        strTitle = strResponseVar + " vs " + strFactorA + " & " + strFactorB;
        subTitleDescr = "        ";
        txtTitle = new Text(50, 25, strTitle);
        txtTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,25));
        txtTitleDescr = new Text (60, 45, subTitleDescr);
        txtTitleDescr.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 20)); 
           
        initialize();

        nFactorA_Levels = this.anova2_Model.getNFactorA_Levels();
        nFactorB_Levels = this.anova2_Model.getNFactorB_Levels();
      
        initial_yMin = anova2_Model.getMinVertical();
        initial_yMax = anova2_Model.getMaxVertical();
        initial_yRange = initial_yMax - initial_yMin;
        
        // ****************  Switch for doing the plots  ***************
        switch (choiceOfPlot) { 
            case "ResVsFit":
                setUpResVsFit();                
                break;
                
            case "Residuals":
                setUpResidualsPlot(); 
                doResidualsPlot();
                break;
                
            default:
                System.out.println("Arrggg!  No known choice #2");
                System.exit(2);
                break;
        }

        // Insets: Top, right, bottom, left
        gridPane.setPadding(new Insets(10, 15, 5, 10));

        titlePad.setMinHeight(60); // To give the X Label some room       
        xScalePad.setMinHeight(80); // To give the X scale some room 
        yScalePad.setMinWidth(60); // To give the Y scale some room 
        gridPane.add(xScalePad, 0, 2);
        gridPane.add(yScalePad, 0, 0);
        gridPane.add(titlePad, 1, 0);
        gridPane.add(numericalXAxis, 1, 2);        
        gridPane.add(numericalYAxis, 0, 1);

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
   
        txtTitleDescr.setText(subTitleDescr);
        
        // ****************  Switch for doing the plots  ***************
        switch (choiceOfPlot) { 
            case "ResVsFit":
                doResVsFit();                 
                break;

            case "Residuals":
                doResidualsPlot();                 
                break;
                
            default:
                System.out.println("Arrggg!  No known choice #2");
                System.exit(2);
                break;
        }
    }
    
    private void setUpResVsFit() {
        subTitleDescr = "Residuals vs. Fitted values";
        rvfMinResid = 1.0; rvfMaxResid = -1.0;
        rvfMinMean = Double.MAX_VALUE; rvfMaxMean = Double.MIN_VALUE;

        // Get info needed for x, y initial scale values
        for (int theBetweenBatch = 1; theBetweenBatch <= nFactorA_Levels; theBetweenBatch++)
        {
            for (int theWithinBatch = 1; theWithinBatch <= nFactorB_Levels; theWithinBatch++) {
                int theAppropriateLevel = (theBetweenBatch - 1) * nFactorB_Levels + theWithinBatch;
                
                QuantitativeDataVariable tempUDM = new QuantitativeDataVariable();
                tempUDM = anova2_Model.getPrelimAB().getIthUDM(theAppropriateLevel);
                QuantitativeDataVariable tempResVsFitNDV = new QuantitativeDataVariable("null", tempUDM.getTheDeviations());
                
                int nDevs = tempResVsFitNDV.getLegalN();

                rvfMinResid = Math.min(tempResVsFitNDV.getMinValue(), rvfMinResid);
                rvfMaxResid = Math.max(tempResVsFitNDV.getMaxValue(), rvfMaxResid);
                rvfMinMean = Math.min(rvfMinMean, tempUDM.getTheMean());
                rvfMaxMean = Math.max(rvfMaxMean, tempUDM.getTheMean());

            }  // Loop through within batches
        }   //  Loop through between batches  

        rvfAxisBound = 1.025 * Math.max(Math.abs(rvfMinResid), rvfMaxResid);
        
        numericalXAxis = new JustAnAxis(rvfMinMean, rvfMaxMean);
        numericalXAxis.setSide(Side.BOTTOM);
        numericalXAxis.setPrefSize(gridPane.getWidth() - 50, 40);  
        numericalXAxis.setLabel("xAxis");
         
        deltaX = 0.005 * (rvfMaxMean - rvfMinMean);

        numericalYAxis = new JustAnAxis(-rvfAxisBound, rvfAxisBound);   
        numericalYAxis.setSide(Side.LEFT);
        numericalYAxis.setBounds(initial_yMin, initial_yMax);
        yRange = 2.0 * rvfAxisBound;

        // This constant controls the rate of scale change when dragging
        deltaY = 0.005 * yRange;

        numericalYAxis.setPrefSize(20, gridPane.getHeight() - 50);
        numericalYAxis.setLayoutX(500); numericalYAxis.setLayoutY(50);
        numericalYAxis.setLabel(anova2_Model.getResponseLabel());
        numericalYAxis.setLabel("Residuals");       
    }
    
    private void doResVsFit() {
        positionTopInfo();
       
        gc.clearRect(0, 0 , twoWayANOVACanvas.getWidth(), twoWayANOVACanvas.getHeight());

        for (int theBetweenBatch = 1; theBetweenBatch <= nFactorA_Levels; theBetweenBatch++)
        {
            for (int theWithinBatch = 1; theWithinBatch <= nFactorB_Levels; theWithinBatch++) {
                int theAppropriateLevel = (theBetweenBatch - 1) * nFactorB_Levels + theWithinBatch;
                
                /*
                UnivariateContinDataObj tempUCDO = new UnivariateContinDataObj();
                tempUCDO = anova2_Model.getPrelimAB().getIthLevel(theAppropriateLevel).getUCDO();
                UnivariateContinDataObj tempResVsFit = new UnivariateContinDataObj(tempUCDO.getTheDeviations(), "null");     
                */
                
                QuantitativeDataVariable tempUDM = new QuantitativeDataVariable();
                tempUDM = anova2_Model.getPrelimAB().getIthUDM(theAppropriateLevel);
                QuantitativeDataVariable tempResVsFitNDV = new QuantitativeDataVariable("null", tempUDM.getTheDeviations());
                double daMean = tempUDM.getTheMean();
                
                
                
                int daN = tempResVsFitNDV.getLegalN();
                for (int resPoint = 0; resPoint < daN; resPoint++) {
                
                double xx = numericalXAxis.getDisplayPosition(daMean);
                double yy = numericalYAxis.getDisplayPosition(tempResVsFitNDV.getIthDataPtAsDouble(resPoint));

                gc.fillOval(xx - 5, yy - 5, 10, 10);
                }
            }  // Loop through within batches
        }   //  Loop through between batches   
        
            double zeroLineLeft = numericalXAxis.getDisplayPosition(numericalXAxis.getLowerBound());
            double zeroLineRight = numericalXAxis.getDisplayPosition(numericalXAxis.getUpperBound());
            double zeroHeight = numericalYAxis.getDisplayPosition(0.0);
            
            gc.setLineWidth(2);
            gc.setStroke(Color.TOMATO);
            gc.strokeLine(zeroLineLeft, zeroHeight, zeroLineRight, zeroHeight);
            gc.setLineWidth(1);
            gc.setStroke(Color.BLACK);
    }
    
    private void setUpResidualsPlot() {
        alDouble_theResiduals = new ArrayList();
        ArrayList<QuantitativeDataVariable> prelimAB_levels = anova2_Model.getPrelimAB().getAllTheUDMs();
        subTitleDescr = "Normal scores vs. Residuals";
        int nLevelsAB = prelimAB_levels.size();
        // System.out.println("269, ContViews, nPrelimLevels = " + nLevelsAB);
        for (int abLevs = 1; abLevs < nLevelsAB; abLevs++) {
            String daLabel = prelimAB_levels.get(abLevs).getDataLabel();
            double daMean = prelimAB_levels.get(abLevs).getTheMean();
            //System.out.println("\n\n273, ContViews, daLabel = " + daLabel);
            //System.out.println("274, ContViews, daMean = " + daMean);
            int daN = prelimAB_levels.get(abLevs).getLegalN();
            double[] theData;
            theData = prelimAB_levels.get(abLevs).getLegalDataAsDoubles();
            for (int preResid = 0; preResid < daN; preResid++) {
                double theIthResidual = theData[preResid] - daMean;
                alDouble_theResiduals.add(theIthResidual);
            }
            nResidualsInArrayList = alDouble_theResiduals.size();
        }

        //UnivariateContinDataObj theResidualsUCDO = new UnivariateContinDataObj(theResiduals, "Residuals");
        // NumericDataVariable theResidualsNDV = new NumericDataVariable("residuals", theResiduals);
        // The QQa are double[nResids][2] -- [][] = [][Z, data]
        //theQQs = theResidualsUCDO.getTheQQPlot_ZX();
        
        double smallestResidual = theQQs[0][1];
        double largestResidual = theQQs[nResidualsInArrayList - 1][1];
        
        double smallestZ = theQQs[0][0];
        double largestZ = theQQs[nResidualsInArrayList - 1][0];

        // **************************************************************
        numericalXAxis = new JustAnAxis(smallestResidual, largestResidual);
        numericalXAxis.setSide(Side.BOTTOM);
        numericalXAxis.setPrefSize(gridPane.getWidth() - 50, 40);  
        numericalXAxis.setLabel("Residuals");
         
        deltaX = 0.005 * (largestResidual - smallestResidual);

        numericalYAxis = new JustAnAxis(smallestZ, largestZ);
        numericalYAxis.setSide(Side.LEFT);
        yRange = largestZ - smallestZ;

        // This constant controls the rate of scale change when dragging
        deltaY = 0.005 * yRange;

        numericalYAxis.setPrefSize(20, gridPane.getHeight() - 50);
        numericalYAxis.setLayoutX(500); numericalYAxis.setLayoutY(50);
        numericalYAxis.setLabel(anova2_Model.getResponseLabel());
        numericalYAxis.setLabel("Normal scores"); 
        // **************************************************************
        
    }
    
    private void doResidualsPlot() {
        positionTopInfo();
       
        gc.clearRect(0, 0 , twoWayANOVACanvas.getWidth(), twoWayANOVACanvas.getHeight());
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);        
        // System.out.println("302, ContViews, nResidualsInArrayList = " + nResidualsInArrayList);
        
        for (int qqPoint = 0; qqPoint < nResidualsInArrayList; qqPoint++)
        {
            //System.out.println("306 ContViews, z / data = " + theQQs[qqPoint][0] + " / " + theQQs[qqPoint][1]);
                double xx = numericalXAxis.getDisplayPosition(theQQs[qqPoint][1]);
                double yy = numericalYAxis.getDisplayPosition(theQQs[qqPoint][0]);

                gc.fillOval(xx - 5, yy - 5, 10, 10);
        }  
    }
    
    private void positionTopInfo() {
        int i, j;
       double atIWidth, tempPosition, startRect, jumpRect, k1, k2;
        atIWidth = anchorTitleInfo.getWidth();

        k1 = 12.0;  //  Hack for font 25 
        k2 = 10.0;    //  Hack for font 20
        txtTitle.setX(atIWidth / 2. - k1 * strTitle.length() / 2.);
        txtTitleDescr.setX(atIWidth / 2. - k2 * subTitleDescr.length() / 2.);
        txtTitleDescr.setY(50);
    }
    
    private void setHandlers() {
        numericalYAxis.setOnMouseDragged(yAxisMouseHandler); 
        numericalYAxis.setOnMousePressed(yAxisMouseHandler); 
        numericalYAxis.setOnMouseReleased(yAxisMouseHandler); 
        numericalXAxis.setOnMouseDragged(xAxisMouseHandler);  
        numericalXAxis.setOnMousePressed(xAxisMouseHandler); 
        numericalXAxis.setOnMouseReleased(xAxisMouseHandler);
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
                    numericalXAxis.setLowerBound(newX_Lower ); 
                    numericalXAxis.setUpperBound(newX_Upper );
                    dragging = false;
                }
            }
            else 
            if(mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED)
            {
                dragging = true;
                
                double xPix_Dragging = mouseEvent.getX();
                newX_Lower = numericalXAxis.getLowerBound();
                newX_Upper = numericalXAxis.getUpperBound(); 

                dispLowerBound = numericalXAxis.getDisplayPosition(numericalXAxis.getLowerBound());
                dispUpperBound = numericalXAxis.getDisplayPosition(numericalXAxis.getUpperBound());

                double frac = mouseEvent.getX() / dispUpperBound;
                
                // Still dragging right
                if((xPix_Dragging > xPix_InitialPress) && (xPix_Dragging > xPix_MostRecentDragPoint))
                {    
                    if (frac > 0.5) //  Right of center -- OK
                    {
                        newX_Upper = numericalXAxis.getUpperBound() - deltaX;
                    }
                    else  // Left of Center
                    {
                        newX_Lower = numericalXAxis.getLowerBound() - deltaX;
                    }
                }
                else 
                if ((xPix_Dragging < xPix_InitialPress) && (xPix_Dragging < xPix_MostRecentDragPoint))
                {   
                    if (frac < 0.5) // Left of center
                    {
                        newX_Lower = numericalXAxis.getLowerBound() + deltaX;
                    }
                    else    // Right of center -- OK
                    {
                        newX_Upper = numericalXAxis.getUpperBound() + deltaX;
                    }
                }    

                numericalXAxis.setLowerBound(newX_Lower ); 
                numericalXAxis.setUpperBound(newX_Upper );

                dispLowerBound = numericalXAxis.getDisplayPosition(numericalXAxis.getLowerBound());
                dispUpperBound = numericalXAxis.getDisplayPosition(numericalXAxis.getUpperBound());
                xPix_MostRecentDragPoint = mouseEvent.getX();
                
                switch (choiceOfPlot) {

                    case "ResVsFit": doResVsFit(); break;
                    
                    case "Residuals": doResidualsPlot(); break;

                    default:
                        System.out.println("Arrggg!  No known choice #4");
                        System.exit(4);
                        break;
                }   // end switch  
            }   // end if mouse dragged
        }   //  end handle
    }; 
    

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
                
                switch (choiceOfPlot) {

                    case "ResVsFit": doResVsFit(); break;
                    
                    default:
                        System.out.println("Arrggg!  No known choice #4");
                        System.exit(4);
                        break;
                }   // end switch  
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
    
    private void initialize() { 
        xScalePad = new Label(" "); yScalePad = new Label(" ");  
        xLabelPad = new Label(" "); yLabelPad = new Label(" ");
        titlePad  = new Label(" ");
        anchorTitleInfo = new AnchorPane();
        anchorTitleInfo.getChildren().addAll(txtTitle, txtTitleDescr);
    }
}
