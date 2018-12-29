/**************************************************
 *                ANOVA1_Procedure                *
 *                    12/26/18                    *
 *                     18:00                      *
 *************************************************/
// ***********************************************************
//    Sublcasses:                                            *
//     ANOVA1_Categorical_Procedure, ANOVA1_QUANT_Procedure  *
// ***********************************************************

package ANOVA_One;

import dataObjects.CatQuantDataVariable;
import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import superClasses.Distributions;
import java.util.ArrayList;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

import splat.*;

public class ANOVA1_Procedure {
    //  POJOs

    boolean ok;
    boolean checkForLegalChoices;
    int n_QDVs; 
    String returnStatus, daProcedure;
    String explanatoryVariable, responseVariable, subTitle;
    ArrayList<String> allTheLabels;
 
    // My classes
    ArrayList<ColumnOfData> anova1_ColsOfData;
    ArrayList<String> varLabel;
    CatQuantDataVariable cqdv;
    Distributions prob;
    QuantitativeDataVariable tempQDV;
    ArrayList<QuantitativeDataVariable> allTheQDVs;
    Data_Manager dm;
    
    // POJOs / FX
    TextArea myText;

    public ANOVA1_Procedure(Data_Manager dm) {
        this.dm = dm;
        anova1_ColsOfData = new ArrayList();
        varLabel = new ArrayList();
        returnStatus = "OK";
    }
    
    public void doStackedOrNot() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Ok, so I have a question about your data source...");
        alert.setHeaderText("You need to make a decision.");
        String stackQuestion = "Is your data (a) stacked in two columns, one for groups/treatments " + 
                               "\n and one for data values, (b) all in separate columns, or (c) " +
                               "\n in summary form with means, standard deviations, and sample sizes?";
        alert.setContentText(stackQuestion);
        ButtonType buttonTypeStacked = new ButtonType("Stacked");
        ButtonType buttonTypeSeparate = new ButtonType("Separate");
        ButtonType buttonTypeSummary = new ButtonType("Summary");
        ButtonType buttonTypeCancel  = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeStacked, buttonTypeSeparate, buttonTypeSummary, buttonTypeCancel);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeStacked) {
            doStacked();
        }
        else {
            if (result.get() == buttonTypeSeparate) {
                doNotStacked();

            }
            else {
                if (result.get() == buttonTypeSummary) {
                    // No op

                }
                else { /* User canceled or closed the dialog */
                           /*  Need to return something here     */
                }
            }
            
        }       
    }
    
    protected String doStacked() { return "This is a string."; }
    
    protected String doNotStacked() { return "This is a string."; }
    
    protected String prepareTheStructs() { return "This is a string."; }
    
    public String getExplanatoryVariable() { return explanatoryVariable; }
    public String getResponseVariable() { return responseVariable; }
    public String getSubTitle() { 
        String subTitle = responseVariable + " vs. " + explanatoryVariable; 
        return subTitle;
    }
    
    public String getReturnStatus() { return returnStatus; }

}