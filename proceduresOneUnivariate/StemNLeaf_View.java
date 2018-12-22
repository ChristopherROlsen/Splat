/****************************************************************************
 *                      StemNLeaf_View                                      * 
 *                         12/09/18                                         *
 *                          00:00                                           *
 ***************************************************************************/

package proceduresOneUnivariate;

import genericClasses.DragableAnchorPane;
import genericClasses.StringUtilities;
import java.util.ArrayList;
import java.util.Formatter;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import t_Procedures.*;

public class StemNLeaf_View {
    // POJOs
    boolean[] radioButtonSettings;    
    
    int maxSpaces, spacesNeeded;  
    int n_OneLineToPrint, n_TwoLinesToPrint, n_FiveLinesToPrint;
    int nRadioButtons, linePerStemChoice;
    int[] observedValues;
    
    double initHoriz, initVert, initWidth, initHeight;   

    String sl_Title1, sl_Title2_1, sl_Title2_2, sl_Title2_5, slTitleLines;
    String sourceString, tempString, strThisLine;
    String[] strRadioButtonDescriptions;
    ArrayList<String> oneLineSL, twoLineSL, fiveLineSL; 
    
    // POJOs / FX
    AnchorPane radioButtonRow;
    RadioButton[] sl_RadioButtons;   

    // My objects
    DragableAnchorPane dragableAnchorPane;
    Exploration_Dashboard explore_Dashboard;
    StringUtilities myStringUtilities;  
    StemNLeaf_Model stemNLeaf_Model;

    // FX Objects
    Pane containingPane;   
    TextArea txtArea, txtArea_SL_1, txtArea_SL_2, txtArea_SL_5;
    Text titleText_x, titleText_2, thisText;
    AnchorPane theSLAnchorPane;
   
    public StemNLeaf_View(StemNLeaf_Model stemLeaf_Model, Exploration_Dashboard explore_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;

        this.stemNLeaf_Model = stemLeaf_Model;
        
        nRadioButtons = 3;
        strRadioButtonDescriptions = new String[3];
        strRadioButtonDescriptions[0] = " One line / stem ";
        strRadioButtonDescriptions[1] = " Two lines / stem ";
        strRadioButtonDescriptions[2] = " Five lines / stem ";   
        
        myStringUtilities = new StringUtilities();
        
        oneLineSL = new ArrayList<>();
        twoLineSL = new ArrayList<>();
        fiveLineSL = new ArrayList<>();
        
        oneLineSL = stemLeaf_Model.get_1_LineSL();
        twoLineSL = stemLeaf_Model.get_2_LineSL();
        fiveLineSL = stemLeaf_Model.get_5_LineSL();  
             
        n_OneLineToPrint = oneLineSL.size();
        n_TwoLinesToPrint = twoLineSL.size();
        n_FiveLinesToPrint = fiveLineSL.size();
    }
    
    public StemNLeaf_View(StemNLeaf_Model stemLeaf_Model, Single_t_Dashboard single_t_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;

        this.stemNLeaf_Model = stemLeaf_Model;
        
        nRadioButtons = 3;
        strRadioButtonDescriptions = new String[3];
        strRadioButtonDescriptions[0] = " One line / stem ";
        strRadioButtonDescriptions[1] = " Two lines / stem ";
        strRadioButtonDescriptions[2] = " Five lines / stem ";   
        
        myStringUtilities = new StringUtilities();
        
        oneLineSL = new ArrayList<>();
        twoLineSL = new ArrayList<>();
        fiveLineSL = new ArrayList<>();
        
        oneLineSL = stemLeaf_Model.get_1_LineSL();
        twoLineSL = stemLeaf_Model.get_2_LineSL();
        fiveLineSL = stemLeaf_Model.get_5_LineSL();  
             
        n_OneLineToPrint = oneLineSL.size();
        n_TwoLinesToPrint = twoLineSL.size();
        n_FiveLinesToPrint = fiveLineSL.size();
    }
    
    public void completeTheDeal() {
        setUpUI();  
        makeTheRadioButtons();
        setUpAnchorPane();
        containingPane = dragableAnchorPane.getTheContainingPane();
        doTheGraph();
    }
    
    private void setUpUI() {
        int ordMag = stemNLeaf_Model.getOrderOfMagnitude();
        
        Formatter fmt = new Formatter();
        String daFormat = "%-10." + String.valueOf(Math.abs(ordMag)) + "f"; 
        fmt.format(daFormat, Math.pow(10., ordMag));
        String strOrdMag = "1|0 = " + fmt;      
        
        txtArea = new TextArea();
        txtArea.heightProperty().addListener(ov-> {doTheGraph();});
        txtArea.widthProperty().addListener(ov-> {doTheGraph();});
        
        sl_Title1 = "Stem & Leaf Plot\n";
        sl_Title2_1 = strRadioButtonDescriptions[0];
        sl_Title2_2 = strRadioButtonDescriptions[1];
        sl_Title2_5 = strRadioButtonDescriptions[2];
        
        String slVarName = stemNLeaf_Model.getTheQDV().getDataLabel() + "\n";
        int slNSize = stemNLeaf_Model.getTheQDV().getLegalN();
        String strSLNSize = "N = " + String.valueOf(slNSize) + "    ";
        slTitleLines = sl_Title1 + slVarName + strSLNSize + strOrdMag + "\n\n";

        //  One line per stem is the default
        doOneLiners();  
        txtArea = txtArea_SL_1;
 
        containingPane = new Pane();
    }
    
    public void doOneLiners() {
        txtArea_SL_1 = new TextArea(); 
        txtArea_SL_1.setWrapText(false);
        txtArea_SL_1.setEditable(false);
        txtArea_SL_1.setPrefColumnCount(50);

        // Title area
        titleText_x = new Text(20, 20, sl_Title1 + sl_Title2_1);
        titleText_x.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 20));
  
        // Stem & Leaf proper        
        txtArea_SL_1.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR,12)); 
        strThisLine = slTitleLines;
        txtArea_SL_1.appendText(strThisLine);
        thisText = new Text(20, 19 * 0 + 40, strThisLine);
        
        for (int printLines = 0; printLines < n_OneLineToPrint; printLines++)
        {
            tempString = oneLineSL.get(printLines);
            strThisLine = tempString + "\n";
            thisText = new Text(20, 19 * (printLines + 3) + 40, strThisLine);
            txtArea_SL_1.appendText(strThisLine);
        }        
    }
    
    public void doTwoLiners() {
        txtArea_SL_2 = new TextArea();  // Where text will be drawn
        txtArea_SL_2.setWrapText(false);
        txtArea_SL_2.setEditable(false);
        txtArea_SL_2.setPrefColumnCount(50);

        // Title area
        titleText_x = new Text(20, 20, sl_Title1 + sl_Title2_2);
        titleText_x.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 20));   

        // Stem & Leaf proper        
        txtArea_SL_2.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR,12)); 
        strThisLine = slTitleLines;
        txtArea_SL_2.appendText(strThisLine);
        thisText = new Text(20, 19 * 0 + 40, strThisLine);      

        for (int printLines = 0; printLines < n_TwoLinesToPrint; printLines++)
        {
            tempString = twoLineSL.get(printLines);
            strThisLine = tempString + "\n";
            thisText = new Text(20, 19 * printLines + 40, strThisLine);
            txtArea_SL_2.appendText(strThisLine);
        }        
    }
    
    public void doFiveLiners() {
        txtArea_SL_5 = new TextArea(); 
        txtArea_SL_5.setWrapText(false);
        txtArea_SL_5.setEditable(false);
        txtArea_SL_5.setPrefColumnCount(50);

        // Title area
        titleText_x = new Text(20, 20, sl_Title1 + sl_Title2_5);
        titleText_x.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 20));  

        // Stem & Leaf proper        
        txtArea_SL_5.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR,12)); 
        strThisLine = slTitleLines;
        txtArea_SL_5.appendText(strThisLine);
        thisText = new Text(20, 19 * 0 + 40, strThisLine);      

        for (int printLines = 0; printLines < n_FiveLinesToPrint; printLines++)
        {
            tempString = fiveLineSL.get(printLines);
            strThisLine = tempString + "\n";
            thisText = new Text(20, 19 * printLines + 40, strThisLine);
            txtArea_SL_5.appendText(strThisLine);
        }         
    }
    
    private void setUpAnchorPane() {
        
        dragableAnchorPane = new DragableAnchorPane();    
        theSLAnchorPane = dragableAnchorPane.getTheAP(); 
        
        for (int iBtns = 0; iBtns < nRadioButtons; iBtns++) {
            
            sl_RadioButtons[iBtns].translateXProperty()
                                        .bind(txtArea.widthProperty()
                                        .divide(250.0)
                                        .multiply(65. * iBtns)
                                        .subtract(225.0));
        }

        
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(radioButtonRow, titleText_x, txtArea);
        
        dragableAnchorPane.makeDragable();  
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
        
        doTheGraph();
    }
    
    public void doTheGraph() {
        double paneWidth = initWidth;
        double titleWidth = titleText_x.getLayoutBounds().getWidth();
        double titleTextEdge = (paneWidth - titleWidth) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();

        AnchorPane.setTopAnchor(radioButtonRow, 0.01 * tempHeight);
        AnchorPane.setLeftAnchor(radioButtonRow, titleTextEdge * tempWidth);
        AnchorPane.setRightAnchor(radioButtonRow, titleTextEdge * tempWidth);
        AnchorPane.setBottomAnchor(radioButtonRow, 0.925 * tempHeight);
        
        AnchorPane.setTopAnchor(titleText_x, 0.075 * initHeight);        
        AnchorPane.setLeftAnchor(titleText_x, titleTextEdge * initWidth);
        AnchorPane.setRightAnchor(titleText_x, titleTextEdge * initWidth);
        AnchorPane.setBottomAnchor(titleText_x, 0.8 * initHeight);
        
        AnchorPane.setTopAnchor(txtArea, 0.20 * initHeight);
        AnchorPane.setLeftAnchor(txtArea, 0.0 * initWidth);
        AnchorPane.setRightAnchor(txtArea, 0.0 * initWidth);
        AnchorPane.setBottomAnchor(txtArea,0.0 * initHeight);        
    }
    
    public void makeTheRadioButtons() { 
        // Determine which graphs are initially shown
        // Here none are shown, by design; let the user choose
        radioButtonSettings = new boolean[nRadioButtons];
        for (int ithBox = 0; ithBox < nRadioButtons; ithBox++) {
            radioButtonSettings[ithBox] = false;
        }
        
        radioButtonRow = new AnchorPane();
        radioButtonRow.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        sl_RadioButtons = new RadioButton[nRadioButtons];
        
        ToggleGroup group = new ToggleGroup();

        for (int i = 0; i < nRadioButtons; i++) {
            sl_RadioButtons[i] = new RadioButton(strRadioButtonDescriptions[i]);
            group.getToggles().add(sl_RadioButtons[i]);
            sl_RadioButtons[i].setMaxWidth(Double.MAX_VALUE);
            sl_RadioButtons[i].setId(strRadioButtonDescriptions[i]);
            sl_RadioButtons[i].setSelected(radioButtonSettings[i]);
            sl_RadioButtons[i].setTextFill(Color.BLUE);
            sl_RadioButtons[i].setStyle(
                                "-fx-font-size: 14;" +
                                "-fx-border-insets: -5; " + 
                                "-fx-border-radius: 5;" +
                                "-fx-border-style: dotted;" +
                                "-fx-border-width: 0;" );

            sl_RadioButtons[i].setOnAction(e->{
                RadioButton tb = ((RadioButton) e.getTarget());
                
                String daID = tb.getId();
                Boolean checkValue = tb.selectedProperty().getValue();
                
                for (int ithID = 0; ithID < nRadioButtons; ithID++) {
                    if (daID.equals(strRadioButtonDescriptions[ithID])) {
                        radioButtonSettings[ithID] = (checkValue == true);
                        linePerStemChoice = ithID;
                        
                        switch(linePerStemChoice) {
                            case 0:
                                dragableAnchorPane.getTheAP()
                                                  .getChildren()
                                                  .removeAll(titleText_x, txtArea);
                                doOneLiners();
                                txtArea = txtArea_SL_1;
                                dragableAnchorPane.getTheAP()
                                                  .getChildren()
                                                  .addAll(titleText_x, txtArea); 
                            break;
                            
                            case 1:
                                dragableAnchorPane.getTheAP()
                                                  .getChildren()
                                                  .removeAll(titleText_x, txtArea);
                                doTwoLiners();
                                txtArea = txtArea_SL_2;
                                dragableAnchorPane.getTheAP()
                                                  .getChildren()
                                                  .addAll(titleText_x, txtArea);                                
                            break;
                            
                            case 2:
                                dragableAnchorPane.getTheAP()
                                                  .getChildren()
                                                  .removeAll(titleText_x, txtArea);
                                doFiveLiners();
                                txtArea = txtArea_SL_5;
                                dragableAnchorPane.getTheAP()
                                                  .getChildren()
                                                  .addAll(titleText_x, txtArea); 
                            break;
                            
                            default:
                                System.out.println("Ack! Switch fault at 314 in SL_View");  
                        }
                        doTheGraph();
                    }
                }

            }); //  end setOnAction
        }  
        sl_RadioButtons[0].setSelected(true);
        radioButtonRow.getChildren().addAll(sl_RadioButtons);
        }
    
    public Pane getTheContainingPane() { return containingPane; }
}


