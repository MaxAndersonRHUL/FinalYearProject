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
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by max on 20/12/2016.
 */
public class StatesModelView {

    protected Stage statesViewStage;
    Canvas canvas;
    int stateSize = 40;
    int stateSeperation = 50;
    ScrollPane scrollView;

    int currentViewX = 5000;
    int currentViewY = 0;

    int canvasSize = 2000;

    int worldViewSize = 10000;

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

    public void drawFullModelView() {

        State currentState = CurrentSimulationReference.model.startingState;
        LinkedList<StateViewLocation> backlog = new LinkedList<StateViewLocation>();
        HashSet<State> visistedStates = new HashSet<>();
        canvas.getGraphicsContext2D().strokeRect((worldViewSize / 2) + stateSeperation, stateSeperation, stateSize, stateSize);
        int previousX = stateSeperation + (stateSize/2) + (worldViewSize) / 2;
        int previousY = stateSeperation + (stateSize/2);

        while(currentState != null) {

            int amountOfActions = currentState.getActions().size();
            int amFactor = amountOfActions/2;

            for (Action act : currentState.getActions()) {
                int locationX = previousX + (stateSeperation) - (amFactor * stateSeperation);
                int locationY = previousY + stateSeperation;

                int screenViewLocationX = locationX - currentViewX;
                int screenViewLocationY = locationY - currentViewY;

                canvas.getGraphicsContext2D().strokeRect(screenViewLocationX, screenViewLocationY, stateSize, stateSize);
                canvas.getGraphicsContext2D().strokeLine(previousX - currentViewX, (previousY-currentViewY) + (stateSize/2), screenViewLocationX + (stateSize/2), screenViewLocationY);

                if(!visistedStates.contains(act.resultingState)) {
                    backlog.addLast(new StateViewLocation(act.resultingState, locationX + (stateSize/2), previousY + stateSeperation + (stateSize/2)));
                    visistedStates.add(act.resultingState);
                }
                amFactor--;
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