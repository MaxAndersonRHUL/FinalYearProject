package RL;

import javafx.animation.AnimationTimer;

/**
 * Created by max on 16/10/2016.
 */
public class WorldViewUpdater extends AnimationTimer {

    protected long frameBefore = 0;
    protected int framesPassed = 0;
    View updatingView;
    Controller monitoringController;

    public WorldViewUpdater(View updateView, Controller monitoringController) {
        this.updatingView = updateView;
        this.monitoringController = monitoringController;
    }

    @Override
    public void handle(long now) {
        framesPassed++;
        if (framesPassed % 10 == 0) {
            updatingView.setFpsText((int) (1 / ((now - frameBefore) / 1000000000.0)));
            int simRate = CurrentSimulationReference.controller.getAverageIterationRate();
            updatingView.setSimRateText(simRate);
            updatingView.setTotalIterationsText(CurrentSimulationReference.controller.getTotalIterations());
        }
        frameBefore = now;
        updatingView.redraw();
    }
}
