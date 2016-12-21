package RL.XOWorld;

import RL.Action;
import RL.CurrentSimulationReference;
import RL.State;
import RL.View;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Created by max on 11/12/2016.
 */
public class XOView extends View {

    Canvas canvas;
    GraphicsContext gc;
    private static XOView instance;
    private int gridCellSize = 80;
    protected Button pausePlay;

    TextField setSimRateField;

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        HBox editVariables = new HBox(20);

        canvas = new Canvas();
        gc = canvas.getGraphicsContext2D();
        setCanvasSize(canvas);

        pausePlay = new Button("PAUSE");
        pausePlay.setOnAction(new pauseSimulation());

        Text setSimRateText = new Text("Set simulation rate: ");
        setSimRateField = new TextField("5");
        Button setSimRateButton = new Button("Set");
        setSimRateButton.setOnAction(new editSimRate());

        editVariables.getChildren().addAll(pausePlay, setSimRateText, setSimRateField, setSimRateButton);

        root.getChildren().addAll(canvas, editVariables, simViewPanel);
        root.setAlignment(Pos.TOP_CENTER);
        editVariables.setAlignment(Pos.TOP_CENTER);

        System.out.println("RUNNING");

        primaryStage.setTitle("XO View");
        primaryStage.setScene(new Scene(root));
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    public void setCanvasSize(Canvas canvas) {
        canvas.setHeight(XOModel.getInstance().boardSizeY * gridCellSize);
        canvas.setWidth(XOModel.getInstance().boardSizeX * gridCellSize);
    }

    @Override
    public void redraw() {
        clearCanvas(gc, canvas);
        drawBoard((XOState) (XOModel.getInstance().getAgent().currentState));
    }

    public LocationState[][] cloneArray(LocationState[][] initial) {
        LocationState[][] result = new LocationState[initial.length][];
        for (int r = 0; r < initial.length; r++) {
            result[r] = initial[r].clone();
        }
        return result;
    }

    public void drawBoard(XOState state) {
        LocationState[][] board = XOModel.getInstance().getFullBoard(state.getStateIdentity()).board;
        for (int j = 0; j < board.length; j++) {
            for (int i = 0; i < board[0].length; i++) {
                gc.strokeRect(i * gridCellSize, (j) * gridCellSize, gridCellSize, gridCellSize);
                if(board[j][i] == LocationState.CROSS) {
                    gc.setFill(Color.BLACK);
                    gc.setFont(new Font(20));
                    gc.fillText("CROSS", (i * gridCellSize) , (j * gridCellSize) + gridCellSize/2);
                } else if(board[j][i] == LocationState.NAUGHT) {
                    gc.setFill(Color.BLACK);
                    gc.setFont(new Font(20));
                    gc.fillText("NAUGHT", (i * gridCellSize) , (j * gridCellSize) + gridCellSize/2);
                } else if(board[j][i] == LocationState.EMPTY) {
                    gc.setFill(new Color(0.2, 0.2, 0.2, 0.7));
                    gc.setFont(new Font(15));
                    LocationState[][] newBoard = cloneArray(board);
                    XOModel.getInstance().removeEnemyStates(newBoard);
                    newBoard[j][i] = XOModel.getInstance().playerMarker;
                    State transState = CurrentSimulationReference.model.getStates().get(new XOBoard(newBoard));
                    if(transState != null) {
                        Action action = CurrentSimulationReference.model.getStateTransitionTo(state, transState);
                        if(action != null) {
                            gc.fillText("" + action.getValue(), (i * gridCellSize) + 4 , (j * gridCellSize) + (gridCellSize * 0.8));
                        }
                    } else {
                        gc.fillText("0", (i * gridCellSize) , (j * gridCellSize) + gridCellSize);
                    }
                }
            }
        }
    }

    public static XOView getInstance() {
        if (instance == null) {
            instance = new XOView();
        }
        return instance;
    }
}

class pauseSimulation implements EventHandler<ActionEvent> {
    public void handle(ActionEvent event) {
        if(CurrentSimulationReference.controller.executionPaused) {
            CurrentSimulationReference.controller.playExecution();
            XOView.getInstance().pausePlay.setText("Pause");
        } else  {
            CurrentSimulationReference.controller.pauseExecution();
            XOView.getInstance().pausePlay.setText("Play");
        }

    }
}


class editSimRate implements EventHandler<ActionEvent> {
    public void handle(ActionEvent event) {
        CurrentSimulationReference.controller.setIterationSpeed(Integer.parseInt(XOView.getInstance().setSimRateField.getCharacters().toString()));
    }
}
