package RL;

import com.sun.glass.ui.Screen;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;

/**
 * Created by max on 17/02/2017.
 */
public class InfiniteCanvas {

    Canvas currentViewCanvas;
    ScrollPane scrollView;
    Pane pane;

    int height;
    int width;

    double currentViewX;
    double currentViewY;

    private boolean changedSinceLastUpdate = false;

    public boolean hasChangedSincedLastUpdated() {
        return changedSinceLastUpdate;
    }

    public InfiniteCanvas(int height, int width, Canvas canvas) {
        currentViewCanvas = canvas;

        pane = new Pane();
        pane.setMinHeight(height);
        pane.setMinWidth(width);
        pane.getChildren().addAll(canvas);
        scrollView = new ScrollPane(pane);
        scrollView.setPannable(true);
        scrollView.setMaxHeight(Screen.getMainScreen().getHeight() / 1.5);
        scrollView.setMaxWidth(Screen.getMainScreen().getWidth() / 1.5);

        canvas.setHeight(Screen.getMainScreen().getHeight() / 1.5);
        canvas.setWidth(Screen.getMainScreen().getWidth() / 1.5);

        scrollView.hvalueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                update();
            }
        });

        scrollView.vvalueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                update();
            }
        });

        this.height = height;
        this.width = width;
    }

    public void update() {
        double tempW = scrollView.getVvalue() * width;
        double tempH = scrollView.getHvalue() * height;

        if(tempW != currentViewY || tempH != currentViewX) {

            changedSinceLastUpdate = true;
            currentViewCanvas.setLayoutY(currentViewY - ((scrollView.getMaxHeight()) * scrollView.getVvalue()));
            currentViewCanvas.setLayoutX(currentViewX - ((scrollView.getMaxWidth()) * scrollView.getHvalue()));
        } else {
            changedSinceLastUpdate = false;
        }

        currentViewY = tempW;
        currentViewX = tempH;

    }

    public ScrollPane getScrollView() {
        return scrollView;
    }

}
