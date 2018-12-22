/************************************************************
 *                        ColumnOfData                      *
 *                          11/20/18                        *
 *                           00:00                          *
 ***********************************************************/
package genericClasses;

import java.util.ArrayList;
import java.util.Arrays;
import splat.*;

public class ColumnOfData {
    //  POJOs
    private boolean isNumeric = false; 
    private int nCasesInColumn, nCategorical, nReals, nMissing;
    
    private String varName, varUnits, varType, genericVarInfo,
                   varDisplayFormat;
    
    ArrayList<String> alStr_allTheCases, alStr_distinctValues;
    
    // My classes
    DataCleaner cleanThis;
    QuantitativeDataVariable cdv;
    Splat_DataManager dm;

    public ColumnOfData() { 
        alStr_allTheCases = new ArrayList<>(); 
        nCasesInColumn = 0;
        varName = "";
        varUnits = "";
        genericVarInfo = "*";
    } 

    public ColumnOfData (ColumnOfData dataColumn) {  // Copy constructor
        varName = dataColumn.getVarLabel();
        alStr_allTheCases = new ArrayList<>(); 
        nCasesInColumn = dataColumn.getColumnSize();
        isNumeric = dataColumn.getIsReal();
        varName = dataColumn.getVarLabel();
        varUnits = dataColumn.getVarUnits();
        genericVarInfo = "*"; 
        for (int ithCase = 0; ithCase < nCasesInColumn; ithCase++) {
            String textToAdd = dataColumn.getTextInIthRow(ithCase);
            alStr_allTheCases.add(textToAdd);
        } 
    }

    public ColumnOfData(Splat_DataManager dm, int nCasesInColumn, String varName) {
        this.dm = dm;
        this.nCasesInColumn = nCasesInColumn;
        alStr_allTheCases = new ArrayList<>();
        for (int iCase = 0; iCase < nCasesInColumn; iCase++) { 
            alStr_allTheCases.add("");
        }
        this.varName = varName;
        varUnits = "";
        isNumeric = true;
        genericVarInfo = "*";
    }

    public ColumnOfData(Splat_DataManager dm, int chosenVector) {
        this.dm = dm;
        varName = dm.getVariableName(chosenVector);
        nCasesInColumn = dm.getSpreadsheetColumn(chosenVector).getColumnSize();    
        isNumeric = dm.getVariableIsNumeric(chosenVector);
        alStr_allTheCases = new ArrayList<>();
        alStr_allTheCases = dm.getSpreadsheetColumn(chosenVector).getTheCases();
        varName = "";
        varUnits = "";
        genericVarInfo = "*";
    }

    public ColumnOfData(CategoricalDataVariable catDatVar) {
        nCasesInColumn = catDatVar.get_N();
        String daData[] = new String[nCasesInColumn];
        daData = catDatVar.getDataAsStrings();
        alStr_allTheCases = new ArrayList<>();
        for (int iCase = 0; iCase < nCasesInColumn; iCase++) { 
            alStr_allTheCases.add(daData[iCase]);
        }
        varName = catDatVar.getDataLabel();
        varUnits = "";
        isNumeric = false;
        genericVarInfo = "*";
    }

    
    public ColumnOfData(QuantitativeDataVariable qdv_Model) {
        nCasesInColumn = qdv_Model.getLegalN();
        alStr_allTheCases = new ArrayList<>();
        alStr_allTheCases = qdv_Model.getLegalCases_AsALStrings();
        varName = qdv_Model.getDataLabel();
        isNumeric = true; 
        varUnits = qdv_Model.getDataUnits();
        genericVarInfo = "*";
    }

    // Needed by the BivariateCatagoricalDataObj
    public ColumnOfData(String varName, ArrayList<String> theData) {
        this.nCasesInColumn = theData.size();
        alStr_allTheCases = new ArrayList<>();
        for (int iCase = 0; iCase < nCasesInColumn; iCase++) { 
            alStr_allTheCases.add(theData.get(iCase));
        }
        this.varName = varName;
        varUnits = "";
        isNumeric = true;
        genericVarInfo = "*";
    }
    

    public void setAssociatedDataManager(Splat_DataManager dm) {
        this.dm = dm;
    }
    
    public QuantitativeDataVariable makeANumericDataVariable() { 
        cdv = new QuantitativeDataVariable(this);        
        return cdv;
    }

    public void addNCasesOfThese(int nNewCases, String ofThese) {
        // System.out.println("116 cd, addNCasesOfThese(int nNewCases, String ofThese)");
        // System.out.println("117 cd, nCasesInCol = " + nCasesInColumn);
        // System.out.println("118 cd, adding " + nNewCases + " of these: ->" + ofThese + "<-");
        nCasesInColumn += nNewCases;
        for (int iNewCase = 0; iNewCase < nNewCases; iNewCase++) { 
            alStr_allTheCases.add(ofThese);
        }
        // System.out.println("123 cd, nCasesInCol = " + nCasesInColumn);
        // this.toString();
    }
 
    //*************************************************************************
    //*                                                                 *     *
    //* checkData operates under two possibilities.  First, the user might    *
    //* want to check a column from the data manager (fromDataManager = true) *
    //* while entering or editing data. Second, a user may have already chosen*
    //* a statistical process (e.g. ANOVA) and that process will make a final *
    //* check of data before executing (fromDataManager = false).             *
    //*                                                                       *
    //*                                                                       *
    //************************************************************************/
    
    public void assignDataType() {
        nCategorical = 0;
        nReals = 0;
        nMissing = 0;
        for (int ithCase = 0; ithCase < nCasesInColumn; ithCase++) {
            String thisElement = this.getTextInIthRow(ithCase);
            String thingy = getDataTypeOfThisElement(thisElement); 
            switch (thingy) {
                case "real":
                    nReals++;
                    //System.out.println("148 cd, reals so far: " + nReals);
                    break;
                case "categorical":
                    nCategorical++;
                    //System.out.println("152 cd, categoricals so far: " + nCategorical);
                    break;
                case "missing":
                    nMissing++;
                    // System.out.println("156, missing case = " + ithCase);
                    // System.out.println("157 cd, missing so far: " + nMissing);
                    break;
                    
                default:
                    System.out.println("Col of Data 161 -- Switch failure!!!");
                    System.exit(178);
            }
        }

        if (nReals >= 0.60 * nCasesInColumn) {
            varType = "real";
            
        } else 
        if (nCategorical >= 0.60 * nCasesInColumn) {
            varType = "categorical";          
        }
        else 
            varType = "???";
        
        //System.out.println("176 cd, nReals = " + nReals);
        //System.out.println("177 cd, nMissing = " + nMissing);
        //System.out.println("178 cd, nCategorical = " + nCategorical);
        
    } // assignDataType
    
/*
    public void setCleanedStrings(String[] externallyCleanedStrings) {
        int nCleanedStrings = externallyCleanedStrings.length;
        if (alStr_allTheCases.size() == nCleanedStrings) {
            for (int ithCleaned = 0; ithCleaned < nCleanedStrings; ithCleaned++) {
                alStr_allTheCases.set(ithCleaned, externallyCleanedStrings[ithCleaned]);
            }
        }
    }
*/
    // retrieve or store data
    public String getTextInIthRow(int ithRow) {
        String dataString = alStr_allTheCases.get(ithRow);
        if (dataString.equals(" ")) 
            {dataString = "*"; }
        return dataString;
    }


    public void setData(int row, String toThisValue) {
        alStr_allTheCases.set(row, toThisValue);  
    }
   
/*
    public void insertThisDataInThisRow(String data, int row) {
        alStr_allTheCases.add(row, data);
        nCasesInColumn++;
    }
    
    public void deleteDataInThisRow(int row) {
        alStr_allTheCases.remove(row);
        nCasesInColumn--;       
    }
*/
    public String getDataTypeOfThisColumn() { return varType; }
    
    public String getDataTypeOfThisElement(String dataElement) {
        //System.out.println("241 cd, dataElement = " + dataElement + ", len = " + dataElement.length());
        String theDataType = "";
        if (DataUtilities.stringIsADouble(dataElement) == true) {
            //System.out.println("244 cd, data is real");
            theDataType = "real";
            return theDataType;
        }
        else
        if ( dataElement.equals("categorical")) {
            //System.out.println("250 cd, data is categorical");
            return "categorical";
        }
        else {
            return "missing";
        }
    }        
    
    public ColumnOfData getColumnOfData() {return this; }
    
    public String getGenericVarInfo() { return genericVarInfo; }
    public void setGenericVarInfo(String toThisInfo) {
        genericVarInfo = toThisInfo;
    }
    
    public boolean getIsReal() {return isNumeric; }
    public void setIsReal(boolean isNumeric) {this.isNumeric = isNumeric; }

    public String getVarLabel () { return varName; }
    public void setVarLabel(String toThis) {varName = toThis; }
    
    public String getVarUnits () { return varUnits; }
    public void setVarUnits(String toThis) { varUnits = toThis; }
    
    public String getDisplayFormat() { return varDisplayFormat; }
    public void setDisplayFormat(String toThisFormat) {
        varDisplayFormat = toThisFormat;
    }

    public int getColumnSize() { return alStr_allTheCases.size(); }   

    public ArrayList<String> getTheCases() {
        return alStr_allTheCases; 
    }
   
    public ArrayList<String> getDistinctValues() {
        int tempInt;
        if (alStr_distinctValues == null)
            tempInt = findNumberOfDistinctValues();
        return alStr_distinctValues;
    }
    
    public int findNumberOfDistinctValues() {
        alStr_distinctValues = new ArrayList();
        int nCases = alStr_allTheCases.size();
        int nDistinct = 1;
        String[] tempData = new String[nCases];
        tempData[0] = alStr_allTheCases.get(0);

        for (int ith = 0; ith < nCases; ith++) {
            tempData[ith] = alStr_allTheCases.get(ith);
        }    
        
        Arrays.sort(tempData, 0, nCases - 1);
        for (int i = 1; i < nCases; i++) {
            if (!(tempData[i].equals(tempData[i - 1]))) {
                nDistinct++;
                alStr_distinctValues.add(tempData[i - 1]);
            }
        }

        return nDistinct;
    }  //countVals
    
    public int getNReals() { return nReals; }
    public int getNCategorical() { return nCategorical; }
    public int getNMissing() { return nMissing; }


    @Override
    public String toString() {  
        // Force an error here to see the stacktrace
        // int x = 5;
        // int y = 0;
        // int z = x / y;
        System.out.println("\nCol of Data -- toString");
        System.out.println("Var name = " + varName);
        System.out.println("isNumeric = " + isNumeric);
        System.out.println("nCasesInColumn = " + nCasesInColumn);
        for (int ithCase = 0; ithCase < nCasesInColumn; ithCase++){
            System.out.println(alStr_allTheCases.get(ithCase));
        }
        String taf = "That's All, Folks!!";
        return taf;  
    }
        
}   //  end class ColumnOfData
