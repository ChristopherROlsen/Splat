/************************************************************
 *                       Splat_EditOps                      *
 *                          12/23/18                        *
 *                            18:00                         *
 ***********************************************************/
package splat;

import dataObjects.ColumnOfData;
import utilityClasses.DataCleaner;

public class Edit_Ops {
    
    int nVars, nCases;
    
    PositionTracker tracker;
    Data_Manager dm;

    public Edit_Ops(Data_Manager dm) {
        this.dm = dm;
        tracker = dm.getPositionTracker();
    }
    
    public void cleanDataInColumn() {
        int col = tracker.get_CurrentDG().getCol();  
        System.out.println("17 edOps, col = " + col);
        // true = data check originates in the data grid
        ColumnOfData tempCol = dm.getAllTheColumns().get(col);
        tempCol.assignDataType();
        DataCleaner dc = new DataCleaner(tempCol);
        dc.cleanAway();
    }

    public void deleteObservation() {  //  *******   Then what?????

        //int row = waldo.getCurrentRow();
        //int col = waldo.getCurrentCol();
        //dm.getAllTheColumns().get(col).deleteDataInThisRow(row);  
    } // deleteObservation

    public void insertObservation() { //  *******   Then what?????
        // int row = waldo.getCurrentRow();
        // int col = waldo.getCurrentCol();
        // String data = dm.getDataElementFromStruct(col, row - 1);
        //  dm.getAllTheColumns().get(col).insertThisDataInThisRow(data, row);       
    } // insertObservation

    public void insertRow() { //  *******   Then what?????
        // int row = waldo.getCurrentRow();
        // int nCols = waldo.getNVarsInStruct();
        // for (int dataCol = 0; dataCol < nCols; dataCol++) {
        //     dm.getAllTheColumns().get(dataCol).insertThisDataInThisRow("*", row);
        // }
        // dm.sendDataStructToGrid();
    } // insertRow

    public void deleteRow() {
        int row = tracker.get_CurrentDG().getRow();
        int nCols = tracker.getNVarsInStruct();
        System.out.println("59 EditOps, nCols = " + nCols);
        for (int dataCol = 0; dataCol < nCols; dataCol++) {
            System.out.println("eo 61, dataCol = " + dataCol);
            dm.getAllTheColumns().get(dataCol).deleteDataInThisRow(row);
        }
        System.out.println("63 EditOps, nCases = " + dm.getNCasesInStruct());

        dm.setNCasesInStruct(dm.getNCasesInStruct() - 1);
        tracker.setNCasesInStruct(tracker.getNCasesInStruct() - 1);
        System.out.println("65 EditOps, nCases = " + dm.getNCasesInStruct());
        dm.sendDataStructToGrid();
    } // DelRow
    
    public void addColumn() { dm.addNVariables(1); }

    //  Seems to work if nVars > maxVarsInGrid, but not if not
    public void deleteColumn() {
        int col = tracker.get_CurrentDG().getCol(); 
        int tempNCases = dm.getAllTheColumns().get(0).getColumnSize();
        int tempNVars = dm.getAllTheColumns().size();
        dm.getAllTheColumns().remove(col);
        tracker.setNVarsInStruct(tracker.getNVarsInStruct() - 1);
        if (tracker.getNVarsInStruct() < tracker.getMaxVarsInGrid()) {
            dm.addNVariables(1);
            for (int ithCase = 0; ithCase < tempNCases; ithCase++) {
                dm.getAllTheColumns().get(tempNVars - 1).getTheCases().add("");
            }
        }
        dm.sendDataStructToGrid();
        
    } // deleteColumn
} // EditOps

