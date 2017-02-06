package RL.gridWorld;

import RL.State;
import RL.StateIdentity;
import RL.ValueIterationController;
import RL.View;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Map;

/**
 * Created by max on 31/01/2017.
 */
public class ValueIterationGridView extends View {

    Stage primaryStage;
    GraphicsContext graphicsCont;

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

        root.getChildren().addAll(canvas);

        primaryStage.setTitle("Grid World View");
        primaryStage.setScene(new Scene(root));

        ValueIterationController controller = ValueIterationController.getInstance();
        controller.beginCalculatingValueIteration();

    }

    private void drawStateValues() {
        for(Map.Entry<StateIdentity, State> entry : ValueIterationController.getInstance().states.entrySet()) {
            GridWorldState state = (GridWorldState) entry.getValue();
            double val = ValueIterationController.getInstance().stateValues.get(entry.getKey());
            String formatedDouble = GridWorldView.getInstance().decimal2.format(val);
            GridWorldView.getInstance().drawTextInState(state, graphicsCont, "" + formatedDouble);
        }
    }

    @Override
    public void redraw() {
        clearCanvas(graphicsCont, canvas);
        drawGrid();
        drawStateValues();
    }

    private void drawGrid() {
        graphicsCont.setFont(new Font(14));
        graphicsCont.setFill(Color.RED);
        GridWorldView.getInstance().drawGridStates(graphicsCont, false);
    }

}
