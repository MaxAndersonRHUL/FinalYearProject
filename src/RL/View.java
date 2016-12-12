package RL;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Created by max on 11/12/2016.
 */
public abstract class View {

    public Text fpsText;
    public Text simRateText;
    public Canvas canvas;

    // The rate that the view is updating, or how frequently the WorldViewUpdater's 'handle' function is called.
    public void setFpsText(int currentFPS) {
        fpsText.setText("FPS: " + currentFPS);
    }

    public void setSimRateText(int simRate) {
        simRateText.setText("Average simulation rate per second: " + simRate);
    }

    public void start(Stage stage) {

        WorldViewUpdater updater = new WorldViewUpdater();
        updater.start();

    }

    public void clearCanvas(GraphicsContext graphics, Canvas canvas) {
        graphics.setFill(Color.WHITE);
        graphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public abstract void redraw();



}
