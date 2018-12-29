/**************************************************
 *                 EnterHandler                   *
 *                    11/09/18                    *
 *                     00:00                      *
 *************************************************/

package splat;

import dataObjects.ColumnOfData;
import dataObjects.CoordPairOfIntegers;

public class EnterHandler {
    
    boolean entryToRightOfData, entryBelowData;
    
    int nVarsToAdd, nCasesToAdd, nVarsInStruct, nCasesInStruct,dg_EntryCol, 
        dg_EntryRow, dsRow_AtEntry, dsCol_AtEntry, actualCol, actualRow,
        maxVarsInGrid;
    
    String theEntry;
    
    CoordPairOfIntegers theDG, theDS, theDS_AtEntry; //  temp vars only
    PositionTracker tracker;
    Data_Grid dg;
    Data_Manager dm;
            
    public EnterHandler( PositionTracker tracker, Data_Grid dg, Data_Manager dm) {
        this.tracker = tracker;
        this.dg = dg;
        this.dm = dm;
        maxVarsInGrid = dm.getMaxVisVars();
    }
    
    public void handleTableEntry (int dg_EntryCol, int dg_EntryRow, String theStringEntered) {
        System.out.println("\n35 seh, handleTableEntry (int entryColumn, int entryRow, String theStringEntered)");
        System.out.println("36 seh, Entry Col/Row/String = " + dg_EntryCol + " / " + dg_EntryRow + " / " + theStringEntered);
        this.dg_EntryCol = dg_EntryCol;
        this.dg_EntryRow = dg_EntryRow;
        dsCol_AtEntry = tracker.get_lrDS().getCol();
        dsRow_AtEntry = tracker.get_lrDS().getRow();
        nVarsInStruct = tracker.getNVarsInStruct();

        theEntry = theStringEntered;
        actualCol = tracker.getFirstVarIdentifier() + dg_EntryCol;               
        actualRow = tracker.getFirstCaseIdentifier() + dg_EntryRow;  
        tracker.set_CurrentDG(dg_EntryCol, dg_EntryRow);
        tracker.set_CurrentDS(actualCol, actualRow);

        System.out.println("49 seh, tracker.cursorIsAtLastCase()= " + tracker.cursorIsAtLastCase());
        System.out.println("50 seh, tracker.cursorIsAtBottomOfGrid()= " + tracker.cursorIsAtBottomOfGrid());
        System.out.println("51 seh, tracker.get_CurrentDS().getRow() = " + tracker.get_CurrentDS().getRow());
        System.out.println("52 seh, tracker.get_lrDS().getRow() = " + tracker.get_lrDS().getRow());
        System.out.println("53 seh, actualCol = " + actualCol);
        System.out.println("54 seh, actualRow = " + actualRow);
        
        int actRowPlus1 = actualRow + 1;
        int actColPlus1 = actualCol + 1;
        
        if (!dm.getdataExists()) {  //  If no data yet
            System.out.println("60 seh, No Data Yet");
            System.out.println("61 seh, Entry Col/Row = " + dg_EntryCol + " / " + dg_EntryRow);
            dm.initialDataEntry(dg_EntryCol, dg_EntryRow,  theStringEntered);
            tracker.set_CurrentDG(dg_EntryCol, dg_EntryRow + 1);
            tracker.set_CurrentDS(actualCol, actualRow + 1);
            System.out.println("65 seh, end No Data Yet");
            System.out.println("66 seh, Entry Col/Row = " + dg_EntryCol + " / " + dg_EntryRow);
        }       
        else if ((actColPlus1 > tracker.getNVarsInStruct()) && (actRowPlus1 > tracker.getNCasesInStruct())) {
            System.out.println("69 seh, Right and down");
            System.out.println("70 seh, Entry Col/Row = " + dg_EntryCol + " / " + dg_EntryRow);
            handleEntryToRightAndDown();
            System.out.println("72 seh, end Right and down");
            System.out.println("73 seh, Entry Col/Row = " + dg_EntryCol + " / " + dg_EntryRow);
        }
        else if ((actColPlus1 > tracker.getNVarsInStruct()) || (actRowPlus1 > tracker.getNCasesInStruct())) {
            // Click is outside existing data rectangle
            System.out.println("77 seh, to the right or below");
            System.out.println("78 seh, Entry Col/Row = " + dg_EntryCol + " / " + dg_EntryRow);
            if (actColPlus1 > tracker.getNVarsInStruct()) {
                System.out.println("80 seh, to the right");
                System.out.println("81 seh, Entry Col/Row = " + dg_EntryCol + " / " + dg_EntryRow);            
                handleEntryToRightOfData();
                System.out.println("83 seh, end to the right");
                System.out.println("84 seh, Entry Col/Row = " + dg_EntryCol + " / " + dg_EntryRow);
            }

            if (actRowPlus1 > tracker.getNCasesInStruct()) {
                System.out.println("88 seh, to the below!");
                System.out.println("89 seh, Entry Col/Row = " + dg_EntryCol + " / " + dg_EntryRow);
                handleEntryBelowData();
                System.out.println("91 seh, end to the below!");
                System.out.println("92 seh, Entry Col/Row = " + dg_EntryCol + " / " + dg_EntryRow);
            }
            System.out.println("94 seh, end to the right or below");
            System.out.println("95 seh, Entry Col/Row = " + dg_EntryCol + " / " + dg_EntryRow);
        }
        else {
            System.out.println("98 seh, The only thing left");
            System.out.println("99 seh, Entry Col/Row = " + dg_EntryCol + " / " + dg_EntryRow);
            if (theEntry.equals("")) { 
                //System.out.println("110 Setting entry to *");
                theEntry = "*"; 
            }
            dm.setDataElementInStruct(actualCol, actualRow, theEntry);
            dm.sendDataStructToGrid();
            tracker.set_CurrentDG(dg_EntryCol, dg_EntryRow + 1);
            System.out.println("107 seh, end The only thing left");
            System.out.println("108 seh, Entry Col/Row = " + dg_EntryCol + " / " + dg_EntryRow);
        }
        System.out.println("110 seh, end handleTableEntry (int entryColumn, int entryRow, String theStringEntered)");
        System.out.println("111 seh, Entry Col/Row/String = " + dg_EntryCol + " / " + dg_EntryRow + " / " + theStringEntered);
}   //  handleTableEntry
    
    private void handleEntryToRightAndDown() {
        System.out.println("115 seh, handleEntryToRightAndDown()");
        System.out.println("116 seh, Entry Col/Row = " + dg_EntryCol + " / " + dg_EntryRow);
        //  Do the right
        theDG = tracker.cpiDS_to_cpiDG(tracker.get_lrDS());
        nVarsToAdd = dg_EntryCol - theDG.getCol() + 1;
        nCasesToAdd = dm.getAllTheColumns().get(0).getColumnSize();
        for (int ithColToFill = theDG.getCol(); ithColToFill <= dg_EntryCol; ithColToFill++ ) {
            String varName = "Var #" + String.valueOf(ithColToFill + 1);
            dm.getAllTheColumns().add(new ColumnOfData(dm, nCasesToAdd, varName));
            for (int jthCase = 0; jthCase < nCasesToAdd; jthCase++) {
                dm.setDataElementInStruct(ithColToFill, jthCase, "*");
            }
        }   //  endFor

        // Do the down
        nVarsInStruct = actualCol + nVarsToAdd - 1;
        tracker.setNVarsInStruct(nVarsInStruct);
        theDG = tracker.cpiDS_to_cpiDG(tracker.get_lrDS());
        int nColsToFill = tracker.get_CurrentDS().getCol() + 1;
        nCasesToAdd = dg_EntryRow - theDG.getRow() + 1;          
        tracker.setNCasesInStruct(tracker.getNCasesInStruct() + nCasesToAdd);
        for (int ithColToFill = 0; ithColToFill < maxVarsInGrid; ithColToFill++ ) {
            if (ithColToFill < nVarsInStruct) {
                dm.getAllTheColumns().get(ithColToFill).addNCasesOfThese(nCasesToAdd, "*");
            }
            else {
                dm.getAllTheColumns().get(ithColToFill).addNCasesOfThese(nCasesToAdd, " ");
            }
            for (int ithCaseToAdd = 0; ithCaseToAdd < nCasesToAdd; ithCaseToAdd++) {
                int daRow = theDG.getRow() + ithCaseToAdd;
            }
        }   //  endFor

        dm.setDataElementInStruct(actualCol, actualRow, theEntry);
        dm.sendDataStructToGrid();
        tracker.set_CurrentDG(dg_EntryCol, dg_EntryRow + 1);   
        System.out.println("151 seh, end handleEntryToRightAndDown()");
        System.out.println("152 seh, Entry Col/Row = " + dg_EntryCol + " / " + dg_EntryRow);
    }
    
    // To do:  Fix for actual col
    private void handleEntryToRightOfData() {
        System.out.println("157 seh, handleEntryToRightOfData()");
        System.out.println("158 seh, Entry Col/Row = " + dg_EntryCol + " / " + dg_EntryRow);
        theDG = tracker.cpiDS_to_cpiDG(tracker.get_lrDS());
        nVarsToAdd = dg_EntryCol - theDG.getCol() + 1;
        nCasesToAdd = dm.getAllTheColumns().get(0).getColumnSize();
        for (int ithColToFill = theDG.getCol(); ithColToFill <= dg_EntryCol; ithColToFill++ ) {
            String varName = "Var #" + String.valueOf(ithColToFill + 1);
            dm.getAllTheColumns().add(new ColumnOfData(dm, nCasesToAdd, varName));
            for (int jthCase = 0; jthCase < nCasesToAdd; jthCase++) {
                dm.setDataElementInStruct(ithColToFill, jthCase, "*");
            }
        }   //  endFor
        tracker.cpi_lrDS.setCol(tracker.cpi_lrDS.getCol() + nVarsToAdd);
        dm.setDataElementInStruct(actualCol, actualRow, theEntry);
        dm.sendDataStructToGrid();
        tracker.set_CurrentDG(dg_EntryCol, dg_EntryRow + 1);
        System.out.println("173 seh, end handleEntryToRightOfData()");
        System.out.println("174 seh, Entry Col/Row = " + dg_EntryCol + " / " + dg_EntryRow);
    }
    
    private void handleEntryBelowData() {
        System.out.println("178 seh, handleEntryBelowData()");
        System.out.println("179 seh, Entry Col/Row = " + dg_EntryCol + " / " + dg_EntryRow);
        theDG = tracker.cpiDS_to_cpiDG(tracker.get_lrDS());
        int nColsToFill = tracker.get_CurrentDS().getCol() + 1;
        nCasesToAdd = dg_EntryRow - theDG.getRow() + 1;          
        tracker.setNCasesInStruct(tracker.getNCasesInStruct() + nCasesToAdd);
        for (int ithColToFill = 0; ithColToFill < maxVarsInGrid; ithColToFill++ ) {
            if (ithColToFill < nVarsInStruct) {
                dm.getAllTheColumns().get(ithColToFill).addNCasesOfThese(nCasesToAdd, "*");
            }
            else {
                dm.getAllTheColumns().get(ithColToFill).addNCasesOfThese(nCasesToAdd, " ");
            }
            for (int ithCaseToAdd = 0; ithCaseToAdd < nCasesToAdd; ithCaseToAdd++) {
                int daRow = theDG.getRow() + ithCaseToAdd;
            }
        }   //  endFor
        
        dm.setDataElementInStruct(actualCol, actualRow, theEntry);
        if (dg_EntryRow == (tracker.getMaxCasesInGrid() - 1)) {
            System.out.println("198 dg dg_EntryRow == (tracker.getMaxCasesInGrid() - 1)");
            System.out.println("199 seh, Entry Col/Row = " + dg_EntryCol + " / " + dg_EntryRow);
            tracker.setFirstCaseIdentifier(tracker.getFirstCaseIdentifier() + 1);
            CoordPairOfIntegers  tempCPI = tracker.get_CurrentDS();
            tracker.set_CurrentDS(tempCPI.getCol(), tempCPI.getRow() + 1);
            dm.sendDataStructToGrid();
            //handleEntryBelowData();
        } else {
            System.out.println("206 seh, At normal data entry");
            System.out.println("207 seh, Entry Col/Row = " + dg_EntryCol + " / " + dg_EntryRow);
            dm.sendDataStructToGrid();
            tracker.set_CurrentDG(dg_EntryCol, dg_EntryRow + 1); 
            System.out.println("210 seh, end At normal data entry");
            System.out.println("211 seh, Entry Col/Row = " + dg_EntryCol + " / " + dg_EntryRow);
        }
        System.out.println("213 seh, handleEntryBelowData()");
        System.out.println("214 seh, Entry Col/Row = " + dg_EntryCol + " / " + dg_EntryRow);
    }    
}
