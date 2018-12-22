/************************************************************
 *                       Splat_DataGrid                     *
 *                          11/22/18                        *
 *                            00:00                         *
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

// *********************************************************************
// To do:                                                              *
//        Resolve nCasesInGrid and nVisualCasesInGrid                  * 
//        Resolve nVariablesInGrid nVisualVisualVarsInGrid             * 
// *********************************************************************

package splat;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import genericClasses.*;
import java.util.ArrayList;

public class Splat_DataGrid {
    
    boolean bobClicksTwice,  // Do not remember what this is for; vestigial?
            comingFromLeftRightClick, enteringDataInGrid;
    
    private int nVarsInStruct,    
                nCasesInStruct,   
                nCasesInGrid,
                nVarsInGrid;
    
    int nVisualCasesInGrid, nVisualVarsInGrid, blueColumn, blueRow, 
        //  Need the following for gotoEnd
        firstDG_Var, firstDG_Case, firstDS_Case, firstDS_Var,
        lastDG_Var, lastDG_Case, lastDS_Var, lastDS_Case;
    
    // ****************************************************************
    // *  bgCol, bgRow are declared in many places to limit scope.    *
    // * Eventually get rid of them?                                  *
    // ****************************************************************
    
    String kCode, savedCellContents, type;
    
    //  My classes
    Splat_DataManager dm;
    PositionTracker tracker;
    Splat_EnterHandler enterHandler;
    CoordPairOfIntegers temp_CPI_DS;
    
    // POJOs / FX
    GridPane gridPane;
    
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
    
    public Splat_DataGrid(Splat_DataManager dm, PositionTracker tracker) { 
        // System.out.println("83 dg, Splat_DataGrid(Splat_DataManager dm, PositionTracker waldo)");
        this.dm = dm; 
        this.tracker = tracker;
        enterHandler = new Splat_EnterHandler(tracker, this, dm);
        gridPane = new GridPane();
        nVisualVarsInGrid = dm.getMaxVisVars();
        nVisualCasesInGrid = dm.getMaxVisCases();
        nCasesInStruct = tracker.getNCasesInStruct();
        nVarsInStruct = tracker.getNVarsInStruct();

        gridCellz = new ArrayList<ArrayList<TextField>>();
        
        // On 10/27/2018, replaced usage of function setOnKeyReleased() with a pressFilter.
        EventHandler<KeyEvent> pressFilter = new EventHandler <KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                //System.out.println("\n\n109 dg, handle(KeyEvent e)");
                type = e.getEventType().getName();
                String kCode1 = e.getCode().toString();
                //System.out.println("112 dg, type / kCode = " + type + " / " + kCode1);
                blueColumn = tracker.get_CurrentDG().getCol();
                blueRow = tracker.get_CurrentDG().getRow();
                nCasesInStruct = tracker.getNCasesInStruct();
                nVarsInStruct = tracker.getNVarsInStruct();
                if(type.equals("KEY_PRESSED")) {
                    // System.out.println("118 dg, type.equals(KEY_PRESSED)");
                    switch (kCode1) {
                        case "ENTER":
                            // System.out.println("121 dg, to doEnter()");
                            doEnter();
                            // System.out.println("123 dg, back from doEnter()");
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
                            // System.out.println("131 dg, switch default");
                    }
                }
                // System.out.println("134 dg, end handle(KeyEvent e)");
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
                    } catch (Exception exc)
                    {
                        System.out.println("There was an error fleshing out the gridPane with gridCells: " + exc.getMessage());                    
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
        // System.out.println("247 dg, upOneRow()");
        if (bobClicksTwice) {  return; }
      
        restoreSavedGridCell();
        temp_CPI_DS = tracker.get_CurrentDS();
        int bgCol = tracker.get_CurrentDG().getCol();
        int bgRow = tracker.get_CurrentDG().getRow();
        
        if (!tracker.cursorIsAtFirstCase() && !tracker.cursorIsAtTopOfGrid()) {
            // System.out.println("256 dg, !tracker.cursorIsAtFirstCase() && !tracker.cursorIsAtTopOfGrid()");
            tracker.set_CurrentDG(tracker.get_CurrentDG().getCol(), tracker.get_CurrentDG().getRow() - 1);
            tracker.set_CurrentDS(tracker.get_CurrentDS().getCol(), tracker.get_CurrentDS().getRow() - 1);
        }
        else {
            if(!tracker.cursorIsAtFirstCase() && tracker.cursorIsAtTopOfGrid()) {
                // System.out.println("262 dg, !tracker.cursorIsAtFirstCase() && tracker.cursorIsAtTopOfGrid()");
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
        // System.out.println("274 dg, tracker.get_CurrentDG().getRow() = " + tracker.get_CurrentDG().getRow());
        // System.out.println("275 dg, tracker.get_CurrentDS().getRow() = " + tracker.get_CurrentDS().getRow());
    } // upOne
    
    public void downOneRow() {
        // System.out.println("279 dg, downOneRow()");
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
        // System.out.println("301 dg, tracker.get_CurrentDG().getRow() = " + tracker.get_CurrentDG().getRow());
        // System.out.println("302 dg, tracker.get_CurrentDS().getRow() = " + tracker.get_CurrentDS().getRow());
    }

    public void leftOneCol() { 
        // System.out.println("306 dg, leftOneCol()");
        if (bobClicksTwice) {  return; }
      
        restoreSavedGridCell();
        temp_CPI_DS = tracker.get_CurrentDS();
        int bgCol = tracker.get_CurrentDG().getCol();
        int bgRow = tracker.get_CurrentDG().getRow();
        
        if (!tracker.cursorIsAtFirstVariable() && !tracker.cursorIsAtLeftOfGrid()) {
            // System.out.println("315 dg, !posTracker.cursorIsAtFirstVariable() && !posTracker.cursorIsAtLeftOfGrid()");
            tracker.set_CurrentDG(tracker.get_CurrentDG().getCol() - 1, tracker.get_CurrentDG().getRow());
            tracker.set_CurrentDS(tracker.get_CurrentDS().getCol() - 1, tracker.get_CurrentDS().getRow());
        }
        else {
            if(!tracker.cursorIsAtFirstVariable() && tracker.cursorIsAtLeftOfGrid()) {
                // System.out.println("321 dg, !posTracker.cursorIsAtFirstVariable() && posTracker.cursorIsAtLeftOfGrid()");
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
        // System.out.println("333 dg, tracker.get_CurrentDG().getRow() = " + tracker.get_CurrentDG().getRow());
        // System.out.println("334 dg, tracker.get_CurrentDS().getRow() = " + tracker.get_CurrentDS().getRow());
    } // leftOne

    private void rightOneCol() {
        // System.out.println("338 dg, rightOneCol()");
        if (bobClicksTwice) { return;}

        if (!tracker.cursorIsAtLastVariable() && !tracker.cursorIsAtRightOfGrid()) {
            // System.out.println("342 dg, !posTracker.cursorIsAtLastVariable() && !posTracker.cursorIsAtRightOfGrid()");
            restoreSavedGridCell();
            temp_CPI_DS = tracker.get_CurrentDS();
            int bgCol = tracker.get_CurrentDG().getCol();
            int bgRow = tracker.get_CurrentDG().getRow();
            tracker.set_CurrentDG(tracker.get_CurrentDG().getCol() + 1, tracker.get_CurrentDG().getRow());
            tracker.set_CurrentDS(tracker.get_CurrentDS().getCol() + 1, tracker.get_CurrentDS().getRow());
        }
        else
            if(!tracker.cursorIsAtLastVariable() && tracker.cursorIsAtRightOfGrid()) {
            // System.out.println("352 dg, !posTracker.cursorIsAtLastVariable() && posTracker.cursorIsAtRightOfGrid()");
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
                // System.out.println("365 dg, posTracker.cursorIsAtLastVariable() && !posTracker.cursorIsAtRightOfGrid()");
                // No-op;
        } else {
            if (tracker.cursorIsAtLastVariable() && tracker.cursorIsAtRightOfGrid()) {
                // System.out.println("369 dg, posTracker.cursorIsAtLastVariable() && posTracker.cursorIsAtRightOfGrid()");
                // No-op;
            }
            else { System.out.println ("dg 372, Unhandled!!!");
                System.exit(317);
            }
        }
        setComingFromLeftRightClick(true);
        // System.out.println("377 dg, tracker.get_CurrentDG().getRow() = " + tracker.get_CurrentDG().getRow());
        // System.out.println("378 dg, tracker.get_CurrentDS().getRow() = " + tracker.get_CurrentDS().getRow());
    } 
    
    private void upOnePage() {
        // System.out.println("382 dg, upOnePage()");
        restoreSavedGridCell();
        bobClicksTwice = false; 
        if (tracker.getFirstCaseIdentifier() >= nVisualCasesInGrid) { 
            // System.out.println("386 dg, posTracker.getFirstCaseIdentifier() >= maxCasesInGrid");
            tracker.setFirstCaseIdentifier(tracker.getFirstCaseIdentifier()- nVisualCasesInGrid);
            tracker.set_CurrentDS(tracker.get_CurrentDS().getCol(), tracker.get_CurrentDS().getRow() - nVisualCasesInGrid);
            dm.sendDataStructToGrid();
        }
    } 
    
    private void downOnePage() {  
        // System.out.println("394 dg, DownOnePage()");
        restoreSavedGridCell();
        bobClicksTwice = false; 
        if (tracker.getLastCaseInGrid() < nCasesInStruct) { 
            // System.out.println("398 dg, posTracker.getLastCaseInGrid() < nCasesInStruct");
            tracker.setFirstCaseIdentifier(tracker.getFirstCaseIdentifier()+ nVisualCasesInGrid);
            tracker.set_CurrentDS(tracker.get_CurrentDS().getCol(), tracker.get_CurrentDS().getRow() + nVisualCasesInGrid);
            dm.sendDataStructToGrid();
        }
    } // downOnePage

    public void goHome() {
        // System.out.println("406 dg, goHome()");
        bobClicksTwice = false; 
        tracker.set_ulDG(0, 0);
        tracker.set_ulDS(0, 0);
        tracker.set_CurrentDS(0, 0);
        dm.sendDataStructToGrid();
        tracker.set_CurrentDG(0, 0);
    } // goHome

    private void goToEnd() {
        // System.out.println("416 dg, goToEnd()");
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
        // System.out.println("461 dg, setBlueCell(int toThisCol, int toThisRow) " + toThisCol + " / " + toThisRow);
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

    private void setGridElement(int col, int row, String toThis) {
        gridCellz.get(col).get(row).setText(toThis); 
    }
    
    //***************************************************************************
    //                   Methods to help debugging                              *
    // *************************************************************************/
    public void printInfo(int lineNumber, String info) {
        //System.out.println("\n\n 513 dg, line number = " + lineNumber + ", " + info + "\n");
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
   
    private void restoreSavedGridCell() {
        int savedColumn = tracker.getRestorationCol();
        int savedRow = tracker.getRestorationRow();
        savedCellContents = tracker.getRestorationContents();
        setGridCell(savedColumn, savedRow, savedCellContents);
    }
}
