package RL.gridWorld;

import RL.CurrentSimulationReference;
import RL.QLearningController;
import RL.XOWorld.XOModel;
import RL.XOWorld.XOView;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Created by max on 17/10/2016.
 */
public class GridWorldLauncher extends Application {

    private static TextField gridSizeXField;
    private static TextField gridSizeYField;
    private static TextField gridCellSizeField;
    private static TextField gridArrowSizeField;
    private static TextField gridFontSizeField;
    public static TextField learningIterationSpeedField;
    private static TextField startRewardLocationXField;
    private static TextField startRewardLocationYField;

    public static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    public Node setupGridWorldTab() {
        VBox root = new VBox(10);
        Text titleText = new Text("Grid World");
        titleText.setFont(new Font(18));

        HBox endButtons = new HBox(20);

        Font stdFont = new Font(14);

        HBox gridSizeX = new HBox(10);
        Text gridSizeXText = new Text("Grid size X value: ");
        gridSizeXText.setFont(stdFont);
        gridSizeXField = new TextField("10");

        HBox gridSizeY = new HBox(10);
        Text gridSizeYText = new Text("Grid size Y value: ");
        gridSizeYText.setFont(stdFont);
        gridSizeYField = new TextField("6");

        HBox gridCellSize = new HBox(10);
        Text gridCellSizeText = new Text("Grid Cell Size: ");
        gridCellSizeText.setFont(stdFont);
        gridCellSizeField = new TextField("100");

        HBox gridArrowSize = new HBox(10);
        Text gridArrowSizeText = new Text("Grid Arrow Size: ");
        gridArrowSizeText.setFont(stdFont);
        gridArrowSizeField = new TextField("14");

        HBox gridFontSize = new HBox(10);
        Text gridFontSizeText = new Text("Grid Font Size: ");
        gridFontSizeText.setFont(stdFont);
        gridFontSizeField = new TextField("14");

        HBox learningIterationSpeed = new HBox(10);
        Text learningIterationSpeedText = new Text("Target iterations per second: ");
        learningIterationSpeedText.setFont(stdFont);
        learningIterationSpeedField = new TextField("2");

        HBox rewardCoordinate = new HBox(10);
        Text rewardCoordinateText = new Text("Reward Coordinate");
        rewardCoordinateText.setFont(stdFont);
        startRewardLocationXField = new TextField("1");
        Text xCoord = new Text("X:");
        xCoord.setFont(stdFont);
        startRewardLocationYField = new TextField("1");
        Text yCoord = new Text("Y:");
        yCoord.setFont(stdFont);

        startRewardLocationXField.setPrefColumnCount(2);
        startRewardLocationYField.setPrefColumnCount(2);

        Button creatorButton = new Button("Create world");
        creatorButton.setOnAction(new creatorButtonHandler());

        Button startButton = new Button("Start");
        startButton.setOnAction(new startGridWorldQLearningButtonHandler());

        gridSizeX.getChildren().addAll(gridSizeXText, gridSizeXField);
        gridSizeY.getChildren().addAll(gridSizeYText, gridSizeYField);
        gridCellSize.getChildren().addAll(gridCellSizeText, gridCellSizeField);
        gridArrowSize.getChildren().addAll(gridArrowSizeText, gridArrowSizeField);
        gridFontSize.getChildren().addAll(gridFontSizeText, gridFontSizeField);
        learningIterationSpeed.getChildren().addAll(learningIterationSpeedText, learningIterationSpeedField);
        rewardCoordinate.getChildren().addAll(rewardCoordinateText, xCoord, startRewardLocationXField, yCoord, startRewardLocationYField);
        endButtons.getChildren().addAll(startButton, creatorButton);

        root.getChildren().addAll(titleText, gridSizeX, gridSizeY, gridCellSize, gridArrowSize, gridFontSize, learningIterationSpeed, rewardCoordinate, endButtons);
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

    public static void setupGridViewFromVariables() {
        GridWorldModel model = GridWorldModel.getInstance();
        GridWorldView view = GridWorldView.getInstance();

        model.setGridSizeX(Integer.parseInt(GridWorldLauncher.gridSizeXField.getCharacters().toString()));
        model.setGridSizeY(Integer.parseInt(GridWorldLauncher.gridSizeYField.getCharacters().toString()));
        view.setGridCellSize(Integer.parseInt(GridWorldLauncher.gridCellSizeField.getCharacters().toString()));
        view.setArrowSize(Integer.parseInt(GridWorldLauncher.gridArrowSizeField.getCharacters().toString()));
        view.setTextSize(Integer.parseInt(GridWorldLauncher.gridFontSizeField.getCharacters().toString()));

    }

    public static void setupInitialRewardStateFromField() {
        GridWorldModel.getInstance().getStates().get(new GridWorldCoordinate(Integer.parseInt(startRewardLocationXField.getText()), Integer.parseInt(startRewardLocationYField.getText()))).setReward(1);
    }
}

class creatorButtonHandler implements EventHandler<ActionEvent> {
    public void handle(ActionEvent event) {
        GridWorldLauncher.setupGridViewFromVariables();
        GridWorldModel.getInstance().setupEmptyGrid();
        GridWorldCreator.getInstance().start(GridWorldLauncher.primaryStage);
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

        GridWorldLauncher.setupGridViewFromVariables();

        CurrentSimulationReference.model = GridWorldModel.getInstance();
        GridWorldModel.getInstance().setupFullGrid();

        GridWorldLauncher.setupInitialRewardStateFromField();

        CurrentSimulationReference.view = GridWorldView.getInstance();
        GridWorldView.getInstance().setupView(GridWorldLauncher.primaryStage);

        CurrentSimulationReference.controller = QLearningController.getInstance();
        QLearningController.getInstance().setIterationSpeed(Integer.parseInt(GridWorldLauncher.learningIterationSpeedField.getCharacters().toString()));
        QLearningController.getInstance().startSimulation();

    }
}
