/***********************************************************
*                X2Assoc_Dashboard                         *
*                     05/15/18                             *
*                      12:00                               *
***********************************************************/
package chiSquare;

import genericClasses.Dashboard;
import genericClasses.DragableAnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import probabilityDistributions.ChiSquareDistribution;

public class X2Assoc_Dashboard extends Dashboard {
    // POJOs
    int df, nCategories;    //  Unique after editing
    int[] observedCounts; 
    
    double chiSquare;
    
    String[] assocCheckBoxDescr = {" Chi square \n (Inference)", 
                                   "Print Statistics \n      (basic)", 
                                   "Print Statistics\n   (advanced)", 
                                   "   Mosaic \n    Plot", 
                                   " Segmented \n bar chart",
                                   " Pie Chart "};
    // My classes
    ChiSquareDistribution x2Distr;
    ChiSqPDFView x2PDFView;
    
    DragableAnchorPane pdfViewDRGridPane, mosaicPlotDRGridPane, 
                       segmentedDRGridPane, assocPrintStatsDRGridPane, 
                       assocPrintAdvStatsDRGridPane, pieChartDRGridPane;    
    
    X2Assoc_Model x2assoc_Model;
    X2Assoc_MosaicPlotView mosaicPlotView;
    X2Assoc_PieChartView pieChartView;
    X2Assoc_PrintAdvStats assocPrintAdvStats;
    X2Assoc_PrintStats assocPrintStats;
    X2Assoc_SegBarChartView segBarChartView;

    // POJOs / FX
    CheckBox[] assocCheckBoxes;
    
    Pane pdfViewContainingPane, mosaicPlotContainingPane, segmentedBarChartContainingPane,
             assocPrintStatsContainingPane, assocPrintAdvStatsContainingPane,
             pieChartContainingPane; 
      
    public X2Assoc_Dashboard(X2Assoc_Procedure x2Assoc_Procedure, X2Assoc_Model x2assoc_Model) {
        super(6);
        checkBoxDescr = new String[nCheckBoxes];
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = assocCheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            if (checkBoxes[ithCheckBox].isSelected() == true) 
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            else
                checkBoxes[ithCheckBox].setTextFill(Color.RED);
        }  
        setTitle("ChiSquare Association dashboard");  
        this.x2assoc_Model = x2assoc_Model;
        df = x2assoc_Model.getDF();       
        chiSquare = x2assoc_Model.getChiSquare();
        x2Distr = new ChiSquareDistribution(df);   
    }  
     
    public void putEmAllUp() { 
        if (checkBoxSettings[0] == true) {
            pdfViewContainingPane.setVisible(true);
            x2PDFView.doTheGraph();
        }
        else
            pdfViewContainingPane.setVisible(false); 

        if (checkBoxSettings[1] == true) {
            assocPrintStatsContainingPane.setVisible(true);
        }
        else
            assocPrintStatsContainingPane.setVisible(false);  

        if (checkBoxSettings[2] == true) {
            assocPrintAdvStatsContainingPane.setVisible(true);
        }
        else
            assocPrintAdvStatsContainingPane.setVisible(false);   

        if (checkBoxSettings[3] == true) {
            mosaicPlotContainingPane.setVisible(true);
            mosaicPlotView.doThePlot();
        }
        else
            mosaicPlotContainingPane.setVisible(false);    

        if (checkBoxSettings[4] == true) {
            segmentedBarChartContainingPane.setVisible(true);
            segBarChartView.doThePlot();
        }
        else
            segmentedBarChartContainingPane.setVisible(false); 
       
        if (checkBoxSettings[5] == true) {
            pieChartContainingPane.setVisible(true);
            pieChartView.doThePlot();
        }
        else
            pieChartContainingPane.setVisible(false); 

    }
    
    public void populateTheBackGround() {
        
        x2PDFView = new ChiSqPDFView(x2assoc_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        x2PDFView.completeTheDeal();
        pdfViewContainingPane = x2PDFView.getTheContainingPane();
        pdfViewContainingPane.setStyle(containingPaneStyle);

        assocPrintStats = new X2Assoc_PrintStats(x2assoc_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        assocPrintStats.completeTheDeal();
        assocPrintStatsContainingPane = assocPrintStats.getTheContainingPane();
        assocPrintStatsContainingPane.setStyle(containingPaneStyle);
        
        assocPrintAdvStats = new X2Assoc_PrintAdvStats(x2assoc_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        assocPrintAdvStats.completeTheDeal();
        assocPrintAdvStatsContainingPane = assocPrintAdvStats.getTheContainingPane();
        assocPrintAdvStatsContainingPane.setStyle(containingPaneStyle);

        mosaicPlotView = new X2Assoc_MosaicPlotView(x2assoc_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        mosaicPlotView.completeTheDeal();
        mosaicPlotContainingPane = mosaicPlotView.getTheContainingPane();
        mosaicPlotContainingPane.setStyle(containingPaneStyle);

        segBarChartView = new X2Assoc_SegBarChartView(x2assoc_Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        segBarChartView.completeTheDeal();        
        segmentedBarChartContainingPane = segBarChartView.getTheContainingPane(); 
        segmentedBarChartContainingPane.setStyle(containingPaneStyle);
        
        pieChartView = new X2Assoc_PieChartView(x2assoc_Model, this, sixteenths_across[5], sixteenths_down[5], initWidth[5], initHeight[5]);
        pieChartView.completeTheDeal();        
        pieChartContainingPane = pieChartView.getTheContainingPane();
        pieChartContainingPane.setStyle(containingPaneStyle);
   
        backGround.getChildren().addAll(pdfViewContainingPane,
                                        assocPrintStatsContainingPane, 
                                        assocPrintAdvStatsContainingPane,
                                        mosaicPlotContainingPane, 
                                        segmentedBarChartContainingPane,
                                        pieChartContainingPane
                                        );          
    }
}
