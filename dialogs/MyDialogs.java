/****************************************************************************
 *                          MyDialogs                                       * 
 *                           05/15/18                                       *
 *                            18:00                                         *
 ***************************************************************************/

package dialogs;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

public class MyDialogs {
    //  POJOs
    int intResp;
    String stringResp;

    public void Msg(int type, String title, String announcement) {

        Alert.AlertType aType = Alert.AlertType.INFORMATION;
        if (type == 1) {
            aType = Alert.AlertType.WARNING;
        } else if (type == 2) {
            aType = Alert.AlertType.ERROR;
        }
        Alert alert = new Alert(aType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(announcement);
        alert.showAndWait();

    } // msgDiag

    public int YesNoCancel(int type, String title, String question) {
        int temp = 0;
        Alert.AlertType aType = Alert.AlertType.CONFIRMATION;
        if (type == 1) {
            aType = Alert.AlertType.WARNING;
        }
        Alert alert = new Alert(aType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(question);
        ButtonType buttonTypeOne = new ButtonType("Yes");
        ButtonType buttonTypeTwo = new ButtonType("No");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne) {
            temp = 1;
        } else if (result.get() == buttonTypeTwo) {
            temp = 2;
        }

        return temp;

    } // YesNoCancel

    public int YesNo(int type, String title, String question) {
        int temp = 0;
        Alert.AlertType aType = Alert.AlertType.CONFIRMATION;
        if (type == 1) {
            aType = Alert.AlertType.WARNING;
        }
        Alert alert = new Alert(aType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(question);
        ButtonType buttonTypeOne = new ButtonType("Yes");
        ButtonType buttonTypeTwo = new ButtonType("No");
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne) {
            temp = 1;
        } else if (result.get() == buttonTypeTwo) {
            temp = 2;
        }

        return temp;

    } // YesNo

    public String Input (String title, String question) {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(question);
        Optional<String> result = dialog.showAndWait();
        String returnMe;
        if (result.isPresent()) {
            returnMe = result.get();
        } else {
            returnMe = null;
        }
        
        return returnMe;

    } // InputDialog
    
}
