/**************************************************
 *               Logistic_Dashboard               *
 *                    08/25/18                    *
 *                     00:00                      *
 *************************************************/
package regressionLogistic;

import superClasses.Dashboard;
import genericClasses.DragableAnchorPane;
import dataObjects.QuantitativeDataVariable;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import proceduresOneUnivariate.NormProb_Model;
import proceduresOneUnivariate.NormProb_View;

public class Logistic_Dashboard extends Dashboard {
    // POJOs
    final String[] logisticCheckBoxDescr = { " Model Utility Test",
                                         " Logistic Plot ", " Residuals ",
                                         " RegrReport ", " NPP Residuals "};
    
    // My classes
    Logistic_View logisticReg_View; 
    LogisticResids_View logisticResids_View;
    DragableAnchorPane pdfViewDRAnchorPane, bestFitDRAnchorPane, logResidsDRAnchorPane,
                       prntRegReportDRAnchorPane, nppResidsDRAnchorPane;      
    PrintLogisticReport_View prntRegReportView;
    Logistic_Model logistic_Model;     
    // Regression_PDFView regression_PDFView;
    NormProb_Model normProb_Model;
    NormProb_View nppResids_View;
    Logistic_Procedure logistic_Procedure;    
    Logistic_View logistic_View;
    
    QuantitativeDataVariable qdv_Resids;

    // POJOs / FX
    CheckBox[] regrCheckBoxes;
    Pane pdfViewContainingPane, logisticViewContainingPane, logisticResidsContainingPane,
         prntRegReportContainingPane, nppResidsContainingPane; 
            
    public Logistic_Dashboard(Logistic_Procedure regression_Procedure, Logistic_Model logistic_Model) {
        super(5);  // nCheckBoxes = 5;
        this.logistic_Procedure = regression_Procedure;
        this.logistic_Model = logistic_Model;
        qdv_Resids = new QuantitativeDataVariable();
        qdv_Resids = logistic_Model.getQDVResids();
        normProb_Model = new NormProb_Model("Residuals", qdv_Resids);
        
        checkBoxDescr = new String[nCheckBoxes];
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = logisticCheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            if (checkBoxes[ithCheckBox].isSelected() == true) 
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            else
                checkBoxes[ithCheckBox].setTextFill(Color.RED);
        }
        setTitle("Logistic regression dashboard"); 
    }  
    
    public void putEmAllUp() { 
        
        /*
        if (checkBoxSettings[0] == true) {
            pdfViewContainingPane.setVisible(true);
            regression_PDFView.doTheGraph();
        }
        else
            pdfViewContainingPane.setVisible(false);
        */
        
        if (checkBoxSettings[1] == true) {
            logisticViewContainingPane.setVisible(true);
            logistic_View.doTheGraph();
        }
        else 
            logisticViewContainingPane.setVisible(false);  
        

        if (checkBoxSettings[2] == true) {
            logisticResidsContainingPane.setVisible(true);
            logisticResids_View.doTheGraph();
        }
        else {
            logisticResidsContainingPane.setVisible(false); 
        }
        
        if (checkBoxSettings[3] == true) {
            prntRegReportContainingPane.setVisible(true);
        }
        else {
            prntRegReportContainingPane.setVisible(false); 
        }
        

        if (checkBoxSettings[4] == true) {
            nppResidsContainingPane.setVisible(true);
            nppResids_View.doTheGraph();
        }
        else {
            nppResidsContainingPane.setVisible(false); 
        } 

    }
    
    public void populateTheBackGround() {
        //   In Dashboard, initial widths are 675, initial heights are 375
        /*
        regression_PDFView = new Regression_PDFView(logistic_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        regression_PDFView.completeTheDeal();
        pdfViewContainingPane = regression_PDFView.getTheContainingPane(); 
        pdfViewContainingPane.setStyle(containingPaneStyle);
        */
        

        prntRegReportView = new PrintLogisticReport_View(logistic_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        prntRegReportView.completeTheDeal();
        prntRegReportContainingPane = prntRegReportView.getTheContainingPane(); 
        prntRegReportContainingPane.setStyle(containingPaneStyle);

        initWidth[2] = 700;

        nppResids_View = new NormProb_View(normProb_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        nppResids_View.completeTheDeal();
        nppResidsContainingPane = nppResids_View.getTheContainingPane(); 
        nppResidsContainingPane.setStyle(containingPaneStyle);

        
        logistic_View = new Logistic_View(logistic_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        logistic_View.completeTheDeal();
        logisticViewContainingPane = logistic_View.getTheContainingPane(); 
        logisticViewContainingPane.setStyle(containingPaneStyle);
        

        logisticResids_View = new LogisticResids_View(logistic_Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        logisticResids_View.completeTheDeal();        
        logisticResidsContainingPane = logisticResids_View.getTheContainingPane();  
        logisticResidsContainingPane.setStyle(containingPaneStyle);

        
        backGround.getChildren().addAll(//pdfViewContainingPane,
                                         logisticViewContainingPane, 
                                         logisticResidsContainingPane,
                                         prntRegReportContainingPane,
                                         nppResidsContainingPane
                                         );          
    }
}
