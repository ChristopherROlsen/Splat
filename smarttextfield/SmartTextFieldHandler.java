/****************************************************************************
 *                     SmartTextFieldHandler                                * 
 *                           05/17/18                                       *
 *                             15:00                                        *
 ***************************************************************************/
package smarttextfield;

import genericClasses.MyAlerts;
import java.util.ArrayList;

public class SmartTextFieldHandler {
    // POJOs
    boolean blankIsOK = false;
    
    boolean mb_Negative = false, mb_NonPositive = false, mb_NonZero = false, 
            mb_NonNegative = false, mb_Positive = false, mb_Integer = false, 
            mb_Real = false, mb_NonBlank = true, mb_Numeric = false, 
            mb_Probability = false, mb_PositiveInteger = false;
    
    boolean hasARestriction, isTraversed, legalFraction, legalDecimal,
            comingFromEnter;
    
    double doubleIfDouble;
    
    int intIfInt, lastAccessed_AL_Index, nChangesToIgnore; 
    static int current_AL_Index, prevSmartTF_Number, nextSmartTF_Number;

    String stringToCheck;

    // My classes
    MyAlerts myAlerts;
    SmartTextFieldChecker stfChecker;
    ArrayList<SmartTextField> handlerArrayList;
    
    public SmartTextFieldHandler() { 
        handlerArrayList = new ArrayList<>();
        current_AL_Index = 0;
        initialize();      
    }
    
    public ArrayList<SmartTextField> getHandlerArrayList() {
        return handlerArrayList;
    }
    
    public void setHandlerArrayList(ArrayList<SmartTextField> alstf) {
        handlerArrayList = alstf;
    }
    
    public void setHandlerTransversal(boolean doTransversal) {
        isTraversed = doTransversal;
    }
    
    public void setHandlerTransversalIndex(int travIndex) {
        current_AL_Index = travIndex;
    }
    
    public void setFocusRequest(int focusHere) {
        handlerArrayList.get(intIfInt).getTextField().requestFocus();
    }
    
    private void initialize() {
        myAlerts = new MyAlerts();
        stfChecker = new SmartTextFieldChecker(this);
        lastAccessed_AL_Index = -1;     
    }
    
    /**************************************************************
    *  If we are checkingAllRestrictions after a click on ENTER   *
    *  nChangesToIgnore is set to 2 b/c the changeListener in     *
    *  the SmartTextField needs to consume two changes: losing    *
    *  focus and the click on the Alert.                          *
     * @return 
    **************************************************************/

    public int getNChangesToIgnore() {
        if (nChangesToIgnore > 0)
            nChangesToIgnore--;
        return nChangesToIgnore;
    }
    
    public void setNChangesToIgnore(int toThisManyChanges) {
        nChangesToIgnore = toThisManyChanges;
    }

    // Note: Only the alert class can set ShowingAnAlert
    public boolean getShowingAnAlert() {
        return myAlerts.getShowingAnAlert();
    }

    public boolean getComingFromEnter() {return comingFromEnter; }
    
    public void setComingFromEnter(boolean toThis) {
        comingFromEnter = toThis;
    }
    
    public int getCurrentAccessed_AL_Index() {return current_AL_Index;}
    
    public void setCurrentAccessed_AL_Index( int toThis_AL_Index) {
        current_AL_Index = toThis_AL_Index;
    }
    
    public int getLastAccessed_AL_Index() {return lastAccessed_AL_Index;}
    
    public void setLastAccessed_AL_Index( int toThis_AL_Index) {
        lastAccessed_AL_Index = toThis_AL_Index;
    }
  
    public boolean finalCheckForBlanksInArray(int startHere, int endHere) {
        boolean continueCheck = true;   //  To molify compiler
        boolean mustBeNonBlank = true;  //  To molify compiler
        boolean isBlank = true;         //  To molify compiler
        for (int smartFieldIndex = startHere; smartFieldIndex <= endHere; smartFieldIndex++) {
            if (continueCheck == true) {
                SmartTextField tempSTF = new SmartTextField();
                tempSTF = handlerArrayList.get(smartFieldIndex).getSmartTextField();
                stfChecker.setSTF(tempSTF);
                mustBeNonBlank = handlerArrayList.get(smartFieldIndex).getSmartTextField_MB_NONBLANK();
                String tempString = tempSTF.getText();
                isBlank = tempString.trim().isEmpty();
                continueCheck = ((mustBeNonBlank == false) || (isBlank == false));
            }
        }
        return continueCheck;
    }
    
    public boolean finalCheck4IndividualSTFBlank(SmartTextField singleSTF) {
        SmartTextField tempSTF = new SmartTextField();
        tempSTF = singleSTF;
        stfChecker.setSTF(tempSTF);
        boolean mustBeNonBlank = tempSTF.getSmartTextField_MB_NONBLANK();
        String tempString = tempSTF.getText();
        boolean isBlank = tempString.trim().isEmpty();
        boolean okToContinue = ((mustBeNonBlank == false) || (isBlank == false));
        return okToContinue;
    }
 
    public void setPreAndPostSmartTF (int shiftTab, int unshiftTab) {
        prevSmartTF_Number = shiftTab; nextSmartTF_Number = unshiftTab;        
    }
    
    public SmartTextFieldChecker getSmartTextFieldChecker() {return stfChecker; }
}
