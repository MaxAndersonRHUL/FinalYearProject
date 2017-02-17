package RL;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
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

    private Slider slider;
    private Text sliderLabel;

    protected HBox simViewPanel;
    protected HBox simViewPanel2;

    protected Text statusText;

    protected Button showStatesViewButton;
    protected Button pureRandomButton;

    protected Button pausePlay;

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
        simViewPanel2 = new HBox(10);

        fpsText = new Text();
        simRateText = new Text("Simulations per second:");
        totalIterationsText = new Text();

        showStatesViewButton = new Button("View Model");
        showStatesViewButton.setOnAction(new showStatesView());

        sliderLabel = new Text();
        sliderLabel.prefWidth(200);

        slider = new Slider(0,5,1);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(0.05);
        slider.setMinorTickCount(5);
        slider.setSnapToTicks(true);
        slider.setPrefSize(300,30);

        pureRandomButton = new Button("Full Random Choices");
        pureRandomButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(CurrentSimulationReference.controller.getPureRandomMode()) {
                    CurrentSimulationReference.controller.setPureRandomMode(false);
                    slider.setVisible(true);
                    sliderLabel.setVisible(true);
                    pureRandomButton.setText("Full Random Choice");

                } else {
                    CurrentSimulationReference.controller.setPureRandomMode(true);
                    slider.setVisible(false);
                    sliderLabel.setVisible(false);
                    pureRandomButton.setText("Learning-controlled Choice");
                }

            }
        });

        pausePlay = new Button("PAUSE");
        pausePlay.setOnAction(new pauseSimulation());

        // Code for observing and changing slider value taken and modified from: http://stackoverflow.com/questions/26552495/javafx-set-slider-value-after-dragging-mouse-button
        // By Stackoverflow user: James_D
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> source, Number oldValue, Number newValue) {
                double finalValue = Math.round( slider.getValue() * 100.0 ) / 100.0;
                sliderLabel.setText(String.format("%1$,.2f", finalValue));
                QLearningController.getInstance().setExploValue(finalValue);
            } });

        simViewPanel.getChildren().addAll(fpsText, simRateText, totalIterationsText, showStatesViewButton, slider, sliderLabel, pureRandomButton);
        simViewPanel2.getChildren().addAll(pausePlay);

        simViewPanel.setStyle("-fx-background-color: #94a6a8");
        simViewPanel.setPadding(new Insets(10,10,0,10));
        simViewPanel.setAlignment(Pos.TOP_CENTER);

        simViewPanel2.setStyle("-fx-background-color: #94a6a8");
        simViewPanel2.setPadding(new Insets(4,10,10,10));
        simViewPanel2.setAlignment(Pos.TOP_CENTER);

        start(stage);

        WorldViewUpdater updater = new WorldViewUpdater(this, CurrentSimulationReference.controller);
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

class pauseSimulation implements EventHandler<ActionEvent> {
    public void handle(ActionEvent event) {
        if(CurrentSimulationReference.controller.executionPaused) {
            CurrentSimulationReference.controller.playExecution();
            CurrentSimulationReference.view.pausePlay.setText("Pause");
        } else  {
            CurrentSimulationReference.controller.pauseExecution();
            CurrentSimulationReference.view.pausePlay.setText("Play");
        }

    }
}
