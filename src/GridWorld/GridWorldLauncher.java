package GridWorld;

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

    public static void setupGridViewFromVariables() {
        GridWorldModel model = GridWorldModel.getInstance();
        GridWorldView view = GridWorldView.getInstance();

        model.setGridSizeX(Integer.parseInt(GridWorldLauncher.gridSizeXField.getCharacters().toString()));
        model.setGridSizeY(Integer.parseInt(GridWorldLauncher.gridSizeYField.getCharacters().toString()));
        view.setGridCellSize(Integer.parseInt(GridWorldLauncher.gridCellSizeField.getCharacters().toString()));
        view.setArrowSize(Integer.parseInt(GridWorldLauncher.gridArrowSizeField.getCharacters().toString()));
        view.setTextSize(Integer.parseInt(GridWorldLauncher.gridFontSizeField.getCharacters().toString()));

    }

}

class creatorButtonHandler implements EventHandler<ActionEvent> {
    public void handle(ActionEvent event) {
        GridWorldLauncher.setupGridViewFromVariables();
        GridWorldModel.getInstance().setupEmptyGrid();
        GridWorldCreator.getInstance().start(GridWorldLauncher.primaryStage);
    }
}

class startButtonHandler implements EventHandler<ActionEvent> {
    //When the start button is clicked on the main menu.
    public void handle(ActionEvent event) {

        GridWorldLauncher.setupGridViewFromVariables();

        GridWorldQLearning.getInstance().setIterationSpeed(Integer.parseInt(GridWorldLauncher.learningIterationSpeedField.getCharacters().toString()));
        GridWorldModel.getInstance().setupFullGrid();
        GridWorldView.getInstance().start(GridWorldLauncher.primaryStage);

    }
}
