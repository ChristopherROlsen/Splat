/****************************************************************************
 *                        SmartTextField                                    * 
 *                           05/17/17                                       *
 *                            15:00                                         *
 ***************************************************************************/

/****************************************************************************
 *  Note to self:  Are all these constructors necessary?                    *
 ***************************************************************************/

package smarttextfield;

import javafx.scene.control.TextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class SmartTextField {
    // POJOs
    boolean blankIsOK = false;
    boolean showingAnAlert = false, ignoreFocusChanges = false;
    
    boolean mb_Negative = false, mb_NonPositive = false, mb_NonZero = false, 
            mb_NonNegative = false, mb_Positive = false, mb_Integer = false, 
            mb_Real = false, mb_NonBlank = false, mb_Numeric = false, 
            mb_Probability = false, mb_PositiveInteger = false;  
    boolean hasARestriction, comingFromEnter;

    int intIfInt, shiftTabTo_TF, tabTo_TF, previous_AL_Index, current_AL_Index;
    
    double doubleIfDouble;
    
    // My classes
    SmartTextField thisSTF;
    SmartTextFieldHandler stfHandler;
    
    // POJOs / FX
    String stringToCheck;
    TextField thisTF;
    
    public SmartTextField() {}
    
    public SmartTextField(SmartTextFieldHandler stfHandler) {   // Completely blank -- no clue what for
        //System.out.println("Constructor A");
        this.stfHandler = stfHandler;
        thisTF = new TextField("");
        shiftTabTo_TF = -1;      // Should just sit there
        tabTo_TF = -1;          // Should just sit there
        initialize();
    }
    
    public SmartTextField(SmartTextFieldHandler stfHandler, int shiftTab, int unshiftTab) { 
        this.stfHandler = stfHandler;
        thisTF = new TextField("");
        shiftTabTo_TF = shiftTab; 
        tabTo_TF = unshiftTab; 
        //System.out.println("shiftTabTo_TF = " + shiftTabTo_TF);
        //System.out.println("     tabTo_TF = " + tabTo_TF);
        initialize();
    }
    
    public SmartTextField(SmartTextFieldHandler stfHandler, String theTFValue) { 
        //System.out.println("Constructor C");
        this.stfHandler = stfHandler;
        thisTF = new TextField(theTFValue);
        shiftTabTo_TF = -1;  // Should just sit there
        tabTo_TF = -1;       // Should just sit there
        initialize();
    }
    
    public SmartTextField(SmartTextFieldHandler stfHandler, String theTFValue, int shiftTab, int unshiftTab) {
        //System.out.println("Constructor D");
        this.stfHandler = stfHandler;
        thisTF = new TextField(theTFValue); 
        shiftTabTo_TF = shiftTab; 
        tabTo_TF = unshiftTab;    // and Enter Key
        initialize();
    }  
    
    private void initialize() { 
        thisSTF = this;
        
        previous_AL_Index = -1;
        current_AL_Index = -1;
        
        thisTF.focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue,
                                                                         Boolean newPropertyValue) {
                showingAnAlert = stfHandler.getShowingAnAlert();
                comingFromEnter = stfHandler.getComingFromEnter();          
                int tempChangesToIgnore = stfHandler.getNChangesToIgnore();
                ignoreFocusChanges = ((showingAnAlert == true) || (tempChangesToIgnore > 0));
                if ((showingAnAlert == false) && (tempChangesToIgnore == 0) && (comingFromEnter == false)) {
                    if (newPropertyValue) {  // This TF is coming on focus
                        int tempCurr = getArrayListPosition(thisTF);
                        stfHandler.setCurrentAccessed_AL_Index(tempCurr);
                    }
                    else {  // This TF is going off focus

                        String daText = thisTF.getText();
                        int tempCurr = getArrayListPosition(thisTF);
                        stfHandler.setLastAccessed_AL_Index(tempCurr);
                        if (!daText.isEmpty()) {
                            thisTF.setText(daText);
                            thisTF.commitValue();
                            stfHandler.getSmartTextFieldChecker().setSTF(thisSTF);

                            boolean restrictionsCheck = stfHandler.getSmartTextFieldChecker().checkAllRestrictions();
                            if (restrictionsCheck == true) { // i.e. okToContinue
                                thisSTF.setText(getTextField().getText());
                                thisTF.fireEvent(new ActionEvent(this, null));
                            }
                            else {
                                int tempPrev = stfHandler.getLastAccessed_AL_Index();
                                stfHandler.getHandlerArrayList().get(tempPrev).getTextField().setText("");
                                stfHandler.getHandlerArrayList().get(tempPrev).getTextField().requestFocus();                              
                            }                          
                        }
                    } 
                }   //  End if ignore
                else
                {
                    // System.out.println("Showing an alert");
                }
            }   //  End change
        });
        
        thisTF.setFocusTraversable(false);
        //handlerArrayList.get(tf)
        //                .getTextField()
        //                .focusedProperty();

        thisTF.setOnKeyTyped(stfKeyEventHandler);

        thisTF.setOnKeyPressed(stfKeyEventHandler);

        thisTF.setOnKeyReleased(stfKeyEventHandler);

        thisTF.addEventHandler(MouseEvent.MOUSE_CLICKED, e-> handleMouseClick(e));            
    }
 
    public void handleMouseClick(MouseEvent e) {
        //System.out.println("handleMouseClick(MouseEvent e)");
        Object nodyWody = e.getSource();
        TextField tffy = (TextField)nodyWody;
        previous_AL_Index = stfHandler.getLastAccessed_AL_Index();

        //System.out.println("previous_AL_Index = " + previous_AL_Index);
        current_AL_Index = getArrayListPosition(tffy);
        // *******************************************************************
        // Protect against the first click Index initializations
        if (previous_AL_Index < 0) {
            previous_AL_Index = 0;
            stfHandler.setLastAccessed_AL_Index(0);
        }
        
        if (current_AL_Index < 0) {
            current_AL_Index = 0;
            stfHandler.setCurrentAccessed_AL_Index(0);
        }
        
        // *******************************************************************
        stfHandler.setLastAccessed_AL_Index(current_AL_Index);
        //System.out.println("current_AL_Index = " + current_AL_Index);

    }
    
    public int getArrayListPosition( TextField ofThisTextField)  {

        for (int tf = 0; tf < stfHandler.handlerArrayList.size(); tf++) {
            if (ofThisTextField.equals(stfHandler.handlerArrayList.get(tf).getTextField())) {
                // System.out.println("tf = " + tf);
                current_AL_Index = tf;
                break;  //  Exit the loop
            }
        }
        return current_AL_Index;
    }        
    
    private EventHandler<KeyEvent> stfKeyEventHandler = new EventHandler<KeyEvent>()  {
        public void handle(KeyEvent ke) {      
            ignoreFocusChanges = true;
            KeyCode keyCode = ke.getCode();  
            //System.out.println("STF/174, keyCode = " + keyCode);
            if (ke.getEventType() == KeyEvent.KEY_TYPED) {
                //System.out.println("STF/175 KeyEvent.KEY_TYPED, keyCode = " + keyCode);            
            }   // end KEY_TYPED
            else
            if (ke.getEventType() == KeyEvent.KEY_PRESSED) { 
                //System.out.println("STF/179 KeyEvent.KEY_PRESSED, keyCode = " + keyCode);
                if ((keyCode == KeyCode.TAB) && (ke.isShiftDown() == true)) {
                    //System.out.println("STF/182 ((keyCode == KeyCode.TAB) && (ke.isShiftDown() == true)), keyCode = " + keyCode);
                    doShiftTabKey();
                    ke.consume();
                }   
                else
                if ((keyCode == KeyCode.TAB) && (ke.isShiftDown() == false)) {
                    //System.out.println("STF/188 ((keyCode == KeyCode.TAB) && (ke.isShiftDown() == false)), keyCode = " + keyCode);
                    doEnterKey(); 
                    ke.consume();
                }                     
            }   //  end KEY_PRESSED
            else
            if (ke.getEventType() == KeyEvent.KEY_RELEASED) {   
                //System.out.println("STF/195 KeyEvent.KEY_RELEASED, keyCode = " + keyCode);
                if (keyCode == KeyCode.ENTER) {
                   //System.out.println("STF/196 keyCode == KeyCode.ENTER, keyCode = " + keyCode);
                   doEnterKey();   
                }
                else {  }                         
                        
            }   
            ignoreFocusChanges = false;
        }   //  end HandleKeyEvent
    };    
    
    public void doEnterKey() {    
        //System.out.println("doEnterKey()");
        //System.out.println("shiftTabTo_TF, tabTo_TF = " + shiftTabTo_TF + ", " + tabTo_TF);
        int tempCurr = getArrayListPosition(thisTF);
        stfHandler.setLastAccessed_AL_Index(tempCurr);
        stfHandler.setComingFromEnter(true);
        showingAnAlert = stfHandler.getShowingAnAlert();
        ignoreFocusChanges = showingAnAlert;
        if (showingAnAlert == false)  {  
            int prevSTF = shiftTabTo_TF;       
            int nextSTF = tabTo_TF;
            ignoreFocusChanges = true;
            stfHandler.getSmartTextFieldChecker().setSTF(this);
            ignoreFocusChanges = false;
            ignoreFocusChanges = true;
            stfHandler.setComingFromEnter(true);
            boolean restrictionsCheck = stfHandler.getSmartTextFieldChecker().checkAllRestrictions();
            stfHandler.setComingFromEnter(false);
            ignoreFocusChanges = false;           
            
            if (restrictionsCheck == true) {    //  i.e. okToContinue
                ignoreFocusChanges = true;
                thisTF.setText(getTextField().getText());
                thisTF.fireEvent(new ActionEvent(this, null));
                stfHandler.setComingFromEnter(true);
                stfHandler.getHandlerArrayList().get(nextSTF).getTextField().requestFocus();
                stfHandler.setComingFromEnter(false);
                current_AL_Index = nextSTF;
            }
            else {
                ignoreFocusChanges = true;
                stfHandler.getHandlerArrayList().get(tempCurr).getTextField().setText("");
                stfHandler.getHandlerArrayList().get(tempCurr).getTextField().requestFocus();
                ignoreFocusChanges = false;
            }
        
        } else { 
            // System.out.println("--------> Showing alert"); 
        }
    }   //  end DoEnterKey
    
    public void doShiftTabKey() {  
        //System.out.println("doShiftTabKey(), current_AL_Index = " + current_AL_Index);

        if (current_AL_Index < 0)
            current_AL_Index = 0;

        int prevSTF = stfHandler.handlerArrayList.get(current_AL_Index).getPrevSmartTF();
        //System.out.println("doShiftTabKey(), prevSTF = " + prevSTF);
        stfHandler.handlerArrayList
                  .get(current_AL_Index)
                  .getTextField()
                  .fireEvent(new ActionEvent(stfHandler.handlerArrayList.get(current_AL_Index).getTextField(), null));
        stfHandler.handlerArrayList.get(prevSTF).getTextField().requestFocus();
        //System.out.println("STF line 245, prevSTF = " + prevSTF);
        current_AL_Index = prevSTF;
    }     
    
    
    public String getText() {return thisTF.getText(); }
    public void setText(String theText) { thisTF.setText(theText); }
    
    public boolean getIsEditable() {return thisTF.isEditable(); }
    public void setEditable(boolean eddyWeddy) { thisTF.setEditable(eddyWeddy); }
    
    public TextField getTextField() {return thisTF;} 
    public SmartTextField getSmartTextField() {return this; }
    
    public int getSmartTextInteger() { return intIfInt; }
    public void setSmartTextInteger( int toThis) { intIfInt = toThis; }
    
    public double getSmartTextDouble() { return doubleIfDouble; }
    public void setSmartTextDouble( double toThis) { doubleIfDouble = toThis; }
    
    
    public boolean getSmartTextField_MB_NEGATIVE() { 
        return mb_Negative; 
    }
    
    public void setSmartTextField_MB_NEGATIVE(boolean mustBe) { 
        mb_Negative = mustBe;
        mb_NonBlank = true;
    }
 
    public boolean getSmartTextField_MB_NONPOSITIVE() { 
        return mb_NonPositive; 
    }
        
    public void setSmartTextField_MB_NONPOSITIVE(boolean mustBe) { 
        mb_NonPositive = mustBe; 
        mb_NonBlank = true;
    }
    
    public boolean getSmartTextField_MB_NONZERO() { 
        return mb_NonZero; 
    }

    public void setSmartTextField_MB_NONZERO(boolean mustBe) { 
        mb_NonZero = mustBe; 
        mb_NonBlank = true;
    }
    
    public boolean getSmartTextField_MB_NONNEGATIVE() { 
        return mb_NonNegative; 
    }

    public void setSmartTextField_MB_NONNEGATIVE(boolean mustBe) { 
        mb_NonNegative = mustBe;
        mb_NonBlank = true;
    }
    
    public boolean getSmartTextField_MB_POSITIVE() { 
        return mb_Positive; 
    }
           
    public void setSmartTextField_MB_POSITIVE(boolean mustBe) { 
        mb_Positive = mustBe; 
        mb_NonBlank = true;
    }

    public boolean getSmartTextField_MB_INTEGER() { 
        return mb_Integer; 
    }
    
    public void setSmartTextField_MB_INTEGER(boolean mustBe) { 
        mb_Integer = mustBe; 
        mb_NonBlank = true;
    }
    
    public boolean getSmartTextField_MB_POSITIVEINTEGER() { 
        return mb_PositiveInteger;
    }
        
    public void setSmartTextField_MB_POSITIVEINTEGER(boolean mustBe) { 
        mb_PositiveInteger = mustBe;
        //System.out.println("356 STF, mb_PositiveInteger = " + mb_PositiveInteger);
        mb_NonBlank = true;
    }

    public boolean getSmartTextField_MB_REAL() { 
        return mb_Real; 
    }    
    
    public void setSmartTextField_MB_REAL(boolean mustBe) { 
        mb_Real = mustBe;
        mb_NonBlank = true;
    }
    
    public boolean getSmartTextField_MB_NONBLANK() { 
        return mb_NonBlank; 
    }
 
    public void setSmartTextField_MB_NONBLANK(boolean mustBe) { 
        mb_NonBlank = mustBe;
        //System.out.println("374 STF, mb_NonBlank = " + mb_NonBlank);    
    }
    
    public boolean getSmartTextField_MB_NUMERIC() { 
        return mb_Numeric;
    }
    
    public void setSmartTextField_MB_NUMERIC(boolean mustBe) { 
        mb_Numeric = mustBe; 
        mb_NonBlank = true;
    }
    
    public boolean getSmartTextField_MB_PROBABILITY() { 
        return mb_Probability; 
    } 
    
    public void setSmartTextField_MB_PROBABILITY(boolean mustBe) { 
        mb_Probability = mustBe;
        mb_NonBlank = true;
    } 
    
    public void setPreAndPostSmartTF (int shiftTab, int unshiftTab) {
        shiftTabTo_TF = shiftTab; tabTo_TF = unshiftTab;        
    }
    
    public int getPrevSmartTF () {
        return shiftTabTo_TF;        
    }
    
    public void setPrevSmartTF (int newPrev) {  //  No idea whatFor
        shiftTabTo_TF = newPrev;        
    }
        
    public int getNextSmartTF () {
        return tabTo_TF;        
    }
    
    public void setNextSmartTF (int newNext) {     //  No idea whatFor
        tabTo_TF = newNext;        
    }
    
    public String toString() {
        String textString = new String(this.getText());
        //int left = this.getPrevSmartTF();
        //int right = this.getNextSmartTF();
        int left = -1;
        int right = -1;
        String outString = new String(textString + ", " + left + ", " + right);
        return outString;
    }
}
