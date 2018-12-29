/************************************************************
 *                                                          *
 *                     Splat_VarCheckList                   *
 *                          07/12/18                        *
 *                            21:00                         *
 ***********************************************************/

// VarCheckList constructs a pane with all the variables for checking purposes.
// The pane is sized and then inserted into a control.

package splat;

import java.util.ArrayList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import genericClasses.*;

public class Var_CheckList {
    // POJOs
    int numVars;
    
    Double stdWidth = 125.0;
    Double stdHeight = 200.0;
    
    // POJOs / FX
    ArrayList<CheckBox> box;
    ScrollPane sPanel;
    Data_Manager myData;
    PositionTracker waldo;
    VBox vPanel;

    public Var_CheckList (Data_Manager dm, Boolean numericOnly, Double width, Double height) {
                
        myData = dm;
        waldo = dm.getPositionTracker();
        numVars = waldo.getNVarsInStruct();

        if (width != null) {
            stdWidth = width;
        }
        if (height != null) {
            stdHeight = height;
        }
        
        vPanel = new VBox(5);
        box = new ArrayList();
        for (int i = 0; i < numVars; i++) {
            box.add(new CheckBox());
            box.get(i).setPrefWidth(stdWidth);
            box.get(i).setText(myData.getVariableName(i));
            vPanel.getChildren().add(box.get(i));
            if ((numericOnly) & (!myData.getVariableIsNumeric(i))) {
                box.get(i).setDisable(true);
            }
        }
        
        sPanel = new ScrollPane(vPanel);

    } // constructor
    
    public ScrollPane getPane () {
        
        sPanel.setMinHeight(stdHeight);
        sPanel.setMaxHeight(stdHeight);
        sPanel.setMinWidth(stdWidth);
        sPanel.setMaxWidth(stdWidth);
        sPanel.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sPanel.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return sPanel;
        
    } // getPane
    
    public void setDisabled (boolean setMe) {

        for (int i = 0; i < numVars; i++) {
            box.get(i).setDisable(setMe);
        }

    }
    
    public ArrayList<Integer> getSelected () {
        
        ArrayList<Integer> selected = new ArrayList();
        
        for (int i = 0; i < waldo.getNVarsInStruct(); i++) {
            
            if (box.get(i).isSelected()) {
                selected.add(i);
            }
        }
        
        return selected;
        
    } // updateSelected    

} // VarList