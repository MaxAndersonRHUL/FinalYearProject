package RL;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by max on 23/02/2017.
 */
public class ExperimentationController {

    private static long iterationsPerSave = 10;
    private static long currentIterCounter = 0;
    //private static ScheduledExecutorService updateControl;

    static LinkedList<RewardIterationPoint> listOfRewards = new LinkedList<>();

    private static HashMap<ExperimentableValue, ArrayList<VariableRecord>> exprValues = new HashMap<>();
    private static ObservableList<ExperimentableValue> observableValues = FXCollections.observableArrayList();

    private static ExperimentableValue averageRewardPer100Actions = new ExperimentableValue(0.0, "Average Reward per 100 actions");
    private static ExperimentableValue averageRewardPer1000Actions = new ExperimentableValue(0.0, "Average Reward per 1000 actions");
    private static ExperimentableValue totalRewardExprVal = new ExperimentableValue(0.0, "Total Reward Recieved");

    // This implementation works for a time-based data recording. I decided it makes more sense to save information
    // based on learning iterations, not time.
    /*
    public static void beginGatheringData() {
        Runnable updateVals = new Runnable() {
            public void run() {
                for (Map.Entry<ExperimentableValue, ArrayList<Number>> entry : exprValues.entrySet()) {
                    entry.getValue().add(entry.getKey().getValue());
                    System.out.println("UPDATING VALUE: " + entry.getKey().getName() + " WITH: " + entry.getKey().getValue());
                }
            }
        };

        updateControl = Executors.newScheduledThreadPool(1);
        updateControl.scheduleAtFixedRate(updateVals, 0, valueUpdateTimeStep, TimeUnit.MILLISECONDS);

    }
    */

    public static void setIterationsPerSave(int num) {
        iterationsPerSave = num;
    }

    public static void registerExperimentableValue(ExperimentableValue newValue) {
        exprValues.put(newValue, new ArrayList<>());
        observableValues.add(newValue);
    }

    public static HashMap<ExperimentableValue, ArrayList<VariableRecord>> getExperimentableValues() {
        return exprValues;
    }

    public static ArrayList<VariableRecord> getRecordedValuesOfExperementableVar(ExperimentableValue val) {
        ArrayList<VariableRecord> toReturn = exprValues.get(val);
        return toReturn;
    }

    public static ObservableList<ExperimentableValue> getObservableValues() {
        return observableValues;
    }

    public static void calculateCalculableExprValues() {
        long totalIters = CurrentSimulationReference.controller.getTotalIterations();
        long searchIterations100 = totalIters - 100;
        long searchIterations1000 = totalIters - 1000;
        double totalReward100 = 0;
        double totalReward1000 = 0;
        double totalReward = 0;
        for(RewardIterationPoint point : listOfRewards) {
            totalReward = totalReward + point.reward;
            if(point.iteration >= searchIterations100) {
                totalReward100 = totalReward100 + point.reward;
            }
            if(point.iteration >= searchIterations1000) {
                totalReward1000 = totalReward1000 + point.reward;
            }
        }

        totalRewardExprVal.setValue(totalReward);

        if(totalReward1000 == 0) {
            averageRewardPer1000Actions.setValue(0);

        } else {
            double avgReward1000 = 0;
            if (totalIters < 1000) {
                avgReward1000 = totalReward1000 / totalIters;
            } else {
                avgReward1000 = totalReward1000 / 1000;
            }
            averageRewardPer1000Actions.setValue(avgReward1000);
        }
        if(totalReward100 == 0) {
            averageRewardPer100Actions.setValue(0);
        } else {
            double avgReward100 = 0;
            if (totalIters < 100) {
                avgReward100 = totalReward100 / totalIters;
            } else {
                avgReward100 = totalReward100 / 100;
            }
            averageRewardPer100Actions.setValue(avgReward100);
        }


    }

    public static void iterationOccurred() {
        double reward = CurrentSimulationReference.model.mainAgent.currentState.getReward();
        if(reward != 0) {
            listOfRewards.add(new RewardIterationPoint(reward, CurrentSimulationReference.controller.getTotalIterations()));
        }

        currentIterCounter++;
        if(currentIterCounter < iterationsPerSave) {
            return;
        }
        currentIterCounter = 0;
        calculateCalculableExprValues();
        for (Map.Entry<ExperimentableValue, ArrayList<VariableRecord>> entry : exprValues.entrySet()) {
            entry.getValue().add(new VariableRecord(entry.getKey().getValue(), CurrentSimulationReference.controller.getTotalIterations()));
            entry.getKey().setAmountOfRecords(entry.getValue().size());
        }
        GraphView.dataAdded();
    }

}

// I want tuples in java
class RewardIterationPoint {
    public double reward;
    public long iteration;

    public RewardIterationPoint(double reward, long iteration) {
        this.reward = reward;
        this.iteration = iteration;
    }

}
