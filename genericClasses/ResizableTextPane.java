/******************************************************************
 *                  ResizableTextWindow                           *
 *                       05/16/18                                 *
 *                        12:00                                   *
 *****************************************************************/
package genericClasses;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import java.util.ArrayList;
import javafx.stage.Stage;

public class ResizableTextPane extends Stage {
    // POJOs
    int nCols = 55; //  Make this the longest string of those passed??
    int nLines; 
    double spacePerLine, spacePerColumnFudgeFactor, fudgeFactor,
           slopeYFudgeFactor, divideByFudgeFactor;
    
    String title;
    
    // FX objects
    BorderPane borderPane;
    GridPane gridPane;
    Pane textPane, quasiScene;
    Scene scene;
    ScrollPane scrollPane;
    Slider slider_FontSize, slider_LineSize;
    Stage textStage;
    Text[] textArray;
    
    private DoubleProperty fontSize = new SimpleDoubleProperty(10);
    private final DoubleProperty fudgyWudgy = new SimpleDoubleProperty(1.5);
    private final IntegerProperty color = new SimpleIntegerProperty(50);
 
    public ResizableTextPane(String title, ArrayList<String> daStrings) {
        this.title = title;
        nLines = daStrings.size();
        textPane = new Pane();

        textStage = new Stage();
        textArray = new Text[nLines];      
        
        slopeYFudgeFactor = 18.0;
        spacePerColumnFudgeFactor = 13.0; 
        divideByFudgeFactor = 90.;
        spacePerLine = 10;
        
        for (int textLine = 0; textLine < nLines; textLine++) {
            textArray[textLine] = new Text("  " + daStrings.get(textLine));
            textArray[textLine].setStroke(Color.BLACK);
            textArray[textLine].setFill(Color.WHITE);
            textArray[textLine].setX(0.0);
            textArray[textLine].setY(textLine * spacePerLine);
            textPane.getChildren().add(textArray[textLine]);
        }
        
        scrollPane = new ScrollPane(textPane);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        
        quasiScene = new Pane(scrollPane);  
        
        scrollPane.setMinHeight(Math.max(400, quasiScene.getHeight()));
        scrollPane.setMaxHeight(quasiScene.getHeight());
        scrollPane.setMinWidth(Math.max(800, quasiScene.getWidth()));
        scrollPane.setMaxWidth(quasiScene.getWidth());

        slider_FontSize = new Slider(1.5, 3.0, 1.5);
        slider_FontSize.setShowTickMarks(false);
        slider_FontSize.setShowTickLabels(false);
        slider_FontSize.setMajorTickUnit(.5);
        slider_FontSize.setBlockIncrement(.1);
        slider_FontSize.setMinorTickCount(5);
        slider_FontSize.setSnapToPixel(false);
        
        slider_FontSize.valueProperty().addListener(ov -> 
              fontSize.set(10 * slider_FontSize.getValue()));
        
        slider_LineSize = new Slider(2.0, 5.0, 2.5);
        slider_LineSize.setShowTickMarks(false);
        slider_LineSize.setShowTickLabels(false);
        slider_LineSize.setMajorTickUnit(10.0);
        slider_LineSize.setBlockIncrement(5.0);
        slider_LineSize.setMinorTickCount(10);
        slider_LineSize.setSnapToPixel(false);
        slider_LineSize.setOrientation(Orientation.VERTICAL);
        
        slider_LineSize.valueProperty().addListener(ov -> {
              spacePerLine = 5.0 * slider_LineSize.getValue();
              redoTheSizing();
              }); 
        
        borderPane = new BorderPane();
        borderPane.setLeft(slider_LineSize);
        borderPane.setTop(slider_FontSize);
        borderPane.setCenter(quasiScene);
        scene = new Scene(borderPane);  
 
    }
    
    public void doDaRest() {
        redoTheSizing();

        textPane.styleProperty().bind(Bindings.concat("-fx-font-family: \"Courier New\"; ",
                                                  /* "-fx-font-weight: black;", */
                                                  "-fx-font-size: ", fontSize.asString(), ";",
                                                  "-fx-base: rgb(200, 255, 255,",color.asString(),");"));
       
        
        fudgyWudgy.bind(quasiScene.widthProperty().divide(750.));
  
        quasiScene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                redoTheSizing();
    }
});
        
        quasiScene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                redoTheSizing();
    }
});

        this.setTitle(title);
        this.setScene(scene);
        this.sizeToScene();      
        redoTheSizing();  
    }
    
    private void redoTheSizing() {
        scrollPane.setMinHeight(Math.max(400, quasiScene.getHeight()));
        scrollPane.setMaxHeight(quasiScene.getHeight());
        scrollPane.setMinWidth(Math.max(800, quasiScene.getWidth()));
        scrollPane.setMaxWidth(quasiScene.getWidth());
        fudgeFactor = fudgyWudgy.get();
        for (int textLine = 0; textLine < nLines; textLine++) {
            textArray[textLine].setX(0.0);
            textArray[textLine].setY(textLine * spacePerLine);
        }
    }   //  end redoSizing   
}
