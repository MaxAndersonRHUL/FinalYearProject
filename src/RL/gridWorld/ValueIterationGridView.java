package RL.gridWorld;

import RL.State;
import RL.StateIdentity;
import RL.ValueIterationController;
import RL.View;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Map;

/**
 * Created by max on 31/01/2017.
 */
public class ValueIterationGridView extends View {

    Stage primaryStage;
    GraphicsContext graphicsCont;
    Text iterationsAmountText;

    public void hideView() {
        primaryStage.hide();
    }

    public void showView() {
        primaryStage.show();
    }


    @Override
    public void start(Stage stage) {
        VBox root = new VBox(1);

        primaryStage = stage;
        canvas = new Canvas();
        GridWorldView.getInstance().setCanvasSize(canvas);
        graphicsCont = canvas.getGraphicsContext2D();

        iterationsAmountText = new Text("");
        iterationsAmountText.setFont(new Font(19));

        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(10,10,10,10));
        root.getChildren().addAll(canvas, iterationsAmountText);

        primaryStage.setTitle("Grid World View");
        primaryStage.setScene(new Scene(root));

        ValueIterationController controller = ValueIterationController.getInstance();
        controller.beginCalculatingValueIteration();
    }

    private void drawStateValues() {
        for(Map.Entry<StateIdentity, State> entry : ValueIterationController.getInstance().states.entrySet()) {
            GridWorldState state = (GridWorldState) entry.getValue();

            double val;

            if(ValueIterationController.getInstance().stateValues.containsKey(entry.getKey())) {
                val = ValueIterationController.getInstance().stateValues.get(entry.getKey());
            } else {
                continue;
            }
            String formatedDouble = GridWorldView.getInstance().decimal2.format(val);
            graphicsCont.setFont(new Font(14));
            graphicsCont.setFill(Color.RED);
            GridWorldView.getInstance().drawTextInState(state, graphicsCont, "" + formatedDouble);

            if(ValueIterationController.getInstance().actionsChosen.containsKey(entry.getKey())) {

                GridWorldCoordinate destCoord = (GridWorldCoordinate) ValueIterationController.getInstance().actionsChosen.get(entry.getKey()).getMostProbableState().getStateIdentity();
                GridWorldCoordinate srcCoord = (GridWorldCoordinate) entry.getKey();

                if(destCoord.x + 1 == srcCoord.x && destCoord.y == srcCoord.y) {
                    GridWorldView.getInstance().drawLeftTransition(srcCoord.x, srcCoord.y, graphicsCont);
                }
                if(destCoord.x - 1 == srcCoord.x && destCoord.y == srcCoord.y) {
                    GridWorldView.getInstance().drawRightTransition(srcCoord.x, srcCoord.y, graphicsCont);
                }
                if(destCoord.x == srcCoord.x && destCoord.y + 1 == srcCoord.y) {
                    GridWorldView.getInstance().drawUpTransition(srcCoord.x, srcCoord.y, graphicsCont);
                }
                if(destCoord.x == srcCoord.x && destCoord.y - 1 == srcCoord.y) {
                    GridWorldView.getInstance().drawDownTransition(srcCoord.x, srcCoord.y, graphicsCont);
                }

            }
        }
    }

    @Override
    public void redraw() {
        clearCanvas(graphicsCont, canvas);
        drawGrid();
        drawStateValues();
        updateIterationsText();
        drawTransitions();
    }

    private void drawTransitions() {

    }

    private void updateIterationsText() {
        iterationsAmountText.setText("ITERATIONS: " + ValueIterationController.getInstance().getAmountOfIterations());
    }

    private void drawGrid() {
        GridWorldView.getInstance().drawGridStates(graphicsCont, false);
    }

}
