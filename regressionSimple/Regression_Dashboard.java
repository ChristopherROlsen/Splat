/**************************************************
 *               Regression_Dashboard             *
 *                    08/25/18                    *
 *                     00:00                      *
 *************************************************/
package regressionSimple;

import genericClasses.*;
import proceduresOneUnivariate.*;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;


public class Regression_Dashboard extends Dashboard {
    // POJOs
    final String[] regrCheckBoxDescr = { " Model Utility Test",
                                         " Scatterplot ", " Residual plot ",
                                         " RegrReport ", " DiagReport ",
                                         "NPP Residuals"};
    
    // My classes
    BestFit_View bestFitView; 
    DragableAnchorPane pdfViewDRAnchorPane, bestFitDRAnchorPane, residualsDRAnchorPane,
                       prntRegReportDRAnchorPane, prntDiagDRAnchorPane, nppResidsDRAnchorPane;   
    
    PrintDiagReport_View prntDiagReportView; 
    PrintRegrReport_View prntRegReportView;
    Regression_Model regression_Model;   
    NormProb_Model normProb_Model;
    Regression_PDFView regression_PDFView;
    Regression_Procedure regression_Procedure;    
    Residuals_View residualsView;
    NormProb_View nppResidsView;
    QuantitativeDataVariable qdv_Resids;

    // POJOs / FX
    CheckBox[] regrCheckBoxes;
    Pane pdfViewContainingPane, bestFitContainingPane, residualsContainingPane,
         prntRegReportContainingPane, prntDiagReportContainingPane,
         nppResidsContainingPane; 
            
    public Regression_Dashboard(Regression_Procedure regression_Procedure, Regression_Model regression_Model) {
        super(6);  // nCheckBoxes = 6;
        this.regression_Procedure = regression_Procedure;
        this.regression_Model = regression_Model;
        qdv_Resids = new QuantitativeDataVariable();
        qdv_Resids = regression_Model.getQDVResids();
        
        normProb_Model = new NormProb_Model(qdv_Resids);
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
        setTitle("Simple regression dashboard"); 
    }  
    
    public void putEmAllUp() { 
        
        if (checkBoxSettings[0] == true) {
            pdfViewContainingPane.setVisible(true);
            regression_PDFView.doTheGraph();
        }
        else
            pdfViewContainingPane.setVisible(false);
        
        if (checkBoxSettings[1] == true) {
            bestFitContainingPane.setVisible(true);
            bestFitView.doTheGraph();
        }
        else 
            bestFitContainingPane.setVisible(false);  
        
        if (checkBoxSettings[2] == true) {
            residualsContainingPane.setVisible(true);
            residualsView.doTheGraph();
        }
        else {
            residualsContainingPane.setVisible(false); 
        }
        
        if (checkBoxSettings[3] == true) {
            prntRegReportContainingPane.setVisible(true);
        }
        else {
            prntRegReportContainingPane.setVisible(false); 
        }

        if (checkBoxSettings[4] == true) {
            prntDiagReportContainingPane.setVisible(true);
        }
        else {
            prntDiagReportContainingPane.setVisible(false); 
        }   
        
        if (checkBoxSettings[5] == true) {
            nppResidsContainingPane.setVisible(true);
            nppResidsView.doTheGraph();
        }
        else {
            nppResidsContainingPane.setVisible(false); 
        }  
    }
    
    public void populateTheBackGround() {
        //   In Dashboard, initial widths are 675, initial heights are 375
        regression_PDFView = new Regression_PDFView(regression_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        regression_PDFView.completeTheDeal();
        pdfViewContainingPane = regression_PDFView.getTheContainingPane(); 
        pdfViewContainingPane.setStyle(containingPaneStyle);
        
        prntRegReportView = new PrintRegrReport_View(regression_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        prntRegReportView.completeTheDeal();
        prntRegReportContainingPane = prntRegReportView.getTheContainingPane(); 
        prntRegReportContainingPane.setStyle(containingPaneStyle);
        
        initWidth[2] = 700;
        prntDiagReportView = new PrintDiagReport_View(regression_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        prntDiagReportView.completeTheDeal();
        prntDiagReportContainingPane = prntDiagReportView.getTheContainingPane(); 
        prntDiagReportContainingPane.setStyle(containingPaneStyle);

        bestFitView = new BestFit_View(regression_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        bestFitView.completeTheDeal();
        bestFitContainingPane = bestFitView.getTheContainingPane(); 
        bestFitContainingPane.setStyle(containingPaneStyle);
        
        residualsView = new Residuals_View(regression_Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        residualsView.completeTheDeal();        
        residualsContainingPane = residualsView.getTheContainingPane();  
        residualsContainingPane.setStyle(containingPaneStyle);
        
        nppResidsView = new NormProb_View(normProb_Model, this, sixteenths_across[5], sixteenths_down[5], initWidth[5], initHeight[5]);
        nppResidsView.completeTheDeal();        
        nppResidsContainingPane = nppResidsView.getTheContainingPane();  
        nppResidsContainingPane.setStyle(containingPaneStyle);

        backGround.getChildren().addAll( pdfViewContainingPane,
                                         bestFitContainingPane, 
                                         residualsContainingPane,
                                         prntRegReportContainingPane,
                                         prntDiagReportContainingPane,
                                         nppResidsContainingPane);          
    }
}