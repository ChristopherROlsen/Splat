 /*************************************************
 *                  CatQuantPair                  *
 *                    05/16/18                    *
 *                      12:00                     *
 *************************************************/
package genericClasses;

public class CatQuantPair {
    // POJOs
    private int theQuantValueInt;
    private double theQuantValueDouble;
    private String theCatValue;
    
    public CatQuantPair(String theInCatValue, double quantValueDouble)  {
        theCatValue = theInCatValue;
        theQuantValueDouble = quantValueDouble;     
    }
    
    public CatQuantPair(String theInCatValue, int quantValueInt)  {
        theCatValue = theInCatValue;
        theQuantValueInt = quantValueInt;     
    }
    
    public CatQuantPair(String theInCatValue, String theInQuantValue)  {
        theCatValue = theInCatValue;
        theQuantValueDouble = Double.parseDouble(theInQuantValue);     
    }

    public CatQuantPair getCQP() {return this; }

    public String getCatValue() { return theCatValue;}
    public double getQuantValueInt() { return theQuantValueInt;}
    public double getQuantValueDouble() { return theQuantValueDouble;}
    public String getQuantValueAsString() { return String.valueOf(theQuantValueDouble);}

    public void setCatValue(String newCatValue) { theCatValue = newCatValue;}
    public void setQuantValueInt(int newQuantValue) { theQuantValueInt = newQuantValue;} 
    public void setQuantValueDouble(double newQuantValue) { theQuantValueDouble = newQuantValue;}   
}
