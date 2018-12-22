/**************************************************
 *            TwoWayANOVAUnbalancedView           *
 *                   05/15/18                     *
 *                     12:00                      *
 *************************************************/
package ANOVA_Two;

import genericClasses.JustAnAxis;
import matrixProcedures.Matrix;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ANOVA2_UnbalancedView extends Region{
    // POJOs
    boolean dragging;
    
    int nDataPoints;
    int lineToDraw;  // NONE = 0, BESTFIT = 1, Y_EQUALS 0 = 2
    int NONE = 0;
    int BESTFIT = 1;
    int Y_EQUALS_0 = 2;
    
    double xMin, xMax, yMin, yMax, xRange, yRange;
    double slope, intercept;
    double xPix_InitialPress, yPix_InitialPress, xPix_MostRecentDragPoint, yPix_MostRecentDragPoint;
    double newX_Lower, newX_Upper, newY_Lower, newY_Upper, deltaX, deltaY;
    double dispLowerBound, dispUpperBound;    
    double dataArray[][];
    
    String[] XYLabels;
    
    // My classes
    ANOVA2_UnbalancedRegression regModel;
    JustAnAxis xAxis, yAxis;
    Matrix X, Y;

    
    // POJOs / FX
    Canvas regressionCanvas;
    GraphicsContext gc;
    GridPane gridPane;
    Label xLabelPad, yLabelPad, xScalePad, yScalePad, titlePad, title;
    Line line;
    Scene scene;    
    Stage stage;

    ANOVA2_UnbalancedView(ANOVA2_UnbalancedRegression regModel, 
                   String[] Labels, 
                   Matrix X, Matrix Y, 
                   int lineToDraw) 
    {
        this.regModel = regModel;
        this.lineToDraw = lineToDraw; 

        XYLabels = new String[2];
        XYLabels = Labels;
        
        String graphsCSS = getClass().getResource("/css/Graphs.css").toExternalForm();
        
        title = new Label("Title goes here"); 
        title.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 20));        
        title.getStyleClass().add("titleLabel");              
        GridPane.setHalignment(title, HPos.CENTER); 
        
        xScalePad = new Label(" ");
        yScalePad = new Label(" "); 
        xLabelPad = new Label(" ");
        yLabelPad = new Label(" ");
        titlePad  = new Label(" ");

        this.X = X; this.Y = Y;
        stage = new Stage();
        
        if (lineToDraw == BESTFIT)
        {
            slope = regModel.getSimpleRegSlope();
            intercept = regModel.getSimpleRegIntercept();
        }
        else
        if (lineToDraw == Y_EQUALS_0)
        {
            slope = intercept = 0.0;
        }           
            
        // Position the Stage/Window on the screen
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(screenBounds.getWidth() - stage.getWidth()/4);
        stage.setY(screenBounds.getHeight() - stage.getHeight()/4);
        stage.setResizable(true);
        stage.setTitle("Stage Title");
        
        gridPane = new GridPane();
        gridPane.setStyle("-fx-background-color: white;");
        
        scene = new Scene(gridPane, 800, 800);
        scene.getStylesheets().add(graphsCSS);
        
        // Initial values apparently needed for Canvas at construction ?
        regressionCanvas = new Canvas(600, 600);
        gc = regressionCanvas.getGraphicsContext2D();
        
        scene.heightProperty().addListener(ov-> {doThePlot();});
        scene.widthProperty().addListener(ov-> {doThePlot();});

        constructDataArray();
        
        // These constants control the rate of change when dragging        
        deltaX = 0.005 * xRange; deltaY = 0.005 * yRange;

        //  **************   X Axis  **********************
        xAxis = new JustAnAxis(xMin, xMax);
        xAxis.setSide(Side.BOTTOM);

        //  **************   Y Axis  **********************
        yAxis = new JustAnAxis(yMin, yMax);
        yAxis.setSide(Side.LEFT);
        yAxis.setPrefSize(100, gridPane.getHeight() - 50);        
     
        // Insets: Top, right, bottom, left
        gridPane.setPadding(new Insets(10, 10, 5, 10));

        titlePad.setMinHeight(60); // To give the X Label some room       
        xScalePad.setMinHeight(60); // To give the X scale some room 
        
        gridPane.add(xScalePad, 0, 2);
        gridPane.add(titlePad, 0, 0);
        gridPane.add(yAxis, 0, 1);
        gridPane.add(xAxis, 1, 2);   

        //  **************   Y Labels & Titles  **********************
        gridPane.add(title, 1, 0); 

        setHandlers();
       
        gridPane.prefHeightProperty().bind(scene.heightProperty());
        gridPane.prefWidthProperty().bind(scene.widthProperty());
        gridPane.add(regressionCanvas, 1, 1);     
        
        stage.setResizable(true);
        stage.setScene(scene);   
        stage.sizeToScene();
        
        regressionCanvas.heightProperty().bind(scene.heightProperty().subtract(150));
        regressionCanvas.widthProperty().bind(scene.widthProperty().subtract(150));
        
        stage.show();

        // The initial plot 
        doThePlot();   
    }
    
    private void doThePlot()
    {    
        gc.clearRect(0, 0 , regressionCanvas.getWidth(), regressionCanvas.getHeight());

        for (int i = 0; i < nDataPoints; i++)
        {
            double xx = xAxis.getDisplayPosition(dataArray[i][0]);
            double yy = yAxis.getDisplayPosition(dataArray[i][1]);
            gc.fillOval(xx - 4, yy - 4, 8, 8); //  0.5*radius to get dot to center
        }
        
        if (lineToDraw != NONE)
        {
            line = new Line();
            double x1 = xAxis.getDisplayPosition(xMin);
            double y1 = yAxis.getDisplayPosition(slope * xMin + intercept);
            double x2 = xAxis.getDisplayPosition(xMax);
            double y2 = yAxis.getDisplayPosition(slope * xMax + intercept);
            gc.setLineWidth(4);
            gc.setStroke(Color.TOMATO);
            gc.strokeLine(x1, y1, x2, y2);           
        }
    }
    
    private void constructDataArray()
    {
        nDataPoints = X.getRowDimension();
        dataArray = new double[nDataPoints][2];
        
        xMin = xMax = X.get(0, 0);
        yMin = yMax = Y.get(0, 0);
        
        for (int iRow = 0; iRow < nDataPoints; iRow++)
        {
            double tempDoubleX = X.get(iRow, 0);
            double tempDoubleY = Y.get(iRow, 0);
            
            dataArray[iRow][0] = tempDoubleX;
            dataArray[iRow][1] = tempDoubleY;
   
            if (tempDoubleX < xMin) xMin = tempDoubleX;
            if (tempDoubleY < yMin) yMin = tempDoubleY;
            if (tempDoubleX > xMax) xMax = tempDoubleX;
            if (tempDoubleY > yMax) yMax = tempDoubleY;
        } 
        
        xRange = xMax - xMin;
        yRange = yMax - yMin;
        
        //  Make room for the circles
        xMin = xMin - .02 * xRange; xMax = xMax + .02 * xRange;
        yMin = yMin - .02 * yRange; yMax = yMax + .02 * yRange;                 
    }
    
    private void setHandlers()
    {
        xAxis.setOnMouseDragged(xAxisMouseHandler); 
        xAxis.setOnMousePressed(xAxisMouseHandler); 
        xAxis.setOnMouseReleased(xAxisMouseHandler);  
        yAxis.setOnMouseDragged(yAxisMouseHandler);  
        yAxis.setOnMousePressed(yAxisMouseHandler); 
        yAxis.setOnMouseReleased(yAxisMouseHandler); 
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
                
                doThePlot();
            }
        }
    };
    
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
                doThePlot();
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
}

