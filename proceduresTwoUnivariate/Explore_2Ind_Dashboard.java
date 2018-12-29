/**************************************************
 *              Explore_2Ind_Dashboard            *
 *                    11/10/18                    *
 *                     03:00                      *
 *************************************************/
package proceduresTwoUnivariate;

import superClasses.Dashboard;
import proceduresOneUnivariate.HorizontalBoxPlot_View;
import proceduresOneUnivariate.VerticalBoxPlot_Model;
import proceduresOneUnivariate.HorizontalBoxPlot_Model;
import proceduresOneUnivariate.VerticalBoxPlot_View;
import dataObjects.QuantitativeDataVariable;
import genericClasses.DragableAnchorPane;
import java.util.ArrayList;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

public class Explore_2Ind_Dashboard extends Dashboard {
    //  POJOs
    final String[] regrCheckBoxDescr = { "HBoxPlot", "VBoxPlot",
                                         "QQPlot", "BBSLPlot"};
    
    // My classes
    BBSL_Model bbsl_Model;
    BBSL_View bbsl_View; 
    Explore_2Ind_PrepareStructs comp2Ind_prepStructs;
    DragableAnchorPane hBoxDRAnchorPane, vBoxDRAnchorPane,
                       qqPlotDRAnchorPane, bbslDRAnchorPane;  
    HorizontalBoxPlot_Model hBox_Model;
    HorizontalBoxPlot_View hBox_View; 
    QQPlot_Model qqPlot_Model;
    QQPlot_View qqPlot_View;
    ArrayList<QuantitativeDataVariable> theThreeNDVs;
    VerticalBoxPlot_Model vBox_Model;
    VerticalBoxPlot_View vBox_View;

    // POJOs / FX
    CheckBox[] twoIndCheckBoxes;
    Pane hBoxContainingPane, vBoxContainingPane,
         qqPlotContainingPane, bbslContainingPane; 
    
    Text txtTitle = new Text("Two independent samples dashboard");
            
    public Explore_2Ind_Dashboard(Explore_2Ind_PrepareStructs xPlore2Ind_prepStructs, ArrayList<QuantitativeDataVariable> allTheQDVs) {
        super(4);
        hBox_Model = new HorizontalBoxPlot_Model();
        hBox_Model = xPlore2Ind_prepStructs.getHBox_Model();
        vBox_Model = new VerticalBoxPlot_Model();
        vBox_Model = xPlore2Ind_prepStructs.getVBox_Model();
        qqPlot_Model = new QQPlot_Model();
        qqPlot_Model = xPlore2Ind_prepStructs.getQQ_Model();
        bbsl_Model = new BBSL_Model();
        bbsl_Model = xPlore2Ind_prepStructs.getBBSL_Model();
        this.comp2Ind_prepStructs = xPlore2Ind_prepStructs;

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
        setTitle("Two independent samples dashboard"); 
        
    }  

    
    public void putEmAllUp() { 
        if (checkBoxSettings[0] == true) {
            hBoxContainingPane.setVisible(true);
            hBox_View.doTheGraph();
        }
        else 
            hBoxContainingPane.setVisible(false);  
        
        if (checkBoxSettings[1] == true) {
            vBoxContainingPane.setVisible(true);
            vBox_View.doTheGraph();
        }
        else {
            vBoxContainingPane.setVisible(false); 
        }
        
        if (checkBoxSettings[2] == true) {
            qqPlotContainingPane.setVisible(true);
            qqPlot_View.doTheGraph();
        }
        else {
            qqPlotContainingPane.setVisible(false); 
        }

        if (checkBoxSettings[3] == true) {
            bbslContainingPane.setVisible(true);
            bbsl_View.doTheGraph();
        }
        else {
            bbslContainingPane.setVisible(false); 
        }        
    }
    
    public void populateTheBackGround() {
        // System.out.println("109 C2I_Dash, populateTheBackGround()");        
        containingPaneStyle =  "-fx-background-color: white;" +
            "-fx-border-color: blue, blue;" + 
            "-fx-border-width: 4, 4;" +
            "fx-border-radius: 0, 0;" +
            "-fx-border-insets: -4, -4;" +
            "-fx-border-style: solid centered, solid centered;";
        
        hBox_View = new HorizontalBoxPlot_View(hBox_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        hBox_View.completeTheDeal();
        hBoxContainingPane = hBox_View.getTheContainingPane(); 
        hBoxContainingPane.setStyle(containingPaneStyle);
        
        vBox_View = new VerticalBoxPlot_View(vBox_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        vBox_View.completeTheDeal();
        vBoxContainingPane = vBox_View.getTheContainingPane(); 
        vBoxContainingPane.setStyle(containingPaneStyle);

        qqPlot_View = new QQPlot_View(qqPlot_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        qqPlot_View.completeTheDeal();
        qqPlotContainingPane = qqPlot_View.getTheContainingPane(); 
        qqPlotContainingPane.setStyle(containingPaneStyle);
        
        bbsl_View = new BBSL_View(bbsl_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        bbsl_View.completeTheDeal();        
        bbslContainingPane = bbsl_View.getTheContainingPane();  
        bbslContainingPane.setStyle(containingPaneStyle);

        
        backGround.getChildren().addAll(hBoxContainingPane, 
                                         vBoxContainingPane,
                                         qqPlotContainingPane,
                                         bbslContainingPane);          
    }
}