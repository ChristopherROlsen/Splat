/************************************************************
 *                  Transformations_Calculations            *
 *                          08/12/18                        *
 *                            21:00                         *
 ***********************************************************/
/************************************************************
 *  Note: Not all calculations below take string arrays.    *
 *        Will add these as sloth diminishes.  New methods  *
 *        should be able to mimic unaryOpsOfVars            *
 *        Current functions that take string arrays:        *
 *           -- unaryOpsOfVars                              *  
 ***********************************************************/

package genericClasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Transformations_Calculations {
    // POJOs
    int nOriginalDataPoints;
    double tempDouble;
    ArrayList<Double> alDouble_AllTheData;
    final String missingData;
    String[] strTransformedData;
    ArrayList<String> alStr_Var_1_Data, alStr_Var_2_Data, tempAlString, 
                      alStr_AllTheData, alStr_TheLegalData;
    
    // My classes
    NormalScores normScores;
    QuantitativeDataVariable qdv;
    
   public Transformations_Calculations() { 
        missingData = "*";
   }
   
   public String[] linearTransformation(ArrayList<String> alStr_Var_1_Data, double alphaValue, double betaValue) {
        nOriginalDataPoints =  alStr_Var_1_Data.size();
        strTransformedData = new String[nOriginalDataPoints];
        for (int dataPoint = 0; dataPoint < nOriginalDataPoints; dataPoint++) {
            String strDataPoint = alStr_Var_1_Data.get(dataPoint);

            if (strDataPoint.equals(missingData)) {
                strTransformedData[dataPoint] = missingData;
            } else {
                Double dblDataPoint = Double.parseDouble(strDataPoint);
                double reScaled = alphaValue + betaValue * dblDataPoint;
                strTransformedData[dataPoint] = String.valueOf(reScaled );
            }   
        }
        return strTransformedData; 
   }
   
    public String[] linTransWithFunc(ArrayList<String> alStr_Var_1_Data, 
                                     String chosenProcedure,
                                     double alphaValue, double betaValue) {
        double tempDouble;
        nOriginalDataPoints =  alStr_Var_1_Data.size();
        strTransformedData = new String[nOriginalDataPoints];
        for (int dataPoint = 0; dataPoint < nOriginalDataPoints; dataPoint++) {
            String strDataPoint = alStr_Var_1_Data.get(dataPoint);
            if (strDataPoint.equals(missingData)) {
                strTransformedData[dataPoint] = missingData;
            } else {
                Double dblDataPoint = Double.parseDouble(strDataPoint);
                if(chosenProcedure.equals("ln") && (dblDataPoint > 0.0))  {
                    tempDouble = alphaValue + betaValue * Math.log(dblDataPoint);
                    strTransformedData[dataPoint] = String.valueOf(tempDouble);
                }
                else if(chosenProcedure.equals("log10") && (dblDataPoint > 0.0))  {
                    tempDouble = alphaValue + betaValue * Math.log10(dblDataPoint);                
                        strTransformedData[dataPoint] = String.valueOf(tempDouble);
                }
                else if(chosenProcedure.equals("sqrt") && (dblDataPoint >= 0.0))  {
                    tempDouble = alphaValue + betaValue * Math.sqrt(dblDataPoint);                
                        strTransformedData[dataPoint] = String.valueOf(tempDouble);
                }
                else if(chosenProcedure.equals("recip") && (dblDataPoint != 0.0))  {
                        tempDouble = alphaValue + betaValue / dblDataPoint;                
                        strTransformedData[dataPoint] = String.valueOf(tempDouble);
                }   
                else if(chosenProcedure.equals("exp10")) {
                        tempDouble = alphaValue + betaValue * Math.pow(10.0, dblDataPoint);
                        strTransformedData[dataPoint] = String.valueOf(tempDouble);
                }    
                else if(chosenProcedure.equals("exp"))  {
                        tempDouble = alphaValue + betaValue * Math.exp(dblDataPoint);
                        strTransformedData[dataPoint] = String.valueOf(tempDouble);
                }
                else 
                    strTransformedData[dataPoint] = "*";                
            }             
        }
        return strTransformedData;
    }
    
    public String[] unaryOpsOfVars(double[]  double_1_Data,
                                   String chosenProcedure)    {
       
       tempAlString = convertArrayOfDouble_To_alStrArrayList(double_1_Data);
       
       return unaryOpsOfVars(tempAlString, chosenProcedure);   
   } 
    

   public String[] unaryOpsOfVars(String[]  strVar_1_Data,
                                   String chosenProcedure)    {
       
       tempAlString = convertStrArray_To_alStrArrayList(strVar_1_Data);
       
       return unaryOpsOfVars(tempAlString, chosenProcedure);   
   } 

   public String[] unaryOpsOfVars(ArrayList<String>  alStr_Var_1_Data,
                                   String uOpProcedure) {
        alStr_Var_1_Data.toString();
        String strDataPoint;
        nOriginalDataPoints =  alStr_Var_1_Data.size();
        strTransformedData = new String[nOriginalDataPoints];
        qdv = new QuantitativeDataVariable("xxx", alStr_Var_1_Data);
        int sizeofColumn, nLegalDoubles;
        switch (uOpProcedure) {

            case "percentile":
                for (int dataPoint = 0; dataPoint < nOriginalDataPoints; dataPoint++) {
                    strDataPoint = alStr_Var_1_Data.get(dataPoint);
                    if (strDataPoint.equals(missingData)) {
                        strTransformedData[dataPoint] = missingData;
                    } else { 
                        Double dblDataPoint = Double.parseDouble(strDataPoint);
                        strTransformedData[dataPoint] = String.valueOf(qdv.getIthPercentile(dblDataPoint));
                    }                   
                }
                break;
                
            case "z-score":
                double mean, stDev, zScore;
                mean = qdv.getTheMean();
                stDev = qdv.getTheStandDev();
                for (int dataPoint = 0; dataPoint < nOriginalDataPoints; dataPoint++) {
                   strDataPoint = alStr_Var_1_Data.get(dataPoint);
                    if (strDataPoint.equals(missingData)) {
                        strTransformedData[dataPoint] = missingData;
                    } else {
                    Double dblDataPoint = Double.parseDouble(strDataPoint);
                    tempDouble = dblDataPoint;   //  Unbox
                }
                    zScore = (tempDouble - mean) / stDev;
                    strTransformedData[dataPoint] = String.valueOf(zScore);
                }
                break;
                
            case "rank":
                boolean endOfStory;
                int startOfTie, endOfTie;
                Double rank, daRank;
                // var1_Data is an arrayList of Strings;
                sizeofColumn = alStr_Var_1_Data.size();
                nLegalDoubles = qdv.get_nDataPointsLegal();
                for (int ithOrigData = 0; ithOrigData < sizeofColumn; ithOrigData++ ) {
                    
                }
                alDouble_AllTheData = new ArrayList<>();
                alDouble_AllTheData = qdv.getLegalCases_AsALDoubles();
                alStr_TheLegalData = qdv.getLegalCases_AsALStrings();
                Collections.sort(alDouble_AllTheData);
                Map<String, Double> mapStringsToDoubles = new HashMap<>();
                Map<Double, Double> mapDoublesToRanks = new HashMap<>();
                
                for (int ithOrigData = 0; ithOrigData < nLegalDoubles; ithOrigData++ ) {
                    mapStringsToDoubles.put(alStr_TheLegalData.get(ithOrigData), alDouble_AllTheData.get(ithOrigData));
                }   
                
                startOfTie = 0;    //  Start process at first number;
                endOfTie = 0;      // subscript is as in ArrayList
                endOfStory = false;

                do {
                    for (int askIfTie = startOfTie; askIfTie < nLegalDoubles; askIfTie++) {
                        if (alDouble_AllTheData.get(askIfTie) <= alDouble_AllTheData.get(startOfTie)) {
                            endOfTie = askIfTie;
                        }
                    }

                    rank = (startOfTie + 1 + endOfTie + 1) / 2.0;
                    for (int daTies = startOfTie; daTies <= endOfTie; daTies++) {
                        mapDoublesToRanks.put(alDouble_AllTheData.get(daTies), rank);
                    }
                    
                    startOfTie = endOfTie + 1;
                    endOfTie = startOfTie;
                    if (endOfTie == nLegalDoubles)
                        endOfStory = true;      
                }   while (endOfStory == false);
                
                System.out.println(mapStringsToDoubles.entrySet());
                System.out.println(mapDoublesToRanks.entrySet());
                
            for (int ithRanked = 0; ithRanked < nOriginalDataPoints; ithRanked ++) {
                String tempString = qdv.getIthDataPtAsString(ithRanked);
                if (mapStringsToDoubles.containsKey(tempString)) {
                    tempDouble = Double.parseDouble(tempString);
                    daRank = mapDoublesToRanks.get(tempDouble);
                    String daString = String.valueOf(daRank);
                    strTransformedData[ithRanked] = String.valueOf(daString);
                } else {
                strTransformedData[ithRanked] = "*";
                }
            }
                break;
                
            case "rankits":    //  For normal probability plot; 
                               //  Using rankits (used in qqnorm in R)
                // System.out.println("216 T_C, doing Rankits");
                // ?????????  Can this be simplified  ???????????????
                alStr_TheLegalData = new ArrayList<>();
                alStr_TheLegalData = qdv.getLegalCases_AsALStrings();
                sizeofColumn = alStr_Var_1_Data.size();
                nLegalDoubles = qdv.get_nDataPointsLegal();
                double[] dblLegalCases = new double[nLegalDoubles];
                double[] dblSortedLegalCases = new double[nLegalDoubles];
                double[] ns = new double[nLegalDoubles];
                for (int ith = 0; ith < nLegalDoubles; ith++) {
                    dblLegalCases[ith] = Double.parseDouble(alStr_TheLegalData.get(ith));
                }
                System.arraycopy(dblLegalCases, 0, dblSortedLegalCases, 0, dblLegalCases.length);
                Arrays.sort(dblSortedLegalCases);                
                normScores = new NormalScores();
                ns = normScores.getNormalScores(nLegalDoubles);
                
                for (int ithOriginalPoint = 0; ithOriginalPoint < nOriginalDataPoints; ithOriginalPoint ++) {
                    String tempString = qdv.getIthDataPtAsString(ithOriginalPoint);
                    if (tempString.equals(missingData)) {
                        strTransformedData[ithOriginalPoint] = "*";
                    } else {
                        double tempDouble = qdv.getIthDataPtAsDouble(ithOriginalPoint);
                        for (int jth = 0; jth < nLegalDoubles; jth++)
                        {
                            if (tempDouble == dblSortedLegalCases[jth]) {
                                strTransformedData[ithOriginalPoint] = String.valueOf(ns[jth]);
                                break;
                            }
                        }
                    }
                }            
                break;                
                
            default:
                System.out.println("Ack!!! Fault at TC-213");
                System.exit(214);
                break;
        }
        return strTransformedData; 
   }

    
   public String[] binaryOpsOfVars(ArrayList<String>  alStr_Var_1_Data,
                                   String binaryOperation,
                                   ArrayList<String>  alStr_Var_2_Data) {
        nOriginalDataPoints =  alStr_Var_1_Data.size();
        strTransformedData = new String[nOriginalDataPoints];
        for (int dataPoint = 0; dataPoint < nOriginalDataPoints; dataPoint++) {
            String strDataPoint_1 = alStr_Var_1_Data.get(dataPoint);
            String strDataPoint_2 = alStr_Var_2_Data.get(dataPoint);
            if (strDataPoint_1.equals(missingData)  
                    || (strDataPoint_2.equals(missingData))) {
                strTransformedData[dataPoint] = missingData;
            } else {
                Double dblDataPoint_1 = Double.parseDouble(strDataPoint_1);
                Double dblDataPoint_2 = Double.parseDouble(strDataPoint_2);
                if(binaryOperation.equals("+"))  {
                    tempDouble = dblDataPoint_1 + dblDataPoint_2;
                    strTransformedData[dataPoint] = String.valueOf(tempDouble);
                }
                else if(binaryOperation.equals("-"))  {
                    tempDouble = dblDataPoint_1 - dblDataPoint_2;
                    strTransformedData[dataPoint] = String.valueOf(tempDouble);
                }
                else if(binaryOperation.equals("*"))  {
                    tempDouble = dblDataPoint_1 * dblDataPoint_2;
                    strTransformedData[dataPoint] = String.valueOf(tempDouble);
                }
                else if ((binaryOperation.equals("/") && (dblDataPoint_2 != 0.0)))  {
                    tempDouble = dblDataPoint_1 / dblDataPoint_2;
                    strTransformedData[dataPoint] = String.valueOf(tempDouble);
                }
                else
                    strTransformedData[dataPoint] = "*";
            }   
        }
        System.out.println("294 t_GUI, strTransformedData = " + strTransformedData);
        return strTransformedData; 
   }
   
   public String[] linearCombinationOfVars(ArrayList<String>  alStr_Var_1_Data, 
                                     ArrayList<String>  alStr_Var_2_Data, 
                                     double alphaValue, 
                                     double betaValue) {
        nOriginalDataPoints =  alStr_Var_1_Data.size();
        strTransformedData = new String[nOriginalDataPoints];
        for (int dataPoint = 0; dataPoint < nOriginalDataPoints; dataPoint++) {
            String strDataPoint_1 = alStr_Var_1_Data.get(dataPoint);
            String strDataPoint_2 = alStr_Var_2_Data.get(dataPoint);
            if (strDataPoint_1.equals(missingData)  
                    || (strDataPoint_2.equals(missingData))) {
                strTransformedData[dataPoint] = missingData;
            } else {
                Double dblDataPoint_1 = Double.parseDouble(strDataPoint_1);
                Double dblDataPoint_2 = Double.parseDouble(strDataPoint_2);
                double reScaled = alphaValue * dblDataPoint_1 
                                    + betaValue * dblDataPoint_2;
                strTransformedData[dataPoint] = String.valueOf(reScaled );
            }   
        }
        return strTransformedData; 
   }
   
   private ArrayList<String> convertArrayOfDouble_To_alStrArrayList(double[] arrayOfDoubles) {
        ArrayList<String> alOfStrs = new ArrayList<>();
        for (int ith = 0; ith < arrayOfDoubles.length; ith++) {
            alOfStrs.add(String.valueOf(arrayOfDoubles[ith]));
        }
        return alOfStrs;
   }
   
   private ArrayList<String> convertStrArray_To_alStrArrayList(String[] arrayOfStrings) {
        ArrayList<String> alOfStrs = new ArrayList<>();
        alOfStrs.addAll(Arrays.asList(arrayOfStrings));
        return alOfStrs;
   }
    
}
