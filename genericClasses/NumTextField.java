/*
            Victor's NEW Code
*/

package genericClasses;

import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class NumTextField extends TextField {

    private String fieldContents = "";
    private TextField thisTF;
    private NumTextField numTF;
    private double missing, currentNumber;
    private boolean validNumber = false;
    private boolean badFormat = false;

    public NumTextField(String initString) {
        fieldContents = initString;
        thisTF = new TextField();
        missing = -123456.123456;
        initialize();
    }

    public NumTextField(String initString, double tempMissing) {
        fieldContents = initString;
        thisTF = new TextField();
        missing = tempMissing;
        initialize();
    }

    public NumTextField(String initString, double tempMissing, double tempWidth) {
        fieldContents = initString;
        thisTF = new TextField();
        missing = tempMissing;
        this.setMaxWidth(tempWidth);
        this.setMinWidth(tempWidth);
        initialize();
    }

    private void initialize() {

        numTF = this;

        numTF.setOnKeyReleased(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                checkup();
            }
        });
        
        numTF.setText(fieldContents);

    } // initialize

    private void checkup() { // keep an eye on the content of the textfield

        Double temp = new Double(0.0);

        // these characters are not yet a valid number, but also not an error:
        if (this.getText().equals("")
                || this.getText().equals(".")
                || this.getText().equals("-")
                || this.getText().equals("-.")) {
            badFormat = false;
            validNumber = false;
            currentNumber = missing;
        } else {
            try {
                temp = Double.valueOf(this.getText());
                currentNumber = temp.doubleValue();
                validNumber = true;
                badFormat = false;
            } catch (NumberFormatException num_e) {
                // if they have entered bad characters:
                validNumber = false;
                badFormat = true;
                currentNumber = missing;
            }
        }

        // Yell at the user if they have entered non-numeric characters
        // change the background to red, font to white
        if (badFormat) {
            this.setStyle("-fx-text-fill: white");
            this.setStyle("-fx-control-inner-background: red");
        } else {
            this.setStyle("-fx-text-fill: black");
            this.setStyle("-fx-control-inner-background: white");
        }

    } // checkup

    public boolean isValid() {
        checkup();
        return validNumber;
    }

    public double getDouble() {

        checkup();

        if (badFormat) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Non-numeric");
            alert.setHeaderText(null);
            alert.setContentText("You must enter a valid number.");
            alert.showAndWait();
            return missing;

        }

        return currentNumber;

    } // getDouble

    public int getInteger() {

        checkup();

        if (badFormat) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Non-numeric");
            alert.setHeaderText(null);
            alert.setContentText("You must enter a valid number.");
            alert.showAndWait();
            return -99;

        }

        return (int) Math.round(currentNumber);

    } // getInt

    public void setNumber(double tempNumber, int digits) {

        this.setText(String.format("%1." + digits + "f", tempNumber));

    }

}
