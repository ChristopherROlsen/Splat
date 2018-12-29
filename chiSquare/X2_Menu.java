/***********************************************************
*                      X2Menu                              *
*                     05/15/18                             *
*                      15:00                               *
***********************************************************/
package chiSquare;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import splat.*;

public class X2_Menu {
    //  POJOs
    boolean x2Done;

    String strMainTitle, strGOFChoiceTitle, strGOFDescr,  
           strAssocTitle, strAssocDescr, strExperTitle, 
           strExperDescr, strHomogTitle, strHomogDescr, strIndepTitle, 
           strIndepDescr, chosenProcedure;        

    // My classes
    Data_Manager myData;
    MainMenu mainMenu;
    X2_HelpMakeChoice x2ChoiceHelp_Control;
    X2Assoc_Model association_Control;

    // POJOs / FX
    Button btnGOF, btnExperiment, btnHomog, btnIndep,  btnGOFHelp, 
           btnExperHelp, btnHomogHelp, btnIndepHelp, btnCancel;
    Font font24;
    GridPane gofGrid, expGrid, homogGrid, indepGrid;
    HBox gofRow, expRow, homogRow, indepRow, hBoxExperTitle,
         hBoxHomogTitle, hBoxIndepTitle, bottomBtnHBox;
    Pane middlePane;
    Stage stage;
    Text thisText, txtMainTitle, txtGOFChoiceTitle, txtGOFDescr, 
         txtAssocTitle, txtAssocDescr, txtExperTitle, txtExperDescr, 
         txtHomogTitle, txtHomogDescr, txtIndepTitle, 
         txtIndepDescr;

    TextArea myText;
    VBox root, middleVBox;

    public X2_Menu() {}    

    public void chooseProcedure(){
        
        stage = new Stage();

        root = new VBox();

        setUpTextLines();
        setUpButtonActions(); 
        constructMiddlePane();
        
        root.getChildren().addAll(middlePane, bottomBtnHBox);
        
        //                             w,    h
        Scene scene = new Scene(root, 850, 600);
        
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                String kCode = ke.getCode().toString();
                if (kCode.equals("ESCAPE")) {
                    chosenProcedure = "ESCAPE";
                    stage.close();
                }
            }
        });

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                event.consume();
                chosenProcedure = "ESCAPE";
                stage.close();
            }
        });       
        
        stage.setTitle("Chi square tests");
        stage.setScene(scene);
        stage.showAndWait();
    }    
        
    private void setUpTextLines() {
         
        strMainTitle  = "\n      Using the descriptions below, choose the desired chi square procedure     ";
        txtMainTitle = new Text(strMainTitle);
        txtMainTitle.setFill(Color.RED);
        txtMainTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 24));        
        
        strGOFChoiceTitle = "\n     Goodness of fit";
        txtGOFChoiceTitle = new Text(strGOFChoiceTitle);
        txtGOFChoiceTitle.setFill(Color.BLUE);
        txtGOFChoiceTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 20)); 
        
        strGOFDescr = "\n       A goodness of fit test is performed if a single categorical variable has been measured on data from a single sample." ;
        txtGOFDescr = new Text(strGOFDescr);
        txtGOFDescr.setFill(Color.BLACK); 
        txtGOFDescr.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 14));
 
        strAssocTitle = "\n     Test of association (Three possibilities)";
        txtAssocTitle = new Text(strAssocTitle);
        txtAssocTitle.setFill(Color.BLUE);
        txtAssocTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 20)); 
        
        strAssocDescr = "\n        In tests of association, two categorical variables are typically -- though not always -- recorded in a two-way table. " +
                        "\n        The interpretation of the variables differs for the following three contexts.";
        txtAssocDescr = new Text(strAssocDescr);
        txtAssocDescr.setFill(Color.BLACK); 
        txtAssocDescr.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 14));
        
        strExperTitle = "\n                           Context 1: An experimental study\n";
        txtExperTitle = new Text(strExperTitle);
        txtExperTitle.setFill(Color.GREEN);
        txtExperTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 18)); 
        
        strExperDescr = "        In an experimental study, the value of the 'treatment variable' is assigned to an experimental unit, and the value" +
                        "\n        of the 'response variable' is observed.";
        txtExperDescr = new Text(strExperDescr);
        txtExperDescr.setFill(Color.BLACK); 
        txtExperDescr.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 14));
        
        strHomogTitle = "\n                           Context 2: Homogeneity of proportions";
        txtHomogTitle = new Text(strHomogTitle);
        txtHomogTitle.setFill(Color.GREEN);
        txtHomogTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 18)); 
        
        strHomogDescr = "\n         In a study of homogeneity of proportions, sub-populations of an overall population are defined, and the value of the" +
                        "\n         variable of interest is observed in order to compare the sub-populations.";
        txtHomogDescr = new Text(strHomogDescr);
        txtHomogDescr.setFill(Color.BLACK); 
        txtHomogDescr.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 14));

        strIndepTitle = "\n                           Context 3: Independence of two variables";
        txtIndepTitle = new Text(strIndepTitle);
        txtIndepTitle.setFill(Color.GREEN);
        txtIndepTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 18)); 
        
        strIndepDescr = "\n         In a study of independence, two variables of interest are measured in a sample taken from a single population. The" +
                        "\n         purpose of an independence study is to determine if the two variables might be related in some way."; 
        txtIndepDescr = new Text(strIndepDescr);
        txtIndepDescr.setFill(Color.BLACK); 
        txtIndepDescr.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 14));                     
    }
    
    public void setUpButtonActions() {

        bottomBtnHBox = new HBox();
        //                                 top right bottom left
        bottomBtnHBox.setPadding(new Insets(15, 12, 15, 50));
        bottomBtnHBox.setSpacing(35);
        
        btnGOF = new Button("Goodness of fit");
        btnGOF.setLayoutX(50);
        btnGOFHelp = new Button("Example");
 
        btnExperiment = new Button ("Experimental Study");
        btnExperiment.setLayoutX(200);
        btnExperHelp = new Button("Example");
        
        btnHomog = new Button("Homogeneity of proportions");
        btnHomog.setLayoutX(375);
        btnHomogHelp = new Button("Example");
        
        btnIndep = new Button("Independence of variables");
        btnIndep.setLayoutX(600);
        btnIndepHelp = new Button("Example");
        
        bottomBtnHBox.getChildren().addAll(btnGOF, btnExperiment, btnHomog, btnIndep);  

        btnGOF.setOnAction((ActionEvent event) -> {
            chosenProcedure = "GOF";
            stage.close();
        });
        
        btnGOFHelp.setOnAction((ActionEvent event) -> {
            X2_HelpMakeChoice x2ChoiceHelp = new X2_HelpMakeChoice("GOF");
            x2ChoiceHelp.showAndWait();
            btnGOF.requestFocus();
        }); 
         
        btnExperiment.setOnAction((ActionEvent event) -> {
            chosenProcedure = "EXPERIMENT";
            stage.close();
        });
        
        btnExperHelp.setOnAction((ActionEvent event) -> {
            X2_HelpMakeChoice x2ChoiceHelp = new X2_HelpMakeChoice("EXPERIMENT");
            x2ChoiceHelp.showAndWait();
            btnExperiment.requestFocus();
        });

        btnHomog.setOnAction((ActionEvent event) -> {
            chosenProcedure = "HOMOGENEITY";
            stage.close();
        });
        
        btnHomogHelp.setOnAction((ActionEvent event) -> {
            X2_HelpMakeChoice x2ChoiceHelp = new X2_HelpMakeChoice("HOMOGENEITY");
            x2ChoiceHelp.showAndWait();
            btnHomog.requestFocus();
        });

        btnIndep.setOnAction((ActionEvent event) -> {
            chosenProcedure = "INDEPENDENCE";
            stage.close();
        });
        
        btnIndepHelp.setOnAction((ActionEvent event) -> {
            X2_HelpMakeChoice x2ChoiceHelp = new X2_HelpMakeChoice("INDEPENDENCE");
            x2ChoiceHelp.showAndWait();
            btnIndep.requestFocus();
        });
    }
    
    private void constructMiddlePane() {
 
        //  Stuff in middle
        middlePane = new Pane();
        middlePane.setPrefHeight(525);
        middlePane.setPrefWidth(850);

        ColumnConstraints ccText = new ColumnConstraints(750);
        ColumnConstraints ccBtn = new ColumnConstraints(80);
        
        gofGrid = new GridPane();
        gofGrid.add(txtGOFDescr, 0, 0);
        gofGrid.add(btnGOFHelp, 1, 0);
        gofGrid.getColumnConstraints().addAll(ccText, ccBtn);

        expGrid = new GridPane();
        expGrid.add(txtExperDescr, 0, 0);
        expGrid.add(btnExperHelp, 1, 0);    
        expGrid.getColumnConstraints().addAll(ccText, ccBtn);
        
        homogGrid = new GridPane();
        homogGrid.add(txtHomogDescr, 0, 0);
        homogGrid.add(btnHomogHelp, 1, 0);         
        homogGrid.getColumnConstraints().addAll(ccText, ccBtn);
        
        indepGrid = new GridPane();
        indepGrid.add(txtIndepDescr, 0, 0);
        indepGrid.add(btnIndepHelp, 1, 0);   
        indepGrid.getColumnConstraints().addAll(ccText, ccBtn);
        
        hBoxExperTitle = new HBox(txtExperTitle);
        hBoxHomogTitle = new HBox(txtHomogTitle);
        hBoxIndepTitle = new HBox(txtIndepTitle);
        
        middleVBox = new VBox();
        middleVBox.getChildren()
            .addAll(txtMainTitle, txtGOFChoiceTitle, gofGrid, 
                    txtAssocTitle, txtAssocDescr, 
                    hBoxExperTitle, expGrid, hBoxHomogTitle, homogGrid, 
                    hBoxIndepTitle, indepGrid);
       
        middlePane.getChildren().add(middleVBox);
    }
    
    public String getChosenProcedure() {return chosenProcedure;}
}




