/**************************************************
 *                   BBSL_Model                   *
 *                    12/25/18                    *
 *                      18:00                     *
 *************************************************/

package proceduresTwoUnivariate;

import proceduresOneUnivariate.StemNLeaf_Model;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import t_Procedures.Indep_t_PrepStructs;

public final class BBSL_Model {
    // POJOs
    boolean posNumbersExist, negNumbersExist;

    int maxLeftSize, maxRightSize, maxBbslLineSize,
        bbslFirstNonZeroColumn, bbslFirstNonConstantColumn;
    
    double maxValue;

    String blanx, descriptionOfVariable,
           leftDataLabel, rightDataLabel,
           leftLeafString, rightLeafString, 
           tempLeftString, tempRightString;
    
    ArrayList<String> oneLineStemPlotLeft, twoLineStemPlotLeft, fiveLineStemPlotLeft,
                      oneLineStemPlotRight, twoLineStemPlotRight, fiveLineStemPlotRight,
                      oneLineStemPlotAll, twoLineStemPlotAll, fiveLineStemPlotAll,
                      oneLineBBSL, twoLineBBSL, fiveLineBBSL;
    
    // POJOs / FX
    Scene sandlScene1, sandlScene2, sandlScene5;
    Stage sandlStage1, sandlStage2, sandlStage5;

    ArrayList<QuantitativeDataVariable> bbslAllTheQDVs;  
    ArrayList<Text> textLines;

    TextArea textArea1, textArea2, textArea5;
    
    BBSL_Model() {}

    public BBSL_Model(Explore_2Ind_PrepareStructs exp_2Ind_Structs, String descriptionOfVariable, ArrayList<QuantitativeDataVariable> allTheQDVs) {
        this.descriptionOfVariable = descriptionOfVariable;
        bbslAllTheQDVs = new ArrayList<>();
        bbslAllTheQDVs.add(allTheQDVs.get(0));  //  All
        bbslAllTheQDVs.add(allTheQDVs.get(1));  // Left
        bbslAllTheQDVs.add(allTheQDVs.get(2));  // Right
        makeTheBBSL();
    }
    
    public BBSL_Model(Indep_t_PrepStructs exp_2Ind_Structs, String descriptionOfVariable, ArrayList<QuantitativeDataVariable> allTheQDVs) {
        this.descriptionOfVariable = descriptionOfVariable;
        bbslAllTheQDVs = new ArrayList<>();
        bbslAllTheQDVs.add(allTheQDVs.get(0));  //  All
        bbslAllTheQDVs.add(allTheQDVs.get(1));  // Left
        bbslAllTheQDVs.add(allTheQDVs.get(2));  // Right
        makeTheBBSL();
    }
    
    private void makeTheBBSL() {
        textArea1 = new TextArea();
        textArea1.setFont(Font.font("Courier New"));    
        textArea2 = new TextArea();
        textArea2.setFont(Font.font("Courier New"));        
        textArea5 = new TextArea();
        textArea5.setFont(Font.font("Courier New"));
                        
        oneLineStemPlotLeft = new ArrayList<>();
        twoLineStemPlotLeft = new ArrayList<>();
        fiveLineStemPlotLeft = new ArrayList<>();

        oneLineStemPlotRight = new ArrayList<>();
        twoLineStemPlotRight = new ArrayList<>();
        fiveLineStemPlotRight = new ArrayList<>();
        
        oneLineBBSL = new ArrayList<>();
        twoLineBBSL = new ArrayList<>();
        fiveLineBBSL = new ArrayList<>();
        
        maxValue = (int)bbslAllTheQDVs.get(0).getMaxValue();
        leftDataLabel = bbslAllTheQDVs.get(1).getTheDataLabel();
        rightDataLabel = bbslAllTheQDVs.get(2).getTheDataLabel();

        StemNLeaf_Model sandLAll = new StemNLeaf_Model("Null", bbslAllTheQDVs.get(0), false, 0, 0, 0);
        int orderOfMagnitude = sandLAll.getOrderOfMagnitude();  // for 1|0 in SL
        
        // Needed by left and right to get proper columns;
        bbslFirstNonZeroColumn = sandLAll.getFirstNonZeroColumn();
        bbslFirstNonConstantColumn = sandLAll.getFirstNonConstantColumn();
        
        StemNLeaf_Model sandLLeft = new StemNLeaf_Model("Null", bbslAllTheQDVs.get(1), true, orderOfMagnitude,
                                                        bbslFirstNonZeroColumn, bbslFirstNonConstantColumn);
        StemNLeaf_Model sandLRight = new StemNLeaf_Model("Null", bbslAllTheQDVs.get(2), true, orderOfMagnitude,
                                                         bbslFirstNonZeroColumn, bbslFirstNonConstantColumn);
      
        ArrayList<String> theAllOneLiners = new ArrayList();
        ArrayList<String> theAllTwoLiners = new ArrayList();
        ArrayList<String> theAllFiveLiners = new ArrayList();
        ArrayList<String> theLeftOneLiners = new ArrayList();
        ArrayList<String> theRightOneLiners = new ArrayList();
        ArrayList<String> theLeftTwoLiners = new ArrayList();
        ArrayList<String> theRightTwoLiners = new ArrayList();
        ArrayList<String> theLeftFiveLiners = new ArrayList();
        ArrayList<String> theRightFiveLiners = new ArrayList();
       
        theAllOneLiners = sandLAll.get_1_LineSL(); 
        theAllTwoLiners = sandLAll.get_2_LineSL(); 
        theAllFiveLiners = sandLAll.get_5_LineSL(); 

        
        theLeftOneLiners = sandLLeft.get_1_LineSL();
        theRightOneLiners = sandLRight.get_1_LineSL();
        theLeftTwoLiners = sandLLeft.get_2_LineSL();
        theRightTwoLiners = sandLRight.get_2_LineSL();
        theLeftFiveLiners = sandLLeft.get_5_LineSL();
        theRightFiveLiners = sandLRight.get_5_LineSL();
        
        int nAllOneLiners = theAllOneLiners.size();
        int nAllTwoLiners = theAllTwoLiners.size();
        int nAllFiveLiners = theAllFiveLiners.size();
        
        int nLeftOneLiners = theLeftOneLiners.size();
        int nRightOneLiners = theRightOneLiners.size();
        int nLeftTwoLiners = theLeftTwoLiners.size();
        int nRightTwoLiners = theRightTwoLiners.size();
        int nLeftFiveLiners = theLeftFiveLiners.size();
        int nRightFiveLiners = theRightFiveLiners.size();
        
        int vertBarPosOne = theAllOneLiners.get(0).indexOf("|");
        int vertBarPosTwo = theAllTwoLiners.get(0).indexOf("|");
        int vertBarPosFive = theAllFiveLiners.get(0).indexOf("|");
        
/****************************************************************************
 *                        Start Code for one liners                         *
 ***************************************************************************/        

/****************************************************************************
 *  Need to know stem lengths to (a) position the bbsl, and (b) set up the  * 
 *  size of the text boxes                                                  *
 ***************************************************************************/

        maxLeftSize = 0; maxRightSize = 0; maxBbslLineSize = 0;
        for (int jthLeftLiner = 0; jthLeftLiner < nLeftOneLiners; jthLeftLiner++) {
            maxLeftSize = Math.max(maxLeftSize, theLeftOneLiners.get(jthLeftLiner).length());
        }

        for (int jthRightLiner = 0; jthRightLiner < nRightOneLiners; jthRightLiner++) {
            maxRightSize = Math.max(maxRightSize, theRightOneLiners.get(jthRightLiner).length());
        }  

        maxBbslLineSize = maxLeftSize + maxRightSize;
     
        for (int ithOneLiner = 0; ithOneLiner < nAllOneLiners; ithOneLiner++) {
            
            initStrings();
            String tempAllString = theAllOneLiners.get(ithOneLiner);
            String daAllStem = tempAllString.substring(0, vertBarPosOne + 1);
            for (int jthLeftLiner = 0; jthLeftLiner < nLeftOneLiners; jthLeftLiner++) {
                tempLeftString = theLeftOneLiners.get(jthLeftLiner);
                String daLeftStem = tempLeftString.substring(0, vertBarPosOne + 1);
                if (daAllStem.equals(daLeftStem)) {
                    StringBuilder daLeftLeafs = new StringBuilder(tempLeftString);
                    daLeftLeafs.delete(0, vertBarPosOne + 1);
                    daLeftLeafs.reverse();
                    leftLeafString = new String(daLeftLeafs);
                    break;
                }
            }
            
            
            for (int kthRightLiner = 0; kthRightLiner < nRightOneLiners; kthRightLiner++) {
                tempRightString = theRightOneLiners.get(kthRightLiner);
                String daRightStem = tempRightString.substring(0, vertBarPosOne + 1);
                if (daAllStem.equals(daRightStem)) {
                    StringBuilder daRightLeafs = new StringBuilder(tempRightString);
                    daRightLeafs.delete(0, vertBarPosOne + 1);
                    rightLeafString = new String(daRightLeafs);
                    break;
                }
            }      
            String daLine = leftLeafString + "|" + daAllStem + rightLeafString;
            blanx = generateNBlanx(maxLeftSize + 5 - leftLeafString.length());
            String printLine = blanx + daLine;
            oneLineBBSL.add(printLine);
        }
       
/****************************************************************************
 *                        Start Code for two liners                         *
 ***************************************************************************/        
        
        maxLeftSize = 0; maxRightSize = 0; maxBbslLineSize = 0;    
        
        // Need to know stem lengths to set up text boxes
        for (int jthLeftLiner = 0; jthLeftLiner < nLeftTwoLiners; jthLeftLiner++) {
            maxLeftSize = Math.max(maxLeftSize, theLeftTwoLiners.get(jthLeftLiner).length());
        }

        for (int jthRightLiner = 0; jthRightLiner < nRightTwoLiners; jthRightLiner++) {
            maxRightSize = Math.max(maxRightSize, theRightTwoLiners.get(jthRightLiner).length());
        } 

        maxBbslLineSize = maxLeftSize + maxRightSize;

        for (int ithTwoLiner = 0; ithTwoLiner < nAllTwoLiners; ithTwoLiner++) {
            
        initStrings();
  
            String tempAllString = theAllTwoLiners.get(ithTwoLiner);
            String daAllStem = tempAllString.substring(0, vertBarPosTwo + 1);
            for (int jthLeftLiner = 0; jthLeftLiner < nLeftTwoLiners; jthLeftLiner++) {
                tempLeftString = theLeftTwoLiners.get(jthLeftLiner);
                String daLeftStem = tempLeftString.substring(0, vertBarPosTwo + 1);
                if (daAllStem.equals(daLeftStem)) {
                    StringBuilder daLeftLeafs = new StringBuilder(tempLeftString);
                    daLeftLeafs.delete(0, vertBarPosTwo + 1);
                    daLeftLeafs.reverse();
                    leftLeafString = new String(daLeftLeafs);
                    break;
                }
            }
            
            for (int kthRightLiner = 0; kthRightLiner < nRightTwoLiners; kthRightLiner++) {
                tempRightString = theRightTwoLiners.get(kthRightLiner);
                String daRightStem = tempRightString.substring(0, vertBarPosTwo + 1);
                if (daAllStem.equals(daRightStem)) {
                    StringBuilder daRightLeafs = new StringBuilder(tempRightString);
                    daRightLeafs.delete(0, vertBarPosTwo + 1);
                    rightLeafString = new String(daRightLeafs);
                    break;
                }
            }    

            String daLine = leftLeafString + "|" + daAllStem + rightLeafString;
            blanx = generateNBlanx(maxLeftSize + 5 - leftLeafString.length());
            String printLine = blanx + daLine;
            twoLineBBSL.add(printLine);
        }

/****************************************************************************
 *                        Start Code for five liners                         *
 ***************************************************************************/        
        
        maxLeftSize = 0; maxRightSize = 0; maxBbslLineSize = 0;    
        
        // Need to know stem lengths to set up text boxes
        for (int jthLeftLiner = 0; jthLeftLiner < nLeftFiveLiners; jthLeftLiner++) {
            maxLeftSize = Math.max(maxLeftSize, theLeftFiveLiners.get(jthLeftLiner).length());
        }

        for (int jthRightLiner = 0; jthRightLiner < nRightFiveLiners; jthRightLiner++) {
            maxRightSize = Math.max(maxRightSize, theRightFiveLiners.get(jthRightLiner).length());
        }

        maxBbslLineSize = maxLeftSize + maxRightSize;
    
        for (int ithFiveLiner = 0; ithFiveLiner < nAllFiveLiners; ithFiveLiner++) {
            
            initStrings();
  
            String tempAllString = theAllFiveLiners.get(ithFiveLiner);
            String daAllStem = tempAllString.substring(0, vertBarPosFive + 1);
            for (int jthLeftLiner = 0; jthLeftLiner < nLeftFiveLiners; jthLeftLiner++) {
                tempLeftString = theLeftFiveLiners.get(jthLeftLiner);
                String daLeftStem = tempLeftString.substring(0, vertBarPosFive + 1);
                if (daAllStem.equals(daLeftStem)) {
                    StringBuilder daLeftLeafs = new StringBuilder(tempLeftString);
                    daLeftLeafs.delete(0, vertBarPosFive + 1);
                    daLeftLeafs.reverse();
                    leftLeafString = new String(daLeftLeafs);
                    break;
                }
            }
            
            for (int kthRightLiner = 0; kthRightLiner < nRightFiveLiners; kthRightLiner++) {
                tempRightString = theRightFiveLiners.get(kthRightLiner);
                String daRightStem = tempRightString.substring(0, vertBarPosFive + 1);
                if (daAllStem.equals(daRightStem)) {
                    StringBuilder daRightLeafs = new StringBuilder(tempRightString);
                    daRightLeafs.delete(0, vertBarPosFive + 1);
                    rightLeafString = new String(daRightLeafs);
                    break;
                }
            }    

            String daLine = leftLeafString + "|" + daAllStem + rightLeafString;
            blanx = generateNBlanx(maxLeftSize + 5 - leftLeafString.length());
            String printLine = blanx + daLine;
            fiveLineBBSL.add(printLine);
        }
    }
    
    private void initStrings() {
        leftLeafString = ""; rightLeafString = "";
        tempLeftString = ""; tempRightString = "";
    }
    
    public ArrayList<String> get_1_LineBBSL() { return oneLineBBSL; }
    public ArrayList<String> get_2_LineBBSL() { return twoLineBBSL; }
    public ArrayList<String> get_5_LineBBSL() { return fiveLineBBSL; } 
    
    public String getDescriptionOfVariable() { return descriptionOfVariable; }
    
    public int getBBSLFirstNonZeroColumn() { return bbslFirstNonZeroColumn; }
    public int getBBSLFirstNonConstantColumn() { return bbslFirstNonConstantColumn; }

    public ArrayList<QuantitativeDataVariable> getAllUDMs() { return bbslAllTheQDVs; }

    public double getMax() { return maxValue; }
    
    public String generateNBlanx(int nBlanks) {
        StringBuilder theBlanks = new StringBuilder();
        for (int iBlank = 0; iBlank < nBlanks; iBlank++) {
            theBlanks.append(" ");
        }
        return theBlanks.toString();
    }
}

