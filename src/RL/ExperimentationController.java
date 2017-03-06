package RL;

import RL.gridWorld.GridWorldModel;
import RL.gridWorld.GridWorldView;
import com.rits.cloning.Cloner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by max on 23/02/2017.
 */
public class ExperimentationController {

    private static long iterationsPerSave = 10;
    private static long currentIterCounter = 0;
    //private static ScheduledExecutorService updateControl;

    private static int currentExperimentNumber = 0;
    private static int totalExperimentsTodo = 4;

    static LinkedList<RewardIterationPoint> listOfRewards = new LinkedList<>();

    private static ConcurrentHashMap<StateIdentity, State> initialEnvironmentState;

    private static boolean finishedAllSimulations = false;

    private static boolean runningAverageSimulation = false;

    // This data structure is a list of hashmaps. The hash maps map experimentable values to a list of records that
    // this controller has recorded. Since more than 1 simulation can run and the results averaged, the map is in a list.
    // Each entry to the outer most list is a full simulation. When only running 1 simulation and not averaging results,
    // there will only be 1 entry into the outer most list.
    private static ConcurrentLinkedDeque<HashMap<ExperimentableValue, ConcurrentLinkedDeque<VariableRecord>>> exprValues = new ConcurrentLinkedDeque<>();
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

    private static void setExprValuesToZero() {
        for(ExperimentableValue val : exprValues.getLast().keySet()) {
            val.setValue(0.0);
        }
    }

    public static void fullSimulationResetGridWorldQ() {
        GridWorldView.getInstance().pauseRedrawExecution();
        QLearningController.getInstance().reset();
        Cloner cloner = new Cloner();
        GridWorldModel.getInstance().reset(cloner.deepClone(initialEnvironmentState));

        setExprValuesToZero();

        CurrentSimulationReference.controller.resumeEndedSimulation();

        GridWorldView.getInstance().resetDrawState();

        GridWorldView.getInstance().fullRedraw();
        GridWorldView.getInstance().resumeRedrawExecution();
    }

    public static void simFinished() {
        if(!runningAverageSimulation) {
            return;
        }
        currentExperimentNumber++;
        if(currentExperimentNumber == totalExperimentsTodo) {
            finishedAllSimulations = true;
            CurrentSimulationReference.view.pauseRedrawExecution();
            QLearningController.getInstance().reset();
            QLearningController.getInstance().pauseExecution();
            calculateFinalResults();
        } else if (!finishedAllSimulations){
            listOfRewards.clear();
            addNewSimToList();
            fullSimulationResetGridWorldQ();
        }
    }

    public static void addNewSimToList() {
        boolean mk1 = false;

        for(ExperimentableValue value : exprValues.getLast().keySet()) {
            if(!mk1) {
                exprValues.add(new HashMap<ExperimentableValue, ConcurrentLinkedDeque<VariableRecord>>());
                mk1 = true;
            }
            value.setValue(0);
            exprValues.getLast().put(value, new ConcurrentLinkedDeque<>());
        }
    }

    private static void calculateFinalResults() {

       // ArrayList<ExperimentableValue> averagedExprValues = new ArrayList<>();

        for(ExperimentableValue exprValue : exprValues.getFirst().keySet()) {
            ExperimentableValue newAveragedValue = new ExperimentableValue(0, exprValue.getName() + " Averaged over " + totalExperimentsTodo + " tests");
            //averagedExprValues.add(newAveragedValue);
            ConcurrentLinkedDeque newListOfRecords =  new ConcurrentLinkedDeque<>();
            exprValues.getLast().put(newAveragedValue, newListOfRecords);
            ArrayList<Iterator<VariableRecord>> exprIterators = new ArrayList<>();
            for(HashMap<ExperimentableValue, ConcurrentLinkedDeque<VariableRecord>> map : exprValues) {
                exprIterators.add(map.get(exprValue).iterator());
            }

            long currentIterationBeingCalculated = 0;
            while(exprIterators.get(0).hasNext()) {

                double sum = 0;
                int amount = 0;
                VariableRecord lastRecordFound = null;
                for(Iterator<VariableRecord> iter : exprIterators) {
                    lastRecordFound = iter.next();
                    sum = sum + lastRecordFound.getValue().doubleValue();
                    amount++;
                }

                double averageForThisIteration = sum/amount;
                newListOfRecords.add(new VariableRecord(averageForThisIteration, lastRecordFound.getIterationsRecordedOn()));
                currentIterationBeingCalculated++;
            }
        }
    }

    // Using cloning library, under apache open source license.
    // https://github.com/kostaskougios/cloning
    public static void setupForAverageSim(int simAmount, int iterAmount) {
        totalExperimentsTodo = simAmount;
        CurrentSimulationReference.controller.finishAfterIterations = iterAmount;

        runningAverageSimulation = true;

        Cloner cloner = new Cloner();
        initialEnvironmentState = cloner.deepClone(CurrentSimulationReference.model.getStates());
    }

    public static void setIterationsPerSave(int num) {
        iterationsPerSave = num;
    }

    public static void registerExperimentableValue(ExperimentableValue newValue) {
        if(exprValues.size() == 0) {
            exprValues.add(new HashMap<ExperimentableValue, ConcurrentLinkedDeque<VariableRecord>>());
        }
        exprValues.getLast().put(newValue, new ConcurrentLinkedDeque<>());
        observableValues.add(newValue);
    }

    public static HashMap<ExperimentableValue, ConcurrentLinkedDeque<VariableRecord>> getExperimentableValues() {
        return exprValues.getFirst();
    }

    public static ConcurrentLinkedDeque<VariableRecord> getRecordedValuesOfExperementableVar(ExperimentableValue val) {
        ConcurrentLinkedDeque<VariableRecord> toReturn = exprValues.getLast().get(val);
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
        double reward = CurrentSimulationReference.model.mainAgent.getCurrentState().getReward();
        if(reward != 0) {
            listOfRewards.add(new RewardIterationPoint(reward, CurrentSimulationReference.controller.getTotalIterations()));
        }

        currentIterCounter++;
        if(currentIterCounter < iterationsPerSave) {
            return;
        }
        currentIterCounter = 0;
        calculateCalculableExprValues();
        for (Map.Entry<ExperimentableValue, ConcurrentLinkedDeque<VariableRecord>> entry : exprValues.getLast().entrySet()) {
            entry.getValue().add(new VariableRecord(entry.getKey().getValue(), CurrentSimulationReference.controller.getTotalIterations()));
            entry.getKey().setAmountOfRecords(entry.getValue().size());
        }
        GraphView.dataAdded();
    }

}


class RewardIterationPoint {
    public double reward;
    public long iteration;

    public RewardIterationPoint(double reward, long iteration) {
        this.reward = reward;
        this.iteration = iteration;
    }

}
