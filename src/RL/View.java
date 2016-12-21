package RL;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Created by max on 11/12/2016.
 */
public abstract class View {

    protected Text fpsText;
    protected Text simRateText;
    protected Text totalIterationsText;
    protected Canvas canvas;

    protected StatesModelView modelView;

    protected HBox simViewPanel;

    protected Text statusText;

    protected Button showStatesViewButton;

    // The rate that the view is updating, or how frequently the WorldViewUpdater's 'handle' function is called.
    void setFpsText(int currentFPS) {
        fpsText.setText("FPS: " + currentFPS);
    }

    void setSimRateText(int simRate) {
        simRateText.setText("Average simulation rate per second: " + simRate);
    }

    void setTotalIterationsText(long totalIterations) {
        totalIterationsText.setText("Amount of iterations " + totalIterations);
    }

    public void setupView(Stage stage) {
        simViewPanel = new HBox(10);

        fpsText = new Text();
        simRateText = new Text("Simulations per second:");
        totalIterationsText = new Text();

        showStatesViewButton = new Button("View Model");
        showStatesViewButton.setOnAction(new showStatesView());

        simViewPanel.getChildren().addAll(fpsText, simRateText, totalIterationsText, showStatesViewButton);

        simViewPanel.setStyle("-fx-background-color: #808080");
        simViewPanel.setAlignment(Pos.TOP_CENTER);

        start(stage);

        WorldViewUpdater updater = new WorldViewUpdater();
        updater.start();

    }

    public StatesModelView getGetStatesModelView() {
        return modelView;
    }

    public abstract void start(Stage stage);

    public void setStatusText(String text) {
        if(statusText != null) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (statusText != null) {
                        statusText.setText(text);
                    }
                }
            });
        }
    }

    protected void showStatesView() {
        if(modelView == null) {
            modelView = new StatesModelView();
        }
        modelView.showStatesView();
    }

    protected void clearCanvas(GraphicsContext graphics, Canvas canvas) {
        graphics.setFill(Color.WHITE);
        graphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public abstract void redraw();

}

class showStatesView implements EventHandler<ActionEvent> {

    public void handle(ActionEvent event) {
        CurrentSimulationReference.view.showStatesView();
    }
}
