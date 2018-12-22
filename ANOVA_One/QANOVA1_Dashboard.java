/**************************************************
 *                QANOVA1_Dashboard                *
 *                    12/01/18                    *
 *                     12:00                      *
 *************************************************/
package ANOVA_One;

import genericClasses.Dashboard;
import genericClasses.DragableAnchorPane;
import genericClasses.QuantitativeDataVariable;
import java.util.ArrayList;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import proceduresOneUnivariate.*;

public class QANOVA1_Dashboard extends Dashboard {
    // POJOs
    final String[] anova1CheckBoxDescr = { " FDist \n (Inference)",  "Box Plot ",
                                           " Circle Plot ",
                                         " Mean and Error Bars ", " Post Hoc ", 
                                         " Print Stats "};

    // My classes
    DragableAnchorPane fPDFPlotDRAnchorPane, boxPlotDRAnchorPane, 
                       circlePlotDRAnchorPane, postHocDRAnchorPane,
                       meanAndBarsDRAnchorPane, printReportDRAnchorPane;   

    QANOVA1_Model qanova1Model;
    ANOVA1_Procedure anova1_procedure; 
    ArrayList<QuantitativeDataVariable> allTheQDVs;
    FDistPDFView fPDFView;
    QANOVA1_BoxPlotView boxPlotView;
    QANOVA1_CirclePlotView circlePlotView;
    QANOVA1_PostHocView postHocView; 
    QANOVA1_MeanAndErrorView meanAndBarsView; 
    QANOVA1_PrintReportView printReportView;
    VerticalBoxPlot_Model boxPlotModel;
    // POJOs / FX
    CheckBox[] anova1CheckBoxes;
    Text txtTitle;

    Pane fPDFPlotContainingPane, boxPlotContainingPane, 
         circlePlotContainingPane, meanAndBarsContainingPane, 
         postHocContainingPane, printReportContainingPane;
 
    Scene anova1Scene;
    Screen primaryANOVA1Screen;
    Stage anova1DashboardStage;
            
    public QANOVA1_Dashboard(ANOVA1_Procedure anova1_procedure, QANOVA1_Model qanova1Model) {
        super(6);
        System.out.println("56 QANOVA_Dash, constructing");
        txtTitle = new Text("Title This is a basic garden variety title");
        checkBoxDescr = new String[nCheckBoxes];
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = anova1CheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            if (checkBoxes[ithCheckBox].isSelected() == true) 
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            else
                checkBoxes[ithCheckBox].setTextFill(Color.RED);
        }  
        setTitle("One Way ANOVA dashboard");
        this.anova1_procedure = anova1_procedure;
        this.qanova1Model = qanova1Model;
    }  
    
    public void putEmAllUp() { 
        
        if (checkBoxSettings[0] == true) {
            fPDFPlotContainingPane.setVisible(true);
            fPDFView.doTheGraph();
        }
        else 
            fPDFPlotContainingPane.setVisible(false);  
               
        if (checkBoxSettings[1] == true) {
            boxPlotContainingPane.setVisible(true);
            boxPlotView.doTheGraph();
        }
        else 
            boxPlotContainingPane.setVisible(false);  
        
        if (checkBoxSettings[2] == true) {
            circlePlotContainingPane.setVisible(true);
            circlePlotView.doTheGraph();
        }
        else {
            circlePlotContainingPane.setVisible(false); 
        }
        
        if (checkBoxSettings[3] == true) {
            meanAndBarsContainingPane.setVisible(true);
            meanAndBarsView.doTheGraph();
        }
        else {
            meanAndBarsContainingPane.setVisible(false); 
        }

        if (checkBoxSettings[4] == true) {
            postHocContainingPane.setVisible(true);
            postHocView.doTheGraph();
        }
        else {
            postHocContainingPane.setVisible(false); 
        } 
        
        if (checkBoxSettings[5] == true) {
            printReportContainingPane.setVisible(true);
        }
        else {
            printReportContainingPane.setVisible(false); 
        } 
    }
    
    public void populateTheBackGround() {
        //   In Dashboard, initial widths are 675, initial heights are 375
        fPDFView = new FDistPDFView(qanova1Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        fPDFView.completeTheDeal();
        fPDFPlotContainingPane = fPDFView.getTheContainingPane(); 

        allTheQDVs = new ArrayList<>();
        allTheQDVs = qanova1Model.getAllQDVs();
        boxPlotModel = new VerticalBoxPlot_Model(allTheQDVs);
        boxPlotView = new QANOVA1_BoxPlotView(qanova1Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        boxPlotView.completeTheDeal();
        boxPlotContainingPane = boxPlotView.getTheContainingPane(); 

        circlePlotView = new QANOVA1_CirclePlotView(qanova1Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        circlePlotView.completeTheDeal();        
        circlePlotContainingPane = circlePlotView.getTheContainingPane();     

        meanAndBarsView = new QANOVA1_MeanAndErrorView(qanova1Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        meanAndBarsView.completeTheDeal();
        meanAndBarsContainingPane = meanAndBarsView.getTheContainingPane();
        
        postHocView = new QANOVA1_PostHocView(qanova1Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        postHocView.completeTheDeal();
        postHocContainingPane = postHocView.getTheContainingPane(); 
        
        // "Override
        initWidth[5] = 725;
        printReportView = new QANOVA1_PrintReportView(qanova1Model, this, sixteenths_across[5], sixteenths_down[5], initWidth[5], initHeight[5]);
        printReportView.completeTheDeal();
        printReportContainingPane = printReportView.getTheContainingPane(); 

        backGround.getChildren().addAll(fPDFPlotContainingPane,
                                        boxPlotContainingPane, 
                                        circlePlotContainingPane,
                                        meanAndBarsContainingPane,
                                        postHocContainingPane,
                                        printReportContainingPane);  
    }
}
