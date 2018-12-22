/****************************************************************************
 *                           MyX2Alerts                                      *
 *                            05/15/18                                      *
 *                             12:00                                        *
 ***************************************************************************/

package chiSquare;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
public class MyX2Alerts {
    
    static boolean showingAnAlert;
    
    static Alert adjustDotsAlert, doVarInfoAlert, doAxesPointsAlert, 
          clickTheDotsAlert, saveToDataFileAlert, tfsNotOKAlert,
          axesNotOKAlert, nonIntegerAlert, showNoOpenFileAlert, 
          nonDoubleAlert, missingDoubleAlert, missingDataAlert,
          missingInputFieldAlert, mustBePositiveIntegerAlert,
          mustBeDoubleAlert, mustBeIntegerAlert, badFractionAlert,
          tfNotEditableAlert, mustBeNonBlankAlert, mustBeNonNegIntegerAlert,
          illegalProbabilityAlert, nonUniqueCategoriesAlert, nonSumToOneAlert;
    
   public MyX2Alerts() { showingAnAlert = false;}    //  End constructor
    
    public static void showBadFractionAlert() { 
        showingAnAlert = true;
        badFractionAlert = new Alert(AlertType.WARNING);   
        badFractionAlert.setTitle("Warning!  Bad fraction entry!");
        badFractionAlert.setHeaderText("You have entered something other than a legal fraction or decimal.");
        badFractionAlert.setContentText("In this data entry circumstance we are on the lookout for freelance"
                                      + "\nfractious entries fraught with frenetic non-compliant fractions."
                                      + "\nBasically, we frown on that sort of frivolity.  Let's get with "
                                      + "\nthe program. xxx/yyy and x.yyy are fine, your format is not."
                                      + "\nShall we try that again???");              
        badFractionAlert.showAndWait(); 
        showingAnAlert = false;
    }
    
    public static void showIllegalProbabilityAlert() { 
        showingAnAlert = true;
        illegalProbabilityAlert = new Alert(AlertType.WARNING);   
        illegalProbabilityAlert.setTitle("Warning!  Illegal proportion or probability!");
        illegalProbabilityAlert.setHeaderText("You have entered something that cannot be a legal value!");
        illegalProbabilityAlert.setContentText("Only positive proper fractions and decimals between 0 and 1 are legal"
                                             + "\nin this field.  Blame it on Bernoulli, or if you are of a certain"
                                             + "\nage, (https://en.wikipedia.org/wiki/Blame_It_on_the_Bossa_Nova)."
                                             + "\nJust don't blame me.  Be ye frequentist or Bayesian, you gotta follow"
                                             + "\nthe rules laid down by Kolmogorov in 1933. No rebellion allowed.");    
        illegalProbabilityAlert.showAndWait();
        showingAnAlert = false;
    }
    
    public static void showMustBeIntegerAlert() { 
        showingAnAlert = true;
        mustBeIntegerAlert = new Alert(AlertType.WARNING);   
        mustBeIntegerAlert.setTitle("Warning! Must be integer!");
        mustBeIntegerAlert.setHeaderText("You have entered something other than an integer.");
        mustBeIntegerAlert.setContentText("Aha! Thought you'd slip one by, did you?  Take a flying leap off the"
                                         + "\ncollection of integers into the snake pit of open intervals, "
                                         + "\ni.e. {(k, k + 1), k " + "\u220A" + " integers?  That sort of "
                                         + "\nmathematical hocus-pocus is not allowed.  You are hereby warned"
                                         + "\nto stay in this neighborhood:  {... -2, -1, 0, +1, +2..}");         
        mustBeIntegerAlert.showAndWait(); 
        showingAnAlert = false;
    }
    
    public static void showMustBeNonNegIntegerAlert() { 
        showingAnAlert = true;
        mustBeNonNegIntegerAlert = new Alert(AlertType.WARNING);   
        mustBeNonNegIntegerAlert.setTitle("Warning!  Must be nonnegative integer!");
        mustBeNonNegIntegerAlert.setHeaderText("You have entered something other than a non-negative integer.");
        mustBeNonNegIntegerAlert.setContentText("Aha! Thought you'd slip one by, did you?  Trying to impress"
                                         + "\nyour lab partner with your knowledge of more advanced math?"
                                         + "\nDid you try something irrational?  Complex? Transcendental?"
                                         + "\nThat sort of wizardry is not allowed: This is your yellow "
                                         + "\nbrick road of possible values for this field: 0, 1, 2, ...");           
        mustBeNonNegIntegerAlert.showAndWait(); 
        showingAnAlert = false;
    }
    
    public static void showMustBeNonBlankAlert() { 
        showingAnAlert = true;
        mustBeNonBlankAlert = new Alert(AlertType.WARNING);   
        mustBeNonBlankAlert.setTitle("Warning! Must be nonblank!");
        mustBeNonBlankAlert.setHeaderText("You have left a data entry field blank.");
        mustBeNonBlankAlert.setContentText("Did you think your perfidy would pass without notice?  Are you "
                                         + "\none of those who trust in haruspices to get correct answers"
                                         + "\nwith the information you provide and a consultation with the"
                                         + "\nentrails of chickens?  That may have worked in ancient Rome,"
                                         + "\nbut it won't work here.  Let's try this entry thing again.");     

        mustBeNonBlankAlert.showAndWait(); 
        showingAnAlert = false;
    }
    
    public static void showMustBePositiveIntegerAlert() { 
        showingAnAlert = true;
        mustBePositiveIntegerAlert = new Alert(AlertType.WARNING);   
        mustBePositiveIntegerAlert.setTitle("Warning!  Must be positive integer!");
        mustBePositiveIntegerAlert.setHeaderText("You have entered something other than a positive integer.");
        mustBePositiveIntegerAlert.setContentText("Ok, so here's the deal.  There are numbers, and there are other"
                                                + "\nthan numbers, like words and punctuation.  What you must enter "
                                                + "\nin this field are numbers, and numbers of the Arabic persuation."
                                                + "\nThe Decline and Fall of the Roman Empire included the Decline and"
                                                + "\nFall of Roman numerals.  Let's try this number thing again...");        
        
        mustBePositiveIntegerAlert.showAndWait(); 
        showingAnAlert = false;
    }
   
    public static void showMustBeDoubleAlert() { 
        showingAnAlert = true;
        mustBeDoubleAlert = new Alert(AlertType.WARNING);   
        mustBeDoubleAlert.setTitle("Warning! Must be a number!");
        mustBeDoubleAlert.setHeaderText("You have entered something other than a number.");
        mustBeDoubleAlert.setContentText("Ok, so here's the deal.  There are numbers, and there are other"
                                    + "\nthan numbers, like words and punctuation.  What you must enter "
                                    + "\nin this field are numbers, and numbers of the Arabic persuation."
                                    + "\nThe Decline and Fall of the Roman Empire included the Decline and"
                                    + "\nFall of Roman numerals also.  Let's try this number thing again.");              
        mustBeDoubleAlert.showAndWait();
        showingAnAlert = false;
    }
    
    public static void showMissingInformationAlert() { 
        showingAnAlert = true;
        missingDataAlert = new Alert(AlertType.WARNING);
        missingDataAlert.setTitle("Warning!  Missing information!");
        missingDataAlert.setHeaderText("Uh, it's about your data entry...it's not all there!");
        missingDataAlert.setContentText("You don't tug on Superman's cape, you don't spit into the wind;"
                                    + "\nyou don't pull the mask off that old Lone Ranger, and you don't"
                                    + "\ndo statistical calculations with missing information.  Let's try"
                                    + "\nto alleviate this rather, if I may say so, distressing lapse on"
                                    + "\nyour part, and then we shall proceed...");        
        missingDataAlert.showAndWait(); 
        showingAnAlert = false;
    }
    
    public static void showTFNotEditableAlert() { 
        showingAnAlert = true;
        tfNotEditableAlert = new Alert(AlertType.WARNING);   
        tfNotEditableAlert.setTitle("Warning! Non-editable field!");
        tfNotEditableAlert.setContentText("In your attempt to write the next Great American statistical"
                                    + "\nnovel you have attempted to edit a field that is not editable."
                                    + "\nThis means that you will have to go with what is there, no matter"
                                    + "\nhow much more graceful or maybe even correct your prose or numbers"
                                    + "\nwould be.  All great writers have editors.  Live with it.");        
     
        tfNotEditableAlert.showAndWait(); 
        showingAnAlert = false;
    }

    public static void showNonUniqueCategoriesAlert() { 
        showingAnAlert = true;
        nonUniqueCategoriesAlert = new Alert(AlertType.WARNING);   
        nonUniqueCategoriesAlert.setTitle("Warning! Non-unique collection of information!");
        nonUniqueCategoriesAlert.setHeaderText("Your 'category' names must be unique.");
        nonUniqueCategoriesAlert.setContentText("Ok, so here's the thing.  While there are many Henry's, there "
                                              + "\nwas -- thank goodness!!! -- only one Henry VIII, for which.  "
                                              + "\nBolyns everywhere should be thankful.  But, we digress.  You"
                                              + "\nneed to provide unigue names for categorical information."
                                              + "\nSimilar is Ok, same is not unlike less than good enough.");        
        nonUniqueCategoriesAlert.showAndWait(); 
        showingAnAlert = false;
    }
    
    public static void showNonSumToOneAlert() { 
        showingAnAlert = true;
        nonSumToOneAlert = new Alert(AlertType.CONFIRMATION);   
        nonSumToOneAlert.setTitle("Uh-oh, possible problem here...");
        nonSumToOneAlert.setHeaderText("There is a difficulty with your numbers.");
        nonSumToOneAlert.setContentText("The sum of your expected proportions is different from 1.0.  This could be"
                                     + "\ndue to rountoff error, in which case your proportions will be (slightly)"
                                     + "\nadjusted for the chi square calculations.  I your proportions are due to"
                                     + "\ndeeper doo-doo problms, you can fix them yourself.  Your call.");        
        nonSumToOneAlert.showAndWait(); 
        showingAnAlert = false;
    }
    
    public static boolean getShowingAnAlert() {return showingAnAlert; }
}
