/**************************************************
 *              CoordPairOfIntegers               *
 *                    07/27/18                    *
 *                     21:00                      *
 *************************************************/
package genericClasses;

public class CoordPairOfIntegers {
    
    
    int row, col;
    
    public CoordPairOfIntegers() { row = 0; col = 0; }
    
    // The -1's are so that a col or row can be individually (re)set
    public void setColAndRow(int toThisCol, int toThisRow) {
            col = toThisCol;
            row = toThisRow;
    }
    
    public CoordPairOfIntegers getRowAndCol() {return this; }
    
    public int getRow() { return this.row; }
    public void setRow(int toThisRow) { 
        row = toThisRow; }
    
    public int getCol() { return this.col; }
    public void setCol(int toThisCol) { 
        col = toThisCol; }
    
    public String toString() {
        
        String cpiString = "col = " + String.valueOf(col) 
                          + "; row = " + String.valueOf(row);
        return cpiString;
    }
    
}
