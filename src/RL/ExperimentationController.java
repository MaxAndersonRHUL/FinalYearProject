package RL;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by max on 23/02/2017.
 */
public class ExperimentationController {

    private static long iterationsPerSave = 10;
    private static long currentIterCounter = 0;
    //private static ScheduledExecutorService updateControl;

    private static HashMap<ExperimentableValue, ArrayList<VariableRecord>> exprValues = new HashMap<>();
    private static ObservableList<ExperimentableValue> observableValues = FXCollections.observableArrayList();

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

    public static void iterationOccurred() {
        currentIterCounter++;
        if(currentIterCounter < iterationsPerSave) {
            return;
        }
        currentIterCounter = 0;
        for (Map.Entry<ExperimentableValue, ArrayList<VariableRecord>> entry : exprValues.entrySet()) {
            entry.getValue().add(new VariableRecord(entry.getKey().getValue(), CurrentSimulationReference.controller.getTotalIterations()));
        }
        GraphView.dataAdded();
    }

}
