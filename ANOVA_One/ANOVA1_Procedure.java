/**************************************************
 *                ANOVA1_Procedure                *
 *                    12/14/18                    *
 *                     00:00                      *
 *************************************************/
// ************************************************
//    Called by Splat_Menu  -- Handles both       *
//              ANOVA and QANOVA                  *
// ************************************************

package ANOVA_One;

import genericClasses.CatQuantDataVariable;
import genericClasses.ColumnOfData;
import genericClasses.QuantitativeDataVariable;
import baseClasses.Distributions;
import dialogs.*;
import java.util.ArrayList;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

import splat.*;
import proceduresTwoUnivariate.*;

public class ANOVA1_Procedure {
    //  POJOs

    boolean ok;
    
    int n_QDVs; 
    String returnStatus, daProcedure;
    ArrayList<String> allTheLabels;
    
    // boolean stackedOrNot;
    
    // My classes
    ArrayList<ColumnOfData> anova1_ColsOfData;
    ArrayList<String> varLabel;
    ANOVA1_Model anova1Model;
    QANOVA1_Model qanova1Model;
    ANOVA1_Dashboard anova1Dashboard;
    QANOVA1_Dashboard qanova1Dashboard;
    CatQuantDataVariable cqdv;
    Explore_2Ind_PrepareStructs xplore_2Ind_Prep;
    Distributions prob;
    QuantitativeDataVariable tempQDV;
    ArrayList<QuantitativeDataVariable> allTheQDVs;
    Splat_DataManager dm;
    
    // POJOs / FX
    TextArea myText;

    public ANOVA1_Procedure(Splat_DataManager dm, String daProcedure) {
        this.dm = dm;
        anova1_ColsOfData = new ArrayList();
        varLabel = new ArrayList();
        this.daProcedure = daProcedure;
        returnStatus = "OK";
        // doStackedOrNot();
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

    private void doStacked() {
        ANOVA1_Stacked_Dialog anova1_S_Dialog = new ANOVA1_Stacked_Dialog( dm );
        anova1_ColsOfData = anova1_S_Dialog.getData();
  
        //                                Categorical,             Quantitative
        cqdv = new CatQuantDataVariable(dm, anova1_ColsOfData.get(0), anova1_ColsOfData.get(1));    
        allTheQDVs = new ArrayList();
        allTheQDVs = cqdv.getAllQDVs();

        prepareTheStructs();
    }

    private String doNotStacked() {
        returnStatus = "Ok";
        ANOVA1_NotStacked_Dialog anova1_NS_Dialog = new ANOVA1_NotStacked_Dialog( dm );
        anova1_NS_Dialog.show_ANOVA1_NS_Dialog();
        returnStatus = anova1_NS_Dialog.getReturnStatus();
        System.out.println("118 notStacked, returnStatus = " + returnStatus);
        if (!returnStatus.equals("Ok")) 
            { return returnStatus; }
        // else...
        anova1_ColsOfData = anova1_NS_Dialog.getDataSet();
         
         int nColumnsOfData = anova1_ColsOfData.size();
        // Stack the columns into one, put in allTheQDVs[0]
        // Construct a ColumnOfData, make the QDV
        String varName = "All";
        ArrayList<String> tempAlStr = new ArrayList<>();
        for (int ith = 0; ith < nColumnsOfData; ith++) {
            ColumnOfData tempCol = anova1_ColsOfData.get(ith);
            int nColSize = tempCol.getColumnSize();
            for (int jth = 0; jth < nColSize; jth++) {
                tempAlStr.add(tempCol.getTheCases().get(jth));
            }
        }
        ColumnOfData tempCOD = new ColumnOfData(varName, tempAlStr);
        tempQDV = new QuantitativeDataVariable(tempCOD);

        allTheQDVs = new ArrayList();
        allTheQDVs.add(tempQDV);

        for (int ith = 0; ith < nColumnsOfData; ith++) {
            tempQDV = new QuantitativeDataVariable(anova1_ColsOfData.get(ith));  
            allTheQDVs.add(tempQDV);                 
        } 
        
        prepareTheStructs();
        return returnStatus;
    }
    
    private String prepareTheStructs() {
        System.out.println("148 ANOVA_Proc, daProcedure = " + daProcedure);
        switch (daProcedure) {
            case "ANOVA1":
                n_QDVs = allTheQDVs.size();
                allTheLabels = new ArrayList<>();
                for (int iVars = 0; iVars < n_QDVs; iVars++) {
                    allTheLabels.add(allTheQDVs.get(iVars).getTheDataLabel());
                }

                // The QDVs already have labels -- dump allTheLabels & get from QDV?
                anova1Model = new ANOVA1_Model(this, "ExplanVar", "ResponseVar", allTheQDVs, allTheLabels); 
                anova1Dashboard = new ANOVA1_Dashboard(this, anova1Model);
                anova1Dashboard.populateTheBackGround();
                anova1Dashboard.putEmAllUp();
                anova1Dashboard.showAndWait();
                returnStatus = anova1Dashboard.getReturnStatus();
                returnStatus = "Ok";
                break;
                
            case "QANOVA1":
                n_QDVs = allTheQDVs.size();
                allTheLabels = new ArrayList<>();
                for (int iVars = 0; iVars < n_QDVs; iVars++) {
                    allTheLabels.add(allTheQDVs.get(iVars).getTheDataLabel());
                }

                // The QDVs already have labels -- dump allTheLabels & get from QDV?
                qanova1Model = new QANOVA1_Model(this, "ExplanVar", "ResponseVar", allTheQDVs, allTheLabels); 
                qanova1Dashboard = new QANOVA1_Dashboard(this, qanova1Model);
                qanova1Dashboard.populateTheBackGround();
                qanova1Dashboard.putEmAllUp();
                qanova1Dashboard.showAndWait();
                returnStatus = qanova1Dashboard.getReturnStatus();
                returnStatus = "Ok";
                break;
            default:
                System.out.println("184 ANOVA1_Proc, daProcedure not supported");
                System.out.println("185 ANOVA1_Proc, daProcedure = " + daProcedure);
                returnStatus = "NotOK";
                break;
        }
        
        return returnStatus;
        
    } // OneFactor CR
    
    public String getReturnStatus() { return returnStatus; }

}