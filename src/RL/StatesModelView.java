package RL;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by max on 20/12/2016.
 */
public class StatesModelView {

    protected Stage statesViewStage;
    Canvas canvas;
    int stateSize = 75;
    int stateSeperation = 80;
    ScrollPane scrollView;

    int currentViewX = 500;
    int currentViewY = 0;

    int canvasSize = 2000;

    int worldViewSize = 1000;

    public void setupStatesModelView() {
        VBox box = new VBox(10);
        HBox buttonsMenu = new HBox(10);
        canvas = new Canvas();
        canvas.setHeight(canvasSize);
        canvas.setWidth(canvasSize);
        scrollView  = new ScrollPane(canvas);

        Button refreshAllStates = new Button("Refresh");
        refreshAllStates.setOnAction(new refreshView());

        Button scrollUp = new Button("UP");
        scrollUp.setOnAction(new scrollViewUp());
        Button scrollLeft = new Button("LEFT");
        scrollLeft.setOnAction(new scrollViewLeft());
        Button scrollRight = new Button("Right");
        scrollRight.setOnAction(new scrollViewRight());
        Button scrollDown = new Button("Down");
        scrollDown.setOnAction(new scrollViewDown());

        buttonsMenu.getChildren().addAll(refreshAllStates, scrollDown, scrollRight,scrollLeft, scrollUp);

        box.getChildren().addAll(scrollView, buttonsMenu);
        box.setAlignment(Pos.TOP_CENTER);

        buttonsMenu.setAlignment(Pos.TOP_CENTER);

        drawFullModelView();

        statesViewStage = new Stage();
        statesViewStage.setScene(new Scene(box, 450,450));
    }

    public void scrollViewDown() {
        currentViewY = currentViewY + 100;
        redrawAllStates();
        //canvas.setTranslateY(currentViewY);
    }

    public void scrollViewRight() {
        currentViewX = currentViewX + 150;
        redrawAllStates();
        //canvas.setTranslateX(currentViewX);
    }

    public void scrollViewLeft() {
        currentViewX = currentViewX - 150;
        redrawAllStates();
        //canvas.setTranslateY(currentViewY);
    }

    public void scrollViewUp() {
        currentViewY = currentViewY - 100;
        redrawAllStates();
        //canvas.setTranslateX(currentViewX);
    }

    private Point2D findNextClosestLocation(HashSet<Point2D> set, Point2D point) {
        if(!set.contains(point)) {
            return point;
        } else {
            for(int i = 0; i < 1000; i++) {
                Point2D nPoint = new Point2D(point.x, point.y + (i*stateSeperation));
                if(!set.contains(nPoint)) {
                    return nPoint;
                }
            }
        }
        return null;
    }

    public void drawFullModelView() {

        State currentState = CurrentSimulationReference.model.startingState;
        LinkedList<StateViewLocation> backlog = new LinkedList<StateViewLocation>();
        HashMap<State, Point2D> visistedStates = new HashMap<>();
        HashSet<Point2D> takenLocations = new HashSet<>();

        int previousX = stateSeperation + (stateSize/2) + (worldViewSize) / 2;
        int previousY = stateSeperation + (stateSize/2);

        canvas.getGraphicsContext2D().strokeRect(previousX - currentViewX - (stateSize/2), previousY-currentViewY - (stateSize/2), stateSize, stateSize);

        while(currentState != null) {

            int amountOfActions = currentState.getActions().size();
            int amFactor = amountOfActions;
            int i = amountOfActions/2;

            for (Action act : currentState.getActions()) {

                if(!visistedStates.containsKey(act.getMostProbableState())) {

                    int locationX = previousX + (stateSeperation) - (amFactor * stateSeperation);
                    int locationY = previousY + stateSeperation;

                    Point2D nPoint = findNextClosestLocation(takenLocations, new Point2D(locationX, locationY));

                    locationX = nPoint.x;
                    locationY = nPoint.y;

                    takenLocations.add(nPoint);

                    int screenViewLocationX = locationX - currentViewX;
                    int screenViewLocationY = locationY - currentViewY;
                    canvas.getGraphicsContext2D().strokeRect(screenViewLocationX, screenViewLocationY, stateSize, stateSize);

                    canvas.getGraphicsContext2D().setFont(new Font((stateSize/6) + 3));
                    canvas.getGraphicsContext2D().setFill(Color.BLACK);
                    canvas.getGraphicsContext2D().fillText(act.getMostProbableState().toString(), screenViewLocationX + 2, screenViewLocationY + (stateSize/2));

                    StateViewLocation stateLocObj = new StateViewLocation(act.getMostProbableState(), locationX + (stateSize/2), previousY + stateSeperation + (stateSize/2));
                    backlog.addLast(stateLocObj);
                    visistedStates.put(act.getMostProbableState(), stateLocObj.location);
                    canvas.getGraphicsContext2D().strokeLine(previousX - currentViewX, (previousY-currentViewY) + (stateSize/2), screenViewLocationX + (stateSize/2), screenViewLocationY);

                } else {

                    int locationX = visistedStates.get(act.getMostProbableState()).x;
                    int locationY = visistedStates.get(act.getMostProbableState()).y + (stateSize/2);

                    int screenViewLocationX = locationX - currentViewX;
                    int screenViewLocationY = locationY - currentViewY;

                    canvas.getGraphicsContext2D().fillRect(screenViewLocationX, screenViewLocationY, stateSize/10, stateSize/10);

                    canvas.getGraphicsContext2D().strokeLine(previousX - currentViewX, (previousY-currentViewY) + (stateSize/2), screenViewLocationX, screenViewLocationY);
                }
                i--;
                amFactor = (i*2);
            }
            StateViewLocation loc = backlog.pollFirst();
            if(loc == null) {
                break;
            }
            currentState = loc.state;
            previousX = loc.location.x;
            previousY = loc.location.y;
        }
    }

    public void redrawAllStates() {
        canvas.getGraphicsContext2D().setFill(Color.WHITE);
        canvas.getGraphicsContext2D().fillRect(0,0,canvasSize, canvasSize);
        drawFullModelView();
    }

    public void showStatesView() {
        if(statesViewStage == null) {
            setupStatesModelView();
        }
        statesViewStage.show();

        scrollView.setHvalue(0.5);
    }
}

class scrollViewLeft implements EventHandler<ActionEvent> {
    public void handle(ActionEvent event) {
        CurrentSimulationReference.view.getGetStatesModelView().scrollViewLeft();
    }
}

class scrollViewUp implements EventHandler<ActionEvent> {
    public void handle(ActionEvent event) {
        CurrentSimulationReference.view.getGetStatesModelView().scrollViewUp();
    }
}

class scrollViewDown implements EventHandler<ActionEvent> {
    public void handle(ActionEvent event) {
        CurrentSimulationReference.view.getGetStatesModelView().scrollViewDown();
    }
}

class scrollViewRight implements EventHandler<ActionEvent> {
    public void handle(ActionEvent event) {
        CurrentSimulationReference.view.getGetStatesModelView().scrollViewRight();
    }
}

class refreshView implements EventHandler<ActionEvent> {
    public void handle(ActionEvent event) {
        CurrentSimulationReference.view.getGetStatesModelView().redrawAllStates();
    }
}