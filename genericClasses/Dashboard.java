/**************************************************
 *                   Dashboard                    *
 *                   05/16/18                     *
 *                     12:00                      *
 *************************************************/

/*
               Is boolean rather than Boolean OK?
*/
package genericClasses;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public abstract class Dashboard extends Stage {
    // POJOs
    protected Boolean checkBoxSettings[];
    
    // Max # of sixteenths = 7 at present;
    
    protected int nCheckBoxes, nSpacers;
    // protected int MAXCHECKBOXES = 7;
    
    final protected double W_ONE_16TH, H_ONE_16TH;
    protected double backGroundHeight, dashWidth, dashHeight,
                     upperLeftX, upperLeftY, lowerRightX, lowerRightY;
    protected final double CHECKBOXHEIGHT = 100.0;
    protected double[] sixteenths_across, sixteenths_down;
    
    protected double[] initWidth, initHeight; 
    
    private String returnStatus;
    protected final String cbStyle = "-fx-font-size: 14;" +
                                "-fx-border-insets: -5; " + 
                                "-fx-border-radius: 5;" +
                                "-fx-border-style: dotted;" +
                                "-fx-border-width: 0;" ;

    protected String containingPaneStyle = "-fx-background-color: white;" +
                                       "-fx-border-color: blue, blue;" + 
                                       "-fx-border-width: 4, 4;" +
                                       "-fx-border-radius: 0, 0;" +
                                       "-fx-border-insets: 0, 0;" +
                                       "-fx-border-style: solid centered, solid centered;";
    
    protected String[] checkBoxDescr;
    
    // POJOs / FX
    protected CheckBox[] checkBoxes;
    protected HBox checkBoxRow;
    protected VBox root;
    protected Pane backGround;
    final protected Rectangle2D visualBounds;   //  Visual area available for use
    Region[] spacer;
    final protected Scene scene;
    final protected Screen primaryScreen;

                
    public Dashboard(int numberOfCheckBoxes) { 
        primaryScreen = Screen.getPrimary();
        visualBounds = primaryScreen.getVisualBounds();
        upperLeftX = 0.025 * visualBounds.getMaxX();
        upperLeftY = 0.025 * visualBounds.getMaxY();
        lowerRightX = 0.975 * visualBounds.getMaxX();
        lowerRightY = 0.975 * visualBounds.getMaxY(); 
        
        nCheckBoxes = numberOfCheckBoxes;
        nSpacers = numberOfCheckBoxes + 1;
        
        initWidth = new double[nCheckBoxes];
        initHeight = new double[nCheckBoxes];

        checkBoxSettings = new Boolean[nCheckBoxes];    
        checkBoxes = new CheckBox[nCheckBoxes];  
        spacer = new Region[nSpacers];
        
        for (int i = 0; i < nCheckBoxes; i++) {
            initWidth[i] = 675; 
            initHeight[i] = 375;
            checkBoxSettings[i] = false;
        }

        dashWidth = lowerRightX - upperLeftX;
        dashHeight = lowerRightY - upperLeftY;
        W_ONE_16TH = 0.0625 * dashWidth;        
        H_ONE_16TH = 0.0625 * dashHeight;    
        setX(25); setY(25);
        setWidth(dashWidth);
        setHeight(dashHeight);


        sixteenths_across = new double[nCheckBoxes];
        sixteenths_down = new double[nCheckBoxes];
        for (int ith_16th = 0; ith_16th < nCheckBoxes; ith_16th++) {
           sixteenths_across[ith_16th] = upperLeftX + (ith_16th + 1) * W_ONE_16TH;
           sixteenths_down[ith_16th] = upperLeftY + (ith_16th + 1) * H_ONE_16TH;
        }

        // Left spacer min width apparently must be set to have space on the left end
        spacer[0] = new Region();
        spacer[0].setMinWidth(50);
        HBox.setHgrow(spacer[0], Priority.ALWAYS);
        
        for (int iSpacer = 1; iSpacer < nSpacers; iSpacer++) {
            spacer[iSpacer] = new Region();
            HBox.setHgrow(spacer[iSpacer], Priority.ALWAYS);
        }
        
        for (int i = 0; i < nCheckBoxes; i++) {
            checkBoxes[i] = new CheckBox();
            checkBoxes[i].setText("");
            checkBoxes[i].setPrefWidth(400);
            checkBoxes[i].setId("");
            checkBoxes[i].setSelected(false);
            checkBoxes[i].setStyle(cbStyle);
            
            if (checkBoxes[i].isSelected() == true) 
                checkBoxes[i].setTextFill(Color.GREEN);
            else
                checkBoxes[i].setTextFill(Color.RED);

            //  Set Checkbox Action
            checkBoxes[i].setOnAction(e->{
                CheckBox tb = ((CheckBox) e.getTarget());
                
                String daID = tb.getId();
                Boolean checkValue = tb.selectedProperty().getValue();
                // Reset selected color
                if (checkValue == true)
                    tb.setTextFill(Color.GREEN);
                else 
                    tb.setTextFill(Color.RED);

                for (int daCase = 0; daCase < nCheckBoxes; daCase++) {
                    if (daID.equals(checkBoxDescr[daCase])) {
                        checkBoxSettings[daCase] = (checkValue == true) ? true: false; 
                    }
                }
                putEmAllUp();
            });   
        }
        
        checkBoxRow = new HBox();
        checkBoxRow.setMinHeight(50);
        checkBoxRow.setAlignment(Pos.CENTER);
        
        // Sourround the checkBoxes with spacers
        for (int ithCB = 0; ithCB < nCheckBoxes; ithCB++) {
            checkBoxRow.getChildren().add(spacer[ithCB]);
            checkBoxRow.getChildren().add(checkBoxes[ithCB]);
        }
        
        setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                returnStatus = "Cancel";
                close();
            }
        });
        
        // Formerly MakeTheBackGround
        backGround = new Pane();
        backGround.setStyle("-fx-background-color: lightblue;");
        backGroundHeight = dashHeight /* - titleTextHeight - checkBoxHeight */;
        backGround.setPrefSize(dashWidth, backGroundHeight);  
        
        // populateTheBackGround();
        root = new VBox();
        
        Text titleText = new Text("");
        
        root.getChildren().addAll(titleText, checkBoxRow, backGround);
        scene = new Scene(root, dashWidth, dashHeight);

        setScene(scene);        
    }
        
    public String getReturnStatus() { return returnStatus; }
    //protected Rectangle2D getVisualBounds() { return visualBounds; }
        
    // Must put in extended
    public abstract void populateTheBackGround();
    public abstract void putEmAllUp();
  
}

