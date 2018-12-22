/************************************************************
 *                                                          *
 *                       Splat_VarList                      *
 *                          09/10/18                        *
 *                            18:00                         *
 ***********************************************************/
package splat;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

public class Splat_VarList {
    //POJO
    boolean numericOnly;
    
    int numVars;

    Double stdWidth = 125.0;
    Double stdHeight = 200.0;
    
     
    ObservableList<String> varNames;
    
    // My classes
     Splat_DataManager myData;
     PositionTracker waldo;
     
    // POJOs / FX
    ListView vPanel;

    public Splat_VarList(Splat_DataManager dm, boolean numeric, Double width, Double height) {
        myData = dm;
        waldo = new PositionTracker();
        waldo = dm.getPositionTracker();
        numericOnly = numeric;
        numVars = waldo.getNVarsInStruct();
        if (width != null) {
            stdWidth = width;
        }
        if (height != null) {
            stdHeight = height;
        }

        vPanel = new ListView();
        varNames = FXCollections.observableArrayList();
        for (int i = 0; i < numVars; i++) {
            if (!numericOnly) {
                varNames.add(myData.getVariableName(i));
            } else {
                if (myData.getVariableIsNumeric(i)) {
                    varNames.add(myData.getVariableName(i));
                }
            }
        }

        vPanel.setItems(varNames);
        vPanel.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    } // constructor

    public ListView getPane() {
        vPanel.setMinHeight(stdHeight);
        vPanel.setMaxHeight(stdHeight);
        vPanel.setMinWidth(stdWidth);
        vPanel.setMaxWidth(stdWidth);
        return vPanel;
    } // getPane
    
    public void clearList() {
        varNames.remove(0, varNames.size());
    }

    public void resetList() {
        varNames.remove(0, varNames.size());
        for (int i = 0; i < numVars; i++) {
            if (!numericOnly) {
                varNames.add(myData.getVariableName(i));
            } else {
                if (myData.getVariableIsNumeric(i)) {
                    varNames.add(myData.getVariableName(i));
                }
            }
        }
    }

    public void delVarName(ArrayList<String> thisOne) {
        varNames.removeAll(thisOne);
    }

    public void addVarName(ArrayList<String> addThis) {
        varNames.addAll(addThis);
    }

    public int getNumSelected() {
        return vPanel.getSelectionModel().getSelectedItems().size();
    }

    public ArrayList<String> getNamesSelected() {
        ArrayList<String> selectedNames = new ArrayList();
        selectedNames.addAll(vPanel.getSelectionModel().getSelectedItems());
        return selectedNames;
    } // getNamesSelected

    public ArrayList<Integer> getVarIndices() {
        ArrayList<String> allNames = new ArrayList();
        ArrayList<Integer> selectedVars = new ArrayList();
        allNames.addAll(varNames);
        for(String eachVar : allNames) {
            selectedVars.add(myData.getVariableIndex(eachVar));
        }
        return selectedVars;
    } // getSelected
    
    public ObservableList<String> getVarList() { return varNames; }
    
    public String toString() {
        for (int iVars = 0; iVars < numVars; iVars++) {
            System.out.println(varNames.get(iVars));
        }
        return "OK, that's the VarList";
    }

} // VarList
