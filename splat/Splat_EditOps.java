/************************************************************
 *                       Splat_EditOps                      *
 *                          10/21/18                        *
 *                            18:00                         *
 ***********************************************************/
package splat;

import genericClasses.ColumnOfData;
import genericClasses.DataCleaner;

public class Splat_EditOps {
    
    int nVars, nCases;
    
    PositionTracker tracker;
    Splat_DataManager dm;

    public Splat_EditOps(Splat_DataManager dm) {
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

    public void deleteRow() {  //  *******   Then what?????
        // int row = waldo.getCurrentRow();
        // int col = waldo.getCurrentCol();
        // int nCols = waldo.getNVarsInStruct();
        // for (int dataCol = 0; dataCol < nCols; dataCol++) {
            // System.out.println("eo 50, dataCol = " + dataCol);
        //     dm.getAllTheColumns().get(dataCol).deleteDataInThisRow(row - 1);
        // }
        // dm.sendDataStructToGrid();
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

