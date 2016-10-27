package sample;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by New on 10/23/2016.
 */
public class GridWorldCreator {

    private static GridWorldCreator instance;
    private GridWorldView gridView;

    private GridWorldCreator() {
    }

    public static GridWorldCreator getInstance() {
        if(instance == null) {
            instance = new GridWorldCreator();
        }
        return instance;
    }

    public void start(Stage primaryStage) {

        gridView = GridWorldView.getInstance();

        VBox root = new VBox(25);

        Canvas canvas = new Canvas();

        primaryStage.close();

        primaryStage.setTitle("Grid World Creator");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

}
