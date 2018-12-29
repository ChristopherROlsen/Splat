/**************************************************
 *             Independent_t_Dashboard            *
 *                    06/04/18                    *
 *                     03:00                      *
 *************************************************/
package t_Procedures;

import superClasses.Dashboard;
import dataObjects.QuantitativeDataVariable;
import genericClasses.DragableAnchorPane;
import java.util.ArrayList;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import proceduresOneUnivariate.*;
import proceduresTwoUnivariate.*;

public class Indep_t_Dashboard extends Dashboard {
    //  POJOs
    String[] indepTCheckBoxDescr = { "t-test", "HBoxPlot", "VBoxPlot",
                                         "QQPlot", "BBSLPlot",
                                         };    
    // My classes
    BBSL_Model bbsl_Model;
    BBSL_View bbsl_View; 
    Indep_t_PrepStructs indep_t_prepStructs;
    DragableAnchorPane hBoxDRAnchorPane, vBoxDRAnchorPane,
                       qqPlotDRAnchorPane, bbslDRAnchorPane,
                       tPDFDRAnchorPane;  
    HorizontalBoxPlot_Model hBox_Model;
    HorizontalBoxPlot_View hBox_View; 
    QQPlot_Model qqPlot_Model;
    QQPlot_View qqPlot_View;
    ArrayList<QuantitativeDataVariable> theThreeQDVs;
    VerticalBoxPlot_Model vBox_Model;
    VerticalBoxPlot_View vBox_View;
    Indep_t_Model indep_t_Model;
    Indep_t_PDFView indep_t_PDF_View;

    // POJOs / FX
    CheckBox[] twoIndCheckBoxes;
    Pane hBoxContainingPane, vBoxContainingPane,
         qqPlotContainingPane, bbslContainingPane,
         indep_t_ContainingPane; 
    
    Text txtTitle = new Text("Title This is a basic garden variety title");
            
    public Indep_t_Dashboard(Indep_t_PrepStructs indep_t_prepStructs, ArrayList<QuantitativeDataVariable> allTheQDVs) {
        super(5);       

        this.indep_t_prepStructs = indep_t_prepStructs;
        hBox_Model = new HorizontalBoxPlot_Model();
        hBox_Model = indep_t_prepStructs.getHBox_Model();
        vBox_Model = new VerticalBoxPlot_Model();
        vBox_Model = indep_t_prepStructs.getVBox_Model();
        qqPlot_Model = new QQPlot_Model();
        qqPlot_Model = indep_t_prepStructs.getQQ_Model();
        bbsl_Model = new BBSL_Model(indep_t_prepStructs, "Null", allTheQDVs);
        bbsl_Model = indep_t_prepStructs.getBBSL_Model();
        indep_t_Model = indep_t_prepStructs.getIndepTModel();
        
        checkBoxDescr = new String[nCheckBoxes];
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = indepTCheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            if (checkBoxes[ithCheckBox].isSelected() == true) 
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            else
                checkBoxes[ithCheckBox].setTextFill(Color.RED);
        }
        setTitle("Independent t inference dashboard");  
        
    }  

    
    public void putEmAllUp() { 
        if (checkBoxSettings[0] == true) {
            indep_t_ContainingPane.setVisible(true);
            indep_t_PDF_View.doTheGraph();
        }
        else {
            indep_t_ContainingPane.setVisible(false); 
        }
        if (checkBoxSettings[1] == true) {
            hBoxContainingPane.setVisible(true);
            hBox_View.doTheGraph();
        }
        else 
            hBoxContainingPane.setVisible(false);  
        
        if (checkBoxSettings[2] == true) {
            vBoxContainingPane.setVisible(true);
            vBox_View.doTheGraph();
        }
        else {
            vBoxContainingPane.setVisible(false); 
        }
        
        if (checkBoxSettings[3] == true) {
            qqPlotContainingPane.setVisible(true);
            qqPlot_View.doTheGraph();
        }
        else {
            qqPlotContainingPane.setVisible(false); 
        }

        if (checkBoxSettings[4] == true) {
            bbslContainingPane.setVisible(true);
        }
        else {
            bbslContainingPane.setVisible(false); 
        }      
    }
    
    public void populateTheBackGround() {       
        containingPaneStyle =  "-fx-background-color: white;" +
            "-fx-border-color: blue, blue;" + 
            "-fx-border-width: 4, 4;" +
            "fx-border-radius: 0, 0;" +
            "-fx-border-insets: -4, -4;" +
            "-fx-border-style: solid centered, solid centered;";
        
        indep_t_PDF_View = new Indep_t_PDFView(indep_t_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        indep_t_PDF_View.completeTheDeal();        
        indep_t_ContainingPane = indep_t_PDF_View.getTheContainingPane();  
        indep_t_ContainingPane.setStyle(containingPaneStyle);
        
        hBox_View = new HorizontalBoxPlot_View(hBox_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        hBox_View.completeTheDeal();
        hBoxContainingPane = hBox_View.getTheContainingPane(); 
        hBoxContainingPane.setStyle(containingPaneStyle);
        
        vBox_View = new VerticalBoxPlot_View(vBox_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        vBox_View.completeTheDeal();
        vBoxContainingPane = vBox_View.getTheContainingPane(); 
        vBoxContainingPane.setStyle(containingPaneStyle);

        qqPlot_View = new QQPlot_View(qqPlot_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        qqPlot_View.completeTheDeal();
        qqPlotContainingPane = qqPlot_View.getTheContainingPane(); 
        qqPlotContainingPane.setStyle(containingPaneStyle);
        
        bbsl_View = new BBSL_View(bbsl_Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        bbsl_View.completeTheDeal();        
        bbslContainingPane = bbsl_View.getTheContainingPane();  
        bbslContainingPane.setStyle(containingPaneStyle);
        


        
        backGround.getChildren().addAll(indep_t_ContainingPane,
                                        hBoxContainingPane, 
                                        vBoxContainingPane,
                                        qqPlotContainingPane,
                                        bbslContainingPane);          
    }
}
