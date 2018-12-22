/****************************************************************************
 *                        BBSL_View                                         * 
 *                         10/26/18                                         *
 *                          21:00                                           *
 ***************************************************************************/
package proceduresTwoUnivariate;

import genericClasses.DragableAnchorPane;
import genericClasses.StringUtilities;
import genericClasses.ResizableTextPane;
import java.util.ArrayList;
import javafx.event.EventHandler;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import genericClasses.QuantitativeDataVariable;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import t_Procedures.*;

public class BBSL_View {
    // POJOs
    
    boolean[] radioButtonSettings; 
    
    int maxSpaces, spacesNeeded, titleLeftNSize, titleRightNSize, 
        n_OneLineToPrint, n_TwoLinesToPrint, n_FiveLinesToPrint,
        oneLineWidth, oneLineHeight, twoLineWidth, twoLineHeight,
        fiveLineWidth, fiveLineHeight, charsInToVertChar, nTitleBlanks, 
        nVarNameBlanks, nNSizeBlanks, nSubTitleBlanks, nOrdMagBlanks, 
        nBBSLBlanks, nTitleText, linesPerStem;
    
    int nRadioButtons, nSpacers, linePerStemChoice;
    
    double initHoriz, initVert, initWidth, initHeight;  

    String sourceString, tempString, strThisLine, titleVarName, strSLNSize,
           strOrdMag, slTitle, leftDataLabel, rightDataLabel, strBBSL,
           strTitleBlanks, strVarNameBlanks, strNSizeBlanks, titleOrdMag, 
           strSubTitleBlanks, strOrdMagBlanks, strBBSLBlanks, strVarName,
           titleNSize, titleBBSL, strTitleTextBlanks;

    String bbsl_Title1, bbsl_Title2_1, bbsl_Title2_2, bbsl_Title2_5, 
           bbslTitleLines, preSLTitleText;
        
    String[] strRadioButtonDescriptions;
    static ArrayList<String> oneLineBBSL, twoLineBBSL, fiveLineBBSL; 
    ArrayList<String> oneLineStrings2Print, twoLinesStrings2Print, 
                      fiveLinesStrings2Print;
    
    // My objects
    BBSL_Model bbsl_Model;
    Explore_2Ind_Dashboard compare2Ind_Dashboard;
    Indep_t_Dashboard independent_t_Dashboard;
    DragableAnchorPane dragableAnchorPane; 
    ArrayList<QuantitativeDataVariable> bbslAllTheQDVs;
    ResizableTextPane rtp;
    
    // FX Objects
    AnchorPane theSLAnchorPane;
    Pane containingPane;   

    Text titleText, thisText;
    Text titleText_x, titleText_2;
    TextArea txtArea, txtArea_BBSL_1, txtArea_BBSL_2, txtArea_BBSL_5;

    HBox radioButtonRow;
    Region[] spacers;
    RadioButton[] bbsl_RadioButtons;  
   
    public BBSL_View(BBSL_Model bbsl_Model, Explore_2Ind_Dashboard compare2Ind_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        
        this.bbsl_Model = bbsl_Model;
        postInit();
    }
    
    public BBSL_View(BBSL_Model bbsl_Model, Indep_t_Dashboard independent_t_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        
        this.bbsl_Model = bbsl_Model;
        postInit();
    }
    
    private void postInit() {
        nRadioButtons = 3;
        nSpacers = 2;
        strRadioButtonDescriptions = new String[3];
        strRadioButtonDescriptions[0] = " One line / stem ";
        strRadioButtonDescriptions[1] = " Two lines / stem ";
        strRadioButtonDescriptions[2] = " Five lines / stem "; 
        
        oneLineBBSL = new ArrayList<>();
        twoLineBBSL = new ArrayList<>();
        fiveLineBBSL = new ArrayList<>();
        
        oneLineStrings2Print = new ArrayList<>();
        twoLinesStrings2Print = new ArrayList<>();
        fiveLinesStrings2Print = new ArrayList<>();
        
        oneLineBBSL = bbsl_Model.get_1_LineBBSL();
        twoLineBBSL = bbsl_Model.get_2_LineBBSL();
        fiveLineBBSL = bbsl_Model.get_5_LineBBSL();  
         
        n_OneLineToPrint = oneLineBBSL.size();
        n_TwoLinesToPrint = twoLineBBSL.size();
        n_FiveLinesToPrint = fiveLineBBSL.size();       
    }
    
    public void completeTheDeal() {
        setUpUI(); 
        makeTheButtonBox();
        setUpAnchorPane();
        formatTheTitleInfo();   // ***************************************
        containingPane = dragableAnchorPane.getTheContainingPane(); 
        //doTheGraph();
    }
    
    private void setUpUI() {     
        // Construct the magnitude
        double max = (int)bbsl_Model.getMax();
        int ordMag = (int)Math.floor(Math.log10(max));
        strOrdMag = "1|0 = " + Math.pow(10, ordMag) + "\n\n";

        txtArea = new TextArea();
        txtArea.heightProperty().addListener(ov-> {doTheGraph();});
        txtArea.widthProperty().addListener(ov-> {doTheGraph();});
        
        bbsl_Title1 = "Back to Back Stem & Leaf Plot\n";
        bbsl_Title2_1 = strRadioButtonDescriptions[0];
        bbsl_Title2_2 = strRadioButtonDescriptions[1];
        bbsl_Title2_5 = strRadioButtonDescriptions[2];
        
        formatTheTitleInfo();

        nTitleText = charsInToVertChar / 2 + 5 - bbsl_Title1.indexOf("k S");
        strTitleTextBlanks = StringUtilities.getStringOfNSpaces(nTitleText);        
        preSLTitleText = strTitleTextBlanks + bbsl_Title1;
        doOneLiners();
        txtArea = txtArea_BBSL_1;
         
        containingPane = new Pane();
    }
    
    public void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();    
        theSLAnchorPane = dragableAnchorPane.getTheAP(); 
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(radioButtonRow, titleText_x, txtArea);
        
        dragableAnchorPane.makeDragable();  
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
        
        doTheGraph();
    }
    
    public void makeTheButtonBox() { 
        // Determine which graphs are initially shown
        // Here none are shown, by design; let the user choose

        radioButtonRow = new HBox(10);
        // radioButtonRow.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        
        spacers = new Region[nSpacers];
        radioButtonSettings = new boolean[nRadioButtons];
        bbsl_RadioButtons = new RadioButton[nRadioButtons];
    
        
        ToggleGroup group = new ToggleGroup();

        for (int i = 0; i < nRadioButtons; i++) {
            bbsl_RadioButtons[i] = new RadioButton(strRadioButtonDescriptions[i]);
            group.getToggles().add(bbsl_RadioButtons[i]);
            bbsl_RadioButtons[i].setMaxWidth(Double.MAX_VALUE);
            bbsl_RadioButtons[i].setId(strRadioButtonDescriptions[i]);
            bbsl_RadioButtons[i].setSelected(radioButtonSettings[i]);
            bbsl_RadioButtons[i].setTextFill(Color.BLUE);
            bbsl_RadioButtons[i].setStyle(
                                "-fx-font-size: 14;" +
                                "-fx-border-insets: -5; " + 
                                "-fx-border-radius: 5;" +
                                "-fx-border-style: dotted;" +
                                "-fx-border-width: 0;" );

            bbsl_RadioButtons[i].setOnAction(e->{
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
                                txtArea = txtArea_BBSL_1;
                                dragableAnchorPane.getTheAP()
                                                  .getChildren()
                                                  .addAll(titleText_x, txtArea); 
                            break;
                            
                            case 1:
                                dragableAnchorPane.getTheAP()
                                                  .getChildren()
                                                  .removeAll(titleText_x, txtArea);
                                doTwoLiners();
                                txtArea = txtArea_BBSL_2;
                                dragableAnchorPane.getTheAP()
                                                  .getChildren()
                                                  .addAll(titleText_x, txtArea);                                
                            break;
                            
                            case 2:
                                dragableAnchorPane.getTheAP()
                                                  .getChildren()
                                                  .removeAll(titleText_x, txtArea);
                                doFiveLiners();
                                txtArea = txtArea_BBSL_5;
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
        
        for (int ithSpacer = 0; ithSpacer < nSpacers; ithSpacer++) {
            spacers[ithSpacer] = new Region();
            spacers[ithSpacer].setPrefSize(2, 2);
            radioButtonRow.setHgrow(spacers[ithSpacer], Priority.ALWAYS);
        }    
            
        for (int ithBtn = 0; ithBtn < nRadioButtons; ithBtn++) {
            radioButtonSettings[ithBtn] = false;
            radioButtonRow.setMargin(bbsl_RadioButtons[ithBtn], new Insets(10));
        }
        
        for (int ithPair = 0; ithPair < nSpacers; ithPair++) {
            radioButtonRow.getChildren().addAll(bbsl_RadioButtons[ithPair], spacers[ithPair]);
        }
        radioButtonRow.getChildren().add(bbsl_RadioButtons[nRadioButtons - 1]);   
         
        bbsl_RadioButtons[0].setSelected(true);

        }
    
    public void doTheGraph() {
        double paneWidth = initWidth;
        double titleWidth = titleText_x.getLayoutBounds().getWidth();
        double titleTextEdge = (paneWidth - titleWidth) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();

        AnchorPane.setTopAnchor(radioButtonRow, 0.01 * tempHeight);
        AnchorPane.setLeftAnchor(radioButtonRow, 0.05 * tempWidth);
        AnchorPane.setRightAnchor(radioButtonRow, 0.05 * tempWidth);
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
    
    private void doOneLiners() {
        // ****************************   OneLiners   ********************************************
        
        formatTheTitleInfo();   //   ************************
                
                
        txtArea_BBSL_1 = new TextArea();  // Where text will be drawn
        txtArea_BBSL_1.setWrapText(false);
        txtArea_BBSL_1.setEditable(false);
        txtArea_BBSL_1.setPrefColumnCount(50);
        txtArea_BBSL_1.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR,12)); 
        
        // Title area
        titleText_x = new Text(20, 20, bbsl_Title1 + bbsl_Title2_1);
        titleText_x.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 20));
          
        
        // Stem & Leaf proper    
        
        bbslTitleLines = titleBBSL + "\n" +
                         titleVarName + "\n" +
                         titleNSize + "\n" +
                         titleOrdMag;
        
        txtArea_BBSL_1.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR,12)); 
        strThisLine = bbslTitleLines;
        txtArea_BBSL_1.appendText(strThisLine);
        thisText = new Text(20, 19 * 0 + 40, strThisLine);
        
        tempString = oneLineBBSL.get(0);
        charsInToVertChar = tempString.indexOf('|');
        oneLineWidth = 0;
        
        
        for (int printLines = 0; printLines < n_OneLineToPrint; printLines++) { 
            if (oneLineWidth < tempString.length()) {
                oneLineWidth = tempString.length();
            }            
        }
        
        nTitleBlanks = charsInToVertChar / 8;
        nSubTitleBlanks = charsInToVertChar / 2;
        strTitleBlanks = StringUtilities.getStringOfNSpaces(nTitleBlanks);
        strSubTitleBlanks = StringUtilities.getStringOfNSpaces(nSubTitleBlanks);
        
 

        for (int printLines = 0; printLines < n_OneLineToPrint; printLines++)
        {
            tempString = oneLineBBSL.get(printLines);
            strThisLine = tempString + "\n";
            oneLineStrings2Print.add(strThisLine);
            txtArea_BBSL_1.appendText(strThisLine);
        }      
        oneLineHeight = n_OneLineToPrint  + 5;
    }
    
    private void doTwoLiners() {
            // ****************************   TwoLiners   ********************************************
        txtArea_BBSL_2 = new TextArea();  // Where text will be drawn
        txtArea_BBSL_2.setWrapText(false);
        txtArea_BBSL_2.setEditable(false);
        txtArea_BBSL_2.setPrefColumnCount(50);
        txtArea_BBSL_2.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR,12)); 

        // Title area
        titleText_x = new Text(20, 20, bbsl_Title1 + bbsl_Title2_1);
        titleText_x.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 20));
          
        
        // Stem & Leaf proper    
        
        bbslTitleLines = titleBBSL + "\n" +
                         titleVarName + "\n" +
                         titleNSize + "\n" +
                         titleOrdMag;
        
        txtArea_BBSL_2.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR,12)); 
        strThisLine = bbslTitleLines;
        txtArea_BBSL_2.appendText(strThisLine);
        thisText = new Text(20, 19 * 0 + 40, strThisLine);        
        
        
        
        
        tempString = twoLineBBSL.get(0);
        charsInToVertChar = tempString.indexOf('|');
        twoLineWidth = 0;
        for (int printLines = 0; printLines < n_TwoLinesToPrint; printLines++) { 
            if (twoLineWidth < tempString.length()) {
                twoLineWidth = tempString.length();
            }            
        }
        
        nTitleBlanks = charsInToVertChar / 8;
        nSubTitleBlanks = charsInToVertChar / 2;
        strTitleBlanks = StringUtilities.getStringOfNSpaces(nTitleBlanks);
        strSubTitleBlanks = StringUtilities.getStringOfNSpaces(nSubTitleBlanks);
        
        for (int printLines = 0; printLines < n_TwoLinesToPrint; printLines++)
        {
            tempString = twoLineBBSL.get(printLines);
            strThisLine = tempString + "\n";
        twoLinesStrings2Print.add(strThisLine);
            txtArea_BBSL_2.appendText(strThisLine);
        }      
        oneLineHeight = n_TwoLinesToPrint  + 5;      
    }
    
    private void doFiveLiners() {
        // ****************************   FiveLiners   ********************************************
        txtArea_BBSL_5 = new TextArea();  // Where text will be drawn
        txtArea_BBSL_5.setWrapText(false);
        txtArea_BBSL_5.setEditable(false);
        txtArea_BBSL_5.setPrefColumnCount(50);
        txtArea_BBSL_5.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR,12));
        
        // Title area
        titleText_x = new Text(20, 20, bbsl_Title1 + bbsl_Title2_1);
        titleText_x.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 20));
          
        
        // Stem & Leaf proper    
        
        bbslTitleLines = titleBBSL + "\n" +
                         titleVarName + "\n" +
                         titleNSize + "\n" +
                         titleOrdMag;
        
        txtArea_BBSL_5.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR,12)); 
        strThisLine = bbslTitleLines;
        txtArea_BBSL_5.appendText(strThisLine);
        thisText = new Text(20, 19 * 0 + 40, strThisLine);          
        
        
        
        tempString = fiveLineBBSL.get(0);
        charsInToVertChar = tempString.indexOf('|');
        fiveLineWidth = 0;
        for (int printLines = 0; printLines < n_FiveLinesToPrint; printLines++) { 
            if (fiveLineWidth < tempString.length()) {
                fiveLineWidth = tempString.length();
            }            
        }
        
        nTitleBlanks = charsInToVertChar / 8;
        nSubTitleBlanks = charsInToVertChar / 2;
        strTitleBlanks = StringUtilities.getStringOfNSpaces(nTitleBlanks);
        strSubTitleBlanks = StringUtilities.getStringOfNSpaces(nSubTitleBlanks);
        
        
        for (int printLines = 0; printLines < n_FiveLinesToPrint; printLines++)
        {
            tempString = fiveLineBBSL.get(printLines);
            strThisLine = tempString + "\n";
            fiveLinesStrings2Print.add(strThisLine);
            txtArea_BBSL_5.appendText(strThisLine);
        }      
        fiveLineHeight = n_FiveLinesToPrint  + 5;        
    }
    
    private void formatTheTitleInfo() {
    /***********************************************
    *        strXXX = the untranslated string      *
    *          nXXX = number of blanks to insert;  *
    *  strXXXBlanks = the string of blanks         *
    *         slXXX = the translated string        *
    ***********************************************/

        strBBSL = "Back to Back Stem & Leaf Plot";
        nBBSLBlanks = charsInToVertChar + 1 - strBBSL.indexOf("k S");
        strBBSLBlanks = StringUtilities.getStringOfNSpaces(nBBSLBlanks);
        titleBBSL = strBBSLBlanks + "Back to Back Stem & Leaf Plot";
        titleText = new Text(10, 20, strBBSL);
        titleText.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,16));
        
        leftDataLabel = bbsl_Model.getAllUDMs().get(1).getDataLabel();
        rightDataLabel = bbsl_Model.getAllUDMs().get(2).getDataLabel(); 

        strVarName = leftDataLabel + " vs. " + rightDataLabel;        
        nVarNameBlanks = charsInToVertChar + 1 - strVarName.indexOf("vs.");
        strVarNameBlanks = StringUtilities.getStringOfNSpaces(nVarNameBlanks);
        titleVarName = strVarNameBlanks + strVarName;
        
        titleLeftNSize = bbsl_Model.getAllUDMs().get(1).getLegalN();
        titleRightNSize = bbsl_Model.getAllUDMs().get(2).getLegalN();
        strSLNSize = strSubTitleBlanks + "N1 = " + String.valueOf(titleLeftNSize) + "    "
                          + "N2 = " + String.valueOf(titleRightNSize);
        nNSizeBlanks = charsInToVertChar + 4 - strSLNSize.indexOf("N2");
        strNSizeBlanks = StringUtilities.getStringOfNSpaces(nNSizeBlanks);
        titleNSize = strNSizeBlanks + strSLNSize;
        
        nOrdMagBlanks = charsInToVertChar + 1 - strOrdMag.indexOf("=");
        strOrdMagBlanks = StringUtilities.getStringOfNSpaces(nOrdMagBlanks); 
        titleOrdMag = strOrdMagBlanks + strOrdMag;
    }   //  formatTheTitleInfo
    
    EventHandler<MouseEvent> txtArea4StringsMouseHandler = new EventHandler<MouseEvent>() 
    {
        @Override
        public void handle(MouseEvent mouseEvent) 
        {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                String title;
                switch (linesPerStem) {
                    case 1:
                        title = "Back to back stem and leaf plot -- 1 line per stem";
                        rtp = new ResizableTextPane(title, oneLineStrings2Print);
                        break;
                    case 2:
                        title = "Back to back stem and leaf plot -- 2 lines per stem";
                        rtp = new ResizableTextPane(title, twoLinesStrings2Print);
                        break;
                    case 5:
                        title = "Back to back stem and leaf plot -- 5 lines per stem";
                        rtp = new ResizableTextPane(title, fiveLinesStrings2Print);
                        break;
                    default:
                        System.exit(184);
                }                
                rtp.doDaRest();  
                rtp.showAndWait();
            }
        }
    };
    
    public Pane getTheContainingPane() { return containingPane; }
}


 

