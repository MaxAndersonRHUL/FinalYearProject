package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.w3c.dom.events.Event;

/**
 * Created by max on 17/10/2016.
 */
public class GridWorldLauncher extends Application {

    public static TextField gridSizeXField;
    public static TextField gridSizeYField;
    public static TextField gridCellSizeField;
    public static TextField gridArrowSizeField;
    public static TextField gridFontSizeField;
    public static TextField learningIterationSpeedField;

    public static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.primaryStage = primaryStage;

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
        gridCellSizeField = new TextField("180");

        HBox gridArrowSize = new HBox(10);
        Text gridArrowSizeText = new Text("Grid Arrow Size: ");
        gridArrowSizeText.setFont(stdFont);
        gridArrowSizeField = new TextField("18");

        HBox gridFontSize = new HBox(10);
        Text gridFontSizeText = new Text("Grid Font Size: ");
        gridFontSizeText.setFont(stdFont);
        gridFontSizeField = new TextField("20");

        HBox learningIterationSpeed = new HBox(10);
        Text learningIterationSpeedText = new Text("Target iterations per second: ");
        learningIterationSpeedText.setFont(stdFont);
        learningIterationSpeedField = new TextField("2");

        Button creatorButton = new Button("Create world");
        creatorButton.setOnAction(new creatorButtonHandler());

        Button startButton = new Button("Start");
        startButton.setOnAction(new startButtonHandler());

        gridSizeX.getChildren().addAll(gridSizeXText, gridSizeXField);
        gridSizeY.getChildren().addAll(gridSizeYText, gridSizeYField);
        gridCellSize.getChildren().addAll(gridCellSizeText, gridCellSizeField);
        gridArrowSize.getChildren().addAll(gridArrowSizeText, gridArrowSizeField);
        gridFontSize.getChildren().addAll(gridFontSizeText, gridFontSizeField);
        learningIterationSpeed.getChildren().addAll(learningIterationSpeedText, learningIterationSpeedField);
        endButtons.getChildren().addAll(startButton, creatorButton);

        root.getChildren().addAll(titleText, gridSizeX, gridSizeY, gridCellSize, gridArrowSize, gridFontSize, learningIterationSpeed, endButtons);
        root.setPadding(new Insets(10, 10, 10, 10));

        root.setAlignment(Pos.TOP_CENTER);
        gridSizeX.setAlignment(Pos.TOP_CENTER);
        gridSizeY.setAlignment(Pos.TOP_CENTER);
        gridCellSize.setAlignment(Pos.TOP_CENTER);
        gridArrowSize.setAlignment(Pos.TOP_CENTER);
        gridFontSize.setAlignment(Pos.TOP_CENTER);
        learningIterationSpeed.setAlignment(Pos.TOP_CENTER);
        endButtons.setAlignment(Pos.TOP_CENTER);

        primaryStage.setTitle("Grid World - Final Year Project");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}

class creatorButtonHandler implements EventHandler<ActionEvent> {
    public void handle(ActionEvent event) {
        GridWorldCreator.getInstance().start(GridWorldLauncher.primaryStage);
    }
}

class startButtonHandler implements EventHandler<ActionEvent> {
    //When the start button is clicked on the main menu.
    public void handle(ActionEvent event) {

        GridWorldModel.getInstance().setGridSizeX(Integer.parseInt(GridWorldLauncher.gridSizeXField.getCharacters().toString()));
        GridWorldModel.getInstance().setGridSizeY(Integer.parseInt(GridWorldLauncher.gridSizeYField.getCharacters().toString()));
        GridWorldView.getInstance().setGridCellSize(Integer.parseInt(GridWorldLauncher.gridCellSizeField.getCharacters().toString()));
        GridWorldView.getInstance().setArrowSize(Integer.parseInt(GridWorldLauncher.gridArrowSizeField.getCharacters().toString()));
        GridWorldView.getInstance().setTextSize(Integer.parseInt(GridWorldLauncher.gridFontSizeField.getCharacters().toString()));

        GridWorldModel.getInstance().setupBasicGrid();
        GridWorldQLearning.getInstance().setIterationSpeed(Integer.parseInt(GridWorldLauncher.learningIterationSpeedField.getCharacters().toString()));
        GridWorldView.getInstance().start(GridWorldLauncher.primaryStage);

    }
}
