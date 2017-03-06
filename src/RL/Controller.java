package RL;

import java.util.concurrent.ConcurrentLinkedDeque;

public abstract class Controller {

    protected ExperimentableValue explorationValue = new ExperimentableValue(2, "Exploration Value");

    private long timeSaveTemp;
    private int targetFPS = 8;
    private long evoRate = 1000000000 / targetFPS;
    private long totalIterations = 0;
    private ConcurrentLinkedDeque<Integer> forAverageIterationRate = new ConcurrentLinkedDeque<Integer>();

    public long finishAfterIterations = -1;

    public boolean executionPaused = false;

    public boolean deterministicEnvironment = true;

    private boolean pureRandomMode;

    private boolean simRunning = true;

    public void setAmountOfIterationsToCompute(long iters) {
        finishAfterIterations = iters;
    }

    public void reset() {
        totalIterations = 0;
    }


    public void resumeEndedSimulation() {
        executionPaused = false;
        simRunning = true;
    }


    public void stepSimulation() {

        Action learningChoice;

        if(pureRandomMode) {
            learningChoice = CurrentSimulationReference.model.moveAgentRandom();
        } else {
            learningChoice = makeActionChoice();
        }

        // makeActionChoice should only return null if there are no actions to take.
        // If there are no actions, we don't need to update any values.
        if(learningChoice == null) {
            return;
        }

        State result = CurrentSimulationReference.model.decideActionChoiceResult(learningChoice);
        CurrentSimulationReference.model.mainAgent.doAction(learningChoice);
        CurrentSimulationReference.model.mainAgent.forceSetCurrentState(result);
        //Model.getInstance().mainAgent.currentState = result;

        updateAgentLearningValues();
    }

    void setPureRandomMode(boolean mode) {
        pureRandomMode = mode;
    }

    boolean getPureRandomMode() {
        return pureRandomMode;
    }


    public void setExploValue(double value) {
        if(value < 0.01) {
            explorationValue.setValue(0.01);
            return;
        }
        explorationValue.setValue(value);
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

    public void finishedSimulation() {
        ExperimentationController.simFinished();
    }

    protected abstract Action makeActionChoice();

    protected abstract void updateAgentLearningValues();

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
                    while (simRunning) {
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

                        if(totalIterations == finishAfterIterations) {
                            finishedSimulation();
                            continue;
                        }

                        if(!executionPaused) {
                            totalIterations++;
                            stepSimulation();
                            ExperimentationController.iterationOccurred();
                            CurrentSimulationReference.model.stateChanged();
                        }
                    }
                }
            }.start();
    }

}
