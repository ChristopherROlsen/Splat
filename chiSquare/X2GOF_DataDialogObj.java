/****************************************************************************
 *                      GOF_DataDialogObj                                   * 
 *                          05/15/18                                        *
 *                            15:00                                         *
 ***************************************************************************/

/****************************************************************************
 *  This object is passed back to the X2GOF_DataDialog                      * 
 ***************************************************************************/

package chiSquare;

public class X2GOF_DataDialogObj {
    // POJOs
    int nCategories;
    int[] gofObs;
    
    double[] gofExpectedProps;

    String strGofVariable;
    String[] strGofCategories;
    
    public X2GOF_DataDialogObj() { nCategories = 0;}
    
    public X2GOF_DataDialogObj(int nCategories) { 
        this.nCategories = nCategories; 
        gofExpectedProps = new double[nCategories];
        gofObs = new int[nCategories];
        strGofCategories = new String[nCategories];    
    }
    
    public int getNCategories() { 
        return nCategories; 
    }
    public void setNCategories(int toThis) {nCategories = toThis; }
    
    public String[] getTheGOFCategories () {return strGofCategories; }
    
    public void setTheGOFCategories( String[] theGOFCats) {
        System.arraycopy(theGOFCats, 0, strGofCategories, 0, nCategories);   
    }
    
    public String getGOFVariable() {
        return strGofVariable; 
    }
    public void setGOFVariable( String theVar) {
        strGofVariable = theVar; } 
    
    public double[] getExpectedProps() {return gofExpectedProps; }
    
    public void setExpectedProps (double[] expProps) {
        System.arraycopy(expProps, 0, gofExpectedProps, 0, nCategories);
    }

    public int[] getObservedValues() {return gofObs; }
    
    public void setObservedValues (int[] obsVals) {
        System.arraycopy(obsVals, 0, gofObs, 0, nCategories);
    }  
}

