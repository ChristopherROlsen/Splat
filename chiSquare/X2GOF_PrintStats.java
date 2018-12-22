/****************************************************************************
 *                      GOF_PrintStats                                      * 
 *                         06/02/18                                         *
 *                          00:00                                           *
 ***************************************************************************/

package chiSquare;

import genericClasses.ResizableTextPane;
import genericClasses.DragableAnchorPane;
import genericClasses.PrintTextReport_View;

public class X2GOF_PrintStats extends PrintTextReport_View {
    // POJOs
    int nCategories, df, maxSpaces, spacesNeeded, nCellsBelow5;                                                                                                                                                                                  
    int[] observedValues;
    
    double chiSquare, pValue, observedTotal, propTotal, expectedTotal, 
           standResidsTotal, contribTotal;    
    double[] expectedValues, chiSquareContribution, resids, standResids, 
             expectedProportions;
    
    String strGofVariable;
    String[] categoriesAsStrings;
    
    // My classes
    DragableAnchorPane dragableAnchorPane;
    MyX2StringUtilities myStringUtilities;
    ResizableTextPane rtp;
    X2GOF_Dashboard gofDashboard;
    X2GOF_Model x2GOF_Model;
 
   
    public X2GOF_PrintStats(X2GOF_Model x2GOF_Model, X2GOF_Dashboard gofDashboard, 
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        
        this.gofDashboard = gofDashboard;
        this.x2GOF_Model = x2GOF_Model;
        myStringUtilities = new MyX2StringUtilities();
        
        sourceString = new String();

        strGofVariable = x2GOF_Model.getGOFVariable();
        nCategories = x2GOF_Model.getNCategories();
        chiSquareContribution = new double[nCategories];
        expectedProportions = new double[nCategories];
        expectedValues = new double[nCategories];
        resids = new double[nCategories];
        standResids = new double[nCategories];        
        observedValues = new int[nCategories];
        
        observedValues = x2GOF_Model.getObservedCounts();
        expectedProportions = x2GOF_Model.getExpectedProportions();  
        chiSquareContribution = x2GOF_Model.getX2Contributions();         
        expectedValues = x2GOF_Model.getExpectedValues(); 
        standResids = x2GOF_Model.getStandResids();
        
        nCellsBelow5 = x2GOF_Model.getNCellsBelow5();
        observedTotal = x2GOF_Model.getObservedTotal();
        propTotal = x2GOF_Model.getPropTotal();
        expectedTotal = x2GOF_Model.getExpectedTotal();
        contribTotal = x2GOF_Model.getContribTotal();
        pValue = x2GOF_Model.getPValue();
        
        categoriesAsStrings = new String[nCategories]; 
        categoriesAsStrings = x2GOF_Model.getCategoriesAsStrings();
        chiSquare = x2GOF_Model.getX2();

        df = x2GOF_Model.getDF();
        strTitleText = "Elementary chi square statistics";
    }   //  End constructor


    public void constructPrintLines() { 
        String tempString;
        addNBlankLines(2); 
        tempString = "                                Variable of interest: " + strGofVariable;
        stringsToPrint.add(tempString);        
        addNBlankLines(2);
        tempString = "                  Observed     Hypothesized     Expected      Contribution";
        stringsToPrint.add(tempString);    
        addNBlankLines(1);
        tempString = "Category            count       proportion       count       to Chi Square";
        stringsToPrint.add(tempString);     
        addNBlankLines(2);     
        
        System.out.println("156 GOF_Print, nCategories = " + nCategories);
        
        for (int printRow = 0; printRow < nCategories; printRow++) {
            sourceString = myStringUtilities.leftMostChars(categoriesAsStrings[printRow], 12);
            tempString = "";
            tempString += String.format("%12s", sourceString);
            tempString += String.format("   %8d", observedValues[printRow]);
            tempString += String.format("         %8.3f", expectedProportions[printRow]);
            tempString += String.format("       %8.2f", expectedValues[printRow]);
            tempString += String.format("      %8.3f", chiSquareContribution[printRow]);                               
            stringsToPrint.add(tempString);
            addNBlankLines(1);
        }
        
        addNBlankLines(2);
 
        tempString = String.format("                 Chi Square = %7.3f", chiSquare);
        stringsToPrint.add(tempString);
        addNBlankLines(1); 
        tempString = String.format("                         df = %3d", df);
        stringsToPrint.add(tempString);
        addNBlankLines(1);
        tempString = String.format("                    p-Value = %7.3f", pValue);
        stringsToPrint.add(tempString);
        addNBlankLines(2);
        nCellsBelow5 = x2GOF_Model.getNCellsBelow5();
        
        if (nCellsBelow5 > 0) {
            addNBlankLines(2);
            tempString = String.format("               ********************       Warning!    ********************");
            stringsToPrint.add(tempString); 
            addNBlankLines(2);
            tempString = String.format("               *** There are %d cells with expected values less than 5. ***\n\n", nCellsBelow5);
            stringsToPrint.add(tempString);
        }                
        addNBlankLines(2);  

    }

}

