/************************************************************
 *                        Splat_Dialog                      *
 *                          11/10/18                        *
 *                            21:00                         *
 ***********************************************************/

/************************************************************
 *                         Subclasses                       *
 *                    Two_Variables_Dialog                  *
 *                      Inference_Dialog                    *
 ***********************************************************/
package dialogs;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import splat.Data_Manager;

public class Splat_Dialog extends Stage {
    
    public Button okButton, cancelButton;
    public String css, returnStatus;
    Data_Manager dm;
    
    public Splat_Dialog() { initialize(); }
    
    
    public Splat_Dialog(Data_Manager dm) {
        this.dm = dm;
        initialize();
    }
    
    private void initialize() {
        css = getClass().getResource("/css/StatDialogs.css").toExternalForm();
        //System.out.println("36 Splat Dialog, creating Splat_Dialog");
        okButton = new Button("");
        okButton.setOnAction((ActionEvent event) -> {
            //System.out.println("39 Splat Dialog, closing with returnStatus = Ok");
            returnStatus = "Ok";
            close();
        });
        
        cancelButton = new Button("");
        cancelButton.setOnAction((ActionEvent event) -> {
            //System.out.println("46 Splat Dialog, closing with returnStatus = Cancel");
            returnStatus = "Cancel";
            close();
        });
        
        setOnCloseRequest((WindowEvent we) -> {
            //System.out.println("52 Splat Dialog, closing with returnStatus = Cancel");
            returnStatus = "Cancel";
            close();
        });
        
    }
    
    public String getReturnStatus() { return returnStatus; }
     
}
