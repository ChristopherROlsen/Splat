/**************************************************
 *            MultRegression_Dashboard            *
 *                    06/30/18                    *
 *                     18:00                      *
 *************************************************/
package regressionMultiple;

import genericClasses.Dashboard;
import genericClasses.DragableAnchorPane;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import regressionSimple.*;
import proceduresOneUnivariate.*;
import genericClasses.*;
import matrixProcedures.Matrix;

public class MultRegression_Dashboard extends Dashboard {
    // POJOs
    int nRows;
    
    final String[] regrCheckBoxDescr = { " Mult Reg Report ", " Mult Reg Diagnostics ",
                                         " Cooks Distance ", " Residuals vs Fit ",
                                         " Normal Resids "};
    
    // My classes
    CooksDist_View cooksDistView; 
    DragableAnchorPane pdfViewDRAnchorPane, bestFitDRAnchorPane, residualsDRAnchorPane,
                       prntRegReportDRAnchorPane, prntDiagDRAnchorPane, normalResidsDRAnchorPane;      
    PrintMultDiagReport_View prntMultDiagReportView; 
    PrintMultRegrReport_View prntMultRegReportView;
    MultRegression_Model multRegression_Model;     
    Regression_PDFView regression_PDFView;
    Matrix Resids;
    MultRegression_Procedure regression_Procedure;    
    ResidsVsFit_View residsVsFit_View;
    NormProb_Model normalResids_Model;
    NormProb_View  normalResids_View;
    QuantitativeDataVariable theResids;

    // POJOs / FX
    CheckBox[] regrCheckBoxes;
    Pane cooksDistContainingPane, residsVsFitContainingPane,
         prntMultRegReportContainingPane, prntMultDiagReportContainingPane,
         normalResidsContainingPane; 
            
    public MultRegression_Dashboard(MultRegression_Procedure regression_Procedure, MultRegression_Model regression_Model) {
        super(5);  // nCheckBoxes = 5;
        this.regression_Procedure = regression_Procedure;
        this.multRegression_Model = regression_Model;

        checkBoxDescr = new String[nCheckBoxes];
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = regrCheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            if (checkBoxes[ithCheckBox].isSelected() == true) 
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            else
                checkBoxes[ithCheckBox].setTextFill(Color.RED);
        }
        setTitle("Multiple regression dashboard"); 
    }  
    
    public void putEmAllUp() { 

        if (checkBoxSettings[0] == true) {
            prntMultRegReportContainingPane.setVisible(true);
        }
        else
            prntMultRegReportContainingPane.setVisible(false);
        
        if (checkBoxSettings[1] == true) {
            prntMultDiagReportContainingPane.setVisible(true);
        }
        else 
            prntMultDiagReportContainingPane.setVisible(false); 
        
        if (checkBoxSettings[2] == true) {

            cooksDistContainingPane.setVisible(true);
            cooksDistView.doTheGraph();
        }
        else {
            cooksDistContainingPane.setVisible(false);
        }
        
        if (checkBoxSettings[3] == true) {
            
            residsVsFitContainingPane.setVisible(true);
            residsVsFit_View.doTheGraph();
        }
        else {
            residsVsFitContainingPane.setVisible(false); 
        }
        
        if (checkBoxSettings[4] == true) {
            
            normalResidsContainingPane.setVisible(true);
            normalResids_View.doTheGraph();
        }
        else {
            normalResidsContainingPane.setVisible(false); 
        }

    }
    
    public void populateTheBackGround() {
        //   In Dashboard, initial widths are 675, initial heights are 375

        prntMultRegReportView = new PrintMultRegrReport_View(multRegression_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        prntMultRegReportView.completeTheDeal();
        prntMultRegReportContainingPane = prntMultRegReportView.getTheContainingPane(); 
        prntMultRegReportContainingPane.setStyle(containingPaneStyle);
        
        initWidth[2] = 700;
        prntMultDiagReportView = new PrintMultDiagReport_View(multRegression_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        prntMultDiagReportView.completeTheDeal();
        prntMultDiagReportContainingPane = prntMultDiagReportView.getTheContainingPane(); 
        prntMultDiagReportContainingPane.setStyle(containingPaneStyle);
        

        cooksDistView = new CooksDist_View(multRegression_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        cooksDistView.completeTheDeal();
        cooksDistContainingPane = cooksDistView.getTheContainingPane(); 
        cooksDistContainingPane.setStyle(containingPaneStyle);
        
        residsVsFit_View = new ResidsVsFit_View(multRegression_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        residsVsFit_View.completeTheDeal();        
        residsVsFitContainingPane = residsVsFit_View.getTheContainingPane();  
        residsVsFitContainingPane.setStyle(containingPaneStyle);
        
        // Getting the residuals and doing a normal probability plot is a bit sloppy...
        nRows = multRegression_Model.getNRows();
        Resids = new Matrix(nRows, 1);
        Resids = multRegression_Model.getR_StudentizedResids();
        
        // Convert for normalResids_Model
        QuantitativeDataVariable qdv_Resids = new QuantitativeDataVariable("Residuals", multRegression_Model.getR_StudentizedResids());
        normalResids_Model = new NormProb_Model(qdv_Resids);
        
        normalResids_View = new NormProb_View(normalResids_Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        normalResids_View.completeTheDeal();        
        normalResidsContainingPane = normalResids_View.getTheContainingPane();  
        normalResidsContainingPane.setStyle(containingPaneStyle);
        
        backGround.getChildren().addAll(//pdfViewContainingPane,
                                         prntMultRegReportContainingPane,
                                         prntMultDiagReportContainingPane,
                                         cooksDistContainingPane, 
                                         residsVsFitContainingPane,
                                         normalResidsContainingPane);   
    }
}
