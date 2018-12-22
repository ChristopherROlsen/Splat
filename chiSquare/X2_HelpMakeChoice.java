/****************************************************************************
 *                      X2_HelpMakeChoice                                   * 
 *                           05/15/18                                       *
 *                            15:00                                         *
 ***************************************************************************/
package chiSquare;

import dialogs.X2ChoiceHelpDialog;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class X2_HelpMakeChoice extends Stage{
    
    // POJOs
    boolean x2ChoiceHelpDialogHidden, cleanReturn;
    
    String strTypeOfStudy, imagePath, strGOFExample, strHWExample, 
           strExperExample, strHomogExample, strIndepExample;
    
    //  My classes
    X2ChoiceHelpDialog x2ChoiceHelpDialog;

    // POJOs / FX
    Scene scene;
    Text txtExample;
    VBox root;
    
    public X2_HelpMakeChoice(String strTypeOfStudy) {
        this.strTypeOfStudy = strTypeOfStudy;
        root = new VBox();
        root.setAlignment(Pos.CENTER);
        
        switch (strTypeOfStudy) {
            case "GOF":
                System.out.println("GOF");
                strGOFExample = "      Meyer, et al (2005) [Nest Trees of Northern Flying Squirrels in the Sierra Nevada. Journal of Mammology" +
                                  "\n      86(2): 275-280.] examined the nesting preferences of northern flying squirrels.  They reported that  " +
                                  "\n      larger and taller trees were selected; snags were chosen more than live trees.  Their data, counts of" +
                                  "\n      values on a single variable (tree species) is summarized below.  The expected proportions are based on" + 
                                  "\n      the availability of the different trees.\n";
                txtExample = new Text(strGOFExample);

                imagePath = "/imageresources/FlyingSquirrels.jpg";
                break;
                                
            case "EXPERIMENT":
                strExperExample = "        Lafferty and Morris (1996) [Altered behavior of Parasitized killifish increases susceptibility " +
                                  "\n        to predation by bird final hosts.  Ecology 77:1390 - 1397] observed that infected fish spend more" +
                                  "\n        time near the water surface.  They investigated whether this increase led to greater predation" +
                                  "\n        by birds.  They assigned the 'infection status' of fish in three different tanks, and observed the " +
                                  "\n        'predation status,' i.e. whether the fish had been eaten.  Their data are summarized below.\n\n";
                txtExample = new Text(strExperExample);

                imagePath = "/imageresources/EatenByBirds.jpg";
                break;   
                
            case "HOMOGENEITY":
                strHomogExample = "     Heath (1995) [An Introduction to Experimental Design and Statistics for Biology, p195] reported data " +
                                  "\n     on the association between plant species and behavior of caterpillars.  Samples of behavior of  " +
                                  "\n     individual carpillars on four different plant species (the populations) were taken to determine if " +
                                  "\n     the distributions of caterpillar behavior were the same for each plant species.  Their summarized data" + 
                                  "\n     are shown below.\n\n";
                txtExample = new Text(strHomogExample);

                imagePath = "/imageresources/CaterpillarBehavior.jpg";
                break; 
                
            case "INDEPENDENCE":
                strIndepExample = "        Marcellini & MacKey (1970) [Habitat preferences of the Lizards, Sceloporus occidentalis and S. graciosus " +
                                  "\n        (Lacertilia, Iguanidae). Herpetologica 26(1):51-56] reported on the places that two lizard species like " +
                                  "\n        to hang out in northern California.  They took a single sample of these creatures and classified them" +
                                  "\n        by the values of species and location.  Their data are summarized below.\n\n";
                txtExample = new Text(strIndepExample);

                imagePath = "/imageresources/LizardHabitat.jpg";
                break; 
        }
        
        txtExample.setFill(Color.BLACK); 
        txtExample.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 16));
        
        Image image = new Image(imagePath);
        ImageView imageView = new ImageView(image);
        
        root.getChildren().addAll(txtExample, imageView);        
        
        switch (strTypeOfStudy) {
            case "GOF":  scene = new Scene(root, 800, 350); break; 
            case "EXPERIMENT": scene = new Scene(root, 800, 350); break; 
            case "HOMOGENEITY": scene = new Scene(root, 900, 375); break;      
            case "INDEPENDENCE": scene = new Scene(root, 1100, 325); break;
            default:
                System.out.println("Ack!! switch failure in chSquare, MainMenu"); 
        }

        setScene(scene);
        setTitle("X2ChoiceHelp");
        
        setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                event.consume();
                close();
            }
        });
    }   
}

