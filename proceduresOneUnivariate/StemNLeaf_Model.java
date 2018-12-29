/************************************************************
 *                     StemAndLeaf_Model                    *
 *                          12/25/18                        *
 *                            18:00                         *
 ***********************************************************/
package proceduresOneUnivariate;

import utilityClasses.StringUtilities;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import splat.Data_Manager;

public class StemNLeaf_Model
{
    // POJOs
    private boolean posNumbersExist, negNumbersExist, ordMagIsPreSet, leEqualsHe;
    
    private int nVarsChosen, orderOfMagnitude, nDataPoints, firstNonZeroColumn, 
        firstNonConstantColumn, firstNonZeroDigitColInString, 
        firstDiffDigitColInString, lengthOfStems, nStems, le_AsInteger, 
        he_AsInteger, bbslFirstNonZeroColumn, bbslFirstNonConstantColumn;
    
    private double[] data_Sorted, data_ReverseSorted;
    
    private String highestStem, lowestStem, descriptionOfVariable;
    private String strDaStrippedNumber[];
    private ArrayList<String> data_AsStrings, theStems_With_Vert, theStems_WO_Vert, 
                      oneLineStems, twoLineStems, fiveLineStems, allTheLabels,
                      oneLineStemPlot, twoLineStemPlot, fiveLineStemPlot;

    // My classes
    private Exploration_Dashboard explore_Dashboard;
    private Data_Manager dataManager; 
    private StemNLeaf_View sandL_View;    
    private QuantitativeDataVariable theQDV;
    
    // POJOs / FX
    private TextArea txtArea1, txtArea2, txtArea5;
    
    public StemNLeaf_Model() { }
    
    public StemNLeaf_Model(String descriptionOfVariable,
                           QuantitativeDataVariable theQDV, 
                           boolean presetOrdMag, 
                           int ordMag,
                           int presetFirstNonZero,
                           int presetFirstNonConstant) 
    {
        ordMagIsPreSet = presetOrdMag;
        if (ordMagIsPreSet) {
            orderOfMagnitude = ordMag;
            bbslFirstNonZeroColumn = presetFirstNonZero;
            bbslFirstNonConstantColumn = presetFirstNonConstant;
        }

        this.theQDV = new QuantitativeDataVariable();
        this.theQDV = theQDV;
        this.descriptionOfVariable = descriptionOfVariable;
        doAllThatSLStuff();
    }
    
    private void doAllThatSLStuff() {
        
        String dataLabel = theQDV.getTheDataLabel();

        txtArea1 = new TextArea();
        txtArea1.setFont(Font.font("Courier New"));
    
        txtArea2 = new TextArea();
        txtArea2.setFont(Font.font("Courier New"));
        
        txtArea5 = new TextArea();
        txtArea5.setFont(Font.font("Courier New"));
        
        nDataPoints = theQDV.get_nDataPointsLegal();
        
        // Data is reverse sorted for the stem and leaf plot.  
        data_ReverseSorted = new double[nDataPoints];
        
        // Sort here is lo to hi
        data_Sorted = theQDV.getTheDataSorted();

        initialize();
        sortDataReversedToStringArray();
        constructTheStems();
        addTheVerticalLine();
        boolean slSuccess = sortLeavesWithinStems();
        
        if (slSuccess == true) {
            construct_2LinesPerStem();
            construct_5LinesPerStem();
        }
        else {
            System.out.println("89 SL_Model, CANNOT DO");
        }
    }
    
    private void initialize(){
        data_AsStrings = new ArrayList<>();
        theStems_With_Vert = new ArrayList<>();
        oneLineStems = new ArrayList<>();
        theStems_WO_Vert = new ArrayList<>();
        twoLineStems = new ArrayList<>();
        fiveLineStems = new ArrayList<>();
        
        posNumbersExist = false; negNumbersExist = false; leEqualsHe = false;
        
        if (data_Sorted[0] < 0.)
            negNumbersExist = true;
        if (data_Sorted[nDataPoints -1] > 0.)
            posNumbersExist = true; 
    }
    
    // Data as doubles is sorted before S&L is constructed
    private void sortDataReversedToStringArray(){
   
        int iData;
        nDataPoints = data_Sorted.length;
        for(iData=0; iData < nDataPoints;iData++) {
            data_ReverseSorted[iData] = data_Sorted[(nDataPoints - 1) - iData];
        }

        for (iData = 0; iData < nDataPoints; iData++) {
            String tempString = String.format("%+012.5f ", data_ReverseSorted[iData]);
            data_AsStrings.add(tempString);
        }               
    }
    
    private void constructTheStems(){
        int iStem;
        strDaStrippedNumber = new String[nDataPoints];

        for (int daNumber = 0; daNumber < nDataPoints; daNumber++) {
            strDaStrippedNumber[daNumber] = new String();
            String strDaNumber = data_AsStrings.get(daNumber);
            strDaStrippedNumber[daNumber] = strDaNumber.substring(0, 6)
                                          + strDaNumber.substring(7, 12);
        }
        
        int strippedColLength = strDaStrippedNumber[0].length();
        
        // Find first nonZero digit column -- iCol = 0 is sign
        for (int iCol = 1; iCol < strippedColLength; iCol++) {
            boolean nonZerosFound = false;
            for (int iDat = 0; iDat < nDataPoints; iDat++) {
                char tempChar = strDaStrippedNumber[iDat].charAt(iCol);
                if (tempChar != '0') {
                    nonZerosFound = true;
                    firstNonZeroColumn = iCol;
                    break;
                }
            }
            if (nonZerosFound == true) { break; }
        }
        
        if (!ordMagIsPreSet) {
            orderOfMagnitude = 5 - firstNonZeroColumn;
        }    
        
        // Now find the first non-constant (& nonZero) column
        for (int iCol = firstNonZeroColumn; iCol < strippedColLength; iCol++) {
            boolean nonConstantColumnFound = false;
            for (int iDat = 1; iDat < nDataPoints; iDat++) {
                char prevTempChar = strDaStrippedNumber[iDat - 1].charAt(iCol);
                char thisTempChar = strDaStrippedNumber[iDat].charAt(iCol);
                if (prevTempChar != thisTempChar) {
                    nonConstantColumnFound = true;
                    firstNonConstantColumn = iCol;
                    break;
                }
            }
            if (nonConstantColumnFound == true) { break; }
        } 
        
        // If doing a back-to-back, with stems of different ordMags, these
        // variables are preset and found by appealing to the BBSL
        if (ordMagIsPreSet) {
            firstNonZeroColumn = bbslFirstNonZeroColumn;
            firstNonConstantColumn = bbslFirstNonConstantColumn;
        }

        if ((!ordMagIsPreSet) && (firstNonZeroColumn != firstNonConstantColumn)) {
            orderOfMagnitude = 5 - firstNonConstantColumn;
        }  
        
        highestStem = new String();
        highestStem = strDaStrippedNumber[0].substring(0,1) 
                      + strDaStrippedNumber[0].substring(firstNonZeroColumn, firstNonConstantColumn + 1);

        lowestStem = new String();
        lowestStem = strDaStrippedNumber[nDataPoints - 1].substring(0,1) 
                      + strDaStrippedNumber[nDataPoints - 1].substring(firstNonZeroColumn, firstNonConstantColumn + 1);        

        String tempString = "";
        he_AsInteger = Integer.parseInt(highestStem);
        le_AsInteger = Integer.parseInt(lowestStem);
        
        for (iStem = he_AsInteger; iStem >= le_AsInteger; iStem--){
            tempString = String.format("%+d", iStem);
            theStems_With_Vert.add(tempString);
            if ((iStem == 0) && posNumbersExist && negNumbersExist) {
                theStems_With_Vert.add("-0");
            }
        }
        
        for (iStem = 0; iStem < theStems_With_Vert.size(); iStem++){
            tempString = theStems_With_Vert.get(iStem);
            theStems_WO_Vert.add(tempString);   //  Needed for 2/5 lines/stem
            tempString += "|";
            theStems_With_Vert.set(iStem, tempString);
        }     
        
        lengthOfStems = tempString.length();
    }
    
    private void addTheVerticalLine() {
        int iStem, iData;
        // ****************** Construct the stem&leaf initial strings
        for (iStem = 0; iStem < theStems_With_Vert.size(); iStem++) {
            oneLineStems.add(theStems_With_Vert.get(iStem));
        }
        
        // **************  Loop through the strings and construct the plot
        //  for dataString highest through dataString lowest
        for (iData = 0; iData < nDataPoints; iData++) {
            String tempInString = strDaStrippedNumber[iData];
            String tempOutString = constructIndividualStem(tempInString);
            
            for (iStem = 0; iStem < theStems_With_Vert.size(); iStem++) {
                if (theStems_With_Vert.get(iStem).equals(tempOutString + "|")) {
                    StringBuilder tempString = new StringBuilder(oneLineStems.get(iStem));
                    tempString.append(getTheLeafAsAString(tempInString));
                    oneLineStems.set(iStem, tempString.toString());
                }
            }  
        }    
    }
    
    private boolean sortLeavesWithinStems() {
        boolean canDoSL = true;
        int startPosition = lengthOfStems;

        int nStemsNeeded = theStems_With_Vert.size();
        
        // Bail outta here!
        if ( nStemsNeeded > 75) {
            canDoSL = false;
            // System.out.println("249 SL_Model, canDoSL = " + canDoSL);
            return canDoSL;
        }
        
        for (int iStem = 0; iStem < nStemsNeeded; iStem++) {
            String tempStem = oneLineStems.get(iStem);
            int lengthOfStemNLeaves = tempStem.length();
            String stemPart = tempStem.substring(0, lengthOfStems);
            // System.out.println("259 SL_M, stemPart = " + stemPart);
            if (lengthOfStemNLeaves - lengthOfStems > 1) {
                //  More than one leaf -- sort.
                int endPosition = lengthOfStemNLeaves;
                String leafPart = tempStem.substring(lengthOfStems, lengthOfStemNLeaves);
                char[] leefs = leafPart.toCharArray();
                Arrays.sort(leefs);
                leafPart = String.valueOf(leefs);
                
                if (stemPart.substring(0,1).equals("-")) {
                    leafPart = StringUtilities.reverseStringCharacters(leafPart);
                }
                          
                String tempString = stemPart + leafPart;
                oneLineStems.set(iStem, tempString);
            }
        }
        
        return canDoSL;
    }
    
    private void construct_2LinesPerStem() {
        String hiSL, loSL;
        String[] twoline_leafOptions = {"01234", "56789"};
        nStems = theStems_With_Vert.size();
        for (int iStem = 0; iStem < nStems; iStem++) {
            String oneLineStem = oneLineStems.get(iStem);
            String tempString = theStems_WO_Vert.get(iStem);
            String hiSB = tempString + "H|";
            String loSB = tempString + "L|";
            
            if (tempString.charAt(0) == '+') {
                hiSL = hiSB + constructLeaves(twoline_leafOptions[1], oneLineStem);
                loSL = loSB + constructLeaves(twoline_leafOptions[0], oneLineStem);

                twoLineStems.add(hiSL);
                twoLineStems.add(loSL);
            }
            else {    // char is '-''
                String preReversedLoSL = constructLeaves(twoline_leafOptions[0], oneLineStem);
                String reversedLoSL = StringUtilities.reverseStringCharacters(preReversedLoSL);
                loSL = loSB + reversedLoSL;                
                
                String preReversedHiSL = constructLeaves(twoline_leafOptions[1], oneLineStem);               
                String reversedHiSL = StringUtilities.reverseStringCharacters(preReversedHiSL);
                hiSL = hiSB + reversedHiSL;

                twoLineStems.add(hiSL);
                twoLineStems.add(loSL);
            }
        }   //  next iStem
    }
    
    private void construct_5LinesPerStem() {
        String[] fivePerSL = new String[5];
        StringBuilder[] fivePerSB = new StringBuilder[5];
        
        String[] fiveline_leafOptions = {"01", "23", "45", "67", "89"};
        String[] fiveline_stemOptions = {".|", "t|", "f|", "s|", "*|"};
        
        nStems = theStems_With_Vert.size();
        for (int iStem = 0; iStem < nStems; iStem++)
        {
            String oneLineStem = oneLineStems.get(iStem);
            String tempString = theStems_WO_Vert.get(iStem);

            if (tempString.charAt(0) == '+') {
                for (int leafOptions = 0; leafOptions < 5; leafOptions++) {
                    fivePerSB[leafOptions] = new StringBuilder(tempString + fiveline_stemOptions[4 - leafOptions]);
                    fivePerSL[leafOptions] = fivePerSB[leafOptions].toString() + constructLeaves(fiveline_leafOptions[4 - leafOptions], oneLineStem);
                    fiveLineStems.add(fivePerSL[leafOptions]);
                }
            } else {    // char is '-''
                for (int leafOptions = 0; leafOptions < 5; leafOptions++) {
                    fivePerSB[leafOptions] = new StringBuilder(tempString + fiveline_stemOptions[leafOptions]);
                    
                    String preReversedSL = constructLeaves(fiveline_leafOptions[leafOptions], oneLineStem);
                    String reversedSL = StringUtilities.reverseStringCharacters(preReversedSL);

                    fivePerSL[leafOptions] = fivePerSB[leafOptions].toString() + reversedSL;                    
                }   
                
                for (int leafOptions = 0; leafOptions < 5; leafOptions++) {
                   fiveLineStems.add(fivePerSL[4 - leafOptions]); 
                }
            }
        }   //  next iStem  
    }
    
    static String constructLeaves(String charsToChooseFrom, String oneLiner) {
        StringBuilder wholeSL = new StringBuilder();
        
        int firstOccurence = oneLiner.indexOf('|');
        for (int iChar = 0; iChar < charsToChooseFrom.length(); iChar++) {
            for (int iLeaf = firstOccurence; iLeaf < oneLiner.length(); iLeaf++) {
                if (charsToChooseFrom.charAt(iChar) == oneLiner.charAt(iLeaf)) {
                    wholeSL.append(charsToChooseFrom.charAt(iChar));
                }
            }
        }
        
        return wholeSL.toString();
    }
       
    private String constructIndividualStem(String strippedNumber) {
        String tempString = strippedNumber;
        StringBuilder stem = new StringBuilder();
        stem.append(strippedNumber.substring(0,1));
        stem.append(strippedNumber.substring(firstNonZeroColumn, firstNonConstantColumn + 1));       
        return stem.toString(); 
    }
    
    private String getTheLeafAsAString(String stringyWingy) {
        // Leaf digit is one past the last stem digit
        int leafDigit = firstNonConstantColumn + 1;
        String theLeaf = stringyWingy.substring(leafDigit, leafDigit + 1);
        return theLeaf;
    } 
    
    public String getDescriptionOfVariable() { return descriptionOfVariable; }
    
    public StemNLeaf_View getStemNLeaf_View() { return sandL_View; }
   
    // This is called by BBSL 
    public int getOrderOfMagnitude()  { return orderOfMagnitude; }
    
    public int getFirstNonZeroColumn() { return firstNonZeroColumn; }
    public int getFirstNonConstantColumn() { return firstNonConstantColumn; }
    
    public ArrayList<String> get_1_LineSL() { return oneLineStems; }
    public ArrayList<String> get_2_LineSL() { return twoLineStems; }
    public ArrayList<String> get_5_LineSL() { return fiveLineStems; }
    
    public QuantitativeDataVariable getTheQDV() {return theQDV; }
}
