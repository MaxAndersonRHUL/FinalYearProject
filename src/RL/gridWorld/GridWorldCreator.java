package RL.gridWorld;

import RL.Action;
import RL.CurrentSimulationReference;
import RL.QLearningController;
import RL.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
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

    String currentlyTyping = "";

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

        Button fillGridButton = new Button("Create Every Transition");
        fillGridButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                GridWorldModel.getInstance().setupFullGrid();
                gridView.fullRedraw(canvas);
            }
        });

        canvas = new Canvas();

        gridView.setCanvasSize(canvas);

        graphics = canvas.getGraphicsContext2D();

        underCanvas.getChildren().addAll(startButton, fillGridButton);
        root.getChildren().addAll(canvas, underCanvas);

        underCanvas.setAlignment(Pos.TOP_CENTER);
        root.setAlignment(Pos.TOP_CENTER);

        gridView.fullRedraw(canvas);

        Scene scene = new Scene(root);

        primaryStage.setTitle("Grid World Creator");
        primaryStage.setScene(scene);
        primaryStage.show();


        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                keyPressed(event.getCode());
            }
        });

        canvas.setOnMouseClicked(new canvasClickedHandler());
    }

    private void keyPressed(KeyCode key) {
        if(currentlySelected != null) {
            gridView.fullRedraw(canvas);
            if(key.isDigitKey()) {
                currentlyTyping = currentlyTyping + key.getName();
                gridView.drawTextInState(currentlySelected, graphics, currentlyTyping);
            }
            if(key == KeyCode.BACK_SPACE) {
                if (currentlyTyping != null && currentlyTyping.length() > 0) {
                    currentlyTyping = currentlyTyping.substring(0, currentlyTyping.length()-1);
                    gridView.drawTextInState(currentlySelected, graphics, currentlyTyping);
                }
            }
            if(key == KeyCode.ENTER) {
                if(currentlyTyping != null) {
                    double dub = Double.parseDouble(currentlyTyping);
                    currentlySelected.setReward(dub);
                    currentlyTyping = null;
                    gridView.fullRedraw(canvas);
                }
            }
        }
    }

    private void deselectCurrent() {
        currentlySelected.setStateColor(defColor);
        currentlySelected = null;
        currentlyTyping = null;
    }

    private void setCurrentlySelected(GridWorldState state) {
        currentlySelected = state;
        state.setStateColor(selectedColor);
    }

    private void makeWallState(GridWorldState state) {
        state.removeAllActions();
        state.setStateColor(Color.GRAY);
        for(State searchState : GridWorldModel.getInstance().getStates().values()) {
            if(searchState == state) {
                continue;
            }
            Action act = GridWorldModel.getInstance().getStateTransitionTo(searchState, state);
            if(act != null) {
                searchState.getActions().remove(act);
            }
        }
    }

    void handleCanvasClicked(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        System.out.println("X: " + x);
        System.out.println("Y: " + y);
        //GridWorldState selected = GridWorldModel.getInstance().getStates()[(int) (y / gridView.getGridCellSize())] [(int) (x / gridView.getGridCellSize())];
        GridWorldState selected = (GridWorldState) GridWorldModel.getInstance().getStates().get(new GridWorldCoordinate((int) (x / gridView.getGridCellSize()) , (int) (y / gridView.getGridCellSize())));

        if(event.getButton() == MouseButton.SECONDARY) {
            if(currentlySelected == null) {
                makeWallState(selected);
            }
        }
        if(event.getButton() == MouseButton.PRIMARY) {
            if (currentlySelected == selected) {
                deselectCurrent();
            } else if (currentlySelected == null) {
                setCurrentlySelected(selected);
            } else if (GridWorldModel.getInstance().canStateTransitionTo(currentlySelected, selected)) {
                boolean found = false;
                for (Action act : currentlySelected.getActions()) {
                    if (act.getMostProbableState() == selected) {
                        found = true;
                        currentlySelected.removeAction(act);
                        break;
                    }
                }
                if (!found) {
                    System.out.println("Adding action");
                    currentlySelected.addAction(new Action(selected, 0));
                }
            } else {
                deselectCurrent();
            }
        }
        gridView.fullRedraw(canvas);
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
