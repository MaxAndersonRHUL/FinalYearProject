package RL.gridWorld;

import RL.*;
import RL.XOWorld.XOModel;
import RL.XOWorld.XOView;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Random;

/**
 * Created by max on 17/10/2016.
 */
public class GridWorldLauncher extends Application {

    private static TextField gridSizeXField;
    private static TextField gridSizeYField;
    private static TextField gridCellSizeField;
    private static TextField gridArrowSizeField;
    private static TextField gridFontSizeField;
    static TextField learningIterationSpeedField;
    private static TextField startRewardLocationXField;
    private static TextField startRewardLocationYField;
    private static TextField startAgentStateXField;
    private static TextField startAgentStateYField;
    private static TextField rewardAmountField;
    private static TextField initQValueMinField;
    private static TextField initQValueMaxField;
    private static TextField exprWaitTimeField;
    private static TextField loadFileNameField;
    private static TextField iterationsPerSimulationField;
    private static TextField amountOfSimulationsField;

    private static Slider discountSlider;

    private static CheckBox enableEpisodicMode;
    private static CheckBox enableAverageExperiment;

    private static Text loadFileStatusText;
    private static Text generalMessageText;

    public static Stage primaryStage;

    public static boolean preLoadedEnvironment = false;

    public static void main(String[] args) {
        launch(args);
    }

    public Node setupGridWorldTab() {
        preLoadedEnvironment = false;
        VBox root = new VBox(10);
        Text titleText = new Text("Grid World");
        titleText.setFont(new Font(18));

        HBox endButtons = new HBox(20);

        HBox scrollPanels = new HBox(15);

        Font stdFont = new Font(14);

        VBox graphicsVars = new VBox(5);
        ScrollPane graphicalElements = new ScrollPane(graphicsVars);

        Text graphicsElementsTitle = new Text("Graphical");
        graphicsElementsTitle.setFont(new Font(18));
        graphicsVars.getChildren().addAll(graphicsElementsTitle);
        graphicsElementsTitle.setUnderline(true);
        graphicsVars.setPadding(new Insets(5,5,5,5));

        VBox simVars = new VBox(5);
        ScrollPane simulationElements = new ScrollPane(simVars);

        VBox experimentTypes = new VBox(5);
        ScrollPane experimentElements = new ScrollPane(experimentTypes);
        Text experimentTypesTitle = new Text("Experiments");
        experimentTypesTitle.setFont(new Font(18));
        experimentTypesTitle.setUnderline(true);
        experimentTypes.setPadding(new Insets(5,5,5,5));

        VBox averageExperimentPanel = new VBox(10);
        averageExperimentPanel.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        enableAverageExperiment = new CheckBox("Run As Experiment");
        enableAverageExperiment.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                enableAverageExperimentClicked();
            }
        });
        averageExperimentPanel.setPadding(new Insets(5,5,5,5));


        Text simulationElementsTitle = new Text("Simulation");
        simulationElementsTitle.setFont(new Font(18));
        simulationElementsTitle.setUnderline(true);
        simVars.getChildren().addAll(simulationElementsTitle);
        simVars.setPadding(new Insets(5,5,5,5));

        HBox amountOfSimulations = new HBox(10);
        Text amountOfSimulationsText = new Text("Amount of Simulations:");
        amountOfSimulationsText.setFont(stdFont);
        amountOfSimulationsField = new TextField("10");
        amountOfSimulationsField.setDisable(true);

        HBox amountOfIterations = new HBox(10);
        iterationsPerSimulationField = new TextField("2500");
        iterationsPerSimulationField.setDisable(true);
        Text iterationsPerSimulationText = new Text("Iterations per Sim");
        iterationsPerSimulationText.setFont(stdFont);

        HBox gridSizeX = new HBox(10);
        Text gridSizeXText = new Text("Grid size X value: ");
        gridSizeXText.setFont(stdFont);
        gridSizeXField = new TextField("10");
        gridSizeXField.setPrefColumnCount(3);

        HBox gridSizeY = new HBox(10);
        Text gridSizeYText = new Text("Grid size Y value: ");
        gridSizeYText.setFont(stdFont);
        gridSizeYField = new TextField("6");
        gridSizeYField.setPrefColumnCount(3);

        HBox gridCellSize = new HBox(10);
        Text gridCellSizeText = new Text("Grid Cell Size: ");
        gridCellSizeText.setFont(stdFont);
        gridCellSizeField = new TextField("100");
        gridCellSizeField.setPrefColumnCount(3);

        HBox gridArrowSize = new HBox(10);
        Text gridArrowSizeText = new Text("Grid Arrow Size: ");
        gridArrowSizeText.setFont(stdFont);
        gridArrowSizeField = new TextField("14");
        gridArrowSizeField.setPrefColumnCount(3);

        HBox gridFontSize = new HBox(10);
        Text gridFontSizeText = new Text("Grid Font Size: ");
        gridFontSizeText.setFont(stdFont);
        gridFontSizeField = new TextField("14");
        gridFontSizeField.setPrefColumnCount(3);

        HBox learningIterationSpeed = new HBox(10);
        Text learningIterationSpeedText = new Text("Target iterations per second: ");
        learningIterationSpeedText.setFont(stdFont);
        learningIterationSpeedField = new TextField("2");
        learningIterationSpeedField.setPrefColumnCount(5);

        HBox initialQValues = new HBox(10);
        Text initQTitle = new Text("Initial Q Values");
        Text randMinQValueText = new Text("Min Q Value:");
        initQValueMinField = new TextField("0");
        initQValueMaxField = new TextField("0");
        Text randMaxQValueText = new Text("Max Q Value:");
        initQTitle.setFont(stdFont);
        initQValueMaxField.setPrefColumnCount(2);
        initQValueMinField.setPrefColumnCount(2);

        HBox exprValueUpdateTime = new HBox(10);
        Text exprValueUpdateTimeText = new Text("Iterations per variable record:");
        exprValueUpdateTimeText.setFont(stdFont);
        exprWaitTimeField = new TextField("5");
        exprWaitTimeField.setPrefColumnCount(3);

        Text discountSliderText = new Text("Discount Variable");
        discountSliderText.setFont(stdFont);

        HBox discountSliderBox = new HBox(10);
        discountSlider = new Slider(0,0.99,0.9);
        discountSlider.setShowTickLabels(true);
        discountSlider.setShowTickMarks(true);
        discountSlider.setMajorTickUnit(0.1);
        discountSlider.setMinorTickCount(10);
        discountSlider.setSnapToTicks(true);
        discountSlider.setPrefSize(300,30);
        Label discountSliderLabel = new Label("0.90");
        discountSlider.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2) {
                double finalValue = Math.round( discountSlider.getValue() * 100.0 ) / 100.0;
                discountSliderLabel.setText(String.format("%1$,.2f", finalValue));
            }
        });

        HBox rewardCoordinate = new HBox(10);
        Text rewardCoordinateText = new Text("Reward");
        rewardCoordinateText.setFont(stdFont);
        startRewardLocationXField = new TextField("1");
        Text xCoord = new Text("X:");
        xCoord.setFont(stdFont);
        startRewardLocationYField = new TextField("1");
        Text yCoord = new Text("Y:");
        yCoord.setFont(stdFont);

        generalMessageText = new Text("");
        generalMessageText.setFont(stdFont);

        rewardAmountField = new TextField("5");
        Text rewardAmountText = new Text("Amount:");
        rewardAmountText.setFont(stdFont);

        HBox startState = new HBox(10);
        Text startStateText = new Text("Agent Start Position");
        startStateText.setFont(stdFont);
        startAgentStateXField = new TextField("0");
        Text startXCoord = new Text("X:");
        startXCoord.setFont(stdFont);
        startAgentStateYField = new TextField("0");
        Text startYCoord = new Text("Y:");
        startYCoord.setFont(stdFont);

        HBox loadExistingEnv = new HBox(10);
        Text loadExistingEnvText = new Text("Load Environment: ");
        loadFileNameField = new TextField("default");
        Button loadFileButton = new Button("Load");
        loadFileButton.setOnAction(new loadEnvironmentFromFileButtonHandler());
        loadFileStatusText = new Text("");
        loadFileStatusText.setFont(stdFont);
        loadFileStatusText.setFill(Color.BLUE);

        enableEpisodicMode = new CheckBox("Episodic");
        enableEpisodicMode.setFont(stdFont);

        startAgentStateXField.setPrefColumnCount(2);
        startAgentStateYField.setPrefColumnCount(2);

        startRewardLocationXField.setPrefColumnCount(2);
        startRewardLocationYField.setPrefColumnCount(2);
        rewardAmountField.setPrefColumnCount(2);

        Button creatorButton = new Button("Create world");
        creatorButton.setOnAction(new creatorButtonHandler());

        Button startButton = new Button("Start");
        startButton.setOnAction(new startGridWorldQLearningButtonHandler());

        gridSizeX.getChildren().addAll(gridSizeXText, gridSizeXField);
        gridSizeY.getChildren().addAll(gridSizeYText, gridSizeYField);
        gridCellSize.getChildren().addAll(gridCellSizeText, gridCellSizeField);
        gridArrowSize.getChildren().addAll(gridArrowSizeText, gridArrowSizeField);
        gridFontSize.getChildren().addAll(gridFontSizeText, gridFontSizeField);
        exprValueUpdateTime.getChildren().addAll(exprValueUpdateTimeText, exprWaitTimeField);
        discountSliderBox.getChildren().addAll(discountSlider, discountSliderLabel);
        initialQValues.getChildren().addAll(randMinQValueText, initQValueMinField, randMaxQValueText, initQValueMaxField);
        learningIterationSpeed.getChildren().addAll(learningIterationSpeedText, learningIterationSpeedField);
        rewardCoordinate.getChildren().addAll(rewardCoordinateText, xCoord, startRewardLocationXField, yCoord, startRewardLocationYField, rewardAmountText, rewardAmountField);
        startState.getChildren().addAll(startStateText, startXCoord, startAgentStateXField, startYCoord, startAgentStateYField);
        loadExistingEnv.getChildren().addAll(loadExistingEnvText, loadFileNameField, loadFileButton);
        endButtons.getChildren().addAll(startButton, creatorButton);

        amountOfSimulations.getChildren().addAll(amountOfSimulationsText, amountOfSimulationsField);
        amountOfIterations.getChildren().addAll(iterationsPerSimulationText, iterationsPerSimulationField);
        averageExperimentPanel.getChildren().addAll(enableAverageExperiment, amountOfSimulations, amountOfIterations);


        graphicsVars.getChildren().addAll(gridCellSize, gridArrowSize, gridFontSize);
        simVars.getChildren().addAll(gridSizeX, gridSizeY, learningIterationSpeed, rewardCoordinate, startState, discountSliderText, discountSliderBox, enableEpisodicMode);
        experimentTypes.getChildren().addAll(experimentTypesTitle, initQTitle, initialQValues, exprValueUpdateTime, averageExperimentPanel);

        scrollPanels.getChildren().addAll(graphicalElements, simulationElements, experimentElements);

        root.getChildren().addAll(titleText, generalMessageText, scrollPanels, loadExistingEnv, loadFileStatusText, endButtons);
        root.setPadding(new Insets(10, 10, 10, 10));

        root.setAlignment(Pos.TOP_CENTER);
        gridSizeX.setAlignment(Pos.TOP_CENTER);
        gridSizeY.setAlignment(Pos.TOP_CENTER);
        gridCellSize.setAlignment(Pos.TOP_CENTER);
        gridArrowSize.setAlignment(Pos.TOP_CENTER);
        gridFontSize.setAlignment(Pos.TOP_CENTER);
        learningIterationSpeed.setAlignment(Pos.TOP_CENTER);
        rewardCoordinate.setAlignment(Pos.TOP_CENTER);
        endButtons.setAlignment(Pos.TOP_CENTER);
        simVars.setAlignment(Pos.TOP_CENTER);
        discountSliderBox.setAlignment(Pos.TOP_CENTER);
        graphicsVars.setAlignment(Pos.TOP_CENTER);
        scrollPanels.setAlignment(Pos.TOP_CENTER);
        experimentTypes.setAlignment(Pos.TOP_CENTER);
        initialQValues.setAlignment(Pos.TOP_CENTER);
        exprValueUpdateTime.setAlignment(Pos.TOP_CENTER);
        loadExistingEnv.setAlignment(Pos.TOP_CENTER);
        averageExperimentPanel.setAlignment(Pos.TOP_CENTER);
        amountOfIterations.setAlignment(Pos.TOP_CENTER);
        amountOfSimulations.setAlignment(Pos.TOP_CENTER);

        GraphView.start();

        return root;
    }

    public Node setupXOWorldTab() {
        VBox root = new VBox(10);

        Button startButton = new Button("Start");
        startButton.setOnAction(new startXOGridWorldNoLearning());

        root.getChildren().addAll(startButton);

        root.setAlignment(Pos.TOP_CENTER);

        return root;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.primaryStage = primaryStage;

        TabPane tabPane = new TabPane();

        Tab gridWorldTab = new Tab();
        gridWorldTab.setText("Grid World");
        gridWorldTab.setContent(setupGridWorldTab());
        gridWorldTab.closableProperty().setValue(false);

        Tab XOTab = new Tab();
        XOTab.setText("XO World");
        XOTab.setContent(setupXOWorldTab());
        XOTab.closableProperty().setValue(false);

        tabPane.getTabs().addAll(gridWorldTab, XOTab);

        primaryStage.setTitle("Grid World - Final Year Project");
        primaryStage.setScene(new Scene(tabPane));
        primaryStage.show();
    }

    public static void loadExistingGridWorld() {
        String fileName = GridWorldLauncher.loadFileNameField.getText();
        loadExistingGridWorld(fileName);
    }

    public static void loadExistingGridWorld(String fileName){
        if(GridWorldSaveControl.loadEnvironmentFromFile(fileName)) {
            loadFileStatusText.setFill(Color.BLUE);
            loadFileStatusText.setText("Loaded environment: " + fileName);

            changeToPreLoadedEnvironmentUI();
            preLoadedEnvironment = true;

        } else {
            loadFileStatusText.setFill(Color.RED);
            loadFileStatusText.setText("Error loading file");
        }

    }

    public static void changeToPreLoadedEnvironmentUI() {
        gridSizeXField.setDisable(true);
        gridSizeYField.setDisable(true);
        startRewardLocationXField.setDisable(true);
        startRewardLocationYField.setDisable(true);
        //startAgentStateXField.setDisable(true);
        //startAgentStateYField.setDisable(true);
        rewardAmountField.setDisable(true);

        gridSizeXField.setText("" + GridWorldModel.getInstance().getGridSizeX());
        gridSizeYField.setText("" + GridWorldModel.getInstance().getGridSizeY());
    }

    public static void enableAverageExperimentClicked() {
        if(enableAverageExperiment.isSelected()) {
            iterationsPerSimulationField.setDisable(false);
            amountOfSimulationsField.setDisable(false);
        } else {
            iterationsPerSimulationField.setDisable(true);
            amountOfSimulationsField.setDisable(true);
        }
    }

    public static void setupGridModelFromVariables() {
        GridWorldModel model = GridWorldModel.getInstance();

        model.setGridSizeX(Integer.parseInt(GridWorldLauncher.gridSizeXField.getCharacters().toString()));
        model.setGridSizeY(Integer.parseInt(GridWorldLauncher.gridSizeYField.getCharacters().toString()));

    }

    public static void setupGridViewFromVariables() {
        GridWorldView view = GridWorldView.getInstance();

        view.setGridCellSize(Integer.parseInt(GridWorldLauncher.gridCellSizeField.getCharacters().toString()));
        view.setArrowSize(Integer.parseInt(GridWorldLauncher.gridArrowSizeField.getCharacters().toString()));
        view.setTextSize(Integer.parseInt(GridWorldLauncher.gridFontSizeField.getCharacters().toString()));
    }

    public static void displayError(String errorText) {
        generalMessageText.setFill(Color.RED);
        generalMessageText.setText(errorText);
    }

    public static void setupInitialRewardStateFromField() {
        GridWorldModel.getInstance().getStates().get(new GridWorldCoordinate(Integer.parseInt(startRewardLocationXField.getText()), Integer.parseInt(startRewardLocationYField.getText()))).setReward(Double.parseDouble(rewardAmountField.getText()));
    }

    public static void setupModelFromVariables() {
        State newStartState = GridWorldModel.getInstance().getStates().get(new GridWorldCoordinate(Integer.parseInt(startAgentStateXField.getText()), Integer.parseInt(GridWorldLauncher.startAgentStateYField.getText())));
        GridWorldModel.getInstance().getAgent().forceSetCurrentState(newStartState);
        GridWorldModel.getInstance().setStartingState(newStartState);
        GridWorldModel.getInstance().setEpisodic(enableEpisodicMode.isSelected());

        QLearningController.getInstance().setDiscountVariable(discountSlider.getValue());
    }

    public static void setupAverageExperiment() {
        if(enableAverageExperiment.isSelected()) {
            int simAmount = Integer.parseInt(amountOfSimulationsField.getText());
            int iterAmount = Integer.parseInt(iterationsPerSimulationField.getText());
            ExperimentationController.setupForAverageSim(simAmount, iterAmount);

        }
    }

    public static void setupExperiments() {

        ExperimentationController.setIterationsPerSave(Integer.parseInt(exprWaitTimeField.getText()));

        double minQ = Double.parseDouble(initQValueMinField.getText());
        double maxQ = Double.parseDouble(initQValueMaxField.getText());

        if(minQ > maxQ) {
            GridWorldLauncher.displayError("Q Value min is greater than Q Value max");
        } else {
            Random random = new Random();
            for(State state : GridWorldModel.getInstance().getStates().values()) {
                for(Action action : state.getActions()) {
                    action.setValue(minQ + (maxQ - minQ) * random.nextDouble());
                }
            }

        }
    }
}

class creatorButtonHandler implements EventHandler<ActionEvent> {
    public void handle(ActionEvent event) {
        GridWorldLauncher.setupGridModelFromVariables();
        GridWorldLauncher.setupGridViewFromVariables();
        GridWorldModel.getInstance().setupEmptyGrid();
        GridWorldCreator.getInstance().start(GridWorldLauncher.primaryStage);
    }
}

class loadEnvironmentFromFileButtonHandler implements EventHandler<ActionEvent> {
    public void handle(ActionEvent event) {
        GridWorldLauncher.loadExistingGridWorld();
    }
}

class startXOGridWorldNoLearning implements EventHandler<ActionEvent> {
    //When the start button is clicked on the main menu.
    public void handle(ActionEvent event) {

        CurrentSimulationReference.model = XOModel.getInstance();
        CurrentSimulationReference.view = XOView.getInstance();

        XOModel.getInstance().setupInitialState();

        XOView.getInstance().setupView(GridWorldLauncher.primaryStage);

        CurrentSimulationReference.controller = QLearningController.getInstance();
        QLearningController.getInstance().setIterationSpeed(1);
        QLearningController.getInstance().startSimulation();

    }
}

class startGridWorldQLearningButtonHandler implements EventHandler<ActionEvent> {
    //When the start button is clicked on the main menu.
    public void handle(ActionEvent event) {
        if(!GridWorldLauncher.preLoadedEnvironment) {
            GridWorldLauncher.setupGridModelFromVariables();
            GridWorldModel.getInstance().setupFullGrid();
            GridWorldLauncher.setupInitialRewardStateFromField();
        } else {

        }

        GridWorldLauncher.setupGridViewFromVariables();

        CurrentSimulationReference.model = GridWorldModel.getInstance();

        CurrentSimulationReference.view = GridWorldView.getInstance();

        GridWorldLauncher.setupModelFromVariables();

        GridWorldLauncher.setupExperiments();

        GridWorldView.getInstance().setupView(GridWorldLauncher.primaryStage);

        CurrentSimulationReference.controller = QLearningController.getInstance();

        GridWorldLauncher.setupAverageExperiment();

        QLearningController.getInstance().setIterationSpeed(Integer.parseInt(GridWorldLauncher.learningIterationSpeedField.getCharacters().toString()));
        QLearningController.getInstance().startSimulation();

        //ExperimentationController.beginGatheringData();
    }
}
