/****************************************************************************
 *                    SmartTextFieldChecker                                 * 
 *                           05/17/18                                       *
 *                            15:00                                         *
 ***************************************************************************/

/****************************************************************************
 *  Checks: positive & negative fractions & decimals  01/03                 * 
 *                  -0.25 a problem in checking probs                       *
 *  Note to self: When Alerts are presented, these constitute changes in    *
 *                focus.  Is there a specific focusChangeListener that      *
 *                could distinguish Alerts, ENTERS, and mouse clicks???     *
 ***************************************************************************/
package smarttextfield;

import genericClasses.DataUtilities;
import genericClasses.MyAlerts;

public class SmartTextFieldChecker {
    // POJOs
    boolean mb_Negative, mb_NonPositive, mb_NonZero, mb_NonNegative, 
            mb_Positive, mb_Integer, mb_Real, mb_NonBlank, mb_Numeric, 
            mb_Probability, mb_PositiveInteger, hasARestriction, isTraversed, 
            legalFraction, legalDecimal, okToContinue, comingFromEnter, 
            showingAnAlert, checkingForProbability;

    int intIfInt, nChangesToIgnore;
    int startNToIgnore = 4;
    
    double doubleIfDouble;

    String stringToCheck;
    
    // My classes
    MyAlerts myAlerts;
    SmartTextFieldHandler stfHandler;
    SmartTextField currentSTF;
    
    public SmartTextFieldChecker() { 
        myAlerts = new MyAlerts();
    }
    
    public SmartTextFieldChecker(SmartTextFieldHandler stfHandler) { 
        this.stfHandler = stfHandler; 
        myAlerts = new MyAlerts();
    }
 
    public void setSmartTextFieldHandler( SmartTextFieldHandler stfh) {
        stfHandler = stfh;
    }
    
    public void setSTF(SmartTextField toThisSTF) {
        currentSTF = toThisSTF; 
        stringToCheck = toThisSTF.getText();   
    } 

    /**************************************************************
    *    These restriction tests return FALSE if:                 *
    *         The MB restriction is asked for AND                 *
    *         the restriction asked for is not fulfilled.         *
     * @return 
    **************************************************************/
    
public boolean checkAllRestrictions() {
         
    /**************************************************************
    *  If we are checkingAllRestrictions after a click on ENTER
    *  nChangesToIgnore is set to 2 b/c the changeListener in     *
    *  the SmartTextField needs to consume two changes: losing    *
    *  focus and the click on the Alert.                          *
    **************************************************************/

        okToContinue = true;
        checkingForProbability = false; // Need to initialize here to avoid
                                        // false Bad Fraction alert.
                                        
        comingFromEnter = stfHandler.getComingFromEnter();
        //System.out.println("In checkAllRestrictions()...");
       // System.out.println("comingFromEnter = " + comingFromEnter);
/*
        if (theTF.isEditable() == false)
        { 
            myAlerts.showTFNotEditableAlert();
            okToContinue = false; 
            return okToContinue;
        } 
*/
        mb_Integer = currentSTF.getSmartTextField_MB_INTEGER();
        mb_Negative = currentSTF.getSmartTextField_MB_NEGATIVE();
        mb_NonBlank = currentSTF.getSmartTextField_MB_NONBLANK();
        mb_NonNegative = currentSTF.getSmartTextField_MB_NONNEGATIVE();
        mb_NonPositive = currentSTF.getSmartTextField_MB_NONPOSITIVE();
        mb_NonZero = currentSTF.getSmartTextField_MB_NONZERO();
        mb_Numeric = currentSTF.getSmartTextField_MB_NUMERIC();
        mb_Positive = currentSTF.getSmartTextField_MB_POSITIVE();
        mb_PositiveInteger = currentSTF.getSmartTextField_MB_POSITIVEINTEGER();
        mb_Probability = currentSTF.getSmartTextField_MB_PROBABILITY();
        mb_Real = currentSTF.getSmartTextField_MB_REAL();
        
        if ( (mb_Integer == true) || (mb_Negative == true) || (mb_NonNegative == true) || (mb_NonPositive == true) ||
             (mb_NonZero == true) || (mb_Numeric == true) || (mb_Positive == true) || (mb_PositiveInteger == true) ||
             (mb_Probability == true) || (mb_Real == true)) {
            
            mb_NonBlank = true;  
        }
        
        //System.out.println("After checking restrictions, mb_NonBlank = " + mb_NonBlank);
        
        //System.out.println("Before checking, mb_Probability = " + mb_Probability);
        if ((mb_Probability == true) && (checkSmartTextField_MB_PROBABILITY() == false)) {
            okToContinue = false;
            //System.out.println("mb_Probability checked, okToContinue = " + okToContinue);
            return okToContinue;
        }
                
        if ((mb_Numeric == true) && (checkSmartTextField_MB_NUMERIC() == false)) {
            okToContinue = false;
            //System.out.println("]mb_Numeric checked, okToContinue = " + okToContinue);
            return okToContinue;
        } 
               
        if ((mb_Real == true) && (checkSmartTextField_MB_REAL() == false)) {
            //System.out.println("mb_Real checked, okToContinue = " + okToContinue);
            okToContinue = false;
            return okToContinue;
        }
               
        if ((mb_Integer == true) && (checkSmartTextField_MB_INTEGER() == false)) {
            //System.out.println("mb_Integer checked, okToContinue = " + okToContinue);
            okToContinue = false;
            return okToContinue;
        }

        if ((mb_PositiveInteger == true) && (checkSmartTextField_MB_POSITIVEINTEGER() == false)) {
            //System.out.println("mb_PositiveInteger checked, okToContinue = " + okToContinue);
            okToContinue = false;
            return okToContinue;
        }
        
        if ((mb_NonBlank == true) && (checkSmartTextField_MB_NONBLANK() == false)) {
            //System.out.println("mb_NonBlank checked, okToContinue = " + okToContinue);
            okToContinue = false;
            return okToContinue;
        }
        //System.out.println("At end of restrictions, okToContinue = " + okToContinue);
    return okToContinue;
}
    
    private boolean checkSmartTextField_IsEditable() {
        
        okToContinue = true;
        //System.out.println("Checking |" + stringToCheck + "| for editable");
        if (currentSTF.getIsEditable() == false)
        { 
            if (comingFromEnter == true)
                stfHandler.setNChangesToIgnore(startNToIgnore);
            showingAnAlert = true;
            myAlerts.showTFNotEditableAlert();
            showingAnAlert = false;
            okToContinue = false; 
        }    
         return okToContinue;       
    } 

    private boolean checkSmartTextField_MB_NEGATIVE() {
        okToContinue = true;
        //System.out.println("Checking |" + stringToCheck + "| for necessary negative");

        
         return okToContinue;       
    }    

    private boolean checkSmartTextField_MB_NONPOSITIVE() { 
        okToContinue = true;
        //System.out.println("Checking |" + stringToCheck + "| for necessary non-positive");
        
        return okToContinue;        
    } 
    
    private boolean checkSmartTextField_MB_NONZERO() {
        okToContinue = true;
        //System.out.println("Checking |" + stringToCheck + "| for nonZero");        
        return okToContinue;        
    } 
    
    private boolean checkSmartTextField_MB_NONNEGATIVE() {
        okToContinue = true;
        //System.out.println("Checking |" + stringToCheck + "| for nonNegative");        
        return okToContinue;        
    } 
  
    private boolean checkSmartTextField_MB_POSITIVE() {
        okToContinue = true;
        //System.out.println("Checking |" + stringToCheck + "| for necessary positive");        
        return okToContinue;        
    }       
        
    private boolean checkSmartTextField_MB_INTEGER() {
        okToContinue = true;
        //System.out.println("Checking |" + stringToCheck + "| for necessary integer");
        if ((mb_Integer == true) && (DataUtilities.stringIsAnInteger(stringToCheck) == false))
        { 
            if (comingFromEnter == true)
                stfHandler.setNChangesToIgnore(startNToIgnore);
            showingAnAlert = true;
            myAlerts.showMustBeIntegerAlert();
            showingAnAlert = false;
            okToContinue = false; 
        }
        else    //  is an integer
        {
            if (mb_Integer == true)
                currentSTF.setSmartTextInteger(intIfInt);
        }
        
        return okToContinue;         
    } 
    
    private boolean checkSmartTextField_MB_POSITIVEINTEGER() {
        okToContinue = true;
        //System.out.println("Checking |" + stringToCheck + "| for necessary positive integer");
        if (mb_PositiveInteger == false)
            return okToContinue;    //  No need to check
        if (DataUtilities.stringIsAnInteger(stringToCheck) == false)
        { 
            if (comingFromEnter == true)
                stfHandler.setNChangesToIgnore(startNToIgnore);
            showingAnAlert = true;
            myAlerts.showMustBePositiveIntegerAlert();
            showingAnAlert = false;
            okToContinue = false; 
            return okToContinue;
        }
        else
        {
            intIfInt = Integer.valueOf(stringToCheck);  
            if (intIfInt < 1)
            {
            if (comingFromEnter == true)
                stfHandler.setNChangesToIgnore(startNToIgnore);
                showingAnAlert = true;
                myAlerts.showMustBePositiveIntegerAlert();
                showingAnAlert = false;
                okToContinue = false;
            }
            else {
                currentSTF.setSmartTextInteger(intIfInt); 
            }
        }
        
        return okToContinue;        
    } 

    private boolean checkSmartTextField_MB_REAL() {
        okToContinue = true;
        //System.out.println("Checking |" + stringToCheck + "| for reality (so to speak)");
        if ((mb_Real == true) && (DataUtilities.stringIsADouble(stringToCheck) == false))
        { 
            if (comingFromEnter == true)
                stfHandler.setNChangesToIgnore(startNToIgnore);
            showingAnAlert = true;
            myAlerts.showMustBeDoubleAlert();
            showingAnAlert = false;
            okToContinue = false; 
        }
        else
        {   // Store this a a legal real if must be
            if (mb_Real == true) {
                doubleIfDouble = Double.valueOf(stringToCheck);
                currentSTF.setSmartTextDouble(doubleIfDouble);   
            }   
        }    
        return okToContinue;        
    } 

    // public so that a range of fields can be checked from the handler
    public boolean checkSmartTextField_MB_NONBLANK() {
        okToContinue = true;
        //System.out.println("Checking |" + stringToCheck + "| for Non-blank");
        if ((mb_NonBlank == true) && (stringToCheck.length() == 0)) {
            if (comingFromEnter == true)
                stfHandler.setNChangesToIgnore(startNToIgnore);
            showingAnAlert = true;
            myAlerts.showMustBeNonBlankAlert();
            showingAnAlert = false;
            okToContinue = false; 
        }
        return okToContinue;        
    } 
    
    private boolean checkSmartTextField_MB_NUMERIC() {
        okToContinue = true;    
        //System.out.println("Checking |" + stringToCheck + "| for numeric");
        okToContinue = (checkSmartTextField_MB_INTEGER() || 
                     checkSmartTextField_MB_REAL());        
        return okToContinue;
    }

    private boolean checkSmartTextField_MB_PROBABILITY() { 
        
        // System.out.println("Checking for legal probability");
        checkingForProbability = true;
        //  Check for legal decimal, and if true check for legal prob
        if (DataUtilities.stringIsADouble(stringToCheck) == true) {
            double tempDouble = Double.valueOf(stringToCheck);
            if ((0 < tempDouble) && (tempDouble < 1.0)) {
                doubleIfDouble = tempDouble;   
                currentSTF.setSmartTextDouble(doubleIfDouble); 
                okToContinue = true;
                // System.out.println("Returning okToContinue A = " + okToContinue); 
                return okToContinue;
            }
        }    
        
        //  Check for legal fraction, and if true check for legal prob
        if (checkForLegalFraction() == true) {  // ,it is now in decimal form
            double tempDouble = Double.valueOf(stringToCheck);
            if ((0 < tempDouble) && (tempDouble < 1.0)) {
                doubleIfDouble = tempDouble;   
                currentSTF.setSmartTextDouble(doubleIfDouble); 
                okToContinue = true;
                //System.out.println("Returning okToContinue B = " + okToContinue); 
                return okToContinue;
            }
        }    
        okToContinue = false;
        if (comingFromEnter == true)
            stfHandler.setNChangesToIgnore(startNToIgnore);
        showingAnAlert = true;   
        myAlerts.showIllegalProbabilityAlert();
        showingAnAlert = false;
        //System.out.println("Returning from checkSmartTextField_MB_PROBABILITY, okToContinue = " + okToContinue);
        return okToContinue;
    }

    private boolean checkForLegalFraction() { 
        okToContinue = true;
        // System.out.println("Checking |" + stringToCheck + "| for legal fraction");
        int numerator = 0, denominator = 0;
        String[] tokens = stringToCheck.split("/");
        //System.out.println("tokens.length = " + tokens.length);
        if (tokens.length != 2) {
            okToContinue = false;
            if (comingFromEnter == true)
                stfHandler.setNChangesToIgnore(startNToIgnore);
            showingAnAlert = true;
            if (checkingForProbability == false)
                myAlerts.showBadFractionAlert();
            showingAnAlert = false;
            //System.out.println("Returning okToContinue (a) = " + okToContinue);
            return okToContinue;
        }
        if ((DataUtilities.stringIsAnInteger(tokens[0]) == false) || 
                (DataUtilities.stringIsAnInteger(tokens[1]) == false)){
            if (comingFromEnter == true)
                stfHandler.setNChangesToIgnore(startNToIgnore);
            showingAnAlert = true;
            myAlerts.showBadFractionAlert();
            showingAnAlert = false;
            okToContinue = false;
            //System.out.println("Returning okToContinue (b) = " + okToContinue);
            return okToContinue;   
        }
        else {  //  Is a legal fraction, convert to decimal and update stringToCheck
            numerator = Integer.parseInt(tokens[0]);
            denominator = Integer.parseInt(tokens[1]);           
            doubleIfDouble = (double)numerator / (double) denominator;
            currentSTF.setSmartTextDouble(doubleIfDouble);
            stringToCheck = new String(String.valueOf(doubleIfDouble));
            currentSTF.setSmartTextDouble(doubleIfDouble);
            currentSTF.setText(stringToCheck);
            //System.out.println("stringToCheck reset to: " + stringToCheck);
            //System.out.println("Returning okToContinue (c) = " + okToContinue);
            return okToContinue;
        }
    }    
}
