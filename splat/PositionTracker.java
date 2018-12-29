/**************************************************
 *                PositionTracker                 *
 *                    11/17/18                    *
 *                     18:00                      *
 *************************************************/

// It is possible (i.e. allowed) to click 'outside' the data structure in
// the DataGrid. This will of  course happen at initial data entry, but
// could also occur by  accident or if the data is 'sparse.'


// All coordinate systems are zero-based:
// 1.  The DataStruct of mCases x nCols
// 2.  The DataGrid

// Only the presented-to-user cases and variables will be 1-based, and will
// be coded with xxx + 1 subscripts.

package splat;
import dataObjects.CoordPairOfIntegers;
import genericClasses.*;

public class PositionTracker {
    int lastDataRowInGrid, lastDataColInGrid;
    
    int lastColumnVar;  //  Stupid, but initialized at 6
    int lastRowInGridWithActualData, lastColumnInGridWithActualData;
    int restorationRow, restorationCol;
    
    private int maxCasesInGrid, maxVarsInGrid;
    
    String restorationContents;
    
    CoordPairOfIntegers cpi_ulDG, cpi_lrDG, cpi_CurrentDG;
    CoordPairOfIntegers  cpi_ulDS, cpi_lrDS, cpi_CurrentDS; 
    
    int nVarsToAdd, nCasesToAdd;
    
    Data_Grid dg;
    Data_Manager dm;
    
    int firstCaseInGrid, lastCaseInGrid, firstVarInGrid, lastVarInGrid;
    int nCasesInGrid, nVarsInGrid;
    
    public PositionTracker() { }    //  Needed by Var_List??

    // Establishes a graphical window for the display of the data under consideration (??).
    public PositionTracker(Data_Manager dm, int max_var, int max_case) {
        maxCasesInGrid = max_case;
        maxVarsInGrid = max_var;
        //System.out.println("50 pt, PositionTracker(Data_Manager dm, int max_var, int max_case)");
        this.dm = dm;
        cpi_lrDG = new CoordPairOfIntegers();
        cpi_ulDG = new CoordPairOfIntegers();
        cpi_ulDS = new CoordPairOfIntegers();
        cpi_lrDS = new CoordPairOfIntegers();
        cpi_CurrentDG = new CoordPairOfIntegers();
        cpi_CurrentDS = new CoordPairOfIntegers();
        cpi_CurrentDS = new CoordPairOfIntegers();
        
        cpi_ulDG.setColAndRow(0, 0);
        lastDataRowInGrid = 0;
        lastDataColInGrid = 0; 
        
        cpi_lrDG.setCol(max_var - 1);  //  At init this is 6 columns
        cpi_lrDG.setRow(max_case - 1);  //  At init this is 12 rows
        cpi_lrDS.setColAndRow(0, 0);
        
        firstCaseInGrid = 0;
        lastCaseInGrid = max_case - 1;
        firstVarInGrid = 0;
        lastVarInGrid = max_var - 1;
    }
    
    public int getRestorationRow() { return restorationRow; }
    public void setRestorationRow(int toThisRow) { restorationRow = toThisRow; }
    
    public int getRestorationCol() { return restorationCol; }
    public void setRestorationCol(int toThisCol) { restorationCol = toThisCol; }
    
    public String getRestorationContents() { return restorationContents; }   
    public void setRestorationContents(String toThisValue) { 
        restorationContents = toThisValue; 
    }
    
    public void updateMaxCases(int newMax) { maxCasesInGrid = newMax; }
    public void updateMaxVars(int newMax) { maxVarsInGrid = newMax;}
    
    public void setTrackerDataGrid(Data_Grid dg) {this.dg = dg; }     
    
    public CoordPairOfIntegers get_ulDG() {return cpi_ulDG; } 
    public CoordPairOfIntegers get_lrDG() {return cpi_lrDG; }
    
    public CoordPairOfIntegers get_ulDS() {return cpi_ulDS; } 
    public CoordPairOfIntegers get_lrDS() {return cpi_lrDS; }
    
    public void set_lrDS(int toThis_DSCol, int toThis_DSRow) {
        cpi_lrDS.setCol(toThis_DSCol);
        cpi_lrDS.setRow(toThis_DSRow);
        int dsCol = cpi_lrDS.getCol();
        int dsRow = cpi_lrDS.getRow();
        //System.out.println("101 pt, currentDS:  cpi_lrDS_Col / cpi_lrDS_Row = " + dsCol + " / " + dsRow); 
    }
    
    public void set_ulDS(int toThis_DSCol, int toThis_DSRow) {
        cpi_ulDS.setCol(toThis_DSCol);
        cpi_ulDS.setRow(toThis_DSRow);
        //System.out.println("107 pt, currentDS:  cpi_ulDS_Col / cpi_ulDS_Row = " + toThis_DSCol + " / " + toThis_DSRow); 
    } 
    
    public void set_lrDG(int toThis_DGCol, int toThis_DGRow) {
        cpi_lrDG.setCol(toThis_DGCol);
        cpi_lrDG.setRow(toThis_DGRow); 
        //System.out.println("113 pt, currentDS:  cpi_lrDG_Col / cpi_lrDG_Row = " + toThis_DGCol + " / " + toThis_DGRow); 
    }  
    
    public void set_ulDG(int toThis_DGCol, int toThis_DGRow) {
        cpi_ulDG.setCol(toThis_DGCol);
        cpi_ulDG.setRow(toThis_DGRow);
        firstVarInGrid = toThis_DGCol;
        firstCaseInGrid = toThis_DGRow;
        // System.out.println("121 pt, currentDS:  cpi_ulDG_Col / cpi_ulDG_Row = " + toThis_DGCol + " / " + toThis_DGRow); 
    } 
     
    public CoordPairOfIntegers get_CurrentDG() { return cpi_CurrentDG; }

    public void set_CurrentDG(int toThisCol, int toThisRow) {
        cpi_CurrentDG.setColAndRow(toThisCol, toThisRow);
        dg.setBlueCell(toThisCol, toThisRow);
        // System.out.println("139 pt,  set blue cell toThisCol / toThisRow = " + toThisCol + " / " + toThisRow);
        //  And save information about currentDG for future restoration
        restorationCol = toThisCol; 
        restorationRow = toThisRow;
        restorationContents = dg.getGridCell(toThisCol, toThisRow);
        dg.setGridCell(toThisCol, toThisRow, "");
        // System.out.println("135 pt, restCol/restRow/restVal = " + restorationCol + " / " + restorationRow + " / " + restorationContents);
    } 
    
    public CoordPairOfIntegers get_CurrentDS() { return cpi_CurrentDS; }
  
     public void set_CurrentDS(int toThisCol, int toThisRow) {
        cpi_CurrentDS.setColAndRow(toThisCol, toThisRow);
        int dsCol = cpi_CurrentDS.getCol();
        int dsRow = cpi_CurrentDS.getRow();
        //System.out.println("144 pt, currentDS:  dsCol / dsRow = " + dsCol + " / " + dsRow);    
    }
     
    public int getMaxCasesInGrid() { return maxCasesInGrid; }
    public int getMaxVarsInGrid() { return maxVarsInGrid; }


    public CoordPairOfIntegers cpiDG_to_cpiDS(CoordPairOfIntegers cpi_DG) {
        //System.out.println("152 pt, cpi_DG = " + cpi_DG.toString());
        CoordPairOfIntegers cpi_transformed = new CoordPairOfIntegers();
        cpi_transformed.setCol(cpi_DG.getCol() + cpi_ulDG.getCol());
        cpi_transformed.setCol(cpi_DG.getRow() + cpi_ulDG.getRow());
        //System.out.println("156 pt, cpi_transformed_toDS = " + cpi_transformed.toString());
        return cpi_transformed;
    } 
   
    public CoordPairOfIntegers cpiDS_to_cpiDG(CoordPairOfIntegers cpi_DS) {
        //System.out.println("161 pt, cpi_DS = " + cpi_DS.toString());
        CoordPairOfIntegers cpi_transformed = new CoordPairOfIntegers();
        cpi_transformed.setCol(cpi_DS.getCol() - cpi_ulDG.getCol());
        cpi_transformed.setRow(cpi_DS.getRow() - cpi_ulDG.getRow());
        //System.out.println("165 pt, cpi_transformed_toDG = " + cpi_transformed.toString());
        return cpi_transformed;
    }     

    public int getNCasesInStruct() { return cpi_lrDS.getRow(); } 

    // Only called at file read??;
    public void setNCasesInStruct(int toThis) { cpi_lrDS.setRow(toThis); }
    
    public int getNVarsInStruct() { return cpi_lrDS.getCol(); }
    public void setNVarsInStruct(int toThis) { cpi_lrDS.setCol( toThis); }   

    public int getFirstCaseIdentifier() { return firstCaseInGrid; }
    public int getFirstVarIdentifier() { return firstVarInGrid; }

    public int getLastCaseInGrid() { return lastCaseInGrid;  }  
    
    public void setFirstVarIdentifier(int toThisCol) { 
        firstVarInGrid = toThisCol;
        cpi_ulDG.setCol(toThisCol);
        cpi_ulDS.setCol(toThisCol);
        lastVarInGrid = firstVarInGrid + maxVarsInGrid - 1;
        cpi_lrDG.setCol(lastVarInGrid);
    }
 
    // To do:  Untangle this first/last case mess!!!
    public void setFirstCaseIdentifier(int toThis) { 
        // System.out.println("192 pt, setFirstCaseIdentifier(int toThis) = " + toThis);
        firstCaseInGrid = toThis; 
        lastCaseInGrid = firstCaseInGrid + maxCasesInGrid - 1;
        set_ulDG(cpi_ulDG.getCol(), firstCaseInGrid);
        set_ulDS(cpi_ulDS.getCol(), firstCaseInGrid);
    }
    
    //  These methods have a relatively ambiguous names.  The intended sense
    //  is to indicate where the cursor was at the time the mouse was clicked,
    //  not where it landed as a response to the mouse click.
    
    public boolean cursorIsAtFirstCase() {
        boolean cursorIsAtFirstCase = (cpi_CurrentDS.getRow() == 0);
        //System.out.println("205 pt, Cursor at first case = " + cursorIsAtFirstCase);
        return cursorIsAtFirstCase;       
    }

    public boolean cursorIsAtLastCase() {
        boolean cursorIsAtLastCase = (cpi_CurrentDS.getRow() >= cpi_lrDS.getRow());
        //System.out.println("211 pt, Cursor at last case = " + cursorIsAtLastCase);
        return cursorIsAtLastCase;       
    }
    
    public boolean cursorIsAtFirstVariable() {
        boolean cursorIsAtFirstVariable = (cpi_CurrentDS.getCol() == 0);
        // System.out.println("217 pt, cursorIsAtFirstVariable = " + cursorIsAtFirstVariable);
        return cursorIsAtFirstVariable;       
    }
    
    public boolean cursorIsAtLastVariable() {
        boolean cursorIsAtLastVariable = (cpi_CurrentDS.getCol() == cpi_lrDS.getCol() - 1);
        //System.out.println("223 pt, cursorIsAtLastVariable = " + cursorIsAtLastVariable);
        return cursorIsAtLastVariable;          
    }
    
    public boolean cursorIsAtTopOfGrid() {
        boolean cursorIsAtTopOfGrid = (cpi_CurrentDG.getRow() == 0);
        // System.out.println("229 pt, cursorIsAtTopOfGrid = " + cursorIsAtTopOfGrid);
        return cursorIsAtTopOfGrid;       
    }
    
    public boolean cursorIsAtBottomOfGrid() {
        boolean cursorIsAtBottomOfGrid = (cpi_CurrentDG.getRow() == (maxCasesInGrid - 1));
        //System.out.println("235 pt, cursorIsAtBottomOfGrid = " + cursorIsAtBottomOfGrid);
        return cursorIsAtBottomOfGrid;       
    }   
     
    public boolean cursorIsAtLeftOfGrid() {
        boolean cursorIsAtLeftOfGrid = (cpi_CurrentDG.getCol() == 0);
        //System.out.println("241 pt, cursorIsAtLeftOfGrid = " + cursorIsAtLeftOfGrid);
        return cursorIsAtLeftOfGrid;       
    }
     
    public boolean cursorIsAtRightOfGrid() {
        boolean cursorIsAtRightOfGrid = (cpi_CurrentDG.getCol() == maxVarsInGrid - 1);
        //System.out.println("247 pt, cursorIsAtRightOfGrid = " + cursorIsAtRightOfGrid);  
        return cursorIsAtRightOfGrid;       
    } 
}
