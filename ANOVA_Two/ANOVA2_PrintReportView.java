/**************************************************
 *            ANOVA2_PrintReportView              *
 *                   02/17/18                     *
 *                     12:00                      *
 *************************************************/
package ANOVA_Two;

import genericClasses.DragableAnchorPane;
import genericClasses.StringUtilities;
import java.util.ArrayList;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.*;
import javafx.scene.control.TextArea;

public class ANOVA2_PrintReportView {
    // POJOs
    int nCategories, df, maxSpaces, spacesNeeded, nCellsBelow5;                                                                                                                                                                                  
    int[] observedValues;
    
    double initHoriz, initVert, initWidth, initHeight;    
    
    String sourceString, tempString, strThisLine;

    static ArrayList<String> anova1StringsToPrint; 
    
    // My objects
    StringUtilities myStringUtilities;  
    DragableAnchorPane dragableAnchorPane;
    ANOVA2_Model anova2Model;
    ANOVA2_Dashboard anova2Dashboard;
    
    // FX Objects
    Pane containingPane;   
    TextArea txtArea4Strings; 
    Text titleText, thisText;
    AnchorPane thePSAnchorPane;
 
    ANOVA2_PrintReportView(ANOVA2_Model anova2Model,  ANOVA2_Dashboard anova2Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        
        this.anova2Model = anova2Model;
        myStringUtilities = new StringUtilities();
        
        sourceString = new String();
        anova1StringsToPrint = new ArrayList<>();
        anova1StringsToPrint = anova2Model.getANOVA2Report();
    }
    
    public void completeTheDeal() {
        setUpUI();                 
        setUpAnchorPane();
        containingPane = dragableAnchorPane.getTheContainingPane();       
    }
    
    private void setUpUI() {
        titleText = new Text(250, 20, "One-way Analysis of Variance");
        titleText.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20)); 
        txtArea4Strings = new TextArea();  // Where text will be drawn
        txtArea4Strings.setWrapText(false);
        txtArea4Strings.setEditable(false);
        txtArea4Strings.setPrefColumnCount(50);
        txtArea4Strings.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR,12)); 
        
        for (int printLines = 0; printLines < anova1StringsToPrint.size(); printLines++)
        {
            tempString = anova1StringsToPrint.get(printLines);
            strThisLine = tempString;
            thisText = new Text(20, 19 * printLines + 40, strThisLine);
            thisText.setFont(Font.font("Courier New", FontWeight.NORMAL, FontPosture.REGULAR, 15));
            txtArea4Strings.appendText(strThisLine);
        }
 
        containingPane = new Pane();
    }
    
    private void setUpAnchorPane() {
        
        dragableAnchorPane = new DragableAnchorPane();
        // Construct the draggable
        thePSAnchorPane = dragableAnchorPane.getTheAP();
        // Finish the job -- make it dragable
        dragableAnchorPane.makeDragable();
        thePSAnchorPane.getChildren().addAll(titleText, txtArea4Strings);     
        double paneWidth = initWidth;
        double titleWidth = titleText.getLayoutBounds().getWidth();
        double titleTextEdge = (paneWidth - titleWidth) / (2 * paneWidth);
        AnchorPane.setTopAnchor(titleText, 0.0 * initHeight);        
        AnchorPane.setLeftAnchor(titleText, titleTextEdge * initWidth);
        AnchorPane.setRightAnchor(titleText, titleTextEdge * initWidth);
        AnchorPane.setBottomAnchor(titleText, 0.075 * initHeight);
        
        AnchorPane.setTopAnchor(txtArea4Strings, 0.075 * initHeight);
        AnchorPane.setLeftAnchor(txtArea4Strings, 0.0 * initWidth);
        AnchorPane.setRightAnchor(txtArea4Strings, 0.0 * initWidth);
        AnchorPane.setBottomAnchor(txtArea4Strings,0.0 * initHeight);

        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    public Pane getTheContainingPane() { return containingPane; }
}
