package GridWorld;

import javafx.animation.AnimationTimer;

/**
 * Created by max on 16/10/2016.
 */
public class WorldViewUpdater extends AnimationTimer {

    long frameBefore = 0;
    int framesPassed = 0;

    @Override
    public void handle(long now) {


        framesPassed++;
        if (framesPassed % 10 == 0) {
            GridWorldView.getInstance().setFpsText((int) (1 / ((now - frameBefore) / 1000000000.0)));
            int simRate = GridWorldQLearning.getInstance().getAverageIterationRate();
            GridWorldView.getInstance().setSimRateText(simRate);
        }
        frameBefore = now;
        GridWorldView.getInstance().redraw();
        GridWorldView.getInstance().drawAgent();

    }
}
