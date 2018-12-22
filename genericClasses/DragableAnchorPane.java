/*
            DragableResizableAnchorPane
                     05/16/18
                      12:00
 */

package genericClasses;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class DragableAnchorPane extends Pane {
    // POJOs
    boolean isInTopDragZone;
    int nRows, nCols;
    private static final int MARGIN = 12;    
    //  Scene coordinates
    double thisNodeDragAnchorX, thisNodeDragAnchorY, dragHeight, dragWidth,
           minimumWidth, minimumHeight, thisNodeTranslateX, thisNodeTranslateY;
    private double clickX, clickY, nodeX, nodeY, nodeH, nodeW;
    private static final double MIN_W = 30;
    private static final double MIN_H = 20;
    
    public String theParent;
    
    // My classes
    public static enum S { DEFAULT, DRAG, NW_RESIZE, SW_RESIZE, NE_RESIZE,
                           SE_RESIZE, E_RESIZE, W_RESIZE, N_RESIZE, S_RESIZE;
                         }
    private S state;
    
    // POJOs / FX
    AnchorPane theAnchorPane;
    public Pane theContainingPane;

    DoubleProperty thisNodeX = new SimpleDoubleProperty();
    DoubleProperty thisNodeY = new SimpleDoubleProperty();
    
    public DragableAnchorPane() {
        super();
        theAnchorPane = new AnchorPane();
    }
    
    public AnchorPane getTheAP() {return theAnchorPane; }
    
    public void setParent(String daParent) { theParent = daParent; }
    
    public void makeDragable() {
        translateXProperty().bind(thisNodeX);
        translateYProperty().bind(thisNodeY);
        setPrefSize(500, 500);
        theContainingPane = this;
        
        setStyle("-fx-background-color: white;");
  
        setOnMousePressed(
            new EventHandler<MouseEvent>() {
                public void handle(final MouseEvent mouseEvent) {
                    theContainingPane.toFront();
                    setNewEventCoordinates(mouseEvent);
                    
                    // Corner drag is jumpy without this!!!!!
                    if (isInTopDragZone(mouseEvent)) {
                        state = S.DRAG;

                    thisNodeTranslateX = getTranslateX();
                    thisNodeDragAnchorX = mouseEvent.getSceneX();
                    thisNodeTranslateY = getTranslateY();
                    thisNodeDragAnchorY = mouseEvent.getSceneY();                     
                    }

                    if (isInLeftRightBottomResizeZone(mouseEvent)) {
                        state = currentMouseState(mouseEvent);
                    } else {
                        state = S.DEFAULT;
                    }
                }
            });

        setOnMouseDragged(
            new EventHandler<MouseEvent>() {
                public void handle(final MouseEvent mouseEvent) {

                    if (isInTopDragZone(mouseEvent)) {
                        double dragX = mouseEvent.getSceneX() - thisNodeDragAnchorX;
                        thisNodeX.setValue(thisNodeTranslateX + dragX);
                        double dragY = mouseEvent.getSceneY() - thisNodeDragAnchorY;
                        thisNodeY.setValue(thisNodeTranslateY + dragY); 
                    }
                    else {
                        resizeViaMouse(mouseEvent);
                    }
                }
            });
        
        
        setOnMouseMoved(
            new EventHandler<MouseEvent>() {
                public void handle(final MouseEvent mouseEvent) {
                    S state = currentMouseState(mouseEvent);
                    Cursor cursor = getCursorForState(state);
                    setCursor(cursor);    
                }
            });
       
        
        setOnMouseReleased(
            new EventHandler<MouseEvent>() {
                public void handle(final MouseEvent mouseEvent) {
                    S state = currentMouseState(mouseEvent);
                    Cursor cursor = getCursorForState(state);
                    setCursor(cursor);    
                }
            });
    }
    
    public void setInitialEventCoordinates(double x, double y, double h, double w) {
        thisNodeX.set(x);
        thisNodeY.set(y);
        setMinWidth(w); setMinHeight(h);
        setMaxWidth(w); setMaxHeight(h);
        theAnchorPane.setMinWidth(w); theAnchorPane.setMinHeight(h);
        theAnchorPane.setMaxWidth(w); theAnchorPane.setMaxHeight(h);
        minimumWidth = w; 
        minimumHeight = h;
    }
    

    public void setPanePositionAndSize(double x, double y, double h, double w) {
        thisNodeX.set(x);
        thisNodeY.set(y);
        setMinWidth(w); setMinHeight(h);
        setMaxWidth(w); setMaxHeight(h);
        theAnchorPane.setMinWidth(w); theAnchorPane.setMinHeight(h);
        theAnchorPane.setMaxWidth(w); theAnchorPane.setMaxHeight(h);
    }
   
    private S currentMouseState(MouseEvent event) {
        S state = S.DEFAULT;
        isInTopDragZone = false;
        boolean left = isLeftResizeZone(event);
        boolean right = isRightResizeZone(event);
        boolean top = isTopResizeZone(event);
        boolean bottom = isBottomResizeZone(event);

        if (left && top) state = S.NW_RESIZE;
        else if (left && bottom) state = S.SW_RESIZE;
        else if (right && top) state = S.NE_RESIZE;
        else if (right && bottom) state = S.SE_RESIZE;
        else if (right) state = S.E_RESIZE;
        else if (left) state = S.W_RESIZE;
        else if (top) {
            state = S.DRAG;
            isInTopDragZone = true;
        }   // i.e. top but not in resize
        else if (bottom) 
            state = S.S_RESIZE;
     
        return state;
    }

    private static Cursor getCursorForState(S state) {
        switch (state) {
            case NW_RESIZE: return Cursor.NW_RESIZE;
            case SW_RESIZE: return Cursor.SW_RESIZE;
            case NE_RESIZE: return Cursor.NE_RESIZE;
            case SE_RESIZE: return Cursor.SE_RESIZE;
            case E_RESIZE:  return Cursor.E_RESIZE;
            case W_RESIZE:  return Cursor.W_RESIZE;
            case N_RESIZE:  return Cursor.N_RESIZE;
            case S_RESIZE:  return Cursor.S_RESIZE;
            case DRAG:      return Cursor.OPEN_HAND;    // Added by Chris
            default:        return Cursor.DEFAULT;
        }
    }

    protected void resizeViaMouse(MouseEvent event) { 
            double dragwidth = theAnchorPane.getWidth();
            // System.out.println("dragwidth = " + dragwidth);
            // System.out.println("DragAP 194, ");
            double mouseX = parentX(event.getX());
            double mouseY = parentY(event.getY());           
            
            if (state == S.DRAG) {  
                setPanePositionAndSize(mouseX - clickX, mouseY - clickY, nodeH, nodeW);
            } else if (state != S.DEFAULT) {
                //resizing
                double newX = nodeX;
                double newY = nodeY;
                double newH = nodeH;
                double newW = nodeW;

                // Right Resize
                if (state == S.E_RESIZE || state == S.NE_RESIZE || state == S.SE_RESIZE) {
                    newW = mouseX - nodeX;
                }
                
                // Left Resize
                if (state == S.W_RESIZE || state == S.NW_RESIZE || state == S.SW_RESIZE) {
                    newX = mouseX;
                    newW = nodeW + nodeX - newX;
                }

                // Bottom Resize
                if (state == S.S_RESIZE || state == S.SE_RESIZE || state == S.SW_RESIZE) {
                    newH = mouseY - nodeY;
                }
                
                // Top Resize (Just the corners)
                if (state == S.NW_RESIZE || state == S.NE_RESIZE) {
                    newY = mouseY;
                    newH = nodeH + nodeY - newY;
                }

                //min valid rect Size Check
                if (newW < MIN_W) {
                    if (state == S.W_RESIZE || state == S.NW_RESIZE || state == S.SW_RESIZE)
                        newX = newX - MIN_W + newW;
                    newW = MIN_W;
                }

                if (newH < MIN_H) {
                    if (state == S.N_RESIZE || state == S.NW_RESIZE || state == S.NE_RESIZE)
                        newY = newY + newH - MIN_H;
                    newH = MIN_H;
                }
 
                if ((minimumHeight <= newH) && (minimumWidth <= newW))
                    setPanePositionAndSize(newX, newY, newH, newW);
            }
    }
   
    private boolean isInLeftRightBottomResizeZone(MouseEvent event) {
        return isLeftResizeZone(event) || isRightResizeZone(event)
                || isBottomResizeZone(event);
    }
    
    private void setNewEventCoordinates(MouseEvent mouseEvent) {
        nodeX = nodeX();
        nodeY = nodeY();
        nodeH = nodeH();
        nodeW = nodeW();
        clickX = mouseEvent.getX();
        clickY = mouseEvent.getY();
    }

    private boolean isInTopDragZone(MouseEvent event) {
        return isInTopDragZone;
    }

    private boolean isLeftResizeZone(MouseEvent event) {
        return intersect(0, event.getX());
    }

    private boolean isRightResizeZone(MouseEvent event) {
        return intersect(nodeW(), event.getX());
    }
    
    private boolean isTopResizeZone(MouseEvent event) {
        return intersect(0, event.getY());
    }

    private boolean isBottomResizeZone(MouseEvent event) {
        return intersect(nodeH(), event.getY());
    }

    private boolean intersect(double side, double point) {
        return side + MARGIN > point && side - MARGIN < point;
    }

    private double parentX(double localX) {
        return nodeX() + localX;
    }

    private double parentY(double localY) {
        return nodeY() + localY;
    }

    private double nodeX() {
        return getBoundsInParent().getMinX();
    }

    private double nodeY() {
        return getBoundsInParent().getMinY();
    }

    private double nodeW() {
        return getBoundsInParent().getWidth();
    }

    private double nodeH() {
        return getBoundsInParent().getHeight();
    }
    
    public Pane getTheContainingPane() {
        theContainingPane.getChildren().add(theAnchorPane);
        return theContainingPane;
    }
    
    public double getDragHeight() { return dragHeight; }
    public double getDragWidth() { return dragWidth; }
}
