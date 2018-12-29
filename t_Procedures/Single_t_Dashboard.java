/**************************************************
 *               Single_t_Dashboard               *
 *                    12/25/18                    *
 *                     15:00                      *
 *************************************************/
package t_Procedures;

import superClasses.Dashboard;
import dataObjects.QuantitativeDataVariable;
import genericClasses.DragableAnchorPane;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import proceduresOneUnivariate.*;

public class Single_t_Dashboard extends Dashboard {
    //  POJOs
    String[] indepTCheckBoxDescr = { "t-test", "HBoxPlot", "VBoxPlot",
                                         "StemPlot",
                                         };    
    // My classes
    StemNLeaf_Model stemNLeaf_Model;
    StemNLeaf_View stemNLeaf_View; 
    Single_t_PrepStructs single_t_prepStructs;
    DragableAnchorPane hBoxDRAnchorPane, vBoxDRAnchorPane,
                       stemNLeafDRAnchorPane, tPDFDRAnchorPane;  
    HorizontalBoxPlot_Model hBox_Model;
    HorizontalBoxPlot_View hBox_View; 
    QuantitativeDataVariable theQDV;
    VerticalBoxPlot_Model vBox_Model;
    VerticalBoxPlot_View vBox_View;
    Single_t_Model single_t_Model;
    Single_t_PDFView single_t_PDF_View;

    // POJOs / FX
    CheckBox[] twoIndCheckBoxes;
    Pane hBoxContainingPane, vBoxContainingPane,
         bbslContainingPane, single_t_ContainingPane; 
    
    Text txtTitle = new Text("Title This is a basic garden variety title");
            
    public Single_t_Dashboard(Single_t_PrepStructs single_t_prepStructs, QuantitativeDataVariable theQDV) {
        super(4);       
        System.out.println("46 Single_t_Dashboard, constructing");
        this.single_t_prepStructs = single_t_prepStructs;
        hBox_Model = new HorizontalBoxPlot_Model();
        hBox_Model = single_t_prepStructs.getHBox_Model();
        vBox_Model = new VerticalBoxPlot_Model();
        vBox_Model = single_t_prepStructs.getVBox_Model();
        
        // ****************************************************************
        // *  The stemNLeaf_Model parameters are also supporting a back-  *
        // *  to-back stem and leaf plot.                                 *
        // ****************************************************************
        stemNLeaf_Model = new StemNLeaf_Model("Null", theQDV, false, 0, 0, 0);
        stemNLeaf_Model = single_t_prepStructs.getStemNLeaf_Model();
        single_t_Model = single_t_prepStructs.getSingleTModel();
        
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
        setTitle("Single t inference dashboard");  
        
    }  

    
    public void putEmAllUp() { 
        if (checkBoxSettings[0] == true) {
            single_t_ContainingPane.setVisible(true);
            single_t_PDF_View.doTheGraph();
        }
        else {
            single_t_ContainingPane.setVisible(false); 
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
        
        single_t_PDF_View = new Single_t_PDFView(single_t_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        single_t_PDF_View.completeTheDeal();        
        single_t_ContainingPane = single_t_PDF_View.getTheContainingPane();  
        single_t_ContainingPane.setStyle(containingPaneStyle);
        
        hBox_View = new HorizontalBoxPlot_View(hBox_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        hBox_View.completeTheDeal();
        hBoxContainingPane = hBox_View.getTheContainingPane(); 
        hBoxContainingPane.setStyle(containingPaneStyle);
        
        vBox_View = new VerticalBoxPlot_View(vBox_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        vBox_View.completeTheDeal();
        vBoxContainingPane = vBox_View.getTheContainingPane(); 
        vBoxContainingPane.setStyle(containingPaneStyle);

        stemNLeaf_View = new StemNLeaf_View(stemNLeaf_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        stemNLeaf_View.completeTheDeal();        
        bbslContainingPane = stemNLeaf_View.getTheContainingPane();  
        bbslContainingPane.setStyle(containingPaneStyle);
        
        backGround.getChildren().addAll(single_t_ContainingPane,
                                        hBoxContainingPane, 
                                        vBoxContainingPane,
                                        bbslContainingPane);          
    }
}
