/****************************************************************************
 *                    GOF_PrintAdvStats                                     * 
 *                        06/02/18                                          *
 *                          09:00                                           *
 ***************************************************************************/

package chiSquare;

import genericClasses.PrintTextReport_View;
import java.util.ArrayList;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.layout.AnchorPane;

public class X2GOF_PrintAdvStats extends PrintTextReport_View {
    // POJOs
    int nCategories, df, maxSpaces, spacesNeeded, nCellsBelow5;
    int[] observedValues;
    
    double chiSquare, pValue, observedTotal, propTotal, expectedTotal, 
           standResidsTotal, contribTotal, cohens_W;    
    double[] expectedValues, chiSquareContribution, resids, standResids, 
             expectedProportions;
    
    String strGofVariable;
    String[] categoriesAsStrings;
    
    // My classes
    MyX2StringUtilities myStringUtilities; 
    X2GOF_Dashboard gofDashboard;
    X2GOF_Model x2GOF_Model;

    // FX Objects
    AnchorPane anchorPane;
    static ScrollPane scrollPane;
    Text txtTitle1, txtTitle2;


    public X2GOF_PrintAdvStats(X2GOF_Model x2GOF_Model, X2GOF_Dashboard gofDashboard, 
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {

        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        this.gofDashboard = gofDashboard;
        this.x2GOF_Model = x2GOF_Model;        
        myStringUtilities = new MyX2StringUtilities();
        
        sourceString = new String();
        stringsToPrint = new ArrayList<>();

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
        cohens_W = x2GOF_Model.getCohensW();
        
        categoriesAsStrings = new String[nCategories]; 
        categoriesAsStrings = x2GOF_Model.getCategoriesAsStrings();
        chiSquare = x2GOF_Model.getX2();

        df = x2GOF_Model.getDF();
        strTitleText = "Advanced chi square statistics";
    }
    
    public void constructPrintLines() {  
        String tempString;
        addNBlankLines(2);
        tempString = "                                Variable of interest: " + strGofVariable;
        stringsToPrint.add(tempString); 
        addNBlankLines(2);
        tempString = "                  Observed     Hypothesized     Expected      Contribution       Standardized   ";
        stringsToPrint.add(tempString); 
        addNBlankLines(1);
        tempString = "   Category         count       proportion       count       to Chi Square     Contribution (z)   ";
        stringsToPrint.add(tempString);     
        
        for (int printRow = 0; printRow < nCategories; printRow++) {
            sourceString = myStringUtilities.leftMostChars(categoriesAsStrings[printRow], 12);
            addNBlankLines(1);
            tempString = "";
            tempString += String.format("%12s", sourceString);
            tempString += String.format("    %8d", observedValues[printRow]);
            tempString += String.format("        %8.3f", expectedProportions[printRow]);
            tempString += String.format("       %8.2f", expectedValues[printRow]);
            tempString += String.format("       %8.3f", chiSquareContribution[printRow]);
            tempString += String.format("            %8.3f", standResids[printRow]);                           
            stringsToPrint.add(tempString);
        }

        addNBlankLines(3);
        tempString = String.format("                 Chi Square = %7.3f", chiSquare);
        stringsToPrint.add(tempString);
        addNBlankLines(1);
        tempString = String.format("                         df = %3d", df);
        stringsToPrint.add(tempString);
        addNBlankLines(1);
        tempString = String.format("                    p-Value = %7.3f", pValue);
        stringsToPrint.add(tempString);
        addNBlankLines(1);
        stringsToPrint.add(String.format("                  Cohen's W = %7.3f", cohens_W));  
        addNBlankLines(2);
        nCellsBelow5 = x2GOF_Model.getNCellsBelow5();
        if (nCellsBelow5 > 0) {
            addNBlankLines(2);
            tempString = String.format("               ********************      Warning!    ********************   ");
            stringsToPrint.add(tempString);  
            addNBlankLines(2);
            tempString = String.format("               *** There are %d cells with expected values less than 5. ***", nCellsBelow5);
            stringsToPrint.add(tempString);
            addNBlankLines(2);
        }                 
    }
}

