/**************************************************
 *                  MyX2Utilities                 *
 *                    05/15/18                    *
 *                     12:00                      *
 *************************************************/

/**************************************************
 *                     ToDo                       *
 *     Make this a singleton pattern              *
 *************************************************/ 

package chiSquare;

import javafx.scene.control.TextField;

public class MyX2Utilities {
    
    public MyX2Utilities() { }
    
    public static boolean check4Double(String allegedDouble) {
        double tempDouble;
        boolean doubleQuery = true;
        String tempText = allegedDouble;
        try {
            tempDouble = Double.valueOf(tempText);
        } catch (NumberFormatException e) {
            doubleQuery = false;
        }
        return doubleQuery;
    }
    
    public static boolean check4Integer(String allegedInteger) {
        int tempInteger;
        boolean integerQuery = true;
        String tempText = allegedInteger;
        try {
            tempInteger = Integer.valueOf(tempText);
        } catch (NumberFormatException e) {
            integerQuery = false;
        }
        return integerQuery;
    }
    
    public static boolean check4NonNegInteger(String allegedInteger) {
        int tempInteger = 0;
        boolean nonNegIntegerQuery = true;
        String tempText = allegedInteger;
        try {
            tempInteger = Integer.valueOf(tempText);
        } catch (NumberFormatException e) {
            nonNegIntegerQuery = false;
        }
        
        if (tempInteger < 0)
            nonNegIntegerQuery = false;
        return nonNegIntegerQuery;
    }
    
    public static boolean check_TextField_4Blanks(TextField tf) {
        boolean okToContinue = true;
        String temp = new String(tf.getText());
        if (temp.trim().equals("")) 
            okToContinue = false;
        return okToContinue;
    } 
    
    // ****************  For Debugging Only !!!!!!!!!!!!!!!!!!!
    
    public void printTheseTwoNumbers(String whatever, double num1, double num2) {
        System.out.println(whatever + " " + num1 + " / " + num2);
    }
}


