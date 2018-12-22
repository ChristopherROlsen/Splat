/**************************************************
 *                  X2GriddyWiddy                 *
 *                    05/15/18                    *
 *                     15:00                      *
 *************************************************/

package chiSquare;

import genericClasses.DataUtilities;
import genericClasses.MyAlerts;
import java.util.*;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.time.LocalTime;
import javafx.beans.Observable;
import javafx.geometry.Orientation;
import javafx.scene.layout.BorderPane;

public class X2GriddyWiddy extends Control {     
    // POJOs
    boolean isAnInteger;
    int numberOfColumns, numberOfRows, nRowsMinusOne, nColumnsMinusOne;

    String tempString, textInColIRowJ, caseString, blankDataElement;
    String blanx = "  ";

    // My classes
    MyAlerts myAlerts;    
     
    // POJOs / FX
    BorderPane borderPane;
    GridPane gridPane;
    LocalTime localTime; // For future development[?]
    Object tfObject; 
    ScrollBar sbHorizontal, sbVertical;
    Set variableNames;
    TextField tf[][];

    X2GriddyWiddy()  { }
    
    public X2GriddyWiddy (int numberOfRows, int numberOfColumns){
        
        myAlerts = new MyAlerts();
        
        this.numberOfRows = numberOfRows;
        this.numberOfColumns = numberOfColumns;
        
        nRowsMinusOne = numberOfRows - 1;
        nColumnsMinusOne = numberOfColumns - 1;
        blankDataElement = "";
 
        sbHorizontal = new ScrollBar(); //  Default orientation is HORIZ
        sbVertical = new ScrollBar();
        sbVertical.setOrientation(Orientation.VERTICAL);
    
        // Listener for horizontal scroll bar value invalidation  
        sbHorizontal.valueProperty().addListener((Observable ov) -> {
            horizBarResponse();   
        });
 
        // Listener for vertical scroll bar value invalidation 
        sbVertical.valueProperty().addListener((Observable ov) -> {
            vertBarResponse();
        }); 
    
        gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        borderPane = new BorderPane();
        borderPane.setCenter(gridPane);
        borderPane.setBottom(sbHorizontal);
        borderPane.setRight(sbVertical);
        
        tfObject = new Object();

        tf = new TextField[numberOfColumns][numberOfRows];

        for (int ithCol = 0; ithCol < numberOfColumns; ithCol++) {
            for (int jthRow = 0; jthRow < numberOfRows; jthRow++) {
                
                tf[ithCol][jthRow] = new TextField("    ");
                
                /*************************************************************
                * setFocusTraversable = false so that the traversing in the  *
                * grid is under manual control.                              *
                *************************************************************/                 
                tf[ithCol][jthRow].setFocusTraversable(false);
                tf[ithCol][jthRow].setOnMouseClicked(textFieldMouseEventHandler);
                tf[ithCol][jthRow].setOnKeyTyped(x2Grid_KeyEventHandler);
                tf[ithCol][jthRow].setOnKeyPressed(x2Grid_KeyEventHandler);
                tf[ithCol][jthRow].setOnKeyReleased(x2Grid_KeyEventHandler);
                gridPane.add(tf[ithCol][jthRow], ithCol, jthRow);
            }
        }

        tf[1][1].requestFocus();
    }   // end constructor
    
    public BorderPane getGridPane () {return borderPane; }
    
    public TextField getTF_col_row(int col, int row) {
        return tf[col][row];
    }
    
    @Override
    protected Skin<?> createDefaultSkin() { // Mastering JavaFX8 Controls, Chap 10
        return new X2GriddyWiddySkin(this);   // I have no clue what this does!
    }

    // Skin
    public class X2GriddyWiddySkin extends SkinBase<X2GriddyWiddy> {    // Mastering JavaFX8 Controls, Chap 10
        public X2GriddyWiddySkin(X2GriddyWiddy control) {               // I have no clue what this does!
            super(control);
        }
    }  
    
    public void horizBarResponse()  { }
    
    public void vertBarResponse()  { }    
         
    public void updateVertBar(int toHere) { }
    
    public void updateHorzBar(int toHere) { }
    
    EventHandler<KeyEvent> x2Grid_KeyEventHandler = new EventHandler<KeyEvent>()  {
        public void handle(KeyEvent ke) {           
            KeyCode keyCode = ke.getCode();  
            tfObject = ke.getSource();
            int row = GridPane.getRowIndex((TextField)tfObject);
            int col = GridPane.getColumnIndex((TextField)tfObject);           
            blankDataElement = tf[col][row].getText();

            if (ke.getEventType() == KeyEvent.KEY_TYPED) { }
            else
            if (ke.getEventType() == KeyEvent.KEY_PRESSED) { 
                if ((keyCode == KeyCode.TAB)  &&(ke.isShiftDown() == true)) {
                    doShiftTab(col, row, blankDataElement); 
                    ke.consume();
                }               
                else
                if (keyCode == KeyCode.TAB) {
                    doEnterKey(col, row, blankDataElement); 
                    ke.consume();
                }            
            }   //  end KEY_PRESSED
            else
            if (ke.getEventType() == KeyEvent.KEY_RELEASED) {
                if (null != keyCode) 
                switch (keyCode) {
                    case ENTER:
                        doEnterKey(col, row, blankDataElement);
                        break;
                    case UP:
                        doUpOneRow(col, row, blankDataElement);
                        break;
                    case DOWN:
                        doDownOneRow(col, row, blankDataElement);
                        break;
                    case LEFT:
                        doLeftOneColumn(col, row, blankDataElement);
                        break;
                    case RIGHT:
                        doRightOneColumn(col, row, blankDataElement);
                        break;                                   
                    default:
                        break;
                }                                   
            }   //  End KeyReleased
        }   //  end HandleKeyEvent
    };
   
    public void doUpOneRow(int columnInBlock, int rowInBlock, String gridDataElement){   
        if (rowInBlock > 1) { 
            tf[columnInBlock][rowInBlock - 1].requestFocus();
        }  else  { }
    }   //  end doUpOneRow
    
    public void doDownOneRow(int columnInBlock, int rowInBlock, String gridDataElement){  
        //                  This is the last row
        if (rowInBlock + 1 == numberOfRows) { 
            //System.out.println("I ain't movin'!!!");
        }     
        //         Not at bottom of block       &&        There are more rows 
        if (rowInBlock + 1 < numberOfRows) { 
            tf[columnInBlock][rowInBlock + 1].requestFocus();
        }  else  { }
    }
    
    public void doLeftOneColumn(int columnInBlock, int rowInBlock, String gridDataElement){        

        if (columnInBlock > 1) { 
            tf[columnInBlock - 1][rowInBlock].requestFocus();
        } else  { }
    }
    
    public void doRightOneColumn(int currentColumn, int currentRow, String gridDataElement){ 
        
        //                  This is the last variable
        if (currentColumn + 1 == numberOfColumns) { }     
        //         Not at right end of block       &&        There are more variables
        else 
        if (currentColumn < numberOfColumns) { 
            tf[currentColumn + 1][currentRow].requestFocus();
        } else  { }
    }
    
    public void doEnterKey(int currentColumn, int currentRow, String gridDataElement) {    
        tf[currentColumn][currentRow].setText(gridDataElement);
        handleEnter(currentColumn, currentRow, gridDataElement);
    }
    
    public void handleEnter(int currentColumn, int currentRow, String gridDataElement) { 
        
        //  If this is an observed value cell, check for an integer.
        
        if ((currentRow > 0) && (currentColumn > 0)) {  //  Numeric field?
            String temp = gridDataElement.trim();
            isAnInteger = DataUtilities.stringIsANonNegativeInteger(temp);
            if (!isAnInteger) {
                MyAlerts.showMustBeNonNegIntegerAlert();
                tf[currentColumn][currentRow].setText("");
                tf[currentColumn][currentRow].requestFocus();
                return;
            }   
        }

        //  Last column in grid?
        if ((currentColumn + 1 == numberOfColumns) &&
            (currentRow +1 < numberOfRows))  { 
            tf[1][currentRow + 1].requestFocus();
        }   
        else //  Last column and last row in grid?
        if ((currentColumn + 1 == numberOfColumns) &&
            (currentRow +1 ==  numberOfRows))  { 
            tf[1][1].requestFocus();
        }
        //         Not at right end of block       &&        There are more variables
        else 
        if (currentColumn < numberOfColumns) { 
            tf[currentColumn + 1][currentRow].requestFocus();
        } else  { }
    }
    
    public void doShiftTab(int currentColumn, int currentRow, String gridDataElement) {   
        tf[currentColumn][currentRow].setText(gridDataElement);
        handleShiftTab(currentColumn, currentRow, gridDataElement);
    }
    
    public void handleShiftTab(int currentColumn, int currentRow, String gridDataElement) { 
        
        if ((currentRow > 0) && (currentColumn > 0)) {//  Numeric field?
            String temp = gridDataElement.trim();
            isAnInteger = DataUtilities.stringIsANonNegativeInteger(temp);
            if (!isAnInteger) {
                MyAlerts.showMustBeNonNegIntegerAlert();
                tf[currentColumn][currentRow].setText("");
                tf[currentColumn][currentRow].requestFocus();
                return;
            }   
        }

        //  Last column in grid?
        if ((currentColumn == 1) && (currentRow > 1)) { 
            tf[nColumnsMinusOne][currentRow - 1].requestFocus();
        }   
        else  //  Last column and last row in grid?
        if ((currentColumn == 1) &&
            (currentRow == 1))  { 
            tf[nColumnsMinusOne][nRowsMinusOne].requestFocus();
        }
        //         Not at right end of block       &&        There are more variables
        else 
        if (currentColumn > 1) { 
            tf[currentColumn - 1][currentRow].requestFocus();
        } else  { }
    }   //  End handle shift tab
                         
    EventHandler<MouseEvent> textFieldMouseEventHandler = new EventHandler<MouseEvent>()  {
        @Override
        public void handle(MouseEvent me) {             
            if (me.getEventType() == MouseEvent.MOUSE_CLICKED) {
                tfObject = me.getSource();
  
                int gridPaneRow = GridPane.getRowIndex((TextField)tfObject);
                int gridPaneCol = GridPane.getColumnIndex((TextField)tfObject);
                //  First check to see if click is in the data range
                if ((gridPaneCol > nColumnsMinusOne) && (gridPaneRow >= numberOfRows)) { 
                    tf[nColumnsMinusOne][nRowsMinusOne].requestFocus();
                }
                else
                if (gridPaneCol >= numberOfColumns) { 
                    tf[nColumnsMinusOne][gridPaneRow].requestFocus();
                }
                else
                if (gridPaneRow >= numberOfColumns) { 
                    tf[gridPaneCol][nRowsMinusOne].requestFocus();
                }
            }
        }
    };  //  End Mouse event handler  

    public int getGriddyWiddy_IJ(int ithRow, int jthCol) {
        String preTrimmed = tf[jthCol + 1][ithRow + 1].getText();
        String trimmed = preTrimmed.trim();
        return Integer.parseInt(trimmed);
    }
} // X2GriddyWiddy
