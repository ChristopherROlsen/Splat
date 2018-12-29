/************************************************************
 *                          DataGrid                        *
 *                          12/26/18                        *
 *                            18:00                         *
 ***********************************************************/

/**************************************************
*  All coordinate systems are zero-based:         *
*  1.  The DataStruct of mCases x nCols           *
*  2.  The DataGrid                               *
*                                                 *
*  Only the presented-to-user cases and variables *
*  will be 1-based, and will appear in the code   *
*  with xxx + 1 subscripts.                       *
*                                                 *
*  Todo:  Is bobclickstwice vestigial???          *
                                                  *
**************************************************/

package splat;

import dataObjects.CoordPairOfIntegers;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import java.util.ArrayList;
import utilityClasses.PrintExceptionInfo;

public class Data_Grid {
    
    boolean bobClicksTwice,  // Do not remember what this is for; vestigial?
            comingFromLeftRightClick, enteringDataInGrid;
    
    private int nVarsInStruct,    
                nCasesInStruct,   
                nCasesInGrid,
                nVarsInGrid;
    
    private int nVisualCasesInGrid, nVisualVarsInGrid, blueColumn, blueRow, 
        //  Need the following for gotoEnd
        firstDG_Var, firstDG_Case, firstDS_Case, firstDS_Var,
        lastDG_Var, lastDG_Case, lastDS_Var, lastDS_Case;
    
    // ****************************************************************
    // *  bgCol, bgRow are declared in many places to limit scope.    *
    // * Eventually get rid of them?                                  *
    // ****************************************************************
    
    private String kCode, savedCellContents, type;
    
    //  My classes
    private Data_Manager dm;
    private PositionTracker tracker;
    private EnterHandler enterHandler;
    private CoordPairOfIntegers temp_CPI_DS;
    
    // POJOs / FX
    private GridPane gridPane;
    
    private ArrayList<ArrayList<TextField>>gridCellz;
    
    EventHandler<MouseEvent> mouseHandler = (MouseEvent me) -> {
    /*********************************************************************
     *  In the event of a double click the intent is to edit within the   *
     *  grid cell.  Setting bobClicksTwice (Named for Bob Hayden, who     *
     *  suggested the idea) to true intercepts the left- and right-arrow  *
     *  cell traversal.  Any movement other than left- or right-arrow     *
     *  sets bobClicksTwice to false, restoring cell traversal.           *
     *********************************************************************/

        if(me.getClickCount() == 2){
            bobClicksTwice = true;  
        }
        restoreSavedGridCell();
        Object tfObject = me.getSource();
        blueRow = GridPane.getRowIndex((TextField) tfObject);
        blueColumn = GridPane.getColumnIndex((TextField) tfObject);

        int tempVarNumber = tracker.get_ulDG().getCol() + blueColumn;
        int tempCaseNumber = tracker.get_ulDG().getRow() + blueRow;
        tracker.set_CurrentDS(tempVarNumber, tempCaseNumber);
        tracker.set_CurrentDG(blueColumn, blueRow);
    };  //  End mouse handler
    
    public Data_Grid(Data_Manager dm, PositionTracker tracker) { 
        this.dm = dm; 
        this.tracker = tracker;
        enterHandler = new EnterHandler(tracker, this, dm);
        gridPane = new GridPane();
        nVisualVarsInGrid = dm.getMaxVisVars();
        nVisualCasesInGrid = dm.getMaxVisCases();
        nCasesInStruct = tracker.getNCasesInStruct();
        nVarsInStruct = tracker.getNVarsInStruct();

        gridCellz = new ArrayList<>();
        EventHandler<KeyEvent> pressFilter = (KeyEvent e) -> {
            type = e.getEventType().getName();
            String kCode1 = e.getCode().toString();
            blueColumn = tracker.get_CurrentDG().getCol();
            blueRow = tracker.get_CurrentDG().getRow();
            nCasesInStruct = tracker.getNCasesInStruct();
            nVarsInStruct = tracker.getNVarsInStruct();
            if(type.equals("KEY_PRESSED")) {
                switch (kCode1) {
                    case "ENTER":
                        doEnter();
                        //  Consume event so that no additional listener
                        // handles it and gives the effect of two ENTERs
                        e.consume();
                        break;
                    case "DOWN":
                        downOneRow();
                        break;
                    case "UP": upOneRow(); break;
                    case "RIGHT": rightOneCol(); break;
                    case "TAB": rightOneCol(); break;
                    case "LEFT": leftOneCol(); break;
                    case "PAGE_DOWN": downOnePage(); break;
                    case "PAGE_UP": upOnePage(); break;
                    case "HOME": goHome(); break;
                    case "END": goToEnd(); break;
                    default:
                }
            }
        };
        
        gridPane.addEventFilter(KeyEvent.KEY_PRESSED, pressFilter);
        gridPane.addEventFilter(KeyEvent.KEY_RELEASED, pressFilter);
        gridPane.addEventFilter(KeyEvent.KEY_TYPED, pressFilter);
        
        // ***** ***** Lay out the grid ***** *****
        for (int thisVar = 0; thisVar < nVisualVarsInGrid; thisVar++) {
                    
            ArrayList<TextField> newAL = new ArrayList<>();
            
            for (int thisCase = 0; thisCase < nVisualCasesInGrid; thisCase++) {
                TextField tf = new TextField("");
                tf.getStyleClass().add("cells");
                tf.setEditable(true);
                tf.setOnMousePressed(mouseHandler);
                             
                newAL.add(tf);
            }
            gridCellz.add(newAL);
        } 
              
        for (int thisVar = 0; thisVar < nVisualVarsInGrid; thisVar++) {
            for (int thisCase = 0; thisCase < nVisualCasesInGrid; thisCase++) {
                gridPane.add(gridCellz.get(thisVar).get(thisCase), thisVar, thisCase);
            }
        }          
    }   //  End constructor
    
    public void adjustGridHeightAndWidth(int newMaxCasesInGrid, int newMaxVarsInGrid)
    {
        // ***** ***** Lay out the grid ***** *****
        for (int thisVar = 0; thisVar < newMaxVarsInGrid; thisVar++) {
            
            if (thisVar > nVisualVarsInGrid-1) // append new column if we've gone beyond the former boundary
            {
                ArrayList<TextField> newAL = new ArrayList<>();
            
                for (int thisCase = 0; thisCase < nVisualCasesInGrid; thisCase++) { // use the old value here.  Will add on new rows all at once later.

                    TextField tf = new TextField("");
                    tf.getStyleClass().add("cells");
                    tf.setEditable(true);
                    tf.setOnMousePressed(mouseHandler);

                    newAL.add(tf);                  
                }
                gridCellz.add(thisVar,newAL);
                
           for (int thisCase = 0; thisCase < nVisualCasesInGrid; thisCase++) 
                {
                    try {
                        gridPane.add(gridCellz.get(thisVar).get(thisCase), thisVar, thisCase);
                    } catch (Exception ex)
                    {
                        PrintExceptionInfo pei = new PrintExceptionInfo( ex, "Exception in DataGrid, line 187");                    
                    }                      
                }
            } else if (thisVar < nVisualVarsInGrid)
                for (int localVar = nVisualVarsInGrid - 1; localVar >= newMaxVarsInGrid; localVar--)
                {
                    for (int localRow = 0; localRow < nVisualCasesInGrid; localRow++)           
                        gridPane.getChildren().remove(gridCellz.get(localVar).get(localRow));
                }
                            
            if (newMaxCasesInGrid > nVisualCasesInGrid)
            {
                for (int thisCase = nVisualCasesInGrid; thisCase < newMaxCasesInGrid; thisCase++) {
                
                    TextField tf = new TextField("");
                    tf.getStyleClass().add("cells");
                    tf.setEditable(true);
                    tf.setOnMousePressed(mouseHandler);

                    ArrayList<TextField> al = gridCellz.get(thisVar);
                    al.add(tf);
                    gridPane.add(gridCellz.get(thisVar).get(thisCase), thisVar, thisCase);
                }                             
            } else if (newMaxCasesInGrid < nVisualCasesInGrid)              
                for (int thisCase = nVisualCasesInGrid-1; thisCase >= newMaxCasesInGrid; thisCase--)  
                    gridPane.getChildren().remove(gridCellz.get(thisVar).get(thisCase));    
        }

        nVisualCasesInGrid = newMaxCasesInGrid;
        nVisualVarsInGrid = newMaxVarsInGrid;
    }
    
    public void setPosTracker(PositionTracker tracker) { this.tracker = tracker; }
     
    private void doEnter() {    
        blueColumn = tracker.get_CurrentDG().getCol();
        blueRow = tracker.get_CurrentDG().getRow();
        dm.setDataIsClean(false);
        bobClicksTwice = false; 
        String tempGridString = gridCellz.get(blueColumn).get(blueRow).getText();
        enterHandler.handleTableEntry (blueColumn, blueRow, tempGridString);  
    } // End doEnter(String theKey) {   

    public void upOneRow() { 
        if (bobClicksTwice) {  return; }
      
        restoreSavedGridCell();
        temp_CPI_DS = tracker.get_CurrentDS();
        int bgCol = tracker.get_CurrentDG().getCol();
        int bgRow = tracker.get_CurrentDG().getRow();
        
        if (!tracker.cursorIsAtFirstCase() && !tracker.cursorIsAtTopOfGrid()) {
            tracker.set_CurrentDG(tracker.get_CurrentDG().getCol(), tracker.get_CurrentDG().getRow() - 1);
            tracker.set_CurrentDS(tracker.get_CurrentDS().getCol(), tracker.get_CurrentDS().getRow() - 1);
        }
        else {
            if(!tracker.cursorIsAtFirstCase() && tracker.cursorIsAtTopOfGrid()) {
                restoreSavedGridCell();
                temp_CPI_DS = tracker.get_CurrentDS();            
                bgCol = tracker.get_CurrentDG().getCol();
                bgRow = tracker.get_CurrentDG().getRow();            
                
                tracker.set_CurrentDS(tracker.get_CurrentDS().getCol(), tracker.get_CurrentDS().getRow() - 1);
                tracker.setFirstCaseIdentifier(tracker.getFirstCaseIdentifier() - 1);
                dm.sendDataStructToGrid();
                tracker.set_CurrentDG( bgCol, 0 );
            }
        }
    } // upOne
    
    public void downOneRow() {
        restoreSavedGridCell();
        temp_CPI_DS = tracker.get_CurrentDS();
        
        int bgCol = tracker.get_CurrentDG().getCol();
        int bgRow = tracker.get_CurrentDG().getRow();

        if (!tracker.cursorIsAtBottomOfGrid()) {
            tracker.set_CurrentDS(tracker.get_CurrentDS().getCol(), tracker.get_CurrentDS().getRow() + 1);
            tracker.set_CurrentDG(bgCol, bgRow + 1);
        }
        else {  //  Cursor at bottom of grid
            restoreSavedGridCell();
            temp_CPI_DS = tracker.get_CurrentDS();            
            bgCol = tracker.get_CurrentDG().getCol();
            bgRow = tracker.get_CurrentDG().getRow();            

            tracker.set_CurrentDS(tracker.get_CurrentDS().getCol(), tracker.get_CurrentDS().getRow() + 1);
            tracker.setFirstCaseIdentifier(tracker.getFirstCaseIdentifier() + 1);
            dm.sendDataStructToGrid();
            tracker.set_CurrentDG( bgCol, bgRow );
        }  
    }

    public void leftOneCol() { 
        if (bobClicksTwice) {  return; }
      
        restoreSavedGridCell();
        temp_CPI_DS = tracker.get_CurrentDS();
        int bgCol = tracker.get_CurrentDG().getCol();
        int bgRow = tracker.get_CurrentDG().getRow();
        
        if (!tracker.cursorIsAtFirstVariable() && !tracker.cursorIsAtLeftOfGrid()) {
            tracker.set_CurrentDG(tracker.get_CurrentDG().getCol() - 1, tracker.get_CurrentDG().getRow());
            tracker.set_CurrentDS(tracker.get_CurrentDS().getCol() - 1, tracker.get_CurrentDS().getRow());
        }
        else {
            if(!tracker.cursorIsAtFirstVariable() && tracker.cursorIsAtLeftOfGrid()) {
                restoreSavedGridCell();
                temp_CPI_DS = tracker.get_CurrentDS();            
                bgCol = tracker.get_CurrentDG().getCol();
                bgRow = tracker.get_CurrentDG().getRow();            
                
                tracker.set_CurrentDS(tracker.get_CurrentDS().getCol() - 1, tracker.get_CurrentDS().getRow());
                tracker.setFirstVarIdentifier(tracker.getFirstVarIdentifier() - 1);
                dm.sendDataStructToGrid();
                tracker.set_CurrentDG( 0, bgRow );
            }
        }
    } // leftOne

    private void rightOneCol() {
        if (bobClicksTwice) { return;}

        if (!tracker.cursorIsAtLastVariable() && !tracker.cursorIsAtRightOfGrid()) {
            restoreSavedGridCell();
            temp_CPI_DS = tracker.get_CurrentDS();
            int bgCol = tracker.get_CurrentDG().getCol();
            int bgRow = tracker.get_CurrentDG().getRow();
            tracker.set_CurrentDG(tracker.get_CurrentDG().getCol() + 1, tracker.get_CurrentDG().getRow());
            tracker.set_CurrentDS(tracker.get_CurrentDS().getCol() + 1, tracker.get_CurrentDS().getRow());
        }
        else
            if(!tracker.cursorIsAtLastVariable() && tracker.cursorIsAtRightOfGrid()) {
                restoreSavedGridCell();
                temp_CPI_DS = tracker.get_CurrentDS();            
                int bgCol = tracker.get_CurrentDG().getCol();
                int bgRow = tracker.get_CurrentDG().getRow();            
                
                tracker.set_CurrentDS(tracker.get_CurrentDS().getCol() + 1, tracker.get_CurrentDS().getRow());
                tracker.setFirstVarIdentifier(tracker.getFirstVarIdentifier() + 1);
                dm.sendDataStructToGrid();
                tracker.set_CurrentDG(nVisualVarsInGrid - 1, bgRow );
            }
        else 
            if (tracker.cursorIsAtLastVariable() && !tracker.cursorIsAtRightOfGrid()) {
                // No-op;
        } else {
            if (tracker.cursorIsAtLastVariable() && tracker.cursorIsAtRightOfGrid()) {
                // No-op;
            }
            else { System.out.println ("dg 333, Unhandled!!!");
                System.exit(334);
            }
        }
        setComingFromLeftRightClick(true);
    } 
    
    private void upOnePage() {
        restoreSavedGridCell();
        bobClicksTwice = false; 
        if (tracker.getFirstCaseIdentifier() >= nVisualCasesInGrid) { 
            tracker.setFirstCaseIdentifier(tracker.getFirstCaseIdentifier()- nVisualCasesInGrid);
            tracker.set_CurrentDS(tracker.get_CurrentDS().getCol(), tracker.get_CurrentDS().getRow() - nVisualCasesInGrid);
            dm.sendDataStructToGrid();
        }
    } 
    
    private void downOnePage() {  
        restoreSavedGridCell();
        bobClicksTwice = false; 
        if (tracker.getLastCaseInGrid() < nCasesInStruct) { 
            tracker.setFirstCaseIdentifier(tracker.getFirstCaseIdentifier()+ nVisualCasesInGrid);
            tracker.set_CurrentDS(tracker.get_CurrentDS().getCol(), tracker.get_CurrentDS().getRow() + nVisualCasesInGrid);
            dm.sendDataStructToGrid();
        }
    } // downOnePage

    public void goHome() {
        bobClicksTwice = false; 
        tracker.set_ulDG(0, 0);
        tracker.set_ulDS(0, 0);
        tracker.set_CurrentDS(0, 0);
        dm.sendDataStructToGrid();
        tracker.set_CurrentDG(0, 0);
    } // goHome

    private void goToEnd() {
        nVarsInStruct = tracker.getNVarsInStruct();
        nCasesInStruct = tracker.getNCasesInStruct();
        nVisualVarsInGrid = tracker.getMaxVarsInGrid();
        nVisualCasesInGrid = tracker.getMaxCasesInGrid();
        
        if (nVarsInStruct <= nVisualVarsInGrid) {
            firstDG_Var = 0; 
            lastDG_Var = nVarsInStruct - 1;
            firstDS_Var = 0;
        }
        else {
            firstDG_Var = nVarsInStruct - nVisualVarsInGrid; 
            lastDG_Var = nVisualVarsInGrid - 1;
            firstDS_Var = nVarsInStruct - nVisualVarsInGrid;         
        }
        
        if (nCasesInStruct <= nVisualCasesInGrid) {
            firstDG_Case = 0; 
            lastDG_Case = nCasesInStruct - 1;
            firstDS_Case = 0;
        }
        else {
            firstDG_Case = nCasesInStruct - nVisualCasesInGrid;
            lastDG_Case = nVisualCasesInGrid - 1;
            firstDS_Case = nCasesInStruct - nVisualCasesInGrid;        
        }        

        restoreSavedGridCell();
        bobClicksTwice = false; 
        tracker.set_ulDG(firstDG_Var, firstDS_Case);
        tracker.set_ulDS(firstDS_Var, firstDS_Case);
        tracker.set_CurrentDS(firstDS_Var, firstDS_Case);
        tracker.setFirstCaseIdentifier(firstDS_Case);
        tracker.setFirstVarIdentifier(firstDS_Var);
        dm.sendDataStructToGrid();
        tracker.set_CurrentDG(lastDG_Var, lastDG_Case);   
    } // goEnd
     
    // ***********************************************************************
    // *            Methods to cut down on repetitive code                   *
    // **********************************************************************/
 
    // Data already copied from the dataStruct to the grid
    public void setBlueCell(int toThisCol, int toThisRow) { 
        makeEmAllWhite();
        gridCellz.get(toThisCol).get(toThisRow).requestFocus();
        gridCellz.get(toThisCol).get(toThisRow).setStyle("-fx-background-color: lightblue;"); 
    } 
    
    private void makeEmAllWhite() {
        for (int col = 0 ; col < nVisualVarsInGrid; col++) {
            for (int row = 0; row < nVisualCasesInGrid; row++) {
                gridCellz.get(col).get(row).setStyle("-fx-background-color: white;");     
            }
        }
    }

    public String getGridCell(int col, int row) { 
        return gridCellz.get(col).get(row).getText(); 
    }

    public void setGridCell(int col, int row, String toThis) {
    // ************************************************************************
    // * This is a hack to prevent the String from overflowing the TextField. *
    // * Overflow leads to weird values in the grid, apparently b/c the       *
    // * resulting string is right justified.  This should not result in      *
    // * much loss in accuracy b/c iterative procedures do not show the       *
    // * intermediate results in the grid.  A possible way to get more        *
    // * precision might be to set the TextFields larger.  Experiment at      *
    // * great peril!!                                                         *
    // ************************************************************************
        if (toThis.length() > 10) {
            toThis = toThis.substring(0, 9);
        }
        gridCellz.get(col).get(row).setText(toThis);
    }

    public GridPane getGridPane() {return gridPane; }
    

    public boolean getComingFromLeftRightClick() {
        return comingFromLeftRightClick;
    }
    
    public void setComingFromLeftRightClick( boolean toThis) {
        comingFromLeftRightClick = toThis;
    }

   public void setNCasesInGrid(int toThis) { nCasesInGrid = toThis; }
    
   public void setNVarsInGrid(int toThis) { nVarsInGrid = toThis; }
   
    private void restoreSavedGridCell() {
        int savedColumn = tracker.getRestorationCol();
        int savedRow = tracker.getRestorationRow();
        savedCellContents = tracker.getRestorationContents();
        setGridCell(savedColumn, savedRow, savedCellContents);
    }
    
    //***************************************************************************
    //                   Methods to help debugging                              *
    // *************************************************************************/
    public void printInfo(int lineNumber, String info) {
        //System.out.println("\n\n 464 dg, line number = " + lineNumber + ", " + info + "\n");
        //System.out.println("                         kCode = " + kCode);
        // System.out.println("                   currentVar = " + currentVar);
        // System.out.println("    tracker.getCurrentDS() = " + tracker.getCurrentDS());
        //System.out.println("                   nVarsInStruct = " + tracker.getNVarsInStruct()); 
        //System.out.println("                  nCasesInStruct = " + tracker.getNCasesInStruct()); 
        // System.out.println("              firstCaseInGrid = " + firstCaseInGrid);
        // System.out.println("               lastCaseInGrid = " + lastCaseInGrid);
        //System.out.println("   waldo.getCurrentBG() " + tracker.get_CurrentDG()); 
        //System.out.println("                firstVarInGrid = " + firstVarInGrid); 
        //System.out.println("                 lastVarInGrid = " + lastVarInGrid); 
        // System.out.println("       cursorIsAtBottomOfGrid = " + cursorIsAtBottomOfGrid());
        //System.out.println("                    nCasesInGrid = " + nCasesInGrid);
        //System.out.println("                  nCasesInStruct = " + nCasesInStruct);
        
        //for (int iCol = 0; iCol < nVarsInStruct; iCol++) {
        //    System.out.println("iCol / size = " + iCol + " / " + dm.getAllTheColumns().get(iCol).getColumnSize());
        //}
    }
}
