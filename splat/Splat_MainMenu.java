/************************************************************
 *                       Splat_MainMenu                     *
 *                          11/22/18                        *
 *                            00:00                         *
 ***********************************************************/

// **********************************************************
// To do:                                                   *
//         Create dialogs for delete row / col              *
// **********************************************************
package splat;

import regressionMultiple.MultRegression_Procedure;
import regressionSimple.Regression_Procedure;
import genericClasses.Transformations_GUI;
import proceduresOneUnivariate.Exploration_Procedure;
import dialogs.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import proceduresTwoUnivariate.*;
import chiSquare.*;
import ANOVA_One.*;
import ANOVA_Two.*;
import z_procedures.*;
import t_Procedures.*;
import regressionLogistic.*;

public class Splat_MainMenu extends MenuBar {
    //  POJOs
    boolean ok;
    
    String procedure, returnStatus;
    final String ESCAPE = "ESCAPE";
    final String EXPERIMENT = "EXPERIMENT";
    final String GOF = "GOF";
    final String INDEPENDENCE = "INDEPENDENCE";
    final String HOMOGENEITY = "HOMOGENEITY";
    
    // My classes
    private final MyDialogs splatDialogs;
    Splat_DataManager dm;
    Splat_FileOps myFileOps;
    Splat_EditOps myEditOps;
    PositionTracker tracker;

    Splat_MainMenu(Application splat, Splat_DataManager dm, Label fileLabel) {

        this.dm = dm;
        myFileOps = new Splat_FileOps(this.dm);
        myEditOps = new Splat_EditOps(this.dm);
        
        int visVars = dm.getMaxVisVars();
        int visCases = dm.getMaxVisCases();
        tracker = new PositionTracker(dm, visVars, visCases);
        tracker.setNVarsInStruct(0);
        
        Menu fileMenu = new Menu("File");
        MenuItem clearData = new MenuItem("Clear Data");
        MenuItem getData = new MenuItem("Open File");
        MenuItem addData = new MenuItem("Add Variables");
        MenuItem saveData = new MenuItem("Save Data");
        MenuItem saveAsData = new MenuItem("Save Data As");
        MenuItem exitProgram = new MenuItem("Exit");
        fileMenu.getItems().addAll(clearData, getData, addData, saveData,
                saveAsData, exitProgram);

        Menu editMenu = new Menu("Edit");
        MenuItem insObs = new MenuItem("Insert an Observation");
        MenuItem addCol = new MenuItem("Add a Column");
        MenuItem delUnstacked = new MenuItem("Delete Unstacked variables");
        MenuItem delRow = new MenuItem("Delete a Row");
        MenuItem delCol = new MenuItem("Delete a Column");
        MenuItem cleanCol = new MenuItem("Clean Column data");

        editMenu.getItems().addAll(insObs, addCol, delUnstacked, 
                                   delRow, delCol, cleanCol);

        Menu dataMenu = new Menu("Data");
        MenuItem linTransOfVar = new MenuItem("Linear transformation");
        MenuItem nonlinTransOfVar = new MenuItem("Nonlinear transformation");
        MenuItem linearCombOfVars = new MenuItem("Linear combination");
        MenuItem unaryOperationsWithVars = new MenuItem("Unary operations");
        MenuItem binaryOperationsWithVars = new MenuItem("Binary operations");
        dataMenu.getItems().addAll(linTransOfVar,nonlinTransOfVar,linearCombOfVars,
                unaryOperationsWithVars, binaryOperationsWithVars);
        
        Menu statsMenu = new Menu("Analyze");
        MenuItem explore2IndData = new MenuItem("Explor 2 Ind Data");
        MenuItem exploreUnivariateData = new MenuItem("Explore Univ Data");
        statsMenu.getItems().addAll(exploreUnivariateData, explore2IndData);

        Menu twoSamp = new Menu("Tests for One or Two Samples");
        MenuItem runOneSamp = new MenuItem("Z and T Test for One Sample");
        MenuItem runInd_ttest = new MenuItem("T Test for Independent Groups");
        MenuItem runDttest = new MenuItem("T Test for Related/Paired Groups");
        twoSamp.getItems().addAll(runOneSamp, runInd_ttest, runDttest);
        statsMenu.getItems().add(twoSamp);

        Menu anova = new Menu("Analysis of Variance");
        MenuItem anova_CRD = new MenuItem("One-Factor ANOVA for "
                + "Completely Randomized Design");
        MenuItem runRCBanova = new MenuItem("One-Factor ANOVA for "
                + "Randomized (Complete) Block Design");
        MenuItem runCR2anova = new MenuItem("Two-Factor ANOVA for "
                + "Completely Randomized Design");
        MenuItem runRepeatedanova = new MenuItem("Repeated measures ANOVA for "
                + "One variable");
        anova.getItems().addAll(anova_CRD, runRCBanova, runCR2anova, runRepeatedanova);
        statsMenu.getItems().add(anova);
        
        Menu corrReg = new Menu("Regression Analysis");
        MenuItem runSimpleReg = new MenuItem("Linear Regression");
        MenuItem runMultReg = new MenuItem("Multiple Regression");
        MenuItem runLogistic = new MenuItem("Logistic regression");
        corrReg.getItems().addAll(runSimpleReg, runMultReg, runLogistic);
        statsMenu.getItems().add(corrReg);
        
        Menu chiSqr = new Menu("Analysis of Frequency or Proportion");
        MenuItem chiSquareRawCounts = new MenuItem("Chi square: table entry");
        MenuItem chiSquareFileData = new MenuItem("Chi square: data in file");
        MenuItem runProp1 = new MenuItem("One-Sample Proportion Test");
        MenuItem runProp2 = new MenuItem("Two-Sample Proportion Test");
        chiSqr.getItems().addAll(chiSquareRawCounts, chiSquareFileData, runProp1, runProp2);
        statsMenu.getItems().add(chiSqr);

        Menu probMenu = new Menu("Probability");
        MenuItem statCalc = new MenuItem("Statistical Probabilities, Critical Values, and Power");
        MenuItem statsTables = new MenuItem("Statistical Tables");
        MenuItem binom = new MenuItem("Create Binomial Distribution");
        probMenu.getItems().addAll(statCalc, statsTables, binom);
       
        this.getMenus().addAll(fileMenu, editMenu, dataMenu, statsMenu,
                probMenu);

        splatDialogs = new MyDialogs();
        clearData.setOnAction((ActionEvent event) -> {
            dm.getDataGrid().goHome(); // <------------- 
            myFileOps.ClearTable();
 
            fileLabel.setText("File: " + dm.getFileName());
        });

        // **************************************************************
        // *                    File Menu                               *
        // **************************************************************
        
        getData.setOnAction((ActionEvent event) -> {
            myFileOps.ClearTable();
            myFileOps.getDataFromFile(0);
            fileLabel.setText("File: " + dm.getFileName());
            dm.sendDataStructToGrid();
            dm.setDataIsClean(true);
        });
        
        saveData.setOnAction((ActionEvent event) -> {
            myFileOps.SaveData(dm, false);
            dm.setDataIsClean(true);
            fileLabel.setText("File: " + dm.getFileName());
        });

        saveAsData.setOnAction((ActionEvent event) -> {
            myFileOps.SaveData(dm, true);
            dm.setDataIsClean(true);
            fileLabel.setText("File: " + dm.getFileName());
        });

        exitProgram.setOnAction((ActionEvent event) -> {
            myFileOps.ExitProgram(dm);
        });

        // **************************************************************
        // *                    Edit Menu                               *
        // **************************************************************
        
        delUnstacked.setOnAction((ActionEvent event) -> {
            dm.deleteUnstacked();
        });

        insObs.setOnAction((ActionEvent event) -> {
            myEditOps.insertObservation();
            //myData.checkAllData();
        });

        delRow.setOnAction((ActionEvent event) -> {
            myEditOps.deleteRow();
        });
        
        addCol.setOnAction((ActionEvent event) -> {
            myEditOps.addColumn();
        });

        
        delCol.setOnAction((ActionEvent event) -> {
            // Delete the column mouse is currently in
            myEditOps.deleteColumn();
        });
        
        cleanCol.setOnAction((ActionEvent event) -> {
            myEditOps.cleanDataInColumn();
        });
        
        // **************************************************************
        // *              Transformations & Operations                  *
        // **************************************************************        

        linTransOfVar.setOnAction((ActionEvent event) -> {
            Transformations_GUI trans_GUI = new Transformations_GUI(dm);
            trans_GUI.linTransVars();
        });

        nonlinTransOfVar.setOnAction((ActionEvent event) -> {
            Transformations_GUI trans_GUI = new Transformations_GUI(dm);
            trans_GUI.nonLinTransVars();
        });
        
        linearCombOfVars.setOnAction((ActionEvent event) -> {
            Transformations_GUI trans_GUI = new Transformations_GUI(dm);
            trans_GUI.linearCombOfVariables();
        });
        
        unaryOperationsWithVars.setOnAction((ActionEvent event) -> {
            //myData.checkAllData();
            Transformations_GUI runMe = new Transformations_GUI(dm);
            runMe.unaryOperationOnVar();
        });

        binaryOperationsWithVars.setOnAction((ActionEvent event) -> {
            //myData.checkAllData();
            Transformations_GUI runMe = new Transformations_GUI(dm);
            runMe.binaryOpsWithVariables();
        });

        // **************************************************************
        // *                  Data Exploration                          *
        // **************************************************************
        
        exploreUnivariateData.setOnAction((ActionEvent event) -> {
            Exploration_Procedure explor_Proc = new Exploration_Procedure(dm);
            returnStatus = explor_Proc.doTheProcedure();
        });

        explore2IndData.setOnAction((ActionEvent event) -> {
            Explore_2Ind_Procedure twoIndVar_Proc = new Explore_2Ind_Procedure(dm);
            returnStatus = twoIndVar_Proc.doTheProcedure();
        });

        runOneSamp.setOnAction((ActionEvent event) -> {
            Single_t_procedure singleT = new Single_t_procedure(dm);
            singleT.prepColumnsFromNonStacked();
        });

        // **************************************************************
        // *               Inference for means                          *
        // **************************************************************
        
        // ******          Independent t procedure          ******
        runInd_ttest.setOnAction((ActionEvent event) -> {
            Indep_t_procedure indyT = new Indep_t_procedure(dm);
            returnStatus = indyT.chooseTheStructureOfData();
        });

        runDttest.setOnAction((ActionEvent event) -> {
            if (ok) {
                //TTest_RM trans_GUI = new TTest_RM();
                //ok = trans_GUI.computeTTestWithin(dm);
            }
        });
        
        // **************************************************************
        // *              One way Analysis of Variance                  *
        // **************************************************************

        anova_CRD.setOnAction((ActionEvent event) -> {
            // ANOVA1_Procedure anova1_Proc = new ANOVA1_Procedure(dm, "ANOVA1");
            ANOVA1_Procedure anova1_Proc = new ANOVA1_Procedure(dm, "QANOVA1");
            anova1_Proc.doStackedOrNot();
            returnStatus = anova1_Proc.getReturnStatus(); 
        });
        
        // **************************************************************
        // *              Two way Analysis of Variance                  *
        // **************************************************************

        runCR2anova.setOnAction((ActionEvent event) -> {
            System.out.println("Running CR2anova");
            ANOVA2_Procedure anova2_Proc = new ANOVA2_Procedure(dm, "Factorial");
            returnStatus = anova2_Proc.getReturnStatus();
        });

        //              Randomized Block
        runRCBanova.setOnAction((ActionEvent event) -> {
            System.out.println("Running RCBanova");
            ANOVA2_Procedure anova2_Proc = new ANOVA2_Procedure(dm, "RCB");
            returnStatus = anova2_Proc.getReturnStatus();
        });
        
        //              Repeated measures
        runRepeatedanova.setOnAction((ActionEvent event) -> {
            System.out.println("Running Repeated measures anova");
            ANOVA2_Procedure anova2_Proc = new ANOVA2_Procedure(dm, "Repeat");
            returnStatus = anova2_Proc.getReturnStatus();
        });
        
        // **************************************************************
        // *                   Regression                               *
        // **************************************************************
        
        runSimpleReg.setOnAction((ActionEvent event) -> {
                Regression_Procedure simpleRegrProc = new Regression_Procedure(dm);
                returnStatus = simpleRegrProc.doTheProcedure();
        });
        
        // ----------------------------------------
        runMultReg.setOnAction((ActionEvent event) -> {
            MultRegression_Procedure multRegProc = new MultRegression_Procedure(dm);
            returnStatus = multRegProc.doTheProcedure();
        });

        runLogistic.setOnAction((ActionEvent event) -> {
            Logistic_Procedure logisticReg_Proc = new Logistic_Procedure(dm);
            returnStatus = logisticReg_Proc.doTheProcedure();
        });
        
        // **************************************************************
        // *                    Chi square                              *
        // **************************************************************

        chiSquareRawCounts.setOnAction((ActionEvent event) -> {
            X2_Menu x2Menu = new X2_Menu();
            x2Menu.chooseProcedure();
            procedure = x2Menu.getChosenProcedure();
            switch(procedure) {
                case GOF: 
                    X2GOF_Procedure x2GOF_Proc = new X2GOF_Procedure(); 
                    returnStatus = x2GOF_Proc.doGOF_FromCounts();
                    break; 
                case EXPERIMENT: 
                case HOMOGENEITY: 
                case INDEPENDENCE: 
                    X2Assoc_Procedure x2Assoc_Proc = new X2Assoc_Procedure(procedure);
                    x2Assoc_Proc.doAssoc_FromCounts();
                    break;
                case ESCAPE: break;
                default:
                    System.out.println("Ack!! switch failure in chSquare, MainMenu");
            }
        });

        chiSquareFileData.setOnAction((ActionEvent event) -> {
            X2_Menu x2Menu = new X2_Menu();
            x2Menu.chooseProcedure();
            procedure = x2Menu.getChosenProcedure();
            switch(procedure) {
                case GOF: 
                    X2GOF_Procedure x2GOF_Proc = new X2GOF_Procedure(); 
                    x2GOF_Proc.doGOF_FromFileData(dm);
                    break;     
                case EXPERIMENT: 
                case HOMOGENEITY: 
                case INDEPENDENCE: 
                    X2Assoc_Procedure x2Assoc_Proc = new X2Assoc_Procedure(procedure);
                    x2Assoc_Proc.doAssoc_FromFile(dm);
                    break;
                case ESCAPE: break;
                default:
                    System.out.println("Ack!! switch failure in chSquare, MainMenu");
            }
        });
        
        // **************************************************************
        // *                Inference for proportions                   *
        // **************************************************************

        runProp1.setOnAction((ActionEvent event) -> {
            //myData.checkAllData();
            // Deleted from Splat 10/17/18
            //OneSampleProportion trans_GUI = new OneSampleProportion();
            //runMe.OneSampProportion_Dialog(dm);
        });

        runProp2.setOnAction((ActionEvent event) -> {
            System.out.println("385 s-main, constructing 2Prop");
            TwoProp_Difference twoPropDiff_Proc = new TwoProp_Difference();
            returnStatus = twoPropDiff_Proc.getReturnStatus();
        });

    } // End constructor
    
    public Splat_DataManager getDataManager() { return dm; }
} // class
