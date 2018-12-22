/****************************************************************************
 *                       MyStringUtilities                                  * 
 *                            05/15/18                                      *
 *                             12:00                                        *
 ***************************************************************************/

package chiSquare;

public class MyX2StringUtilities {
    // POJOs
    boolean fieldSizeIsEven, stringSizeIsEven;
    int maxSpaces, spacesNeeded, fieldSize, stringSize, fieldSizeMod2, 
        stringSizeMod2, nLeftSpaces, nRightSpaces;
    
    String inText, outText;
    
    public MyX2StringUtilities() { }
    
    public String leftMostChars(String original, int leftChars)  {
        String truncated;
        if (original.length() < leftChars) {
            truncated = original + getStringOfNSpaces(leftChars - original.length());
        }
        else 
            {truncated = original.substring(0, leftChars); }

        return truncated;
    }

    public String centerTextInString(String s, int fieldSize)  {
        char pad = ' ';
        s = s.trim();   // Eliminate leading and trailing spaces
        if (s == null)
            return "   ";

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
    
    public String getStringOfNSpaces(int nSpaces) {
        String tempString = "";
        for (int iSpaces = 0; iSpaces < nSpaces; iSpaces++) 
            {tempString += " ";}
        return tempString;
    }
    
}


