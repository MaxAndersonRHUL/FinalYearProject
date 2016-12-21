package RL.gridWorld;

import RL.Action;
import RL.CurrentSimulationReference;
import RL.QLearningController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Created by New on 10/23/2016.
 */
public class GridWorldCreator {

    private static GridWorldCreator instance;
    private GridWorldView gridView;

    private GraphicsContext graphics;
    private Canvas canvas;

    private GridWorldState currentlySelected = null;

    Color selectedColor = new Color(0,0,1,0.4);
    Color defColor = new Color(1,1,1,0);

    private GridWorldCreator() {
    }

    public static GridWorldCreator getInstance() {
        if (instance == null) {
            instance = new GridWorldCreator();
        }
        return instance;
    }

    public void start(Stage primaryStage) {

        primaryStage.close();

        gridView = GridWorldView.getInstance();

        VBox root = new VBox(25);
        VBox underCanvas = new VBox(20);

        Button startButton = new Button("Start Simulation");
        startButton.setOnAction(new startCreateButtonHandler());

        canvas = new Canvas();

        gridView.setCanvasSize(canvas);

        graphics = canvas.getGraphicsContext2D();

        underCanvas.getChildren().addAll(startButton);
        root.getChildren().addAll(canvas, underCanvas);

        underCanvas.setAlignment(Pos.TOP_CENTER);
        root.setAlignment(Pos.TOP_CENTER);

        gridView.redraw(canvas);

        primaryStage.setTitle("Grid World Creator");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        canvas.setOnMouseClicked(new canvasClickedHandler());
    }



    private void deselectCurrent() {
        currentlySelected.setStateColor(defColor);
        currentlySelected = null;
    }

    private void setCurrentlySelected(GridWorldState state) {
        currentlySelected = state;
        state.setStateColor(selectedColor);
    }

    public void handleCanvasClicked(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        System.out.println("X: " + x);
        System.out.println("Y: " + y);
        //GridWorldState selected = GridWorldModel.getInstance().getStates()[(int) (y / gridView.getGridCellSize())] [(int) (x / gridView.getGridCellSize())];
        GridWorldState selected = (GridWorldState) GridWorldModel.getInstance().getStates().get(new GridWorldCoordinate((int) (x / gridView.getGridCellSize()) , (int) (y / gridView.getGridCellSize())));
        if(currentlySelected == selected) {
            deselectCurrent();
        } else if (currentlySelected == null) {
            setCurrentlySelected(selected);
        } else if (GridWorldModel.getInstance().canStateTransitionTo(currentlySelected, selected)) {
            boolean found = false;
            for (Action act : currentlySelected.getActions()) {
                if (act.resultingState == selected) {
                    found = true;
                    currentlySelected.removeAction(act);
                    break;
                }
            }
            if (!found) {
                System.out.println("Adding action");
                currentlySelected.addAction(new Action(selected));
            }
        } else {
            deselectCurrent();
        }
        gridView.redraw(canvas);
    }
}

class canvasClickedHandler implements EventHandler<MouseEvent> {
    @Override
    public void handle(MouseEvent event) {
        GridWorldCreator.getInstance().handleCanvasClicked(event);
    }
}

class startCreateButtonHandler implements EventHandler<ActionEvent> {
    //When the start button is clicked on the main menu.
    public void handle(ActionEvent event) {

        GridWorldLauncher.setupGridViewFromVariables();

        CurrentSimulationReference.model = GridWorldModel.getInstance();

        CurrentSimulationReference.view = GridWorldView.getInstance();
        GridWorldView.getInstance().setupView(GridWorldLauncher.primaryStage);

        CurrentSimulationReference.controller = QLearningController.getInstance();
        QLearningController.getInstance().setIterationSpeed(Integer.parseInt(GridWorldLauncher.learningIterationSpeedField.getCharacters().toString()));
        QLearningController.getInstance().startSimulation();

    }
}
