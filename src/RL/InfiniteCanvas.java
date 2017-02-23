package RL;

import RL.gridWorld.GridWorldView;
import com.sun.glass.ui.Screen;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
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

    double canvasWidth;
    double canvasHeight;

    double currentViewX;
    double currentViewY;

    private boolean changedSinceLastUpdate = false;

    public boolean hasChangedSincedLastUpdated() {
        return changedSinceLastUpdate;
    }

    public void drawRect(double x, double y, double w, double h) {
        Point2D point = convertToWorldCoord(x, y);

        if(point.getX() + w < currentViewX || point.getX() > currentViewX + canvasWidth || point.getY() + h < currentViewY || point.getY() > currentViewY + canvasHeight) {
            return;
        }
        currentViewCanvas.getGraphicsContext2D().strokeRect(point.getX(), point.getY(), w, h);
    }

    public void drawText(String text, double x, double y, int maxW) {
        Point2D point = convertToWorldCoord(x, y);

        if(point.getX() + maxW < currentViewX || point.getX() > currentViewX + canvasWidth || point.getY() + 10 < currentViewY || point.getY() > currentViewY + canvasHeight) {
            return;
        }

        currentViewCanvas.getGraphicsContext2D().fillText(text, point.getX(), point.getY(), maxW);
    }

    public void drawText(String text, double x, double y) {
        Point2D point = convertToWorldCoord(x, y);

        if(point.getX() < currentViewX || point.getX() > currentViewX + canvasWidth || point.getY() + 10 < currentViewY || point.getY() > currentViewY + canvasHeight) {
            return;
        }

        currentViewCanvas.getGraphicsContext2D().fillText(text, point.getX(), point.getY());
    }

    public Point2D convertToWorldCoord(double x, double y) {
        return new Point2D(x - currentViewX, y - currentViewY);
    }

    public InfiniteCanvas(int height, int width, Canvas canvas) {
        currentViewCanvas = canvas;

        pane = new Pane();
        pane.setMinHeight(height);
        pane.setMinWidth(width);
        pane.getChildren().addAll(canvas);
        scrollView = new ScrollPane(pane);
        scrollView.setPannable(true);

        scrollView.setMaxHeight(Screen.getMainScreen().getHeight() / 1.75);
        scrollView.setMaxWidth(Screen.getMainScreen().getWidth() / 1.75);

        canvasHeight = Screen.getMainScreen().getHeight() / 1.75;
        canvasWidth = Screen.getMainScreen().getWidth() / 1.75;

        canvas.setHeight(canvasHeight);
        canvas.setWidth(canvasWidth);

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

        System.out.println("Y: " + currentViewY);
        System.out.println("X: " + currentViewX);

        double tempW = ((scrollView.getVvalue() * 0.5) * width);
        double tempH = (scrollView.getHvalue() * 0.5) * height;

        if(tempW != currentViewY || tempH != currentViewX) {

            changedSinceLastUpdate = true;
            currentViewCanvas.setLayoutY(currentViewY - ((scrollView.getMaxHeight()) * scrollView.getVvalue()));
            currentViewCanvas.setLayoutX(currentViewX - ((scrollView.getMaxWidth()) * scrollView.getHvalue()));
            GridWorldView.getInstance().fullRedraw();

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
