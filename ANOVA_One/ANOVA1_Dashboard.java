/**************************************************
 *                ANOVA1_Dashboard                *
 *                    12/27/18                    *
 *                     15:00                      *
 *************************************************/
package ANOVA_One;

import superClasses.Dashboard;
import genericClasses.DragableAnchorPane;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import proceduresOneUnivariate.*;

public class ANOVA1_Dashboard extends Dashboard {
    // POJOs
    private String explanatoryVariable, responseVariable;
    private final String[] anova1CheckBoxDescr = { " FDist \n (Inference)",  "Box Plot ",
                                           " Circle Plot ",
                                         " Mean and Error Bars ", " Post Hoc ", 
                                         " Print Stats "};

    // My classes
    private DragableAnchorPane fPDFPlotDRAnchorPane, boxPlotDRAnchorPane, 
                       circlePlotDRAnchorPane, postHocDRAnchorPane,
                       meanAndBarsDRAnchorPane, printReportDRAnchorPane;   

    private ANOVA1_Model anova1Model;
    private ANOVA1_Procedure anova1_procedure; 
    private ArrayList<QuantitativeDataVariable> allTheQDVs;
    private FDistPDFView fPDFView;
    // ANOVA1_BoxPlotView boxPlotView; 
    private VerticalBoxPlot_View boxPlotView;
    private ANOVA1_CirclePlotView circlePlotView;
    private ANOVA1_PostHocView postHocView; 
    private ANOVA1_MeanAndErrorView meanAndBarsView; 
    private ANOVA1_PrintReportView printReportView;
    private VerticalBoxPlot_Model boxPlotModel;
    // POJOs / FX
    private CheckBox[] anova1CheckBoxes;
    private Text txtTitle;

    Pane fPDFPlotContainingPane, boxPlotContainingPane, 
         circlePlotContainingPane, meanAndBarsContainingPane, 
         postHocContainingPane, printReportContainingPane;
 
    Scene anova1Scene;
    Screen primaryANOVA1Screen;
    Stage anova1DashboardStage;
            
    public ANOVA1_Dashboard(ANOVA1_Categorical_Procedure anova1_procedure, ANOVA1_Model anova1Model) {
        super(6);
        txtTitle = new Text("Title This is a basic garden variety title");
        explanatoryVariable = anova1_procedure.getExplanatoryVariable();
        responseVariable = anova1_procedure.getResponseVariable();
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
        this.anova1Model = anova1Model;
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
        fPDFView = new FDistPDFView(anova1Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        fPDFView.completeTheDeal();
        fPDFPlotContainingPane = fPDFView.getTheContainingPane(); 
        
        // boxPlotView = new ANOVA1_BoxPlotView(anova1Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        allTheQDVs = new ArrayList<>();
        allTheQDVs = anova1Model.getAllQDVs();
        boxPlotModel = new VerticalBoxPlot_Model(anova1Model, allTheQDVs);
        boxPlotView = new VerticalBoxPlot_View(boxPlotModel, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        boxPlotView.completeTheDeal();
        boxPlotContainingPane = boxPlotView.getTheContainingPane(); 

        circlePlotView = new ANOVA1_CirclePlotView(anova1Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        circlePlotView.completeTheDeal();        
        circlePlotContainingPane = circlePlotView.getTheContainingPane();     

        meanAndBarsView = new ANOVA1_MeanAndErrorView(anova1Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        meanAndBarsView.completeTheDeal();
        meanAndBarsContainingPane = meanAndBarsView.getTheContainingPane();
        
        postHocView = new ANOVA1_PostHocView(anova1Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        postHocView.completeTheDeal();
        postHocContainingPane = postHocView.getTheContainingPane(); 
        
        // "Override
        initWidth[5] = 725;
        printReportView = new ANOVA1_PrintReportView(anova1Model, this, sixteenths_across[5], sixteenths_down[5], initWidth[5], initHeight[5]);
        printReportView.completeTheDeal();
        printReportContainingPane = printReportView.getTheContainingPane(); 

        backGround.getChildren().addAll(fPDFPlotContainingPane,
                                        boxPlotContainingPane, 
                                        circlePlotContainingPane,
                                        meanAndBarsContainingPane,
                                        postHocContainingPane,
                                        printReportContainingPane);  
    }
    
    public String getExplanVar() { return explanatoryVariable; }
    public String getResponseVar() { return responseVariable; }
    public String getSubTitle() { 
        String subTitle = responseVariable + " vs. " + explanatoryVariable; 
        return subTitle;
    }
}
