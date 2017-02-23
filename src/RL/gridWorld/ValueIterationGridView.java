package RL.gridWorld;

import RL.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

/**
 * Created by max on 31/01/2017.
 */
public class ValueIterationGridView extends View {

    Stage primaryStage;
    GraphicsContext graphicsCont;
    Text iterationsAmountText;

    private boolean finalDraw = false;

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
        ScrollPane scroll = new ScrollPane(canvas);

        scroll.setMaxHeight(com.sun.glass.ui.Screen.getMainScreen().getHeight() / 1.5);
        scroll.setMaxWidth(com.sun.glass.ui.Screen.getMainScreen().getWidth() / 1.5);
        scroll.setPannable(true);

        GridWorldView.getInstance().setCanvasSize(canvas);
        graphicsCont = canvas.getGraphicsContext2D();

        iterationsAmountText = new Text("");
        iterationsAmountText.setFont(new Font(19));

        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(10,10,10,10));
        root.getChildren().addAll(scroll, iterationsAmountText);

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

                List<Action> IDs = ValueIterationController.getInstance().actionsChosen.get(entry.getKey());

                for(Action nsAction : IDs) {

                    GridWorldCoordinate destCoord = (GridWorldCoordinate) nsAction.getMostProbableState().getStateIdentity();
                    GridWorldCoordinate srcCoord = (GridWorldCoordinate) entry.getKey();

                    if (destCoord.x + 1 == srcCoord.x && destCoord.y == srcCoord.y) {
                        GridWorldView.getInstance().drawLeftTransition(srcCoord.x, srcCoord.y, graphicsCont);
                    }
                    if (destCoord.x - 1 == srcCoord.x && destCoord.y == srcCoord.y) {
                        GridWorldView.getInstance().drawRightTransition(srcCoord.x, srcCoord.y, graphicsCont);
                    }
                    if (destCoord.x == srcCoord.x && destCoord.y + 1 == srcCoord.y) {
                        GridWorldView.getInstance().drawUpTransition(srcCoord.x, srcCoord.y, graphicsCont);
                    }
                    if (destCoord.x == srcCoord.x && destCoord.y - 1 == srcCoord.y) {
                        GridWorldView.getInstance().drawDownTransition(srcCoord.x, srcCoord.y, graphicsCont);
                    }
                }
            }
        }
    }

    // This redraw function is called every frame, and so can be used to visualize the value iteration values
    // and policy changes as they are calculated - however, for large canvases, the amount of drawing to the
    // canvas results in a crash. Therefore, unless changed, the redraw function will only draw the view once,
    // when the value iteration has completed its calculation.
    @Override
    public void redraw() {
        if(ValueIterationController.getInstance().isComplete()) {
            if(!finalDraw) {
                clearCanvas(graphicsCont, canvas);
                drawGrid();
                drawStateValues();
                updateIterationsText();
                drawTransitions();
                finalDraw =  true;
            }

        }

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
