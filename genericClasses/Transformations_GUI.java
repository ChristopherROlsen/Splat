/************************************************************
 *                    Transformations_GUI                   *
 *                          12/26/18                        *
 *                            00:00                         *
 ***********************************************************/

package genericClasses;

import utilityClasses.Transformations_Calculations;
import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import splat.PositionTracker;
import java.util.ArrayList;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import smarttextfield.SmartTextField;
import smarttextfield.SmartTextFieldHandler;
import splat.*;

public class Transformations_GUI {
    // POJOs
    int var1Index, var2Index, functionIndex, unaryIndex, operationIndex,
        nOriginalDataPoints, nLinTransVarsParams, nlinTransFuncsParams, nLinComVarsParams,
        maxResizableStrSize, controlWidth, controlHeight, numVars, nUnaryFuncsParams;
        
    double alphaValue, betaValue, widthFudgeFactor, tempDouble;
    ArrayList<Double> alDouble_AllTheData;
    
    String strAlphaValue, strBetaValue, strVar_1_Value, strVar_2_Value, strUnaryValue, 
            strFunctionValue, strOperationValue, strText1, controlTitle,
            chosenProcedure;
    
    String newVarName = "New Var Name";
    String initBinaryOp, initUnaryOp, initFunc;
    String[] varNames, funcNames, operNames, unaryNames, strTransformedData, 
             linTransVarsParams, linTransFuncsParams, linComVarsParams,
             resizableStrs;
    
    ArrayList<String> alStr_Var_1_Data, alStr_Var_2_Data;  
    ObservableList<String> variableNames, functionNames, operationNames;
   
    // My classes
    Data_Manager myData;
    PositionTracker positionTracker;
    SmartTextField stfUnaryFunctions;  //  ???  What do these do?
    SmartTextField[] stfLinearTransformation, stfLinearCombination, stfLinearTransformationFunctions;
    ArrayList<SmartTextField> linTransVarsPanel, linComVarsPanel, 
                              linTransFuncsPanel, unaryFuncsPanel;
    SmartTextFieldHandler linTransVarsHandler, linComVarsHandler, 
            linTransFuncsHandler, unaryFuncsHandler;
    
    QuantitativeDataVariable ndv;
    
    Transformations_Calculations transCalc;

    // POJOs / FX
    Button btnOK, btnCancel;
    ChoiceBox <String> cbVarChoice1, cbVarChoice2, cbFuncChoice, cbUnaryChoice,
                       cbOperChoice;
    HBox hBoxChoices, hBoxOkCancel;
    Label strEqual, strPlus, strMult1, strMult2, strLeftParen, strRightParen;
    Stage controlStage;
    Text txtResizableText;
    TextField tfAlpha, tfBeta, tfNewVariable;
    VBox root;

    private final DoubleProperty fontSize = new SimpleDoubleProperty(10);
    private final IntegerProperty color = new SimpleIntegerProperty(50);
    
    public Transformations_GUI(Data_Manager myData) {
        this.myData = myData;
        positionTracker = myData.getPositionTracker();
        numVars = positionTracker.getNVarsInStruct();
        nOriginalDataPoints = positionTracker.getNCasesInStruct();
        varNames = new String[numVars];

        for (int i = 0; i < numVars; i++) {
            varNames[i] = myData.getVariableName(i);
        }        

        transCalc = new Transformations_Calculations();
        initGUI();
    }

    public void linTransVars() {
        linTransVarsParams = new String[] {newVarName, "0.0", "1.0"};
        // Initialize numeric choices
        strAlphaValue = linTransVarsParams[1];
        strBetaValue = linTransVarsParams[2];
        nLinTransVarsParams = linTransVarsParams.length;    
        stfLinearTransformation = new SmartTextField[nLinTransVarsParams];
        linTransVarsPanel = new ArrayList<>();

        initParameters("linearTransformationOfAVariable");
        
        linTransVarsHandler = new SmartTextFieldHandler();

        for (int ithLinTransVars = 0; ithLinTransVars < nLinTransVarsParams; ithLinTransVars++) {
            int tempPrev = (ithLinTransVars - 1) % nLinTransVarsParams;
            int tempNext = (ithLinTransVars + 1) % nLinTransVarsParams;
            stfLinearTransformation[ithLinTransVars] = new SmartTextField(linTransVarsHandler,
                                                         tempPrev,
                                                         tempNext);
            stfLinearTransformation[ithLinTransVars].setEditable(true);
            stfLinearTransformation[ithLinTransVars].getTextField().setText(linTransVarsParams[ithLinTransVars]); 
            linTransVarsPanel.add(ithLinTransVars, stfLinearTransformation[ithLinTransVars]);
        }

        linTransVarsHandler.setHandlerArrayList(linTransVarsPanel);
        tfNewVariable = stfLinearTransformation[0].getTextField();  //*********************
        stfLinearTransformation[1].setSmartTextField_MB_REAL(true);
        stfLinearTransformation[2].setSmartTextField_MB_REAL(true);
    
        tfAlpha = stfLinearTransformation[1].getTextField();  // Just to shorten variable name
        tfBeta = stfLinearTransformation[2].getTextField();   // for easier reading
        setUpAlphaAndBeta();
        
        hBoxChoices = new HBox();
        hBoxChoices.getChildren().addAll(tfNewVariable, strEqual, tfAlpha,
                                     strPlus, tfBeta, strMult1, cbVarChoice1);
        
        Button okBtnLinTransVars = new Button("OK");
        okBtnLinTransVars.setOnAction(e -> okLinTransVar());
        
        Button cancelBtnLinTransVars = new Button("Cancel");
        cancelBtnLinTransVars.setOnAction(e -> closeControlStage());
        
        hBoxOkCancel = new HBox();
        hBoxOkCancel.getChildren().addAll(okBtnLinTransVars, cancelBtnLinTransVars); 
        showTheControlAndWait();
    }
    
    public void linearCombOfVariables() {
        linComVarsParams = new String[] {newVarName, "1.0", "1.0"};
        // Initialize numeric choices
        strAlphaValue = linComVarsParams[1];
        strBetaValue = linComVarsParams[2];
        nLinComVarsParams = linComVarsParams.length;         
        stfLinearCombination = new SmartTextField[nLinComVarsParams];
        linComVarsPanel = new ArrayList<>();          

        initParameters("linearCombinationOfVariables");
        
        linComVarsHandler = new SmartTextFieldHandler();

        for (int ithLinComVars = 0; ithLinComVars < nLinComVarsParams; ithLinComVars++) {
            int tempPrev = (ithLinComVars - 1) % nLinComVarsParams;
            int tempNext = (ithLinComVars + 1) % nLinComVarsParams;
            stfLinearCombination[ithLinComVars] = new SmartTextField(linComVarsHandler,
                                                         tempPrev,
                                                         tempNext);
            stfLinearCombination[ithLinComVars].setEditable(true);
            stfLinearCombination[ithLinComVars].getTextField().setText(linComVarsParams[ithLinComVars]); 
            linComVarsPanel.add(ithLinComVars, stfLinearCombination[ithLinComVars]);
        }

        linComVarsHandler.setHandlerArrayList(linComVarsPanel);
        tfNewVariable = stfLinearCombination[0].getTextField();  //*********************
        stfLinearCombination[1].setSmartTextField_MB_REAL(true);
        stfLinearCombination[2].setSmartTextField_MB_REAL(true);

        tfAlpha = stfLinearCombination[1].getTextField();  // Short variable name
        tfBeta = stfLinearCombination[2].getTextField();  // Short variable name
        setUpAlphaAndBeta();

        hBoxChoices = new HBox();
        hBoxChoices.getChildren().addAll(tfNewVariable, strEqual, tfAlpha, strMult1, cbVarChoice1,
                                     strPlus, tfBeta, strMult2, cbVarChoice2);
        
        Button okBtnLinComVars = new Button("OK");
        okBtnLinComVars.setOnAction(e -> okLinComVars());
        Button cancelBtnLinComVars = new Button("Cancel");
        cancelBtnLinComVars.setOnAction(e -> closeControlStage());     
        
        hBoxOkCancel = new HBox();
        hBoxOkCancel.getChildren().addAll(okBtnLinComVars, cancelBtnLinComVars);
        showTheControlAndWait();
    }
    
    public void nonLinTransVars() {
        linTransFuncsParams = new String[] {newVarName, "0.0", "1.0"}; 
        // Initialize numeric choices
        strAlphaValue = linTransFuncsParams[1];
        strBetaValue = linTransFuncsParams[2];
        nlinTransFuncsParams = linTransFuncsParams.length;    
        stfLinearTransformationFunctions = new SmartTextField[nlinTransFuncsParams];
        linTransFuncsPanel = new ArrayList<>();   

        initParameters("linearTransformationWithFunction");
        
        linTransFuncsHandler = new SmartTextFieldHandler();
        
        for (int ithLinTransFunc = 0; ithLinTransFunc < nlinTransFuncsParams; ithLinTransFunc++) {
            int tempPrev = (ithLinTransFunc - 1) % nlinTransFuncsParams;
            int tempNext = (ithLinTransFunc + 1) % nlinTransFuncsParams;
            stfLinearTransformationFunctions[ithLinTransFunc] = new SmartTextField(linTransFuncsHandler,
                                                         tempPrev,
                                                         tempNext);
            stfLinearTransformationFunctions[ithLinTransFunc].setEditable(true);
            stfLinearTransformationFunctions[ithLinTransFunc].getTextField().setText(linTransFuncsParams[ithLinTransFunc]); 
            linTransFuncsPanel.add(ithLinTransFunc, stfLinearTransformationFunctions[ithLinTransFunc]);
        }

        linTransFuncsHandler.setHandlerArrayList(linTransFuncsPanel);
        tfNewVariable = stfLinearTransformationFunctions[0].getTextField();  //*********************
        stfLinearTransformationFunctions[1].setSmartTextField_MB_REAL(true);
        stfLinearTransformationFunctions[2].setSmartTextField_MB_REAL(true);

        tfAlpha = stfLinearTransformationFunctions[1].getTextField();  // Short variable name
        tfBeta = stfLinearTransformationFunctions[2].getTextField();  // Short variable name
        setUpAlphaAndBeta();

        hBoxChoices = new HBox();
        hBoxChoices.getChildren().addAll(tfNewVariable, strEqual, tfAlpha,
                                     strPlus, tfBeta, strMult1, cbFuncChoice, 
                                     strLeftParen, cbVarChoice1, strRightParen);
        
        Button okBtnLinTransFuncs = new Button("OK");
        okBtnLinTransFuncs.setOnAction(e -> okLinTransWithFuncs(strFunctionValue));
        
        Button cancelBtnLinTransFuncs = new Button("Cancel");
        cancelBtnLinTransFuncs.setOnAction(e -> closeControlStage());
        
        hBoxOkCancel = new HBox();
        hBoxOkCancel.getChildren().addAll(okBtnLinTransFuncs, cancelBtnLinTransFuncs);
        showTheControlAndWait();
    }
    
    public void unaryOperationOnVar() {
        nUnaryFuncsParams = unaryNames.length;   
        unaryFuncsHandler = new SmartTextFieldHandler();
        stfUnaryFunctions = new SmartTextField(unaryFuncsHandler);
        stfUnaryFunctions.getTextField().setText(newVarName);
        unaryFuncsPanel = new ArrayList<>();   

        initParameters("unaryOperationOnVariable");

        stfUnaryFunctions.getTextField().setEditable(true);        
        tfNewVariable = stfUnaryFunctions.getTextField();  //*********************
        hBoxChoices = new HBox();
        hBoxChoices.getChildren().addAll(tfNewVariable, strEqual, cbUnaryChoice, 
                                     strLeftParen, cbVarChoice1, strRightParen);
        
        Button okBtnUnaryFuncs = new Button("OK");
        okBtnUnaryFuncs.setOnAction(e -> okUnaryOperation(chosenProcedure));
        
        Button cancelBtnUnaryFuncs = new Button("Cancel");
        cancelBtnUnaryFuncs.setOnAction(e -> closeControlStage());
        
        hBoxOkCancel = new HBox();
        hBoxOkCancel.getChildren().addAll(okBtnUnaryFuncs, cancelBtnUnaryFuncs);
        showTheControlAndWait();        
    }    
    
    
    public void binaryOpsWithVariables() {
        hBoxChoices = new HBox();
        hBoxChoices.getChildren().addAll(tfNewVariable, strEqual, cbVarChoice1, 
                                         cbOperChoice, cbVarChoice2);

        initParameters("binaryOperationOfVariables");      
        
        Button okButtonOpsWithVars = new Button("OK");
        okButtonOpsWithVars.setOnAction(e -> okBinaryOpsWithVars(strOperationValue));
        Button cancelButtonOpsWithVars = new Button("Cancel");
        cancelButtonOpsWithVars.setOnAction(e -> closeControlStage());  
        
        hBoxOkCancel = new HBox();
        hBoxOkCancel.getChildren().addAll(okButtonOpsWithVars, cancelButtonOpsWithVars);
        showTheControlAndWait();
    }
    
    public void var1ItemChanged(ObservableValue <? extends String> observable,
                                String oldValue,
                                String newValue) {
        System.out.println("Var1ItemChanged: old = " + oldValue + " new = " + newValue);
        strVar_1_Value = newValue;

    }
    
    public void var1IndexChanged(ObservableValue <? extends Number> observable,
                                 Number oldValue,
                                 Number newValue) {
        System.out.println("Var1IndexChanged: old = " + oldValue + " new = " + newValue);
        var1Index = (int)newValue;
        alStr_Var_1_Data = myData.getAllTheColumns().get(var1Index).getTheCases();
    }
    
    public void var2ItemChanged(ObservableValue <? extends String> observable,
                                String oldValue,
                                String newValue) {
        System.out.println("Var2ItemChanged: old = " + oldValue + " new = " + newValue);
        strVar_2_Value = newValue;
        
    }
    
    public void var2IndexChanged(ObservableValue <? extends Number> observable,
                                 Number oldValue,
                                 Number newValue) {
        System.out.println("Var2IndexChanged: old = " + oldValue + " new = " + newValue);
        var2Index = (int)newValue;
        alStr_Var_2_Data = myData.getAllTheColumns().get(var2Index).getTheCases();
    }
    
    public void functionItemChanged(ObservableValue <? extends String> observable,
                                    String oldValue,
                                    String newValue) {
        System.out.println("FunctionItemChanged: old = " + oldValue + " new = " + newValue);
        strFunctionValue = newValue;
    }
    
    public void functionIndexChanged(ObservableValue <? extends Number> observable,
                                     Number oldValue,
                                     Number newValue) {
        System.out.println("FunctionIndexChanged:  old = " + oldValue + " new = " + newValue);
        functionIndex = (int)newValue;
    }
    
    public void unaryItemChanged(ObservableValue <? extends String> observable,
                                    String oldValue,
                                    String newValue) {
        System.out.println("UnaryItemChanged: old = " + oldValue + " new = " + newValue);
        strUnaryValue = newValue;
    }
    
    public void unaryIndexChanged(ObservableValue <? extends Number> observable,
                                     Number oldValue,
                                     Number newValue) {
        System.out.println("UnaryIndexChanged:  old = " + oldValue + " new = " + newValue);
        unaryIndex = (int)newValue;
        chosenProcedure = unaryNames[unaryIndex];
        System.out.println("345 trans_GUI, chosenProcedure = " + chosenProcedure);
    }
    
    public void operationItemChanged(ObservableValue <? extends String> observable,
                                    String oldValue,
                                    String newValue) {
        System.out.println("OperationItemChanged: old = " + oldValue + " new = " + newValue);
        strOperationValue = newValue;
    }
    
    public void operationIndexChanged(ObservableValue <? extends Number> observable,
                                     Number oldValue,
                                     Number newValue) {
        System.out.println("OperationIndexChanged:  old = " + oldValue + " new = " + newValue);
        operationIndex = (int)newValue;
    }
    
    public void okLinTransVar() {
        // if (somethingNotFilledIn) {
        //   doAlertThing
        //   close stage
        // }
        setUpTransformation();       
        strTransformedData = transCalc.linearTransformation(alStr_Var_1_Data, alphaValue, betaValue);
        addVarToStructure();
        closeControlStage();
    }
    
    public void okLinComVars() {
        // if (somethingNotFilledIn) {
        //   doAlertThing
        //   close stage
        // }
        setUpTransformation();
        strTransformedData = transCalc.linearCombinationOfVars(alStr_Var_1_Data, 
                                                         alStr_Var_2_Data, 
                                                         alphaValue, 
                                                         betaValue);
        addVarToStructure();
        closeControlStage();
    }
  
    public void okLinTransWithFuncs(String ltfProcedure) {
        setUpTransformation();
        
        strTransformedData = transCalc.linTransWithFunc(alStr_Var_1_Data, 
                                                 ltfProcedure, 
                                                 alphaValue, 
                                                 betaValue);       
        addVarToStructure();
        closeControlStage();
    }

    public void okUnaryOperation(String uOpProcedure) {
        strTransformedData = new String[nOriginalDataPoints];  
        strTransformedData = transCalc.unaryOpsOfVars(alStr_Var_1_Data,
                                                      uOpProcedure);      
        addVarToStructure();
        closeControlStage();
    }   //  end of unary
    
    public void okBinaryOpsWithVars(String strOperationValue) {
        // if (somethingNotFilledIn) {
        //   doAlertThing
        //   close stage
        // }

        strTransformedData = new String[nOriginalDataPoints];
        strTransformedData = transCalc.binaryOpsOfVars(alStr_Var_1_Data,
                                                       strOperationValue,
                                                       alStr_Var_2_Data);
        addVarToStructure();
        closeControlStage();
    }
    
    public void closeControlStage() { controlStage.close(); }
    
    private void initGUI() {
        funcNames = new String[] {"ln", "log10", "sqrt", "recip", "exp10", "exp"};
        unaryNames = new String[] {"z-score", "percentile", "rank", "rankits"};
        operNames = new String[] {"+", "-", "*", "/"};
        
        resizableStrs = new String[4];
        tfNewVariable = new TextField ("Variable name");
        tfNewVariable.setPadding(new Insets(5, 0, 5, 10));
        tfAlpha = new TextField();
        tfAlpha = new TextField();
        
        variableNames = FXCollections.<String>observableArrayList(varNames);
        functionNames = FXCollections.<String>observableArrayList(funcNames);
        operationNames = FXCollections.<String>observableArrayList(operNames);
        
        cbVarChoice1 = new ChoiceBox();
        cbVarChoice1.getItems().addAll(variableNames);
        cbVarChoice1.setMinWidth(150);
        cbVarChoice1.setMaxWidth(300);
        cbVarChoice1.getSelectionModel().select(0);
        cbVarChoice1.getSelectionModel().selectedItemProperty()
                                        .addListener(this::var1ItemChanged);
        cbVarChoice1.getSelectionModel().selectedIndexProperty()
                                        .addListener(this::var1IndexChanged);
        
        cbVarChoice2 = new ChoiceBox();
        cbVarChoice2.getItems().addAll(variableNames);
        cbVarChoice2.setMinWidth(150);
        cbVarChoice2.setMaxWidth(300);     
        cbVarChoice2.getSelectionModel().select(0);
        cbVarChoice2.getSelectionModel().selectedItemProperty()
                                        .addListener(this::var2ItemChanged);
        cbVarChoice2.getSelectionModel().selectedIndexProperty()
                                        .addListener(this::var2IndexChanged);   
        
        cbFuncChoice = new ChoiceBox();
        cbFuncChoice.getItems().addAll(functionNames);
        cbFuncChoice.setMinWidth(75);
        cbFuncChoice.setMaxWidth(150);    
        cbFuncChoice.getSelectionModel().select(0);
        cbFuncChoice.getSelectionModel().selectedItemProperty()
                                        .addListener(this::functionItemChanged);
        cbFuncChoice.getSelectionModel().selectedIndexProperty()
                                        .addListener(this::functionIndexChanged);
        
        cbUnaryChoice = new ChoiceBox();
        cbUnaryChoice.getItems().addAll(unaryNames);
        cbUnaryChoice.setMinWidth(75);
        cbUnaryChoice.setMaxWidth(150);    
        cbUnaryChoice.getSelectionModel().select(0);
        cbUnaryChoice.getSelectionModel().selectedItemProperty()
                                        .addListener(this::unaryItemChanged);
        cbUnaryChoice.getSelectionModel().selectedIndexProperty()
                                        .addListener(this::unaryIndexChanged);
        
        cbOperChoice = new ChoiceBox();
        cbOperChoice.getItems().addAll(operationNames);
        cbOperChoice.setMinWidth(75);
        cbOperChoice.setMaxWidth(150);   
        cbOperChoice.getSelectionModel().select(0);
        cbOperChoice.getSelectionModel().selectedItemProperty()
                                        .addListener(this::operationItemChanged);
        cbOperChoice.getSelectionModel().selectedIndexProperty()
                                        .addListener(this::operationIndexChanged);

        strEqual = new Label("  =  ");
        strEqual.setPadding(new Insets(5, 5, 5, 5));
        strPlus = new Label(" + ");
        strPlus.setPadding(new Insets(5, 5, 5, 5));
        
        // Two mults needed for linear combination
        strMult1 = new Label(" x "); strMult1.setPadding(new Insets(5, 5, 5, 5));
        strMult2 = new Label(" x "); strMult2.setPadding(new Insets(5, 5, 5, 5));
        strLeftParen = new Label("("); strLeftParen.setPadding(new Insets(5, 5, 5, 5));
        strRightParen = new Label(")"); strRightParen.setPadding(new Insets(5, 5, 5, 5));       
    }
    
    public void initParameters(String forThisMethod) {
        String textToResize;
        
        switch (forThisMethod) {
            
            case "linearTransformationOfAVariable":
                controlTitle = "Linear transformation of a variable";
                controlWidth = 750; controlHeight = 300; widthFudgeFactor = 0.65;
                resizableStrs[0] = "\n    This control is used to create a linear transformation of an existing variable.  Enter the new variable\n";
                resizableStrs[1] = "    label and coefficiencts, then choose the variable to transform from the drop down menu.\n\n" ;
                resizableStrs[2] = "      The format for the choices is: \n\n";
                resizableStrs[3] = "          <NewVariable name> = <a> + <b> x <variable>\n\n"; 
                
                alStr_Var_1_Data = myData.getAllTheColumns().get(0).getTheCases();
            break;
            
            case "linearCombinationOfVariables":
                controlTitle = "Linear Combination of Variables";                
                controlWidth = 975; controlHeight = 300; widthFudgeFactor = 0.55;
                resizableStrs[0] = "\n    This control is used to calculate a linear combination of variables.  Enter the name of the new variable, \n";
                resizableStrs[1] = "      then a and its variable, then b and its variables from the drop down menus.\n\n" ;
                resizableStrs[2] = "      The format for the choices is: \n\n";
                resizableStrs[3] = "          <NewVariable name> = <a> * <variable> + <b> * <variable>\n\n";    
                alStr_Var_1_Data = myData.getAllTheColumns().get(0).getTheCases();
                alStr_Var_2_Data = myData.getAllTheColumns().get(1).getTheCases();
                cbVarChoice2.getSelectionModel().select(1);
                
            break;
                
            case "linearTransformationWithFunction":
                controlTitle = "Linear Transformation with Function";                
                controlWidth = 800; controlHeight = 300; widthFudgeFactor = 0.60;
                resizableStrs[0] = "\n    This control is used to create a linear transformation of f(an existing variable).  Enter the new variable\n";
                resizableStrs[1] = "    label and coefficiencts, then choose the function and the variable to transform from the drop down menu.\n\n" ;
                resizableStrs[2] = "      The format for the choices is: \n\n";
                resizableStrs[3] = "          <NewVariable name> = <a> + <b> x <variable>\n\n";    
                chosenProcedure= "ln";
                alStr_Var_1_Data = myData.getAllTheColumns().get(0).getTheCases();
                strFunctionValue = "ln";
                
                
            break;

            case "binaryOperationOfVariables":
                controlTitle = "Binary Operation of Variables";                
                controlWidth = 650; controlHeight = 300; widthFudgeFactor = 0.60;
                resizableStrs[0] = "\n    This control is used to perform arithmetic operations on variables. Enter the new variable label,\n";
                resizableStrs[1] = "    and choose the variables and the operation from the drop down menus.\n\n"  ;
                resizableStrs[2] = "      The format for the choices is: \n\n";
                resizableStrs[3] = "          <NewVariable name> = <variable>) <operation>  <variable>\n\n";     
                strOperationValue = "+";
                alStr_Var_1_Data = myData.getAllTheColumns().get(0).getTheCases();
                alStr_Var_2_Data = myData.getAllTheColumns().get(1).getTheCases();
                cbVarChoice2.getSelectionModel().select(1);
            break;
            
            case "unaryOperationOnVariable":
                controlTitle = "Unary Operation on a Variable";                
                controlWidth = 650; controlHeight = 300; widthFudgeFactor = 0.60;
                resizableStrs[0] = "\n    This control is used to perform unary operations on variables. Enter the new variable label,\n";
                resizableStrs[1] = "    and choose the unary operation from the drop down menus.\n\n"  ;
                resizableStrs[2] = "      The format for the choices is: \n\n";
                resizableStrs[3] = "          <NewVariable name> = <variable>) <operation>  <variable>\n\n";  
                chosenProcedure = "z-score";
                alStr_Var_1_Data = myData.getAllTheColumns().get(0).getTheCases();
            break;
            
            default:
                System.out.println("Ack!! Switchfault, DataTransormations 508");
                System.exit(1);
        }
        
        textToResize = new String();
        maxResizableStrSize = 0;
        for (String resizableStr : resizableStrs) {
            textToResize += resizableStr;
            if (resizableStr.length() > maxResizableStrSize) {
                maxResizableStrSize = resizableStr.length();
            }         
        }
        
        txtResizableText = new Text(textToResize);
    }
    
    public void setUpAlphaAndBeta() {
        tfAlpha.setPrefColumnCount(5);        
        tfAlpha.textProperty().set(strAlphaValue);
        tfAlpha.textProperty().addListener((observable, oldValue, newValue) -> {
            // System.out.println("alpha changed from " + oldValue + " to " + newValue);
            strAlphaValue = newValue;
        });
        
        tfBeta.setPrefColumnCount(5);
        tfBeta.textProperty().set(strBetaValue);
        tfBeta.textProperty().addListener((observable, oldValue, newValue) -> {
            // System.out.println("alpha changed from " + oldValue + " to " + newValue);
            strBetaValue = newValue;
        });     
    }
    
    public void showTheControlAndWait() {
        root = new VBox();
        root.getChildren().addAll(txtResizableText, hBoxChoices, hBoxOkCancel);
        
        fontSize.bind(root.widthProperty().add(root.heightProperty()).divide(widthFudgeFactor * maxResizableStrSize));
        root.styleProperty().bind(Bindings.concat("-fx-font-family: Serif; ",
                                                  "-fx-font-size: ", fontSize.asString(), ";"
                                                 ,"-fx-base: rgb(200, 255, 255,",color.asString(),");")); 

        controlStage = new Stage();
        Scene scene = new Scene(root, controlWidth, controlHeight);
        
        controlStage.setTitle(controlTitle);
        controlStage.setScene(scene);
        controlStage.showAndWait();      
    }
    
    public void setUpTransformation() {
        strTransformedData = new String[nOriginalDataPoints];
        alphaValue = Double.valueOf(strAlphaValue);
        betaValue = Double.valueOf(strBetaValue);        
    }
    
    public double getVar1(int ithPoint) {
        return Double.parseDouble(alStr_Var_1_Data.get(ithPoint));
    }
    
    public double getVar2(int ithPoint) {
        return Double.parseDouble(alStr_Var_2_Data.get(ithPoint));
    }
    
    private void addVarToStructure() {
        int col;     
        ColumnOfData theNewColumn;
        col = positionTracker.getNVarsInStruct();
        myData.addNVariables(1);  
        myData.setVariableNameInStruct(col, tfNewVariable.getText());
        //System.out.println("637 Trans_GUI, col = " + col);
        theNewColumn = myData.getAllTheColumns().get(col - 1);
        int columnSize = theNewColumn.getColumnSize();
 
        for (int cases = 0; cases < columnSize; cases++) {
            myData.setDataElementInStruct(col, cases, strTransformedData[cases]);
            String tempString = myData.getDataElementFromStruct(col, cases);
        }
        myData.sendDataStructToGrid();
    }

}
