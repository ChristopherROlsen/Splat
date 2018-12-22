/**************************************************
 *                X2GOF_Dashboard                 *
 *                    05/15/18                    *
 *                     15:00                      *
 *************************************************/
package chiSquare;

import genericClasses.Dashboard;
import genericClasses.DragableAnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import probabilityDistributions.*;

public class X2GOF_Dashboard extends Dashboard {
    // POJOs
    boolean initialCBSettings[] = {false, false, false, false, false };

    double chiSquare;
   
    int nCategories;
    int[] observedCounts;
    
    String[] gofCategories;
    
    final String[] gofCheckBoxDescr = {" Chi square \n (Inference)", 
                                 "Print Statistics \n      (basic)", 
                                 "Print Statistics\n   (advanced)", 
                                 "   Plot of \n residuals", 
                                 " Plot of observed\n& expected values"};

    // My classes
    ChiSquareDistribution x2Distr;
    ChiSqPDFView x2PDFView;
    
    DragableAnchorPane obsExpDRAnchorPane, x2PDFDRAnchorPane,
                       gofPrintStatsDRAnchorPane, gofPrintAdvStatsDRAnchorPane,
                       setBarChartDRAnchorPane;  
    
    X2GOF_Model x2GOF_Model;
    X2GOF_ObsExpView chiSqObsExpView;
    X2GOF_PrintAdvStats gofPrintAdvStats;
    X2GOF_PrintStats gofPrintStats;
    X2GOF_Procedure x2GOF_Procedure; 
    X2GOF_ResidualsView gofResidView;

    //  POJOs / FX
    
    Pane obsExpContainingPane, gofResidualsContainingPane,
         gofPrintStatsContainingPane, gofPrintAdvStatsContainingPane,
         segBarChartContainingPane, x2PDFContainingPane; 
            
    public X2GOF_Dashboard(X2GOF_Procedure x2GOF_Procedure, X2GOF_Model x2GOF_Model) {
        super(5);
        // nCheckBoxes = 5;
        checkBoxDescr = new String[nCheckBoxes];
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = gofCheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            if (checkBoxes[ithCheckBox].isSelected() == true) 
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            else
                checkBoxes[ithCheckBox].setTextFill(Color.RED);
        }
        
        setTitle("ChiSquare GOF dashboard");       
        this.x2GOF_Procedure = x2GOF_Procedure;
        this.x2GOF_Model = new X2GOF_Model();
        this.x2GOF_Model = x2GOF_Model;
        populateTheBackGround();
        putEmAllUp();
    }  

    @Override
    public void putEmAllUp() {   
        if (checkBoxSettings[0] == true) {
            x2PDFContainingPane.setVisible(true);
            x2PDFView.doTheGraph();
        }
        else 
            x2PDFContainingPane.setVisible(false);  
        
        if (checkBoxSettings[1] == true) {
            gofPrintStatsContainingPane.setVisible(true);
        }
        else {
            gofPrintStatsContainingPane.setVisible(false); 
        }
        
        if (checkBoxSettings[2] == true) {
            gofPrintAdvStatsContainingPane.setVisible(true);
        }
        else {
            gofPrintAdvStatsContainingPane.setVisible(false); 
        }

        if (checkBoxSettings[3] == true) {
            gofResidualsContainingPane.setVisible(true);
            gofResidView.doTheGraph();
        }
        else {
            gofResidualsContainingPane.setVisible(false); 
        }   
        
        if (checkBoxSettings[4] == true) {
            obsExpContainingPane.setVisible(true);
            chiSqObsExpView.doTheGraph();
        }
        else {
            obsExpContainingPane.setVisible(false); 
        }
    }
    
    public void populateTheBackGround() {
        x2PDFView = new ChiSqPDFView(x2GOF_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        x2PDFView.completeTheDeal();
        x2PDFContainingPane = x2PDFView.getTheContainingPane(); 
        x2PDFContainingPane.setStyle(containingPaneStyle);
        
        gofPrintStats = new X2GOF_PrintStats(x2GOF_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1] - 80, initHeight[1] - 50);
        gofPrintStats.completeTheDeal();
        gofPrintStatsContainingPane = gofPrintStats.getTheContainingPane(); 
        gofPrintStatsContainingPane.setStyle(containingPaneStyle);
        
        gofPrintAdvStats = new X2GOF_PrintAdvStats(x2GOF_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2] + 30, initHeight[2] - 50);
        gofPrintAdvStats.completeTheDeal();
        gofPrintAdvStatsContainingPane = gofPrintAdvStats.getTheContainingPane(); 
        gofPrintAdvStatsContainingPane.setStyle(containingPaneStyle);
        
        gofResidView = new X2GOF_ResidualsView(x2GOF_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        gofResidView.completeTheDeal();        
        gofResidualsContainingPane = gofResidView.getTheContainingPane();  
        gofResidualsContainingPane.setStyle(containingPaneStyle);

        chiSqObsExpView = new X2GOF_ObsExpView(x2GOF_Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        chiSqObsExpView.completeTheDeal();        
        obsExpContainingPane = chiSqObsExpView.getTheContainingPane();  
        obsExpContainingPane.setStyle(containingPaneStyle);    
        
        backGround.getChildren().addAll(x2PDFContainingPane, 
                                         gofPrintStatsContainingPane,
                                         gofPrintAdvStatsContainingPane,
                                         gofResidualsContainingPane,
                                         obsExpContainingPane);          
    }
}
