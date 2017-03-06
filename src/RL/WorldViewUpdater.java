package RL;

import javafx.animation.AnimationTimer;

/**
 * Created by max on 16/10/2016.
 */
public class WorldViewUpdater extends AnimationTimer {

    protected long frameBefore = 0;
    protected int framesPassed = 0;
    //View updatingView;
    //Controller monitoringController;

    boolean pauseUpdater = false;

    public WorldViewUpdater(View updateView, Controller monitoringController) {
        //this.updatingView = updateView;
        //this.monitoringController = monitoringController;
    }

    public void pauseUpdates() {
        pauseUpdater = true;
    }

    public void startUpdates() {
        pauseUpdater = false;
    }

    @Override
    public void handle(long now) {
        if(pauseUpdater) {
            return;
        }
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
