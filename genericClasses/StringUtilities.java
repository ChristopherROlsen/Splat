/****************************************************************************
 *                       MyStringUtilities                                  * 
 *                            12/22/18                                      *
 *                             12:00                                        *
 ***************************************************************************/

package genericClasses;

import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.control.TextField;

public class StringUtilities {
    
    public StringUtilities() { }

    public static String centerTextInString(String s, int fieldSize)  {
        char pad = ' ';
        s = s.trim();   // Eliminate leading and trailing spaces
        if (s == null || fieldSize <= s.length())
            return s;

        StringBuilder sb = new StringBuilder(fieldSize);
        for (int i = 0; i < (fieldSize - s.length()) / 2; i++) {
            sb.append(pad);
        }
        sb.append(s);
        while (sb.length() < fieldSize) {
            sb.append(pad);
        }
        return sb.toString();
    }     
    
    public static String getStringOfNSpaces(int nSpaces) {
        String tempString = "";
        for (int iSpaces = 0; iSpaces < nSpaces; iSpaces++) 
            {tempString += " ";}
        return tempString;
    }
    
    public static String reverseStringCharacters( String toBeReversed) {
        String reversedString;  
        char[] stringAsChars = toBeReversed.toCharArray();
        Arrays.sort(stringAsChars);
        
        int leefsLength = stringAsChars.length;
        char[] revleefs = new char[leefsLength];

        for (int ithLeef = 0; ithLeef < leefsLength; ithLeef++) {
            revleefs[ithLeef] = stringAsChars[leefsLength - ithLeef - 1];
        }
        System.arraycopy(revleefs, 0, stringAsChars, 0, leefsLength);                    
        reversedString = String.valueOf(stringAsChars);     
        return reversedString;
    }
    
    public static String truncateString(String inpString, int maxLength) {
        int len = inpString.length();
        String temp = inpString;
        if (len > maxLength) {
            temp = inpString.substring(0, maxLength);
        }
        return temp;
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
    
    public static boolean check4PosReal(String allegedPosReal) {
        double tempReal = 0;
        boolean posRealQuery = true;
        String tempText = allegedPosReal;
        try {
            tempReal = Double.valueOf(tempText);
        } catch (NumberFormatException e) {
            posRealQuery = false;
        }
        
        if (tempReal < 0)
            posRealQuery = false;
        return posRealQuery;
    }
    
    public static double[] convert_alStr_to_alDoubles (ArrayList<String> alString) {
        String tempString;
        int nDataPoints = alString.size();
        double[] alDoubles = new double[nDataPoints];
        for (int ith = 0; ith < nDataPoints; ith++) {
            tempString = alString.get(ith);
            boolean doubleIsOK = DataUtilities.stringIsADouble(tempString);
            if (doubleIsOK == true) {
                alDoubles[ith] = Double.valueOf(tempString);
            } else {
                System.out.println("Unclean Data! Conversion error in DataUtilities!!");
                System.exit(241);
            }       
        }
        return alDoubles;
    }

    public static double[] convert_arrayStr_to_arrayDoubles (String[] arrayOfStrings) {
        String tempString;
        int nDataPoints = arrayOfStrings.length;
        double[] alDoubles = new double[nDataPoints];
        for (int ith = 0; ith < nDataPoints; ith++) {
            tempString = arrayOfStrings[ith];
            boolean doubleIsOK = DataUtilities.stringIsADouble(tempString);
            if (doubleIsOK == true) {
                alDoubles[ith] = Double.valueOf(tempString);
            } else {
                System.out.println("Unclean Data! Conversion error in DataUtilities!!");
                System.exit(258);
            }       
        }
        return alDoubles;
    }
    
    public static Double convertStringToDouble( String fromThis) {
        Double toThis = Double.parseDouble(fromThis);
        return toThis;
    }
    
    public static int TextFieldToPrimitiveInt(TextField theTF) {
        String strTheText = theTF.getText();
        int tempInt = Integer.parseInt(strTheText);  
        return tempInt;
    }    
}

