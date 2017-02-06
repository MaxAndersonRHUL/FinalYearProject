package RL.gridWorld;

import RL.*;
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

import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.util.HashMap;

public class GridWorldView extends View{

    Stage primaryStage;

    TextField setSimRateField;

    private GraphicsContext graphicsCont;

    private int gridCellSize = 80;
    private int arrowSize = 12;

    public DecimalFormat decimal2 = new DecimalFormat("#.00");
    private DecimalFormat decimal1 = new DecimalFormat("#.0");

    private int rewardTextSize = 15;

    private int lastDrawnAgentPosX = 0;
    private int lastDrawnAgentPosY = 0;

    private static GridWorldView instance;

    Color upTransitionColor = Color.RED;
    Color dowNTransitionColor = Color.BLUE;
    Color leftTransitionColor = Color.GREEN;
    Color rightTransitionColor = Color.GRAY;

    ValueIterationGridView valueIterationView;

    @Override
    public void start(Stage primaryStage) {
        //Parent root = FXMLLoader.load(getClass().getResource("RL.gridWorld.fxml"));

        instance = this;

        VBox root = new VBox(1);

        VBox underCanvas = new VBox(20);
        HBox statusText = new HBox(40);
        HBox editVariables = new HBox(40);

        Text setSimRateText = new Text("Set simulation rate: ");
        setSimRateField = new TextField("5");
        Button setSimRateButton = new Button("Set");
        setSimRateButton.setOnAction(new editSimRate());

        Button showValueIterationView = new Button("Value Iteration");
        showValueIterationView.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                displayValueIteration();
            }
        });

        this.primaryStage = primaryStage;
        canvas = new Canvas();
        setCanvasSize(canvas);
        graphicsCont = canvas.getGraphicsContext2D();

        editVariables.getChildren().addAll(setSimRateText, setSimRateField, setSimRateButton);

        underCanvas.getChildren().addAll(editVariables, statusText, this.simViewPanel, showValueIterationView);

        root.getChildren().addAll(canvas, underCanvas);

        underCanvas.setAlignment(Pos.TOP_CENTER);
        statusText.setAlignment(Pos.TOP_CENTER);
        editVariables.setAlignment(Pos.TOP_CENTER);
        root.setAlignment(Pos.TOP_CENTER);

        //drawArrow(graphicsCont, 0,0, 25, 270, Color.RED);

        setCanvasSize(canvas);

        //drawRotatedImage(graphicsCont, rect, 20, 10,10);

        primaryStage.setTitle("Grid World View");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }

    private void displayValueIteration() {
        if(valueIterationView == null) {
            valueIterationView = new ValueIterationGridView();
            valueIterationView.setupView(new Stage());
        }
        valueIterationView.showView();
    }

    private void clearLastAgentPosition() {
        graphicsCont.setFill(Color.WHITE);
        graphicsCont.fillRect(lastDrawnAgentPosX * gridCellSize + (gridCellSize * 0.25), lastDrawnAgentPosY * gridCellSize + (gridCellSize * 0.25), gridCellSize / 2, gridCellSize / 2);
    }

    public void drawAgent() {
        drawAgent(GridWorldModel.getInstance().getAgent(), graphicsCont);
    }

    public void drawAgent(GraphicsContext graphics) {
        drawAgent(GridWorldModel.getInstance().getAgent(), graphics);
    }

    public void drawAgent(Agent agent, GraphicsContext gc) {
        GridWorldState state = (GridWorldState) agent.currentState;
        gc.setFill(Color.BLACK);
        gc.fillRect(state.getStateIdentity().x * gridCellSize + (gridCellSize * 0.25), state.getStateIdentity().y * gridCellSize + (gridCellSize * 0.25), gridCellSize / 2, gridCellSize / 2);
        lastDrawnAgentPosX = state.getStateIdentity().x;
        lastDrawnAgentPosY = state.getStateIdentity().y;
    }

    public int getGridCellSize() {
        return gridCellSize;
    }

    public void setGridCellSize(int size) {
        gridCellSize = size;
    }

    public void setArrowSize(int size) {
        arrowSize = size;
    }

    public void setTextSize(int size) {
        rewardTextSize = size;
    }

    public static GridWorldView getInstance() {
        if (instance == null) {
            instance = new GridWorldView();
        }
        return instance;
    }

    // Draws an arrow pointing east to the graphics context, rotate by an angle and filled with a color.
    private void drawArrow(GraphicsContext context, int locX, int locY, int size, int angle, Color color) {
        double[] polyPoints = new double[16];

        polyPoints[0] = (0.35 * size) + locX;
        polyPoints[1] = 0 + locY;

        polyPoints[2] = (0.65 * size) + locX;
        polyPoints[3] = 0 + locY;

        polyPoints[4] = (0.65 * size) + locX;
        polyPoints[5] = 0.5 * size + locY;

        polyPoints[6] = (1 * size) + locX;
        polyPoints[7] = (0.5 * size) + locY;

        polyPoints[8] = (0.5 * size) + locX;
        polyPoints[9] = (1 * size) + locY;

        polyPoints[10] = 0 + locX;
        polyPoints[11] = (0.5 * size) + locY;

        polyPoints[12] = (0.35 * size) + locX;
        polyPoints[13] = (0.5 * size) + locY;

        polyPoints[14] = (0.35 * size) + locX;
        polyPoints[15] = 0 + locY;

        AffineTransform.getRotateInstance(Math.toRadians(angle), (locX + (size / 2)), (locY + (size / 2))).transform(polyPoints, 0, polyPoints, 0, 8);

        // For AffineTransform to rotate the polygon, it must be in a single array. For javaFX to draw
        // the polygon, it must be split into 2 arrays. Below, the single list of points in the format
        // x0, y0, x1, y1, x2, y2 etc. Is split into arrays of x coordinates and y coordinates.
        double[] yPoints = new double[8];
        double[] xPoints = new double[8];

        for (int i = 0; i < 16; i++) {
            if (i % 2 == 0) {
                xPoints[i / 2] = polyPoints[i];
            } else {
                yPoints[i / 2] = polyPoints[i];
            }
        }

        context.setFill(color);
        context.fillPolygon(xPoints, yPoints, 8);
    }

    public void redraw(Canvas canvas) {
        //clearLastAgentPosition();
        clearCanvas(canvas.getGraphicsContext2D(), canvas);
        drawGridStates(canvas.getGraphicsContext2D(), true);
        drawAllStateTransitions(true, canvas.getGraphicsContext2D());
        drawAgent(canvas.getGraphicsContext2D());
    }

    public void redraw() {
        redraw(canvas);
    }

    public void drawAllStateTransitions(boolean drawArrows, GraphicsContext graphics) {
            for (State state : GridWorldModel.getInstance().getStates().values()) {
                drawStateTransition((GridWorldState) state, drawArrows, graphics);
            }
    }

    private void drawActionValueText(int x, int y, Action act, Color color, GraphicsContext gc) {
        double num = act.getValue();
        gc.setFill(color);
        gc.fillText(decimal1.format(act.getValue()), x, y);
    }

    private void drawLeftTransition(int x, int y, GraphicsContext gc) {
        int calcX = (x) * gridCellSize - (arrowSize / 2);
        int calcY = (int) ((y) * gridCellSize + gridCellSize - (gridCellSize * 0.5));
        drawArrow(gc, calcX, calcY, arrowSize, 90, leftTransitionColor);
    }

    private void drawLeftTransitionValue(int x, int y, Action act, Color color, GraphicsContext gc) {
        int calcTextX = (x) * gridCellSize + (arrowSize / 2);
        int calcTextY = (int) ((y) * gridCellSize + gridCellSize - (gridCellSize * 0.35));

        drawActionValueText(calcTextX, calcTextY, act, color, gc);
    }

    private void drawRightTransition(int x, int y, GraphicsContext gc) {
        drawArrow(gc, (int) (x * gridCellSize + gridCellSize - (arrowSize / 1.5)), (int) ((y) * gridCellSize + (gridCellSize * 0.4)), arrowSize, 270, rightTransitionColor);

        //int calcTextX = (int) (x*gridCellSize + gridCellSize - (arrowSize*2.5));
        //int calcTextY = (int) ((y)*gridCellSize + (gridCellSize * 0.5));
    }

    private void drawRightTransitionValue(int x, int y, Action act, Color color, GraphicsContext gc) {
        int calcTextX = (int) (x * gridCellSize + gridCellSize - (arrowSize * 2.5));
        int calcTextY = (int) ((y) * gridCellSize + (gridCellSize * 0.5));

        drawActionValueText(calcTextX, calcTextY, act, color, gc);
    }

    private void drawDownTransition(int x, int y, GraphicsContext gc) {
        drawArrow(gc, (int) (x * gridCellSize + (gridCellSize * 0.5)), y * gridCellSize + (gridCellSize - arrowSize / 2), arrowSize, 360, dowNTransitionColor);

        //int calcTextX = (int) (x*gridCellSize + (gridCellSize*0.5));
        //int calcTextY = (int) (y*gridCellSize + (gridCellSize - arrowSize/1.5));
    }

    private void drawDownTransitionValue(int x, int y, Action act, Color color, GraphicsContext gc) {
        int calcTextX = (int) (x * gridCellSize + (gridCellSize * 0.5));
        int calcTextY = (int) (y * gridCellSize + (gridCellSize - arrowSize / 1.5));

        drawActionValueText(calcTextX, calcTextY, act, color, gc);
    }

    private void drawUpTransition(int x, int y, GraphicsContext gc) {
        drawArrow(gc, (int) (x * gridCellSize + (gridCellSize * 0.3)), y * gridCellSize - (arrowSize / 2), arrowSize, 180, upTransitionColor);

        //int calcTextX =(int) (x*gridCellSize + (gridCellSize*0.3));
        //int calcTextY =(int) (y*gridCellSize + arrowSize*2);

    }

    private void drawUpTransitionValue(int x, int y, Action act, Color color, GraphicsContext gc) {
        int calcTextX = (int) (x * gridCellSize + (gridCellSize * 0.3));
        int calcTextY = (int) (y * gridCellSize + arrowSize * 2);

        drawActionValueText(calcTextX, calcTextY, act, color, gc);
    }

    private void drawStateTransition(GridWorldState state, boolean drawArrows, GraphicsContext gc) {
        GridWorldModel model = GridWorldModel.getInstance();
        HashMap<StateIdentity, State> states = model.getStates();
        if (state.getStateIdentity().x > 0) {
            Action foundAction = model.getStateTransitionTo(state, states.get(new GridWorldCoordinate(state.getStateIdentity().x - 1, state.getStateIdentity().y)));
            if (foundAction != null) {
                //Draw arrow pointing WEST
                if (drawArrows) {
                    drawLeftTransition(state.getStateIdentity().x, state.getStateIdentity().y, gc);
                }
                drawLeftTransitionValue(state.getStateIdentity().x, state.getStateIdentity().y, foundAction, leftTransitionColor, gc);
            }
        }
        if (state.getStateIdentity().x < model.getGridSizeX() - 1) {
            Action foundAction = model.getStateTransitionTo(state, states.get(new GridWorldCoordinate(state.getStateIdentity().x + 1, state.getStateIdentity().y)));
            if (foundAction != null) {
                //Draw arrow pointing EAST
                if (drawArrows) {
                    drawRightTransition(state.getStateIdentity().x, state.getStateIdentity().y, gc);
                }
                drawRightTransitionValue(state.getStateIdentity().x, state.getStateIdentity().y, foundAction, rightTransitionColor, gc);
            }
        }
        if (state.getStateIdentity().y < model.getGridSizeY() - 1) {
            Action foundAction = model.getStateTransitionTo(state, states.get(new GridWorldCoordinate(state.getStateIdentity().x, state.getStateIdentity().y + 1)));
            if (foundAction != null) {
                //Draw arrow pointing SOUTH
                if (drawArrows) {
                    drawDownTransition(state.getStateIdentity().x, state.getStateIdentity().y, gc);
                }
                drawDownTransitionValue(state.getStateIdentity().x, state.getStateIdentity().y, foundAction, dowNTransitionColor, gc);
            }
        }
        if (state.getStateIdentity().y > 0) {
            Action foundAction = model.getStateTransitionTo(state, states.get(new GridWorldCoordinate(state.getStateIdentity().x, state.getStateIdentity().y - 1)));
            if (foundAction != null) {
                //Draw arrow pointing NORTH
                if (drawArrows) {
                    drawUpTransition(state.getStateIdentity().x, state.getStateIdentity().y, gc);
                }
                drawUpTransitionValue(state.getStateIdentity().x, state.getStateIdentity().y, foundAction, upTransitionColor, gc);
            }
        }
    }

    public void setCanvasSize(Canvas canvas) {
        GridWorldModel model = GridWorldModel.getInstance();
        canvas.setHeight(model.getGridSizeY() * gridCellSize);
        canvas.setWidth(model.getGridSizeX() * gridCellSize);
    }


    private void drawState(GridWorldState state, GraphicsContext gc, boolean drawReward) {
        Color col = state.getStateColor();
        gc.strokeRect((state.getStateIdentity().x) * gridCellSize, (state.getStateIdentity().y) * gridCellSize, gridCellSize, gridCellSize);
        if (col != null) {
            gc.setFill(state.getStateColor());
            gc.fillRect((state.getStateIdentity().x) * gridCellSize + 2, (state.getStateIdentity().y) * gridCellSize + 2, gridCellSize - 4, gridCellSize - 4);
        }
        if(drawReward) {
            double reward = state.getReward();
            if (reward != 0) {
                gc.setFont(new Font(rewardTextSize));
                gc.setFill(Color.RED);
                String formatedDouble = decimal2.format(reward);
                drawTextInState(state, gc, formatedDouble);
            }
        }
    }

    public void drawTextInState(GridWorldState state, GraphicsContext gc, String text) {
        gc.fillText(text, state.getStateIdentity().x * gridCellSize + (gridCellSize / 2) - (rewardTextSize + text.length()), state.getStateIdentity().y * gridCellSize + (gridCellSize / 2), gridCellSize);
    }

    public void drawGridStates(GraphicsContext gc, boolean drawReward) {
            for (State state : GridWorldModel.getInstance().getStates().values()) {
                drawState((GridWorldState) state, gc, drawReward);
            }
    }
}

class editSimRate implements EventHandler<ActionEvent> {
    public void handle(ActionEvent event) {
        QLearningController.getInstance().setIterationSpeed(Integer.parseInt(GridWorldView.getInstance().setSimRateField.getCharacters().toString()));
    }
}
