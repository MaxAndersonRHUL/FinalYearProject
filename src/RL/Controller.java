package RL;

import java.util.concurrent.ConcurrentLinkedDeque;

public abstract class Controller {

    protected double explorationValue = 2;

    private long timeSaveTemp;
    private int targetFPS = 8;
    private long evoRate = 1000000000 / targetFPS;
    private long totalIterations = 0;
    private ConcurrentLinkedDeque<Integer> forAverageIterationRate = new ConcurrentLinkedDeque<Integer>();

    public boolean executionPaused = false;

    public abstract void stepSimulation();

    public void setExploValue(double value) {
        if(value < 0.01) {
            explorationValue = 0.01;
            return;
        }
        explorationValue = value;
    }

    public void setIterationSpeed(int iterationsPerSecond) {
        targetFPS = iterationsPerSecond;
        evoRate = 1000000000 / targetFPS;
        forAverageIterationRate.clear();
    }

    public int getAverageIterationRate() {
        return calculateAverage(forAverageIterationRate);
    }

    private int calculateAverage(ConcurrentLinkedDeque<Integer> valList) {
        double sum = 0;
        if (!valList.isEmpty()) {
            for (Integer mark : valList) {
                sum += mark;
            }
            // Round up
            return (int) ((sum / valList.size()) + 0.5 ) ;
        }
        return 0;
    }

    public void pauseExecution() {
        executionPaused = true;

    }

    public long getTotalIterations() {
        return totalIterations;
    }

    public void playExecution() {
        executionPaused = false;
    }

    private void addValueToRateList(int val) {
        if (forAverageIterationRate.size() > evoRate) {
            forAverageIterationRate.removeFirst();
        }
        forAverageIterationRate.addLast(val);
    }

    public void startSimulation() {
            new Thread() {
                public void run() {
                    while (true) {
                        long now = System.nanoTime();
                        long updateLength = now - timeSaveTemp;
                        addValueToRateList((int) (1 / ((now - timeSaveTemp) / 1000000000.0)));
                        timeSaveTemp = now;
                        long sleepTime = (timeSaveTemp - System.nanoTime() + evoRate) / 1000000;
                        if (sleepTime > 0) {
                            try {
                                Thread.sleep(sleepTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        if(!executionPaused) {
                            totalIterations++;
                            stepSimulation();
                            CurrentSimulationReference.model.stateChanged();
                        }
                    }
                }
            }.start();
    }

}
