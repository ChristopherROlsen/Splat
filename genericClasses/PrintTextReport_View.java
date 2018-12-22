/****************************************************************************
 *                   PrintTextReport_View                                   * 
 *                         06/20/18                                         *
 *                          00:00                                           *
 ***************************************************************************/

package genericClasses;

import java.util.ArrayList;
import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class PrintTextReport_View {
    
   public double initHoriz, initVert, initWidth, initHeight; 
    
    public String strRTPTitle;
    public String sourceString, strTextPaneTitle, strTitleText;
    public ArrayList<String> stringsToPrint; 
    
    // My classes
    DragableAnchorPane dragableAnchorPane;
    ResizableTextPane rtp;
    
    // FX Objects
    public AnchorPane thePSAnchorPane;
    public Pane containingPane;   
    public TextArea txtArea4Strings; 
    public Text txtTitle, thisText;
    
    public PrintTextReport_View(double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        
        stringsToPrint = new ArrayList<>();
    }
    
    public void setUpUI() {
        strRTPTitle = "Regression diagnostics (advanced)";
        txtTitle = new Text(250, 20, strTitleText);
        txtTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20)); 
        txtArea4Strings = new TextArea();  // Where text will be drawn
        txtArea4Strings.setOnMouseClicked(txtArea4StringsMouseHandler);
        txtArea4Strings.setWrapText(false);
        txtArea4Strings.setEditable(false);
        txtArea4Strings.setPrefColumnCount(50);
        txtArea4Strings.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR,12)); 
        for (int printLines = 0; printLines < stringsToPrint.size(); printLines++)
        {
            String tempString = stringsToPrint.get(printLines);
            String strThisLine = tempString;
            thisText = new Text(20, 19 * printLines + 40, strThisLine);
            thisText.setFont(Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, 15));
            txtArea4Strings.appendText(strThisLine);
        }

        containingPane = new Pane();
    }
    
    public void completeTheDeal() {
        constructPrintLines();
        setUpUI();                 
        setUpAnchorPane();

        containingPane = dragableAnchorPane.getTheContainingPane();       
    }    
    
    
    public void setUpAnchorPane() {
        
        dragableAnchorPane = new DragableAnchorPane();
        // Construct the draggable
        thePSAnchorPane = dragableAnchorPane.getTheAP();
        // Finish the job -- make it dragable
        dragableAnchorPane.makeDragable();
        thePSAnchorPane.getChildren().addAll(txtTitle, txtArea4Strings);     
        double paneWidth = initWidth;
        double titleWidth = txtTitle.getLayoutBounds().getWidth();
        double titleTextEdge = (paneWidth - titleWidth) / (2 * paneWidth);
        
        AnchorPane.setTopAnchor(txtTitle, 0.0 * initHeight);        
        AnchorPane.setLeftAnchor(txtTitle, titleTextEdge * initWidth);
        AnchorPane.setRightAnchor(txtTitle, titleTextEdge * initWidth);
        AnchorPane.setBottomAnchor(txtTitle, 0.075 * initHeight);
        
        AnchorPane.setTopAnchor(txtArea4Strings, 0.075 * initHeight);
        AnchorPane.setLeftAnchor(txtArea4Strings, 0.0 * initWidth);
        AnchorPane.setRightAnchor(txtArea4Strings, 0.0 * initWidth);
        AnchorPane.setBottomAnchor(txtArea4Strings,0.0 * initHeight);

        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    public void constructPrintLines() {}    //  Reports construct their lines
    
    public void addNBlankLines(int thisMany) {
        for (int ithBlank = 0; ithBlank < thisMany; ithBlank++) {
            stringsToPrint.add("\n");
        }
    }
    
    public EventHandler<MouseEvent> txtArea4StringsMouseHandler = new EventHandler<MouseEvent>() 
    {
        @Override
        public void handle(MouseEvent mouseEvent) 
        {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                rtp = new ResizableTextPane(strRTPTitle, stringsToPrint);
                rtp.doDaRest();  
                rtp.showAndWait();
            }
        }
    }; 
    
    public Pane getTheContainingPane() { return containingPane; }    
}
