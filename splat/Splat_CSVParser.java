/************************************************************
 *                                                          *
 *                      Splat_CSVParser                     *
 *                          05/17/18                        *
 *                            15:00                         *
 ***********************************************************/
package splat;

import java.util.ArrayList;

/*
 * Adapted by C. Olsen from O'Reilly, Java Cookbook, 3rd ed 
 */

public class Splat_CSVParser 
{

    public Splat_CSVParser(char sep) 
    {
        fieldSep = sep;
    }

    /** The fields in the current String */
    protected ArrayList<String> list = new ArrayList<>();

    /** the separator char for this parser */
    protected char fieldSep;

    /** parse: break the input String into fields
     * @param line
     * @return java.util.Iterator containing each field
     * from the original as a String, in order.
     */
    public ArrayList<String> parse(String line) 
    {
        StringBuffer sb = new StringBuffer();
        list.clear();            // recycle to initial state
        int i = 0;

        if (line.length() == 0) 
        {
            list.add(line);
            return list;
        }

        do {
            sb.setLength(0);
            if (i < line.length() && line.charAt(i) == '"')
                i = advQuoted(line, sb, ++i);    // skip quote
            else
                i = advPlain(line, sb, i);
            
            String sbString = sb.toString();
            list.add(sbString);
            // System.out.println("55, CSVParser, csv ***** = " + sbString);
            i++;
        } while (i < line.length());
        
        // System.out.println("59, CSVParser, List = " + list);
        
        return list;
    }

    /** advQuoted: quoted field; return index of next separator
     * @param s
     * @param sb
     * @param i
     * @return  */
    protected int advQuoted(String s, StringBuffer sb, int i)
    {
        int j;
        int len= s.length();
        for (j=i; j<len; j++) {
            if (s.charAt(j) == '"' && j+1 < len) 
            {
                if (s.charAt(j+1) == '"') 
                {
                    j++; // skip escape char
                } else if (s.charAt(j+1) == fieldSep) 
                { //next delimeter
                    j++; // skip end quotes
                    break;
                }
            } else if (s.charAt(j) == '"' && j+1 == len) 
            { // end quote @ line end
                break; //done
            }
            sb.append(s.charAt(j));    // regular character.
        }
        return j;
    }

    /** advPlain: unquoted field; return index of next separator
     * @param s
     * @param sb
     * @param i
     * @return  */
    protected int advPlain(String s, StringBuffer sb, int i)
    {
        int j;

        j = s.indexOf(fieldSep, i); // look for separator
        // System.out.println("csv: " + " i = " + i + ", j = " + j);
        if (j == -1) 
        {                   // none found
            sb.append(s.substring(i));
            return s.length();
        } else 
        {
            sb.append(s.substring(i, j));
            return j;
        }
    }
}