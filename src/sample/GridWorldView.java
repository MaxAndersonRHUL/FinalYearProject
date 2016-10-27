package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class GridWorldView  {

    Stage primaryStage;
    Canvas canvas;
    Text fpsText;
    Text simRateText;

    TextField  setSimRateField;

    GraphicsContext graphicsCont;

    int gridCellSize = 80;
    int arrowSize = 12;

    DecimalFormat decimal2 = new DecimalFormat("#.00");
    DecimalFormat decimal1 = new DecimalFormat("#.0");

    GridWorldModel model;

    int rewardTextSize = 15;

    int lastDrawnAgentPosX = 0;
    int lastDrawnAgentPosY = 0;

    static GridWorldView instance;

    Color upTransitionColor = Color.RED;
    Color dowNTransitionColor = Color.BLUE;
    Color leftTransitionColor = Color.GREEN;
    Color rightTransitionColor = Color.GRAY;

    public void start(Stage primaryStage){
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        instance = this;

        model = GridWorldModel.getInstance();

        VBox root = new VBox(1);

        VBox underCanvas = new VBox(20);
        HBox statusText = new HBox(40);
        HBox editVariables = new HBox(40);

        fpsText = new Text("Yes, this is definitely text.");
        simRateText = new Text("OK!");
        Text setSimRateText = new Text("Set simulation rate: ");
        setSimRateField = new TextField("5");
        Button setSimRateButton = new Button("Set");
        setSimRateButton.setOnAction(new editSimRate());

        this.primaryStage = primaryStage;
        canvas = new Canvas();
        setCanvasSize(canvas);
        graphicsCont = canvas.getGraphicsContext2D();

        editVariables.getChildren().addAll(setSimRateText, setSimRateField, setSimRateButton);

        statusText.getChildren().addAll(fpsText, simRateText);

        underCanvas.getChildren().addAll(editVariables, statusText);

        root.getChildren().addAll(canvas, underCanvas);

        underCanvas.setAlignment(Pos.TOP_CENTER);
        statusText.setAlignment(Pos.TOP_CENTER);
        editVariables.setAlignment(Pos.TOP_CENTER);
        root.setAlignment(Pos.TOP_CENTER);

        //drawArrow(graphicsCont, 0,0, 25, 270, Color.RED);

        setCanvasSize(canvas);

        //drawRotatedImage(graphicsCont, rect, 20, 10,10);

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        WorldViewUpdater updater = new WorldViewUpdater();
        updater.start();

        drawAllStateTransitions(true);
    }

    private void clearLastAgentPosition() {
        graphicsCont.setFill(Color.WHITE);
        graphicsCont.fillRect(lastDrawnAgentPosX*gridCellSize + (gridCellSize*0.25), lastDrawnAgentPosY*gridCellSize + (gridCellSize*0.25), gridCellSize/2, gridCellSize/2);
    }

    private void clearCanvas() {
        graphicsCont.setFill(Color.WHITE);
        graphicsCont.fillRect(0,0,canvas.getWidth(), canvas.getHeight());
    }

    private void drawAgent(Agent agent, GraphicsContext gc) {
        GridWorldState state = agent.currentState;
        gc.setFill(Color.BLACK);
        gc.fillRect(state.x*gridCellSize + (gridCellSize*0.25), state.y*gridCellSize + (gridCellSize*0.25), gridCellSize/2, gridCellSize/2);
        lastDrawnAgentPosX = state.x;
        lastDrawnAgentPosY = state.y;
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
        if(instance == null) {
            instance = new GridWorldView();
        }
        return instance;
    }

    // The rate that the view is updating, or how frequently the WorldViewUpdater's 'handle' function is called.
    public void setFpsText(int currentFPS) {
        fpsText.setText("FPS: " + currentFPS);
    }

    public void setSimRateText(int simRate) {
        simRateText.setText("Simulation rate per second: " + simRate);
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

        AffineTransform.getRotateInstance(Math.toRadians(angle), (locX + (size/2)),(locY + (size/2))).transform(polyPoints,0, polyPoints,0,8);

        // For AffineTransform to rotate the polygon, it must be in a single array. For javaFX to draw
        // the polygon, it must be split into 2 arrays. Below, the single list of points in the format
        // x0, y0, x1, y1, x2, y2 etc. Is split into arrays of x coordinates and y coordinates.
        double[] yPoints = new double[8];
        double[] xPoints = new double[8];
        
        for(int i = 0; i < 16; i++) {
            if(i%2 == 0) {
                xPoints[i/2] = polyPoints[i];
            } else {
                yPoints[i/2] = polyPoints[i];
            }
        }

        context.setFill(color);
        context.fillPolygon(xPoints, yPoints, 8);
    }

    public void redraw() {
        //clearLastAgentPosition();
        clearCanvas();
        drawGridStates(graphicsCont);
        drawAllStateTransitions(true);

        drawAgent(model.getAgent(), graphicsCont);
    }

    private void drawAllStateTransitions(boolean drawArrows) {
        for(GridWorldState[] stateArray : model.getGridWorldStates()) {
            for(GridWorldState state : stateArray) {
                drawStateTransition(state, drawArrows, graphicsCont);
            }
        }
    }

    private void drawActionValueText(int x, int y, Action act, Color color, GraphicsContext gc) {
        double num = act.getValue();
        gc.setFill(color);
        gc.fillText(decimal1.format(act.getValue()), x, y);
    }

    private void drawLeftTransition(int x, int y, GraphicsContext gc) {
        int calcX = (x) *gridCellSize - (arrowSize/2);
        int calcY = (int) ((y)*gridCellSize + gridCellSize - (gridCellSize*0.5));
        drawArrow(gc, calcX, calcY, arrowSize , 90, leftTransitionColor);
    }

    private void drawLeftTransitionValue(int x, int y, Action act, Color color, GraphicsContext gc) {
        int calcTextX = (x) *gridCellSize + (arrowSize/2);
        int calcTextY = (int) ((y)*gridCellSize + gridCellSize - (gridCellSize*0.35));

        drawActionValueText(calcTextX, calcTextY, act, color, gc);
    }

    private void drawRightTransition(int x, int y, GraphicsContext gc) {
        drawArrow(gc, (int)(x*gridCellSize + gridCellSize - (arrowSize/1.5)), (int) ((y)*gridCellSize + (gridCellSize * 0.4)), arrowSize , 270, rightTransitionColor);

        //int calcTextX = (int) (x*gridCellSize + gridCellSize - (arrowSize*2.5));
        //int calcTextY = (int) ((y)*gridCellSize + (gridCellSize * 0.5));
    }

    private void drawRightTransitionValue(int x, int y, Action act, Color color, GraphicsContext gc) {
        int calcTextX = (int) (x*gridCellSize + gridCellSize - (arrowSize*2.5));
        int calcTextY = (int) ((y)*gridCellSize + (gridCellSize * 0.5));

        drawActionValueText(calcTextX, calcTextY, act, color, gc);
    }

    private void drawDownTransition(int x, int y, GraphicsContext gc) {
        drawArrow(gc, (int) (x*gridCellSize + (gridCellSize*0.5)), y*gridCellSize + (gridCellSize - arrowSize/2), arrowSize , 360, dowNTransitionColor);

        //int calcTextX = (int) (x*gridCellSize + (gridCellSize*0.5));
        //int calcTextY = (int) (y*gridCellSize + (gridCellSize - arrowSize/1.5));
    }

    private void drawDownTransitionValue(int x, int y, Action act, Color color, GraphicsContext gc) {
        int calcTextX = (int) (x*gridCellSize + (gridCellSize*0.5));
        int calcTextY = (int) (y*gridCellSize + (gridCellSize - arrowSize/1.5));

        drawActionValueText(calcTextX, calcTextY, act, color, gc);
    }

    private void drawUpTransition(int x, int y, GraphicsContext gc) {
        drawArrow(gc, (int) (x*gridCellSize + (gridCellSize*0.3)), y*gridCellSize - (arrowSize/2), arrowSize , 180, upTransitionColor);

        //int calcTextX =(int) (x*gridCellSize + (gridCellSize*0.3));
        //int calcTextY =(int) (y*gridCellSize + arrowSize*2);

    }

    private void drawUpTransitionValue(int x, int y, Action act, Color color, GraphicsContext gc) {
        int calcTextX =(int) (x*gridCellSize + (gridCellSize*0.3));
        int calcTextY =(int) (y*gridCellSize + arrowSize*2);

        drawActionValueText(calcTextX, calcTextY, act, color, gc);
    }

    private void drawStateTransition(GridWorldState state, boolean drawArrows, GraphicsContext gc) {
        GridWorldState[][] states = model.getGridWorldStates();
        if(state.x > 0) {
            Action foundAction = model.getStateTransitionTo(state, states[state.y][state.x-1]);
            if(foundAction != null) {
                //Draw arrow pointing WEST
                if(drawArrows) {
                    drawLeftTransition(state.x, state.y, gc);
                }
                drawLeftTransitionValue(state.x, state.y, foundAction, leftTransitionColor, gc);
            }
        }
        if(state.x < model.getGridSizeX()-1) {
            Action foundAction = model.getStateTransitionTo(state, states[state.y][state.x+1]);
            if(foundAction != null) {
                //Draw arrow pointing EAST
                if(drawArrows) {
                    drawRightTransition(state.x, state.y, gc);
                }
                drawRightTransitionValue(state.x, state.y, foundAction, rightTransitionColor, gc);
            }
        }
        if(state.y < model.getGridSizeY()-1) {
            Action foundAction = model.getStateTransitionTo(state, states[state.y+1][state.x]);
            if(foundAction != null) {
                //Draw arrow pointing SOUTH
                if(drawArrows) {
                    drawDownTransition(state.x, state.y, gc);
                }
                drawDownTransitionValue(state.x, state.y, foundAction, dowNTransitionColor, gc);
            }
        }
        if(state.y > 0) {
            Action foundAction = model.getStateTransitionTo(state, states[state.y-1][state.x]);
            if(foundAction != null) {
                //Draw arrow pointing NORTH
                if(drawArrows) {
                    drawUpTransition(state.x, state.y, gc);
                }
                drawUpTransitionValue(state.x, state.y, foundAction, upTransitionColor, gc);
            }
    }
    }

    private void setCanvasSize(Canvas canvas) {
        canvas.setHeight(model.getGridSizeY() * gridCellSize );
        canvas.setWidth(model.getGridSizeX() * gridCellSize);
    }



    private void drawState(GridWorldState state, GraphicsContext gc) {
        Color col = state.getStateColor();
        if(col == null) {
            gc.strokeRect((state.x)*gridCellSize,(state.y)*gridCellSize,gridCellSize, gridCellSize);
        } else {
            gc.setFill(state.getStateColor());
            gc.fillRect((state.x)*gridCellSize + gc.getLineWidth(),(state.y)*gridCellSize + gc.getLineWidth(), gridCellSize - gc.getLineWidth(), gridCellSize- gc.getLineWidth());
        }

        double reward = state.getReward();
        if(reward != 0) {
            gc.setFont(new Font(rewardTextSize));
            gc.setFill(Color.RED);
            String formatedDouble = decimal2.format(reward);
            gc.fillText(formatedDouble, state.x * gridCellSize + (gridCellSize/2) - (rewardTextSize + formatedDouble.length()), state.y * gridCellSize + (gridCellSize/2), gridCellSize);
        }
    }

    private void drawGridStates(GraphicsContext gc) {
        for(GridWorldState[] stateArray : model.getGridWorldStates()) {
            for(GridWorldState state : stateArray) {
                drawState(state, gc);
            }
        }
    }
}

class editSimRate implements EventHandler<ActionEvent> {
    public void handle(ActionEvent event) {
        GridWorldQLearning.getInstance().setIterationSpeed(Integer.parseInt(GridWorldView.getInstance().setSimRateField.getCharacters().toString()));
    }
}
