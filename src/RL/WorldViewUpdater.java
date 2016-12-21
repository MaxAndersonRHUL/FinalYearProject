package RL;

import javafx.animation.AnimationTimer;

/**
 * Created by max on 16/10/2016.
 */
public class WorldViewUpdater extends AnimationTimer {

    protected long frameBefore = 0;
    protected int framesPassed = 0;

    @Override
    public void handle(long now) {


        framesPassed++;
        if (framesPassed % 10 == 0) {
            CurrentSimulationReference.view.setFpsText((int) (1 / ((now - frameBefore) / 1000000000.0)));
            int simRate = CurrentSimulationReference.controller.getAverageIterationRate();
            CurrentSimulationReference.view.setSimRateText(simRate);
            CurrentSimulationReference.view.setTotalIterationsText(CurrentSimulationReference.controller.getTotalIterations());
        }
        frameBefore = now;
        CurrentSimulationReference.view.redraw();
    }
}
