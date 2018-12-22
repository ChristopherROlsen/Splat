/**************************************************
 *                  RCB_Dashboard                *
 *                    05/15/18                    *
 *                     15:00                      *
 *************************************************/
package ANOVA_Two;

import genericClasses.Dashboard;
import genericClasses.DragableAnchorPane;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class RCB_Dashboard extends Dashboard {
    // POJOs
    final String[] anova2CheckBoxDescr = { " Box Plot ", " Circle Plot ",
                                           "MainEffectA", "MainEffectB",
                                           " Margin of Error ", " Stand Dev ", 
                                           " Stand Err ", " Interaction ", 
                                           " Post Hoc ", " Print Stats "
                                         };
    
    // My classes
    ANOVA2_Procedure anova2_procedure;
    
    ANOVA2_BoxPlotView boxPlotView; 
    ANOVA2_CirclePlotView circlePlotView;
    ANOVA2_MargOfErrView margOfErrView; 
    ANOVA2_StDevView standDevView;
    ANOVA2_StErrView standErrView; 
    ANOVA2_InteractionView interactionView;
    ANOVA2_ResVsFitView resVsFitView;
    ANOVA2_MainEffect_AView mainEffect_AView;
    ANOVA2_MainEffect_BView mainEffect_BView;
    ANOVA2_PrintReportView printReportView;
    ANOVA2_MainEffect_AView mainEffectAView;
    ANOVA2_MainEffect_BView mainEffectBView;

    DragableAnchorPane boxPlotDRAnchorPane, circlePlotDRAnchorPane,
                       marginErrDRAnchorPane, standDevDRAnchorPane, 
                       standErrDRAnchorPane, interactionDRAnchorPane, 
                       postHocDRAnchorPane, printReportDRAnchorPane,
                       mainEffectAPane, mainEffectBPane;   

    RCB_Model anova2Model;
    
    //  POJO / FX
    CheckBox[] anova1CheckBoxes;
    
    Pane boxPlotContainingPane, circlePlotContainingPane,
         marginErrContainingPane, standDevContainingPane, 
         standErrContainingPane, interactionContainingPane,
         postHocContaingingPane, printReportContainingPane,
         mainEffectAContainingPane, mainEffectBContainingPane;

    Scene anova1Scene;
    Screen primaryANOVA1Screen;
    Stage anova1DashboardStage;
    Text titleText = new Text("Title This is a basic garden variety title");
    
    public RCB_Dashboard(ANOVA2_Procedure anova2_platform, RCB_Model anova2Model) {
        super(10);
        checkBoxDescr = new String[nCheckBoxes];
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = anova2CheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            if (checkBoxes[ithCheckBox].isSelected() == true) 
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            else
                checkBoxes[ithCheckBox].setTextFill(Color.RED);
        }  
        setTitle("RCB dashboard");
        this.anova2_procedure = anova2_platform;
        this.anova2Model = anova2Model;
    }  
    
    @Override
    public void putEmAllUp() { 
        if (checkBoxSettings[0] == true) {
            boxPlotContainingPane.setVisible(true);
            boxPlotView.doThePlot();
        }
        else 
            boxPlotContainingPane.setVisible(false);  
        
        if (checkBoxSettings[1] == true) {
            circlePlotContainingPane.setVisible(true);
            circlePlotView.doThePlot();
        }
        else {
            circlePlotContainingPane.setVisible(false); 
        }
        
        if (checkBoxSettings[2] == true) {
            mainEffectAContainingPane.setVisible(true);
            mainEffectAView.doThePlot();
        }
        else {
            mainEffectAContainingPane.setVisible(false); 
        }
  
        if (checkBoxSettings[3] == true) {
            mainEffectBContainingPane.setVisible(true);
            mainEffectBView.doThePlot();
        }
        else {
            mainEffectBContainingPane.setVisible(false); 
        }
/*
        
        if (checkBoxSettings[3] == true) {
            standDevContainingPane.setVisible(true);
            standDevView.doTheGraph();
        }
        else {
            standDevContainingPane.setVisible(false); 
        }
        
        if (checkBoxSettings[4] == true) {
            standErrContainingPane.setVisible(true);
            standErrView.doTheGraph();
        }
        else {
            standErrContainingPane.setVisible(false); 
        }
*/
        if (checkBoxSettings[5] == true) {
            interactionContainingPane.setVisible(true);
            interactionView.doThePlot();
        }
        else {
            interactionContainingPane.setVisible(false); 
        } 

/*        
        if (checkBoxSettings[6] == true) {
            printReportContainingPane.setVisible(true);
        }
        else {
            printReportContainingPane.setVisible(false); 
        } 
    */

    }
    
    @Override
    public void populateTheBackGround() {
        
/*        
        boxPlotView = new ANOVA2_BoxPlotView(anova2Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        boxPlotView.completeTheDeal();
        boxPlotContainingPane = boxPlotView.getTheContainingPane();
    
        circlePlotView = new ANOVA2_CirclePlotView(anova2Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        circlePlotView.completeTheDeal();        
        circlePlotContainingPane = circlePlotView.getTheContainingPane();  
     
        mainEffectAView = new ANOVA2_MainEffect_AView(anova2Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[3], initHeight[3]);
        mainEffectAView.completeTheDeal();        
        mainEffectAContainingPane = mainEffectAView.getTheContainingPane();  
        
        mainEffectBView = new ANOVA2_MainEffect_BView(anova2Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        mainEffectBView.completeTheDeal();        
        mainEffectBContainingPane = mainEffectBView.getTheContainingPane();  

       
        standDevView = new ANOVA2_StandDevView(anova2Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        standDevView.completeTheDeal();
        standDevContainingPane = standDevView.getTheContainingPane(); 
        
        standErrView = new ANOVA2_StandErrView(anova2Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        standErrView.completeTheDeal();
        standErrContainingPane = standErrView.getTheContainingPane();


        interactionView = new ANOVA2_InteractionView(anova2Model, this, sixteenths_across[5], sixteenths_down[5], initWidth[5], initHeight[5]);
        interactionView.completeTheDeal();
        interactionContainingPane = interactionView.getTheContainingPane(); 

        
        printReportView = new ANOVA2_PrintReportView(anova2Model, this, sixteenths_across[6], sixteenths_down[6], initWidth[6], initHeight[6]);
        printReportView.completeTheDeal();
        printReportContainingPane = printReportView.getTheContainingPane(); 


        backGround.getChildren().addAll(boxPlotContainingPane, 
                                        circlePlotContainingPane,
                                        marginErrContainingPane,
                                        standDevContainingPane,
                                        standErrContainingPane,
                                        interactionContainingPane,
                                        printReportContainingPane); 

        */

        System.out.println("200, ANOVA2_Dashboard, bp = " + boxPlotContainingPane);
        System.out.println("cp = " + circlePlotContainingPane);
        System.out.println("mA = " + mainEffectAContainingPane);
        System.out.println("mB = " + mainEffectBContainingPane);        
        System.out.println("iC = " + interactionContainingPane);
        
        System.out.println("bg = " + backGround);
                                
        /*
        backGround.getChildren().addAll(boxPlotContainingPane, 
                                        circlePlotContainingPane, 
                                        mainEffectAContainingPane,
                                        mainEffectBContainingPane,
                                        interactionContainingPane);
        */
    }
}


