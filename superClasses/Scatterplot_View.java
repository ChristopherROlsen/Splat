/**************************************************
 *                Scatterplot_View                *
 *                    12/24/18                    *
 *                      12:00                     *
 *************************************************/
package superClasses;

// *******************************************************************
// *   Subclasses:                                                   *
// *        Scatterplot_W_CheckBoxex_View                            *
// *******************************************************************

import genericClasses.DragableAnchorPane;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

public class Scatterplot_View extends Region {
    
    boolean dragging;
    
    public double xDataMin, xDataMax, yDataMin, yDataMax, xRange, yRange, xPix_InitialPress, 
           yPix_InitialPress, xPix_MostRecentDragPoint, checkBoxHeight, 
           yPix_MostRecentDragPoint, newX_Lower, newX_Upper, newY_Lower, 
           newY_Upper, deltaX, deltaY, dispLowerBound, dispUpperBound, 
           domainValue, xGraphLeft, xGraphRight, xStart, yStart, xStop, yStop, 
           initHoriz, initVert, initWidth, initHeight;
    
    public String explanatoryVariable, responseVariable, graphsCSS;
    
    public Canvas graphCanvas;
    public GraphicsContext gc;
    
    // My classes
    public DragableAnchorPane dragableAnchorPane;
    public genericClasses.JustAnAxis xAxis;    
    public genericClasses.JustAnAxis yAxis;

    
    public Scatterplot_View(double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
            
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        graphCanvas = new Canvas(initWidth, initHeight);
        
        graphsCSS = getClass().getResource("/css/Graphs.css").toExternalForm();
    } 
    
    public void doTheGraph() { }    
        
    public void setHandlers()
    { 
        xAxis.setOnMouseDragged(xAxisMouseHandler);  
        xAxis.setOnMousePressed(xAxisMouseHandler); 
        xAxis.setOnMouseReleased(xAxisMouseHandler); 
         
        yAxis.setOnMouseDragged(yAxisMouseHandler); 
        yAxis.setOnMousePressed(yAxisMouseHandler); 
        yAxis.setOnMouseReleased(yAxisMouseHandler); 
        
        dragableAnchorPane.setOnMouseReleased(scatterplotMouseHandler);
    }  
    
    EventHandler<MouseEvent> xAxisMouseHandler = new EventHandler<MouseEvent>() 
    {
        @Override
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
                    xRange = newX_Upper - newX_Lower;
                    deltaX = 0.005 * xRange;
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
                
                //  Make this call in a separate method from the mouseEventHandler?
                
                if (xAxis.getHasForcedLowScaleEnd()) {
                    newX_Lower = xAxis.getForcedLowScaleEnd();
                }
                if (xAxis.getHasForcedHighScaleEnd()) {
                    newX_Upper = xAxis.getForcedHighScaleEnd();                 
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
    
     EventHandler<MouseEvent> yAxisMouseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) { 
                yPix_InitialPress = mouseEvent.getY(); 
                yPix_MostRecentDragPoint = mouseEvent.getY();
                dragging = false;
            }
            else 
            if(mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
                if (dragging == true) {
                    yAxis.setLowerBound(newY_Lower ); 
                    yAxis.setUpperBound(newY_Upper );
                    yRange = newY_Upper - newY_Lower;
                    deltaY = 0.005 * yRange;
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
                
                if((yPix_Dragging > yPix_InitialPress) && (yPix_Dragging > yPix_MostRecentDragPoint)) {    
                    if (frac < 0.5) {
                        newY_Upper = yAxis.getUpperBound() + deltaY;
                    } else {
                        newY_Lower = yAxis.getLowerBound() + deltaY;;
                    }
                }
                else 
                if ((yPix_Dragging < yPix_InitialPress) && (yPix_Dragging < yPix_MostRecentDragPoint)){   
                    if (frac < 0.5){
                        newY_Upper = yAxis.getUpperBound() - deltaY;
                    }
                    else {
                        newY_Lower = yAxis.getLowerBound() - deltaY;;
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
                
                doTheGraph();
            }
        }
    };   
     
    EventHandler<MouseEvent> scatterplotMouseHandler = new EventHandler<MouseEvent>() 
    {
        @Override
        public void handle(MouseEvent mouseEvent) 
        {

        }
    };  
    
}
