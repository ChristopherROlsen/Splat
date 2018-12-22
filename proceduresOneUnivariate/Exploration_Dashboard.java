/**************************************************
 *             Exploration_Dashboard              *
 *                   08/15/18                    *
 *                    18:00                      *
 *************************************************/
package proceduresOneUnivariate;

import genericClasses.Dashboard;
import genericClasses.QuantitativeDataVariable;
import genericClasses.DragableAnchorPane;
import javafx.stage.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Exploration_Dashboard extends Dashboard {
    // POJOs
    int nCategories;
    int[] observedCounts;

    final String[] regrCheckBoxDescr = { "Histogram", "NormalPlot",
                                         "StemNLeaf", "DotPlot",
                                         "HorizBox", "VertBox",
                                         "Ogive" };

    // My classes
    DragableAnchorPane histogramDRAnchorPane, normProbDRAnchorPane,
                       stemNLeafDRAnchorPane, dotPlotDRAnchorPane,
                       horizBoxDRAnchorPane, vertBoxDRAnchorPane,
                       ogiveDRAnchorPane;  
    Exploration_PrepareStructs exp_Structures; 
    QuantitativeDataVariable stemNLeaf_Model;
       
    DotPlot_Model dotPlotModel;    
    Histogram_Model histModel;
    HorizontalBoxPlot_Model hBoxModel;
    NormProb_Model normProbModel;
    StemNLeaf_Model stemNLeafModel;
    VerticalBoxPlot_Model vBoxModel;
    Ogive_Model ogiveModel;
    
    DotPlot_View dotPlot_View;          
    Histogram_View histogram_View; 
    HorizontalBoxPlot_View horizBoxView;
    NormProb_View normProb_View;
    StemNLeaf_View stemNLeaf_View;
    VerticalBoxPlot_View vertBoxView;
    Ogive_View ogiveView;
    
    //  POJO / FX
    Pane histogramContainingPane, normProbContainingPane,
         stemNLeafContainingPane, dotPlotContainingPane,
          horizBoxContainingPane, vertBoxContainingPane,
          ogiveContainingPane; 
    
    Stage univDashboardStage;
    
    Text titleText = new Text("Univariate exploration");
            
    public Exploration_Dashboard(Exploration_PrepareStructs exploration_PrepStructs, QuantitativeDataVariable univ_Model) {
        super(7);
        exp_Structures = exploration_PrepStructs;

        histModel = new Histogram_Model();
        histModel = exploration_PrepStructs.getHistModel();
      
        normProbModel = new NormProb_Model();
        normProbModel = exploration_PrepStructs.getNormProbModel();
       
        stemNLeafModel = new StemNLeaf_Model();
        stemNLeafModel = exploration_PrepStructs.getStemNLeafModel();
       
        dotPlotModel = new DotPlot_Model();
        dotPlotModel = exploration_PrepStructs.getDotPlotModel();
        
        hBoxModel = new HorizontalBoxPlot_Model();
        hBoxModel = exploration_PrepStructs.getHBoxModel();
        
        vBoxModel = new VerticalBoxPlot_Model();
        vBoxModel = exploration_PrepStructs.getVBoxModel(); 
        
        ogiveModel = new Ogive_Model();
        ogiveModel = exploration_PrepStructs.getOgiveModel(); 
        
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
        setTitle("Exploration dashboard"); 
    }  
    
    
    public void putEmAllUp() { 
        //System.out.println("263 dash, putEmAllUp()");
        if (checkBoxSettings[0] == true) {
            histogramContainingPane.setVisible(true);
            histogram_View.doTheGraph();
        }
        else 
            histogramContainingPane.setVisible(false);  
        
        if (checkBoxSettings[1] == true) {
            normProbContainingPane.setVisible(true);
            normProb_View.doTheGraph();
        }
        else {
            normProbContainingPane.setVisible(false); 
        }
        
        if (checkBoxSettings[2] == true) {
            stemNLeafContainingPane.setVisible(true);
            stemNLeaf_View.doTheGraph();
        }
        else {
            stemNLeafContainingPane.setVisible(false); 
        }

        if (checkBoxSettings[3] == true) {
            dotPlotContainingPane.setVisible(true);
            dotPlot_View.doTheGraph();
        }
        else {
            dotPlotContainingPane.setVisible(false); 
        }   
        
        if (checkBoxSettings[4] == true) {
            horizBoxContainingPane.setVisible(true);
            horizBoxView.doTheGraph();
        }
        else {
            horizBoxContainingPane.setVisible(false); 
        }

        if (checkBoxSettings[5] == true) {
            vertBoxContainingPane.setVisible(true);
            vertBoxView.doTheGraph();
        }
        else {
            vertBoxContainingPane.setVisible(false); 
        } 
        
        if (checkBoxSettings[6] == true) {
            ogiveContainingPane.setVisible(true);
            ogiveView.doTheGraph();
        }
        else {
            ogiveContainingPane.setVisible(false); 
        } 
    }
    
    public void populateTheBackGround() {
        
        String containingPaneStyle = "-fx-background-color: white;" +
            "-fx-border-color: blue, blue;" + 
            "-fx-border-width: 4, 4;" +
            "fx-border-radius: 0, 0;" +
            "-fx-border-insets: -4, -4;" +
            "-fx-border-style: solid centered, solid centered;";
        
        stemNLeaf_View = new StemNLeaf_View(stemNLeafModel, this, sixteenths_across[0], sixteenths_down[0], 675, 375);
        stemNLeaf_View.completeTheDeal();
        stemNLeafContainingPane = stemNLeaf_View.getTheContainingPane(); 
        stemNLeafContainingPane.setStyle(containingPaneStyle);
        
        dotPlot_View = new DotPlot_View(dotPlotModel, this, sixteenths_across[1], sixteenths_down[1], 675, 375);
        dotPlot_View.completeTheDeal();
        dotPlotContainingPane = dotPlot_View.getTheContainingPane(); 
        dotPlotContainingPane.setStyle(containingPaneStyle);
        
        histogram_View = new Histogram_View(histModel, this, sixteenths_across[2], sixteenths_down[2], 675, 375);
        histogram_View.completeTheDeal();
        histogramContainingPane = histogram_View.getTheContainingPane(); 
        histogramContainingPane.setStyle(containingPaneStyle);
        
        normProb_View = new NormProb_View(normProbModel, this, sixteenths_across[3], sixteenths_down[3], 675, 375);
        normProb_View.completeTheDeal();        
        normProbContainingPane = normProb_View.getTheContainingPane();  
        normProbContainingPane.setStyle(containingPaneStyle);

        horizBoxView = new HorizontalBoxPlot_View(hBoxModel, this, sixteenths_across[4], sixteenths_down[4], 675, 375);
        horizBoxView.completeTheDeal();        
        horizBoxContainingPane = horizBoxView.getTheContainingPane();  
        horizBoxContainingPane.setStyle(containingPaneStyle);
        
        vertBoxView = new VerticalBoxPlot_View(vBoxModel, this, sixteenths_across[5], sixteenths_down[5], 675, 375);
        vertBoxView.completeTheDeal();        
        vertBoxContainingPane = vertBoxView.getTheContainingPane();  
        vertBoxContainingPane.setStyle(containingPaneStyle);
        
        ogiveView = new Ogive_View(ogiveModel, this, sixteenths_across[6], sixteenths_down[6], 675, 375);
        ogiveView.completeTheDeal();        
        ogiveContainingPane = ogiveView.getTheContainingPane();  
        ogiveContainingPane.setStyle(containingPaneStyle);
        
        backGround.getChildren().addAll(histogramContainingPane, 
                                         normProbContainingPane,
                                         stemNLeafContainingPane,
                                         dotPlotContainingPane,
                                         horizBoxContainingPane,
                                         vertBoxContainingPane,
                                         ogiveContainingPane);          
    }
    
}